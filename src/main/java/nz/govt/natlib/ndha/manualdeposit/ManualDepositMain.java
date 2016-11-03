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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.common.ilsquery.CmsRecord;
import nz.govt.natlib.ndha.common.ilsquery.DcRecord;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.common.mets.FileSystemObject.SortBy;
import nz.govt.natlib.ndha.common.mets.FileTypesSingleton;
import nz.govt.natlib.ndha.common.mets.StructMap;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositPresenter.TreeDragSource;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositPresenter.TreeDropTarget;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.BulkUploadForm;
import nz.govt.natlib.ndha.manualdeposit.bulkupload.IBulkUpload;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.CustomizeMetaDataForm;
import nz.govt.natlib.ndha.manualdeposit.customizemetadata.ICustomizeMetaDataEditorView;
import nz.govt.natlib.ndha.manualdeposit.customui.LabelTextPair;
import nz.govt.natlib.ndha.manualdeposit.dialogs.About;
import nz.govt.natlib.ndha.manualdeposit.dialogs.ApplicationProperties;
import nz.govt.natlib.ndha.manualdeposit.dialogs.DuplicateFiles;
import nz.govt.natlib.ndha.manualdeposit.dialogs.EnterDirectory;
import nz.govt.natlib.ndha.manualdeposit.dialogs.MissingFiles;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;
import nz.govt.natlib.ndha.manualdeposit.metadata.PersonalSettings;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData.UserGroupDesc;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEventsEditor;
import nz.govt.natlib.ndha.manualdeposit.provenanceevent.ProvenanceEventsEditorView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.MultiSplitLayout;

public class ManualDepositMain extends javax.swing.JFrame implements
		IManualDepositMainFrame {

	private enum ScreenPosition {
		search, fileSystem, intellectualEntity, structMap, metaData, jobQueue, notSpecified;
	}

	private static final long serialVersionUID = -7713788695550768940L;
	private final static Log LOG = LogFactory.getLog(ManualDepositMain.class);
	private ManualDepositPresenter depositPresenter;
	// DO NOT DELETE THE NEXT 3 ITEMS
	// They must be present for drag drop even though they don't appear to be in
	// use.
	@SuppressWarnings("unused")
	private TreeDragSource theDragSource;
	@SuppressWarnings("unused")
	private TreeDropTarget theDragTarget;
	@SuppressWarnings("unused")
	private TreeDropTarget theDragTargetSelf;
	private FormControl theFormControl;
	private final static String SPLIT_IE_ATTR = "splitAddIEDivider";
	private final static String SPLIT_MAIN_ATTR = "splitMainDivider";
	private final static String SPLIT_MAIN_DETAIL_ATTR = "splitMainDetailDivider";
	private final static String SPLIT_MAIN_RIGHT_ATTR = "splitMainRightDivider";
	private final static String META_DATA_COL_1_ATTR = "MetaDataColWidth1";
	private final static String META_DATA_COL_2_ATTR = "MetaDataColWidth2";
	private final static String JOB_QUEUE_DIVIDER_1_ATTR = "JobQueueDivider1";
	private final static String JOB_QUEUE_DIVIDER_2_ATTR = "JobQueueDivider2";
	private final static String JOB_QUEUE_DIVIDER_3_ATTR = "JobQueueDivider3";
	private final static String JOB_QUEUE_DIVIDER_4_ATTR = "JobQueueDivider4";
	private final static int MINIMUM_JOB_QUEUE_WINDOW_HEIGHT = 70;
	private AppProperties theAppProperties;
	private UserGroupData theUserGroupData = null;
	private Font theStandardFont = new Font("Tahoma", Font.PLAIN, 11);
	private String theCurrentDirectory = null;
	private String theOldTemplate = "";
	private String theOldStructTemplate = "";
	private int theSearchHeight = 0;
	private int theMetadataHeight = 0;
	private ScreenPosition theCurrentCursorPosition = ScreenPosition.search;
	private String theSettingsPath = "./";
	private int theOldHeight1 = 0;
	private int theOldHeight2 = 0;
	private int theOldHeight3 = 0;
	private int theOldHeight4 = 0;
	private String title = "NLNZ Indigo ";
	private boolean isPublishedUserGroup = false;

	/**
	 * Creates new form ManualDepositMain
	 */
	public ManualDepositMain() {
		super();
		initComponents(); // NOPMD
	}

	public javax.swing.JFrame getComponent() {
		return this;
	}

	public void setPresenter(final ManualDepositPresenter thePresenter) {
		depositPresenter = thePresenter;
	}

	@SuppressWarnings("serial")
	public void setupScreen(final AppProperties appProperties,
			final String settingsPath) throws Exception {
		LOG.debug("setupScreen");
		this.setJMenuBar(mnuMain);
		theSettingsPath = settingsPath;
		LOG.debug("setupScreen, setting provenance event presenter");
		theAppProperties = appProperties;
		theUserGroupData = theAppProperties.getUserData().getUser(
				theAppProperties.getLoggedOnUser()).getUserGroupData();
		final boolean searchVisible = (theUserGroupData.isIncludeCMS2Search()
				|| theUserGroupData.isIncludeCMS1Search()
				|| theUserGroupData.isIncludeProducerList() || theUserGroupData
				.isIncludeNoCMSOption());
		pnlCmsReference.setVisible(searchVisible);
		mnuViewShowSearch.setVisible(searchVisible);
		//change radio button text if user group is published.
		if (theUserGroupData.getUserGroupDesc().equals(UserGroupDesc.Published)){
			isPublishedUserGroup = true;
			rbnStaffMediated.setText("Audio");
			rbnNoCmsRef.setText("Video");
		}
		if (theUserGroupData.isIncludeCMS2Search()) {
			rbnCMS2.setSelected(true);
		} else if (theUserGroupData.isIncludeCMS1Search()) {
			rbnCMS1.setSelected(true);
		} else if (theUserGroupData.isIncludeProducerList()) {
			rbnStaffMediated.setSelected(true);
		} else if (theUserGroupData.isIncludeNoCMSOption()) {
			rbnNoCmsRef.setSelected(true);
		}
		rbnCMS2.setVisible(theUserGroupData.isIncludeCMS2Search());
		rbnCMS1.setVisible(theUserGroupData.isIncludeCMS1Search());
		rbnNoCmsRef.setVisible(theUserGroupData.isIncludeNoCMSOption());
		rbnStaffMediated.setVisible(theUserGroupData.isIncludeProducerList());
		rbnCMS1.setText(theAppProperties.getApplicationData().getCMS1Label());
		rbnCMS2.setText(theAppProperties.getApplicationData().getCMS2Label());
		if (theUserGroupData.isIncludeCMS2Search()) {
			rbnCMS2.setSelected(true);
		} else {
			if (theUserGroupData.isIncludeCMS1Search()) {
				rbnCMS1.setSelected(true);
			} else {
				rbnNoCmsRef.setSelected(true);
			}
		}
		setTitle(title + theAppProperties.getAppVersion());
		ClassLoader cLoader = Thread.currentThread().getContextClassLoader();
		java.net.URL imageURL = cLoader.getResource("Indigo_logo_64x64.jpg");
		setIconImage(Toolkit.getDefaultToolkit().getImage(imageURL));
		LOG.debug("setupScreen, setting FormControl");
		try {
			theFormControl = new FormControl(this, theSettingsPath);
			fixBackwardsCompatibility();
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
		LOG.debug("setupScreen, adding handlers");
		depositPresenter.addHandlers(treeFileSystem, treeEntities,
				treeStructMap, cmbSelectTemplate, cmbSelectStructTemplate,
				cmbSortBy, cmbFixityType, tblDetail, tblJobQueueRunning, tblJobQueuePending,
				tblJobQueueFailed, tblJobQueueDeposited, tblJobQueueComplete,
				mnuFileFavourites, lstProducers, lstMaterialFlow);
		LOG.debug("setupScreen, handlers added");
		theUserGroupData.setEmuMaterialFlowID(theUserGroupData.getMaterialFlowID());
		checkButtons();
		setCMSDetails();
		setHotKeyVisibility();
		depositPresenter.checkForInitialLoadScreenSizes(theFormControl,
				splitAddIE, SPLIT_IE_ATTR, splitMain, SPLIT_MAIN_ATTR,
				splitMainDetail, SPLIT_MAIN_DETAIL_ATTR, splitMainRight,
				SPLIT_MAIN_RIGHT_ATTR);
		final Action updateDividersAction = new AbstractAction() {
			public void actionPerformed(final ActionEvent e) {
				Timer t = (Timer) e.getSource();
				t.stop();
				splitAddIE.setDividerLocation(theFormControl.getExtra(
						SPLIT_IE_ATTR, 175));
				splitMain.setDividerLocation(theFormControl.getExtra(
						SPLIT_MAIN_ATTR, 200));
				splitMainDetail.setDividerLocation(theFormControl.getExtra(
						SPLIT_MAIN_DETAIL_ATTR, 200));
				splitMainRight.setDividerLocation(theFormControl.getExtra(
						SPLIT_MAIN_RIGHT_ATTR, 200));
				splitMain.repaint();
				splitMainDetail.repaint();
				splitMainRight.repaint();
				TableColumn col = tblDetail.getColumnModel().getColumn(0);
				col.setPreferredWidth(theFormControl.getExtra(
						META_DATA_COL_1_ATTR, 200));
				col = tblDetail.getColumnModel().getColumn(1);
				col.setPreferredWidth(theFormControl.getExtra(
						META_DATA_COL_2_ATTR, 200));

				MultiSplitLayout layout = mspJobQueue.getMultiSplitLayout();
				layout.setFloatingDividers(false);
				MultiSplitLayout.Split model = (MultiSplitLayout.Split) layout
						.getModel();
				MultiSplitLayout.Divider divider = (MultiSplitLayout.Divider) model
						.getChildren().get(1);
				Rectangle bounds = divider.getBounds();
				int top = theFormControl.getExtra(JOB_QUEUE_DIVIDER_1_ATTR,
						bounds.y);
				bounds.y = top;
				divider.setBounds(bounds);
				theOldHeight1 = top;

				divider = (MultiSplitLayout.Divider) model.getChildren().get(3);
				bounds = divider.getBounds();
				top = theFormControl.getExtra(JOB_QUEUE_DIVIDER_2_ATTR,
						bounds.y);
				bounds.y = top;
				divider.setBounds(bounds);
				theOldHeight2 = top;

				divider = (MultiSplitLayout.Divider) model.getChildren().get(5);
				bounds = divider.getBounds();
				top = theFormControl.getExtra(JOB_QUEUE_DIVIDER_3_ATTR,
						bounds.y);
				bounds.y = top;
				divider.setBounds(bounds);
				theOldHeight3 = top;

				divider = (MultiSplitLayout.Divider) model.getChildren().get(7);
				bounds = divider.getBounds();
				top = theFormControl.getExtra(JOB_QUEUE_DIVIDER_4_ATTR,
						bounds.y);
				bounds.y = top;
				divider.setBounds(bounds);
				theOldHeight4 = top;
			}
		};
		new Timer(200, updateDividersAction).start();
		final PersonalSettings personalSettings = theAppProperties
				.getApplicationData().getPersonalSettings();
		theStandardFont = personalSettings.getStandardFont();
		final SortBy sortBy = personalSettings.getSortFilesBy();
		for (int i = 0; i < cmbSortBy.getItemCount(); i++) {
			final SortBy item = (SortBy) cmbSortBy.getItemAt(i);
			if (item.equals(sortBy)) {
				cmbSortBy.setSelectedIndex(i);
				break;
			}
		}
		setJobQueuePanes();
		LOG.debug("setupScreen, end");
		addHotKeyListener(this);
	}

	public void showMissingFiles(final String settingsPath,
			List<FileSystemObject> filesMissing) {
		final MissingFiles missingFiles = new MissingFiles(this, true,
				settingsPath, filesMissing);
		missingFiles.setVisible(true);
	}
	
	public void showDuplicateFiles(final String settingsPath,
			Set<String> theDuplicateFiles) {
		final DuplicateFiles duplicateFiles = new DuplicateFiles(this, true,
				settingsPath, theDuplicateFiles);
		duplicateFiles.setVisible(true);
	}

	private void fixBackwardsCompatibility() {
		// The following code is included for backwards compatibility
		// The original versions stored a lot of personal application data
		// in the form Extras area
		// The new version stores it all in a PersonalSettings object that is
		// retrieved from ApplicationData.
		// This next piece of code will move any stored personal data from
		// the form Extras into the PersonalSettings file.
		final int favouriteCount = theFormControl.getExtra("FavouriteCount", 0);
		final PersonalSettings personalSettings = theAppProperties
				.getApplicationData().getPersonalSettings();
		if (favouriteCount != 0) {
			for (int i = 0; i < favouriteCount; i++) {
				final String favouriteName = String.format("Favourite%d", i);
				final String favourite = theFormControl.getExtra(favouriteName,
						"");
				personalSettings.addFavourite(favourite);
				theFormControl.deleteExtra(favouriteName);
			}
			theFormControl.deleteExtra("FavouriteCount");
		}
		if (!theFormControl.getExtra("Font", "").equals("")) {
			personalSettings.setFontName(theFormControl.getExtra("Font", ""));
			theFormControl.deleteExtra("Font");
		}
		if (!theFormControl.getExtra("FontSize", "").equals("")) {
			personalSettings.setFontSize(Integer.parseInt(theFormControl
					.getExtra("FontSize", "")));
			theFormControl.deleteExtra("FontSize");
		}
		if (!theFormControl.getExtra("FontBold", "").equals("")) {
			personalSettings.setFontBold(Boolean.parseBoolean(theFormControl
					.getExtra("FontBold", "")));
			theFormControl.deleteExtra("FontBold");
		}
		if (!theFormControl.getExtra("FontItalic", "").equals("")) {
			personalSettings.setFontItalic(Boolean.parseBoolean(theFormControl
					.getExtra("FontItalic", "")));
			theFormControl.deleteExtra("FontItalic");
		}
		if (!theFormControl.getExtra("FontPlain", "").equals("")) {
			personalSettings.setFontPlain(Boolean.parseBoolean(theFormControl
					.getExtra("FontPlain", "")));
			theFormControl.deleteExtra("FontPlain");
		}
		if (!theFormControl.getExtra("SortFilesBy", "").equals("")) {
			final String sortBy = theFormControl.getExtra("SortFilesBy", "");
			for (SortBy sortByTest : SortBy.values()) {
				if (sortByTest.description().equalsIgnoreCase(sortBy)) {
					personalSettings.setSortFilesBy(sortByTest);
					break;
				}
			}
			theFormControl.deleteExtra("SortFilesBy");
		}
		if (!theFormControl.getExtra("CurrentPath", "").equals("")) {
			personalSettings.setCurrentPath(theFormControl.getExtra(
					"CurrentPath", ""));
			theFormControl.deleteExtra("CurrentPath");
		}
	}

	private void addHotKeyListener(final Container container) {
		final Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			final Component component = components[i];
			component.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(final java.awt.event.KeyEvent evt) {
					checkHotKey(evt);
				}
			});
			if (components[i] instanceof Container) {
				addHotKeyListener((Container) component);
			}
		}
	}

	public void showView() {
		setVisible(true);
	}

	public void showError(final String header, final String message) {
		showError(header, message, null);
	}

	public void showError(final String header, final String message,
			final Exception ex) {
		final StringBuffer prompt = new StringBuffer();
		if (message != null) {
			prompt.append(message);
			prompt.append("\n");
		}
		if (ex != null) {
			if (ex.getMessage() != null) {
				prompt.append(ex.getMessage());
				prompt.append("\n");
			}
			final StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			LOG.error(message, ex);
		}
		JOptionPane.showMessageDialog(this, prompt.toString(), header,
				JOptionPane.ERROR_MESSAGE);
	}

	public void showMessage(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean confirm(String message) {
		return confirm(message, false);
	}

	public boolean confirm(String message, boolean useYesNo) {
		int optionType;
		if (useYesNo) {
			optionType = JOptionPane.YES_NO_OPTION;
		} else {
			optionType = JOptionPane.OK_CANCEL_OPTION;
		}
		return (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				optionType) == JOptionPane.OK_OPTION);
	}

	public String getInput(String header, String message) {
		return JOptionPane.showInputDialog(this, message, header,
				JOptionPane.QUESTION_MESSAGE);
	}

	public String getInput(String header, String message, String defaultInput) {
		return JOptionPane.showInputDialog(this, message, defaultInput);
	}

	public void setWaitCursor(boolean isWaiting) {
		if (isWaiting) {
			Cursor hourglass = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglass);
		} else {
			Cursor normal = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normal);
		}
	}

	public void setProgressLevel(int percentage) {
		if(percentage == 0){
			jProgressBar1.setValue(percentage);
			jProgressBar1.setStringPainted(false);
			jProgressBar1.repaint();
		}
		else{
			jProgressBar1.setValue(percentage);
			jProgressBar1.setStringPainted(true);
			//jProgressBar1.setString("Almost finished: " + percentage + "%");
			jProgressBar1.repaint();
		}
		
	}
	
	public void setFormFont(Font theFont) {
		theStandardFont = theFont;
		UIManager.put("OptionPane.messageFont", theStandardFont);
		UIManager.put("OptionPane.buttonFont", theStandardFont);
		UIManager.put("TextField.font", theStandardFont);
		FormUtilities.setFormFont(this, theStandardFont);
		depositPresenter.setStandardFont(theStandardFont);
	}

	private boolean canSearch() {
		boolean searchFieldHasData = false;
		for (LabelTextPair pair : getSearchFields()) {
			if (pair.getField().getText() != null
					&& !pair.getField().getText().equals("")) {
				searchFieldHasData = true;
				break;
			}
		}
		return searchFieldHasData;
	}

	public void checkButtons() {
		cmdDoSearch.setEnabled(canSearch());
		cmdSaveAsSharedTemplate.setEnabled(depositPresenter
				.canSaveSharedTemplate());
		cmdSaveAsTemplate.setEnabled(depositPresenter.canSaveTemplate());
		cmdDeleteTemplate.setEnabled(depositPresenter.canDeleteTemplate());
		cmdSaveSharedStructTemplate.setEnabled(depositPresenter
				.canSaveSharedStructTemplate());
		cmdSaveStructTemplate.setEnabled(depositPresenter
				.canSaveStructTemplate());
		cmdDeleteStructTemplate.setEnabled(depositPresenter
				.canDeleteStructTemplate());
		cmdLoad.setEnabled(depositPresenter.canSubmit());
		cmdCancel.setEnabled(depositPresenter.canCancel());
		mnuTemplatesDelete.setEnabled(depositPresenter.canDeleteTemplate());
		mnuTemplatesSave.setEnabled(depositPresenter.canSaveTemplate());
		mnuTemplatesSaveShared.setEnabled(depositPresenter
				.canSaveSharedTemplate());
		mnuShowBulkLoad.setEnabled(depositPresenter.bulkUploadsPresent());
		cmdCustomizeMetaData.setEnabled(depositPresenter.isCustomizeMetaData());
	}
	
	public void setIELabel(boolean submitOK) {
		if (submitOK){
			
			int ieSize = depositPresenter.getEntities().size();
			lblNoOfIEs.setText(ieSize + " IEs are added for loading.");
			//lblNoOfIEs.setForeground(java.awt.Color.blue);
			lblNoOfIEs.setVisible(true);
			
			if (ieSize == 1){
				int fileSize = depositPresenter.getEntities().get(0).getAllFiles().size();
				lblNoOfFiles.setText(fileSize + " Files are added for loading.");
				//lblNoOfFiles.setForeground(java.awt.Color.blue);
				lblNoOfFiles.setVisible(true);
			}
		}else {
			lblNoOfIEs.setVisible(false);
			lblNoOfFiles.setVisible(false);
		}
	}
	
	public void setProgressBarVisible(boolean isVisible) {
		jProgressBar1.setVisible(isVisible);
	}

	public void setSearchType(MetaDataFields.ECMSSystem cmsSystem) {
		boolean needsChange = true;
		switch (cmsSystem) {
		case CMS1:
			needsChange = !rbnCMS1.isSelected();
			rbnCMS1.setSelected(true);
			break;
		case CMS2:
			needsChange = !rbnCMS2.isSelected();
			rbnCMS2.setSelected(true);
			break;
		case NoSystem:
			needsChange = !rbnNoCmsRef.isSelected();
			rbnNoCmsRef.setSelected(true);
			break;
		case StaffMediated:
			needsChange = !rbnStaffMediated.isSelected();
			rbnStaffMediated.setSelected(true);
			break;
		default:
			break;
		}
		if (needsChange) {
			setCMSDetails();
		}
		checkButtons();
	}

	private void closeForm(java.awt.event.WindowEvent evt) {
		boolean doClose = true;
		if (depositPresenter.jobsRunning()) {
			String message = "You currently have jobs outstanding which will be automatically reloaded the next time this application runs\n\nDo you wish to close?";
			if (!confirm(message)) {
				doClose = false;
			}
		}
		if (doClose) {
			if (theUserGroupData.isIncludeNoCMSOption()
					|| theUserGroupData.isIncludeProducerList()
					|| theUserGroupData.isIncludeCMS2Search()
					|| theUserGroupData.isIncludeCMS1Search()) {
				theFormControl.setExtra(SPLIT_IE_ATTR, splitAddIE
						.getDividerLocation());
			}
			theFormControl.setExtra(SPLIT_MAIN_ATTR, splitMain
					.getDividerLocation());
			theFormControl.setExtra(SPLIT_MAIN_DETAIL_ATTR, splitMainDetail
					.getDividerLocation());
			theFormControl.setExtra(SPLIT_MAIN_RIGHT_ATTR, splitMainRight
					.getDividerLocation());
			TableColumn col = tblDetail.getColumnModel().getColumn(0);
			theFormControl.setExtra(META_DATA_COL_1_ATTR, col.getWidth());
			col = tblDetail.getColumnModel().getColumn(1);
			theFormControl.setExtra(META_DATA_COL_2_ATTR, col.getWidth());
			MultiSplitLayout.Split model = (MultiSplitLayout.Split) mspJobQueue
					.getMultiSplitLayout().getModel();
			MultiSplitLayout.Divider divider = (MultiSplitLayout.Divider) model
					.getChildren().get(1);
			Rectangle bounds = divider.getBounds();
			theFormControl.setExtra(JOB_QUEUE_DIVIDER_1_ATTR, bounds.y);
			divider = (MultiSplitLayout.Divider) model.getChildren().get(3);
			bounds = divider.getBounds();
			theFormControl.setExtra(JOB_QUEUE_DIVIDER_2_ATTR, bounds.y);
			divider = (MultiSplitLayout.Divider) model.getChildren().get(5);
			bounds = divider.getBounds();
			theFormControl.setExtra(JOB_QUEUE_DIVIDER_3_ATTR, bounds.y);
			divider = (MultiSplitLayout.Divider) model.getChildren().get(7);
			bounds = divider.getBounds();
			theFormControl.setExtra(JOB_QUEUE_DIVIDER_4_ATTR, bounds.y);
			theFormControl.closing(evt);
			System.exit(0);
		}
	}

	private void fileProperties() {
		ApplicationProperties appProperties = new ApplicationProperties(this,
				true, theStandardFont, theSettingsPath, theAppProperties
						.getApplicationData().getPersonalSettings());
		appProperties.setVisible(true);
		depositPresenter.setupFavourites();
		depositPresenter.refreshJobQueue();
	}

	public IBulkUpload createBulkUploadForm() {
		BulkUploadForm retVal = BulkUploadForm.create(this, true,
				theSettingsPath);
		return retVal;
	}
	
	public ICustomizeMetaDataEditorView createCustomizeMetaDataForm() {
		CustomizeMetaDataForm retVal = CustomizeMetaDataForm.create(this, true, theSettingsPath);
		return retVal;
	}

	private void manageStructMapFileDesc() {
		StructMapFileDescManagement sppStructMapFileDescManagement = new StructMapFileDescManagement(
				this, true, theSettingsPath, theUserGroupData
						.getFileTypesPropFile());
		sppStructMapFileDescManagement.setFormFont(theStandardFont);
		sppStructMapFileDescManagement.showView();
		try {
			FileTypesSingleton fileTypes = FileTypesSingleton
					.getFileTypesSingleton();
			fileTypes.reload();
		} catch (Exception ex) {
			showError("Error reloading structure map file types", ex
					.getMessage());
		}
	}

	private List<LabelTextPair> getSearchFields() {
		List<LabelTextPair> labelTextPairs = new ArrayList<LabelTextPair>();
		labelTextPairs.add(LabelTextPair.create(lblSearch1, txtSearch1));
		labelTextPairs.add(LabelTextPair.create(lblSearch2, txtSearch2));
		labelTextPairs.add(LabelTextPair.create(lblSearch3, txtSearch3));
		labelTextPairs.add(LabelTextPair.create(lblSearch4, txtSearch4));
		labelTextPairs.add(LabelTextPair.create(lblSearch5, txtSearch5));
		labelTextPairs.add(LabelTextPair.create(lblSearch6, txtSearch6));
		labelTextPairs.add(LabelTextPair.create(lblSearch7, txtSearch7));
		labelTextPairs.add(LabelTextPair.create(lblSearch8, txtSearch8));
		labelTextPairs.add(LabelTextPair.create(lblSearch9, txtSearch9));
		labelTextPairs.add(LabelTextPair.create(lblSearch10, txtSearch10));
		labelTextPairs.add(LabelTextPair.create(lblSearch11, txtSearch11));
		labelTextPairs.add(LabelTextPair.create(lblSearch12, txtSearch12));
		labelTextPairs.add(LabelTextPair.create(lblSearch13, txtSearch13));
		labelTextPairs.add(LabelTextPair.create(lblSearch14, txtSearch14));
		labelTextPairs.add(LabelTextPair.create(lblSearch15, txtSearch15));
		return labelTextPairs;
	}

	private void showSearchFields(ILSQueryType.eServerType serverType) {
		depositPresenter.showSearchFields(serverType, getSearchFields());
	}

	private void setCMSDetails() {
		CardLayout layout = (CardLayout) (pnlSearchDetail.getLayout());
		CmsRecord rec = new DcRecord("");
		List<LabelTextPair> searchFields;
		if (rbnCMS2.isSelected()) {
			pnlSearchDetail.setVisible(true);
			cmdDoSearch.setVisible(true);
			layout.show(pnlSearchDetail, "Search");
			if (isPublishedUserGroup){
				theUserGroupData.setMaterialFlowID(theUserGroupData.getSerialMaterialFlowID());
				showSearchFields(ILSQueryType.eServerType.CMS1);
				searchFields = depositPresenter.getSearchAttributes().getSearchAttributes(ILSQueryType.eServerType.CMS1).getTheSearchFields();
			} else{
				theUserGroupData.setMaterialFlowID(theUserGroupData.getEmuMaterialFlowID());
				showSearchFields(ILSQueryType.eServerType.CMS2);
				searchFields = depositPresenter.getSearchAttributes().getSearchAttributes(ILSQueryType.eServerType.CMS2).getTheSearchFields();
			}
			//If CMS 2  is not working disable "Search" button.
			if (searchFields.size() == 1){
				cmdDoSearch.setVisible(false);
			}
			depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.CMS2);
		} else if (rbnCMS1.isSelected()) {
			if (isPublishedUserGroup){
				theUserGroupData.setMaterialFlowID(theUserGroupData.getMonoMaterialFlowID());
			}else {
				theUserGroupData.setMaterialFlowID(theUserGroupData.getAlmaMaterialFlowID());
			}
			pnlSearchDetail.setVisible(true);
			cmdDoSearch.setVisible(true);
			layout.show(pnlSearchDetail, "Search");
			showSearchFields(ILSQueryType.eServerType.CMS1);
			searchFields = depositPresenter.getSearchAttributes().getSearchAttributes(ILSQueryType.eServerType.CMS1).getTheSearchFields();
			//If CMS 1 system is not working disable "Search" button
			if (searchFields.size() == 1){
				cmdDoSearch.setVisible(false);
			}
			depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.CMS1);
		} else if (rbnStaffMediated.isSelected()) {
			if (isPublishedUserGroup){
				theUserGroupData.setMaterialFlowID(theUserGroupData.getAudioMaterialFlowID());
				pnlSearchDetail.setVisible(true);
				cmdDoSearch.setVisible(true);
				layout.show(pnlSearchDetail, "Search");
				showSearchFields(ILSQueryType.eServerType.CMS1);
				searchFields = depositPresenter.getSearchAttributes().getSearchAttributes(ILSQueryType.eServerType.CMS1).getTheSearchFields();
				//If CMS 1 system is not working disable "Search" button
				if (searchFields.size() == 1){
					cmdDoSearch.setVisible(false);
				}
				//depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.CMS1);
			}else {
				pnlSearchDetail.setVisible(true);
				layout.show(pnlSearchDetail, "SelectProducer");
				//depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.StaffMediated);
			}
			depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.StaffMediated);
		} else if (rbnNoCmsRef.isSelected()) {
			if (isPublishedUserGroup){
				theUserGroupData.setMaterialFlowID(theUserGroupData.getVideoMaterialFlowID());
				pnlSearchDetail.setVisible(true);
				cmdDoSearch.setVisible(true);
				layout.show(pnlSearchDetail, "Search");
				showSearchFields(ILSQueryType.eServerType.CMS1);
				searchFields = depositPresenter.getSearchAttributes().getSearchAttributes(ILSQueryType.eServerType.CMS1).getTheSearchFields();
				//If CMS 1 system is not working disable "Search" button
				if (searchFields.size() == 1){
					cmdDoSearch.setVisible(false);
				}
				//depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.CMS2);
			}else {
				pnlSearchDetail.setVisible(false);
				//depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.NoSystem);
			}
			depositPresenter.setCMSResults(rec, MetaDataFields.ECMSSystem.NoSystem);
		} 
	}

	private void setDragSourceFileSystem() {
		theDragSource = this.depositPresenter.new TreeDragSource(
				treeFileSystem, DnDConstants.ACTION_COPY_OR_MOVE);
		theDragTarget = this.depositPresenter.new TreeDropTarget(treeEntities);
		theDragTargetSelf = null;
	}

	private void setDragSourceEntity() {
		theDragSource = this.depositPresenter.new TreeDragSource(treeEntities,
				DnDConstants.ACTION_COPY_OR_MOVE);
		theDragTarget = this.depositPresenter.new TreeDropTarget(treeStructMap);
		theDragTargetSelf = this.depositPresenter.new TreeDropTarget(
				treeEntities);
	}

	private void setDragSourceStructMap() {
		theDragSource = this.depositPresenter.new TreeDragSource(treeStructMap,
				DnDConstants.ACTION_COPY_OR_MOVE);
		theDragTarget = this.depositPresenter.new TreeDropTarget(treeStructMap);
		theDragTargetSelf = this.depositPresenter.new TreeDropTarget(
				treeStructMap);
	}

	private String getTemplateName() {
		return getInput("Template Name", "Please enter new template name");
	}

	private void clearMetaData() {
		cmbSelectTemplate.setSelectedIndex(0);
	}

	private void saveStructTemplate() {
		try {
			String templateName = getTemplateName();
			if ((templateName != null) && (!templateName.equals(""))) {
				depositPresenter.saveStructTemplate(templateName);
			}
		} catch (Exception ex) {
			this.showError("Error saving template", ex.getMessage());
		}
		checkButtons();
	}

	private void deleteStructTemplate() {
		if (confirm("Delete template?")) {
			depositPresenter.deleteStructTemplate();
			checkButtons();
		}
	}

	private void saveStructSharedTemplate() {
		try {
			String templateName = getTemplateName();
			if ((templateName != null) && (!templateName.equals(""))) {
				depositPresenter.saveSharedStructTemplate(templateName);
			}
		} catch (Exception ex) {
			this.showError("Error saving template", ex.getMessage());
		}
		checkButtons();
	}

	private void saveTemplate() {
		try {
			String templateName = getTemplateName();
			if ((templateName != null) && (!templateName.equals(""))) {
				depositPresenter.saveTemplate(templateName);
			}
		} catch (Exception ex) {
			this.showError("Error saving template", ex.getMessage());
		}
		checkButtons();
	}

	private void deleteTemplate() {
		if (confirm("Delete template?")) {
			depositPresenter.deleteTemplate();
			checkButtons();
		}
	}

	private void saveSharedTemplate() {
		try {
			String templateName = getTemplateName();
			if ((templateName != null) && (!templateName.equals(""))) {
				depositPresenter.saveSharedTemplate(templateName);
			}
		} catch (Exception ex) {
			this.showError("Error saving template", ex.getMessage());
		}
		checkButtons();
	}

	public SortBy getCurrentSortBy() {
		if (cmbSortBy.getSelectedItem() == null) {
			return SortBy.FileName;
		} else {
			return (SortBy) cmbSortBy.getSelectedItem();
		}
	}

	public void setCurrentDirectory(String currentDirectory) {
		theCurrentDirectory = currentDirectory;
	}

	private void setSortBy() {
		DefaultTreeModel model = (DefaultTreeModel) treeFileSystem.getModel();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
		if (node.getUserObject() instanceof FileSystemObject) {
			FileSystemObject fso = (FileSystemObject) node.getUserObject();
			fso.setSortBy(getCurrentSortBy());
			theAppProperties.getApplicationData().getPersonalSettings()
					.setSortFilesBy(getCurrentSortBy());
			depositPresenter.addFileSystemRoot(fso, false, false,
					theCurrentDirectory);
		}
	}

	private void showMetaData(boolean changeSelected) {
		if (changeSelected) {
			mnuViewShowMetaData.setSelected(!mnuViewShowMetaData.isSelected());
		}
		if (!mnuViewShowMetaData.isSelected()) {
			theMetadataHeight = splitMainDetail.getDividerLocation();
		}
		pnlDetail.setVisible(mnuViewShowMetaData.isSelected());
		if (mnuViewShowMetaData.isSelected()) {
			splitMainDetail.setDividerLocation(theMetadataHeight);
		}
	}

	private void showSearch(boolean changeSelected) {
		if (changeSelected) {
			mnuViewShowSearch.setSelected(!mnuViewShowSearch.isSelected());
		}
		if (!mnuViewShowSearch.isSelected()) {
			theSearchHeight = splitAddIE.getDividerLocation();
		}
		pnlCmsReference.setVisible(mnuViewShowSearch.isSelected());
		if (mnuViewShowSearch.isSelected()) {
			splitAddIE.setDividerLocation(theSearchHeight);
		}
	}

	private void processFileSystemKey(char key) {
		TreePath[] path = treeFileSystem.getSelectionPaths();
		if (path != null) {
			setWaitCursor(true);
			for (int i = 0; i < path.length; i++) {

			}
			JPopupMenu menu = depositPresenter.processFileTreeKeyPress(key,
					path);
			if (menu != null) {
				menu.show(treeFileSystem, 50, 120);
			}
			setWaitCursor(false);
		}
	}

	private void moveItem(boolean moveUp) {
		if ((moveUp && mnuHotKeysMoveFileUp.isEnabled())
				|| (!moveUp && mnuHotKeysMoveFileDown.isEnabled())) {
			if (theCurrentCursorPosition == ScreenPosition.intellectualEntity) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeEntities
						.getSelectionPath().getLastPathComponent();
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
						.getParent();
				FileSystemObject file = (FileSystemObject) node.getUserObject();
				depositPresenter.moveIEFile(file, nodeParent.getUserObject(),
						moveUp);
			} else { // Must be in the Struct Map
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeStructMap
						.getSelectionPath().getLastPathComponent();
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
						.getParent();
				depositPresenter.moveStructObject(node.getUserObject(),
						nodeParent.getUserObject(), moveUp);
			}
		}
	}

	private void setHotKeyVisibility() {
		boolean fileSystemEntityRootNotSet = ((theCurrentCursorPosition == ScreenPosition.fileSystem) && (!depositPresenter
				.getEntityRootSet()));
		mnuHotKeysMenu.setVisible(fileSystemEntityRootNotSet);
		mnuHotKeysSetIE.setVisible(fileSystemEntityRootNotSet);
		mnuHotKeysSetEachFileIE.setVisible(fileSystemEntityRootNotSet
				&& depositPresenter.getIncludeMultiEntityMenuItem());
		boolean fileSystemEntityRootSet = ((theCurrentCursorPosition == ScreenPosition.fileSystem) && (depositPresenter
				.getEntityRootSet()));
		mnuHotKeysDigitalOriginal.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('d'));
		mnuHotKeysAccessCopy.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('a'));
                mnuHotKeysAccessCopyHigh.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('h'));
                mnuHotKeysAccessCopyMedium.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('i'));
                mnuHotKeysAccessCopyLow.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('l'));
                mnuHotKeysAccessCopyEpub.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('b'));
                mnuHotKeysAccessCopyPdf.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('c'));                
		mnuHotKeysModifiedMaster.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('m'));
		mnuHotKeysPreservationCopy.setVisible(fileSystemEntityRootSet
				&& depositPresenter.canAddRepresentationType('p'));
		mnuHotKeysMakeStruct.setVisible(false);
		mnuHotKeysSelectAllFiles.setVisible(false);
		if (theCurrentCursorPosition == ScreenPosition.fileSystem) {
			TreePath[] paths = treeFileSystem.getSelectionPaths();
			mnuHotKeysOpenFile.setVisible(true);
			mnuHotKeysStoreAsFavourite.setVisible(true);
			mnuHotKeysOpenFile.setEnabled(depositPresenter.canOpenFiles(paths));
			mnuHotKeysStoreAsFavourite.setEnabled(depositPresenter
					.canStoreFavourites(paths));
			mnuHotKeysSetIE.setEnabled(depositPresenter.canSetIE());
			mnuHotKeysUseFileForIE
					.setEnabled(depositPresenter.canSetFileAsIE());
		} else {
			mnuHotKeysOpenFile.setVisible(false);
			mnuHotKeysStoreAsFavourite.setVisible(false);
		}
		if (theCurrentCursorPosition == ScreenPosition.intellectualEntity) {
			mnuHotKeysMoveFileUp.setVisible(true);
			mnuHotKeysMoveFileDown.setVisible(true);
			mnuHotKeysMoveFileUp.setText("<Alt>Up - Move file up IE");
			mnuHotKeysMoveFileDown.setText("<Alt>Down - Move file down IE");
			mnuHotKeysDelete
					.setText("<Delete> - Delete file(s) or folder(s) from IE");
			mnuHotKeysDelete.setEnabled(depositPresenter.canDeleteEntityItem());
			mnuHotKeysMakeStruct.setVisible(true);
			mnuHotKeysSelectAllFiles.setVisible(true);
			mnuHotKeysMakeStruct.setEnabled(depositPresenter
					.canCreateAutoStructItem());
			mnuHotKeysSelectAllFiles.setEnabled(depositPresenter
					.canCreateAutoStructItem());
			if (treeEntities.getSelectionPath() == null) {
				mnuHotKeysMoveFileUp.setEnabled(false);
				mnuHotKeysMoveFileDown.setEnabled(false);
			} else {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeEntities
						.getSelectionPath().getLastPathComponent();
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
						.getParent();
				if (nodeParent != null
						&& (node.getUserObject() instanceof FileSystemObject)) {
					FileSystemObject file = (FileSystemObject) node
							.getUserObject();
					mnuHotKeysMoveFileUp.setEnabled(depositPresenter
							.canMoveIEFile(file, nodeParent.getUserObject(),
									true));
					mnuHotKeysMoveFileDown.setEnabled(depositPresenter
							.canMoveIEFile(file, nodeParent.getUserObject(),
									false));
				} else {
					mnuHotKeysMoveFileUp.setEnabled(false);
					mnuHotKeysMoveFileDown.setEnabled(false);
				}
			}
		} else if (theCurrentCursorPosition == ScreenPosition.structMap) {
			mnuHotKeysMoveFileUp.setVisible(true);
			mnuHotKeysMoveFileDown.setVisible(true);
			mnuHotKeysMoveFileUp.setText("<Alt>Up - Move file up Struct Map");
			mnuHotKeysMoveFileDown
					.setText("<Alt>Down - Move file down Struct Map");
			mnuHotKeysDelete
					.setText("<Delete> - Delete file or structure item(s) from structure map");
			mnuHotKeysDelete.setEnabled(depositPresenter.canDeleteStructItem());
			if (treeStructMap.getSelectionPath() == null) {
				mnuHotKeysMoveFileUp.setEnabled(false);
				mnuHotKeysMoveFileDown.setEnabled(false);
			} else {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeStructMap
						.getSelectionPath().getLastPathComponent();
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node
						.getParent();
				if (nodeParent != null
						&& (node.getUserObject() instanceof FileSystemObject)
						|| (node.getUserObject() instanceof StructMap)) {
					mnuHotKeysMoveFileUp.setEnabled(depositPresenter
							.canMoveStructObject(node.getUserObject(),
									nodeParent.getUserObject(), true));
					mnuHotKeysMoveFileDown.setEnabled(depositPresenter
							.canMoveStructObject(node.getUserObject(),
									nodeParent.getUserObject(), false));
				} else {
					mnuHotKeysMoveFileUp.setEnabled(false);
					mnuHotKeysMoveFileDown.setEnabled(false);
				}
			}
		} else {
			mnuHotKeysDelete.setVisible(false);
			mnuHotKeysMoveFileUp.setVisible(false);
			mnuHotKeysMoveFileDown.setVisible(false);
		}
	}
	
	private void jobQueueMousePressedCommon(java.awt.event.MouseEvent evt,
			JTable table) {
		if (evt.isPopupTrigger()) {
			if (table.getSelectedRowCount() == 0) {
				int row = table.rowAtPoint(evt.getPoint());
				if (row > -1) {
					table.setRowSelectionInterval(row, row);
				}
			}
			JPopupMenu menu = depositPresenter.getJobQueueMenu((JTable) evt
					.getSource());
			if (menu != null) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void jobQueueMouseReleaseCommon(java.awt.event.MouseEvent evt,
			JTable table) {
		if (evt.isPopupTrigger()) {
			if (table.getSelectedRowCount() == 0) {
				int row = table.rowAtPoint(evt.getPoint());
				if (row > -1) {
					table.setRowSelectionInterval(row, row);
				}
			}
			JPopupMenu menu = depositPresenter.getJobQueueMenu((JTable) evt
					.getSource());
			if (menu != null) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void setJobQueuePanes() {
		String layoutDef = "(COLUMN (LEAF name=running weight=0.1) (LEAF name=pending weight=0.2) (LEAF name=failed weight=0.1) (LEAF name=deposited weight=0.3) (LEAF name=complete weight=0.3))";
		MultiSplitLayout.Node modelRoot = MultiSplitLayout
				.parseModel(layoutDef);
		mspJobQueue.getMultiSplitLayout().setModel(modelRoot);
		mspJobQueue.add(pnlJobQueueRunning, "running");
		mspJobQueue.add(pnlJobQueuePending, "pending");
		mspJobQueue.add(pnlJobQueueFailed, "failed");
		mspJobQueue.add(pnlJobQueueDeposited, "deposited");
		mspJobQueue.add(pnlJobQueueComplete, "complete");
	}

	private void checkJobQueueWindowSizes() {
		MultiSplitLayout.Split model = (MultiSplitLayout.Split) mspJobQueue
				.getMultiSplitLayout().getModel();
		MultiSplitLayout.Divider divider1 = (MultiSplitLayout.Divider) model
				.getChildren().get(1);
		Rectangle bounds1 = divider1.getBounds();
		int height1 = bounds1.y;
		MultiSplitLayout.Divider divider2 = (MultiSplitLayout.Divider) model
				.getChildren().get(3);
		Rectangle bounds2 = divider2.getBounds();
		int height2 = bounds2.y;
		MultiSplitLayout.Divider divider3 = (MultiSplitLayout.Divider) model
				.getChildren().get(5);
		Rectangle bounds3 = divider3.getBounds();
		int height3 = bounds3.y;
		MultiSplitLayout.Divider divider4 = (MultiSplitLayout.Divider) model
				.getChildren().get(7);
		Rectangle bounds4 = divider4.getBounds();
		int height4 = bounds4.y;
		int totalHeight = mspJobQueue.getHeight();
		boolean movingUp;
		if ((height1 > theOldHeight1) || (height2 > theOldHeight2)
				|| (height3 > theOldHeight3) || (height4 > theOldHeight4)) {
			movingUp = false;
		} else {
			movingUp = true;
		}
		if (movingUp) {
			if (height4 - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT < height3) {
				height4 = height3 + MINIMUM_JOB_QUEUE_WINDOW_HEIGHT;
			}
			if (height3 - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT < height2) {
				height3 = height2 + MINIMUM_JOB_QUEUE_WINDOW_HEIGHT;
			}
			if (height2 - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT < height1) {
				height2 = height1 + MINIMUM_JOB_QUEUE_WINDOW_HEIGHT;
			}
			if (height1 - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT < 0) {
				height1 = MINIMUM_JOB_QUEUE_WINDOW_HEIGHT;
			}
		} else {
			if (height4 + MINIMUM_JOB_QUEUE_WINDOW_HEIGHT > totalHeight) {
				height4 = totalHeight - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT + 1;
			}
			if (height3 + MINIMUM_JOB_QUEUE_WINDOW_HEIGHT > height4) {
				height3 = height4 - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT + 1;
			}
			if (height2 + MINIMUM_JOB_QUEUE_WINDOW_HEIGHT > height3) {
				height2 = height3 - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT + 1;
			}
			if (height1 + MINIMUM_JOB_QUEUE_WINDOW_HEIGHT > height2) {
				height1 = height2 - MINIMUM_JOB_QUEUE_WINDOW_HEIGHT + 1;
			}
		}
		theOldHeight1 = height1;
		theOldHeight2 = height2;
		theOldHeight3 = height3;
		theOldHeight4 = height4;
		bounds1.y = height1;
		divider1.setBounds(bounds1);
		bounds2.y = height2;
		divider2.setBounds(bounds2);
		bounds3.y = height3;
		divider3.setBounds(bounds3);
		bounds4.y = height4;
		divider4.setBounds(bounds4);
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceButtonGroup = new javax.swing.ButtonGroup();
        mnuMain = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuFileProperties = new javax.swing.JMenuItem();
        mnuFileFavourites = new javax.swing.JMenu();
        mnuFileSelectDirectory = new javax.swing.JMenuItem();
        mnuManageStructMapFileDesc = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuFileExit = new javax.swing.JMenuItem();
        mnuTemplates = new javax.swing.JMenu();
        mnuTemplatesSave = new javax.swing.JMenuItem();
        mnuTemplatesDelete = new javax.swing.JMenuItem();
        mnuTemplatesSaveShared = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuTemplatesClear = new javax.swing.JMenuItem();
        mnuView = new javax.swing.JMenu();
        mnuViewShowSearch = new javax.swing.JCheckBoxMenuItem();
        mnuViewShowMetaData = new javax.swing.JCheckBoxMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuShowBulkLoad = new javax.swing.JMenuItem();
        mnuHotKeys = new javax.swing.JMenu();
        mnuHotKeysMenu = new javax.swing.JMenuItem();
        mnuHotKeysSetIE = new javax.swing.JMenuItem();
        mnuHotKeysSetEachFileIE = new javax.swing.JMenuItem();
        mnuHotKeysUseFileForIE = new javax.swing.JMenuItem();
        mnuHotKeysOpenFile = new javax.swing.JMenuItem();
        mnuHotKeysStoreAsFavourite = new javax.swing.JMenuItem();
        mnuHotKeysDigitalOriginal = new javax.swing.JMenuItem();
        mnuHotKeysPreservationCopy = new javax.swing.JMenuItem();
        mnuHotKeysModifiedMaster = new javax.swing.JMenuItem();
        mnuHotKeysAccessCopy = new javax.swing.JMenuItem();
        mnuHotKeysDelete = new javax.swing.JMenuItem();
        mnuHotKeysMoveFileUp = new javax.swing.JMenuItem();
        mnuHotKeysMoveFileDown = new javax.swing.JMenuItem();
        mnuHotKeysMakeStruct = new javax.swing.JMenuItem();
        mnuHotKeysSelectAllFiles = new javax.swing.JMenuItem();
        mnuHotKeysAccessCopyHigh = new javax.swing.JMenuItem();
        mnuHotKeysAccessCopyMedium = new javax.swing.JMenuItem();
        mnuHotKeysAccessCopyLow = new javax.swing.JMenuItem();
        mnuHotKeysAccessCopyEpub = new javax.swing.JMenuItem();
        mnuHotKeysAccessCopyPdf = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuHelpContents = new javax.swing.JMenuItem();
        mnuHelpAbout = new javax.swing.JMenuItem();
        tabMain = new javax.swing.JTabbedPane();
        pnlAddIE = new javax.swing.JPanel();
        splitAddIE = new javax.swing.JSplitPane();
        pnlCmsReference = new javax.swing.JPanel();
        pnlSource = new javax.swing.JPanel();
        rbnCMS2 = new javax.swing.JRadioButton();
        rbnCMS1 = new javax.swing.JRadioButton();
        rbnStaffMediated = new javax.swing.JRadioButton();
        rbnNoCmsRef = new javax.swing.JRadioButton();
        pnlSearchDetail = new javax.swing.JPanel();
        pnlSelectProducer = new javax.swing.JPanel();
        scrlProducerList = new javax.swing.JScrollPane();
        lstProducers = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        txtProducerFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstMaterialFlow = new javax.swing.JList();
        jLabel11 = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        txtSearch1 = new javax.swing.JTextField();
        lblSearch1 = new javax.swing.JLabel();
        txtSearch2 = new javax.swing.JTextField();
        lblSearch2 = new javax.swing.JLabel();
        txtSearch3 = new javax.swing.JTextField();
        lblSearch3 = new javax.swing.JLabel();
        txtSearch4 = new javax.swing.JTextField();
        lblSearch4 = new javax.swing.JLabel();
        txtSearch5 = new javax.swing.JTextField();
        lblSearch5 = new javax.swing.JLabel();
        txtSearch6 = new javax.swing.JTextField();
        lblSearch6 = new javax.swing.JLabel();
        txtSearch7 = new javax.swing.JTextField();
        lblSearch7 = new javax.swing.JLabel();
        txtSearch8 = new javax.swing.JTextField();
        lblSearch8 = new javax.swing.JLabel();
        txtSearch9 = new javax.swing.JTextField();
        lblSearch9 = new javax.swing.JLabel();
        txtSearch10 = new javax.swing.JTextField();
        lblSearch10 = new javax.swing.JLabel();
        txtSearch11 = new javax.swing.JTextField();
        lblSearch11 = new javax.swing.JLabel();
        txtSearch12 = new javax.swing.JTextField();
        lblSearch12 = new javax.swing.JLabel();
        txtSearch13 = new javax.swing.JTextField();
        lblSearch13 = new javax.swing.JLabel();
        txtSearch14 = new javax.swing.JTextField();
        lblSearch14 = new javax.swing.JLabel();
        txtSearch15 = new javax.swing.JTextField();
        lblSearch15 = new javax.swing.JLabel();
        cmdDoSearch = new javax.swing.JButton();
        pnlTrees = new javax.swing.JPanel();
        splitMainDetail = new javax.swing.JSplitPane();
        splitMain = new javax.swing.JSplitPane();
        pnlFileSystem = new javax.swing.JPanel();
        scrlFileSystem = new javax.swing.JScrollPane();
        treeFileSystem = new javax.swing.JTree();
        lblSelectFiles = new javax.swing.JLabel();
        cmbSortBy = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cmbFixityType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        pnlEntity = new javax.swing.JPanel();
        pnlRightSide = new javax.swing.JPanel();
        splitMainRight = new javax.swing.JSplitPane();
        pnlIntellectualEntity = new javax.swing.JPanel();
        scrlEntities = new javax.swing.JScrollPane();
        treeEntities = new javax.swing.JTree();
        lblIE = new javax.swing.JLabel();
        pnlStructMapParent = new javax.swing.JPanel();
        pnlStructMap = new javax.swing.JPanel();
        lblStructureMap = new javax.swing.JLabel();
        scrlStructMap = new javax.swing.JScrollPane();
        treeStructMap = new javax.swing.JTree();
        jLabel21 = new javax.swing.JLabel();
        cmbSelectStructTemplate = new javax.swing.JComboBox();
        cmdSaveStructTemplate = new javax.swing.JButton();
        cmdDeleteStructTemplate = new javax.swing.JButton();
        cmdSaveSharedStructTemplate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        pnlDetail = new javax.swing.JPanel();
        scrlDetail = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        cmbSelectTemplate = new javax.swing.JComboBox();
        cmdSaveAsTemplate = new javax.swing.JButton();
        cmdDeleteTemplate = new javax.swing.JButton();
        cmdSaveAsSharedTemplate = new javax.swing.JButton();
        cmdClearMetaData = new javax.swing.JButton();
        pnlButtons = new javax.swing.JPanel();
        cmdCancel = new javax.swing.JButton();
        cmdLoad = new javax.swing.JButton();
        cmdAddProvenanceNote = new javax.swing.JButton();
        cmdCustomizeMetaData = new javax.swing.JButton();
        lblNoOfIEs = new javax.swing.JLabel();
        lblNoOfFiles = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar(0, 100);
        pnlJobQueue = new javax.swing.JPanel();
        mspJobQueue = new org.jdesktop.swingx.JXMultiSplitPane();
        pnlJobQueueRunning = new javax.swing.JPanel();
        scrlJobQueueRunning = new javax.swing.JScrollPane();
        tblJobQueueRunning = new javax.swing.JTable();
        pnlJobQueuePending = new javax.swing.JPanel();
        scrlJobQueuePending = new javax.swing.JScrollPane();
        tblJobQueuePending = new javax.swing.JTable();
        pnlJobQueueFailed = new javax.swing.JPanel();
        scrlJobQueueFailed = new javax.swing.JScrollPane();
        tblJobQueueFailed = new javax.swing.JTable();
        pnlJobQueueDeposited = new javax.swing.JPanel();
        scrlJobQueueDeposited = new javax.swing.JScrollPane();
        tblJobQueueDeposited = new javax.swing.JTable();
        pnlJobQueueComplete = new javax.swing.JPanel();
        scrlJobQueueComplete = new javax.swing.JScrollPane();
        tblJobQueueComplete = new javax.swing.JTable();

        mnuFile.setMnemonic('F');
        mnuFile.setText("File");

        mnuFileProperties.setMnemonic('P');
        mnuFileProperties.setText("Properties");
        mnuFileProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFilePropertiesActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileProperties);

        mnuFileFavourites.setText("Favourite Directories");
        mnuFile.add(mnuFileFavourites);

        mnuFileSelectDirectory.setText("Find Directory");
        mnuFileSelectDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileSelectDirectoryActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileSelectDirectory);

        mnuManageStructMapFileDesc.setMnemonic('S');
        mnuManageStructMapFileDesc.setText("Manage Structure Map File Descriptions");
        mnuManageStructMapFileDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuManageStructMapFileDescActionPerformed(evt);
            }
        });
        mnuFile.add(mnuManageStructMapFileDesc);
        mnuFile.add(jSeparator1);

        mnuFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        mnuFileExit.setMnemonic('x');
        mnuFileExit.setText("Exit");
        mnuFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileExit);

        mnuMain.add(mnuFile);

        mnuTemplates.setMnemonic('T');
        mnuTemplates.setText("Templates");

        mnuTemplatesSave.setMnemonic('S');
        mnuTemplatesSave.setText("Save Template");
        mnuTemplatesSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTemplatesSaveActionPerformed(evt);
            }
        });
        mnuTemplates.add(mnuTemplatesSave);

        mnuTemplatesDelete.setMnemonic('D');
        mnuTemplatesDelete.setText("Delete Template");
        mnuTemplatesDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTemplatesDeleteActionPerformed(evt);
            }
        });
        mnuTemplates.add(mnuTemplatesDelete);

        mnuTemplatesSaveShared.setMnemonic('V');
        mnuTemplatesSaveShared.setText("Save as Shared Template");
        mnuTemplatesSaveShared.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTemplatesSaveSharedActionPerformed(evt);
            }
        });
        mnuTemplates.add(mnuTemplatesSaveShared);
        mnuTemplates.add(jSeparator2);

        mnuTemplatesClear.setMnemonic('C');
        mnuTemplatesClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTemplatesClearActionPerformed(evt);
            }
        });
        mnuTemplates.add(mnuTemplatesClear);

        mnuMain.add(mnuTemplates);

        mnuView.setMnemonic('V');
        mnuView.setText("View");

        mnuViewShowSearch.setMnemonic('1');
        mnuViewShowSearch.setSelected(true);
        mnuViewShowSearch.setText("Show Search (<Ctrl>S)");
        mnuViewShowSearch.setActionCommand("Show Search (<ctrl>S)");
        mnuViewShowSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViewShowSearchActionPerformed(evt);
            }
        });
        mnuView.add(mnuViewShowSearch);

        mnuViewShowMetaData.setMnemonic('2');
        mnuViewShowMetaData.setSelected(true);
        mnuViewShowMetaData.setText("Show Metadata (<Ctrl>M)");
        mnuViewShowMetaData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViewShowMetaDataActionPerformed(evt);
            }
        });
        mnuView.add(mnuViewShowMetaData);
        mnuView.add(jSeparator3);

        mnuShowBulkLoad.setText("Show Bulk Load Screen");
        mnuShowBulkLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuShowBulkLoadActionPerformed(evt);
            }
        });
        mnuView.add(mnuShowBulkLoad);

        mnuMain.add(mnuView);

        mnuHotKeys.setMnemonic('K');
        mnuHotKeys.setText("Hot Keys");

        mnuHotKeysMenu.setMnemonic('M');
        mnuHotKeysMenu.setText("<Alt>M - Display File Menu");
        mnuHotKeysMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysMenuActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysMenu);

        mnuHotKeysSetIE.setMnemonic('S');
        mnuHotKeysSetIE.setText("<Alt>S - Select current directory as IE root");
        mnuHotKeysSetIE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysSetIEActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysSetIE);

        mnuHotKeysSetEachFileIE.setMnemonic('E');
        mnuHotKeysSetEachFileIE.setText("<Alt>E - Make each file an IE");
        mnuHotKeysSetEachFileIE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysSetEachFileIEActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysSetEachFileIE);

        mnuHotKeysUseFileForIE.setMnemonic('S');
        mnuHotKeysUseFileForIE.setText("<Alt>S - Select file to describe the IE");
        mnuHotKeysUseFileForIE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysUseFileForIEActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysUseFileForIE);

        mnuHotKeysOpenFile.setMnemonic('O');
        mnuHotKeysOpenFile.setText("<Alt>O - Open Item(s)");
        mnuHotKeysOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysOpenFileActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysOpenFile);

        mnuHotKeysStoreAsFavourite.setMnemonic('F');
        mnuHotKeysStoreAsFavourite.setText("<Alt> F - Store Directory As Favourite");
        mnuHotKeysStoreAsFavourite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysStoreAsFavouriteActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysStoreAsFavourite);

        mnuHotKeysDigitalOriginal.setMnemonic('D');
        mnuHotKeysDigitalOriginal.setText("<Alt>D - Add selected files to a new Digital Original");
        mnuHotKeysDigitalOriginal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysDigitalOriginalActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysDigitalOriginal);

        mnuHotKeysPreservationCopy.setMnemonic('P');
        mnuHotKeysPreservationCopy.setText("<Alt>P - Add selected files to a new Preservation Copy");
        mnuHotKeysPreservationCopy.setActionCommand("<Alt>P - Add selected files to a new Preservation Master");
        mnuHotKeysPreservationCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysPreservationCopyActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysPreservationCopy);

        mnuHotKeysModifiedMaster.setMnemonic('M');
        mnuHotKeysModifiedMaster.setText("<Alt>M - Add selected files to a new Modified Master");
        mnuHotKeysModifiedMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysModifiedMasterActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysModifiedMaster);

        mnuHotKeysAccessCopy.setMnemonic('A');
        mnuHotKeysAccessCopy.setText("<Alt>A - Add selected files to a new Access Copy");
        mnuHotKeysAccessCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysAccessCopyActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysAccessCopy);

        mnuHotKeysDelete.setMnemonic('L');
        mnuHotKeysDelete.setText("<Delete> - Delete file or folder");
        mnuHotKeysDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysDeleteActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysDelete);

        mnuHotKeysMoveFileUp.setMnemonic('U');
        mnuHotKeysMoveFileUp.setText("<Alt>Up - Move file up");
        mnuHotKeysMoveFileUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysMoveFileUpActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysMoveFileUp);

        mnuHotKeysMoveFileDown.setMnemonic('D');
        mnuHotKeysMoveFileDown.setText("<Alt>Down - Move file down");
        mnuHotKeysMoveFileDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysMoveFileDownActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysMoveFileDown);

        mnuHotKeysMakeStruct.setMnemonic('S');
        mnuHotKeysMakeStruct.setText("<Alt>S - Create a Structure Map item from selected files");
        mnuHotKeysMakeStruct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysMakeStructActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysMakeStruct);

        mnuHotKeysSelectAllFiles.setMnemonic('R');
        mnuHotKeysSelectAllFiles.setText("<Alt>R - Select the same file from all representation types & make a struct item");
        mnuHotKeysSelectAllFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysSelectAllFilesActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysSelectAllFiles);

        mnuHotKeysAccessCopyHigh.setMnemonic('H');
        mnuHotKeysAccessCopyHigh.setText("<Alt>H - Add selected files to a new Access Copy HIGH");
        mnuHotKeysAccessCopyHigh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysAccessCopyHighActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysAccessCopyHigh);

        mnuHotKeysAccessCopyMedium.setMnemonic('I');
        mnuHotKeysAccessCopyMedium.setText("<Alt>I - Add selected files to a new Access Copy MEDIUM");
        mnuHotKeysAccessCopyMedium.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysAccessCopyMediumActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysAccessCopyMedium);

        mnuHotKeysAccessCopyLow.setMnemonic('L');
        mnuHotKeysAccessCopyLow.setText("<Alt>L - Add selected files to a new Access Copy LOW");
        mnuHotKeysAccessCopyLow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysAccessCopyLowActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysAccessCopyLow);

        mnuHotKeysAccessCopyEpub.setMnemonic('B');
        mnuHotKeysAccessCopyEpub.setText("<Alt>B - Add selected files to a new Access Copy EPUB");
        mnuHotKeysAccessCopyEpub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysAccessCopyEpubActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysAccessCopyEpub);

        mnuHotKeysAccessCopyPdf.setMnemonic('C');
        mnuHotKeysAccessCopyPdf.setText("<Alt>C - Add selected files to a new Access Copy PDF");
        mnuHotKeysAccessCopyPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHotKeysAccessCopyPdfActionPerformed(evt);
            }
        });
        mnuHotKeys.add(mnuHotKeysAccessCopyPdf);

        mnuMain.add(mnuHotKeys);

        mnuHelp.setMnemonic('H');
        mnuHelp.setText("Help");

        mnuHelpContents.setMnemonic('C');
        mnuHelpContents.setText("Contents");
        mnuHelpContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHelpContentsActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuHelpContents);

        mnuHelpAbout.setMnemonic('A');
        mnuHelpAbout.setText("About");
        mnuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHelpAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuHelpAbout);

        mnuMain.add(mnuHelp);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        //setTitle(title);
        setName("frmMain"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabMain.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jobQueueFocusGained(evt);
            }
        });

        pnlAddIE.setFont(new java.awt.Font("Tahoma", 0, 5)); // NOI18N

        splitAddIE.setDividerLocation(175);
        splitAddIE.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlCmsReference.setBorder(javax.swing.BorderFactory.createTitledBorder("CMS Reference"));
        pnlCmsReference.setName("pnlCMSReference"); // NOI18N
        pnlCmsReference.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pnlCmsReferenceFocusGained(evt);
            }
        });

        pnlSource.setBorder(javax.swing.BorderFactory.createTitledBorder("Source"));

        sourceButtonGroup.add(rbnCMS2);
        rbnCMS2.setSelected(true);
        rbnCMS2.setText("Tapuhi");
        rbnCMS2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbnCMS2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbnCMS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnCMS2SearchTypeChanged(evt);
            }
        });

        sourceButtonGroup.add(rbnCMS1);
        rbnCMS1.setText("CMS1");
        rbnCMS1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbnCMS1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbnCMS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnCMS1SearchTypeChanged(evt);
            }
        });

        sourceButtonGroup.add(rbnStaffMediated);
        rbnStaffMediated.setText("Staff Mediated");
        rbnStaffMediated.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbnStaffMediated.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbnStaffMediated.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnStaffMediatedActionPerformed(evt);
            }
        });

        sourceButtonGroup.add(rbnNoCmsRef);
        rbnNoCmsRef.setText("Currently no CMS reference");
        rbnNoCmsRef.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbnNoCmsRef.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbnNoCmsRef.setName("rbnNoCmsRef"); // NOI18N
        rbnNoCmsRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnNoCmsRefSearchTypeChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlSourceLayout = new org.jdesktop.layout.GroupLayout(pnlSource);
        pnlSource.setLayout(pnlSourceLayout);
        pnlSourceLayout.setHorizontalGroup(
            pnlSourceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSourceLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSourceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbnCMS2)
                    .add(rbnCMS1)
                    .add(rbnNoCmsRef)
                    .add(rbnStaffMediated))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSourceLayout.setVerticalGroup(
            pnlSourceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSourceLayout.createSequentialGroup()
                .add(rbnCMS2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbnCMS1)
                .add(4, 4, 4)
                .add(rbnStaffMediated)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbnNoCmsRef)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pnlSearchDetail.setLayout(new java.awt.CardLayout());

        pnlSelectProducer.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Producer"));
        pnlSelectProducer.setMaximumSize(null);

        lstProducers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstProducersValueChanged(evt);
            }
        });
        lstProducers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstProducersMouseReleased(evt);
            }
        });
        scrlProducerList.setViewportView(lstProducers);

        jLabel2.setText("Filter");

        txtProducerFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProducerFilterUpdate(evt);
            }
        });

        lstMaterialFlow.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstMaterialFlowValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstMaterialFlow);

        jLabel11.setText("Select Material Flow");

        org.jdesktop.layout.GroupLayout pnlSelectProducerLayout = new org.jdesktop.layout.GroupLayout(pnlSelectProducer);
        pnlSelectProducer.setLayout(pnlSelectProducerLayout);
        pnlSelectProducerLayout.setHorizontalGroup(
            pnlSelectProducerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectProducerLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSelectProducerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSelectProducerLayout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(txtProducerFilter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                    .add(scrlProducerList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(pnlSelectProducerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSelectProducerLayout.createSequentialGroup()
                        .add(jLabel11)
                        .add(174, 174, 174))
                    .add(pnlSelectProducerLayout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        pnlSelectProducerLayout.setVerticalGroup(
            pnlSelectProducerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectProducerLayout.createSequentialGroup()
                .add(pnlSelectProducerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel11)
                    .add(txtProducerFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectProducerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .add(scrlProducerList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlSearchDetail.add(pnlSelectProducer, "SelectProducer");

        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder("Search for"));

        txtSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch1.setText("jLabel3");

        txtSearch2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch2.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch2.setText("jLabel3");

        txtSearch3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch3.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch3.setText("jLabel3");

        txtSearch4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch4.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch4.setText("jLabel3");

        txtSearch5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch5.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch5.setText("jLabel3");

        txtSearch6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch6.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch6.setText("jLabel3");

        txtSearch7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch7.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch7.setText("jLabel3");

        txtSearch8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch8.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch8.setText("jLabel3");

        txtSearch9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch9.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch9.setText("jLabel3");

        txtSearch10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch10.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch10.setText("jLabel3");

        txtSearch11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch11.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch11.setText("jLabel3");

        txtSearch12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch12.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch12.setText("jLabel3");

        txtSearch13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch13.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch13.setText("jLabel3");

        txtSearch14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch14.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch14.setText("jLabel3");

        txtSearch15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });
        txtSearch15.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ManualDepositMain.this.caretUpdate(evt);
            }
        });

        lblSearch15.setText("jLabel3");

        cmdDoSearch.setText("Search");
        cmdDoSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSearch(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlSearchLayout = new org.jdesktop.layout.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblSearch3)
                    .add(lblSearch2)
                    .add(lblSearch1)
                    .add(lblSearch4))
                .add(18, 18, 18)
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtSearch4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .add(txtSearch3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .add(txtSearch2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .add(txtSearch1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblSearch8)
                    .add(lblSearch7)
                    .add(lblSearch6)
                    .add(lblSearch5))
                .add(24, 24, 24)
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtSearch8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .add(txtSearch7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .add(txtSearch6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .add(txtSearch5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblSearch12)
                    .add(lblSearch9)
                    .add(lblSearch10)
                    .add(lblSearch11))
                .add(24, 24, 24)
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtSearch12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .add(txtSearch11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .add(txtSearch10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                    .add(txtSearch9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(cmdDoSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlSearchLayout.createSequentialGroup()
                        .add(lblSearch15)
                        .add(24, 24, 24)
                        .add(txtSearch15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                    .add(pnlSearchLayout.createSequentialGroup()
                        .add(lblSearch13)
                        .add(24, 24, 24)
                        .add(txtSearch13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                    .add(pnlSearchLayout.createSequentialGroup()
                        .add(lblSearch14)
                        .add(24, 24, 24)
                        .add(txtSearch14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSearchLayout.createSequentialGroup()
                .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSearchLayout.createSequentialGroup()
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch1)
                            .add(txtSearch1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch2)
                            .add(txtSearch2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch3)
                            .add(txtSearch3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(pnlSearchLayout.createSequentialGroup()
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch5)
                            .add(txtSearch5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txtSearch9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblSearch9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch6)
                            .add(txtSearch6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblSearch10)
                            .add(txtSearch10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch7)
                            .add(txtSearch7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblSearch11)
                            .add(txtSearch11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch8)
                            .add(lblSearch4)
                            .add(txtSearch8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblSearch12)
                            .add(txtSearch12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txtSearch4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSearchLayout.createSequentialGroup()
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch13)
                            .add(txtSearch13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch14)
                            .add(txtSearch14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSearchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblSearch15)
                            .add(txtSearch15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                        .add(cmdDoSearch)))
                .addContainerGap())
        );

        pnlSearchDetail.add(pnlSearch, "Search");

        org.jdesktop.layout.GroupLayout pnlCmsReferenceLayout = new org.jdesktop.layout.GroupLayout(pnlCmsReference);
        pnlCmsReference.setLayout(pnlCmsReferenceLayout);
        pnlCmsReferenceLayout.setHorizontalGroup(
            pnlCmsReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCmsReferenceLayout.createSequentialGroup()
                .add(pnlSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSearchDetail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlCmsReferenceLayout.setVerticalGroup(
            pnlCmsReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCmsReferenceLayout.createSequentialGroup()
                .add(pnlCmsReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnlSearchDetail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(24, 24, 24))
        );

        splitAddIE.setLeftComponent(pnlCmsReference);
        pnlCmsReference.getAccessibleContext().setAccessibleName("CMS Reference Search");

        pnlTrees.setBorder(javax.swing.BorderFactory.createTitledBorder("Intellectual Entity"));
        pnlTrees.setAutoscrolls(true);
        pnlTrees.setName("pnlIntellectualEntity"); // NOI18N

        splitMainDetail.setDividerLocation(200);
        splitMainDetail.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitMainDetail.setContinuousLayout(true);
        splitMainDetail.setDoubleBuffered(true);

        splitMain.setDividerLocation(200);

        pnlFileSystem.setPreferredSize(new java.awt.Dimension(400, 100));

        treeFileSystem.setAutoscrolls(true);
        treeFileSystem.setDoubleBuffered(true);
        treeFileSystem.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                treeFileSystemTreeExpanded(evt);
            }
        });
        treeFileSystem.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeFileSystemValueChanged(evt);
            }
        });
        treeFileSystem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fileSystemFocusGained(evt);
            }
        });
        treeFileSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                treeFileSystemKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                treeFileSystemKeyTyped(evt);
            }
        });
        treeFileSystem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeFileSystemMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                treeFileSystemMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeFileSystemMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeFileSystemMouseReleased(evt);
            }
        });
        scrlFileSystem.setViewportView(treeFileSystem);

        lblSelectFiles.setText("Select File(s)");

        cmbSortBy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSortBy.setMinimumSize(new java.awt.Dimension(70, 18));
        cmbSortBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSortByActionPerformed(evt);
            }
        });
        cmbSortBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        jLabel19.setText("Sort by");
        
        cmbFixityType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbFixityType.setMinimumSize(new java.awt.Dimension(70, 18));

        jLabel3.setText("Fixity");

        org.jdesktop.layout.GroupLayout pnlFileSystemLayout = new org.jdesktop.layout.GroupLayout(pnlFileSystem);
        pnlFileSystem.setLayout(pnlFileSystemLayout);
        pnlFileSystemLayout.setHorizontalGroup(
            pnlFileSystemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlFileSystem)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlFileSystemLayout.createSequentialGroup()
                .add(pnlFileSystemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlFileSystemLayout.createSequentialGroup()
                        .add(lblSelectFiles)
                        .add(35, 35, 35)
                        .add(jLabel19))
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFileSystemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cmbFixityType, 0, 64, Short.MAX_VALUE)
                    .add(cmbSortBy, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnlFileSystemLayout.setVerticalGroup(
            pnlFileSystemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFileSystemLayout.createSequentialGroup()
                .add(pnlFileSystemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSelectFiles)
                    .add(jLabel19)
                    .add(cmbSortBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(4, 4, 4)
                .add(pnlFileSystemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmbFixityType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlFileSystem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
        );

        splitMain.setLeftComponent(pnlFileSystem);

        splitMainRight.setDividerLocation(200);

        pnlIntellectualEntity.setPreferredSize(new java.awt.Dimension(400, 500));

        treeEntities.setToolTipText("");
        treeEntities.setDoubleBuffered(true);
        treeEntities.setEditable(true);
        treeEntities.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeEntitiesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                treeEntitiesMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeEntitiesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeEntitiesMouseReleased(evt);
            }
        });
        treeEntities.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeEntitiesValueChanged(evt);
            }
        });
        treeEntities.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                entitiesFocusGained(evt);
            }
        });
        treeEntities.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                treeEntitiesKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                treeEntitiesKeyTyped(evt);
            }
        });
        scrlEntities.setViewportView(treeEntities);

        lblIE.setText("Intellectual Entity");

        org.jdesktop.layout.GroupLayout pnlIntellectualEntityLayout = new org.jdesktop.layout.GroupLayout(pnlIntellectualEntity);
        pnlIntellectualEntity.setLayout(pnlIntellectualEntityLayout);
        pnlIntellectualEntityLayout.setHorizontalGroup(
            pnlIntellectualEntityLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIntellectualEntityLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblIE)
                .addContainerGap(105, Short.MAX_VALUE))
            .add(scrlEntities, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        pnlIntellectualEntityLayout.setVerticalGroup(
            pnlIntellectualEntityLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIntellectualEntityLayout.createSequentialGroup()
                .add(lblIE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(scrlEntities, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
        );

        splitMainRight.setLeftComponent(pnlIntellectualEntity);

        pnlStructMap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlStructMap.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                structMapFocusGained(evt);
            }
        });

        lblStructureMap.setText("Structure Map");

        treeStructMap.setToolTipText("");
        treeStructMap.setDoubleBuffered(true);
        treeStructMap.setEditable(true);
        treeStructMap.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeStructMapValueChanged(evt);
            }
        });
        treeStructMap.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                structMapFocusGained(evt);
            }
        });
        treeStructMap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                treeStructMapKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                treeStructMapKeyTyped(evt);
            }
        });
        treeStructMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeStructMapMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                treeStructMapMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeStructMapMouseReleased(evt);
            }
        });
        scrlStructMap.setViewportView(treeStructMap);

        jLabel21.setText("Select");

        cmbSelectStructTemplate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSelectStructTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSelectStructTemplateActionPerformed(evt);
            }
        });
        cmbSelectStructTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdSaveStructTemplate.setText("Save");
        cmdSaveStructTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveStructTemplateActionPerformed(evt);
            }
        });
        cmdSaveStructTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdDeleteStructTemplate.setText("Delete");
        cmdDeleteStructTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteStructTemplateActionPerformed(evt);
            }
        });
        cmdDeleteStructTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdSaveSharedStructTemplate.setText("Save Shared");
        cmdSaveSharedStructTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveSharedStructTemplateActionPerformed(evt);
            }
        });
        cmdSaveSharedStructTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        jLabel1.setText("Templates");

        org.jdesktop.layout.GroupLayout pnlStructMapLayout = new org.jdesktop.layout.GroupLayout(pnlStructMap);
        pnlStructMap.setLayout(pnlStructMapLayout);
        pnlStructMapLayout.setHorizontalGroup(
            pnlStructMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStructMapLayout.createSequentialGroup()
                .add(pnlStructMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlStructMapLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(cmdSaveStructTemplate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmdDeleteStructTemplate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmdSaveSharedStructTemplate))
                    .add(pnlStructMapLayout.createSequentialGroup()
                        .add(pnlStructMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlStructMapLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlStructMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblStructureMap)
                                    .add(jLabel1)))
                            .add(pnlStructMapLayout.createSequentialGroup()
                                .add(84, 84, 84)
                                .add(jLabel21)))
                        .add(18, 18, 18)
                        .add(cmbSelectStructTemplate, 0, 358, Short.MAX_VALUE)))
                .addContainerGap())
            .add(scrlStructMap, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        );
        pnlStructMapLayout.setVerticalGroup(
            pnlStructMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStructMapLayout.createSequentialGroup()
                .add(lblStructureMap)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStructMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jLabel21)
                    .add(cmbSelectStructTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlStructMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmdSaveStructTemplate)
                    .add(cmdDeleteStructTemplate)
                    .add(cmdSaveSharedStructTemplate))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlStructMap, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlStructMapParentLayout = new org.jdesktop.layout.GroupLayout(pnlStructMapParent);
        pnlStructMapParent.setLayout(pnlStructMapParentLayout);
        pnlStructMapParentLayout.setHorizontalGroup(
            pnlStructMapParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStructMapParentLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlStructMap, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlStructMapParentLayout.setVerticalGroup(
            pnlStructMapParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStructMap, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        splitMainRight.setRightComponent(pnlStructMapParent);

        org.jdesktop.layout.GroupLayout pnlRightSideLayout = new org.jdesktop.layout.GroupLayout(pnlRightSide);
        pnlRightSide.setLayout(pnlRightSideLayout);
        pnlRightSideLayout.setHorizontalGroup(
            pnlRightSideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(splitMainRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
        );
        pnlRightSideLayout.setVerticalGroup(
            pnlRightSideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(splitMainRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout pnlEntityLayout = new org.jdesktop.layout.GroupLayout(pnlEntity);
        pnlEntity.setLayout(pnlEntityLayout);
        pnlEntityLayout.setHorizontalGroup(
            pnlEntityLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlEntityLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRightSide, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEntityLayout.setVerticalGroup(
            pnlEntityLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRightSide, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        splitMain.setRightComponent(pnlEntity);

        splitMainDetail.setLeftComponent(splitMain);

        scrlDetail.setBackground(new java.awt.Color(255, 255, 255));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblDetail.setOpaque(false);
        tblDetail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                metaDatalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblDetailFocusLost(evt);
            }
        });
        scrlDetail.setViewportView(tblDetail);

        jLabel4.setText("Select Template");

        cmbSelectTemplate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSelectTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSelectTemplateActionPerformed(evt);
            }
        });
        cmbSelectTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });
        cmbSelectTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSelectTemplateKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cmbSelectTemplateKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cmbSelectTemplateKeyTyped(evt);
            }
        });

        cmdSaveAsTemplate.setText("Save Template");
        cmdSaveAsTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveAsTemplateActionPerformed(evt);
            }
        });
        cmdSaveAsTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdDeleteTemplate.setText("Delete Template");
        cmdDeleteTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteTemplateActionPerformed(evt);
            }
        });
        cmdDeleteTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdSaveAsSharedTemplate.setText("Save Shared Template");
        cmdSaveAsSharedTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveAsSharedTemplateActionPerformed(evt);
            }
        });
        cmdSaveAsSharedTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdClearMetaData.setText("Clear Metadata");
        cmdClearMetaData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdClearMetaDataActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlDetailLayout = new org.jdesktop.layout.GroupLayout(pnlDetail);
        pnlDetail.setLayout(pnlDetailLayout);
        pnlDetailLayout.setHorizontalGroup(
            pnlDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDetailLayout.createSequentialGroup()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbSelectTemplate, 0, 347, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmdDeleteTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmdSaveAsTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmdSaveAsSharedTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(cmdClearMetaData))
            .add(scrlDetail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE)
        );
        pnlDetailLayout.setVerticalGroup(
            pnlDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDetailLayout.createSequentialGroup()
                .add(pnlDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(cmdClearMetaData)
                    .add(cmdSaveAsSharedTemplate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cmbSelectTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmdDeleteTemplate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cmdSaveAsTemplate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlDetail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
        );

        splitMainDetail.setBottomComponent(pnlDetail);

        cmdCancel.setText("Cancel");
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelActionPerformed(evt);
            }
        });
        cmdCancel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdLoad.setText("Load Entity");
        cmdLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLoadActionPerformed(evt);
            }
        });
        cmdLoad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notSpecifiedFocusGained(evt);
            }
        });

        cmdAddProvenanceNote.setText("Edit Provenance Notes");
        cmdAddProvenanceNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddProvenanceNoteActionPerformed(evt);
            }
        });

        cmdCustomizeMetaData.setText("Customize MetaData");
        cmdCustomizeMetaData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCustomizeMetaDataActionPerformed(evt);
            }
        });

        lblNoOfIEs.setText("No of IEs");

        lblNoOfFiles.setText("No of Files");
        
        jProgressBar1.setValue(0);

        org.jdesktop.layout.GroupLayout pnlButtonsLayout = new org.jdesktop.layout.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .add(cmdCancel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmdLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(59, 59, 59)
                .add(cmdAddProvenanceNote)
                .add(18, 18, 18)
                .add(cmdCustomizeMetaData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 152, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lblNoOfIEs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lblNoOfFiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlButtonsLayout.setVerticalGroup(
                pnlButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(pnlButtonsLayout.createSequentialGroup()
                    .add(12, 12, 12)
                    .add(pnlButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pnlButtonsLayout.createSequentialGroup()
                            .add(1, 1, 1)
                            .add(jProgressBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(pnlButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cmdCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cmdLoad, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cmdAddProvenanceNote)
                            .add(cmdCustomizeMetaData)
                            .add(lblNoOfIEs)
                            .add(lblNoOfFiles))))
            );

        org.jdesktop.layout.GroupLayout pnlTreesLayout = new org.jdesktop.layout.GroupLayout(pnlTrees);
        pnlTrees.setLayout(pnlTreesLayout);
        pnlTreesLayout.setHorizontalGroup(
            pnlTreesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(splitMainDetail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .add(pnlButtons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlTreesLayout.setVerticalGroup(
            pnlTreesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTreesLayout.createSequentialGroup()
                .add(splitMainDetail)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        splitAddIE.setRightComponent(pnlTrees);

        org.jdesktop.layout.GroupLayout pnlAddIELayout = new org.jdesktop.layout.GroupLayout(pnlAddIE);
        pnlAddIE.setLayout(pnlAddIELayout);
        pnlAddIELayout.setHorizontalGroup(
            pnlAddIELayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAddIELayout.createSequentialGroup()
                .addContainerGap()
                .add(splitAddIE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 803, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlAddIELayout.setVerticalGroup(
            pnlAddIELayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAddIELayout.createSequentialGroup()
                .add(splitAddIE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabMain.addTab("Add Intellectual Entity", pnlAddIE);

        pnlJobQueue.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jobQueueFocusGained(evt);
            }
        });

        mspJobQueue.setDividerSize(2);
        mspJobQueue.setMinimumSize(new java.awt.Dimension(100, 100));
        mspJobQueue.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                mspJobQueueMouseDragged(evt);
            }
        });

        pnlJobQueueRunning.setBorder(javax.swing.BorderFactory.createTitledBorder("Running Jobs"));

        tblJobQueueRunning.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblJobQueueRunning.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jobQueueFocusGained(evt);
            }
        });
        tblJobQueueRunning.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblJobQueueRunningMouseReleased(evt);
            }
        });
        scrlJobQueueRunning.setViewportView(tblJobQueueRunning);

        org.jdesktop.layout.GroupLayout pnlJobQueueRunningLayout = new org.jdesktop.layout.GroupLayout(pnlJobQueueRunning);
        pnlJobQueueRunning.setLayout(pnlJobQueueRunningLayout);
        pnlJobQueueRunningLayout.setHorizontalGroup(
            pnlJobQueueRunningLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueRunning, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );
        pnlJobQueueRunningLayout.setVerticalGroup(
            pnlJobQueueRunningLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueRunning, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        mspJobQueue.add(pnlJobQueueRunning);

        pnlJobQueuePending.setBorder(javax.swing.BorderFactory.createTitledBorder("Pending Jobs"));

        tblJobQueuePending.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblJobQueuePending.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jobQueueFocusGained(evt);
            }
        });
        tblJobQueuePending.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblJobQueuePendingMouseReleased(evt);
            }
        });
        scrlJobQueuePending.setViewportView(tblJobQueuePending);

        org.jdesktop.layout.GroupLayout pnlJobQueuePendingLayout = new org.jdesktop.layout.GroupLayout(pnlJobQueuePending);
        pnlJobQueuePending.setLayout(pnlJobQueuePendingLayout);
        pnlJobQueuePendingLayout.setHorizontalGroup(
            pnlJobQueuePendingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueuePending, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );
        pnlJobQueuePendingLayout.setVerticalGroup(
            pnlJobQueuePendingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueuePending, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        mspJobQueue.add(pnlJobQueuePending);

        pnlJobQueueFailed.setBorder(javax.swing.BorderFactory.createTitledBorder("Failed Jobs"));

        tblJobQueueFailed.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblJobQueueFailed.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jobQueueFocusGained(evt);
            }
        });
        tblJobQueueFailed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblJobQueueFailedMouseReleased(evt);
            }
        });
        scrlJobQueueFailed.setViewportView(tblJobQueueFailed);

        org.jdesktop.layout.GroupLayout pnlJobQueueFailedLayout = new org.jdesktop.layout.GroupLayout(pnlJobQueueFailed);
        pnlJobQueueFailed.setLayout(pnlJobQueueFailedLayout);
        pnlJobQueueFailedLayout.setHorizontalGroup(
            pnlJobQueueFailedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueFailed, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );
        pnlJobQueueFailedLayout.setVerticalGroup(
            pnlJobQueueFailedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueFailed, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        mspJobQueue.add(pnlJobQueueFailed);

        pnlJobQueueDeposited.setBorder(javax.swing.BorderFactory.createTitledBorder("Deposited Jobs"));

        tblJobQueueDeposited.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblJobQueueDeposited.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jobQueueFocusGained(evt);
            }
        });
        tblJobQueueDeposited.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblJobQueueDepositedMouseReleased(evt);
            }
        });
        scrlJobQueueDeposited.setViewportView(tblJobQueueDeposited);

        org.jdesktop.layout.GroupLayout pnlJobQueueDepositedLayout = new org.jdesktop.layout.GroupLayout(pnlJobQueueDeposited);
        pnlJobQueueDeposited.setLayout(pnlJobQueueDepositedLayout);
        pnlJobQueueDepositedLayout.setHorizontalGroup(
            pnlJobQueueDepositedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueDeposited, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );
        pnlJobQueueDepositedLayout.setVerticalGroup(
            pnlJobQueueDepositedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueDeposited, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        mspJobQueue.add(pnlJobQueueDeposited);

        pnlJobQueueComplete.setBorder(javax.swing.BorderFactory.createTitledBorder("Complete Jobs"));

        tblJobQueueComplete.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblJobQueueComplete.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jobQueueFocusGained(evt);
            }
        });
        tblJobQueueComplete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblJobQueueCompleteMouseReleased(evt);
            }
        });
        scrlJobQueueComplete.setViewportView(tblJobQueueComplete);

        org.jdesktop.layout.GroupLayout pnlJobQueueCompleteLayout = new org.jdesktop.layout.GroupLayout(pnlJobQueueComplete);
        pnlJobQueueComplete.setLayout(pnlJobQueueCompleteLayout);
        pnlJobQueueCompleteLayout.setHorizontalGroup(
            pnlJobQueueCompleteLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueComplete, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );
        pnlJobQueueCompleteLayout.setVerticalGroup(
            pnlJobQueueCompleteLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlJobQueueComplete, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        mspJobQueue.add(pnlJobQueueComplete);

        org.jdesktop.layout.GroupLayout pnlJobQueueLayout = new org.jdesktop.layout.GroupLayout(pnlJobQueue);
        pnlJobQueue.setLayout(pnlJobQueueLayout);
        pnlJobQueueLayout.setHorizontalGroup(
            pnlJobQueueLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlJobQueueLayout.createSequentialGroup()
                .addContainerGap()
                .add(mspJobQueue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlJobQueueLayout.setVerticalGroup(
            pnlJobQueueLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlJobQueueLayout.createSequentialGroup()
                .addContainerGap()
                .add(mspJobQueue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabMain.addTab("Job Queue", pnlJobQueue);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabMain)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuHotKeysAccessCopyHighActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHotKeysAccessCopyHighActionPerformed
        processFileSystemKey('h');
    }//GEN-LAST:event_mnuHotKeysAccessCopyHighActionPerformed

    private void mnuHotKeysAccessCopyMediumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHotKeysAccessCopyMediumActionPerformed
        processFileSystemKey('i');
    }//GEN-LAST:event_mnuHotKeysAccessCopyMediumActionPerformed

    private void mnuHotKeysAccessCopyLowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHotKeysAccessCopyLowActionPerformed
        processFileSystemKey('l');
    }//GEN-LAST:event_mnuHotKeysAccessCopyLowActionPerformed

    private void cmdCustomizeMetaDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCustomizeMetaDataActionPerformed
        setWaitCursor(true);
		ICustomizeMetaDataEditorView customizeMetaDataForm = createCustomizeMetaDataForm();
		customizeMetaDataForm.setFormFont(theStandardFont);
		depositPresenter.customizeMetaData(customizeMetaDataForm);
		setWaitCursor(false);
    }//GEN-LAST:event_cmdCustomizeMetaDataActionPerformed

    private void mnuHotKeysAccessCopyEpubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHotKeysAccessCopyEpubActionPerformed
        processFileSystemKey('b');
    }//GEN-LAST:event_mnuHotKeysAccessCopyEpubActionPerformed

    private void mnuHotKeysAccessCopyPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHotKeysAccessCopyPdfActionPerformed
         processFileSystemKey('c');
    }//GEN-LAST:event_mnuHotKeysAccessCopyPdfActionPerformed

	private void mnuShowBulkLoadActionPerformed(java.awt.event.ActionEvent evt) {
		depositPresenter.checkForBulkLoadQueue();
	}
	
	private void lstProducersMousePressed(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			JPopupMenu menu = depositPresenter.getProducerMenu();
			if (menu != null) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void lstProducersMouseReleased(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			JPopupMenu menu = depositPresenter.getProducerMenu();
			if (menu != null) {
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void mspJobQueueMouseDragged(java.awt.event.MouseEvent evt) {
		checkJobQueueWindowSizes();
	}
	
	// Mouse Pressed event handlers added to provide right click functionality when Indigo run through Xming.
	private void tblJobQueueCompleteMousePressed(java.awt.event.MouseEvent evt) {
		jobQueueMousePressedCommon(evt, tblJobQueueComplete);
	}

	private void tblJobQueueDepositedMousePressed(java.awt.event.MouseEvent evt) {
		jobQueueMousePressedCommon(evt, tblJobQueueDeposited);
	}

	private void tblJobQueueFailedMousePressed(java.awt.event.MouseEvent evt) {
		jobQueueMousePressedCommon(evt, tblJobQueueFailed);
	}

	private void tblJobQueueRunningMousePressed(java.awt.event.MouseEvent evt) {
		jobQueueMousePressedCommon(evt, tblJobQueueRunning);
	}

	private void tblJobQueuePendingMousePressed(java.awt.event.MouseEvent evt) {
		jobQueueMousePressedCommon(evt, tblJobQueuePending);
	}

	private void tblJobQueueCompleteMouseReleased(java.awt.event.MouseEvent evt) {
		jobQueueMouseReleaseCommon(evt, tblJobQueueComplete);
	}

	private void tblJobQueueDepositedMouseReleased(java.awt.event.MouseEvent evt) {
		jobQueueMouseReleaseCommon(evt, tblJobQueueDeposited);
	}

	private void tblJobQueueFailedMouseReleased(java.awt.event.MouseEvent evt) {
		jobQueueMouseReleaseCommon(evt, tblJobQueueFailed);
	}

	private void tblJobQueueRunningMouseReleased(java.awt.event.MouseEvent evt) {
		jobQueueMouseReleaseCommon(evt, tblJobQueueRunning);
	}

	private void tblJobQueuePendingMouseReleased(java.awt.event.MouseEvent evt) {
		jobQueueMouseReleaseCommon(evt, tblJobQueuePending);
	}

	private void checkHotKey(java.awt.event.KeyEvent evt) {
		if ((evt.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
			if (evt.getKeyCode() == KeyEvent.VK_1) {
				showSearch(true);
			} else if (evt.getKeyCode() == KeyEvent.VK_2) {
				showMetaData(true);
			}
		} else if ((evt.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
			if (evt.getKeyCode() == KeyEvent.VK_S) {
				showSearch(true);
			} else if (evt.getKeyCode() == KeyEvent.VK_M) {
				showMetaData(true);
			}
		}
		if (evt.getKeyCode() == KeyEvent.VK_F5) {
			depositPresenter.refreshFileList();
		}
	}

	private void treeFileSystemValueChanged(
			javax.swing.event.TreeSelectionEvent evt) {
		theCurrentCursorPosition = ScreenPosition.fileSystem;
		setHotKeyVisibility();
	}

	private void doSearch(java.awt.event.ActionEvent evt) {
		if (canSearch()) {
			setWaitCursor(true);
			depositPresenter.resetRadioButtons();
			//If Published user group then always use CMS1 search for any option
			if (isPublishedUserGroup){
				if (rbnStaffMediated.isSelected()) {
					depositPresenter.setStaffMediated(true);
				} else if (rbnNoCmsRef.isSelected()) {
					depositPresenter.setNoCMS(true);
				} else if (rbnCMS2.isSelected()){
					depositPresenter.setIsCMS2(true);
				}
				depositPresenter.searchCMS(this, ILSQueryType.eServerType.CMS1, getSearchFields());
			} else {
				if (rbnCMS2.isSelected()) {
					depositPresenter.searchCMS(this, ILSQueryType.eServerType.CMS2, getSearchFields());
				} else if (rbnCMS1.isSelected()) {
					depositPresenter.searchCMS(this, ILSQueryType.eServerType.CMS1, getSearchFields());
				}
			}
			setWaitCursor(false);
		}
	}

	private void treeFileSystemKeyPressed(java.awt.event.KeyEvent evt) {
		stopEditingTree();
	}

	private void tblDetailFocusLost(java.awt.event.FocusEvent evt) {
	}

	private void mnuHotKeysUseFileForIEActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('s');
	}

	private void lstMaterialFlowValueChanged(
			javax.swing.event.ListSelectionEvent evt) {
		checkButtons();
	}

	private void rbnStaffMediatedActionPerformed(java.awt.event.ActionEvent evt) {
		setCMSDetails();
	}

	private void mnuFileSelectDirectoryActionPerformed(
			java.awt.event.ActionEvent evt) {
		EnterDirectory enterDir = new EnterDirectory(this, true,
				theSettingsPath, depositPresenter);
		enterDir.setVisible(true);
	}

	private void lstProducersValueChanged(
			javax.swing.event.ListSelectionEvent evt) {
		checkButtons();
	}

	private void txtProducerFilterUpdate(java.awt.event.ActionEvent evt) {
		depositPresenter.filterProducerList(txtProducerFilter.getText());
	}

	private void mnuManageStructMapFileDescActionPerformed(
			java.awt.event.ActionEvent evt) {
		manageStructMapFileDesc();
	}

	private void cmdAddProvenanceNoteActionPerformed(
			java.awt.event.ActionEvent evt) {
		setWaitCursor(true);
		ProvenanceEventsEditorView editor = new ProvenanceEventsEditor(this,
				true, theSettingsPath, theAppProperties.getApplicationData()
						.getMaximumProvenanceEventLength());
		editor.setFormFont(theStandardFont);
		depositPresenter.editProvenanceNotes(editor);
		tblDetail.repaint();
		setWaitCursor(false);
	}

	private void treeStructMapKeyTyped(java.awt.event.KeyEvent evt) {
		if (evt.getKeyChar() == KeyEvent.VK_DELETE) {
			depositPresenter.deleteStructMapItem();
		}
	}

	private void treeStructMapValueChanged(
			javax.swing.event.TreeSelectionEvent evt) {
		setHotKeyVisibility();
	}

	private void treeEntitiesValueChanged(
			javax.swing.event.TreeSelectionEvent evt) {
		setHotKeyVisibility();
	}

	private void treeStructMapKeyPressed(java.awt.event.KeyEvent evt) {
		if ((evt.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
			if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
				moveItem(false);
			} else if (evt.getKeyCode() == KeyEvent.VK_UP) {
				moveItem(true);
			}
		}
	}

	private void mnuHotKeysDeleteActionPerformed(java.awt.event.ActionEvent evt) {
		if (theCurrentCursorPosition == ScreenPosition.intellectualEntity) {
			depositPresenter.deleteEntity();
		} else if (theCurrentCursorPosition == ScreenPosition.structMap) {
			depositPresenter.deleteStructMapItem();
		}
	}

	private void mnuHotKeysSelectAllFilesActionPerformed(
			java.awt.event.ActionEvent evt) {
		depositPresenter.createAutoStructItem(true);
	}

	private void mnuHotKeysMakeStructActionPerformed(
			java.awt.event.ActionEvent evt) {
		depositPresenter.createAutoStructItem(false);
	}

	private void mnuHotKeysMoveFileDownActionPerformed(
			java.awt.event.ActionEvent evt) {
		moveItem(false);
	}

	private void mnuHotKeysMoveFileUpActionPerformed(
			java.awt.event.ActionEvent evt) {
		moveItem(true);
	}

	private void mnuHotKeysAccessCopyActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('a');
	}

	private void mnuHotKeysModifiedMasterActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('m');
	}

	private void mnuHotKeysPreservationCopyActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('p');
	}

	private void mnuHotKeysDigitalOriginalActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('d');
	}

	private void mnuHotKeysStoreAsFavouriteActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('f');
	}

	private void mnuHotKeysOpenFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('o');
	}

	private void mnuHotKeysSetEachFileIEActionPerformed(
			java.awt.event.ActionEvent evt) {
		processFileSystemKey('e');
	}

	private void mnuHotKeysSetIEActionPerformed(java.awt.event.ActionEvent evt) {
		processFileSystemKey('s');
	}

	private void mnuHotKeysMenuActionPerformed(java.awt.event.ActionEvent evt) {
		this.processFileSystemKey('m');
	}

	private void metaDatalFocusGained(java.awt.event.FocusEvent evt) {
		theCurrentCursorPosition = ScreenPosition.metaData;
		setHotKeyVisibility();
	}

	private void jobQueueFocusGained(java.awt.event.FocusEvent evt) {
		theCurrentCursorPosition = ScreenPosition.jobQueue;
		setHotKeyVisibility();
	}

	private void structMapFocusGained(java.awt.event.FocusEvent evt) {
		theCurrentCursorPosition = ScreenPosition.structMap;
		setHotKeyVisibility();
	}

	private void notSpecifiedFocusGained(java.awt.event.FocusEvent evt) {
		theCurrentCursorPosition = ScreenPosition.notSpecified;
		setHotKeyVisibility();
	}

	private void entitiesFocusGained(java.awt.event.FocusEvent evt) {
		theCurrentCursorPosition = ScreenPosition.intellectualEntity;
		setHotKeyVisibility();
	}

	private void pnlCmsReferenceFocusGained(java.awt.event.FocusEvent evt) {
		theCurrentCursorPosition = ScreenPosition.search;
		setHotKeyVisibility();
	}

	private void fileSystemFocusGained(java.awt.event.FocusEvent evt) {
		theCurrentCursorPosition = ScreenPosition.fileSystem;
		setHotKeyVisibility();
	}

	private void cmdSaveSharedStructTemplateActionPerformed(
			java.awt.event.ActionEvent evt) {
		saveStructSharedTemplate();
	}

	private void cmdDeleteStructTemplateActionPerformed(
			java.awt.event.ActionEvent evt) {
		deleteStructTemplate();
	}

	private void cmdSaveStructTemplateActionPerformed(
			java.awt.event.ActionEvent evt) {
		saveStructTemplate();
	}

	private void cmbSelectStructTemplateActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (depositPresenter.loadStructTemplate()) {
			theOldStructTemplate = (String) cmbSelectStructTemplate
					.getSelectedItem();
		} else {
			cmbSelectStructTemplate.setSelectedItem(theOldStructTemplate);
		}
		checkButtons();
	}

	private void mnuViewShowMetaDataActionPerformed(
			java.awt.event.ActionEvent evt) {
		showMetaData(false);
	}

	private void mnuViewShowSearchActionPerformed(java.awt.event.ActionEvent evt) {
		showSearch(false);
	}

	private void treeEntitiesMouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() > 1) {
			TreePath path = treeEntities.getSelectionPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			depositPresenter.openFile(node);
		}
	}

	// The tree is defined as not editable, but pressing F2 or triple clicking
	// still makes it editable
	// This is a total kludge to work around this issue.
	@SuppressWarnings("serial")
	private void stopEditingTree() {
		treeFileSystem.stopEditing();
		Action stopEditingAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				Timer t = (Timer) e.getSource();
				t.stop();
				try {
					treeFileSystem.stopEditing();
				} catch (Exception ex) {
				}
			}
		};
		new Timer(100, stopEditingAction).start();
	}

	private void treeFileSystemMouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() > 1) {
			TreePath path = treeFileSystem.getSelectionPath();
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				depositPresenter.openFile(node);
			}
			stopEditingTree();
		}
	}

	private void mnuHelpContentsActionPerformed(java.awt.event.ActionEvent evt) {
		URL index = ClassLoader.getSystemResource("./Help/index.html");
		new HelpWindow("Indigo Help", index, theSettingsPath);
	}

	private void mnuHelpAboutActionPerformed(java.awt.event.ActionEvent evt) {
		About about = new About(this, true, theSettingsPath);
		about.setFormFont(theStandardFont);
		about.setVisible(true);
	}

	private void cmdClearMetaDataActionPerformed(java.awt.event.ActionEvent evt) {
		clearMetaData();
	}

	private void mnuTemplatesClearActionPerformed(java.awt.event.ActionEvent evt) {
		clearMetaData();
	}

	private void caretUpdate(javax.swing.event.CaretEvent evt) {
		checkButtons();
	}

	// GEN-FIRST:event_cmbSortByActionPerformed
	private void cmbSortByActionPerformed(java.awt.event.ActionEvent evt) {
		setSortBy();
	}// GEN-LAST:event_cmbSortByActionPerformed

	// GEN-FIRST:event_mnuFileExitActionPerformed
	private void mnuFileExitActionPerformed(java.awt.event.ActionEvent evt) {
		closeForm(null);
	}// GEN-LAST:event_mnuFileExitActionPerformed

	// GEN-FIRST:event_mnuFilePropertiesActionPerformed
	private void mnuFilePropertiesActionPerformed(java.awt.event.ActionEvent evt) {
		fileProperties();
	}// GEN-LAST:event_mnuFilePropertiesActionPerformed

	// GEN-FIRST:event_cmdSaveAsSharedTemplateActionPerformed
	private void cmdSaveAsSharedTemplateActionPerformed(
			java.awt.event.ActionEvent evt) {
		this.saveSharedTemplate();
	}// GEN-LAST:event_cmdSaveAsSharedTemplateActionPerformed

	// GEN-FIRST:event_mnuTemplatesSaveSharedActionPerformed
	private void mnuTemplatesSaveSharedActionPerformed(
			java.awt.event.ActionEvent evt) {
		saveSharedTemplate();
	}// GEN-LAST:event_mnuTemplatesSaveSharedActionPerformed

	// GEN-FIRST:event_mnuTemplatesDeleteActionPerformed
	private void mnuTemplatesDeleteActionPerformed(
			java.awt.event.ActionEvent evt) {
		deleteTemplate();
	}// GEN-LAST:event_mnuTemplatesDeleteActionPerformed

	// GEN-FIRST:event_mnuTemplatesSaveActionPerformed
	private void mnuTemplatesSaveActionPerformed(java.awt.event.ActionEvent evt) {
		saveTemplate();
	}// GEN-LAST:event_mnuTemplatesSaveActionPerformed

	// GEN-FIRST:event_treeStructMapMouseClicked
	private void treeStructMapMouseClicked(java.awt.event.MouseEvent evt) {
		setDragSourceStructMap();
	}// GEN-LAST:event_treeStructMapMouseClicked
	
	// GEN-FIRST:event_treeEntitiesMousePressed
	private void treeStructMapMousePressed(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			TreePath path = treeStructMap.getClosestPathForLocation(evt.getX(),
					evt.getY());
			if (path != null) {
				treeStructMap.setSelectionPath(path);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				JPopupMenu menu = depositPresenter.getStructMapMenu(node);
				if (menu != null) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}		
	}// GEN-LAST:event_treeEntitiesMousePressed

	// GEN-FIRST:event_treeEntitiesMousePressed
	private void treeEntitiesMousePressed(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			TreePath path = treeEntities.getClosestPathForLocation(evt.getX(),
					evt.getY());
			if (path != null) {
				treeEntities.setSelectionPath(path);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				JPopupMenu menu = depositPresenter.getEntityMenu(node);
				if (menu != null) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
		else{
			setDragSourceEntity();
		}		
	}// GEN-LAST:event_treeEntitiesMousePressed

	// GEN-FIRST:event_treeFileSystemKeyTyped
	private void treeFileSystemKeyTyped(java.awt.event.KeyEvent evt) {
		if ((evt.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
			processFileSystemKey(evt.getKeyChar());
		}
		stopEditingTree();
	}// GEN-LAST:event_treeFileSystemKeyTyped

	// GEN-FIRST:event_treeEntitiesKeyTyped
	private void treeEntitiesKeyTyped(java.awt.event.KeyEvent evt) {
		if (evt.getKeyChar() == KeyEvent.VK_DELETE) {
			depositPresenter.deleteEntity();
		} else {
			if ((evt.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
				if (evt.getKeyChar() == 's') {
					depositPresenter.createAutoStructItem(false);
				} else if (evt.getKeyChar() == 'r') {
					depositPresenter.createAutoStructItem(true);
				}
				processFileSystemKey(evt.getKeyChar());
			}
		}
	}// GEN-LAST:event_treeEntitiesKeyTyped

	private void treeEntitiesKeyPressed(java.awt.event.KeyEvent evt) {
		if ((evt.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0) {
			if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
				moveItem(false);
			} else if (evt.getKeyCode() == KeyEvent.VK_UP) {
				moveItem(true);
			}
		}
	}

	// GEN-FIRST:event_cmbSelectTemplateKeyReleased
	private void cmbSelectTemplateKeyReleased(java.awt.event.KeyEvent evt) {
		checkButtons();
	}// GEN-LAST:event_cmbSelectTemplateKeyReleased

	// GEN-FIRST:event_cmbSelectTemplateKeyPressed
	private void cmbSelectTemplateKeyPressed(java.awt.event.KeyEvent evt) {
		checkButtons();
	}// GEN-LAST:event_cmbSelectTemplateKeyPressed

	// GEN-FIRST:event_cmbSelectTemplateKeyTyped
	private void cmbSelectTemplateKeyTyped(java.awt.event.KeyEvent evt) {
		checkButtons();
	}// GEN-LAST:event_cmbSelectTemplateKeyTyped

	// GEN-FIRST:event_cmdDeleteTemplateActionPerformed
	private void cmdDeleteTemplateActionPerformed(java.awt.event.ActionEvent evt) {
		deleteTemplate();
	}// GEN-LAST:event_cmdDeleteTemplateActionPerformed

	// GEN-FIRST:event_cmbSelectTemplateActionPerformed
	private void cmbSelectTemplateActionPerformed(java.awt.event.ActionEvent evt) {
		if (depositPresenter.loadTemplate()) {
			theOldTemplate = (String) cmbSelectTemplate.getSelectedItem();
		} else {
			cmbSelectTemplate.setSelectedItem(theOldTemplate);
		}
		checkButtons();
	}// GEN-LAST:event_cmbSelectTemplateActionPerformed

	// GEN-FIRST:event_formWindowClosing
	private void formWindowClosing(java.awt.event.WindowEvent evt) {
		closeForm(evt);
	}// GEN-LAST:event_formWindowClosing

	// GEN-FIRST:event_cmdSaveAsTemplateActionPerformed
	private void cmdSaveAsTemplateActionPerformed(java.awt.event.ActionEvent evt) {
		saveTemplate();
	}// GEN-LAST:event_cmdSaveAsTemplateActionPerformed

	// GEN-FIRST:event_treeEntitiesMouseEntered
	private void treeEntitiesMouseEntered(java.awt.event.MouseEvent evt) {
		setDragSourceEntity();
	}// GEN-LAST:event_treeEntitiesMouseEntered

	// GEN-FIRST:event_treeFileSystemMouseEntered
	private void treeFileSystemMouseEntered(java.awt.event.MouseEvent evt) {
		this.setDragSourceFileSystem();
	}// GEN-LAST:event_treeFileSystemMouseEntered

	// GEN-FIRST:event_treeStructMapMouseEntered
	private void treeStructMapMouseEntered(java.awt.event.MouseEvent evt) {
		setDragSourceStructMap();
	}// GEN-LAST:event_treeStructMapMouseEntered

	// GEN-FIRST:event_treeStructMapMouseReleased
	private void treeStructMapMouseReleased(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			TreePath path = treeStructMap.getClosestPathForLocation(evt.getX(),
					evt.getY());
			if (path != null) {
				treeStructMap.setSelectionPath(path);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				JPopupMenu menu = depositPresenter.getStructMapMenu(node);
				if (menu != null) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}// GEN-LAST:event_treeStructMapMouseReleased

	// GEN-FIRST:event_treeEntitiesMouseReleased
	private void treeEntitiesMouseReleased(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			TreePath path = treeEntities.getClosestPathForLocation(evt.getX(),
					evt.getY());
			if (path != null) {
				treeEntities.setSelectionPath(path);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				JPopupMenu menu = depositPresenter.getEntityMenu(node);
				if (menu != null) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}// GEN-LAST:event_treeEntitiesMouseReleased

	// GEN-FIRST:event_rbnNoCmsRefSearchTypeChanged
	private void rbnNoCmsRefSearchTypeChanged(java.awt.event.ActionEvent evt) {
		setCMSDetails();
		// _presenter.setCMSID("", MetaDataFields.ECMSSystem.NoSystem, "");
	}// GEN-LAST:event_rbnNoCmsRefSearchTypeChanged

	// GEN-FIRST:event_rbnCMS1SearchTypeChanged
	private void rbnCMS1SearchTypeChanged(java.awt.event.ActionEvent evt) {
		setCMSDetails();
		// _presenter.setCMSID("", MetaDataFields.ECMSSystem.CMS1, "");
	}// GEN-LAST:event_rbnCMS1SearchTypeChanged

	// GEN-FIRST:event_rbnCMS2SearchTypeChanged
	private void rbnCMS2SearchTypeChanged(java.awt.event.ActionEvent evt) {
		setCMSDetails();
		// _presenter.setCMSID("", MetaDataFields.ECMSSystem.CMS2, "");
	}// GEN-LAST:event_rbnCMS2SearchTypeChanged

	// GEN-FIRST:event_cmdCancelActionPerformed
	private void cmdCancelActionPerformed(java.awt.event.ActionEvent evt) {
		depositPresenter.resetScreen();
		checkButtons();
	}// GEN-LAST:event_cmdCancelActionPerformed

	// GEN-FIRST:event_cmdLoadActionPerformed
	private void cmdLoadActionPerformed(java.awt.event.ActionEvent evt) {
		depositPresenter.loadEntity();
		checkButtons();
	}// GEN-LAST:event_cmdLoadActionPerformed

	// GEN-FIRST:event_treeFileSystemMouseReleased
	private void treeFileSystemMouseReleased(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			TreePath path = treeFileSystem.getClosestPathForLocation(
					evt.getX(), evt.getY());
			if (path != null) {
				treeFileSystem.setSelectionPath(path);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				JPopupMenu menu = depositPresenter.getFileSystemMenu(node);
				if (menu != null) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}// GEN-LAST:event_treeFileSystemMouseReleased

	// GEN-FIRST:event_treeFileSystemMousePressed
	private void treeFileSystemMousePressed(java.awt.event.MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			TreePath path = treeFileSystem.getClosestPathForLocation(
					evt.getX(), evt.getY());
			if (path != null) {
				treeFileSystem.setSelectionPath(path);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				JPopupMenu menu = depositPresenter.getFileSystemMenu(node);
				if (menu != null) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
		else{
			setDragSourceFileSystem();
		}
		
	}// GEN-LAST:event_treeFileSystemMousePressed

	// GEN-FIRST:event_treeFileSystemTreeExpanded
	private void treeFileSystemTreeExpanded(
			javax.swing.event.TreeExpansionEvent evt) {
		setWaitCursor(true);
		LOG.debug("Expanding Tree");
		TreePath currentPath = evt.getPath();
		depositPresenter.expandFileSystemTree(currentPath);
		setWaitCursor(false);
	}// GEN-LAST:event_treeFileSystemTreeExpanded

    // Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JComboBox cmbFixityType;
    private javax.swing.JComboBox cmbSelectStructTemplate;
    private javax.swing.JComboBox cmbSelectTemplate;
    private javax.swing.JComboBox cmbSortBy;
    private javax.swing.JButton cmdAddProvenanceNote;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdClearMetaData;
    private javax.swing.JButton cmdCustomizeMetaData;
    private javax.swing.JButton cmdDeleteStructTemplate;
    private javax.swing.JButton cmdDeleteTemplate;
    private javax.swing.JButton cmdDoSearch;
    private javax.swing.JButton cmdLoad;
    private javax.swing.JButton cmdSaveAsSharedTemplate;
    private javax.swing.JButton cmdSaveAsTemplate;
    private javax.swing.JButton cmdSaveSharedStructTemplate;
    private javax.swing.JButton cmdSaveStructTemplate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblIE;
    private javax.swing.JLabel lblNoOfFiles;
    private javax.swing.JLabel lblNoOfIEs;
    private javax.swing.JLabel lblSearch1;
    private javax.swing.JLabel lblSearch10;
    private javax.swing.JLabel lblSearch11;
    private javax.swing.JLabel lblSearch12;
    private javax.swing.JLabel lblSearch13;
    private javax.swing.JLabel lblSearch14;
    private javax.swing.JLabel lblSearch15;
    private javax.swing.JLabel lblSearch2;
    private javax.swing.JLabel lblSearch3;
    private javax.swing.JLabel lblSearch4;
    private javax.swing.JLabel lblSearch5;
    private javax.swing.JLabel lblSearch6;
    private javax.swing.JLabel lblSearch7;
    private javax.swing.JLabel lblSearch8;
    private javax.swing.JLabel lblSearch9;
    private javax.swing.JLabel lblSelectFiles;
    private javax.swing.JLabel lblStructureMap;
    private javax.swing.JList lstMaterialFlow;
    private javax.swing.JList lstProducers;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuFileExit;
    private javax.swing.JMenu mnuFileFavourites;
    private javax.swing.JMenuItem mnuFileProperties;
    private javax.swing.JMenuItem mnuFileSelectDirectory;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuHelpAbout;
    private javax.swing.JMenuItem mnuHelpContents;
    private javax.swing.JMenu mnuHotKeys;
    private javax.swing.JMenuItem mnuHotKeysAccessCopy;
    private javax.swing.JMenuItem mnuHotKeysAccessCopyEpub;
    private javax.swing.JMenuItem mnuHotKeysAccessCopyHigh;
    private javax.swing.JMenuItem mnuHotKeysAccessCopyLow;
    private javax.swing.JMenuItem mnuHotKeysAccessCopyMedium;
    private javax.swing.JMenuItem mnuHotKeysAccessCopyPdf;
    private javax.swing.JMenuItem mnuHotKeysDelete;
    private javax.swing.JMenuItem mnuHotKeysDigitalOriginal;
    private javax.swing.JMenuItem mnuHotKeysMakeStruct;
    private javax.swing.JMenuItem mnuHotKeysMenu;
    private javax.swing.JMenuItem mnuHotKeysModifiedMaster;
    private javax.swing.JMenuItem mnuHotKeysMoveFileDown;
    private javax.swing.JMenuItem mnuHotKeysMoveFileUp;
    private javax.swing.JMenuItem mnuHotKeysOpenFile;
    private javax.swing.JMenuItem mnuHotKeysPreservationCopy;
    private javax.swing.JMenuItem mnuHotKeysSelectAllFiles;
    private javax.swing.JMenuItem mnuHotKeysSetEachFileIE;
    private javax.swing.JMenuItem mnuHotKeysSetIE;
    private javax.swing.JMenuItem mnuHotKeysStoreAsFavourite;
    private javax.swing.JMenuItem mnuHotKeysUseFileForIE;
    private javax.swing.JMenuBar mnuMain;
    private javax.swing.JMenuItem mnuManageStructMapFileDesc;
    private javax.swing.JMenuItem mnuShowBulkLoad;
    private javax.swing.JMenu mnuTemplates;
    private javax.swing.JMenuItem mnuTemplatesClear;
    private javax.swing.JMenuItem mnuTemplatesDelete;
    private javax.swing.JMenuItem mnuTemplatesSave;
    private javax.swing.JMenuItem mnuTemplatesSaveShared;
    private javax.swing.JMenu mnuView;
    private javax.swing.JCheckBoxMenuItem mnuViewShowMetaData;
    private javax.swing.JCheckBoxMenuItem mnuViewShowSearch;
    private org.jdesktop.swingx.JXMultiSplitPane mspJobQueue;
    private javax.swing.JPanel pnlAddIE;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCmsReference;
    private javax.swing.JPanel pnlDetail;
    private javax.swing.JPanel pnlEntity;
    private javax.swing.JPanel pnlFileSystem;
    private javax.swing.JPanel pnlIntellectualEntity;
    private javax.swing.JPanel pnlJobQueue;
    private javax.swing.JPanel pnlJobQueueComplete;
    private javax.swing.JPanel pnlJobQueueDeposited;
    private javax.swing.JPanel pnlJobQueueFailed;
    private javax.swing.JPanel pnlJobQueuePending;
    private javax.swing.JPanel pnlJobQueueRunning;
    private javax.swing.JPanel pnlRightSide;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlSearchDetail;
    private javax.swing.JPanel pnlSelectProducer;
    private javax.swing.JPanel pnlSource;
    private javax.swing.JPanel pnlStructMap;
    private javax.swing.JPanel pnlStructMapParent;
    private javax.swing.JPanel pnlTrees;
    private javax.swing.JRadioButton rbnNoCmsRef;
    private javax.swing.JRadioButton rbnStaffMediated;
    private javax.swing.JRadioButton rbnCMS2;
    private javax.swing.JRadioButton rbnCMS1;
    private javax.swing.JScrollPane scrlDetail;
    private javax.swing.JScrollPane scrlEntities;
    private javax.swing.JScrollPane scrlFileSystem;
    private javax.swing.JScrollPane scrlJobQueueComplete;
    private javax.swing.JScrollPane scrlJobQueueDeposited;
    private javax.swing.JScrollPane scrlJobQueueFailed;
    private javax.swing.JScrollPane scrlJobQueuePending;
    private javax.swing.JScrollPane scrlJobQueueRunning;
    private javax.swing.JScrollPane scrlProducerList;
    private javax.swing.JScrollPane scrlStructMap;
    private javax.swing.ButtonGroup sourceButtonGroup;
    private javax.swing.JSplitPane splitAddIE;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JSplitPane splitMainDetail;
    private javax.swing.JSplitPane splitMainRight;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTable tblJobQueueComplete;
    private javax.swing.JTable tblJobQueueDeposited;
    private javax.swing.JTable tblJobQueueFailed;
    private javax.swing.JTable tblJobQueuePending;
    private javax.swing.JTable tblJobQueueRunning;
    private javax.swing.JTree treeEntities;
    private javax.swing.JTree treeFileSystem;
    private javax.swing.JTree treeStructMap;
    private javax.swing.JTextField txtProducerFilter;
    private javax.swing.JTextField txtSearch1;
    private javax.swing.JTextField txtSearch10;
    private javax.swing.JTextField txtSearch11;
    private javax.swing.JTextField txtSearch12;
    private javax.swing.JTextField txtSearch13;
    private javax.swing.JTextField txtSearch14;
    private javax.swing.JTextField txtSearch15;
    private javax.swing.JTextField txtSearch2;
    private javax.swing.JTextField txtSearch3;
    private javax.swing.JTextField txtSearch4;
    private javax.swing.JTextField txtSearch5;
    private javax.swing.JTextField txtSearch6;
    private javax.swing.JTextField txtSearch7;
    private javax.swing.JTextField txtSearch8;
    private javax.swing.JTextField txtSearch9;
    // End of variables declaration//GEN-END:variables
	
}
