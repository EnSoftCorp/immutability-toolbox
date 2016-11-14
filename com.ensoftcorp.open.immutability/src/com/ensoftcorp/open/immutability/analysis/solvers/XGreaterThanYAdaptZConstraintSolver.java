package com.ensoftcorp.open.immutability.analysis.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class XGreaterThanYAdaptZConstraintSolver {

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

	public static boolean satisify(Node x, Node y, Node z) {
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		if(xTypes.isEmpty()){
			Log.warning("x [" + x.address().toAddressString() + "] is untyped, constraints cannot be satisfied");
			return false;
		}
		
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		if(yTypes.isEmpty()){
			Log.warning("y [" + y.address().toAddressString() + "] is untyped, constraints cannot be satisfied");
			return false;
		}
		
		Set<ImmutabilityTypes> zTypes = getTypes(z);
		if(zTypes.isEmpty()){
			Log.warning("z [" + z.address().toAddressString() + "] is untyped, constraints cannot be satisfied");
			return false;
		}
		
		return satisify(x, xTypes, y, yTypes, z, zTypes);
	}

	private static short getCase(Set<ImmutabilityTypes> xTypes, Set<ImmutabilityTypes> yTypes, Set<ImmutabilityTypes> zTypes) {
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
		input <<= 3;

		setID = 0;
		for (EnumSet<ImmutabilityTypes> set : sets) {
			if (zTypes.equals(set)) {
				break;
			} else {
				setID++;
			}
		}
		input |= setID;

		return input;
	}

	public static boolean satisify(Node x, Set<ImmutabilityTypes> xTypes, Node y, Set<ImmutabilityTypes> yTypes, Node z, Set<ImmutabilityTypes> zTypes) {
		boolean xTypesChanged = false;
		boolean yTypesChanged = false;
		boolean zTypesChanged = false;
		short input = getCase(xTypes, yTypes, zTypes);
		switch (input) {
		case 0: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 1: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 2: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 3: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 4: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 5: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD]
			return false;
		case 6: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 8: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 9: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 10: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 11: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 12: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 13: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 14: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 16: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 17: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD, READONLY]
			return false;
		case 18: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 19: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 20: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 21: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD]
			return false;
		case 22: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE]
			return false;
		case 24: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 25: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 26: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 27: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 28: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 29: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD]
			return false;
		case 30: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE]
			return false;
		case 32: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 33: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY], zTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 34: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 35: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 36: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 37: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 38: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE]
			return false;
		case 40: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 41: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 42: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 43: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 44: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 45: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 46: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE]
			return false;
		case 48: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 49: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD, READONLY]
			return false;
		case 50: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 51: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, READONLY]
			return false;
		case 52: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 53: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD]
			return false;
		case 54: // xTypes=[MUTABLE, POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE]
			return false;
		case 64: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 65: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 66: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 67: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 68: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 69: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD]
			return false;
		case 70: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 72: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 73: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 74: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 75: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 76: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 77: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD]
			return false;
		case 78: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 80: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 81: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD, READONLY]
			return false;
		case 82: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 83: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 84: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 85: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD]
			return false;
		case 86: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE]
			return false;
		case 88: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 89: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 90: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 91: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 92: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 93: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD]
			return false;
		case 94: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE]
			return false;
		case 96: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 97: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY], zTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 98: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 99: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 100: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 101: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 102: // xTypes=[POLYREAD, READONLY], yTypes=[READONLY], zTypes=[MUTABLE]
			return false;
		case 104: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 105: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD, READONLY]
			return false;
		case 106: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 107: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 108: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 109: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD]
			return false;
		case 110: // xTypes=[POLYREAD, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE]
			return false;
		case 112: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 113: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD, READONLY]
			return false;
		case 114: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 115: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, READONLY]
			return false;
		case 116: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 117: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD]
			return false;
		case 118: // xTypes=[POLYREAD, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE]
			return false;
		case 128: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 129: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 130: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 131: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 132: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 133: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 134: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 136: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 137: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 138: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 139: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 140: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 141: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged;
		case 142: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 144: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 145: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 146: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 147: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 148: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 149: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD]
			return false;
		case 150: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE]
			return false;
		case 152: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 153: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 154: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 155: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 156: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 157: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 158: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE]
			return false;
		case 160: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 161: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY], zTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 162: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 163: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 164: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 165: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY], zTypes=[POLYREAD]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 166: // xTypes=[MUTABLE, POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE]
			return false;
		case 168: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 169: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD], zTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || zTypesChanged;
		case 170: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 171: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 172: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 173: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 174: // xTypes=[MUTABLE, POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE]
			return false;
		case 176: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 177: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE], zTypes=[POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 178: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 179: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 180: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 181: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE], zTypes=[POLYREAD]
			return false;
		case 182: // xTypes=[MUTABLE, POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE]
			return false;
		case 192: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 193: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 194: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 195: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 196: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 197: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD]
			return false;
		case 198: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 200: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 201: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 202: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 203: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 204: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 205: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 206: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 208: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 209: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD, READONLY]
			return false;
		case 210: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 211: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 212: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 213: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD]
			return false;
		case 214: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE]
			return false;
		case 216: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 217: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 218: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 219: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 220: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 221: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD]
			return false;
		case 222: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE]
			return false;
		case 224: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 225: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY], zTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 226: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 227: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 228: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 229: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 230: // xTypes=[MUTABLE, READONLY], yTypes=[READONLY], zTypes=[MUTABLE]
			return false;
		case 232: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 233: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD, READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 234: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 235: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 236: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 237: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 238: // xTypes=[MUTABLE, READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE]
			return false;
		case 240: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 241: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD, READONLY]
			return false;
		case 242: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 243: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, READONLY]
			return false;
		case 244: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE], zTypes=[READONLY]
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 245: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD]
			return false;
		case 246: // xTypes=[MUTABLE, READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE]
			return false;
		case 256: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 257: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 258: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 259: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 260: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[READONLY]
			return false;
		case 261: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD]
			return false;
		case 262: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 264: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 265: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 266: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 267: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 268: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY], zTypes=[READONLY]
			return false;
		case 269: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD]
			return false;
		case 270: // xTypes=[READONLY], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 272: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 273: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD, READONLY]
			return false;
		case 274: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 275: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 276: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[READONLY]
			return false;
		case 277: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD]
			return false;
		case 278: // xTypes=[READONLY], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE]
			return false;
		case 280: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 281: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 282: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 283: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 284: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY], zTypes=[READONLY]
			return false;
		case 285: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD]
			return false;
		case 286: // xTypes=[READONLY], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE]
			return false;
		case 288: // xTypes=[READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 289: // xTypes=[READONLY], yTypes=[READONLY], zTypes=[POLYREAD, READONLY]
			return false;
		case 290: // xTypes=[READONLY], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 291: // xTypes=[READONLY], yTypes=[READONLY], zTypes=[MUTABLE, READONLY]
			return false;
		case 292: // xTypes=[READONLY], yTypes=[READONLY], zTypes=[READONLY]
			return false;
		case 293: // xTypes=[READONLY], yTypes=[READONLY], zTypes=[POLYREAD]
			return false;
		case 294: // xTypes=[READONLY], yTypes=[READONLY], zTypes=[MUTABLE]
			return false;
		case 296: // xTypes=[READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 297: // xTypes=[READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD, READONLY]
			return false;
		case 298: // xTypes=[READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 299: // xTypes=[READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE, READONLY]
			return false;
		case 300: // xTypes=[READONLY], yTypes=[POLYREAD], zTypes=[READONLY]
			return false;
		case 301: // xTypes=[READONLY], yTypes=[POLYREAD], zTypes=[POLYREAD]
			return false;
		case 302: // xTypes=[READONLY], yTypes=[POLYREAD], zTypes=[MUTABLE]
			return false;
		case 304: // xTypes=[READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD, READONLY]
			return false;
		case 305: // xTypes=[READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD, READONLY]
			return false;
		case 306: // xTypes=[READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 307: // xTypes=[READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE, READONLY]
			return false;
		case 308: // xTypes=[READONLY], yTypes=[MUTABLE], zTypes=[READONLY]
			return false;
		case 309: // xTypes=[READONLY], yTypes=[MUTABLE], zTypes=[POLYREAD]
			return false;
		case 310: // xTypes=[READONLY], yTypes=[MUTABLE], zTypes=[MUTABLE]
			return false;
		case 320: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 321: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 322: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 323: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 324: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 325: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 326: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 328: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 329: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 330: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 331: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 332: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 333: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 334: // xTypes=[POLYREAD], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 336: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 337: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 338: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 339: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 340: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 341: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD]
			return false;
		case 342: // xTypes=[POLYREAD], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE]
			return false;
		case 344: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 345: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 346: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 347: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 348: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 349: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 350: // xTypes=[POLYREAD], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE]
			return false;
		case 352: // xTypes=[POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 353: // xTypes=[POLYREAD], yTypes=[READONLY], zTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 354: // xTypes=[POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 355: // xTypes=[POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 356: // xTypes=[POLYREAD], yTypes=[READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 357: // xTypes=[POLYREAD], yTypes=[READONLY], zTypes=[POLYREAD]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 358: // xTypes=[POLYREAD], yTypes=[READONLY], zTypes=[MUTABLE]
			return false;
		case 360: // xTypes=[POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 361: // xTypes=[POLYREAD], yTypes=[POLYREAD], zTypes=[POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 362: // xTypes=[POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 363: // xTypes=[POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 364: // xTypes=[POLYREAD], yTypes=[POLYREAD], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 365: // xTypes=[POLYREAD], yTypes=[POLYREAD], zTypes=[POLYREAD]
			return false;
		case 366: // xTypes=[POLYREAD], yTypes=[POLYREAD], zTypes=[MUTABLE]
			return false;
		case 368: // xTypes=[POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 369: // xTypes=[POLYREAD], yTypes=[MUTABLE], zTypes=[POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 370: // xTypes=[POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 371: // xTypes=[POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 372: // xTypes=[POLYREAD], yTypes=[MUTABLE], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 373: // xTypes=[POLYREAD], yTypes=[MUTABLE], zTypes=[POLYREAD]
			return false;
		case 374: // xTypes=[POLYREAD], yTypes=[MUTABLE], zTypes=[MUTABLE]
			return false;
		case 384: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 385: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 386: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 387: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 388: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 389: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 390: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 392: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 393: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 394: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, POLYREAD]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 395: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 396: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 397: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY], zTypes=[POLYREAD]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 398: // xTypes=[MUTABLE], yTypes=[POLYREAD, READONLY], zTypes=[MUTABLE]
			return false;
		case 400: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 401: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 402: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 403: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 404: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 405: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 406: // xTypes=[MUTABLE], yTypes=[MUTABLE, POLYREAD], zTypes=[MUTABLE]
			return false;
		case 408: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 409: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD, READONLY]
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 410: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 411: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 412: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 413: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY], zTypes=[POLYREAD]
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 414: // xTypes=[MUTABLE], yTypes=[MUTABLE, READONLY], zTypes=[MUTABLE]
			return false;
		case 416: // xTypes=[MUTABLE], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 417: // xTypes=[MUTABLE], yTypes=[READONLY], zTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 418: // xTypes=[MUTABLE], yTypes=[READONLY], zTypes=[MUTABLE, POLYREAD]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 419: // xTypes=[MUTABLE], yTypes=[READONLY], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 420: // xTypes=[MUTABLE], yTypes=[READONLY], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 421: // xTypes=[MUTABLE], yTypes=[READONLY], zTypes=[POLYREAD]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 422: // xTypes=[MUTABLE], yTypes=[READONLY], zTypes=[MUTABLE]
			return false;
		case 424: // xTypes=[MUTABLE], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 425: // xTypes=[MUTABLE], yTypes=[POLYREAD], zTypes=[POLYREAD, READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 426: // xTypes=[MUTABLE], yTypes=[POLYREAD], zTypes=[MUTABLE, POLYREAD]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 427: // xTypes=[MUTABLE], yTypes=[POLYREAD], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 428: // xTypes=[MUTABLE], yTypes=[POLYREAD], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 429: // xTypes=[MUTABLE], yTypes=[POLYREAD], zTypes=[POLYREAD]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 430: // xTypes=[MUTABLE], yTypes=[POLYREAD], zTypes=[MUTABLE]
			return false;
		case 432: // xTypes=[MUTABLE], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 433: // xTypes=[MUTABLE], yTypes=[MUTABLE], zTypes=[POLYREAD, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 434: // xTypes=[MUTABLE], yTypes=[MUTABLE], zTypes=[MUTABLE, POLYREAD]
			return false;
		case 435: // xTypes=[MUTABLE], yTypes=[MUTABLE], zTypes=[MUTABLE, READONLY]
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 436: // xTypes=[MUTABLE], yTypes=[MUTABLE], zTypes=[READONLY]
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 437: // xTypes=[MUTABLE], yTypes=[MUTABLE], zTypes=[POLYREAD]
			return false;
		case 438: // xTypes=[MUTABLE], yTypes=[MUTABLE], zTypes=[MUTABLE]
			return false;
		default:
			throw new IllegalArgumentException("Unhandled case: xTypes=" + xTypes.toString() + ", yTypes=" + yTypes.toString() + ", zTypes=" + zTypes.toString());
		}
	}

}
