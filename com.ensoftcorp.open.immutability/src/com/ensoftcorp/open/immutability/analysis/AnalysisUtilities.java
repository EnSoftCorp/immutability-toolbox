package com.ensoftcorp.open.immutability.analysis;

import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.java.commons.wishful.JavaStopGap;
import com.ensoftcorp.open.jimple.commons.wishful.JimpleStopGap;

public class AnalysisUtilities {
	
	/**
	 * Used as an attribute key to temporarily compute the potential immutability qualifiers
	 */
	public static final String IMMUTABILITY_QUALIFIERS = "IMMUTABILITY_QUALIFIERS";
	
	public static final String DUMMY_ASSIGNMENT_NODE = "DUMMY_ASSIGNMENT_NODE";
	public static final String DUMMY_RETURN_NODE = "DUMMY_RETURN_NODE";
	public static final String DUMMY_RETURN_EDGE = "DUMMY_RETURN_EDGE";
	
	/**
	 * Adds DUMMY_RETURN_NODE to void methods and DUMMY_ASSIGNMENT_NODE from unassigned callsites to a dummy assignment node
	 */
	public static void addDummyReturnAssignments(){
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Adding dummy return assignments...");
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
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Removing dummy return assignments...");
		// edges connected to the dummy nodes will be removed once the nodes are removed
		Q dummyNodes = Common.universe().nodesTaggedWithAny(DUMMY_RETURN_NODE, DUMMY_ASSIGNMENT_NODE);
		AtlasHashSet<Node> dummyNodesToRemove = new AtlasHashSet<Node>();
		for(Node dummyNode : dummyNodes.eval().nodes()){
			dummyNodesToRemove.add(dummyNode);
		}
		while(!dummyNodesToRemove.isEmpty()){
			Node dummyNode = dummyNodesToRemove.getFirst();
			dummyNodesToRemove.remove(dummyNode);
			Graph.U.delete(dummyNode);
		}
		
		// these edges were likely added to compensate in irregularities in the graph
		// lets remove them now if we added any
		Q dummyEdges = Common.universe().edgesTaggedWithAny(DUMMY_RETURN_EDGE);
		AtlasHashSet<Edge> dummyEdgesToRemove = new AtlasHashSet<Edge>();
		for(Edge dummyEdge : dummyEdges.eval().edges()){
			dummyEdgesToRemove.add(dummyEdge);
		}
		while(!dummyEdgesToRemove.isEmpty()){
			Edge dummyEdge = dummyEdgesToRemove.getFirst();
			dummyEdgesToRemove.remove(dummyEdge);
			Graph.U.delete(dummyEdge);
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
		if(node != null){
			Set<ImmutabilityTypes> typeSet = getTypes(node);
			String logMessage = "Remove: " + typesToRemove.toString() + " from " + typeSet.toString() + " for " + node.getAttr(XCSG.name);
			boolean typesChanged = typeSet.removeAll(typesToRemove);
			if(typesChanged){
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info(logMessage);
				if(ImmutabilityPreferences.isDebugLoggingEnabled() && getTypes(node).isEmpty()) {
					Log.warning("Remove on '" + node.getAttr(XCSG.name).toString() + "' resulted in an empty type set.\n" + node.toString(), 
						new RuntimeException(node.getAttr(XCSG.name).toString() + " is untyped."));
				}
			}
			return typesChanged;
		} else {
			return false;
		}
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param node
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean removeTypes(Node node, ImmutabilityTypes... types){
		if(node != null){
			EnumSet<ImmutabilityTypes> typesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes type : types){
				typesToRemove.add(type);
			}
			return removeTypes(node, typesToRemove);
		} else {
			return false;
		}
	}
	
	/**
	 * Adds the MUTABLE type qualifier for a graph element
	 * USE EXTREME CAUTION WHEN USING THIS METHOD!!!!
	 * ADDING TYPES CAN BREAK FIXED POINT GUARENTEES!!!
	 * @param node
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean addMutable(Node node){
		if(node != null){
			return addTypes(node, ImmutabilityTypes.MUTABLE);
		} else {
			return false;
		}
	}
	
//	/**
//	 * Sets the MUTABLE type qualifier for a graph element
//	 * USE EXTREME CAUTION WHEN USING THIS METHOD!!!!
//	 * ADDING TYPES CAN BREAK FIXED POINT GUARENTEES!!!
//	 * @param node
//	 * @param qualifier
//	 * @return Returns true if the type qualifier changed
//	 */
//	public static boolean setMutable(Node node){
//		if(node != null){
//			Set<ImmutabilityTypes> types = getTypes(node); 
//			if(types.size() == 1 && types.contains(ImmutabilityTypes.MUTABLE)){
//				return false;
//			} else{
//				types.clear();
//				types.add(ImmutabilityTypes.MUTABLE);
//				return true;
//			}
//		} else {
//			return false;
//		}
//	}
	
	/**
	 * Adds a type qualifier for a graph element
	 * USE EXTREME CAUTION WHEN USING THIS METHOD!!!!
	 * ADDING TYPES CAN BREAK FIXED POINT GUARENTEES!!!
	 * @param node
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean addTypes(Node node, ImmutabilityTypes... types){
		if(node != null){
			EnumSet<ImmutabilityTypes> typesToAdd = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes type : types){
				typesToAdd.add(type);
			}
			return addTypes(node, typesToAdd);
		} else {
			return false;
		}
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * USE EXTREME CAUTION WHEN USING THIS METHOD!!!!
	 * ADDING TYPES CAN BREAK FIXED POINT GUARENTEES!!!
	 * @param node
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean addTypes(Node node, Set<ImmutabilityTypes> typesToAdd){
		if(node != null){
		Set<ImmutabilityTypes> typeSet = getTypes(node);
			String logMessage = "Add: " + typesToAdd.toString() + " to " + typeSet.toString() + " for " + node.getAttr(XCSG.name);
			boolean typesChanged = typeSet.addAll(typesToAdd);
			if(typesChanged){
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info(logMessage);
			}
			return typesChanged;
		} else {
			return false;
		}
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
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Parsing reference for " + node.address().toAddressString());
		
		AtlasSet<Node> parsedReferences = new AtlasHashSet<Node>();
		AtlasHashSet<Node> worklist = new AtlasHashSet<Node>();
		worklist.add(node);
		
		Q dataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.DataFlow_Edge);
		
		while(!worklist.isEmpty()){
			GraphElement reference = worklist.getFirst();
			worklist.remove(reference);
			if(reference != null && needsProcessing(reference)){
				if(reference.taggedWith(XCSG.Cast)){
					for(Node workItem : dataFlowEdges.predecessors(Common.toQ(reference)).eval().nodes()){
						worklist.add(workItem);
					}
					continue;
				}
				
				if(reference.taggedWith(JimpleStopGap.DATAFLOW_DISPLAY_NODE)){
					for(Node workItem : JimpleStopGap.getDisplayNodeReferences(reference)){
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
		if(ge.taggedWith(XCSG.Cast)){
			return true;
		}
		
		if(ge.taggedWith(JimpleStopGap.DATAFLOW_DISPLAY_NODE)){
			return true;
		}

		// don't process callsites, field accesses, or array accesses
		// they will be handled in the main inference routine
		if(ge.taggedWith(XCSG.CallSite)){
			return false;
		}
		if(ge.taggedWith(XCSG.InstanceVariableAccess) || ge.taggedWith(JavaStopGap.CLASS_VARIABLE_ACCESS)){
			return false;
		}
		if(ge.taggedWith(XCSG.ArrayAccess)){
			return false;
		}
		
		// if the reference is typeable then no processing is needed
		return !isTypable(ge);
	}
	
	public static boolean isTypable(GraphElement ge){
		// invalid types
		if(ge.taggedWith(XCSG.InstanceVariableAccess) || ge.taggedWith(JavaStopGap.CLASS_VARIABLE_ACCESS)){
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
			if(!ge.taggedWith(XCSG.InstanceVariableAssignment) && !ge.taggedWith(JavaStopGap.CLASS_VARIABLE_ASSIGNMENT)){
				return true;
			}
		}
		
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
//			// according to Reference 1 this should not be a default type,
//			// but looking at the reiminfer reference implementation there
//			// are several cases where the mutable type gets "added" during 
//			// constraint checking. Since mutable is the most specific type
//			// a polyread will always be chosen over it if there is one, so
//			// instead of adding hacks all over its better to just admit the 
//			// issue with the type system and add mutable to the default type
//			// of fields...we could promote mutables to polyread after the 
//			// fact if we wanted to clean things up
//			// Reference: https://github.com/proganalysis/type-inference/blob/master/object-immutability/src/edu/rpi/reim/ReimTransformer.java#L250
			if(ImmutabilityPreferences.isAllowDefaultMutableInstancesVariablesEnabled()){
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			}
		} else if(ge.taggedWith(XCSG.ClassVariable)){
			// Section 3 of Reference 1
			// static fields are initialized to S(sf) = {readonly, mutable}
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Method)){
			// Section 3 of Reference 1
			// methods can have a static type of {readonly, polyread, mutable}
			// From Reference 1: "qm is mutable when m accesses static state
			// through some static field and then mutates this static state;
			// qm is polyread if m accesses static state but does not mutate
			// this state directly, however, m may return this static state
			// to the caller and the caller may mutate it; qm is readonly
			// otherwise"
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
			qualifiers.add(ImmutabilityTypes.POLYREAD); // TODO: what does it mean for a local reference to be polyread? ~Ben
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.CaughtValue)){
			// caught exceptions could be polyread since they could come from multiple call stacks
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD); 
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		}  else if(ge.taggedWith(XCSG.ElementFromCollection)){			
			// TODO: should probably treat these like array components (mutations to these mutate the collection)
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD); // TODO: what does it mean for a local reference to be polyread? ~Ben
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.ParameterPass)){
			// Section 2.4 of Reference 1
			// "All other references are initialized to the maximal
			// set of qualifiers, i.e. S(x) = {readonly, polyread, mutable}"
			qualifiers.add(ImmutabilityTypes.READONLY);
			qualifiers.add(ImmutabilityTypes.POLYREAD); // TODO: what does it mean for a local reference to be polyread? ~Ben
			qualifiers.add(ImmutabilityTypes.MUTABLE);
		} else if(ge.taggedWith(XCSG.Assignment)){
			if(!ge.taggedWith(XCSG.InstanceVariableAssignment) && !ge.taggedWith(JavaStopGap.CLASS_VARIABLE_ASSIGNMENT)){
				// could be a local reference
				// Section 2.4 of Reference 1
				// "All other references are initialized to the maximal
				// set of qualifiers, i.e. S(x) = {readonly, polyread, mutable}"
				qualifiers.add(ImmutabilityTypes.READONLY);
				qualifiers.add(ImmutabilityTypes.POLYREAD); // TODO: what does it mean for a local reference to be polyread? ~Ben
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			}
		} else {
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
		Q classVariablesAccessed = variablesAccessed.nodesTaggedWithAny(JavaStopGap.CLASS_VARIABLE_ACCESS);
		Q localVariables = variablesAccessed.difference(instanceVariablesAccessed, classVariablesAccessed);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		Q fieldsAccessed = interproceduralDataFlowEdges.predecessors(instanceVariablesAccessed.union(classVariablesAccessed));
		return localVariables.union(fieldsAccessed).eval().nodes();
	}
	
}
