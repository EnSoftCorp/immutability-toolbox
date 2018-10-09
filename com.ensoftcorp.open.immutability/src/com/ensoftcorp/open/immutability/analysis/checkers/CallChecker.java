package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.query.Query;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.CommonQueries;
import com.ensoftcorp.open.commons.xcsg.Undocumented;
import com.ensoftcorp.open.immutability.analysis.AnalysisUtilities;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYMethodAdaptZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XMethodAdaptYGreaterThanEqualZConstraintSolver;
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
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) {
			String values = "x:" + getTypes(x).toString() + ", y:" + getTypes(y).toString() + ", this:" + getTypes(identity).toString() + ", return:" + getTypes(ret).toString() + ", m':" + getTypes(containingMethod).toString();
			Log.info("TCALL (x=y.m(z), x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", m=" + method.getAttr(Undocumented.SIGNATURE) + ")\n" + values);
		}
		
		boolean typesChanged = false;
		
		/////////////////////// start qx madapt qret <: qx /////////////////////// 
		if(processReturnAssignmentConstraints(x, ret)){
			typesChanged = true;
		}
		/////////////////////// end qx madapt qret <: qx ///////////////////////  
		
		/////////////////////// start qy <: qx madapt qthis /////////////////////// 
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) {
			String values = "x:" + getTypes(x).toString() + ", y:" + getTypes(y).toString() + ", this:" + getTypes(identity).toString();
			Log.info("Process TCALL Identity Constraint qy <: qx madapt qthis\n" + values);
		}
		if(XMethodAdaptYGreaterThanEqualZConstraintSolver.satisify(x, identity, y)){
			typesChanged = true;
		}
		/////////////////////// end qy <: qx madapt qthis ///////////////////////

		/////////////////////// start qz <: qx madapt qp ///////////////////////
		if(processParameterConstraints(x, parametersPassedEdges)){
			typesChanged = true;
		}
		/////////////////////// end qz <: qx madapt qp /////////////////////////
		
		// check if method overrides another method (of course this will be empty for static methods)
		Q overridesEdges = Query.universe().edges(XCSG.Overrides);
		Node overriddenMethod = overridesEdges.successors(Common.toQ(method)).eval().nodes().one();
		if(overriddenMethod != null){
			if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TCALL (Overridden Method)");
			
			// Method (method) -Contains-> ReturnValue (ret)
			Node overriddenRet = Common.toQ(overriddenMethod).children().nodes(XCSG.ReturnValue).eval().nodes().one();
			
			// constraint: overriddenReturn <: return
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Override Return Constraint overriddenReturn <: return");
			
			if(XGreaterThanEqualYConstraintSolver.satisify(ret, overriddenRet)){
				typesChanged = true;
			}
			
			// Method (method) -Contains-> Identity
			Node overriddenMethodIdentity = Common.toQ(overriddenMethod).children().nodes(XCSG.Identity).eval().nodes().one();

			// constraint: this <: overriddenThis 
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Override Identity Constraint this <: overriddenThis");
			
			if(XGreaterThanEqualYConstraintSolver.satisify(overriddenMethodIdentity, identity)){
				typesChanged = true;
			}

			// Method (method) -Contains-> Parameter (p1, p2, ...)
			AtlasSet<Node> overriddenMethodParameters = Common.toQ(overriddenMethod).children().nodes(XCSG.Parameter).eval().nodes();
			
			// get the parameters of the method
			AtlasSet<Node> parameters = Common.toQ(parametersPassedEdges).nodes(XCSG.Parameter).eval().nodes();
					
			// for each parameter and overridden parameter pair
			// constraint: p <: pOverriden
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Override Parameter Constraint p <: pOverriden");
			long numParams = parameters.size();
			long numOverriddenParams = overriddenMethodParameters.size();
			if(numParams == numOverriddenParams){
				if(numOverriddenParams > 0){
					for(int i=0; i<numOverriddenParams; i++){
						Node p = Common.toQ(parameters).selectNode(XCSG.parameterIndex, i).eval().nodes().one();
						Node pOverridden = Common.toQ(overriddenMethodParameters).selectNode(XCSG.parameterIndex, i).eval().nodes().one();

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
		
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) {
			Log.info("TSCALL (x=y.m(z), x=" + x.getAttr(XCSG.name) + ", m=" + method.getAttr(Undocumented.SIGNATURE) + ")");
		}
		
		boolean typesChanged = false;
		
		/////////////////////// start qx madapt qret <: qx /////////////////////// 
		if(processReturnAssignmentConstraints(x, ret)){
			typesChanged = true;
		}
		/////////////////////// end qx madapt qret <: qx ///////////////////////// 

		/////////////////////// start qz <: qx madapt qp /////////////////////////
		if(processParameterConstraints(x, parametersPassedEdges)){
			typesChanged = true;
		}
		/////////////////////// end qz <: qx madapt qp ///////////////////////////
		
		/////////////////////// start qm' <: qx madapt qm /////////////////////////
		// m' is the method that contains the callsite m()
		Node containingMethod = CommonQueries.getContainingFunction(callsite);
		if(processStaticDispatchConstraints(x, method, containingMethod)){
			typesChanged = true;
		}
		/////////////////////// end qm' <: qx madapt qm ///////////////////////////
		
		return typesChanged;
	}

	private static boolean processStaticDispatchConstraints(Node x, Node method, Node containingMethod) {
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) {
			String values = "x:" + getTypes(x).toString() + ", m:" + getTypes(method).toString() + ", m':" + getTypes(containingMethod).toString();
			Log.info("Process Static Dispatch Constraint qm' <: qx madapt qm\n" + values);
		}

		// qm' <: qx madapt qm
		// = qx madapt qm :> qm'
		return XMethodAdaptYGreaterThanEqualZConstraintSolver.satisify(x, method, containingMethod);
	}
	
	/**
	 * qz <: qx madapt qp
	 * @param x
	 * @param parametersPassedEdges
	 * @return
	 */
	private static boolean processParameterConstraints(Node x, AtlasSet<Edge> parametersPassedEdges) {
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Parameter Constraint qz <: qx madapt qp");

		boolean typesChanged = false;
		
		// for each z,p pair process s(x), s(z), and s(p)
		for(Edge parametersPassedEdge : parametersPassedEdges){
			Node z = parametersPassedEdge.from();
			Node p = parametersPassedEdge.to();
			
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) {
				Log.info("x = m(z->p), x:" + AnalysisUtilities.getTypes(x).toString() 
						+ ", z:" + AnalysisUtilities.getTypes(z).toString() 
						+ ", p:" + AnalysisUtilities.getTypes(p).toString());
			}
			
			// qz <: qx madapt qp
			// = qx madapt qp :> qz
			if(XMethodAdaptYGreaterThanEqualZConstraintSolver.satisify(x, p, z)){
				typesChanged = true;
			}
			
			
			// TODO: case mutation to parameter mutates a field which is a part of a container
			if(ImmutabilityPreferences.isContainerConsiderationEnabled()){
				if(!getTypes(p).contains(ImmutabilityTypes.READONLY)){
					Q localDataFlowEdges = Query.universe().edges(XCSG.LocalDataFlow);
					for(Node paramValue : localDataFlowEdges.predecessors(Common.toQ(z)).eval().nodes()){
						if(paramValue.taggedWith(XCSG.InstanceVariableAccess)){
							Node instanceVariableAccess = paramValue;
							// each instance containing x has been mutated as well
							// TODO: should this be like a basic assignment constraint 
							// between each parent container field or just all are not readonly???
							// for now going with the latter since its easier to implement...
							for(Node container : AnalysisUtilities.getAccessedContainers(instanceVariableAccess)){
								for(Node containerReference : AnalysisUtilities.parseReferences(container)){
									if(ImmutabilityPreferences.isDebugLoggingEnabled()) {
										Log.info("A mutation to " + paramValue.getAttr(XCSG.name).toString() + " mutated container " + containerReference.getAttr(XCSG.name).toString());
									}
									if(removeTypes(containerReference, ImmutabilityTypes.READONLY)){
										typesChanged = true;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return typesChanged;
	}
	
	/**
	 * qx madapt qret <: qx
	 * @param x
	 * @param ret
	 * @return
	 */
	private static boolean processReturnAssignmentConstraints(Node x, Node ret) {
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) {
			String values = "x:" + getTypes(x).toString() + ", return:" + getTypes(ret).toString();
			Log.info("Process TCALL Return Constraint qx madapt qret <: qx\n" + values);
		}

//		// TODO: how should this be handled?
		// follow up question, does it need to be handled?
//		if(x.taggedWith(AnalysisUtilities.DUMMY_ASSIGNMENT_NODE)){
//			return false;
//		}
//		if(ret.taggedWith(AnalysisUtilities.DUMMY_RETURN_NODE)){
//			return false;
//		}
		
		// qx madapt qret <: qx
		// = qx :> qx madapt qret
		return XGreaterThanEqualYMethodAdaptZConstraintSolver.satisify(x, x, ret);
	}
	
}
