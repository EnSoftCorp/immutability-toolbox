package com.ensoftcorp.open.purity.analysis;

import static com.ensoftcorp.open.purity.analysis.Utilities.getTypes;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.NodeDirection;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.purity.analysis.checkers.BasicAssignmentChecker;
import com.ensoftcorp.open.purity.analysis.checkers.CallChecker;
import com.ensoftcorp.open.purity.analysis.checkers.FieldAssignmentChecker;
import com.ensoftcorp.open.purity.analysis.checkers.SanityChecks;
import com.ensoftcorp.open.purity.log.Log;
import com.ensoftcorp.open.purity.preferences.PurityPreferences;

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
	 * Tag applied to references that resulted in no immutability types
	 * This tag should ideally never be applied and represents and error in the 
	 * type system or implementation
	 */
	public static final String UNTYPED = "UNTYPED";
	
	// caching some common graph types
	private static Graph typeOfGraph;
	private static AtlasSet<GraphElement> defaultReadonlyTypes;
	
	/**
	 * Runs the side effect (purity) analysis
	 * @param monitor 
	 * @return Returns the time in milliseconds taken to complete the analysis
	 */
	public static boolean run(IProgressMonitor monitor){
		if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Purity analysis started");
		
		// cache
		initializeCache(monitor);
		
		long start = System.nanoTime();
		runAnalysis();
		long stop = System.nanoTime();
		double runtime = (stop-start)/1000.0/1000.0;
		if(PurityPreferences.isGeneralLoggingEnabled()) {
			long numReadOnly = Common.universe().nodesTaggedWithAny(READONLY).eval().nodes().size();
			long numPolyRead = Common.universe().nodesTaggedWithAny(POLYREAD).eval().nodes().size();
			long numMutable = Common.universe().nodesTaggedWithAny(MUTABLE).eval().nodes().size();
			String summary = "READONLY: " + numReadOnly + ", POLYREAD: " + numPolyRead + ", MUTABLE: " + numMutable;
			Log.info("Purity analysis completed in " + runtime + " ms\n" + summary);
		}
		
		boolean isSane = true;
		if(PurityPreferences.isRunSanityChecksEnabled()){
			Log.info("Running sanity checks...");
			isSane = SanityChecks.run();
			if(isSane){
				Log.info("Sanity checks completed. Everything is sane.");
			} else {
				Log.warning("Sanity checks failed!");
			}
		}
		
		return isSane;
	}

	private static void initializeCache(IProgressMonitor monitor) {
		typeOfGraph = Common.resolve(monitor, Common.universe().edgesTaggedWithAny(XCSG.TypeOf).eval());
		initializeDefaultReadonlyTypes();
	}
	
	private static void initializeDefaultReadonlyTypes() {
		defaultReadonlyTypes = new AtlasHashSet<GraphElement>();
		
		// null type
		defaultReadonlyTypes.add(Common.universe().nodesTaggedWithAny(XCSG.Java.NullType).eval().nodes().getFirst());
		
		// autoboxing
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Integer").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Long").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Short").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Boolean").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Byte").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Double").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Float").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Character").eval().nodes().getFirst());
		
		// a few other objects are special cases for all practical purposes
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "String").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Number").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.util.concurrent.atomic", "AtomicInteger").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.util.concurrent.atomic", "AtomicLong").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.math", "BigDecimal").eval().nodes().getFirst());
		defaultReadonlyTypes.add(Common.typeSelect("java.math", "BigInteger").eval().nodes().getFirst());
	}

	/**
	 * Runs the side effect (purity) analysis
	 */
	private static void runAnalysis(){
		// TODO: remove when there are appropriate alternatives
		Utilities.addClassVariableAccessTags();
		
		TreeSet<GraphElement> worklist = new TreeSet<GraphElement>();

		// add all assignments to worklist
		Q assignments = Common.resolve(new NullProgressMonitor(), Common.universe().nodesTaggedWithAny(XCSG.Assignment));
		for(GraphElement assignment : assignments.eval().nodes()){
			worklist.add(assignment);
		}
		
		// add all assignments to worklist
		// treating parameter passes as assignments (for all purposes they are...)
		Q parameterPass = Common.resolve(new NullProgressMonitor(), Common.universe().nodesTaggedWithAny(XCSG.ParameterPass));
		for(GraphElement assignment : parameterPass.eval().nodes()){
			worklist.add(assignment);
		}
		
		Q callsites = Common.universe().nodesTaggedWithAny(XCSG.CallSite);
		Q localDFEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		Q assignedCallsites = localDFEdges.predecessors(Common.universe().nodesTaggedWithAny(XCSG.Assignment)).nodesTaggedWithAny(XCSG.CallSite);
		Q unassignedCallsites = callsites.difference(assignedCallsites);
		Q unassignedDynamicDispatchCallsites = Common.resolve(new NullProgressMonitor(), unassignedCallsites.nodesTaggedWithAny(XCSG.DynamicDispatchCallSite));
		for(GraphElement unassignedDynamicDispatchCallsite : unassignedDynamicDispatchCallsites.eval().nodes()){
			worklist.add(unassignedDynamicDispatchCallsite);
		}
		
		Q unassignedStaticDispatchCallsites = Common.resolve(new NullProgressMonitor(), unassignedCallsites.nodesTaggedWithAny(XCSG.StaticDispatchCallSite));
		for(GraphElement unassignedStaticDispatchCallsite : unassignedStaticDispatchCallsites.eval().nodes()){
			worklist.add(unassignedStaticDispatchCallsite);
		}
		
		int iteration = 1;
		while(true){
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Purity analysis iteration: " + iteration);
			long startIteration = System.nanoTime();
			boolean fixedPoint = true;

			for(GraphElement workItem : worklist){
				if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Applying inference rules for " + workItem.getAttr(XCSG.name) + ", Address: " + workItem.address().toAddressString());
				long startInferenceRules = System.nanoTime();
				boolean typesChanged = applyInferenceRules(workItem);
				long stopInferenceRules = System.nanoTime();
				if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Applied inference rules for " + workItem.getAttr(XCSG.name) + ", Address: " + workItem.address().toAddressString() + " in " + (stopInferenceRules-startInferenceRules)/1000.0/1000.0 + "ms");
				
				if(typesChanged){
					fixedPoint = false;
				}
			}
			
			long stopIteration = System.nanoTime();
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Purity analysis iteration: " + iteration + " completed in " + (stopIteration-startIteration)/1000.0/1000.0 + "ms");
			
			// If every reference was in the worklist then theoretically each item
			// should only ever be visited at most 3 times (because in the worst
			// case the first two visits remove 1 immutability type and fixed point is
			// reached on the 3rd visit when there is nothing left to remove since
			// there must be at least one immutability type left, mutable for the case
			// of one type left, for each reference).
			// So for some implementations after 3 iterations the fixed point should have 
			// been reached (and perhaps a 4th iteration to realize it), but...
			// not every reference is placed in our worklist (some references are processed
			// on-demand) so we must run until fixed point.
			if(fixedPoint){
				if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Purity analysis reached fixed point in " + iteration + " iterations");
				break;
			} else {
				// fixed point has not been reached
				// go for another pass
				iteration++;
			}
		}
		
		if(PurityPreferences.isPartialProgramAnalysisEnabled()){
			// serialize immutability sets to in Atlas tags
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Converting immutability sets into tags...");
			AtlasSet<GraphElement> attributedGraphElements = Common.universe().selectNode(Utilities.IMMUTABILITY_QUALIFIERS).eval().nodes();
			for(GraphElement attributedGraphElement : attributedGraphElements){
				Set<ImmutabilityTypes> types = getTypes(attributedGraphElement);
				if(types.isEmpty()){
					attributedGraphElement.tag(UNTYPED);
				} else {
					for(ImmutabilityTypes type : types){
						attributedGraphElement.tag(type.toString());
					}
				}
				attributedGraphElement.removeAttr(Utilities.IMMUTABILITY_QUALIFIERS);
			}
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Converted immutability sets into tags.");
		} else {
			// flattens the type hierarchy to the maximal types
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Extracting maximal types...");
			long startExtraction = System.nanoTime();
			extractMaximalTypes();
			long stopExtraction = System.nanoTime();
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Extracted maximal types in " + (stopExtraction-startExtraction)/1000.0/1000.0 + "ms");
			
			// tags pure methods
			// must be run after extractMaximalTypes()
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Applying method purity tags...");
			long startPurityTagging = System.nanoTime();
			tagPureMethods();
			long stopPurityTagging = System.nanoTime();
			if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Applied method purity tags in " + (stopPurityTagging-startPurityTagging)/1000.0/1000.0 + "ms");
		}
		
		// TODO: remove when there are appropriate alternatives
		Utilities.removeClassVariableAccessTags();
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
		
		boolean typesChanged = false;
		
		if(workItem.taggedWith(XCSG.Assignment) || workItem.taggedWith(XCSG.ParameterPass)){
			Graph localDFGraph = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow).eval();
			Graph interproceduralDFGraph = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow).eval();
			Graph instanceVariableAccessedGraph = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed).eval();
			Graph identityPassedToGraph = Common.universe().edgesTaggedWithAny(XCSG.IdentityPassedTo).eval();
			Graph containsGraph = Common.universe().edgesTaggedWithAny(XCSG.Contains).eval();

			// consider data flow edges
			// incoming edges represent a read relationship in an assignment
			// outgoing edges represent a write relationship in an assignment
			GraphElement to = workItem;
			AtlasSet<GraphElement> inEdges = localDFGraph.edges(to, NodeDirection.IN);
			for(GraphElement edge : inEdges){
				GraphElement from = edge.getNode(EdgeDirection.FROM);
				
				// if the from node is a cast we need to step back along data flow edges
				// until we have found what it was casted from
				if(from.taggedWith(XCSG.Cast)){
					GraphElement current = from;
					while(current.taggedWith(XCSG.Cast)){
						Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
						current = localDataFlowEdges.predecessors(Common.toQ(current)).eval().nodes().getFirst();
					}
					from = current;
				}
				
				boolean involvesField = false;
				
				// TWRITE
				if(to.taggedWith(XCSG.InstanceVariableAssignment)){
					// Type Rule 3 - TWRITE
					// let, x.f = y
					try {
						// Reference (y) -LocalDataFlow-> InstanceVariableAssignment (f=)
						GraphElement y = from;
						if(y.taggedWith(XCSG.InstanceVariableValue)){
							GraphElement interproceduralEdgeFromField = interproceduralDFGraph.edges(y, NodeDirection.IN).getFirst();
							y = interproceduralEdgeFromField.getNode(EdgeDirection.FROM);
						}
						GraphElement instanceVariableAssignment = to; // (f=)
						
						// InstanceVariableAssignment (f=) -InterproceduralDataFlow-> InstanceVariable (f)
						GraphElement interproceduralEdgeToField = interproceduralDFGraph.edges(instanceVariableAssignment, NodeDirection.OUT).getFirst();
						GraphElement f = interproceduralEdgeToField.getNode(EdgeDirection.TO);
						
						// Reference (x) -InstanceVariableAccessed-> InstanceVariableAssignment (.f)
						GraphElement instanceVariableAccessedEdge = instanceVariableAccessedGraph.edges(instanceVariableAssignment, NodeDirection.IN).getFirst();
						GraphElement x = instanceVariableAccessedEdge.getNode(EdgeDirection.FROM);
						
						if(FieldAssignmentChecker.handleFieldWrite(x, f, y)){
							typesChanged = true;
						}
					} catch (Exception e){
						if(PurityPreferences.isGeneralLoggingEnabled()) Log.error("Error parsing field write for work item: " + workItem.address().toAddressString(), e);
					}
					
					involvesField = true;
				}
				
				// TREAD
				if(from.taggedWith(XCSG.InstanceVariableValue)){
					// Type Rule 4 - TREAD
					// let, x = y.f
					try {
						GraphElement x = to;
						if(x.taggedWith(XCSG.InstanceVariableValue)){
							GraphElement interproceduralEdgeFromField = interproceduralDFGraph.edges(x, NodeDirection.IN).getFirst();
							x = interproceduralEdgeFromField.getNode(EdgeDirection.FROM);
						}
						
						GraphElement instanceVariableValue = from; // (.f)
						
						// InstanceVariable (f) -InterproceduralDataFlow-> InstanceVariableValue (.f) 
						GraphElement interproceduralEdgeFromField = interproceduralDFGraph.edges(instanceVariableValue, NodeDirection.IN).getFirst();
						GraphElement f = interproceduralEdgeFromField.getNode(EdgeDirection.FROM);
						
						// Reference (y) -InstanceVariableAccessed-> InstanceVariableValue (.f)
						GraphElement instanceVariableAccessedEdge = instanceVariableAccessedGraph.edges(instanceVariableValue, NodeDirection.IN).getFirst();
						GraphElement y = instanceVariableAccessedEdge.getNode(EdgeDirection.FROM);
						
						if(FieldAssignmentChecker.handleFieldRead(x, y, f)){
							typesChanged = true;
						}
					} catch (Exception e){
						if(PurityPreferences.isGeneralLoggingEnabled()) Log.error("Error parsing field read for work item: " + workItem.address().toAddressString(), e);
					}
					
					involvesField = true;
				}
				
				// Type Rule 7 - TSREAD
				// let, x = sf
				try {
					if(from.taggedWith(Utilities.CLASS_VARIABLE_VALUE)){
						GraphElement x = to;

						// ClassVariable (sf) -InterproceduralDataFlow-> ClassVariableValue (.sf) 
						GraphElement interproceduralEdgeFromField = interproceduralDFGraph.edges(from, NodeDirection.IN).getFirst();
						GraphElement sf = interproceduralEdgeFromField.getNode(EdgeDirection.FROM);
						
						GraphElement m = Utilities.getContainingMethod(x);
						if(FieldAssignmentChecker.handleStaticFieldRead(x, sf, m)){
							typesChanged = true;
						}
						involvesField = true;
					}	
				} catch (Exception e){
					if(PurityPreferences.isGeneralLoggingEnabled()) Log.error("Error parsing static field read for work item: " + workItem.address().toAddressString(), e);
				}
				
				// Type Rule 8 - TSWRITE
				// let, sf = x
				try {
					if(to.taggedWith(Utilities.CLASS_VARIABLE_ASSIGNMENT)){
						// ClassVariableAssignment (sf=) -InterproceduralDataFlow-> ClassVariable (sf)
						GraphElement interproceduralEdgeToField = interproceduralDFGraph.edges(to, NodeDirection.OUT).getFirst();
						GraphElement sf = interproceduralEdgeToField.getNode(EdgeDirection.TO);
						
						GraphElement x = from;
						GraphElement m = Utilities.getContainingMethod(x);
						if(FieldAssignmentChecker.handleStaticFieldWrite(sf, x, m)){
							typesChanged = true;
						}
						involvesField = true;
					}	
				} catch (Exception e){
					if(PurityPreferences.isGeneralLoggingEnabled()) Log.error("Error parsing static field read for work item: " + workItem.address().toAddressString(), e);
				}
				
				// TCALL
				boolean involvesCallsite = false;
				if(from.taggedWith(XCSG.DynamicDispatchCallSite)){
					// Type Rule 5 - TCALL
					// let, x = y.m(z)
					try {
						GraphElement x = to;
						GraphElement callsite = from;
						
						// TODO: update this with method signature
//						GraphElement method = Utilities.getInvokedMethodSignature(callsite);
//						GraphElement identity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();
						
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
						// note that we could also use a control flow call edge to get the method
						// Control Flow Block (cf) -Contains-> Callsite (m)
						// Control Flow Block (cf) -Call-> Method (method)
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
						
						if(CallChecker.handleCall(x, y, identity, method, ret, parametersPassedEdges)){
							typesChanged = true;
						}
					} catch (Exception e){
						if(PurityPreferences.isGeneralLoggingEnabled()) Log.error("Error parsing callsite for work item: " + workItem.address().toAddressString(), e);
					}
					
					involvesCallsite = true;
				}
				
				// TSCALL
				if(from.taggedWith(XCSG.StaticDispatchCallSite)){
					
					// Type Rule 8 - TSCALL
					// let, x = m(z)
					try {
						GraphElement x = to;
						GraphElement callsite = from;
						
						// TODO: update this with method signature
//						GraphElement method = Utilities.getInvokedMethodSignature(callsite);
//						GraphElement identity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();
						
						// ReturnValue (ret) -InterproceduralDataFlow-> CallSite (m)
						GraphElement interproceduralDataFlowEdge = interproceduralDFGraph.edges(callsite, NodeDirection.IN).getFirst();
						GraphElement ret = interproceduralDataFlowEdge.getNode(EdgeDirection.FROM);
						
						// Method (method) -Contains-> ReturnValue (ret)
						// note that we could also use a control flow call edge to get the method
						// Control Flow Block (cf) -Contains-> Callsite (m)
						// Control Flow Block (cf) -Call-> Method (method)
						GraphElement containsEdge = containsGraph.edges(ret, NodeDirection.IN).getFirst();
						GraphElement method = containsEdge.getNode(EdgeDirection.FROM);
						
						// Method (method) -Contains-> Parameter (p1, p2, ...)
						AtlasSet<GraphElement> parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
						
						// ControlFlow -Contains-> CallSite
						// CallSite -Contains-> ParameterPassed (z1, z2, ...)
						AtlasSet<GraphElement> parametersPassed = Common.toQ(callsite).parent().children().nodesTaggedWithAny(XCSG.ParameterPass).eval().nodes();
						
						// ParameterPassed (z1, z2, ...) -InterproceduralDataFlow-> Parameter (p1, p2, ...)
						// such that z1-InterproceduralDataFlow->p1, z2-InterproceduralDataFlow->p2, ...
						AtlasSet<GraphElement> parametersPassedEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow)
								.betweenStep(Common.toQ(parametersPassed), Common.toQ(parameters)).eval().edges();
						
						if(CallChecker.handleStaticCall(x, callsite, method, ret, parametersPassedEdges)){
							typesChanged = true;
						}
					} catch (Exception e){
						if(PurityPreferences.isGeneralLoggingEnabled()) Log.error("Error parsing callsite for work item: " + workItem.address().toAddressString(), e);
					}
					
					involvesCallsite = true;
				}
				
				// Type Rule 2 - TASSIGN
				// let x = y
				if(!involvesField && !involvesCallsite){
					GraphElement x = to;
					GraphElement y = from;
					
					if(BasicAssignmentChecker.handleAssignment(x, y)){
						typesChanged = true;
					}
				}
			}
		} else if(workItem.taggedWith(XCSG.DynamicDispatchCallSite)){
			// constraints need to be checked for each callsite without an assignment 
			// of this instance method to satisfy constraints on receivers and parameters passed
			// note that this could be refined with a precise call graph instead of 
			// a CHA, but we'd also have to update the callsite resolution which brought
			// us to this point in the first place
			GraphElement unassignedDynamicDispatchCallsite = workItem;
			if(CallChecker.handleUnassignedDynamicDispatchCallsites(unassignedDynamicDispatchCallsite)){
				typesChanged = true;
			}
		} else if(workItem.taggedWith(XCSG.StaticDispatchCallSite)){
			// constraints need to be checked for each callsite without an assignment 
			// of this class method to satisfy constraints on parameters passed
			GraphElement unassignedStaticDispatchCallsite = workItem;
			if(CallChecker.handleUnassignedStaticDispatchCallsites(unassignedStaticDispatchCallsite)){
				typesChanged = true;
			}
		}
		
		return typesChanged;
	}
	
	public static ImmutabilityTypes getDefaultMaximalType(GraphElement ge) {
		ImmutabilityTypes maximalType;
		if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
			maximalType = ImmutabilityTypes.MUTABLE;
		} else if(isDefaultReadonlyType(ge) || isDefaultReadonlyType(getObjectType(ge))){
			// several java objects are readonly for all practical purposes
			maximalType = ImmutabilityTypes.READONLY;
		} else if(ge.taggedWith(XCSG.MasterReturn)){
			maximalType = ImmutabilityTypes.READONLY;
		} else if(ge.taggedWith(XCSG.InstanceVariable)){
			maximalType = ImmutabilityTypes.READONLY;
		} else if(ge.taggedWith(XCSG.ClassVariable)){
			maximalType = ImmutabilityTypes.READONLY;
		} else if(ge.taggedWith(XCSG.Method)){
			maximalType = ImmutabilityTypes.READONLY;
		} else if(ge.taggedWith(XCSG.ParameterPass)){
			maximalType = ImmutabilityTypes.READONLY;
		} else {
			maximalType = ImmutabilityTypes.READONLY;
		}
		return maximalType;
	}
	
	private static GraphElement getObjectType(GraphElement ge) {
		// ge -TypeOf-> getType
		GraphElement typeOfEdge = typeOfGraph.edges(ge, NodeDirection.OUT).getFirst();
		GraphElement geType = null;
		if(typeOfEdge != null){
			geType = typeOfEdge.getNode(EdgeDirection.TO);
		}
		return geType;
	}

	public static EnumSet<ImmutabilityTypes> getDefaultTypes(GraphElement ge) {
		EnumSet<ImmutabilityTypes> qualifiers = EnumSet.noneOf(ImmutabilityTypes.class);
		
		// ge -TypeOf-> getType
		GraphElement typeOfEdge = typeOfGraph.edges(ge, NodeDirection.OUT).getFirst();
		GraphElement geType = null;
		if(typeOfEdge != null){
			geType = typeOfEdge.getNode(EdgeDirection.TO);
		}
		
		if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
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
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
		} else if(ge.taggedWith(XCSG.InstanceVariable)){
			// Section 2.4 of Reference 1
			// "Fields are initialized to S(f) = {readonly, polyread}"
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
		} else if(ge.taggedWith(XCSG.ClassVariable)){
			// Section 3 of Reference 1
			// static fields are initialized to S(sf) = {readonly, mutable}
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Method)){
			// Section 3 of Reference 1
			// methods can have a static type of {readonly, polyread, mutable}
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.ParameterPass)){
			// Section 2.4 of Reference 1
			// "All other references are initialized to the maximal
			// set of qualifiers, i.e. S(x) = {readonly, polyread, mutable}"
			// treating parameters passed as an implicit assignment that can be polyread
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else {
			// Section 2.4 of Reference 1
			// "All other references are initialized to the maximal
			// set of qualifiers, i.e. S(x) = {readonly, polyread, mutable}"
			// But, what does it mean for a local reference to be polyread? ~Ben
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		}
		return qualifiers;
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
		
		// literals
		if(type.taggedWith(XCSG.Literal)){
			return true;
		}
		
		// operations on primitives or references to primitives
		if(type.taggedWith(XCSG.Operator)){
			return true;
		}

		return defaultReadonlyTypes.contains(type);
	}
	
	/**
	 * Flattens the remaining immutability qualifiers to the maximal type
	 * and applies the maximal type as a tag
	 */
	private static void extractMaximalTypes(){
		AtlasSet<GraphElement> attributedGraphElements = Common.universe().selectNode(Utilities.IMMUTABILITY_QUALIFIERS).eval().nodes();
		for(GraphElement attributedGraphElement : attributedGraphElements){
			LinkedList<ImmutabilityTypes> orderedTypes = new LinkedList<ImmutabilityTypes>();
			orderedTypes.addAll(getTypes(attributedGraphElement));
			if(orderedTypes.isEmpty()){
				attributedGraphElement.tag(UNTYPED);
			} else {
				Collections.sort(orderedTypes);
				ImmutabilityTypes maximalType = orderedTypes.getLast();
				attributedGraphElement.tag(maximalType.toString());
			}
		}
		
		// tag the graph elements that were never touched as the default maximal type (most likely readonly)
		Q methods = Common.universe().nodesTaggedWithAny(XCSG.Method);
		Q parameters = Common.universe().nodesTaggedWithAny(XCSG.Parameter);
		Q masterReturns = Common.universe().nodesTaggedWithAny(XCSG.MasterReturn);
		Q instanceVariables = Common.universe().nodesTaggedWithAny(XCSG.InstanceVariable);
		Q thisNodes = Common.universe().nodesTaggedWithAll(XCSG.InstanceMethod).children().nodesTaggedWithAny(XCSG.Identity);
		Q classVariables = Common.universe().nodesTaggedWithAny(XCSG.ClassVariable);
		// note local variables may also get tracked, but only if need be during the analysis
		Q trackedItems = methods.union(parameters, masterReturns, instanceVariables, thisNodes, classVariables);
		Q untouchedTrackedItems = trackedItems.difference(trackedItems.nodesTaggedWithAny(READONLY, POLYREAD, MUTABLE), Common.toQ(attributedGraphElements));
		for(GraphElement untouchedTrackedItem : untouchedTrackedItems.eval().nodes()){
			ImmutabilityTypes maximalType = getDefaultMaximalType(untouchedTrackedItem);
			untouchedTrackedItem.tag(maximalType.toString());
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
			// from reference 1 section 3
			// a method is pure if 
			// 1) it does not mutate prestates reachable through parameters
			// this includes the formal parameters and implicit "this" parameter
			Q parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter);
			Q mutableParameters = parameters.nodesTaggedWithAny(MUTABLE);
			if(mutableParameters.eval().nodes().size() > 0){
				return false;
			}
			Q mutableIdentity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).nodesTaggedWithAny(MUTABLE);
			if(mutableIdentity.eval().nodes().size() > 0){
				return false;
			}
			
			// 2) it does not mutate prestates reachable through static fields
			if(method.taggedWith(MUTABLE)){
				return false;
			}
			
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
