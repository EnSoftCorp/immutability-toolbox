package com.ensoftcorp.open.purity.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ensoftcorp.open.purity.Activator;
import com.ensoftcorp.open.purity.log.Log;

/**
 * UI for setting purity analysis preferences
 * 
 * @author Ben Holland
 */
public class PurityPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static boolean initialized = false;
	
	/**
	 * Enable/disable running sanity checks
	 */
	public static final String RUN_SANITY_CHECKS = "RUN_SANITY_CHECKS";
	public static final String RUN_SANITY_CHECKS_DESCRIPTION = "Run sanity checks";
	public static final Boolean RUN_SANITY_CHECKS_DEFAULT = true;
	private static boolean runSanityChecksValue = RUN_SANITY_CHECKS_DEFAULT;
	
	public static boolean isRunSanityChecksEnabled(){
		return runSanityChecksValue;
	}
	
	/**
	 * Enable/disable removing the qualifier sets after the maximal type has been extracted
	 */
	public static final String REMOVE_QUALIFIER_SETS = "REMOVE_QUALIFIER_SETS";
	public static final String REMOVE_QUALIFIER_SETS_DESCRIPTION = "Remove Qualifier Sets";
	public static final Boolean REMOVE_QUALIFIER_SETS_DEFAULT = true;
	private static boolean removeQualifierSetsValue = REMOVE_QUALIFIER_SETS_DEFAULT;
	
	public static boolean isRemoveQualifierSetsEnabled(){
		return removeQualifierSetsValue;
	}
	
	/**
	 * Enable/disable general logging to the Atlas log
	 */
	public static final String GENERAL_LOGGING = "GENERAL_LOGGING";
	public static final String GENERAL_LOGGING_DESCRIPTION = "Enable General Logging";
	public static final Boolean GENERAL_LOGGING_DEFAULT = true;
	private static Boolean generalLoggingValue = GENERAL_LOGGING_DEFAULT;
	
	public static boolean isGeneralLoggingEnabled(){
		return generalLoggingValue;
	}

	/**
	 * Enables/disables inference rule logging to the Atlas log
	 */
	public static final String INFERENCE_RULE_LOGGING = "INFERENCE_RULE_LOGGING";
	public static final String INFERENCE_RULE_LOGGING_DESCRIPTION = "Enable Inference Rule Logging";
	public static final Boolean INFERENCE_RULE_LOGGING_DEFAULT = false;
	private static Boolean inferenceRuleLoggingValue = INFERENCE_RULE_LOGGING_DEFAULT;
	
	public static boolean isInferenceRuleLoggingEnabled(){
		return inferenceRuleLoggingValue;
	}
	
	/**
	 * Enables verbose debug logging to the Atlas log
	 */
	public static final String DEBUG_LOGGING = "DEBUG_LOGGING";
	public static final String DEBUG_LOGGING_DESCRIPTION = "Enable Debug Logging";
	public static final Boolean DEBUG_LOGGING_DEFAULT = false;
	private static Boolean debugLoggingValue = DEBUG_LOGGING_DEFAULT;
	
	public static boolean isDebugLoggingEnabled(){
		return debugLoggingValue;
	}
	
	public PurityPreferences() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(RUN_SANITY_CHECKS, RUN_SANITY_CHECKS_DEFAULT);
		preferences.setDefault(REMOVE_QUALIFIER_SETS, REMOVE_QUALIFIER_SETS_DEFAULT);
		preferences.setDefault(GENERAL_LOGGING, GENERAL_LOGGING_DEFAULT);
		preferences.setDefault(INFERENCE_RULE_LOGGING, INFERENCE_RULE_LOGGING_DEFAULT);
		preferences.setDefault(DEBUG_LOGGING, DEBUG_LOGGING_DEFAULT);
		setPreferenceStore(preferences);
		setDescription("Configure preferences for the Purity Analysis Toolbox plugin.");
		
		if(!initialized){
			getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
					try {
						runSanityChecksValue = Activator.getDefault().getPreferenceStore().getBoolean(RUN_SANITY_CHECKS);
						removeQualifierSetsValue = Activator.getDefault().getPreferenceStore().getBoolean(REMOVE_QUALIFIER_SETS);
						generalLoggingValue = Activator.getDefault().getPreferenceStore().getBoolean(GENERAL_LOGGING);
						inferenceRuleLoggingValue = Activator.getDefault().getPreferenceStore().getBoolean(INFERENCE_RULE_LOGGING);
						debugLoggingValue = Activator.getDefault().getPreferenceStore().getBoolean(DEBUG_LOGGING);
					} catch (Exception e){
						Log.warning("Error accessing purity analysis preferences, using defaults...", e);
					}
				}
			});
			initialized = true;
		}
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(RUN_SANITY_CHECKS, "&" + RUN_SANITY_CHECKS_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(REMOVE_QUALIFIER_SETS, "&" + REMOVE_QUALIFIER_SETS_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(GENERAL_LOGGING, "&" + GENERAL_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(INFERENCE_RULE_LOGGING, "&" + INFERENCE_RULE_LOGGING_DESCRIPTION, getFieldEditorParent()));
		addField(new BooleanFieldEditor(DEBUG_LOGGING, "&" + DEBUG_LOGGING_DESCRIPTION, getFieldEditorParent()));
	}

}
