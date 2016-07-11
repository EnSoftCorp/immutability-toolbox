package com.ensoftcorp.open.purity.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ensoftcorp.open.purity.Activator;
import com.ensoftcorp.open.purity.preferences.PurityPreferences;

/**
 * UI for setting purity analysis preferences
 * 
 * @author Ben Holland
 */
public class PurityPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String GENERATE_SUMMARIES_DESCRIPTION = "Generate summaries (partial program analysis)";
	private static final String LOAD_SUMMARIES_DESCRIPTION = "Load summaries (partial program analysis)";
	private static final String RUN_SANITY_CHECKS_DESCRIPTION = "Run sanity checks";
	private static final String GENERAL_LOGGING_DESCRIPTION = "Enable General Logging";
	private static final String INFERENCE_RULE_LOGGING_DESCRIPTION = "Enable Inference Rule Logging";
	private static final String DEBUG_LOGGING_DESCRIPTION = "Enable Debug Logging";
	
	private static boolean changeListenerAdded = false;
	
	public PurityPreferencesPage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferences);
		setDescription("Configure preferences for the Purity Analysis Toolbox plugin.");
		
		// use to update cached values if user edits a preference
		if(!changeListenerAdded){
			getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
					PurityPreferences.loadPreferences();
				}
			});
			changeListenerAdded = true;
		}
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PurityPreferences.GENERATE_SUMMARIES, "&" + GENERATE_SUMMARIES_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PurityPreferences.LOAD_SUMMARIES, "&" + LOAD_SUMMARIES_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PurityPreferences.RUN_SANITY_CHECKS, "&" + RUN_SANITY_CHECKS_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PurityPreferences.GENERAL_LOGGING, "&" + GENERAL_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PurityPreferences.INFERENCE_RULE_LOGGING, "&" + INFERENCE_RULE_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PurityPreferences.DEBUG_LOGGING, "&" + DEBUG_LOGGING_DESCRIPTION, getFieldEditorParent()));
	}

}
