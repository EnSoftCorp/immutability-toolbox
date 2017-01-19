package com.ensoftcorp.open.immutability.experimental;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.log.Log;

public class AFieldAdaptBGreaterThanEqualCFieldAdaptDConstraintSolver {

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

	public static boolean satisify(Node a, Node b, Node c, Node d) {
		Set<ImmutabilityTypes> aTypes = getTypes(a);
		if (aTypes.isEmpty()) {
			Log.warning("a [" + a.address().toAddressString() + "] is untyped, constraint 'qa adapt zb :> qc adapt qd' cannot be satisfied", new IllegalArgumentException());
			return false;
		}

		Set<ImmutabilityTypes> bTypes = getTypes(b);
		if (bTypes.isEmpty()) {
			Log.warning("b [" + b.address().toAddressString() + "] is untyped, constraint 'qa adapt zb :> qc adapt qd' cannot be satisfied", new IllegalArgumentException());
			return false;
		}

		Set<ImmutabilityTypes> cTypes = getTypes(c);
		if (cTypes.isEmpty()) {
			Log.warning("c [" + c.address().toAddressString() + "] is untyped, constraint 'qa adapt zb :> qc adapt qd' cannot be satisfied", new IllegalArgumentException());
			return false;
		}

		Set<ImmutabilityTypes> dTypes = getTypes(d);
		if (dTypes.isEmpty()) {
			Log.warning("d [" + d.address().toAddressString() + "] is untyped, constraint 'qa adapt zb :> qc adapt qd' cannot be satisfied", new IllegalArgumentException());
			return false;
		}

		return satisify(a, aTypes, b, bTypes, c, cTypes, d, dTypes);
	}

	private static short getCase(Set<ImmutabilityTypes> aTypes, Set<ImmutabilityTypes> bTypes, Set<ImmutabilityTypes> cTypes, Set<ImmutabilityTypes> dTypes) {
		short input = 0;

		int setID = 0;
		for (EnumSet<ImmutabilityTypes> set : sets) {
			if (aTypes.equals(set)) {
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;

		setID = 0;
		for (EnumSet<ImmutabilityTypes> set : sets) {
			if (bTypes.equals(set)) {
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;

		setID = 0;
		for (EnumSet<ImmutabilityTypes> set : sets) {
			if (cTypes.equals(set)) {
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;

		setID = 0;
		for (EnumSet<ImmutabilityTypes> set : sets) {
			if (dTypes.equals(set)) {
				break;
			} else {
				setID++;
			}
		}
		input |= setID;

		return input;
	}

	private static boolean satisify(Node a, Set<ImmutabilityTypes> aTypes, Node b, Set<ImmutabilityTypes> bTypes, Node c, Set<ImmutabilityTypes> cTypes, Node d, Set<ImmutabilityTypes> dTypes) {
		boolean aTypesChanged = false;
		boolean bTypesChanged = false;
		boolean cTypesChanged = false;
		boolean dTypesChanged = false;
		short input = getCase(aTypes, bTypes, cTypes, dTypes);
		switch (input) {
		case 0: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 4: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 5: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 6: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 8: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 9: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 10: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 11: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 12: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 13: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 14: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 16: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 17: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 18: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 19: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 20: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 21: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 22: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 24: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 25: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 26: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 27: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 28: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 29: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 30: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 32: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 33: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 34: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 35: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 36: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 37: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 38: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 40: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 41: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 42: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 43: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 44: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 45: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 46: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 48: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 49: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 50: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 51: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 52: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 53: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 54: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 64: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 65: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 66: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 67: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 68: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 69: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 70: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 72: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 73: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 74: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 75: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 76: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 77: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 78: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 80: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 81: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 82: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 83: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 84: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 85: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 86: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 88: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 89: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 90: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 91: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 92: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 93: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 94: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 96: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 97: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 98: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 99: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 100: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 101: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 102: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 104: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 105: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 106: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 107: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 108: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 109: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 110: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 112: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 113: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 114: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 115: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 116: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 117: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 118: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 128: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 129: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 130: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 131: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 132: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 133: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 134: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 136: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 137: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 138: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 139: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 140: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 141: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 142: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 144: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 145: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 146: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 147: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 148: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 149: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 150: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 152: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 153: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 154: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 155: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 156: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 157: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 158: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 160: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 161: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 162: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 163: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 164: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 165: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 166: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 168: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 169: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 170: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 171: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 172: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 173: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 174: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 176: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 177: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 178: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 179: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 180: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 181: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 182: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 192: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 193: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 194: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 195: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 196: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 197: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 198: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 200: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 201: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 202: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 203: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 204: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 205: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 206: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 208: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 209: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 210: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 211: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 212: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 213: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 214: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 216: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 217: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 218: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 219: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 220: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 221: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 222: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 224: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 225: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 226: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 227: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 228: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 229: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 230: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 232: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 233: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 234: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 235: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 236: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 237: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 238: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 240: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 241: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 242: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 243: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 244: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 245: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 246: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 256: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 257: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 258: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 259: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 260: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 261: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 262: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 264: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 265: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 266: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 267: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 268: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 269: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 270: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 272: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 273: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 274: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 275: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 276: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 277: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 278: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 280: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 281: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 282: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 283: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 284: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 285: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 286: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 288: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 289: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 290: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 291: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 292: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 293: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 294: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 296: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 297: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 298: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 299: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 300: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 301: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 302: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 304: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 305: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 306: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 307: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 308: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 309: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 310: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 320: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 321: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 322: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 323: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 324: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 325: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 326: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 328: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 329: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 330: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 331: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 332: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 333: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 334: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 336: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 337: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 338: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 339: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 340: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 341: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 342: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 344: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 345: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 346: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 347: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 348: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 349: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 350: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 352: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 353: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 354: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 355: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 356: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 357: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 358: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 360: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 361: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 362: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 363: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 364: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 365: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 366: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 368: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 369: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 370: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 371: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 372: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 373: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 374: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 384: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 385: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 386: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 387: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 388: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 389: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 390: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 392: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 393: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 394: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 395: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 396: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 397: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 398: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 400: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 401: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 402: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 403: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 404: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 405: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 406: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 408: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 409: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 410: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 411: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 412: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 413: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 414: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 416: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 417: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 418: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 419: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 420: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 421: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 422: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 424: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 425: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 426: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 427: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 428: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 429: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 430: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 432: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 433: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 434: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 435: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 436: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 437: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 438: // aTypes=[MUTABLE, POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 512: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 513: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 514: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 515: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 516: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 517: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 518: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 520: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 521: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 522: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 523: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 524: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 525: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 526: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 528: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 529: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 530: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 531: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 532: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 533: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 534: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 536: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 537: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 538: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 539: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 540: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 541: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 542: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 544: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 545: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 546: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 547: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 548: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 549: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 550: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 552: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 553: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 554: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 555: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 556: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 557: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 558: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 560: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 561: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 562: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 563: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 564: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 565: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 566: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 576: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 577: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 578: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 579: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 580: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 581: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 582: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 584: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 585: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 586: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 587: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 588: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 589: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 590: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 592: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 593: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 594: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 595: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 596: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 597: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 598: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 600: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 601: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 602: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 603: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 604: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 605: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 606: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 608: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 609: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 610: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 611: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 612: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 613: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 614: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 616: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 617: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 618: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 619: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 620: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 621: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 622: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 624: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 625: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 626: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 627: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 628: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 629: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 630: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 640: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 641: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 642: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 643: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 644: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 645: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 646: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 648: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 649: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 650: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 651: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 652: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 653: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 654: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 656: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 657: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 658: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 659: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 660: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 661: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 662: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 664: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 665: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 666: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 667: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 668: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 669: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 670: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 672: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 673: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 674: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 675: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 676: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 677: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 678: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 680: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 681: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 682: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 683: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 684: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 685: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 686: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 688: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 689: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 690: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 691: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 692: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 693: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 694: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 704: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 705: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 706: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 707: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 708: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 709: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 710: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 712: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 713: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 714: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 715: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 716: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 717: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 718: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 720: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 721: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 722: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 723: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 724: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 725: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 726: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 728: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 729: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 730: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 731: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 732: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 733: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 734: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 736: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 737: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 738: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 739: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 740: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 741: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 742: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 744: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 745: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 746: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 747: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 748: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 749: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 750: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 752: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 753: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 754: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 755: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 756: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 757: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 758: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 768: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 769: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 770: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 771: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 772: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 773: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 774: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 776: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 777: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 778: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 779: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 780: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 781: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 782: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 784: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 785: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 786: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 787: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 788: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 789: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 790: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 792: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 793: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 794: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 795: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 796: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 797: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 798: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 800: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 801: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 802: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 803: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 804: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 805: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 806: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 808: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 809: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 810: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 811: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 812: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 813: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 814: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 816: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 817: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 818: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 819: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 820: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 821: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 822: // aTypes=[POLYREAD, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 832: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 833: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 834: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 835: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 836: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 837: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 838: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 840: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 841: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 842: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 843: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 844: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 845: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 846: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 848: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 849: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 850: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 851: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 852: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 853: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 854: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 856: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 857: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 858: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 859: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 860: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 861: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 862: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 864: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 865: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 866: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 867: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 868: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 869: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 870: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 872: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 873: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 874: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 875: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 876: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 877: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 878: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 880: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 881: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 882: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 883: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 884: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 885: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 886: // aTypes=[POLYREAD, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 896: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 897: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 898: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 899: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 900: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 901: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 902: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 904: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 905: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 906: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 907: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 908: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 909: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 910: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 912: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 913: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 914: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 915: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 916: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 917: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 918: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 920: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 921: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 922: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 923: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 924: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 925: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 926: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 928: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 929: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 930: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 931: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 932: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 933: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 934: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 936: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 937: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 938: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 939: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 940: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 941: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 942: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 944: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 945: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 946: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 947: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 948: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 949: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 950: // aTypes=[POLYREAD, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1024: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1025: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1026: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1027: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1028: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1029: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1030: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1032: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1033: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1034: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1035: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1036: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1037: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1038: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1040: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1041: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1042: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1043: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1044: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1045: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1046: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1048: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1049: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1050: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1051: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1052: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1053: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1054: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1056: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1057: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1058: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1059: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1060: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1061: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1062: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1064: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1065: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1066: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1067: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1068: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1069: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1070: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1072: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1073: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1074: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1075: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1076: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 1077: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1078: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1088: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1089: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1090: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1091: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1092: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1093: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1094: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1096: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1097: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1098: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1099: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1100: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1101: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1102: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1104: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1105: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1106: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1107: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1108: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1109: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1110: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1112: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1113: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1114: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1115: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1116: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1117: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1118: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1120: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1121: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1122: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1123: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1124: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1125: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1126: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1128: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1129: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1130: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1131: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1132: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1133: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1134: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1136: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1137: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1138: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1139: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1140: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1141: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1142: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1152: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1153: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1154: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1155: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1156: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1157: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1158: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1160: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1161: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1162: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1163: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1164: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1165: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged;
		case 1166: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1168: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1169: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1170: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1171: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1172: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1173: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1174: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1176: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1177: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1178: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1179: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1180: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1181: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1182: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1184: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1185: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1186: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1187: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1188: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1189: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1190: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1192: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1193: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || dTypesChanged;
		case 1194: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1195: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1196: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1197: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1198: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1200: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1201: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1202: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1203: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1204: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1205: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1206: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1216: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1217: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1218: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1219: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1220: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1221: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1222: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1224: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1225: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1226: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1227: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1228: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1229: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1230: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1232: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1233: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1234: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1235: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1236: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1237: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1238: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1240: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1241: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1242: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1243: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1244: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1245: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1246: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1248: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1249: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1250: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1251: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1252: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1253: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1254: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1256: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1257: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1258: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1259: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1260: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1261: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1262: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1264: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1265: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1266: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1267: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1268: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1269: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1270: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1280: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1281: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1282: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1283: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1284: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 1285: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1286: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1288: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1289: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1290: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1291: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1292: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 1293: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1294: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1296: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1297: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1298: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1299: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1300: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 1301: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1302: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1304: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1305: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1306: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1307: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1308: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 1309: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1310: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1312: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1313: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1314: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1315: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1316: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 1317: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 1318: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1320: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1321: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1322: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1323: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1324: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 1325: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1326: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1328: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1329: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1330: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1331: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1332: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 1333: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1334: // aTypes=[MUTABLE, POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1344: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1345: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1346: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1347: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1348: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1349: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1350: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1352: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1353: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || cTypesChanged || dTypesChanged;
		case 1354: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1355: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1356: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1357: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || cTypesChanged;
		case 1358: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1360: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1361: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1362: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1363: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1364: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1365: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1366: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1368: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1369: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1370: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1371: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1372: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1373: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1374: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1376: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1377: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1378: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1379: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1380: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1381: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1382: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1384: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1385: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || dTypesChanged;
		case 1386: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1387: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1388: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1389: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1390: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1392: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1393: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1394: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1395: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1396: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1397: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1398: // aTypes=[MUTABLE, POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1408: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1409: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1410: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1411: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1412: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1413: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1414: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1416: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1417: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1418: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1419: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1420: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1421: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1422: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1424: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1425: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1426: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1427: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1428: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1429: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1430: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1432: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1433: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1434: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1435: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1436: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1437: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1438: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1440: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1441: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1442: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1443: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1444: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1445: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1446: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1448: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1449: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1450: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1451: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1452: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1453: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1454: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1456: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1457: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1458: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1459: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1460: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1461: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1462: // aTypes=[MUTABLE, POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1536: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1537: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1538: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1539: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1540: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1541: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1542: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1544: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1545: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1546: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1547: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1548: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1549: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1550: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1552: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1553: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1554: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1555: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1556: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1557: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1558: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1560: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1561: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1562: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1563: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1564: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1565: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1566: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1568: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1569: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1570: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1571: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1572: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1573: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1574: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1576: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1577: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1578: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1579: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1580: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1581: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1582: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1584: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1585: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1586: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1587: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1588: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1589: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1590: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1600: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1601: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1602: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1603: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1604: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 1605: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1606: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1608: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1609: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1610: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1611: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1612: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 1613: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1614: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1616: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1617: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1618: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1619: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1620: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 1621: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1622: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1624: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1625: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1626: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1627: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1628: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 1629: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1630: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1632: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1633: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1634: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1635: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1636: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 1637: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 1638: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1640: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1641: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1642: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1643: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1644: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 1645: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1646: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1648: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1649: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1650: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1651: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1652: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 1653: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1654: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1664: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1665: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1666: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1667: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1668: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1669: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1670: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1672: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1673: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1674: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1675: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1676: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1677: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1678: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1680: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1681: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1682: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1683: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1684: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1685: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1686: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1688: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1689: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1690: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1691: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1692: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1693: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1694: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1696: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1697: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1698: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1699: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1700: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1701: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1702: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1704: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1705: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1706: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1707: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1708: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1709: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1710: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1712: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1713: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1714: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1715: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1716: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return aTypesChanged || bTypesChanged;
		case 1717: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1718: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1728: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1729: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1730: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1731: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1732: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1733: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1734: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1736: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1737: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1738: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1739: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1740: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1741: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1742: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1744: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1745: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1746: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1747: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1748: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1749: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1750: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1752: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1753: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1754: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1755: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1756: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1757: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1758: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1760: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1761: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1762: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1763: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1764: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1765: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1766: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1768: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1769: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1770: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1771: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1772: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1773: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1774: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1776: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1777: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1778: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1779: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1780: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1781: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1782: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1792: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1793: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1794: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1795: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1796: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 1797: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1798: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1800: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1801: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1802: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1803: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1804: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 1805: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1806: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1808: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1809: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1810: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1811: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1812: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 1813: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1814: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1816: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1817: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1818: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1819: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1820: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 1821: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1822: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1824: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1825: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1826: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1827: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1828: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 1829: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 1830: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1832: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1833: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1834: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1835: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1836: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 1837: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1838: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1840: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1841: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1842: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1843: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1844: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 1845: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1846: // aTypes=[MUTABLE, READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1856: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1857: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1858: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1859: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1860: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1861: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 1862: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1864: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1865: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1866: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1867: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1868: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1869: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1870: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1872: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1873: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 1874: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1875: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1876: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1877: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 1878: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1880: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1881: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 1882: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1883: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1884: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1885: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 1886: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1888: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1889: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1890: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1891: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 1892: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1893: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1894: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1896: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1897: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1898: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1899: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 1900: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1901: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1902: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1904: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1905: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 1906: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1907: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 1908: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 1909: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1910: // aTypes=[MUTABLE, READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 1920: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1921: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1922: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1923: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1924: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1925: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1926: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1928: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1929: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1930: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1931: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1932: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1933: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1934: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 1936: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1937: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1938: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1939: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1940: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1941: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1942: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1944: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1945: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 1946: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1947: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1948: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1949: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1950: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 1952: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1953: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1954: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1955: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1956: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1957: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1958: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 1960: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 1961: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1962: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 1963: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1964: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1965: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1966: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 1968: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1969: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1970: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 1971: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 1972: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 1973: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 1974: // aTypes=[MUTABLE, READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2048: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2049: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2050: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2051: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2052: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2053: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2054: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2056: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2057: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2058: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2059: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2060: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2061: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2062: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2064: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2065: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2066: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2067: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2068: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2069: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2070: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2072: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2073: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2074: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2075: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2076: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2077: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2078: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2080: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2081: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2082: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2083: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2084: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2085: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2086: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2088: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2089: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2090: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2091: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2092: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2093: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2094: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2096: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2097: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2098: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2099: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2100: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2101: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2102: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2112: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2113: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2114: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2115: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2116: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2117: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2118: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2120: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2121: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2122: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2123: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2124: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2125: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2126: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2128: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2129: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2130: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2131: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2132: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 2133: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2134: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2136: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2137: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2138: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2139: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2140: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 2141: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2142: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2144: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2145: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2146: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2147: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2148: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 2149: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 2150: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2152: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2153: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2154: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2155: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2156: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 2157: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2158: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2160: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2161: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2162: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2163: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2164: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 2165: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2166: // aTypes=[READONLY], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2176: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2177: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2178: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2179: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2180: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2181: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2182: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2184: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2185: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2186: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2187: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2188: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2189: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2190: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2192: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2193: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2194: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2195: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2196: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2197: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2198: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2200: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2201: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2202: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2203: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2204: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2205: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2206: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2208: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2209: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2210: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2211: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2212: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2213: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2214: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2216: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2217: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2218: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2219: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2220: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2221: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2222: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2224: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2225: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2226: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2227: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2228: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2229: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2230: // aTypes=[READONLY], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2240: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2241: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2242: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2243: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2244: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2245: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2246: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2248: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2249: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2250: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2251: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2252: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2253: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2254: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2256: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2257: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2258: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2259: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2260: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2261: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2262: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2264: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2265: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2266: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2267: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2268: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2269: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2270: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2272: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2273: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2274: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2275: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2276: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2277: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2278: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2280: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2281: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2282: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2283: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2284: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2285: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2286: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2288: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2289: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2290: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2291: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2292: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2293: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2294: // aTypes=[READONLY], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2304: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2305: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2306: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2307: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2308: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2309: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2310: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2312: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2313: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2314: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2315: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2316: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2317: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2318: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2320: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2321: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2322: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2323: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2324: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 2325: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2326: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2328: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2329: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2330: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2331: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2332: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 2333: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2334: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2336: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2337: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2338: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2339: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2340: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 2341: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 2342: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2344: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2345: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2346: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2347: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2348: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 2349: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2350: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2352: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2353: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2354: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2355: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2356: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 2357: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2358: // aTypes=[READONLY], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2368: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2369: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2370: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2371: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2372: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2373: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2374: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2376: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2377: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2378: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2379: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2380: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2381: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2382: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2384: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2385: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2386: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2387: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2388: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 2389: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2390: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2392: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2393: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2394: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2395: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2396: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 2397: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2398: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2400: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2401: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2402: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2403: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2404: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 2405: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 2406: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2408: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2409: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2410: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2411: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2412: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 2413: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2414: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2416: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2417: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2418: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2419: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2420: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 2421: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2422: // aTypes=[READONLY], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2432: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2433: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2434: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2435: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2436: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2437: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2438: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2440: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2441: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2442: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2443: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2444: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2445: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2446: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2448: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2449: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2450: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2451: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2452: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2453: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2454: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2456: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2457: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2458: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2459: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2460: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2461: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2462: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2464: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2465: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2466: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2467: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2468: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2469: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2470: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2472: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2473: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2474: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2475: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2476: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2477: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2478: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2480: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2481: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2482: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2483: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2484: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.READONLY));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2485: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2486: // aTypes=[READONLY], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2560: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2561: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2562: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2563: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2564: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2565: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2566: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2568: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2569: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2570: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2571: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2572: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2573: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2574: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2576: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2577: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2578: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2579: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2580: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2581: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2582: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2584: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2585: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2586: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2587: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2588: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2589: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2590: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2592: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2593: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2594: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2595: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2596: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2597: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2598: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2600: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2601: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2602: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2603: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2604: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2605: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2606: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2608: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2609: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2610: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2611: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2612: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 2613: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2614: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2624: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2625: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2626: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2627: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2628: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2629: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2630: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2632: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2633: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2634: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2635: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2636: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2637: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2638: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2640: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2641: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2642: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2643: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2644: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2645: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2646: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2648: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2649: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2650: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2651: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2652: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2653: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2654: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2656: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2657: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2658: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2659: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2660: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2661: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2662: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2664: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2665: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2666: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2667: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2668: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2669: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2670: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2672: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2673: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2674: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2675: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2676: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2677: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2678: // aTypes=[POLYREAD], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2688: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2689: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2690: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2691: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2692: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2693: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2694: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2696: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2697: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return bTypesChanged || cTypesChanged || dTypesChanged;
		case 2698: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2699: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2700: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2701: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			return bTypesChanged || cTypesChanged;
		case 2702: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2704: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2705: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2706: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2707: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2708: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2709: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2710: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2712: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2713: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2714: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2715: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2716: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2717: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2718: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2720: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2721: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2722: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2723: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2724: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2725: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2726: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2728: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2729: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return bTypesChanged || dTypesChanged;
		case 2730: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2731: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2732: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2733: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2734: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2736: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2737: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2738: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2739: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2740: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2741: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2742: // aTypes=[POLYREAD], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2752: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2753: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2754: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2755: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2756: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2757: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2758: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2760: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2761: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2762: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2763: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2764: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2765: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2766: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2768: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2769: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2770: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2771: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2772: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2773: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2774: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2776: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2777: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2778: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2779: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2780: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2781: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2782: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2784: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2785: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2786: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2787: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2788: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2789: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2790: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2792: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2793: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2794: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2795: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2796: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2797: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2798: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2800: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2801: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2802: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2803: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2804: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 2805: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2806: // aTypes=[POLYREAD], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2816: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2817: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2818: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2819: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2820: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2821: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2822: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2824: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2825: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2826: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2827: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2828: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 2829: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 2830: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2832: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2833: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2834: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2835: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2836: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 2837: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2838: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2840: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2841: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2842: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2843: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2844: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 2845: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 2846: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2848: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2849: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 2850: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2851: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 2852: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 2853: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 2854: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2856: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2857: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 2858: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2859: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 2860: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 2861: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2862: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2864: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 2865: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 2866: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2867: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 2868: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 2869: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2870: // aTypes=[POLYREAD], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2880: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2881: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2882: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2883: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2884: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2885: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2886: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2888: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2889: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2890: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2891: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2892: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2893: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2894: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2896: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2897: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2898: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2899: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2900: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2901: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2902: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2904: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2905: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2906: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2907: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2908: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2909: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2910: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2912: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2913: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2914: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2915: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2916: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2917: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2918: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2920: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2921: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2922: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2923: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2924: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2925: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 2926: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2928: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2929: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2930: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2931: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2932: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2933: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2934: // aTypes=[POLYREAD], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 2944: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2945: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2946: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2947: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2948: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2949: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2950: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2952: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2953: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2954: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2955: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2956: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2957: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2958: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 2960: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2961: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2962: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2963: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2964: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2965: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2966: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2968: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2969: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 2970: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2971: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2972: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2973: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2974: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 2976: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2977: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2978: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2979: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2980: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2981: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2982: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 2984: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 2985: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2986: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 2987: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2988: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2989: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2990: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 2992: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2993: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2994: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 2995: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 2996: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.POLYREAD));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 2997: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 2998: // aTypes=[POLYREAD], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 3072: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3073: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3074: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3075: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3076: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3077: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 3078: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3080: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3081: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3082: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3083: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3084: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3085: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3086: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3088: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3089: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 3090: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3091: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3092: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3093: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 3094: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3096: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3097: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3098: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3099: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3100: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3101: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 3102: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 3104: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3105: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3106: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3107: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3108: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3109: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3110: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 3112: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3113: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3114: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3115: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3116: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3117: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3118: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3120: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3121: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 3122: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3123: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 3124: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 3125: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 3126: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 3136: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3137: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3138: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3139: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3140: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3141: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 3142: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3144: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3145: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3146: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3147: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3148: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3149: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3150: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3152: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3153: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 3154: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3155: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3156: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3157: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 3158: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3160: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3161: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3162: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3163: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3164: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3165: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 3166: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 3168: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3169: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3170: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3171: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3172: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3173: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3174: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 3176: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3177: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3178: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3179: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3180: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3181: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3182: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3184: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3185: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 3186: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3187: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 3188: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3189: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 3190: // aTypes=[MUTABLE], bTypes=[POLYREAD, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 3200: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3201: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3202: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3203: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3204: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3205: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3206: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3208: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3209: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3210: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3211: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3212: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3213: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3214: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3216: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3217: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3218: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3219: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3220: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3221: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3222: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3224: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3225: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3226: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3227: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3228: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3229: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3230: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 3232: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3233: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3234: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3235: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3236: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3237: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3238: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 3240: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3241: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3242: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3243: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3244: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3245: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3246: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3248: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3249: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3250: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3251: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3252: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3253: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 3254: // aTypes=[MUTABLE], bTypes=[MUTABLE, POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 3264: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3265: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3266: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3267: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3268: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3269: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 3270: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3272: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3273: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3274: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3275: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3276: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3277: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3278: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3280: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3281: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 3282: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3283: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3284: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3285: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 3286: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3288: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3289: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3290: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3291: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3292: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3293: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 3294: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 3296: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3297: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3298: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3299: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3300: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3301: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3302: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 3304: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3305: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3306: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3307: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3308: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3309: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3310: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3312: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3313: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 3314: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3315: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 3316: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 3317: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 3318: // aTypes=[MUTABLE], bTypes=[MUTABLE, READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 3328: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3329: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3330: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3331: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3332: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 3333: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 3334: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3336: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3337: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3338: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3339: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3340: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			return false;
		case 3341: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			return false;
		case 3342: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3344: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3345: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 3346: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3347: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3348: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			return false;
		case 3349: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return false;
		case 3350: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3352: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3353: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3354: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3355: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3356: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			return false;
		case 3357: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return false;
		case 3358: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 3360: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3361: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			return false;
		case 3362: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3363: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return false;
		case 3364: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[READONLY], dTypes=[READONLY]
			return false;
		case 3365: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[READONLY], dTypes=[POLYREAD]
			return false;
		case 3366: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 3368: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3369: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			return false;
		case 3370: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3371: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return false;
		case 3372: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[READONLY]
			return false;
		case 3373: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[POLYREAD]
			return false;
		case 3374: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3376: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 3377: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return false;
		case 3378: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3379: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return false;
		case 3380: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[READONLY]
			return false;
		case 3381: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 3382: // aTypes=[MUTABLE], bTypes=[READONLY], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 3392: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3393: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3394: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3395: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3396: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3397: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3398: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3400: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3401: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3402: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3403: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3404: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3405: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3406: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3408: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3409: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3410: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3411: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3412: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3413: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3414: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3416: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3417: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3418: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3419: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3420: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3421: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3422: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 3424: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3425: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3426: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3427: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3428: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3429: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3430: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 3432: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3433: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3434: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3435: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3436: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3437: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3438: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3440: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3441: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3442: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3443: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3444: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.POLYREAD));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3445: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 3446: // aTypes=[MUTABLE], bTypes=[POLYREAD], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		case 3456: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3457: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3458: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3459: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3460: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3461: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3462: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3464: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3465: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3466: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3467: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3468: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3469: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3470: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD, READONLY], dTypes=[MUTABLE]
			return false;
		case 3472: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3473: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3474: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3475: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3476: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3477: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3478: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3480: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3481: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD, READONLY]
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return cTypesChanged || dTypesChanged;
		case 3482: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3483: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3484: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3485: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[POLYREAD]
			return removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3486: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE, READONLY], dTypes=[MUTABLE]
			return false;
		case 3488: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3489: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3490: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3491: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3492: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3493: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.READONLY));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3494: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[READONLY], dTypes=[MUTABLE]
			return false;
		case 3496: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 3497: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD, READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3498: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, POLYREAD]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 3499: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3500: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3501: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[POLYREAD]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.POLYREAD));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3502: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[POLYREAD], dTypes=[MUTABLE]
			return false;
		case 3504: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3505: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3506: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, POLYREAD]
			return false;
		case 3507: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE, READONLY]
			return removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
		case 3508: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[READONLY]
			aTypesChanged = removeTypes(a, EnumSet.of(ImmutabilityTypes.MUTABLE));
			bTypesChanged = removeTypes(b, EnumSet.of(ImmutabilityTypes.MUTABLE));
			cTypesChanged = removeTypes(c, EnumSet.of(ImmutabilityTypes.MUTABLE));
			dTypesChanged = removeTypes(d, EnumSet.of(ImmutabilityTypes.READONLY));
			return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;
		case 3509: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[POLYREAD]
			return false;
		case 3510: // aTypes=[MUTABLE], bTypes=[MUTABLE], cTypes=[MUTABLE], dTypes=[MUTABLE]
			return false;
		default:
			throw new IllegalArgumentException("AAdaptBGreaterThanCAdaptDConstraintSolver Unhandled Case: aTypes=" + aTypes.toString() + ", bTypes=" + bTypes.toString() + ", cTypes=" + cTypes.toString() + ", dTypes=" + dTypes.toString());
		}
	}

}
