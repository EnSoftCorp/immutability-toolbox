package com.ensoftcorp.open.immutability.analysis.checkers;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.util.Set;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.StandardQueries;
import com.ensoftcorp.open.immutability.analysis.AnalysisUtilities;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityTypes;
import com.ensoftcorp.open.immutability.analysis.solvers.XAdaptYGreaterThanZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanYAdaptZConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.solvers.XGreaterThanYConstraintSolver;
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
		
		// qy <: qx adapt qf
		// = qx adapt qf :> qy
		if(XAdaptYGreaterThanZConstraintSolver.satisify(x, xTypes, f, fTypes, y, yTypes)){
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

		// qy adapt qf <: qx
		// = qx :> qy adapt qf
		if(XGreaterThanYAdaptZConstraintSolver.satisify(x, xTypes, y, yTypes, f, fTypes)){
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
		
		if(XGreaterThanYConstraintSolver.satisify(x, xTypes, m, mStaticTypes)){
			typesChanged = true;
		}
		
		if(BasicAssignmentChecker.handleAssignment(x, sf)){
			typesChanged = true;
		}
		
		return typesChanged;
	}
	
}
