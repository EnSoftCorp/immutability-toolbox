package com.ensoftcorp.open.immutability.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ensoftcorp.open.immutability.analysis.ImmutabilityAnalysis;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

/**
 * A menu selection handler for running the immutability analysis
 * 
 * @author Ben Holland
 */
public class RunImmutabilityAnalysisHandler extends AbstractHandler {
	public RunImmutabilityAnalysisHandler() {}

	/**
	 * Runs the immutability analysis
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ImmutabilityJob job = new ImmutabilityJob();
		job.schedule();
		return null;
	}
	
	private static class ImmutabilityJob extends Job {
		public ImmutabilityJob() {
			super("Running Immutability Analysis");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			ImmutabilityAnalysis.run(monitor);
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Job completed.");
			return Status.OK_STATUS;
		}	
	}
	
}
