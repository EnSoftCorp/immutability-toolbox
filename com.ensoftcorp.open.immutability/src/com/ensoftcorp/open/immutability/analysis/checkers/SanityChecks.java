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
		
		// this check is expensive and often wrong...type of edges pull in a lot of things
//		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Checking that known readonly types are typed as readonly...");
//		resultsAreSane &= !defaultReadonlyTypesAreReadonly();
		
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
	
//	/**
//	 * Checks that readonly types are actually readonly
//	 * @return
//	 */
//	private static boolean defaultReadonlyTypesAreReadonly(){
//		Q readOnlyTypes = Common.typeSelect("java.lang", "Integer")
//				.union(Common.typeSelect("java.lang", "Long"), 
//					   Common.typeSelect("java.lang", "Short"), 
//					   Common.typeSelect("java.lang", "Boolean"),
//					   Common.typeSelect("java.lang", "Byte"),
//					   Common.typeSelect("java.lang", "Double"),
//					   Common.typeSelect("java.lang", "Float"),
//					   Common.typeSelect("java.lang", "Character"),
//					   Common.typeSelect("java.lang", "String"),
//					   Common.typeSelect("java.lang", "Number"),
//					   Common.typeSelect("java.util.concurrent.atomic", "AtomicInteger"),
//					   Common.typeSelect("java.util.concurrent.atomic", "AtomicLong"),
//					   Common.typeSelect("java.math", "BigDecimal"),
//					   Common.typeSelect("java.math", "BigInteger"),
//					   Common.universe().nodesTaggedWithAny(XCSG.Java.NullType));
//
//		Q typeOfEdges = Common.universe().edgesTaggedWithAny(XCSG.TypeOf);
//		
//		AtlasHashSet<GraphElement> defaultReadonlyTypes = new AtlasHashSet<GraphElement>();
//		Q readonlyReferences = typeOfEdges.predecessors(readOnlyTypes);
//		Q identities = readonlyReferences.nodesTaggedWithAny(XCSG.Identity);
//		Q arrayComponents = readonlyReferences.nodesTaggedWithAny(XCSG.ArrayComponents);
//		// consider constructors?
//		defaultReadonlyTypes.addAll(readonlyReferences.difference(identities, arrayComponents).eval().nodes());
//		defaultReadonlyTypes.addAll(Common.universe().nodesTaggedWithAny(XCSG.Null, XCSG.Literal, XCSG.Operator).eval().nodes());
//		
//		int unexpectedTypes = 0; 
//		for(GraphElement ge : defaultReadonlyTypes){
//			if(ge.taggedWith(XCSG.Null)){
//				// null is a special case, mutations can happen to nulls but its a runtime exception
//				// one might argue this does not change the program state but it does if a runtime exception is thrown!
//				continue;
//			}
//			if(ge.taggedWith(XCSG.Operator)){
//				// we only need to consider operators on-demand so not all operators will actually be typed
//				// but if they are they'd better not be typed as anything but readonly
//				if(ge.taggedWith(ImmutabilityTags.POLYREAD) || ge.taggedWith(ImmutabilityTags.MUTABLE) || ge.taggedWith(ImmutabilityAnalysis.UNTYPED)){
//					if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.warning("Readonly type " + ge.address().toAddressString() + " is not readonly.");
//					unexpectedTypes++;
//				}
//				continue;
//			}
//			if(!ge.taggedWith(ImmutabilityTags.READONLY)){
//				if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.warning("Readonly type " + ge.address().toAddressString() + " is not readonly.");
//				unexpectedTypes++;
//			}
//		}
//		boolean hasUnexpectedTypes = unexpectedTypes > 0;
//		if(hasUnexpectedTypes) Log.warning("There are " + unexpectedTypes + " nodes that were expected to be readonly types that are not.");
//		return hasUnexpectedTypes;
//	}

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
