package com.ensoftcorp.open.immutability.analysis.codegen;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class XGreaterThanYConstraintSolverGenerator {

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
		
		for(int x : allSets){
			for(int y : allSets){
				String result = getResult(sets.get(x-1), sets.get(y-1));
				if(!result.equals("")){
					System.out.println("if(xTypes.equals(SET" + x + ")){");
					System.out.println("if(yTypes.equals(SET" + y + ")){");
					System.out.println(result);
					System.out.println("}");
					System.out.println("}");
				}
			}
		}
		System.out.println("return false;");
	}
	
	private static int counter = 1;
	
	private static String getResult(EnumSet<ImmutabilityTypes> xTypes, EnumSet<ImmutabilityTypes> yTypes) {
		// process s(x)
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				if(xType.compareTo(yType) >= 0){
					isSatisfied = true;
					break satisfied;
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
				if(xType.compareTo(yType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		
		if(xTypesToRemove.isEmpty() && yTypesToRemove.isEmpty()){
			String prefix = "if(ImmutabilityPreferences.isConstraintProfilingEnabled()) incrementCounter(" + counter++ + ");\n";
			return prefix + "return false;";
		} else {
			if(xTypesToRemove.isEmpty()){
				String prefix = "if(ImmutabilityPreferences.isConstraintProfilingEnabled()) incrementCounter(" + counter++ + ");\n";
				return prefix + "return removeTypes(y, EnumSet.of(" + getSetString(yTypesToRemove) + "));";
			}
			if(yTypesToRemove.isEmpty()){
				String prefix = "if(ImmutabilityPreferences.isConstraintProfilingEnabled()) incrementCounter(" + counter++ + ");\n";
				return prefix + "return removeTypes(x, EnumSet.of(" + getSetString(xTypesToRemove) + "));";
			}
			String result = "";
			result += "if(ImmutabilityPreferences.isConstraintProfilingEnabled()) incrementCounter(" + counter++ + ");\n";
			result += "boolean xTypesChanged = removeTypes(x, EnumSet.of(" + getSetString(xTypesToRemove) + "));\n";
			result += "boolean yTypesChanged = removeTypes(y, EnumSet.of(" + getSetString(yTypesToRemove) + "));\n";
			result += "return xTypesChanged || yTypesChanged;";
			return result;
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
