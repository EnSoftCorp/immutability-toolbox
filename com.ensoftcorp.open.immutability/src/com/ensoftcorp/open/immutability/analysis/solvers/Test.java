package com.ensoftcorp.open.immutability.analysis.solvers;

import java.util.ArrayList;
import java.util.EnumSet;

import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class Test {

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
	
	public static void main(String[] args) {
		
		int[] allSets = new int[]{1,2,3,4,5,6,7};
		for(int a : allSets){
			for(int b : allSets){
				for(int c : allSets){
					for(int d : allSets){
						// a.b = c.d
						// x.f1 = y.f2
						// a=x, b=f1, c=y, d=f2
						EnumSet<ImmutabilityTypes> a1Types = sets.get(a-1);
						EnumSet<ImmutabilityTypes> b1Types = sets.get(b-1);
						EnumSet<ImmutabilityTypes> c1Types = sets.get(c-1);
						EnumSet<ImmutabilityTypes> d1Types = sets.get(d-1);
						EnumSet<ImmutabilityTypes> a1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> b1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> c1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> d1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						
						EnumSet<ImmutabilityTypes> a2Types = sets.get(a-1);
						EnumSet<ImmutabilityTypes> b2Types = sets.get(b-1);
						EnumSet<ImmutabilityTypes> c2Types = sets.get(c-1);
						EnumSet<ImmutabilityTypes> d2Types = sets.get(d-1);
						EnumSet<ImmutabilityTypes> a2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> b2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> c2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> d2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						
						// approach 1
						// TWRITE: x.f1 = y, qy <: qx adapt qf1
						// = qx adapt qf1 :> qy
						xAdaptYGreaterThanEqualZ(a1Types, b1Types, c1Types, a1TypesToRemove, b1TypesToRemove, c1TypesToRemove);
						// TREAD: x = y.f2, qy adapt qf2 <: qx
						// qx :> qy adapt qf2
						xGreaterThanEqualYAdaptZ(a1Types, c1Types, d1Types, a1TypesToRemove, c1TypesToRemove, d1TypesToRemove);
						
						// approach 2
						// Combined: x.f1 = y.f2, qy adapt qf2 <: qx adapt qf1
						// = qx adapt qf1 :> qy adapt qf2
						aAdaptBGreaterThanEqualCAdaptD(a2Types, b2Types, c2Types, d2Types, a2TypesToRemove, b2TypesToRemove, c2TypesToRemove, d2TypesToRemove);
						
						boolean changed = false;
						changed |= a1TypesToRemove.retainAll(a2TypesToRemove);
						changed |= a2TypesToRemove.retainAll(a1TypesToRemove);
						
						changed |= b1TypesToRemove.retainAll(b2TypesToRemove);
						changed |= b2TypesToRemove.retainAll(b1TypesToRemove);
						
						changed |= c1TypesToRemove.retainAll(c2TypesToRemove);
						changed |= c2TypesToRemove.retainAll(c1TypesToRemove);
						
						changed |= d1TypesToRemove.retainAll(d2TypesToRemove);
						changed |= d2TypesToRemove.retainAll(d1TypesToRemove);
						
						if(changed){
							System.out.println("changed");
						}
					}
				}
			}
		}
	}
	
	private static void xGreaterThanEqualYAdaptZ(EnumSet<ImmutabilityTypes> xTypes, 
												 EnumSet<ImmutabilityTypes> yTypes, 
												 EnumSet<ImmutabilityTypes> zTypes, 
												 EnumSet<ImmutabilityTypes> xTypesToRemove, 
												 EnumSet<ImmutabilityTypes> yTypesToRemove, 
												 EnumSet<ImmutabilityTypes> zTypesToRemove){
		// process s(x)
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				for(ImmutabilityTypes zType : zTypes){
					ImmutabilityTypes yAdaptedZ = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, zType);
					if(xType.compareTo(yAdaptedZ) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		
		// process s(y)
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes zType : zTypes){
					ImmutabilityTypes yAdaptedZ = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, zType);
					if(xType.compareTo(yAdaptedZ) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		
		// process s(z)
		for(ImmutabilityTypes zType : zTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes yType : yTypes){
					ImmutabilityTypes yAdaptedZ = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, zType);
					if(xType.compareTo(yAdaptedZ) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				zTypesToRemove.add(zType);
			}
		}
	}
	
	private static void xAdaptYGreaterThanEqualZ(EnumSet<ImmutabilityTypes> xTypes, 
												 EnumSet<ImmutabilityTypes> yTypes, 
												 EnumSet<ImmutabilityTypes> zTypes, 
												 EnumSet<ImmutabilityTypes> xTypesToRemove, 
												 EnumSet<ImmutabilityTypes> yTypesToRemove, 
												 EnumSet<ImmutabilityTypes> zTypesToRemove){
		// process s(x)
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				for(ImmutabilityTypes zType : zTypes){
					ImmutabilityTypes xAdaptedY = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, yType);
					if(xAdaptedY.compareTo(zType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}	
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		
		// process s(y)
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes zType : zTypes){
					ImmutabilityTypes xAdaptedY = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, yType);
					if(xAdaptedY.compareTo(zType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}

		// process s(z)
		for(ImmutabilityTypes zType : zTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes yType : yTypes){
					ImmutabilityTypes xAdaptedY = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, yType);
					if(xAdaptedY.compareTo(zType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				zTypesToRemove.add(zType);
			}
		}
	}
	
	private static void aAdaptBGreaterThanEqualCAdaptD(EnumSet<ImmutabilityTypes> aTypes, 
								EnumSet<ImmutabilityTypes> bTypes, 
								EnumSet<ImmutabilityTypes> cTypes, 
								EnumSet<ImmutabilityTypes> dTypes,
								EnumSet<ImmutabilityTypes> aTypesToRemove, 
								EnumSet<ImmutabilityTypes> bTypesToRemove, 
								EnumSet<ImmutabilityTypes> cTypesToRemove, 
								EnumSet<ImmutabilityTypes> dTypesToRemove){
		
		// process s(a)
		for(ImmutabilityTypes aType : aTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes bType : bTypes){
				for(ImmutabilityTypes cType : cTypes){
					for(ImmutabilityTypes dType : dTypes){
						ImmutabilityTypes aAdaptedB = ImmutabilityTypes.getAdaptedFieldViewpoint(aType, bType);
						ImmutabilityTypes cAdaptedD = ImmutabilityTypes.getAdaptedFieldViewpoint(cType, dType);
						if(aAdaptedB.compareTo(cAdaptedD) >= 0){
							isSatisfied = true;
							break satisfied;
						}
					}
				}
			}
			if(!isSatisfied){
				aTypesToRemove.add(aType);
			}
		}
		
		// process s(b)
		for(ImmutabilityTypes bType : bTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes aType : aTypes){
				for(ImmutabilityTypes cType : cTypes){
					for(ImmutabilityTypes dType : dTypes){
						ImmutabilityTypes aAdaptedB = ImmutabilityTypes.getAdaptedFieldViewpoint(aType, bType);
						ImmutabilityTypes cAdaptedD = ImmutabilityTypes.getAdaptedFieldViewpoint(cType, dType);
						if(aAdaptedB.compareTo(cAdaptedD) >= 0){
							isSatisfied = true;
							break satisfied;
						}
					}
				}
			}
			if(!isSatisfied){
				bTypesToRemove.add(bType);
			}
		}
		
		// process s(c)
		for(ImmutabilityTypes cType : cTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes bType : bTypes){
				for(ImmutabilityTypes aType : aTypes){
					for(ImmutabilityTypes dType : dTypes){
						ImmutabilityTypes aAdaptedB = ImmutabilityTypes.getAdaptedFieldViewpoint(aType, bType);
						ImmutabilityTypes cAdaptedD = ImmutabilityTypes.getAdaptedFieldViewpoint(cType, dType);
						if(aAdaptedB.compareTo(cAdaptedD) >= 0){
							isSatisfied = true;
							break satisfied;
						}
					}
				}
			}
			if(!isSatisfied){
				cTypesToRemove.add(cType);
			}
		}
		
		// process s(d)
		for(ImmutabilityTypes dType : dTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes bType : bTypes){
				for(ImmutabilityTypes cType : cTypes){
					for(ImmutabilityTypes aType : aTypes){
						ImmutabilityTypes aAdaptedB = ImmutabilityTypes.getAdaptedFieldViewpoint(aType, bType);
						ImmutabilityTypes cAdaptedD = ImmutabilityTypes.getAdaptedFieldViewpoint(cType, dType);
						if(aAdaptedB.compareTo(cAdaptedD) >= 0){
							isSatisfied = true;
							break satisfied;
						}
					}
				}
			}
			if(!isSatisfied){
				dTypesToRemove.add(dType);
			}
		}
	}

}
