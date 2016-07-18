package com.ensoftcorp.open.immutability.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ensoftcorp.open.commons.utils.DisplayUtils;

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
			// TODO: implement
			DisplayUtils.showMessage("Not Implemented");
//			// flattens the type hierarchy to the maximal types
//			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Extracting maximal types...");
//			long startExtraction = System.nanoTime();
//			ImmutabilityAnalysis.extractMaximalTypes();
//			long stopExtraction = System.nanoTime();
//			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Extracted maximal types in " + FORMAT.format((stopExtraction-startExtraction)/1000.0/1000.0) + " ms");
//			
//			// tags pure methods
//			// must be run after extractMaximalTypes
//			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Applying method immutability tags...");
//			long startImmutabilityTagging = System.nanoTime();
//			ImmutabilityAnalysis.tagPureMethods();
//			long stopImmutabilityTagging = System.nanoTime();
//			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Applied method immutability tags in " + FORMAT.format((stopImmutabilityTagging-startImmutabilityTagging)/1000.0/1000.0) + " ms");
//			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Maximal immutability types extracted.");
			return Status.OK_STATUS;
		}
	}
	
}
