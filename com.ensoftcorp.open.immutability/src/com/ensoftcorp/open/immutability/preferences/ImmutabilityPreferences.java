package com.ensoftcorp.open.immutability.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ensoftcorp.open.immutability.Activator;
import com.ensoftcorp.open.immutability.log.Log;

public class ImmutabilityPreferences extends AbstractPreferenceInitializer {

	private static boolean initialized = false;
	
	/**
	 * Enable/disable partial program analysis (summaries)
	 * If enabled the type sets are converted to tags and the resulting index can be used to resume analysis later
	 * If disabled the maximal type is extracted from the type set and the qualifier sets are removed
	 */
	public static final String GENERATE_SUMMARIES = "GENERATE_SUMMARIES";
	public static final Boolean GENERATE_SUMMARIES_DEFAULT = false;
	private static boolean generateSummariesValue = GENERATE_SUMMARIES_DEFAULT;
	
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
	
	public static boolean isLoadSummariesEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return loadSummariesValue;
	}
	
	/**
	 * Enable/disable general logging to the Atlas log
	 */
	public static final String GENERAL_LOGGING = "GENERAL_LOGGING";
	public static final Boolean GENERAL_LOGGING_DEFAULT = true;
	private static Boolean generalLoggingValue = GENERAL_LOGGING_DEFAULT;
	
	public static boolean isGeneralLoggingEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return generalLoggingValue;
	}

	/**
	 * Enables/disables inference rule logging to the Atlas log
	 */
	public static final String INFERENCE_RULE_LOGGING = "INFERENCE_RULE_LOGGING";
	public static final Boolean INFERENCE_RULE_LOGGING_DEFAULT = false;
	private static Boolean inferenceRuleLoggingValue = INFERENCE_RULE_LOGGING_DEFAULT;
	
	public static boolean isInferenceRuleLoggingEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return inferenceRuleLoggingValue;
	}
	
	/**
	 * Enables verbose debug logging to the Atlas log
	 */
	public static final String DEBUG_LOGGING = "DEBUG_LOGGING";
	public static final Boolean DEBUG_LOGGING_DEFAULT = false;
	private static Boolean debugLoggingValue = DEBUG_LOGGING_DEFAULT;
	
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
	
	public static boolean isRunSanityChecksEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return runSanityChecksValue;
	}
	
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(GENERATE_SUMMARIES, GENERATE_SUMMARIES_DEFAULT);
		preferences.setDefault(LOAD_SUMMARIES, LOAD_SUMMARIES_DEFAULT);
		preferences.setDefault(RUN_SANITY_CHECKS, RUN_SANITY_CHECKS_DEFAULT);
		preferences.setDefault(GENERAL_LOGGING, GENERAL_LOGGING_DEFAULT);
		preferences.setDefault(INFERENCE_RULE_LOGGING, INFERENCE_RULE_LOGGING_DEFAULT);
		preferences.setDefault(DEBUG_LOGGING, DEBUG_LOGGING_DEFAULT);
	}
	
	/**
	 * Loads or refreshes current preference values
	 */
	public static void loadPreferences() {
		try {
			IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
			runSanityChecksValue = preferences.getBoolean(RUN_SANITY_CHECKS);
			generateSummariesValue = preferences.getBoolean(GENERATE_SUMMARIES);
			loadSummariesValue = preferences.getBoolean(LOAD_SUMMARIES);
			generalLoggingValue = preferences.getBoolean(GENERAL_LOGGING);
			inferenceRuleLoggingValue = preferences.getBoolean(INFERENCE_RULE_LOGGING);
			debugLoggingValue = preferences.getBoolean(DEBUG_LOGGING);
		} catch (Exception e){
			Log.warning("Error accessing immutability analysis preferences, using defaults...", e);
		}
		initialized = true;
	}

}
