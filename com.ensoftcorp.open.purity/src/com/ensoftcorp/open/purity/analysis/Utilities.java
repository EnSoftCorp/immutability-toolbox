package com.ensoftcorp.open.purity.analysis;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.NodeDirection;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.purity.log.Log;
import com.ensoftcorp.open.purity.preferences.PurityPreferences;

public class Utilities {
	
//	// caching for some common graph types
//	private static boolean cacheInitialized = false;
//	private static AtlasSet<GraphElement> defaultReadonlyTypes;
//	
//	private static void initializeCache(IProgressMonitor monitor) {
//		// initialize the cache of default readonly types
//		defaultReadonlyTypes = new AtlasHashSet<GraphElement>();
//		
//		// autoboxing
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Integer").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Long").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Short").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Boolean").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Byte").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Double").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Float").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Character").eval().nodes().getFirst());
//		
//		// a few other objects are special cases for all practical purposes
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "String").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.lang", "Number").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.util.concurrent.atomic", "AtomicInteger").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.util.concurrent.atomic", "AtomicLong").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.math", "BigDecimal").eval().nodes().getFirst());
//		defaultReadonlyTypes.add(Common.typeSelect("java.math", "BigInteger").eval().nodes().getFirst());
//	}
//	
//	/**
//	 * Returns true if the given type is a default readonly type
//	 * @param type
//	 * @return
//	 */
//	public static boolean isDefaultReadonlyType(GraphElement type) {
//		if(type == null){
//			return false;
//		}
//		if(!cacheInitialized){
//			initializeCache(new NullProgressMonitor());
//		}
//		return type.taggedWith(XCSG.Primitive) || defaultReadonlyTypes.contains(type);
//	}
	
	/**
	 * Used as an attribute key to temporarily compute the potential immutability qualifiers
	 */
	public static final String IMMUTABILITY_QUALIFIERS = "IMMUTABILITY_QUALIFIERS";
	
	// TODO: bug EnSoft to make tags like these...
	public static final String CLASS_VARIABLE_ASSIGNMENT = "CLASS_VARIABLE_ASSIGNMENT";
	public static final String CLASS_VARIABLE_VALUE = "CLASS_VARIABLE_VALUE";
	public static final String CLASS_VARIABLE_ACCESS = "CLASS_VARIABLE_ACCESS";
	
	// TODO: pester EnSoft to remove these...or at least unify with source graph format
	public static final String DATAFLOW_DISPLAY_NODE = "DATAFLOW_DISPLAY_NODE";
	
	public static final String DUMMY_ASSIGNMENT_NODE = "DUMMY_ASSIGNMENT_NODE";
	public static final String DUMMY_RETURN_NODE = "DUMMY_RETURN_NODE";
	public static final String DUMMY_RETURN_EDGE = "DUMMY_RETURN_EDGE";
	
	/**
	 * Adds DUMMY_RETURN_NODE to void methods and DUMMY_ASSIGNMENT_NODE from unassigned callsites to a dummy assignment node
	 */
	public static void addDummyReturnAssignments(){
		if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Adding dummy return assignments...");
		Q returnsEdges = Common.universe().edgesTaggedWithAny(XCSG.Returns).retainEdges();
		Q voidMethods = returnsEdges.predecessors(Common.types("void"));
		for(GraphElement voidMethod : voidMethods.eval().nodes()){
			createDummyReturnNode(voidMethod);
		}
		
		// the remaining methods without returns are likely ill formed methods
		// we can correct for it and move on, but these should be fixed up stream
		// in Atlas if they occur or there is an assumption here that is being violated
		Q allMethods = Common.universe().nodesTaggedWithAny(XCSG.Method);
		Q returnValues = Common.universe().nodesTaggedWithAny(XCSG.ReturnValue);
		AtlasSet<Node> malformedMethods = allMethods.difference(returnValues.parent()).eval().nodes();
		if(!malformedMethods.isEmpty()){
			for(Node malformedMethod : malformedMethods){
				Log.warning("Added a dummy return node for malformed method " + malformedMethod.address().toAddressString());
				createDummyReturnNode(malformedMethod);
			}
		}

		Log.info("Added " + Common.universe().nodesTaggedWithAny(DUMMY_RETURN_NODE).eval().nodes().size() + " dummy return nodes.");
		
		// sanity check (all (expected) methods have a return value)
		returnValues = Common.universe().nodesTaggedWithAny(XCSG.ReturnValue); // refresh stale references
		AtlasSet<Node> unexpectedMethodsMissingReturns = allMethods.difference(returnValues.parent()).eval().nodes();
		if(!unexpectedMethodsMissingReturns.isEmpty()){
			throw new RuntimeException("There are " + unexpectedMethodsMissingReturns.size() + " unexpected methods missing return value nodes!");
		}
		
		// if the callsite does not have an incoming interprocedural data flow edge
		// then it must be a void method in which case we need to link it up with the corresponding
		// dummy (or regular) return node. Since the dummy nodes are just place holders for readonly types,
		// its not terribly important to completely resolve dynamic dispatches and we can just link
		// to the dummy return node of the signature method
		returnValues = Common.universe().nodesTaggedWithAny(XCSG.ReturnValue); // refresh stale references
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		Q callsitesWithReturn = interproceduralDataFlowEdges.successors(returnValues).nodesTaggedWithAny(XCSG.CallSite);
		Q callsites = Common.universe().nodesTaggedWithAny(XCSG.CallSite);
		Q callsitesWithoutReturn = callsites.difference(callsitesWithReturn);
		for(Node callsiteWithoutReturn : callsitesWithoutReturn.eval().nodes()){
			GraphElement method = getInvokedMethodSignature(callsiteWithoutReturn);
			GraphElement returnValue = Common.toQ(method).children().nodesTaggedWithAny(XCSG.ReturnValue).eval().nodes().getFirst();
			createDummyReturnValueEdge(returnValue, callsiteWithoutReturn);
		}
		
		Log.info("Added " + Common.universe().edgesTaggedWithAny(DUMMY_RETURN_EDGE).eval().edges().size() + " dummy return value edges.");
		
		// sanity check (all callsites have an incoming data flow edge from a return value)
		callsites = Common.universe().nodesTaggedWithAny(XCSG.CallSite); // refresh stale references
		returnValues = Common.universe().nodesTaggedWithAny(XCSG.ReturnValue); // refresh stale references
		interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow); // refresh stale references
		Q callsitesWithReturns = interproceduralDataFlowEdges.successors(returnValues).nodesTaggedWithAny(XCSG.CallSite);
		AtlasSet<Node> callsiteNodesWithoutReturn = callsites.difference(callsitesWithReturns).eval().nodes();
		if(!callsiteNodesWithoutReturn.isEmpty()){
			throw new RuntimeException("There are " + callsiteNodesWithoutReturn.size() + " missing returns to callsites!");
		}
		
		// create dummy assignment nodes for callsites without assignments
		Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		Q assignments = Common.universe().nodesTaggedWithAny(XCSG.Assignment);
		Q assignedCallsites = localDataFlowEdges.predecessors(assignments).nodesTaggedWithAny(XCSG.CallSite);
		Q unassignedCallsites = callsites.difference(assignedCallsites);
		for(GraphElement unassignedCallsite : unassignedCallsites.eval().nodes()){
			createDummyAssignmentNode(unassignedCallsite);
		}
		
		Log.info("Added " + Common.universe().nodesTaggedWithAny(DUMMY_ASSIGNMENT_NODE).eval().nodes().size() + " dummy assignment nodes.");
		
		// sanity check (all callsites are assigned to an assignment node)
		localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow); // refresh stale references
		assignments = Common.universe().nodesTaggedWithAny(XCSG.Assignment); // refresh stale references
		AtlasSet<Node> unexpectedCallsitesMissingAssignments =  callsites.difference(localDataFlowEdges.predecessors(assignments)).eval().nodes();
		if(!unexpectedCallsitesMissingAssignments.isEmpty()){
			throw new RuntimeException("There are " + unexpectedCallsitesMissingAssignments.size() + " unexpected callsites missing assignments!");
		}
	}

	private static GraphElement createDummyReturnValueEdge(GraphElement returnValue, GraphElement callsite){
		GraphElement interproceduralDataFlowEdge = Graph.U.createEdge(returnValue, callsite);
		interproceduralDataFlowEdge.tag(XCSG.InterproceduralDataFlow);
		interproceduralDataFlowEdge.tag(DUMMY_RETURN_EDGE);
		return interproceduralDataFlowEdge;
	}
	
	private static GraphElement createDummyAssignmentNode(GraphElement unassignedCallsite) {
		// create the dummy assignment node
		GraphElement dummyAssignmentNode = Graph.U.createNode();
		dummyAssignmentNode.putAttr(XCSG.name, DUMMY_ASSIGNMENT_NODE);
		dummyAssignmentNode.tag(XCSG.Assignment);
		dummyAssignmentNode.tag(DUMMY_ASSIGNMENT_NODE);
		
		// create edge from unassigned callsite to the dummy assignment node
		GraphElement localDataFlowEdge = Graph.U.createEdge(unassignedCallsite, dummyAssignmentNode);
		localDataFlowEdge.tag(XCSG.LocalDataFlow);
		
		// create a contains edge from the callsites parent to the dummy assignment node
		GraphElement parent = Common.toQ(unassignedCallsite).parent().eval().nodes().getFirst();
		GraphElement containsEdge = Graph.U.createEdge(parent, dummyAssignmentNode);
		containsEdge.tag(XCSG.Contains);
		
		return dummyAssignmentNode;
	}

	private static GraphElement createDummyReturnNode(GraphElement method) {
		GraphElement returnValue = Graph.U.createNode();
		returnValue.putAttr(XCSG.name, DUMMY_RETURN_NODE);
		returnValue.tag(XCSG.ReturnValue);
		returnValue.tag(DUMMY_RETURN_NODE);
		// create a contains edge from the void method to the return value
		GraphElement containsEdge = Graph.U.createEdge(method, returnValue);
		containsEdge.tag(XCSG.Contains);
		return returnValue;
	}
	
	/**
	 * Removes DUMMY_RETURN_NODE and DUMMY_ASSIGNMENT_NODE nodes and any edges connected to them
	 */
	public static void removeDummyReturnAssignments(){
		if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Removing dummy return assignments...");
		// edges connected to the dummy nodes will be removed once the nodes are removed
		Q dummyNodes = Common.universe().nodesTaggedWithAny(DUMMY_RETURN_NODE, DUMMY_ASSIGNMENT_NODE);
		TreeSet<Node> dummyNodesToRemove = new TreeSet<Node>();
		for(Node dummyNode : dummyNodes.eval().nodes()){
			dummyNodesToRemove.add(dummyNode);
		}
		while(!dummyNodesToRemove.isEmpty()){
			Node dummyNode = dummyNodesToRemove.pollFirst();
			Graph.U.delete(dummyNode);
		}
		
		// these edges were likely added to compensate in irregularities in the graph
		// lets remove them now if we added any
		Q dummyEdges = Common.universe().edgesTaggedWithAny(DUMMY_RETURN_EDGE);
		TreeSet<Edge> dummyEdgesToRemove = new TreeSet<Edge>();
		for(Edge dummyEdge : dummyEdges.eval().edges()){
			dummyEdgesToRemove.add(dummyEdge);
		}
		while(!dummyEdgesToRemove.isEmpty()){
			Edge dummyEdge = dummyEdgesToRemove.pollFirst();
			Graph.U.delete(dummyEdge);
		}
	}
	
	/**
	 * Adds DATAFLOW_DISPLAY_NODE tags to display nodes
	 * Data flow display nodes are added for graph display reasons...
	 */
	public static void addDataFlowDisplayNodeTags() {
		if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Adding data flow display node tags...");
		ArrayList<String> nonDataFlowDisplayNodeTags = new ArrayList<String>();
		for(String tag : XCSG.HIERARCHY.childrenOfOneParent(XCSG.DataFlow_Node)){
			nonDataFlowDisplayNodeTags.add(tag);
		}
		String[] nonDataFlowDisplayNodeTagArray = new String[nonDataFlowDisplayNodeTags.size()];
		nonDataFlowDisplayNodeTags.toArray(nonDataFlowDisplayNodeTagArray);
		Q dataFlowNodes = Common.universe().nodesTaggedWithAny(XCSG.DataFlow_Node);
		Q classVariableAccessNodes = Common.universe().nodesTaggedWithAny(CLASS_VARIABLE_ACCESS);
		Q nonVanillaDataFlowNodes = Common.universe().nodesTaggedWithAny(nonDataFlowDisplayNodeTagArray);
		for(GraphElement dataFlowDisplayNode : dataFlowNodes.difference(classVariableAccessNodes, nonVanillaDataFlowNodes).eval().nodes()){
			dataFlowDisplayNode.tag(DATAFLOW_DISPLAY_NODE);
		}
		
		// sanity check, better to fail fast here than later...
		Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		Q displayNodes = Common.universe().nodesTaggedWithAny(DATAFLOW_DISPLAY_NODE);
		
		// data flow display nodes should be accessible only from a local data flow edge
		Q localDataFlowDisplayNodes = localDataFlowEdges.reverseStep(displayNodes).retainEdges();
		if(localDataFlowDisplayNodes.intersection(displayNodes).eval().nodes().size() != displayNodes.eval().nodes().size()){
			throw new RuntimeException("Unexpected data flow display nodes!");
		}
		
		// data flow display nodes parents should not also be data flow display nodes
		Q dataFlowDisplayNodeParents = localDataFlowEdges.predecessors(displayNodes);
		if(!dataFlowDisplayNodeParents.nodesTaggedWithAny(DATAFLOW_DISPLAY_NODE).eval().nodes().isEmpty()){
			throw new RuntimeException("Unexpected data flow display nodes parents!");
		}
	}
	
	/**
	 * Removes DATAFLOW_DISPLAY_NODE tags to display nodes
	 */
	public static void removeDataFlowDisplayNodeTags() {
		if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Removing data flow display node tags...");
		AtlasSet<Node> dataFlowDisplayNodes = Common.universe().nodesTaggedWithAny(DATAFLOW_DISPLAY_NODE).eval().nodes();
		TreeSet<Node> dataFlowDisplayNodesToUntag = new TreeSet<Node>();
		for(Node dataFlowDisplayNode : dataFlowDisplayNodes){
			dataFlowDisplayNodesToUntag.add(dataFlowDisplayNode);
		}
		while(!dataFlowDisplayNodes.isEmpty()){
			Node dataFlowDisplayNode = dataFlowDisplayNodesToUntag.pollFirst();
			dataFlowDisplayNode.tags().remove(DATAFLOW_DISPLAY_NODE);
		}
	}
	
	public static AtlasSet<Node> getDisplayNodeReferences(GraphElement displayNode){
		Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		Q dataFlowDisplayNodeParents = localDataFlowEdges.predecessors(Common.toQ(displayNode));
		return dataFlowDisplayNodeParents.eval().nodes();
	}
	
	/**
	 * Adds CLASS_VARIABLE_ASSIGNMENT, CLASS_VARIABLE_VALUE, and CLASS_VARIABLE_ACCESS
	 * tags to reads/writes on static variables
	 */
	public static void addClassVariableAccessTags() {
		if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Adding class variable access tags...");
		Q classVariables = Common.universe().nodesTaggedWithAny(XCSG.ClassVariable);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		AtlasSet<Node> classVariableAssignments = interproceduralDataFlowEdges.predecessors(classVariables).eval().nodes();
		for(GraphElement classVariableAssignment : classVariableAssignments){
			classVariableAssignment.tag(CLASS_VARIABLE_ASSIGNMENT);
			classVariableAssignment.tag(CLASS_VARIABLE_ACCESS);
		}
		AtlasSet<Node> classVariableValues = interproceduralDataFlowEdges.successors(classVariables).eval().nodes();
		for(GraphElement classVariableValue : classVariableValues){
			classVariableValue.tag(CLASS_VARIABLE_VALUE);
			classVariableValue.tag(CLASS_VARIABLE_ACCESS);
		}
	}
	
	/**
	 * Removes CLASS_VARIABLE_ASSIGNMENT, CLASS_VARIABLE_VALUE, and CLASS_VARIABLE_ACCESS
	 * tags to reads/writes on static variables
	 */
	public static void removeClassVariableAccessTags() {
		if(PurityPreferences.isGeneralLoggingEnabled()) Log.info("Removing class variable access tags...");
		Q classVariables = Common.universe().nodesTaggedWithAny(XCSG.ClassVariable);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		
		// untag class variable assignments
		AtlasSet<Node> classVariableAssignments = interproceduralDataFlowEdges.predecessors(classVariables).eval().nodes();
		TreeSet<Node> classVariableAssignmentsToUntag = new TreeSet<Node>();
		for(Node classVariableAssignmentToUntag : classVariableAssignments){
			classVariableAssignmentsToUntag.add(classVariableAssignmentToUntag);
		}
		while(!classVariableAssignmentsToUntag.isEmpty()){
			Node classVariableAssignmentToUntag = classVariableAssignmentsToUntag.pollFirst();
			classVariableAssignmentToUntag.tags().remove(CLASS_VARIABLE_ASSIGNMENT);
			classVariableAssignmentToUntag.tags().remove(CLASS_VARIABLE_ACCESS);
		}
		// untag class variable values
		AtlasSet<Node> classVariableValues = interproceduralDataFlowEdges.successors(classVariables).eval().nodes();
		TreeSet<Node> classVariableValuesToUntag = new TreeSet<Node>();
		for(Node classVariableValueToUntag : classVariableValues){
			classVariableValuesToUntag.add(classVariableValueToUntag);
		}
		while(!classVariableValuesToUntag.isEmpty()){
			Node classVariableValueToUntag = classVariableValuesToUntag.pollFirst();
			classVariableValueToUntag.tags().remove(CLASS_VARIABLE_VALUE);
			classVariableValueToUntag.tags().remove(CLASS_VARIABLE_ACCESS);
		}
	}
	
	/**
	 * Given a callsite this method returns the invoked method signature
	 * @param callsite
	 * @return
	 */
	public static Node getInvokedMethodSignature(GraphElement callsite) {
		// XCSG.InvokedSignature connects a dynamic dispatch to its signature method
		// XCSG.InvokedFunction connects a static dispatch to it actual target method
		Q invokedEdges = Common.universe().edgesTaggedWithAny(XCSG.InvokedSignature, XCSG.InvokedFunction);
		Node method = invokedEdges.successors(Common.toQ(callsite)).eval().nodes().getFirst();
		return method;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param node
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean removeTypes(Node node, Set<ImmutabilityTypes> typesToRemove){
		Set<ImmutabilityTypes> typeSet = getTypes(node);
		String logMessage = "Remove: " + typesToRemove.toString() + " from " + typeSet.toString() + " for " + node.getAttr(XCSG.name);
		boolean typesChanged = typeSet.removeAll(typesToRemove);
		if(typesChanged){
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info(logMessage);
			if(PurityPreferences.isDebugLoggingEnabled() && typeSet.isEmpty()) Log.warning("Remove result in an empty type set.");
		}
		return typesChanged;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param node
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean removeTypes(Node node, ImmutabilityTypes... types){
		EnumSet<ImmutabilityTypes> typesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes type : types){
			typesToRemove.add(type);
		}
		return removeTypes(node, typesToRemove);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<ImmutabilityTypes> getTypes(GraphElement ge){
		if(ge.hasAttr(IMMUTABILITY_QUALIFIERS)){
			return (Set<ImmutabilityTypes>) ge.getAttr(IMMUTABILITY_QUALIFIERS);
		} else {
			EnumSet<ImmutabilityTypes> qualifiers = getDefaultTypes(ge);
			ge.putAttr(IMMUTABILITY_QUALIFIERS, qualifiers);
			return qualifiers;
		}
	}
	
	public static GraphElement getObjectType(GraphElement ge) {
		Q typeOfEdges = Common.universe().edgesTaggedWithAny(XCSG.TypeOf);
		return typeOfEdges.successors(Common.toQ(ge)).eval().nodes().getFirst();
	}
	
	public static AtlasSet<Node> parseReferences(Node node){
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Parsing reference for " + node.address().toAddressString());
		AtlasSet<Node> parsedReferences = new AtlasHashSet<Node>();
		TreeSet<Node> worklist = new TreeSet<Node>();
		worklist.add(node);
		
		Q dataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.DataFlow_Edge);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		
		while(!worklist.isEmpty()){
			GraphElement reference = worklist.pollFirst();
			if(reference != null && needsProcessing(reference)){
				if(reference.taggedWith(XCSG.Cast)){
					for(Node workItem : dataFlowEdges.predecessors(Common.toQ(reference)).eval().nodes()){
						worklist.add(workItem);
					}
					continue;
				}
				
				if(reference.taggedWith(DATAFLOW_DISPLAY_NODE)){
					for(Node workItem : Utilities.getDisplayNodeReferences(reference)){
						worklist.add(workItem);
					}
					continue;
				}
				
				if(reference.taggedWith(XCSG.CallSite)){
					// parse return, a callsite on a callsite must be a callsite on the resulting object from the first callsite
					Node method = Utilities.getInvokedMethodSignature(reference);
					worklist.add(Common.toQ(method).children().nodesTaggedWithAny(XCSG.ReturnValue).eval().nodes().getFirst());
					continue;
				}
				
				// get the field for instance and class variable assignments
				if(reference.taggedWith(XCSG.InstanceVariableAssignment) || reference.taggedWith(Utilities.CLASS_VARIABLE_ASSIGNMENT)){
					for(Node workItem : interproceduralDataFlowEdges.successors(Common.toQ(reference)).eval().nodes()){
						worklist.add(workItem);
					}
					continue;
				}
				
				// get the field for instance and class variable values
				if(reference.taggedWith(XCSG.InstanceVariableValue) || reference.taggedWith(Utilities.CLASS_VARIABLE_VALUE)){
					for(Node workItem : interproceduralDataFlowEdges.predecessors(Common.toQ(reference)).eval().nodes()){
						worklist.add(workItem);
					}
					continue;
				}
				
				// get the array components being written to
				if(reference.taggedWith(XCSG.ArrayWrite)){
					for(Node workItem : interproceduralDataFlowEdges.successors(Common.toQ(reference)).eval().nodes()){
						worklist.add(workItem);
					}
					continue;
				}
				
				// get the array components being read from
				if(reference.taggedWith(XCSG.ArrayRead)){
					for(Node workItem : interproceduralDataFlowEdges.predecessors(Common.toQ(reference)).eval().nodes()){
						worklist.add(workItem);
					}
					continue;
				}
				
				String message = "Unhandled reference type for GraphElement " + node.address().toAddressString() + "\n" + node.toString();
				RuntimeException e = new RuntimeException(message);
				Log.error(message, e);
				throw e;
			} else {
				if(reference == null){
					String message = "Null reference for GraphElement " + node.address().toAddressString();
					RuntimeException e = new RuntimeException(message);
					Log.error(message, e);
					throw e;
				} else {
					parsedReferences.add(reference);
				}
			}
		}
		
		return parsedReferences;
	}
	
	private static boolean needsProcessing(GraphElement ge){
		if(ge.taggedWith(DATAFLOW_DISPLAY_NODE)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Cast)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.CallSite)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.InstanceVariableAccess) || ge.taggedWith(Utilities.CLASS_VARIABLE_ACCESS)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.ArrayAccess)){
			return true;
		}
		
		return !isTypable(ge);
	}
	
	public static boolean isTypable(GraphElement ge){
		// invalid types
		if(ge.taggedWith(XCSG.InstanceVariableAccess) || ge.taggedWith(Utilities.CLASS_VARIABLE_ACCESS)){
			return false;
		}
		
		if(ge.taggedWith(XCSG.ArrayAccess)){
			return false;
		}
		
		// valid types
		if(ge.taggedWith(DUMMY_ASSIGNMENT_NODE) || ge.taggedWith(DUMMY_RETURN_NODE)){
			// these are dummy read only nodes
			return true;
		}
		
		if(ge.taggedWith(XCSG.Null)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Literal) || ge.taggedWith(XCSG.Type)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Method)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Identity)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Parameter)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.ReturnValue)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.InstanceVariable) || ge.taggedWith(XCSG.ClassVariable)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.ArrayComponents)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.ParameterPass)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Operator)){
			return true;
		} 
		
		if(ge.taggedWith(XCSG.CaughtValue)){
			return true;
		} 
		
		if(ge.taggedWith(XCSG.ElementFromCollection)){
			return true;
		}
		
		if(ge.taggedWith(XCSG.Assignment)){
			if(!ge.taggedWith(XCSG.InstanceVariableAssignment) && !ge.taggedWith(Utilities.CLASS_VARIABLE_ASSIGNMENT)){
				return true;
			}
		}
		
//		if(isDefaultReadonlyType(getObjectType(ge))){
//			return true;
//		}
		
		// something made it through the gap...
		return false;
	}
	
	public static EnumSet<ImmutabilityTypes> getDefaultTypes(GraphElement ge) {
		if(!isTypable(ge)){
			RuntimeException e = new RuntimeException("Unexpected graph element: " + ge.address());
			Log.error("Unexpected graph element: " + ge.address(), e);
			throw e;
		}
		
		EnumSet<ImmutabilityTypes> qualifiers = EnumSet.noneOf(ImmutabilityTypes.class);
		
		if(ge.taggedWith(DUMMY_ASSIGNMENT_NODE) || ge.taggedWith(DUMMY_RETURN_NODE)){
			// these are dummy read only nodes that help to provide context sensitivity
			// in unassigned callsites or void methods
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Null)){
			// null does not modify the stack or heap so it is readonly
			// however in order to satisfy constraints the other types should be initialized
			// note that assignments of nulls to a field can still mutate an object
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Literal) || ge.taggedWith(XCSG.Type)){
			// several java objects are readonly for all practical purposes
			// however in order to satisfy constraints the other types should be initialized
			// Note that at least in Jimple its possible for a Type -> Literal -> Formal Parameter
			// not the normal Type -> Literal -> Actual Parameter -> Formal Parameter
			// so in this case the Type graph element should be treated as the type literal
			// and hence readonly...TODO: bug EnSoft to see if this graph pattern is expected!
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
			// Type Rule 1 - TNEW
			// return type of a constructor is only mutable
			// x = new C(); // no effect on qualifier to x
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.ReturnValue)){
			// Section 2.4 of Reference 1
			// "Method returns are initialized S(ret) = {readonly, polyread} for each method m"
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
		} else if (ge.taggedWith(XCSG.Parameter)){
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Identity)){
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
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
		} else if(ge.taggedWith(XCSG.Operator)){
			// the result of a primitive operation on primitives or primitive references is always readonly
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.ArrayComponents)){
			qualifiers.add(ImmutabilityTypes.READONLY);
//			qualifiers.add(ImmutabilityTypes.POLYREAD); // an array component is basically a local reference, TODO: what about array fields?
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.CaughtValue)){
			// caught exceptions could be polyread since they could come from multiple call stacks
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD); 
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		}  else if(ge.taggedWith(XCSG.ElementFromCollection)){
			// TODO: should probably treat these like array components (mutations to these mutate the collection)
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.ParameterPass)){
			// Section 2.4 of Reference 1
			// "All other references are initialized to the maximal
			// set of qualifiers, i.e. S(x) = {readonly, polyread, mutable}"
			// But, what does it mean for a local reference to be polyread? ~Ben
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Assignment)){
			if(!ge.taggedWith(XCSG.InstanceVariableAssignment) && !ge.taggedWith(Utilities.CLASS_VARIABLE_ASSIGNMENT)){
				// could be a local reference
				// Section 2.4 of Reference 1
				// "All other references are initialized to the maximal
				// set of qualifiers, i.e. S(x) = {readonly, polyread, mutable}"
				// But, what does it mean for a local reference to be polyread? ~Ben
				qualifiers.add(ImmutabilityTypes.READONLY);
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			}
		}
		
//		else if(isDefaultReadonlyType(Utilities.getObjectType(ge))){
//			// several java objects are readonly for all practical purposes
//			// however in order to satisfy constraints the other types should be initialized
//			qualifiers.add(ImmutabilityTypes.READONLY);
//			qualifiers.add(ImmutabilityTypes.POLYREAD);
//			qualifiers.add(ImmutabilityTypes.MUTABLE);
//		} 
		
		else {
			RuntimeException e = new RuntimeException("Unexpected graph element: " + ge.address());
			Log.error("Unexpected graph element: " + ge.address(), e);
			throw e;
		}
		return qualifiers;
	}
	
	/**
	 * Returns the fields or local variables accessed for an instance variable access
	 * @param fieldAccess
	 * @return
	 */
	public static AtlasSet<Node> getAccessedContainers(GraphElement fieldAccess){
		Q instanceVariableAccessedEdges = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed);
		Q variablesAccessed = instanceVariableAccessedEdges.reverse(Common.toQ(fieldAccess));
		Q instanceVariablesAccessed = variablesAccessed.nodesTaggedWithAny(XCSG.InstanceVariableAccess);
		Q classVariablesAccessed = variablesAccessed.nodesTaggedWithAny(CLASS_VARIABLE_ACCESS);
		Q localVariables = variablesAccessed.difference(instanceVariablesAccessed, classVariablesAccessed);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		Q fieldsAccessed = interproceduralDataFlowEdges.predecessors(instanceVariablesAccessed.union(classVariablesAccessed));
		return localVariables.union(fieldsAccessed).eval().nodes();
	}
	
	/**
	 * Returns the containing method of a given graph element or null if one is not found
	 * @param node
	 * @return
	 */
	public static Node getContainingMethod(Node node) {
		// NOTE: the enclosing method may be two steps or more above
		return getContainingNode(node, XCSG.Method);
	}
	
	/**
	 * Find the next immediate containing node with the given tag.
	 * 
	 * @param node 
	 * @param containingTag
	 * @return the next immediate containing node, or null if none exists; never returns the given node
	 */
	public static Node getContainingNode(Node node, String containingTag) {
		if(node == null){
			return null;
		}
		
		while(true) {
			GraphElement containsEdge = Graph.U.edges(node, NodeDirection.IN).taggedWithAll(XCSG.Contains).getFirst();
			if(containsEdge == null){
				return null;
			}
			
			Node parent = containsEdge.getNode(EdgeDirection.FROM);
			if(parent.taggedWith(containingTag)){
				return parent;
			}
			
			node = parent;
		}
	}
	
}
