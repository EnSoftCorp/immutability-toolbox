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

public class XAdaptYGreaterThanZConstraintSolver {

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

	public static boolean satisify(Node x, Node y, Node z) {
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> yTypes = getTypes(y);
		Set<ImmutabilityTypes> zTypes = getTypes(z);
		return satisify(x, xTypes, y, yTypes, z, zTypes);
	}

	public static boolean satisify(Node x, Set<ImmutabilityTypes> xTypes, Node y, Set<ImmutabilityTypes> yTypes, Node z, Set<ImmutabilityTypes> zTypes) {
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(1);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(2);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(3);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(4);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(5);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(6);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(7);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(8);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(9);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(10);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(11);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(12);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(13);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(14);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(15);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(16);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(17);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(18);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(19);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(20);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(21);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(22);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(23);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(24);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(25);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(26);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(27);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(28);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(29);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(30);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(31);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(32);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(33);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(34);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(35);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(36);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(37);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(38);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(39);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(40);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(41);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(42);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(43);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(44);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(45);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(46);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(47);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(48);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(49);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(50);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(51);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(52);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(53);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(54);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(55);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(56);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(57);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(58);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(59);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(60);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(61);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(62);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(63);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(64);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(65);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(66);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(67);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(68);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(69);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(70);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(71);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(72);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(73);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(74);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(75);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(76);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(77);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(78);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(79);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(80);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(81);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(82);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(83);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(84);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(85);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(86);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(87);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(88);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(89);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(90);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(91);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(92);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(93);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(94);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(95);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(96);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(97);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(98);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(99);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(100);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(101);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(102);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(103);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(104);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(105);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(106);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(107);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(108);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(109);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(110);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(111);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(112);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(113);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(114);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(115);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(116);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(117);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(118);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(119);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(120);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(121);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(122);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(123);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(124);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(125);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(126);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(127);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(128);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(129);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(130);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(131);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(132);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(133);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(134);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(135);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(136);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(137);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(138);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(139);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(140);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(141);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(142);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(143);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(144);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(145);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(146);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(147);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(148);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(149);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(150);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(151);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(152);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(153);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(154);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(155);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(156);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(157);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(158);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(159);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(160);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(161);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(162);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(163);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(164);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(165);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(166);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(167);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(168);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(169);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(170);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(171);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(172);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(173);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(174);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(175);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(176);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(177);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(178);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(179);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(180);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(181);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(182);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(183);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(184);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(185);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(186);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(187);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(188);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(189);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(190);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(191);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(192);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(193);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(194);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(195);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(196);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(197);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(198);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(199);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(200);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(201);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(202);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(203);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(204);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(205);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(206);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(207);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(208);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(209);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(210);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(211);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(212);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(213);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(214);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(215);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(216);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(217);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(218);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(219);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(220);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(221);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(222);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(223);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(224);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(225);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(226);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(227);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(228);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(229);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(230);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(231);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(232);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(233);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(234);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(235);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(236);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(237);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(238);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(239);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(240);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(241);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(242);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(243);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(244);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(245);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(246);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(247);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(248);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(249);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(250);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(251);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(252);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(253);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(254);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(255);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(256);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(257);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(258);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(259);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(260);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(261);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(262);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(263);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(264);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(265);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(266);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(267);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(268);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(269);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(270);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(271);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(272);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(273);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(274);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(275);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(276);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(277);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(278);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(279);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(280);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(281);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(282);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(283);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(284);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(285);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(286);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(287);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(288);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(289);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(290);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(291);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(292);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(293);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(294);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(295);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(296);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(297);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(298);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(299);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(300);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(301);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(302);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(303);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(304);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(305);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(306);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(307);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(308);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(309);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(310);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(311);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(312);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(313);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(314);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(315);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(316);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(317);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(318);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(319);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(320);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(321);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(322);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(323);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(324);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(325);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(326);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(327);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(328);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(329);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(330);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(331);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(332);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(333);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(334);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(335);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(336);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(337);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(338);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(339);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(340);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(341);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.READONLY));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(342);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(343);
					return false;
				}
			}
		}
		return false;
	}

}
