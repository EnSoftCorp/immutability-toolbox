package com.ensoftcorp.open.immutability.analysis.checkers;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.StandardQueries;
import com.ensoftcorp.open.immutability.analysis.solvers.XAdaptYGreaterThanEqualZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYAdaptZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYConstraintSolver;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class CallChecker {

	/**
	 * Let, x=y.m(z)
	 * @param x
	 * @param y
	 * @param identity
	 * @param method
	 * @param ret
	 * @param parametersPassedEdges
	 * @param containingMethod 
	 * @return
	 */
	public static boolean handleCall(Node x, Node y, Node identity, Node method, Node ret, AtlasSet<Edge> parametersPassedEdges, Node containingMethod) {
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TCALL (x=y.m(z), x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", m=" + method.getAttr("##signature") + ")");
		
		boolean typesChanged = false;
		
		/////////////////////// start qx adapt qret <: qx /////////////////////// 
		if(processReturnAssignmentConstraints(x, ret)){
			typesChanged = true;
		}
		/////////////////////// end qx adapt qret <: qx ///////////////////////  
		
		/////////////////////// start qy <: qx adapt qthis /////////////////////// 
		
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint qy <: qx adapt qthis");
		
		if(XAdaptYGreaterThanEqualZConstraintSolver.satisify(x, identity, y)){
			typesChanged = true;
		}
		
		/////////////////////// end qy <: qx adapt qthis ///////////////////////

		/////////////////////// start qz <: qx adapt qp ///////////////////////
		if(processParameterConstraints(x, parametersPassedEdges)){
			typesChanged = true;
		}
		/////////////////////// end qz <: qx adapt qp /////////////////////////
		
		// check if method overrides another method (of course this will be empty for static methods)
		Q overridesEdges = Common.universe().edgesTaggedWithAny(XCSG.Overrides);
		GraphElement overriddenMethod = overridesEdges.successors(Common.toQ(method)).eval().nodes().getFirst();
		if(overriddenMethod != null){
			if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TCALL (Overridden Method)");
			
			// Method (method) -Contains-> ReturnValue (ret)
			Node overriddenRet = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.ReturnValue).eval().nodes().getFirst();
			
			// constraint: overriddenReturn <: return
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint overriddenReturn <: return");
			
			if(XGreaterThanEqualYConstraintSolver.satisify(ret, overriddenRet)){
				typesChanged = true;
			}
			
			// Method (method) -Contains-> Identity
			Node overriddenMethodIdentity = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();

			// constraint: this <: overriddenThis 
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint this <: overriddenThis");
			
			if(XGreaterThanEqualYConstraintSolver.satisify(overriddenMethodIdentity, identity)){
				typesChanged = true;
			}

			// Method (method) -Contains-> Parameter (p1, p2, ...)
			AtlasSet<Node> overriddenMethodParameters = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
			
			// get the parameters of the method
			AtlasSet<Node> parameters = Common.toQ(parametersPassedEdges).nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
					
			// for each parameter and overridden parameter pair
			// constraint: p <: pOverriden
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint p <: pOverriden");
			long numParams = parameters.size();
			long numOverriddenParams = overriddenMethodParameters.size();
			if(numParams == numOverriddenParams){
				if(numOverriddenParams > 0){
					for(int i=0; i<numOverriddenParams; i++){
						Node p = Common.toQ(parameters).selectNode(XCSG.parameterIndex, i).eval().nodes().getFirst();
						Node pOverridden = Common.toQ(overriddenMethodParameters).selectNode(XCSG.parameterIndex, i).eval().nodes().getFirst();

						if(XGreaterThanEqualYConstraintSolver.satisify(pOverridden, p)){
							typesChanged = true;
						}
					}
				}
			} else {
				// note it seems that some Jimple methods are missing the parameter passed to edges 
				// which is causing us not to find the parameters for the base method
				// this should not happen and is a bug in Atlas!
				Log.warning("Missing parameters for Method: " + method.address().toAddressString() 
						+ " or Overriden Method: " + overriddenMethod.address().toAddressString(), 
						new RuntimeException("Parameter counts do not match!"));
			}
		}
		
		return typesChanged;
	}
	
	/**
	 * Type Rule 8 - TSCALL
	 * let, x = m(z)
	 * 
	 * @param x
	 * @param method
	 * @param ret
	 * @param parametersPassedEdges
	 * @return
	 */
	public static boolean handleStaticCall(Node x, Node callsite, Node method, Node ret, AtlasSet<Edge> parametersPassedEdges) {
		
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TSCALL (x=y.m(z), x=" + x.getAttr(XCSG.name) + ", m=" + method.getAttr("##signature") + ")");
		
		boolean typesChanged = false;
		
		/////////////////////// start qx adapt qret <: qx /////////////////////// 
		if(processReturnAssignmentConstraints(x, ret)){
			typesChanged = true;
		}
		/////////////////////// end qx adapt qret <: qx ///////////////////////// 

		/////////////////////// start qz <: qx adapt qp /////////////////////////
		if(processParameterConstraints(x, parametersPassedEdges)){
			typesChanged = true;
		}
		/////////////////////// end qz <: qx adapt qp ///////////////////////////
		
		/////////////////////// start qm' <: qx adapt qm /////////////////////////
		// m' is the method that contains the callsite m()
		Node containingMethod = StandardQueries.getContainingFunction(callsite);
		if(processStaticDispatchConstraints(x, method, containingMethod)){
			typesChanged = true;
		}
		/////////////////////// end qm' <: qx adapt qm ///////////////////////////
		
		return typesChanged;
	}

	private static boolean processStaticDispatchConstraints(Node x, Node method, Node containingMethod) {
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Static Dispatch Constraint qm' <: qx adapt qm");

		// qm' <: qx adapt qm
		// = qx adapt qm :> qm'
		return XAdaptYGreaterThanEqualZConstraintSolver.satisify(x, method, containingMethod);
	}
	
	/**
	 * qz <: qx adapt qp
	 * @param x
	 * @param parametersPassedEdges
	 * @return
	 */
	private static boolean processParameterConstraints(Node x, AtlasSet<Edge> parametersPassedEdges) {
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint qz <: qx adapt qp");

		boolean typesChanged = false;
		
		// for each z,p pair process s(x), s(z), and s(p)
		for(GraphElement parametersPassedEdge : parametersPassedEdges){
			Node z = parametersPassedEdge.getNode(EdgeDirection.FROM);
			Node p = parametersPassedEdge.getNode(EdgeDirection.TO);
			
			// qz <: qx adapt qp
			// = qx adapt qp :> qz
			if(XAdaptYGreaterThanEqualZConstraintSolver.satisify(x, p, z)){
				typesChanged = true;
			}
		}
		
		return typesChanged;
	}
	
	/**
	 * qx adapt qret <: qx
	 * @param x
	 * @param ret
	 * @return
	 */
	private static boolean processReturnAssignmentConstraints(Node x, Node ret) {
		
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint qx adapt qret <: qx");

//		// TODO: how should this be handled?
//		if(x.taggedWith(AnalysisUtilities.DUMMY_ASSIGNMENT_NODE)){
//			return false;
//		}
//		if(ret.taggedWith(AnalysisUtilities.DUMMY_RETURN_NODE)){
//			return false;
//		}
		
		// qx adapt qret <: qx
		// = qx :> qx adapt qret
		return XGreaterThanEqualYAdaptZConstraintSolver.satisify(x, x, ret);
	}
	
}
