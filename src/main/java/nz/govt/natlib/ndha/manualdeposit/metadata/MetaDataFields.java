/**
 * Software License
 *
 * Copyright 2007/2010 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.manualdeposit.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nz.govt.natlib.ndha.common.PropertiesUtil;
import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exceptions.XmlException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidApplicationDataException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidCMSSystemException;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class MetaDataFields implements Serializable,
		Iterable<IMetaDataTypeExtended> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 436612415898570626L;
	private final static Log LOG = LogFactory.getLog(MetaDataFields.class);
	private static String theCmsIDName = "CMSIdentifier";
	private static String theCmsSystemName = "CMSSystem";
	private static String theCmsDescriptionName = "CMSDescription";
	private static String theCmsReferenceNumber = "ReferenceNumber";
	private final List<IMetaDataTypeExtended> theMetaData = new ArrayList<IMetaDataTypeExtended>();

	public enum EMetaDataType {
		Entity, File;
	}

	public enum ECMSSystem {
		CMS2, CMS1, NoSystem, StaffMediated;
	}

	public Iterator<IMetaDataTypeExtended> iterator() {
		return theMetaData.iterator();
	}

	public int size() {
		return theMetaData.size();
	}

	public String getCMSIDAttributeName() {
		return theCmsIDName;
	}

	public String getCMSID(String cmsSystem) {
		String retVal = null;
		if(cmsSystem.equalsIgnoreCase("CMS1")){
			theCmsIDName = "objectIdentifierValue";
		} else {
			theCmsIDName = "CMSIdentifier";
		}
		final IMetaDataTypeExtended type = getMetaDataType(theCmsIDName);
		if (type != null) {
			retVal = type.getDataFieldValue();
		}
		return retVal;
	}

	public void setCMSID(final String value) throws Exception {
		final IMetaDataTypeExtended type = getMetaDataType(theCmsIDName);
		if (type != null) {
			type.setDataFieldValue(value);
		}
	}

	public String getCMSSystemAttributeName() {
		return theCmsSystemName;
	}

	public String getCMSSystem() {
		String retVal = null;
		final IMetaDataTypeExtended type = getMetaDataType(theCmsSystemName);
		if (type != null) {
			retVal = type.getDataFieldValue();
		}
		return retVal;
	}

	public void setCMSSystem(final String value)
			throws InvalidCMSSystemException {
		final IMetaDataTypeExtended type = getMetaDataType(theCmsSystemName);
		if (type != null) {
			try {
				type.setDataFieldValue(value);
			} catch (Exception ex) {
				throw new InvalidCMSSystemException(value, ex, value);
			}
		}
	}

	public ECMSSystem getCMSSystemType()
			throws InvalidApplicationDataException, InvalidCMSSystemException {
		final String system = getCMSSystem();
		ECMSSystem retVal = ECMSSystem.NoSystem;
		ApplicationData appData = ApplicationData.getInstance();
		for (int i = 0; i < ECMSSystem.values().length; i++) {
			if (appData.getCMSSystemText(ECMSSystem.values()[i]).equals(system)) {
				retVal = ECMSSystem.values()[i];
				break;
			}
		}
		return retVal;
	}

	public String getCMSDescriptionAttributeName() {
		return theCmsDescriptionName;
	}

	public String getCMSDescription() {
		String retVal = "";
		IMetaDataTypeExtended type = getMetaDataType(theCmsDescriptionName);
		if (type != null) {
			retVal = type.getDataFieldValue();
		}
		return retVal;
	}

	public boolean setCMSDescription(final String value) throws Exception {
		boolean succeeded = false;
		IMetaDataTypeExtended type = getMetaDataType(theCmsDescriptionName);
		if (type != null) {
			type.setDataFieldValue(value);
			succeeded = true;
		}
		return succeeded;
	}

	public String getCMSReferenceNumberAttributeName() {
		return theCmsReferenceNumber;
	}

	public String getCMSReferenceNumber() {
		String retVal = "";
		IMetaDataTypeExtended type = getMetaDataType(theCmsReferenceNumber);
		if (type != null) {
			retVal = type.getDataFieldValue();
		}
		return retVal;
	}

	public void setCMSReferenceNumber(final String value) throws Exception {
		final IMetaDataTypeExtended type = getMetaDataType(theCmsReferenceNumber);
		if (type != null) {
			type.setDataFieldValue(value);
		}
	}

	public void addMetaData(final IMetaDataTypeExtended value) {
		int sortOrder = value.getSortOrder();
		if (sortOrder == 0) {
			sortOrder = theMetaData.get(theMetaData.size() - 1).getSortOrder();
			sortOrder++;
			value.setSortOrder(sortOrder);
		}
		theMetaData.add(value);
		reSort();
	}

	public void replaceProvenanceEvents(final List<ProvenanceEvent> events) {
		for (int i = theMetaData.size() - 1; i >= 0; i--) {
			IMetaDataTypeExtended metaData = theMetaData.get(i);
			if (metaData.getDataType().equals(EDataType.ProvenanceNote)) {
				theMetaData.remove(metaData);
			}
		}
		for (ProvenanceEvent event : events) {
			IMetaDataTypeExtended metaData = event.toMetadataType(event
					.toString());
			theMetaData.add(metaData);
		}
	}

	public void deleteMetaData(final IMetaDataTypeExtended value) {
		for (int i = 0; i < theMetaData.size(); i++) {
			IMetaDataTypeExtended meta = theMetaData.get(i);
			if (meta.isEquivalentTo(value, true)) {
				theMetaData.remove(i);
				break;
			}
		}
		reSort();
	}

	public void deleteMetaDataOfType(final IMetaDataTypeExtended value) {
		for (int i = theMetaData.size() - 1; i > 0; i--) {
			IMetaDataTypeExtended meta = theMetaData.get(i);
			if (meta.isEquivalentTo(value, false)) {
				theMetaData.remove(i);
			}
		}
		reSort();
	}

	public void deleteDuplicateMetaData(final IMetaDataTypeExtended value) {
		for (int i = theMetaData.size() - 1; i > 0; i--) {
			IMetaDataTypeExtended meta = theMetaData.get(i);
			if ((value.isEquivalentTo(meta, false))
					&& (!value.isEquivalentTo(meta, true))) {
				theMetaData.remove(i);
			}
		}
		reSort();
	}

	public IMetaDataTypeExtended getAt(final int position) {
		return getMetaDataFields().get(position);
	}

	public List<IMetaDataTypeExtended> getMetaDataFields() {
		Collections.sort(theMetaData, new MetaDataFieldsComparator());
		return theMetaData;
	}
	
	public Map<String, EDataType> getMetaDataEDataTypes(String setKey) {
		Map<String, EDataType> dataTypes = new HashMap<String, EDataType>();// = metaDataByName();
		for (IMetaDataTypeExtended meta : theMetaData) {
			if(setKey.equals("By Name")){
				dataTypes.put(meta.getDataFieldName(), meta.getDataType());
			}else if(setKey.equals("By Desc")){
				dataTypes.put(meta.getDataFieldDescription(), meta.getDataType());
			}
		}		
		
		return dataTypes;
	}

	public void reSort() {
		Collections.sort(theMetaData, new MetaDataFieldsComparator());
		int currentSortOrder = 1;
		for (IMetaDataTypeExtended meta : theMetaData) {
			meta.setSortOrder(currentSortOrder);
			currentSortOrder++;
		}
	}

	private Map<String, IMetaDataTypeExtended> metaDataByName() {
		final Map<String, IMetaDataTypeExtended> retVal = new HashMap<String, IMetaDataTypeExtended>();
		for (IMetaDataTypeExtended meta : theMetaData) {
			retVal.put(meta.getDataFieldName(), meta);
		}
		return retVal;
	}
	
	private Map<String, IMetaDataTypeExtended> metaDataByDesc() {
		final Map<String, IMetaDataTypeExtended> retVal = new HashMap<String, IMetaDataTypeExtended>();
		for (IMetaDataTypeExtended meta : theMetaData) {
			retVal.put(meta.getDataFieldDescription(), meta);
		}
		return retVal;
	}

	public IMetaDataTypeExtended getMetaDataType(final int sortOrder,
			final String theType) {
		IMetaDataTypeExtended data = null;
		for (IMetaDataTypeExtended meta : theMetaData) {
			if ((meta.getSortOrder() == sortOrder)
					&& (meta.getDataFieldName().equals(theType))) {
				data = meta;
				break;
			}
		}
		return data;
	}

	public IMetaDataTypeExtended getMetaDataType(final String theType) {
		final Map<String, IMetaDataTypeExtended> byName = metaDataByName();
		if (byName.containsKey(theType)) {
			return byName.get(theType);
		} else {
			return null;
		}
	}	
	
	public IMetaDataTypeExtended getMetaDataTypeByDesc(final String theDesc) {
		final Map<String, IMetaDataTypeExtended> byName = metaDataByDesc();
		if (byName.containsKey(theDesc)) {
			return byName.get(theDesc);
		} else {
			return null;
		}
	}

	public boolean setMetaDataValue(final String theType, final String theValue)
			throws Exception {
		Map<String, IMetaDataTypeExtended> byName = metaDataByName();
		if (byName.containsKey(theType)) {
			byName.get(theType).setDataFieldValue(theValue);
			return true;
		} else {
			return false;
		}
	}

	public MetaDataFields getCopy() throws Exception {
		MetaDataFields result = MetaDataFields.create();
		for (IMetaDataTypeExtended meta : theMetaData) {
			IMetaDataTypeExtended metaCopy = new MetaDataTypeImpl();
			meta.duplicate(metaCopy);
			result.addMetaData(metaCopy);
		}
		return result;
	}

	public static MetaDataFields create() {
		return new MetaDataFields();
	}

	public MetaDataFields() {

	}

	public static MetaDataFields create(final String xmlFileName)
			throws FileNotFoundException {
		return new MetaDataFields(xmlFileName);
	}

	public MetaDataFields(final String fileName) throws FileNotFoundException {
		String xmlFileName = fileName;
		File xmlFile = new File(xmlFileName);
		if (!xmlFile.exists()) { // Search for it
			ClassLoader contextClassLoader = PropertiesUtil
					.getContextClassLoaderInternal();
			Enumeration<URL> urls = PropertiesUtil.getResources(
					contextClassLoader, xmlFileName);
			if ((urls == null) || (!urls.hasMoreElements())) {
				LOG.error("MetaData file not found - " + xmlFileName);
				throw new FileNotFoundException(xmlFileName + " not found");
			}
			URL xmlFileNameURL = (URL) urls.nextElement();
			xmlFile = new File(xmlFileNameURL.getPath());
			xmlFileName = xmlFile.getPath();
		}
		loadFromXML(xmlFileName);
	}

	public void storeAsXML(final String xmlFileName) throws IOException,
			XmlException {
		XMLHandler handler = new XMLHandler(MetaDataTypeImpl
				.getStandardObjectType(), xmlFileName);
		try {
			ArrayList<XMLObject> objects = new ArrayList<XMLObject>();
			XMLObject documentation = XMLObject.create("Documentation");
			String documentationDetail = "To edit this file, use Indigo.exe /MetaData";
			documentation.setObjectValue(documentationDetail);
			objects.add(documentation);
			reSort();
			for (IMetaDataTypeExtended meta : theMetaData) {
				XMLObject object = meta.getXMLObject(handler);
				objects.add(object);
			}
			handler.setObjects(objects);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage(), ex);
		}
		handler.writeXMLFile();
	}

	public void loadFromXML(final String xmlFileName)
			throws FileNotFoundException {
		XMLHandler handler = new XMLHandler(MetaDataTypeImpl
				.getStandardObjectType(), xmlFileName);
		List<String> keys = handler.getObjectNames();
		for (String key : keys) {
			try {
				XMLObject object = handler.getObject(key);
				if (object.getObjectType().equals(
						MetaDataTypeImpl.getStandardObjectType())) {
					IMetaDataTypeExtended meta = new MetaDataTypeImpl(object);
					theMetaData.add(meta);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex.getMessage(), ex);
			}
		}
		reSort();
	}

}
