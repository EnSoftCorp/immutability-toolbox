package com.ensoftcorp.open.immutability.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

/**
 * A menu handler for re-importing summaries
 * 
 * @author Ben Holland
 */
public class ImportSummaryHandler extends AbstractHandler {
	public ImportSummaryHandler() {}

	/**
	 * Imports summaries
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ImportSummaryJob job = new ImportSummaryJob();
		job.schedule();
		return null;
	}
	
	private static class ImportSummaryJob extends Job {
		public ImportSummaryJob() {
			super("Importing Summary...");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// TODO: implement
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Imported Summary.");
			return Status.OK_STATUS;
		}	
	}
	
}