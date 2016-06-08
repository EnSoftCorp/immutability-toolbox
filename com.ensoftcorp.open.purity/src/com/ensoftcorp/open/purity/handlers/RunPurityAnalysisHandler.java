package com.ensoftcorp.open.purity.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.ensoftcorp.open.purity.analysis.PurityAnalysis;

/**
 * A menu selection handler for running the purity analysis
 * 
 * @author Ben Holland
 */
public class RunPurityAnalysisHandler extends AbstractHandler {
	public RunPurityAnalysisHandler() {}

	/**
	 * Runs the purity analysis
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return PurityAnalysis.run();
	}
	
}
