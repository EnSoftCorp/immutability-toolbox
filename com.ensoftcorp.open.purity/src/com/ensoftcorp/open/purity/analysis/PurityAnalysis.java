package com.ensoftcorp.open.purity.analysis;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.NodeDirection;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;

/**
 * Implements an Atlas native implementation of the context-sensitive method 
 * purity and side-effect analysis proposed in:
 * 1) ReIm & ReImInfer: Checking and Inference of Reference Immutability, OOPSLA 2012 
 * 2) Method Purity and ReImInfer: Method Purity Inference for Java, FSE 2012
 * 
 * @author Ben Holland
 */
public class PurityAnalysis {

	private static final String IMMUTABILITY_QUALIFIER = "IMMUTABILITY_QUALIFIER";
	
	/**
	 * Encodes the immutability qualifications as types 
	 * 
	 * @author Ben Holland
	 */
	public static enum ImmutabilityTypes {
		// note that MUTABLE <: POLYREAD <: READONLY
		MUTABLE("MUTABLE"), POLYREAD("POLYREAD"), READONLY("READONLY");
		
		private String name;
		
		private ImmutabilityTypes(String name){
			this.name = name;
		}
		
		@Override
		public String toString(){
			return name;
		}
		
		public static ImmutabilityTypes getImmutabilityType(String s){
			if(s.equals("MUTABLE")){
				return ImmutabilityTypes.MUTABLE;
			} else if(s.equals("POLYREAD")){
				return ImmutabilityTypes.POLYREAD;
			} else if(s.equals("READONLY")){
				return ImmutabilityTypes.READONLY;
			} else {
				return null;
			}
		}

		/**
		 * Viewpoint adaptation is a concept from Universe Types. Local variables,
		 * callsite receivers, field accesses can be "adapted" to a point of
		 * view of the variable at the left-hand-side.
		 * 
		 * Specifically, 
		 * lhs=? and rhs=mutable => mutable
		 * lhs=? and rhs=readonly => readonly
		 * lhs=q and rhs=polyread => q
		 * 
		 * @param lhs
		 * @param rhs
		 * @return
		 */
		public static ImmutabilityTypes adaptViewpoint(ImmutabilityTypes lhs, ImmutabilityTypes rhs){
			if(rhs == ImmutabilityTypes.MUTABLE){
				// ? and MUTABLE = MUTABLE
				return ImmutabilityTypes.MUTABLE;
			} else if(rhs == ImmutabilityTypes.READONLY){
				// ? and READONLY = READONLY
				return ImmutabilityTypes.READONLY;
			} else {
				// rhs must be ImmutabilityTypes.POLYREAD
				// q and POLYREAD = q
				return lhs;
			}
		}
	}
	
	public static double run(){
		long start = System.nanoTime();
		runAnalysis();
		long stop = System.nanoTime();
		return (stop-start)/1000.0/1000.0;
	}
	
	private static void runAnalysis(){
		TreeSet<GraphElement> worklist = new TreeSet<GraphElement>();
		
		// add all new instantiations to worklist
		Q newRefs = Common.universe().nodesTaggedWithAny(XCSG.Instantiation, XCSG.ArrayInstantiation);
		for(GraphElement newRef : newRefs.eval().nodes()){
			worklist.add(newRef);
		}
	
		// add all parameters to worklist
		Q parameters = Common.universe().nodesTaggedWithAny(XCSG.Parameter);
		for(GraphElement parameter : parameters.eval().nodes()){
			worklist.add(parameter);
		}
		
		while(!worklist.isEmpty()){
			GraphElement workItem = worklist.pollFirst();
			worklist.addAll(applyInferenceRules(workItem));
		}
	}
	
	private static Set<GraphElement> applyInferenceRules(GraphElement workItem){
		Graph dfGraph = Common.universe().edgesTaggedWithAny(XCSG.DataFlow_Edge).eval();
		Set<GraphElement> result = new HashSet<GraphElement>();
		
		GraphElement from = workItem;
		AtlasSet<GraphElement> outEdges = dfGraph.edges(from, NodeDirection.OUT);

		// for each assignment 
		for(GraphElement edge : outEdges){
			GraphElement to = edge.getNode(EdgeDirection.TO);
			
			if(to.taggedWith(XCSG.Assignment)){
				// Type Rule 1 - TNEW
				if(from.taggedWith(XCSG.Instantiation) || from.taggedWith(XCSG.ArrayInstantiation)){
					setTypeQualifier(to, ImmutabilityTypes.MUTABLE);
					result.add(to);
				} else {
					if(to.taggedWith(XCSG.Field)){
						// Type Rule 3 - TWRITE
						
					} else {
						// Type Rule 2 - TASSIGN
						ImmutabilityTypes fromType = getTypeQualifier(from);
						ImmutabilityTypes toType = getTypeQualifier(to);
						// of the from type is more generic than the to type
						// then Type Rule 2 is violated, so the to type must be
						// made at least as generic as the from type
						if(fromType.compareTo(toType) > 0){
							setTypeQualifier(to, fromType);
							result.add(to);
						}
					}
				}
			}
			
		}
		
		// TODO: implement
		return null;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	private static boolean setTypeQualifier(GraphElement ge, ImmutabilityTypes qualifier){
		if(getTypeQualifier(ge) == qualifier){
			return false;
		} else {
			ge.putAttr(IMMUTABILITY_QUALIFIER, qualifier);
			return true;
		}
	}
	
	public static ImmutabilityTypes getTypeQualifier(GraphElement ge){
		if(ge.hasAttr(IMMUTABILITY_QUALIFIER)){
			return ImmutabilityTypes.getImmutabilityType(ge.getAttr(IMMUTABILITY_QUALIFIER).toString());
		} else {
			return null;
		}
	}
	
	/**
	 * Returns true if the method is pure
	 * @param method
	 */
	public static boolean isPure(GraphElement method){
		if(!method.taggedWith(XCSG.Method)){
			return false;
		} else if(isPureDefault(method)){
			return true;
		} else {
			boolean isPure = true;
			// TODO: check if "this" receiver object in any callsite is not mutable
			// TODO: check if parameter is not mutable
			// TODO: check if static immutability type is not mutable
			return isPure;
		}
	}
	
	private static boolean isPureDefault(GraphElement method){
		// from : https://github.com/SoftwareEngineeringToolDemos/FSE-2012-ReImInfer/blob/master/inference-framework/checker-framework/checkers/src/checkers/inference/reim/ReimChecker.java
//		defaultPurePatterns.add(Pattern.compile(".*\\.equals\\(java\\.lang\\.Object\\)$"));
//        defaultPurePatterns.add(Pattern.compile(".*\\.hashCode\\(\\)$"));
//        defaultPurePatterns.add(Pattern.compile(".*\\.toString\\(\\)$"));
//        defaultPurePatterns.add(Pattern.compile(".*\\.compareTo\\(.*\\)$"));
		return false;
	}
	
	private static boolean isDefaultReadonlyType(GraphElement type) {
		// null types
		GraphElement nullType = Common.universe().nodesTaggedWithAny(XCSG.Java.NullType).eval().nodes().getFirst();
		if (type.equals(nullType)) {
			return true;
		}
		
		// primitive types
		if(type.taggedWith(XCSG.Primitive)){
			return true;
		}
		
		// autoboxing
		GraphElement integerType = Common.typeSelect("java.lang", "Integer").eval().nodes().getFirst();
		GraphElement longType = Common.typeSelect("java.lang", "Long").eval().nodes().getFirst();
		GraphElement shortType = Common.typeSelect("java.lang", "Short").eval().nodes().getFirst();
		GraphElement booleanType = Common.typeSelect("java.lang", "Boolean").eval().nodes().getFirst();
		GraphElement byteType = Common.typeSelect("java.lang", "Byte").eval().nodes().getFirst();
		GraphElement doubleType = Common.typeSelect("java.lang", "Double").eval().nodes().getFirst();
		GraphElement floatType = Common.typeSelect("java.lang", "Float").eval().nodes().getFirst();
		GraphElement characterType = Common.typeSelect("java.lang", "Character").eval().nodes().getFirst();
		if (type.equals(integerType) 
				|| type.equals(longType) 
				|| type.equals(shortType) 
				|| type.equals(booleanType) 
				|| type.equals(byteType)
				|| type.equals(doubleType) 
				|| type.equals(floatType) 
				|| type.equals(characterType)) {
			return true;
		}
		
		// strings are a special case
		GraphElement stringType = Common.typeSelect("java.lang", "String").eval().nodes().getFirst();
		if(type.equals(stringType)){
			return true;
		}
		
		return false;
	}
	
}
