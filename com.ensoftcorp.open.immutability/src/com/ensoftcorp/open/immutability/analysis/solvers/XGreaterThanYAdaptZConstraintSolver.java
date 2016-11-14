package com.ensoftcorp.open.immutability.analysis.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
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
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		Set<ImmutabilityTypes> zTypes = getTypes(z);
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
		case 0:
			return false;
		case 1:
			return false;
		case 2:
			return false;
		case 3:
			return false;
		case 4:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 5:
			return false;
		case 6:
			return false;
		case 8:
			return false;
		case 9:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 10:
			return false;
		case 11:
			return false;
		case 12:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 13:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 14:
			return false;
		case 16:
			return false;
		case 17:
			return false;
		case 18:
			return false;
		case 19:
			return false;
		case 20:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 21:
			return false;
		case 22:
			return false;
		case 24:
			return false;
		case 25:
			return false;
		case 26:
			return false;
		case 27:
			return false;
		case 28:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 29:
			return false;
		case 30:
			return false;
		case 32:
			return false;
		case 33:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 34:
			return false;
		case 35:
			return false;
		case 36:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 37:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 38:
			return false;
		case 40:
			return false;
		case 41:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 42:
			return false;
		case 43:
			return false;
		case 44:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 45:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 46:
			return false;
		case 48:
			return false;
		case 49:
			return false;
		case 50:
			return false;
		case 51:
			return false;
		case 52:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
		case 53:
			return false;
		case 54:
			return false;
		case 64:
			return false;
		case 65:
			return false;
		case 66:
			return false;
		case 67:
			return false;
		case 68:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 69:
			return false;
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
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 77:
			return false;
		case 78:
			return false;
		case 80:
			return false;
		case 81:
			return false;
		case 82:
			return false;
		case 83:
			return false;
		case 84:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 85:
			return false;
		case 86:
			return false;
		case 88:
			return false;
		case 89:
			return false;
		case 90:
			return false;
		case 91:
			return false;
		case 92:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 93:
			return false;
		case 94:
			return false;
		case 96:
			return false;
		case 97:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 98:
			return false;
		case 99:
			return false;
		case 100:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 101:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 102:
			return false;
		case 104:
			return false;
		case 105:
			return false;
		case 106:
			return false;
		case 107:
			return false;
		case 108:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 109:
			return false;
		case 110:
			return false;
		case 112:
			return false;
		case 113:
			return false;
		case 114:
			return false;
		case 115:
			return false;
		case 116:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 117:
			return false;
		case 118:
			return false;
		case 128:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 129:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 130:
			return false;
		case 131:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 132:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 133:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 134:
			return false;
		case 136:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 137:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 138:
			return false;
		case 139:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 140:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 141:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged;
		case 142:
			return false;
		case 144:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 145:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 146:
			return false;
		case 147:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 148:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 149:
			return false;
		case 150:
			return false;
		case 152:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 153:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 154:
			return false;
		case 155:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 156:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 157:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 158:
			return false;
		case 160:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 161:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 162:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 163:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 164:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 165:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 166:
			return false;
		case 168:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 169:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || zTypesChanged;
		case 170:
			return false;
		case 171:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 172:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 173:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 174:
			return false;
		case 176:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 177:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 178:
			return false;
		case 179:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 180:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 181:
			return false;
		case 182:
			return false;
		case 192:
			return false;
		case 193:
			return false;
		case 194:
			return false;
		case 195:
			return false;
		case 196:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 197:
			return false;
		case 198:
			return false;
		case 200:
			return false;
		case 201:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 202:
			return false;
		case 203:
			return false;
		case 204:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 205:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 206:
			return false;
		case 208:
			return false;
		case 209:
			return false;
		case 210:
			return false;
		case 211:
			return false;
		case 212:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 213:
			return false;
		case 214:
			return false;
		case 216:
			return false;
		case 217:
			return false;
		case 218:
			return false;
		case 219:
			return false;
		case 220:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 221:
			return false;
		case 222:
			return false;
		case 224:
			return false;
		case 225:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 226:
			return false;
		case 227:
			return false;
		case 228:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 229:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 230:
			return false;
		case 232:
			return false;
		case 233:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 234:
			return false;
		case 235:
			return false;
		case 236:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 237:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 238:
			return false;
		case 240:
			return false;
		case 241:
			return false;
		case 242:
			return false;
		case 243:
			return false;
		case 244:
			return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
		case 245:
			return false;
		case 246:
			return false;
		case 256:
			return false;
		case 257:
			return false;
		case 258:
			return false;
		case 259:
			return false;
		case 260:
			return false;
		case 261:
			return false;
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
			return false;
		case 274:
			return false;
		case 275:
			return false;
		case 276:
			return false;
		case 277:
			return false;
		case 278:
			return false;
		case 280:
			return false;
		case 281:
			return false;
		case 282:
			return false;
		case 283:
			return false;
		case 284:
			return false;
		case 285:
			return false;
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
			return false;
		case 305:
			return false;
		case 306:
			return false;
		case 307:
			return false;
		case 308:
			return false;
		case 309:
			return false;
		case 310:
			return false;
		case 320:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 321:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 322:
			return false;
		case 323:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 324:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 325:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 326:
			return false;
		case 328:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 329:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 330:
			return false;
		case 331:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 332:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 333:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 334:
			return false;
		case 336:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 337:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
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
			return false;
		case 342:
			return false;
		case 344:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 345:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 346:
			return false;
		case 347:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 348:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 349:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 350:
			return false;
		case 352:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 353:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 354:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 355:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 356:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 357:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
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
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 369:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 370:
			return false;
		case 371:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 372:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 373:
			return false;
		case 374:
			return false;
		case 384:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 385:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 386:
			return false;
		case 387:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 388:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y,
					EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 389:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 390:
			return false;
		case 392:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 393:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 394:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 395:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 396:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 397:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 398:
			return false;
		case 400:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 401:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 402:
			return false;
		case 403:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 404:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 405:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 406:
			return false;
		case 408:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 409:
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return yTypesChanged || zTypesChanged;
		case 410:
			return false;
		case 411:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 412:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 413:
			return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
		case 414:
			return false;
		case 416:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
		case 417:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 418:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
		case 419:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 420:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 421:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
			return xTypesChanged || yTypesChanged || zTypesChanged;
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
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 433:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 434:
			return false;
		case 435:
			return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
		case 436:
			xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
			zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
			return xTypesChanged || yTypesChanged || zTypesChanged;
		case 437:
			return false;
		case 438:
			return false;
		default:
			throw new IllegalArgumentException("Unhandled case: xTypes=" + xTypes.toString() 
				+ ", yTypes=" + yTypes.toString() 
				+ ", zTypes=" + zTypes.toString());
		}
	}

}
