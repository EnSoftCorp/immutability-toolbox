package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYConstraintSolver;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class BasicAssignmentChecker {

	/**
	 * Solves and satisfies constraints for Type Rule 2 - TASSIGN
	 * Let, x = y
	 * 
	 * @param x The reference being written to
	 * @param y The reference be read from
	 * @return
	 */
	public static boolean handleAssignment(Node x, Node y) {
		// x and y should be non-null
		if(x==null){
			throw new IllegalArgumentException("x is null");
		}
		if(y==null){
			throw new IllegalArgumentException("y is null");
		}
		if(x.equals(y)){
			throw new IllegalArgumentException("x and y are the same references");
		}
		
		// x and y should not be fields
		if(x.taggedWith(XCSG.Field) || y.taggedWith(XCSG.Field)){
			String message = "Basic assignment cannot involve fields!";
			IllegalArgumentException e = new IllegalArgumentException(message);;
			Log.error(message + "\n\nx:\n" + x.toString() + "\n\ny:\n" + y.toString(), e);
			throw e;
		}
		
		// x and y should not be callsites
		if(x.taggedWith(XCSG.CallSite) || y.taggedWith(XCSG.CallSite)){
			String message = "Basic assignment cannot involve callsites!";
			IllegalArgumentException e = new IllegalArgumentException(message);;
			Log.error(message + "\n\nx:\n" + x.toString() + "\n\ny:\n" + y.toString(), e);
			throw e;
		}
		
		// x and y should not be parameter passes 
		// (assignments to parameters or assignments to stack parameters)
		if(x.taggedWith(XCSG.ParameterPass) || y.taggedWith(XCSG.ParameterPass)){
			String message = "Basic assignment cannot involve parameter passing!";
			IllegalArgumentException e = new IllegalArgumentException(message);;
			Log.error(message + "\n\nx:\n" + x.toString() + "\n\ny:\n" + y.toString(), e);
			throw e;
		}
		
		// inference rule logging
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) {
			String values = "x:" + getTypes(x).toString() + ", y:" + getTypes(y).toString();
			Log.info("TASSIGN (x=y, x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ")\n" + values);
		}

		// debug logging
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) {
			Log.info("Processing Constraint x :> y");
		}
		
		return XGreaterThanEqualYConstraintSolver.satisify(x, y);
	}
	
}
