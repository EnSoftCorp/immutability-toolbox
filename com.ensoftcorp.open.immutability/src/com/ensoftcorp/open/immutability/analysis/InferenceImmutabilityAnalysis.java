package com.ensoftcorp.open.immutability.analysis;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.addMutable;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.StandardQueries;
import com.ensoftcorp.open.commons.utilities.DisplayUtils;
import com.ensoftcorp.open.immutability.analysis.checkers.BasicAssignmentChecker;
import com.ensoftcorp.open.immutability.analysis.checkers.CallChecker;
import com.ensoftcorp.open.immutability.analysis.checkers.FieldAssignmentChecker;
import com.ensoftcorp.open.immutability.analysis.checkers.SanityChecks;
import com.ensoftcorp.open.immutability.analysis.solvers.XEqualsYConstraintSolver;
import com.ensoftcorp.open.immutability.constants.ImmutabilityTags;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.java.commons.wishful.JavaStopGap;
import com.ensoftcorp.open.jimple.commons.wishful.JimpleStopGap;

public class InferenceImmutabilityAnalysis extends ImmutabilityAnalysis {

	/**
	 * Helper for formatting decimal strings
	 */
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##"); 
	
	/**
	 * Helper class to store a File object result
	 */
	private static class FileResult {
		File file = null;
	}
	
	/**
	 * Runs the reference immutability analysis
	 */
	public boolean run(IProgressMonitor monitor){
		final FileResult fileResult = new FileResult();
		if(ImmutabilityPreferences.isLoadSummariesEnabled()){
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
					dialog.setFilterNames(new String[] { "Immutability Analysis Results", "All Files (*.*)" });
					dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
					fileResult.file = new File(dialog.open());
				}
			});
			try {
				SummaryUtilities.importSummary(fileResult.file);
			} catch (FileNotFoundException e) {
				DisplayUtils.showError(e, "Could not find summary file.");
			} catch (XMLStreamException e) {
				DisplayUtils.showError(e, "Error parsing summary file.");
			}
			fileResult.file = null;
		}
		if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
					dialog.setFilterNames(new String[] { "Immutability Analysis Results", "All Files (*.*)" });
					dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
					try {
						String projectName = Common.universe().nodesTaggedWithAny(XCSG.Project).eval().nodes().getFirst().getAttr(XCSG.name).toString();
						dialog.setFileName(projectName + "-immutability.xml");
					} catch (Exception e){}
					fileResult.file = new File(dialog.open());
				}
			});
		}
		File outputFile = fileResult.file;
		
		if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
			if(outputFile==null){
				Log.warning("No output file selected, immutability results will not be serialized to XML file.");
			} else {
				Log.info("Immutability results will be serialized to " + outputFile.getAbsolutePath());
			}
		}
		
		// TODO: remove when there are appropriate alternatives
		JavaStopGap.addClassVariableAccessTags();
		JimpleStopGap.addDataFlowDisplayNodeTags();
		
		AnalysisUtilities.addDummyReturnAssignments();

		AtlasHashSet<Node> worklist = new AtlasHashSet<Node>();

		// add all assignments to worklist
		// this includes dummy return assignments which are fillers for providing 
		// context sensitivity when the return value of a call is unused
		Q assignments = Common.universe().nodesTaggedWithAny(XCSG.Assignment);
		assignments = Common.resolve(new NullProgressMonitor(), assignments);
		for(Node assignment : assignments.eval().nodes()){
			worklist.add(assignment);
		}
		
		int iteration = 1;
		while(true){
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Immutability analysis iteration: " + iteration);
			long startIteration = System.nanoTime();
			
			boolean typesChanged = false;
			for(Node workItem : worklist){
				try {
					if(applyInferenceRules(workItem)){
						typesChanged = true;
					}
				} catch (Exception e){
					Log.error("Error applying inference rules for work item: " + workItem.address().toAddressString() + "\n" + workItem.toString(), e);
					throw e;
				}
			}
			
			long stopIteration = System.nanoTime();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Immutability analysis iteration: " + iteration + " completed in " + FORMAT.format((stopIteration-startIteration)/1000.0/1000.0) + " ms");
			
			// the worklist consists of assignments (including stack assignments)
			// in the worst case an iteration through the worklist only removes one type from one reference in an assignment
			// since there are at most only 3 valid types for each reference, the algorithm must reach fixed point in
			// O(3*n) iterations where n is the number of typed references used in the set of assignments
			if(!typesChanged){
				if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Immutability analysis reached fixed point in " + iteration + " iterations");
				break;
			} else {
				// fixed point has not been reached
				// go for another pass
				iteration++;
			}
		}
		
		if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
			// serialize immutability sets to Atlas tags
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Converting immutability sets into tags...");
			convertImmutabilityTypesToTags();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Converted immutability sets into tags.");
			
			// serialize field and method tags
			if(outputFile != null){
				try {
					SummaryUtilities.exportSummary(outputFile);
				} catch (Exception e){
					Log.error("Could not save summaries.", e);
					DisplayUtils.showError(e, "Could not save summaries.");
				}
			}
		} else {
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
		}
		
		boolean isSane = true;
		if(ImmutabilityPreferences.isRunSanityChecksEnabled()){
			Log.info("Running sanity checks...");
			isSane = SanityChecks.run();
			if(isSane){
				Log.info("Sanity checks completed. Everything is sane.");
			} else {
				Log.warning("Sanity checks failed!");
			}
		}
		
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Performing cleanup...");

		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Removing Immutability Qualifier Sets...");
		AtlasSet<Node> attributedNodes = Common.universe().selectNode(AnalysisUtilities.IMMUTABILITY_QUALIFIERS).eval().nodes();
		AtlasHashSet<Node> attributedNodesToUnattribute = new AtlasHashSet<Node>(attributedNodes);
		while(!attributedNodesToUnattribute.isEmpty()){
			Node attributedNode = attributedNodesToUnattribute.getFirst();
			attributedNodesToUnattribute.remove(attributedNode);
			attributedNode.removeAttr(AnalysisUtilities.IMMUTABILITY_QUALIFIERS);
		}
		
		AnalysisUtilities.removeDummyReturnAssignments();
		
		// TODO: remove when there are appropriate alternatives
		JimpleStopGap.removeDataFlowDisplayNodeTags();
		JavaStopGap.removeClassVariableAccessTags();
		
		return isSane;
	}

	/**
	 * Given a graph element, each inference rule (TNEW, TASSIGN, TWRITE, TREAD, TCALL) is checked
	 * and unsatisfied qualifier types are removed or reduced (a new type may be added, but it will 
	 * replace other types reducing the total number of types) from graph elements
	 * 
	 * @param workItem Returns true if any type qualifier sets changed
	 * @return
	 */
	private static boolean applyInferenceRules(Node workItem) throws RuntimeException {
		
		boolean typesChanged = false;
		
		Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		Q instanceVariableAccessedEdges = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed);
		Q identityPassedToEdges = Common.universe().edgesTaggedWithAny(XCSG.IdentityPassedTo);

		// consider data flow edges
		// incoming edges represent a read relationship in an assignment
		// outgoing edges represent a write relationship in an assignment
		Node to = workItem;
		AtlasSet<Edge> inEdges = localDataFlowEdges.reverseStep(Common.toQ(to)).eval().edges();
		for(GraphElement edge : inEdges){
			for(Node from : AnalysisUtilities.parseReferences(edge.getNode(EdgeDirection.FROM))){
				if(to.taggedWith(XCSG.ArrayWrite) || from.taggedWith(XCSG.ArrayRead)){
					continue;
				}
				
				// process constraints for assignments to references
				boolean involvesField = false;
				boolean involvesCallsite = false;
				
				// TWRITE
				if(to.taggedWith(XCSG.InstanceVariableAssignment)){
					involvesField = true;
				}
				
				// TREAD
				if(from.taggedWith(XCSG.InstanceVariableValue)){
					involvesField = true;
				}
				
				// TSREAD
				if(from.taggedWith(JavaStopGap.CLASS_VARIABLE_VALUE)){
					involvesField = true;
				}
				
				// TSWRITE
				if(to.taggedWith(JavaStopGap.CLASS_VARIABLE_ASSIGNMENT)){
					involvesField = true;
				}	
				
				// TCALL
				if(from.taggedWith(XCSG.DynamicDispatchCallSite)){
					involvesCallsite = true;
				}
				
				// TSCALL
				if(from.taggedWith(XCSG.StaticDispatchCallSite)){
					involvesCallsite = true;
				}
				
				// Type Rule 2 - TASSIGN
				// let x = y
				if((!involvesField && !involvesCallsite)){
					AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(to);
					for(Node x : xReferences){
						AtlasSet<Node> yReferences = AnalysisUtilities.parseReferences(from);;
						for(Node y : yReferences){
							if(BasicAssignmentChecker.handleAssignment(x, y)){
								typesChanged = true;
							}
						}
					}
				}
			}
		}
	
		return typesChanged;
	}
	
	/**
	 * Converts the immutability types to tags for partial program analysis
	 */
	private void convertImmutabilityTypesToTags(){
		Q typesToExtract = Common.universe().selectNode(AnalysisUtilities.IMMUTABILITY_QUALIFIERS);
		AtlasSet<Node> attributedNodes = Common.resolve(new NullProgressMonitor(), typesToExtract.eval()).nodes();
		for(GraphElement attributedNode : attributedNodes){
			Set<ImmutabilityTypes> types = getTypes(attributedNode);
			if(types.isEmpty()){
				attributedNode.tag(ImmutabilityTags.UNTYPED);
			} else {
				for(ImmutabilityTypes type : types){
					attributedNode.tag(type.toString());
				}
			}
		}
		
		AtlasSet<Node> itemsToTrack = getUntrackedItems(attributedNodes);
		for(GraphElement untouchedTrackedItem : itemsToTrack){
			Set<ImmutabilityTypes> defaultTypes = AnalysisUtilities.getDefaultTypes(untouchedTrackedItem);
			for(ImmutabilityTypes type : defaultTypes){
				untouchedTrackedItem.tag(type.toString());
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
		Q typesToExtract = Common.universe().selectNode(AnalysisUtilities.IMMUTABILITY_QUALIFIERS);
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
