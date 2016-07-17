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
 * A menu handler for re-exporting summaries
 * 
 * @author Ben Holland
 */
public class ExtractMaximalTypesHandler extends AbstractHandler {
	public ExtractMaximalTypesHandler() {}

	/**
	 * Extract maximal types
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ExtractMaximalTypesJob job = new ExtractMaximalTypesJob();
		job.schedule();
		return null;
	}
	
	private static class ExtractMaximalTypesJob extends Job {
		public ExtractMaximalTypesJob() {
			super("Extracting maximal immutability types...");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Maximal immutability types extracted.");
			return Status.OK_STATUS;
		}	
	}
	
}
