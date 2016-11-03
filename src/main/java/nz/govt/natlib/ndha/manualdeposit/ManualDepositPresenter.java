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

package nz.govt.natlib.ndha.manualdeposit;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nz.govt.natlib.ndha.common.ChecksumDigest;
import nz.govt.natlib.ndha.common.FileUtils;
//import nz.govt.natlib.ndha.common.MD5Digest;
import nz.govt.natlib.ndha.common.XMLHandler;
import nz.govt.natlib.ndha.common.XMLHandler.XMLObject;
import nz.govt.natlib.ndha.common.exceptions.MetsException;
import nz.govt.natlib.ndha.common.exceptions.XmlException;
import nz.govt.natlib.ndha.common.exlibris.IDeposit;
import nz.govt.natlib.ndha.common.exlibris.MaterialFlow;
import nz.govt.natlib.ndha.common.exlibris.Producer;
import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.common.ilsquery.CmsRecord;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType;
import nz.govt.natlib.ndha.common.mets.FSOCollection;
import nz.govt.natlib.ndha.common.mets.FileGroup;
import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.RepresentationTypes;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.SortBy;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.FixityTypes;
import nz.govt.natlib.ndha.common.mets.FileType;
import nz.govt.natlib.ndha.common.mets.StructMap;
import nz.govt.natlib.ndha.common.mets.StructMapCollection;
import nz.govt.natlib.ndha.common.xmltransformer.DcToHtmlTransformer;
import nz.govt.natlib.ndha.common.xmltransformer.DcToHtmlTransformerImpl;
import nz.govt.natlib.ndha.common.xmltransformer.QueryResults;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadItem;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadItem.JobState;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadPresenter;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadQueueManagement;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.IBulkUpload;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.CustomizeMetaDataPresenter;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.CustomizeMetaDataTableModel;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.ICustomizeMetaDataEditorView;
import nz.govt.natlib.ndha.manualdeposit.customui.CustomTreeUI;
import nz.govt.natlib.ndha.manualdeposit.customui.DepositTreeEditor;
import nz.govt.natlib.ndha.manualdeposit.customui.DepositTreeModel;
import nz.govt.natlib.ndha.manualdeposit.customui.DepositTreeModel.ETreeType;
import nz.govt.natlib.ndha.manualdeposit.customui.IconRenderer;
import nz.govt.natlib.ndha.manualdeposit.customui.LabelTextPair;
import nz.govt.natlib.ndha.manualdeposit.customui.SearchAttributeDetail;
import nz.govt.natlib.ndha.manualdeposit.customui.TableRenderer;
import nz.govt.natlib.ndha.manualdeposit.customui.TransferableTreeNode;
import nz.govt.natlib.ndha.manualdeposit.customui.TreeEditorField;
import nz.govt.natlib.ndha.manualdeposit.dialogs.ContentExists;
import nz.govt.natlib.ndha.manualdeposit.dialogs.EnterEntityNameAndPrefix;
import nz.govt.natlib.ndha.manualdeposit.exceptions.BulkLoadException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidApplicationDataException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidCMSSystemException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.JobQueueException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.SearchException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.TemplateException;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.JobQueueManagement;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.JobQueueTableModel;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.UploadJob;
import nz.govt.natlib.ndha.manualdeposit.login.ILoginPresenter;
import nz.govt.natlib.ndha.manualdeposit.login.ILoginView;
import nz.govt.natlib.ndha.manualdeposit.login.LoginEvent;
import nz.govt.natlib.ndha.manualdeposit.login.LoginListener;
import nz.govt.natlib.ndha.manualdeposit.metadata.ApplicationData;
import nz.govt.natlib.ndha.manualdeposit.metadata.EDataType;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataElementCellEditor;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataListValues;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTableModel;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTypeImpl;
import nz.govt.natlib.ndha.manualdeposit.metadata.PersonalSettings;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData.UserGroupDesc;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEvent;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEventsEditorView;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEventsPresenter;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceNotesObserver;
import nz.govt.natlib.ndha.manualdeposit.search.SearchAttributeCollection;
import nz.govt.natlib.ndha.manualdeposit.search.SearchAttributeController;
import nz.govt.natlib.ndha.srusearchclient.SruRequest;
import nz.govt.natlib.ndha.srusearchclient.SruService;
import nz.govt.natlib.ndha.srusearchclient.impl.SimpleQuery;
import nz.govt.natlib.ndha.srusearchclient.impl.SruRequestImpl;
import nz.govt.natlib.ndha.srusearchclient.impl.SruServiceImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Presenter for the main Manual Deposit form
 */
public class ManualDepositPresenter implements Serializable,
		ProvenanceNotesObserver {

	private static final long serialVersionUID = -1954676845912968993L;
	private final static Log LOG = LogFactory
			.getLog(ManualDepositPresenter.class);
	private final List<DefaultMutableTreeNode> theNodeInClipboard = new ArrayList<DefaultMutableTreeNode>();
	private boolean droppingIsAllowed = false;
	private final IManualDepositMainFrame manualDepositFrame;
	private boolean entityRootSet = false;
	private ETreeType dragFromTreeType;
	private boolean isCopying = false;
	private FileSystemObject theFsoRoot = null;
	private FileSystemObject fsoRootTemp = null;
	private FileSystemObject theFsoRootFile = null;
	private FileSystemObject fsoRootFileTemp = null;
	private final List<JTree> trees = new ArrayList<JTree>();
	private JTree theFileSystemTree;
	private JTree theEntityTree;
	private JTree theStructMapTree;
	private JComboBox theTemplateList;
	private JComboBox theStructTemplateList;
	private JComboBox theSortByList;
	private JComboBox theFixityTypesList;
	private JTable theMetaDataTable;
	private MetaDataTableModel metaDataTableModel;
	private JTable theJobQueueRunningTable;
	private JTable theJobQueuePendingTable;
	private JTable theJobQueueFailedTable;
	private JTable theJobQueueDepositedTable;
	private JTable theJobQueueCompleteTable;
	private JTable theCustomizeMetaDataTable;
	private JList theProducerList;
	private JList theMaterialFlowList;
	private JMenu theFavouritesMenu;
	private static final String[] JOB_QUEUE_COLUMNS = { "Entity", "Status" };
	private static final String NO_TEMPLATE_TEXT = "<no template selected - clear data>";
	private final AppProperties applicationProperties;
	private UserGroupData userGroupData = null;
	private JobQueueManagement jobQueueMgmt;
	private String currentIconDirectory = "icons/16/";
	private Font standardFont;
	private CustomizeMetaDataTableModel customizeMetaDataTableModel;
	private static BuildIEWorker buildIE_Worker;

	// System defined meta data fields
	private final static String SUBMITTED_BY_NAME = "SubmittedBy";
	private final static String SHARED_TEMPLATE_TEXT = " (shared)";
	private final static String BLANK_ID = "unknown";
	private final ILoginView loginView;
	private final ILoginPresenter theLoginPresenter;
	private List<Producer> producers;
	private MetaDataFields.ECMSSystem currentCmsSystem = null;
	private final static String PROVENANCE_EVENT_ID_TYPE = "ProvenanceEventIdentifierType";
	private final static String PROVENANCE_EVENT_ID_VALUE = "ProvenanceEventIdentifierValue";
	private final static String PROVENANCE_EVENT_OUTCOME = "ProvenanceEventOutcome";
	private final static String PROVENANCE_EVENT_OUTCOME_DETAIL = "ProvenanceEventOutcomeDetail";
	private final static String PROVENANCE_EVENT_EVENT_TYPE = "ProvenanceNoteEventType";
	private final static String TEMPLATE_IDENTIFIER = "OriginalObjectType";
	private static final String PROVENANCE_EVENT_DESCRIPTION = "ProvenanceEventDescription";
//	private MD5Digest theDigest = null;
	private ChecksumDigest theDigest = null;
	private final static String XML_SUFFIX = ".xml";
	private final static String ERROR_OCCURRED = "An error has occurred";
	private SearchAttributeController searchAttributes;
	private boolean customizeMetaData = false;
	private boolean isStaffMediated = false;
	private boolean isNoCMS = false;
	private boolean isCMS2 = false;
	// These were used for performance improvements - leave for now in case
	// other problems surface
	// private long _loadChildrenTime = 0;
	// private long _addEntitiesTime = 0;
	// private long _addStructMapTime = 0;
	// private long _addFileSystemRootTime = 0;

	// private ProvenanceEventsPresenter provenanceNotesPresenter;
	
	public SearchAttributeController getSearchAttributes() {
		return searchAttributes;
	}

	public void setSearchAttributes(SearchAttributeController searchAttributes) {
		this.searchAttributes = searchAttributes;
	}

	private final FileFilter xmlFilter = new FileFilter() {
		public boolean accept(final File file) {
			return file.isFile()
					&& file.getName().toLowerCase().endsWith(".xml");
		}
	};

	public ManualDepositPresenter(final IManualDepositMainFrame theFrame,
			final ILoginView login, final ILoginPresenter loginPresenter,
			final AppProperties appProperties) {
		manualDepositFrame = theFrame;
		applicationProperties = appProperties;
		loginView = login;
		theLoginPresenter = loginPresenter;
		//Below mentioned try catch block is not required for 'Staff Mediated' login if we want to skip url checking....
//		try {
//			searchAttributes = SearchAttributeController.create(appProperties);
//		} catch (SearchException ex) {
//			LOG.error("Error loading search attributes", ex);
//			theFrame.showError("Error loading search attributes",
//					"The search sttributes could not be loaded", ex);
//		}
	}

	public void setupScreen() {
		LOG.debug("Presenter setup");

		if (applicationProperties == null) {
			LOG.debug("AppProperties null");
		} else {
			LOG.debug("AppProperties not null");
		}
		// _metaDataTableModel.setPropertyValue(_submittedByName,
		// System.getProperty("user.name")); //Must go after the
		// _Frame.setupScreen
		// Must set up _jobQueueMgmt after the frame has run setupScreen
		// The local _jobQueueTable and _jobQueueTableModel are set up there.
		// Things won't die if they aren't set up, but the tables won't update
		// properly
		standardFont = applicationProperties.getApplicationData()
				.getPersonalSettings().getStandardFont();
		loginView.setFormFont(standardFont);
		loginView.setPresenter(theLoginPresenter);
		theLoginPresenter.clearLoginListeners();
		theLoginPresenter.addLoginListener(new LoginListener() {
			public void loginFailed(final LoginEvent e) {
				manualDepositFrame.showError("Unable to log in", e
						.getErrorMessage());
				System.exit(-1);
			}

			public void loginSucceeded(final LoginEvent e) {
				processLogin(e.getLoginName(), e.getLoginPassword());
			}
		});
		if (applicationProperties.getLoggedOnUser() == null) {
			LOG.debug("Presenter setup, About to setup login");
			theLoginPresenter.setup();
			LOG.debug("Presenter setup, login done");
		} else {
			processLogin(applicationProperties.getLoggedOnUser(), "");
		}
	}

	private void processLogin(final String userName, final String password) {
		try {
			applicationProperties.setLoggedOnUser(userName);
			if (userName.equals(System.getProperty("user.name"))){
				applicationProperties.setSysProxyUser(userName);
			} else {
				applicationProperties.setSysProxyUser(System.getProperty("user.name"));
			}
			applicationProperties.setSysProxyPassword(password);
		} catch (Exception ex) {
			manualDepositFrame.showError("Could not log on",
					"An error has occurred\n" + ex.getMessage());
			theLoginPresenter.setup();
			return;
		}
		applicationProperties.setLoggedOnUserPassword(password);
		
		try {
			userGroupData = applicationProperties.getUserData().getUser(
					applicationProperties.getLoggedOnUser()).getUserGroupData();
			
	//		String pattern = userGroupData.getNoCMSMetaDataFile().substring(userGroupData.getNoCMSMetaDataFile().lastIndexOf("/")+1);
	//		if (pattern.matches("Unpublished MetaData.xml")){
			if (userGroupData.isIncludeCMS2Search() == false && userGroupData.isIncludeCMS1Search() == false){
				LOG.debug("Staff Mediated log in. CMS will not be loaded.");
			} else {
				try {
					searchAttributes = SearchAttributeController.create(applicationProperties);
				} catch (SearchException ex) {
					LOG.error("Error loading search attributes", ex);
				}
			}
		} catch (Exception ex) {
			manualDepositFrame.showError("Load Error",
					"Error setting user data", ex);
		}
		try {
			manualDepositFrame.setupScreen(applicationProperties,
					applicationProperties.getApplicationData()
							.getSettingsPath());
		} catch (Exception ex) {
			manualDepositFrame.showError("Load Error",
					"Error setting up screen", ex);
		}
		LOG.debug("Presenter setup, About to reset screen");
		resetScreen();
		LOG.debug("Presenter setup, Screen reset");
		manualDepositFrame.setFormFont(standardFont);
		manualDepositFrame.showView();
		LOG.debug("loginSucceeded, before load producers list");
		if (showProducers()) {
			if (!userGroupData.getUserGroupDesc().equals(UserGroupDesc.Published)){
				loadProducersList();
			}
		}
		LOG.debug("loginSucceeded, end");
		setupJobQueue();
		checkForBulkLoadQueue();
	}

	public void checkForInitialLoadScreenSizes(final FormControl control,
			final JSplitPane verticalMain, final String verticalMainName,
			final JSplitPane verticalSub, final String verticalSubName,
			final JSplitPane horizontalMain, final String horizontalMainName,
			final JSplitPane horizontalSub, final String horizontalSubName) {
		if ((control.getWidth() == -1) && (control.getHeight() == -1)
				&& (control.getLeft() == -1) && (control.getTop() == -1)) {
			final GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			final GraphicsDevice[] gs = ge.getScreenDevices();
			if (gs.length > 0) {
				final DisplayMode dm = gs[0].getDisplayMode();
				final int screenWidth = dm.getWidth() - 20;
				final int screenHeight = dm.getHeight() - 50;
				// Set form bounds
				control.setTop(10);
				control.setLeft(10);
				control.setWidth(screenWidth);
				control.setHeight(screenHeight);
				control.resizeScreen();
				// Set divider positions
				int dividerPosition = 175;
				verticalMain.setDividerLocation(dividerPosition);
				control.setExtra(verticalMainName, dividerPosition);
				dividerPosition = screenHeight / 3;
				verticalSub.setDividerLocation(dividerPosition);
				control.setExtra(verticalSubName, dividerPosition);
				dividerPosition = screenWidth / 3;
				horizontalMain.setDividerLocation(dividerPosition);
				control.setExtra(horizontalMainName, dividerPosition);
				horizontalSub.setDividerLocation(dividerPosition);
				control.setExtra(horizontalSubName, dividerPosition);
			}
		}

	}

	private void setupJobQueue() {
		try {
			LOG.debug("Before create jobQueueMgmt");
			jobQueueMgmt = JobQueueManagement.create(applicationProperties,
					manualDepositFrame, theJobQueueRunningTable,
					theJobQueuePendingTable, theJobQueueFailedTable,
					theJobQueueDepositedTable, theJobQueueCompleteTable);
		} catch (Exception ex) {
			manualDepositFrame.showError("Error loading Job Queue Management",
					ERROR_OCCURRED, ex);
			reportException(ex);
		}
		if (jobQueueMgmt == null) {
			LOG.debug("Job Queue Mgmt null");
		} else {
			LOG.debug("Job Queue Mgmt  not null");
		}
	}

	public boolean bulkUploadsPresent() {
		final String bulkUploadDirectory = applicationProperties
				.getApplicationData().getBulkUploadQueuePath();
		final File[] files = BulkUploadQueueManagement
				.getUploadFiles(bulkUploadDirectory);
		return (files != null && files.length > 0);
	}

	public void checkForBulkLoadQueue() {
		if (bulkUploadsPresent()) {
			final IBulkUpload bulkForm = manualDepositFrame
					.createBulkUploadForm();
			final BulkUploadPresenter presenter = BulkUploadPresenter.create(
					bulkForm, this, applicationProperties);
			presenter.showBulkUploads();
			manualDepositFrame.checkButtons();
		}
	}

	public void loadPath(final String path) {
		final FileSystemObject fso = new FileSystemObject("My Computer", null,
				null);
		fso.loadChildren(false);
		if ((path == null) || path.equals("")) {
			addFileSystemRoot(fso, false, false);
		} else {
			fso.ensureChildPathLoaded(path);
			addFileSystemRoot(fso, false, false, path);
			final FSOCollection coll = FSOCollection.create();
			coll.add(fso);
			final FileSystemObject child = coll.getFSOByFullPath(path, true);
			selectNode(child, ETreeType.FileSystemTree);
		}
	}

	public void resetScreen() {
		manualDepositFrame.setWaitCursor(true);
		setCustomizeMetaData(false);
		entityRootSet = false;
		theFsoRootFile = null;
		customizeMetaDataTableModel = new CustomizeMetaDataTableModel();
		clearTemplate();
		loadTemplate();
		resetRadioButtons();
		final String currentPath = applicationProperties.getApplicationData()
				.getPersonalSettings().getCurrentPath();
		loadPath(currentPath);
		addIntellectualEntities(null);
		addStructMap(null);
		updateWorkerProgress(0);
		manualDepositFrame.setWaitCursor(false);
	}

	public JPopupMenu getProducerMenu() {
		final JPopupMenu menu = new JPopupMenu();
		final JMenuItem item = new JMenuItem("Refresh producer list");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				itmRefreshProducerListActionPerformed(evt);
			}
		});
		menu.add(item);
		return menu;
	}

	public JPopupMenu getFileSystemMenu(final DefaultMutableTreeNode node) {
		JPopupMenu menu = null;
		fsoRootFileTemp = null;
		if (node.getUserObject() instanceof FileSystemObject) {
			boolean allowBulkUpload = false;
			final FileSystemObject fso = (FileSystemObject) node
					.getUserObject();
			menu = new JPopupMenu();
			if ((fso != null) && (fso.getFile() != null)
					&& (fso.getFile().exists())) {
				if (!entityRootSet) {
					stopEditingMetaData();
					allowBulkUpload = applicationProperties.getUserData()
							.getUser(applicationProperties.getLoggedOnUser())
							.isAllowBulkLoad();
					String addRootText;
					boolean canAddMultiRoot = false;
					setCustomizeMetaData(false);
					if (fso.getIsFile()) {
						addRootText = "Use file to describe IE root";
						final DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
								.getParent();
						if (nodeParent.getUserObject() instanceof FileSystemObject) {
							fsoRootTemp = (FileSystemObject) nodeParent
									.getUserObject();
							fsoRootFileTemp = fso;
						} else {
							fsoRootTemp = fso;
						}
					} else {
						addRootText = "Set as root of Intellectual Entity";
						canAddMultiRoot = (userGroupData != null && userGroupData
								.isIncludeMultiEntityMenuItem());
						fsoRootTemp = fso;
					}
					JMenuItem item = new JMenuItem(addRootText);
					item.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent evt) {
							manualDepositFrame.setWaitCursor(true);
							itmSetRootActionPerformed(evt);
							manualDepositFrame.setWaitCursor(false);
						}
					});
					menu.add(item);
					if (canAddMultiRoot) {
						item = new JMenuItem(
								"Set each file as an Intellectual Entity");
						item
								.addActionListener(new java.awt.event.ActionListener() {
									public void actionPerformed(
											final java.awt.event.ActionEvent evt) {
										itmSetMultipleRootActionPerformed(evt);
									}
								});
						menu.add(item);
						
						// Added 5/09/2013 by Ben
						// New menu option for creating multiple complex IEs
						item = new JMenuItem(
								"Set each folder as an Intellectual Entity");
						item
								.addActionListener(new java.awt.event.ActionListener() {
									public void actionPerformed(
											final java.awt.event.ActionEvent evt) {
										itmSetMultipleRootFolderActionPerformed(evt);
									}
								});
						menu.add(item);
						
						
					}
				}
				if (fso.getIsFile()) {
					if (menu.getComponentCount() > 0) {
						menu.addSeparator();
					}
					if (isWindows()){
						JMenuItem item = new JMenuItem("Open File");
						item.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(
									final java.awt.event.ActionEvent evt) {
								itmOpenFile(evt);
							}
						});
						menu.add(item);
						
						item = new JMenuItem("Open File Location");
						item.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(
									final java.awt.event.ActionEvent evt) {
								itmOpenFileLocation(evt);
							}
						});
						menu.add(item);
					}
				} else {
					if (menu.getComponentCount() > 0) {
						menu.addSeparator();
					}
					JMenuItem item = new JMenuItem(
							"Store this directory as a favourite");
					item.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent evt) {
							itmStoreAsFavourite(evt);
						}
					});
					menu.add(item);
					item = new JMenuItem("Refresh directory file list");
					item.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent evt) {
							itmRefreshDirectory(evt);
						}
					});
					menu.add(item);
					if (allowBulkUpload) {
						item = new JMenuItem("Bulk load this directory");
						item
								.addActionListener(new java.awt.event.ActionListener() {
									public void actionPerformed(
											final java.awt.event.ActionEvent evt) {
										bulkLoadDirectory(evt);
									}
								});
						if (!metaDataOkay(false)) {
							item.setEnabled(false);
							item.setToolTipText("Meta data incomplete");
						}
						menu.add(item);
						
						item = new JMenuItem("Bulk load each file as an Intellectual Entity");
						item
								.addActionListener(new java.awt.event.ActionListener() {
									public void actionPerformed(
											final java.awt.event.ActionEvent evt) {
										bulkLoadDirectoryAsIEs(evt);
									}
								});
						if (!metaDataOkay(false)) {
							item.setEnabled(false);
							item.setToolTipText("Meta data incomplete");
						}
						menu.add(item);
					}
				}
			}
		}
		if (menu == null || menu.getComponentCount() == 0) {
			return null;
		} else {
			return menu;
		}
	}
	
	private boolean isWindows(){
		boolean isWindows = false;
		String os = System.getProperty("os.name");
		if (os != null){
			os = os.toLowerCase();
			isWindows = os.indexOf("windows") != -1;
		}
		return isWindows;
	}

	public boolean isCustomizeMetaData() {
		return customizeMetaData;
	}

	public void setCustomizeMetaData(boolean customizeMetaData) {
		this.customizeMetaData = customizeMetaData;
	}
	
	public boolean isCMS2() {
		return isCMS2;
	}

	public void setIsCMS2(boolean isCMS2) {
		this.isCMS2 = isCMS2;
		
	}
	
	public boolean isStaffMediated() {
		return isStaffMediated;
	}

	public void setStaffMediated(boolean isStaffMediated) {
		this.isStaffMediated = isStaffMediated;
	}
	
	public boolean isNoCMS() {
		return isNoCMS;
	}

	public void setNoCMS(boolean isNoCMS) {
		this.isNoCMS = isNoCMS;
		
	}
	
	public void resetRadioButtons(){
		setIsCMS2(false);
		setStaffMediated(false);
		setNoCMS(false);
	}

	public JPopupMenu getJobQueueMenu(final JTable table) {
		return jobQueueMgmt.getJobQueueMenu(table);
	}

	private ETreeType getTreeType(final JTree tree) {
		final DepositTreeModel model = (DepositTreeModel) tree.getModel();
		return model.getTreeType();
	}
	
	/*
	 * 	Compare customized meta data with parent MetaDataFields object. 
	 *	Return a copy of parent with any meta data fields updated where customized field is not blank. 
	 */
	private MetaDataFields buildCustomizedMetaData(String entityName){
		MetaDataFields originalMetaData = metaDataTableModel.getMetaData();
		MetaDataFields parentMetaData = null;
		Map<String, EDataType> EDataTypes = metaDataTableModel.getMetaData().getMetaDataEDataTypes("By Desc");
		try {
			parentMetaData = originalMetaData.getCopy();
			if(customizeMetaDataTableModel.getCustomMetaData() != null){
				Map<String, String> customMetaData = customizeMetaDataTableModel.getCustomMetaDataForEntity(entityName);		
				Set<String> customMetaDataFields = customMetaData.keySet();
				
				for(String customField: customMetaDataFields){
					for(IMetaDataTypeExtended parentField: parentMetaData){
						if(parentField.getDataFieldDescription().equals(customField)){
							String childFieldValue = customMetaData.get(customField);
							if(!childFieldValue.isEmpty()){
								
								if(EDataTypes.get(customField).toString().equals("Multi-select")){
									
									// Replace custom Metadata field value with correct background value from MetaDataListValue object							
									IMetaDataTypeExtended entity = metaDataTableModel.getMetaData().getMetaDataTypeByDesc(customField);
									List<MetaDataListValues> fieldValues = entity.getListItems();
									for(MetaDataListValues value: fieldValues){
										if(childFieldValue.equals(value.getDisplay())){
											parentMetaData.setMetaDataValue(parentField.getDataFieldName(), value.getValue());
											break;
										}
									}
									
								} else{
									parentMetaData.setMetaDataValue(parentField.getDataFieldName(), childFieldValue);
								}
								
								break;
							} 
							else{
								break;
							}
						}					
					}
				}
			}
		} catch (Exception ex) {
			manualDepositFrame.showError("Couldn't customize metadata", ERROR_OCCURRED, ex);
			reportException(ex);
		}
		
		// Add custom provenance notes for each IE.
		try {
			Map<String, List<IMetaDataTypeExtended>> allChildProvNotes = customizeMetaDataTableModel
					.getChildProvenanceEvents();
			List<IMetaDataTypeExtended> entityProvNotes = allChildProvNotes.get(entityName);
			if (entityProvNotes != null) {
				for (IMetaDataTypeExtended provNote : entityProvNotes) {
					parentMetaData.addMetaData(provNote);
				}
			}
		} catch (Exception ex) {
			manualDepositFrame.showError("Couldn't add provenance notes to the entity", ERROR_OCCURRED, ex);
			reportException(ex);
		}
		return parentMetaData;
	}

	private JTree getTree(final ETreeType treeType) {
		JTree retVal = null;
		for (JTree tree : trees) {
			final DepositTreeModel model = (DepositTreeModel) tree.getModel();
			if (model.getTreeType().equals(treeType)) {
				retVal = tree;
				break;
			}
		}
		return retVal;
	}

	private DepositTreeModel getModel(final ETreeType treeType) {
		DepositTreeModel retVal = null;
		for (JTree tree : trees) {
			DepositTreeModel model = (DepositTreeModel) tree.getModel();
			if (model.getTreeType().equals(treeType)) {
				retVal = model;
				break;
			}
		}
		return retVal;
	}

	private void processDragNodes(RepresentationTypes dragToType) {
		ArrayList<DefaultMutableTreeNode> nodeInClipboard = getClipboardCopy();
		putIERootInClipboard();
		DefaultMutableTreeNode newNode = addEntity(dragToType);
		replaceClipboardWithCopy(nodeInClipboard);
		dropNodes(newNode, getModel(ETreeType.EntityTree));
	}

	public boolean canAddRepresentationType(char keyPressed) {
		boolean canAdd = true;
		ArrayList<FileGroupCollection> entities = getEntities();
		for (int i = 0; i < RepresentationTypes.values().length; i++) {
			RepresentationTypes typeToTest = RepresentationTypes.values()[i];
			if (keyPressed == typeToTest.hotKeyValue()) {
				if (!typeToTest.allowMultiples()) {
					canAdd = (!entityTypeAdded(typeToTest, entities));
				}
				break;
			}
		}
		return canAdd;
	}

	public boolean canMoveIEFile(FileSystemObject file, Object parent,
			boolean moveUp) {
		FSOCollection siblings = new FSOCollection();
		if (parent instanceof FileSystemObject) {
			FileSystemObject parentFSO = (FileSystemObject) parent;
			siblings = parentFSO.getChildren();
		} else if (parent instanceof FileGroup) {
			FileGroup parentFileGroup = (FileGroup) parent;
			siblings = parentFileGroup.getChildren();
		}
		return canMoveFile(file, siblings, moveUp);
	}

	private boolean canMoveFile(FileSystemObject file, FSOCollection siblings,
			boolean moveUp) {
		boolean canMove = false;
		if (siblings.size() > 0) {
			if (moveUp) {
				canMove = (!file.equals(siblings.get(0)));
			} else {
				canMove = (!file.equals(siblings.get(siblings.size() - 1)));
			}
		}
		return canMove;
	}

	private void refreshMetaData() {
		if (theMetaDataTable != null) {
			theMetaDataTable.repaint();
		}
		if (metaDataTableModel != null) {
			metaDataTableModel.fireTableStructureChanged();
			setupMetaDataColumns();
		}
	}

	public void onCloseOfProvenanceNotesDialog(
			ProvenanceEventsPresenter provenanceEventsPresenter) {
		MetaDataFields fields = metaDataTableModel.getMetaData();
		ArrayList<IMetaDataTypeExtended> metaDataTypes = (ArrayList<IMetaDataTypeExtended>) fields
				.getMetaDataFields();

		removeCurrentProvenanceFilesFrom(fields, metaDataTypes);

		int noteNumber = 1;
		for (ProvenanceEvent note : provenanceEventsPresenter
				.getProvenanceNotes()) {
			IMetaDataTypeExtended provenanceNoteMetaDataType = note
					.toMetadataType("Note " + noteNumber++);
			metaDataTypes.add(provenanceNoteMetaDataType);
		}
		refreshMetaData();
	}

	private void removeCurrentProvenanceFilesFrom(MetaDataFields fields,
			ArrayList<IMetaDataTypeExtended> metaDataTypes) {
		List<IMetaDataTypeExtended> fieldsToRemove = new ArrayList<IMetaDataTypeExtended>();
		for (IMetaDataTypeExtended metaDataType : metaDataTypes) {
			if (metaDataType.getDataType() == EDataType.ProvenanceNote) {
				fieldsToRemove.add(metaDataType);
			}
		}

		for (IMetaDataTypeExtended metaDataType : fieldsToRemove) {
			fields.getMetaDataFields().remove(metaDataType);
		}
	}

	public void editProvenanceNotes(ProvenanceEventsEditorView editor) {
		MetaDataFields metaDataFieldCollection = metaDataTableModel.getMetaData();
		if (!entityRootSet){
			theFsoRoot = null;
		}
		ProvenanceEventsPresenter provenanceNotesPresenter = new ProvenanceEventsPresenter(
				editor, applicationProperties, getEntities(), theFsoRoot);
		editor.setPresenter(provenanceNotesPresenter);
		provenanceNotesPresenter.openForm(metaDataFieldCollection.getMetaDataFields(), customizeMetaDataTableModel.getChildProvenanceEvents());
		List<ProvenanceEvent> events = provenanceNotesPresenter.getProvenanceNotes();
		Map<String, List<ProvenanceEvent>> childEvents = provenanceNotesPresenter.getChildProvenanceNotes();
		metaDataFieldCollection.replaceProvenanceEvents(events);
		customizeMetaDataTableModel.replaceChildProvenanceEvents(childEvents);
		refreshMetaData();
	}
	
	public void customizeMetaData(ICustomizeMetaDataEditorView editor) {
		ICustomizeMetaDataEditorView metaDataForm = manualDepositFrame.createCustomizeMetaDataForm();
		// PASS A PERSISTANT CustomizeMetaDataTableModel TO THE PRESENTER -----------		
		CustomizeMetaDataPresenter customizeMetaDataPresenter = CustomizeMetaDataPresenter.create(metaDataForm, this, customizeMetaDataTableModel, metaDataTableModel, theEntityTree);
		editor.setPresenter(customizeMetaDataPresenter);	
		ArrayList<FileGroupCollection> entities = getEntities();
		MetaDataFields metaData = metaDataTableModel.getMetaData();
		if (metaData != null){
			customizeMetaDataPresenter.addTableData(entities, metaData);
		}		
		customizeMetaDataPresenter.showMetaData();
	}

	public boolean canMoveStructObject(Object child, Object parent,
			boolean moveUp) {
		boolean canMove = false;
		if (child instanceof FileSystemObject) { // parent must be a StructMap
			FileSystemObject fileFSO = (FileSystemObject) child;
			StructMap parentStruct = (StructMap) parent;
			FSOCollection siblings = parentStruct.getFiles();
			siblings.reSortList(true);
			canMove = canMoveFile(fileFSO, siblings, moveUp);
		} else { // Must be a StructMap
			StructMap map = (StructMap) child;
			StructMapCollection siblings = null;
			if (parent instanceof StructMap) {
				StructMap parentStruct = (StructMap) parent;
				siblings = parentStruct.getChildren();
			} else { // It will be a string - the root
				siblings = getStructures();
			}
			if (siblings != null) {
				if (moveUp) {
					canMove = (!map.equals(siblings.get(0)));
				} else {
					canMove = (!map.equals(siblings.get(siblings.size() - 1)));
				}
			}
		}
		return canMove;
	}

	public void moveIEFile(FileSystemObject file, Object parent, boolean moveUp) {
		if (canMoveIEFile(file, parent, moveUp)) {
			FSOCollection siblings = new FSOCollection();
			if (parent instanceof FileSystemObject) {
				FileSystemObject parentFSO = (FileSystemObject) parent;
				siblings = parentFSO.getChildren();
				siblings.setSortBy(parentFSO.getSortBy());
			} else if (parent instanceof FileGroup) {
				FileGroup parentFileGroup = (FileGroup) parent;
				siblings = parentFileGroup.getChildren();
				siblings.setSortBy(SortBy.UserArranged);
			}
			siblings.getFSOList();
			int fileSortOrder = file.getSortOrder();
			FileSystemObject fileToSwapWith;
			if (moveUp) {
				fileToSwapWith = siblings.get(0);
				for (int i = 0; i < siblings.size(); i++) {
					if (file.equals(siblings.get(i))) {
						break;
					}
					fileToSwapWith = siblings.get(i);
				}
			} else {
				fileToSwapWith = siblings.get(siblings.size() - 1);
				for (int i = siblings.size() - 1; i >= 0; i--) {
					if (file.equals(siblings.get(i))) {
						break;
					}
					fileToSwapWith = siblings.get(i);
				}
			}
			// Shouldn't happen, but apparently sometimes does
			if (fileToSwapWith.getSortOrder() == fileSortOrder) {
				siblings.resetOrder();
				fileSortOrder = file.getSortOrder();
			}
			file.setSortOrder(fileToSwapWith.getSortOrder());
			fileToSwapWith.setSortOrder(fileSortOrder);
			siblings.getFSOList();
			addIntellectualEntities(getEntities());
			this.selectNode(file, ETreeType.EntityTree);
			updateWorkerProgress(100);
		}
	}

	public void selectNode(Object nodeObject, ETreeType treeType) {
		JTree tree = getTree(treeType);
		DepositTreeModel model = (DepositTreeModel) tree.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model
				.getRoot();
		DefaultMutableTreeNode node = findNode(rootNode, nodeObject);
		if (node != null) {
			LOG.debug("selectNode Node object found");
			TreePath path = new TreePath(node.getPath());
			tree.scrollPathToVisible(path);
			tree.setSelectionPath(path);
		} else {
			LOG.debug("selectNode Node object not found");
		}
	}

	public void moveStructObject(Object child, Object parent, boolean moveUp) {
		if (canMoveStructObject(child, parent, moveUp)) {
			if (child instanceof FileSystemObject) {
				FileSystemObject file = (FileSystemObject) child;
				StructMap parentStruct = (StructMap) parent; // Must be a
				// StructMap
				FSOCollection siblings = parentStruct.getFiles();
				int fileSortOrder = file.getStructSortOrder();
				FileSystemObject fileToSwapWith;
				if (moveUp) {
					fileToSwapWith = siblings.get(0);
					for (int i = 0; i < siblings.size(); i++) {
						if (file.equals(siblings.get(i))) {
							break;
						}
						fileToSwapWith = siblings.get(i);
					}
				} else {
					fileToSwapWith = siblings.get(siblings.size() - 1);
					for (int i = siblings.size() - 1; i >= 0; i--) {
						if (file.equals(siblings.get(i))) {
							break;
						}
						fileToSwapWith = siblings.get(i);
					}
				}
				file.setStructSortOrder(fileToSwapWith.getStructSortOrder());
				fileToSwapWith.setStructSortOrder(fileSortOrder);
				siblings.reSortList(true);
			} else { // Must be a StructMap
				StructMap childStruct = (StructMap) child;
				StructMapCollection siblings;
				if (parent instanceof StructMap) {
					StructMap parentStruct = (StructMap) parent; // Must also be
					// a
					// StructMap
					siblings = parentStruct.getChildren();
				} else { // Must be a string - the root
					siblings = getStructures();
				}
				int childSortOrder = childStruct.getSortOrder();
				StructMap structToSwapWith;
				if (moveUp) {
					structToSwapWith = siblings.get(0);
					for (int i = 0; i < siblings.size(); i++) {
						if (childStruct.equals(siblings.get(i))) {
							break;
						}
						structToSwapWith = siblings.get(i);
					}
				} else {
					structToSwapWith = siblings.get(siblings.size() - 1);
					for (int i = siblings.size() - 1; i >= 0; i--) {
						if (childStruct.equals(siblings.get(i))) {
							break;
						}
						structToSwapWith = siblings.get(i);
					}
				}
				childStruct.setSortOrder(structToSwapWith.getSortOrder());
				structToSwapWith.setSortOrder(childSortOrder);
				siblings.getStructMapList(); // Re-order
			}
			addStructMap(getStructures());
			this.selectNode(child, ETreeType.StructMapTree);
		}
	}

	private DefaultMutableTreeNode findNode(DefaultMutableTreeNode node,
			Object userObject) {
		DefaultMutableTreeNode retVal = null;
		if (node.getUserObject().equals(userObject)) {
			retVal = node;
		}
		if (retVal == null) {
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode) node
						.getChildAt(i);
				retVal = findNode(nodeChild, userObject);
				if (retVal != null) {
					break;
				}
			}
		}
		return retVal;
	}

	public boolean canCreateAutoStructItem() {
		boolean canCreate = false;
		if ((theEntityTree != null) && (theEntityTree.getSelectionCount() > 0)) {
			TreePath[] paths = theEntityTree.getSelectionPaths();
			for (int i = 0; i < paths.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
						.getLastPathComponent();
				if (node.getUserObject() instanceof FileSystemObject) {
					canCreate = true;
					break;
				}
			}
		}
		return canCreate;
	}

	private void getMatchingFiles(String baseFileName,
			DefaultMutableTreeNode rootNode, FSOCollection files) {
		if (rootNode.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) rootNode.getUserObject();
			if (fso.getFileNameWithoutRepTypeOrSuffix().equals(baseFileName)) {
				files.add(fso);
			}
		}
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode
					.getChildAt(i);
			getMatchingFiles(baseFileName, node, files);
		}
	}

	public void createAutoStructItem(boolean fromSingleFile) {
		if (canCreateAutoStructItem()) {
			JTree tree = getTree(ETreeType.EntityTree);
			if ((tree != null) && (tree.getSelectionCount() > 0)) {
				String structNodeName = getNewStructNodeName();
				if (structNodeName == null) {
					return;
				}
				StructMapCollection structure = getStructures();
				FSOCollection files = new FSOCollection();
				if (fromSingleFile) {
					// Complex one - need to recurse through tree & get all
					// matching files
					TreePath[] paths = tree.getSelectionPaths();
					DefaultMutableTreeNode nodeSource = null;
					for (int i = 0; i < paths.length; i++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
								.getLastPathComponent();
						if (node.getUserObject() instanceof FileSystemObject) {
							nodeSource = node;
							break;
						}
					}
					if (nodeSource != null) {
						DepositTreeModel model = (DepositTreeModel) tree
								.getModel();
						DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model
								.getRoot();
						FileSystemObject fso = (FileSystemObject) nodeSource
								.getUserObject();
						String baseFileName = fso
								.getFileNameWithoutRepTypeOrSuffix();
						getMatchingFiles(baseFileName, rootNode, files);
					}
				} else {
					TreePath[] paths = tree.getSelectionPaths();
					for (int i = 0; i < paths.length; i++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
								.getLastPathComponent();
						if (node.getUserObject() instanceof FileSystemObject) {
							FileSystemObject fso = (FileSystemObject) node
									.getUserObject();
							files.add(fso);
						}
					}
				}
				
				files.setSortBy(manualDepositFrame.getCurrentSortBy());
				files.reSortList();
				int structSortOrder = 0;
				for (FileSystemObject fso : files) {
					fso.setStructSortOrder(structSortOrder);
					structSortOrder++;
				}
				StructMap map = StructMap
						.create(structNodeName, null, files, 0);
				structure.add(map);
				addStructMap(structure);
			}
		}
	}

	private String getNewStructNodeName() {
		return (manualDepositFrame.getInput("Structure Name",
				"Please enter the new structure name"));
	}

	public JPopupMenu processFileTreeKeyPress(char keyPressed, TreePath[] paths) {
		JPopupMenu menu = null;
		if (entityRootSet) {
			ArrayList<FileGroupCollection> entities = getEntities();
			for (int i = 0; i < RepresentationTypes.values().length; i++) {
				RepresentationTypes typeToTest = RepresentationTypes.values()[i];
				if (keyPressed == typeToTest.hotKeyValue()) {
					boolean doProcess = true;
					if (!typeToTest.allowMultiples()) {
						doProcess = (!entityTypeAdded(typeToTest, entities));
					}
					if (doProcess) {
						dragFromTreeType = ETreeType.FileSystemTree;
						processDragNodes(typeToTest);
					}
					break;
				}
			}
		} else {
			if (keyPressed == 'm') {
				if (pathLengthOkay(paths)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
							.getLastPathComponent();
					menu = getFileSystemMenu(node);
				}
			} else if (keyPressed == 's') {
				if (pathLengthOkay(paths)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
							.getLastPathComponent();
					setEntityRoot(node);
				}
			} else if (keyPressed == 'e') {
				if (pathLengthOkay(paths)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
							.getLastPathComponent();
					setMultipleRoot(node);
				}
			} else if (keyPressed == 'o') {
				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					openFile(node);
				}
			} else if (keyPressed == 'f') {
				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					storeAsFavourite(node);
				}
			}
		}
		return menu;
	}

	private boolean pathLengthOkay(TreePath[] path) {
		boolean isOkay = true;
		if (path.length > 1) {
			manualDepositFrame.showError("Invalid Selection",
					"You can only select one file to process at a time");
		}
		return isOkay;
	}

	public void setEntityRoot(FileSystemObject fsoRoot,
			FileSystemObject fsoRootFile) {
		theFsoRoot = fsoRoot;
		theFsoRootFile = fsoRootFile;
		setRoot();
	}

	public void setEntityRoot(DefaultMutableTreeNode node) {
		if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fsoRoot = (FileSystemObject) node.getUserObject();
			FileSystemObject fsoRootFile = null;
			if (fsoRoot.getIsFile()) {
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
						.getParent();
				fsoRootFile = fsoRoot;
				fsoRoot = (FileSystemObject) nodeParent.getUserObject();
			}
			setEntityRoot(fsoRoot, fsoRootFile);
		}
	}

	public void setMultipleRoot(DefaultMutableTreeNode node) {
		if ((userGroupData != null && userGroupData
				.isIncludeMultiEntityMenuItem())
				&& (node.getUserObject() instanceof FileSystemObject)) {
			theFsoRoot = (FileSystemObject) node.getUserObject();
			setMultipleRoot_startThread();
		}
	}

	public void setEntityNameAndFilePrefix(String entityName, String filePrefix) {
		setRoot_startThread(entityName, filePrefix, true, false);
	}

	private void setRoot() {
		setRoot(true, true, false);
	}

	private void setRoot(boolean promptForEntityName, boolean updateScreen, boolean childrenLoaded) {
		if (theFsoRootFile != null) {
			String[] defaultInputSplit = theFsoRootFile.getNameParts(true);
			String entityName = defaultInputSplit[0];
			String entityPrefix = entityName;
			if (promptForEntityName) {
				EnterEntityNameAndPrefix getDetails = new EnterEntityNameAndPrefix(
						manualDepositFrame.getComponent(), true,
						applicationProperties.getApplicationData()
								.getSettingsPath(), entityName, entityPrefix);
				getDetails.setVisible(true);
				if (!getDetails.isCancelled()) {
					setRoot_startThread(getDetails.getEntityName(), getDetails.getFilePrefix(), updateScreen, childrenLoaded);
				}
			} else {
				setRoot_startThread(entityName, entityPrefix, updateScreen, childrenLoaded);
			}
		} else {
			setRoot_startThread(null, null, updateScreen, childrenLoaded);
		}
	}
	
	/**
	 * SetRoot for use in the Bulk Loader. To handle standard bulk load and bulk load each file as an IE.
	 * @param promptForEntityName
	 * @param updateScreen
	 * @param childrenLoaded
	 * @param bulkLoadEachFileAsIE
	 * Added by Ben. 05/08/2015.
	 */
	private void setRoot(boolean promptForEntityName, boolean updateScreen, boolean childrenLoaded, boolean bulkLoadEachFileAsIE) {
		if (theFsoRootFile != null) {
			String[] defaultInputSplit = theFsoRootFile.getNameParts(true);
			String entityName;
			String entityPrefix = defaultInputSplit[0];
			
			if(bulkLoadEachFileAsIE){
				entityName = theFsoRootFile.getFileNameWithoutRepTypeOrSuffix();
			}
			else{
				entityName = defaultInputSplit[0];
			}
			
			setRoot(entityName, entityPrefix, updateScreen, childrenLoaded);
		} 
		else{
			setRoot(null, null, updateScreen, childrenLoaded);
		}
	}

	private boolean thereAreMissingFiles() {
		return ((theDigest != null) && (theDigest.getMissingFiles().size() > 0));
	}
	
	private boolean thereAreDuplicateFiles() {
		return ((theDigest != null) && (theDigest.getDuplicateFiles().size() > 0));
	}

	private void showMissingFiles() {
		if (thereAreMissingFiles()) {
			manualDepositFrame.showMissingFiles(applicationProperties
					.getApplicationData().getSettingsPath(), theDigest
					.getMissingFiles());
		}
	}
	
	private void showDuplicateFiles() {
		if (thereAreDuplicateFiles()) {
			manualDepositFrame.showDuplicateFiles(applicationProperties
					.getApplicationData().getSettingsPath(), theDigest
					.getDuplicateFiles()
					);
		}
	}
	
	
	/**
	 * 
	 * Added by Ben. 14/10/2013.
	 */
	private void setMultipleRootFromFolders_startThread(){
		
		BuildIEWorker buildIE = new BuildIEWorker("SetMultipleRootFromFolders");
		buildIE.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("progress")) {
                    int progress = (Integer) evt.getNewValue();
                    manualDepositFrame.setProgressLevel(progress);
                } 
            }
        });
		buildIE.execute();
		
		return;
	}
	
	
	/**
	 * 
	 * Added by Ben. 14/10/2013.
	 */
	private void setMultipleRoot_startThread(){
		
		BuildIEWorker buildIE = new BuildIEWorker("SetMultipleRoot");
		buildIE.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("progress")) {
                    int progress = (Integer) evt.getNewValue();
                    manualDepositFrame.setProgressLevel(progress);
                } 
            }
        });
		buildIE.execute();
		
		return;
	}
	
	
	/**
	 * 
	 * Added by Ben. 14/10/2013.
	 */
	private void setRoot_startThread(String entityName, String filePrefix, boolean updateScreen, boolean childrenLoaded){
		
		BuildIEWorker buildIE = new BuildIEWorker("SetRoot", entityName, filePrefix, updateScreen, childrenLoaded);
		buildIE.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("progress")) {
                    int progress = (Integer) evt.getNewValue();
                    manualDepositFrame.setProgressLevel(progress);
                } 
            }
        });
		buildIE.execute();
		
		return;
	}
	
	
	/**
	 * 
	 * Added by Ben. 14/10/2013.
	 */
	private void dragFromFileSystemTree_startThread(List<FileGroupCollection> entities, FileSystemObject rootNode, boolean recursive, boolean isEditingEntity){
		
		BuildIEWorker buildIE = new BuildIEWorker("dragFromFileSystemTree", entities, rootNode, recursive, isEditingEntity);
		buildIE.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("progress")) {
                    int progress = (Integer) evt.getNewValue();
                    manualDepositFrame.setProgressLevel(progress);
                } 
            }
        });
		buildIE.execute();
		
		return;
	}
	
	
	/**
	 * 
	 * Added by Ben. 14/10/2013.
	 */
	private void updateWorkerProgress(int val){
		if(buildIE_Worker != null){
			buildIE_Worker.myPublish(val);
			try {
				Thread.sleep(50);
			} 
			catch (Exception ignore) {
	        }
		}
		else if(val == 0){
			manualDepositFrame.setProgressLevel(val);
		}
	}
	
	
	/**
	 * 
	 * Added by Ben. 14/10/2013.
	 */
	public class BuildIEWorker extends SwingWorker<Object, Object> {

		private String buildIEType;
		private String entityName;
		private String filePrefix;
		private boolean updateScreen;
		private boolean childrenLoaded;
		private List<FileGroupCollection> entities;
		private FileSystemObject rootNode;
		private boolean recursive;
		private boolean isEditingEntity;

		public BuildIEWorker(String buildIEType) {
			this.buildIEType = buildIEType;
	    }
		
	    public BuildIEWorker(String buildIEType, String entityName, String filePrefix, boolean updateScreen, boolean childrenLoaded) {
	    	this.buildIEType = buildIEType;
	    	this.entityName = entityName;
			this.filePrefix = filePrefix;
			this.updateScreen = updateScreen;
			this.childrenLoaded = childrenLoaded;
	    }
	    
	    public BuildIEWorker(String buildIEType, List<FileGroupCollection> entities) {
	    	this.buildIEType = buildIEType;
	    	this.entities = entities;
	    }
	    
	    
	    public BuildIEWorker(String buildIEType, List<FileGroupCollection> entities, FileSystemObject rootNode, boolean recursive, boolean isEditingEntity){
	    	this.buildIEType = buildIEType;
	    	this.entities = entities;
	    	this.rootNode = rootNode;
			this.recursive = recursive;
			this.isEditingEntity = isEditingEntity;
	    }
		
		
        @Override
        protected Object doInBackground() throws Exception {
        	manualDepositFrame.setWaitCursor(true);
        	buildIE_Worker = this;
        	if(buildIEType.equals("SetRoot")){
        		setRoot(entityName, filePrefix,	updateScreen, childrenLoaded);
        	}
        	else if(buildIEType.equals("SetMultipleRoot")){
        		setMultipleRoot();
        	}
        	else if(buildIEType.equals("SetMultipleRootFromFolders")){
        		setMultipleRootFromFolders();
        	}
        	else if(buildIEType.equals("dragFromFileSystemTree")){
        		publish(20);
        		addIntellectualEntities(entities);
				addFileSystemRoot(rootNode, recursive, isEditingEntity);
        	}
            return null;
        }
        
        
        @Override
        protected void done() {
        	manualDepositFrame.setWaitCursor(true);
        	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theFileSystemTree.getModel().getRoot();
        	expandNode(theFileSystemTree, rootNode, true);
        	theFileSystemTree.repaint();
        	rootNode = (DefaultMutableTreeNode) theStructMapTree.getModel().getRoot();
        	expandNode(theStructMapTree, rootNode, true);
        	theStructMapTree.repaint();
        	rootNode = (DefaultMutableTreeNode) theEntityTree.getModel().getRoot();
        	expandNode(theEntityTree, rootNode, true);
    		// Update all IE nodes again - to fix issue of labels being truncated with ellipsis, eg. "Som..." instead of "Some Folder"  
//        	theEntityTree.updateUI();
        	
        	// 14.Oct.2014 - Ben
        	// Note - this has now been fixed by setting theEntityTree to use a Large Model - in method addHandlers().
        	// updateUI() fixes ellipsis problem but causes bug with renaming entities and files.

           	setProgress(100);
           	manualDepositFrame.setWaitCursor(false);
           	buildIE_Worker = null;
        }
        
        @Override
        protected void process(List<Object> values) {
            for (Object number : values) {
            	setProgress((Integer)number);
            }
        }
        
        public void myPublish(int value) {
            publish(value);
          }
        
    }
	
	

	private void setRoot(String entityName, String filePrefix,
			boolean updateScreen, boolean childrenLoaded) {
		String theFilePrefix = null;
		if ((theFsoRootFile != null) && (filePrefix == null)) {
			manualDepositFrame.showError("Cannot set entity root",
					"No file prefix found");
			return;
		}
		else if((theFsoRootFile != null) && (filePrefix != null)){
			theFilePrefix = filePrefix;
		}
//		manualDepositFrame.setWaitCursor(true);
		entityRootSet = true;
		if (!childrenLoaded) {
			if (theFsoRootFile != null) {
				theFsoRoot.loadChildren(true, false, filePrefix, false);
			} else {
				theFsoRoot.loadChildren(true);
			}
		}
		try {
			String fixityType = theFixityTypesList.getSelectedItem().toString();
			theDigest = theFsoRoot.getChildren().checkForChecksumDigestFile(
					theFsoRoot.getFullPath(), fixityType, theFilePrefix);
			DepositTreeModel model = (DepositTreeModel) theEntityTree
					.getModel();
			model.setChecksumDigest(theDigest);
		} catch (Exception ex) {
			reportException(ex);
		}
		showMissingFiles();
		showDuplicateFiles();
		List<FileGroupCollection> entities;
		updateWorkerProgress(10);
		if (theFsoRootFile != null) {
			entities = addEntitiesFromFileStructure(entityName);
		} else {
			entities = addEntitiesFromFileStructure(null);
		}
		updateWorkerProgress(70);
		addStructMapFromFileStructure(entities);
		if (updateScreen) {
//			if (theDigest != null){
//				List<FileSystemObject> fsoChildren = theFsoRoot.getChildren().getFSOList();
//				for (FileSystemObject fsoChild: fsoChildren){
//					if (fsoChild.getOriginalMD5() != null){
//						fsoChild.setMD5Value(fsoChild.getOriginalMD5());
//					}
//				}
//			}
			addFileSystemRoot(theFsoRoot, true, true);
		}
		applicationProperties.getApplicationData().getPersonalSettings()
				.setCurrentPath(theFsoRoot.getFullPath());
//		manualDepositFrame.setWaitCursor(false);
		updateWorkerProgress(90);
	}
	
	
	
	private void setMultipleRoot() {
//		manualDepositFrame.setWaitCursor(true);
		entityRootSet = true;
		theFsoRoot.loadChildren(true);
		//Customize metadata is activated only when atleast one metadata field is customizable and when setting multiple root.
		if (isMetadataCustomizable()){
			setCustomizeMetaData(true);
		}else{
			setCustomizeMetaData(false);
		}
		ArrayList<FileGroupCollection> groups = new ArrayList<FileGroupCollection>();
		final int maxTypes = RepresentationTypes.values().length;
		RepresentationTypes defaultType = RepresentationTypes.values()[0];
		for (int i = 0; i < maxTypes; i++) {
			RepresentationTypes testType = RepresentationTypes.values()[i];
			if (testType.isDefaultType()) {
				defaultType = RepresentationTypes.values()[i];
				break;
			}
		}
		FSOCollection allFiles = theFsoRoot.getAllChildren(true, true);
		if (allFiles != null) {
			FSOCollection representationTypeDirectories = new FSOCollection();
			for (FileSystemObject fso : theFsoRoot.getChildren()) {
				if ((!fso.getIsFile()) && (fso.getEntityType() != null)) {
					representationTypeDirectories.add(fso);
				}
			}
			try {
				String fixityType = theFixityTypesList.getSelectedItem().toString();
				theDigest = allFiles.checkForChecksumDigestFile(theFsoRoot
						.getFullPath(), fixityType, null);
				DepositTreeModel model = (DepositTreeModel) theEntityTree
						.getModel();
				model.setChecksumDigest(theDigest);
			} catch (Exception ex) {
				reportException(ex);
			}
			showMissingFiles();
			showDuplicateFiles();
			
			//********************
			int totalFiles = allFiles.size();
			int filesProcessed = 0;
			float percentageComplete = 0;
			int progressSection = 0;
			updateWorkerProgress(1);
			//***********************
			
			for (FileSystemObject fso : allFiles) {
				if (fso.getIsFile()
						&& !parentIsRepTypeDirectory(
								representationTypeDirectories, fso)) {
					if (fso.getOriginalChecksum() == null && theDigest != null) {
						ChecksumDigest.FileStatus status = theDigest
								.getFileStatus(fso);
						if (status != null) {
							fso
									.setOriginalChecksum(status.getFSO()
											.getOriginalChecksum());
						}
					}
					if (fileShouldBeAdded(fso)) {
						String entityName = fso.getFileName();
						int entityNumber = 1;
						String entityNameDup = String.format("%s(%d)",
								entityName, entityNumber);
						for (FileGroupCollection testGroup : groups) {
							if ((testGroup.getEntityName()
									.equalsIgnoreCase(entityName))
									|| (testGroup.getEntityName()
											.equalsIgnoreCase(entityNameDup))) {
								entityNumber++;
								entityNameDup = String.format("%s(%d)",
										entityName, entityNumber);
							}
						}
						if (entityNumber > 1) {
							entityName = entityNameDup;
						}
						FileGroupCollection group = new FileGroupCollection(
								entityName, theFsoRoot.getFullPath());
						FSOCollection theChildren = new FSOCollection();
						theChildren.add(fso);
						FileGroup newGroup = FileGroup.create(defaultType
								.description(), BLANK_ID, defaultType,
								theChildren);
						try {
							group.add(newGroup);
							for (FileSystemObject subTypeFSO : representationTypeDirectories) {
								FileSystemObject fsoTest = subTypeFSO
										.getChildren()
										.getFSOByFileNameNoSuffixOrRepType(
												fso
														.getFileNameWithoutRepTypeOrSuffix(),
												true);
								if (fsoTest != null) {
									if (fsoTest.getOriginalChecksum() == null
											&& theDigest != null) {
										ChecksumDigest.FileStatus status = theDigest
												.getFileStatus(fsoTest);
										if (status != null) {
											fsoTest.setOriginalChecksum(status
													.getFSO().getOriginalChecksum());
										}
									}
									FSOCollection theSubChildren = new FSOCollection();
									theSubChildren.add(fsoTest);
									String description = subTypeFSO
											.getEntityType().description();
									String dirName = subTypeFSO
											.getInterpretedFileName();
									RepresentationTypes repType = subTypeFSO
											.getEntityType();
									if (!dirName.equalsIgnoreCase(repType
											.fullDirectoryName())
											&& (!dirName
													.equalsIgnoreCase(repType
															.alternateDirectoryName()))) {
										description += " - " + dirName;
									}
									FileGroup subGroup = FileGroup.create(
											description, BLANK_ID, subTypeFSO
													.getEntityType(),
											theSubChildren);
									group.add(subGroup);
								}
							}
							groups.add(group);
						} catch (Exception ex) {
							manualDepositFrame.showError(
									"Couldn't add file group",
									"An error has occurred in "
											+ newGroup.getEntityName(), ex);
							reportException(ex);
						}
					}
				}
				// UPDATE PROGRESS BAR
				filesProcessed++;
				percentageComplete = ((float)filesProcessed / (float)totalFiles) * 100;
    				if((int)percentageComplete > 75 && progressSection < 75){
    					updateWorkerProgress(23);
    					progressSection = 75;
    				}
    				else if((int)percentageComplete > 50 && progressSection < 50){
    					updateWorkerProgress(15);
    					progressSection = 50;
    				}    					
    				else if((int)percentageComplete > 25 && progressSection < 25){
    					updateWorkerProgress(8);
    					progressSection = 25;
    				}
			}
			updateWorkerProgress(30);
		}
		addIntellectualEntities(groups);
		updateWorkerProgress(65);
		addFileSystemRoot(theFsoRoot, true, true);
		updateWorkerProgress(85);
		applicationProperties.getApplicationData().getPersonalSettings()
				.setCurrentPath(theFsoRoot.getFullPath());
	}
	
	
	/**
	 * setMultipleRootFromFolders creates multiple complex IEs. Using the first level of sub-folders below the parent as each IE. 
	 * Added 05/09/2013 by Ben.
	 */
	private void setMultipleRootFromFolders() {
//		manualDepositFrame.setWaitCursor(true);
		entityRootSet = true;
		theFsoRoot.loadChildren(true);
		//Customize metadata is activated only when atleast one metadata field is customizable and when setting multiple root.
		if (isMetadataCustomizable()){
			setCustomizeMetaData(true);
		}else{
			setCustomizeMetaData(false);
		}
		ArrayList<FileGroupCollection> groups = new ArrayList<FileGroupCollection>();
		final int maxTypes = RepresentationTypes.values().length;
		RepresentationTypes defaultType = RepresentationTypes.values()[0];
		for (int i = 0; i < maxTypes; i++) {
			RepresentationTypes testType = RepresentationTypes.values()[i];
			if (testType.isDefaultType()) {
				defaultType = RepresentationTypes.values()[i];
				break;
			}
		}
		FSOCollection allFiles = theFsoRoot.getChildren(true);
		if (allFiles != null) {
			FSOCollection representationTypeDirectories = new FSOCollection();
			try {
				String fixityType = theFixityTypesList.getSelectedItem().toString();
				theDigest = allFiles.checkForChecksumDigestFile(theFsoRoot
						.getFullPath(), fixityType, null);
				DepositTreeModel model = (DepositTreeModel) theEntityTree
						.getModel();
				model.setChecksumDigest(theDigest);
			} catch (Exception ex) {
				reportException(ex);
			}
			showMissingFiles();
			showDuplicateFiles();
			
			//********************
			int totalFiles = allFiles.size();
			int filesProcessed = 0;
			float percentageComplete = 0;
			int progressSection = 0;
			updateWorkerProgress(1);
			//***********************
			
			// Iterate through  1st level sub-folders
			for (FileSystemObject fso : allFiles) {
				if (!fso.getIsFile()
						&& !parentIsRepTypeDirectory(
								representationTypeDirectories, fso)) {
					if (fso.getOriginalChecksum() == null && theDigest != null) {
						ChecksumDigest.FileStatus status = theDigest
								.getFileStatus(fso);
						if (status != null) {
							fso
									.setOriginalChecksum(status.getFSO()
											.getOriginalChecksum());
						}
					}
					if (fileShouldBeAdded(fso)) {
						String entityName = fso.getFileName();
						int entityNumber = 1;
						String entityNameDup = String.format("%s(%d)",
								entityName, entityNumber);
						for (FileGroupCollection testGroup : groups) {
							if ((testGroup.getEntityName()
									.equalsIgnoreCase(entityName))
									|| (testGroup.getEntityName()
											.equalsIgnoreCase(entityNameDup))) {
								entityNumber++;
								entityNameDup = String.format("%s(%d)",
										entityName, entityNumber);
							}
						}
						if (entityNumber > 1) {
							entityName = entityNameDup;
						}
						FileGroupCollection group = new FileGroupCollection(
								entityName, theFsoRoot.getFullPath());
						FSOCollection theChildren = new FSOCollection();
						// *************************************************
						// Adds all children of sub-folder to Digital Original FileGroup
						for (FileSystemObject  fsoChild: fso.getChildren(false)) {
							
							// Check file against Files to Ignore list first
							if (fileShouldBeAdded(fsoChild)) {
								// Add fixity value from Digest object
								if (fsoChild.getIsFile()) {
									if (fsoChild.getOriginalChecksum() == null && theDigest != null) {
										ChecksumDigest.FileStatus status = theDigest.getFileStatus(fsoChild);
										if (status != null) {
											fsoChild.setOriginalChecksum(status.getFSO().getOriginalChecksum());
										}
									}
								}
								
								theChildren.add(fsoChild);
							}
						}
						// *************************************************
						FileGroup newGroup = FileGroup.create(defaultType
								.description(), BLANK_ID, defaultType,
								theChildren);
						try {
							group.add(newGroup);
							groups.add(group);
						} catch (Exception ex) {
							manualDepositFrame.showError(
									"Couldn't add file group",
									"An error has occurred in "
											+ newGroup.getEntityName(), ex);
							reportException(ex);
						}
					}
				}
				// UPDATE PROGRESS BAR
				filesProcessed++;
				percentageComplete = ((float)filesProcessed / (float)totalFiles) * 100;
    				if((int)percentageComplete > 75 && progressSection < 75){
    					updateWorkerProgress(23);
    					progressSection = 75;
    				}
    				else if((int)percentageComplete > 50 && progressSection < 50){
    					updateWorkerProgress(15);
    					progressSection = 50;
    				}    					
    				else if((int)percentageComplete > 25 && progressSection < 25){
    					updateWorkerProgress(8);
    					progressSection = 25;
    				}
			}
			updateWorkerProgress(30);
		}
		addIntellectualEntities(groups);
		updateWorkerProgress(65);
		addFileSystemRoot(theFsoRoot, true, true);
		updateWorkerProgress(85);
		applicationProperties.getApplicationData().getPersonalSettings()
				.setCurrentPath(theFsoRoot.getFullPath());
//		manualDepositFrame.setWaitCursor(false);
	}
	
	

	public void expandNode(JTree whichTree, DefaultMutableTreeNode node,
			boolean recurse) {
		
		whichTree.expandPath(new TreePath(node.getPath()));
		if (recurse && node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
						.getChildAt(i);
				expandNode(whichTree, child, recurse);
			}
		}
	}

	public void addChildFiles(DefaultMutableTreeNode rootNode,
			FSOCollection children, DepositTreeModel model,
			String pathToDisplay, JTree whichTree) {
		LOG.debug("addChildFiles, root: " + rootNode.getUserObject());
		model.reload(rootNode);
/*		for (FileSystemObject fso : children) {
			if (fso.getFile() == null) {
				LOG.debug("add child (file is null): " + fso.getDescription());
			} else {
				LOG.debug("add child (file is not null): "
						+ fso.getDescription());
			}
			if ((!model.getTreeType().equals(ETreeType.FileSystemTree))
					|| (!fso.getIsFile()) || (!fileIsInEntity(fso))) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode();
				node.setUserObject(fso);
				model.insertNodeInto(node, rootNode, rootNode.getChildCount());
				addChildFiles(node, fso.getChildren(), model, pathToDisplay,
						whichTree);
				if ((pathToDisplay != null)
						&& (pathToDisplay.equals(fso.getFullPath()))) {
					expandNode(whichTree, node, false);
					whichTree.scrollPathToVisible(new TreePath(node.getPath()));
				}
			}
		}*/
		for (int i=0; i<children.size(); i++) {
			FileSystemObject fso = children.get(i);
			if (fso.getFile() == null) {
				LOG.debug("add child (file is null): " + fso.getDescription());
			} else {
				LOG.debug("add child (file is not null): "
						+ fso.getDescription());
			}
			if ((!model.getTreeType().equals(ETreeType.FileSystemTree))
					|| (!fso.getIsFile()) || (!fileIsInEntity(fso))) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode();
				node.setUserObject(fso);
				model.insertNodeInto(node, rootNode, rootNode.getChildCount());
				addChildFiles(node, fso.getChildren(), model, pathToDisplay,
						whichTree);
				if ((pathToDisplay != null)
						&& (pathToDisplay.equals(fso.getFullPath()))) {
					expandNode(whichTree, node, false);
					whichTree.scrollPathToVisible(new TreePath(node.getPath()));
				}
			}
		}
	}

	public void addFileSystemRoot(FileSystemObject rootNode, boolean recursive,
			boolean isEditingEntity) {
		addFileSystemRoot(rootNode, recursive, isEditingEntity, null);
	}

	public void addFileSystemRoot(FileSystemObject fso, boolean recursive,
			boolean isEditingEntity, String pathToDisplay) {
		fso.setSortBy(manualDepositFrame.getCurrentSortBy());
		String pathWanted = pathToDisplay;
		if (pathWanted != null) {
			File fileWanted = new File(pathWanted);
			while (fileWanted != null && !fileWanted.exists()) {
				fileWanted = fileWanted.getParentFile();
			}
			if (fileWanted != null) {
				pathWanted = fileWanted.getAbsolutePath();
			}
		}
		manualDepositFrame.setCurrentDirectory(pathWanted);
		manualDepositFrame.checkButtons();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootNode.setUserObject(fso);
		DepositTreeModel model;
		if (theFileSystemTree.getModel() instanceof DepositTreeModel) {
			model = (DepositTreeModel) theFileSystemTree.getModel();
			model.setRoot(rootNode);
		} else {
			model = new DepositTreeModel(rootNode, ETreeType.FileSystemTree);
			theFileSystemTree.setModel(model);
		}
		LOG.debug("addFileSystemRoot");
		addChildFiles(rootNode, fso.getChildren(), model, pathWanted,
				theFileSystemTree);
		if(buildIE_Worker == null){
			expandNode(theFileSystemTree, rootNode, recursive);
		}
		updateWorkerProgress(80);
		theFileSystemTree.repaint();
		theEntityTree.repaint();
	}

	public DefaultMutableTreeNode addIntellectualEntity(
			DefaultMutableTreeNode rootNode, FileGroup entity, boolean editEntry) {
		DepositTreeModel model = (DepositTreeModel) theEntityTree.getModel();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
		newNode.setUserObject(entity);
		model.insertNodeInto(newNode, rootNode, rootNode.getChildCount());
		if (entity.getChildren() == null) {
			entity.setChildren(FSOCollection.create());
		}
		addChildFiles(newNode, entity.getChildren(), model, null, null);
		TreePath newPath = new TreePath(newNode.getPath());
		theEntityTree.scrollPathToVisible(newPath);
		theEntityTree.setSelectionPath(newPath);
		theEntityTree.repaint();
		if (editEntry) {
			editEntity();
		}
		return newNode;
	}

	public void editEntity() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) theEntityTree
				.getSelectionPath().getLastPathComponent();
		if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) node.getUserObject();
			fso.setIsEditing(true);
		}
		theEntityTree.startEditingAtPath(theEntityTree.getSelectionPath());
		if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) node.getUserObject();
			fso.setIsEditing(false);
		}
	}

	public void addIntellectualEntities(List<FileGroupCollection> entities) {
		updateWorkerProgress(50);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		DepositTreeModel model;
		if (theEntityTree.getModel() instanceof DepositTreeModel) {
			model = (DepositTreeModel) theEntityTree.getModel();
			model.setRoot(rootNode);
		} else {
			model = new DepositTreeModel(rootNode, ETreeType.EntityTree);
			theEntityTree.setModel(model);
		}
		if (entities == null) {
			rootNode.setUserObject("Intellectual Entity");
		} else {
			// Set folder as Root
			if (entities.size() == 1) {
				FileGroupCollection collection = entities.get(0);
				rootNode.setUserObject(collection);
				for (int i = 0; collection.getFileGroupList() != null
						&& i < collection.getFileGroupList().size(); i++) {
					addIntellectualEntity(rootNode, collection
							.getFileGroupList().get(i), false);
				}
			} 
			// Set each file as IE
			else {
				rootNode.setUserObject(theFsoRoot.getDescription());
				for (FileGroupCollection entity : entities) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
					childNode.setUserObject(entity);
					model.insertNodeInto(childNode, rootNode, rootNode
							.getChildCount());
					for (int i = 0; entity.getFileGroupList() != null
							&& i < entity.getFileGroupList().size(); i++) {
						addIntellectualEntity(childNode, entity
								.getFileGroupList().get(i), false);
					}
				}
			}
		}
		// When the Swing Worker is used, expanding the leaf nodes has been moved to the end of the IE building process. Now located in the Swing Worker's Done() method.
		if(buildIE_Worker == null){
			expandNode(theEntityTree, rootNode, true);
		}
		updateWorkerProgress(60);
		theEntityTree.repaint();
	}

	public void addStructMapItem(StructMap structMapItem,
			DefaultMutableTreeNode rootNode, boolean editEntry) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
		newNode.setUserObject(structMapItem);
		DepositTreeModel model = (DepositTreeModel) theStructMapTree.getModel();
		model.insertNodeInto(newNode, rootNode, rootNode.getChildCount());
		for (int i = 0; structMapItem.getChildren() != null
				&& i < structMapItem.getChildren().size(); i++) {
			addStructMapItem(structMapItem.getChildren().get(i), newNode, false);
		}
		for (int i = 0; structMapItem.getFiles() != null
				&& i < structMapItem.getFiles().size(); i++) {
			DefaultMutableTreeNode newFileNode = new DefaultMutableTreeNode();
			newFileNode.setUserObject(structMapItem.getFiles().get(i));
			model.insertNodeInto(newFileNode, newNode, newNode.getChildCount());
		}
		TreePath newPath = new TreePath(newNode.getPath());
		theStructMapTree.scrollPathToVisible(newPath);
		theStructMapTree.setSelectionPath(newPath);
		if (editEntry) {
			editStructMap();
		}
	}

	// The parameter "nodeIndex" has been added. Contains a specified position in the tree that the new node will be inserted.
	public void addFileToStructMap(FileSystemObject fso, DefaultMutableTreeNode rootNode, int nodeIndex, boolean editEntry) {
		// Assumes that the structure has already been created - this is just UI
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
		newNode.setUserObject(fso);
		DepositTreeModel model = (DepositTreeModel) theStructMapTree.getModel();
		// The specified index is now passed to insertNodeInto, to put the newNode into the correct position
		//model.insertNodeInto(newNode, rootNode, rootNode.getChildCount());
		model.insertNodeInto(newNode, rootNode, nodeIndex);
		TreePath newPath = new TreePath(newNode.getPath());
		theStructMapTree.scrollPathToVisible(newPath);
		theStructMapTree.setSelectionPath(newPath);
		if (editEntry) {
			editStructMap();
		}
	}

	public void addStructMap(StructMapCollection structure) {
		if (theStructMapTree == null) {
			return;
		}
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootNode.setUserObject("Structure Map");
		DepositTreeModel model;
		if (theStructMapTree.getModel() instanceof DepositTreeModel) {
			model = (DepositTreeModel) theStructMapTree.getModel();
			model.setRoot(rootNode);
		} else {
			model = new DepositTreeModel(rootNode, ETreeType.StructMapTree);
			theStructMapTree.setModel(model);
		}

		for (int i = 0; structure != null && i < structure.size(); i++) {
			addStructMapItem(structure.get(i), rootNode, false);
		}
		// If Struct Map is not being built through the Swing Worker Thread.
		if(buildIE_Worker == null){
			expandNode(theStructMapTree, rootNode, true);
		}
	}

	public void deleteEntityNode(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node
				.getParent();
		DepositTreeModel model = (DepositTreeModel) theEntityTree.getModel();
		if (parentNode != null) {
			if (parentNode.getUserObject() instanceof FileSystemObject) {
				FileSystemObject parent = (FileSystemObject) parentNode
						.getUserObject();
				if (node.getUserObject() instanceof FileSystemObject) { // Can't
					// be
					// anything
					// else
					parent.getChildren().remove(
							(FileSystemObject) node.getUserObject());
				}
			} else if (parentNode.getUserObject() instanceof FileGroup) {
				FileGroup parent = (FileGroup) parentNode.getUserObject();
				if (node.getUserObject() instanceof FileSystemObject) { // Can't
					// be
					// anything
					// else
					parent.getChildren().remove(
							(FileSystemObject) node.getUserObject());
				}
			} else if (parentNode.getUserObject() instanceof FileGroupCollection) {
				FileGroupCollection parent = (FileGroupCollection) parentNode
						.getUserObject();
				if (node.getUserObject() instanceof FileGroup) { // Can't be
					// anything
					// else
					parent.getFileGroupList().remove(
							(FileGroup) node.getUserObject());
				}
			}
			model.removeNodeFromParent(node);
		}
	}

	public void deleteStructMapNode(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node
				.getParent();
		if (parentNode != null) {
			if (parentNode.getUserObject() instanceof StructMap) { // Can't be
				// anything
				// else
				StructMap parent = (StructMap) parentNode.getUserObject();
				if (node.getUserObject() instanceof FileSystemObject) {
					parent.getFiles().remove(
							(FileSystemObject) node.getUserObject());
				} else if (node.getUserObject() instanceof StructMap) {
					parent.getChildren().remove(
							(StructMap) node.getUserObject());
				}
			}
		}
		DepositTreeModel model = (DepositTreeModel) theStructMapTree.getModel();
		model.removeNodeFromParent(node);
	}

	public StructMapCollection getStructures() {
		StructMapCollection retVal = new StructMapCollection();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theStructMapTree
				.getModel().getRoot();
		for (int i = 0; rootNode != null && i < rootNode.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode
					.getChildAt(i);
			if (node.getUserObject() instanceof StructMap) {
				StructMap entity = (StructMap) node.getUserObject();
				retVal.add(entity);
			}
		}
		return retVal;
	}

	public void expandFileSystemTree(TreePath currentPath) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentPath
				.getLastPathComponent();
		FileSystemObject fso = (FileSystemObject) node.getUserObject();
		if ((fso.getChildren() != null) && (fso.getChildren().size() == 1)
				&& (fso.getChildren().get(0).getIsPlaceholder())) {
//			LOG.debug("Before load children");
			fso.loadChildren(false);
//			LOG.debug("After load children");
			DepositTreeModel model = (DepositTreeModel) theFileSystemTree
					.getModel();
//			LOG.debug("Before remove children");
			node.removeAllChildren();
//			LOG.debug("Before reload node");
			model.reload(node);
//			LOG.debug("Before add children");
			addChildFiles(node, fso.getChildren(), model, null, null);
//			LOG.debug("Before expand path");
			theFileSystemTree.expandPath(currentPath);
		}

	}

	private boolean fileShouldBeAdded(FileSystemObject fso) {
		boolean shouldAdd = true;
		if (fso.getIsFile()) {
			PersonalSettings personalSettings = applicationProperties
					.getApplicationData().getPersonalSettings();
			shouldAdd = !personalSettings.ignoreFile(fso.getFileName());
		}
		return shouldAdd;
	}

	private boolean parentIsRepTypeDirectory(
			FSOCollection representationTypeDirectories, FileSystemObject fso) {
		boolean retVal = false;
		for (FileSystemObject fsoParent : representationTypeDirectories) {
			for (FileSystemObject fsoChild : fsoParent.getAllChildren(true)) {
				if (fsoChild == fso) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}
	
	

	private void itmRefreshProducerListActionPerformed(
			java.awt.event.ActionEvent evt) {
		loadProducersList();
	}

	private void itmSetRootActionPerformed(java.awt.event.ActionEvent evt) {
		theFsoRoot = fsoRootTemp;
		theFsoRootFile = fsoRootFileTemp;
		setRoot();
	}

	private void itmSetMultipleRootActionPerformed(
			java.awt.event.ActionEvent evt) {
		theFsoRoot = fsoRootTemp;
		setMultipleRoot_startThread();
	}
	
	private void itmSetMultipleRootFolderActionPerformed(
			java.awt.event.ActionEvent evt) {
		theFsoRoot = fsoRootTemp;
		setMultipleRootFromFolders_startThread();
	}

	public String getRootFileName() {
		String retVal;
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) this.theFileSystemTree
				.getModel().getRoot();
		if (rootNode.getUserObject() instanceof FileSystemObject) {
			retVal = rootNode.getUserObject().toString();
		} else if (rootNode.getUserObject() instanceof String) {
			retVal = rootNode.getUserObject().toString();
		} else {
			retVal = null;
		}
		return retVal;
	}

	public String getRootEntityName() {
		String retVal;
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theEntityTree
				.getModel().getRoot();
		if (rootNode.getUserObject() instanceof FileGroup) {
			retVal = rootNode.getUserObject().toString();
		} else if (rootNode.getUserObject() instanceof FileGroupCollection) {
			retVal = rootNode.getUserObject().toString();
		} else if (rootNode.getUserObject() instanceof String) {
			retVal = rootNode.getUserObject().toString();
		} else {
			retVal = "Intellectual Entity";
		}
		return retVal;
	}

	private String getEntityName() {
		String entityName = "Unknown entity type";
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theEntityTree
				.getModel().getRoot();
		if (rootNode.getUserObject() instanceof FileGroupCollection) {
			FileGroupCollection collection = (FileGroupCollection) rootNode
					.getUserObject();
			entityName = collection.getEntityName();
		} else if (rootNode.getUserObject() instanceof String) {
			entityName = (String) rootNode.getUserObject();
		}
		return entityName;
	}

	public ArrayList<FileGroupCollection> getEntities() {
		ArrayList<FileGroupCollection> retVal = new ArrayList<FileGroupCollection>();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theEntityTree
				.getModel().getRoot();
		if (rootNode.getUserObject() instanceof FileGroupCollection) {
			FileGroupCollection collection = (FileGroupCollection) rootNode
					.getUserObject();
			retVal.add(collection);
		} else {
			for (int i = 0; rootNode != null && i < rootNode.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode
						.getChildAt(i);
				if (node.getUserObject() instanceof FileGroupCollection) {
					FileGroupCollection collection = (FileGroupCollection) node
							.getUserObject();
					retVal.add(collection);
				}
			}
		}
		return retVal;
	}

	private StructMap getMap(StructMapCollection structure, String newObject) {
		StructMap retVal = null;
		for (int i = 0; structure != null && i < structure.size(); i++) {
			StructMap map = structure.get(i);
			if (map.getStructureName().equalsIgnoreCase(newObject)) {
				retVal = map;
				break;
			}
			retVal = getMap(map.getChildren(), newObject);
		}
		return retVal;
	}

	private StructMapCollection getStructMap(
			StructMapCollection currentStructure, FSOCollection files) {
		StructMapCollection retVal = currentStructure;
		if (retVal == null) {
			retVal = new StructMapCollection();
		}
		FileType fileType = null;
		for (int i = 0; files != null && i < files.size(); i++) {
			FileSystemObject file = files.get(i);
			int structSortOrder = 0;
			try {
				fileType = file.getFileType();
				structSortOrder = fileType.getSortOrder();
			} catch (Exception ex) {
			}
			if (!file.getIsFile()) { // Process structure first
				StructMap map = getMap(currentStructure, file.getDescription());
				if (map == null) {
					map = StructMap.create(file.getDescription(), null, null,
							structSortOrder);
					retVal.add(map);
				}
				map.setChildren(getStructMap(map.getChildren(), file
						.getChildren()));
			} else {
				try {
					String structName;
					if (fileType != null) {
						structName = String.format("%s %s", fileType.getDescription(), file.getSequenceNumberFromFilename(fileType.getFilePrefix()));
						StructMap map = getMap(currentStructure, structName);
						if (map == null) {
							map = StructMap.create(structName, null, null, fileType.getSortOrder());
							FSOCollection mapFiles = new FSOCollection();
							map.setFiles(mapFiles);
							retVal.add(map);
						}
						// If the structure map contains extra layers then recurse through and add to struct map
						if(fileType.chkExtraLayers()){
							HashMap<String, String> extraLayers = fileType.getExtraLayers();
							String description = extraLayers.get("DescriptionL2");
							String filePrefix = extraLayers.get("FilePrefixL2");
							if((description != null && !description.isEmpty()) && (filePrefix != null && !filePrefix.isEmpty())){
								addStructMapExtraLayers(map.getChildren(), file, fileType, description, filePrefix, 3);	
							}
						}
						else{
							map.addFile(file, true);
							map.getFiles().resetStructOrder();
						}
					} else {
						structName = file.getFileName();
					}
				} catch (Exception ex) {
					manualDepositFrame.showError("Error loading structure map",
							ERROR_OCCURRED, ex);
				}
			}
		}
		retVal.resortByName();
		return retVal;
	}
	
	private void addStructMapExtraLayers(StructMapCollection currentStructure, FileSystemObject file, FileType fileType, String description, String filePrefix, int nextLayer){
				
				String structName = String.format("%s %s", description, file.getSequenceNumberFromFilename(filePrefix));
				StructMap map = getMap(currentStructure, structName);
				if (map == null) {
					map = StructMap.create(structName, null, null, fileType.getSortOrder());
					FSOCollection mapFiles = new FSOCollection();
					map.setFiles(mapFiles);
					currentStructure.add(map);
				}
					
				HashMap<String, String> extraLayers = fileType.getExtraLayers();
				description = extraLayers.get("DescriptionL" + nextLayer);
				filePrefix = extraLayers.get("FilePrefixL" + nextLayer);
				if((description != null && !description.isEmpty()) && (filePrefix != null && !filePrefix.isEmpty())){
					// If there is a next layer
					addStructMapExtraLayers(map.getChildren(), file, fileType, description, filePrefix, nextLayer++);
				}
				else{
					// If not then add files to current map
					map.addFile(file, true);
					map.getFiles().resetStructOrder();
				}

	}

	private void addStructMapFromFileStructure(List<FileGroupCollection> groups) {
		if (groups.size() > 1) {
			return; // Can't have a structure map for a multiple entity
		}
		StructMapCollection structure = new StructMapCollection();
		List<FileGroup> entities = groups.get(0).getFileGroupList();
		for (FileGroup entity : entities) {
			FSOCollection collection = FSOCollection.create();
			collection.addAll(entity.getChildren());
			collection.setSortBy(SortBy.FileType);
			collection.reSortList();
			structure = getStructMap(structure, collection); // Relies on
			// function
			// adding to
			// structure
			// rather than
			// replacing it.
		}
		addStructMap(structure);
	}

	private List<FileGroupCollection> addEntitiesFromFileStructure() {
		return addEntitiesFromFileStructure(null);
	}

	private List<FileGroupCollection> addEntitiesFromFileStructure(String mainEntityName) {
		List<FileGroupCollection> groups = new ArrayList<FileGroupCollection>();
		FileGroupCollection group;
		if (mainEntityName == null) {
			group = new FileGroupCollection(FileUtils
					.specialCharToUnderscore(theFsoRoot.getDescription()),
					theFsoRoot.getFullPath());
		} else {
			group = new FileGroupCollection(mainEntityName, theFsoRoot
					.getFullPath());
		}
		groups.add(group);
		final int maxTypes = RepresentationTypes.values().length;
		int[] typeCounts = new int[maxTypes];
		boolean standardDirectoriesFound = false;
		for (int j = 0; j < maxTypes; j++) {
			RepresentationTypes theType = RepresentationTypes.values()[j];
			if (theFsoRoot.getEntityType() != null
					&& theFsoRoot.getEntityType().equals(theType)) {
				typeCounts[j]++;
				standardDirectoriesFound = true;
			}
		}
		int multipleMandatoryFound = 0;
		if (theFsoRoot.getEntityType() != null
				&& theFsoRoot.getEntityType().multipleMandatory()) {
			multipleMandatoryFound++;
		}
		for (FileSystemObject fso : theFsoRoot.getChildren()) {
			if (!fso.getIsFile()) {
				if (fso.getEntityType() != null
						&& fso.getEntityType().multipleMandatory()) {
					multipleMandatoryFound++;
				}
				for (int j = 0; j < maxTypes; j++) {
					if (fso.getEntityType() != null
							&& fso.getEntityType().equals(
									RepresentationTypes.values()[j])) {
						standardDirectoriesFound = true;
						typeCounts[j]++;
						break;
					}
				}
			}
		}
		boolean addStandardEntities = false;
		if (standardDirectoriesFound) {
			addStandardEntities = true;
			for (int j = 0; j < maxTypes; j++) {
				RepresentationTypes theType = RepresentationTypes.values()[j];
				if ((theType.mandatory() && typeCounts[j] == 0)
						|| (!theType.allowMultiples() && typeCounts[j] > 1)
						|| (theType.multipleMandatory() && multipleMandatoryFound == 0)) {
					addStandardEntities = false;
					break;
				}
			}
		}
		updateWorkerProgress(30);
		if (addStandardEntities) {
			for (int i = 0; i < maxTypes; i++) {
				if (typeCounts[i] > 0) {
					RepresentationTypes currentType = RepresentationTypes
							.values()[i];
					int noOfTypes = 0;
					if ((theFsoRoot.getEntityType() != null)
							&& (theFsoRoot.getEntityType().equals(currentType))) {
						noOfTypes++;
						addStandardEntityData(group, theFsoRoot, false,
								currentType, typeCounts[i], noOfTypes);
					}
					for (int j = 0; theFsoRoot.getChildren() != null
							&& j < theFsoRoot.getChildren().size(); j++) {
						FileSystemObject fso = theFsoRoot.getChildren().get(j);
						if (fso.getIsDirectory()
								&& (fso.getEntityType() != null)
								&& (fso.getEntityType().equals(currentType))) {
							noOfTypes++;
							addStandardEntityData(group, fso, true,
									currentType, typeCounts[i], noOfTypes);
						}
					}
				}
			}
			for (FileGroupCollection groupCollection : groups) {
				for (int i = groupCollection.size() - 1; i >= 0; i--) {
					FileGroup groupCheck = groupCollection.get(i);
					if (groupCheck.getChildren().size() == 0) {
						groupCollection.remove(groupCheck);
					}
				}
			}
			addIntellectualEntities(groups);
		} else {
			addIntellectualEntities(groups);
		}
		return groups;

	}

	private void addStandardEntityData(FileGroupCollection group,
			FileSystemObject fso, boolean addChildDirectories,
			RepresentationTypes currentType, int noOfCurrentTypeEntities,
			int currentTypeNo) {
		String entityName;
		if (noOfCurrentTypeEntities > 1) {
			entityName = String.format("%s (%d)", currentType.description(),
					currentTypeNo);
		} else {
			entityName = currentType.description();
		}
		FileGroup entity = FileGroup.create(entityName, BLANK_ID, currentType,
				getTranslatedChildren(fso.getChildren(addChildDirectories)));
		try {
			group.add(entity);
		} catch (Exception ex) {
			manualDepositFrame.showError("Couldn't add file group",
					"An error has occurred in " + group.getEntityName(), ex);
			reportException(ex);
		}
	}

	public boolean canOpenFiles(TreePath[] paths) {
		boolean retVal = true;
		if (paths == null) {
			retVal = false;
		} else {
			for (TreePath path : paths) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				if (node.getUserObject() instanceof FileSystemObject) {
					FileSystemObject fso = (FileSystemObject) node
							.getUserObject();
					if (!fso.getIsFile()) {
						retVal = false;
						break;
					}
				} else {
					retVal = false;
					break;
				}
			}
		}
		return retVal;
	}

	public void setupFavourites() {
		theFavouritesMenu.removeAll();
		PersonalSettings personalSettings = applicationProperties
				.getApplicationData().getPersonalSettings();
		if (personalSettings.getNoOfFavourites() > 0) {
			for (String favourite : personalSettings.getFavourites()) {
				addFavouriteMenuItem(favourite);
			}
			theFavouritesMenu.addSeparator();
			addFavouriteMenuItem("Clear Favourites");
		} else {
			JMenuItem mnuDirectory = new JMenuItem();
			mnuDirectory.setText("No favourites found");
			theFavouritesMenu.add(mnuDirectory);
		}
	}

	private void addFavouriteMenuItem(String directoryName) {
		JMenuItem mnuDirectory = new JMenuItem();
		mnuDirectory.setText(directoryName);
		mnuDirectory.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mnuLoadFavouriteActionPerformed(evt);
			}
		});
		theFavouritesMenu.add(mnuDirectory);
	}

	public void clearFavourites() {
		if (manualDepositFrame.confirm("Delete all favourites?")) {
			applicationProperties.getApplicationData().getPersonalSettings()
					.clearFavourites();
			setupFavourites();
		}
	}

	private void mnuLoadFavouriteActionPerformed(java.awt.event.ActionEvent evt) {
		String directory = evt.getActionCommand();
		if (directory.equals("Clear Favourites")) {
			clearFavourites();
		} else {
			File testFile = new File(directory);
			if (testFile.exists()) {
				loadPath(directory);
			} else {
				manualDepositFrame.showError("Invalid Directory",
						"Favourite directory does not exist");
			}
		}
	}

	public void storeAsFavourite(DefaultMutableTreeNode node) {
		PersonalSettings personalSettings = applicationProperties
				.getApplicationData().getPersonalSettings();
		// This will fail if you give it a non-FSO node
		FileSystemObject fso = (FileSystemObject) node.getUserObject();
		if (!fso.getIsFile()) {
			String dir = fso.getFullPath();
			personalSettings.addFavourite(dir);
			setupFavourites();
			manualDepositFrame.showMessage("Favourite Stored", dir
					+ " has been stored as a favourite directory");
		}
	}

	public boolean canStoreFavourites(TreePath[] paths) {
		boolean retVal = false;
		if (paths != null) {
			retVal = true;
		}
		return retVal;
	}

	public boolean showProducers() {
		boolean retVal = false;
		if (applicationProperties != null) {
			retVal = userGroupData.isIncludeProducerList();
		}
		LOG.debug("showProducers? " + retVal);
		return retVal;
	}

	private void loadProducersList() {
		try {
			LOG.debug("loadProducersList, start");
			DefaultListModel model;
			if (theProducerList.getModel() instanceof DefaultListModel) {
				model = (DefaultListModel) theProducerList.getModel();
				model.clear();
			} else {
				model = new DefaultListModel();
			}
			LOG.debug("loadProducersList, Before create deposit");
			IDeposit deposit = applicationProperties.getApplicationData()
					.getDeposit();
			producers = deposit.getProducers(applicationProperties
					.getLoggedOnUser());
			LOG.debug("loadProducersList, After get list");
			for (Producer data : producers) {
				model.addElement(data);
			}
			theProducerList.setModel(model);
			theProducerList
					.addListSelectionListener(new ListSelectionListener() {
						public void valueChanged(
								javax.swing.event.ListSelectionEvent evt) {
							lstProducersValueChanged(evt);
						}
					});
			theProducerList.repaint();
			LOG.debug("loadProducersList, end");
		} catch (Exception ex) {
			manualDepositFrame.showError("Error loading producers",
					"Unable to load producer list", ex);
			reportException(ex);
		}
	}

	private void lstProducersValueChanged(
			javax.swing.event.ListSelectionEvent evt) {
		if (theProducerList.getSelectedValue() != null) {
			Producer currentProducer = (Producer) theProducerList
					.getSelectedValue();
			try {
				loadMaterialFlowList(currentProducer.getID());
			} catch (Exception ex) {
				manualDepositFrame.showError(
						"Couldn't load material flow list", ERROR_OCCURRED, ex);
				reportException(ex);
			}
		}
	}

	public void loadMaterialFlowList(String producerID) throws Exception {
		LOG.debug("loadMaterialFlowList, start");
		DefaultListModel model;
		if (theMaterialFlowList.getModel() instanceof DefaultListModel) {
			model = (DefaultListModel) theMaterialFlowList.getModel();
			model.clear();
		} else {
			model = new DefaultListModel();
		}
		LOG.debug("loadMaterialFlowList, Before create deposit");
		IDeposit deposit = applicationProperties.getApplicationData()
				.getDeposit();
		LOG
				.debug("loadMaterialFlowList, After create deposit, before get list");
		ArrayList<MaterialFlow> flows = deposit.getMaterialFlows(producerID);
		LOG.debug("loadProducersList, After get list");
		for (MaterialFlow data : flows) {
			model.addElement(data);
		}
		theMaterialFlowList.setModel(model);
		LOG.debug("loadMaterialFlowList, start");
	}

	public void filterProducerList(String theFilter) {
		if (producers == null || producers.isEmpty())
			return;
		DefaultListModel model = (DefaultListModel) theProducerList.getModel();
//		Producer selectedProducer = null;
//		if (theProducerList.getSelectedValue() != null) {
//			selectedProducer = (Producer) theProducerList.getSelectedValue();
//		}
		model.clear();
		for (Producer data : producers) {
			if (data.getDescription().toLowerCase().contains(
					theFilter.toLowerCase())) {
				model.addElement(data);
			}
		}
//		if (selectedProducer != null) {
//			theProducerList.setSelectedValue(selectedProducer, true);
//		}
//		
//		theProducerList.setSelectedIndex(-1);
	}

	private boolean fileIsInEntityArray(FileSystemObject fso,
			FSOCollection fsoList) {
		boolean fileFound = false;
		for (FileSystemObject fsoTest : fsoList) {
			if (fso.getFullPath().equals(fsoTest.getFullPath())) {
				fileFound = true;
				break;
			} else {
				if (fsoTest.getChildren() != null) {
					if (fileIsInEntityArray(fso, fsoTest.getChildren())) {
						fileFound = true;
						break;
					}
				}
			}
		}
		return fileFound;
	}

	public boolean fileIsInEntity(FileSystemObject fso) {
		boolean fileFound = false;
		if (entityRootSet) {
			List<FileGroupCollection> entities = getEntities();
			for (int i = 0; !fileFound && i < entities.size(); i++) {
				List<FileGroup> innerEntities = entities.get(i)
						.getFileGroupList();
				for (int j = 0; !fileFound && j < innerEntities.size(); j++) {
					FileGroup entity = innerEntities.get(j);
					if (entity.getChildren() != null) {
						fileFound = fileIsInEntityArray(fso, entity
								.getChildren());
					}
				}
			}
		}
		return fileFound;
	}

	public boolean fileIsInStructMap(FileSystemObject fso) {

		StructMapCollection structure = getStructures();
		return fileIsInStructMap(structure, fso);
	}

	private boolean fileIsInStructMap(StructMapCollection structure,
			FileSystemObject fso) {
		boolean fileFound = false;
		for (int i = 0; structure != null && !fileFound && i < structure.size(); i++) {
			StructMap struct = structure.get(i);
			if (struct.getFiles() != null) {
				for (int j = 0; !fileFound && j < struct.getFiles().size(); j++) {
					FileSystemObject fsoTest = struct.getFiles().get(j);
					if (fso.getFullPath().equals(fsoTest.getFullPath())) {
						fileFound = true;
						break;
					}
				}
			}
			if (!fileFound) {
				fileFound = fileIsInStructMap(struct.getChildren(), fso);
			}
		}
		return fileFound;
	}

	/**
	 * Creates a new file object from the original In the process it translates
	 * file / directory names removing prefixes.
	 */
	private FileSystemObject getTranslatedFile(FileSystemObject fso) {
		FSOCollection files = new FSOCollection();
		if (fileShouldBeAdded(fso)) {
			files.add(fso);
		}
		files = getTranslatedChildren(files);
		files.get(0).setSortOrder(0);
		return files.get(0);
	}

	/**
	 * Creates a new array of file objects from the original In the process it
	 * translates file / directory names removing prefixes.
	 */
	private FSOCollection getTranslatedChildren(FSOCollection children) {
		FSOCollection retVal = new FSOCollection();
		for (int i = 0; children != null && i < children.size(); i++) {
			FileSystemObject oldObject = children.get(i);
			FileSystemObject newObject = new FileSystemObject(oldObject
					.getFileName(), oldObject.getFile(),
					getTranslatedChildren(oldObject.getChildren()));
			newObject.setOriginalChecksum(oldObject.getOriginalChecksum());
			newObject.setChecksumType(oldObject.getChecksumType());
			newObject.setIsDuplicate(oldObject.getIsDuplicate());
			if (fileShouldBeAdded(newObject)) {
				retVal.add(newObject);
			}
		}
		return retVal;
	}

	private JPopupMenu getEntityMenu(FileSystemObject fso) {
		final JPopupMenu menu = new JPopupMenu();
		if (fso.getIsFile()) {
			JMenuItem item;
			ArrayList<FileGroupCollection> entities = getEntities();
			if (entities.size() == 1) {
				item = new JMenuItem("Delete File");
				item.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						itmDeleteEntityItem(evt);
					}
				});
				menu.add(item);
			}
			item = new JMenuItem("Rename File");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					itmRenameEntity(evt);
				}
			});
			menu.add(item);
			if (menu.getComponentCount() > 0) {
				menu.addSeparator();
			}
			item = new JMenuItem("Open File");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					itmOpenFile(evt);
				}
			});
			menu.add(item);
		} else {
			JMenuItem item = new JMenuItem("Delete Directory (and children)");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					itmDeleteEntityItem(evt);
				}
			});
			menu.add(item);
			item = new JMenuItem("Rename Directory");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					itmRenameEntity(evt);
				}
			});
			menu.add(item);
		}
		return menu;
	}

	private void putIERootInClipboard() {
		for (JTree tree : trees) {
			DepositTreeModel model = (DepositTreeModel) tree.getModel();
			if (model.getTreeType() == ETreeType.EntityTree) {
				theNodeInClipboard.clear();
				theNodeInClipboard
						.add((DefaultMutableTreeNode) model.getRoot());
				break;
			}
		}
	}

	private DefaultMutableTreeNode addEntity(RepresentationTypes entityType) {
		DefaultMutableTreeNode retVal = null;
		DefaultMutableTreeNode node = theNodeInClipboard.get(0);
		if (node.getUserObject() instanceof FileGroupCollection) {
			int noOfEntities = noOfEntityTypesAdded(entityType, getEntities());
			noOfEntities++;
			FileGroupCollection collection = (FileGroupCollection) node
					.getUserObject();
			FileGroup newEntity = FileGroup.create(this.getEntityName(
					entityType, noOfEntities), BLANK_ID, entityType, null);
			try {
				collection.add(newEntity);
			} catch (Exception ex) {
				manualDepositFrame
						.showError("Couldn't add file group",
								"An error has occurred in "
										+ newEntity.getEntityName(), ex);
				reportException(ex);
			}
			retVal = addIntellectualEntity(node, newEntity, false);
		}
		return retVal;
	}

	private void itmAddEntity(java.awt.event.ActionEvent evt,
			RepresentationTypes entityType) {
		addEntity(entityType);
	}

	private ArrayList<DefaultMutableTreeNode> getClipboardCopy() {
		ArrayList<DefaultMutableTreeNode> nodeInClipboard = new ArrayList<DefaultMutableTreeNode>();
		for (DefaultMutableTreeNode nodeFromClipboard : theNodeInClipboard) {
			nodeInClipboard.add(nodeFromClipboard);
		}
		return nodeInClipboard;
	}

	private void replaceClipboardWithCopy(
			ArrayList<DefaultMutableTreeNode> clipboard) {
		theNodeInClipboard.clear();
		for (DefaultMutableTreeNode nodeFromClipboard : clipboard) {
			theNodeInClipboard.add(nodeFromClipboard);
		}
	}

	public boolean canDeleteEntityItem() {
		boolean canDelete = false;
		JTree tree = theEntityTree;
		if (tree != null) {
			if (tree.getSelectionCount() > 0) {
				TreePath[] paths = tree.getSelectionPaths();
				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					if ((node.getUserObject() instanceof FileGroup)
							|| (node.getUserObject() instanceof FileSystemObject)
							|| ((getEntities().size() > 1) && (node
									.getUserObject() instanceof FileGroupCollection))) {
						canDelete = true;
						break;
					}
				}
			}
		}
		return canDelete;
	}

	public void resequenceEntity() {
		JTree tree = theEntityTree;
		TreePath[] paths = tree.getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
					.getLastPathComponent();
			if ((node.getUserObject() instanceof FileGroup)
					|| (node.getUserObject() instanceof FileSystemObject)
					|| ((getEntities().size() > 1) && (node.getUserObject() instanceof FileGroupCollection))) {
				FileGroup group = (FileGroup) node.getUserObject();
				String sequenceNumber = manualDepositFrame.getInput(
						"Enter Sequence Number", "Enter new sequence number",
						String.format("%d", group.getRevisionNumber()));
				if (sequenceNumber != null) {
					try {
						int newSequence = Integer.parseInt(sequenceNumber);
						group.setRevisionNumber(newSequence);
					} catch (Exception ex) {
						manualDepositFrame.showError("Invalid Sequence Number",
								"Sequence number must be a valid number", ex);
					}
				}
			}
		}
	}

	public void setRepresentationCode() {
		JTree tree = theEntityTree;
		TreePath[] paths = tree.getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
					.getLastPathComponent();
			if ((node.getUserObject() instanceof FileGroup)
					|| (node.getUserObject() instanceof FileSystemObject)
					|| ((getEntities().size() > 1) && (node.getUserObject() instanceof FileGroupCollection))) {
				FileGroup group = (FileGroup) node.getUserObject();
				String repCode = manualDepositFrame.getInput(
						"Enter Representation Code",
						"Enter new representation code", group
								.getRepresentationCode());
				if (repCode != null) {
					group.setRepresentationCode(repCode);
				}
			}
		}
	}

	public void deleteEntity() {
		if (canDeleteEntityItem()) {
			JTree tree = theEntityTree;
			TreePath[] paths = tree.getSelectionPaths();
			for (int i = 0; i < paths.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
						.getLastPathComponent();
				if ((node.getUserObject() instanceof FileGroup)
						|| (node.getUserObject() instanceof FileSystemObject)
						|| ((getEntities().size() > 1) && (node.getUserObject() instanceof FileGroupCollection))) {
					deleteEntityNode(node);
				}
			}
			ArrayList<FileGroupCollection> entities = getEntities();
			renumberEntityTypes(entities);
			addFileSystemRoot(theFsoRoot, true, false);			
			deleteStructMapItemsNotInIE();
			refreshEntityTree();
		}
	}
	
	/**
	 * Refreshes Entity tree to expand any labels that may not have rendered fully. Eg. "Som..." instead of "Some Folder" 
	 */
	private void refreshEntityTree(){
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theEntityTree.getModel().getRoot();
		DefaultTreeModel model = (DefaultTreeModel) theEntityTree.getModel();
		if (rootNode.getChildCount() > 0) {
			for (int i = 0; i < rootNode.getChildCount(); i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode
						.getChildAt(i);
				model.nodeStructureChanged(child);
				model.nodeChanged(child);
			}
			expandNode(theEntityTree, rootNode, true);
		}
	}

	private void deleteMapItemIfNotInIE(DefaultMutableTreeNode node) {
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode) node
					.getChildAt(i);
			deleteMapItemIfNotInIE(nodeChild);
		}
		if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) node.getUserObject();
			if (!this.fileIsInEntity(fso)) {
				this.deleteStructMapNode(node);
			}
		}
	}

	private void deleteStructMapItemsNotInIE() {
		DepositTreeModel model = getModel(ETreeType.StructMapTree);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
		deleteMapItemIfNotInIE(node);

	}

	public boolean canDeleteStructItem() {
		boolean canMove = false;
		JTree tree = this.getTree(ETreeType.StructMapTree);
		if (tree != null) {
			if (tree.getSelectionCount() > 0) {
				TreePath[] paths = tree.getSelectionPaths();
				for (int i = 0; i < paths.length; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
							.getLastPathComponent();
					if ((node.getUserObject() instanceof StructMap)
							|| (node.getUserObject() instanceof FileSystemObject)) {
						canMove = true;
						break;
					}
				}
			}
		}
		return canMove;
	}

	public void deleteStructMapItem() {
		if (canDeleteStructItem()) {
			JTree tree = this.getTree(ETreeType.StructMapTree);
			if (tree != null) {
				if (tree.getSelectionCount() > 0) {
					TreePath[] paths = tree.getSelectionPaths();
					for (int i = 0; i < paths.length; i++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i]
								.getLastPathComponent();
						if ((node.getUserObject() instanceof StructMap)
								|| (node.getUserObject() instanceof FileSystemObject)) {
							deleteStructMapNode(node);
						}
					}
				}
			}
		}
	}

	private void itmDeleteEntityItem(java.awt.event.ActionEvent evt) {
		deleteEntity();
		manualDepositFrame.checkButtons();
	}

	private void itmRenameEntity(java.awt.event.ActionEvent evt) {
		editEntity();
	}

	public void openFile(DefaultMutableTreeNode node) {
		if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) node.getUserObject();
			
			if (fso.getIsFile()) {
				try {
					String fileName = fso.getFile().getAbsolutePath();
					fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
					Pattern pattern = Pattern.compile("\\s{2,}");
					Matcher matcher = pattern.matcher(fileName);
					if (matcher.find()){
						Desktop.getDesktop().open(fso.getFile());
					}else{
						Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL "
										+ fso.getFile().getAbsolutePath());
					}
				} catch (Exception ex) {
					String message = "Could not open file "
							+ fso.getDescription();
					manualDepositFrame.showError("Could not open file",
							message, ex);
					reportException(ex);
				}
			}
		}
	}
	
	public void openFileLocation(DefaultMutableTreeNode node) {
		if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) node.getUserObject();
			if (fso.getIsFile()) {
				try {
					String fileName = fso.getFile().getAbsolutePath();
					fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
					Pattern pattern = Pattern.compile("\\s{2,}");
					Matcher matcher = pattern.matcher(fileName);
					if (matcher.find()){
						Desktop.getDesktop().open(new File(fso.getFullPathNoFileName()));
					}else{
						Runtime.getRuntime().exec("explorer.exe /select," + fso.getFile().getAbsolutePath());
					}
				} catch (Exception ex) {
					String message = "Could not open file location " + fso.getDescription();
					manualDepositFrame.showError("Could not open file location", message, ex);
					reportException(ex);
				}
			}
		}
	}

	private void itmOpenFile(java.awt.event.ActionEvent evt) {
		ArrayList<DefaultMutableTreeNode> clipboard = getClipboardCopy();
		for (DefaultMutableTreeNode node : clipboard) {
			openFile(node);
		}
	}
	
	private void itmOpenFileLocation(java.awt.event.ActionEvent evt) {
		ArrayList<DefaultMutableTreeNode> clipboard = getClipboardCopy();
		for (DefaultMutableTreeNode node : clipboard) {
			openFileLocation(node);
		}
	}

	private void itmStoreAsFavourite(java.awt.event.ActionEvent evt) {
		ArrayList<DefaultMutableTreeNode> clipboard = getClipboardCopy();
		for (DefaultMutableTreeNode node : clipboard) {
			storeAsFavourite(node);
		}
	}

	public void refreshFileList() {
		ArrayList<DefaultMutableTreeNode> clipboard = getClipboardCopy();
		for (DefaultMutableTreeNode node : clipboard) {
			if (node.getUserObject() instanceof FileSystemObject) {
				node.removeAllChildren();
				FileSystemObject fso = (FileSystemObject) node.getUserObject();
				fso.loadChildren(false);
				DepositTreeModel model = (DepositTreeModel) theFileSystemTree
						.getModel();
				addChildFiles(node, fso.getChildren(), model, null,
						theFileSystemTree);
			}
		}
	}

	private void itmRefreshDirectory(java.awt.event.ActionEvent evt) {
		refreshFileList();
	}

	private void removeIgnoreFiles(FSOCollection children) {
		int size = children.size();
		for (int i = 0; i < size; i++) {
			FileSystemObject fsoChild = children.get(i);
			if (!fileShouldBeAdded(fsoChild)) {
				synchronized (children) {
					children.remove(fsoChild);
					i--;
					size--;
				}
			}
		}
	}

	private void handleApplicationDataException(
			InvalidApplicationDataException appEx) {
		LOG.error("Application Data error", appEx);
		manualDepositFrame.showError("Error loading application data",
				"There was an error loading the application data", appEx);
	}

	private void handleCMSException(InvalidCMSSystemException cmsEx) {
		LOG.error("CMS System error " + cmsEx.getCmsTypeRequested(), cmsEx);
		manualDepositFrame.showError("Error loading CMS data",
				"There was an error loading the CMS data "
						+ cmsEx.getCmsTypeRequested(), cmsEx);
	}

	private class LoadBulkFiles implements Runnable {

		private final BulkUploadPresenter thePresenter;
		private final ManualDepositPresenter theParent;

		public LoadBulkFiles(BulkUploadPresenter presenter,
				ManualDepositPresenter parent) {
			thePresenter = presenter;
			theParent = parent;
		}

		public void run() {
			FileSystemObject fso = FileSystemObject.create(fsoRootTemp
					.getDescription(), fsoRootTemp.getFile(), null);
			fso.loadChildren(true);
			FSOCollection children = fso.getChildren();
			removeIgnoreFiles(children);
			int size = children.size();
			int counter = 0;
			int max = size;
			thePresenter.setMaxProgress(max);
			thePresenter.setProgressVisible(true);
			// Commented out stuff is for performance improvements.
			// Leave it in for now in case we need further investigation
			// Date startDate = new Date();
			// long itemCreateTime = 0;
			// long setRootTime = 0;
			// long filterFSOTime = 0;
			// long removeDupsTime = 0;
			// long addUploadTime = 0;
			// _loadChildrenTime = 0;
			// _addEntitiesTime = 0;
			// _addStructMapTime = 0;
			// _addFileSystemRootTime = 0;

			fsoRootTemp.loadChildren(true);
			FSOCollection fsoRootTempChildren = fsoRootTemp.getChildren();
			for (int i = 0; i < size; i++) {
				if (thePresenter.bulkLoadCancelled()) {
					break;
				}
				thePresenter.setCurrentProgress(counter);
				thePresenter.setStatus(String.format(
						"Processing files (%d of %d) ...", counter, max));
				counter++;
				FileSystemObject fsoChild = children.get(i);
				MetaDataFields metaData = null;
				try {
					metaData = metaDataTableModel.getMetaData().getCopy();
				} catch (Exception ex) {
					manualDepositFrame.showError("Error loading meta data",
							"There was an error loading the meta data", ex);
					return;
				}
				if (fsoChild.getIsFile()) {
					theFsoRootFile = fsoChild;
					String[] defaultInputSplit = theFsoRootFile.getNameParts(false);
					String entityName = defaultInputSplit[0];
					String entityPrefix = entityName;
					FSOCollection theChildren = FSOCollection.create();
					for (int j = 0; j < fsoRootTempChildren.size(); j++) {
						FileSystemObject fsoTest = fsoRootTempChildren.get(j);
						if (fsoTest.getIsDirectory()) {
							if (fsoTest.getEntityType() != null) {
								FileSystemObject fsoEntity = FileSystemObject
										.create(fsoTest.getDescription(),
												fsoTest.getFile(), null);
								fsoEntity.loadChildren(true, false,
										entityPrefix, false);
								theChildren.add(fsoEntity);
							}
						} else {
							String[] testParts = fsoTest.getNameParts(false);
							if (testParts[0].equalsIgnoreCase(entityPrefix)) {
								theChildren.add(fsoTest);
								fsoRootTempChildren.remove(fsoTest);
								j--;
							}
						}
					}
					theFsoRoot = FileSystemObject.create(fsoRootTemp
							.getDescription(), fsoRootTemp.getFile(),
							theChildren);
					setRoot(false, false, true, false);
					try {
							BulkUploadItem item = BulkUploadItem.create(
									fsoRootTemp, theParent, userGroupData
											.getTranslatedName(getEntityName()),
									getEntities().get(0), getStructures(),
									metaData, userGroupData
											.getTranslatedName(getEntityName()),
									fso.getBaseDirectory(), applicationProperties, false);
							if (!canSubmit(false)) {
								item.setJobState(JobState.InvalidSIP);
							}
							if (thePresenter.bulkLoadCancelled()) {
								break;
							}
							try {
								thePresenter.addBulkUpload(item);
							} catch (BulkLoadException ex) {
								LOG.error("Error in bulk load", ex);
							}
						String filePrefix = getEntityName();
						for (int j = 0; j < size; j++) {
							FileSystemObject fsoChildTest = children.get(j);
							String[] nameParts = fsoChildTest.getNameParts(false);
							if (nameParts[0].equalsIgnoreCase(filePrefix)) {
								synchronized (children) {
									children.remove(fsoChildTest);
									i = -1;
									j--;
									size--;
								}
							}
						}
					} catch (InvalidApplicationDataException appEx) {
						handleApplicationDataException(appEx);
					} catch (InvalidCMSSystemException cmsEx) {
						handleCMSException(cmsEx);
					}
				} else {
					if (fsoChild.getEntityType() == null) {
						theFsoRootFile = null;
						theFsoRoot = fsoChild;
						setRoot(false, false, false, false);
						String[] nameParts = fsoChild.getNameParts(false);
						try {
							BulkUploadItem item = BulkUploadItem.create(
									theFsoRoot, theParent, nameParts[0],
									getEntities().get(0), getStructures(),
									metaData, getEntityName(), fso
											.getBaseDirectory(),
									applicationProperties, false);
							if (!canSubmit(false)) {
								item.setJobState(JobState.InvalidSIP);
							}
							if (thePresenter.bulkLoadCancelled()) {
								break;
							}
							try {
								thePresenter.addBulkUpload(item);
							} catch (BulkLoadException ex) {
								LOG.error("Error in bulk load", ex);
							}
							children.remove(fsoChild);
							i = -1;
							size--;
						} catch (InvalidApplicationDataException appEx) {
							handleApplicationDataException(appEx);
						} catch (InvalidCMSSystemException cmsEx) {
							handleCMSException(cmsEx);
						}
					}
				}
			}
			thePresenter.setNoOfFiles();
			thePresenter.setProgressVisible(false);
			thePresenter.setStatus("");
			thePresenter.setLoadingFiles(false);
		}
	}
	
	private class LoadBulkFilesAsIEs implements Runnable {

		private final BulkUploadPresenter thePresenter;
		private final ManualDepositPresenter theParent;

		public LoadBulkFilesAsIEs(BulkUploadPresenter presenter,
				ManualDepositPresenter parent) {
			thePresenter = presenter;
			theParent = parent;
		}

		public void run() {
			FileSystemObject fso = FileSystemObject.create(fsoRootTemp
					.getDescription(), fsoRootTemp.getFile(), null);
			fso.loadChildren(true);
			FSOCollection children = fso.getChildren();
			removeIgnoreFiles(children);
			int size = children.size();
			int counter = 0;
			int max = size;
			thePresenter.setMaxProgress(max);
			thePresenter.setProgressVisible(true);
			// Commented out stuff is for performance improvements.
			// Leave it in for now in case we need further investigation
			// Date startDate = new Date();
			// long itemCreateTime = 0;
			// long setRootTime = 0;
			// long filterFSOTime = 0;
			// long removeDupsTime = 0;
			// long addUploadTime = 0;
			// _loadChildrenTime = 0;
			// _addEntitiesTime = 0;
			// _addStructMapTime = 0;
			// _addFileSystemRootTime = 0;

			fsoRootTemp.loadChildren(true);
			FSOCollection fsoRootTempChildren = fsoRootTemp.getChildren();
			for (int i = 0; i < size; i++) {
				if (thePresenter.bulkLoadCancelled()) {
					break;
				}
				thePresenter.setCurrentProgress(counter);
				thePresenter.setStatus(String.format(
						"Processing files (%d of %d) ...", counter, max));
				counter++;
				FileSystemObject fsoChild = children.get(i);
				MetaDataFields metaData = null;
				try {
					metaData = metaDataTableModel.getMetaData().getCopy();
				} catch (Exception ex) {
					manualDepositFrame.showError("Error loading meta data",
							"There was an error loading the meta data", ex);
					return;
				}
				if (fsoChild.getIsFile()) {
					theFsoRootFile = fsoChild;
					String[] defaultInputSplit = theFsoRootFile.getNameParts(false);
					String entityPrefix = defaultInputSplit[0];
					String entityName = theFsoRootFile.getFileNameWithoutRepTypeOrSuffix();
					FSOCollection theChildren = FSOCollection.create();
					for (int j = 0; j < fsoRootTempChildren.size(); j++) {
						FileSystemObject fsoTest = fsoRootTempChildren.get(j);
						if (fsoTest.getIsDirectory()) {
							if (fsoTest.getEntityType() != null) {
								FileSystemObject fsoEntity = FileSystemObject
										.create(fsoTest.getDescription(),
												fsoTest.getFile(), null);
								fsoEntity.loadChildren(true, false,
										entityName, true);
								theChildren.add(fsoEntity);
							}
						} else {
							if ((fsoTest.getFileNameWithoutRepTypeOrSuffix()).equalsIgnoreCase(entityName)) {
								theChildren.add(fsoTest);
								fsoRootTempChildren.remove(fsoTest);
								j--;
							}
						}
					}
					theFsoRoot = FileSystemObject.create(fsoRootTemp
							.getDescription(), fsoRootTemp.getFile(),
							theChildren);
					setRoot(false, false, true, true);
					try {
						
							BulkUploadItem item = BulkUploadItem.create(fsoRootTemp, theParent, userGroupData.getTranslatedName(entityPrefix),
									getEntities().get(0), getStructures(), metaData, userGroupData.getTranslatedName(getEntityName()),
									fso.getBaseDirectory(), applicationProperties, true);
							if (!canSubmit(false)) {
								item.setJobState(JobState.InvalidSIP);
							}
							if (thePresenter.bulkLoadCancelled()) {
								break;
							}
							try {
								thePresenter.addBulkUpload(item);
							} catch (BulkLoadException ex) {
								LOG.error("Error in bulk load", ex);
							}
						for (int j = 0; j < size; j++) {
							FileSystemObject fsoChildTest = children.get(j);
							if ((fsoChildTest.getFileNameWithoutRepTypeOrSuffix()).equalsIgnoreCase(entityName)) {
								synchronized (children) {
									children.remove(fsoChildTest);
									i = -1;
									j--;
									size--;
								}
							}
						}
					} catch (InvalidApplicationDataException appEx) {
						handleApplicationDataException(appEx);
					} catch (InvalidCMSSystemException cmsEx) {
						handleCMSException(cmsEx);
					} 
				} else {
					if (fsoChild.getEntityType() == null) {
						theFsoRootFile = null;
						theFsoRoot = fsoChild;
						setRoot(false, false, false, true);
						String[] nameParts = fsoChild.getNameParts(false);
						try {
							BulkUploadItem item = BulkUploadItem.create(
									theFsoRoot, theParent, nameParts[0],
									getEntities().get(0), getStructures(),
									metaData, getEntityName(), fso
											.getBaseDirectory(),
									applicationProperties, true);
							if (!canSubmit(false)) {
								item.setJobState(JobState.InvalidSIP);
							}
							if (thePresenter.bulkLoadCancelled()) {
								break;
							}
							try {
								thePresenter.addBulkUpload(item);
							} catch (BulkLoadException ex) {
								LOG.error("Error in bulk load", ex);
							}
							children.remove(fsoChild);
							i = -1;
							size--;
						} catch (InvalidApplicationDataException appEx) {
							handleApplicationDataException(appEx);
						} catch (InvalidCMSSystemException cmsEx) {
							handleCMSException(cmsEx);
						}
					}
				}
			}
			thePresenter.setNoOfFiles();
			thePresenter.setProgressVisible(false);
			thePresenter.setStatus("");
			thePresenter.setLoadingFiles(false);
		}
	}

	private void bulkLoadDirectory(java.awt.event.ActionEvent evt) {
		setProgressBarVisible(false);
		IBulkUpload bulkForm = manualDepositFrame.createBulkUploadForm();
		BulkUploadPresenter presenter = BulkUploadPresenter.create(bulkForm,
				this, applicationProperties);
		LoadBulkFiles bulkLoader = new LoadBulkFiles(presenter, this);
		presenter.setStatus("Processing files ...");
		presenter.setLoadingFiles(true);
		new Thread(bulkLoader).start();
		presenter.showBulkUploads();
		resetScreen();
	}
	
	private void bulkLoadDirectoryAsIEs(java.awt.event.ActionEvent evt) {
		setProgressBarVisible(false);
		IBulkUpload bulkForm = manualDepositFrame.createBulkUploadForm();
		BulkUploadPresenter presenter = BulkUploadPresenter.create(bulkForm,
				this, applicationProperties);
		LoadBulkFilesAsIEs bulkLoader = new LoadBulkFilesAsIEs(presenter, this);
		presenter.setStatus("Processing files ...");
		presenter.setLoadingFiles(true);
		new Thread(bulkLoader).start();
		presenter.showBulkUploads();
		resetScreen();
	}

	public void setProgressBarVisible(boolean b) {
		manualDepositFrame.setProgressBarVisible(b);
	}

	public AppProperties getAppProperties() {
		return applicationProperties;
	}

	public JPopupMenu getEntityMenu(DefaultMutableTreeNode node) {
		theNodeInClipboard.clear();
		theNodeInClipboard.add(node);
		JPopupMenu menu = null;
		if (node.getUserObject() instanceof FileSystemObject) {
			menu = getEntityMenu((FileSystemObject) node.getUserObject());
		} else if (node.getUserObject() instanceof FileGroupCollection) {
			menu = getEntityMenu((FileGroupCollection) node.getUserObject());
		} else if (node.getUserObject() instanceof FileGroup) {
			menu = getEntityMenu((FileGroup) node.getUserObject());
		}
		return menu;
	}

	private String getEntityName(RepresentationTypes entityType, int entityNo) {
		String retVal = "";
		if (entityNo == 1) {
			retVal = entityType.toString();
		} else {
			retVal = entityType.toString() + " (" + entityNo + ")";
		}
		return retVal;
	}

	private void renumberEntityTypes(
			ArrayList<FileGroupCollection> entitiesSuper) {
		HashMap<RepresentationTypes, Integer> typeCount = new HashMap<RepresentationTypes, Integer>();
		for (FileGroupCollection entities : entitiesSuper) {
			for (FileGroup entityTest : entities) {
				int entityNo = 1;
				if (typeCount.containsKey(entityTest.getEntityType())) {
					entityNo = typeCount.get(entityTest.getEntityType());
					entityNo++;
					typeCount.remove(entityTest.getEntityType());
				}
				typeCount.put(entityTest.getEntityType(), entityNo);
				entityTest.setEntityName(getEntityName(entityTest
						.getEntityType(), entityNo));
			}
		}
	}

	private int noOfEntityTypesAdded(RepresentationTypes entityType,
			ArrayList<FileGroupCollection> entitiesSuper) {
		int retVal = 0;
		for (FileGroupCollection entities : entitiesSuper) {
			for (FileGroup entityTest : entities) {
				if (entityTest.getEntityType().equals(entityType)) {
					retVal++;
				}
			}
		}
		return retVal;
	}

	private boolean entityTypeAdded(RepresentationTypes entityType,
			ArrayList<FileGroupCollection> entitiesSuper) {
		boolean retVal = false;
		for (FileGroupCollection entities : entitiesSuper) {
			for (FileGroup entityTest : entities) {
				if (entityTest.getEntityType().equals(entityType)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	public boolean getEntityRootSet() {
		return entityRootSet;
	}

	public boolean getIncludeMultiEntityMenuItem() {
		return userGroupData.isIncludeMultiEntityMenuItem();
	}

	private JPopupMenu getEntityMenu(FileGroupCollection entitiesClicked) {
		if (!entityRootSet) {
			return null; // Don't want to have a menu unless we are editing an
			// entity
		}
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Rename Intellectual Entity");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				itmRenameEntity(evt);
			}
		});
		menu.add(item);

		ArrayList<FileGroupCollection> entities = getEntities();
		if (entities.size() == 1) {
			menu.addSeparator();
			JMenu addTypes = new JMenu("Add Representation Type");
			boolean entityTypesAdded = false;
			for (int i = 0; i < RepresentationTypes.values().length; i++) {
				final RepresentationTypes typeToAdd = RepresentationTypes
						.values()[i];
				boolean addType = true;
				if (!typeToAdd.allowMultiples()) {
					addType = (!entityTypeAdded(typeToAdd, entities));
				}
				if (addType) {
					entityTypesAdded = true;
					JMenuItem subItem = new JMenuItem("Add "
							+ typeToAdd.description());
					subItem
							.addActionListener(new java.awt.event.ActionListener() {
								public void actionPerformed(
										java.awt.event.ActionEvent evt) {
									itmAddEntity(evt, typeToAdd);
								}
							});
					addTypes.add(subItem);
				}
			}
			if (entityTypesAdded) {
				menu.add(addTypes);
			}
		} else {
			item = new JMenuItem("Delete Intellectual Entity");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					itmDeleteEntityItem(evt);
				}
			});
			menu.add(item);
		}
		if (thereAreMissingFiles()) {
			menu.addSeparator();
			item = new JMenuItem("Show Missing Files");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					showMissingFiles();
				}
			});
			menu.add(item);
		}
		return menu;
	}

	private JPopupMenu getEntityMenu(FileGroup entity) {
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem item;
		ArrayList<FileGroupCollection> entities = getEntities();
		if (entities.size() == 1) {
			item = new JMenuItem("Delete Representation Type");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					itmDeleteEntityItem(evt);
				}
			});
			menu.add(item);
		}
		return menu;
	}

	public JPopupMenu getStructMapMenu(DefaultMutableTreeNode node) {
		theNodeInClipboard.clear();
		theNodeInClipboard.add(node);
		JPopupMenu menu = null;
		if (node.getUserObject() instanceof String) {
			menu = getStructMapMenu();
		} else if (node.getUserObject() instanceof FileSystemObject) {
			menu = getStructMapMenu((FileSystemObject) node.getUserObject());
		} else if (node.getUserObject() instanceof StructMap) {
			menu = getStructMapMenu((StructMap) node.getUserObject());
		}
		return menu;
	}

	private JPopupMenu getStructMapMenu() { // For the root item only
		if (!entityRootSet) {
			return null; // Don't want to have a menu unless we are editing an
			// entity
		}
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Add Structure Item");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				itmAddStructure(evt);
			}
		});
		menu.add(item);
		return menu;
	}

	private JPopupMenu getStructMapMenu(StructMap map) {
		if (!entityRootSet) {
			return null; // Don't want to have a menu unless we are editing an
			// entity
		}
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Rename Structure Item");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				itmRenameStructMap(evt);
			}
		});
		menu.add(item);
		item = new JMenuItem("Add Structure Item");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				itmAddStructure(evt);
			}
		});
		menu.add(item);
		menu.addSeparator();
		item = new JMenuItem("Delete Structure Item");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				itmDeleteStructure(evt);
			}
		});
		menu.add(item);
		return menu;
	}

	private JPopupMenu getStructMapMenu(FileSystemObject fso) {
		if (!entityRootSet) {
			return null; // Don't want to have a menu unless we are editing an
			// entity
		}
		final JPopupMenu menu = new JPopupMenu();
		ArrayList<FileGroupCollection> entities = getEntities();
		if (entities.size() == 1) {
			JMenuItem item = new JMenuItem("Delete File");
			item.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					itmDeleteStructure(evt);
				}
			});
			menu.add(item);
		}
		return menu;
	}

	private void itmRenameStructMap(java.awt.event.ActionEvent evt) {
		editStructMap();
	}

	private void editStructMap() {
		theStructMapTree
				.startEditingAtPath(theStructMapTree.getSelectionPath());
	}

	private void itmAddStructure(java.awt.event.ActionEvent evt) {
		DefaultMutableTreeNode node = theNodeInClipboard.get(0);
		if ((node.getUserObject() instanceof StructMap)
				|| (node.getUserObject() instanceof String)) {
			StructMap newStruct = StructMap.create("New Structure", null, null,
					0);
			if (node.getUserObject() instanceof StructMap) {
				StructMap parent = (StructMap) node.getUserObject();
				if (parent.getChildren() == null) {
					parent.setChildren(new StructMapCollection());
				}
				parent.getChildren().add(newStruct);
			}
			addStructMapItem(newStruct, node, true);
		}
		manualDepositFrame.checkButtons();
	}

	private void itmDeleteStructure(java.awt.event.ActionEvent evt) {
		DefaultMutableTreeNode node = theNodeInClipboard.get(0);
		deleteStructMapNode(node);
		manualDepositFrame.checkButtons();
	}

	public void setStandardFont(Font theFont) {
		standardFont = theFont;
		resetTrees(standardFont.getSize());

		if (theMetaDataTable != null) {
			TableColumn col = theMetaDataTable.getColumnModel().getColumn(1);
			col.setCellEditor(new MetaDataElementCellEditor(standardFont));
		}
	}

	private void resetTrees(int fontSize) {
		int rowHeight = 0;
		if (fontSize < 14) {
			currentIconDirectory = "icons/16/";
			rowHeight = 16;
		} else if (fontSize < 18) {
			currentIconDirectory = "icons/24/";
			rowHeight = 24;
		} else if (fontSize < 22) {
			currentIconDirectory = "icons/32/";
			rowHeight = 32;
		} else if (fontSize < 26) {
			currentIconDirectory = "icons/48/";
			rowHeight = 48;
		} else if (fontSize < 30) {
			currentIconDirectory = "icons/64/";
			rowHeight = 64;
		} else {
			currentIconDirectory = "icons/128/";
			rowHeight = 128;
		}
		for (JTree tree : trees) {
			tree.setRowHeight(rowHeight);
			tree.setCellRenderer(new IconRenderer(currentIconDirectory));
			DepositTreeEditor editor = (DepositTreeEditor) tree.getCellEditor();
			editor.setStandardFont(standardFont);
		}
	}

	public void addHandlers(JTree fileSystemTree, JTree entityTree,
			JTree structMapTree, JComboBox templateList,
			JComboBox structTemplateList, JComboBox sortByList, JComboBox fixityTypesList, 
			JTable metaDataTable, JTable jobQueueRunningTable,
			JTable jobQueuePendingTable, JTable jobQueueFailedTable,
			JTable jobQueueDepositedTable, JTable jobQueueCompleteTable,
			JMenu favouritesMenu, JList producerList, JList materialFlowList)
			throws Exception {
		LOG.debug("addHandlers, start");
		theFileSystemTree = fileSystemTree;
		theEntityTree = entityTree;
		theStructMapTree = structMapTree;
		theTemplateList = templateList;
		theStructTemplateList = structTemplateList;
		theSortByList = sortByList;
		theFixityTypesList = fixityTypesList;
		theMetaDataTable = metaDataTable;
		theJobQueueRunningTable = jobQueueRunningTable;
		theJobQueuePendingTable = jobQueuePendingTable;
		theJobQueueFailedTable = jobQueueFailedTable;
		theJobQueueDepositedTable = jobQueueDepositedTable;
		theJobQueueCompleteTable = jobQueueCompleteTable;
		theFavouritesMenu = favouritesMenu;
		theProducerList = producerList;
		theMaterialFlowList = materialFlowList;
		addTreeSelectionListener(theFileSystemTree);
		addTreeSelectionListener(theEntityTree);
		addTreeSelectionListener(theStructMapTree);
		theEntityTree.setLargeModel(true);
		theStructMapTree.setLargeModel(true);
		loadTemplates();
		loadSortBy();
		loadFixityTypes();
		addMetaDataTableModelAndHandlers();
		addJobQueueTableModelAndHandlers();
		addCustomizeMetaDataTableModelAndHandlers();
		setupFavourites();
	}

	private void addTreeSelectionListener(JTree theTree) {
		if (theTree == null) {
			return;
		}
		trees.add(theTree);
		theTree.setCellEditor(new DepositTreeEditor(theTree,
				new DefaultTreeCellRenderer(), new DefaultCellEditor(
						new TreeEditorField(applicationProperties
								.getApplicationData()
								.getMaximumStructureLength()))));
		theTree.setUI(new CustomTreeUI());
		theTree.setCellRenderer(new IconRenderer(currentIconDirectory));
		theTree
				.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
					public void valueChanged(
							javax.swing.event.TreeSelectionEvent evt) {
						treeValueChanged(evt);
					}
				});
	}

	private FileSystemObject getSelectedFSO() {
		FileSystemObject fso = null;
		JTree tree = getTree(ETreeType.FileSystemTree);
		if (tree.getSelectionPath() != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getSelectionPath().getLastPathComponent();
			if (node != null
					&& node.getUserObject() instanceof FileSystemObject) {
				fso = (FileSystemObject) node.getUserObject();
			}
		}
		return fso;
	}

	public boolean canSetIE() {
		boolean retVal = false;
		FileSystemObject fso = getSelectedFSO();
		if ((fso != null) && (!fso.getIsFile())) {
			retVal = true;
		}
		return retVal;
	}

	public boolean canSetFileAsIE() {
		boolean retVal = false;
		FileSystemObject fso = getSelectedFSO();
		if ((fso != null) && (fso.getIsFile())) {
			retVal = true;
		}
		return retVal;
	}

	private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {
		JTree sourceTree = (JTree) evt.getSource();
		TreePath[] paths = sourceTree.getSelectionPaths();
		if (paths != null) {
			theNodeInClipboard.clear();
			for (TreePath path : paths) {
				theNodeInClipboard.add((DefaultMutableTreeNode) path
						.getLastPathComponent());
			}
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) evt.getPath()
				.getLastPathComponent();
		JTree tree = (JTree) evt.getSource();
		if (getTreeType(tree).equals(ETreeType.FileSystemTree)) {
			if (node.getUserObject() instanceof FileSystemObject) {
				tree.setEditable(false);
			}
		}
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node
				.getParent();
		while (parent != null
				&& parent.getUserObject() instanceof FileSystemObject) {
			node = parent;
			parent = (DefaultMutableTreeNode) node.getParent();
		}
		tree.setEditable(true);
		if ((parent != null) && (parent.getUserObject() instanceof StructMap)) {
			if (node.getUserObject() instanceof FileSystemObject) {
				tree.setEditable(false);
			}
		}
	}

	private void setupMetaDataColumns() {
		TableRenderer renderer = new TableRenderer();
		TableColumn col = theMetaDataTable.getColumnModel().getColumn(0);
		col.setCellRenderer(renderer);
		col.setResizable(true);
		col = theMetaDataTable.getColumnModel().getColumn(1);
		col.setCellRenderer(renderer);
		col.setCellEditor(new MetaDataElementCellEditor(standardFont));
	}

	private void addMetaDataTableModelAndHandlers() throws Exception {
		metaDataTableModel = new MetaDataTableModel(userGroupData,
				MetaDataFields.ECMSSystem.CMS2);
		metaDataTableModel
				.addTableModelListener(new MetaDataTableModelListener());
		if (theMetaDataTable == null) {
			return;
		}
		theMetaDataTable.setModel(metaDataTableModel);
		theMetaDataTable.setSurrendersFocusOnKeystroke(true);
		theMetaDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		theMetaDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		theMetaDataTable.setColumnSelectionAllowed(true);
		theMetaDataTable.setRowSelectionAllowed(true);
		setupMetaDataColumns();
	}
	
	// Initialise the CustomizeMetaDataTableModel object customizeMetaDataTableModel
	private void addCustomizeMetaDataTableModelAndHandlers() throws Exception {
		customizeMetaDataTableModel = new CustomizeMetaDataTableModel();
	}
	
	private void setJobQueueTableDefaults(JTable jobQueueTable,
			JobQueueTableModel jobQueueTableModel) {
		jobQueueTableModel
				.addTableModelListener(new JobQueueTableModelListener());
		jobQueueTable.setModel(jobQueueTableModel);
		jobQueueTable.setSurrendersFocusOnKeystroke(true);
		jobQueueTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jobQueueTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jobQueueTable.setColumnSelectionAllowed(true);
		jobQueueTable.setRowSelectionAllowed(true);
		TableRenderer renderer = new TableRenderer();
		TableColumn col = jobQueueTable.getColumnModel().getColumn(0);
		int width = 200;
		col.setPreferredWidth(width);
		col.setWidth(width);
		col.setCellRenderer(renderer);

		col = jobQueueTable.getColumnModel().getColumn(1);
		width = 1400;
		col.setPreferredWidth(width);
		col.setWidth(width);
		col.setCellRenderer(renderer);
	}

	public void addJobQueueTableModelAndHandlers() {
		JobQueueTableModel jobQueueRunningTableModel = new JobQueueTableModel(
				JOB_QUEUE_COLUMNS);
		setJobQueueTableDefaults(theJobQueueRunningTable,
				jobQueueRunningTableModel);

		JobQueueTableModel jobQueuePendingTableModel = new JobQueueTableModel(
				JOB_QUEUE_COLUMNS);
		setJobQueueTableDefaults(theJobQueuePendingTable,
				jobQueuePendingTableModel);

		JobQueueTableModel jobQueueFailedTableModel = new JobQueueTableModel(
				JOB_QUEUE_COLUMNS);
		setJobQueueTableDefaults(theJobQueueFailedTable,
				jobQueueFailedTableModel);

		JobQueueTableModel jobQueueDepositedTableModel = new JobQueueTableModel(
				JOB_QUEUE_COLUMNS);
		setJobQueueTableDefaults(theJobQueueDepositedTable,
				jobQueueDepositedTableModel);

		JobQueueTableModel jobQueueCompleteTableModel = new JobQueueTableModel(
				JOB_QUEUE_COLUMNS);
		setJobQueueTableDefaults(theJobQueueCompleteTable,
				jobQueueCompleteTableModel);

		refreshJobQueue();
	}

	public void refreshJobQueue() {
		if (jobQueueMgmt != null) {
			jobQueueMgmt.refreshJobQueue();
		}
	}

	private void loadSortBy() {
		theSortByList.removeAllItems();
		for (SortBy sortBy : SortBy.values()) {
			if (sortBy.visible()) {
				theSortByList.addItem(sortBy);
			}
		}
	}
	
	private void loadFixityTypes() {
		theFixityTypesList.removeAllItems();
		for (FixityTypes fixity : FixityTypes.values()) {
			if (fixity.visible()) {
				theFixityTypesList.addItem(fixity);
			}
		}
	}

	private void loadTemplates() {
		if (theTemplateList != null) {
			theTemplateList.removeAllItems();
			theTemplateList.addItem(NO_TEMPLATE_TEXT);
		}
		if (theStructTemplateList != null) {
			theStructTemplateList.removeAllItems();
			theStructTemplateList.addItem(NO_TEMPLATE_TEXT);
		}
		// Load local templates
		if (applicationProperties.getApplicationData().getSettingsPath() != null) {
			if (theTemplateList != null) {
				// Load MetaData templates
				File templateDirectoryFile = new File(applicationProperties
						.getApplicationData().getMetaDataTemplatePath());
				try {
					if (templateDirectoryFile.exists()
							&& templateDirectoryFile.isDirectory()) {
						for (File templateFile : templateDirectoryFile
								.listFiles(xmlFilter)) {
							if (templateFile.isFile()) {
								theTemplateList.addItem(FileUtils
										.getFileNameNoSuffix(templateFile));
							}
						}
					}
				} catch (Exception ex) {
					reportException(ex);
				}
			}
			if (theStructTemplateList != null) {
				// Structure templates
				File templateDirectoryFile = new File(applicationProperties
						.getApplicationData().getStructTemplatePath());
				try {
					if (templateDirectoryFile.exists()
							&& templateDirectoryFile.isDirectory()) {
						for (File templateFile : templateDirectoryFile
								.listFiles(xmlFilter)) {
							if (templateFile.isFile()) {
								theStructTemplateList.addItem(FileUtils
										.getFileNameNoSuffix(templateFile));
							}
						}
					}
				} catch (Exception ex) {
					reportException(ex);
				}
			}
		}
		// Load shared templates
		if (userGroupData.getSharedTemplatePath() != null) {
			if (theTemplateList != null) {
				// Load MetaData templates
				File templateDirectoryFile = new File(userGroupData
						.getSharedMetadataTemplatePath());
				try {
					if (templateDirectoryFile.exists()
							&& templateDirectoryFile.isDirectory()) {
						for (File templateFile : templateDirectoryFile
								.listFiles(xmlFilter)) {
							if (templateFile.isFile()) {
								theTemplateList.addItem(FileUtils
										.getFileNameNoSuffix(templateFile)
										+ SHARED_TEMPLATE_TEXT);
							}
						}
					}
				} catch (Exception ex) {
					reportException(ex);
				}
			}
			if (theStructTemplateList != null) {
				// Structure templates
				File templateDirectoryFile = new File(userGroupData
						.getSharedStructTemplatePath());
				try {
					if (templateDirectoryFile.exists()
							&& templateDirectoryFile.isDirectory()) {
						for (File templateFile : templateDirectoryFile
								.listFiles(xmlFilter)) {
							if (templateFile.isFile()) {
								theStructTemplateList.addItem(FileUtils
										.getFileNameNoSuffix(templateFile)
										+ SHARED_TEMPLATE_TEXT);
							}
						}
					}
				} catch (Exception ex) {
					reportException(ex);
				}
			}
		}
	}

	private String getTemplateName() {
		String templateName = "";
		if (theTemplateList != null
				&& theTemplateList.getSelectedItem() != null) {
			templateName = (String) theTemplateList.getSelectedItem();
		}
		return templateName;
	}

	private String getStructTemplateName() {
		String templateName = "";
		if (theStructTemplateList != null
				&& theStructTemplateList.getSelectedItem() != null) {
			templateName = (String) theStructTemplateList.getSelectedItem();
		}
		return templateName;
	}

	public boolean canSaveTemplate() {
		boolean canSave = false;
		if (metaDataTableModel != null) {
			for (int i = 0; i < metaDataTableModel.getRowCount(); i++) {
				IMetaDataTypeExtended property = metaDataTableModel.getRow(i);
				if ((property != null) && (property.getSavedWithTemplate())
						&& (property.getDataFieldValue() != null)
						&& (!property.getDataFieldValue().equals(""))) {
					canSave = true;
					break;
				}
			}
		}
		return canSave;
	}

	public boolean canSaveStructTemplate() {
		boolean canSave = false;
		if (getStructures().size() > 0) {
			canSave = true;
		}
		return canSave;
	}

	public boolean canSaveSharedTemplate() {
		if (userGroupData != null
				&& userGroupData.getSharedMetadataTemplatePath() != null) {
			File templateDirectory = new File(userGroupData
					.getSharedMetadataTemplatePath());
			return templateDirectory.exists()
					&& templateDirectory.isDirectory() && canSaveTemplate();
		} else {
			return false;
		}
	}

	public boolean canSaveSharedStructTemplate() {
		if (userGroupData.getSharedStructTemplatePath() != null) {
			File templateDirectory = new File(userGroupData
					.getSharedStructTemplatePath());
			return templateDirectory.exists()
					&& templateDirectory.isDirectory()
					&& canSaveStructTemplate();
		} else {
			return false;
		}
	}

	public boolean canDeleteTemplate() {
		String templateName = getTemplateName();
		return ((!templateName.equalsIgnoreCase(NO_TEMPLATE_TEXT)) && (templateExists(templateName)));
	}

	public boolean canDeleteStructTemplate() {
		String templateName = getStructTemplateName();
		return ((!templateName.equalsIgnoreCase(NO_TEMPLATE_TEXT)) && (structTemplateExists(templateName)));
	}

	public void deleteTemplate() {
		boolean deleted = false;
		String templateName = getTemplateName();
		String fileName;
		theTemplateList.setSelectedIndex(0);
		if (templateName.endsWith(SHARED_TEMPLATE_TEXT)) {
			fileName = getSharedTemplateFileName(templateName);
		} else {
			fileName = this.getTemplateFileName(templateName);
		}
		File file = new File(fileName);
		if (file.delete()) {
			deleted = true;
		}
		if (deleted) {
			theTemplateList.removeItem(templateName);
			manualDepositFrame.showMessage("Template deleted",
					"Template successfully deleted");
		} else {
			manualDepositFrame.showError("Template not deleted",
					"Could not delete template");
		}
	}

	public void saveTemplate(String templateName) throws TemplateException,
			IOException, XmlException {
		if (!canSaveTemplate()) {
			throw new TemplateException("Cannot save template");
		}
		stopEditingMetaData();
		String templateFileName = getTemplateFileName(templateName);
		saveTemplateToXML(templateFileName);
		addTemplateNameToList(templateName);
	}

	public void saveSharedTemplate(String templateName)
			throws TemplateException, IOException, XmlException {
		if (!canSaveSharedTemplate()) {
			throw new TemplateException("Cannot save shared template");
		}
		stopEditingMetaData();
		String templateNameShared = templateName + SHARED_TEMPLATE_TEXT;
		String templateFileName = getSharedTemplateFileName(templateName);
		saveTemplateToXML(templateFileName);
		addTemplateNameToList(templateNameShared);
	}

	private void saveTemplateToXML(String templateFileName)
			throws TemplateException, IOException, XmlException {
		File templateFile = new File(templateFileName);
		if (templateFile.exists()) {
			throw new TemplateException(
					"A template with that name already exists");
		}
		XMLHandler handler = new XMLHandler("template", templateFileName);
		XMLObject template = handler.createXMLObject("template");
		MetaDataFields metaDataList = metaDataTableModel.getMetaData();
		for (int i = 0; i < metaDataList.size(); i++) {
			IMetaDataTypeExtended property = metaDataList.getAt(i);
			if ((property != null) && (property.getSavedWithTemplate())
					&& (property.getDataFieldValue() != null)
					&& (!property.getDataFieldValue().equals(""))) {
				String objectName = String.format("%s.%d", property
						.getDataFieldName(), property.getSortOrder());
				XMLObject metaData = handler.createXMLObject(property
						.getDataFieldName(), objectName, property
						.getDataFieldValue());
				if (property.getDataType() == EDataType.ProvenanceNote) {
					metaData.addAttribute(PROVENANCE_EVENT_DESCRIPTION, property.getProvenanceEventDescription());
					metaData.addAttribute(PROVENANCE_EVENT_ID_TYPE, property
							.getProvenanceEventIdentifierType());
					metaData.addAttribute(PROVENANCE_EVENT_ID_VALUE, property
							.getProvenanceEventIdentifierValue());
					metaData
							.addAttribute(PROVENANCE_EVENT_OUTCOME, String
									.format("%b", property
											.getProvenanceEventOutcome()));
					metaData.addAttribute(PROVENANCE_EVENT_OUTCOME_DETAIL,
							property.getProvenanceEventOutcomeDetail());
					metaData.addAttribute(PROVENANCE_EVENT_EVENT_TYPE, property
							.getProvenanceNoteEventType());
				}
				metaData.addAttribute("SortOrder", String.format("%d", property
						.getSortOrder()));
				metaData.addAttribute(TEMPLATE_IDENTIFIER, property
						.getDataFieldName());
				template.addChild(property.getDataFieldName() + "."
						+ property.getDataFieldValue(), metaData);
			}
		}
		try {
			handler.addObject(template);
		} catch (Exception ex) {
			throw new TemplateException(
					"An error occurred saving the template", ex);
		}
		handler.writeXMLFile();

	}

	private void addTemplateNameToList(String templateName) {
		boolean templateAlreadyInList = false;
		for (int i = 0; !templateAlreadyInList
				&& i < theTemplateList.getItemCount(); i++) {
			templateAlreadyInList = (theTemplateList.getItemAt(i)
					.equals(templateName));
		}
		if (!templateAlreadyInList) {
			theTemplateList.addItem(templateName);
			theTemplateList.repaint();
		}
	}

	public void deleteStructTemplate() {
		boolean deleted = false;
		String templateName = getStructTemplateName();
		String fileName;
		if (templateName.endsWith(SHARED_TEMPLATE_TEXT)) {
			fileName = this.getSharedStructTemplateFileName(templateName);
		} else {
			fileName = this.getStructTemplateFileName(templateName);
		}
		File file = new File(fileName);
		if (file.delete()) {
			deleted = true;
		}
		if (deleted) {
			theStructTemplateList.removeItem(templateName);
			manualDepositFrame.showMessage("Template deleted",
					"Template successfully deleted");
		} else {
			manualDepositFrame.showError("Template not deleted",
					"Could not delete template");
		}
	}

	public void saveStructTemplate(String templateName)
			throws TemplateException, IOException, XmlException {
		if (!canSaveStructTemplate()) {
			throw new TemplateException("Cannot save template");
		}
		String templateFileName = getStructTemplateFileName(templateName);
		File templateFile = new File(templateFileName);
		if (templateFile.exists()) {
			throw new TemplateException(
					"A template with that name already exists");
		}
		StructMapCollection maps = getStructures();
		maps.storeAsXML(templateFileName, false);
		addStructTemplateNameToList(templateName);
	}

	private void addStructTemplateNameToList(String templateName) {
		boolean templateAlreadyInList = false;
		for (int i = 0; !templateAlreadyInList
				&& i < theStructTemplateList.getItemCount(); i++) {
			templateAlreadyInList = (theStructTemplateList.getItemAt(i)
					.equals(templateName));
		}
		if (!templateAlreadyInList) {
			theStructTemplateList.addItem(templateName);
		}
	}

	public void saveSharedStructTemplate(String templateName)
			throws TemplateException, IOException, XmlException {
		if (!canSaveSharedStructTemplate()) {
			throw new TemplateException("Cannot save shared template");
		}
		String templateNameShared = templateName + SHARED_TEMPLATE_TEXT;
		String templateFileName = getSharedStructTemplateFileName(templateName);
		File templateFile = new File(templateFileName);
		if (templateFile.exists()) {
			throw new TemplateException(
					"A shared template with that name already exists");
		}
		StructMapCollection maps = getStructures();
		maps.storeAsXML(templateFileName, false);
		addStructTemplateNameToList(templateNameShared);
	}

	private String getSharedTemplateFileName(String templateName) {
		if (userGroupData.getSharedMetadataTemplatePath() != null) {
			return userGroupData.getSharedMetadataTemplatePath() + "/"
					+ templateName.replace(SHARED_TEMPLATE_TEXT, "")
					+ XML_SUFFIX;
		} else {
			return null;
		}
	}

	private String getTemplateFileName(String templateName) {
		if (applicationProperties.getApplicationData()
				.getMetaDataTemplatePath() != null) {
			return applicationProperties.getApplicationData()
					.getMetaDataTemplatePath()
					+ "/" + templateName + XML_SUFFIX;
		} else {
			return null;
		}
	}

	private String getSharedStructTemplateFileName(String templateName) {
		if (userGroupData.getSharedStructTemplatePath() != null) {
			return userGroupData.getSharedStructTemplatePath() + "/"
					+ templateName.replace(SHARED_TEMPLATE_TEXT, "")
					+ XML_SUFFIX;
		} else {
			return null;
		}
	}

	private String getStructTemplateFileName(String templateName) {
		if (applicationProperties.getApplicationData().getStructTemplatePath() != null) {
			return applicationProperties.getApplicationData()
					.getStructTemplatePath()
					+ "/" + templateName + XML_SUFFIX;
		} else {
			return null;
		}
	}

	private boolean templateExists(String templateName) {
		boolean retVal = !templateName.equals("");
		if (retVal) {
			String templateFileName;
			if (templateName.endsWith(SHARED_TEMPLATE_TEXT)) {
				templateFileName = getSharedTemplateFileName(templateName);
			} else {
				templateFileName = getTemplateFileName(templateName);
			}
			if (templateFileName == null) {
				retVal = false;
			} else {
				File templateFile = new File(templateFileName);
				retVal = templateFile.exists() && templateFile.isFile();
			}
		}
		return retVal;
	}

	private boolean structTemplateExists(String templateName) {
		boolean retVal = !templateName.equals("");
		if (retVal) {
			String templateFileName;
			if (templateName.endsWith(SHARED_TEMPLATE_TEXT)) {
				templateFileName = getSharedStructTemplateFileName(templateName);
			} else {
				templateFileName = getStructTemplateFileName(templateName);
			}
			if (templateFileName == null) {
				retVal = false;
			} else {
				File templateFile = new File(templateFileName);
				retVal = templateFile.exists() && templateFile.isFile();
			}
		}
		return retVal;
	}

	private String willOverwrite(int sortOrder, String dataTypeName,
			String dataValue) {
		String retVal = "";
		MetaDataFields metaData = metaDataTableModel.getMetaData();
		IMetaDataTypeExtended dataType = metaData.getMetaDataType(sortOrder,
				dataTypeName);
		if (dataType == null) {
			dataType = metaData.getMetaDataType(dataTypeName);
		}
		if (dataType != null && dataType.getIsVisible()) {
			if ((dataType.getIsSet() && !dataValue.equalsIgnoreCase(dataType
					.getDataFieldValue()))) {
				retVal = dataType.getDataFieldDescription();
			}
		}
		return retVal;
	}

	private void loadMetaDataField(int sortOrder, String dataTypeName,
			String dataValue) {
		MetaDataFields metaData = metaDataTableModel.getMetaData();
		IMetaDataTypeExtended dataType = metaData.getMetaDataType(sortOrder,
				dataTypeName);
		if (dataType == null) {
			dataType = metaData.getMetaDataType(dataTypeName);
		}
		if (dataType != null) {
			if ((dataType.getAllowsMultipleRows())
					&& (dataType.getSortOrder() != sortOrder)) {
				IMetaDataTypeExtended newData = new MetaDataTypeImpl();
				try {
					dataType.duplicate(newData);
					newData.setSortOrder(sortOrder);
					newData.setDataFieldValue(dataValue);
				} catch (Exception ex) {
					manualDepositFrame
							.showError("Error loading meta data field",
									ERROR_OCCURRED, ex);
				}
				this.metaDataTableModel.addRow(newData);
			} else {
				try {
					dataType.setDataFieldValue(dataValue);
				} catch (Exception ex) {
					manualDepositFrame
							.showError("Error loading meta data field",
									ERROR_OCCURRED, ex);
				}
			}
		}
	}

	private void clearStructTemplate() {
		addStructMap(new StructMapCollection());
	}

	private void clearTemplate() {
		MetaDataFields.ECMSSystem cmsSystem = MetaDataFields.ECMSSystem.NoSystem;
		if (metaDataTableModel != null) {
			if (theMetaDataTable != null && theMetaDataTable.isEditing()) {
				theMetaDataTable.getCellEditor().stopCellEditing();
			}
			metaDataTableModel.clearTableData();
			try {
				if (userGroupData.getUserGroupDesc().equals(UserGroupDesc.StaffMediated)){
					cmsSystem = MetaDataFields.ECMSSystem.StaffMediated;
					metaDataTableModel.getMetaData().setCMSSystem(applicationProperties.getApplicationData()
							.getCMSSystemText(cmsSystem));
				} else if (userGroupData.getUserGroupDesc().equals(UserGroupDesc.Published)){
					cmsSystem = MetaDataFields.ECMSSystem.CMS2;
				} else if (userGroupData.getUserGroupDesc().equals(UserGroupDesc.Digitisation) || userGroupData.getUserGroupDesc().equals(UserGroupDesc.Sound)
						|| userGroupData.getUserGroupDesc().equals(UserGroupDesc.Video)){
					cmsSystem = MetaDataFields.ECMSSystem.CMS1;
				} else {
					cmsSystem = metaDataTableModel.getMetaData().getCMSSystemType();
				}
			} catch (InvalidApplicationDataException appEx) {
				handleApplicationDataException(appEx);
			} catch (InvalidCMSSystemException cmsEx) {
				handleCMSException(cmsEx);
			}
			refreshMetaData();
		} else {
			if (userGroupData.isIncludeProducerList()) {
				cmsSystem = MetaDataFields.ECMSSystem.StaffMediated;
			} else if (userGroupData.isIncludeCMS2Search()) {
				cmsSystem = MetaDataFields.ECMSSystem.CMS2;
			} else if (userGroupData.isIncludeCMS1Search()) {
				cmsSystem = MetaDataFields.ECMSSystem.CMS1;
			}
		}
		manualDepositFrame.setSearchType(cmsSystem);
	}

	public boolean loadStructTemplate() {
		boolean templateLoaded = true;
		String templateName = getStructTemplateName();
		if ((templateName != null) && (!templateName.equals(NO_TEMPLATE_TEXT))
				&& (structTemplateExists(templateName))) {
			StructMapCollection structure = getStructures();
			if (structure.size() > 0) {
				if (!manualDepositFrame
						.confirm("The existing structure will be overwritten\nDo you wish to continue?")) {
					templateLoaded = false;
				}
			}
			if (templateLoaded) {
				String templateFileName;
				if (templateName.endsWith(SHARED_TEMPLATE_TEXT)) {
					templateFileName = getSharedStructTemplateFileName(templateName);
				} else {
					templateFileName = getStructTemplateFileName(templateName);
				}
				try {
					if (templateLoaded) {
						structure.loadFromXML(templateFileName, null);
					}
				} catch (Exception ex) {
					reportException(ex);
				}
			}
			if (templateLoaded) {
				structure.resetOrder();
				addStructMap(structure);
			}
		} else {
			clearStructTemplate();
		}
		return templateLoaded;
	}

	private void stopEditingMetaData() {
		if ((theMetaDataTable != null) && (theMetaDataTable.isEditing())) {
			theMetaDataTable.getCellEditor().stopCellEditing();
		}
	}

	private void changeMetaData(MetaDataFields.ECMSSystem cmsSystem) {
		if (metaDataTableModel != null) {
			if (currentCmsSystem != cmsSystem) {
				currentCmsSystem = cmsSystem;
				
				//Refresh the metadata if the usergroup is NOT 'StaffMediated' and if not using CMS systems for StaffMediated deposits.
					//If Usergroup description is not there in usergroup data xml then it defaults to 'None'.
				String userGroup = userGroupData.getUserGroupDesc().name();
				String currentCMSId = metaDataTableModel.getMetaData().getCMSID(cmsSystem.name());
				if (!userGroup.equals("StaffMediated")){
					try {
						metaDataTableModel.setMetaDataType(cmsSystem);
						refreshMetaData();
					} catch (Exception ex) {
						reportException(ex);
					}
				}else{
					if (currentCMSId.equals("")){
						try {
							metaDataTableModel.setMetaDataType(cmsSystem);
							refreshMetaData();
						} catch (Exception ex) {
							reportException(ex);
						}
					}
				}
			}
			stopEditingMetaData();
		}
	}

	public boolean loadTemplate() {
		if ((theMetaDataTable != null) && (theMetaDataTable.isEditing())) {
			theMetaDataTable.getCellEditor().stopCellEditing();
		}
		boolean templateLoaded = true;
		String templateName = getTemplateName();
		if ((templateName != null) && (!templateName.equals(NO_TEMPLATE_TEXT))
				&& (templateExists(templateName))) {
			String templateFileName;
			if (templateName.endsWith(SHARED_TEMPLATE_TEXT)) {
				templateFileName = getSharedTemplateFileName(templateName);
			} else {
				templateFileName = getTemplateFileName(templateName);
			}
			try {
				XMLHandler handler = new XMLHandler("template",
						templateFileName);
				XMLObject template = handler.getObject(handler.getObjectNames()
						.get(0));
				// Check whether things will be overwritten first
				StringWriter overwrittenWriter = new StringWriter();
				String fieldsOverwritten = "";
				for (XMLObject object : template.getChildObjects()) {
					int sortOrder = Integer.parseInt(object
							.getAttribute("SortOrder"));
					String originalObjectType = object
							.getAttribute(TEMPLATE_IDENTIFIER);
					String overwrite = willOverwrite(sortOrder,
							originalObjectType, object.getObjectValue());
					if (!overwrite.equals("")) {
						if (!overwrittenWriter.toString().equals("")) {
							overwrittenWriter.append(", ");
						}
						overwrittenWriter.append(overwrite);
					}
				}
				fieldsOverwritten = overwrittenWriter.toString();
				// Now do it
				if ((fieldsOverwritten.equals(""))
						|| (manualDepositFrame
								.confirm("The following fields will be overwritten\n"
										+ fieldsOverwritten
										+ "\nDo you wish to continue?"))) {
					// First make sure we have the right meta data loaded, then clear it
					XMLObject cmsSystemObj = null;
					for (XMLObject object : template.getChildObjects()) {
						if (object.getObjectType().equalsIgnoreCase("CMSSystem") || object.getObjectType().equalsIgnoreCase("objectIdentifierType")) {
							cmsSystemObj = object;
							break;
						}
					}
					MetaDataFields.ECMSSystem cmsSystem = MetaDataFields.ECMSSystem.NoSystem;
					if (cmsSystemObj != null) {
						if (cmsSystemObj.getObjectValue().equalsIgnoreCase("ALMAMMS")){
							cmsSystem = MetaDataFields.ECMSSystem.CMS1;
						} else {
							for (int i = 0; i < MetaDataFields.ECMSSystem.values().length; i++) {
								if (ApplicationData.getInstance().getCMSSystemText(MetaDataFields.ECMSSystem.values()[i]).equalsIgnoreCase(cmsSystemObj.getObjectValue())) {
									cmsSystem = MetaDataFields.ECMSSystem.values()[i];
									break;
								}
							}
						}
						changeMetaData(cmsSystem);
					}
					// Setting the search type will overwrite any fields marked
					// as PopulatedFromCMS with blanks
					// This is not a good thing if the template does not have
					// the CMS details stored
					// Therefore we will store the CMS details here & put them
					// back after the search type is set
					// If the template has the CMS details it will overwrite
					// them later.
					MetaDataFields metaData = metaDataTableModel.getMetaData();
					ArrayList<IMetaDataTypeExtended> metaDataToSave = new ArrayList<IMetaDataTypeExtended>();
					for (IMetaDataTypeExtended data : metaData) {
						if (data.getIsPopulatedFromCMS()) {
							IMetaDataTypeExtended dataToSave = new MetaDataTypeImpl();
							try {
								data.duplicate(dataToSave);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							metaDataToSave.add(dataToSave);
						}
					}
					manualDepositFrame.setSearchType(cmsSystem);
					for (IMetaDataTypeExtended data : metaData) {
						if (data.getIsPopulatedFromCMS()) {
							for (IMetaDataTypeExtended dataSaved : metaDataToSave) {
								if (dataSaved.isEquivalentTo(data, true)) {
									data.setDataFieldValue(dataSaved
											.getDataFieldValue());
									break;
								}
							}
						}
					}
					for (XMLObject object : template.getChildObjects()) {
						if (object.getObjectType().equals(
								ProvenanceEvent.XML_OBJECT_TYPE)) {
							ProvenanceEvent event = new ProvenanceEvent(
									object.getAttribute(PROVENANCE_EVENT_ID_TYPE),
									object.getAttribute(PROVENANCE_EVENT_ID_VALUE),
									object.getAttribute(PROVENANCE_EVENT_EVENT_TYPE),
									Boolean.parseBoolean(object.getAttribute(PROVENANCE_EVENT_OUTCOME)),
									object.getAttribute(PROVENANCE_EVENT_OUTCOME_DETAIL),
									applicationProperties.getApplicationData().getMaximumProvenanceEventLength(),
									object.getAttribute(PROVENANCE_EVENT_DESCRIPTION));
							IMetaDataTypeExtended dataType = event
									.toMetadataType(object.getObjectValue());
							metaDataTableModel.addRow(dataType);

						} else {
							int sortOrder = Integer.parseInt(object
									.getAttribute("SortOrder"));
							String originalObjectType = object
									.getAttribute(TEMPLATE_IDENTIFIER);
							loadMetaDataField(sortOrder, originalObjectType,
									object.getObjectValue());
						}
					}
				} else {
					templateLoaded = false;
				}
			} catch (Exception ex) {
				reportException(ex);
			}
		} else {
			clearTemplate();
		}
		if (theMetaDataTable != null) {
			refreshMetaData();
		}
		return templateLoaded;
	}
	
	private boolean isMetadataCustomizable() {
		boolean canCustomize = false;
		MetaDataFields metaData = metaDataTableModel.getMetaData();
		for (IMetaDataTypeExtended data : metaData) {
			if (data.getIsCustomizable()){
				canCustomize = true;
				break;
			}
		}
		return canCustomize;
	}

	public boolean canCancel() {
		return entityRootSet;
	}

	private boolean metaDataOkay(boolean checkSystemGeneratedItems) {
		MetaDataFields metaData = metaDataTableModel.getMetaData();
		return metaDataOkay(metaData, checkSystemGeneratedItems);
	}

	public boolean metaDataOkay(MetaDataFields metaData, boolean checkSystemGeneratedItems) {
		boolean dataOkay = true;
		if (metaData == null || metaData.getMetaDataFields() == null) {
			dataOkay = false;
		} else {
			for (int i = 0; i < metaData.getMetaDataFields().size(); i++) {
				IMetaDataTypeExtended theType = metaData.getMetaDataFields()
						.get(i);
				if ((theType.getIsCompulsory())
						&& (theType.getDataFieldValue() == null || theType
								.getDataFieldValue().length() == 0)) {
					try {
						if ((!theType.getDataFieldName().equals(metaData.getCMSIDAttributeName()))
								|| ((!metaData.getCMSSystemType().equals(MetaDataFields.ECMSSystem.NoSystem)) 
										&& (!metaData.getCMSSystemType().equals(MetaDataFields.ECMSSystem.StaffMediated)))) {
							boolean notSystemGeneratedOrCheckNotNeeded = checkSystemGeneratedItems;
							if (!notSystemGeneratedOrCheckNotNeeded) {
								notSystemGeneratedOrCheckNotNeeded = !theType.getIsPopulatedFromCMS();
							}
							if (notSystemGeneratedOrCheckNotNeeded) {
								dataOkay = false;
								break;
							}
						}
					} catch (InvalidApplicationDataException appEx) {
						handleApplicationDataException(appEx);
					} catch (InvalidCMSSystemException cmsEx) {
						handleCMSException(cmsEx);
					}
				}
			}
		}
		if (dataOkay && userGroupData.isIncludeProducerList()) {
			try {
				dataOkay = (((!metaData.getCMSSystemType().equals(
						MetaDataFields.ECMSSystem.StaffMediated))) || ((theProducerList
						.getSelectedValue() != null) && (theMaterialFlowList
						.getSelectedValue() != null)));
			} catch (InvalidApplicationDataException appEx) {
				handleApplicationDataException(appEx);
			} catch (InvalidCMSSystemException cmsEx) {
				handleCMSException(cmsEx);
			}
		}
		return dataOkay;
	}

	public boolean canSubmit() {
		return canSubmit(true);
	}

	public boolean canSubmit(boolean checkMetaData) {
		boolean submitOK = entityRootSet;
		if (submitOK) {
			boolean multipleMandatoryTypesNeeded = false;
			boolean multipleMandatoryTypesFound = false;
			for (RepresentationTypes theType : RepresentationTypes.values()) {
				if (theType.mandatory() || theType.multipleMandatory()) {
					boolean typeFound = false;
					for (FileGroupCollection entities : getEntities()) {
						for (FileGroup entityTest : entities) {
							if (entityTest.getEntityType().equals(theType)) {
								typeFound = true;
								break;
							}
						}
						if (typeFound) {
							break;
						}
					}
					if (theType.multipleMandatory()) {
						multipleMandatoryTypesNeeded = true;
						if (typeFound) {
							multipleMandatoryTypesFound = true;
						}
					}
					if (theType.mandatory() && !typeFound) {
						submitOK = false;
						break;
					}
				}
			}
			if (multipleMandatoryTypesNeeded && !multipleMandatoryTypesFound) {
				submitOK = false;
			}
			if (submitOK && checkMetaData) {
				submitOK = metaDataOkay(true);
			}
		}
		manualDepositFrame.setIELabel(submitOK);
		return submitOK;
	}

	private void addFileToEntityFile(FileSystemObject fileFrom,
			FileSystemObject fileTo) {
		if (fileTo.getChildren() == null) {
			fileTo.setChildren(new FSOCollection());
		}
		fileTo.getChildren().add(getTranslatedFile(fileFrom));
	}
	
	
	// Added 23/09/2013 by Ben. RE: #996 - To fix jumbling issue in IE pane.
	private void sortFilesInEntity(FileGroup entity, SortBy sortType) {
		if (entity.getChildren() != null) {
			entity.getChildren().setSortBy(sortType);
			entity.getChildren().reSortList();
		}
	}

	private void addFileToEntity(FileSystemObject fileFrom, FileGroup entity) {
		if (entity.getChildren() == null) {
			entity.setChildren(new FSOCollection());
		}
		FileSystemObject fsoTranslated = getTranslatedFile(fileFrom);
		fsoTranslated.setParentFileGroup(entity);
		entity.getChildren().add(fsoTranslated);
	}

	private boolean droppingDoesntMakeSense(
			List<DefaultMutableTreeNode> sourceNode,
			DefaultMutableTreeNode destinationNode) {
		/**
		 * Doesn't make sense if: The destination node is the source node The
		 * destination node is the immediate parent of the source node The
		 * destination node is a child of the source node
		 */
		boolean doesntMakeSense = false;
		for (DefaultMutableTreeNode node : sourceNode) {
			doesntMakeSense = ((node.equals(destinationNode)) || ((((DefaultMutableTreeNode) node)
					.getParent() != null) && (((DefaultMutableTreeNode) node)
					.getParent().equals(destinationNode))));
			for (int i = 0; !doesntMakeSense && i < node.getChildCount(); i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node
						.getChildAt(i);
				if (destinationNode.equals(childNode)) {
					doesntMakeSense = true;
				} else {
					ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
					nodeList.add(childNode);
					doesntMakeSense = droppingDoesntMakeSense(nodeList,
							destinationNode);
				}
			}
			if (doesntMakeSense) {
				break;
			}
		}
		return doesntMakeSense;
	}

	private boolean dropOK(DefaultMutableTreeNode destinationNode,
			JTree destinationTree) {
		boolean canDrop = false;
		if (droppingDoesntMakeSense(theNodeInClipboard, destinationNode)) {
			return false;
		}

		if (dragFromTreeType.equals(ETreeType.FileSystemTree)) {
			for (DefaultMutableTreeNode node : theNodeInClipboard) {
				if (node.getUserObject() instanceof FileSystemObject) {
					if (entityRootSet) {
						if (destinationNode.getUserObject() instanceof FileGroup) {
							canDrop = true;
						} else if (destinationNode.getUserObject() instanceof FileSystemObject) {
							canDrop = true;
						}
					} else {
						canDrop = true;
					}
				}
				if (!canDrop) {
					break;
				}
			}
		} else if (dragFromTreeType.equals(ETreeType.EntityTree)) {
			// If we have a multiple entity structure, can't drop at all
			if (getEntities().size() == 1) {
				// Can drop onto the Entity Tree or the StructMap tree
				DepositTreeModel model = (DepositTreeModel) destinationTree
						.getModel();
				if (model.getTreeType().equals(ETreeType.EntityTree)) {
					// Dragging to itself
					// Can drop a FileSystemObject or a StructMap onto a
					// StructMap
					// Can only drop a FileSystemObject onto a FileSystemObject
					for (DefaultMutableTreeNode node : theNodeInClipboard) {
						if ((node.getUserObject() instanceof FileSystemObject)
								&& ((destinationNode.getUserObject() instanceof FileSystemObject) || (destinationNode
										.getUserObject() instanceof FileGroup))) {
							canDrop = true;
							break;
						}
					}
				} else { // Dragging to StructMap
					// Can only drop a FileSystemObject
					// Can drop onto a FileSystemObject or a StructMap
					for (DefaultMutableTreeNode node : theNodeInClipboard) {
						if ((node.getUserObject() instanceof FileSystemObject)
								&& ((destinationNode.getUserObject() instanceof StructMap) || (destinationNode
										.getUserObject() instanceof FileSystemObject))) {
							FileSystemObject fso = (FileSystemObject) node
									.getUserObject();
							canDrop = !fileIsInStructMap(fso) || isCopying;
							if (canDrop) {
								break;
							}
						}
					}
				}
			}
		} else { // Dragging from StructMap
			// Can only drop onto the StructMap tree
			// Can drop a FileSystemObject or a StructMap onto a StructMap
			// Can only drop a FileSystemObject onto a FileSystemObject
			DepositTreeModel model = (DepositTreeModel) destinationTree
					.getModel();
			if (model.getTreeType().equals(ETreeType.StructMapTree)) {
				for (DefaultMutableTreeNode node : theNodeInClipboard) {
					if ((node.getUserObject() instanceof FileSystemObject)
							|| (node.getUserObject() instanceof StructMap)) {
						if (destinationNode.getUserObject() instanceof StructMap) {
							canDrop = true;
						} else if (node.getUserObject() instanceof FileSystemObject) {
							canDrop = true;
						}
					}
					if (canDrop) {
						break;
					}
				}
			}
		}
		return canDrop;
	}

	public void showSearchFields(ILSQueryType.eServerType searchType,
			List<LabelTextPair> searchFields) {
		List<LabelTextPair> theSearchFields = new ArrayList<LabelTextPair>(
				searchFields);
		SearchAttributeCollection attributes = searchAttributes
				.getSearchAttributes(searchType);
		for (int i = 0; i < attributes.size() && i < theSearchFields.size(); i++) {
			LabelTextPair sourcePair = attributes.get(i);
			LabelTextPair targetPair = theSearchFields.get(i);
			targetPair.setIndex(sourcePair.getIndex());
			targetPair.getLabel().setText(sourcePair.getLabel().getText());
			targetPair.getLabel().setVisible(true);
			
			//Checking the source text field...If the CMS is down the exception is handled in "SearchAttributeCollection" 
			//and text field is set to null to display a proper error message.
			if (sourcePair.getField() != null){
				targetPair.getField().setVisible(true);
				targetPair.getField().setText("");
				targetPair.getLabel().setForeground(Color.black);
			}else {
				targetPair.getField().setVisible(false);
				targetPair.getLabel().setForeground(Color.red);
			}
		}
		for (int i = attributes.size(); i < theSearchFields.size(); i++) {
			LabelTextPair pair = theSearchFields.get(i);
			pair.getLabel().setVisible(false);
			pair.getField().setVisible(false);
		}
	}

	public SearchAttributeDetail getIDAttribute(String idWanted,
			ILSQueryType.eServerType searchType) {
		return searchAttributes.getIDAttribute(idWanted, searchType);
	}

	public void searchCMS(Frame parent, ILSQueryType.eServerType searchType,
			List<LabelTextPair> searchAttributeList) {
		ICMSSearchResults resultsForm = new CMSSearchResults(parent, true,
				applicationProperties.getApplicationData().getSettingsPath());
		resultsForm.setFormFont(standardFont);
		CMSSearchResultsPresenter presenter = new CMSSearchResultsPresenter(
				resultsForm, searchAttributes.getSearchAttributesSupplied(
						searchType, searchAttributeList), this,
				applicationProperties, searchType);
		resultsForm.setPresenter(presenter);
		presenter.setupPresenter();
	}

	public void setCMSResults(CmsRecord rec, MetaDataFields metaData) throws MetsException {
		for (IMetaDataTypeExtended data : metaData.getMetaDataFields()) {
			if (data.getIsPopulatedFromCMS()) {
				String value = "";
				if (data.getCMSFieldName().equalsIgnoreCase("Id")) {
					
					//If CMS is ALMA always populate alma_xxxx as MMS_ID in 'ObjectIdentitfierValue' metadata field. 
					if (rec.getId() != null && rec.getId().contains("alma")){
						value = getRecId(rec.getId());
					}else{
						value = rec.getId();
					}
				} else if (data.getCMSFieldName().equalsIgnoreCase("Title")) {
					value = rec.getTitleStatement();
				} else if (data.getCMSFieldName().equalsIgnoreCase("Publisher")) {
					value = rec.getPublisher();
				} else if (data.getCMSFieldName().equalsIgnoreCase("Author")) {
					value = rec.getAuthorPersonal();
				} else if (data.getCMSFieldName().equalsIgnoreCase("Reference")) {
					value = rec.getReference();
				} else if (data.getCMSFieldName().equalsIgnoreCase("Deposit Type")) {
					value = rec.getDepositType();
				} else if (data.getCMSFieldName().equalsIgnoreCase("Description")) {
					value = rec.getDescription();
				} else if (data.getCMSFieldName().equalsIgnoreCase("Coverage")) {
					value = rec.getCoverage();
				} else if (data.getCMSFieldName().equalsIgnoreCase("Rights")) {
					value = rec.getRights();
					if (value == null) {
						value = "";
					}
					int colonPosition = value.indexOf(":");
					if (colonPosition >= 0) {
						value = value.substring(0, colonPosition - 1).trim();
					}
				} else if (data.getCMSFieldName().equalsIgnoreCase("Relation")) {
					value = rec.getRelation();
				}
				if (data.getDataType() == EDataType.MultiSelect) {
					boolean valueFound = false;
					for (MetaDataListValues listVal : data.getListItems()) {
						for (String test : listVal.getCmsMappings()) {
							if (test.equalsIgnoreCase(value)) {
								value = listVal.getValue();
								valueFound = true;
								break;
							}
						}
						if (valueFound) {
							break;
						}
					}
					if (!valueFound) {
						value = "";
					}
				}
				if (value == null) {
					value = "";
				}
				data.setDataFieldValue(value);
			}
		}
	}
	
	private String getRecId(String recId){
		String almaId = "";
		Pattern pattern = Pattern.compile("alma_\\d+,*");
		Matcher matcher = pattern.matcher(recId);
		if (matcher.find()){
			recId = matcher.group(0).replace("alma_", "");
			if (recId.endsWith(",")){
				almaId = recId.substring(0, recId.lastIndexOf(","));
			} else {
				almaId = recId;
			}
			
		}else{
			//should never reach here as almaId should never be an empty string.
			almaId = "";
		}
		return almaId;
	}

	public void setCMSResults(CmsRecord rec, MetaDataFields.ECMSSystem cmsSystem) {
		if (metaDataTableModel != null) {
			changeMetaData(cmsSystem);
			try {
				metaDataTableModel.addPermanentField(SUBMITTED_BY_NAME, System
						.getProperty("user.name")); // Must go after the
				// _Frame.setupScreen
			} catch (Exception ex) {
				manualDepositFrame.showError("Error setting user name",
						ERROR_OCCURRED, ex);
			}
			MetaDataFields metaData = metaDataTableModel.getMetaData();
			String cmsID = metaData.getCMSID(cmsSystem.name());
			String userGroup = userGroupData.getUserGroupDesc().name();
			try {
				if (userGroup.equals("StaffMediated")){
					if (rec.getId() == null){
						if (cmsID == ""){
							setCMSResults(rec, metaData);
							metaData.setCMSSystem(applicationProperties.getApplicationData().getCMSSystemText(cmsSystem));
						}
					}else {
						setCMSResults(rec, metaData);
					}
				}else{
					setCMSResults(rec, metaData);
				}
			} catch (Exception ex) {
				manualDepositFrame.showError("Error setting CMS data", ERROR_OCCURRED, ex);
			}
			manualDepositFrame.checkButtons();
		}
		if (theMetaDataTable != null) {
			refreshMetaData();
		}
	}

	private String checkStructMapForEmptyStructures(StructMapCollection maps) {
		String retVal = "";
		for (StructMap map : maps) {
			if (map.getChildren().size() == 0 && map.getFiles().size() == 0) {
				retVal = "You have at least one structure with no files\n";
				break;
			}
			return checkStructMapForEmptyStructures(map.getChildren());
		}
		return retVal;
	}

	public boolean cmsIDExistsInDps(String cmsID, String cmsSystem, boolean isEmu) throws Exception {
		boolean depositsExist = false;
		SruService sruService = new SruServiceImpl();
		SruRequest request = new SruRequestImpl();
		request.setUrl(applicationProperties.getApplicationData().getDPSSearchUrl());
		request.setStartRecord(0);
		request.setMaximumRecords(10);
		request.setSchema("DC");
		SimpleQuery query = new SimpleQuery();
		if (isEmu){
			query.addBasicCriteria("recordId=" + cmsID + " system=" + cmsSystem);
		} else {
			query.addBasicCriteria("IE.objectIdentifier.objectIdentifierType.ALMAMMS=" + cmsID);
		}
		request.setQuery(query);
		DcToHtmlTransformer transformer = new DcToHtmlTransformerImpl();
		String xmlResult = sruService.execute(request);
		QueryResults results = transformer.parseResults(xmlResult);
		if (results.getNoOfRecords() > 0) {
			depositsExist = true;
		}
		return depositsExist;
	}

	private boolean existingDeposits() {
		MetaDataFields metaData = metaDataTableModel.getMetaData();
		boolean depositsExist = false;
		String cmsID = "";
		boolean isCMSSystem = metaData.getAt(1).getDataFieldName().equalsIgnoreCase("CMSSystem");
		if (!isCMSSystem){
			cmsID = metaData.getCMSID("CMS1");
		} else {
			cmsID = metaData.getCMSID("CMS2");
		}
		try {
			if (cmsID == null || cmsID.trim().length() == 0){
			return depositsExist;
		}
		long startTime =  System.currentTimeMillis();
			depositsExist = cmsIDExistsInDps(cmsID, metaData.getCMSSystem(), isCMSSystem);
			LOG.info("Check CMS ID exists in DPS took "+ (System.currentTimeMillis() -  startTime)+ "milliseconds");
			if (depositsExist) {
				ContentExists contentExists = new ContentExists(manualDepositFrame.getComponent(), true,
						applicationProperties.getApplicationData().getSettingsPath(),
						applicationProperties.getApplicationData().getContentAggregatorUrl(), 
						metaData.getCMSSystem(), cmsID);
				contentExists.setVisible(true);
				depositsExist = !contentExists.isMakeDeposit();
			}
		} catch (Exception ex) {
			manualDepositFrame.showError(
					"Error querying the DPS for existing deposits",
					ERROR_OCCURRED, ex);
			reportException(ex);
		}
		return depositsExist;
	}

	private void checkJobQueueManagement() {
		if (jobQueueMgmt == null) {
			this.setupJobQueue();
		}
	}

	public void addJob(UploadJob job) {
		checkJobQueueManagement();
		jobQueueMgmt.addJob(job);
		LOG.debug("Job added, running? " + jobQueueMgmt.jobsRunning());
	}

	public UploadJob getUploadJob(String jobName) {
		UploadJob result = jobQueueMgmt.getUploadJob(jobName);
		return result;
	}

	public void loadEntity() {
		theMetaDataTable.editingStopped(null);
		if (!this.canSubmit()) {
			manualDepositFrame.showError("Cannot submit",
					"Please complete all mandatory fields");
			return;
		}
		if (existingDeposits()) {
			return;
		}
		String entityMessage = "";
		String FileMessage = "";
		for (FileGroupCollection collection : getEntities()) {
			if (collection.getFileGroupList().size() == 0) {
				entityMessage = "You have at least one entity with no structure\n";
			}
			for (FileGroup group : collection.getFileGroupList()) {
				if (group.getChildren().size() == 0) {
					FileMessage = "You have at least one representation type with no files\n";
				}
			}
		}
		String structureMessage = checkStructMapForEmptyStructures(getStructures());
		StringBuffer confirmMessage = new StringBuffer();
		if (!entityMessage.equals("")) {
			confirmMessage.append(entityMessage);
		}
		if (!FileMessage.equals("")) {
			confirmMessage.append(FileMessage);
		}
		if (!structureMessage.equals("")) {
			confirmMessage.append(structureMessage);
		}
		if (!confirmMessage.toString().equals("")) {
			confirmMessage.append("\nDo you still wish to load this?");
			if (!manualDepositFrame.confirm(confirmMessage.toString())) {
				return;
			}
		}
		String producerID = "";
		String materialFlowID = "";
		MetaDataFields metaData = metaDataTableModel.getMetaData();
		try {
			// should get producer and material flow id from screen if the system is 'StaffMediated'(For sound) and if group is published or Unpublished 
				//else should get the values from xml file.
			if (metaData.getCMSSystemType().equals(MetaDataFields.ECMSSystem.StaffMediated) || 
					userGroupData.getUserGroupDesc().name().equals("StaffMediated")) {
				if (userGroupData.isIncludeProducerList()){
					if (theProducerList.getSelectedValue() == null) {
						// Should never reach here because the UI should force
						// selection
						manualDepositFrame.showError("Cannot submit",
								"Must select a producer before submitting");
						return;
					}
					if (theMaterialFlowList.getSelectedValue() == null) {
						// Should never reach here because the UI should force selection
						manualDepositFrame.showError("Cannot submit",
								"Must select a material flow before submitting");
						return;
					}
					Producer theProducer = (Producer) theProducerList
							.getSelectedValue();
					producerID = theProducer.getID();
					MaterialFlow theMaterialFlow = (MaterialFlow) theMaterialFlowList.getSelectedValue();
					materialFlowID = theMaterialFlow.getID();
				}
			} else {
				producerID = userGroupData.getUserProducerID();
				materialFlowID = userGroupData.getMaterialFlowID();
				System.out.println("*************** " + materialFlowID + " ****************");
			}
		} catch (InvalidApplicationDataException appEx) {
			handleApplicationDataException(appEx);
		} catch (InvalidCMSSystemException cmsEx) {
			handleCMSException(cmsEx);
		}
		try {
			ArrayList<FileGroupCollection> entities = getEntities();
			String batchName = getEntityName();
			String entityName;
			if (entities.size() >= 1) {
				entityName = entities.get(0).getEntityName();
			} else {
				entityName = batchName;
			}
			LOG.debug("Loading entity " + entityName);
			metaData = buildCustomizedMetaData(entities.get(0).getEntityName());
			StructMapCollection structures = getStructures();
			UploadJob job = UploadJob.create(batchName, entityName, System
					.getProperty("user.name"), entities.get(0), structures,
					metaData, producerID, materialFlowID,
					applicationProperties, theFsoRoot.getFullPath(),
					theFsoRootFile == null, theFixityTypesList.getSelectedItem().toString());
			if (entities.size() > 1) {
				for (int i = 1; i < entities.size(); i++) {
					metaData = metaDataTableModel.getMetaData();
					metaData = buildCustomizedMetaData(entities.get(i).getEntityName());
					FileGroupCollection entity = entities.get(i);
					job.addJob(entity.getEntityName(), entity, structures,
							metaData);
				}
			}
			job.saveJob(true);
			LOG.debug("Job created " + job.getStatus());
			addJob(job);
			manualDepositFrame.showMessage("Job Submitted",
					"Your job has been added to the job queue");
			resetScreen();
		} catch (FileNotFoundException ex) {
			manualDepositFrame.showError("Output directory not found",
					ERROR_OCCURRED, ex);
			reportException(ex);
		} catch (JobQueueException jex) {
			manualDepositFrame.showError("An error occurred in the job queue",
					ERROR_OCCURRED, jex);
			reportException(jex);
		}
	}

	public boolean jobsOutstanding() {
		return (jobQueueMgmt != null && jobQueueMgmt.jobsOutstanding());
	}

	public boolean jobsRunning() {
		return (jobQueueMgmt != null && jobQueueMgmt.jobsRunning());
	}

	static void threadMessage(String message) {
		String threadName = Thread.currentThread().getName();
		System.out.format("%s: %s%n", threadName, message);
	}

	private ArrayList<DefaultMutableTreeNode> cleanDuplicateFSOs(
			ArrayList<DefaultMutableTreeNode> nodes) {
		ArrayList<DefaultMutableTreeNode> retVal = new ArrayList<DefaultMutableTreeNode>();
		ArrayList<FileSystemObject> fsoList = new ArrayList<FileSystemObject>();
		for (DefaultMutableTreeNode nodeFromClipboard : nodes) {
			if (nodeFromClipboard.getUserObject() instanceof FileSystemObject) {
				FileSystemObject fso = (FileSystemObject) nodeFromClipboard
						.getUserObject();
				if (!fsoList.contains(fso)) {
					fsoList.add(fso);
					for (FileSystemObject fsoChild : fso.getAllChildren(false)) {
						fsoList.add(fsoChild);
					}
					retVal.add(nodeFromClipboard);
				}
			}
		}
		return retVal;
	}

	private boolean dropNodes(DefaultMutableTreeNode node,
			DepositTreeModel model) {
		boolean succeeded = false;
		try {
			ArrayList<DefaultMutableTreeNode> nodeInClipboard = getClipboardCopy();
			if (!entityRootSet) { // Has to be a drag from the file system to
				// the entity
				succeeded = true;
				for (DefaultMutableTreeNode nodeFromClipboard : nodeInClipboard) {
					if (nodeFromClipboard.getUserObject() instanceof FileSystemObject) {
						FileSystemObject fso = (FileSystemObject) nodeFromClipboard
								.getUserObject();
						if (fso.getIsFile()) {
							DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) nodeFromClipboard
									.getParent();
							theFsoRoot = (FileSystemObject) nodeParent
									.getUserObject();
							theFsoRootFile = fso;
						} else {
							theFsoRoot = fso;
							theFsoRootFile = null;
						}
						setRoot();
					}
				}
			} else {
				if (dragFromTreeType.equals(ETreeType.FileSystemTree)) {
					// Must have a FileSystemObject and dragging to entity tree
					nodeInClipboard = cleanDuplicateFSOs(nodeInClipboard);
					for (DefaultMutableTreeNode nodeFromClipboard : nodeInClipboard) {
						FileSystemObject fso = (FileSystemObject) nodeFromClipboard
								.getUserObject();
						fso.setSortBy(SortBy.UserArranged);
						if (node.getUserObject() instanceof FileSystemObject) {
							DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
									.getParent();
							FileSystemObject fsoDestination = (FileSystemObject) node
									.getUserObject();
							if (nodeParent.getUserObject() instanceof FileSystemObject) {
								FileSystemObject fsoParent = (FileSystemObject) nodeParent
										.getUserObject();
								fsoParent.getChildren().add(fso,
										fsoDestination.getSortOrder());
							} else {
								FileGroup entity = (FileGroup) nodeParent
										.getUserObject();
								entity.getChildren().add(fso,
										fsoDestination.getSortOrder());
							}
						} else {
							fso.setSortOrder(0);
							addFileToEntity(fso, (FileGroup) node
									.getUserObject());
						}
					}
					// Added 23/09/2013 by Ben. Ref: #996 - Jumbling issue
					sortFilesInEntity((FileGroup) node.getUserObject(), manualDepositFrame.getCurrentSortBy());
					dragFromFileSystemTree_startThread(getEntities(), theFsoRoot, true, false);
					
				} else if (dragFromTreeType.equals(ETreeType.EntityTree)) {
					if (model.getTreeType().equals(ETreeType.EntityTree)) {
						// Must have a FileSystemObject
						for (DefaultMutableTreeNode nodeFromClipboard : nodeInClipboard) {
							FileSystemObject fso = (FileSystemObject) nodeFromClipboard
									.getUserObject();
							DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) nodeFromClipboard
									.getParent();
							deleteEntityNode(nodeFromClipboard);
							if (node.getUserObject() instanceof FileSystemObject) {
								FileSystemObject fsoDestination = (FileSystemObject) node
										.getUserObject();
								if (fsoDestination.getIsFile()) {
									nodeParent = (DefaultMutableTreeNode) node
											.getParent();
									if (nodeParent.getUserObject() instanceof FileSystemObject) {
										FileSystemObject fsoParent = (FileSystemObject) nodeParent
												.getUserObject();
										fsoParent.getChildren().add(fso,
												fsoDestination.getSortOrder());
									} else {
										FileGroup entity = (FileGroup) nodeParent
												.getUserObject();
										entity.getChildren().add(fso,
												fsoDestination.getSortOrder());
									}
								} else {
									addFileToEntityFile(fso,
											(FileSystemObject) node
													.getUserObject());
								}
							} else {
								addFileToEntity(fso, (FileGroup) node
										.getUserObject());
							}
						}
						addIntellectualEntities(getEntities());
						updateWorkerProgress(100);
					} else {
						// Must have a FileSystemObject
						for (DefaultMutableTreeNode nodeFromClipboard : nodeInClipboard) {
							FileSystemObject fso = (FileSystemObject) nodeFromClipboard.getUserObject();
							// Must have a StructMap or a FileSystemObject parent
							if (node.getUserObject() instanceof StructMap) {
								StructMap map = (StructMap) node.getUserObject();
								if (map.getFiles() == null) {
									map.setFiles(new FSOCollection());
								}
								map.getFiles().add(fso);
								map.getFiles().setSortBy(manualDepositFrame.getCurrentSortBy());
								map.getFiles().reSortList();
								
								int nodeIndex = 0;
								for (FileSystemObject fso1 : map.getFiles()) {
									if (fso.equals(fso1)) {
										break; // Exit the FOR loop
									}
									nodeIndex++;
								}
								addFileToStructMap(fso, node, nodeIndex, false);
							} else {
								DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
										.getParent();
								StructMap mapParent = (StructMap) nodeParent
										.getUserObject();
								FileSystemObject fsoDestination = (FileSystemObject) node
										.getUserObject();
								mapParent.getFiles().add(fso,
										fsoDestination.getSortOrder());
								addStructMap(getStructures());
							}
						}
						// Reset the Struct Sort Order
						if (node.getUserObject() instanceof StructMap) {
							StructMap updatedStructMap = (StructMap) node.getUserObject();
							int structSortOrder = 0;
							for (FileSystemObject fsoUpdated : updatedStructMap.getFiles()) {
								fsoUpdated.setStructSortOrder(structSortOrder);
								structSortOrder++;
							}
						}
						else{
							DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
									.getParent();
							StructMap mapParent = (StructMap) nodeParent
									.getUserObject();
							int structSortOrder = 0;
							for (FileSystemObject fsoUpdated : mapParent.getFiles()) {
								fsoUpdated.setStructSortOrder(structSortOrder);
								structSortOrder++;
							}
						}
					}
				} else {
					// Can drag a FileSystemObject or a StructMap
					// drag source parent can only be a StructMap
					// drag target can be a StructMap or a FileSystemObject
					// if it's a FileSystemObject, we are re-ordering
					for (DefaultMutableTreeNode nodeFromClipboard : nodeInClipboard) {
						StructMap mapSourceParent = (StructMap) ((DefaultMutableTreeNode) nodeFromClipboard
								.getParent()).getUserObject();
						StructMap mapDestination;
						int position = 0;
						if (node.getUserObject() instanceof StructMap) {
							mapDestination = (StructMap) node.getUserObject();
						} else {
							FileSystemObject fsoChild = (FileSystemObject) node
									.getUserObject();
							position = fsoChild.getSortOrder();
							DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
									.getParent();
							mapDestination = (StructMap) nodeParent
									.getUserObject();
						}
						deleteStructMapNode(nodeFromClipboard);
						if (nodeFromClipboard.getUserObject() instanceof FileSystemObject) {
							FileSystemObject fso = (FileSystemObject) nodeFromClipboard
									.getUserObject();
							if (mapDestination.getFiles() == null) {
								mapDestination.setFiles(new FSOCollection());
							}
							mapDestination.getFiles().add(fso, position);
						} else {
							StructMap mapFrom = (StructMap) nodeFromClipboard
									.getUserObject();
							mapSourceParent.getChildren().remove(mapFrom);
							if (mapDestination.getChildren() == null) {
								mapDestination
										.setChildren(new StructMapCollection());
							}
							mapDestination.getChildren().add(mapFrom);
						}
					}
					addStructMap(getStructures());
				}
			}
		} catch (Exception ex) {
			reportException(ex);
		}
		return succeeded;
	}

	private static void reportException(Exception ex) {
		LOG.error(ex.getMessage(), ex);
		ex.printStackTrace();
	}

	public class TreeDragSource implements DragSourceListener,
			DragGestureListener, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8464280817263799553L;
		DragSource source;
		DragGestureRecognizer recognizer;
		TransferableTreeNode transferable;
		JTree sourceTree;

		public TreeDragSource(JTree tree, int actions) {
			sourceTree = tree;
			DepositTreeModel model = (DepositTreeModel) tree.getModel();
			dragFromTreeType = model.getTreeType();
			source = new DragSource();
			recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
					actions, this);
		}

		/*
		 * Drag Gesture Handler
		 */
		public void dragGestureRecognized(DragGestureEvent dge) {
			if (theNodeInClipboard.size() > 0) {
				TreePath path = new TreePath(theNodeInClipboard.get(0)
						.getPath());
				transferable = new TransferableTreeNode(path);
				try {
					source.startDrag(dge, DragSource.DefaultMoveNoDrop,
							transferable, this);
				} catch (Exception ex) {
				}
			}
		}

		/*
		 * Drag Event Handlers
		 */
		public void dragEnter(DragSourceDragEvent dsde) {
		}

		public void dragExit(DragSourceEvent dse) {
			dse.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
		}

		public void dragOver(DragSourceDragEvent dsde) {
			if (droppingIsAllowed) {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultMoveDrop);
				isCopying = false;
			} else {
				dsde.getDragSourceContext().setCursor(
						DragSource.DefaultCopyNoDrop);
			}
		}

		public void dropActionChanged(DragSourceDragEvent dsde) {
		}

		public void dragDropEnd(DragSourceDropEvent dsde) {
		}
	}

	public class TreeDropTarget implements DropTargetListener, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4008020000842600911L;
		DropTarget target;
		JTree targetTree;

		public TreeDropTarget(JTree tree) {
			targetTree = tree;
			target = new DropTarget(targetTree, this);
		}

		/**
		 * Drop Event Handlers
		 */
		private TreeNode getNodeForEvent(DropTargetDropEvent dtde) {
			Point p = dtde.getLocation();
			DropTargetContext dtc = dtde.getDropTargetContext();
			JTree tree = (JTree) dtc.getComponent();
			TreePath path = tree.getClosestPathForLocation(p.x, p.y);
			return (TreeNode) path.getLastPathComponent();
		}

		private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
			Point p = dtde.getLocation();
			DropTargetContext dtc = dtde.getDropTargetContext();
			JTree tree = (JTree) dtc.getComponent();
			TreePath path = tree.getClosestPathForLocation(p.x, p.y);
			return (TreeNode) path.getLastPathComponent();
		}

		public void dragEnter(DropTargetDragEvent dtde) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) getNodeForEvent(dtde);
			if (dropOK(node, targetTree)) {
				droppingIsAllowed = true;
				dtde.acceptDrag(dtde.getDropAction());
			} else {
				droppingIsAllowed = false;
				dtde.rejectDrag();
			}
		}

		public void dragOver(DropTargetDragEvent dtde) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) getNodeForEvent(dtde);
			if (dropOK(node, targetTree)) {
				droppingIsAllowed = true;
				dtde.acceptDrag(dtde.getDropAction());
			} else {
				droppingIsAllowed = false;
				dtde.rejectDrag();
			}
		}

		public void dragExit(DropTargetEvent dte) {
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		public void drop(DropTargetDropEvent dtde) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) getNodeForEvent(dtde);
			DepositTreeModel model = (DepositTreeModel) targetTree.getModel();
			if ((dropOK(node, targetTree)) && (dropNodes(node, model))) {
				dtde.acceptDrop(dtde.getDropAction());
				dtde.dropComplete(true);
			} else {
				dtde.rejectDrop();
			}
		}
	}

	public class MetaDataTableModelListener implements TableModelListener {
		public void tableChanged(TableModelEvent evt) {
			if (evt.getType() == TableModelEvent.UPDATE) {
				int column = evt.getColumn();
				int row = evt.getFirstRow();
				if ((column >= 0) && (row >= 0)) {
					theMetaDataTable.setColumnSelectionInterval(column, column);
					theMetaDataTable.setRowSelectionInterval(row, row);
					manualDepositFrame.checkButtons();
					IMetaDataTypeExtended theType = metaDataTableModel
							.getRow(row);
					if (theType.getAllowsMultipleRows()) {
						boolean hasBlankMultiple = false;
						for (int i = 0; i < metaDataTableModel.getRowCount(); i++) {
							IMetaDataTypeExtended testType = metaDataTableModel
									.getRow(i);
							if ((testType.getDataType().equals(theType
									.getDataType()))
									&& (testType.getDataFieldName()
											.equalsIgnoreCase(theType
													.getDataFieldName()))) {
								if (!testType.getIsSet()) {
									hasBlankMultiple = true;
									break;
								}
							}
						}
						if (!hasBlankMultiple) {
							IMetaDataTypeExtended newType = new MetaDataTypeImpl();
							newType.setAllowsMultipleRows(theType
									.getAllowsMultipleRows());
							newType.setDataFieldDescription(theType
									.getDataFieldDescription());
							newType
									.setDataFieldName(theType
											.getDataFieldName());
							newType.setDataType(theType.getDataType());
							newType.setIsCompulsory(theType.getIsCompulsory());
							newType
									.setIsSetBySystem(theType
											.getIsSetBySystem());
							newType.setListItems(theType.getListItems());
							newType.setSavedWithTemplate(theType
									.getSavedWithTemplate());
							newType.setWillBeUploaded(theType
									.getWillBeUploaded());
							metaDataTableModel.addRow(newType);
						}
					}
				}
			}
		}
	}

	public class JobQueueTableModelListener implements TableModelListener {
		public void tableChanged(TableModelEvent evt) {
			if (evt.getType() == TableModelEvent.UPDATE) {
				int column = evt.getColumn();
				int row = evt.getFirstRow();
				JTable tbl = (JTable) evt.getSource();
				tbl.setColumnSelectionInterval(column, column);
				tbl.setRowSelectionInterval(row, row);
				manualDepositFrame.checkButtons();
			}
		}
	}
}
