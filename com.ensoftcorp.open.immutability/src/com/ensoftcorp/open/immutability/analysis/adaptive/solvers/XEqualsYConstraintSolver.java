package com.ensoftcorp.open.immutability.analysis.adaptive.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class XEqualsYConstraintSolver {

	public static boolean satisfy(Node x, ImmutabilityTypes yType){
		return satisfy(x, yType);
	}
	
	public static boolean satisfy(Node x, ImmutabilityTypes yType, boolean adaptXToY){
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		if (xTypes.isEmpty()) {
			Log.warning("x [" + x.address().toAddressString() + "] is untyped, constraint 'qx equals Y' cannot be satisfied", new IllegalArgumentException());
			return false;
		}
		if (adaptXToY) {
			Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
			for (ImmutabilityTypes xType : xTypes) {
				ImmutabilityTypes xAdaptedY = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, xType);
				if(!xAdaptedY.equals(yType)){
					xTypesToRemove.add(xAdaptedY);
				}
			}
			return removeTypes(x, xTypesToRemove);
		} else {
			switch (yType) {
			case MUTABLE:
				return removeTypes(x, ImmutabilityTypes.READONLY, ImmutabilityTypes.POLYREAD);
			case POLYREAD:
				return removeTypes(x, ImmutabilityTypes.READONLY, ImmutabilityTypes.MUTABLE);
			case READONLY:
				return removeTypes(x, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.MUTABLE);
			default:
				throw new IllegalArgumentException("Unexpected immutability type!");
			}
		}
	}
	
//	public static boolean satisfy(Node x, Node y) {
//		Set<ImmutabilityTypes> intersection = EnumSet.noneOf(ImmutabilityTypes.class);
//		Set<ImmutabilityTypes> xTypes = getTypes(x);
//		Set<ImmutabilityTypes> yTypes = getTypes(y);
//		
//		// TODO: implement
//	}
	
}
