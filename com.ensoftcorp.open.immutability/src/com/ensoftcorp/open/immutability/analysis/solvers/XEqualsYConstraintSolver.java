package com.ensoftcorp.open.immutability.analysis.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;

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
		
		switch(y){
			case MUTABLE:
				return xTypes.removeAll(EnumSet.of(ImmutabilityTypes.READONLY, ImmutabilityTypes.POLYREAD));
			case POLYREAD:
				return xTypes.removeAll(EnumSet.of(ImmutabilityTypes.READONLY, ImmutabilityTypes.MUTABLE));
			case READONLY:
				return xTypes.removeAll(EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.MUTABLE));
			default:
				throw new IllegalArgumentException("Unexpected immutability type!");
		}
	}
	
//	public static boolean satisify(Node x, Node y) {
//		Set<ImmutabilityTypes> intersection = EnumSet.noneOf(ImmutabilityTypes.class);
//		Set<ImmutabilityTypes> xTypes = getTypes(x);
//		Set<ImmutabilityTypes> yTypes = getTypes(y);
//		
//		// TODO: implement
//	}
	
}
