package com.ensoftcorp.open.immutability.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.open.commons.wishful.StopGap;
import com.ensoftcorp.open.immutability.log.Log;
import com.ensoftcorp.open.immutability.preferences.ImmutabilityPreferences;

public class SummaryUtilities {
	
	private static Field field;
	
	private static class Field {
		String type;
		String pkg;
		String parentClass;
		String name;
		String immutabilityQualifiers;
		
		@Override
		public String toString() {
			return "Field [type=" + type + ", package=" + pkg + ", class=" + parentClass + ", name=" + name
					+ ", immutabilityQualifiers=" + immutabilityQualifiers + "]";
		}
	}
	
	private static Method method;
	
	private static class Method {
		String type;
		String pkg;
		String parentClass;
		String signature;
		String immutabilityQualifiers;
		List<Parameter> parameters = new LinkedList<Parameter>();
		String identityImmutabilityQualifiers;
		String returnImmutabilityQualifiers;
		
		@Override
		public String toString() {
			return "Method [type=" + type + ", package=" + pkg + ", class=" + parentClass + ", signature=" + signature
					+ ", immutabilityQualifiers=" + immutabilityQualifiers + ", parameters=" + parameters
					+ ", identityImmutabilityQualifiers=" + identityImmutabilityQualifiers
					+ ", returnImmutabilityQualifiers=" + returnImmutabilityQualifiers + "]";
		}
		
		private static class Parameter {
			int index;
			String immutabilityQualifiers;
			
			@Override
			public String toString() {
				return "Parameter [index=" + index + ", immutabilityQualifiers=" + immutabilityQualifiers + "]";
			}
		}
	}
	
	private static int fieldsImported = 0;
	private static int fieldsSummarized = 0;
	private static int methodsImported = 0;
	private static int methodsSummarized = 0;
	
	private static Version atlasVersion = Platform.getBundle("com.ensoftcorp.atlas.core").getVersion();
	private static Version immutabilityToolboxVersion = Platform.getBundle("com.ensoftcorp.open.immutability").getVersion();
	
	public static void importSummary(File inputXMLFile) throws FileNotFoundException, XMLStreamException {
		fieldsImported = 0;
		fieldsSummarized = 0;
		methodsImported = 0;
		methodsSummarized = 0;
		Log.info("Importing summary: " + inputXMLFile.getAbsolutePath());
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLStreamReader xmlr = xmlif.createXMLStreamReader(new FileReader(inputXMLFile));
		while (xmlr.hasNext()) {
			processEvent(xmlr);
			xmlr.next();
		}
		xmlr.close();
		String message = "Imported " + fieldsImported + "/" + fieldsSummarized + " field summaries.";
		message += "\nImported " + methodsImported + "/" + methodsSummarized + " method summaries.";
		Log.info("Summary imported.\n" + message);
		
		if(fieldsImported == 0 && methodsImported == 0){
			Log.warning("No matching summaries were imported!");
		}
	}

	private static void processEvent(XMLStreamReader xmlr) {
		switch (xmlr.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				String startElementName = xmlr.getLocalName();
				if(startElementName.equals("field")){
					field = new Field();
					for(int i=0; i < xmlr.getAttributeCount(); i++){
						String name = xmlr.getAttributeLocalName(i);
						String value = xmlr.getAttributeValue(i);
						if (name.equals("type")) {
							field.type = value;
						} else if (name.equals("class")) {
							field.parentClass = value;
						} else if (name.equals("package")) {
							field.pkg = value;
						} else if (name.equals("name")) {
							field.name = value;
						} else if (name.equals("immutability")) {
							field.immutabilityQualifiers = value;
						}
					}
				} else if(startElementName.equals("method")){
					method = new Method();
					for(int i=0; i < xmlr.getAttributeCount(); i++){
						String name = xmlr.getAttributeLocalName(i);
						String value = xmlr.getAttributeValue(i);
						if (name.equals("type")) {
							method.type = value;
						} else if (name.equals("class")) {
							method.parentClass = value;
						} else if (name.equals("package")) {
							method.pkg = value;
						} else if (name.equals("signature")) {
							method.signature = value;
						} else if (name.equals("immutability")) {
							method.immutabilityQualifiers = value;
						}
					}
				} else if(startElementName.equals("this")){
					for(int i=0; i < xmlr.getAttributeCount(); i++){
						String name = xmlr.getAttributeLocalName(i);
						String value = xmlr.getAttributeValue(i);
						if (name.equals("immutability")) {
							method.identityImmutabilityQualifiers = value;
						} 
					}
				} else if(startElementName.equals("return")){
					for(int i=0; i < xmlr.getAttributeCount(); i++){
						String name = xmlr.getAttributeLocalName(i);
						String value = xmlr.getAttributeValue(i);
						if (name.equals("immutability")) {
							method.returnImmutabilityQualifiers = value;
						} 
					}
				} else if(startElementName.equals("parameter")){
					Method.Parameter parameter = new Method.Parameter();
					for(int i=0; i < xmlr.getAttributeCount(); i++){
						String name = xmlr.getAttributeLocalName(i);
						String value = xmlr.getAttributeValue(i);
						if(name.equals("index")){
							parameter.index = Integer.parseInt(value);
						} else if (name.equals("immutability")) {
							parameter.immutabilityQualifiers = value;
						} 
					}
					method.parameters.add(parameter);
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				String endElementName = xmlr.getLocalName();
				if(endElementName.equals("field") && field != null){
					fieldsSummarized++;
					tagField(field);
					field = null;
				} else if(endElementName.equals("method") && method != null){
					methodsSummarized++;
					tagMethod(method);
					method = null;
				}
				break;
			default:
				break;
		}
	}
	
	private static void tagMethod(Method method) {
		Q packages = Common.universe().nodesTaggedWithAny(XCSG.Package).selectNode(XCSG.name, method.pkg);
		Q parents = packages.contained().nodesTaggedWithAny(XCSG.Type).selectNode(XCSG.name, method.parentClass);
		Q methods = parents.children().nodesTaggedWithAny(method.type);
		methods = methods.selectNode(StopGap.SIGNATURE, method.signature);
		
		AtlasSet<Node> methodNodes = methods.eval().nodes();
		
		if(methodNodes.isEmpty()){
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("No matching method for imported method.\n" + method.toString());
		} else if(methodNodes.size() > 1){
			Log.warning("Multiple matches for imported method.\n" + method.toString());
		}
		// apply imported tags
		for(Node methodNode : methodNodes){
			methodsImported++;
			setImmutabilityQualifierSet(methodNode, method.immutabilityQualifiers);
			if(method.identityImmutabilityQualifiers != null){
				Node identityNode = Common.toQ(methodNode).children().nodesTaggedWithAll(XCSG.Identity).eval().nodes().getFirst();
				if(identityNode != null){
					setImmutabilityQualifierSet(identityNode, method.identityImmutabilityQualifiers);
				} else {
					Log.warning("Missing identity node for method: " +  methodNode.address().toAddressString());
				}
			}
			if(method.returnImmutabilityQualifiers != null){
				Node returnNode = Common.toQ(methodNode).children().nodesTaggedWithAll(XCSG.ReturnValue).eval().nodes().getFirst();
				if(returnNode != null){
					setImmutabilityQualifierSet(returnNode, method.returnImmutabilityQualifiers);
				} else {
					Log.warning("Missing return value node for method: " +  methodNode.address().toAddressString());
				}
			}
			Q methodParameters = Common.toQ(methodNode).children().nodesTaggedWithAny(XCSG.Parameter);
			for(Method.Parameter parameter : method.parameters){
				Node parameterNode = methodParameters.selectNode(XCSG.parameterIndex, parameter.index).eval().nodes().getFirst();
				if(parameterNode != null){
					setImmutabilityQualifierSet(parameterNode, parameter.immutabilityQualifiers);
				} else {
					Log.warning("Missing parameter node (index=" + parameter.index + ") for method: " +  methodNode.address().toAddressString());
				}
			}
		}
	}
	
	private static void tagField(Field field) {
		Q packages = Common.universe().nodesTaggedWithAny(XCSG.Package).selectNode(XCSG.name, field.pkg);
		Q parents = packages.contained().nodesTaggedWithAny(XCSG.Type).selectNode(XCSG.name, field.parentClass);
		Q fields = parents.children().nodesTaggedWithAny(field.type);
		fields = fields.selectNode(XCSG.name, field.name);
		
		AtlasSet<Node> fieldNodes = fields.eval().nodes();
		
		if(fieldNodes.isEmpty()){
			if(ImmutabilityPreferences.isDebugLoggingEnabled()) Log.info("No matching field for imported field.\n" + field.toString());
		} else if(fieldNodes.size() > 1){
			Log.warning("Multiple matches for imported field.\n" + field.toString());
		}
		
		// apply imported tags
		for(Node fieldNode : fieldNodes){
			fieldsImported++;
			setImmutabilityQualifierSet(fieldNode, field.immutabilityQualifiers);
		}
	}
	
	private static Set<ImmutabilityTypes> setImmutabilityQualifierSet(Node node, String immutabilityQualifiersString) {
		EnumSet<ImmutabilityTypes> qualifiers = EnumSet.noneOf(ImmutabilityTypes.class);
		for(String immutabilityQualifier : immutabilityQualifiersString.trim().split(",")){
			if(immutabilityQualifier.equals(ImmutabilityTypes.READONLY.toString())){
				qualifiers.add(ImmutabilityTypes.READONLY);
			} else if(immutabilityQualifier.equals(ImmutabilityTypes.POLYREAD.toString())){
				qualifiers.add(ImmutabilityTypes.POLYREAD);
			} else if(immutabilityQualifier.equals(ImmutabilityTypes.MUTABLE.toString())){
				qualifiers.add(ImmutabilityTypes.MUTABLE);
			} else {
				Log.warning("Importing untyped qualifier for node: " + node.address().toAddressString());
				node.tag(ImmutabilityAnalysis.UNTYPED);
			}
		}
		return qualifiers;
	}

	public static void exportSummary(File outputXMLFile) throws FileNotFoundException, XMLStreamException {
		fieldsSummarized=0;
		methodsSummarized=0;
		Log.info("Exporting summary: " + outputXMLFile.getAbsolutePath());
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = output.createXMLStreamWriter(new FileOutputStream(outputXMLFile));
		writer.writeStartDocument();
		
		writer.writeStartElement("immutability");
		writer.writeAttribute("atlas", atlasVersion.getMajor() + "." + atlasVersion.getMinor() + "." + atlasVersion.getMicro());
		writer.writeAttribute("immutability-toolbox", immutabilityToolboxVersion.getMajor() + "." + immutabilityToolboxVersion.getMinor() + "." + immutabilityToolboxVersion.getMicro());
		
		for(Node field : Common.universe().nodesTaggedWithAny(XCSG.Field).nodesTaggedWithAny(ImmutabilityAnalysis.READONLY, ImmutabilityAnalysis.POLYREAD, ImmutabilityAnalysis.MUTABLE, ImmutabilityAnalysis.UNTYPED).eval().nodes()){
			try {
				serializeField(field, writer);
			} catch (Exception e){
				Log.error("Error serializing field: " + field.address().toAddressString(), e);
				throw e;
			}
			writer.flush();
			fieldsSummarized++;
		}
		
		for(Node method : Common.universe().nodesTaggedWithAny(XCSG.Method).nodesTaggedWithAny(ImmutabilityAnalysis.READONLY, ImmutabilityAnalysis.POLYREAD, ImmutabilityAnalysis.MUTABLE, ImmutabilityAnalysis.UNTYPED).eval().nodes()){
			try {
				serializeMethod(method, writer);
			} catch (Exception e){
				Log.error("Error serializing method: " + method.address().toAddressString(), e);
				throw e;
			}
			writer.flush();
			methodsSummarized++;
		}
		
		writer.writeEndElement();
		
		writer.writeEndDocument();
		writer.flush();
		writer.close();
		
		String message = "Summarized: " + fieldsSummarized + " fields, " + methodsSummarized + " methods.";
		Log.info("Summary exported.\n" + message);
	}
	
	private static void serializeField(Node field, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("field");
		
		if(field.taggedWith(XCSG.ClassVariable)){
			writer.writeAttribute("type", XCSG.ClassVariable);
		} else if(field.taggedWith(XCSG.InstanceVariable)){
			writer.writeAttribute("type", XCSG.InstanceVariable);
		} else {
			Log.warning("Skipping method for unknown field type for field: " + field.address().toAddressString());
			return;
		}
		
		Node parentClass = Common.toQ(field).parent().eval().nodes().getFirst();
		if(parentClass == null){
			Log.warning("Skipping filed, because field " + field.address().toAddressString() + " does not have a parent!");
			return;
		}
		
		Node pkg = Common.toQ(field).containers().nodesTaggedWithAny(XCSG.Package).eval().nodes().getFirst();
		if(pkg == null){
			Log.warning("Package for field " + field.address().toAddressString() + " does not exist!");
			return;
		}
		
		writer.writeAttribute("package", pkg.getAttr(XCSG.name).toString());
		writer.writeAttribute("class", parentClass.getAttr(XCSG.name).toString());
		writer.writeAttribute("name", field.getAttr(XCSG.name).toString());
		
		String fieldImmutabilityTags = stringifyImmutabilityTags(field);
		if(fieldImmutabilityTags.equals("")){
			Log.warning("Missing type qualifier tags on field: " + field.address().toAddressString());
		}
		writer.writeAttribute("immutability", fieldImmutabilityTags);
		writer.writeEndElement();
	}

	private static void serializeMethod(Node method, XMLStreamWriter writer) throws XMLStreamException {
		// write method
		writer.writeStartElement("method");
		writer.writeAttribute("signature", method.getAttr(StopGap.SIGNATURE).toString());
		
		if(method.taggedWith(XCSG.Constructor)){
			writer.writeAttribute("type", XCSG.Constructor);
		} else if(method.taggedWith(XCSG.ClassMethod)){
			writer.writeAttribute("type", XCSG.ClassMethod);
		} else if(method.taggedWith(XCSG.InstanceMethod)){
			writer.writeAttribute("type", XCSG.InstanceMethod);
		} else {
			Log.warning("Skipping method, because of unknown method type for method: " + method.address().toAddressString());
			return;
		}
		
		Node parentClass = Common.toQ(method).parent().eval().nodes().getFirst();
		if(parentClass == null){
			Log.warning("Skipping method because method " + method.address().toAddressString() + " does not have a parent!");
			return;
		}
		
		Node pkg = Common.toQ(method).containers().nodesTaggedWithAny(XCSG.Package).eval().nodes().getFirst();
		if(pkg == null){
			Log.warning("Skipping, method because package for method " + method.address().toAddressString() + " does not exist!");
			return;
		}
		
		writer.writeAttribute("package", pkg.getAttr(XCSG.name).toString());
		writer.writeAttribute("class", parentClass.getAttr(XCSG.name).toString());
		String methodImmutabilityTags = stringifyImmutabilityTags(method);
		if(methodImmutabilityTags.equals("")){
			Log.warning("Missing type qualifier tags on method: " + method.address().toAddressString());
		}
		writer.writeAttribute("immutability", methodImmutabilityTags);
		
		// write this node (if one exists)
		Node thisNode = Common.toQ(method).children().nodesTaggedWithAll(XCSG.Identity).eval().nodes().getFirst();
		if(thisNode != null){
			writer.writeStartElement("this");
			String thisImmutabilityTags = stringifyImmutabilityTags(thisNode);
			if(thisImmutabilityTags.equals("")){
				Log.warning("Missing type qualifier tags on this node: " + thisNode.address().toAddressString());
			}
			writer.writeAttribute("immutability", thisImmutabilityTags);
			writer.writeEndElement();
		}
		
		// write parameters
		for(Node parameter : Common.toQ(method).children().nodesTaggedWithAll(XCSG.Parameter).eval().nodes()){
			writer.writeStartElement("parameter");
			writer.writeAttribute("index", parameter.getAttr(XCSG.parameterIndex).toString());
			String parameterImmutabilityTags = stringifyImmutabilityTags(method);
			if(parameterImmutabilityTags.equals("")){
				Log.warning("Missing type qualifier tags on parameter: " + parameter.address().toAddressString());
			}
			writer.writeAttribute("immutability", parameterImmutabilityTags);
			writer.writeEndElement();
		}
		
		// write return node (if one exists)
		Node returnNode = Common.toQ(method).children().nodesTaggedWithAll(XCSG.ReturnValue).eval().nodes().getFirst();
		if(returnNode != null && !returnNode.taggedWith(AnalysisUtilities.DUMMY_RETURN_NODE)){
			writer.writeStartElement("return");
			String returnImmutabilityTags = stringifyImmutabilityTags(returnNode);
			if(returnImmutabilityTags.equals("")){
				Log.warning("Missing type qualifier tags on return node: " + returnNode.address().toAddressString());
			}
			writer.writeAttribute("immutability", returnImmutabilityTags);
			writer.writeEndElement();	
		}
		
		writer.writeEndElement();
	}

	private static String stringifyImmutabilityTags(Node node) {
		String prefix = "";
		String immutabilityTags = "";
		if(node.taggedWith(ImmutabilityAnalysis.READONLY)){
			immutabilityTags += ImmutabilityAnalysis.READONLY;
			prefix = ",";
		}
		if(node.taggedWith(ImmutabilityAnalysis.POLYREAD)){
			immutabilityTags += (prefix + ImmutabilityAnalysis.POLYREAD);
			prefix = ",";
		}
		if(node.taggedWith(ImmutabilityAnalysis.MUTABLE)){
			immutabilityTags += (prefix + ImmutabilityAnalysis.MUTABLE);
			prefix = ",";
		}
		if(node.taggedWith(ImmutabilityAnalysis.UNTYPED)){
			immutabilityTags += (prefix + ImmutabilityAnalysis.UNTYPED);
		}
		return immutabilityTags;
	}
	
}
