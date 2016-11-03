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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import nz.govt.natlib.ndha.common.FileUtils;
import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.common.ilsquery.CmsRecord;
import nz.govt.natlib.ndha.common.ilsquery.DcRecord;
import nz.govt.natlib.ndha.common.mets.FSOCollection;
import nz.govt.natlib.ndha.common.mets.FileGroup;
import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.StructMap;
import nz.govt.natlib.ndha.common.mets.StructMapCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.RepresentationTypes;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.SortBy;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadPresenter;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.IBulkUpload;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.CustomizeMetaDataPresenter;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.CustomizeMetaDataTableModel;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.ICustomizeMetaDataEditorView;
import nz.govt.natlib.ndha.manualdeposit.customui.DepositTreeModel;
import nz.govt.natlib.ndha.manualdeposit.customui.DepositTreeModel.ETreeType;
import nz.govt.natlib.ndha.manualdeposit.jobmanagement.UploadJob;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTableModel;
import nz.govt.natlib.ndha.manualdeposit.metadata.PersonalSettings;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * _presenter is the Presenter we are testing<br>
 * _theFrame is a mock screen that we use to test what the presenter is doing<br>
 * Every test needs to finish with assertFalse(_theFrame._cursorIsWaiting);<br>
 * This is to make sure the cursor is not set incorrectly<br>
 */
public class ManualDepositPresenterTest {

	private final static Log LOG = LogFactory
			.getLog(ManualDepositPresenterTest.class);
	private ManualDepositPresenter depositPresenter;
	private ManualDepositFrameTest theFrame;
	private LoginTest loginFrame;
	private LoginPresenterTest loginPresenter;
	private AppProperties applicationProperties;
	private boolean isVisible;
	private final static String RESOURCES_PATH = "./src/test/resources/";
	private final static String RESOURCES_OUTPUT_PATH = RESOURCES_PATH
			+ "Output/";
	private final static String RESOURCES_JOB_QUEUE_PATH = RESOURCES_PATH
			+ "JobQueue/";
	private final static String RESOURCES_INPUT_PATH = RESOURCES_PATH
			+ "Input/";
	private final static String RESOURCES_INPUT_PATH_NAMED = RESOURCES_INPUT_PATH
			+ "NamedStuff/";
	private final static String RESOURCES_INPUT_MANUAL = RESOURCES_INPUT_PATH
			+ "ManuallyConfigured/";
	private final static String RESOURCES_INPUT_MULTIPLE = RESOURCES_INPUT_PATH
			+ "MultipleFileLoad/";
	private final static String RESOURCES_INPUT_SOUND = RESOURCES_INPUT_PATH
			+ "Sound/";
	private final static String RESOURCES_SETTINGS_PATH = RESOURCES_PATH
			+ "Settings/";
	private final static String FORM_CONTROL_NAME = "ManualDepositPresenterTest";
	private final static String FAVOURITES_PATH = RESOURCES_SETTINGS_PATH
			+ "PersonalSettings.xml";
	private final static String LOGIN_NAME = "mngroot";
	private final static String LOGIN_PASSWORD = "mngroot";

	public class BulkUploadTest implements IBulkUpload {

		protected boolean cursorIsWaiting = false;

		public void setPresenter(BulkUploadPresenter presenter) {

		}

		public void closeForm() {

		}

		public void setStatus(String status) {

		}

		public void setMaxProgress(int max) {

		}

		public void setCurrentProgress(int current) {

		}

		public void setProgressVisible(boolean isVisible) {

		}

		public void showGlassPane(boolean show) {

		}

		public void chkButtons() {

		}

		public void showView() {
			isVisible = true;
		}

		public void setCanClose(boolean canClose) {

		}

		public void setWaitCursor(boolean isWaiting) {
			cursorIsWaiting = isWaiting;
		}

		public void showError(String header, String message) {
			showError(header, message, null);
		}

		public void showError(String header, String message, Exception ex) {
			LOG.debug("Error occurred " + header + ", " + message, ex);
			fail();
		}

		public void showMessage(String header, String message) {
			LOG.debug("show Message " + header + ", " + message);
		}

		public boolean confirm(String message) {
			return true;
		}

		public boolean confirm(String message, boolean useYesNo) {
			return true;
		}

		public void setFormFont(Font theFont) {

		}
	}
	
	public class CustomizeMetaDataFormTest implements ICustomizeMetaDataEditorView{
		
		protected boolean cursorIsWaiting = false;

		@Override
		public void showView() {
			isVisible = true;
		}

		@Override
		public void setFormFont(Font theFont) {
		}

		@Override
		public void setPresenter(CustomizeMetaDataPresenter thePresenter) {

		}

		@Override
		public void setVisible(boolean isVisible) {
			
		}

		@Override
		public void checkButtons() {
			
		}

		@Override
		public void showGlassPane(boolean show) {
			
		}

		@Override
		public void showError(String header, String message) {
			showError(header, message, null);			
		}

		@Override
		public void showError(String header, String message, Exception ex) {
			LOG.debug("Error occurred " + header + ", " + message, ex);
			fail();
		}

		@Override
		public void showMessage(String header, String message) {
			LOG.debug("show Message " + header + ", " + message);
		}

		@Override
		public void setWaitCursor(boolean isWaiting) {
			cursorIsWaiting = isWaiting;			
		}

		@Override
		public boolean confirm(String message) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean confirm(String message, boolean useYesNo) {
			return true;
		}

		@Override
		public void setCanClose(boolean canClose) {
			
		}

		@Override
		public Map<String, Object> getTableData() {
			return null;
		}

		@Override
		public void setTableDataBlank(ArrayList<FileGroupCollection> entities,
				List<String> metaDataList) {
			
		}

		@Override
		public void setTableData(ArrayList<FileGroupCollection> entities,
				List<String> metaDataList,
				CustomizeMetaDataTableModel metaDataValues) {
			
		}
		
	}

	public class ManualDepositFrameTest implements IManualDepositMainFrame {

		protected JTree treeFileSystem = new JTree();
		protected JTree treeEntities = new JTree();
		protected JTree treeStructMap = new JTree();
		protected JTable tblDetail = new JTable();
		protected JTable tblJobQueueRunning = new JTable();
		protected JTable tblJobQueuePending = new JTable();
		protected JTable tblJobQueueFailed = new JTable();
		protected JTable tblJobQueueDeposited = new JTable();
		protected JTable tblJobQueueComplete = new JTable();
		protected JComboBox cmbSelectTemplate = new JComboBox();
		protected JComboBox cmbSelectStructTemplate = new JComboBox();
		protected JComboBox cmbSortBy = new JComboBox();
		protected JComboBox cmbFixityTypes = new JComboBox();
		protected FormControl frmControl = null;
		protected JMenu mnuFileFavourites = new JMenu();
		protected JList lstProducers = new JList();
		protected JList lstMaterialFlows = new JList();
		protected boolean cursorIsWaiting = false;
		private ManualDepositPresenter localPresenter;
		private String currentPath = null;
		private String inputResult = "Rubbish Message";
		private boolean missingFiles = false;

		public void setPresenter(ManualDepositPresenter thePresenter) {
			localPresenter = thePresenter;
		}

		public javax.swing.JFrame getComponent() {
			return null;
		}

		public void setCurrentDirectory(String currentDirectory) {
			currentPath = currentDirectory;
		}

		public void showMissingFiles(String settingsPath,
				List<FileSystemObject> filesMissing) {
			missingFiles = true;
		}

		public boolean getMissingFiles() {
			return missingFiles;
		}

		public void setMissingFiles(boolean value) {
			missingFiles = value;
		}

		public IBulkUpload createBulkUploadForm() {
			BulkUploadTest bulkTest = new BulkUploadTest();
			return bulkTest;
		}

		public void setupScreen(AppProperties appProperties, String settingsPath)
				throws Exception {
			if (applicationProperties == null) {
				try {
					applicationProperties = new AppProperties();
				} catch (Exception ex) {
					fail();
				}
			}
			PersonalSettings personalSettings = applicationProperties
					.getApplicationData().getPersonalSettings();
			personalSettings.setCurrentPath(null);
			/*
			 * _includeCMS2Search = includeCMS2Search; _includeCMS1Search
			 * = includeCMS1Search; _includeNoCMSOption = includeNoCMSOption;
			 */
			try {
				frmControl = new FormControl(FORM_CONTROL_NAME,
						applicationProperties.getApplicationData()
								.getSettingsPath());
			} catch (Exception ex) {
				LOG.error("Error loading form parameters", ex);
			}
			localPresenter.addHandlers(treeFileSystem, treeEntities,
					treeStructMap, cmbSelectTemplate, cmbSelectStructTemplate,
					cmbSortBy, cmbFixityTypes, tblDetail, tblJobQueueRunning,
					tblJobQueuePending, tblJobQueueFailed,
					tblJobQueueDeposited, tblJobQueueComplete,
					mnuFileFavourites, lstProducers, lstMaterialFlows);
		}

		public void showView() {
			isVisible = true;
		}

		public void setWaitCursor(boolean isWaiting) {
			cursorIsWaiting = isWaiting;
		}

		public void checkButtons() {
			// Don't need to do anything here.
		}

		public void storeCurrentPath(String path) {
			currentPath = path;
		}

		public String getCurrentPath() {
			return currentPath;
		}

		public void setSearchType(MetaDataFields.ECMSSystem cmsSystem) {

		}

		public MetaDataTableModel getMetaDataTableModel() {
			return (MetaDataTableModel) tblDetail.getModel();
		}

		public void showError(String header, String message) {
			showError(header, message, null);
		}

		public void showError(String header, String message, Exception ex) {
			LOG.debug("Error occurred " + header + ", " + message, ex);
			fail();
		}

		public void showMessage(String header, String message) {
			LOG.debug("show Message " + header + ", " + message);
		}

		public boolean confirm(String message) {
			return true;
		}

		public boolean confirm(String message, boolean useYesNo) {
			return true;
		}

		public void setInputResult(String value) {
			inputResult = value;
		}

		public String getInput(String header, String message) {
			return inputResult;
		}

		public String getInput(String header, String message,
				String defaultInput) {
			return "Rubbish message";
		}

		public void setFormFont(Font theFont) {

		}

		public SortBy getCurrentSortBy() {
			if (cmbSortBy.getSelectedItem() == null) {
				return SortBy.FileName;
			} else {
				return (SortBy) cmbSortBy.getSelectedItem();
			}
		}

		public ICustomizeMetaDataEditorView createCustomizeMetaDataForm() {
			CustomizeMetaDataFormTest customizeMetaDataTest = new CustomizeMetaDataFormTest();
			return customizeMetaDataTest;
		}

		public void setIELabel(boolean submitOK) {
		}

		public void setProgressLevel(int percentage) {
		}

		public void setProgressBarVisible(boolean isVisible) {
		}

		@Override
		public void showDuplicateFiles(String settingsPath,
				Set<String> duplicateFiles) {
			// TODO Auto-generated method stub
			
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File outputDir = new File(RESOURCES_OUTPUT_PATH);
		outputDir.mkdir();
		
		File jobQueueDir = new File(RESOURCES_JOB_QUEUE_PATH);
		jobQueueDir.mkdir();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File outputFile = new File(RESOURCES_OUTPUT_PATH);
		String[] children = outputFile.list();
		for (String forDeletion : children) {
			if (!forDeletion.contains(".svn")) {
				FileUtils.deleteFileOrDirectoryRecursive(RESOURCES_OUTPUT_PATH
						+ forDeletion);
			}
		}
		FileUtils.deleteFileOrDirectoryRecursive(RESOURCES_OUTPUT_PATH
				+ "TestNewJobQueue.xml");
		FileUtils.deleteFileOrDirectoryRecursive(RESOURCES_OUTPUT_PATH
				+ "NamedStuff.xml");
		FileUtils.deleteFileOrDirectoryRecursive(RESOURCES_OUTPUT_PATH
				+ "streams");
		File jobQueue = new File(RESOURCES_JOB_QUEUE_PATH);
		children = jobQueue.list();
		for (String forDeletion : children) {
			if (!forDeletion.contains(".svn")) {
				FileUtils
						.deleteFileOrDirectoryRecursive(RESOURCES_JOB_QUEUE_PATH
								+ forDeletion);
			}
		}
		/*
		 * FileUtils.deleteFileOrDirectoryRecursive(_resourcesJobQueuePath +
		 * "NamedStuff.xml");
		 * FileUtils.deleteFileOrDirectoryRecursive(_resourcesJobQueuePath +
		 * "TestNewJobQueue/");
		 * FileUtils.deleteFileOrDirectoryRecursive(_resourcesJobQueuePath +
		 * "NamedStuff/");
		 * FileUtils.deleteFileOrDirectoryRecursive(_resourcesJobQueuePath +
		 * "TestNewJobQueue.xml");
		 */
		FileUtils.deleteFileOrDirectoryRecursive(FAVOURITES_PATH);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		theFrame = new ManualDepositFrameTest();
		loginFrame = new LoginTest();
		loginPresenter = new LoginPresenterTest();
		try {
			applicationProperties = new AppProperties();
		} catch (Exception ex) {
			fail();
		}
		applicationProperties.setSysProxyUser(LOGIN_NAME);
		applicationProperties.setSysProxyPassword(LOGIN_PASSWORD);
		depositPresenter = new ManualDepositPresenter(theFrame, loginFrame,
				loginPresenter, applicationProperties);
		theFrame.setPresenter(depositPresenter);
		try {
			depositPresenter.setupScreen();
			loginPresenter.login(LOGIN_NAME, LOGIN_PASSWORD);
		} catch (Exception ex) {
			fail();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testFormUtilities() {
		JFrame frame = new JFrame();
		Font theFont = new Font("Arial", Font.BOLD, 14);
		javax.swing.JMenuBar mnuMain = new JMenuBar();
		JMenu menu1 = new JMenu();
		JMenuItem menu1_1 = new JMenuItem();
		JMenuItem menu1_2 = new JMenuItem();
		JMenuItem menu1_3 = new JMenuItem();
		menu1.add(menu1_1);
		menu1.add(menu1_2);
		menu1.add(menu1_3);
		JMenu menu2 = new JMenu();
		JMenuItem menu2_1 = new JMenuItem();
		JMenuItem menu2_2 = new JMenuItem();
		JMenuItem menu2_3 = new JMenuItem();
		menu1.add(menu2_1);
		menu1.add(menu2_2);
		menu1.add(menu2_3);
		JMenu menu3 = new JMenu();
		JMenuItem menu3_1 = new JMenuItem();
		JMenuItem menu3_2 = new JMenuItem();
		JMenuItem menu3_3 = new JMenuItem();
		menu1.add(menu3_1);
		menu1.add(menu3_2);
		menu1.add(menu3_3);
		frame.setJMenuBar(mnuMain);
		mnuMain.add(menu1);
		mnuMain.add(menu2);
		mnuMain.add(menu3);

		JPanel pnlTest = new JPanel();
		pnlTest.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Test Border"));
		frame.add(pnlTest);
		JLabel label1 = new JLabel();
		JTextField text1 = new JTextField();
		pnlTest.add(label1);
		pnlTest.add(text1);
		JLabel label2 = new JLabel();
		JTextField text2 = new JTextField();
		frame.add(label2);
		frame.add(text2);

		assertFalse(menu1.getFont().equals(theFont));
		assertFalse(menu2.getFont().equals(theFont));
		assertFalse(menu3.getFont().equals(theFont));
		assertFalse(menu1_1.getFont().equals(theFont));
		assertFalse(menu1_2.getFont().equals(theFont));
		assertFalse(menu1_3.getFont().equals(theFont));
		assertFalse(menu2_1.getFont().equals(theFont));
		assertFalse(menu2_2.getFont().equals(theFont));
		assertFalse(menu2_3.getFont().equals(theFont));
		assertFalse(menu3_1.getFont().equals(theFont));
		assertFalse(menu3_2.getFont().equals(theFont));
		assertFalse(menu3_3.getFont().equals(theFont));
		assertFalse(pnlTest.getFont().equals(theFont));
		assertFalse(label1.getFont().equals(theFont));
		assertFalse(label2.getFont().equals(theFont));
		assertFalse(text1.getFont().equals(theFont));
		assertFalse(text2.getFont().equals(theFont));

		FormUtilities.setFormFont(frame, theFont);

		assertTrue(menu1.getFont().equals(theFont));
		assertTrue(menu2.getFont().equals(theFont));
		assertTrue(menu3.getFont().equals(theFont));
		assertTrue(menu1_1.getFont().equals(theFont));
		assertTrue(menu1_2.getFont().equals(theFont));
		assertTrue(menu1_3.getFont().equals(theFont));
		assertTrue(menu2_1.getFont().equals(theFont));
		assertTrue(menu2_2.getFont().equals(theFont));
		assertTrue(menu2_3.getFont().equals(theFont));
		assertTrue(menu3_1.getFont().equals(theFont));
		assertTrue(menu3_2.getFont().equals(theFont));
		assertTrue(menu3_3.getFont().equals(theFont));
		assertTrue(pnlTest.getFont().equals(theFont));
		assertTrue(label1.getFont().equals(theFont));
		assertTrue(label2.getFont().equals(theFont));
		assertTrue(text1.getFont().equals(theFont));
		assertTrue(text2.getFont().equals(theFont));
	}

	@Test
	public final void testSetup() {
		isVisible = false;
		/*
		 * _includeCMS2Search = false; _includeCMS1Search = false;
		 * _includeNoCMSOption = false;
		 */
		try {
			depositPresenter.setupScreen();
			loginPresenter.login(LOGIN_NAME, LOGIN_PASSWORD);
		} catch (Exception ex) {
			fail();
		}
		assertTrue(isVisible);
		/*
		 * assertTrue(_includeCMS2Search); assertTrue(_includeCMS1Search);
		 * assertTrue(_includeNoCMSOption);
		 */

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		File test = new File(RESOURCES_INPUT_PATH);
		FileSystemObject fso = FileSystemObject.create("Input", test, null);
		rootNode.setUserObject(fso);
		DepositTreeModel model = new DepositTreeModel(rootNode,
				ETreeType.EntityTree);
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) theFrame.treeFileSystem
				.getLastSelectedPathComponent();
		assertTrue(selectedNode == null);
		theFrame.treeFileSystem.setModel(model);
		// _presenter.selectIEFile(fso);
		depositPresenter.selectNode(fso, ETreeType.EntityTree);
		selectedNode = (DefaultMutableTreeNode) theFrame.treeFileSystem
				.getLastSelectedPathComponent();
		assertTrue(selectedNode.equals(rootNode));
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testResetScreen() {
		depositPresenter.resetScreen();
		// ArrayList<FileGroupCollection> files = _presenter.getEntities();
		assertTrue(depositPresenter.getRootFileName().trim().equalsIgnoreCase(
				"My Computer"));
		assertTrue(depositPresenter.getEntities() == null
				|| depositPresenter.getEntities().size() == 0);
		assertTrue(depositPresenter.getStructures() == null
				|| depositPresenter.getStructures().size() == 0);
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testGetFileSystemMenu() {
		// Includes a test for setting the root entity

		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Nothing");
		JPopupMenu menu = depositPresenter.getFileSystemMenu(node);
		// menu should be null if the user object isn't a FileSystemObject
		assertTrue(menu == null);

		File theFile = new File(RESOURCES_INPUT_SOUND);
		String description = "ManualDeposit";
		FileSystemObject root = new FileSystemObject(description, theFile, null);
		node = new DefaultMutableTreeNode(root);
		AppProperties props = depositPresenter.getAppProperties();
		UserGroupData userGroupData = null;
		try {
			userGroupData = props.getUserData()
					.getUser(props.getLoggedOnUser()).getUserGroupData();
		} catch (Exception ex) {
			fail();
		}
		userGroupData.setIncludeMultiEntityMenuItem(false);
		menu = depositPresenter.getFileSystemMenu(node);
		assertTrue(menu != null);
		int menuCount = menu.getComponentCount();
		assertTrue(menuCount >= 1);
		try {
			depositPresenter.setupScreen();
		} catch (Exception ex) {
			fail();
		}
		userGroupData.setIncludeMultiEntityMenuItem(true);
		menu = depositPresenter.getFileSystemMenu(node);
		assertTrue(menu != null && menu.getComponentCount() == menuCount + 2);

		try {
			depositPresenter.setupScreen();
		} catch (Exception ex) {
			fail();
		}
		theFile = new File(RESOURCES_INPUT_SOUND
				+ "/Preservation Copy/OHC-0000_s_1.wav");
		description = "OHC-0000_s_1.wav";
		FileSystemObject newRoot = new FileSystemObject(description, theFile,
				null);
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(newRoot);
		node.add(childNode);
		userGroupData.setIncludeMultiEntityMenuItem(false);
		menu = depositPresenter.getFileSystemMenu(childNode);
		assertTrue(menu != null);
		menuCount = menu.getComponentCount();
		assertTrue(menuCount >= 1);
		try {
			depositPresenter.setupScreen();
		} catch (Exception ex) {
			fail();
		}
		userGroupData.setIncludeMultiEntityMenuItem(true);
		menu = depositPresenter.getFileSystemMenu(childNode);
		assertTrue(menu != null && menu.getComponentCount() == menuCount);

		root = setRoot();
		// assertTrue(_presenter _theFrame._rootNode.equals(root));
		assertFalse(theFrame.cursorIsWaiting);
		// node = new DefaultMutableTreeNode(root);
		// menu = _presenter.getFileSystemMenu(node);
		// menu should have at least one item
		// When the menu is fixed, implement this check
		// assertTrue(menu != null && menu.getComponentCount() >= 1);
		assertFalse(theFrame.cursorIsWaiting);
	}

	private final FileSystemObject setRoot() {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();
		String description = "NamedStuff";
		File theFile = new File(RESOURCES_INPUT_PATH_NAMED);
		FSOCollection theChildren = null;
		FileSystemObject root = new FileSystemObject(description, theFile,
				theChildren);
		node.setUserObject(root);
		depositPresenter.setEntityRoot(node);
		return root;
	}

	@Test
	public final void testFileIsInEntity() {
		try {
			depositPresenter.setupScreen();
		} catch (Exception ex) {
			fail();
		}
		FileSystemObject root = setRoot();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		// Before we add files to the entity we shouldn't be able to find it
		boolean fileIsThere = depositPresenter.fileIsInEntity(root
				.getChildren().get(0));
		assertFalse(fileIsThere);
		FileGroup entity = FileGroup.create("Test Entity", "",
				RepresentationTypes.DigitalOriginal, root.getChildren());
		// FileGroup entity = new FileGroup("Test Entity",
		// RepresentationTypes.DigitalOriginal, root.getChildren());
		try {
			Thread.sleep(250);
			depositPresenter.getEntities().get(0).add(entity);
		} catch (Exception ex) {
			fail();
		}
		// Now we have added files we should be able to find it
		fileIsThere = depositPresenter
				.fileIsInEntity(root.getChildren().get(0));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		assertTrue(fileIsThere);
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testFileIsInStructMap() {
		FileSystemObject root = setRoot();
		FileGroup entity = FileGroup.create("Test Entity", "",
				RepresentationTypes.DigitalOriginal, root.getChildren());
		// FileGroup entity = new FileGroup("Test Entity",
		// RepresentationTypes.DigitalOriginal, root.getChildren());
		try {
			Thread.sleep(500);
			depositPresenter.getEntities().get(0).add(entity);
		} catch (Exception ex) {
			fail();
		}
		StructMap map = new StructMap("Map", null, root.getAllChildren(true), 0);
		StructMapCollection maps = new StructMapCollection();
		maps.add(map);

		// Before we add files to the map we shouldn't be able to find it
		int i = 0;
		FileSystemObject fsoTest = root.getAllChildren(true).get(i);
		while (fsoTest.getIsChecksumDigestFile("Mini Monkey", null)
				|| fsoTest.getDescription().contains(".svn")) {
			i++;
			fsoTest = root.getAllChildren(true).get(i);
		}
		depositPresenter.addStructMap(null);
		boolean fileIsThere = depositPresenter.fileIsInStructMap(fsoTest);
		assertFalse(fileIsThere);
		depositPresenter.addStructMap(maps);
		// Now we have added files we should be able to find it
		fileIsThere = depositPresenter.fileIsInStructMap(fsoTest);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		assertTrue(fileIsThere);
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testGetEntityMenu() {
		String entityName = "Nothing";
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(entityName);
		JPopupMenu menu = depositPresenter.getEntityMenu(node);
		// menu should be null if the entity root hasn't been set
		assertTrue(menu == null);

		FileSystemObject root = setRoot();
		FileGroupCollection groups = new FileGroupCollection(entityName, root
				.getFullPath());
		FileGroup group = FileGroup.create(entityName, "",
				RepresentationTypes.DigitalOriginal, root.getChildren());
		// FileGroup group = new FileGroup(entityName,
		// RepresentationTypes.DigitalOriginal, root.getChildren());
		try {
			groups.add(group);
		} catch (Exception ex) {
			fail();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		node = new DefaultMutableTreeNode(groups);
		menu = depositPresenter.getEntityMenu(node);
		// Should have at least two items - "Rename Intellectual Entity" &
		// "Add Entity"
		// Text may change so only check that they're there
		assertTrue(menu != null && menu.getComponentCount() > 1);

		FileGroup entity = FileGroup.create("Test Entity", "",
				RepresentationTypes.DigitalOriginal, root.getChildren());
		// FileGroup entity = new FileGroup("Test Entity",
		// RepresentationTypes.DigitalOriginal, root.getChildren());
		try {
			depositPresenter.getEntities().get(0).add(entity);
		} catch (Exception ex) {
			fail();
		}
		node = new DefaultMutableTreeNode(entity);
		menu = depositPresenter.getEntityMenu(node);
		// Should have at one item - "Delete Entity"
		// "Rename Entity" has been deleted
		// Text may change so only check that they're there
		// assertTrue(menu != null && menu.getComponentCount() > 1);
		assertTrue(menu != null && menu.getComponentCount() == 1);

		FileSystemObject fso = root.getChildren().get(0);
		node = new DefaultMutableTreeNode(fso);
		menu = depositPresenter.getEntityMenu(node);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		// Should have at least two items - "Delete" & "Rename"
		// Applies to both file & directory, though the text is different for
		// each
		assertTrue(menu != null && menu.getComponentCount() > 1);
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testGetStructMapMenu() {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Nothing");
		JPopupMenu menu = depositPresenter.getStructMapMenu(node);
		// menu should be null if the entity root hasn't been set
		assertTrue(menu == null);

		FileSystemObject root = setRoot();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		menu = depositPresenter.getStructMapMenu(node);
		// Should have at one item - "Add Structure Item"
		// Text may change so only check that it's there
		assertTrue(menu != null && menu.getComponentCount() == 1);

		StructMap map = new StructMap("Test Entity", null, root.getChildren(),
				0);
		// _theFrame.getStructures().add(map);
		node = new DefaultMutableTreeNode(map);
		menu = depositPresenter.getStructMapMenu(node);
		// Should have at least 3 items - "Delete" & "Rename" & "Add"
		// Text may change so only check that they're there
		assertTrue(menu != null && menu.getComponentCount() > 2);

		FileSystemObject fso = root.getChildren().get(0);
		node = new DefaultMutableTreeNode(fso);
		menu = depositPresenter.getStructMapMenu(node);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		// Should have one item - "Delete"
		// Text may change so only check that it's there
		assertTrue(menu != null && menu.getComponentCount() == 1);
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testAddEntityModelListener() {
		// TODO write the test
		// May be too hard to test - leave for now
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testAddStructMapModelListener() {
		// TODO write the test
		// May be too hard to test - leave for now
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testAddMetaDataTableModelAndHandlers() {
		// Includes loadMetadata test
		try {
			depositPresenter.setupScreen();
			// _presenter.addMetaDataTableModelAndHandlers(_theFrame.tblDetail);
		} catch (Exception ex) {
			fail();
		}
		MetaDataTableModel model = (MetaDataTableModel) theFrame.tblDetail
				.getModel();
		assertTrue(model.getRowCount() == 8);
		for (int i = 0; i < model.getRowCount(); i++) {
			IMetaDataTypeExtended theType = model.getRow(i);
			try {
				theType.setDataFieldValue(theType.getDataFieldName());
			} catch (Exception ex) {
				fail();
			}
			assertFalse(model.isCellEditable(i, 0));
			assertFalse(model.isCellEditable(i, 1) == theType
					.getIsSetBySystem());
			assertTrue(model.getValueAt(i, 0).equals(
					theType.getDataFieldDescription()));
			assertTrue(model.getValueAt(i, 1).equals(
					theType.getDataFieldValue()));
			assertTrue(model.getValueAt(i, 1)
					.equals(theType.getDataFieldName()));
			model.setValueAt("Spidey roolz", i, 1);
			assertTrue(theType.getDataFieldValue().equals("Spidey roolz"));
		}
		setRoot();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		assertTrue(depositPresenter.canSubmit());
		for (int i = 0; i < model.getRowCount(); i++) {
			IMetaDataTypeExtended theType = model.getRow(i);
			try {
				theType.setDataFieldValue("");
			} catch (Exception ex) {
				fail();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		assertFalse(depositPresenter.canSubmit());
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testAddJobQueueTableModelAndHandlers() {
		// TODO write the test
		// May be too hard to test - leave for now
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testTemplates() {
		try {
			depositPresenter.setupScreen();
			// _presenter.addMetaDataTableModelAndHandlers(_theFrame.tblDetail);
			CmsRecord rec = new DcRecord("");
			depositPresenter.setCMSResults(rec,
					MetaDataFields.ECMSSystem.CMS2);
			MetaDataTableModel model = (MetaDataTableModel) theFrame.tblDetail
					.getModel();
			// _presenter.addTemplateHandlers(_theFrame.cmbSelectTemplate,
			// _theFrame.cmbSelectStructTemplate);
			String testTemplate = "Gobbledegook test template";
			for (int i = 0; i < model.getRowCount(); i++) {
				IMetaDataTypeExtended theType = model.getRow(i);
				theType.setDataFieldValue(theType.getDataFieldName());
			}
			theFrame.cmbSelectTemplate.addItem(testTemplate);
			theFrame.cmbSelectTemplate.setSelectedItem(testTemplate);
			assertTrue(depositPresenter.canSaveTemplate());
			assertFalse(depositPresenter.canDeleteTemplate());
			theFrame.cmbSelectTemplate.removeItem(testTemplate);
			// _presenter.deleteTemplate();
			// _theFrame.cmbSelectTemplate.addItem(testTemplate);
			// _theFrame.cmbSelectTemplate.setSelectedItem(testTemplate);
			// assertTrue(_presenter.canSaveTemplate());
			// assertFalse(_presenter.canDeleteTemplate());
			String templateName = "Test Template";
			depositPresenter.saveTemplate(templateName);
			theFrame.cmbSelectTemplate.setSelectedItem(templateName);
			assertTrue(depositPresenter.canSaveTemplate());
			assertTrue(depositPresenter.canDeleteTemplate());
			for (int i = 0; i < model.getRowCount(); i++) {
				IMetaDataTypeExtended theType = model.getRow(i);
				theType.setDataFieldValue("");
			}
			depositPresenter.loadTemplate();
			for (int i = 0; i < model.getRowCount(); i++) {
				IMetaDataTypeExtended theType = model.getRow(i);
				theType.setDataFieldValue(theType.getDataFieldName());
				assertTrue(theType.getDataFieldName().equals(
						theType.getDataFieldValue()));
			}
			depositPresenter.deleteTemplate();
			templateName = "TestTemplate";
			File testFile = new File(RESOURCES_PATH + "Templates/MetaData/"
					+ templateName + ".xml");
			if (testFile.exists()) {
				testFile.delete();
			}
			for (int i = 0; i < model.getRowCount(); i++) {
				IMetaDataTypeExtended theType = model.getRow(i);
				theType.setDataFieldValue(theType.getDataFieldName());
			}
			assertTrue(depositPresenter.canSaveSharedTemplate());
			depositPresenter.saveSharedTemplate(templateName);
			theFrame.cmbSelectTemplate
					.setSelectedIndex(theFrame.cmbSelectTemplate.getItemCount() - 1);
			assertTrue(depositPresenter.canDeleteTemplate());
			assertTrue(testFile.exists());
			depositPresenter.deleteTemplate();
			assertFalse(testFile.exists());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(theFrame.cursorIsWaiting);
	}

	private FileGroup getDO() {
		// Digital Original
		FSOCollection mainFSO = FSOCollection.create();
		// Chapter 3
		FSOCollection theChildren = FSOCollection.create();
		File childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter3/test_87_archive_7_P_7.jpg");
		FileSystemObject fso = FileSystemObject.create(
				"P_7_test_87_archive_7.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter3/test_87_archive_8_P_8.jpg");
		fso = FileSystemObject.create("P_8_test_87_archive_8.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter3");
		fso = FileSystemObject.create("Chapter3", childFile, theChildren);
		mainFSO.add(fso);
		// Chapter 2
		theChildren = FSOCollection.create();
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter2/test_87_archive_6_P_6.jpg");
		fso = FileSystemObject.create("P_6_test_87_archive_6.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter2/test_87_archive_5_P_5.jpg");
		fso = FileSystemObject.create("P_5_test_87_archive_5.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter2/test_87_archive_4_P_4.jpg");
		fso = FileSystemObject.create("P_4_test_87_archive_4.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter2");
		fso = FileSystemObject.create("Chapter2", childFile, theChildren);
		mainFSO.add(fso);
		// Chapter 1
		theChildren = FSOCollection.create();
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter1/test_87_archive_3_P_3.jpg");
		fso = FileSystemObject.create("P_3_test_87_archive_3.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter1/test_87_archive_2_P_2.jpg");
		fso = FileSystemObject.create("P_2_test_87_archive_2.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter1/test_87_archive_1_P_1.jpg");
		fso = FileSystemObject.create("P_1_test_87_archive_1.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/DO_Originals/Chapter1");
		fso = FileSystemObject.create("Chapter1", childFile, theChildren);
		mainFSO.add(fso);
		FileGroup group = FileGroup.create("Digital Original", "DO",
				RepresentationTypes.DigitalOriginal, mainFSO);
		return group;
	}

	private FileGroup getAC1() {
		// Digital Original
		FSOCollection mainFSO = FSOCollection.create();
		// Chapter 3
		FSOCollection theChildren = FSOCollection.create();
		File childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter3/test_87_view_7_P_7.jpg");
		FileSystemObject fso = FileSystemObject.create(
				"P_7_test_87_view_7.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter3/test_87_view_8_P_8.jpg");
		fso = FileSystemObject
				.create("P_8_test_87_view_8.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter3");
		fso = FileSystemObject.create("Chapter3", childFile, theChildren);
		mainFSO.add(fso);
		// Chapter 2
		theChildren = FSOCollection.create();
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter2/test_87_view_6_P_6.jpg");
		fso = FileSystemObject
				.create("P_6_test_87_view_6.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter2/test_87_view_5_P_5.jpg");
		fso = FileSystemObject
				.create("P_5_test_87_view_5.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter2/test_87_view_4_P_4.jpg");
		fso = FileSystemObject
				.create("P_4_test_87_view_4.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter2");
		fso = FileSystemObject.create("Chapter2", childFile, theChildren);
		mainFSO.add(fso);
		// Chapter 1
		theChildren = FSOCollection.create();
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter1/test_87_view_3_P_3.jpg");
		fso = FileSystemObject
				.create("P_3_test_87_view_3.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter1/test_87_view_2_P_2.jpg");
		fso = FileSystemObject
				.create("P_2_test_87_view_2.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter1/test_87_view_1_P_1.jpg");
		fso = FileSystemObject
				.create("P_1_test_87_view_1.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_ Display/Chapter1");
		fso = FileSystemObject.create("Chapter1", childFile, theChildren);
		mainFSO.add(fso);
		FileGroup group = FileGroup.create("Access Copy", "AC1",
				RepresentationTypes.AccessCopy, mainFSO);
		return group;
	}

	private FileGroup getAC2() {
		// Digital Original
		FSOCollection mainFSO = FSOCollection.create();
		// Chapter 3
		FSOCollection theChildren = FSOCollection.create();
		File childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter3/test_87_thumb_7_P_7.jpg");
		FileSystemObject fso = FileSystemObject.create(
				"P_7_test_87_thumb_7.jpg", childFile, null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter3/test_87_thumb_8_P_8.jpg");
		fso = FileSystemObject.create("P_8_test_87_thumb_8.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter3");
		fso = FileSystemObject.create("Chapter3", childFile, theChildren);
		mainFSO.add(fso);
		// Chapter 2
		theChildren = FSOCollection.create();
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter2/test_87_thumb_6_P_6.jpg");
		fso = FileSystemObject.create("P_6_test_87_thumb_6.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter2/test_87_thumb_5_P_5.jpg");
		fso = FileSystemObject.create("P_5_test_87_thumb_5.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter2/test_87_thumb_4_P_4.jpg");
		fso = FileSystemObject.create("P_4_test_87_thumb_4.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter2");
		fso = FileSystemObject.create("Chapter2", childFile, theChildren);
		mainFSO.add(fso);
		// Chapter 1
		theChildren = FSOCollection.create();
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter1/test_87_thumb_3_P_3.jpg");
		fso = FileSystemObject.create("P_3_test_87_thumb_3.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter1/test_87_thumb_2_P_2.jpg");
		fso = FileSystemObject.create("P_2_test_87_thumb_2.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter1/test_87_thumb_1_P_1.jpg");
		fso = FileSystemObject.create("P_1_test_87_thumb_1.jpg", childFile,
				null);
		theChildren.add(fso);
		childFile = new File(
				"./src/test/resources/Input/NamedStuff/AC_Thumbnails/Chapter1");
		fso = FileSystemObject.create("Chapter1", childFile, theChildren);
		mainFSO.add(fso);
		FileGroup group = FileGroup.create("Access Copy 2", "AC2",
				RepresentationTypes.AccessCopy, mainFSO);
		return group;
	}

	private StructMap getChapter1(FSOCollection files) {
		StructMapCollection children = StructMapCollection.create("Chapter1");
		StructMap map = StructMap.create("Page 1", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_1_P_1.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_1_P_1.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_1_P_1.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Page 2", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_2_P_2.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_2_P_2.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_2_P_2.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Page 3", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_3_P_3.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_3_P_3.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_3_P_3.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Chapter1", children, null, 0);
		return map;
	}

	private StructMap getChapter2(FSOCollection files) {
		StructMapCollection children = StructMapCollection.create("Chapter2");
		StructMap map = StructMap.create("Page 4", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_4_P_4.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_4_P_4.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_4_P_4.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Page 5", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_5_P_5.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_5_P_5.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_5_P_5.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Page 6", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_6_P_6.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_6_P_6.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_6_P_6.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Chapter2", children, null, 0);
		return map;
	}

	private StructMap getChapter3(FSOCollection files) {
		StructMapCollection children = StructMapCollection.create("Chapter3");
		StructMap map = StructMap.create("Page 7", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_7_P_7.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_7_P_7.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_7_P_7.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Page 8", null, null, 0);
		map.addFile(files
				.getFSOByFullFileName("test_87_thumb_8_P_8.jpg", false), false);
		map.addFile(
				files.getFSOByFullFileName("test_87_view_8_P_8.jpg", false),
				false);
		map.addFile(files.getFSOByFullFileName("test_87_archive_8_P_8.jpg",
				false), false);
		children.add(map);
		map = StructMap.create("Chapter3", children, null, 0);
		return map;
	}

	@Test
	public final void testLoadEntity() {
		// TODO When the presenter is calling the GoAPI this will need to change
		/**
		 * Here's one I prepared earlier. Copy job to the job queue directory
		 */
		try {
			tearDownAfterClass(); // Clean up any extraneous stuff left over
									// from other tests.
		} catch (Exception ex) {
		}
		ArrayList<FileGroupCollection> entities = new ArrayList<FileGroupCollection>();
		String rootDirectory = null;
		try {
			rootDirectory = new File("./src/test/resources/Input/NamedStuff")
					.getCanonicalPath();
		} catch (Exception ex) {
			fail();
		}
		FileGroupCollection collection = FileGroupCollection.create(
				"TestNewJobQueue", rootDirectory);
		UploadJob job = null;
		try {
			collection.add(getDO());
			collection.add(getAC1());
			collection.add(getAC2());
			entities.add(collection);
			StructMapCollection structures = StructMapCollection.create();
			FSOCollection allFiles = collection.getAllFiles();
			structures.add(getChapter1(allFiles));
			structures.add(getChapter2(allFiles));
			structures.add(getChapter3(allFiles));
			MetaDataFields theMetaData = null;
			theMetaData = MetaDataFields
					.create("./src/test/resources/Input/TestNewJobQueue/metaData.xml");
			job = UploadJob.create("TestNewJobQueue", "TestNewJobQueue",
					"PlayerM", entities.get(0), structures, theMetaData, "5",
					"5", applicationProperties, rootDirectory, true, "Mini Monkey");
			if (entities.size() > 1) {
				for (int i = 1; i < entities.size(); i++) {
					FileGroupCollection entity = entities.get(i);
					job.addJob(entity.getEntityName(), entity, structures,
							theMetaData);
				}
			}
			job.saveJob(false);
			job.prepareJobToRun();
		} catch (Exception ex) {
			fail();
		}
		/*
		 * String inPath = _resourcesInputPath + "TestNewJobQueue.xml"; String
		 * outPath = _resourcesPath + "JobQueue/TestNewJobQueue.xml";
		 * job.saveJob(); try { FileUtils.copyFile(inPath, outPath); inPath =
		 * _resourcesInputPath + "TestNewJobQueue"; outPath = _resourcesPath +
		 * "JobQueue/TestNewJobQueue"; FileUtils.ensureDirectoryExists(outPath);
		 * File inFile = new File(inPath); for (File child : inFile.listFiles())
		 * { if (child.isFile()) { //Don't want to copy the .svn directory
		 * FileUtils.copyFileToDirectory(child.getPath(), outPath); } } } catch
		 * (Exception ex) { ex.printStackTrace(); fail(); }
		 */
		try {
			LOG.debug("Before presenter setup");
			depositPresenter.getAppProperties().clearLoggedOnUser();
			depositPresenter.setupScreen();
			LOG.debug("After presenter setup");
		} catch (Exception ex) {
			fail();
		}
		LOG.debug("testLoadEntity About to log in");
		loginPresenter.login(LOGIN_NAME, LOGIN_PASSWORD);
		try {
			// Put in a delay as the login is not synchronous
			Thread.sleep(250);
		} catch (Exception ex) {
		}
		LOG.debug("logged in");
		int loopCount = 0; // Should be finished in much less than 5 seconds
		while ((depositPresenter.jobsRunning()) && (loopCount < 5000)) {
			loopCount++;
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
				ex.printStackTrace();
				fail();
			}
		}
		/**
		 * Here's one I prepared earlier. Check that the presenter has loaded &
		 * run the job
		 */
		assertFalse(depositPresenter.jobsRunning());
		String outPath = RESOURCES_OUTPUT_PATH
				+ applicationProperties.getLoggedOnUser();
		File outFile = new File(outPath);
		assertTrue(outFile.exists());
		String[] children = outFile.list();
		assertTrue(children.length == 1);
		assertTrue(children[0].startsWith("TestNewJobQueue"));
		try {
			tearDownAfterClass();
		} catch (Exception ex) {
			fail();
		}

		/**
		 * Now check that it will load from the screen
		 */
		setRoot();
		LOG.debug("Before load entity "
				+ applicationProperties.getApplicationData().getMetsSavePath());
		DcRecord rec = new DcRecord("");
		rec.setId("100");
		rec.setTitle("Hello World");
		rec.setReference("Ref");
		rec.setRights("Unrestricted");
		depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.CMS2);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		depositPresenter.loadEntity();
		LOG.debug("After load entity "
				+ applicationProperties.getApplicationData().getMetsSavePath());
		loopCount = 0; // Should be finished in much less than 5 seconds
		while ((depositPresenter.jobsRunning()) && (loopCount < 150)) {
			loopCount++;
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
				ex.printStackTrace();
				fail();
			}
		}
		assertFalse(depositPresenter.jobsRunning());
		children = outFile.list();
		assertTrue(children.length == 1);
		assertTrue(children[0].startsWith("NamedStuff"));
		try {
			tearDownAfterClass();
		} catch (Exception ex) {
			fail();
		}
		try {
			tearDownAfterClass();
		} catch (Exception ex) {
			fail();
		}
		assertFalse(theFrame.cursorIsWaiting);
	}

	private TreePath[] loadEntity(String entityPath) {
		DepositTreeModel model = (DepositTreeModel) theFrame.treeFileSystem
				.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model
				.getRoot();
		File theFile = new File(entityPath);
		LOG.debug("testProcessFileTreeKeyPress, root full path "
				+ theFile.getAbsolutePath());
		try {
			LOG.debug("testProcessFileTreeKeyPress, root canonical path "
					+ theFile.getCanonicalPath());
			theFrame.storeCurrentPath(theFile.getCanonicalPath());
		} catch (Exception ex) {
			fail();
		}
		FileSystemObject fso = (FileSystemObject) rootNode.getUserObject();
		fso.ensureChildPathLoaded(theFrame.getCurrentPath());
		FSOCollection coll = FSOCollection.create();
		coll.add(fso);
		FileSystemObject child = coll.getFSOByFullPath(theFrame
				.getCurrentPath(), true);
		depositPresenter.addFileSystemRoot(fso, false, false, theFrame
				.getCurrentPath());
		depositPresenter.selectNode(child, ETreeType.FileSystemTree);
		TreePath currentTreePath = theFrame.treeFileSystem.getSelectionPath();
		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentTreePath
				.getLastPathComponent();
		assertTrue(currentNode.getUserObject() instanceof FileSystemObject);
		fso = (FileSystemObject) currentNode.getUserObject();
		assertFalse(fso.getIsPlaceholder());
		assertTrue(fso.getFullPath().equals(theFrame.getCurrentPath()));
		return theFrame.treeFileSystem.getSelectionPaths();
	}

	private void doIEMoveTest(int childNumber, int maxChildren,
			FileSystemObject fso, Object parent) {
		int childNo = childNumber;
		// Move them down to the bottom & back up again
		while (childNo > 0) {
			assertTrue(depositPresenter.canMoveIEFile(fso, parent, true));
			depositPresenter.moveIEFile(fso, parent, true);
			childNo--;
			if (childNo == 0) {
				assertFalse(depositPresenter.canMoveIEFile(fso, parent, true));
			}
		}
		while (childNo < maxChildren - 1) {
			assertTrue(depositPresenter.canMoveIEFile(fso, parent, false));
			depositPresenter.moveIEFile(fso, parent, false);
			childNo++;
			if (childNo == maxChildren - 1) {
				assertFalse(depositPresenter.canMoveIEFile(fso, parent, false));
			}
		}
		while (childNo > 0) {
			assertTrue(depositPresenter.canMoveIEFile(fso, parent, true));
			depositPresenter.moveIEFile(fso, parent, true);
			childNo--;
			if (childNo == 0) {
				assertFalse(depositPresenter.canMoveIEFile(fso, parent, true));
			}
		}
	}

	private void doStructMoveTest(int childNumber, int maxChildren,
			Object child, Object parent) {
		int childNo = childNumber;
		// Move them down to the bottom & back up again
		while (childNo > 0) {
			assertTrue(depositPresenter
					.canMoveStructObject(child, parent, true));
			depositPresenter.moveStructObject(child, parent, true);
			childNo--;
			if (childNo == 0) {
				assertFalse(depositPresenter.canMoveStructObject(child, parent,
						true));
			}
		}
		while (childNo < maxChildren - 1) {
			assertTrue(depositPresenter.canMoveStructObject(child, parent,
					false));
			depositPresenter.moveStructObject(child, parent, false);
			childNo++;
			if (childNo == maxChildren - 1) {
				assertFalse(depositPresenter.canMoveStructObject(child, parent,
						false));
			}
		}
		while (childNo > 0) {
			assertTrue(depositPresenter
					.canMoveStructObject(child, parent, true));
			depositPresenter.moveStructObject(child, parent, true);
			childNo--;
			if (childNo == 0) {
				assertFalse(depositPresenter.canMoveStructObject(child, parent,
						true));
			}
		}
	}

	private void addFilesToEntityAndCheckResults(
			DepositTreeModel fileSystemModel, DepositTreeModel entityModel,
			String[] fileNames // Make sure you send 3 file names
			, RepresentationTypes theType) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) fileSystemModel
				.getRoot();
		FSOCollection coll = FSOCollection.create();
		coll = FSOCollection.create();
		coll.add((FileSystemObject) rootNode.getUserObject());
		FileSystemObject child = coll.getFSOByFullFileName(fileNames[0], true);
		depositPresenter.selectNode(child, ETreeType.FileSystemTree);
		TreePath firstPath = theFrame.treeFileSystem.getSelectionPath();
		child = coll.getFSOByFullFileName(fileNames[1], true);
		depositPresenter.selectNode(child, ETreeType.FileSystemTree);
		TreePath secondPath = theFrame.treeFileSystem.getSelectionPath();
		child = coll.getFSOByFullFileName(fileNames[2], true);
		depositPresenter.selectNode(child, ETreeType.FileSystemTree);
		TreePath thirdPath = theFrame.treeFileSystem.getSelectionPath();
		TreePath[] selectionPaths = new TreePath[] { firstPath, secondPath,
				thirdPath };
		theFrame.treeFileSystem.setSelectionPaths(selectionPaths);
		depositPresenter.processFileTreeKeyPress(theType.hotKeyValue(),
				selectionPaths);
		// Check that is has been created
		child = coll.getFSOByFullFileName(fileNames[0], true);
		depositPresenter.selectNode(child, ETreeType.FileSystemTree);
		assertTrue(theFrame.treeFileSystem.getSelectionPath() == null);
		rootNode = (DefaultMutableTreeNode) entityModel.getRoot();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode
				.getChildAt(rootNode.getChildCount() - 1);
		assertTrue(node.getUserObject() instanceof FileGroup);
		FileGroup group = (FileGroup) node.getUserObject();
		assertTrue(group.getEntityType().equals(theType));
		assertTrue(node.getChildCount() == 3);
	}

	@Test
//Temporarily adding ignore as this testcase has some defect and fails
// all the time. This has to be fixed in the next release.
	@Ignore
	public final void testHeapsOfStuff() {
		/**
		 * Test a lot of the entity creation / deletion Tests hot keys also
		 */
		depositPresenter.refreshFileList();
		DepositTreeModel fileSystemModel = (DepositTreeModel) theFrame.treeFileSystem
				.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) fileSystemModel
				.getRoot();
		assertTrue(rootNode.getChildCount() > 0);
		DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode
				.getChildAt(0);
		assertTrue(childNode.getChildCount() == 1); // Should be a placeholder
		DefaultMutableTreeNode grandChildNode = (DefaultMutableTreeNode) childNode
				.getChildAt(0);
		assertTrue(grandChildNode.getUserObject() instanceof FileSystemObject);
		FileSystemObject fso = (FileSystemObject) grandChildNode
				.getUserObject();
		assertTrue(fso.getIsPlaceholder());
		TreePath currentTreePath = new TreePath(childNode.getPath());
		depositPresenter.expandFileSystemTree(currentTreePath);
		if (childNode.getChildCount() > 0) {
			grandChildNode = (DefaultMutableTreeNode) childNode.getChildAt(0);
			assertTrue(grandChildNode.getUserObject() instanceof FileSystemObject);
			fso = (FileSystemObject) grandChildNode.getUserObject();
			assertFalse(fso.getIsPlaceholder());
		}
		try {
			setUp();
		} catch (Exception ex) {
			fail();
		}
		// After setup the model will have changed - but it shouldn't change
		// again after this
		fileSystemModel = (DepositTreeModel) theFrame.treeFileSystem.getModel();
		rootNode = (DefaultMutableTreeNode) fileSystemModel.getRoot();

		TreePath[] selectionPaths = loadEntity(RESOURCES_INPUT_PATH_NAMED);
		// Entity root not set yet - should be able to get a menu
		JPopupMenu menu = depositPresenter.processFileTreeKeyPress('m',
				selectionPaths);
		assertTrue(menu != null);
		// Set the root
		menu = depositPresenter.processFileTreeKeyPress('s', selectionPaths);
		DepositTreeModel entityModel = (DepositTreeModel) theFrame.treeEntities
				.getModel();
		DepositTreeModel structMapModel = (DepositTreeModel) theFrame.treeStructMap
				.getModel();
		assertTrue(menu == null);
		// The digital original should already have been added automatically
		// Only one allowed, so this should fail.
		assertFalse(depositPresenter.canAddRepresentationType('d'));
		// Only one allowed, but none added so far, so this should succeed.
		assertTrue(depositPresenter.canAddRepresentationType('p'));
		// multiple allowed, but none added so far, so this should succeed.
		assertTrue(depositPresenter.canAddRepresentationType('a'));
		// multiple allowed, two added so far, so this should succeed.
		assertTrue(depositPresenter.canAddRepresentationType('m'));

		// While we've got the named stuff loaded, might as well check the move
		// up/down
		DefaultMutableTreeNode ieRootNode = (DefaultMutableTreeNode) entityModel
				.getRoot();
		// Right - we should have one DO & 2 AC
		assertTrue(ieRootNode.getChildCount() == 3);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) ieRootNode
				.getChildAt(0);
		assertTrue(node.getUserObject() instanceof FileGroup);
		FileGroup group = (FileGroup) node.getUserObject();
		assertTrue(group.getEntityType().equals(
				RepresentationTypes.DigitalOriginal));
		int childNo = 0;
		childNode = (DefaultMutableTreeNode) node.getChildAt(childNo);
		assertTrue(childNode.getUserObject() instanceof FileSystemObject);
		fso = (FileSystemObject) childNode.getUserObject();
		// while (fso.getFileName().equalsIgnoreCase(".svn")) {
		// childNo++;
		// childNode = (DefaultMutableTreeNode)node.getChildAt(childNo);
		// assertTrue(childNode.getUserObject() instanceof FileSystemObject);
		// fso = (FileSystemObject)childNode.getUserObject();
		// }
		// childNo = 0;
		fso.setSortBy(SortBy.UserArranged);
		while (!fso.getFileName().equalsIgnoreCase("Chapter1")) {
			childNo++;
			childNode = (DefaultMutableTreeNode) node.getChildAt(childNo);
			assertTrue(childNode.getUserObject() instanceof FileSystemObject);
			fso = (FileSystemObject) childNode.getUserObject();
		}
		doIEMoveTest(childNo, node.getChildCount(), fso, group);

		childNo = 0;
		DefaultMutableTreeNode subChildNode = (DefaultMutableTreeNode) childNode
				.getChildAt(childNo);
		assertTrue(subChildNode.getUserObject() instanceof FileSystemObject);
		FileSystemObject fsoChild = (FileSystemObject) subChildNode
				.getUserObject();
		fsoChild.setSortBy(SortBy.UserArranged);
		while (!fsoChild.getFileName().equalsIgnoreCase(
				"test_87_archive_1_P_1.jpg")) {
			childNo++;
			subChildNode = (DefaultMutableTreeNode) childNode
					.getChildAt(childNo);
			assertTrue(subChildNode.getUserObject() instanceof FileSystemObject);
			fsoChild = (FileSystemObject) subChildNode.getUserObject();
		}
		fso.setSortBy(SortBy.UserArranged);
		fsoChild.setSortBy(SortBy.UserArranged);
		// doIEMoveTest(childNo, childNode.getChildCount(), fsoChild, fso);

		node = (DefaultMutableTreeNode) ieRootNode.getChildAt(1);
		assertTrue(node.getUserObject() instanceof FileGroup);
		group = (FileGroup) node.getUserObject();
		assertTrue(group.getEntityType().equals(RepresentationTypes.AccessCopy));
		node = (DefaultMutableTreeNode) ieRootNode.getChildAt(2);
		assertTrue(node.getUserObject() instanceof FileGroup);
		group = (FileGroup) node.getUserObject();
		assertTrue(group.getEntityType().equals(RepresentationTypes.AccessCopy));

		DefaultMutableTreeNode structRootNode = (DefaultMutableTreeNode) structMapModel
				.getRoot();
		childNo = 0;
		childNode = (DefaultMutableTreeNode) structRootNode.getChildAt(childNo);
		assertTrue(childNode.getUserObject() instanceof StructMap);
		StructMap map = (StructMap) childNode.getUserObject();
		while (!map.getStructureName().equalsIgnoreCase("Chapter1")) {
			childNo++;
			childNode = (DefaultMutableTreeNode) structRootNode
					.getChildAt(childNo);
			assertTrue(childNode.getUserObject() instanceof StructMap);
			map = (StructMap) childNode.getUserObject();
		}
		doStructMoveTest(childNo, structRootNode.getChildCount(), map,
				structRootNode.getUserObject());
		childNo = 0;
		subChildNode = (DefaultMutableTreeNode) childNode.getChildAt(childNo);
		assertTrue(subChildNode.getUserObject() instanceof StructMap);
		map = (StructMap) subChildNode.getUserObject();
		while (!map.getStructureName().equalsIgnoreCase("Page 1")) {
			childNo++;
			subChildNode = (DefaultMutableTreeNode) childNode
					.getChildAt(childNo);
			assertTrue(subChildNode.getUserObject() instanceof StructMap);
			map = (StructMap) subChildNode.getUserObject();
		}
		doStructMoveTest(childNo, structRootNode.getChildCount(), map,
				childNode.getUserObject());
		childNo = 0;
		DefaultMutableTreeNode subSubChildNode = (DefaultMutableTreeNode) subChildNode
				.getChildAt(childNo);
		assertTrue(subSubChildNode.getUserObject() instanceof FileSystemObject);
		fso = (FileSystemObject) subSubChildNode.getUserObject();
		while (!fso.getFileName().equalsIgnoreCase("test_87_view_1_P_1.jpg")) {
			childNo++;
			subSubChildNode = (DefaultMutableTreeNode) subChildNode
					.getChildAt(childNo);
			assertTrue(subSubChildNode.getUserObject() instanceof FileSystemObject);
			fso = (FileSystemObject) subSubChildNode.getUserObject();
		}
		doStructMoveTest(childNo, subChildNode.getChildCount(), fso,
				subChildNode.getUserObject());

		rootNode = (DefaultMutableTreeNode) fileSystemModel.getRoot();
		FSOCollection coll = FSOCollection.create();
		coll.add((FileSystemObject) rootNode.getUserObject());
		FileSystemObject child = coll.getFSOByFullFileName(
				"test_87_view_1_P_1.jpg", true);
		depositPresenter.selectNode(child, ETreeType.EntityTree);
		assertTrue(depositPresenter.canCreateAutoStructItem());

		rootNode = (DefaultMutableTreeNode) entityModel.getRoot();
		depositPresenter.selectNode(rootNode.getUserObject(),
				ETreeType.EntityTree);
		assertFalse(depositPresenter.canCreateAutoStructItem());
		node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
		depositPresenter.selectNode(node.getUserObject(), ETreeType.EntityTree);
		assertFalse(depositPresenter.canCreateAutoStructItem());

		// Delete all the struct map & entity nodes so we can re-add them using
		// hot keys
		rootNode = (DefaultMutableTreeNode) structMapModel.getRoot();
		int nodeNo = 0;
		node = (DefaultMutableTreeNode) rootNode.getChildAt(nodeNo);
		map = (StructMap) node.getUserObject();
		while (!map.getStructureName().equals("Chapter1")) {
			nodeNo++;
			node = (DefaultMutableTreeNode) rootNode.getChildAt(nodeNo);
			map = (StructMap) node.getUserObject();
		}
		nodeNo = 0;
		childNode = (DefaultMutableTreeNode) node.getChildAt(nodeNo);
		map = (StructMap) childNode.getUserObject();
		while (!map.getStructureName().equals("Page 1")) {
			nodeNo++;
			childNode = (DefaultMutableTreeNode) node.getChildAt(nodeNo);
			map = (StructMap) childNode.getUserObject();
		}
		assertTrue(childNode.getChildCount() == 3);
		grandChildNode = (DefaultMutableTreeNode) childNode.getChildAt(0);
		TreePath path = new TreePath(grandChildNode.getPath());
		theFrame.treeStructMap.setSelectionPath(path);
		Object nodeObject = grandChildNode.getUserObject();
		// _presenter.selectNode(nodeObject, ETreeType.StructMapTree);
		assertTrue(depositPresenter.canDeleteStructItem());
		depositPresenter.deleteStructMapItem();
		assertTrue(childNode.getChildCount() == 2);
		int noOfNodes = node.getChildCount();
		path = new TreePath(childNode.getPath());
		theFrame.treeStructMap.setSelectionPath(path);
		nodeObject = childNode.getUserObject();
		// _presenter.selectNode(nodeObject, ETreeType.StructMapTree);
		assertTrue(depositPresenter.canDeleteStructItem());
		depositPresenter.deleteStructMapItem();
		assertTrue(node.getChildCount() == noOfNodes - 1);

		noOfNodes = rootNode.getChildCount();
		for (int i = 0; i < noOfNodes; i++) {
			node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
			path = new TreePath(node.getPath());
			theFrame.treeStructMap.setSelectionPath(path);
			nodeObject = node.getUserObject();
			// _presenter.selectNode(nodeObject, ETreeType.StructMapTree);
			assertTrue(depositPresenter.canDeleteStructItem());
			depositPresenter.deleteStructMapItem();
			assertTrue(rootNode.getChildCount() == noOfNodes - i - 1);
			if (rootNode.getChildCount() > 0) {
				node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
				assertFalse(node.getUserObject().equals(nodeObject));
			}
		}
		assertTrue(rootNode.getChildCount() == 0);

		// Struct map should be clear now - clear IE
		rootNode = (DefaultMutableTreeNode) entityModel.getRoot();
		node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
		nodeNo = 0;
		childNode = (DefaultMutableTreeNode) node.getChildAt(nodeNo);
		fso = (FileSystemObject) childNode.getUserObject();
		while (!fso.getDescription().equals("Chapter1")) {
			nodeNo++;
			childNode = (DefaultMutableTreeNode) rootNode.getChildAt(nodeNo);
			fso = (FileSystemObject) childNode.getUserObject();
		}
		nodeNo = 0;
		grandChildNode = (DefaultMutableTreeNode) childNode.getChildAt(nodeNo);
		fso = (FileSystemObject) grandChildNode.getUserObject();
		while (!fso.getFileName().equals("test_87_archive_1_P_1.jpg")) {
			nodeNo++;
			grandChildNode = (DefaultMutableTreeNode) childNode
					.getChildAt(nodeNo);
			fso = (FileSystemObject) grandChildNode.getUserObject();
		}
		noOfNodes = childNode.getChildCount();
		path = new TreePath(grandChildNode.getPath());
		JPopupMenu pop = depositPresenter.getEntityMenu(grandChildNode);
		assertTrue(pop.getSubElements().length == 3);
		pop = depositPresenter.getEntityMenu(childNode);
		assertTrue(pop.getSubElements().length == 2);
		pop = depositPresenter.getEntityMenu(node);
		assertTrue(pop.getSubElements().length == 1);
		pop = depositPresenter.getEntityMenu(rootNode);
		assertTrue(pop.getSubElements().length == 3);

		theFrame.treeEntities.setSelectionPath(path);
		// _presenter.selectNode(nodeObject, ETreeType.EntityTree);
		assertTrue(depositPresenter.canDeleteEntityItem());
		depositPresenter.deleteEntity();
		assertTrue(childNode.getChildCount() == noOfNodes - 1);
		noOfNodes = node.getChildCount();
		path = new TreePath(childNode.getPath());
		theFrame.treeEntities.setSelectionPath(path);
		// _presenter.selectNode(nodeObject, ETreeType.EntityTree);
		assertTrue(depositPresenter.canDeleteEntityItem());
		depositPresenter.deleteEntity();
		assertTrue(node.getChildCount() == noOfNodes - 1);

		rootNode = (DefaultMutableTreeNode) entityModel.getRoot();
		noOfNodes = rootNode.getChildCount();
		for (int i = 0; i < noOfNodes; i++) {
			node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
			path = new TreePath(node.getPath());
			theFrame.treeEntities.setSelectionPath(path);
			nodeObject = node.getUserObject();
			// _presenter.selectNode(nodeObject, ETreeType.EntityTree);
			assertTrue(depositPresenter.canDeleteEntityItem());
			depositPresenter.deleteEntity();
			assertTrue(rootNode.getChildCount() == noOfNodes - i - 1);
			if (rootNode.getChildCount() > 0) {
				node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
				assertFalse(node.getUserObject().equals(nodeObject));
			}
			rootNode = (DefaultMutableTreeNode) entityModel.getRoot(); // Make
																		// sure
																		// we
																		// have
																		// the
																		// right
																		// root
																		// node
																		// - it
																		// changes
		}
		assertTrue(rootNode.getChildCount() == 0);
		depositPresenter.resetScreen();

		// Adding an auto structure item from a single file
		// should result in all three representation types being there
		selectionPaths = loadEntity(RESOURCES_INPUT_MANUAL);
		// Entity root not set yet - should be able to get a menu
		menu = depositPresenter.processFileTreeKeyPress('m', selectionPaths);
		assertTrue(menu != null);
		// Set the root
		menu = depositPresenter.processFileTreeKeyPress('s', selectionPaths);
		assertTrue(menu == null);
		// Only one allowed, but none added so far, so this should succeed.
		assertTrue(depositPresenter.canAddRepresentationType('d'));
		// Only one allowed, but none added so far, so this should succeed.
		assertTrue(depositPresenter.canAddRepresentationType('p'));
		// multiple allowed, but none added so far, so this should succeed.
		assertTrue(depositPresenter.canAddRepresentationType('a'));
		// multiple allowed, two added so far, so this should succeed.
		assertTrue(depositPresenter.canAddRepresentationType('m'));

		// Create DO
		rootNode = (DefaultMutableTreeNode) fileSystemModel.getRoot();
		String[] fileNames = { "File1.jpg", "File1.tif", "File1.tn" };
		addFilesToEntityAndCheckResults(fileSystemModel, entityModel,
				fileNames, RepresentationTypes.DigitalOriginal);

		// Create AC 1
		rootNode = (DefaultMutableTreeNode) fileSystemModel.getRoot();
		fileNames = new String[] { "File2.jpg", "File2.tif", "File2.tn" };
		addFilesToEntityAndCheckResults(fileSystemModel, entityModel,
				fileNames, RepresentationTypes.AccessCopy);

		// Create AC 2
		rootNode = (DefaultMutableTreeNode) fileSystemModel.getRoot();
		fileNames = new String[] { "File3.jpg", "File3.tif", "File3.tn" };
		addFilesToEntityAndCheckResults(fileSystemModel, entityModel,
				fileNames, RepresentationTypes.AccessCopy);

		// Should now have a completely populated IE - test Struct map
		// Adding an auto structure item from a single file
		rootNode = (DefaultMutableTreeNode) entityModel.getRoot();
		coll = FSOCollection.create();
		node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
		group = (FileGroup) node.getUserObject();
		coll = group.getChildren();
		child = coll.getFSOByFullFileName("File1.jpg", true);
		depositPresenter.selectNode(child, ETreeType.EntityTree);
		assertTrue(depositPresenter.canCreateAutoStructItem());
		theFrame.setInputResult("First Test");
		depositPresenter.createAutoStructItem(true);
		rootNode = (DefaultMutableTreeNode) structMapModel.getRoot();
		assertTrue(rootNode.getChildCount() == 1);
		node = (DefaultMutableTreeNode) rootNode.getChildAt(0);
		assertTrue(node.getChildCount() == 3);
		assertTrue(node.getUserObject() instanceof StructMap);
		map = (StructMap) node.getUserObject();
		assertTrue(map.getStructureName().equalsIgnoreCase("First Test"));
		node = (DefaultMutableTreeNode) node.getChildAt(0);
		;
		assertTrue(node.getUserObject().equals(child));

		// Adding an auto structure item NOT from a single file
		// should result in only one representation type being there - the one
		// selected
		rootNode = (DefaultMutableTreeNode) entityModel.getRoot();
		coll = FSOCollection.create();
		node = (DefaultMutableTreeNode) rootNode.getChildAt(1);
		group = (FileGroup) node.getUserObject();
		coll = group.getChildren();
		child = coll.getFSOByFullFileName("File2.jpg", true);
		depositPresenter.selectNode(child, ETreeType.EntityTree);
		assertTrue(depositPresenter.canCreateAutoStructItem());
		theFrame.setInputResult("Second Test");
		depositPresenter.createAutoStructItem(false);
		rootNode = (DefaultMutableTreeNode) structMapModel.getRoot();
		assertTrue(rootNode.getChildCount() == 2);
		node = (DefaultMutableTreeNode) rootNode.getChildAt(1);
		assertTrue(node.getChildCount() == 1);
		assertTrue(node.getUserObject() instanceof StructMap);
		map = (StructMap) node.getUserObject();
		assertTrue(map.getStructureName().equalsIgnoreCase("Second Test"));
		node = (DefaultMutableTreeNode) node.getChildAt(0);
		;
		assertTrue(node.getUserObject().equals(child));

		depositPresenter.resetScreen();
		selectionPaths = loadEntity(RESOURCES_INPUT_MULTIPLE);
		// Entity root should now be unset - should be able to get a menu
		menu = depositPresenter.processFileTreeKeyPress('m', selectionPaths);
		assertTrue(menu != null);
		// Set the multiple root
		menu = depositPresenter.processFileTreeKeyPress('e', selectionPaths);
		assertTrue(menu == null);
		rootNode = (DefaultMutableTreeNode) entityModel.getRoot();
		LOG.debug("ChildCount: " + rootNode.getChildCount());
		assertTrue(rootNode.getChildCount() >= 3); // 3 on local PC, 4 on
													// server. Gah!
		for (int i = 0; i < 3; i++) {
			node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			assertTrue(node.getUserObject() instanceof FileGroupCollection);
		}
	}

	@Test
	public final void testFavourites() {
		LOG
				.debug("*************************************************************************");
		LOG.debug("testFavourites");
		LOG
				.debug("*************************************************************************");
		// Set it up
		FileUtils.deleteFileOrDirectoryRecursive(FAVOURITES_PATH);
		try {
			setUp();
		} catch (Exception ex) {
			fail();
		}
		DepositTreeModel fileSystemModel = (DepositTreeModel) theFrame.treeFileSystem
				.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) fileSystemModel
				.getRoot();
		FSOCollection coll = FSOCollection.create();
		FileSystemObject fso = (FileSystemObject) rootNode.getUserObject();
		coll.add(fso);
		if (fso.getFile() == null) {
			LOG.debug("Root Object (file null): " + fso.getDescription());
		} else {
			try {
				LOG.debug("Root Object (file not null): "
						+ fso.getFile().getCanonicalPath());
			} catch (Exception ex) {
			}
		}
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode
					.getChildAt(i);
			fso = (FileSystemObject) node.getUserObject();
			if (fso.getFile() == null) {
				LOG.debug("Child " + i + " (file null): "
						+ fso.getDescription());
			} else {
				try {
					LOG.debug("Child " + i + " (file not null): "
							+ fso.getFile().getCanonicalPath());
				} catch (Exception ex) {
				}
			}
		}
		File settingsPathFile = new File(RESOURCES_SETTINGS_PATH);
		File favouritesPathFile = new File(FAVOURITES_PATH);
		assertTrue(favouritesPathFile.exists());
		FileSystemObject child = null;
		// Make sure the tree includes the path
		String fullPath = "";
		try {
			fullPath = settingsPathFile.getCanonicalPath();
		} catch (Exception ex) {
			fail();
		}
		String[] paths;
		String fileSeparator = System.getProperty("file.separator");
		if (fileSeparator.equals("\\")) {
			fullPath = fullPath.replaceAll("/", fileSeparator);
			paths = fullPath.split("[\\\\]");
		} else {
			fullPath = fullPath.replaceAll("[\\\\]", fileSeparator);
			paths = fullPath.split(fileSeparator);
		}
		String currentPath = "";
		String rootPath = paths[0] + fileSeparator;
		LOG.debug("Full Path: " + fullPath);
		LOG.debug("Root Path: " + rootPath);
		for (int i = 0; i < paths.length; i++) {
			LOG.debug("Path " + i + ": " + paths[i]);
		}
		for (int i = 0; i < paths.length; i++) {
			currentPath += paths[i] + fileSeparator;
			LOG.debug("Current Path: " + currentPath);
			child = coll.getFSOByFullPath(currentPath, true);
			if (child == null) {
				LOG.debug("Child is null (not found)");
			} else {
				if (child.getFile() == null) {
					LOG.debug("Child found, file is null");
				} else {
					LOG.debug("Child found, path " + child.getFullPath());
				}
			}
			depositPresenter.selectNode(child, ETreeType.FileSystemTree);
			TreePath currentTreePath = theFrame.treeFileSystem
					.getSelectionPath();
			LOG.debug("currentTreePath: " + currentTreePath.toString());
			depositPresenter.expandFileSystemTree(currentTreePath);
		}

		// Test the favourites
		// No favourites loaded - check that the menu is empty
		TreePath[] treePaths = null;
		depositPresenter.clearFavourites();
		assertFalse(depositPresenter.canStoreFavourites(treePaths));
		treePaths = theFrame.treeFileSystem.getSelectionPaths();
		assertTrue(depositPresenter.canStoreFavourites(treePaths));
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) theFrame.treeFileSystem
				.getLastSelectedPathComponent();
		fso = (FileSystemObject) node.getUserObject();
		JPopupMenu menu = (JPopupMenu) theFrame.mnuFileFavourites
				.getSubElements()[0];
		assertTrue(menu.getSubElements().length == 1);
		JMenuItem item = (JMenuItem) menu.getSubElements()[0];
		String noFavouritesText = item.getText();
		assertFalse(noFavouritesText.equals(fso.getFullPath()));

		// Store a favourite & check that it is included in the menu
		depositPresenter.storeAsFavourite(node);
		assertTrue(menu.getSubElements().length == 2);
		item = (JMenuItem) menu.getSubElements()[0];
		assertTrue(item.getText().equals(fso.getFullPath()));
		// Reset the screen & then check that the favourite is included in the
		// menu
		try {
			setUp();
		} catch (Exception ex) {
			fail();
		}
		menu = (JPopupMenu) theFrame.mnuFileFavourites.getSubElements()[0];
		assertTrue(menu.getSubElements().length == 2);
		item = (JMenuItem) menu.getSubElements()[0];
		assertTrue(item.getText().equals(fso.getFullPath()));
		// Delete the storage file & make sure that there are no menu items
		FileUtils.deleteFileOrDirectoryRecursive(FAVOURITES_PATH);
		try {
			setUp();
		} catch (Exception ex) {
			fail();
		}
		menu = (JPopupMenu) theFrame.mnuFileFavourites.getSubElements()[0];
		assertTrue(menu.getSubElements().length == 1);
		item = (JMenuItem) menu.getSubElements()[0];
		assertTrue(item.getText().equals(noFavouritesText));

		// Check clearing
		depositPresenter.storeAsFavourite(node);
		assertTrue(menu.getSubElements().length == 2);
		item = (JMenuItem) menu.getSubElements()[0];
		assertTrue(item.getText().equals(fso.getFullPath()));
		depositPresenter.clearFavourites();
		assertTrue(theFrame.mnuFileFavourites.getSubElements().length == 1);
		item = (JMenuItem) menu.getSubElements()[0];
		assertTrue(item.getText().equals(noFavouritesText));

		// Check loading a directory
		fileSystemModel = (DepositTreeModel) theFrame.treeFileSystem.getModel();
		rootNode = (DefaultMutableTreeNode) fileSystemModel.getRoot();
		coll = FSOCollection.create();
		coll.add((FileSystemObject) rootNode.getUserObject());
		currentPath = rootPath;
		depositPresenter.loadPath(currentPath);
		DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) theFrame.treeFileSystem
				.getLastSelectedPathComponent();
		FileSystemObject fsoNew = (FileSystemObject) newNode.getUserObject();
		assertTrue(fsoNew.getFullPath().equals(currentPath));
		currentPath = fso.getFullPath();
		depositPresenter.loadPath(currentPath);
		newNode = (DefaultMutableTreeNode) theFrame.treeFileSystem
				.getLastSelectedPathComponent();
		fsoNew = (FileSystemObject) newNode.getUserObject();
		assertTrue(fsoNew.getFullPath().equals(fso.getFullPath()));
		FileUtils.deleteFileOrDirectoryRecursive(FAVOURITES_PATH);
	}
}
