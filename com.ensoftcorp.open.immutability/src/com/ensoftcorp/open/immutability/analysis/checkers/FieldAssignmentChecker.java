package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.immutability.analysis.AnalysisUtilities;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.analysis.adaptive.solvers.XEqualsYConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XFieldAdaptYGreaterThanEqualZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanEqualYFieldAdaptZConstraintSolver;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.java.commons.wishful.JavaStopGap;

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
		// x, y, and f should be non-null
		if(x==null){
			throw new IllegalArgumentException("x is null");
		}
		if(y==null){
			throw new IllegalArgumentException("y is null");
		}
		if(f==null){
			throw new IllegalArgumentException("f is null");
		}
		
		// f should always be an instance variable
		if(!f.taggedWith(XCSG.InstanceVariable)){
			throw new IllegalArgumentException("f is not an instance variable");
		}
		
		// x or y could be instance variables
		boolean adaptXToY = false;
		@SuppressWarnings("unused")
		boolean adaptYToX = false;
		if (x.taggedWith(XCSG.InstanceVariableValue)) {
			x = AnalysisUtilities.getInstanceVariableFromInstanceVariableValue(x);
			adaptXToY = true;
		}
		if (y.taggedWith(XCSG.InstanceVariableValue)) {
			y = AnalysisUtilities.getInstanceVariableFromInstanceVariableValue(y);
			adaptYToX = true;
		}
		
		// x or y could be class variables
		if (x.taggedWith(JavaStopGap.CLASS_VARIABLE_VALUE)) {
			x = AnalysisUtilities.getClassVariableFromClassVariableValue(x);
		}
		if (y.taggedWith(JavaStopGap.CLASS_VARIABLE_VALUE)) {
			y = AnalysisUtilities.getClassVariableFromClassVariableValue(y);
		}
		
		// x or y could by dynamic dispatches
		if (x.taggedWith(XCSG.DynamicDispatchCallSite)) {
			// TODO: implement
			return false;
		}
		if (y.taggedWith(XCSG.DynamicDispatchCallSite)) {
			// TODO: implement
			return false;
		}

		// x or y could be static dispatches
		if (x.taggedWith(XCSG.StaticDispatchCallSite)) {
			// TODO: implement
			return false;
		}
		if (y.taggedWith(XCSG.StaticDispatchCallSite)) {
			// TODO: implement
			return false;
		}
		
		// y could be an enclosing instance parameter
		if (y.taggedWith(XCSG.Java.EnclosingInstanceParameter)) {
			// TODO: implement
			return false;
		}
		
		// log inference rule
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) {
			String values = "x:" + getTypes(x).toString() + ", f:" + getTypes(f).toString() + ", y:" + getTypes(y).toString();
			String constraints = "x=mutable, qy <: mutable fadapt qf";
			Log.info("TWRITE (x.f=y, x=" + x.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ")\n" + constraints + "\n" + values);
		}
		
		boolean typesChanged = false;
		
		// x must be mutable
		if(XEqualsYConstraintSolver.satisfy(x, ImmutabilityTypes.MUTABLE, adaptXToY)){
			typesChanged = true;
		}
		
		// qy <: mutable fadapt qf
		if(XFieldAdaptYGreaterThanEqualZConstraintSolver.satisify(ImmutabilityTypes.MUTABLE, f, y)){
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
		// x, y, and f should be non-null
		if(x==null){
			throw new IllegalArgumentException("x is null");
		}
		if(y==null){
			throw new IllegalArgumentException("y is null");
		}
		if(f==null){
			throw new IllegalArgumentException("f is null");
		}
		
		// f should always be an instance variable
		if(!f.taggedWith(XCSG.InstanceVariable)){
			throw new IllegalArgumentException("f is not an instance variable");
		}
		
		// x or y could be instance variables
		if (x.taggedWith(XCSG.InstanceVariableValue)) {
			x = AnalysisUtilities.getInstanceVariableFromInstanceVariableValue(x);
		}
		if (y.taggedWith(XCSG.InstanceVariableValue)) {
			y = AnalysisUtilities.getInstanceVariableFromInstanceVariableValue(y);
		}
		
		// x or y could be class variables
		if (x.taggedWith(JavaStopGap.CLASS_VARIABLE_VALUE)) {
			x = AnalysisUtilities.getClassVariableFromClassVariableValue(x);
		}
		if (y.taggedWith(JavaStopGap.CLASS_VARIABLE_VALUE)) {
			y = AnalysisUtilities.getClassVariableFromClassVariableValue(y);
		}
		
		// x or y could by dynamic dispatches
		if (x.taggedWith(XCSG.DynamicDispatchCallSite)) {
			// TODO: implement
			return false;
		}
		if (y.taggedWith(XCSG.DynamicDispatchCallSite)) {
			// TODO: implement
			return false;
		}

		// x or y could be static dispatches
		if (x.taggedWith(XCSG.StaticDispatchCallSite)) {
			// TODO: implement
			return false;
		}
		if (y.taggedWith(XCSG.StaticDispatchCallSite)) {
			// TODO: implement
			return false;
		}
		
		// log inference rule
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()){
			String values = "x:" + getTypes(x).toString() + ", f:" + getTypes(f).toString() + ", y:" + getTypes(y).toString();
			Log.info("TREAD (x=y.f, x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ")\n" + values);
		}
		
		boolean typesChanged = false;
		
		// qy adapt qf <: qx
		if(XGreaterThanEqualYFieldAdaptZConstraintSolver.satisify(x, y, f)){
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
		// x, sf, and m should be non-null
		if(x==null){
			throw new IllegalArgumentException("x is null");
		}
		if(sf==null){
			throw new IllegalArgumentException("sf is null");
		}
		if(m==null){
			throw new IllegalArgumentException("m is null");
		}
		
		// log inference rule
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()){
			String values = "x:" + getTypes(x).toString() + ", sf:" + getTypes(sf).toString() + ", m:" + getTypes(m).toString();
			Log.info("TSWRITE (sf=x in m, sf=" + sf.getAttr(XCSG.name) + ", x=" + x.getAttr(XCSG.name) + ", m=" + m.getAttr(XCSG.name) + ")\n" + values);
		}
		
		// check constraint
		// a write to a static field means the containing method cannot be pure
		return XEqualsYConstraintSolver.satisfy(m, ImmutabilityTypes.MUTABLE);
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
		// x, sf, and m should be non-null
		if(x==null){
			throw new IllegalArgumentException("x is null");
		}
		if(sf==null){
			throw new IllegalArgumentException("sf is null");
		}
		if(m==null){
			throw new IllegalArgumentException("m is null");
		}
		
		// log inference rule
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()){
			String values = "x:" + getTypes(x).toString() + ", sf:" + getTypes(sf).toString() + ", m:" + getTypes(m).toString();
			Log.info("TSREAD (x=sf in m, x=" + x.getAttr(XCSG.name) + ", sf=" + sf.getAttr(XCSG.name) + ", m=" + m.getAttr(XCSG.name) + ")\n" + values);
		}
		
		// check constraint
		// m <: x
		// = x :> m
		return XGreaterThanEqualYConstraintSolver.satisify(x, m);
	}
	
}
