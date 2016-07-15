package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.EnumSet;
import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.utils.StandardQueries;
import com.ensoftcorp.open.immutability.analysis.AnalysisUtilities;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class FieldAssignmentChecker {

	/**
	 * Solves and satisfies constraints for Type Rule 3 - TWRITE
	 * Let, x.f = y
	 * 
	 * @param x The receiver object
	 * @param f The field of the receiver object being written to
	 * @param y The reference being read from
	 * @return Returns true if the graph element's ImmutabilityTypes have changed
	 */
	public static boolean handleFieldWrite(Node x, Node f, Node y) {

		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TWRITE (x.f=y, x=" + x.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		
		// if x is a reference it must be mutable
		// if x is a field it must be polyread
		if(removeTypes(x, ImmutabilityTypes.READONLY)){
			typesChanged = true;
		}
		
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> fTypes = getTypes(f);
		Set<ImmutabilityTypes> yTypes = getTypes(y);

		// if y is only mutable then f cannot be readonly
		if((yTypes.contains(ImmutabilityTypes.MUTABLE)) && yTypes.size()==1){
			if(removeTypes(f, ImmutabilityTypes.READONLY)){
				typesChanged = true;
			}
		}
		
		// process s(x)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(x) for constraint qy " + getTypes(y).toString() + " <: qx " + getTypes(x).toString() + " adapt qf " + getTypes(f).toString());
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes xAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, fType);
					if(xAdaptedF.compareTo(yType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
				if(!isSatisfied){
					xTypesToRemove.add(xType);
				}
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(y)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(y) for constraint qy " + getTypes(y).toString() + " <: qx " + getTypes(x).toString() + " adapt qf " + getTypes(f).toString());
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes xAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, fType);
					if(xAdaptedF.compareTo(yType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
				if(!isSatisfied){
					yTypesToRemove.add(yType);
				}
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			typesChanged = true;
		}

		// process s(f)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(f) for constraint qy " + getTypes(y).toString() + " <: qx " + getTypes(x).toString() + " adapt qf " + getTypes(f).toString());
		Set<ImmutabilityTypes> fTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes fType : fTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes yType : yTypes){
					ImmutabilityTypes xAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(xType, fType);
					if(xAdaptedF.compareTo(yType) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
				if(!isSatisfied){
					fTypesToRemove.add(fType);
				}
			}
		}
		if(removeTypes(f, fTypesToRemove)){
			typesChanged = true;
		}
		
		return typesChanged;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 4 - TREAD
	 * Let, x = y.f
	 * 
	 * @param x The reference being written to
	 * @param y The receiver object
	 * @param f The field of the receiver object being read from
	 * @return Returns true if the graph element's ImmutabilityTypes have changed
	 */
	public static boolean handleFieldRead(Node x, Node y, Node f) {
		
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TREAD (x=y.f, x=" + x.getAttr(XCSG.name) + ", y=" + y.getAttr(XCSG.name) + ", f=" + f.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		
		
		boolean xIsPolyreadField = x.taggedWith(XCSG.Field) && (xTypes.contains(ImmutabilityTypes.POLYREAD) && xTypes.size() == 1);
		boolean xIsMutableReference = !x.taggedWith(XCSG.Field) && (xTypes.contains(ImmutabilityTypes.MUTABLE) && xTypes.size() == 1);
		 
		if(xIsPolyreadField || xIsMutableReference){
			// if x is a polyread field then the read field (f) and its container's must be polyread
			// if x is a mutable reference then f and its container fields must be polyread
			// for example x = z.y.f, if x has been mutated then so has f, y, and z
			if(removeTypes(f, ImmutabilityTypes.READONLY)){
				typesChanged = true;
			}
			for(Node container : AnalysisUtilities.getAccessedContainers(y)){
				if(removeTypes(container, ImmutabilityTypes.READONLY)){
					typesChanged = true;
				}
				if(container.taggedWith(XCSG.ClassVariable)){
					if(removeTypes(StandardQueries.getContainingMethod(x), ImmutabilityTypes.READONLY)){
						typesChanged = true;
					}
				}
			}
		}
	
		if(y.taggedWith(XCSG.InstanceVariable) || y.taggedWith(XCSG.ClassVariable)){
			// the remaining constraints are too strong for multiple fields
			return typesChanged;
		}
		
		Set<ImmutabilityTypes> fTypes = getTypes(f);
		Set<ImmutabilityTypes> yTypes = getTypes(y);

		// process s(x)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(x) for constraint qy " + getTypes(y).toString() + " adapt qf " + getTypes(f).toString() + " <: qx " + getTypes(x).toString());
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes yType : yTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(y)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(y) for constraint qy " + getTypes(y).toString() + " adapt qf " + getTypes(f).toString() + " <: qx " + getTypes(x).toString());
		Set<ImmutabilityTypes> yTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes yType : yTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes fType : fTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				yTypesToRemove.add(yType);
			}
		}
		if(removeTypes(y, yTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(f)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(f) for constraint qy " + getTypes(y).toString() + " adapt qf " + getTypes(f).toString() + " <: qx " + getTypes(x).toString());
		Set<ImmutabilityTypes> fTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes fType : fTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				for(ImmutabilityTypes yType : yTypes){
					ImmutabilityTypes yAdaptedF = ImmutabilityTypes.getAdaptedFieldViewpoint(yType, fType);
					if(xType.compareTo(yAdaptedF) >= 0){
						isSatisfied = true;
						break satisfied;
					}
				}
			}
			if(!isSatisfied){
				fTypesToRemove.add(fType);
			}
		}
		if(removeTypes(f, fTypesToRemove)){
			typesChanged = true;
		}
		
		return typesChanged;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 6, - TSWRITE
	 * Let, sf = x
	 * 
	 * @param sf The static field being written to
	 * @param x The reference being read from
	 * @param m The method where the assignment happens
	 * 
	 * @return
	 */
	public static boolean handleStaticFieldWrite(Node sf, Node x, Node m) {
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TSWRITE (sf=x, sf=" + sf.getAttr(XCSG.name) + ", x=" + x.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		
		if(removeTypes(m, ImmutabilityTypes.READONLY)){
			typesChanged = true;
		}
		
		if(BasicAssignmentChecker.handleAssignment(sf, x)){
			typesChanged = true;
		}

		return typesChanged;
	}
	
	/**
	 * Solves and satisfies constraints for Type Rule 7, - TSREAD
	 * Let, x = sf
	 * 
	 * @param x The reference being written to
	 * @param sf The static field being read from
	 * @param m The method where the assignment happens
	 * @return
	 */
	public static boolean handleStaticFieldRead(Node x, Node sf, Node m) {
		if(ImmutabilityPreferences.isInferenceRuleLoggingEnabled()) Log.info("TSREAD (x=sf, x=" + x.getAttr(XCSG.name) + ", sf=" + sf.getAttr(XCSG.name) + ")");
		
		boolean typesChanged = false;
		
		Set<ImmutabilityTypes> xTypes = getTypes(x);
		Set<ImmutabilityTypes> mStaticTypes = getTypes(m);
		
		// process s(x)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(x) for constraint qm " + getTypes(m).toString() + " <: qx " + getTypes(x).toString());
		Set<ImmutabilityTypes> xTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes xType : xTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes mStaticType : mStaticTypes){
				if(xType.compareTo(mStaticType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				xTypesToRemove.add(xType);
			}
		}
		if(removeTypes(x, xTypesToRemove)){
			typesChanged = true;
		}
		
		// process s(m)
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Process s(m) for constraint qm " + getTypes(m).toString() + " <: qx " + getTypes(x).toString());
		Set<ImmutabilityTypes> mStaticTypesToRemove = EnumSet.noneOf(ImmutabilityTypes.class);
		for(ImmutabilityTypes mStaticType : mStaticTypes){
			boolean isSatisfied = false;
			satisfied:
			for(ImmutabilityTypes xType : xTypes){
				if(xType.compareTo(mStaticType) >= 0){
					isSatisfied = true;
					break satisfied;
				}
			}
			if(!isSatisfied){
				mStaticTypesToRemove.add(mStaticType);
			}
		}
		if(removeTypes(m, mStaticTypesToRemove)){
			typesChanged = true;
		}
		
		if(BasicAssignmentChecker.handleAssignment(x, sf)){
			typesChanged = true;
		}
		
		return typesChanged;
	}
	
}
