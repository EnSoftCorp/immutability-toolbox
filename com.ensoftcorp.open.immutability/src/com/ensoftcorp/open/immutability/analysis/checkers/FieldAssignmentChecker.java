package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.Set;

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
		
		// if x is a reference it must be mutable
		// if x is a field it must be polyread
		// TWRITE precondition
		if(removeTypes(x, ImmutabilityTypes.READONLY)){
			typesChanged = true;
		}
		
		// if y is only mutable then f cannot be readonly
		// ISSUE 2 - not documented in the publications and accounts for 52 
		// of reiminfer 0.1.2 and 0.1.3 failures on immutability benchmark
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		if((yTypes.contains(ImmutabilityTypes.MUTABLE)) && yTypes.size()==1){
			if(removeTypes(f, ImmutabilityTypes.READONLY)){
				typesChanged = true;
			}
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
		// qy adapt qf <: qx
		// = qx :> qy adapt qf
		return XGreaterThanEqualYAdaptZConstraintSolver.satisify(x, y, f);
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
		// a write to a static field means the containing method cannot be pure (readonly)
		return removeTypes(m, ImmutabilityTypes.READONLY);
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
