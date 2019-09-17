package com.onesait.edge.engine.zigbee.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.onesait.edge.engine.zigbee.jsoncontroller.AttributesJson;
import com.onesait.edge.engine.zigbee.jsoncontroller.ClustersInfoJson;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclCommand;
import com.onesait.edge.engine.zigbee.model.ZclDatatype;
import com.onesait.edge.engine.zigbee.model.ZclDevicetype;
import com.onesait.edge.engine.zigbee.model.ZclEvent;
import com.onesait.edge.engine.zigbee.model.ZclParam;
import com.onesait.edge.engine.zigbee.model.ZigbeeClusterLibrary;
import com.onesait.edge.engine.zigbee.util.DoubleByte;

/**
 *
 * @author fgminambre
 */
@Service
public class ZclService {

	private static final Logger LOG = LoggerFactory.getLogger(ZclService.class);

	private static final String ATTRIBUTE_NAME_READ = "read";
	private static final String ATTRIBUTE_NAME_MIN = "min";
	private static final String ATTRIBUTE_NAME_MAX = "max";
	private static final String ATTRIBUTE_NAME_SIGNALTYPE = "signaltype";
	private static final String ATTRIBUTE_NAME_TOPIC = "topic";
	private static final String ATTRIBUTE_NAME_NAME = "name";
	//ahumanes
	private static final String ATTRIBUTE_NAME_MANCODE = "mancode";
	private static final String ATTRIBUTE_NAME_FACTOR = "factor";
	private static final String ATTRIBUTE_NAME_DEFAULT="default";
	private static final String ATTRIBUTE_ALIAS="alias";
	private static final String ATTRIBUTE_ID="attid";
	
	//
	private static final String ATTRIBUTE_NAME_ID = "id";
	private static final String ATTRIBUTE_NAME_TYPE = "type";
	private static final String ATTRIBUTE_NAME_LENGTH = "length";
	private static final String ATTRIBUTE_NAME_DEV = "dev";
	private static final String ATTRIBUTE_NAME_WHEN = "when";
	private static final String ATTRIBUTE_NAME_BITMASK = "bitmask";
	private static final String XML_PATH="XML_PATH";

	private static final String TAG_NAME_ZCL = "zcl";
	private static final String TAG_NAME_DEVICE = "device";
	private static final String TAG_NAME_CLUSTER = "cluster";
	private static final String TAG_NAME_DATATYPES = "datatypes";
	private static final String TAG_NAME_DATATYPE = "datatype";
	private static final String TAG_NAME_PROFILES = "profiles";
	private static final String TAG_NAME_PROFILE = "profile";
	private static final String TAG_NAME_FACTOR = "factor";

	private static final String XML_NAME = "zcl";
	private static final String XML_EXTENSION = ".xml";
	@Value("${zigbee.xml.path}")
	private String xmlpath;
	
	private List<ClustersInfoJson> clustersinfo =new ArrayList<>();
	

	org.w3c.dom.Document document;

	private ZigbeeClusterLibrary zcl = new ZigbeeClusterLibrary();

	public ZigbeeClusterLibrary getZcl() {
		return zcl;
	}

	@PostConstruct
	public void init() {
		InputStream is = null;
		try {
			if (System.getenv(XML_PATH) != null) {
				this.xmlpath = System.getenv("XML_PATH");
			}

			if (System.getenv(XML_PATH) == null) {
				String url = xmlpath + XML_NAME + XML_EXTENSION;
				// String url="D:\\Documentos\\Clases de zigbee\\zcl.xml";
				is = new ClassPathResource(url).getInputStream();
				LOG.info("XML file from {}", url);
			} else {
				File initialFile = new File(this.xmlpath);
				is = new FileInputStream(initialFile);
				LOG.info("XML file from {}", this.xmlpath);
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			if (is != null) {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(is);
				doc.getDocumentElement().normalize();
				document = doc;
				this.visitDocument();
				LOG.info("XML loaded");
			}
		} catch (Exception e) {
			LOG.error("ERROR: {}", e.getMessage());
		} finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					LOG.error("Error closing the file");
				}
			}
		}
		
	}

	public void visitDocument() {
		org.w3c.dom.Element element = document.getDocumentElement();
		if ((element != null) && element.getTagName().equals(TAG_NAME_ZCL)) {
			visitElementZcl(element);
		}
	}

	void visitElementZcl(org.w3c.dom.Element element) {

		org.w3c.dom.NodeList nodes = element.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals(TAG_NAME_DATATYPES)) {
					visitElementDatatypes(nodeElement);
				}
				if (nodeElement.getTagName().equals(TAG_NAME_CLUSTER)) {
					visitElementCluster(nodeElement);
				}
				if ((nodeElement != null) && nodeElement.getTagName().equals(TAG_NAME_PROFILES)) {
					visitElementProfiles(nodeElement);
				}
				if ((nodeElement != null) && nodeElement.getTagName().equals(TAG_NAME_DEVICE)) {
					visitElementDevice(nodeElement);
				}
			}
		}
	}

	private class Identification {
		public Identification(String name, DoubleByte id) {
			super();
			this.name = name;
			this.id = id;
		}

		private String name;
		private DoubleByte id;
	}

	private void visitElementDevice(Element element) {

		Identification iden = getIdParameters(element);

		ZclDevicetype devType = null;
		if (iden != null) {
			devType = new ZclDevicetype(iden.name, iden.id);
			org.w3c.dom.NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				org.w3c.dom.Node node = nodes.item(i);
				if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
					if (nodeElement.getTagName().equals(TAG_NAME_CLUSTER)) {
						visitElementRequiredCluster(nodeElement, devType);
					}
				}
			}
			if (devType != null) {
				this.zcl.putZclDevicetype(devType);
			}
		}
	}

	private Identification getIdParameters(Element element) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		String name = "";
		DoubleByte id = null;
		Identification iden = null;
		try {
			for (int i = 0; i < attrs.getLength(); i++) {
				org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
				if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
					name = attr.getValue();
				} else if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
					Integer val = Integer.decode(attr.getValue());
					id = new DoubleByte(val);
				}
			}
			iden = new Identification(name, id);
		} catch (Exception e) {
		}
		return iden;
	}

	private void visitElementRequiredCluster(Element element, ZclDevicetype devType) {
		Identification iden = getIdParameters(element);
		if (iden != null) {
			devType.getRequiredServerClusters().put(iden.id, iden.name);
		}
	}

	void visitElementDatatypes(org.w3c.dom.Element element) {
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals(TAG_NAME_DATATYPE)) {
					visitElementDatatype(nodeElement);
				}
			}
		}
	}

	void visitElementDatatype(org.w3c.dom.Element element) {
		Boolean analog = Boolean.FALSE;
		String invalid = "";
		Integer length = 0;
		String description = "";
		String name = "";
		Byte id = null;

		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if ("analog".equals(attr.getName())) {
				try {
					analog = new Boolean(attr.getValue());
				} catch (Exception e) {
					analog = Boolean.FALSE;
				}
			}
			if ("invalid".equals(attr.getName())) {
				invalid = attr.getValue();
			}
			if (attr.getName().equals(ATTRIBUTE_NAME_LENGTH)) {
				try {
					length = Integer.parseInt(attr.getValue());
				} catch (Exception e) {
					length = 0;
				}
			}
			if ("description".equals(attr.getName())) {
				description = attr.getValue();
			}
			if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
				name = attr.getValue();
			}
			if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
				try {
					int val = Integer.decode(attr.getValue());
					id = new Byte((byte) val);
				} catch (Exception e) {
				}
			}
		}
		if (id != null) {
			ZclDatatype datatype = new ZclDatatype(id, name, description, length, invalid, analog);
			this.zcl.putZclDatatype(datatype);
		}
	}

	void visitElementProfiles(org.w3c.dom.Element element) {
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals(TAG_NAME_PROFILE)) {
					visitElementProfile(nodeElement);
				}
			}
		}
	}

	void visitElementProfile(org.w3c.dom.Element element) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		String name = "";
		DoubleByte id = null;
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
				name = attr.getValue();
			}
			if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
				try {
					Integer val = Integer.decode(attr.getValue());
					id = new DoubleByte(val);
				} catch (Exception e) {
				}
			}
		}

		if (id != null && !name.equals("")) {
			this.zcl.putProfile(id, name);
		}
	}

	void visitElementCluster(org.w3c.dom.Element element) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		String name = "";
		DoubleByte id = null;
		boolean server = Boolean.FALSE;
		boolean manSpec = Boolean.FALSE;
		
		ClustersInfoJson clusinfo=new ClustersInfoJson();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
				name = attr.getValue();
				clusinfo.setName(name);
			}
			if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
				try {
					Integer val = Integer.decode(attr.getValue());
					id = new DoubleByte(val);
					clusinfo.setClusterid(id.toString());
				} catch (Exception e) {
				}
			}
			if ("server".equals(attr.getName())) {
				try {
					server = Boolean.parseBoolean(attr.getValue());
				} catch (Exception e) {
				}
			}
			if ("manufacturerSpecific".equals(attr.getName())) {
				try {
					manSpec = Boolean.parseBoolean(attr.getValue());
					
				} catch (Exception e) {
				}
			}
		}
//		clustersinfo.add(clusinfo);

		ZclCluster cluster = null;
		if (id != null) {
			cluster = new ZclCluster(id, name, this.zcl);
			cluster.setManSpec(manSpec);
			clusinfo.setAttributes(new ArrayList<AttributesJson>());
			org.w3c.dom.NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				AttributesJson attjson=new AttributesJson();
				org.w3c.dom.Node node = nodes.item(i);
				if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
					if (nodeElement.getTagName().equals("attribute")) {
						visitElementAttribute(nodeElement, cluster,attjson);
						clusinfo.getAttributes().add(attjson);
					}
					if (nodeElement.getTagName().equals("command")) {
						visitElementCommand(nodeElement, cluster);
					}
				}
			}

			this.zcl.putZclCluster(cluster);
			this.clustersinfo.add(clusinfo);
			if (server) {
				ZclCluster serverCl = (ZclCluster) cluster.clone();
				this.zcl.putServerCluster(serverCl);
			}
		}
	}

	public List<ClustersInfoJson> getClustersinfo() {
		return clustersinfo;
	}

	void visitElementAttribute(org.w3c.dom.Element element, ZclCluster cluster, AttributesJson attjson) {
		DoubleByte id = null;
		String name = "";
		String typeName = "";
		Long defaultValue = 0l;
		Boolean mandatory = Boolean.TRUE;
		String access = "rw";
		Boolean reportable = Boolean.TRUE;
		Boolean read = Boolean.FALSE;
		DoubleByte code = null;
		String manufacturer = "";

		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if ("reportable".equals(attr.getName())) {
				try {
					reportable = new Boolean(attr.getValue());
				} catch (Exception e) {
					reportable = Boolean.TRUE;
				}
			} else if ("access".equals(attr.getName())) {
				access = attr.getValue();
			} else if ("mandatory".equals(attr.getName())) {
				try {
					mandatory = new Boolean(attr.getValue());
				} catch (Exception e) {
					mandatory = Boolean.TRUE;
				}
			} else if ("default".equals(attr.getName())) {
				try {
					defaultValue = Long.decode(attr.getValue());
				} catch (Exception e) {
					defaultValue = 0L;
				}
			} else if (attr.getName().equals(ATTRIBUTE_NAME_TYPE)) {
				typeName = attr.getValue();
			} else if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
				name = attr.getValue();
				attjson.setName(name);
			} else if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
				try {
					id = new DoubleByte(Integer.decode(attr.getValue()));
					attjson.setAttributeid(id.toString());
				} catch (Exception e) {
				}
			} else if (attr.getName().equals(ATTRIBUTE_NAME_READ)) {
				try {
					read = new Boolean(attr.getValue());
				} catch (Exception e) {
				}
			} else if ("code".equals(attr.getName())) {
				try {
					code = new DoubleByte(Integer.decode(attr.getValue()));
				} catch (Exception e) {
				}
			} else if ("manufacturer".equals(attr.getName())) {
				manufacturer = attr.getValue();
				attjson.setManufacturer(manufacturer);
			}
		}

		org.w3c.dom.NodeList nodes = element.getChildNodes();
		ZclDatatype zcldatatype = this.zcl.getZclDataypeByName(typeName);
		if (id != null && cluster != null && zcldatatype != null) {
			ZclAttribute attribute = new ZclAttribute(id, name, zcldatatype, defaultValue, mandatory, access,
					reportable, read, code, manufacturer);
			for (int i = 0; i < nodes.getLength(); i++) {
				org.w3c.dom.Node node = nodes.item(i);
				if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
					if (nodeElement.getTagName().equals("Range")) {
						visitElementRange(nodeElement, attribute);
					} else if (nodeElement.getTagName().equals("Reporting")) {
						visitElementReporting(nodeElement, attribute,attjson);
					} else if (nodeElement.getTagName().equals("enum")) {
						visitElementEnum(nodeElement, attribute);
					} else if (nodeElement.getTagName().equals("event")) {
						visitElementEvent(nodeElement, attribute);
					} else if (nodeElement.getTagName().equals("conversion")) {
						visitElementConversion(nodeElement,attribute);
					} else if (nodeElement.getTagName().equals("RequestSum")) {
						visitElemtSumAttr(nodeElement,attribute);
					}

					//ahumanes: en desarrollo
					
				}
			}
			
			cluster.putAttribute(attribute);
		}
	}

	private void visitElemtSumAttr(org.w3c.dom.Element element, ZclAttribute attribute) {
		String alias="";
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (ATTRIBUTE_ALIAS.equals(attr.getName())) {
				alias = attr.getValue(); //obtiene el alias
			}
		}
		if (alias!=null) {
		attribute.setAlias(alias);
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals(ATTRIBUTE_ID)) {
					visitAttID(nodeElement,attribute);
				}
			}
		}
		
		}
		
	}

	private void visitAttID(org.w3c.dom.Element element, ZclAttribute attribute) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		DoubleByte id = null;
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if ("id".equals(attr.getName())) {
				id = new DoubleByte(Integer.decode(attr.getValue()));
				if (id != null) {
					attribute.getAttrs2sum().add(id);
				}
			}
		}
		
		
		
	}

	private void visitElementConversion(org.w3c.dom.Element element, ZclAttribute att) {
		
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		Double factor = null;
		DoubleByte mancode = null;
		Double defaultFactor=null;
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (ATTRIBUTE_NAME_MANCODE.equals(attr.getName())) {
				mancode = new DoubleByte(Integer.decode(attr.getValue()));
			} else if (attr.getName().equals(ATTRIBUTE_NAME_FACTOR)) {
				factor = Double.valueOf(attr.getValue());
			} else if (ATTRIBUTE_NAME_DEFAULT.equals(attr.getName())) {
				defaultFactor = Double.valueOf(attr.getValue());
			}
		}
		if (mancode != null && factor != null) {
			att.getConversionFactor().put(mancode,factor);
		}
		if (defaultFactor !=null) {
			att.setDefaultConversion(defaultFactor);
		}
		
		
	}

	// private void visitElementAttributeConfig(Element nodeElement,
	// ZclAttribute attribute) {
	// Boolean toRead = Boolean.FALSE;
	// DoubleByte minReportPeriodDb = null;
	// DoubleByte maxReportPeriodDb = null;
	//
	// org.w3c.dom.NamedNodeMap nodeAttrs = nodeElement.getAttributes();
	// for (int i = 0; i < nodeAttrs.getLength(); i++) {
	// org.w3c.dom.Attr attr = (org.w3c.dom.Attr) nodeAttrs.item(i);
	// if (attr.getName().equals(ATTRIBUTE_NAME_READ)) {
	// try{
	// toRead = new Boolean(attr.getValue());
	// }catch(Exception e){
	// toRead = Boolean.FALSE;
	// }
	// } else if (attr.getName().equals(ATTRIBUTE_NAME_MINREPORT)) {
	// try {
	// minReportPeriodDb = new DoubleByte(Integer.decode(attr.getValue()));
	// } catch (Exception e) {
	// minReportPeriodDb = null;
	// }
	// } else if (attr.getName().equals(ATTRIBUTE_NAME_MAXREPORT)) {
	// try {
	// maxReportPeriodDb = new DoubleByte(Integer.decode(attr.getValue()));
	// } catch (Exception e) {
	// maxReportPeriodDb = null;
	// }
	// }
	// }
	//
	// ZbAttributeConfiguration attributeConfig = new ZbAttributeConfiguration(
	// toRead, minReportPeriodDb, maxReportPeriodDb);
	//
	// org.w3c.dom.NodeList childNodes = nodeElement.getChildNodes();
	// for (int i = 0; i < childNodes.getLength(); i++) {
	// org.w3c.dom.Node childNode = childNodes.item(i);
	// if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
	// org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
	// if (childElement.getTagName().equals("event")) {
	// visitElementConfigEvent(childElement, attributeConfig);
	// }
	// }
	// }
	// }

	// private void visitElementConfigEvent(Element nodeElement,
	// ZbAttributeConfiguration attributeConfig) {
	// String topic = "";
	// String signal = "";
	// String when = "";
	//
	// org.w3c.dom.NamedNodeMap nodeAttrs = nodeElement.getAttributes();
	// for (int i = 0; i < nodeAttrs.getLength(); i++) {
	// org.w3c.dom.Attr attr = (org.w3c.dom.Attr) nodeAttrs.item(i);
	// if (attr.getName().equals(ATTRIBUTE_NAME_SIGNALTYPE)) {
	// signal = attr.getValue();
	// } else if (attr.getName().equals(ATTRIBUTE_NAME_TOPIC)) {
	// topic = attr.getValue();
	// } else if (attr.getName().equals(ATTRIBUTE_NAME_WHEN)) {
	// when = attr.getValue();
	// }
	// }
	//
	// ZclEvent event = new ZclEvent(topic, signal);
	// if (when != null && !"".equals(when)) {
	// event.setType(when);
	// }
	// attributeConfig.setEvent(event);
	// }

	private void visitElementEvent(Element element, ZclAttribute attribute) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		String signaltype = null;
		String topic = null;
		Long bitmask = null;
		String when = "";
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (ATTRIBUTE_NAME_SIGNALTYPE.equals(attr.getName())) {
				signaltype = attr.getValue();
			} else if (ATTRIBUTE_NAME_TOPIC.equals(attr.getName())) {
				topic = attr.getValue();
			} else if (ATTRIBUTE_NAME_BITMASK.equals(attr.getName())) {
				try {
					bitmask = Long.decode(attr.getValue());
				} catch (Exception e) {
				}
			} else if (ATTRIBUTE_NAME_WHEN.equals(attr.getName())) {
				when = attr.getValue();
			}
		}
		if (attribute != null) {
			ZclEvent event = new ZclEvent(topic, signaltype);
			if (when != null && !"".equals(when)) {
				event.setType(when);
			}
			event.setBitmask(bitmask);
			attribute.getEvents().add(event);
		}
	}

	void visitElementParamAttribute(org.w3c.dom.Element element, ZclParam param) {
		DoubleByte id = null;

		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
				try {
					id = new DoubleByte(Integer.decode(attr.getValue()));
				} catch (Exception e) {
				}
			}
		}

		param.setAttId(id);
	}

	void visitElementRange(org.w3c.dom.Element element, ZclAttribute attribute) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if ("to".equals(attr.getName())) {
				try {
					attribute.setRangeTo(Long.decode(attr.getValue()));
				} catch (Exception e) {
				}
			} else if ("from".equals(attr.getName())) {
				try {
					attribute.setRangeFrom(Long.decode(attr.getValue()));
				} catch (Exception e) {
				}
			}
		}
	}

	void visitElementReporting(org.w3c.dom.Element element, ZclAttribute attribute, AttributesJson attjson) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals(ATTRIBUTE_NAME_MIN)) {
				try {
					attribute.setMinReportingTime(new DoubleByte(Integer.decode(attr.getValue())));
					attjson.setMinTime(String.valueOf((new DoubleByte(Integer.decode(attr.getValue()))).intValue()));
				} catch (Exception e) {
				}
			} else if (attr.getName().equals(ATTRIBUTE_NAME_MAX)) {
				try {
					attribute.setMaxReportingTime(new DoubleByte(Integer.decode(attr.getValue())));
					attjson.setMaxTime(String.valueOf((new DoubleByte(Integer.decode(attr.getValue()))).intValue()));
				} catch (Exception e) {
				}
			}
		}

		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals(TAG_NAME_FACTOR)) {
					visitElementFactor(nodeElement, attribute);
				}
			}
		}
	}

	private void visitElementFactor(Element element, ZclAttribute attribute) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		DoubleByte factorDeviceId = null;
		Integer factorMinReportingTime = null;
		Integer factorMaxReportingTime = null;
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals(ATTRIBUTE_NAME_DEV)) {
				try {
					factorDeviceId = new DoubleByte(Integer.decode(attr.getValue()));
				} catch (Exception e) {
				}
			} else if (attr.getName().equals(ATTRIBUTE_NAME_MIN)) {
				try {
					factorMinReportingTime = Integer.decode(attr.getValue());
				} catch (Exception e) {
				}
			} else if (attr.getName().equals(ATTRIBUTE_NAME_MAX)) {
				try {
					factorMaxReportingTime = Integer.decode(attr.getValue());
				} catch (Exception e) {
				}
			}
		}
		addReportingFactor(attribute, factorDeviceId, factorMinReportingTime, factorMaxReportingTime);
	}

	private void addReportingFactor(ZclAttribute attribute, DoubleByte factorDeviceId, Integer factorMinReportingTime,
			Integer factorMaxReportingTime) {
		if (factorDeviceId != null && factorMaxReportingTime != null && factorMinReportingTime != null) {
			attribute.addReportingFactor(factorDeviceId, factorMinReportingTime, factorMaxReportingTime);
		}
	}

	void visitElementCommand(org.w3c.dom.Element element, ZclCluster cluster) {
		Byte id = null;
		String name = "";
		Boolean server = Boolean.FALSE;
		Boolean send = Boolean.FALSE;

		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
				name = attr.getValue();
			} else if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
				try {
					int val = Integer.decode(attr.getValue());
					id = new Byte((byte) val);
				} catch (Exception e) {
				}
			} else if ("server".equals(attr.getName())) {
				try {
					server = Boolean.parseBoolean(attr.getValue());
				} catch (Exception e) {
					server = Boolean.FALSE;
				}
			} else if ("send".equals(attr.getName())) {
				try {
					send = Boolean.parseBoolean(attr.getValue());
				} catch (Exception e) {
					send = Boolean.FALSE;
				}
			}
		}

		if (id != null) {
			ZclCommand command = new ZclCommand(id, name);
			command.setSend(send);
			command.setServer(server);
			org.w3c.dom.NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				org.w3c.dom.Node node = nodes.item(i);
				if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
					if (nodeElement.getTagName().equals("param")) {
						visitElementParam(nodeElement, command);
					}
				}
			}
			if (server)
				cluster.putServerCommands(command);
			else
				cluster.putCommands(command);
		}
	}

	void visitElementParam(org.w3c.dom.Element element, ZclCommand command) {
		String paramName = null;
		String typeName = null;

		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals(ATTRIBUTE_NAME_TYPE)) {
				typeName = attr.getValue();
			} else if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
				paramName = attr.getValue();
			}
		}
		if (paramName != null && typeName != null) {
			ZclDatatype zcldatatype = this.zcl.getZclDataypeByName(typeName);
			ZclParam param = new ZclParam(paramName, zcldatatype);
			org.w3c.dom.NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				org.w3c.dom.Node node = nodes.item(i);
				if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
					if (nodeElement.getTagName().equals("default")) {
						visitElementDefault(nodeElement, param);
					} else if (nodeElement.getTagName().equals("attribute")) {
						visitElementParamAttribute(nodeElement, param);
					}
				}
			}
			command.putParam(param);
		}
	}

	void visitElementDefault(org.w3c.dom.Element element, ZclParam param) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		String value = null;
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if ("value".equals(attr.getName())) {
				value = attr.getValue();
			}
		}
		if (value != null) {
			param.setValue(Long.decode(value));
		}
	}

	void visitElementValue(org.w3c.dom.Element element) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if ("desc".equals(attr.getName())) {
			} else if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
			} else if (attr.getName().equals(ATTRIBUTE_NAME_ID)) {
			}
		}
	}

	void visitElementEnum(org.w3c.dom.Element element, ZclAttribute att) {
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		String name = null;
		DoubleByte value = null;
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if ("value".equals(attr.getName())) {
				value = new DoubleByte(Integer.decode(attr.getValue()));
			} else if (attr.getName().equals(ATTRIBUTE_NAME_NAME)) {
				name = attr.getValue();
			}
		}
		if (value != null && name != null)
			att.getEnums().put(value, name);
	}

}