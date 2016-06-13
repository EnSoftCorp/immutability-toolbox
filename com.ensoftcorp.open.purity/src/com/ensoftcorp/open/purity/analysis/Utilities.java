package com.ensoftcorp.open.purity.analysis;

import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.NodeDirection;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.purity.log.Log;
import com.ensoftcorp.open.purity.ui.PurityPreferences;

public class Utilities {
	
	/**
	 * Used as an attribute key to temporarily compute the potential immutability qualifiers
	 */
	public static final String IMMUTABILITY_QUALIFIERS = "IMMUTABILITY_QUALIFIERS";
	
	/**
	 * Used as an attribute key to temporarily compute the potential static immutability qualifiers
	 */
	public static final String STATIC_IMMUTABILITY_QUALIFIERS = "STATIC_IMMUTABILITY_QUALIFIERS";
	
	// TODO: bug EnSoft to make tags like this...
	public static final String CLASS_VARIABLE_ASSIGNMENT = "CLASS_VARIABLE_ASSIGNMENT";
	public static final String CLASS_VARIABLE_VALUE = "CLASS_VARIABLE_VALUE";
	public static final String CLASS_VARIABLE_ACCESS = "CLASS_VARIABLE_ACCESS";
	
	/**
	 * Adds CLASS_VARIABLE_ASSIGNMENT, CLASS_VARIABLE_VALUE, and CLASS_VARIABLE_ACCESS
	 * tags to reads/writes on static variables
	 */
	public static void addClassVariableAccessTags() {
		Q classVariables = Common.universe().nodesTaggedWithAny(XCSG.ClassVariable);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		AtlasSet<GraphElement> classVariableAssignments = interproceduralDataFlowEdges.predecessors(classVariables).eval().nodes();
		for(GraphElement classVariableAssignment : classVariableAssignments){
			classVariableAssignment.tag(CLASS_VARIABLE_ASSIGNMENT);
			classVariableAssignment.tag(CLASS_VARIABLE_ACCESS);
		}
		AtlasSet<GraphElement> classVariableValues = interproceduralDataFlowEdges.successors(classVariables).eval().nodes();
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
		Q classVariables = Common.universe().nodesTaggedWithAny(XCSG.ClassVariable);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		AtlasSet<GraphElement> classVariableAssignments = interproceduralDataFlowEdges.predecessors(classVariables).eval().nodes();
		for(GraphElement classVariableAssignment : classVariableAssignments){
			classVariableAssignment.tags().remove(CLASS_VARIABLE_ASSIGNMENT);
			classVariableAssignment.tags().remove(CLASS_VARIABLE_ACCESS);
		}
		AtlasSet<GraphElement> classVariableValues = interproceduralDataFlowEdges.successors(classVariables).eval().nodes();
		for(GraphElement classVariableValue : classVariableValues){
			classVariableValue.tags().remove(CLASS_VARIABLE_VALUE);
			classVariableValue.tags().remove(CLASS_VARIABLE_ACCESS);
		}
	}
	
	/**
	 * Given a callsite this method returns the invoked method signature
	 * @param callsite
	 * @return
	 */
	public static GraphElement getInvokedMethodSignature(GraphElement callsite) {
		// XCSG.InvokedSignature connects a dynamic dispatch to its signature method
		// XCSG.InvokedFunction connects a static dispatch to it actual target method
		Q invokedEdges = Common.universe().edgesTaggedWithAny(XCSG.InvokedSignature, XCSG.InvokedFunction);
		GraphElement method = invokedEdges.successors(Common.toQ(callsite)).eval().nodes().getFirst();
		return method;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean removeTypes(GraphElement ge, Set<ImmutabilityTypes> typesToRemove){
		Set<ImmutabilityTypes> typeSet = getTypes(ge);
		String logMessage = "Remove: " + typesToRemove.toString() + " from " + typeSet.toString() + " for " + ge.getAttr(XCSG.name);
		boolean typesChanged = typeSet.removeAll(typesToRemove);
		if(typesChanged){
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info(logMessage);
		}
		return typesChanged;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean removeTypes(GraphElement ge, ImmutabilityTypes... types){
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
	public static boolean removeStaticTypes(GraphElement ge, Set<ImmutabilityTypes> typesToRemove){
		Set<ImmutabilityTypes> typeSet = getStaticTypes(ge);
		String logMessage = "Remove: " + typesToRemove.toString() + " from " + typeSet.toString() + " for " + ge.getAttr(XCSG.name);
		boolean typesChanged = typeSet.removeAll(typesToRemove);
		if(typesChanged){
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info(logMessage);
		}
		return typesChanged;
	}
	
	/**
	 * Sets the type qualifier for a graph element
	 * @param ge
	 * @param qualifier
	 * @return Returns true if the type qualifier changed
	 */
	public static boolean removeStaticTypes(GraphElement ge, ImmutabilityTypes... types){
		EnumSet<ImmutabilityTypes> typesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes type : types){
			typesToRemove.add(type);
		}
		return removeStaticTypes(ge, typesToRemove);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<ImmutabilityTypes> getStaticTypes(GraphElement ge){
		if(ge.hasAttr(STATIC_IMMUTABILITY_QUALIFIERS)){
			return (Set<ImmutabilityTypes>) ge.getAttr(STATIC_IMMUTABILITY_QUALIFIERS);
		} else {
			EnumSet<ImmutabilityTypes> qualifiers = PurityAnalysis.getDefaultStaticTypes(ge);
			ge.putAttr(STATIC_IMMUTABILITY_QUALIFIERS, qualifiers);
			return qualifiers;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Set<ImmutabilityTypes> getTypes(GraphElement ge){
		if(ge.hasAttr(IMMUTABILITY_QUALIFIERS)){
			return (Set<ImmutabilityTypes>) ge.getAttr(IMMUTABILITY_QUALIFIERS);
		} else {
			EnumSet<ImmutabilityTypes> qualifiers = PurityAnalysis.getDefaultTypes(ge);
			ge.putAttr(IMMUTABILITY_QUALIFIERS, qualifiers);
			return qualifiers;
		}
	}
	
	/**
	 * Returns the fields or local variables accessed for an instance variable access
	 * @param variableAccess
	 * @return
	 */
	public static AtlasSet<GraphElement> getAccessedContainers(GraphElement variableAccess){
		Q instanceVariableAccessedEdges = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed);
		Q variablesAccessed = instanceVariableAccessedEdges.reverse(Common.toQ(variableAccess));
		Q instanceVariablesAccessed = variablesAccessed.nodesTaggedWithAny(XCSG.InstanceVariableAccess);
		Q classVariablesAccessed = variablesAccessed.nodesTaggedWithAny(CLASS_VARIABLE_ACCESS);
		Q localVariables = variablesAccessed.difference(instanceVariablesAccessed, classVariablesAccessed);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		Q fieldsAccessed = interproceduralDataFlowEdges.predecessors(instanceVariablesAccessed.union(classVariablesAccessed));
		return localVariables.union(fieldsAccessed).eval().nodes();
	}
	
	/**
	 * Returns the containing method of a given graph element or null if one is not found
	 * @param ge
	 * @return
	 */
	public static GraphElement getContainingMethod(GraphElement ge) {
		// NOTE: the enclosing method may be two steps or more above
		return getContainingNode(ge, XCSG.Method);
	}
	
	/**
	 * Find the next immediate containing node with the given tag.
	 * 
	 * @param node 
	 * @param containingTag
	 * @return the next immediate containing node, or null if none exists; never returns the given node
	 */
	public static GraphElement getContainingNode(GraphElement node, String containingTag) {
		if(node == null){
			return null;
		}
		
		while(true) {
			GraphElement containsEdge = Graph.U.edges(node, NodeDirection.IN).taggedWithAll(XCSG.Contains).getFirst();
			if(containsEdge == null){
				return null;
			}
			
			GraphElement parent = containsEdge.getNode(EdgeDirection.FROM);
			if(parent.taggedWith(containingTag)){
				return parent;
			}
			
			node = parent;
		}
	}
	
}
