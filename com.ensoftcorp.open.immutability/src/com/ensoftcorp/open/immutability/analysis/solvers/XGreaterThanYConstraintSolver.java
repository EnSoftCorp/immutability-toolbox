package com.ensoftcorp.open.immutability.analysis.solvers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class XGreaterThanYConstraintSolver {

	// TODO: Remove after profiling is done, use these numbers to fine tune the
	// constraint check orderings
	public static HashMap<Integer, Integer> constraintCounts = new HashMap<Integer, Integer>();

	private static void incrementCounter(Integer check) {
		Integer count = 0;
		if (constraintCounts.containsKey(check)) {
			count = constraintCounts.get(check);
		}
		count++;
		constraintCounts.put(check, count);
	}

	// all possible sets, 3 choose 3, 3 choose 2, and 3 choose 1
	private static final EnumSet<ImmutabilityTypes> SET1 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET2 = EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET3 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET4 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET5 = EnumSet.of(ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET6 = EnumSet.of(ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET7 = EnumSet.of(ImmutabilityTypes.MUTABLE);

	/**
	 * Checks and satisfies x :> y
	 * 
	 * @param xTypes
	 * @param yTypes
	 * @return
	 */
	public static boolean satisify(Node x, Node y) {
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		return satisify(x, xTypes, y, yTypes);
	}

	public static boolean satisify(Node x, Set<ImmutabilityTypes> xTypes, Node y, Set<ImmutabilityTypes> yTypes) {
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(1);
				return false;
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(2);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(3);
				return false;
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(4);
				return false;
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(5);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(6);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(7);
				return false;
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(8);
				return false;
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(9);
				return false;
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(10);
				return false;
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(11);
				return false;
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(12);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(13);
				return false;
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(14);
				return false;
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(15);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(16);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(17);
				return false;
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(18);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(19);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(20);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(21);
				return false;
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(22);
				return false;
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(23);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(24);
				return false;
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(25);
				return false;
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(26);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(27);
				return removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(28);
				return false;
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(29);
				return false;
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(30);
				return false;
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(31);
				return false;
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(32);
				return false;
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(33);
				return false;
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(34);
				return false;
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(35);
				return false;
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(36);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(37);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(38);
				return false;
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(39);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(40);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(41);
				return false;
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(42);
				return false;
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(43);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(44);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(45);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(46);
				return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(47);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(48);
				boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
				boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				return xTypesChanged || yTypesChanged;
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (ImmutabilityPreferences.isConstraintProfilingEnabled())
					incrementCounter(49);
				return false;
			}
		}
		return false;
	}

}
