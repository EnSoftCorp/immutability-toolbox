package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.StandardQueries;
import com.ensoftcorp.open.immutability.analysis.AnalysisUtilities;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.analysis.solvers.XAdaptYGreaterThanZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanYAdaptZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanYConstraintSolver;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.jimple.commons.wishful.JimpleStopGap;

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
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		Set<ImmutabilityTypes> identityTypes = getTypes(identity);
		Set<ImmutabilityTypes> retTypes = getTypes(ret);
		
		boolean isPolyreadField = x.taggedWith(XCSG.Field) && (xTypes.contains(ImmutabilityTypes.POLYREAD) && xTypes.size() == 1);
		boolean isMutableReference = !x.taggedWith(XCSG.Field) && (xTypes.contains(ImmutabilityTypes.MUTABLE) && xTypes.size() == 1);
		
		// if x is a field and polyread then the return value must be polyread
		// if x is a reference and mutable then the return value must be polyread
		// whether the field or reference is polyread or mutable we know know that it
		// is at least not readonly
		if(isPolyreadField || isMutableReference){
			if(removeTypes(ret, ImmutabilityTypes.READONLY)){
				typesChanged = true;
			}
			// if the return value is a field then the field and its container fields must be mutable as well
			Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
			Q returnValues = localDataFlowEdges.predecessors(Common.toQ(ret));
			Q fieldValues = localDataFlowEdges.predecessors(returnValues).nodesTaggedWithAny(XCSG.InstanceVariableValue, JimpleStopGap.CLASS_VARIABLE_VALUE);
			for(Node fieldValue : fieldValues.eval().nodes()){
				for(Node container : AnalysisUtilities.getAccessedContainers(fieldValue)){
					if(removeTypes(container, ImmutabilityTypes.READONLY)){
						typesChanged = true;
					}
					if(container.taggedWith(XCSG.ClassVariable)){
						if(removeTypes(containingMethod, ImmutabilityTypes.READONLY)){
							typesChanged = true;
						}
					}
				}
			}
		}
		
		/////////////////////// start qx adapt qret <: qx /////////////////////// 
		if(processReturnAssignmentConstraints(x, ret)){
			typesChanged = true;
		}
		/////////////////////// end qx adapt qret <: qx ///////////////////////  
		
		/////////////////////// start qy <: qx adapt qthis /////////////////////// 
		
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint qy <: qx adapt qthis");
		
		if(XAdaptYGreaterThanZConstraintSolver.satisify(x, xTypes, identity, identityTypes, y, yTypes)){
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
			Set<ImmutabilityTypes> overriddenRetTypes = getTypes(overriddenRet);
			
			// constraint: overriddenReturn <: return
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint overriddenReturn <: return");
			
			if(XGreaterThanYConstraintSolver.satisify(ret, retTypes, overriddenRet, overriddenRetTypes)){
				typesChanged = true;
			}
			
			// Method (method) -Contains-> Identity
			Node overriddenMethodIdentity = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();
			Set<ImmutabilityTypes> overriddenIdentityTypes = getTypes(overriddenMethodIdentity);

			// constraint: this <: overriddenThis 
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint this <: overriddenThis");
			
			if(XGreaterThanYConstraintSolver.satisify(overriddenMethodIdentity, overriddenIdentityTypes, identity, identityTypes)){
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
						
						Set<ImmutabilityTypes> pTypes = getTypes(p);
						Set<ImmutabilityTypes> pOverriddenTypes = getTypes(pOverridden);
						
						if(XGreaterThanYConstraintSolver.satisify(pOverridden, pOverriddenTypes, p, pTypes)){
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
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		
		boolean isPolyreadField = x.taggedWith(XCSG.Field) && (xTypes.contains(ImmutabilityTypes.POLYREAD) && xTypes.size() == 1);
		boolean isMutableReference = !x.taggedWith(XCSG.Field) && (xTypes.contains(ImmutabilityTypes.MUTABLE) && xTypes.size() == 1);
		
		// if x is a field and polyread then the return value must be polyread
		// if x is a reference and mutable then the return value must be polyread
		// whether the field or reference is polyread or mutable we know know that it
		// is at least not readonly
		if(isPolyreadField || isMutableReference){
			if(removeTypes(ret, ImmutabilityTypes.READONLY)){
				typesChanged = true;
			}
			// if the return value is a field then the field and its container fields must be mutable as well
			Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
			Q returnValues = localDataFlowEdges.predecessors(Common.toQ(ret));
			Q fieldValues = localDataFlowEdges.predecessors(returnValues).nodesTaggedWithAny(XCSG.InstanceVariableValue, JimpleStopGap.CLASS_VARIABLE_VALUE);
			Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
			Q fields = interproceduralDataFlowEdges.predecessors(fieldValues);
			for(Node field : fields.eval().nodes()){
				for(Node container : AnalysisUtilities.getAccessedContainers(field)){
					if(removeTypes(container, ImmutabilityTypes.READONLY)){
						typesChanged = true;
					}
					if(container.taggedWith(XCSG.ClassVariable)){
						if(removeTypes(StandardQueries.getContainingMethod(x), ImmutabilityTypes.READONLY)){
							typesChanged = true;
						}
					}
				}
			}
		}
		
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
		Node containingMethod = StandardQueries.getContainingMethod(callsite);
		if(processStaticDispatchConstraints(x, method, containingMethod)){
			typesChanged = true;
		}
		/////////////////////// end qm' <: qx adapt qm ///////////////////////////
		
		return typesChanged;
	}

	private static boolean processStaticDispatchConstraints(Node x, Node method, Node containingMethod) {
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process Static Dispatch Constraint qm' <: qx adapt qm");
		boolean typesChanged = false;
		
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> mTypes = getTypes(method);
		Set<ImmutabilityTypes> mContainerTypes = getTypes(containingMethod);
		
		// qm' <: qx adapt qm
		// = qx adapt qm :> qm'
		if(XAdaptYGreaterThanZConstraintSolver.satisify(x, xTypes, method, mTypes, containingMethod, mContainerTypes)){
			typesChanged = true;
		}

		return typesChanged;
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
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		
		// for each z,p pair process s(x), s(z), and s(p)
		for(GraphElement parametersPassedEdge : parametersPassedEdges){
			Node z = parametersPassedEdge.getNode(EdgeDirection.FROM);
			Node p = parametersPassedEdge.getNode(EdgeDirection.TO);
			Set<ImmutabilityTypes> zTypes = getTypes(z);
			Set<ImmutabilityTypes> pTypes = getTypes(p);
			
			// qz <: qx adapt qp
			// = qx adapt qp :> qz
			if(XAdaptYGreaterThanZConstraintSolver.satisify(x, xTypes, p, pTypes, z, zTypes)){
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
		
		boolean typesChanged = false;
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> retTypes = getTypes(ret);
		
		// qx adapt qret <: qx
		// = qx :> qx adapt qret
		if(XGreaterThanYAdaptZConstraintSolver.satisify(x,  xTypes, x, xTypes, ret, retTypes)){
			typesChanged = true;
		}

		return typesChanged;
	}
	
}
