package com.ensoftcorp.open.immutability.analysis;

import java.text.DecimalFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.query.Query;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.immutability.constants.ImmutabilityTags;
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
				long numReadOnly = Query.universe().nodes(ImmutabilityTags.READONLY).eval().nodes().size();
				long numPolyRead = Query.universe().nodes(ImmutabilityTags.POLYREAD).eval().nodes().size();
				long numMutable = Query.universe().nodes(ImmutabilityTags.MUTABLE).eval().nodes().size();
				long numPure = Query.universe().nodes(ImmutabilityTags.PURE_METHOD).eval().nodes().size();
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
		AtlasSet<Node> methods = Query.universe().nodes(XCSG.Method).eval().nodes();
		for(Node method : methods){
			if(isPureMethod(method)){
				method.tag(ImmutabilityTags.PURE_METHOD);
			}
			method.tags().remove(ImmutabilityTags.READONLY);
			method.tags().remove(ImmutabilityTags.POLYREAD);
			method.tags().remove(ImmutabilityTags.MUTABLE);
		}
	}
	
	/**
	 * Returns true if the method is pure
	 * Assumes the maximal immutability qualifiers have already been extracted
	 * @param method
	 */
	private boolean isPureMethod(Node method){
		if(!method.taggedWith(XCSG.Method)){
			return false;
		} else if(isPureMethodDefault(method)){
			return true;
		} else {
			// from reference 1 section 3
			// a method is pure if 
			// 1) it does not mutate (not readonly or polyread) prestates reachable through parameters
			// this includes the formal parameters and implicit "this" parameter
			Q parameters = Common.toQ(method).children().nodes(XCSG.Parameter);
			Q mutableParameters = parameters.nodes(ImmutabilityTags.MUTABLE);
			if(mutableParameters.eval().nodes().size() > 0){
				return false;
			}
			Q mutableIdentity = Common.toQ(method).children().nodes(XCSG.Identity).nodes(ImmutabilityTags.MUTABLE);
			if(mutableIdentity.eval().nodes().size() > 0){
				return false;
			}
			
			// 2) it does not mutate prestates reachable through static fields
			// (its static type is not readonly or polyread)
			if(method.taggedWith(ImmutabilityTags.MUTABLE)){
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
	private boolean isPureMethodDefault(Node method){
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
