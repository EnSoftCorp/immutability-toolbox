package com.ensoftcorp.open.immutability.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ensoftcorp.open.immutability.Activator;
import com.ensoftcorp.open.immutability.log.Log;

public class ImmutabilityPreferences extends AbstractPreferenceInitializer {

	private static boolean initialized = false;
	
	/**
	 * Enable/disable immutability analysis
	 */
	public static final String RUN_IMMUTABILITY_ANALYSIS = "RUN_IMMUTABILITY_ANALYSIS";
	public static final Boolean RUN_IMMUTABILITY_ANALYSIS_DEFAULT = false;
	private static boolean runImmutabilityAnalysisValue = RUN_IMMUTABILITY_ANALYSIS_DEFAULT;
	
	/**
	 * Configures whether or not immutability analysis should be run
	 */
	public static void enabledPointsToAnalysis(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(RUN_IMMUTABILITY_ANALYSIS, enabled);
		loadPreferences();
	}
	
	/**
	 * Returns true if immutability analysis is enabled
	 * @return
	 */
	public static boolean isImmutabilityAnalysisEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return runImmutabilityAnalysisValue;
	}
	
	public static final String IMMUTABILITY_ANALYSIS_MODE = "IMMUTABILITY_ANALYSIS_MODE";
	public static final String IMMUTABILITY_ANALYSIS_INFERENCE_MODE = "IMMUTABILITY_ANALYSIS_INFERENCE_MODE";
	public static final String IMMUTABILITY_ANALYSIS_POINTSTO_MODE = "IMMUTABILITY_ANALYSIS_POINTSTO_MODE";
	public static final String IMMUTABILITY_ANALYSIS_MODE_DEFAULT = IMMUTABILITY_ANALYSIS_INFERENCE_MODE;
	private static String analysisModeValue = IMMUTABILITY_ANALYSIS_MODE_DEFAULT;
	
	/**
	 * Configures immutability analysis mode to run using points-to analysis
	 */
	public static void setPointsToAnalysisMode(){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(IMMUTABILITY_ANALYSIS_MODE, IMMUTABILITY_ANALYSIS_POINTSTO_MODE);
		loadPreferences();
	}
	
	/**
	 * Returns true if points-to mode is enabled
	 * @return
	 */
	public static boolean isPointsToAnalysisModeEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return analysisModeValue.equals(IMMUTABILITY_ANALYSIS_POINTSTO_MODE);
	}
	
	/**
	 * Configures immutability analysis mode to run using inference analysis
	 */
	public static void setInferenceAnalysisMode(){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(IMMUTABILITY_ANALYSIS_MODE, IMMUTABILITY_ANALYSIS_INFERENCE_MODE);
		loadPreferences();
	}
	
	/**
	 * Returns true if inference mode is enabled
	 * @return
	 */
	public static boolean isInferenceAnalysisModeEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return analysisModeValue.equals(IMMUTABILITY_ANALYSIS_INFERENCE_MODE);
	}
	
	/**
	 * Enable/disable general logging to the Atlas log
	 */
	public static final String GENERAL_LOGGING = "GENERAL_LOGGING";
	public static final Boolean GENERAL_LOGGING_DEFAULT = true;
	private static Boolean generalLoggingValue = GENERAL_LOGGING_DEFAULT;
	
	/**
	 * Configures general logging
	 */
	public static void enableGeneralLogging(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(GENERAL_LOGGING, enabled);
		loadPreferences();
	}
	
	public static boolean isGeneralLoggingEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return generalLoggingValue;
	}
	
	/**
	 * Enables verbose debug logging to the Atlas log
	 */
	public static final String DEBUG_LOGGING = "DEBUG_LOGGING";
	public static final Boolean DEBUG_LOGGING_DEFAULT = false;
	private static Boolean debugLoggingValue = DEBUG_LOGGING_DEFAULT;
	
	/**
	 * Configures debug logging
	 */
	public static void enableDebugLogging(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(DEBUG_LOGGING, enabled);
		loadPreferences();
	}
	
	public static boolean isDebugLoggingEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return debugLoggingValue;
	}
	
	/**
	 * Enable/disable running sanity checks
	 */
	public static final String RUN_SANITY_CHECKS = "RUN_SANITY_CHECKS";
	public static final Boolean RUN_SANITY_CHECKS_DEFAULT = true;
	private static boolean runSanityChecksValue = RUN_SANITY_CHECKS_DEFAULT;
	
	/**
	 * Configures running sanity checks
	 */
	public static void enableSanityChecks(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(RUN_SANITY_CHECKS, enabled);
		loadPreferences();
	}
	
	public static boolean isRunSanityChecksEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return runSanityChecksValue;
	}
	
	/**
	 * Enable/disable partial program analysis (summaries)
	 * If enabled the type sets are converted to tags and the resulting index can be used to resume analysis later
	 * If disabled the maximal type is extracted from the type set and the qualifier sets are removed
	 */
	public static final String GENERATE_SUMMARIES = "GENERATE_SUMMARIES";
	public static final Boolean GENERATE_SUMMARIES_DEFAULT = false;
	private static boolean generateSummariesValue = GENERATE_SUMMARIES_DEFAULT;
	
	/**
	 * Configures summary generations
	 */
	public static void enableGenerateSummaries(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(GENERATE_SUMMARIES, enabled);
		loadPreferences();
	}
	
	public static boolean isGenerateSummariesEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return generateSummariesValue;
	}
	
	/**
	 * Enable/disable importing existing summaries
	 * If enabled existing summaries will be applied to the index before any other operations
	 */
	public static final String LOAD_SUMMARIES = "LOAD_SUMMARIES";
	public static final Boolean LOAD_SUMMARIES_DEFAULT = false;
	private static boolean loadSummariesValue = LOAD_SUMMARIES_DEFAULT;
	
	/**
	 * Configures loading summaries
	 */
	public static void enableLoadSummaries(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(LOAD_SUMMARIES, enabled);
		loadPreferences();
	}
	
	public static boolean isLoadSummariesEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return loadSummariesValue;
	}
	
	/**
	 * Enable/disable whether instance variables may be set to mutable
	 */
	public static final String ALLOW_ADD_MUTABLE_INSTANCE_VARIABLES = "ALLOW_MUTABLE_INSTANCE_VARIABLES";
	public static final Boolean ALLOW_ADD_MUTABLE_INSTANCE_VARIABLES_DEFAULT = false;
	private static boolean allowAddMutableInstanceVariablesValue = ALLOW_ADD_MUTABLE_INSTANCE_VARIABLES_DEFAULT;
	
	/**
	 * Configures allowing added mutable instance variable
	 */
	public static void enableAllowAddMutableInstanceVariables(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(ALLOW_ADD_MUTABLE_INSTANCE_VARIABLES, enabled);
		loadPreferences();
	}
	
	public static boolean isAllowAddMutableInstanceVariablesEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return allowAddMutableInstanceVariablesValue;
	}
	
	/**
	 * Enable/disable whether instance variables may be include mutables by deafault
	 */
	public static final String ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES = "ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES";
	public static final Boolean ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES_DEFAULT = true;
	private static boolean allowDefaultMutableInstancesVariablesValue = ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES_DEFAULT;
	
	/**
	 * Configures allowing default mutable instance variable
	 */
	public static void enableAllowDefaultMutableInstancesVariables(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES, enabled);
		loadPreferences();
	}
	
	public static boolean isAllowDefaultMutableInstancesVariablesEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return allowDefaultMutableInstancesVariablesValue;
	}
	
	/**
	 * Enable/disable container mutations
	 */
	public static final String CONSIDER_CONTAINERS = "CONSIDER_CONTAINERS";
	public static final Boolean CONSIDER_CONTAINERS_DEFAULT = true;
	private static boolean considerContainersValue = CONSIDER_CONTAINERS_DEFAULT;
	
	/**
	 * Configures container consideration
	 */
	public static void enableContainerConsideration(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(CONSIDER_CONTAINERS, enabled);
		loadPreferences();
	}
	
	public static boolean isContainerConsiderationEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return considerContainersValue;
	}
	
	/**
	 * Enable/disable container mutations
	 */
	public static final String USE_FIELD_ADAPTATIONS = "CONSIDER_CONTAINERS";
	public static final Boolean USE_FIELD_ADAPTATIONS_DEFAULT = true;
	private static boolean useFieldAdaptationsValue = USE_FIELD_ADAPTATIONS_DEFAULT;
	
	/**
	 * Configures field adaptations
	 */
	public static void enableFieldAdaptations(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(USE_FIELD_ADAPTATIONS, enabled);
		loadPreferences();
	}
	
	public static boolean isFieldAdaptationsEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return useFieldAdaptationsValue;
	}
	
	/**
	 * Enables/disables inference rule logging to the Atlas log
	 */
	public static final String INFERENCE_RULE_LOGGING = "INFERENCE_RULE_LOGGING";
	public static final Boolean INFERENCE_RULE_LOGGING_DEFAULT = false;
	private static Boolean inferenceRuleLoggingValue = INFERENCE_RULE_LOGGING_DEFAULT;
	
	/**
	 * Configures inference rule logging
	 */
	public static void enableInferenceRuleLogging(boolean enabled){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(INFERENCE_RULE_LOGGING, enabled);
		loadPreferences();
	}
	
	public static boolean isInferenceRuleLoggingEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return inferenceRuleLoggingValue;
	}
	
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		setDefaults(preferences);
	}
	
	/**
	 * Restores the default preferences
	 */
	public static void restoreDefaults(){
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		setDefaults(preferences);
		loadPreferences();
	}
	
	/**
	 * Helper method to set default preferences
	 * @param preferences
	 */
	private static void setDefaults(IPreferenceStore preferences) {
		preferences.setDefault(RUN_IMMUTABILITY_ANALYSIS, RUN_IMMUTABILITY_ANALYSIS_DEFAULT);
		preferences.setDefault(IMMUTABILITY_ANALYSIS_MODE, IMMUTABILITY_ANALYSIS_INFERENCE_MODE);
		preferences.setDefault(RUN_SANITY_CHECKS, RUN_SANITY_CHECKS_DEFAULT);
		preferences.setDefault(GENERAL_LOGGING, GENERAL_LOGGING_DEFAULT);
		preferences.setDefault(DEBUG_LOGGING, DEBUG_LOGGING_DEFAULT);
		preferences.setDefault(ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES, ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES_DEFAULT);
		preferences.setDefault(GENERATE_SUMMARIES, GENERATE_SUMMARIES_DEFAULT);
		preferences.setDefault(LOAD_SUMMARIES, LOAD_SUMMARIES_DEFAULT);
		preferences.setDefault(ALLOW_ADD_MUTABLE_INSTANCE_VARIABLES, ALLOW_ADD_MUTABLE_INSTANCE_VARIABLES_DEFAULT);
		preferences.setDefault(CONSIDER_CONTAINERS, CONSIDER_CONTAINERS_DEFAULT);
		preferences.setDefault(USE_FIELD_ADAPTATIONS, USE_FIELD_ADAPTATIONS_DEFAULT);
		preferences.setDefault(INFERENCE_RULE_LOGGING, INFERENCE_RULE_LOGGING_DEFAULT);
	}
	
	/**
	 * Loads or refreshes current preference values
	 */
	public static void loadPreferences() {
		try {
			IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
			runImmutabilityAnalysisValue = preferences.getBoolean(RUN_IMMUTABILITY_ANALYSIS);
			analysisModeValue = preferences.getString(IMMUTABILITY_ANALYSIS_MODE);
			runSanityChecksValue = preferences.getBoolean(RUN_SANITY_CHECKS);
			generalLoggingValue = preferences.getBoolean(GENERAL_LOGGING);
			debugLoggingValue = preferences.getBoolean(DEBUG_LOGGING);
			allowDefaultMutableInstancesVariablesValue = preferences.getBoolean(ALLOW_DEFAULT_MUTABLE_INSTANCE_VARIABLES);
			generateSummariesValue = preferences.getBoolean(GENERATE_SUMMARIES);
			loadSummariesValue = preferences.getBoolean(LOAD_SUMMARIES);
			allowAddMutableInstanceVariablesValue = preferences.getBoolean(ALLOW_ADD_MUTABLE_INSTANCE_VARIABLES);
			considerContainersValue = preferences.getBoolean(CONSIDER_CONTAINERS);
			inferenceRuleLoggingValue = preferences.getBoolean(INFERENCE_RULE_LOGGING);
			useFieldAdaptationsValue = preferences.getBoolean(USE_FIELD_ADAPTATIONS);
		} catch (Exception e){
			Log.warning("Error accessing immutability analysis preferences, using defaults...", e);
		}
		initialized = true;
	}

}
