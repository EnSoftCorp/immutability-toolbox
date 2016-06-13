package com.ensoftcorp.open.purity.analysis.checkers;

import static com.ensoftcorp.open.purity.analysis.Utilities.getTypes;
import static com.ensoftcorp.open.purity.analysis.Utilities.removeStaticTypes;
import static com.ensoftcorp.open.purity.analysis.Utilities.removeTypes;

import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.NodeDirection;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.purity.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.purity.analysis.Utilities;
import com.ensoftcorp.open.purity.log.Log;
import com.ensoftcorp.open.purity.ui.PurityPreferences;

public class CallChecker {

	/**
	 * Let, x=y.m(z)
	 * @param x
	 * @param y
	 * @param identity
	 * @param method
	 * @param ret
	 * @param parametersPassedEdges
	 * @return
	 */
	public static boolean handleCall(GraphElement x, GraphElement y, GraphElement identity, GraphElement method, GraphElement ret, AtlasSet<GraphElement> parametersPassedEdges) {
		
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
		
		if(PurityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TCALL (x=y.m(z), x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", m=" + method.getAttr("##signature") + ")");
		
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
			Q fieldValues = localDataFlowEdges.predecessors(returnValues).nodesTaggedWithAny(XCSG.InstanceVariableValue, Utilities.CLASS_VARIABLE_VALUE);
			Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
			Q fields = interproceduralDataFlowEdges.predecessors(fieldValues);
			for(GraphElement field : fields.eval().nodes()){
				for(GraphElement container : Utilities.getAccessedContainers(field)){
					if(removeTypes(container, ImmutabilityTypes.READONLY)){
						typesChanged = true;
					}
					if(container.taggedWith(XCSG.ClassVariable)){
						if(removeStaticTypes(Utilities.getContainingMethod(x), ImmutabilityTypes.READONLY, ImmutabilityTypes.POLYREAD)){
							typesChanged = true;
						}
					}
				}
			}
		}	
		
		/////////////////////// start qx adapt qret <: qx /////////////////////// 
		
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint qx adapt qret <: qx");
		
		// process s(x)
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(x)");
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
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(ret)");
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
		
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint qy <: qx adapt qthis");
		
		// process s(y)
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(y)");
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
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(x)");
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
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(identity)");
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
				
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint qz <: qx adapt qp");
		
		// for each z,p pair process s(x), s(z), and s(p)
		Graph localDFGraph = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow).eval();
		for(GraphElement parametersPassedEdge : parametersPassedEdges){
			GraphElement localDataFlowEdge = localDFGraph.edges(parametersPassedEdge.getNode(EdgeDirection.FROM), NodeDirection.IN).getFirst();
			GraphElement z = localDataFlowEdge.getNode(EdgeDirection.FROM);
			GraphElement p = parametersPassedEdge.getNode(EdgeDirection.TO);
			Set<ImmutabilityTypes> zTypes = getTypes(z);
			Set<ImmutabilityTypes> pTypes = getTypes(p);
			
			// process s(x)
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(x)");
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
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(z)");
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
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(p)");
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
		
		// check if method overrides another method (of course this will be empty for static methods)
		Q overridesEdges = Common.universe().edgesTaggedWithAny(XCSG.Overrides);
		GraphElement overriddenMethod = overridesEdges.successors(Common.toQ(method)).eval().nodes().getFirst();
		if(overriddenMethod != null){
			if(PurityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TCALL (Overridden Method)");
			
			// Method (method) -Contains-> ReturnValue (ret)
			GraphElement overriddenMethodReturn = Common.toQ(overriddenMethod).children().nodesTaggedWithAny(XCSG.ReturnValue).eval().nodes().getFirst();
			Set<ImmutabilityTypes> overriddenRetTypes = getTypes(overriddenMethodReturn);
			
			// constraint: overriddenReturn <: return
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint overriddenReturn <: return");
			
			// process s(ret)
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(ret)");
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
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(overriddenRet)");
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
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint this <: overriddenThis");
			
			// process s(this)
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(this)");
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
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(overriddenRet)");
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
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Constraint p <: pOverriden");
			long numParams = overriddenMethodParameters.size();
			if(numParams > 0){
				for(int i=0; i<numParams; i++){
					GraphElement p = Common.toQ(parameters).selectNode(XCSG.parameterIndex, i).eval().nodes().getFirst();
					GraphElement pOverridden = Common.toQ(overriddenMethodParameters).selectNode(XCSG.parameterIndex, i).eval().nodes().getFirst();
					
					Set<ImmutabilityTypes> pTypes = getTypes(p);
					Set<ImmutabilityTypes> pOverriddenTypes = getTypes(pOverridden);
					
					// process s(p)
					if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(p)");
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
					if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(pOverridden)");
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
	 * Checks and satisfies constraints on callsites to the given target 
	 * instance method that are not assigned
	 * Let r.c(z) be a callsite to method this.m(p)
	 * 
	 * Constraint 1) this <: r
	 * Constraint 2) z1 <: p1, z2 <: p2, z3 <: p3 ...
	 * 
	 * @param unassignedCallsite The dynamic dispatch callsite to check constraints
	 */
	public static boolean handleUnassignedDynamicDispatchCallsites(GraphElement unassignedCallsite) {
		boolean typesChanged = false;
		
		GraphElement method = Utilities.getInvokedMethodSignature(unassignedCallsite);
		
		if(processStrictReceiverConstraints(unassignedCallsite, method)){
			typesChanged = true;
		}
		
		if(processStrictParameterConstraints(unassignedCallsite, method)){
			typesChanged = true;
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
	public static boolean handleStaticCall(GraphElement x, GraphElement method, GraphElement ret, AtlasSet<GraphElement> parametersPassedEdges) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Checks and satisfies constraints on callsites to the given target class (static) method
	 * Let c(z) be a callsite to method m(p)
	 * 
	 * Constraint 1) z1 <: p1, z2 <: p2, z3 <: p3 ...
	 * 
	 * @param unassignedCallsite The static callsite to check constraints
	 */
	public static boolean handleUnassignedStaticDispatchCallsites(GraphElement unassignedCallsite) {
		boolean typesChanged = false;
		
		GraphElement method = Utilities.getInvokedMethodSignature(unassignedCallsite);
		
		if(processStrictParameterConstraints(unassignedCallsite, method)){
			typesChanged = true;
		}
		
		// TODO: handle static type constraints
		
		return typesChanged;
	}
	
	/**
	 * 
	 * @param unassignedCallsite
	 * @param method
	 * @return
	 */
	private static boolean processStrictReceiverConstraints(GraphElement unassignedCallsite, GraphElement method) {
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Callsite Constraint qthis <: qr");
		boolean typesChanged = false;
		
		// TODO: use method signature?
		
		GraphElement identity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();
		Set<ImmutabilityTypes> identityTypes = getTypes(identity);
		Q localDFEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		Q identityPassedToEdges = Common.universe().edgesTaggedWithAny(XCSG.IdentityPassedTo);

		// IdentityPass (.this) -IdentityPassedTo-> CallSite (m)
		Q identityPass = identityPassedToEdges.predecessors(Common.toQ(unassignedCallsite));
		
		// Receiver (r) -LocalDataFlow-> IdentityPass (.this)
		GraphElement r = localDFEdges.predecessors(identityPass).eval().nodes().getFirst();
		Set<ImmutabilityTypes> rTypes = getTypes(r);
		
		// process s(this)
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(this)");
		Set<ImmutabilityTypes> identityTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes identityType : identityTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes rType : rTypes){
				if(rType.compareTo(identityType) >= 0){
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
		
		// process s(r)
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(r)");
		Set<ImmutabilityTypes> rTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes rType : rTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes identityType : identityTypes){
				if(rType.compareTo(identityType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				rTypesToRemove.add(rType);
			}
		}
		if(removeTypes(r, rTypesToRemove)){
			typesChanged = true;
		}
		return typesChanged;
	}

	/**
	 * Given an unassigned callsite and the callsite target, this method checks and satisfies the constraints
	 * that qz <: qp, where p is the formal parameter and z is the actual parameter passed for each callsite.
	 * @param unassignedCallsite
	 * @param method
	 * @return
	 */
	private static boolean processStrictParameterConstraints(GraphElement unassignedCallsite, GraphElement method) {
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process Callsite Constraint qz <: qp");
		boolean typesChanged = false;
		
		// TODO: use method signature?
		
		// Method (method) -Contains-> Parameter (p1, p2, ...)
		AtlasSet<GraphElement> parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
		
		// ControlFlow -Contains-> CallSite
		// CallSite -Contains-> ParameterPassed (z1, z2, ...)
		AtlasSet<GraphElement> parametersPassed = Common.toQ(unassignedCallsite).parent().children().nodesTaggedWithAny(XCSG.ParameterPass).eval().nodes();
		
		// ParameterPassed (z1, z2, ...) -InterproceduralDataFlow-> Parameter (p1, p2, ...)
		// such that z1-InterproceduralDataFlow->p1, z2-InterproceduralDataFlow->p2, ...
		AtlasSet<GraphElement> parametersPassedEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow)
				.betweenStep(Common.toQ(parametersPassed), Common.toQ(parameters)).eval().edges();
		
		// for each z,p pair process s(z), and s(p)
		Graph localDFGraph = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow).eval();
		for(GraphElement parametersPassedEdge : parametersPassedEdges){
			GraphElement localDataFlowEdge = localDFGraph.edges(parametersPassedEdge.getNode(EdgeDirection.FROM), NodeDirection.IN).getFirst();
			GraphElement z = localDataFlowEdge.getNode(EdgeDirection.FROM);
			GraphElement p = parametersPassedEdge.getNode(EdgeDirection.TO);
			Set<ImmutabilityTypes> zTypes = getTypes(z);
			Set<ImmutabilityTypes> pTypes = getTypes(p);
			
			// process s(z)
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(z)");
			Set<ImmutabilityTypes> zTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes zType : zTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes pType : pTypes){
					if(pType.compareTo(zType) >= 0){
						isSatisfied = true;
						break satisfied;
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
			if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(p)");
			Set<ImmutabilityTypes> pTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for(ImmutabilityTypes pType : pTypes){
				boolean isSatisfied = false;
				satisfied:
				for(ImmutabilityTypes zType : zTypes){
					if(pType.compareTo(zType) >= 0){
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
		}
		return typesChanged;
	}
	
}
