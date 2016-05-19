package com.ensoftcorp.open.purity.analysis;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.NodeDirection;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.log.Log;
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

	/**
	 * Tag applied to "pure" methods
	 */
	public static final String PURE_METHOD = "PURE";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "readonly" immutability
	 * Readonly means that in any context the reference is readonly (never mutated)
	 */
	public static final String READONLY = "READONLY";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "polyread" immutability
	 * Polyread means that depending on the context a reference may be mutable or readonly
	 */
	public static final String POLYREAD = "POLYREAD";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "mutable" immutability
	 * Mutable means that in any context the reference is mutable
	 */
	public static final String MUTABLE = "MUTABLE";
	
	/**
	 * Theoretically each item should only ever be visited at most 3 times
	 * So after 4 iterations the fixed point should have been reached
	 */
	private static final int MAX_ITERATIONS = 4;
	
	/**
	 * Used as an attribute key to temporarily compute the potential immutability qualifiers
	 */
	private static final String IMMUTABILITY_QUALIFIERS = "IMMUTABILITY_QUALIFIERS";
	
	/**
	 * Enables general logging to the Atlas log
	 */
	private static final boolean LOG_ENABLED = true;

	/**
	 * Enables inference rule logging to the Atlas log
	 */
	private static final boolean LOG_INFERENCE_RULES_ENABLED = false;
	
	/**
	 * Enables verbose debug logging to the Atlas log
	 */
	private static final boolean DEBUG_LOG_ENABLED = false;
	
	/**
	 * Encodes the immutability qualifications as types 
	 * 
	 * @author Ben Holland
	 */
	private static enum ImmutabilityTypes {
		// note that MUTABLE <: POLYREAD <: READONLY
		// <: denotes a subtype relationship
		// MUTABLE is a subtype of POLYREAD and POLYREAD is a subtype of READONLY
		// MUTABLE is the most specific type and READONLY is the most generic type
		MUTABLE(PurityAnalysis.MUTABLE), POLYREAD(PurityAnalysis.POLYREAD), READONLY(PurityAnalysis.READONLY);
		
		private String name;
		
		private ImmutabilityTypes(String name){
			this.name = name;
		}
		
		@Override
		public String toString(){
			return name;
		}
		
		/**
		 * Viewpoint adaptation is a concept from Universe Types, 
		 * it deals with context-sensitivity issues.
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
		public static ImmutabilityTypes getAdaptedFieldViewpoint(ImmutabilityTypes context, ImmutabilityTypes declaration){
			// see https://github.com/SoftwareEngineeringToolDemos/FSE-2012-ReImInfer/blob/master/inference-framework/checker-framework/checkers/src/checkers/inference/reim/ReimChecker.java#L216
//			if(declaration == ImmutabilityTypes.READONLY){
//				// ? and READONLY = READONLY
//				return ImmutabilityTypes.READONLY;
//			} else if(declaration == ImmutabilityTypes.MUTABLE){
//				// q and MUTABLE = q
//				return context;
//			} else {
//				// declared must be ImmutabilityTypes.POLYREAD
//				// q and POLYREAD = q
//				return context;
//			}
			
			// using the more accurate ReIm' definition of field viewpoint adaptation
			// see https://github.com/proganalysis/type-inference/blob/master/inference-framework/checker-framework/checkers/src/checkers/inference2/reim/ReimChecker.java#L272
			return getAdaptedMethodViewpoint(context, declaration);
		}
		
		/**
		 * Viewpoint adaptation is a concept from Universe Types, 
		 * it deals with context-sensitivity issues.
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
	
	/**
	 * Runs the side effect (purity) analysis
	 * @return Returns the time in milliseconds taken to complete the analysis
	 */
	public static boolean run(){
		if(LOG_ENABLED) Log.info("Purity analysis started");
		long start = System.nanoTime();
		boolean successful = runAnalysis();
		long stop = System.nanoTime();
		double runtime = (stop-start)/1000.0/1000.0;
		if(LOG_ENABLED) {
			if(successful){
				Log.info("Purity analysis completed successfully in " + runtime + " ms");
			} else {
				Log.warning("Purity analysis completed unsuccessfully in " + runtime + " ms");
			}
		}
		return successful;
	}
	
	/**
	 * Runs the side effect (purity) analysis
	 */
	private static boolean runAnalysis(){
		TreeSet<GraphElement> worklist = new TreeSet<GraphElement>();

		// add all assignments to worklist
		Q assignments = Common.resolve(new NullProgressMonitor(), Common.universe().nodesTaggedWithAny(XCSG.Assignment));
		for(GraphElement assignment : assignments.eval().nodes()){
			worklist.add(assignment);
		}
		
		boolean successful = false;
		int iteration = 1;
		while(true){
			if(LOG_ENABLED) Log.info("Purity analysis iteration: " + iteration);
			long startIteration = System.nanoTime();
			boolean fixedPoint = true;

			for(GraphElement workItem : worklist){
				if(DEBUG_LOG_ENABLED) Log.info("Applying inference rules for " + workItem.getAttr(XCSG.name) + ", Address: " + workItem.address().toAddressString());
				long startInferenceRules = System.nanoTime();
				boolean typesChanged = applyInferenceRules(workItem);
				long stopInferenceRules = System.nanoTime();
				if(DEBUG_LOG_ENABLED) Log.info("Applied inference rules for " + workItem.getAttr(XCSG.name) + ", Address: " + workItem.address().toAddressString() + " in " + (stopInferenceRules-startInferenceRules)/1000.0/1000.0 + "ms");
				
				if(typesChanged){
					fixedPoint = false;
				}
			}
			
			long stopIteration = System.nanoTime();
			if(LOG_ENABLED) Log.info("Purity analysis iteration: " + iteration + " completed in " + (stopIteration-startIteration)/1000.0/1000.0 + "ms");
			
			if(fixedPoint){
				if(LOG_ENABLED) Log.info("Purity analysis reached fixed point in " + iteration + " iterations");
				successful = true;
				break;
			} else if(iteration == MAX_ITERATIONS){
				Log.warning("Purity analysis terminated. The maximum number of iterations was exceeded and the result may not be correct for all case!");
				break;
			} else {
				// fixed point has not been reached
				// go for another pass
				iteration++;
			}
		}
		
		// flattens the type hierarchy to the maximal types
		// and sets the final attribute values for the
		// IMMUTABILITY_QUALIFIER attribute
		if(LOG_ENABLED) Log.info("Extracting maximal types...");
		extractMaximalTypes();
		
		// tags pure methods
		// must be run after extractMaximalTypes()
		if(LOG_ENABLED) Log.info("Applying method purity tags...");
		tagPureMethods();
		
		return successful;
	}
	
	/**
	 * Given a graph element, each inference rule (TNEW, TASSIGN, TWRITE, TREAD, TCALL) is checked
	 * and unsatisfied qualifier types are removed or reduced (a new type may be added, but it will 
	 * replace other types reducing the total number of types) from graph elements
	 * 
	 * @param workItem Returns true if any type qualifier sets changed
	 * @return
	 */
	private static boolean applyInferenceRules(GraphElement workItem){
		Graph localDFGraph = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow).eval();
		Graph interproceduralDFGraph = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow).eval();
		Graph instanceVariableAccessedGraph = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed).eval();
		Graph identityPassedToGraph = Common.universe().edgesTaggedWithAny(XCSG.IdentityPassedTo).eval();
		Graph containsGraph = Common.universe().edgesTaggedWithAny(XCSG.Contains).eval();
		
		boolean typesChanged = false;
		
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

				try {
					// Reference (y) -LocalDataFlow-> InstanceVariableAssignment (.f)
					GraphElement y = from;
					GraphElement instanceVariableAssignment = to; // (.f)
					
					// InstanceVariableAssignment (.f) -InterproceduralDataFlow-> InstanceVariable (f)
					GraphElement interproceduralEdgeToField = interproceduralDFGraph.edges(instanceVariableAssignment, NodeDirection.OUT).getFirst();
					GraphElement f = interproceduralEdgeToField.getNode(EdgeDirection.TO);
					
					// Reference (x) -InstanceVariableAccessed-> InstanceVariableAssignment (.f)
					GraphElement instanceVariableAccessedEdge = instanceVariableAccessedGraph.edges(instanceVariableAssignment, NodeDirection.IN).getFirst();
					GraphElement x = instanceVariableAccessedEdge.getNode(EdgeDirection.FROM);
					if(x.taggedWith(XCSG.InstanceVariableValue)){
						interproceduralEdgeToField = interproceduralDFGraph.edges(x, NodeDirection.IN).getFirst();
						x = interproceduralEdgeToField.getNode(EdgeDirection.FROM);
					}
					
					if(handleFieldWrite(x, f, y)){
						typesChanged = true;
					}
				} catch (Exception e){
					if(LOG_ENABLED) Log.error("Error parsing field write for work item: " + workItem.address().toAddressString(), e);
				}
				
				involvesField = true;
			}
			
			// TREAD
			if(from.taggedWith(XCSG.InstanceVariableValue)){
				// Type Rule 4 - TREAD
				// let, x = y.f
				try {
					GraphElement x = to;
					GraphElement instanceVariableValue = from; // (.f)
					
					// InstanceVariable (f) -InterproceduralDataFlow-> InstanceVariableValue (.f) 
					GraphElement interproceduralEdgeFromField = interproceduralDFGraph.edges(instanceVariableValue, NodeDirection.IN).getFirst();
					GraphElement f = interproceduralEdgeFromField.getNode(EdgeDirection.FROM);
					
					// Reference (y) -InstanceVariableAccessed-> InstanceVariableValue (.f)
					GraphElement instanceVariableAccessedEdge = instanceVariableAccessedGraph.edges(instanceVariableValue, NodeDirection.IN).getFirst();
					GraphElement y = instanceVariableAccessedEdge.getNode(EdgeDirection.FROM);
					if(y.taggedWith(XCSG.InstanceVariableValue)){
						interproceduralEdgeFromField = interproceduralDFGraph.edges(y, NodeDirection.IN).getFirst();
						y = interproceduralEdgeFromField.getNode(EdgeDirection.FROM);
					}
					
					if(handleFieldRead(x, y, f)){
						typesChanged = true;
					}
				} catch (Exception e){
					if(LOG_ENABLED) Log.error("Error parsing field read for work item: " + workItem.address().toAddressString(), e);
				}
				
				involvesField = true;
			}
			
			// TCALL
			boolean involvesCallsite = false;
			if(from.taggedWith(XCSG.CallSite) && from.taggedWith(XCSG.DynamicDispatchCallSite)){
				// Type Rule 5 - TCALL
				// let, x = y.m(z)
				try {
					GraphElement x = to;
					GraphElement callsite = from;
					
					// IdentityPass (.this) -IdentityPassedTo-> CallSite (m)
					GraphElement identityPassedToEdge = identityPassedToGraph.edges(callsite, NodeDirection.IN).getFirst();
					GraphElement identityPass = identityPassedToEdge.getNode(EdgeDirection.FROM);
					
					// Receiver (y) -LocalDataFlow-> IdentityPass (.this)
					GraphElement localDataFlowEdge = localDFGraph.edges(identityPass, NodeDirection.IN).getFirst();
					GraphElement y = localDataFlowEdge.getNode(EdgeDirection.FROM);
					
					// ReturnValue (ret) -InterproceduralDataFlow-> CallSite (m)
					GraphElement interproceduralDataFlowEdge = interproceduralDFGraph.edges(callsite, NodeDirection.IN).getFirst();
					GraphElement ret = interproceduralDataFlowEdge.getNode(EdgeDirection.FROM);
					
					// Method (method) -Contains-> ReturnValue (ret)
					GraphElement containsEdge = containsGraph.edges(ret, NodeDirection.IN).getFirst();
					GraphElement method = containsEdge.getNode(EdgeDirection.FROM);
					
					// Method (method) -Contains-> Identity
					GraphElement identity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();
					
					// Method (method) -Contains-> Parameter (p1, p2, ...)
					AtlasSet<GraphElement> parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
					
					// ControlFlow -Contains-> CallSite
					// CallSite -Contains-> ParameterPassed (z1, z2, ...)
					AtlasSet<GraphElement> parametersPassed = Common.toQ(callsite).parent().children().nodesTaggedWithAny(XCSG.ParameterPass).eval().nodes();
					
					// ParameterPassed (z1, z2, ...) -InterproceduralDataFlow-> Parameter (p1, p2, ...)
					// such that z1-InterproceduralDataFlow->p1, z2-InterproceduralDataFlow->p2, ...
					AtlasSet<GraphElement> parametersPassedEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow)
							.betweenStep(Common.toQ(parametersPassed), Common.toQ(parameters)).eval().edges();
					
					if(handleCall(x, y, identity, method, ret, parametersPassedEdges)){
						typesChanged = true;
					}
				} catch (Exception e){
					if(LOG_ENABLED) Log.error("Error parsing callsite for work item: " + workItem.address().toAddressString(), e);
				}
				
				involvesCallsite = true;
			}
			
			// TASSIGN
			if(!involvesField && !involvesCallsite){
				GraphElement x = to;
				GraphElement y = from;
				
				if(handleAssignment(x, y)){
					typesChanged = true;
				}
			}
		}
		
		return typesChanged;
	}

	/**
	 * Solves and satisfies constraints for Type Rule 2 - TASSIGN
	 * Let, x = y
	 * 
	 * @param x The reference being written to
	 * @param y The reference be read from
	 * @return
	 */
	private static boolean handleAssignment(GraphElement x, GraphElement y) {
		
		if(x==null){
			Log.warning("x is null!");
			return false;
		}
		
		if(y==null){
			Log.warning("y is null!");
			return false;
		}
		
		if(LOG_INFERENCE_RULES_ENABLED) Log.info("TASSIGN (x=y, x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		
		if(DEBUG_LOG_ENABLED) Log.info("Process Constraint qy <: qx");
		
		// process s(x)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(x)");
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				if(xType.compareTo(yType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(y)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(y)");
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				if(xType.compareTo(yType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			typesChanged = true;
		}
		return typesChanged;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 3 - TWRITE
	 * Let, x.f = y
	 * 
	 * @param x The receiver object
	 * @param f The field of the receiver object being written to
	 * @param y The reference being read from
	 * @return Returns true if the graph element's ImmutabilityTypes have changed
	 */
	private static boolean handleFieldWrite(GraphElement x, GraphElement f, GraphElement y) {
		
		if(x==null){
			Log.warning("x is null!");
			return false;
		}
		
		if(f==null){
			Log.warning("f is null!");
			return false;
		}
		
		if(y==null){
			Log.warning("y is null!");
			return false;
		}
		
		if(LOG_INFERENCE_RULES_ENABLED) Log.info("TWRITE (x.f=y, x=" + x.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		Set<ImmutabilityTypes> fTypes = getTypes(f);
		// x must be mutable
		if(setTypes(x, ImmutabilityTypes.MUTABLE)){
			typesChanged = true;
		}
		ImmutabilityTypes xType = ImmutabilityTypes.MUTABLE;
		
		// if a field changes in an object then any container objects which contain
		// that field have also changed
		if(x.taggedWith(XCSG.Field)){
			for(GraphElement containerField : getContainerFields(x)){
				if(setTypes(containerField, ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD)){
					typesChanged = true;
				}
			}
		}
		
		if(DEBUG_LOG_ENABLED) Log.info("Process Constraint qy <: qx adapt qf");

		// process s(y)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(y)");
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes fType : fTypes){
				ImmutabilityTypes xAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, fType);
				if(xAdaptedF.compareTo(yType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(f)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(f)");
		Set<ImmutabilityTypes> fTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes fType : fTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				ImmutabilityTypes xAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, fType);
				if(xAdaptedF.compareTo(yType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				fTypesToRemove.add(fType);
			}
		}
		if(removeTypes(f, fTypesToRemove)){
			typesChanged = true;
		}
		return typesChanged;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 4 - TREAD
	 * Let, x = y.f
	 * 
	 * @param x The reference being written to
	 * @param y The receiver object
	 * @param f The field of the receiver object being read from
	 * @return Returns true if the graph element's ImmutabilityTypes have changed
	 */
	private static boolean handleFieldRead(GraphElement x, GraphElement y, GraphElement f) {
		
		if(x==null){
			Log.warning("x is null!");
			return false;
		}
		
		if(y==null){
			Log.warning("y is null!");
			return false;
		}
		
		if(f==null){
			Log.warning("f is null!");
			return false;
		}

		if(LOG_INFERENCE_RULES_ENABLED) Log.info("TREAD (x=y.f, x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		Set<ImmutabilityTypes> fTypes = getTypes(f);
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		
		// if x is only MUTABLE then the field and its container fields must be mutable as well
		if(xTypes.contains(ImmutabilityTypes.MUTABLE) && xTypes.size() == 1){
			for(GraphElement containerField : getContainerFields(f)){
				if(setTypes(containerField, ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD)){
					typesChanged = true;
				}
			}
		}
		
		if(DEBUG_LOG_ENABLED) Log.info("Process Constraint qy adapt qf <: qx");
		
		// process s(x)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(x)");
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(y)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(y)");
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(f)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(f)");
		Set<ImmutabilityTypes> fTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes fType : fTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes yType : yTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				fTypesToRemove.add(fType);
			}
		}
		if(removeTypes(f, fTypesToRemove)){
			typesChanged = true;
		}
		
		return typesChanged;
	}
	
	private static boolean handleCall(GraphElement x, GraphElement y, GraphElement identity, GraphElement method, GraphElement ret, AtlasSet<GraphElement> parametersPassedEdges) {
		
		if(x==null){
			Log.warning("x is null!");
			return false;
		}
		
		if(y==null){
			Log.warning("y is null!");
			return false;
		}
		
		if(identity==null){
			Log.warning("identity is null!");
			return false;
		}
		
		if(ret==null){
			Log.warning("return is null!");
			return false;
		}
		
		if(LOG_INFERENCE_RULES_ENABLED) Log.info("TCALL (x=y.m(z), x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", m=" + method.getAttr("##signature") + ")");
		
		boolean typesChanged = false;
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		Set<ImmutabilityTypes> identityTypes = getTypes(identity);
		Set<ImmutabilityTypes> retTypes = getTypes(ret);
		
		// if x is only MUTABLE then the return value must be mutable or polyread
		if(xTypes.contains(ImmutabilityTypes.MUTABLE) && xTypes.size() == 1){
			if(setTypes(ret, ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD)){
				typesChanged = true;
			}
			
			// if the return value is a field then the field and its container fields must be mutable as well
			Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
			Q returnValues = localDataFlowEdges.predecessors(Common.toQ(ret));
			Q instanceVariableValues = localDataFlowEdges.predecessors(returnValues).nodesTaggedWithAny(XCSG.InstanceVariableValue);
			Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
			Q instanceVariables = interproceduralDataFlowEdges.predecessors(instanceVariableValues);
			for(GraphElement instanceVariable : instanceVariables.eval().nodes()){
				for(GraphElement containerField : getContainerFields(instanceVariable)){
					if(setTypes(containerField, ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD)){
						typesChanged = true;
					}
				}
			}
		}
		
		/////////////////////// start qx adapt qret <: qx /////////////////////// 
		
		if(DEBUG_LOG_ENABLED) Log.info("Process Constraint qx adapt qret <: qx");
		
		// process s(x)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(x)");
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes retType : retTypes){
				ImmutabilityTypes xAdaptedRet = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, retType);
				if(xType.compareTo(xAdaptedRet) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(ret)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(ret)");
		Set<ImmutabilityTypes> retTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes retType : retTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				ImmutabilityTypes xAdaptedRet = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, retType);
				if(xType.compareTo(xAdaptedRet) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				retTypesToRemove.add(retType);
			}
		}
		if(removeTypes(ret, retTypesToRemove)){
			typesChanged = true;
		}
		/////////////////////// end qx adapt qret <: qx /////////////////////// 
		
		/////////////////////// start qy <: qx adapt qthis /////////////////////// 
		
		if(DEBUG_LOG_ENABLED) Log.info("Process Constraint qy <: qx adapt qthis");
		
		// process s(y)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(y)");
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes identityType : identityTypes){
					ImmutabilityTypes xAdaptedThis = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, identityType);
					if(xAdaptedThis.compareTo(yType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(x)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(x)");
		xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				for(ImmutabilityTypes identityType : identityTypes){
					ImmutabilityTypes xAdaptedThis = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, identityType);
					if(xAdaptedThis.compareTo(yType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(identity)
		if(DEBUG_LOG_ENABLED) Log.info("Process s(identity)");
		Set<ImmutabilityTypes> identityTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes identityType : identityTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes yType : yTypes){
					ImmutabilityTypes xAdaptedThis = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, identityType);
					if(xAdaptedThis.compareTo(yType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				identityTypesToRemove.add(identityType);
			}
		}
		if(removeTypes(identity, identityTypesToRemove)){
			typesChanged = true;
		}
		
		/////////////////////// end qy <: qx adapt qthis ///////////////////////

		/////////////////////// start qz <: qx adapt qp ///////////////////////
				
		if(DEBUG_LOG_ENABLED) Log.info("Process Constraint qz <: qx adapt qp");
		
		// for each z,p pair process s(x), s(z), and s(p)
		for(GraphElement parametersPassedEdge : parametersPassedEdges){
			GraphElement z = parametersPassedEdge.getNode(EdgeDirection.FROM);
			GraphElement p = parametersPassedEdge.getNode(EdgeDirection.TO);
			Set<ImmutabilityTypes> zTypes = getTypes(z);
			Set<ImmutabilityTypes> pTypes = getTypes(p);
			
			// process s(x)
			if(DEBUG_LOG_ENABLED) Log.info("Process s(x)");
			xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes xType : xTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes zType : zTypes){
					for(ImmutabilityTypes pType : pTypes){
						ImmutabilityTypes xAdaptedP = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, pType);
						if(xAdaptedP.compareTo(zType) >= 0){
							isSatisfied = true;
							break satisfied;
						}
					}
				}
				if(!isSatisfied){
					xTypesToRemove.add(xType);
				}
			}
			if(removeTypes(x, xTypesToRemove)){
				typesChanged = true;
			}
			
			// process s(z)
			if(DEBUG_LOG_ENABLED) Log.info("Process s(z)");
			Set<ImmutabilityTypes> zTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes zType : zTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes xType : xTypes){
					for(ImmutabilityTypes pType : pTypes){
						ImmutabilityTypes xAdaptedP = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, pType);
						if(xAdaptedP.compareTo(zType) >= 0){
							isSatisfied = true;
							break satisfied;
						}
					}
				}
				if(!isSatisfied){
					zTypesToRemove.add(zType);
				}
			}
			if(removeTypes(z, zTypesToRemove)){
				typesChanged = true;
			}
			
			// process s(p)
			if(DEBUG_LOG_ENABLED) Log.info("Process s(p)");
			Set<ImmutabilityTypes> pTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes pType : pTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes xType : xTypes){
					for(ImmutabilityTypes zType : zTypes){
						ImmutabilityTypes xAdaptedP = ImmutabilityTypes.getAdaptedMethodViewpoint(xType, pType);
						if(xAdaptedP.compareTo(zType) >= 0){
							isSatisfied = true;
							break satisfied;
						}
					}
				}
				if(!isSatisfied){
					pTypesToRemove.add(pType);
				}
			}
			if(removeTypes(p, pTypesToRemove)){
				typesChanged = true;
			}
		}
		
		/////////////////////// end qz <: qx adapt qp ///////////////////////
		
		// check if method overrides another method
		Q overridesEdges = Common.universe().edgesTaggedWithAny(XCSG.Overrides);
		GraphElement overriddenMethod = overridesEdges.successors(Common.toQ(method)).eval().nodes().getFirst();
		if(overriddenMethod != null){
			if(LOG_INFERENCE_RULES_ENABLED) Log.info("TCALL (Overridden Method)");
			
			// Method (method) -Contains-> ReturnValue (ret)
			GraphElement overriddenMethodReturn = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.ReturnValue).eval().nodes().getFirst();
			Set<ImmutabilityTypes> overriddenRetTypes = getTypes(overriddenMethodReturn);
			
			// constraint: overriddenReturn <: return
			if(DEBUG_LOG_ENABLED) Log.info("Process Constraint overriddenReturn <: return");
			
			// process s(ret)
			if(DEBUG_LOG_ENABLED) Log.info("Process s(ret)");
			retTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes retType : retTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes overriddenRetType : overriddenRetTypes){
					if(retType.compareTo(overriddenRetType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
				if(!isSatisfied){
					retTypesToRemove.add(retType);
				}
			}
			if(removeTypes(ret, retTypesToRemove)){
				typesChanged = true;
			}
			
			// process s(overriddenRet)
			if(DEBUG_LOG_ENABLED) Log.info("Process s(overriddenRet)");
			EnumSet<ImmutabilityTypes> overriddenRetTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes overriddenRetType : overriddenRetTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes retType : retTypes){
					if(retType.compareTo(overriddenRetType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
				if(!isSatisfied){
					overriddenRetTypesToRemove.add(overriddenRetType);
				}
			}
			if(removeTypes(overriddenMethodReturn, overriddenRetTypesToRemove)){
				typesChanged = true;
			}
			
			// Method (method) -Contains-> Identity
			GraphElement overriddenMethodIdentity = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();
			Set<ImmutabilityTypes> overriddenIdentityTypes = getTypes(overriddenMethodIdentity);

			// constraint: this <: overriddenThis 
			if(DEBUG_LOG_ENABLED) Log.info("Process Constraint this <: overriddenThis");
			
			// process s(this)
			if(DEBUG_LOG_ENABLED) Log.info("Process s(this)");
			identityTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes identityType : identityTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes overriddenIdentityType : overriddenIdentityTypes){
					if(overriddenIdentityType.compareTo(identityType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
				if(!isSatisfied){
					identityTypesToRemove.add(identityType);
				}
			}
			if(removeTypes(identity, identityTypesToRemove)){
				typesChanged = true;
			}
			
			// process s(overriddenRet)
			if(DEBUG_LOG_ENABLED) Log.info("Process s(overriddenRet)");
			EnumSet<ImmutabilityTypes> overriddenIdentityTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes overriddenIdentityType : overriddenIdentityTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes identityType : identityTypes){
					if(overriddenIdentityType.compareTo(identityType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
				if(!isSatisfied){
					overriddenIdentityTypesToRemove.add(overriddenIdentityType);
				}
			}
			if(removeTypes(overriddenMethodIdentity, overriddenIdentityTypesToRemove)){
				typesChanged = true;
			}

			// Method (method) -Contains-> Parameter (p1, p2, ...)
			AtlasSet<GraphElement> overriddenMethodParameters = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
			
			// get the parameters of the method
			AtlasSet<GraphElement> parameters = new AtlasHashSet<GraphElement>();
			for(GraphElement parametersPassedEdge : parametersPassedEdges){
				GraphElement p = parametersPassedEdge.getNode(EdgeDirection.TO);
				parameters.add(p);
			}
			
			// for each parameter and overridden parameter pair
			// constraint: p <: pOverriden
			if(DEBUG_LOG_ENABLED) Log.info("Process Constraint p <: pOverriden");
			long numParams = overriddenMethodParameters.size();
			if(numParams > 0){
				for(int i=0; i<numParams; i++){
					GraphElement p = Common.toQ(parameters).selectNode(XCSG.parameterIndex, i).eval().nodes().getFirst();
					GraphElement pOverridden = Common.toQ(overriddenMethodParameters).selectNode(XCSG.parameterIndex, i).eval().nodes().getFirst();
					
					Set<ImmutabilityTypes> pTypes = getTypes(p);
					Set<ImmutabilityTypes> pOverriddenTypes = getTypes(pOverridden);
					
					// process s(p)
					if(DEBUG_LOG_ENABLED) Log.info("Process s(p)");
					Set<ImmutabilityTypes> pTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
					for(ImmutabilityTypes pType : pTypes){
						boolean isSatisfied = false;
						satisfied:
						for(ImmutabilityTypes pOverriddenType : pOverriddenTypes){
							if(pOverriddenType.compareTo(pType) >= 0){
								isSatisfied = true;
								break satisfied;
							}
						}
						if(!isSatisfied){
							pTypesToRemove.add(pType);
						}
					}
					if(removeTypes(p, pTypesToRemove)){
						typesChanged = true;
					}
					
					// process s(pOverridden)
					if(DEBUG_LOG_ENABLED) Log.info("Process s(pOverridden)");
					Set<ImmutabilityTypes> pOverriddenTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
					for(ImmutabilityTypes pOverriddenType : pOverriddenTypes){
						boolean isSatisfied = false;
						satisfied:
						for(ImmutabilityTypes pType : pTypes){
							if(pOverriddenType.compareTo(pType) >= 0){
								isSatisfied = true;
								break satisfied;
							}
						}
						if(!isSatisfied){
							pOverriddenTypesToRemove.add(pOverriddenType);
						}
					}
					if(removeTypes(pOverridden, pOverriddenTypesToRemove)){
						typesChanged = true;
					}
				}
			}
		}
		
		return typesChanged;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	private static boolean removeTypes(GraphElement ge, Set<ImmutabilityTypes> typesToRemove){
		Set<ImmutabilityTypes> typeSet = getTypes(ge);
		String logMessage = "Remove: " + typesToRemove.toString() + " from " + typeSet.toString() + " for " + ge.getAttr(XCSG.name);
		boolean typesChanged = typeSet.removeAll(typesToRemove);
		if(typesChanged){
			if(DEBUG_LOG_ENABLED) Log.info(logMessage);
		}
		return typesChanged;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	@SuppressWarnings("unused")
	private static boolean removeTypes(GraphElement ge, ImmutabilityTypes... types){
		EnumSet<ImmutabilityTypes> typesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes type : types){
			typesToRemove.add(type);
		}
		return removeTypes(ge, typesToRemove);
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	private static boolean setTypes(GraphElement ge, Set<ImmutabilityTypes> typesToSet){
		Set<ImmutabilityTypes> typeSet = getTypes(ge);
		
		String logMessage = "Set: " + typeSet.toString() + " to " + typesToSet.toString() + " for " + ge.getAttr(XCSG.name);
		
		boolean typesChanged;
		if(typeSet.containsAll(typesToSet) && typesToSet.containsAll(typeSet)){
			typesChanged = false;
		} else {
			typeSet.clear();
			typeSet.addAll(typesToSet);
			typesChanged = true;
		}
		
		if(typesChanged){
			if(DEBUG_LOG_ENABLED) Log.info(logMessage);
		}
		
		return typesChanged;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	private static boolean setTypes(GraphElement ge, ImmutabilityTypes... types){
		EnumSet<ImmutabilityTypes> typesToSet = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes type : types){
			typesToSet.add(type);
		}
		return setTypes(ge, typesToSet);
	}
	
	@SuppressWarnings("unchecked")
	private static Set<ImmutabilityTypes> getTypes(GraphElement ge){
		if(ge.hasAttr(IMMUTABILITY_QUALIFIERS)){
			return (Set<ImmutabilityTypes>) ge.getAttr(IMMUTABILITY_QUALIFIERS);
		} else {
			EnumSet<ImmutabilityTypes> qualifiers = EnumSet.noneOf(ImmutabilityTypes.class);
			
			Q typeOfEdges = Common.universe().edgesTaggedWithAny(XCSG.TypeOf);
			GraphElement geType = typeOfEdges.successors(Common.toQ(ge)).eval().nodes().getFirst();
			
			GraphElement nullType = Common.universe().nodesTaggedWithAny(XCSG.Java.NullType).eval().nodes().getFirst();
			if(ge.equals(nullType)){
				// assignments of null mutate objects
				// see https://github.com/proganalysis/type-inference/blob/master/inference-framework/checker-framework/checkers/src/checkers/inference2/reim/ReimChecker.java#L181
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			} else if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
				// Type Rule 1 - TNEW
				// return type of a constructor is only mutable
				// x = new C(); // no effect on qualifier to x
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			} else if(isDefaultReadonlyType(ge) || isDefaultReadonlyType(geType)){
				// several java objects are readonly for all practical purposes
				qualifiers.add(ImmutabilityTypes.READONLY);
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

			ge.putAttr(IMMUTABILITY_QUALIFIERS, qualifiers);
			return qualifiers;
		}
	}
	
	/**
	 * Returns the fields of containers that are types of the type for the given field 
	 * and the resulting reachable fields
	 * @param field
	 * @return
	 */
	private static AtlasSet<GraphElement> getContainerFields(GraphElement field){
		Q containsEdges = Common.universe().edgesTaggedWithAny(XCSG.Contains);
		Q typeOfEdges = Common.universe().edgesTaggedWithAny(XCSG.TypeOf);
		Q supertypeEdges = Common.universe().edgesTaggedWithAny(XCSG.Supertype);
		
		AtlasSet<GraphElement> fields = new AtlasHashSet<GraphElement>();
		fields.add(field);
		boolean foundNewFields = false;
		do {
			Q privateFields = Common.toQ(fields).nodesTaggedWithAny(XCSG.privateVisibility);
			Q accessibleFields = Common.toQ(fields).difference(privateFields);
			Q accessibleFieldContainers = containsEdges.predecessors(accessibleFields);
			Q accessibleFieldContainerSubtypes = supertypeEdges.reverse(accessibleFieldContainers);
			Q privateFieldContainers = containsEdges.predecessors(privateFields);
			AtlasSet<GraphElement> reachableFields = typeOfEdges.predecessors(accessibleFieldContainerSubtypes.union(privateFieldContainers)).nodesTaggedWithAny(XCSG.InstanceVariable).eval().nodes();
			foundNewFields = fields.addAll(reachableFields);
		} while(foundNewFields);
		
		return fields;
	}
	
	/**
	 * Returns true if the given type is a default readonly type
	 * @param type
	 * @return
	 */
	private static boolean isDefaultReadonlyType(GraphElement type) {
		if(type == null){
			return false;
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
		
		// a few other objects are special cases for all practical purposes
		if(type.equals(Common.typeSelect("java.lang", "String").eval().nodes().getFirst())){
			return true;
		} else if(type.equals(Common.typeSelect("java.lang", "Number").eval().nodes().getFirst())){
			return true;
		} else if(type.equals(Common.typeSelect("java.util.concurrent.atomic", "AtomicInteger").eval().nodes().getFirst())){
			return true;
		} else if(type.equals(Common.typeSelect("java.util.concurrent.atomic", "AtomicLong").eval().nodes().getFirst())){
			return true;
		} else if(type.equals(Common.typeSelect("java.math", "BigDecimal").eval().nodes().getFirst())){
			return true;
		} else if(type.equals(Common.typeSelect("java.math", "BigInteger").eval().nodes().getFirst())){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Flattens the remaining immutability qualifiers to the maximal type
	 * and applies the maximal type as a tag
	 */
	private static void extractMaximalTypes(){
		AtlasSet<GraphElement> attributedGraphElements = Common.universe().selectNode(IMMUTABILITY_QUALIFIERS).eval().nodes();
		for(GraphElement attributedGraphElement : attributedGraphElements){
			LinkedList<ImmutabilityTypes> orderedTypes = new LinkedList<ImmutabilityTypes>();
			orderedTypes.addAll(getTypes(attributedGraphElement));
			Collections.sort(orderedTypes);
			ImmutabilityTypes maximalType = orderedTypes.getLast();
			// leaving the remaining type qualifiers on the graph element is useful for debugging
			if(!DEBUG_LOG_ENABLED) attributedGraphElement.removeAttr(IMMUTABILITY_QUALIFIERS);
			attributedGraphElement.tag(maximalType.toString());
			
			// tag the graph elements that were never touched as readonly
			Q parameters = Common.universe().nodesTaggedWithAny(XCSG.Parameter);
			Q masterReturns = Common.universe().nodesTaggedWithAny(XCSG.MasterReturn);
			Q instanceVariables = Common.universe().nodesTaggedWithAny(XCSG.InstanceVariable);
			Q thisNodes = Common.universe().nodesTaggedWithAll(XCSG.InstanceMethod).children().nodesTaggedWithAny(XCSG.Identity);
			// note local variables may also get tracked, but only if need be during the analysis
			Q trackedItems = parameters.union(masterReturns, instanceVariables, thisNodes);
			Q untouchedTrackedItems = trackedItems.difference(trackedItems.nodesTaggedWithAny(READONLY, POLYREAD, MUTABLE));
			for(GraphElement untouchedTrackedItem : untouchedTrackedItems.eval().nodes()){
				untouchedTrackedItem.tag(READONLY);
			}
		}
	}
	
	/**
	 * Tags pure methods with "PURE"
	 */
	private static void tagPureMethods(){
		AtlasSet<GraphElement> methods = Common.universe().nodesTaggedWithAny(XCSG.Method).eval().nodes();
		for(GraphElement method : methods){
			if(isPureMethod(method)){
				method.tag(PURE_METHOD);
			}
		}
	}
	
	/**
	 * Returns true if the method is pure
	 * Assumes the maximal immutability qualifiers have already been extracted
	 * @param method
	 */
	private static boolean isPureMethod(GraphElement method){
		if(!method.taggedWith(XCSG.Method)){
			return false;
		} else if(isPureMethodDefault(method)){
			return true;
		} else {
			// check if receiver object in any callsite is not mutable
			Q identity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity);
			Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
			Q identityPassed = interproceduralDataFlowEdges.predecessors(identity);
			Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
			Q receivers = localDataFlowEdges.predecessors(identityPassed);
			// a receiver may be a local reference or a field
			// TODO: receiver could also be a callsite...
			Q instanceVariableValues = receivers.nodesTaggedWithAny(XCSG.InstanceVariableValue);
			Q instanceVariables = interproceduralDataFlowEdges.predecessors(instanceVariableValues);
			receivers = receivers.union(instanceVariables);
			Q mutableReceivers = receivers.nodesTaggedWithAny(ImmutabilityTypes.MUTABLE.toString());
			if(mutableReceivers.eval().nodes().size() > 0){
				return false;
			}
			
			// check if any parameter is not mutable
			Q parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter);
			Q mutableParameters = parameters.nodesTaggedWithAny(ImmutabilityTypes.MUTABLE.toString());
			if(mutableParameters.eval().nodes().size() > 0){
				return false;
			}
			
			// TODO: check if static immutability type is not mutable
			// means check if the method's "this" is not mutable?
			
			return true;
		}
	}
	
	/**
	 * Returns true if the method is a default pure method
	 * @param method
	 * @return
	 */
	private static boolean isPureMethodDefault(GraphElement method){
		// note by convention .equals, .hashCode, .toString, and .compareTo
		// are pure methods, but this is not enforced in overridden methods
		// so we are not assuming it to be universally true (unlike ReIm)
		
		// we could however consider some of the java.lang.Object native methods as pure
		// Object's native methods include: getClass, clone, hashCode, notifyAll, notify, wait, registerNatives
		if(method.taggedWith(XCSG.Java.nativeMethod)){
			if(!Common.toQ(method).intersection(Common.typeSelect("java.lang", "Object").children()).eval().nodes().isEmpty()){
				if(method.getAttr(XCSG.name).equals("getClass")){
					return true;
				}
				if(method.getAttr(XCSG.name).equals("hashCode")){
					return true;
				}
				if(method.getAttr(XCSG.name).equals("clone")){
					// clone is a pure method, but it is also a special case
					// to be consider since its return type is a duplication 
					// of a reference
					// see https://en.wikipedia.org/wiki/Clone_(Java_method)
					return true;
				}
				return false;
			}
		}

		return false;
	}
	
}
