package com.ensoftcorp.open.immutability.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ensoftcorp.open.commons.ui.components.LabelFieldEditor;
import com.ensoftcorp.open.commons.ui.components.SpacerFieldEditor;
import com.ensoftcorp.open.immutability.Activator;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;
import com.ensoftcorp.open.pointsto.log.Log;

/**
 * UI for setting immutability analysis preferences
 * 
 * @author Ben Holland
 */
public class ImmutabilityPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String RUN_IMMUTABILITY_ANALYSIS_DESCRIPTION = "Run immutability analysis";
	
	private static final String IMMUTABILITY_ANALYSIS_POINTSTO_MODE_DESCRIPTION = "Points-to analysis mode (more precise)";
	private static final String IMMUTABILITY_ANALYSIS_INFERENCE_MODE_DESCRIPTION = "Inference based analysis mode (more scalable)";
	
	private static final String RUN_SANITY_CHECKS_DESCRIPTION = "Run sanity checks";
	private static final String GENERAL_LOGGING_DESCRIPTION = "Enable General Logging";
	private static final String DEBUG_LOGGING_DESCRIPTION = "Enable Debug Logging";
	
	private static final String GENERATE_SUMMARIES_DESCRIPTION = "Generate summaries (inference only, partial program analysis)";
	private static final String LOAD_SUMMARIES_DESCRIPTION = "Load summaries (inference only, partial program analysis)";
	private static final String INFERENCE_RULE_LOGGING_DESCRIPTION = "Enable Inference Rule Logging (inference only)";
	
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
		addField(new BooleanFieldEditor(ImmutabilityPreferences.RUN_IMMUTABILITY_ANALYSIS, "&" + RUN_IMMUTABILITY_ANALYSIS_DESCRIPTION, getFieldEditorParent()));

		RadioGroupFieldEditor analysisMode = new RadioGroupFieldEditor(
				ImmutabilityPreferences.IMMUTABILITY_ANALYSIS_MODE,
				"Analysis Mode",
				1,
				new String[][] {
					{ "&" + IMMUTABILITY_ANALYSIS_POINTSTO_MODE_DESCRIPTION, 
						ImmutabilityPreferences.IMMUTABILITY_ANALYSIS_POINTSTO_MODE
					},
					{ "&" + IMMUTABILITY_ANALYSIS_INFERENCE_MODE_DESCRIPTION, 
						ImmutabilityPreferences.IMMUTABILITY_ANALYSIS_INFERENCE_MODE
					}
				},
				getFieldEditorParent(),
				true);
		addField(analysisMode);
		
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Advanced Options", getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.RUN_SANITY_CHECKS, "&" + RUN_SANITY_CHECKS_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.GENERAL_LOGGING, "&" + GENERAL_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.DEBUG_LOGGING, "&" + DEBUG_LOGGING_DESCRIPTION, getFieldEditorParent()));
		
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Inference Based Analysis Options", getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.GENERATE_SUMMARIES, "&" + GENERATE_SUMMARIES_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.LOAD_SUMMARIES, "&" + LOAD_SUMMARIES_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(ImmutabilityPreferences.INFERENCE_RULE_LOGGING, "&" + INFERENCE_RULE_LOGGING_DESCRIPTION, getFieldEditorParent()));
	}

}
