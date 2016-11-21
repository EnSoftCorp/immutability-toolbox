package com.ensoftcorp.open.immutability.analysis;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.open.pointsto.preferences.PointsToPreferences;

public class PointsToImmutabilityAnalysis extends ImmutabilityAnalysis {

	@Override
	public boolean run(IProgressMonitor monitor) {
		if(PointsToPreferences.isPointsToAnalysisEnabled()){
			
			// TODO: implement
			
			return true;
		} else {
			Log.error("Points-to analysis must be enabled to run points-to immutability analysis.",
					new IllegalArgumentException());
			return false;
		}
	}

}
