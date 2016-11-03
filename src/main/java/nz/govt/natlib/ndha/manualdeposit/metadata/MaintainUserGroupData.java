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

import javax.swing.JOptionPane;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserGroupData.UserGroupDesc;

public class MaintainUserGroupData extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -126140009551343064L;
	private MetaDataConfiguratorPresenter thePresenter;
	private FormControl formControl;
	private UserGroupData theUserGroupData;

	/** Creates new form MaintainUserGroupData */
	public MaintainUserGroupData(MetaDataConfiguratorPresenter presenter,
			FormControl control) {
		initComponents(); // NOPMD
		//set material flow labels and text fields to invisible by default
		jLabel22.setVisible(false);
		jLabel23.setVisible(false);
		jLabel25.setText("Alma Material Flow ID");
		jLabel26.setVisible(false);
        txtSerialMaterialFlowID.setVisible(false);
        txtVideoMaterialFlowID.setVisible(false);
        //txtMonoMaterialFlowID.setVisible(false);
        txtMusicMaterialFlowID.setVisible(false);
		thePresenter = presenter;
		thePresenter.addUserGroupHandlers(lblCleanupDirectory, lblDelay,
						txtCleanupDelay, cmbCleanupType,
						lstCharacterTranslations, cmbCharacterPosition,
						txtCharacterToTranslate, txtTranslateItTo, cmbUserGroupDesc);
		formControl = control;
	}

	public void loadUserGroupData(UserGroupData userGroupData) {
		theUserGroupData = userGroupData;
		if (userGroupData.getUserGroupDesc().equals(UserGroupDesc.Published)){
	        jLabel2.setText("Include Serial");
	        jLabel3.setText("Include Mono");
	        jLabel4.setText("Include Video");
	        jLabel5.setText("Include Audio");
	        
	        jLabel9.setText("Serial MetaData File");
	        jLabel10.setText("Mono MetaData File");
	        jLabel11.setText("Video MetaData File");
	        jLabel12.setText("Audio MetaData File");
	        
	        jLabel22.setVisible(true);
	        jLabel23.setVisible(true);
	        jLabel25.setText("Mono Material Flow ID");
	        jLabel26.setVisible(true);
	        txtSerialMaterialFlowID.setVisible(true);
	        //txtMonoMaterialFlowID.setVisible(true);
	        txtVideoMaterialFlowID.setVisible(true);
	        txtMusicMaterialFlowID.setVisible(true);
	        
	        txtSerialMaterialFlowID.setText(theUserGroupData.getSerialMaterialFlowID());
	        txtAlmaOrMonoMaterialFlowID.setText(theUserGroupData.getMonoMaterialFlowID());
	        txtVideoMaterialFlowID.setText(theUserGroupData.getVideoMaterialFlowID());
	        txtMusicMaterialFlowID.setText(theUserGroupData.getAudioMaterialFlowID());
	        
		} else{
	        jLabel22.setVisible(false);
	        jLabel23.setVisible(false);
	        jLabel25.setText("Alma Material Flow ID");
	        jLabel26.setVisible(false);
	        txtSerialMaterialFlowID.setVisible(false);
	        txtVideoMaterialFlowID.setVisible(false);
	        //txtMonoMaterialFlowID.setVisible(false);
	        txtMusicMaterialFlowID.setVisible(false);
	        txtAlmaOrMonoMaterialFlowID.setText(theUserGroupData.getAlmaMaterialFlowID());
	        
	        jLabel2.setText("Include CMS 2 Search?");
	        jLabel3.setText("Include CMS 1 Search?");
	        jLabel4.setText("Include No CMS Option?");
	        jLabel5.setText("Include Staff Mediated?");
	        
	        jLabel9.setText("CMS 2 MetaData File");
	        jLabel10.setText("CMS 1 MetaData File");
	        jLabel11.setText("No CMS MetaData File");
	        jLabel12.setText("Staff Mediated MetaData File");
		}
		chkIncludeMultiEntityMenu.setSelected(theUserGroupData
				.isIncludeMultiEntityMenuItem());
		chkIncludeNoCMSOption.setSelected(theUserGroupData
				.isIncludeNoCMSOption());
		chkIncludeStaffMediated.setSelected(theUserGroupData
				.isIncludeProducerList());
		chkIncludeCMS2Search.setSelected(theUserGroupData
				.isIncludeCMS2Search());
		chkIncludeCMS1Search.setSelected(theUserGroupData
				.isIncludeCMS1Search());
		txtMaterialFlowID.setText(theUserGroupData.getMaterialFlowID());
		txtFileTypesPropsFile.setText(theUserGroupData.getFileTypesPropFile());
		txtNoCMSDataMetaDataFile.setText(theUserGroupData
				.getNoCMSMetaDataFile());
		txtSharedTemplateLocation.setText(theUserGroupData
				.getSharedTemplatePath());
		txtStaffMediatedMetaDataFile.setText(theUserGroupData
				.getStaffMediatedMetaDataFile());
		txtCMS2MetaDataFile.setText(theUserGroupData.getCMS2MetaDataFile());
		txtUserProducerID.setText(theUserGroupData.getUserProducerID());
		txtCMS1MetaDataFile.setText(theUserGroupData
				.getCMS1MetaDataFile());
		txtCleanupDelay.setText(String.format("%s", theUserGroupData
				.getCleanupDelay()));
		txtBulkLoadBatchSize.setText(String.format("%s", theUserGroupData
				.getBulkBatchSize()));
		txtInterimCleanupDirectory.setText(theUserGroupData
				.getInterimFileLocation());
		cmbCleanupType.setSelectedItem(theUserGroupData.getCleanupType());
		cmbUserGroupDesc.setSelectedItem(theUserGroupData.getUserGroupDesc());
	}

	private void loadUserGroupData(boolean isCancel) {
		String configName = "UserGroupData";
		String userGroupFile = "";
		if (formControl != null) {
			userGroupFile = formControl.getExtra(configName, "");
		}
		try {
			userGroupFile = thePresenter.loadUserGroupFile(userGroupFile,
					isCancel);
			formControl.setExtra(configName, userGroupFile);
		} catch (Exception ex) {
			showError("Error loading file", "Could not open specified file\n"
					+ ex.getMessage());
		}
		lblFileName.setText(userGroupFile);
	}

	private void showError(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.ERROR_MESSAGE);
	}

	private void showMessage(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void checkButtons() {
		boolean translationSelected = (lstCharacterTranslations
				.getSelectedValue() != null);
		btnDeleteTranslationChar.setEnabled(translationSelected);
		if (translationSelected) {
			btnUpdateTranslationData.setEnabled(true);
			btnUpdateTranslationData.setText("Update translation");
		} else {
			btnUpdateTranslationData.setText("Add translation");
			boolean enabled = txtCharacterToTranslate.getText() != null
					&& !txtCharacterToTranslate.getText().equals("");
			enabled = enabled && txtTranslateItTo.getText() != null
					&& !txtTranslateItTo.getText().equals("");
			btnUpdateTranslationData.setEnabled(enabled);
		}
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSelectFile = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtSharedTemplateLocation = new javax.swing.JTextField();
        btnBrowseSharedTemplate = new javax.swing.JButton();
        txtFileTypesPropsFile = new javax.swing.JTextField();
        btnFileTypesPropsFile = new javax.swing.JButton();
        cmbCleanupType = new javax.swing.JComboBox();
        lblDelay = new javax.swing.JLabel();
        txtCleanupDelay = new javax.swing.JTextField();
        lblCleanupDirectory = new javax.swing.JLabel();
        txtInterimCleanupDirectory = new javax.swing.JTextField();
        btnInterimCleanupDirectory = new javax.swing.JButton();
        chkIncludeCMS2Search = new javax.swing.JCheckBox();
        chkIncludeCMS1Search = new javax.swing.JCheckBox();
        chkIncludeNoCMSOption = new javax.swing.JCheckBox();
        chkIncludeStaffMediated = new javax.swing.JCheckBox();
        chkIncludeMultiEntityMenu = new javax.swing.JCheckBox();
        txtMaterialFlowID = new javax.swing.JTextField();
        txtUserProducerID = new javax.swing.JTextField();
        txtCMS2MetaDataFile = new javax.swing.JTextField();
        btnBrowseCMS2MetaDataFile = new javax.swing.JButton();
        txtCMS1MetaDataFile = new javax.swing.JTextField();
        btnBrowseCMS1MetaDataFile = new javax.swing.JButton();
        txtNoCMSDataMetaDataFile = new javax.swing.JTextField();
        btnBrowseNoCMSMetaDataFile = new javax.swing.JButton();
        txtStaffMediatedMetaDataFile = new javax.swing.JTextField();
        btnBrowseStaffMediatedMetaDataFile = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblFileName = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstCharacterTranslations = new javax.swing.JList();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtCharacterToTranslate = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cmbCharacterPosition = new javax.swing.JComboBox();
        txtTranslateItTo = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btnUpdateTranslationData = new javax.swing.JButton();
        btnDeleteTranslationChar = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txtBulkLoadBatchSize = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        cmbUserGroupDesc = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtSerialMaterialFlowID = new javax.swing.JTextField();
        txtVideoMaterialFlowID = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtAlmaOrMonoMaterialFlowID = new javax.swing.JTextField();
        txtMusicMaterialFlowID = new javax.swing.JTextField();

        btnSelectFile.setText("Select User Group Data File");
        btnSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFileActionPerformed(evt);
            }
        });

        jLabel1.setText("Shared Template Location");

        jLabel13.setText("Structure map descriptions file");

        jLabel2.setText("Include CMS 2 Search?");

        jLabel3.setText("Include CMS 1 Search?");

        jLabel4.setText("Include No CMS Option?");

        jLabel5.setText("Include Staff Mediated?");

        jLabel6.setText("Include Multi-entity Menu Item?");

        jLabel7.setText("EMu Material Flow ID");

        jLabel8.setText("User Producer ID");

        jLabel9.setText("CMS 2  MetaData File");

        jLabel10.setText("CMS 1 MetaData File");

        jLabel11.setText("No CMS MetaData File");

        jLabel12.setText("Staff Mediated MetaData File");

        btnBrowseSharedTemplate.setText("Browse");
        btnBrowseSharedTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseSharedTemplateActionPerformed(evt);
            }
        });

        btnFileTypesPropsFile.setText("Browse");
        btnFileTypesPropsFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileTypesPropsFileActionPerformed(evt);
            }
        });

        lblDelay.setText("Delay (days)");

        txtCleanupDelay.setText("                       ");

        lblCleanupDirectory.setText("Interim Cleanup Directory");

        btnInterimCleanupDirectory.setText("Browse");
        btnInterimCleanupDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInterimCleanupDirectoryActionPerformed(evt);
            }
        });

        btnBrowseCMS2MetaDataFile.setText("Browse");
        btnBrowseCMS2MetaDataFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseCMS2MetaDataFileActionPerformed(evt);
            }
        });

        btnBrowseCMS1MetaDataFile.setText("Browse");
        btnBrowseCMS1MetaDataFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseCMS1MetaDataFileActionPerformed(evt);
            }
        });

        btnBrowseNoCMSMetaDataFile.setText("Browse");
        btnBrowseNoCMSMetaDataFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseNoCMSMetaDataFileActionPerformed(evt);
            }
        });

        btnBrowseStaffMediatedMetaDataFile.setText("Browse");
        btnBrowseStaffMediatedMetaDataFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseStaffMediatedMetaDataFileActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel14.setText("Cleanup Type");

        jLabel15.setText("This folder is not required for the No File Cleanup type");

        jScrollPane1.setViewportView(lstCharacterTranslations);

        jLabel16.setText("Character Translations");

        jLabel17.setText("Character to translate");

        jLabel18.setText("Translate it to");

        cmbCharacterPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCharacterPositionActionPerformed(evt);
            }
        });

        jLabel19.setText("Search for this");

        btnUpdateTranslationData.setText("Update Data");
        btnUpdateTranslationData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateTranslationDataActionPerformed(evt);
            }
        });

        btnDeleteTranslationChar.setText("Delete Translation Char");
        btnDeleteTranslationChar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTranslationCharActionPerformed(evt);
            }
        });

        jLabel20.setText("Bulk load batch size");

        jLabel21.setText("UserGroupDesc");

        cmbUserGroupDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbUserGroupDescActionPerformed(evt);
            }
        });

        jLabel22.setText("Serial Material Flow ID");

        jLabel23.setText("Video Material Flow ID");

        txtSerialMaterialFlowID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSerialMaterialFlowIDActionPerformed(evt);
            }
        });

        txtVideoMaterialFlowID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVideoMaterialFlowIDActionPerformed(evt);
            }
        });

        jLabel25.setText("Alma Material Flow ID");
        jLabel25.setToolTipText("");

        jLabel26.setText("Audio Material Flow ID");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSave)
                        .addGap(90, 90, 90)
                        .addComponent(btnCancel)
                        .addGap(163, 163, 163))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSelectFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFileName))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel13)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel14)
                                            .addComponent(lblCleanupDirectory))
                                        .addGap(17, 17, 17)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(chkIncludeNoCMSOption)
                                                    .addComponent(chkIncludeCMS2Search))
                                                .addGap(28, 28, 28)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel23)
                                                    .addComponent(jLabel22))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtSerialMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtVideoMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel25)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(txtAlmaOrMonoMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel26)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(txtMusicMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(30, 30, 30)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addGroup(layout.createSequentialGroup()
                                                                .addGap(133, 133, 133)
                                                                .addComponent(txtBulkLoadBatchSize, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                    .addComponent(chkIncludeCMS1Search, javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addComponent(chkIncludeStaffMediated, javax.swing.GroupLayout.Alignment.LEADING))))
                                                        .addGap(92, 92, 92))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel5)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel21)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(cmbUserGroupDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jLabel20))))
                                            .addComponent(txtFileTypesPropsFile)
                                            .addComponent(txtSharedTemplateLocation)
                                            .addComponent(txtInterimCleanupDirectory)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(cmbCleanupType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lblDelay)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtCleanupDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel15)
                                                .addGap(0, 0, Short.MAX_VALUE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel16)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtStaffMediatedMetaDataFile)
                                            .addComponent(txtNoCMSDataMetaDataFile)
                                            .addComponent(txtCMS1MetaDataFile)
                                            .addComponent(txtCMS2MetaDataFile)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel17)
                                                    .addComponent(jLabel18))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(txtCharacterToTranslate)
                                                    .addComponent(txtTranslateItTo, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel19)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmbCharacterPosition, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(chkIncludeMultiEntityMenu)
                                                    .addComponent(txtMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtUserProducerID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(btnDeleteTranslationChar)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(btnUpdateTranslationData, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnInterimCleanupDirectory)
                                    .addComponent(btnBrowseStaffMediatedMetaDataFile)
                                    .addComponent(btnBrowseNoCMSMetaDataFile)
                                    .addComponent(btnBrowseCMS1MetaDataFile)
                                    .addComponent(btnFileTypesPropsFile)
                                    .addComponent(btnBrowseSharedTemplate)
                                    .addComponent(btnBrowseCMS2MetaDataFile))))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectFile)
                    .addComponent(lblFileName))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(btnBrowseSharedTemplate)
                            .addComponent(txtSharedTemplateLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(btnFileTypesPropsFile)
                            .addComponent(txtFileTypesPropsFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtCleanupDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDelay)
                            .addComponent(cmbCleanupType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtInterimCleanupDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCleanupDirectory)
                            .addComponent(btnInterimCleanupDirectory))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(chkIncludeCMS2Search)
                                    .addComponent(jLabel2)
                                    .addComponent(chkIncludeCMS1Search))
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtAlmaOrMonoMaterialFlowID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22)
                        .addComponent(txtSerialMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(chkIncludeNoCMSOption)
                            .addComponent(chkIncludeStaffMediated))
                        .addGap(79, 79, 79)
                        .addComponent(btnBrowseCMS2MetaDataFile))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(txtMusicMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel23)
                                    .addComponent(txtVideoMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(chkIncludeMultiEntityMenu))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel21)
                                .addComponent(cmbUserGroupDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtMaterialFlowID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)
                            .addComponent(txtBulkLoadBatchSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtUserProducerID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtCMS2MetaDataFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtCMS1MetaDataFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBrowseCMS1MetaDataFile))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtNoCMSDataMetaDataFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBrowseNoCMSMetaDataFile))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtStaffMediatedMetaDataFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBrowseStaffMediatedMetaDataFile))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtCharacterToTranslate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(cmbCharacterPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtTranslateItTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDeleteTranslationChar)
                            .addComponent(btnUpdateTranslationData))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addGap(18, 18, 18))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbCharacterPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCharacterPositionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCharacterPositionActionPerformed

    private void cmbUserGroupDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbUserGroupDescActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbUserGroupDescActionPerformed

    private void txtSerialMaterialFlowIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSerialMaterialFlowIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSerialMaterialFlowIDActionPerformed

    private void txtVideoMaterialFlowIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVideoMaterialFlowIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVideoMaterialFlowIDActionPerformed

	private void btnUpdateTranslationDataActionPerformed(
			java.awt.event.ActionEvent evt) {
		thePresenter.updateCharacterTranslation();
	}

	private void btnDeleteTranslationCharActionPerformed(
			java.awt.event.ActionEvent evt) {
		thePresenter.deleteCharacterTranslation();
	}

	private void btnInterimCleanupDirectoryActionPerformed(
			java.awt.event.ActionEvent evt) {
		String extraName = "InterimCleanupDirectory";
		String propertiesFile = thePresenter
				.getInterimCleanupDirectory(formControl.getExtra(extraName, ""));
		txtInterimCleanupDirectory.setText(propertiesFile);
		formControl.setExtra(extraName, propertiesFile);
	}

	private void btnFileTypesPropsFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		String extraName = "FileTypesPropertiesFile";
		String propertiesFile = thePresenter
				.getFileTypesPropertiesFile(formControl.getExtra(extraName, ""));
		txtFileTypesPropsFile.setText(propertiesFile);
		formControl.setExtra(extraName, propertiesFile);
	}

	private void btnSelectFileActionPerformed(java.awt.event.ActionEvent evt) {
		loadUserGroupData(false);
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		loadUserGroupData(true);
	}

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			if (theUserGroupData == null) {
				theUserGroupData = UserGroupData.create("");
			}
			theUserGroupData
					.setIncludeMultiEntityMenuItem(chkIncludeMultiEntityMenu
							.isSelected());
			theUserGroupData.setIncludeNoCMSOption(chkIncludeNoCMSOption
					.isSelected());
			theUserGroupData.setIncludeProducerList(chkIncludeStaffMediated
					.isSelected());
			theUserGroupData.setIncludeCMS2Search(chkIncludeCMS2Search
					.isSelected());
			theUserGroupData.setIncludeCMS1Search(chkIncludeCMS1Search
					.isSelected());
			theUserGroupData.setMaterialFlowID(txtMaterialFlowID.getText());
			theUserGroupData.setEmuMaterialFlowID(txtMaterialFlowID.getText());          
			if (cmbUserGroupDesc.getSelectedItem().equals(UserGroupDesc.Published)){
                theUserGroupData.setSerialMaterialFlowID(txtSerialMaterialFlowID.getText());
                theUserGroupData.setMonoMaterialFlowID(txtAlmaOrMonoMaterialFlowID.getText());
                theUserGroupData.setVideoMaterialFlowID(txtVideoMaterialFlowID.getText());
                theUserGroupData.setAudioMaterialFlowID(txtMusicMaterialFlowID.getText());
			}else{
				theUserGroupData.setAlmaMaterialFlowID(txtAlmaOrMonoMaterialFlowID.getText());
			}
			
			theUserGroupData.setNoCMSMetaDataFile(txtNoCMSDataMetaDataFile
					.getText());
			theUserGroupData.setSharedTemplatePath(txtSharedTemplateLocation
					.getText());
			theUserGroupData
					.setStaffMediatedMetaDataFile(txtStaffMediatedMetaDataFile
							.getText());
			theUserGroupData.setCMS2MetaDataFile(txtCMS2MetaDataFile
					.getText());
			theUserGroupData.setUserProducerID(txtUserProducerID.getText());
			theUserGroupData.setCMS1MetaDataFile(txtCMS1MetaDataFile
					.getText());
			theUserGroupData.setFileTypesPropFile(txtFileTypesPropsFile
					.getText());
			// Cleanup delay validation.
			// Stops user entering a zero day delay time.
			// Added by Ben. 09/09/2013.
			if((cmbCleanupType.getSelectedIndex() == 2) && ((Integer.parseInt(txtCleanupDelay.getText()) == 0))){
				showError("Error saving file", "Cleanup delay can not have a value of 0 days.");
				return;
			}
			else if((cmbCleanupType.getSelectedIndex() != 2) && (Integer.parseInt(txtCleanupDelay.getText()) == 0)){
				txtCleanupDelay.setText(""+theUserGroupData.getCleanupDelay());
			}
			else{
				theUserGroupData.setCleanupDelay(Integer.parseInt(txtCleanupDelay.getText()));
			}
			theUserGroupData.setBulkBatchSize(Integer
					.parseInt(txtBulkLoadBatchSize.getText()));
			theUserGroupData.setInterimFileLocation(txtInterimCleanupDirectory
					.getText());
			theUserGroupData
					.setCleanupType((UserGroupData.ECleanupType) cmbCleanupType
							.getSelectedItem());
			theUserGroupData.setUserGroupDesc((UserGroupData.UserGroupDesc) cmbUserGroupDesc.getSelectedItem());
			thePresenter.saveUserGroupData(theUserGroupData);
			showMessage("File Saved", "User Group Data File saved");

		} catch (Exception ex) {
			showError("Error saving file",
					"Couldn't save user group data file\n" + ex.getMessage());
		}
	}

	private void btnBrowseStaffMediatedMetaDataFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		txtStaffMediatedMetaDataFile.setText(thePresenter.getMetaDataFile(""));
	}

	private void btnBrowseNoCMSMetaDataFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		txtNoCMSDataMetaDataFile.setText(thePresenter.getMetaDataFile(""));
	}

	private void btnBrowseCMS1MetaDataFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		txtCMS1MetaDataFile.setText(thePresenter.getMetaDataFile(""));
	}

	private void btnBrowseCMS2MetaDataFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		txtCMS2MetaDataFile.setText(thePresenter.getMetaDataFile(""));
	}

	private void btnBrowseSharedTemplateActionPerformed(
			java.awt.event.ActionEvent evt) {
		String extraName = "SharedTemplateDefault";
		txtSharedTemplateLocation
				.setText(thePresenter.getSharedTemplateLocation(formControl
						.getExtra(extraName, "")));
		formControl.setExtra(extraName, txtSharedTemplateLocation.getText());
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseCMS1MetaDataFile;
    private javax.swing.JButton btnBrowseCMS2MetaDataFile;
    private javax.swing.JButton btnBrowseNoCMSMetaDataFile;
    private javax.swing.JButton btnBrowseSharedTemplate;
    private javax.swing.JButton btnBrowseStaffMediatedMetaDataFile;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteTranslationChar;
    private javax.swing.JButton btnFileTypesPropsFile;
    private javax.swing.JButton btnInterimCleanupDirectory;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSelectFile;
    private javax.swing.JButton btnUpdateTranslationData;
    private javax.swing.JCheckBox chkIncludeCMS1Search;
    private javax.swing.JCheckBox chkIncludeCMS2Search;
    private javax.swing.JCheckBox chkIncludeMultiEntityMenu;
    private javax.swing.JCheckBox chkIncludeNoCMSOption;
    private javax.swing.JCheckBox chkIncludeStaffMediated;
    private javax.swing.JComboBox cmbCharacterPosition;
    private javax.swing.JComboBox cmbCleanupType;
    private javax.swing.JComboBox cmbUserGroupDesc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCleanupDirectory;
    private javax.swing.JLabel lblDelay;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JList lstCharacterTranslations;
    private javax.swing.JTextField txtAlmaOrMonoMaterialFlowID;
    private javax.swing.JTextField txtBulkLoadBatchSize;
    private javax.swing.JTextField txtCMS1MetaDataFile;
    private javax.swing.JTextField txtCMS2MetaDataFile;
    private javax.swing.JTextField txtCharacterToTranslate;
    private javax.swing.JTextField txtCleanupDelay;
    private javax.swing.JTextField txtFileTypesPropsFile;
    private javax.swing.JTextField txtInterimCleanupDirectory;
    private javax.swing.JTextField txtMaterialFlowID;
    private javax.swing.JTextField txtMusicMaterialFlowID;
    private javax.swing.JTextField txtNoCMSDataMetaDataFile;
    private javax.swing.JTextField txtSerialMaterialFlowID;
    private javax.swing.JTextField txtSharedTemplateLocation;
    private javax.swing.JTextField txtStaffMediatedMetaDataFile;
    private javax.swing.JTextField txtTranslateItTo;
    private javax.swing.JTextField txtUserProducerID;
    private javax.swing.JTextField txtVideoMaterialFlowID;
    // End of variables declaration//GEN-END:variables
	
}