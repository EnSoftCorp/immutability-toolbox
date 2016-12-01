package com.ensoftcorp.open.immutability.analysis;

import com.ensoftcorp.open.immutability.constants.ImmutabilityTags;

/**
 * Encodes the immutability qualifications as types 
 * 
 * @author Ben Holland
 */
public enum ImmutabilityTypes {
	// note that MUTABLE <: POLYREAD <: READONLY
	// <: denotes a subtype relationship
	// MUTABLE is a subtype of POLYREAD and POLYREAD is a subtype of READONLY
	// MUTABLE is the most specific type and READONLY is the most generic type
	MUTABLE(ImmutabilityTags.MUTABLE), POLYREAD(ImmutabilityTags.POLYREAD), READONLY(ImmutabilityTags.READONLY);
	
	private String name;
	
	private ImmutabilityTypes(String name){
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	/**
	 * Viewpoint adaptation is a concept from Universe Types, 
	 * it deals with context-sensitivity issues.
	 * 
	 * Specifically, 
	 * context=? and declaration=readonly => readonly
	 * context=? and declaration=mutable => mutable
	 * context=q and declaration=polyread => q
	 * 
	 * @param context
	 * @param declared
	 * @return
	 */
	public static ImmutabilityTypes getAdaptedFieldViewpoint(ImmutabilityTypes context, ImmutabilityTypes declaration){
//		// see https://github.com/SoftwareEngineeringToolDemos/FSE-2012-ReImInfer/blob/master/inference-framework/checker-framework/checkers/src/checkers/inference/reim/ReimChecker.java#L216
		// FOOL2012 implementation
		if(declaration == ImmutabilityTypes.READONLY){
			// q and READONLY = READONLY
			return ImmutabilityTypes.READONLY;
		} else if(declaration == ImmutabilityTypes.MUTABLE){
			// q and MUTABLE = q
			return context;
		} else {
			// declared must be ImmutabilityTypes.POLYREAD
			// q and POLYREAD = q
			return context;
		}
		
		// vanilla OOPSLA implementation
		//  ReIm' definition of field viewpoint adaptation
		// see https://github.com/proganalysis/type-inference/blob/master/inference-framework/checker-framework/checkers/src/checkers/inference2/reim/ReimChecker.java#L272
//		return getAdaptedMethodViewpoint(context, declaration);
	}
	
	/**
	 * Viewpoint adaptation is a concept from Universe Types, 
	 * it deals with context-sensitivity issues.
	 * 
	 * Specifically, 
	 * context=? and declaration=readonly => readonly
	 * context=? and declaration=mutable => mutable
	 * context=q and declaration=polyread => q
	 * 
	 * @param context
	 * @param declared
	 * @return
	 */
	public static ImmutabilityTypes getAdaptedMethodViewpoint(ImmutabilityTypes context, ImmutabilityTypes declaration){
		if(declaration == ImmutabilityTypes.READONLY){
			// ? and READONLY = READONLY
			return ImmutabilityTypes.READONLY;
		} else if(declaration == ImmutabilityTypes.MUTABLE){
			// ? and MUTABLE = MUTABLE
			return ImmutabilityTypes.MUTABLE;
		} else {
			// declared must be ImmutabilityTypes.POLYREAD
			// q and POLYREAD = q
			return context;
		}
	}
}