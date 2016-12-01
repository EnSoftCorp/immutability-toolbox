package com.ensoftcorp.open.immutability.experimental;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class AFieldAdaptBGreaterThanEqualCFieldAdaptDConstraintSolverGenerator {

	// all possible sets, 3 choose 3, 3 choose 2, and 3 choose 1
	private static final EnumSet<ImmutabilityTypes> SET1 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET2 = EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET3 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET4 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET5 = EnumSet.of(ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET6 = EnumSet.of(ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET7 = EnumSet.of(ImmutabilityTypes.MUTABLE);

	private static final ArrayList<EnumSet<ImmutabilityTypes>> sets = new ArrayList<EnumSet<ImmutabilityTypes>>();
	
	private static void println(FileWriter output, String str) throws IOException{
		output.write(str + "\n");
		output.flush();
	}
	
	public static void main(String[] args) throws IOException{
		
		File file = new File("C:\\Users\\Ben\\Desktop\\gen.txt");
		FileWriter output = new FileWriter(file);
		
		sets.add(SET1);
		sets.add(SET2);
		sets.add(SET3);
		sets.add(SET4);
		sets.add(SET5);
		sets.add(SET6);
		sets.add(SET7);
		
		int[] allSets = new int[]{1,2,3,4,5,6,7};
		
		println(output, "boolean aTypesChanged = false;");
		println(output, "boolean bTypesChanged = false;");
		println(output, "boolean cTypesChanged = false;");
		println(output, "boolean dTypesChanged = false;");
		
		println(output, "short input = getCase(aTypes, bTypes, cTypes, dTypes);");
		
		println(output, "switch (input) {");

		for(int a : allSets){
			for(int b : allSets){
				for(int c : allSets){
					for(int d : allSets){
						// for each reference there are 7 possible sets (3 choose 3 + 3 choose 2 + 3 choose 1)
						// to represent 7 cases we need 3 bits per reference
						// for 3 references a,b,c,d we need 12 bits, so a short is needed
						// format: 0000aaabbbcccddd
						// in total there are 1*7*7*7*7=2401 possible 4 reference set inputs

						EnumSet<ImmutabilityTypes> aTypes = sets.get(a-1);
						EnumSet<ImmutabilityTypes> bTypes = sets.get(b-1);
						EnumSet<ImmutabilityTypes> cTypes = sets.get(c-1);
						EnumSet<ImmutabilityTypes> dTypes = sets.get(d-1);

						short input = getCase(aTypes, bTypes, cTypes, dTypes);
						
						String result = getResult(sets.get(a-1), sets.get(b-1), sets.get(c-1), sets.get(d-1));
						if(!result.equals("")){
							println(output, "case " + input + ":" + " // aTypes=" + aTypes.toString() + ", bTypes=" + bTypes.toString() + ", cTypes=" + cTypes.toString() + ", dTypes=" + dTypes.toString());
							println(output, result);
						} else {
							throw new RuntimeException("empty result");
						}
					}
				}
			}
		}
		println(output, "default:");
		println(output, "throw new IllegalArgumentException(\"AAdaptBGreaterThanCAdaptDConstraintSolver Unhandled Case: aTypes=\" + aTypes.toString() + \", bTypes=\" + bTypes.toString() + \", cTypes=\" + cTypes.toString() + \", dTypes=\" + dTypes.toString());");
		println(output, "}");
	}
	
	private static short getCase(EnumSet<ImmutabilityTypes> aTypes, EnumSet<ImmutabilityTypes> bTypes, EnumSet<ImmutabilityTypes> cTypes, EnumSet<ImmutabilityTypes> dTypes) {
		short input = 0;
		
		int setID = 0;
		for(EnumSet<ImmutabilityTypes> set : sets){
			if(aTypes.equals(set)){
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;
		
		setID = 0;
		for(EnumSet<ImmutabilityTypes> set : sets){
			if(bTypes.equals(set)){
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;
		
		setID = 0;
		for(EnumSet<ImmutabilityTypes> set : sets){
			if(cTypes.equals(set)){
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;
		
		setID = 0;
		for(EnumSet<ImmutabilityTypes> set : sets){
			if(dTypes.equals(set)){
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		
		return input;
	}
	
	private static String getResult(EnumSet<ImmutabilityTypes> aTypes, EnumSet<ImmutabilityTypes> bTypes, EnumSet<ImmutabilityTypes> cTypes, EnumSet<ImmutabilityTypes> dTypes) {
		// process s(a)
		Set<ImmutabilityTypes> aTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
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
		Set<ImmutabilityTypes> bTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
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
		Set<ImmutabilityTypes> cTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
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
		Set<ImmutabilityTypes> dTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
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
		
		// all 4 could be empty
		if(aTypesToRemove.isEmpty() && bTypesToRemove.isEmpty() && cTypesToRemove.isEmpty() && dTypesToRemove.isEmpty()){
			return "return false;";
		} else {
			// 3 of 4 could be empty (3 choose 4 ways = 4 ways)
			// not d
			if(aTypesToRemove.isEmpty() && bTypesToRemove.isEmpty() && cTypesToRemove.isEmpty()){
				return "return removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));";
			}
			// not c
			else if(aTypesToRemove.isEmpty() && bTypesToRemove.isEmpty() && dTypesToRemove.isEmpty()){
				return "return removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));";
			} 
			// not b
			else if(aTypesToRemove.isEmpty() && cTypesToRemove.isEmpty() && dTypesToRemove.isEmpty()){
				return "return removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));";
			} 
			// not a
			else if(bTypesToRemove.isEmpty() && cTypesToRemove.isEmpty() && dTypesToRemove.isEmpty()){
				return "return removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));";
			} else {
				// 2 of 4 could be empty (2 choose 4 = 6 ways)
				// not a,b
				if(aTypesToRemove.isEmpty() && bTypesToRemove.isEmpty()){
					String result = "";
					result += "cTypesChanged = removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));\n";
					result += "dTypesChanged = removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));\n";
					result += "return cTypesChanged || dTypesChanged;";
					return result;
				}
				// not a,c
				else if(aTypesToRemove.isEmpty() && cTypesToRemove.isEmpty()){
					String result = "";
					result += "bTypesChanged = removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));\n";
					result += "dTypesChanged = removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));\n";
					result += "return bTypesChanged || dTypesChanged;";
					return result;
				}
				// not a,d
				else if(aTypesToRemove.isEmpty() && dTypesToRemove.isEmpty()){
					String result = "";
					result += "bTypesChanged = removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));\n";
					result += "cTypesChanged = removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));\n";
					result += "return bTypesChanged || cTypesChanged;";
					return result;
				}
				// not b,c
				else if(bTypesToRemove.isEmpty() && cTypesToRemove.isEmpty()){
					String result = "";
					result += "aTypesChanged = removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));\n";
					result += "dTypesChanged = removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));\n";
					result += "return aTypesChanged || dTypesChanged;";
					return result;
				}
				// not b,d
				else if(bTypesToRemove.isEmpty() && dTypesToRemove.isEmpty()){
					String result = "";
					result += "aTypesChanged = removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));\n";
					result += "cTypesChanged = removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));\n";
					result += "return aTypesChanged || cTypesChanged;";
					return result;
				}
				// not c,d
				else if(cTypesToRemove.isEmpty() && dTypesToRemove.isEmpty()){
					String result = "";
					result += "aTypesChanged = removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));\n";
					result += "bTypesChanged = removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));\n";
					result += "return aTypesChanged || bTypesChanged;";
					return result;
				} else {
					// 1 of 4 could be empty
					if(aTypesToRemove.isEmpty()){
						String result = "";
						result += "bTypesChanged = removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));\n";
						result += "cTypesChanged = removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));\n";
						result += "dTypesChanged = removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));\n";
						result += "return bTypesChanged || cTypesChanged || dTypesChanged;";
						return result;
					} else if(bTypesToRemove.isEmpty()){
						String result = "";
						result += "aTypesChanged = removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));\n";
						result += "cTypesChanged = removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));\n";
						result += "dTypesChanged = removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));\n";
						result += "return aTypesChanged || cTypesChanged || dTypesChanged;";
						return result;
					} else if(cTypesToRemove.isEmpty()){
						String result = "";
						result += "aTypesChanged = removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));\n";
						result += "bTypesChanged = removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));\n";
						result += "dTypesChanged = removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));\n";
						result += "return aTypesChanged || bTypesChanged || dTypesChanged;";
						return result;
					} else if(dTypesToRemove.isEmpty()){
						String result = "";
						result += "aTypesChanged = removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));\n";
						result += "bTypesChanged = removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));\n";
						result += "cTypesChanged = removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));\n";
						result += "return aTypesChanged || bTypesChanged || cTypesChanged;";
						return result;
					} else {
						// none are empty
						String result = "";
						result += "aTypesChanged = removeTypes(a, EnumSet.of(" + getSetString(aTypesToRemove) + "));\n";
						result += "bTypesChanged = removeTypes(b, EnumSet.of(" + getSetString(bTypesToRemove) + "));\n";
						result += "cTypesChanged = removeTypes(c, EnumSet.of(" + getSetString(cTypesToRemove) + "));\n";
						result += "dTypesChanged = removeTypes(d, EnumSet.of(" + getSetString(dTypesToRemove) + "));\n";
						result += "return aTypesChanged || bTypesChanged || cTypesChanged || dTypesChanged;";
						return result;
					}
				}
			}
		}
	}
	
	private static String getSetString(Set<ImmutabilityTypes> typesToRemove){
		String result = "";
		String prefix = "";
		for(ImmutabilityTypes type : typesToRemove){
			result += prefix + "ImmutabilityTypes." + type.toString();
			prefix = ", ";
		}
		return result;
	}
	
}
