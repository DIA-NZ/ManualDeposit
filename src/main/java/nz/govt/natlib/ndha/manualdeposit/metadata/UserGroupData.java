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
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import nz.govt.natlib.ndha.common.PropertiesUtil;
import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exceptions.XmlException;
import nz.govt.natlib.ndha.common.mets.FileTypesSingleton;
import nz.govt.natlib.ndha.manualdeposit.exceptions.CharacterTranslationException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidApplicationDataException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidCMSSystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class UserGroupData {

	public final class CharacterTranslations {
		private final static String CHAR_TRANS_OBJECT_TYPE = "CharacterTranslation";
		private final static String PROP_CHAR = "CharacterToTranslate";
		private final static String PROP_TRANSLATE_TO = "TranslateTo";
		private final static String PROP_POSITION = "Position";

		private String theCharacterToTranslate;
		private String theTranslateTo;
		private ECharacterPosition thePosition;

		public CharacterTranslations(final String characterToTranslate,
				final String translateTo, final ECharacterPosition position) {
			theCharacterToTranslate = characterToTranslate;
			theTranslateTo = translateTo;
			thePosition = position;
		}

		public CharacterTranslations(final XMLObject object) {
			setFromXMLObject(object);
		}

		public String getCharacterToTranslate() {
			return theCharacterToTranslate;
		}

		public void setCharacterToTranslate(final String value)
				throws CharacterTranslationException {
			if (value == null || value.length() == 0) {
				throw new CharacterTranslationException(
						"Translation character can not be null");
			}
			theCharacterToTranslate = value;
		}

		public String getTranslateTo() {
			return theTranslateTo;
		}

		public void setTranslateTo(final String value)
				throws CharacterTranslationException {
			if (value == null || value.length() != 1) {
				throw new CharacterTranslationException(
						"Translation character can only be a single character");
			}
			theTranslateTo = value;
		}

		public ECharacterPosition getPosition() {
			return thePosition;
		}

		public void setPosition(final ECharacterPosition value) {
			thePosition = value;
		}

		public String toString() {
			return String.format("%s translates to %s",
					theCharacterToTranslate, theTranslateTo);
		}

		public XMLObject getXMLObject(final XMLHandler handler) {
			final XMLObject object = handler.createXMLObject(
					CHAR_TRANS_OBJECT_TYPE, CHAR_TRANS_OBJECT_TYPE);
			object.setObjectType(CHAR_TRANS_OBJECT_TYPE);
			object.setObjectName(CHAR_TRANS_OBJECT_TYPE);
			object.addAttribute(PROP_CHAR, theCharacterToTranslate);
			object.addAttribute(PROP_TRANSLATE_TO, theTranslateTo);
			object.addAttribute(PROP_POSITION, thePosition.name());
			return object;
		}

		public void setFromXMLObject(final XMLObject object) {
			object.setObjectType(STANDARD_OBJECT_TYPE);
			if (object.getAttribute(PROP_CHAR) != null) {
				theCharacterToTranslate = object.getAttribute(PROP_CHAR);
			}
			if (object.getAttribute(PROP_TRANSLATE_TO) != null) {
				theTranslateTo = object.getAttribute(PROP_TRANSLATE_TO);
			}
			if (object.getAttribute(PROP_POSITION) != null) {
				thePosition = ECharacterPosition.valueOf(object
						.getAttribute(PROP_POSITION));
			}
		}

	}

	public enum ECharacterPosition {
		Anywhere("Anywhere", "anywhere in the file name"), End("End",
				"at the end of the file name"), Beginning("Start",
				"at the start of the file name");

		private final String _description;
		private final String _fullDescription;

		ECharacterPosition(final String description,
				final String fullDescription) {
			_description = description;
			_fullDescription = fullDescription;
		}

		public String description() {
			return _description;
		}

		public String fullDescription() {
			return _fullDescription;
		}

		public String toString() {
			return _fullDescription;
		}
	}

	private final static Log LOG = LogFactory.getLog(UserGroupData.class);
	private final static String STANDARD_OBJECT_TYPE = "UserData";
	private final static String PROP_SHARED_TEMPLATE = "SharedTemplateLocation";
	private final static String PROP_INCL_CMS_2 = "IncludeCMS2Search";
	private final static String PROP_INCL_CMS_1 = "IncludeCMS1Search";
	private final static String PROP_INCL_NO_CMS = "IncludeNoCMSOption";
	private final static String PROP_INCL_PRODUCER_LIST = "IncludeProducerList";
	private final static String PROP_INCL_MULTI_ENTITY = "IncludeMultiEntityMenuItem";
	private final static String PROP_MATERIAL_FLOW_ID = "MaterialFlowID";
	private final static String PROP_USER_PRODUCER_ID = "UserProducerID";
	private final static String PROP_CMS_2_METADATA_FILE = "CMS2MetaDataFile";
	private final static String PROP_CMS_1_METADATA_FILE = "CMS1MetaDataFile";
	private final static String PROP_NO_CMS_METADATA_FILE = "NoCMSMetaDataFile";
	private final static String PROP_STAFF_MED_METADATA_FILE = "StaffMediatedMetaDataFile";
	private final static String PROP_FILETYPES_PROP_FILE = "FileTypeDescriptionPropertyFile";
	private final static String PROP_INTERIM_FILE_LOCATION = "InterimFileLocation";
	private final static String PROP_CLEANUP_TYPE = "CleanupType";
	private final static String PROP_CLEANUP_DELAY = "CleanupDelay";
	private final static String PROP_BULK_BATCH_SIZE = "BulkBatchSize";
	private final static String PROP_USER_GROUP_DESC = "UserGroupDesc";
	
	private final static String PROP_SERIAL_MATERIAL_FLOW_ID = "SerialMaterialFlowID";
	private final static String PROP_MONO_MATERIAL_FLOW_ID = "MonoMaterialFlowID";
	private final static String PROP_VIDEO_MATERIAL_FLOW_ID = "VideoMaterialFlowID";
	private final static String MUSIC_MATERIAL_FLOW_ID = "AudioMaterialFlowID";
	private final static String PROP_ALMA_MATERIAL_FLOW_ID = "AlmaMaterialFlowID";

	private String sharedTemplatePath;
	private String sharedStructTemplatePath = null;
	private String sharedMetadataTemplatePath = null;
	private boolean includeCMS2Search;
	private boolean includeCMS1Search;
	private boolean includeNoCMSOption;
	private boolean includeProducerList;
	private boolean includeMultiEntityMenuItem;
	private String materialFlowID;
	private String userProducerID;
	private String cms2MetaDataFile;
	private String cms1MetaDataFile;
	private String noCMSMetaDataFile;
	private String staffMediatedMetaDataFile;
	private MetaDataFields cms2MetaData;
	private MetaDataFields cms1MetaData;
	private MetaDataFields noCMSMetaData;
	private MetaDataFields staffMediatedMetaData;
	private String fileTypesPropFile = "";
	private String interimFileLocation = "";
	private ECleanupType cleanupType = ECleanupType.None;
	private int cleanupDelay = 14;
	private int bulkBatchSize = 1;
	private List<CharacterTranslations> characterTranslations = new ArrayList<CharacterTranslations>();
	private UserGroupDesc userGroupDesc = UserGroupDesc.None;
	
	private String serialMaterialFlowID;
	private String monoMaterialFlowID;
	private String videoMaterialFlowID;
	private String musicMaterialFlowID;
	private String almaMaterialFlowID;
	private String emuMaterialFlowID;

	public enum ECleanupType {
		None("No file cleanup"), Immediate(
				"Files cleaned up one day after deposit"), Delayed(
				"Files cleaned up after a specific delay");
		private final String _description;

		ECleanupType(final String description) {
			_description = description;
		}

		public String description() {
			return _description;
		};

		public String toString() {
			return _description;
		};

	}
	
	public enum UserGroupDesc {
		None("No User Group"), Digitisation("Digitisation User Group "), Published("Published User Group"),
		StaffMediated("Unpublished User Group"), Sound("Sound User Group"), Video("Video User Group"), WebHarvest("WebHarvest User Group");
		
		private final String _groupDescription;

		UserGroupDesc(final String groupDescription) {
			_groupDescription = groupDescription;
		}

		public String description() {
			return _groupDescription;
		};

		public String toString() {
			return _groupDescription;
		};

	}

	public String getSharedTemplatePath() {
		return sharedTemplatePath;
	}

	public void setSharedTemplatePath(final String value) {
		sharedTemplatePath = value;
	}

	public String getSharedStructTemplatePath() {
		return sharedStructTemplatePath;
	}

	public String getSharedMetadataTemplatePath() {
		return sharedMetadataTemplatePath;
	}

	public boolean isIncludeCMS2Search() {
		return includeCMS2Search;
	}

	public void setIncludeCMS2Search(final boolean value) {
		includeCMS2Search = value;
	}

	public boolean isIncludeCMS1Search() {
		return includeCMS1Search;
	}

	public void setIncludeCMS1Search(final boolean value) {
		includeCMS1Search = value;
	}

	public boolean isIncludeNoCMSOption() {
		return includeNoCMSOption;
	}

	public void setIncludeNoCMSOption(final boolean value) {
		includeNoCMSOption = value;
	}

	public boolean isIncludeProducerList() {
		return includeProducerList;
	}

	public void setIncludeProducerList(final boolean value) {
		includeProducerList = value;
	}

	public boolean isIncludeMultiEntityMenuItem() {
		return includeMultiEntityMenuItem;
	}

	public void setIncludeMultiEntityMenuItem(final boolean value) {
		includeMultiEntityMenuItem = value;
	}

	public String getMaterialFlowID() {
		return materialFlowID;
	}

	public void setMaterialFlowID(final String value) {
		materialFlowID = value;
	}

	public String getUserProducerID() {
		return userProducerID;
	}

	public void setUserProducerID(final String value) {
		userProducerID = value;
	}

	public String getCMS2MetaDataFile() {
		return cms2MetaDataFile;
	}

	public void setCMS2MetaDataFile(final String value) {
		cms2MetaDataFile = value;
		try {
			cms2MetaData = MetaDataFields.create(cms2MetaDataFile);
		} catch (FileNotFoundException ex) {
			LOG.error(String.format("Could not find CMS 2 meta data file %s",
					cms2MetaDataFile), ex);
		}
	}

	public String getCMS1MetaDataFile() {
		return cms1MetaDataFile;
	}

	public void setCMS1MetaDataFile(final String value) {
		cms1MetaDataFile = value;
		try {
			cms1MetaData = MetaDataFields.create(cms1MetaDataFile);
		} catch (FileNotFoundException ex) {
			LOG.error(String.format("Could not find CMS 1 meta data file %s",
					cms1MetaDataFile), ex);
		}
	}

	public String getNoCMSMetaDataFile() {
		return noCMSMetaDataFile;
	}

	public void setNoCMSMetaDataFile(final String value) {
		noCMSMetaDataFile = value;
		try {
			noCMSMetaData = MetaDataFields.create(noCMSMetaDataFile);
		} catch (FileNotFoundException ex) {
			LOG.error(String.format("Could not find No CMS meta data file %s",
					noCMSMetaDataFile), ex);
		}
	}

	public String getStaffMediatedMetaDataFile() {
		return staffMediatedMetaDataFile;
	}

	public void setStaffMediatedMetaDataFile(final String value) {
		staffMediatedMetaDataFile = value;
		try {
			staffMediatedMetaData = MetaDataFields
					.create(staffMediatedMetaDataFile);
		} catch (FileNotFoundException ex) {
			LOG.error(String.format(
					"Could not find Staff Mediated meta data file %s",
					staffMediatedMetaDataFile), ex);
		}
	}

	public String getFileTypesPropFile() {
		return fileTypesPropFile;
	}

	public void setFileTypesPropFile(final String value) {
		fileTypesPropFile = value;
	}

	public String getInterimFileLocation() {
		return interimFileLocation;
	}

	public void setInterimFileLocation(final String value) {
		interimFileLocation = value;
	}

	public ECleanupType getCleanupType() {
		return cleanupType;
	}

	public void setCleanupType(final ECleanupType value) {
		cleanupType = value;
	}

	public int getCleanupDelay() {
		return cleanupDelay;
	}

	public void setCleanupDelay(final int value) {
		cleanupDelay = value;
	}

	public int getBulkBatchSize() {
		return bulkBatchSize;
	}

	public void setBulkBatchSize(final int value) {
		bulkBatchSize = value;
	}
	
	public UserGroupDesc getUserGroupDesc() {
		return userGroupDesc;
	}

	public void setUserGroupDesc(final UserGroupDesc value) {
		userGroupDesc = value;
	}
	
	public String getSerialMaterialFlowID() {
		return serialMaterialFlowID;
	}

	public void setSerialMaterialFlowID(final String value) {
		serialMaterialFlowID = value;
	}
	
	public String getMonoMaterialFlowID() {
		return monoMaterialFlowID;
	}

	public void setMonoMaterialFlowID(final String value) {
		monoMaterialFlowID = value;
	}
	
	public String getVideoMaterialFlowID() {
		return videoMaterialFlowID;
	}

	public void setVideoMaterialFlowID(final String value) {
		videoMaterialFlowID = value;
	}
	
	public String getAudioMaterialFlowID() {
		return musicMaterialFlowID;
	}

	public void setAudioMaterialFlowID(final String value) {
		musicMaterialFlowID = value;
	}
	
	public String getAlmaMaterialFlowID() {
		return almaMaterialFlowID;
	}

	public void setAlmaMaterialFlowID(final String value) {
		almaMaterialFlowID = value;
	}
	
	public String getEmuMaterialFlowID() {
		return emuMaterialFlowID;
	}

	public void setEmuMaterialFlowID(final String value) {
		emuMaterialFlowID = value;
	}	

	public void loadStructureMapFileTypes(final boolean throwFileTypesException)
			throws FileNotFoundException {
		try {
			FileTypesSingleton.getFileTypesSingleton(fileTypesPropFile);
		} catch (FileNotFoundException ex) {
			final StringBuffer message = new StringBuffer(200);
			message
					.append("Error loading the file containing Structure Map File Descriptions.\nPlease make sure the file \"");
			message.append(fileTypesPropFile);
			message
					.append("\" exists and is of valid format.\nSee the Indigo log for further details.");
			LOG.error(message.toString(), ex);
			if (throwFileTypesException) {
				final FileNotFoundException exNew = new FileNotFoundException(
						message.toString());
				exNew.fillInStackTrace();
				throw exNew; // NOPMD Stack trace is filled in
			}
		}
	}

	public MetaDataFields getCMS2MetaData() throws FileNotFoundException {
		if (cms2MetaData == null) {
			cms2MetaData = MetaDataFields.create(cms2MetaDataFile);
		}
		return cms2MetaData;
	}

	public MetaDataFields getCMS1MetaData() throws FileNotFoundException {
		if (cms1MetaData == null) {
			cms1MetaData = MetaDataFields.create(cms1MetaDataFile);
		}
		return cms1MetaData;
	}

	public MetaDataFields getNoCMSMetaData() throws FileNotFoundException {
		if (noCMSMetaData == null) {
			noCMSMetaData = MetaDataFields.create(noCMSMetaDataFile);
		}
		return noCMSMetaData;
	}

	public MetaDataFields getStaffMediatedMetaData()
			throws FileNotFoundException {
		if (staffMediatedMetaData == null) {
			staffMediatedMetaData = MetaDataFields
					.create(staffMediatedMetaDataFile);
		}
		return staffMediatedMetaData;
	}

	public static UserGroupData create(final String xmlFileName)
			throws FileNotFoundException, InvalidApplicationDataException,
			InvalidCMSSystemException {
		return new UserGroupData(xmlFileName);
	}

	public UserGroupData(final String xmlFileName)
			throws FileNotFoundException, InvalidApplicationDataException,
			InvalidCMSSystemException {
		loadUserGroup(xmlFileName, true, true);
	}

	public static UserGroupData create(final String xmlFileName,
			final boolean checkDetails, final boolean throwFileTypesException)
			throws InvalidCMSSystemException, InvalidApplicationDataException,
			FileNotFoundException {
		return new UserGroupData(xmlFileName, checkDetails,
				throwFileTypesException);
	}

	public UserGroupData(final String xmlFileName, final boolean checkDetails,
			final boolean throwFileTypesException)
			throws InvalidCMSSystemException, InvalidApplicationDataException,
			FileNotFoundException {
		loadUserGroup(xmlFileName, checkDetails, throwFileTypesException);
	}

	public List<CharacterTranslations> getCharacterTranslations() {
		return characterTranslations;
	}

	public void setCharacterTranslations(
			final List<CharacterTranslations> values) {
		characterTranslations = values;
	}

	public boolean characterTranslationExists(
			final CharacterTranslations translation) {
		boolean exists = false;
		for (CharacterTranslations translationCheck : characterTranslations) {
			if (translation.getCharacterToTranslate().equalsIgnoreCase(
					translationCheck.theCharacterToTranslate)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public boolean characterTranslationExists(final String translateFrom) {
		boolean exists = false;
		for (CharacterTranslations translationCheck : characterTranslations) {
			if (translateFrom
					.equalsIgnoreCase(translationCheck.theCharacterToTranslate)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public void addCharacterTranslation(final CharacterTranslations translation)
			throws CharacterTranslationException {
		if (characterTranslationExists(translation)) {
			throw new CharacterTranslationException(
					"Translation character already exists");
		}
		characterTranslations.add(translation);
	}

	public void addCharacterTranslation(final String translateFrom,
			final String translateTo, final ECharacterPosition position)
			throws CharacterTranslationException {
		if (characterTranslationExists(translateFrom)) {
			throw new CharacterTranslationException(
					"Translation character already exists");
		}
		final CharacterTranslations translation = new UserGroupData.CharacterTranslations(
				translateFrom, translateTo, position);
		characterTranslations.add(translation);
	}

	public void deleteCharacterTranslation(
			final CharacterTranslations translation)
			throws CharacterTranslationException {
		if (characterTranslations.contains(translation)) {
			characterTranslations.remove(translation);
		} else {
			deleteCharacterTranslation(translation.theCharacterToTranslate);
		}
	}

	public void deleteCharacterTranslation(final String characterToTranslate)
			throws CharacterTranslationException {
		boolean removedItem = false;
		for (CharacterTranslations translation : characterTranslations) {
			if (characterToTranslate
					.equalsIgnoreCase(translation.theCharacterToTranslate)) {
				characterTranslations.remove(translation);
				removedItem = true;
				break;
			}
		}
		if (!removedItem) {
			throw new CharacterTranslationException(
					"Could not find translation character to delete");
		}
	}

	public String getTranslatedName(final String translateFrom) {
		String result = translateFrom;
		for (CharacterTranslations translation : characterTranslations) {
			switch (translation.getPosition()) {
			case Beginning:
				if (result.startsWith(translation.getCharacterToTranslate())) {
					result = result.replace(translation
							.getCharacterToTranslate(), translation
							.getTranslateTo());
				}
				break;
			case End:
				if (result.endsWith(translation.getCharacterToTranslate())) {
					result = result.replace(translation
							.getCharacterToTranslate(), translation
							.getTranslateTo());
				}
				break;
			case Anywhere:
				if (result.contains(translation.getCharacterToTranslate())) {
					result = result.replace(translation
							.getCharacterToTranslate(), translation
							.getTranslateTo());
				}
				break;
			default:
				break;
			}
		}
		return result;
	}

	private void loadUserGroup(final String xmlFileName,
			final boolean checkDetails, final boolean throwFileTypesException)
			throws FileNotFoundException, InvalidApplicationDataException,
			InvalidCMSSystemException {
		String fileName = xmlFileName;
		if (fileName == null || fileName.equals("")) {
			return;
		}
		File xmlFile = new File(fileName);
		if (!xmlFile.exists()) { // Search for it
			final ClassLoader contextClassLoader = PropertiesUtil
					.getContextClassLoaderInternal();
			final Enumeration<URL> urls = PropertiesUtil.getResources(
					contextClassLoader, xmlFileName);
			if ((urls == null) || (!urls.hasMoreElements())) {
				LOG.error("Application Data file not found - " + fileName);
				throw new FileNotFoundException(fileName + " not found");
			}
			final URL xmlFileNameURL = (URL) urls.nextElement();
			xmlFile = new File(xmlFileNameURL.getPath());
			fileName = xmlFile.getPath();
		}
		loadFromXML(fileName, checkDetails, throwFileTypesException);
	}

	public void storeAsXML(final String xmlFileName) throws IOException,
			XmlException {
		final XMLHandler handler = new XMLHandler(STANDARD_OBJECT_TYPE,
				xmlFileName);
		try {
			final ArrayList<XMLObject> objects = new ArrayList<XMLObject>();
			final XMLObject documentation = XMLObject.create("Documentation");
			final String documentationDetail = "To edit this file, use Indigo.exe /MetaData";
			documentation.setObjectValue(documentationDetail);
			objects.add(documentation);
			final XMLObject object = getXMLObject(handler);
			objects.add(object);
			handler.setObjects(objects);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		handler.writeXMLFile();
	}

	public void loadFromXML(final String xmlFileName,
			final boolean checkDetails, final boolean throwFileTypesException)
			throws FileNotFoundException, InvalidCMSSystemException,
			InvalidApplicationDataException {
		final XMLHandler handler = new XMLHandler(STANDARD_OBJECT_TYPE,
				xmlFileName);
		final List<String> keys = handler.getObjectNames();
		for (String key : keys) {
			final XMLObject object = handler.getObject(key);
			if (object.getObjectType().equals(STANDARD_OBJECT_TYPE)) {
				setFromXMLObject(object, checkDetails, throwFileTypesException);
			}
		}
	}

	public XMLObject getXMLObject(final XMLHandler handler) {
		final XMLObject object = handler.createXMLObject(STANDARD_OBJECT_TYPE,
				STANDARD_OBJECT_TYPE);
		object.setObjectType(STANDARD_OBJECT_TYPE);
		object.setObjectName(STANDARD_OBJECT_TYPE);
		object.addAttribute(PROP_SHARED_TEMPLATE, sharedTemplatePath);
		object.addAttribute(PROP_INCL_CMS_2, String.format("%b",
				includeCMS2Search));
		object.addAttribute(PROP_INCL_CMS_1, String.format("%b",
				includeCMS1Search));
		object.addAttribute(PROP_INCL_NO_CMS, String.format("%b",
				includeNoCMSOption));
		object.addAttribute(PROP_INCL_PRODUCER_LIST, String.format("%b",
				includeProducerList));
		object.addAttribute(PROP_INCL_MULTI_ENTITY, String.format("%b",
				includeMultiEntityMenuItem));
		object.addAttribute(PROP_MATERIAL_FLOW_ID, materialFlowID);
		object.addAttribute(PROP_USER_PRODUCER_ID, userProducerID);
		object.addAttribute(PROP_CMS_2_METADATA_FILE, cms2MetaDataFile);
		object.addAttribute(PROP_CMS_1_METADATA_FILE, cms1MetaDataFile);
		object.addAttribute(PROP_NO_CMS_METADATA_FILE, noCMSMetaDataFile);
		object.addAttribute(PROP_STAFF_MED_METADATA_FILE,
				staffMediatedMetaDataFile);
		object.addAttribute(PROP_FILETYPES_PROP_FILE, fileTypesPropFile);
		object.addAttribute(PROP_INTERIM_FILE_LOCATION, interimFileLocation);
		object.addAttribute(PROP_CLEANUP_TYPE, cleanupType.name());
		object.addAttribute(PROP_CLEANUP_DELAY, String.format("%d",
				cleanupDelay));
		object.addAttribute(PROP_BULK_BATCH_SIZE, String.format("%d",
				bulkBatchSize));
		object.addAttribute(PROP_USER_GROUP_DESC, userGroupDesc.name());
		
		object.addAttribute(PROP_SERIAL_MATERIAL_FLOW_ID, serialMaterialFlowID);
		object.addAttribute(PROP_MONO_MATERIAL_FLOW_ID, monoMaterialFlowID);
		object.addAttribute(PROP_VIDEO_MATERIAL_FLOW_ID, videoMaterialFlowID);
		object.addAttribute(MUSIC_MATERIAL_FLOW_ID, musicMaterialFlowID);
		object.addAttribute(PROP_ALMA_MATERIAL_FLOW_ID, almaMaterialFlowID);

		for (CharacterTranslations translation : characterTranslations) {
			object.addChild("Char_" + translation.getCharacterToTranslate(),
					translation.getXMLObject(handler));
		}
		return object;
	}

	public void setFromXMLObject(final XMLObject object,
			final boolean checkDetails, final boolean throwFileTypesException)
			throws InvalidApplicationDataException, FileNotFoundException,
			InvalidCMSSystemException {
		final ApplicationData appData = ApplicationData.getInstance();
		object.setObjectType(STANDARD_OBJECT_TYPE);
		if (object.getAttribute(PROP_SHARED_TEMPLATE) != null) {
			sharedTemplatePath = object.getAttribute(PROP_SHARED_TEMPLATE);
			final File templateFile = new File(sharedTemplatePath);
			if (checkDetails
					&& ((!templateFile.exists()) || (!templateFile
							.isDirectory()))) {
				sharedTemplatePath = null; // NOPMD This is an initialisation
											// method
				final String message = "The shared template path must exist and must be a directory";
				throw new FileNotFoundException(message);
			} else {
				sharedStructTemplatePath = sharedTemplatePath + "/StructureMap";
				sharedMetadataTemplatePath = sharedTemplatePath + "/MetaData";
			}
		}
		if (object.getAttribute(PROP_INCL_CMS_2) != null) {
			try {
				includeCMS2Search = Boolean.parseBoolean(object
						.getAttribute(PROP_INCL_CMS_2));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_INCL_CMS_1) != null) {
			try {
				includeCMS1Search = Boolean.parseBoolean(object
						.getAttribute(PROP_INCL_CMS_1));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_INCL_NO_CMS) != null) {
			try {
				includeNoCMSOption = Boolean.parseBoolean(object
						.getAttribute(PROP_INCL_NO_CMS));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_INCL_PRODUCER_LIST) != null) {
			try {
				includeProducerList = Boolean.parseBoolean(object
						.getAttribute(PROP_INCL_PRODUCER_LIST));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_INCL_MULTI_ENTITY) != null) {
			try {
				includeMultiEntityMenuItem = Boolean.parseBoolean(object
						.getAttribute(PROP_INCL_MULTI_ENTITY));
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
		if (object.getAttribute(PROP_MATERIAL_FLOW_ID) != null) {
			materialFlowID = object.getAttribute(PROP_MATERIAL_FLOW_ID);
		}
		if (object.getAttribute(PROP_USER_PRODUCER_ID) != null) {
			userProducerID = object.getAttribute(PROP_USER_PRODUCER_ID);
		}
		if (object.getAttribute(PROP_CMS_2_METADATA_FILE) != null) {
			cms2MetaDataFile = object.getAttribute(PROP_CMS_2_METADATA_FILE);
			if (checkDetails) {
				cms2MetaData = MetaDataFields.create(cms2MetaDataFile);
				cms2MetaData.setCMSSystem(appData
						.getCMSSystemText(MetaDataFields.ECMSSystem.CMS2));
			}
		}
		if (object.getAttribute(PROP_CMS_1_METADATA_FILE) != null) {
			cms1MetaDataFile = object
					.getAttribute(PROP_CMS_1_METADATA_FILE);
			if (checkDetails) {
				cms1MetaData = MetaDataFields.create(cms1MetaDataFile);
				cms1MetaData.setCMSSystem(appData
						.getCMSSystemText(MetaDataFields.ECMSSystem.CMS1));
			}
		}
		if (object.getAttribute(PROP_NO_CMS_METADATA_FILE) != null) {
			noCMSMetaDataFile = object.getAttribute(PROP_NO_CMS_METADATA_FILE);
			if (checkDetails) {
				noCMSMetaData = MetaDataFields.create(noCMSMetaDataFile);
				noCMSMetaData.setCMSSystem(appData
						.getCMSSystemText(MetaDataFields.ECMSSystem.NoSystem));
			}
		}
		if (object.getAttribute(PROP_STAFF_MED_METADATA_FILE) != null) {
			staffMediatedMetaDataFile = object
					.getAttribute(PROP_STAFF_MED_METADATA_FILE);
			if (checkDetails) {
				staffMediatedMetaData = MetaDataFields
						.create(staffMediatedMetaDataFile);
				staffMediatedMetaData
						.setCMSSystem(appData
								.getCMSSystemText(MetaDataFields.ECMSSystem.StaffMediated));
			}
		}
		if (object.getAttribute(PROP_FILETYPES_PROP_FILE) != null) {
			fileTypesPropFile = object.getAttribute(PROP_FILETYPES_PROP_FILE);
			if (checkDetails) {
				this.loadStructureMapFileTypes(checkDetails);
			}
		}
		if (object.getAttribute(PROP_INTERIM_FILE_LOCATION) != null) {
			interimFileLocation = object
					.getAttribute(PROP_INTERIM_FILE_LOCATION);
		}
		if (object.getAttribute(PROP_CLEANUP_TYPE) != null) {
			cleanupType = ECleanupType.valueOf(object
					.getAttribute(PROP_CLEANUP_TYPE));
		}
		if (object.getAttribute(PROP_CLEANUP_DELAY) != null) {
			cleanupDelay = Integer.parseInt(object
					.getAttribute(PROP_CLEANUP_DELAY));
		}
		if (object.getAttribute(PROP_BULK_BATCH_SIZE) != null) {
			bulkBatchSize = Integer.parseInt(object
					.getAttribute(PROP_BULK_BATCH_SIZE));
		}
		if (object.getAttribute(PROP_USER_GROUP_DESC) != null) {
			userGroupDesc = UserGroupDesc.valueOf(object
					.getAttribute(PROP_USER_GROUP_DESC));
		}
		
		if (object.getAttribute(PROP_SERIAL_MATERIAL_FLOW_ID) != null) {
			serialMaterialFlowID = object.getAttribute(PROP_SERIAL_MATERIAL_FLOW_ID);
		}
		if (object.getAttribute(PROP_MONO_MATERIAL_FLOW_ID) != null) {
			monoMaterialFlowID = object.getAttribute(PROP_MONO_MATERIAL_FLOW_ID);
		}
		if (object.getAttribute(PROP_VIDEO_MATERIAL_FLOW_ID) != null) {
			videoMaterialFlowID = object.getAttribute(PROP_VIDEO_MATERIAL_FLOW_ID);
		}
		if (object.getAttribute(MUSIC_MATERIAL_FLOW_ID) != null) {
			musicMaterialFlowID = object.getAttribute(MUSIC_MATERIAL_FLOW_ID);
		}
		if (object.getAttribute(PROP_ALMA_MATERIAL_FLOW_ID) != null) {
			almaMaterialFlowID = object.getAttribute(PROP_ALMA_MATERIAL_FLOW_ID);
		}
		
		for (XMLObject child : object.getChildObjects()) {
			characterTranslations.add(new CharacterTranslations(child));
		}
	}
}
