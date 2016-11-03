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
package nz.govt.natlib.ndha.manualdeposit.bulkupload;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import nz.govt.natlib.ndha.common.FileUtils;
import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exceptions.IlsException;
import nz.govt.natlib.ndha.common.exceptions.MetsException;
import nz.govt.natlib.ndha.common.ilsquery.CmsRecord;
import nz.govt.natlib.ndha.common.ilsquery.CmsResults;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType;
import nz.govt.natlib.ndha.common.ilsquery.ILSSearchFacade;
import nz.govt.natlib.ndha.common.ilsquery.IlsSearchFacadeImpl;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType.eServerType;
import nz.govt.natlib.ndha.common.ilsquery.criteria.SingleCriteria;
import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.StructMapCollection;
import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositPresenter;
import nz.govt.natlib.ndha.manualdeposit.customui.SearchAttributeDetail;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidApplicationDataException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidCMSSystemException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.JobQueueException;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.UploadJob;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields.ECMSSystem;
import nz.govt.natlib.ndha.srusearchclient.SruTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class BulkUploadItem {

	private final static String XML_ROOT_NAME = "Entity";
	private final static String SEARCH_TYPE_ATTR = "SearchType";
	private final static String JOB_STATE_ATTR = "JobState";
	private final static String CMS_ID_ATTR = "CMSID";
	private final static String ENTITY_NAME_ATTR = "EntityName";
	private final static String BASE_DIRECTORY_ATTR = "BaseDirectory";
	private final static String JOB_DIRECTORY_ATTR = "JobDirectory";
	private final static String META_DATA_ATTR = "MetaDataPath";
	private final static String FILE_GROUP_COLLECTION_ATTR = "FileGroupCollection";
	private final static String STRUCT_MAP_LOCATION_ATTR = "StructMapLocation";
	private final static String UPLOAD_JOB_NAME = "UploadJobName";
	private final static Log LOG = LogFactory.getLog(BulkUploadItem.class);
	private UploadJob uploadJob;
	private JobState jobState;
	private FileSystemObject fsoSource;
	private ManualDepositPresenter theManualDepositParent;
	private AppProperties applicationProperties;
	private FileGroupCollection theEntities;
	private StructMapCollection theStructures;
	private MetaDataFields theMetaData;
	private String theCmsID;
	private ILSQueryType.eServerType searchType;
	private String searchTypeLabel;
	private String theEntityName;
	private String theBaseDirectory;
	private String theXmlFileName;
	private String jobDirectory;
	private Date depositDateTime;
	private String metaDataFile;
	private Boolean useIENameAsTitle = false;
	private final DateFormat dateFormatFileName = new SimpleDateFormat(
			"ddMMMyyyyHHmmss", Locale.ENGLISH);
	private final Lock bulkLock = new ReentrantLock();
	private boolean queryDPS = true;

	private class QueryCMS implements Runnable {

		private final Lock cmsLock = new ReentrantLock();
		private final ILSQueryType.eServerType theSearchType;
		private static final int RECORDS_PER_PAGE = 10;
		private static final int SEARCH_TIMEOUT = 15000;

		public QueryCMS(final ILSQueryType.eServerType searchType) {
			theSearchType = searchType;
		}

		public boolean lock() {
			return cmsLock.tryLock();
		}

		public void unlock() {
			cmsLock.unlock();
		}

		private String charEncode(final String queryString) {
			if (queryString == null)
				return null;
			if (queryString.contains("/"))
				return queryString.replace("/", "\\/");
			return queryString;
		}

		public void run() {
			try {
				final ILSSearchFacade search = new IlsSearchFacadeImpl(
						applicationProperties.getApplicationData().getSearchStrategyClass());
				search.setCMS2ServerUrl(applicationProperties.getApplicationData().getCMS2SearchUrl());
				search.setCMS1ServerUrl(applicationProperties.getApplicationData().getCMS1SearchUrl());
				final SearchAttributeDetail attribute = theManualDepositParent.getIDAttribute(theCmsID.trim(), theSearchType);
				final SingleCriteria criteria = new SingleCriteria(attribute.getAttribute(), attribute.getAttributeName(),
						charEncode(attribute.getValue()));
				CmsResults results = null;
				int retryLoopCount = 0;
				boolean retry = true;
				String schema = applicationProperties.getApplicationData().getSruSearchSchema();
				while (retry) {
					try {
						if (theSearchType.equals(ILSQueryType.eServerType.CMS1)){
							schema = "dc";
						}
						results = search.runQuery(criteria, theSearchType, 1, RECORDS_PER_PAGE, SEARCH_TIMEOUT,
								schema, applicationProperties.getSysProxyUser(), applicationProperties.getSysProxyPassword());
						retry = false;
					} catch (SruTimeoutException ex) {
						retryLoopCount++;
						if (retryLoopCount <= 3) {
							try {
								Thread.sleep(100);
							} catch (Exception exs) {
							}
						} else {
							LOG.error("Error retrieving CMS ID", ex);
							jobState = JobState.ErrorRetrievingCMSID;
						}
					} catch (Exception ex) {
						LOG.error("Error retrieving CMS ID", ex);
						jobState = JobState.ErrorRetrievingCMSID;
						retry = false;
					}
				}
				if (results != null && results.getRecordCount() == 1) {
					final CmsRecord rec = results.get(0);
					LOG.debug(theCmsID + " CMS results retrieved, before set: "
							+ theMetaData.getAt(0).getDataFieldValue()
							+ ", metaDataFile" + metaDataFile);
					try {
						theManualDepositParent.setCMSResults(rec, theMetaData);
						if ((theSearchType == ILSQueryType.eServerType.CMS1)
								&& (!theCmsID.equalsIgnoreCase(theMetaData.getCMSID(theSearchType.name())))) {
							jobState = JobState.InvalidCMSID;
						} else {
							theCmsID = theMetaData.getCMSID(theSearchType.name());
							LOG.debug(theCmsID + " After set: "
									+ theMetaData.getAt(0).getDataFieldValue()
									+ ", metaDataFile" + metaDataFile);
							if (!theManualDepositParent.metaDataOkay(theMetaData, true)) {
								jobState = JobState.InvalidMetaData;
							}
						}
					} catch (MetsException ex) {
						LOG.error("Error setting METS data", ex);
					}
				} else {
					if (results.getRecordCount() == 0) {
						jobState = JobState.InvalidCMSID;
					} else {
						jobState = JobState.MoreThanOneCMSID;
					}
				}
				if (jobState == JobState.QueryingCMS) {
					jobState = JobState.CMSIDRetrieved;
				}
				if(useIENameAsTitle){
					setIENameAsTitle();
				}
				setJobState(jobState);
				LOG.debug("CMS Queried, Entity Name: " + theEntityName
						+ ", CMS ID: "
						+ theMetaData.getAt(0).getDataFieldValue());
			} catch (IlsException ex) {
				LOG.error("ILS Exception occurred", ex);
			}
		}
	}

	private class QueryDPS implements Runnable {

		private final Lock _cmsLock = new ReentrantLock();

		public boolean lock() {
			return _cmsLock.tryLock();
		}

		public void unlock() {
			_cmsLock.unlock();
		}

		public void run() {
			try {
				boolean isEmuCMS = false;
				if (searchType == ILSQueryType.eServerType.CMS2){
					isEmuCMS = true;
				}
				if (theManualDepositParent.cmsIDExistsInDps(theCmsID.trim(), theMetaData.getCMSSystem(), isEmuCMS)) {
					setJobState(JobState.ItemExistsInDPS);
				} else {
					setJobState(JobState.ItemDoesNotExistInDPS);
				}
			} catch (Exception ex) {
				setJobState(JobState.ErrorQueryingDPS);
			}
		}

	}

	public enum JobState {
		Requested("Requested", false, false), QueryingCMS("Querying CMS",
				false, false), ErrorRetrievingCMSID("Error retrieving CMS ID",
				true, false), MoreThanOneCMSID("More than one CMS record",
				true, false), InvalidCMSID("Invalid CMS ID", true, false), InvalidSIP(
				"SIP is invalid - check for compulsory representations", true,
				false), InvalidMetaData("Meta data is invalid", true, false), CMSIDRetrieved(
				"CMS data retrieved", false, false), QueryingDPS(
				"Querying the DPS", false, false), ItemExistsInDPS(
				"Item already exists in the DPS", true, false), ErrorQueryingDPS(
				"There was an error querying the DPS", true, false), ItemDoesNotExistInDPS(
				"Item does not exist in the DPS", false, false), Batching(
				"Batching item for deposit to the DPS", false, false), IndigoJobCreated(
				"Indigo job created", false, true), IndigoJobCreatedJobNotFound(
				"Indigo job created but job detail no longer exists", false,
				true);

		JobState(final String description, final boolean isError,
				final boolean isComplete) {
			theDescription = description;
			isErrorState = isError;
			isCompleteJob = isComplete;
		}

		private String theDescription;
		private boolean isErrorState;
		private boolean isCompleteJob;

		public String description() {
			return theDescription;
		}

		public boolean isError() {
			return isErrorState;
		}

		public boolean isComplete() {
			return isCompleteJob;
		}
	}

	public static BulkUploadItem create(final FileSystemObject source,
			final ManualDepositPresenter manualDepositParent,
			final String cmsID, final FileGroupCollection entity,
			final StructMapCollection structures,
			final MetaDataFields metaData, final String entityName,
			final String baseDirectory, final AppProperties appProperties, Boolean IENameAsTitle)
			throws InvalidApplicationDataException, InvalidCMSSystemException {
		return new BulkUploadItem(source, manualDepositParent, cmsID, entity,
				structures, metaData, entityName, baseDirectory, appProperties, IENameAsTitle);
	}

	public BulkUploadItem(
			final FileSystemObject source,
			final ManualDepositPresenter manualDepositParent
			// , final IBulkUpload parentForm
			, final String cmsID, final FileGroupCollection entity,
			final StructMapCollection structures,
			final MetaDataFields metaData, final String entityName,
			final String baseDirectory, final AppProperties appProperties, Boolean IENameAsTitle)
			throws InvalidApplicationDataException, InvalidCMSSystemException {
		theManualDepositParent = manualDepositParent;
		theCmsID = cmsID;
		theEntities = entity;
		theStructures = structures;
		theMetaData = metaData;
		theEntityName = entityName;
		theBaseDirectory = baseDirectory;
		depositDateTime = new Date();
		applicationProperties = appProperties;
		useIENameAsTitle = IENameAsTitle;
		if (theMetaData.getCMSSystemType() == ECMSSystem.CMS2) {
			searchType = eServerType.CMS2;
			searchTypeLabel = applicationProperties.getApplicationData().getCMS2Label();
		} else if (theMetaData.getCMSSystemType() == ECMSSystem.CMS1) {
			searchType = eServerType.CMS1;
			searchTypeLabel = applicationProperties.getApplicationData().getCMS1Label();
		} else if (theMetaData.getCMSSystemType() == ECMSSystem.NoSystem) {
			String oiValue = theMetaData.getMetaDataType("objectIdentifierType").getDataFieldValue();
			if (oiValue != null)
			{
				searchType = eServerType.CMS1;
				searchTypeLabel = applicationProperties.getApplicationData().getCMS1Label();
			} else {
				throw new InvalidCMSSystemException("Invalid CMS System");
			}
		} else {
			throw new InvalidCMSSystemException("Invalid CMS System");
		}
		final String entityNamePadded = getEntityNamePadded();
		final String bulkUploadQueuePath = applicationProperties
				.getApplicationData().getBulkUploadQueuePath();
		theXmlFileName = bulkUploadQueuePath + "/" + entityNamePadded + ".xml";
		jobDirectory = bulkUploadQueuePath + "/" + entityNamePadded;
		metaDataFile = bulkUploadQueuePath + "/" + entityNamePadded
				+ "/metaData.xml";
		// log.debug(_cmsID + " set meta data file name: " + _metaDataFile);
		if(useIENameAsTitle){
			setIENameAsTitle();
		}
		setSource(source);
	}

	private String getEntityNamePadded() {
		final StringBuffer entityNamePadded = new StringBuffer();
		entityNamePadded.append(FileUtils.specialCharToUnderscore(theEntityName));
		entityNamePadded.append('_');
		entityNamePadded.append(dateFormatFileName.format(depositDateTime));
		return entityNamePadded.toString();
	}

	public static BulkUploadItem create(final String xmlFileName,
			final AppProperties appProperties,
			final ManualDepositPresenter manualDepositParent)
			throws FileNotFoundException {
		return new BulkUploadItem(xmlFileName, appProperties,
				manualDepositParent);
	}

	public BulkUploadItem(final String xmlFileName,
			final AppProperties appProperties,
			final ManualDepositPresenter manualDepositParent)
			throws FileNotFoundException {
		// log.debug("Creating new UploadJob - xmlFileName " + xmlFileName);
		if (appProperties == null) {
			LOG.debug("AppProperties is null");
		} else {
			LOG.debug("AppProperties is not null, job queue path "
					+ appProperties.getApplicationData().getJobQueuePath());
		}
		theManualDepositParent = manualDepositParent;
		applicationProperties = appProperties;
		theXmlFileName = xmlFileName;
		reloadFromSaveFile();
	}

	public JobState getJobState() {
		return jobState;
	}

	public void setJobState(final JobState value) {
		jobState = value;
		saveJob();
	}

	public boolean isQueryDPS() {
		return queryDPS;
	}

	public void setQueryDPS(final boolean value) {
		queryDPS = value;
	}

	public String getJobStatus() {
		String result;
		if ((uploadJob == null)
				|| (uploadJob.getJobState() == UploadJob.JobState.Batching)) {
			result = jobState.description();
		} else {
			result = uploadJob.getStatus();
		}
		return result;
	}

	public UploadJob.JobState getUploadJobState() {
		if (uploadJob == null) {
			return null;
		} else {
			return uploadJob.getJobState();
		}
	}

	public FileSystemObject getSource() {
		return fsoSource;
	}

	public void setSource(final FileSystemObject source) {
		fsoSource = source;
		setJobState(JobState.Requested);
	}

	public String getCMSID() {
		return theCmsID;
	}

	public void setCMSID(final String cmsID) {
		theCmsID = cmsID;
	}

	public FileGroupCollection getEntity() {
		return theEntities;
	}

	public StructMapCollection getStructures() {
		return theStructures;
	}

	public MetaDataFields getMetaData() {
		return theMetaData;
	}

	public ILSQueryType.eServerType getSearchType() {
		return searchType;
	}
	
	public String getSearchTypeLabel(){
		return searchTypeLabel;
	}

	public String getEntityName() {
		return theEntityName;
	}

	public String getBaseDirectory() {
		return theBaseDirectory;
	}

	public String getXmlFileName() {
		return theXmlFileName;
	}

	public String getJobDirectory() {
		return jobDirectory;
	}

	public String getMetaDataFile() {
		return metaDataFile;
	}

	public boolean lock() {
		return bulkLock.tryLock();
	}

	public void unlock() {
		bulkLock.unlock();
	}

	public void retrieveCMSDetails() {
		setJobState(JobState.QueryingCMS);
		final QueryCMS queryCMS = new QueryCMS(searchType);
		if (queryCMS.lock()) {
			final Thread t = new Thread(queryCMS);
			t.start();
			queryCMS.unlock();
		} else {
			LOG.debug("Couldn't lock CMS Query " + theCmsID);
			setJobState(JobState.ErrorRetrievingCMSID);
		}
	}

	public void checkDPS() {
		setJobState(JobState.QueryingDPS);
		final QueryDPS queryDPS = new QueryDPS();
		if (queryDPS.lock()) {
			final Thread t = new Thread(queryDPS);
			t.start();
			queryDPS.unlock();
		} else {
			LOG.debug("Couldn't lock DPS Query " + theCmsID);
			setJobState(JobState.ErrorQueryingDPS);
		}
	}
	
	/*
	 * If bulk load is run as 'set each file as an IE' then overwrite DCTitle with the Entity Name.
	 */
	private void setIENameAsTitle(){
		try{
			ArrayList<IMetaDataTypeExtended> metaDataTypes = (ArrayList<IMetaDataTypeExtended>) theMetaData.getMetaDataFields();
			
			for(IMetaDataTypeExtended type: metaDataTypes){
				if(type.getDataFieldName().equals("CMSDescription")){
					IMetaDataTypeExtended newCMSDescField = type;
					metaDataTypes.remove(type);
					newCMSDescField.setDataFieldValue(theEntityName);
					metaDataTypes.add(2, newCMSDescField);
					break;
				}
			}
		
		} catch (MetsException ex) {
			LOG.error("Mets Exception occurred", ex);
		}
	}

	public void deleteJob() {
		FileUtils.deleteFileOrDirectoryRecursive(jobDirectory);
		FileUtils.deleteFileOrDirectoryRecursive(theXmlFileName);
	}

	public void setJob(final UploadJob job) {
		uploadJob = job;
	}

	public UploadJob createJob() throws FileNotFoundException,
			InvalidCMSSystemException, InvalidApplicationDataException,
			JobQueueException {
		final UserGroupData userGroupData = applicationProperties.getUserData()
				.getUser(applicationProperties.getLoggedOnUser())
				.getUserGroupData();
		uploadJob = UploadJob.create(theEntityName, theEntityName, System
				.getProperty("user.name"), theEntities, theStructures,
				theMetaData, userGroupData.getUserProducerID(), userGroupData
						.getMaterialFlowID(), applicationProperties,
				theBaseDirectory, false, false, false, "None");
		setJobState(JobState.Batching);
		return uploadJob;
	}

	public void addToJob(final UploadJob job) throws JobQueueException {
		uploadJob = job;
		setJobState(JobState.Batching);
		uploadJob
				.addJob(theEntityName, theEntities, theStructures, theMetaData);
	}

	public void reloadFromSaveFile() throws FileNotFoundException {
		final XMLHandler handler = new XMLHandler(XML_ROOT_NAME, theXmlFileName);
		final List<String> keys = handler.getObjectNames();
		// There can be only one
		if (keys.size() > 1) {
			LOG.debug("Invalid job queue item - more than one object");
			throw new FileNotFoundException("Invalid job queue item");
		}
		final XMLObject object = handler.getObject(keys.get(0));
		LOG.debug("XMLObject created - getting details");
		theEntityName = object.getAttribute(ENTITY_NAME_ATTR);
		LOG.debug("_entityName " + theEntityName);

		theCmsID = object.getAttribute(CMS_ID_ATTR);
		LOG.debug("_cmsID " + theCmsID);
		searchType = ILSQueryType.eServerType.valueOf(object
				.getAttribute(SEARCH_TYPE_ATTR));
		LOG.debug("_searchType " + searchType.toString());
		theBaseDirectory = object.getAttribute(BASE_DIRECTORY_ATTR);
		LOG.debug("_baseDirectory " + theBaseDirectory);
		jobDirectory = object.getAttribute(JOB_DIRECTORY_ATTR);
		LOG.debug("_jobDirectory " + jobDirectory);
		jobState = JobState.valueOf(object.getAttribute(JOB_STATE_ATTR));
		LOG.debug("_jobState " + jobState.toString());
		metaDataFile = object.getAttribute(META_DATA_ATTR);
		LOG.debug(theCmsID + " reloadFromSaveFile set meta data file name: "
				+ metaDataFile);
		if (metaDataFile == null) {
			throw new FileNotFoundException("Could not locate MetaData file");
		} else {
			theMetaData = new MetaDataFields(metaDataFile);
		}
		// This is an initialisation method so I'm making theEntities null
		theEntities = null; // NOPMD
		for (XMLObject childObject : object.getChildObjects()) {
			if (childObject.getObjectType().equals(FILE_GROUP_COLLECTION_ATTR)) {
				theEntities = FileGroupCollection.create(childObject
						.getObjectValue(), childObject.getAttribute("xmlFile"),
						true);
			}
		}
		final List<FileGroupCollection> entities = new ArrayList<FileGroupCollection>();
		entities.add(theEntities);
		final String structureFileName = object
				.getAttribute(STRUCT_MAP_LOCATION_ATTR);
		theStructures = StructMapCollection.create(structureFileName, entities);
		if (object.getAttribute(UPLOAD_JOB_NAME) != null) {
			final String uploadJobName = object.getAttribute(UPLOAD_JOB_NAME);
			uploadJob = theManualDepositParent.getUploadJob(uploadJobName);
		}
	}

	public void saveJob() {
		FileUtils.ensureDirectoryExists(jobDirectory);
		XMLHandler handler;
		try {
			final File xmlFile = new File(theXmlFileName);
			if (xmlFile.exists()) {
				xmlFile.delete();
			}
			handler = new XMLHandler(XML_ROOT_NAME, theXmlFileName);
			final XMLObject object = handler.createXMLObject(ENTITY_NAME_ATTR,
					theEntityName);
			object.addAttribute(ENTITY_NAME_ATTR, theEntityName);
			object.addAttribute(JOB_STATE_ATTR, jobState.name());
			object.addAttribute(META_DATA_ATTR, metaDataFile);
			object.addAttribute(BASE_DIRECTORY_ATTR, theBaseDirectory);
			object.addAttribute(CMS_ID_ATTR, theCmsID);
			object.addAttribute(SEARCH_TYPE_ATTR, searchType.name());
			object.addAttribute(JOB_DIRECTORY_ATTR, jobDirectory);
			theMetaData.storeAsXML(metaDataFile);
			final String xmlFileName = jobDirectory + "/"
					+ theEntities.getEntityName() + ".xml";
			theEntities.storeAsXML(xmlFileName);
			final XMLObject collectionObject = handler.createXMLObject(
					FILE_GROUP_COLLECTION_ATTR, theEntities.getEntityName());
			collectionObject.addAttribute("xmlFile", xmlFileName);
			collectionObject.setObjectValue(theEntities.getEntityName());
			object.addChild(theEntities.getEntityName(), collectionObject);
			final String structureFileName = jobDirectory + "/Structures.xml";
			object.addAttribute(STRUCT_MAP_LOCATION_ATTR, structureFileName);
			theStructures.storeAsXML(structureFileName, true);
			if (uploadJob != null) {
				object.addAttribute(UPLOAD_JOB_NAME, uploadJob.getBatchName());
			}
			handler.addObject(object);
			handler.writeXMLFile();
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

}
