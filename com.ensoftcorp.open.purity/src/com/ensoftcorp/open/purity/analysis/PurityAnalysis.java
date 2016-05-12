package com.ensoftcorp.open.purity.analysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
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

	private static final String IMMUTABILITY_TYPES = "IMMUTABILITY_QUALIFIER";
	
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

		public static Set<ImmutabilityTypes> getAdaptedFieldViewpoints(Set<ImmutabilityTypes> contexts, Set<ImmutabilityTypes> declarations){
			HashSet<ImmutabilityTypes> adaptedFieldViewpoints = new HashSet<ImmutabilityTypes>();
			for(ImmutabilityTypes context : contexts){
				for(ImmutabilityTypes declaration : declarations){
					adaptedFieldViewpoints.add(getAdaptedFieldViewpoint(context, declaration));
				}
			}
			return adaptedFieldViewpoints;
		}
		
		/**
		 * Viewpoint adaptation is a concept from Universe Types.
		 * 
		 * Specifically for fields, 
		 * context=? and declaration=readonly => readonly
		 * context=q and declaration=mutable => q
		 * context=q and declaration=polyread => q
		 * 
		 * @param context
		 * @param declared
		 * @return
		 */
		public static ImmutabilityTypes getAdaptedFieldViewpoint(ImmutabilityTypes context, ImmutabilityTypes declaration){
			if(declaration == ImmutabilityTypes.READONLY){
				// ? and READONLY = READONLY
				return ImmutabilityTypes.READONLY;
			} else if(declaration == ImmutabilityTypes.MUTABLE){
				// q and MUTABLE = q
				return context;
			} else {
				// declared must be ImmutabilityTypes.POLYREAD
				// q and POLYREAD = q
				return context;
			}
		}
		
		public static Set<ImmutabilityTypes> getAdaptedMethodViewpoints(Set<ImmutabilityTypes> contexts, Set<ImmutabilityTypes> declarations){
			HashSet<ImmutabilityTypes> adaptedMethodViewpoints = new HashSet<ImmutabilityTypes>();
			for(ImmutabilityTypes context : contexts){
				for(ImmutabilityTypes declaration : declarations){
					adaptedMethodViewpoints.add(getAdaptedMethodViewpoint(context, declaration));
				}
			}
			return adaptedMethodViewpoints;
		}
		
		/**
		 * Viewpoint adaptation is a concept from Universe Types.
		 * 
		 * Specifically, 
		 * context=? and declaration=readonly => readonly
		 * context=? and declaration=mutable => mutable
		 * context=q and declaration=polyread => q
		 * 
		 * @param context
		 * @param declared
		 * @return
		 */
		public static ImmutabilityTypes getAdaptedMethodViewpoint(ImmutabilityTypes context, ImmutabilityTypes declaration){
			if(declaration == ImmutabilityTypes.READONLY){
				// ? and READONLY = READONLY
				return ImmutabilityTypes.READONLY;
			} else if(declaration == ImmutabilityTypes.MUTABLE){
				// ? and MUTABLE = MUTABLE
				return ImmutabilityTypes.MUTABLE;
			} else {
				// declared must be ImmutabilityTypes.POLYREAD
				// q and POLYREAD = q
				return context;
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
		Graph instanceVariableAccessedGraph = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed).eval();
		Set<GraphElement> workItems = new HashSet<GraphElement>();
		
		GraphElement from = workItem;
		AtlasSet<GraphElement> outEdges = dfGraph.edges(from, NodeDirection.OUT);

		// for each assignment 
		for(GraphElement edge : outEdges){
			GraphElement to = edge.getNode(EdgeDirection.TO);
			
			if(to.taggedWith(XCSG.Assignment)){
				if(from.taggedWith(XCSG.Instantiation) || from.taggedWith(XCSG.ArrayInstantiation)){
					// Type Rule 1 - TNEW
					// return type of a constructor is mutable
					// x = new C(); // no effect on qualifier to x
				}
				
				if(to.taggedWith(XCSG.InstanceVariableAssignment)){
					// Type Rule 3 - TWRITE
					// let, x.f = y
					// note InstanceVariableAssignment -DataFlow-> InstanceVariable (field)
					GraphElement y = from;
					Set<ImmutabilityTypes> yTypes = getTypes(y);
					GraphElement instanceVariableAssignment = to;
					GraphElement f = dfGraph.edges(instanceVariableAssignment, NodeDirection.OUT).getFirst();
					Set<ImmutabilityTypes> fTypes = getTypes(f);
					// note variable -InstanceVariableAccessed-> InstanceVariableAssignment
					GraphElement x = instanceVariableAccessedGraph.edges(instanceVariableAssignment, NodeDirection.IN).getFirst();
					Set<ImmutabilityTypes> xTypes = getTypes(x);
					
					// x must be mutable
					if(removeTypes(x, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY)){
						workItems.add(x);
					}
					
					// some types of y may need to be removed to satisfy constraints
					Set<ImmutabilityTypes> xAdaptedFViewpointTypes = ImmutabilityTypes.getAdaptedFieldViewpoints(xTypes, fTypes);
					Set<ImmutabilityTypes> typesToRemoveFromY = new HashSet<ImmutabilityTypes>();
					ImmutabilityTypes lca = leastCommonAncestor(xAdaptedFViewpointTypes, yTypes);
					for(ImmutabilityTypes yType : yTypes){
						if(yType.compareTo(lca) > 0){
							typesToRemoveFromY.add(yType);
						}
					}
					if(removeTypes(y, typesToRemoveFromY)){
						workItems.add(y);
					}
				} else {
					// Type Rule 2 - TASSIGN
					// let, x = y;
					GraphElement x = to;
					GraphElement y = from;
					Set<ImmutabilityTypes> xTypes = getTypes(x);
					Set<ImmutabilityTypes> yTypes = getTypes(y);
					// note that it is only possible to remove types from y
					Set<ImmutabilityTypes> typesToRemoveFromY = new HashSet<ImmutabilityTypes>();
					ImmutabilityTypes lca = leastCommonAncestor(xTypes, yTypes);
					for(ImmutabilityTypes yType : yTypes){
						if(yType.compareTo(lca) > 0){
							typesToRemoveFromY.add(yType);
						}
					}
					if(removeTypes(y, typesToRemoveFromY)){
						workItems.add(y);
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
	private static boolean removeTypes(GraphElement ge, Set<ImmutabilityTypes> typesToRemove){
		Set<ImmutabilityTypes> typeSet = getTypes(ge);
		return typeSet.removeAll(typesToRemove);
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	private static boolean removeTypes(GraphElement ge, ImmutabilityTypes... types){
		Set<ImmutabilityTypes> typeSet = getTypes(ge);
		HashSet<ImmutabilityTypes> typesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes type : types){
			typesToRemove.add(type);
		}
		return typeSet.removeAll(typesToRemove);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<ImmutabilityTypes> getTypes(GraphElement ge){
		if(ge.hasAttr(IMMUTABILITY_TYPES)){
			return (Set<ImmutabilityTypes>) ge.getAttr(IMMUTABILITY_TYPES);
		} else {
			HashSet<ImmutabilityTypes> qualifiers = new HashSet<ImmutabilityTypes>();
			qualifiers.add(ImmutabilityTypes.MUTABLE);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.READONLY);
			ge.putAttr(IMMUTABILITY_TYPES, qualifiers);
			return qualifiers;
		}
	}
	
	public static ImmutabilityTypes leastCommonAncestor(Set<ImmutabilityTypes> a, Set<ImmutabilityTypes> b){
		HashSet<ImmutabilityTypes> commonTypes = new HashSet<ImmutabilityTypes>();
		commonTypes.addAll(a);
		commonTypes.retainAll(b);
		LinkedList<ImmutabilityTypes> orderedTypes = new LinkedList<ImmutabilityTypes>();
		orderedTypes.addAll(commonTypes);
		Collections.sort(orderedTypes);
		return orderedTypes.getLast();
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
