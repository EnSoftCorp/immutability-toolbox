package com.ensoftcorp.open.immutability.experimental;

import java.util.ArrayList;
import java.util.EnumSet;

import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class TestConstraintEquality {

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
						EnumSet<ImmutabilityTypes> aTypes = sets.get(a-1);
						EnumSet<ImmutabilityTypes> bTypes = sets.get(b-1);
						EnumSet<ImmutabilityTypes> cTypes = sets.get(c-1);
						EnumSet<ImmutabilityTypes> dTypes = sets.get(d-1);
						
						EnumSet<ImmutabilityTypes> a1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> b1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> c1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> d1TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);

						EnumSet<ImmutabilityTypes> a2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> b2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> c2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						EnumSet<ImmutabilityTypes> d2TypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
						
						// approach 1
						// TWRITE: x.f1 = y, qy <: qx adapt qf1
						// = qx adapt qf1 :> qy
						xAdaptYGreaterThanEqualZ(aTypes, bTypes, cTypes, a1TypesToRemove, b1TypesToRemove, c1TypesToRemove);
						// TREAD: x = y.f2, qy adapt qf2 <: qx
						// qx :> qy adapt qf2
						xGreaterThanEqualYAdaptZ(aTypes, cTypes, dTypes, a1TypesToRemove, c1TypesToRemove, d1TypesToRemove);
						
						// approach 2
						// Combined: x.f1 = y.f2, qy adapt qf2 <: qx adapt qf1
						// = qx adapt qf1 :> qy adapt qf2
						aAdaptBGreaterThanEqualCAdaptD(aTypes, bTypes, cTypes, dTypes, a2TypesToRemove, b2TypesToRemove, c2TypesToRemove, d2TypesToRemove);
						
						if(!setEquals(a1TypesToRemove, a2TypesToRemove) 
								|| !setEquals(b1TypesToRemove, b2TypesToRemove) 
								|| !setEquals(c1TypesToRemove, c2TypesToRemove) 
								|| !setEquals(d1TypesToRemove, d2TypesToRemove)){
							System.out.println("\nCase a: " + aTypes.toString() + ", b: " + bTypes.toString() + ", c: " + cTypes.toString() + ", d: " + dTypes.toString());
						}
						
						if(!setEquals(a1TypesToRemove, a2TypesToRemove)){
							System.out.println("a1: " + a1TypesToRemove.toString() + ", a2: " + a2TypesToRemove.toString());
						}
						if(!setEquals(b1TypesToRemove, b2TypesToRemove)){
							System.out.println("b1: " + b1TypesToRemove.toString() + ", b2: " + b2TypesToRemove.toString());
						}
						if(!setEquals(c1TypesToRemove, c2TypesToRemove)){
							System.out.println("c1: " + c1TypesToRemove.toString() + ", c2: " + c2TypesToRemove.toString());
						}
						if(!setEquals(d1TypesToRemove, d2TypesToRemove)){
							System.out.println("d1: " + d1TypesToRemove.toString() + ", d2: " + d2TypesToRemove.toString());
						}
						
					}
				}
			}
		}
	}
	
	private static boolean setEquals(EnumSet<ImmutabilityTypes> aTypes, EnumSet<ImmutabilityTypes> bTypes){
		if(aTypes.size() != bTypes.size()){
			return false;
		} else if(!aTypes.containsAll(bTypes)){
			return false;
		} else if(!bTypes.containsAll(aTypes)){
			return false;
		} else {
			return true;
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
