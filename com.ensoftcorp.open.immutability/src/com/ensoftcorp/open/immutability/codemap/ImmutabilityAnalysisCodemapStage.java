package com.ensoftcorp.open.immutability.codemap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ensoftcorp.open.commons.codemap.PrioritizedCodemapStage;
import com.ensoftcorp.open.immutability.analysis.ImmutabilityAnalysis;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.pointsto.codemap.PointsToCodemapStage;

public class ImmutabilityAnalysisCodemapStage extends PrioritizedCodemapStage {

	public static final String IDENTIFIER = "com.ensoftcorp.open.immutability";
	
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
			return new String[]{ PointsToCodemapStage.IDENTIFIER };
		} else {
			return new String[]{};
		}
	}

	@Override
	public boolean performIndexing(IProgressMonitor monitor) {
		boolean runIndexer = ImmutabilityPreferences.isImmutabilityAnalysisEnabled();
		if(runIndexer){
			ImmutabilityAnalysis.runAnalysis(monitor);
		}
		return runIndexer;
	}

}
