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

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import nz.govt.natlib.ndha.common.FileUtils;
import nz.govt.natlib.ndha.common.exlibris.SIPStatus;
import nz.govt.natlib.ndha.common.guiutilities.DecimalNumberField;
import nz.govt.natlib.ndha.common.guiutilities.WholeNumberField;
import nz.govt.natlib.ndha.manualdeposit.metadata.ApplicationData;
import nz.govt.natlib.ndha.manualdeposit.metadata.EDataType;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataConfigurator;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.IndigoUser;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataConfiguratorPresenter;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataElementCellEditor;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataListValues;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTableModel;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTypeImpl;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXDatePicker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * _presenter is the Presenter we are testing<br>
 * _theFrame is a mock screen that we use to test what the presenter is doing<br>
 * Every test needs to finish with assertFalse(_theFrame._cursorIsWaiting);<br>
 * This is to make sure the cursor is not set incorrectly<br>
 */
public class MetaDataTest {

	private final static Log LOG = LogFactory.getLog(MetaDataTest.class);
	private MetaDataConfiguratorPresenter metaDataPresenter;
	private MetaDataConfiguratorFrameTest theFrame;
	private boolean formIsVisible;
	private final static String CONFIG_SOURCE_FILE = "./src/test/resources/MetaData.xml";
	private final static String CONFIG_FILE = "./src/test/resources/MetaDataTest.xml";

	public class MetaDataConfiguratorFrameTest implements IMetaDataConfigurator {

		private MetaDataConfiguratorPresenter presenter;
		protected boolean cursorIsWaiting = false;
		private JTabbedPane tabMain = new JTabbedPane();
		private JList lstUsers = new JList();
		private JList lstDataList = new JList();
		private JComboBox cmbDataType = new JComboBox();
		private JPanel pnlListItems = new JPanel();
		private JList lstListItems = new JList();
		private JList lstCmsListItems = new JList();
		private JComboBox cmsFieldCombo = new JComboBox();
		private JList lstDNX = new JList();
		private JList lstDNXDetail = new JList();
		private JList lstDC = new JList();
		private JList lstDCXsi = new JList();
		private JList lstDCTerms = new JList();
		private JTabbedPane tabDnxDc = new JTabbedPane();
		private MetaDataTypeImpl metaData = null;
		private JPanel pnlDNX = new JPanel();
		private JPanel pnlDC = new JPanel();
		private boolean editingLookupValue = false;
		private boolean editingCmsValue = false;

		public MetaDataConfiguratorFrameTest() {
			tabDnxDc.addTab("DNX", pnlDNX);
			tabDnxDc.addTab("DC", pnlDC);
		}

		public MetaDataTypeImpl getTheData() {
			return metaData;
		}

		public boolean getIsEditingLookupValue() {
			return editingLookupValue;
		}

		public boolean getIsEditingCmsMapping() {
			return editingCmsValue;
		}

		public void setPresenter(MetaDataConfiguratorPresenter thePresenter) {
			presenter = thePresenter;
		}

		public void setupScreen(String settingsPath) {
			presenter.addMetaDataHandlers(lstDataList, cmbDataType,
					pnlListItems, lstListItems, lstCmsListItems, cmsFieldCombo,
					lstDNX, lstDNXDetail, lstDC, lstDCXsi, lstDCTerms, tabDnxDc);
			presenter.addMainHandlers(tabMain);
			presenter.addUserHandlers(lstUsers);
		}

		public void showUser(IndigoUser user) {
		}

		public void loadUserGroupData(UserGroupData theData) {
		}

		public void showApplicationData(ApplicationData theData) {
		}

		public void showSipStatus(SIPStatus sipData) {
		}

		public void loadData(MetaDataTypeImpl theData, String dcOther) {
			metaData = theData;
		}

		public void setConfigurationFileName(String fileName) {
		}

		public Component getComponent() {
			return null;
		}

		public void setWaitCursor(boolean isWaiting) {
			cursorIsWaiting = isWaiting;
		}

		public void checkButtons() {
			// Don't need to do anything here.
		}

		public void editLookupValue(MetaDataListValues value) {
			editingLookupValue = true;
		}

		public void editCmsMappingValue(String value) {
			editingCmsValue = true;
		}

		public void showView() {
			formIsVisible = true;
		}

		public void showError(String header, String message) {
			// TODO Think of something to do with the error
			// JOptionPane.showMessageDialog(this, message, header,
			// JOptionPane.ERROR_MESSAGE);
		}

		public void showMessage(String header, String message) {
			// TODO Think of something to do with the message
			// JOptionPane.showMessageDialog(this, message, header,
			// JOptionPane.INFORMATION_MESSAGE);
		}

		public boolean confirm(String message) {
			return true;
		}

		public String getInput(String header, String message) {
			return "Rubbish message";
		}

		public void setFormFont(Font theFont) {

		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws FileNotFoundException {
		File configSource = new File(CONFIG_SOURCE_FILE);
		if (!configSource.exists()) {
			throw new FileNotFoundException("Config file does not exist");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		FileUtils.deleteFileOrDirectoryRecursive(CONFIG_FILE);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		theFrame = new MetaDataConfiguratorFrameTest();
		metaDataPresenter = new MetaDataConfiguratorPresenter(theFrame);
		theFrame.setPresenter(metaDataPresenter);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testSetup() {
		formIsVisible = false;
		try {
			metaDataPresenter.setup();
		} catch (Exception ex) {
			fail();
		}
		assertTrue(formIsVisible);
		DefaultComboBoxModel model = (DefaultComboBoxModel) theFrame.cmbDataType
				.getModel();
		assertTrue(model.getSize() == EDataType.values().length);
		assertFalse(theFrame.cursorIsWaiting);
	}

	@Test
	public final void testAddHandlers() {
		// correct additions should already be tested - just test broken stuff
		boolean broken = false;
		try {
			metaDataPresenter.addMainHandlers(theFrame.tabMain);
		} catch (Exception ex) {
			broken = true;
		}
		assertFalse(broken);
		broken = false;
		try {
			metaDataPresenter.addMainHandlers(null);
		} catch (Exception ex) {
			broken = true;
		}
		assertFalse(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(null, null, null, null, null,
					null, null, null, null, null, null, null);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(null, theFrame.cmbDataType,
					theFrame.pnlListItems, theFrame.lstListItems,
					theFrame.lstCmsListItems, theFrame.cmsFieldCombo,
					theFrame.lstDNX, theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi,
					theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList, null,
					theFrame.pnlListItems, theFrame.lstListItems,
					theFrame.lstCmsListItems, theFrame.cmsFieldCombo,
					theFrame.lstDNX, theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi,
					theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, null, theFrame.lstListItems,
					theFrame.lstCmsListItems, theFrame.cmsFieldCombo,
					theFrame.lstDNX, theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi,
					theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertFalse(broken); // This shouldn't break things as there are no
								// handlers for the panel
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems, null,
					theFrame.lstCmsListItems, theFrame.cmsFieldCombo,
					theFrame.lstDNX, theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi,
					theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems,
					theFrame.lstListItems, null, theFrame.cmsFieldCombo,
					theFrame.lstDNX, theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi,
					theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems,
					theFrame.lstListItems, theFrame.lstCmsListItems, null,
					theFrame.lstDNX, theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi,
					theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems,
					theFrame.lstListItems, theFrame.lstCmsListItems,
					theFrame.cmsFieldCombo, null, theFrame.lstDNXDetail,
					theFrame.lstDC, theFrame.lstDCXsi, theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems,
					theFrame.lstListItems, theFrame.lstCmsListItems,
					theFrame.cmsFieldCombo, theFrame.lstDNX, null,
					theFrame.lstDC, theFrame.lstDCXsi, theFrame.lstDCTerms, theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems,
					theFrame.lstListItems, theFrame.lstCmsListItems,
					theFrame.cmsFieldCombo, theFrame.lstDNX,
					theFrame.lstDNXDetail, null, null, theFrame.lstDCTerms,
					theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems,
					theFrame.lstListItems, theFrame.lstCmsListItems,
					theFrame.cmsFieldCombo, theFrame.lstDNX,
					theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi, null,
					theFrame.tabDnxDc);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
		broken = false;
		try {
			metaDataPresenter.addMetaDataHandlers(theFrame.lstDataList,
					theFrame.cmbDataType, theFrame.pnlListItems,
					theFrame.lstListItems, theFrame.lstCmsListItems,
					theFrame.cmsFieldCombo, theFrame.lstDNX,
					theFrame.lstDNXDetail, theFrame.lstDC, theFrame.lstDCXsi, theFrame.lstDCTerms,
					null);
		} catch (Exception ex) {
			broken = true;
		}
		assertTrue(broken);
	}

	private void loadConfigFile() {
		File configSource = new File(CONFIG_SOURCE_FILE);
		File config = new File(CONFIG_FILE);
		if (config.exists()) {
			FileUtils.deleteFileOrDirectoryRecursive(config);
		}
		try {
			FileUtils.copyFile(configSource, config);
		} catch (Exception ex) {
			fail();
		}
		metaDataPresenter.setup();
		metaDataPresenter.loadConfigurationFile(CONFIG_FILE, true);
	}

	@Test
	public final void testLoadData() {
		loadConfigFile();
		assertTrue(metaDataPresenter.getIsDirty() == false);
		DefaultListModel model = (DefaultListModel) theFrame.lstDataList
				.getModel();
		assertTrue(model.getSize() == 9);
		theFrame.lstDataList.setSelectedIndex(0);
		assertTrue(theFrame.getTheData().equals(
				theFrame.lstDataList.getSelectedValue()));
		if (theFrame.lstDNX.getSelectedIndex() == 0) {
			theFrame.lstDNX.setSelectedIndex(1);
		} else {
			theFrame.lstDNX.setSelectedIndex(0);
		}
		assertTrue(theFrame.lstDNXDetail.getSelectedValue() == null);
		// assertTrue(_presenter.getIsDirty() == true);
		assertTrue(theFrame.getTheData().getDnxType().equals(
				theFrame.lstDNX.getSelectedValue()));
		theFrame.lstDNXDetail.setSelectedIndex(0);
		assertTrue(theFrame.getTheData().getDnxSubType().equals(
				theFrame.lstDNXDetail.getSelectedValue()));
		theFrame.tabDnxDc.setSelectedIndex(1);
		// Ajusted by Ben 2.12.2013
		assertTrue(theFrame.getTheData().isDNX());
		assertFalse(theFrame.getTheData().isDNXState());
		theFrame.lstDC.setSelectedIndex(0);
		assertTrue(theFrame.getTheData().getDcType().equals(
				theFrame.lstDC.getSelectedValue()));
		theFrame.cmbDataType.setSelectedItem(EDataType.Boolean);
		assertTrue(theFrame.getTheData().getDataType()
				.equals(EDataType.Boolean));
		assertFalse(theFrame.pnlListItems.isVisible());
		theFrame.cmbDataType.setSelectedItem(EDataType.MultiSelect);
		assertTrue(theFrame.getTheData().getDataType().equals(
				EDataType.MultiSelect));
		assertTrue(theFrame.pnlListItems.isVisible());
		model = (DefaultListModel) theFrame.lstListItems.getModel();
		assertTrue(model.size() == 0);
		// MetaDataListValues value = MetaDataListValues.create("Stuff",
		// "Stuff");
		metaDataPresenter.addDataLookupItem("Stuff", "Stuff");
		// MetaDataListValues value2 = MetaDataListValues.create("Stuff2",
		// "Stuff2");
		metaDataPresenter.addDataLookupItem("Stuff2", "Stuff2");
		assertTrue(model.size() == 2);
		theFrame.editingLookupValue = false;
		theFrame.lstListItems.setSelectedIndex(0);
		assertTrue(theFrame.getIsEditingLookupValue());
		loadConfigFile();
		model = (DefaultListModel) theFrame.lstDataList.getModel();
		metaDataPresenter.addNewItem();
		assertTrue(model.getSize() == 10);
		assertTrue(theFrame.lstDataList.getSelectedIndex() == 9);
		metaDataPresenter.deleteItem();
		model = (DefaultListModel) theFrame.lstDataList.getModel();
		assertTrue(model.getSize() == 9);
		assertTrue(theFrame.lstDataList.getSelectedValue() == null);
	}

	@Test
	public final void testLookupData() {
		loadConfigFile();
		theFrame.lstDataList.setSelectedIndex(0);
		theFrame.cmbDataType.setSelectedItem(EDataType.MultiSelect);
		assertTrue(theFrame.getTheData().getDataType().equals(
				EDataType.MultiSelect));
		assertTrue(theFrame.pnlListItems.isVisible());
		DefaultListModel model = (DefaultListModel) theFrame.lstListItems
				.getModel();
		assertTrue(model.size() == 0);
		assertFalse(metaDataPresenter.canSaveLookupData(null));
		assertFalse(metaDataPresenter.canSaveLookupData(""));
		assertTrue(metaDataPresenter.canSaveLookupData("Test"));
		assertFalse(metaDataPresenter.canDeleteLookupData());
		boolean failed = false;
		try {
			// Can't save unless something is selected - should add instead
			// MetaDataListValues value = MetaDataListValues.create("Stuff",
			// "Stuff", 0);
			metaDataPresenter.saveDataLookupItem("Stuff", "Stuff");
		} catch (Exception ex) {
			failed = true;
		}
		// assertTrue(failed);
		MetaDataListValues value1 = MetaDataListValues.create("Value 1",
				"Value 1", 0);
		metaDataPresenter.addDataLookupItem(value1.getValue(), value1
				.getDisplay());
		MetaDataListValues value2 = MetaDataListValues.create("Value 2",
				"Value 2", 1);
		metaDataPresenter.addDataLookupItem(value2.getValue(), value2
				.getDisplay());
		MetaDataListValues value3 = MetaDataListValues.create("Value 3",
				"Value 3", 2);
		metaDataPresenter.addDataLookupItem(value3.getValue(), value3
				.getDisplay());
		assertTrue(model.getSize() == 3);
		theFrame.lstListItems.setSelectedIndex(0);
		MetaDataListValues newValue = MetaDataListValues.create("New Value",
				"New Value", 3);
		metaDataPresenter.saveDataLookupItem(newValue.getValue(), newValue
				.getDisplay());
		MetaDataListValues testValue = (MetaDataListValues) theFrame.lstListItems
				.getSelectedValue();
		assertTrue(testValue.getDisplay().equals(newValue.getDisplay()));
		assertTrue(testValue.getValue().equals(newValue.getValue()));
		assertTrue(metaDataPresenter.canMoveLookupItem(false));
		assertFalse(metaDataPresenter.canMoveLookupItem(true));
		metaDataPresenter.moveLookupItem(false);
		assertTrue(metaDataPresenter.canMoveLookupItem(false));
		assertTrue(metaDataPresenter.canMoveLookupItem(true));
		assertTrue(theFrame.lstListItems.getSelectedIndex() == 1);
		metaDataPresenter.moveLookupItem(true);
		assertTrue(metaDataPresenter.canMoveLookupItem(false));
		assertFalse(metaDataPresenter.canMoveLookupItem(true));
		assertTrue(theFrame.lstListItems.getSelectedIndex() == 0);
		theFrame.lstListItems.setSelectedIndex(2);
		assertFalse(metaDataPresenter.canMoveLookupItem(false));
		assertTrue(metaDataPresenter.canMoveLookupItem(true));
		assertTrue(metaDataPresenter.canDeleteLookupData());
		metaDataPresenter.deleteLookupItem();
		assertTrue(model.size() == 2);
		testValue = (MetaDataListValues) model.get(0);
		assertTrue(testValue.getDisplay().equals(newValue.getDisplay()));
		assertTrue(testValue.getValue().equals(newValue.getValue()));
		testValue = (MetaDataListValues) model.get(1);
		assertTrue(testValue.getDisplay().equals(value2.getDisplay()));
		assertTrue(testValue.getValue().equals(value2.getValue()));
	}

	@Test
	public final void testSaveData() {
		loadConfigFile();
		theFrame.lstDataList.setSelectedIndex(0);
		assertFalse(metaDataPresenter.canSave("", "", true));
		theFrame.tabDnxDc.setSelectedIndex(0);
		theFrame.lstDNX.setSelectedIndex(1);
		theFrame.lstDNXDetail.setSelectedIndex(1);
		assertTrue(metaDataPresenter.canSave("Title", "", true));
		theFrame.tabDnxDc.setSelectedIndex(1);
		assertFalse(metaDataPresenter.canSave("Title", "", true));
		assertTrue(metaDataPresenter.canSave("Title", "DC", true));
		File config = new File(CONFIG_FILE);
		loadConfigFile();
		long lastModified = config.lastModified();
		LOG
				.debug("Config file: " + config.getAbsolutePath()
						+ ", Exists? " + config.exists() + ", last modified: "
						+ config.lastModified());
		try {
			Thread.sleep(1000);
		} catch (Exception ex) {
		}
		theFrame.lstDataList.setSelectedIndex(0);
		metaDataPresenter
				.updateData("New Field", "New Description", "", 100, false,
						false, false, false, false, false, false,
						"New DC Extra", false);
		metaDataPresenter.saveConfigurationFile();
		config = new File(CONFIG_FILE);
		LOG
				.debug("After save: " + config.getAbsolutePath() + ", Exists? "
						+ config.exists() + ", last modified: "
						+ config.lastModified());
		assertTrue(lastModified < config.lastModified());
		metaDataPresenter
				.updateData("New Field", "New Description", "", 100, false,
						false, false, false, false, false, false,
						"New DC Extra", false);
		assertTrue(theFrame.getTheData().getDataFieldName().equals("New Field"));
		assertTrue(theFrame.getTheData().getDataFieldDescription().equals(
				"New Description"));
		assertFalse(theFrame.getTheData().getIsCompulsory());
		assertFalse(theFrame.getTheData().getIsSet());
		assertFalse(theFrame.getTheData().getIsSetBySystem());
		assertFalse(theFrame.getTheData().getSavedWithTemplate());
		assertFalse(theFrame.getTheData().getAllowsMultipleRows());
		assertFalse(theFrame.getTheData().getWillBeUploaded());
		assertFalse(theFrame.getTheData().getIsCustomizable());
		metaDataPresenter.updateData("New Field Again",
				"New Description Again", "", 100, true, true, true, true, true,
				true, true, "New DC Extra", true);
		assertTrue(theFrame.getTheData().getDataFieldName().equals(
				"New Field Again"));
		assertTrue(theFrame.getTheData().getDataFieldDescription().equals(
				"New Description Again"));
		assertTrue(theFrame.getTheData().getIsCompulsory());
		assertTrue(theFrame.getTheData().getIsVisible());
		assertFalse(theFrame.getTheData().getIsSet());
		assertTrue(theFrame.getTheData().getIsSetBySystem());
		assertTrue(theFrame.getTheData().getSavedWithTemplate());
		assertTrue(theFrame.getTheData().getAllowsMultipleRows());
		assertTrue(theFrame.getTheData().getWillBeUploaded());
		assertTrue(theFrame.getTheData().getIsCustomizable());
		theFrame.tabDnxDc.setSelectedIndex(1);
		metaDataPresenter.updateData("New Field Again",
				"New Description Again", "", 100, true, true, true, true, true,
				true, true, "New DC Extra", true);
		assertTrue(theFrame.getTheData().getDcType().equals("New DC Extra"));
		theFrame.lstDC.setSelectedIndex(0);
		assertTrue(theFrame.getTheData().getDcType().equals(
				theFrame.lstDC.getSelectedValue()));
	}

	@Test
	public final void testMoveItems() {
		loadConfigFile();
		DefaultListModel model = (DefaultListModel) theFrame.lstDataList
				.getModel();
		theFrame.lstDataList.setSelectedIndex(0);
		assertTrue(metaDataPresenter.canMoveItem(false));
		assertFalse(metaDataPresenter.canMoveItem(true));
		metaDataPresenter.moveItem(false);
		assertTrue(theFrame.lstDataList.getSelectedIndex() == 1);
		assertTrue(metaDataPresenter.canMoveItem(false));
		assertTrue(metaDataPresenter.canMoveItem(true));
		metaDataPresenter.moveItem(true);
		assertTrue(theFrame.lstDataList.getSelectedIndex() == 0);
		assertTrue(metaDataPresenter.canMoveItem(false));
		assertFalse(metaDataPresenter.canMoveItem(true));
		theFrame.lstDataList.setSelectedIndex(model.getSize() - 1);
		assertTrue(metaDataPresenter.canMoveItem(true));
		assertFalse(metaDataPresenter.canMoveItem(false));
	}

	@Test
	public final void testMetaDataTypeImpl() {
		MetaDataTypeImpl meta = MetaDataTypeImpl.create();
		MetaDataTypeImpl meta2 = MetaDataTypeImpl.create();
		assertTrue(meta.toString().equals("No description available"));
		assertFalse(meta.getIsSet());
		assertTrue(meta.isEquivalentTo(meta2, true));
		assertTrue(meta.isEquivalentTo(meta2, false));
		meta.setDataFieldName("Name");
		assertFalse(meta.isEquivalentTo(meta2, true));
		assertFalse(meta.isEquivalentTo(meta2, false));
		meta.setDataFieldName(null);
		meta.setSortOrder(5);
		assertFalse(meta.isEquivalentTo(meta2, true));
		assertTrue(meta.isEquivalentTo(meta2, false));
		meta2.setDataFieldName("Name");
		assertFalse(meta.isEquivalentTo(meta2, true));
		assertFalse(meta.isEquivalentTo(meta2, false));
		meta.setSortOrder(0);
		assertFalse(meta.isEquivalentTo(meta2, true));
		assertFalse(meta.isEquivalentTo(meta2, false));
		meta2.setDataFieldName(null);
		// Should set ALL properties here to test the duplicate function
		meta.setAllowsMultipleRows(true);
		meta.setDataFieldDescription("Desc");
		meta.setDataFieldName("Name");
		try {
			meta.setDataFieldValue("Value");
		} catch (Exception ex) {
			fail();
		}
		assertTrue(meta.getIsSet());
		meta.setDataType(EDataType.Text);
		meta.setDcType("DCType");
		meta.setDnxSubType("DNXSubType");
		meta.setDnxType("DNXType");
		meta.setIsCompulsory(true);
		meta.setIsDNX(true);
		meta.setIsSetBySystem(true);
		ArrayList<MetaDataListValues> values = new ArrayList<MetaDataListValues>();
		values.add(new MetaDataListValues("Item 1", "Item 1", 0));
		values.add(new MetaDataListValues("Item 2", "Item 2", 1));
		meta.setListItems(values);
		meta.setSavedWithTemplate(true);
		meta.setSortOrder(5);
		meta.setWillBeUploaded(true);
		assertFalse(meta.isEquivalentTo(meta2, true));
		assertFalse(meta.isEquivalentTo(meta2, false));
		try {
			meta.duplicate(meta2);
		} catch (Exception ex) {
			fail();
		}
		assertTrue(meta.getAllowsMultipleRows() == meta2
				.getAllowsMultipleRows());
		assertTrue(meta.getDataFieldDescription().equals(
				meta2.getDataFieldDescription()));
		assertTrue(meta.getDataFieldName().equals(meta2.getDataFieldName()));
		assertTrue(meta.getDataFieldValue().equals(meta2.getDataFieldValue()));
		assertTrue(meta.getDataType().equals(meta2.getDataType()));
		assertTrue(meta.getDcType().equals(meta2.getDcType()));
		assertTrue(meta.getDnxSubType().equals(meta2.getDnxSubType()));
		assertTrue(meta.getDnxType().equals(meta2.getDnxType()));
		assertTrue(meta.getIsCompulsory() == meta2.getIsCompulsory());
		assertTrue(meta.isDNX() == meta2.isDNX());
		assertTrue(meta.getIsSetBySystem() == meta2.getIsSetBySystem());
		assertTrue(meta.getListItems().size() == meta2.getListItems().size());
		for (int i = 0; i < meta.getListItems().size(); i++) {
			assertTrue(meta.getListItems().get(i).equals(
					meta2.getListItems().get(i)));
		}
		assertTrue(meta.getSavedWithTemplate() == meta2.getSavedWithTemplate());
		assertTrue(meta.getSortOrder() == meta2.getSortOrder());
		assertTrue(meta.getWillBeUploaded() == meta2.getWillBeUploaded());
		assertTrue(meta.isEquivalentTo(meta2, true));
		assertTrue(meta.isEquivalentTo(meta2, false));
		meta2.setSortOrder(100);
		assertFalse(meta.isEquivalentTo(meta2, true));
		assertTrue(meta.isEquivalentTo(meta2, false));
	}

	@Test
	public final void testMetaDataTableModel() {
		loadConfigFile();
		AppProperties appProperties = null;
		try {
			appProperties = new AppProperties();
			appProperties.setLoggedOnUser("mngroot");
			appProperties.setLoggedOnUserPassword("mngroot");
		} catch (Exception ex) {
			fail();
		}
		MetaDataTableModel model = null;
		try {
			UserGroupData userGroupData = appProperties.getUserData().getUser(
					appProperties.getLoggedOnUser()).getUserGroupData();
			model = MetaDataTableModel.create(userGroupData,
					MetaDataFields.ECMSSystem.CMS2);
		} catch (Exception ex) {
			fail();
		}
		assertFalse(model.hasEmptyRow());
		model.addRow(MetaDataTypeImpl.create());
		assertTrue(model.hasEmptyRow());
		try {
			model.setMetaDataType(MetaDataFields.ECMSSystem.CMS1);
		} catch (Exception ex) {
			fail();
		}
		MetaDataFields fields = model.getMetaData();
		// MetaDataFields fields = null;
		// try {
		// fields = MetaDataFields.create(_configFile);
		// } catch (Exception ex) {
		// fail();
		// }
		// model.setMetaData(fields);
		// assertFalse(model.hasEmptyRow());
		try {
			model.setCMSDescription("CMS Description");
		} catch (Exception ex) {
			fail();
		}
		assertTrue(fields.getCMSDescription().equals("CMS Description"));
		try {
			model.setCMSID("CMS ID");
		} catch (Exception ex) {
			fail();
		}
		assertTrue(fields.getCMSID("CMS2").equals("CMS ID"));
		try {
			model.setCMSSystem("CMS System");
		} catch (Exception ex) {
			fail();
		}
		assertTrue(fields.getCMSSystem().equals("CMS System"));
		model.setValueAt("Test", 0, 0);
		assertTrue(fields.getAt(0).getDataFieldName().equals("Test"));
		model.setValueAt("Test Value", 0, 1);
		assertTrue(fields.getAt(0).getDataFieldValue().equals("Test Value"));
	}

	@Test
	public final void testMetaDataCellEditor() {
		loadConfigFile();
		AppProperties appProperties = null;
		try {
			appProperties = new AppProperties();
			appProperties.setLoggedOnUser("mngroot");
			appProperties.setLoggedOnUserPassword("mngroot");
		} catch (Exception ex) {
			fail();
		}
		MetaDataTableModel model = null;
		try {
			UserGroupData userGroupData = appProperties.getUserData().getUser(
					appProperties.getLoggedOnUser()).getUserGroupData();
			model = MetaDataTableModel.create(userGroupData,
					MetaDataFields.ECMSSystem.CMS2);
		} catch (Exception ex) {
			fail();
		}
		Font standardFont = new Font("Arial", 0, 12);
		MetaDataElementCellEditor editor = new MetaDataElementCellEditor(
				standardFont);
		JTable table = new JTable();
		table.setModel(model);
		IMetaDataTypeExtended meta = model.getRow(0);

		meta.setDataType(EDataType.Boolean);
		String value = "true";
		Component comp = editor.getTableCellEditorComponent(table, value, true,
				0, 1);
		assertTrue(comp instanceof JCheckBox);
		JCheckBox chk = (JCheckBox) comp;
		assertTrue(chk.getSelectedObjects() != null);
		value = "false";
		comp = editor.getTableCellEditorComponent(table, value, true, 0, 1);
		assertTrue(editor.getCellEditorValue().equals(value));

		meta.setDataType(EDataType.Date);
		String dateFormat = "dd/MM/yyyy";
		SimpleDateFormat f = new SimpleDateFormat(dateFormat);
		value = "09/04/2008";
		comp = editor.getTableCellEditorComponent(table, value, true, 0, 1);
		assertTrue(comp instanceof JXDatePicker);
		JXDatePicker dt = (JXDatePicker) comp;
		Date theDate = dt.getDate();
		String theDateString = f.format(theDate);
		assertTrue(value.equals(theDateString));
		assertTrue(editor.getCellEditorValue().equals(value));

		meta.setDataType(EDataType.Integer);
		value = "15";
		comp = editor.getTableCellEditorComponent(table, value, true, 0, 1);
		assertTrue(comp instanceof WholeNumberField);
		WholeNumberField num = (WholeNumberField) comp;
		assertTrue(num.getValue() == 15);
		assertTrue(editor.getCellEditorValue().equals(value));

		meta.setDataType(EDataType.MultiSelect);
		ArrayList<MetaDataListValues> values = new ArrayList<MetaDataListValues>();
		MetaDataListValues value1 = new MetaDataListValues("Item 1", "Item 1", 0);
		values.add(value1);
		MetaDataListValues value2 = new MetaDataListValues("Item 2", "Item 2", 1);
		values.add(value2);
		MetaDataListValues value3 = new MetaDataListValues("Item 3", "Item 3", 2);
		values.add(value3);
		meta.setListItems(values);
		comp = editor.getTableCellEditorComponent(table, value, true, 0, 1);
		assertTrue(comp instanceof JComboBox);
		JComboBox cmb = (JComboBox) comp;
		cmb.setSelectedIndex(3);
		MetaDataListValues valueTest = (MetaDataListValues) cmb
				.getSelectedItem();
		assertTrue(valueTest.equals(value3));
		assertTrue(editor.getCellEditorValue().equals(value3));

		meta.setDataType(EDataType.RealNumber);
		value = "30.5";
		comp = editor.getTableCellEditorComponent(table, value, true, 0, 1);
		assertTrue(comp instanceof DecimalNumberField);
		DecimalNumberField num2 = (DecimalNumberField) comp;
		assertTrue(num2.getValue() == 30.5);
		assertTrue(editor.getCellEditorValue().equals("30.50000"));

		meta.setDataType(EDataType.Text);
		value = "Hello World";
		comp = editor.getTableCellEditorComponent(table, value, true, 0, 1);
		assertTrue(comp instanceof JTextField);
		JTextField txt = (JTextField) comp;
		assertTrue(txt.getText().equals(value));
		assertTrue(editor.getCellEditorValue().equals(value));
	}
}
