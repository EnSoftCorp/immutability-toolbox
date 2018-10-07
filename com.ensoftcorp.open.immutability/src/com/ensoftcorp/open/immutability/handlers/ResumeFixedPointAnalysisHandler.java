package com.ensoftcorp.open.immutability.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;

/**
 * A menu handler for resuming the analysis
 * 
 * @author Ben Holland
 */
public class ResumeFixedPointAnalysisHandler extends AbstractHandler {
	public ResumeFixedPointAnalysisHandler() {}

	/**
	 * Resumes the fixed point analysis
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ImportSummaryJob job = new ImportSummaryJob();
		job.schedule();
		return null;
	}
	
	private static class ImportSummaryJob extends Job {
		public ImportSummaryJob() {
			super("Resuming Fixed Point Analysis...");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// TODO: implement
			DisplayUtils.showMessage("Not Implemented");
//			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Reached Fixed Point.");
			return Status.OK_STATUS;
		}	
	}
	
}
