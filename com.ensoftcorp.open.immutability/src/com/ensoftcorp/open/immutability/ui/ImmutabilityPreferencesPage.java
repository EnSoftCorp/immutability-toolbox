package com.ensoftcorp.open.immutability.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ensoftcorp.open.immutability.Activator;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

/**
 * UI for setting immutability analysis preferences
 * 
 * @author Ben Holland
 */
public class ImmutabilityPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String GENERATE_SUMMARIES_DESCRIPTION = "Generate summaries (partial program analysis)";
	private static final String LOAD_SUMMARIES_DESCRIPTION = "Load summaries (partial program analysis)";
	private static final String RUN_SANITY_CHECKS_DESCRIPTION = "Run sanity checks";
	private static final String CONSTRAINT_PROFILING_DESCRIPTION = "Profile constraint solvers";
	private static final String GENERAL_LOGGING_DESCRIPTION = "Enable General Logging";
	private static final String INFERENCE_RULE_LOGGING_DESCRIPTION = "Enable Inference Rule Logging";
	private static final String DEBUG_LOGGING_DESCRIPTION = "Enable Debug Logging";
	
	private static boolean changeListenerAdded = false;
	
	public ImmutabilityPreferencesPage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(preferences);
		setDescription("Configure preferences for the Immutability Analysis Toolbox plugin.");
		
		// use to update cached values if user edits a preference
		if(!changeListenerAdded){
			getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
					ImmutabilityPreferences.loadPreferences();
				}
			});
			changeListenerAdded = true;
		}
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(ImmutabilityPreferences.GENERATE_SUMMARIES, "&" + GENERATE_SUMMARIES_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.LOAD_SUMMARIES, "&" + LOAD_SUMMARIES_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.RUN_SANITY_CHECKS, "&" + RUN_SANITY_CHECKS_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.CONSTRAINT_PROFILING, "&" + CONSTRAINT_PROFILING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.GENERAL_LOGGING, "&" + GENERAL_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.INFERENCE_RULE_LOGGING, "&" + INFERENCE_RULE_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.DEBUG_LOGGING, "&" + DEBUG_LOGGING_DESCRIPTION, getFieldEditorParent()));
	}

}
