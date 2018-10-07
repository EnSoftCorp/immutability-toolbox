package com.ensoftcorp.open.immutability.ui.handlers;

import java.io.File;

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

import com.ensoftcorp.atlas.core.query.Query;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.immutability.analysis.SummaryUtilities;
import com.ensoftcorp.open.immutability.ui.log.Log;

/**
 * A menu handler for re-exporting summaries
 * 
 * @author Ben Holland
 */
public class ExportSummaryHandler extends AbstractHandler {
	public ExportSummaryHandler() {}

	/**
	 * Exports summaries
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ExportSummaryJob job = new ExportSummaryJob();
		job.schedule();
		return null;
	}
	
	private static class ExportSummaryJob extends Job {
		public ExportSummaryJob() {
			super("Exporting Summary...");
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
					FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
					dialog.setFilterNames(new String[] { "Immutability Analysis Results", "All Files (*.*)" });
					dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
					try {
						String projectName = Query.universe().nodes(XCSG.Project).eval().nodes().getFirst().getAttr(XCSG.name).toString();
						dialog.setFileName(projectName + "-immutability.xml");
					} catch (Exception e){}
					fileResult.file = new File(dialog.open());
				}
			});
			File outputFile = fileResult.file;
			if(outputFile != null){
				try {
					SummaryUtilities.exportSummary(outputFile);
				} catch (Exception e){
					Log.error("Could not save summaries.", e);
					DisplayUtils.showError(e, "Could not save summaries.");
				}
				if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Exported Summary.");
			} else {
				if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("No summary export destination was selected.");
			}
			return Status.OK_STATUS;
		}	
	}
	
}
