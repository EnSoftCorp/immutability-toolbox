package com.ensoftcorp.open.immutability.analysis;

import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.getTypes;
import static com.ensoftcorp.open.immutability.analysis.AnalysisUtilities.removeTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.GraphElement.EdgeDirection;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.analysis.utils.StandardQueries;
import com.ensoftcorp.open.commons.utils.DisplayUtils;
import com.ensoftcorp.open.commons.wishful.StopGap;
import com.ensoftcorp.open.immutability.analysis.checkers.BasicAssignmentChecker;
import com.ensoftcorp.open.immutability.analysis.checkers.CallChecker;
import com.ensoftcorp.open.immutability.analysis.checkers.ConstraintSolver;
import com.ensoftcorp.open.immutability.analysis.checkers.FieldAssignmentChecker;
import com.ensoftcorp.open.immutability.analysis.checkers.SanityChecks;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

/**
 * An Atlas native implementation a context-sensitive reference immutability analysis
 * 
 * This algorithm is based off the algorithm proposed in:
 * Reference 1: ReIm & ReImInfer: Checking and Inference of Reference Immutability, OOPSLA 2012 
 * Reference 2: Method Purity and ReImInfer: Method Purity Inference for Java, FSE 2012
 * 
 * @author Ben Holland, Ganesh Santhanam
 */
public class ImmutabilityAnalysis {

	/**
	 * Tag applied to "pure" methods
	 */
	public static final String PURE_METHOD = "PURE";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "readonly" immutability
	 * Readonly means that in any context the reference is readonly (never mutated)
	 */
	public static final String READONLY = "READONLY";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "polyread" immutability
	 * Polyread means that depending on the context a reference may be mutable or readonly
	 */
	public static final String POLYREAD = "POLYREAD";
	
	/**
	 * Tag applied to fields, parameters, variables, etc. denoting a "mutable" immutability
	 * Mutable means that in any context the reference is mutable
	 */
	public static final String MUTABLE = "MUTABLE";
	
	/**
	 * Tag applied to references that resulted in no immutability types
	 * This tag should ideally never be applied and represents and error in the 
	 * type system or implementation
	 */
	public static final String UNTYPED = "UNTYPED";
	
	/**
	 * Helper for formatting decimal strings
	 */
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##"); 

	/**
	 * Runs the reference immutability analysis
	 * @param monitor 
	 * @return Returns the time in milliseconds taken to complete the analysis
	 */
	public static boolean run(IProgressMonitor monitor){
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Immutability analysis started");
		long start = System.nanoTime();
		boolean isSane = runAnalysis();
		long stop = System.nanoTime();
		double runtime = (stop-start)/1000.0/1000.0;
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) {
			if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
				Log.info("Immutability analysis completed in " + FORMAT.format(runtime) + " ms\n");
			} else {
				long numReadOnly = Common.universe().nodesTaggedWithAny(READONLY).eval().nodes().size();
				long numPolyRead = Common.universe().nodesTaggedWithAny(POLYREAD).eval().nodes().size();
				long numMutable = Common.universe().nodesTaggedWithAny(MUTABLE).eval().nodes().size();
				long numPure = Common.universe().nodesTaggedWithAny(PURE_METHOD).eval().nodes().size();
				String summary = "READONLY: " + numReadOnly + ", POLYREAD: " + numPolyRead + ", MUTABLE: " + numMutable  + ", PURE: " + numPure;
				Log.info("Immutability analysis completed in " + FORMAT.format(runtime) + " ms\n" + summary);
			}
		}
		if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("Constraint Solver Counts: " + ConstraintSolver.constraintOrder.toString());
		return isSane;
	}

	private static class FileResult {
		File file = null;
	}
	
	/**
	 * Runs the reference immutability analysis
	 */
	private static boolean runAnalysis(){
		final FileResult fileResult = new FileResult();
		if(ImmutabilityPreferences.isLoadSummariesEnabled()){
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
					dialog.setFilterNames(new String[] { "Immutability Analysis Results", "All Files (*.*)" });
					dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
					fileResult.file = new File(dialog.open());
				}
			});
			try {
				SummaryUtilities.importSummary(fileResult.file);
			} catch (FileNotFoundException e) {
				DisplayUtils.showError(e, "Could not find summary file.");
			} catch (XMLStreamException e) {
				DisplayUtils.showError(e, "Error parsing summary file.");
			}
			fileResult.file = null;
		}
		if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
			Display.getDefault().syncExec(new Runnable(){
				@Override
				public void run() {
					FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
					dialog.setFilterNames(new String[] { "Immutability Analysis Results", "All Files (*.*)" });
					dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
					try {
						String projectName = Common.universe().nodesTaggedWithAny(XCSG.Project).eval().nodes().getFirst().getAttr(XCSG.name).toString();
						dialog.setFileName(projectName + "-immutability.xml");
					} catch (Exception e){}
					fileResult.file = new File(dialog.open());
				}
			});
		}
		File outputFile = fileResult.file;
		
		if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
			if(outputFile==null){
				Log.warning("No output file selected, immutability results will not be serialized to XML file.");
			} else {
				Log.info("Immutability results will be serialized to " + outputFile.getAbsolutePath());
			}
		}
		
		// TODO: remove when there are appropriate alternatives
		StopGap.addClassVariableAccessTags();
		StopGap.addDataFlowDisplayNodeTags();
		
		AnalysisUtilities.addDummyReturnAssignments();

		AtlasHashSet<Node> worklist = new AtlasHashSet<Node>();

		// add all assignments to worklist
		// treating parameter passes as assignments (for all purposes they are...)
		// this includes dummy return assignments which are fillers for providing 
		// context sensitivity when the return value of a call is unused
		Q assignments = Common.universe().nodesTaggedWithAny(XCSG.Assignment, XCSG.ParameterPass);
		assignments = Common.resolve(new NullProgressMonitor(), assignments);
		for(Node assignment : assignments.eval().nodes()){
			worklist.add(assignment);
		}
		
		int iteration = 1;
		while(true){
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Immutability analysis iteration: " + iteration);
			long startIteration = System.nanoTime();
			
			boolean typesChanged = false;
			for(Node workItem : worklist){
				try {
					if(applyInferenceRules(workItem)){
						typesChanged = true;
					}
				} catch (Exception e){
					Log.error("Error applying inference rules for work item: " + workItem.address().toAddressString() + "\n" + workItem.toString(), e);
					throw e;
				}
			}
			
			long stopIteration = System.nanoTime();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Immutability analysis iteration: " + iteration + " completed in " + FORMAT.format((stopIteration-startIteration)/1000.0/1000.0) + " ms");
			
			// If every reference was in the worklist then theoretically each item
			// should only ever be visited at most 3 times (because in the worst
			// case the first two visits remove 1 immutability type and fixed point is
			// reached on the 3rd visit when there is nothing left to remove since
			// there must be at least one immutability type left, mutable for the case
			// of one type left, for each reference).
			// So for some implementations after 3 iterations the fixed point should have 
			// been reached (and perhaps a 4th iteration to realize it), but...
			// not every reference is placed in our worklist (some references are processed
			// on-demand) so we must run until fixed point.
			if(!typesChanged){
				if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Immutability analysis reached fixed point in " + iteration + " iterations");
				break;
			} else {
				// fixed point has not been reached
				// go for another pass
				iteration++;
			}
		}
		
		if(ImmutabilityPreferences.isGenerateSummariesEnabled()){
			// serialize immutability sets to in Atlas tags
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Converting immutability sets into tags...");
			convertImmutabilityTypesToTags();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Converted immutability sets into tags.");
			
			// serialize field and method tags
			if(outputFile != null){
				try {
					SummaryUtilities.exportSummary(outputFile);
				} catch (Exception e){
					Log.error("Could not save summaries.", e);
					DisplayUtils.showError(e, "Could not save summaries.");
				}
			}
		} else {
			// flattens the type hierarchy to the maximal types
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Extracting maximal types...");
			long startExtraction = System.nanoTime();
			extractMaximalTypes();
			long stopExtraction = System.nanoTime();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Extracted maximal types in " + FORMAT.format((stopExtraction-startExtraction)/1000.0/1000.0) + " ms");
			
			// tags pure methods
			// must be run after extractMaximalTypes
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Applying method immutability tags...");
			long startImmutabilityTagging = System.nanoTime();
			tagPureMethods();
			long stopImmutabilityTagging = System.nanoTime();
			if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Applied method immutability tags in " + FORMAT.format((stopImmutabilityTagging-startImmutabilityTagging)/1000.0/1000.0) + " ms");
		}
		
		boolean isSane = true;
		if(ImmutabilityPreferences.isRunSanityChecksEnabled()){
			Log.info("Running sanity checks...");
			isSane = SanityChecks.run();
			if(isSane){
				Log.info("Sanity checks completed. Everything is sane.");
			} else {
				Log.warning("Sanity checks failed!");
			}
		}
		
		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Performing cleanup...");

		if(ImmutabilityPreferences.isGeneralLoggingEnabled()) Log.info("Removing Immutability Qualifier Sets...");
		AtlasSet<Node> attributedNodes = Common.universe().selectNode(AnalysisUtilities.IMMUTABILITY_QUALIFIERS).eval().nodes();
		AtlasHashSet<Node> attributedNodesToUnattribute = new AtlasHashSet<Node>();
		for(Node attributedNode : attributedNodes){
			attributedNodesToUnattribute.add(attributedNode);
		}
		while(!attributedNodesToUnattribute.isEmpty()){
			Node attributedNode = attributedNodesToUnattribute.getFirst();
			attributedNodesToUnattribute.remove(attributedNode);
			attributedNode.removeAttr(AnalysisUtilities.IMMUTABILITY_QUALIFIERS);
		}
		
		AnalysisUtilities.removeDummyReturnAssignments();
		
		// TODO: remove when there are appropriate alternatives
		StopGap.removeDataFlowDisplayNodeTags();
		StopGap.removeClassVariableAccessTags();
		
		return isSane;
	}

	/**
	 * Given a graph element, each inference rule (TNEW, TASSIGN, TWRITE, TREAD, TCALL) is checked
	 * and unsatisfied qualifier types are removed or reduced (a new type may be added, but it will 
	 * replace other types reducing the total number of types) from graph elements
	 * 
	 * @param workItem Returns true if any type qualifier sets changed
	 * @return
	 */
	private static boolean applyInferenceRules(Node workItem) throws RuntimeException {
		
		boolean typesChanged = false;
		
		Q localDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.LocalDataFlow);
		Q interproceduralDataFlowEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow);
		Q instanceVariableAccessedEdges = Common.universe().edgesTaggedWithAny(XCSG.InstanceVariableAccessed);
		Q identityPassedToEdges = Common.universe().edgesTaggedWithAny(XCSG.IdentityPassedTo);

		// consider data flow edges
		// incoming edges represent a read relationship in an assignment
		// outgoing edges represent a write relationship in an assignment
		Node to = workItem;
		AtlasSet<Edge> inEdges = localDataFlowEdges.reverseStep(Common.toQ(to)).eval().edges();
		for(GraphElement edge : inEdges){
			Node from = edge.getNode(EdgeDirection.FROM);
			boolean involvesField = false;
			
			AtlasSet<Node> toReferences = AnalysisUtilities.parseReferences(to);
			for(Node toReference : toReferences){
				if(toReference.taggedWith(XCSG.ArrayComponents)){
					// an assignment to an array mutates the array
					GraphElement arrayComponents = toReference;
					Q arrayIdentityForEdges = Common.universe().edgesTaggedWithAny(XCSG.ArrayIdentityFor);
					Q arrayWrite = interproceduralDataFlowEdges.predecessors(Common.toQ(arrayComponents));
					for(Node arrayIdentity : arrayIdentityForEdges.predecessors(arrayWrite).eval().nodes()){
						for(Node arrayReference : AnalysisUtilities.parseReferences(arrayIdentity)){
							if(AnalysisUtilities.removeTypes(arrayReference, ImmutabilityTypes.READONLY)){
								typesChanged = true;
							}
						}
					}
				}
			}
			
			// TWRITE
			if(to.taggedWith(XCSG.InstanceVariableAssignment)){
				// Type Rule 3 - TWRITE
				// let, x.f = y
				AtlasSet<Node> yReferences = AnalysisUtilities.parseReferences(from);
				for(Node y : yReferences){
					AtlasSet<Node> fReferences = AnalysisUtilities.parseReferences(to);
					for(Node f : fReferences){
						// Reference (x) -InstanceVariableAccessed-> InstanceVariableAssignment (f=)
						Node instanceVariableAssignment = to; // (f=)
						Node instanceVariableAccessed = instanceVariableAccessedEdges.predecessors(Common.toQ(instanceVariableAssignment)).eval().nodes().getFirst();

						if(instanceVariableAccessed.taggedWith(XCSG.InstanceVariableValue) || instanceVariableAccessed.taggedWith(StopGap.CLASS_VARIABLE_VALUE)){
							// if a field changes in an object then that object and any container 
							// objects which contain an object where the field is have also changed
							// for example z.x.f = y, x is being mutated and so is z
							for(Node container : AnalysisUtilities.getAccessedContainers(instanceVariableAccessed)){
								if(removeTypes(container, ImmutabilityTypes.READONLY)){
									typesChanged = true;
								}
								if(container.taggedWith(XCSG.ClassVariable)){
									if(removeTypes(StandardQueries.getContainingMethod(instanceVariableAccessed), ImmutabilityTypes.READONLY)){
										typesChanged = true;
									}
								}
							}
						}
						
						AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(instanceVariableAccessed);
						for(Node x : xReferences){
							if(FieldAssignmentChecker.handleFieldWrite(x, f, y)){
								typesChanged = true;
							}
						}
					}
				}
				
				involvesField = true;
			}
			
			// TREAD
			if(from.taggedWith(XCSG.InstanceVariableValue)){
				// Type Rule 4 - TREAD
				// let, x = y.f
				AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(to);
				for(Node x : xReferences){
					AtlasSet<Node> fReferences = AnalysisUtilities.parseReferences(from);
					for(Node f : fReferences){
						// Reference (y) -InstanceVariableAccessed-> InstanceVariableValue (.f)
						Node instanceVariableValue = from; // (.f)
						Node instanceVariableAccessed = instanceVariableAccessedEdges.predecessors(Common.toQ(instanceVariableValue)).eval().nodes().getFirst();
						AtlasSet<Node> yReferences = AnalysisUtilities.parseReferences(instanceVariableAccessed);
						for(Node y : yReferences){
							if(FieldAssignmentChecker.handleFieldRead(x, y, f)){
								typesChanged = true;
							}
						}
					}
				}
				
				involvesField = true;
			}
			
			// Type Rule 7 - TSREAD
			// let, x = sf
			if(from.taggedWith(StopGap.CLASS_VARIABLE_VALUE)){
				AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(to);
				for(Node x : xReferences){
					Node m = StandardQueries.getContainingMethod(to);
					AtlasSet<Node> sfReferences = AnalysisUtilities.parseReferences(from);
					for(Node sf : sfReferences){
						if(FieldAssignmentChecker.handleStaticFieldRead(x, sf, m)){
							typesChanged = true;
						}
					}
				}
				
				involvesField = true;
			}
			
			// Type Rule 8 - TSWRITE
			// let, sf = x
			if(to.taggedWith(StopGap.CLASS_VARIABLE_ASSIGNMENT)){
				AtlasSet<Node> sfReferences = AnalysisUtilities.parseReferences(to);
				for(Node sf : sfReferences){
					Node m = StandardQueries.getContainingMethod(to);
					AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(from);
					for(Node x : xReferences){
						if(FieldAssignmentChecker.handleStaticFieldWrite(sf, x, m)){
							typesChanged = true;
						}
					}
				}
				
				involvesField = true;
			}	
			
			// TCALL
			boolean involvesCallsite = false;
			if(from.taggedWith(XCSG.DynamicDispatchCallSite)){
				// Type Rule 5 - TCALL
				// let, x = y.m(z)
				Node containingMethod = StandardQueries.getContainingMethod(to);
				AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(to);
				for(Node x : xReferences){
					Node callsite = from;
					
//					// TODO: consider if this should be updated to use only the method signature
//					// get the callsites invoked signature method
//					Node method = Utilities.getInvokedMethodSignature(callsite);
//					
//					// Method (method) -Contains-> Identity
//					Node identity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes().getFirst();
//					
//					// Method (method) -Contains-> ReturnValue (ret)
//					Node ret = Common.toQ(method).children().nodesTaggedWithAny(XCSG.ReturnValue).eval().nodes().getFirst();
//					
//					// IdentityPass (.this) -IdentityPassedTo-> CallSite (m)
//					AtlasSet<Node> identityPassReferences = identityPassedToEdges.predecessors(Common.toQ(callsite)).eval().nodes();
//					for(Node identityPass : identityPassReferences){
//						// Receiver (r) -LocalDataFlow-> IdentityPass (.this)
//						Node r = localDataFlowEdges.predecessors(Common.toQ(identityPass)).eval().nodes().getFirst();
//						AtlasSet<Node> yReferences = Utilities.parseReferences(r);
//						for(Node y : yReferences){
//							// Method (method) -Contains-> Parameter (p1, p2, ...)
//							AtlasSet<Node> parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
//							
//							// ControlFlow -Contains-> CallSite
//							// CallSite -Contains-> ParameterPassed (z1, z2, ...)
//							AtlasSet<Node> parametersPassed = Common.toQ(callsite).parent().children().nodesTaggedWithAny(XCSG.ParameterPass).eval().nodes();
//							
//							// ParameterPassed (z1, z2, ...) -InterproceduralDataFlow-> Parameter (p1, p2, ...)
//							// such that z1-InterproceduralDataFlow->p1, z2-InterproceduralDataFlow->p2, ...
//							AtlasSet<Edge> parametersPassedEdges = interproceduralDataFlowEdges
//									.betweenStep(Common.toQ(parametersPassed), Common.toQ(parameters)).eval().edges();
//							
//							if(CallChecker.handleCall(x, y, identity, method, ret, parametersPassedEdges, containingMethod)){
//								typesChanged = true;
//							}
//						}
//					}

					// IdentityPass (.this) -IdentityPassedTo-> CallSite (m)
					AtlasSet<Node> identityPassReferences = identityPassedToEdges.predecessors(Common.toQ(callsite)).eval().nodes();
					for(Node identityPass : identityPassReferences){
						// Receiver (receiver) -LocalDataFlow-> IdentityPass (.this)
						Node reciever = localDataFlowEdges.predecessors(Common.toQ(identityPass)).eval().nodes().getFirst();
						AtlasSet<Node> yReferences = AnalysisUtilities.parseReferences(reciever);
						for(Node y : yReferences){
							// ReturnValue (ret) -InterproceduralDataFlow-> CallSite (m)
							Node ret = interproceduralDataFlowEdges.predecessors(Common.toQ(callsite)).eval().nodes().getFirst();

							// Method (method) -Contains-> ReturnValue (ret)
							Node method = Common.toQ(ret).parent().eval().nodes().getFirst();
							
							// Method (method) -Contains-> Identity
							// there should only be one identity node, but in case the graph is malformed this will act as an early prevention measure
							// TODO: assert this property through a sanity check before running this computation
							AtlasSet<Node> identities = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).eval().nodes();
							for(Node identity : identities){
								// Method (method) -Contains-> Parameter (p1, p2, ...)
								AtlasSet<Node> parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
								
								// ControlFlow -Contains-> CallSite
								// CallSite -Contains-> ParameterPassed (z1, z2, ...)
								AtlasSet<Node> parametersPassed = Common.toQ(callsite).parent().children().nodesTaggedWithAny(XCSG.ParameterPass).eval().nodes();
								
								// ParameterPassed (z1, z2, ...) -InterproceduralDataFlow-> Parameter (p1, p2, ...)
								// such that z1-InterproceduralDataFlow->p1, z2-InterproceduralDataFlow->p2, ...
								AtlasSet<Edge> parametersPassedEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow)
										.betweenStep(Common.toQ(parametersPassed), Common.toQ(parameters)).eval().edges();
								
								if(CallChecker.handleCall(x, y, identity, method, ret, parametersPassedEdges, containingMethod)){
									typesChanged = true;
								}
							}
						}
					}
				}
				
				involvesCallsite = true;
			}
			
			// TSCALL
			if(from.taggedWith(XCSG.StaticDispatchCallSite)){
				
				// Type Rule 8 - TSCALL
				// let, x = m(z)
				AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(to);
				for(Node x : xReferences){
					Node callsite = from;

					Node method = AnalysisUtilities.getInvokedMethodSignature(callsite);

					// ReturnValue (ret) -InterproceduralDataFlow-> CallSite (m)
					Node ret = interproceduralDataFlowEdges.predecessors(Common.toQ(callsite)).eval().nodes().getFirst();
					
					// Method (method) -Contains-> Parameter (p1, p2, ...)
					AtlasSet<Node> parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter).eval().nodes();
					
					// ControlFlow -Contains-> CallSite
					// CallSite -Contains-> ParameterPassed (z1, z2, ...)
					AtlasSet<Node> parametersPassed = Common.toQ(callsite).parent().children().nodesTaggedWithAny(XCSG.ParameterPass).eval().nodes();
					
					// ParameterPassed (z1, z2, ...) -InterproceduralDataFlow-> Parameter (p1, p2, ...)
					// such that z1-InterproceduralDataFlow->p1, z2-InterproceduralDataFlow->p2, ...
					AtlasSet<Edge> parametersPassedEdges = Common.universe().edgesTaggedWithAny(XCSG.InterproceduralDataFlow)
							.betweenStep(Common.toQ(parametersPassed), Common.toQ(parameters)).eval().edges();

					if(CallChecker.handleStaticCall(x, callsite, method, ret, parametersPassedEdges)){
						typesChanged = true;
					}
				}
				
				involvesCallsite = true;
			}
			
			// Type Rule 2 - TASSIGN
			// let x = y
			if(!involvesField && !involvesCallsite){
				AtlasSet<Node> xReferences = AnalysisUtilities.parseReferences(to);
				for(Node x : xReferences){
					AtlasSet<Node> yReferences = AnalysisUtilities.parseReferences(from);;
					for(Node y : yReferences){
						if(BasicAssignmentChecker.handleAssignment(x, y)){
							typesChanged = true;
						}
					}
				}
			}
		}
		
		return typesChanged;
	}
	
	public static ImmutabilityTypes getDefaultMaximalType(GraphElement ge) {
		ImmutabilityTypes maximalType;
		if(ge.taggedWith(XCSG.Instantiation) || ge.taggedWith(XCSG.ArrayInstantiation)){
			maximalType = ImmutabilityTypes.MUTABLE;
		} else {
			// all other cases default to readonly as the maximal type
			maximalType = ImmutabilityTypes.READONLY;
		}
		return maximalType;
	}
	
	/**
	 * Converts the immutability types to tags for partial program analysis
	 */
	private static void convertImmutabilityTypesToTags(){
		Q typesToExtract = Common.universe().selectNode(AnalysisUtilities.IMMUTABILITY_QUALIFIERS);
		AtlasSet<Node> attributedNodes = Common.resolve(new NullProgressMonitor(), typesToExtract.eval()).nodes();
		for(GraphElement attributedNode : attributedNodes){
			Set<ImmutabilityTypes> types = getTypes(attributedNode);
			if(types.isEmpty()){
				attributedNode.tag(UNTYPED);
			} else {
				for(ImmutabilityTypes type : types){
					attributedNode.tag(type.toString());
				}
			}
		}
		
		AtlasSet<Node> itemsToTrack = getUntrackedItems(attributedNodes);
		for(GraphElement untouchedTrackedItem : itemsToTrack){
			Set<ImmutabilityTypes> defaultTypes = AnalysisUtilities.getDefaultTypes(untouchedTrackedItem);
			for(ImmutabilityTypes type : defaultTypes){
				untouchedTrackedItem.tag(type.toString());
			}
		}
	}

	private static AtlasSet<Node> getUntrackedItems(AtlasSet<Node> attributedNodes) {
		Q literals = Common.universe().nodesTaggedWithAll(XCSG.Literal);
		Q parameters = Common.universe().nodesTaggedWithAny(XCSG.Parameter);
		Q returnValues = Common.universe().nodesTaggedWithAny(XCSG.ReturnValue);
		Q instanceVariables = Common.universe().nodesTaggedWithAny(XCSG.InstanceVariable);
		Q thisNodes = Common.universe().nodesTaggedWithAny(XCSG.Identity);
		Q classVariables = Common.universe().nodesTaggedWithAny(XCSG.ClassVariable);
		// note local variables may also get tracked, but only if need be during the analysis
		Q trackedItems = literals.union(parameters, returnValues, instanceVariables, thisNodes, classVariables);
		Q untouchedTrackedItems = trackedItems.difference(trackedItems.nodesTaggedWithAny(READONLY, POLYREAD, MUTABLE), Common.toQ(attributedNodes));
		AtlasSet<Node> itemsToTrack = new AtlasHashSet<Node>();
		itemsToTrack.addAll(untouchedTrackedItems.eval().nodes());
		return itemsToTrack;
	}
	
	/**
	 * Flattens the remaining immutability qualifiers to the maximal type
	 * and applies the maximal type as a tag
	 */
	public static void extractMaximalTypes(){
		Q methods = Common.universe().nodesTaggedWithAny(XCSG.Method);
		Q typesToExtract = Common.universe().selectNode(AnalysisUtilities.IMMUTABILITY_QUALIFIERS).difference(methods);
		AtlasSet<Node> attributedNodes = Common.resolve(new NullProgressMonitor(), typesToExtract.eval()).nodes();
		for(Node attributedNode : attributedNodes){
			Set<ImmutabilityTypes> types = getTypes(attributedNode);
			ArrayList<ImmutabilityTypes> orderedTypes = new ArrayList<ImmutabilityTypes>(types.size());
			orderedTypes.addAll(types);
			if(orderedTypes.isEmpty()){
				attributedNode.tag(UNTYPED);
			} else {
				Collections.sort(orderedTypes);
				ImmutabilityTypes maximalType = orderedTypes.get(orderedTypes.size()-1);
				attributedNode.tag(maximalType.toString());
			}
		}
		
		AtlasSet<Node> itemsToTrack = getUntrackedItems(attributedNodes);
		for(GraphElement untouchedTrackedItem : itemsToTrack){
			ImmutabilityTypes maximalType = getDefaultMaximalType(untouchedTrackedItem);
			untouchedTrackedItem.tag(maximalType.toString());
		}
	}
	
	/**
	 * Tags pure methods with "PURE"
	 */
	public static void tagPureMethods(){
		AtlasSet<Node> methods = Common.universe().nodesTaggedWithAny(XCSG.Method).eval().nodes();
		for(GraphElement method : methods){
			if(isPureMethod(method)){
				method.tag(PURE_METHOD);
			}
		}
	}
	
	/**
	 * Returns true if the method is pure
	 * Assumes the maximal immutability qualifiers have already been extracted
	 * @param method
	 */
	private static boolean isPureMethod(GraphElement method){
		if(!method.taggedWith(XCSG.Method)){
			return false;
		} else if(isPureMethodDefault(method)){
			return true;
		} else {
			// from reference 1 section 3
			// a method is pure if 
			// 1) it does not mutate prestates reachable through parameters
			// this includes the formal parameters and implicit "this" parameter
			Q parameters = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Parameter);
			Q mutableParameters = parameters.nodesTaggedWithAny(MUTABLE, POLYREAD);
			if(mutableParameters.eval().nodes().size() > 0){
				return false;
			}
			Q mutableIdentity = Common.toQ(method).children().nodesTaggedWithAny(XCSG.Identity).nodesTaggedWithAny(MUTABLE, POLYREAD);
			if(mutableIdentity.eval().nodes().size() > 0){
				return false;
			}
			
			// 2) it does not mutate prestates reachable through static fields
			if(method.taggedWith(MUTABLE)){
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * Returns true if the method is a default pure method
	 * @param method
	 * @return
	 */
	private static boolean isPureMethodDefault(GraphElement method){
		// note by convention .equals, .hashCode, .toString, and .compareTo
		// are pure methods, but this is not enforced in overridden methods
		// so we are not assuming it to be universally true (unlike ReIm)
		
		// we could however consider some of the java.lang.Object native methods as pure
		// Object's native methods include: getClass, clone, hashCode, notifyAll, notify, wait, registerNatives
		if(method.taggedWith(XCSG.Java.nativeMethod)){
			if(!Common.toQ(method).intersection(Common.typeSelect("java.lang", "Object").children()).eval().nodes().isEmpty()){
				if(method.getAttr(XCSG.name).equals("getClass")){
					return true;
				}
				if(method.getAttr(XCSG.name).equals("hashCode")){
					return true;
				}
				if(method.getAttr(XCSG.name).equals("clone")){
					// clone is a pure method, but it is also a special case
					// to be consider since its return type is a duplication 
					// of a reference
					// see https://en.wikipedia.org/wiki/Clone_(Java_method)
					return true;
				}
				return false;
			}
		}

		return false;
	}
	
}
