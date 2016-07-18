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

public class XAdaptZGreaterThanYConstraintSolver {

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
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(3);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(6);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(7);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(8);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(9);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(10);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(11);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(12);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(13);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(14);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(15);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(16);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(17);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(18);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(19);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(20);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(21);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(24);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(27);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(28);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(29);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(30);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(31);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(32);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(35);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(36);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(37);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(38);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(39);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(40);
					return false;
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(43);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(44);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(45);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(46);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(47);
					return false;
				}
			}
		}
		if (xTypes.equals(SET1)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(48);
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(52);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(55);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(56);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(57);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(60);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(61);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(62);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(63);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(64);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(65);
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(68);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(69);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(70);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(73);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(76);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(77);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(78);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(79);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(80);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(81);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(84);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(85);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(86);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(87);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(88);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(89);
					return false;
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(92);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(93);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(94);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(95);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(96);
					return false;
				}
			}
		}
		if (xTypes.equals(SET2)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(97);
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(101);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(104);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(105);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(106);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(107);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(108);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(109);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(110);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(111);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(112);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(113);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(114);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(115);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(116);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(117);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(118);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(119);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(122);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(125);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(126);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(127);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(128);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(129);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(130);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(133);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(134);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(135);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(136);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(137);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(138);
					return false;
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(141);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(142);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(143);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(144);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(145);
					return false;
				}
			}
		}
		if (xTypes.equals(SET3)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(146);
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(150);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(153);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(154);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(155);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(156);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(157);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(158);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(159);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(160);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(161);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(162);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(163);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(164);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(165);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(166);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(167);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(168);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(171);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(174);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(175);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(176);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(177);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(178);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(179);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(182);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(183);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(184);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(185);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(186);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(187);
					return false;
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(190);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(191);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(192);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(193);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(194);
					return false;
				}
			}
		}
		if (xTypes.equals(SET4)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(195);
					return false;
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
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(202);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(203);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(204);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(207);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
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
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(216);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(217);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
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
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(223);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(224);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(225);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(228);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(232);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(235);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(239);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(240);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(241);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(242);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(243);
					return false;
				}
			}
		}
		if (xTypes.equals(SET5)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(244);
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(248);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(251);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(252);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(253);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(256);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(257);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(258);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(259);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(260);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(261);
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(264);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(265);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(266);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(269);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(272);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(273);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(274);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(275);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(276);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(277);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(280);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(281);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(282);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(283);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(284);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(285);
					return false;
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(288);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(289);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(290);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(291);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(292);
					return false;
				}
			}
		}
		if (xTypes.equals(SET6)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(293);
					return false;
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(297);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(300);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET1)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(301);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(302);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(303);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(304);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(305);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(306);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(307);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET2)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(308);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(309);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(310);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(311);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(312);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(313);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(314);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET3)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(315);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(318);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
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
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(321);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET4)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(322);
					return removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(323);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(324);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(325);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(326);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET5)) {
				if (zTypes.equals(SET7)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(329);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.READONLY));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(330);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(331);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.POLYREAD));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(332);
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(333);
					return removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET6)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(334);
					return false;
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
					boolean xTypesChanged = removeTypes(x, EnumSet.of(ImmutabilityTypes.MUTABLE));
					boolean yTypesChanged = removeTypes(y, EnumSet.of(ImmutabilityTypes.POLYREAD));
					boolean zTypesChanged = removeTypes(z, EnumSet.of(ImmutabilityTypes.MUTABLE));
					return xTypesChanged || yTypesChanged || zTypesChanged;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET1)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(337);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET2)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(338);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET3)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(339);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET4)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(340);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET5)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(341);
					return false;
				}
			}
		}
		if (xTypes.equals(SET7)) {
			if (yTypes.equals(SET7)) {
				if (zTypes.equals(SET6)) {
					if (ImmutabilityPreferences.isConstraintProfilingEnabled())
						incrementCounter(342);
					return false;
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
