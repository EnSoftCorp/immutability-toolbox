package com.ensoftcorp.open.purity.analysis.checkers;

import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.open.purity.analysis.PurityAnalysis;
import com.ensoftcorp.open.purity.log.Log;
import com.ensoftcorp.open.purity.preferences.PurityPreferences;

public class SanityChecks {

	public static boolean run(){
		boolean resultsAreSane = true;
		
		if(!PurityPreferences.isPartialProgramAnalysisEnabled()){
			if(isDoubleTagged()){
				resultsAreSane = false;
			}
		}
		
		if(hasUntypedReferences()){
			resultsAreSane = false;
		}
		
		return resultsAreSane;
	}

	/**
	 * No nodes should be tagged with two or more of the following (READONLY, POLYREAD, MUTABLE)
	 * @return
	 */
	private static boolean isDoubleTagged() {
		boolean isDoubleTagged = false;
		
		AtlasSet<GraphElement> readonlyPolyread = Common.universe().nodesTaggedWithAll(PurityAnalysis.READONLY, PurityAnalysis.POLYREAD).eval().nodes();
		if(readonlyPolyread.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + readonlyPolyread.size() + " nodes that are tagged as " + PurityAnalysis.READONLY + " and " + PurityAnalysis.POLYREAD);
		}
		
		AtlasSet<GraphElement> readonlyMutable = Common.universe().nodesTaggedWithAll(PurityAnalysis.READONLY, PurityAnalysis.MUTABLE).eval().nodes();
		if(readonlyMutable.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + readonlyMutable.size() + " nodes that are tagged as " + PurityAnalysis.READONLY + " and " + PurityAnalysis.MUTABLE);
		}
		
		AtlasSet<GraphElement> polyreadMutable = Common.universe().nodesTaggedWithAll(PurityAnalysis.POLYREAD, PurityAnalysis.MUTABLE).eval().nodes();
		if(polyreadMutable.size() > 0){
			isDoubleTagged = true;
			Log.warning("There are " + polyreadMutable.size() + " nodes that are tagged as " + PurityAnalysis.POLYREAD + " and " + PurityAnalysis.MUTABLE);
		}
		
		return isDoubleTagged;
	}
	
	private static boolean hasUntypedReferences(){
		boolean hasUntypedReferences = false;
		AtlasSet<GraphElement> untypedReferences = Common.universe().nodesTaggedWithAny(PurityAnalysis.UNTYPED).eval().nodes();
		if(untypedReferences.size() > 0){
			hasUntypedReferences = true;
			Log.warning("There are " + untypedReferences.size() + " references with no immutability types!");
		}
		return hasUntypedReferences;
	}
	
}
