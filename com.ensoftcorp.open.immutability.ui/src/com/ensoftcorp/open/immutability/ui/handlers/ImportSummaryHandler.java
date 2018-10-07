package com.ensoftcorp.open.immutability.ui.handlers;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.immutability.analysis.SummaryUtilities;
import com.ensoftcorp.open.immutability.ui.log.Log;

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
		
		private static class FileResult {
			File file = null;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final FileResult fileResult = new FileResult();
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
					dialog.setFilterNames(new String[] { "Immutability Analysis Results", "All Files (*.*)" });
					dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
					String path = dialog.open();
					if(path != null){
						fileResult.file = new File(path);
					}
				}
			});
			File inputFile = fileResult.file;
			if(inputFile != null){
				try {
					SummaryUtilities.importSummary(fileResult.file);
				} catch (FileNotFoundException e) {
					DisplayUtils.showError(e, "Could not find summary file.");
				} catch (XMLStreamException e) {
					DisplayUtils.showError(e, "Error parsing summary file.");
				}
				if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Imported Summary.");
			} else {
				if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("No summary file was selected.");
			}
			return Status.OK_STATUS;
		}	
	}
	
}