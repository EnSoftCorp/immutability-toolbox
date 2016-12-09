package com.ensoftcorp.open.immutability.analysis;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.SetDefinitions;
import com.ensoftcorp.open.immutability.constants.ImmutabilityTags;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.java.commons.wishful.JavaStopGap;
import com.ensoftcorp.open.pointsto.common.PointsToAnalysis;
import com.ensoftcorp.open.pointsto.preferences.PointsToPreferences;

public class PointsToImmutabilityAnalysis extends ImmutabilityAnalysis {

	/**
	 * Helper for formatting decimal strings
	 */
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##"); 
	
	@Override
	public boolean run(IProgressMonitor monitor) {
		if(PointsToPreferences.isPointsToAnalysisEnabled()){
			// considers primitives, String literals, and enum constants
			// note: this set also includes null, but that case is explicitly handled in address creation
			//       so all null literals are represented with a single address id to save on space
			
			Q context = SetDefinitions.app(); // only consider mutations inside the application
			
			Q specialInstantiations = Common.universe().nodesTaggedWithAny(XCSG.Java.EnumConstant).difference(Common.universe().nodesTaggedWithAny(XCSG.Null));
			Q objectInstantiations = Common.universe().nodesTaggedWithAny(XCSG.Instantiation, XCSG.ArrayInstantiation).union(specialInstantiations);
			Q instanceVariableWrittenEdges = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableWritten);
			Q instanceVariableAssignments = Common.universe().nodesTaggedWithAny(XCSG.InstanceVariableAssignment);
			for(Node objectInstantiation : context.intersection(objectInstantiations).eval().nodes()){
				Q aliases = PointsToAnalysis.getAliases(objectInstantiation).difference(objectInstantiations).intersection(context);
				boolean readonly = instanceVariableWrittenEdges.predecessors(instanceVariableAssignments).intersection(aliases).eval().nodes().isEmpty();
				markMutableAliases(aliases, readonly);
				if(objectInstantiation.taggedWith(XCSG.ArrayInstantiation)){
					Node arrayInstantiation = objectInstantiation;
					Q arrayMemoryModelAliases = Common.toQ(PointsToAnalysis.getArrayMemoryModelAliases(arrayInstantiation)).difference(objectInstantiations);
					readonly = instanceVariableWrittenEdges.predecessors(instanceVariableAssignments).intersection(arrayMemoryModelAliases).eval().nodes().isEmpty();
					// mutations to the array components mutate the array itself
					markMutableAliases(aliases, readonly);
				}
				
				// if a class variable also has this alias then any mutation to 
				// that alias makes the method where the mutation happened impure
				// TODO: implement
			}
			
			// todo: consider open world assumptions
			// returns and parameters of library methods not known to be immutable are assumed mutable
			
			// flattens the type hierarchy to the maximal types
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Extracting maximal types...");
			long startExtraction = System.nanoTime();
			extractMaximalTypes();
			long stopExtraction = System.nanoTime();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Extracted maximal types in " + FORMAT.format((stopExtraction-startExtraction)/1000.0/1000.0) + " ms");
			
			// tags pure methods
			// must be run after extractMaximalTypes
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Applying method immutability tags...");
			long startImmutabilityTagging = System.nanoTime();
			tagPureMethods();
			long stopImmutabilityTagging = System.nanoTime();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Applied method immutability tags in " + FORMAT.format((stopImmutabilityTagging-startImmutabilityTagging)/1000.0/1000.0) + " ms");
			
			// sanity checks
			boolean isSane = true;
			
			// the points-to analysis should not produce untyped references
			AtlasSet<Node> untypedReferences = Common.universe().nodesTaggedWithAny(ImmutabilityTags.UNTYPED).eval().nodes();
			if(!untypedReferences.isEmpty()){
				isSane = false;
				Log.warning("The points-to analysis reported untyped references!");
			}
			
			return isSane;
		} else {
			Log.error("Points-to analysis must be enabled to run points-to immutability analysis.",
					new IllegalArgumentException());
			return false;
		}
	}

	private void markMutableAliases(Q aliases, boolean readonly) {
		for(Node alias : aliases.eval().nodes()){
			// since we are only placing types for the convenience of client analyses
			// we will only place types on references that would be typed in the reiminfer system
			// note: we actually known something more important (whether any of the aliases could
			// mutate the object they reference), this is just record now
			if(AnalysisUtilities.isTypable(alias)){
				if(!readonly) {
					if(alias.taggedWith(XCSG.InstanceVariable) || alias.taggedWith(XCSG.ReturnValue)){
						AnalysisUtilities.removeTypes(alias, ImmutabilityTypes.READONLY);
					} else {
						AnalysisUtilities.removeTypes(alias, ImmutabilityTypes.READONLY, ImmutabilityTypes.POLYREAD);
					}
				}
			}
		}
	}
	
	private ImmutabilityTypes getDefaultMaximalType(GraphElement ge) {
		ImmutabilityTypes maximalType;
		if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
			maximalType = ImmutabilityTypes.MUTABLE;
		} else {
			// all other cases default to readonly as the maximal type
			maximalType = ImmutabilityTypes.READONLY;
		}
		return maximalType;
	}
	
	/**
	 * Flattens the remaining immutability qualifiers to the maximal type
	 * and applies the maximal type as a tag
	 */
	private void extractMaximalTypes(){
		Q methods = Common.universe().nodesTaggedWithAny(XCSG.Method);
		Q typesToExtract = Common.universe().selectNode(AnalysisUtilities.IMMUTABILITY_QUALIFIERS).difference(methods);
		AtlasSet<Node> attributedNodes = Common.resolve(new NullProgressMonitor(), typesToExtract.eval()).nodes();
		for(Node attributedNode : attributedNodes){
			Set<ImmutabilityTypes> types = getTypes(attributedNode);
			ArrayList<ImmutabilityTypes> orderedTypes = new ArrayList<ImmutabilityTypes>(types.size());
			orderedTypes.addAll(types);
			if(orderedTypes.isEmpty()){
				attributedNode.tag(ImmutabilityTags.UNTYPED);
			} else {
				Collections.sort(orderedTypes);
				ImmutabilityTypes maximalType = orderedTypes.get(orderedTypes.size()-1);
				attributedNode.tag(maximalType.toString());
			}
		}
		AtlasSet<Node> itemsToTrack = getUntrackedItems(attributedNodes);
		for(GraphElement untouchedTrackedItem : itemsToTrack){
			ImmutabilityTypes maximalType = getDefaultMaximalType(untouchedTrackedItem);
			untouchedTrackedItem.tag(maximalType.toString());
		}
	}
	
	private AtlasSet<Node> getUntrackedItems(AtlasSet<Node> attributedNodes) {
		Q literals = Common.universe().nodesTaggedWithAll(XCSG.Literal);
		Q parameters = Common.universe().nodesTaggedWithAny(XCSG.Parameter);
		Q returnValues = Common.universe().nodesTaggedWithAny(XCSG.ReturnValue);
		Q instanceVariables = Common.universe().nodesTaggedWithAny(XCSG.InstanceVariable);
		Q thisNodes = Common.universe().nodesTaggedWithAny(XCSG.Identity);
		Q classVariables = Common.universe().nodesTaggedWithAny(XCSG.ClassVariable);
		// note local variables may also get tracked, but only if need be during the analysis
		Q trackedItems = literals.union(parameters, returnValues, instanceVariables, thisNodes, classVariables);
		Q untouchedTrackedItems = trackedItems.difference(trackedItems.nodesTaggedWithAny(ImmutabilityTags.READONLY, ImmutabilityTags.POLYREAD, ImmutabilityTags.MUTABLE), Common.toQ(attributedNodes));
		AtlasSet<Node> itemsToTrack = new AtlasHashSet<Node>();
		itemsToTrack.addAll(untouchedTrackedItems.eval().nodes());
		return itemsToTrack;
	}

}
