package com.ensoftcorp.open.immutability.analysis.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class XEqualsYConstraintSolver {

	public static boolean satisfty(Node x, ImmutabilityTypes y){
		
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		if (xTypes.isEmpty()) {
			Log.warning("x [" + x.address().toAddressString() + "] is untyped, constraint 'qx equals y' cannot be satisfied", new IllegalArgumentException());
			return false;
		}

		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			if(!xType.equals(y)){
				xTypesToRemove.add(xType);
			}
		}
		return removeTypes(x, xTypesToRemove);
	}
	
//	public static boolean satisify(Node x, Node y) {
//		Set<ImmutabilityTypes> intersection = EnumSet.noneOf(ImmutabilityTypes.class);
//		Set<ImmutabilityTypes> xTypes = getTypes(x);
//		Set<ImmutabilityTypes> yTypes = getTypes(y);
//		
//		// TODO: implement
//	}
	
}
