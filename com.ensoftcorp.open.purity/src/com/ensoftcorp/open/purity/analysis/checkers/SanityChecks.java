package com.ensoftcorp.open.purity.analysis.checkers;

import java.util.HashSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.purity.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.purity.analysis.PurityAnalysis;
import com.ensoftcorp.open.purity.analysis.Utilities;
import com.ensoftcorp.open.purity.log.Log;
import com.ensoftcorp.open.purity.preferences.PurityPreferences;

public class SanityChecks {

	public static boolean run(){
		boolean resultsAreSane = true;
		
		if(!PurityPreferences.isPartialProgramAnalysisEnabled()){
			resultsAreSane &= !isDoubleTagged();
			resultsAreSane &= !methodsDoNotHaveImmutabilityTypes();
		}
		
		resultsAreSane &= !hasUnexpectedTypes();
		resultsAreSane &= !hasUntypedReferences();
		
		return resultsAreSane;
	}
	
	/**
	 * Returns true if there are types on an unexpected node type
	 * @return
	 */
	private static boolean hasUnexpectedTypes() {
		boolean hasUnexpectedTypes = false;
		
		if(gainedTypes(XCSG.Null, XCSG.Literal, XCSG.Primitive, 
								   XCSG.Instantiation, XCSG.ArrayInstantiation, 
								   XCSG.MasterReturn, XCSG.Identity, XCSG.Parameter,
								   XCSG.InstanceVariable, XCSG.ClassVariable,
								   XCSG.Method)){
			hasUnexpectedTypes = true;
		}
		
		if(shouldNotBeTyped(XCSG.InstanceVariableAccess, Utilities.CLASS_VARIABLE_ACCESS)){
			hasUnexpectedTypes = true;
		}
		
		if(defaultReadonlyTypesAreReadonly()){
			hasUnexpectedTypes = true;
		}
		
		return hasUnexpectedTypes;
	}
	
	/**
	 * Checks that methods do not have immutability tags
	 * Only true for whole program analysis (partial program analysis is an exception)
	 * @return
	 */
	private static boolean methodsDoNotHaveImmutabilityTypes(){
		int unexpectedTypes = 0; 
		for(GraphElement ge : Common.universe().nodesTaggedWithAny(XCSG.Method).eval().nodes()){
			if(ge.taggedWith(PurityAnalysis.READONLY) 
					|| ge.taggedWith(PurityAnalysis.POLYREAD) 
					|| ge.taggedWith(PurityAnalysis.MUTABLE)
					|| ge.taggedWith(PurityAnalysis.UNTYPED)
					|| ge.hasAttr(Utilities.IMMUTABILITY_QUALIFIERS)){
						unexpectedTypes++;
			}
		}
		boolean hasUnexpectedTypes = unexpectedTypes > 0;
		if(hasUnexpectedTypes) Log.warning("There are " + unexpectedTypes + " methods that were expected to not to have immutability types that do.");
		return hasUnexpectedTypes;
	}
	
	/**
	 * Checks that readonly types are actually readonly
	 * @return
	 */
	private static boolean defaultReadonlyTypesAreReadonly(){
		Q readOnlyTypes = Common.typeSelect("java.lang", "Integer")
				.union(Common.typeSelect("java.lang", "Long"), 
					   Common.typeSelect("java.lang", "Short"), 
					   Common.typeSelect("java.lang", "Boolean"),
					   Common.typeSelect("java.lang", "Byte"),
					   Common.typeSelect("java.lang", "Double"),
					   Common.typeSelect("java.lang", "Float"),
					   Common.typeSelect("java.lang", "Character"),
					   Common.typeSelect("java.lang", "String"),
					   Common.typeSelect("java.lang", "Number"),
					   Common.typeSelect("java.util.concurrent.atomic", "AtomicInteger"),
					   Common.typeSelect("java.util.concurrent.atomic", "AtomicLong"),
					   Common.typeSelect("java.math", "BigDecimal"),
					   Common.typeSelect("java.math", "BigInteger"),
					   Common.universe().nodesTaggedWithAny(XCSG.Java.NullType));

		Q typeOfEdges = Common.universe().edgesTaggedWithAny(XCSG.TypeOf);
		
		AtlasHashSet<GraphElement> defaultReadonlyTypes = new AtlasHashSet<GraphElement>();
		Q readonlyReferences = typeOfEdges.predecessors(readOnlyTypes);
		Q identities = readonlyReferences.nodesTaggedWithAny(XCSG.Identity);
		defaultReadonlyTypes.addAll(readonlyReferences.difference(identities).eval().nodes());
		defaultReadonlyTypes.addAll(Common.universe().nodesTaggedWithAny(XCSG.Null, XCSG.Literal, XCSG.Operator).eval().nodes());
		
		int unexpectedTypes = 0; 
		for(GraphElement ge : defaultReadonlyTypes){
			if(ge.taggedWith(XCSG.Null)){
				// null is a special case, mutations can happen to nulls but its a runtime exception
				// one might argue this does not change the program state but it does if a runtime exception is thrown!
				continue;
			}
			if(!ge.taggedWith(PurityAnalysis.READONLY)){
				if(PurityPreferences.isDebugLoggingEnabled()) Log.warning("Readonly type " + ge.address().toAddressString() + " is not readonly.");
				unexpectedTypes++;
			}
		}
		boolean hasUnexpectedTypes = unexpectedTypes > 0;
		if(hasUnexpectedTypes) Log.warning("There are " + unexpectedTypes + " nodes that were expected to be readonly types that are not.");
		return hasUnexpectedTypes;
	}
	
	private static boolean shouldNotBeTyped(String... tags){
		int unexpectedTypes = 0;
		for(GraphElement ge : Common.universe().nodesTaggedWithAny(tags).eval().nodes()){
			if(ge.taggedWith(PurityAnalysis.READONLY) 
			|| ge.taggedWith(PurityAnalysis.POLYREAD) 
			|| ge.taggedWith(PurityAnalysis.MUTABLE)
			|| ge.taggedWith(PurityAnalysis.UNTYPED)
			|| ge.hasAttr(Utilities.IMMUTABILITY_QUALIFIERS)){
				unexpectedTypes++;
			}
		}
		boolean hasUnexpectedTypes = unexpectedTypes > 0;
		if(hasUnexpectedTypes) Log.warning("There are " + unexpectedTypes + " nodes that gained unexpected types which should not be typed.");
		return hasUnexpectedTypes;
	}

	private static boolean gainedTypes(String... tags) {
		int unexpectedTypes = 0;
		for(GraphElement ge : Common.universe().nodesTaggedWithAny(tags).eval().nodes()){
			Set<ImmutabilityTypes> defaultTypes = PurityAnalysis.getDefaultTypes(ge);
			Set<ImmutabilityTypes> finalTypes = new HashSet<ImmutabilityTypes>(Utilities.getTypes(ge));
			finalTypes.removeAll(defaultTypes);
			if(!finalTypes.isEmpty()){
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
		
		AtlasSet<GraphElement> readonlyPolyread = Common.universe().nodesTaggedWithAll(PurityAnalysis.READONLY, PurityAnalysis.POLYREAD).eval().nodes();
		if(readonlyPolyread.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + readonlyPolyread.size() + " nodes that are tagged as " + PurityAnalysis.READONLY + " and " + PurityAnalysis.POLYREAD);
		}
		
		AtlasSet<GraphElement> readonlyMutable = Common.universe().nodesTaggedWithAll(PurityAnalysis.READONLY, PurityAnalysis.MUTABLE).eval().nodes();
		if(readonlyMutable.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + readonlyMutable.size() + " nodes that are tagged as " + PurityAnalysis.READONLY + " and " + PurityAnalysis.MUTABLE);
		}
		
		AtlasSet<GraphElement> polyreadMutable = Common.universe().nodesTaggedWithAll(PurityAnalysis.POLYREAD, PurityAnalysis.MUTABLE).eval().nodes();
		if(polyreadMutable.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + polyreadMutable.size() + " nodes that are tagged as " + PurityAnalysis.POLYREAD + " and " + PurityAnalysis.MUTABLE);
		}
		
		return isDoubleTagged;
	}
	
	/**
	 * Returns true if there are nodes with the UNTYPED tag
	 * @return
	 */
	private static boolean hasUntypedReferences(){
		boolean hasUntypedReferences = false;
		AtlasSet<GraphElement> untypedReferences = Common.universe().nodesTaggedWithAny(PurityAnalysis.UNTYPED).eval().nodes();
		if(untypedReferences.size() > 0){
			hasUntypedReferences = true;
			Log.warning("There are " + untypedReferences.size() + " references with no immutability types!");
		}
		return hasUntypedReferences;
	}
	
}
