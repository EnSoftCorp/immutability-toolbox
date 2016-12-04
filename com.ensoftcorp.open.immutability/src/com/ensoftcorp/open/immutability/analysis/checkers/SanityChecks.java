package com.ensoftcorp.open.immutability.analysis.checkers;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.immutability.analysis.AnalysisUtilities;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.constants.ImmutabilityTags;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class SanityChecks {

	public static boolean run(){
		boolean resultsAreSane = true;
		
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking for untyped references...");
		resultsAreSane &= !hasUntypedReferences();
		
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking for conservation of types (types cannot be gained)...");
		resultsAreSane &= !gainedTypes(XCSG.Null, XCSG.Literal, 
									   XCSG.Instantiation, XCSG.ArrayInstantiation, 
									   XCSG.MasterReturn, XCSG.Identity, XCSG.Parameter,
									   XCSG.InstanceVariable, XCSG.ClassVariable,
									   XCSG.Method);
		
		if(!ImmutabilityPreferences.isGenerateSummariesEnabled()){
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking for double tagged immutability types...");
			resultsAreSane &= !isDoubleTagged();
			
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking that methods are not tagged with immutability types...");
			resultsAreSane &= !methodsDoNotHaveImmutabilityTypes();
		}
		
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking all fields are typed...");
		resultsAreSane &= !allFieldsAreTagged();
		
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking all parameters are typed...");
		resultsAreSane &= !allParametersAreTagged();
		
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking all identities are typed...");
		resultsAreSane &= !allIdentititesAreTagged();

		return resultsAreSane;
	}

	private static boolean allIdentititesAreTagged() {
		// each field should be tagged
		long missingTags = 0;
		Q identities = Common.universe().nodesTaggedWithAny(XCSG.Identity);
		for(Node identity : identities.eval().nodes()){
			if(!(identity.taggedWith(ImmutabilityTags.READONLY) || identity.taggedWith(ImmutabilityTags.POLYREAD) || identity.taggedWith(ImmutabilityTags.MUTABLE))){
				missingTags++;
			}
		}
		boolean hasMissingTags = missingTags > 0;
		if(hasMissingTags) Log.warning("There are " + missingTags + " identity that are not tagged.");
		return hasMissingTags;
	}
	
	private static boolean allParametersAreTagged() {
		// each field should be tagged
		long missingTags = 0;
		Q parameters = Common.universe().nodesTaggedWithAny(XCSG.Parameter);
		for(Node parameter : parameters.eval().nodes()){
			if(!(parameter.taggedWith(ImmutabilityTags.READONLY) || parameter.taggedWith(ImmutabilityTags.POLYREAD) || parameter.taggedWith(ImmutabilityTags.MUTABLE))){
				missingTags++;
			}
		}
		boolean hasMissingTags = missingTags > 0;
		if(hasMissingTags) Log.warning("There are " + missingTags + " parameters that are not tagged.");
		return hasMissingTags;
	}
	
	private static boolean allFieldsAreTagged() {
		// each field should be tagged
		long missingTags = 0;
		Q fields = Common.universe().nodesTaggedWithAny(XCSG.Field);
		
		// TODO: remove after Atlas bug is fixed (because this should never happen)
		Q unknownFields = fields.difference(Common.universe().nodesTaggedWithAny(XCSG.ClassVariable, XCSG.InstanceVariable));
		fields = fields.difference(unknownFields);
		
		for(Node field : fields.eval().nodes()){
			if(!(field.taggedWith(ImmutabilityTags.READONLY) || field.taggedWith(ImmutabilityTags.POLYREAD) || field.taggedWith(ImmutabilityTags.MUTABLE))){
				missingTags++;
			}
		}
		boolean hasMissingTags = missingTags > 0;
		if(hasMissingTags) Log.warning("There are " + missingTags + " fields that are not tagged.");
		return hasMissingTags;
	}
	
	/**
	 * Checks that methods do not have immutability tags
	 * Only true for whole program analysis (partial program analysis is an exception)
	 * @return
	 */
	private static boolean methodsDoNotHaveImmutabilityTypes(){
		long unexpectedTypes = 0;
		unexpectedTypes += Common.universe().nodesTaggedWithAll(XCSG.Method, ImmutabilityTags.READONLY).eval().nodes().size();
		unexpectedTypes += Common.universe().nodesTaggedWithAll(XCSG.Method, ImmutabilityTags.POLYREAD).eval().nodes().size();
		unexpectedTypes += Common.universe().nodesTaggedWithAll(XCSG.Method, ImmutabilityTags.MUTABLE).eval().nodes().size();
		unexpectedTypes += Common.universe().nodesTaggedWithAll(XCSG.Method, ImmutabilityTags.UNTYPED).eval().nodes().size();
		boolean hasUnexpectedTypes = unexpectedTypes > 0;
		if(hasUnexpectedTypes) Log.warning("There are " + unexpectedTypes + " methods that were expected to not to have immutability types that do.");
		return hasUnexpectedTypes;
	}
	
	private static boolean gainedTypes(String... tags) {
		int unexpectedTypes = 0;
		for(GraphElement ge : Common.resolve(new NullProgressMonitor(), Common.universe().nodesTaggedWithAny(tags).eval()).nodes()){
			Set<ImmutabilityTypes> defaultTypes = AnalysisUtilities.getDefaultTypes(ge);
			if(ge.taggedWith(ImmutabilityTags.READONLY) && !defaultTypes.contains(ImmutabilityTypes.READONLY)){
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.warning("GraphElement " + ge.address().toAddressString() + " is tagged as READONLY but READONLY is not a valid default for this element.");
				unexpectedTypes++;
			} else if(ge.taggedWith(ImmutabilityTags.POLYREAD) && !defaultTypes.contains(ImmutabilityTypes.POLYREAD)){
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.warning("GraphElement " + ge.address().toAddressString() + " is tagged as POLYREAD but POLYREAD is not a valid default for this element.");
				unexpectedTypes++;
			} else if(ge.taggedWith(ImmutabilityTags.MUTABLE) && !defaultTypes.contains(ImmutabilityTypes.MUTABLE)){
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.warning("GraphElement " + ge.address().toAddressString() + " is tagged as MUTABLE but MUTABLE is not a valid default for this element.");
				unexpectedTypes++;
			}
		}
		boolean hasUnexpectedTypes = unexpectedTypes > 0;
		if(hasUnexpectedTypes) Log.warning("There are " + unexpectedTypes + " nodes that gained unexpected types over thier defaults.");
		return hasUnexpectedTypes;
	}

	/**
	 * Returns true if tagged with two or more of the following (READONLY, POLYREAD, MUTABLE)
	 * @return
	 */
	private static boolean isDoubleTagged() {
		boolean isDoubleTagged = false;
		
		AtlasSet<Node> readonlyPolyread = Common.universe().nodesTaggedWithAll(ImmutabilityTags.READONLY, ImmutabilityTags.POLYREAD).eval().nodes();
		if(readonlyPolyread.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + readonlyPolyread.size() + " nodes that are tagged as " + ImmutabilityTags.READONLY + " and " + ImmutabilityTags.POLYREAD);
		}
		
		AtlasSet<Node> readonlyMutable = Common.universe().nodesTaggedWithAll(ImmutabilityTags.READONLY, ImmutabilityTags.MUTABLE).eval().nodes();
		if(readonlyMutable.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + readonlyMutable.size() + " nodes that are tagged as " + ImmutabilityTags.READONLY + " and " + ImmutabilityTags.MUTABLE);
		}
		
		AtlasSet<Node> polyreadMutable = Common.universe().nodesTaggedWithAll(ImmutabilityTags.POLYREAD, ImmutabilityTags.MUTABLE).eval().nodes();
		if(polyreadMutable.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + polyreadMutable.size() + " nodes that are tagged as " + ImmutabilityTags.POLYREAD + " and " + ImmutabilityTags.MUTABLE);
		}
		
		return isDoubleTagged;
	}
	
	/**
	 * Returns true if there are nodes with the UNTYPED tag
	 * @return
	 */
	private static boolean hasUntypedReferences(){
		boolean hasUntypedReferences = false;
		AtlasSet<Node> untypedReferences = Common.universe().nodesTaggedWithAny(ImmutabilityTags.UNTYPED).eval().nodes();
		if(untypedReferences.size() > 0){
			hasUntypedReferences = true;
			Log.warning("There are " + untypedReferences.size() + " references with no immutability types!");
		}
		return hasUntypedReferences;
	}
	
}
