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
 * Reference 1: ReIm & ReImInfer: Checking and Inference of Reference Immutability, OOPSLA 2012 
 * Reference 2: Method Purity and ReImInfer: Method Purity Inference for Java, FSE 2012
 * 
 * @author Ben Holland, Ganesh Santhanam
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
		// <: denotes a subtype relationship
		// MUTABLE is a subtype of POLYREAD and POLYREAD is a subtype of READONLY
		// MUTABLE is the most specific type and READONLY is the most generic type
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
		
		Q parameters = Common.universe().nodesTaggedWithAny(XCSG.Parameter);
		Q masterReturns = Common.universe().nodesTaggedWithAny(XCSG.MasterReturn);
		Q instanceVariables = Common.universe().nodesTaggedWithAny(XCSG.InstanceVariable);
		Q thisNodes = Common.universe().nodesTaggedWithAll(XCSG.InstanceMethod).children().nodesTaggedWithAny(XCSG.Identity);
		
		// create default types on each tracked item
		// note local variables may also get tracked, but only if need be during the analysis
		for(GraphElement trackedItem : parameters.union(masterReturns, instanceVariables, thisNodes).eval().nodes()){
			getTypes(trackedItem);
		}
		
		TreeSet<GraphElement> worklist = new TreeSet<GraphElement>();

		// add all assignments to worklist
		Q assignments = Common.universe().nodesTaggedWithAny(XCSG.Assignment);
		for(GraphElement assignment : assignments.eval().nodes()){
			worklist.add(assignment);
		}
		
		while(!worklist.isEmpty()){
			GraphElement workItem = worklist.pollFirst();
			worklist.addAll(applyInferenceRules(workItem));
		}
	}
	
	private static Set<GraphElement> applyInferenceRules(GraphElement workItem){
		Graph localDFGraph = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow).eval();
		Graph interproceduralDFGraph = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow).eval();
		Graph instanceVariableAccessedGraph = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed).eval();
		Set<GraphElement> workItems = new HashSet<GraphElement>();
		
		// consider incoming data flow edges
		// incoming edges represent a read relationship in an assignment
		GraphElement to = workItem;
		AtlasSet<GraphElement> inEdges = localDFGraph.edges(to, NodeDirection.IN);
		for(GraphElement edge : inEdges){
			GraphElement from = edge.getNode(EdgeDirection.FROM);

			boolean involvesField = false;
			
			// TWRITE
			if(to.taggedWith(XCSG.InstanceVariableAssignment)){
				// Type Rule 3 - TWRITE
				// let, x.f = y

				// Reference (y) -LocalDataFlow-> InstanceVariableAssignment (.f)
				GraphElement y = from;
				GraphElement instanceVariableAssignment = to; 
				
				// InstanceVariableAssignment (.f) -InterproceduralDataFlow-> InstanceVariable (f)
				GraphElement interproceduralEdgeToField = interproceduralDFGraph.edges(instanceVariableAssignment, NodeDirection.OUT).getFirst();
				GraphElement f = interproceduralEdgeToField.getNode(EdgeDirection.TO);
				
				// Reference (x) -InstanceVariableAccessed-> InstanceVariableAssignment (.f)
				GraphElement instanceVariableAccessedEdge = instanceVariableAccessedGraph.edges(instanceVariableAssignment, NodeDirection.IN).getFirst();
				GraphElement x = instanceVariableAccessedEdge.getNode(EdgeDirection.FROM);

				workItems.addAll(handleFieldWrite(x, f, y));
				
				involvesField = true;
			}
//			
//			// TREAD
//			// x = y.f
//			if(from.taggedWith(XCSG.InstanceVariableValue)){
//				involvesField = true;
//			}
			
			
			// TASSIGN
			if(!involvesField){
				GraphElement x = to;
				GraphElement y = from;
				workItems.addAll(handleAssignment(x, y));
			}
		}
		
		
		
		
		
		
//		// consider outgoing data flow edges
//		// outgoing edges represent a write relationship in an assignment
//		GraphElement from = workItem;
//		AtlasSet<GraphElement> outEdges = dfGraph.edges(from, NodeDirection.OUT);
//		for(GraphElement edge : outEdges){
//			GraphElement to = edge.getNode(EdgeDirection.TO);
//			if(to.taggedWith(XCSG.Assignment)){
//				if(to.taggedWith(XCSG.InstanceVariableAssignment)){
//					// Type Rule 3 - TWRITE
//					// let, x.f = y
//					// note InstanceVariableAssignment -DataFlow-> InstanceVariable (field)
//					GraphElement y = from;
//					GraphElement instanceVariableAssignment = to;
//					GraphElement f = dfGraph.edges(instanceVariableAssignment, NodeDirection.OUT).getFirst();
//					// note variable -InstanceVariableAccessed-> InstanceVariableAssignment
//					GraphElement x = instanceVariableAccessedGraph.edges(instanceVariableAssignment, NodeDirection.IN).getFirst();
//					workItems.addAll(handleFieldWrite(x, f, y));
//				} else {
//					// Type Rule 2 - TASSIGN
//					// let, x = y
//					GraphElement x = to;
//					GraphElement y = from;
//					workItems.addAll(handleAssignment(x, y));
//				}
//			}
//		}
//		
//		// consider incoming data flow edges
//		// incoming edges represent a read relationship in an assignment
//		GraphElement to = workItem;
//		AtlasSet<GraphElement> inEdges = dfGraph.edges(to, NodeDirection.IN);
//		for(GraphElement edge : inEdges){
//			from = edge.getNode(EdgeDirection.FROM);
//			if(to.taggedWith(XCSG.Assignment)){
//				if(from.taggedWith(XCSG.InstanceVariable)){
//					// Type Rule 4 - TREAD
//					// let, x = y.f
//					GraphElement f = from;
//					// note InstanceVariable (field) -DataFlow-> InstanceVariableValue
//					GraphElement instanceVariableValue = to;
//					GraphElement cfBlock = Common.toQ(instanceVariableValue).parent().eval().nodes().getFirst();
//					GraphElement x = Common.toQ(cfBlock).children().nodesTaggedWithAny(XCSG.Assignment).eval().nodes().getFirst();
//					GraphElement y = instanceVariableAccessedGraph.edges(f, NodeDirection.IN).getFirst();
//					workItems.addAll(handleFieldRead(x, y, f));
//				} else {
//					// Type Rule 2 - TASSIGN
//					// let, x = y
//					GraphElement x = to;
//					GraphElement y = from;
//					workItems.addAll(handleAssignment(x, y));
//				}	
//			}
//		}
		
		return workItems;
	}

	/**
	 * Solves and satisfies constraints for Type Rule 2 - TASSIGN
	 * Let, x = y
	 * 
	 * @param x The reference being written to
	 * @param y The reference be read from
	 * @return
	 */
	private static Set<GraphElement> handleAssignment(GraphElement x, GraphElement y) {
		Set<GraphElement> workItems = new HashSet<GraphElement>();
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		
		// process s(x)
		Set<ImmutabilityTypes> xTypesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			for(ImmutabilityTypes yType : yTypes){
				if(xType.compareTo(yType) >= 0){
					isSatisfied = true;
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			workItems.add(x);
		}
		
		// process s(y)
		Set<ImmutabilityTypes> yTypesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			for(ImmutabilityTypes xType : xTypes){
				if(xType.compareTo(yType) >= 0){
					isSatisfied = true;
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		removeTypes(y, yTypesToRemove);
		return workItems;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 3 - TWRITE
	 * Let, x.f = y
	 * 
	 * @param x The receiver object
	 * @param f The field of the receiver object being written to
	 * @param y The reference being read from
	 * @return Returns a set of GraphElements whose set of ImmutabilityTypes have changed
	 */
	private static Set<GraphElement> handleFieldWrite(GraphElement x, GraphElement f, GraphElement y) {
		Set<GraphElement> workItems = new HashSet<GraphElement>();
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		Set<ImmutabilityTypes> fTypes = getTypes(f);
		// x must be mutable
		removeTypes(x, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
		ImmutabilityTypes xType = ImmutabilityTypes.MUTABLE;
		
		// process s(y)
		Set<ImmutabilityTypes> yTypesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			for(ImmutabilityTypes fType : fTypes){
				ImmutabilityTypes xAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, fType);
				if(xAdaptedF.compareTo(yType) >= 0){
					isSatisfied = true;
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		removeTypes(y, yTypesToRemove);
		
		// process s(f)
		Set<ImmutabilityTypes> fTypesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes fType : fTypes){
			boolean isSatisfied = false;
			for(ImmutabilityTypes yType : yTypes){
				ImmutabilityTypes xAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, fType);
				if(xAdaptedF.compareTo(yType) >= 0){
					isSatisfied = true;
				}
			}
			if(!isSatisfied){
				fTypesToRemove.add(fType);
			}
		}
		if(removeTypes(f, fTypesToRemove)){
			workItems.add(f);
		}
		return workItems;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 4 - TREAD
	 * Let, x = y.f
	 * 
	 * @param x The reference being written to
	 * @param y The receiver object
	 * @param f The field of the receiver object being read from
	 * @return Returns a set of GraphElements whose set of ImmutabilityTypes have changed
	 */
	private static Set<GraphElement> handleFieldRead(GraphElement x, GraphElement y, GraphElement f) {
		Set<GraphElement> workItems = new HashSet<GraphElement>();
		Set<ImmutabilityTypes> fTypes = getTypes(f);
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		
		// process s(x)
		Set<ImmutabilityTypes> xTypesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			for(ImmutabilityTypes yType : yTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
					}
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			workItems.add(x);
		}
		
		// process s(y)
		Set<ImmutabilityTypes> yTypesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
					}
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			workItems.add(y);
		}
		
		// process s(f)
		Set<ImmutabilityTypes> fTypesToRemove = new HashSet<ImmutabilityTypes>();
		for(ImmutabilityTypes fType : fTypes){
			boolean isSatisfied = false;
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes yType : yTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
					}
				}
			}
			if(!isSatisfied){
				fTypesToRemove.add(fType);
			}
		}
		if(removeTypes(f, fTypesToRemove)){
			workItems.add(f);
		}
		return workItems;
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
			
			if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
				// Type Rule 1 - TNEW
				// return type of a constructor is only mutable
				// x = new C(); // no effect on qualifier to x
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			} else if(ge.taggedWith(XCSG.MasterReturn)){
				// Section 2.4 of Reference 1
				// "Method returns are initialized S(ret) = {readonly, polyread} for each method m"
				qualifiers.add(ImmutabilityTypes.POLYREAD);
				qualifiers.add(ImmutabilityTypes.READONLY);
			} else if(ge.taggedWith(XCSG.Field)){
				// Section 2.4 of Reference 1
				// "Fields are initialized to S(f) = {readonly, polyread}"
				qualifiers.add(ImmutabilityTypes.POLYREAD);
				qualifiers.add(ImmutabilityTypes.READONLY);
			} else {
				// Section 2.4 of Reference 1
				// "All other references are initialized to the maximal
				// set of qualifiers, i.e. S(x) = {readonly, polyread, mutable}"
				qualifiers.add(ImmutabilityTypes.POLYREAD);
				qualifiers.add(ImmutabilityTypes.READONLY);
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			}

			ge.putAttr(IMMUTABILITY_TYPES, qualifiers);
			return qualifiers;
		}
	}
	
	/**
	 * Returns the least common ancestor for two sets of ImmutabilityTypes
	 * @param a
	 * @param b
	 * @return
	 */
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
	public static boolean isPureMethod(GraphElement method){
		if(!method.taggedWith(XCSG.Method)){
			return false;
		} else if(isPureMethodDefault(method)){
			return true;
		} else {
			boolean isPure = true;
			// TODO: check if "this" receiver object in any callsite is not mutable
			// TODO: check if parameter is not mutable
			// TODO: check if static immutability type is not mutable
			return isPure;
		}
	}
	
	private static boolean isPureMethodDefault(GraphElement method){
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
