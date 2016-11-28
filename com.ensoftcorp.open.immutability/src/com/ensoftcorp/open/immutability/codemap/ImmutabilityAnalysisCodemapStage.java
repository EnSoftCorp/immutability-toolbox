package com.ensoftcorp.open.immutability.codemap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ensoftcorp.open.commons.codemap.PrioritizedCodemapStage;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityAnalysis;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class ImmutabilityAnalysisCodemapStage extends PrioritizedCodemapStage {

	public static final String IDENTIFIER = "com.ensoftcorp.open.immutability";
	private static final String POINTS_TO_ANALYSIS_IDENTIFIER = "com.ensoftcorp.open.pointsto";
	
	@Override
	public String getDisplayName() {
		return "Immutability Analysis";
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public String[] getCodemapStageDependencies() {
		if(ImmutabilityPreferences.isPointsToAnalysisModeEnabled()){
			return new String[]{ POINTS_TO_ANALYSIS_IDENTIFIER };
		} else {
			return new String[]{};
		}
	}

	@Override
	public void performIndexing(IProgressMonitor monitor) {
		if(ImmutabilityPreferences.isImmutabilityAnalysisEnabled()){
			ImmutabilityAnalysis.runAnalysis(monitor);
		}
	}

}
