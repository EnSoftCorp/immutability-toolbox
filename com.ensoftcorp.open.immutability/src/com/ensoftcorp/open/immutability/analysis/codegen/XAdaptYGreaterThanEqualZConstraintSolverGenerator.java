package com.ensoftcorp.open.immutability.analysis.codegen;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;

public class XAdaptYGreaterThanEqualZConstraintSolverGenerator {

	// all possible sets, 3 choose 3, 3 choose 2, and 3 choose 1
	private static final EnumSet<ImmutabilityTypes> SET1 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET2 = EnumSet.of(ImmutabilityTypes.POLYREAD, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET3 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET4 = EnumSet.of(ImmutabilityTypes.MUTABLE, ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET5 = EnumSet.of(ImmutabilityTypes.READONLY);
	private static final EnumSet<ImmutabilityTypes> SET6 = EnumSet.of(ImmutabilityTypes.POLYREAD);
	private static final EnumSet<ImmutabilityTypes> SET7 = EnumSet.of(ImmutabilityTypes.MUTABLE);

	private static final ArrayList<EnumSet<ImmutabilityTypes>> sets = new ArrayList<EnumSet<ImmutabilityTypes>>();
	
	public static void main(String[] args){
		sets.add(SET1);
		sets.add(SET2);
		sets.add(SET3);
		sets.add(SET4);
		sets.add(SET5);
		sets.add(SET6);
		sets.add(SET7);
		
		int[] allSets = new int[]{1,2,3,4,5,6,7};
		
		System.out.println("boolean xTypesChanged = false;");
		System.out.println("boolean yTypesChanged = false;");
		System.out.println("boolean zTypesChanged = false;");
		
		System.out.println("short input = getCase(xTypes, yTypes, zTypes);");
		
		System.out.println("switch (input) {");

		for(int x : allSets){
			for(int y : allSets){
				for(int z : allSets){
					
					// for each reference there are 7 possible sets (3 choose 3 + 3 choose 2 + 3 choose 1)
					// to represent 7 cases we need 3 bits per reference
					// for 3 references x,y,z we need 9 bits, so a short is needed
					// format: 0000000xxxyyyzzz
					// in total there are 1*7*7*7=343 possible 3 reference set inputs

					EnumSet<ImmutabilityTypes> xTypes = sets.get(x-1);
					EnumSet<ImmutabilityTypes> yTypes = sets.get(y-1);
					EnumSet<ImmutabilityTypes> zTypes = sets.get(z-1);

					short input = getCase(xTypes, yTypes, zTypes);
					
					String result = getResult(sets.get(x-1), sets.get(y-1), sets.get(z-1));
					if(!result.equals("")){
						System.out.println("case " + input + ":" + " // xTypes=" + xTypes.toString() + ", yTypes=" + yTypes.toString() + ", zTypes=" + zTypes.toString());
						System.out.println(result);
					} else {
						throw new RuntimeException("empty result");
					}
				}
			}
		}
		System.out.println("default:");
		System.out.println("throw new IllegalArgumentException(\"Unhandled case: xTypes=\" + xTypes.toString() + \", yTypes=\" + yTypes.toString() + \", zTypes=\" + zTypes.toString());");
		System.out.println("}");
	}
	
	private static short getCase(EnumSet<ImmutabilityTypes> xTypes, EnumSet<ImmutabilityTypes> yTypes, EnumSet<ImmutabilityTypes> zTypes) {
		short input = 0;
		
		int setID = 0;
		for(EnumSet<ImmutabilityTypes> set : sets){
			if(xTypes.equals(set)){
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;
		
		setID = 0;
		for(EnumSet<ImmutabilityTypes> set : sets){
			if(yTypes.equals(set)){
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		input <<= 3;
		
		setID = 0;
		for(EnumSet<ImmutabilityTypes> set : sets){
			if(zTypes.equals(set)){
				break;
			} else {
				setID++;
			}
		}
		input |= setID;
		
		return input;
	}
	
	private static String getResult(EnumSet<ImmutabilityTypes> xTypes, EnumSet<ImmutabilityTypes> yTypes, EnumSet<ImmutabilityTypes> zTypes) {
		// process s(x)
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
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
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
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
		Set<ImmutabilityTypes> zTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
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
		
		if(xTypesToRemove.isEmpty() && yTypesToRemove.isEmpty() && zTypesToRemove.isEmpty()){
			return "return false;";
		} else {
			if(xTypesToRemove.isEmpty() && yTypesToRemove.isEmpty()){
				return "return removeTypes(z, EnumSet.of(" + getSetString(zTypesToRemove) + "));";
			} else if(xTypesToRemove.isEmpty() && zTypesToRemove.isEmpty()){
				return "return removeTypes(y, EnumSet.of(" + getSetString(yTypesToRemove) + "));";
			} else if(yTypesToRemove.isEmpty() && zTypesToRemove.isEmpty()){
				return "return removeTypes(x, EnumSet.of(" + getSetString(xTypesToRemove) + "));";
			} else {
				if(xTypesToRemove.isEmpty()){
					String result = "";
					result += "yTypesChanged = removeTypes(y, EnumSet.of(" + getSetString(yTypesToRemove) + "));\n";
					result += "zTypesChanged = removeTypes(z, EnumSet.of(" + getSetString(zTypesToRemove) + "));\n";
					result += "return yTypesChanged || zTypesChanged;";
					return result;
				} else if(yTypesToRemove.isEmpty()){
					String result = "";
					result += "xTypesChanged = removeTypes(x, EnumSet.of(" + getSetString(xTypesToRemove) + "));\n";
					result += "zTypesChanged = removeTypes(z, EnumSet.of(" + getSetString(zTypesToRemove) + "));\n";
					result += "return xTypesChanged || zTypesChanged;";
					return result;
				} else if(zTypesToRemove.isEmpty()){
					String result = "";
					result += "xTypesChanged = removeTypes(x, EnumSet.of(" + getSetString(xTypesToRemove) + "));\n";
					result += "yTypesChanged = removeTypes(y, EnumSet.of(" + getSetString(yTypesToRemove) + "));\n";
					result += "return xTypesChanged || yTypesChanged;";
					return result;
				} else {
					String result = "";
					result += "xTypesChanged = removeTypes(x, EnumSet.of(" + getSetString(xTypesToRemove) + "));\n";
					result += "yTypesChanged = removeTypes(y, EnumSet.of(" + getSetString(yTypesToRemove) + "));\n";
					result += "zTypesChanged = removeTypes(z, EnumSet.of(" + getSetString(zTypesToRemove) + "));\n";
					result += "return xTypesChanged || yTypesChanged || zTypesChanged;";
					return result;
				}
			}
		}
	}
	
	private static String getSetString(Set<ImmutabilityTypes> xTypesToRemove){
		String result = "";
		String prefix = "";
		for(ImmutabilityTypes type : xTypesToRemove){
			result += prefix + "ImmutabilityTypes." + type.toString();
			prefix = ", ";
		}
		return result;
	}
	
}
