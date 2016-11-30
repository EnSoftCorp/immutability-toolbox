package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.analysis.solvers.XAdaptYGreaterThanEqualZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYAdaptZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYConstraintSolver;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class FieldAssignmentChecker {

	/**
	 * Solves and satisfies constraints for Type Rule 3 - TWRITE
	 * Let, x.f = y
	 * 
	 * @param x The receiver object
	 * @param f The field of the receiver object being written to
	 * @param y The reference being read from
	 * @return Returns true if the graph element's ImmutabilityTypes have changed
	 */
	public static boolean handleFieldWrite(Node x, Node f, Node y) {

		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TWRITE (x.f=y, x=" + x.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		
		// x must be mutable
		if(removeTypes(x, ImmutabilityTypes.READONLY, ImmutabilityTypes.POLYREAD)){
			typesChanged = true;
		}
		
		// qy <: qx adapt qf
		// = qx adapt qf :> qy
		if(XAdaptYGreaterThanEqualZConstraintSolver.satisify(x, f, y)){
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
	public static boolean handleFieldRead(Node x, Node y, Node f) {
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()){
			Log.info("TREAD (x=y.f, x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ")");
		}
		
		boolean typesChanged = false;
		
		// qy adapt qf <: qx
		// = qx :> qy adapt qf
		if(XGreaterThanEqualYAdaptZConstraintSolver.satisify(x, y, f)){
			typesChanged = true;
		}
		
		return typesChanged;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 6, - TSWRITE
	 * Let, sf = x
	 * 
	 * @param sf The static field being written to
	 * @param x The reference being read from
	 * @param m The method where the assignment happens
	 * 
	 * @return
	 */
	public static boolean handleStaticFieldWrite(Node sf, Node x, Node m) {
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()){
			Log.info("TSWRITE (sf=x in m, sf=" + sf.getAttr(XCSG.name) + ", x=" + x.getAttr(XCSG.name) + ", m=" + m.getAttr(XCSG.name) + ")");
		}
		// a write to a static field means the containing method cannot be pure (readonly or polyread)
		return removeTypes(m, ImmutabilityTypes.READONLY, ImmutabilityTypes.POLYREAD);
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 7, - TSREAD
	 * Let, x = sf (in m)
	 * 
	 * @param x The reference being written to
	 * @param sf The static field being read from
	 * @param m The method where the assignment happens
	 * @return
	 */
	public static boolean handleStaticFieldRead(Node x, Node sf, Node m) {
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()){
			Log.info("TSREAD (x=sf in m, x=" + x.getAttr(XCSG.name) + ", sf=" + sf.getAttr(XCSG.name) + ", m=" + m.getAttr(XCSG.name) + ")");
		}
		// m <: x
		// = x :> m
		return XGreaterThanEqualYConstraintSolver.satisify(x, m);
	}
	
}
