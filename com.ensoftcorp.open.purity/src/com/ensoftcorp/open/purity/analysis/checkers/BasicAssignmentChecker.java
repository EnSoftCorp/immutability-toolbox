package com.ensoftcorp.open.purity.analysis.checkers;

import static com.ensoftcorp.open.purity.core.Utilities.getTypes;
import static com.ensoftcorp.open.purity.core.Utilities.removeTypes;

import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.purity.core.ImmutabilityTypes;
import com.ensoftcorp.open.purity.ui.PurityPreferences;

public class BasicAssignmentChecker {

	/**
	 * Solves and satisfies constraints for Type Rule 2 - TASSIGN
	 * Let, x = y
	 * 
	 * @param x The reference being written to
	 * @param y The reference be read from
	 * @return
	 */
	public static boolean handleAssignment(GraphElement x, GraphElement y) {
		
		if(x==null){
			Log.warning("x is null!");
			return false;
		}
		
		if(y==null){
			Log.warning("y is null!");
			return false;
		}
		
		if(PurityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TASSIGN (x=y, x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		
		// process s(x)
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(x) for constraint qy " + getTypes(y).toString() + " <: qx " + getTypes(x).toString());
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				if(xType.compareTo(yType) >= 0){
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
		
		// process s(y)
		if(PurityPreferences.isDebugLoggingEnabled()) Log.info("Process s(y) for constraint qy " + getTypes(y).toString() + " <: qx " + getTypes(x).toString());
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				if(xType.compareTo(yType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			typesChanged = true;
		}
		return typesChanged;
	}
	
}
