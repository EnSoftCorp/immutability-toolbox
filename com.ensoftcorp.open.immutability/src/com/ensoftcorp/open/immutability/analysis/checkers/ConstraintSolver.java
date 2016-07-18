package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class ConstraintSolver {

	// TODO: Remove after profiling is done, use these numbers to fine tune the constraint check orderings
	public static HashMap<Integer,Integer> constraintOrder = new HashMap<Integer,Integer>();
	
	private static void incrementCounter(Integer check){
		Integer count = 0;
		if(constraintOrder.containsKey(check)){
			count = constraintOrder.get(check);
		}
		count++;
		constraintOrder.put(check, count);
	}
	
	// all possible sets, 3 choose 3, 3 choose 2, and 3 choose 1
	private static final EnumSet<ImmutabilityTypes> SET1 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET2 = EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET3 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET4 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET5 = EnumSet.of(ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET6 = EnumSet.of(ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET7 = EnumSet.of(ImmutabilityTypes.MUTABLE);

	private static final ArrayList<EnumSet<ImmutabilityTypes>> sets = new ArrayList<EnumSet<ImmutabilityTypes>>();
	
	/**
	 * Checks and satisfies x :> y
	 * 
	 * @param xTypes
	 * @param yTypes
	 * @return
	 */
	public static boolean satisifyXGreaterThanY(Node x, Node y) {
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(1);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(2);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(3);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(4);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(5);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(6);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(7);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(8);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(9);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(10);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(11);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(12);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(13);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(14);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(15);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(16);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(17);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(18);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(19);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y,
						EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(20);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(21);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if(ImmutabilityPreferences.isDebugLoggingEnabled()) incrementCounter(22);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				return xTypesChanged || yTypesChanged;
			}
		}
		return false;
	}

}
