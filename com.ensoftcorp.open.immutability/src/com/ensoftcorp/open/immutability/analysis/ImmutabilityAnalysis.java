package com.ensoftcorp.open.immutability.analysis;

import java.text.DecimalFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

/**
 * An Atlas native implementation a context-sensitive reference immutability analysis
 * 
 * This algorithm is based off the algorithm proposed in:
 * Reference 1: ReIm & ReImInfer: Checking and Inference of Reference Immutability, OOPSLA 2012 
 * Reference 2: Method Purity and ReImInfer: Method Purity Inference for Java, FSE 2012
 * 
 * @author Ben Holland, Ganesh Santhanam
 */
public abstract class ImmutabilityAnalysis {

	/**
	 * Tag applied to "pure" methods
	 */
	public static final String PURE_METHOD = "PURE";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "readonly" immutability
	 * Readonly means that in any context the reference is readonly (never mutated)
	 */
	public static final String READONLY = "READONLY";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "polyread" immutability
	 * Polyread means that depending on the context a reference may be mutable or readonly
	 */
	public static final String POLYREAD = "POLYREAD";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "mutable" immutability
	 * Mutable means that in any context the reference is mutable
	 */
	public static final String MUTABLE = "MUTABLE";
	
	/**
	 * Tag applied to references that resulted in no immutability types
	 * This tag should ideally never be applied and represents and error in the 
	 * type system or implementation
	 */
	public static final String UNTYPED = "UNTYPED";
	
	/**
	 * Helper for formatting decimal strings
	 */
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##"); 

	/**
	 * Runs the implementation of immutability analysis
	 * Returns true if the analysis completed in a consistent state
	 * @param monitor
	 * @return
	 */
	public abstract boolean run(IProgressMonitor monitor);
	
	/**
	 * Runs the reference immutability analysis
	 * @param monitor 
	 * @return Returns the time in milliseconds taken to complete the analysis
	 */
	public static boolean runAnalysis(IProgressMonitor monitor){
		Log.info("Immutability analysis started");
		ImmutabilityAnalysis analysis;
		if(ImmutabilityPreferences.isInferenceAnalysisModeEnabled()){
			analysis = new InferenceImmutabilityAnalysis();
		} else if(ImmutabilityPreferences.isPointsToAnalysisModeEnabled()){
			analysis = new PointsToImmutabilityAnalysis();
		} else {
			throw new RuntimeException("No immutability analysis mode was specified!");
		}
		
		long start = System.nanoTime();
		boolean isSane = analysis.run(monitor);
		long stop = System.nanoTime();
		double runtime = (stop-start)/1000.0/1000.0;
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) {
			if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
				Log.info("Immutability analysis completed in " + FORMAT.format(runtime) + " ms\n");
			} else {
				long numReadOnly = Common.universe().nodesTaggedWithAny(READONLY).eval().nodes().size();
				long numPolyRead = Common.universe().nodesTaggedWithAny(POLYREAD).eval().nodes().size();
				long numMutable = Common.universe().nodesTaggedWithAny(MUTABLE).eval().nodes().size();
				long numPure = Common.universe().nodesTaggedWithAny(PURE_METHOD).eval().nodes().size();
				String summary = "READONLY: " + numReadOnly + ", POLYREAD: " + numPolyRead + ", MUTABLE: " + numMutable  + ", PURE: " + numPure;
				Log.info("Immutability analysis completed in " + FORMAT.format(runtime) + " ms\n" + summary);
			}
		}
		return isSane;
	}

		
	/**
	 * Tags pure methods with "PURE"
	 */
	protected void tagPureMethods(){
		AtlasSet<Node> methods = Common.universe().nodesTaggedWithAny(XCSG.Method).eval().nodes();
		for(GraphElement method : methods){
			if(isPureMethod(method)){
				method.tag(PURE_METHOD);
			}
		}
	}
	
	/**
	 * Returns true if the method is pure
	 * Assumes the maximal immutability qualifiers have already been extracted
	 * @param method
	 */
	private boolean isPureMethod(GraphElement method){
		if(!method.taggedWith(XCSG.Method)){
			return false;
		} else if(isPureMethodDefault(method)){
			return true;
		} else {
			// from reference 1 section 3
			// a method is pure if 
			// 1) it does not mutate prestates reachable through parameters
			// this includes the formal parameters and implicit "this" parameter
			Q parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter);
			Q mutableParameters = parameters.nodesTaggedWithAny(MUTABLE, POLYREAD);
			if(mutableParameters.eval().nodes().size() > 0){
				return false;
			}
			Q mutableIdentity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).nodesTaggedWithAny(MUTABLE, POLYREAD);
			if(mutableIdentity.eval().nodes().size() > 0){
				return false;
			}
			
			// 2) it does not mutate prestates reachable through static fields
			if(method.taggedWith(MUTABLE)){
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * Returns true if the method is a default pure method
	 * @param method
	 * @return
	 */
	private boolean isPureMethodDefault(GraphElement method){
		// note by convention .equals, .hashCode, .toString, and .compareTo
		// are pure methods, but this is not enforced in overridden methods
		// so we are not assuming it to be universally true (unlike ReIm)
		
		// we could however consider some of the java.lang.Object native methods as pure
		// Object's native methods include: getClass, clone, hashCode, notifyAll, notify, wait, registerNatives
		if(method.taggedWith(XCSG.Java.nativeMethod)){
			if(!Common.toQ(method).intersection(Common.typeSelect("java.lang", "Object").children()).eval().nodes().isEmpty()){
				if(method.getAttr(XCSG.name).equals("getClass")){
					return true;
				}
				if(method.getAttr(XCSG.name).equals("hashCode")){
					return true;
				}
				if(method.getAttr(XCSG.name).equals("clone")){
					// clone is a pure method, but it is also a special case
					// to be consider since its return type is a duplication 
					// of a reference
					// see https://en.wikipedia.org/wiki/Clone_(Java_method)
					return true;
				}
				return false;
			}
		}

		return false;
	}
	
}
