package com.ensoftcorp.open.purity.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * A menu selection handler for running the purity analysis
 * 
 * @author Ben Holland
 */
public class PurityPreferencesHandler extends AbstractHandler {
	public PurityPreferencesHandler() {}

	/**
	 * Runs the purity analysis
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = "com.ensoftcorp.open.purity.ui.preferences";
		return PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), id, new String[] {id}, null).open();
	}
	
}
