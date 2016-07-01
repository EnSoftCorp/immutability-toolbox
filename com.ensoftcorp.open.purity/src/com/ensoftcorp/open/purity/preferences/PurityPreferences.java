package com.ensoftcorp.open.purity.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ensoftcorp.open.purity.Activator;
import com.ensoftcorp.open.purity.log.Log;

public class PurityPreferences extends AbstractPreferenceInitializer {

	private static boolean initialized = false;
	
	/**
	 * Enable/disable partial program analysis
	 * If enabled the maximal type is extracted from the type set and the qualifier sets are removed
	 * If disabled the type sets are converted to tags and the resulting index can be used to resume analysis later
	 */
	public static final String PARTIAL_PROGRAM_ANALYSIS = "PARTIAL_PROGRAM_ANALYSIS";
	public static final Boolean PARTIAL_PROGRAM_ANALYSIS_DEFAULT = false;
	public static boolean partialProgramAnalysisValue = PARTIAL_PROGRAM_ANALYSIS_DEFAULT;
	
	public static boolean isPartialProgramAnalysisEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return partialProgramAnalysisValue;
	}
	
	/**
	 * Enable/disable running sanity checks
	 */
	public static final String RUN_SANITY_CHECKS = "RUN_SANITY_CHECKS";
	public static final Boolean RUN_SANITY_CHECKS_DEFAULT = true;
	public static boolean runSanityChecksValue = RUN_SANITY_CHECKS_DEFAULT;
	
	/**
	 * Enable/disable general logging to the Atlas log
	 */
	public static final String GENERAL_LOGGING = "GENERAL_LOGGING";
	public static final Boolean GENERAL_LOGGING_DEFAULT = true;
	public static Boolean generalLoggingValue = GENERAL_LOGGING_DEFAULT;
	
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
	public static Boolean inferenceRuleLoggingValue = INFERENCE_RULE_LOGGING_DEFAULT;
	
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
	public static Boolean debugLoggingValue = DEBUG_LOGGING_DEFAULT;
	
	public static boolean isDebugLoggingEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return debugLoggingValue;
	}
	
	public static boolean isRunSanityChecksEnabled(){
		if(!initialized){
			loadPreferences();
		}
		return runSanityChecksValue;
	}
	
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		preferences.setDefault(PARTIAL_PROGRAM_ANALYSIS, PARTIAL_PROGRAM_ANALYSIS_DEFAULT);
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
			partialProgramAnalysisValue = preferences.getBoolean(PARTIAL_PROGRAM_ANALYSIS);
			generalLoggingValue = preferences.getBoolean(GENERAL_LOGGING);
			inferenceRuleLoggingValue = preferences.getBoolean(INFERENCE_RULE_LOGGING);
			debugLoggingValue = preferences.getBoolean(DEBUG_LOGGING);
		} catch (Exception e){
			Log.warning("Error accessing purity analysis preferences, using defaults...", e);
		}
		initialized = true;
	}

}
