package com.ensoftcorp.open.immutability.analysis.filters;

import java.util.Map;

import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.filters.InvalidFilterParameterException;
import com.ensoftcorp.open.commons.filters.NodeFilter;
import com.ensoftcorp.open.immutability.constants.ImmutabilityTags;

public class PurityFilter extends NodeFilter {

	private static final String FILTER_PURE_FUNCTIONS = "FILTER_PURE_FUNCTIONS";
	private static final String FILTER_FUNCTIONS_WITH_SIDE_EFFECTS = "FILTER_FUNCTIONS_WITH_SIDE_EFFECTS";
	
	public PurityFilter(){
		this.addPossibleParameter(FILTER_PURE_FUNCTIONS, Boolean.class, false);
		this.addPossibleParameter(FILTER_FUNCTIONS_WITH_SIDE_EFFECTS, Boolean.class, false);
		this.setMinimumNumberParametersRequired(1);
	}
	
	@Override
	public String getName() {
		return "Purity";
	}

	@Override
	public String getDescription() {
		return "Filters functions based on whether or not they are pure (have side effects).";
	}

	@Override
	protected String[] getSupportedNodeTags() {
		return new String[]{ XCSG.Method };
	}

	@Override
	public Q filter(Q input, Map<String, Object> parameters) throws InvalidFilterParameterException {
		checkParameters(parameters);
		input = super.filter(input, parameters);
		
		if(isParameterSet(FILTER_PURE_FUNCTIONS, parameters)){
			if((Boolean) getParameterValue(FILTER_PURE_FUNCTIONS, parameters)){
				Q pureMethods = Common.universe().nodesTaggedWithAny(ImmutabilityTags.PURE_METHOD);
				input = input.difference(pureMethods);
			}
		}
		
		if(isParameterSet(FILTER_FUNCTIONS_WITH_SIDE_EFFECTS, parameters)){
			if((Boolean) getParameterValue(FILTER_FUNCTIONS_WITH_SIDE_EFFECTS, parameters)){
				Q allMethods = Common.universe().nodes(XCSG.Method);
				Q pureMethods = allMethods.nodesTaggedWithAny(ImmutabilityTags.PURE_METHOD);
				input = input.difference(allMethods.difference(pureMethods));
			}
		}
		
		return input;
	}

}
