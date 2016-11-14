package com.ensoftcorp.open.immutability.analysis.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class XGreaterThanYConstraintSolver {

	// all possible sets, 3 choose 3, 3 choose 2, and 3 choose 1
	private static final ArrayList<EnumSet<ImmutabilityTypes>> sets = new ArrayList<EnumSet<ImmutabilityTypes>>();
	static {
		EnumSet<ImmutabilityTypes> SET1 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
		EnumSet<ImmutabilityTypes> SET2 = EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
		EnumSet<ImmutabilityTypes> SET3 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD);
		EnumSet<ImmutabilityTypes> SET4 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY);
		EnumSet<ImmutabilityTypes> SET5 = EnumSet.of(ImmutabilityTypes.READONLY);
		EnumSet<ImmutabilityTypes> SET6 = EnumSet.of(ImmutabilityTypes.POLYREAD);
		EnumSet<ImmutabilityTypes> SET7 = EnumSet.of(ImmutabilityTypes.MUTABLE);
		sets.add(SET1);
		sets.add(SET2);
		sets.add(SET3);
		sets.add(SET4);
		sets.add(SET5);
		sets.add(SET6);
		sets.add(SET7);
	}

	/**
	 * Checks and satisfies x :> y
	 * 
	 * @param xTypes
	 * @param yTypes
	 * @return
	 */
	public static boolean satisify(Node x, Node y) {
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		if(xTypes.isEmpty()){
			Log.warning("x [" + x.address().toAddressString() + "] is untyped, constraints cannot be satisfied", new IllegalArgumentException());
			return false;
		}
		
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		if(yTypes.isEmpty()){
			Log.warning("y [" + y.address().toAddressString() + "] is untyped, constraints cannot be satisfied", new IllegalArgumentException());
			return false;
		}
		
		return satisify(x, xTypes, y, yTypes);
	}

	private static short getCase(Set<ImmutabilityTypes> xTypes, Set<ImmutabilityTypes> yTypes) {
		short input = 0;

		int setID = 0;
		for (EnumSet<ImmutabilityTypes> set : sets) {
			if (xTypes.equals(set)) {
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;

		setID = 0;
		for (EnumSet<ImmutabilityTypes> set : sets) {
			if (yTypes.equals(set)) {
				break;
			} else {
				setID++;
			}
		}
		input |= setID;

		return input;
	}

	private static boolean satisify(Node x, Set<ImmutabilityTypes> xTypes, Node y, Set<ImmutabilityTypes> yTypes) {
		boolean xTypesChanged = false;
		boolean yTypesChanged = false;
		short input = getCase(xTypes, yTypes);
		switch (input) {
		case 0: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD]
			return false;
		case 3: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY]
			return false;
		case 4: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 5: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 6: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE]
			return false;
		case 8: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 9: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY]
			return false;
		case 10: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD]
			return false;
		case 11: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY]
			return false;
		case 12: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 13: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD]
			return false;
		case 14: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE]
			return false;
		case 16: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 17: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged;
		case 18: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD]
			return false;
		case 19: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 20: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged;
		case 21: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 22: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE]
			return false;
		case 24: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 25: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 26: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD]
			return false;
		case 27: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY]
			return false;
		case 28: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 29: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 30: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE]
			return false;
		case 32: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 33: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY]
			return false;
		case 34: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD]
			return false;
		case 35: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY]
			return false;
		case 36: // xTypes=[READONLY], yTypes=[READONLY]
			return false;
		case 37: // xTypes=[READONLY], yTypes=[POLYREAD]
			return false;
		case 38: // xTypes=[READONLY], yTypes=[MUTABLE]
			return false;
		case 40: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 41: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 42: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD]
			return false;
		case 43: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 44: // xTypes=[POLYREAD], yTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged;
		case 45: // xTypes=[POLYREAD], yTypes=[POLYREAD]
			return false;
		case 46: // xTypes=[POLYREAD], yTypes=[MUTABLE]
			return false;
		case 48: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 49: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged;
		case 50: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 51: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 52: // xTypes=[MUTABLE], yTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged;
		case 53: // xTypes=[MUTABLE], yTypes=[POLYREAD]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 54: // xTypes=[MUTABLE], yTypes=[MUTABLE]
			return false;
		default:
			throw new IllegalArgumentException("XGreaterThanYConstraintSolver Unhandled Case: xTypes=" + xTypes.toString() + ", yTypes=" + yTypes.toString());
		}
	}

}
