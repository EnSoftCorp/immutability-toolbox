package com.ensoftcorp.open.immutability.analysis.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class XAdaptYGreaterThanZConstraintSolver {

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
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		Set<ImmutabilityTypes> zTypes = getTypes(z);
		return satisify(x, xTypes, y, yTypes, z, zTypes);
	}

	private static short getCase(Set<ImmutabilityTypes> xTypes, Set<ImmutabilityTypes> yTypes,
			Set<ImmutabilityTypes> zTypes) {
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
		case 0:
			return false;
		case 1:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 2:
			return false;
		case 3:
			return false;
		case 4:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 5:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 6:
			return false;
		case 8:
			return false;
		case 9:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 10:
			return false;
		case 11:
			return false;
		case 12:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 13:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 14:
			return false;
		case 16:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 17:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 18:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 19:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 20:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 21:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 22:
			return false;
		case 24:
			return false;
		case 25:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 26:
			return false;
		case 27:
			return false;
		case 28:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 29:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 30:
			return false;
		case 32:
			return false;
		case 33:
			return false;
		case 34:
			return false;
		case 35:
			return false;
		case 36:
			return false;
		case 37:
			return false;
		case 38:
			return false;
		case 40:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 41:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 42:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 43:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 44:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 45:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 46:
			return false;
		case 48:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 49:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 50:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 51:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 52:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 53:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 54:
			return false;
		case 64:
			return false;
		case 65:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 66:
			return false;
		case 67:
			return false;
		case 68:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 69:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 70:
			return false;
		case 72:
			return false;
		case 73:
			return false;
		case 74:
			return false;
		case 75:
			return false;
		case 76:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 77:
			return false;
		case 78:
			return false;
		case 80:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 81:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 82:
			return false;
		case 83:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 84:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 85:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 86:
			return false;
		case 88:
			return false;
		case 89:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 90:
			return false;
		case 91:
			return false;
		case 92:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 93:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 94:
			return false;
		case 96:
			return false;
		case 97:
			return false;
		case 98:
			return false;
		case 99:
			return false;
		case 100:
			return false;
		case 101:
			return false;
		case 102:
			return false;
		case 104:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 105:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 106:
			return false;
		case 107:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 108:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 109:
			return false;
		case 110:
			return false;
		case 112:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 113:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 114:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 115:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 116:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 117:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 118:
			return false;
		case 128:
			return false;
		case 129:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 130:
			return false;
		case 131:
			return false;
		case 132:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 133:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 134:
			return false;
		case 136:
			return false;
		case 137:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 138:
			return false;
		case 139:
			return false;
		case 140:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 141:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 142:
			return false;
		case 144:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 145:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 146:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 147:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 148:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 149:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 150:
			return false;
		case 152:
			return false;
		case 153:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 154:
			return false;
		case 155:
			return false;
		case 156:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 157:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 158:
			return false;
		case 160:
			return false;
		case 161:
			return false;
		case 162:
			return false;
		case 163:
			return false;
		case 164:
			return false;
		case 165:
			return false;
		case 166:
			return false;
		case 168:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 169:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 170:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 171:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 172:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 173:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 174:
			return false;
		case 176:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 177:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 178:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 179:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 180:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 181:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 182:
			return false;
		case 192:
			return false;
		case 193:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 194:
			return false;
		case 195:
			return false;
		case 196:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 197:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 198:
			return false;
		case 200:
			return false;
		case 201:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 202:
			return false;
		case 203:
			return false;
		case 204:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 205:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 206:
			return false;
		case 208:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 209:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 210:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 211:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 212:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 213:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 214:
			return false;
		case 216:
			return false;
		case 217:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 218:
			return false;
		case 219:
			return false;
		case 220:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 221:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 222:
			return false;
		case 224:
			return false;
		case 225:
			return false;
		case 226:
			return false;
		case 227:
			return false;
		case 228:
			return false;
		case 229:
			return false;
		case 230:
			return false;
		case 232:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 233:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 234:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 235:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 236:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 237:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 238:
			return false;
		case 240:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 241:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 242:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 243:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 244:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 245:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 246:
			return false;
		case 256:
			return false;
		case 257:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 258:
			return false;
		case 259:
			return false;
		case 260:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 261:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 262:
			return false;
		case 264:
			return false;
		case 265:
			return false;
		case 266:
			return false;
		case 267:
			return false;
		case 268:
			return false;
		case 269:
			return false;
		case 270:
			return false;
		case 272:
			return false;
		case 273:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 274:
			return false;
		case 275:
			return false;
		case 276:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 277:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 278:
			return false;
		case 280:
			return false;
		case 281:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 282:
			return false;
		case 283:
			return false;
		case 284:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 285:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 286:
			return false;
		case 288:
			return false;
		case 289:
			return false;
		case 290:
			return false;
		case 291:
			return false;
		case 292:
			return false;
		case 293:
			return false;
		case 294:
			return false;
		case 296:
			return false;
		case 297:
			return false;
		case 298:
			return false;
		case 299:
			return false;
		case 300:
			return false;
		case 301:
			return false;
		case 302:
			return false;
		case 304:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 305:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 306:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 307:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 308:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 309:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 310:
			return false;
		case 320:
			return false;
		case 321:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 322:
			return false;
		case 323:
			return false;
		case 324:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 325:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 326:
			return false;
		case 328:
			return false;
		case 329:
			return false;
		case 330:
			return false;
		case 331:
			return false;
		case 332:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 333:
			return false;
		case 334:
			return false;
		case 336:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 337:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 338:
			return false;
		case 339:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 340:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 341:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 342:
			return false;
		case 344:
			return false;
		case 345:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 346:
			return false;
		case 347:
			return false;
		case 348:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 349:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 350:
			return false;
		case 352:
			return false;
		case 353:
			return false;
		case 354:
			return false;
		case 355:
			return false;
		case 356:
			return false;
		case 357:
			return false;
		case 358:
			return false;
		case 360:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 361:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 362:
			return false;
		case 363:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 364:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 365:
			return false;
		case 366:
			return false;
		case 368:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 369:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 370:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 371:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 372:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 373:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 374:
			return false;
		case 384:
			return false;
		case 385:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 386:
			return false;
		case 387:
			return false;
		case 388:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 389:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 390:
			return false;
		case 392:
			return false;
		case 393:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 394:
			return false;
		case 395:
			return false;
		case 396:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 397:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged;
		case 398:
			return false;
		case 400:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 401:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 402:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 403:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 404:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 405:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 406:
			return false;
		case 408:
			return false;
		case 409:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 410:
			return false;
		case 411:
			return false;
		case 412:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 413:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			return xTypesChanged || yTypesChanged;
		case 414:
			return false;
		case 416:
			return false;
		case 417:
			return false;
		case 418:
			return false;
		case 419:
			return false;
		case 420:
			return false;
		case 421:
			return false;
		case 422:
			return false;
		case 424:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 425:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 426:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 427:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 428:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 429:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 430:
			return false;
		case 432:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 433:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 434:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 435:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 436:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 437:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 438:
			return false;
		default:
			throw new IllegalArgumentException("Unhandled case: xTypes=" 
					+ xTypes.toString() + ", yTypes="
					+ yTypes.toString() + ", zTypes=" + zTypes.toString());
		}
	}
}
