package com.ensoftcorp.open.purity.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ensoftcorp.open.purity.Activator;

/**
 * UI for setting purity analysis preferences
 * 
 * @author Ben Holland
 */
public class PurityPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Enable/disable removing the qualifier sets after the maximal type has been extracted
	 */
	public static final String REMOVE_QUALIFIER_SETS = "REMOVE_QUALIFIER_SETS";
	public static final String REMOVE_QUALIFIER_SETS_DESCRIPTION = "Remove Qualifier Sets";
	public static final Boolean REMOVE_QUALIFIER_SETS_DEFAULT = true;
	
	public static boolean isRemoveQualifierSetsEnabled(){
		boolean result = REMOVE_QUALIFIER_SETS_DEFAULT;
		try {
			result = Activator.getDefault().getPreferenceStore().getBoolean(REMOVE_QUALIFIER_SETS);
		} catch (Exception e){
			// TODO: re-enable after class loader issue on shell is fixed
//			Log.error("Error accessing purity analysis preferences.", e);
		}
		return result;
	}
	
	/**
	 * Enable/disable general logging to the Atlas log
	 */
	public static final String GENERAL_LOGGING = "GENERAL_LOGGING";
	public static final String GENERAL_LOGGING_DESCRIPTION = "Enable General Logging";
	public static final Boolean GENERAL_LOGGING_DEFAULT = true;
	
	public static boolean isGeneralLoggingEnabled(){
		boolean result = GENERAL_LOGGING_DEFAULT;
		try {
			result = Activator.getDefault().getPreferenceStore().getBoolean(GENERAL_LOGGING);
		} catch (Exception e){
			// TODO: re-enable after class loader issue on shell is fixed
//			Log.error("Error accessing purity analysis preferences.", e);
		}
		return result;
	}

	/**
	 * Enables/disables inference rule logging to the Atlas log
	 */
	public static final String INFERENCE_RULE_LOGGING = "INFERENCE_RULE_LOGGING";
	public static final String INFERENCE_RULE_LOGGING_DESCRIPTION = "Enable Inference Rule Logging";
	public static final Boolean INFERENCE_RULE_LOGGING_DEFAULT = false;
	
	public static boolean isInferenceRuleLoggingEnabled(){
		boolean result = INFERENCE_RULE_LOGGING_DEFAULT;
		try {
			result = Activator.getDefault().getPreferenceStore().getBoolean(INFERENCE_RULE_LOGGING);
		} catch (Exception e){
			// TODO: re-enable after class loader issue on shell is fixed
//			Log.error("Error accessing purity analysis preferences.", e);
		}
		return result;
	}
	
	/**
	 * Enables verbose debug logging to the Atlas log
	 */
	public static final String DEBUG_LOGGING = "DEBUG_LOGGING";
	public static final String DEBUG_LOGGING_DESCRIPTION = "Enable Debug Logging";
	public static final Boolean DEBUG_LOGGING_DEFAULT = false;
	
	public static boolean isDebugLoggingEnabled(){
		boolean result = DEBUG_LOGGING_DEFAULT;
		try {
			result = Activator.getDefault().getPreferenceStore().getBoolean(DEBUG_LOGGING);
		} catch (Exception e){
			// TODO: re-enable after class loader issue on shell is fixed
//			Log.error("Error accessing purity analysis preferences.", e);
		}
		return result;
	}
	
	public PurityPreferences() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(REMOVE_QUALIFIER_SETS, REMOVE_QUALIFIER_SETS_DEFAULT);
		preferences.setDefault(GENERAL_LOGGING, GENERAL_LOGGING_DEFAULT);
		preferences.setDefault(INFERENCE_RULE_LOGGING, INFERENCE_RULE_LOGGING_DEFAULT);
		preferences.setDefault(DEBUG_LOGGING, DEBUG_LOGGING_DEFAULT);
		setPreferenceStore(preferences);
		setDescription("Configure preferences for the Purity Analysis Toolbox plugin.");	
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(REMOVE_QUALIFIER_SETS, "&" + REMOVE_QUALIFIER_SETS_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(GENERAL_LOGGING, "&" + GENERAL_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(INFERENCE_RULE_LOGGING, "&" + INFERENCE_RULE_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(DEBUG_LOGGING, "&" + DEBUG_LOGGING_DESCRIPTION, getFieldEditorParent()));
	}

}
