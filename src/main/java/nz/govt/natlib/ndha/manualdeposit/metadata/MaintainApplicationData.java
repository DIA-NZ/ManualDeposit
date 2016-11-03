/**
 * Software License
 *
 * Copyright 2007/2008 National Library of New Zealand.
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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;

public class MaintainApplicationData extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5157971977565697484L;
	private MetaDataConfiguratorPresenter thePresenter;
	//private boolean _isSystem = false;
	private FormControl formControl;
	private ApplicationData applicationData;

	/** Creates new form MaintainApplicationData */
	public MaintainApplicationData(MetaDataConfiguratorPresenter presenter,
			FormControl control) {
		initComponents();
		thePresenter = presenter;
		formControl = control;
		checkButtons();
	}

	public void showApplicationData(ApplicationData appData) {
		applicationData = appData;
		if (applicationData != null) {
			txtDepositSetID.setText(applicationData.getDepositSetID());
			txtDepositWSDLURL.setText(applicationData.getDepositWsdlUrl());
			txtFTPPassword.setText(applicationData.getMetsFTPPassword());
			txtFTPPasswordConfirm.setText(applicationData.getMetsFTPPassword());
			txtFTPServer.setText(applicationData.getMetsFTPServer());
			txtFTPUser.setText(applicationData.getMetsFTPUser());
			txtSipStatusDefinitionFile.setText(applicationData
					.getSipStatusFile());
			txtJobQueuePath.setText(applicationData.getJobQueuePath());
			txtBulkQueuePath.setText(applicationData.getBulkUploadQueuePath());
			double refreshInterval = applicationData
					.getJobQueueRefreshInterval();
			refreshInterval = refreshInterval / 1000;
			txtJobQueueRefreshInterval.setText(String.format("%01.01f",
					refreshInterval));
			refreshInterval = applicationData.getSipStatusRefreshInterval();
			refreshInterval = refreshInterval / 60000;
			txtSIPStatusRefreshInterval.setText(String.format("%01.00f",
					refreshInterval));
			txtMETSSavePath.setText(applicationData.getMetsSavePath());
			txtMaxJobsRunning.setText(String.format("%d", applicationData
					.getMaximumJobsRunning()));
			txtSRUSearchSchema.setText(applicationData.getSruSearchSchema());
			txtCMS2Text.setText(applicationData.getCMS2Text());
			txtCMS2Label.setText(applicationData.getCMS2Label());
			txtCMS1SystemText
					.setText(applicationData.getCMS1SystemText());
			txtCMS1Label.setText(applicationData.getCMS1Label());
			txtPDSURL.setText(applicationData.getPdsUrl());
			txtProducerWSDLURL.setText(applicationData.getProducerWsdlUrl());
			txtRepositoryWSDLURL
					.setText(applicationData.getRepositoryWsdlUrl());
			txtCMS2SearchURL.setText(applicationData.getCMS2SearchUrl());
			txtDPSSearchURL.setText(applicationData.getDPSSearchUrl());
			txtContentAggregatorURL.setText(applicationData
					.getContentAggregatorUrl());
			txtCMS1SearchURL.setText(applicationData.getCMS1SearchUrl());
			txtSettingsPath.setText(applicationData.getSettingsPath());
			txtSourceDirOnServer
					.setText(applicationData.getSourceDirOnServer());
			txtUserInstitution.setText(applicationData
					.getDepositUserInstitution());
			txtMaxStructureLength.setText(String.format("%d", applicationData
					.getMaximumStructureLength()));
			txtMaxProvenanceEventLength.setText(String.format("%d",
					applicationData.getMaximumProvenanceEventLength()));
			txtMaxSRUQueries.setText(String.format("%d", applicationData
					.getBulkQueryCount()));
			if (applicationData.getMetsWriterClass().equals(
					"nz.govt.natlib.ndha.common.mets.MetsWriterImpl")) {
				cmbMETSWriter.setSelectedIndex(0);
			} else {
				cmbMETSWriter.setSelectedIndex(1);
			}
			if (applicationData.getDepositClass().equals(
					"nz.govt.natlib.ndha.common.exlibris.DepositDummy")) {
				cmbDepositClass.setSelectedIndex(0);
			} else {
				cmbDepositClass.setSelectedIndex(1);
			}
			if (applicationData.getLoginClass().equals(
					"nz.govt.natlib.ndha.common.exlibris.Login")) {
				cmbLoginClass.setSelectedIndex(0);
			} else {
				cmbLoginClass.setSelectedIndex(1);
			}
			if (applicationData
					.getSearchStrategyClass()
					.equals(
							"nz.govt.natlib.ndha.common.ilsquery.Z3950SearchStrategyImpl")) {
				chkUseZ39Search.setSelected(true);
			} else {
				chkUseZ39Search.setSelected(false);
			}
			chkIncludeFileDates.setSelected(applicationData
					.isIncludeFileDates());
		}
		checkButtons();
	}

	private void loadApplicationData(boolean isCancel) {
		String configName = "ApplicationDataForm";
		String appFile = "";
		if (formControl != null) {
			appFile = formControl.getExtra(configName, "");
		}
		try {
			appFile = thePresenter.loadApplicationFile(appFile, isCancel);
			formControl.setExtra(configName, appFile);
			lblFileName.setText(appFile);
		} catch (Exception ex) {
			showError("Error loading file", "Could not open specified file\n"
					+ ex.getMessage());
		}
	}

	private void saveApplicationData() {
		if (applicationData == null) {
			try {
				applicationData = ApplicationData.createNew();
			} catch (Exception ex) {
				showError("Error saving file", ex.getMessage());
				return;
			}
		}
		applicationData.setDepositSetID(txtDepositSetID.getText());
		applicationData.setDepositWsdlUrl(txtDepositWSDLURL.getText());
		//_appData.setMetsFTPPassword(txtFTPPassword.getText());
		String password = new String(txtFTPPassword.getPassword());
		applicationData.setMetsFTPPassword(password);
		applicationData.setMetsFTPServer(txtFTPServer.getText());
		applicationData.setMetsFTPUser(txtFTPUser.getText());
		applicationData.setSipStatusFile(txtSipStatusDefinitionFile.getText());
		applicationData.setJobQueuePath(txtJobQueuePath.getText());
		applicationData.setBulkUploadQueuePath(txtBulkQueuePath.getText());
		applicationData.setSruSearchSchema(txtSRUSearchSchema.getText());
		applicationData.setCMS2Text(txtCMS2Text.getText());
		applicationData.setCMS2Label(txtCMS2Label.getText());
		applicationData.setCMS1SystemText(txtCMS1SystemText.getText());
		applicationData.setCMS1Label(txtCMS1Label.getText());
		try {
			double refreshInterval = Double
					.parseDouble(txtJobQueueRefreshInterval.getText());
			applicationData
					.setJobQueueRefreshInterval((int) (refreshInterval * 1000));
		} catch (Exception ex) {
			applicationData.setJobQueueRefreshInterval(500);
		}
		try {
			double refreshInterval = Double
					.parseDouble(txtSIPStatusRefreshInterval.getText());
			applicationData
					.setSipStatusRefreshInterval((int) (refreshInterval * 60000));
		} catch (Exception ex) {
			applicationData.setSipStatusRefreshInterval(600000);
		}
		applicationData.setMetsSavePath(txtMETSSavePath.getText());
		try {
			applicationData.setMaximumJobsRunning(Integer
					.parseInt(txtMaxJobsRunning.getText()));
		} catch (Exception ex) {
			applicationData.setMaximumJobsRunning(1);
		}
		try {
			applicationData.setMaximumStructureLength(Integer
					.parseInt(txtMaxStructureLength.getText()));
		} catch (Exception ex) {
			applicationData.setMaximumStructureLength(100);
		}
		try {
			applicationData.setMaximumProvenanceEventLength(Integer
					.parseInt(txtMaxProvenanceEventLength.getText()));
		} catch (Exception ex) {
			applicationData.setMaximumProvenanceEventLength(100);
		}
		try {
			applicationData.setBulkQueryCount(Integer.parseInt(txtMaxSRUQueries
					.getText()));
		} catch (Exception ex) {
			applicationData.setBulkQueryCount(10);
		}
		applicationData.setPdsUrl(txtPDSURL.getText());
		applicationData.setProducerWsdlUrl(txtProducerWSDLURL.getText());
		applicationData.setRepositoryWsdlUrl(txtRepositoryWSDLURL.getText());
		applicationData.setDPSSearchUrl(txtDPSSearchURL.getText());
		applicationData.setContentAggregatorUrl(txtContentAggregatorURL
				.getText());
		applicationData.setCMS2SearchUrl(txtCMS2SearchURL.getText());
		applicationData.setCMS1SearchUrl(txtCMS1SearchURL.getText());
		try {
			applicationData.setSettingsPath(txtSettingsPath.getText());
		} catch (Exception ex) {
			this.showError("Error saving settings path", ex.getMessage());
		}
		applicationData.setSourceDirOnServer(txtSourceDirOnServer.getText());
		applicationData.setDepositUserInstitution(txtUserInstitution.getText());
		if (cmbMETSWriter.getSelectedIndex() == 0) {
			applicationData
					.setMetsWriterClass("nz.govt.natlib.ndha.common.mets.MetsWriterImpl");
		} else {
			applicationData
					.setMetsWriterClass("nz.govt.natlib.ndha.exlibrismetswriter.MetsWriterELImpl");
		}
		if (cmbDepositClass.getSelectedIndex() == 0) {
			applicationData
					.setDepositClass("nz.govt.natlib.ndha.common.exlibris.DepositDummy");
		} else {
			applicationData
					.setDepositClass("nz.govt.natlib.ndha.exlibrismetswriter.Deposit");
		}
		if (cmbLoginClass.getSelectedIndex() == 0) {
			applicationData
					.setLoginClass("nz.govt.natlib.ndha.common.exlibris.Login");
		} else {
			applicationData
					.setLoginClass("nz.govt.natlib.ndha.exlibrismetswriter.Login");
		}
		if (chkUseZ39Search.isSelected()) {
			applicationData
					.setSearchStrategyClass("nz.govt.natlib.ndha.common.ilsquery.Z3950SearchStrategyImpl");
		} else {
			applicationData
					.setSearchStrategyClass("nz.govt.natlib.ndha.common.ilsquery.SruSearchStrategyImpl");
		}
		applicationData.setIncludeFileDates(chkIncludeFileDates.isSelected());
		try {
			thePresenter.saveApplicationFile(applicationData);
			showMessage("File Saved", "Application Data File saved");
		} catch (Exception ex) {
			showError("Error saving file", ex.getMessage());
		}
	}

	private void checkButtons() {
		char[] password = txtFTPPassword.getPassword();
		char[] confirmation = txtFTPPasswordConfirm.getPassword();
		boolean passwordOk = (password.length == confirmation.length);
		for (int i = 0; i < password.length && passwordOk; i++) {
			passwordOk = password[i] == confirmation[i];
		}
		btnSave.setEnabled(passwordOk);
		if (passwordOk) {
			btnSave.setToolTipText("");
		} else {
			btnSave.setToolTipText("FTP Passwords do not match");
		}
	}

	private void showMessage(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showError(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.ERROR_MESSAGE);
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		btnSelectApplicationFile = new javax.swing.JButton();
		txtJobQueuePath = new javax.swing.JTextField();
		txtBulkQueuePath = new javax.swing.JTextField();
		txtSettingsPath = new javax.swing.JTextField();
		txtMETSSavePath = new javax.swing.JTextField();
		txtSourceDirOnServer = new javax.swing.JTextField();
		txtDepositWSDLURL = new javax.swing.JTextField();
		txtProducerWSDLURL = new javax.swing.JTextField();
		txtRepositoryWSDLURL = new javax.swing.JTextField();
		txtPDSURL = new javax.swing.JTextField();
		txtDPSSearchURL = new javax.swing.JTextField();
		txtContentAggregatorURL = new javax.swing.JTextField();
		txtCMS1SearchURL = new javax.swing.JTextField();
		txtCMS2SearchURL = new javax.swing.JTextField();
		txtSipStatusDefinitionFile = new javax.swing.JTextField();
		txtFTPServer = new javax.swing.JTextField();
		txtFTPUser = new javax.swing.JTextField();
		txtFTPPassword = new javax.swing.JPasswordField();
		txtFTPPasswordConfirm = new javax.swing.JPasswordField();
		txtJobQueueRefreshInterval = new javax.swing.JTextField();
		txtMaxJobsRunning = new javax.swing.JTextField();
		txtSRUSearchSchema = new javax.swing.JTextField();
		txtSIPStatusRefreshInterval = new javax.swing.JTextField();
		txtDepositSetID = new javax.swing.JTextField();
		cmbMETSWriter = new javax.swing.JComboBox();
		cmbDepositClass = new javax.swing.JComboBox();
		cmbLoginClass = new javax.swing.JComboBox();
		txtUserInstitution = new javax.swing.JTextField();
		txtMaxStructureLength = new javax.swing.JTextField();
		txtMaxProvenanceEventLength = new javax.swing.JTextField();
		chkUseZ39Search = new javax.swing.JCheckBox();
		jLabel14 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jLabel18 = new javax.swing.JLabel();
		jLabel20 = new javax.swing.JLabel();
		jLabel21 = new javax.swing.JLabel();
		jLabel22 = new javax.swing.JLabel();
		jLabel23 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		jLabel15 = new javax.swing.JLabel();
		jLabel17 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		jLabel19 = new javax.swing.JLabel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		btnSave = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();
		lblFileName = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel24 = new javax.swing.JLabel();
		jLabel25 = new javax.swing.JLabel();
		jLabel26 = new javax.swing.JLabel();
		jLabel27 = new javax.swing.JLabel();
		jLabel28 = new javax.swing.JLabel();
		jLabel29 = new javax.swing.JLabel();
		jLabel30 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jLabel31 = new javax.swing.JLabel();
		jLabel32 = new javax.swing.JLabel();
		jLabel33 = new javax.swing.JLabel();
		jLabel34 = new javax.swing.JLabel();
		txtCMS1SystemText = new javax.swing.JTextField();
		jLabel35 = new javax.swing.JLabel();
		txtCMS2Text = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		chkIncludeFileDates = new javax.swing.JCheckBox();
		jLabel36 = new javax.swing.JLabel();
		txtMaxSRUQueries = new javax.swing.JTextField();
		jLabel38 = new javax.swing.JLabel();
		txtCMS2Label = new javax.swing.JTextField();
		jLabel39 = new javax.swing.JLabel();
		txtCMS1Label = new javax.swing.JTextField();

		btnSelectApplicationFile.setText("Select Application File");
		btnSelectApplicationFile
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						btnSelectApplicationFileActionPerformed(evt);
					}
				});

		txtJobQueuePath
				.setToolTipText("Directory where job details are stored");

		txtBulkQueuePath
				.setToolTipText("Directory where job details are stored");

		txtSettingsPath
				.setToolTipText("Directory where the application stores details such as screen size/position");

		txtMETSSavePath
				.setToolTipText("Root directory of where the application should create the SIP");

		txtSourceDirOnServer.setToolTipText("May not be used at the moment.");

		txtDepositWSDLURL.setToolTipText("");

		txtProducerWSDLURL.setToolTipText("");

		txtRepositoryWSDLURL.setToolTipText("");

		txtPDSURL.setToolTipText("");

		txtDPSSearchURL.setToolTipText("");

		txtContentAggregatorURL.setToolTipText("");

		txtCMS1SearchURL.setToolTipText("");

		txtCMS2SearchURL
				.setToolTipText("MetsCopyLocation is the place where files will be copied to in preparation for loading to the DPS.  Leave blank if using FTP.  This will copy into whatever is the current directory");

		txtSipStatusDefinitionFile
				.setToolTipText("This file contains a list of all the statii that can be returned from the DPS");

		txtFTPPassword
				.setToolTipText("This is encrypted in the configuration file, and not visible here");
		txtFTPPassword.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				txtFTPPasswordFocusLost(evt);
			}
		});

		txtFTPPasswordConfirm
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						txtFTPPasswordConfirmFocusLost(evt);
					}
				});

		txtJobQueueRefreshInterval
				.setToolTipText("How often the system checks the status of jobs in the job queue");

		txtMaxJobsRunning
				.setToolTipText("How many jobs should the system process at a time.");

		txtSIPStatusRefreshInterval
				.setToolTipText("How often the system queries the DPS for the status of deposits");

		cmbMETSWriter.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Standard", "ExLibris" }));

		cmbDepositClass.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Standard", "ExLibris" }));

		cmbLoginClass.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Standard", "ExLibris" }));

		txtMaxStructureLength
				.setToolTipText("Maximum length for structure mao items");

		txtMaxProvenanceEventLength
				.setToolTipText("Maximum length for provenance events");

		chkUseZ39Search
				.setToolTipText("Use Z39 or SRU search.  Preferred option is SRU.");

		jLabel14.setText("Settings Path");

		jLabel13.setText("Job Queue Path");

		jLabel9.setText("METS Save Path");

		jLabel18.setText("Source Dir on Server");

		jLabel20.setText("Deposit WSDL URL");

		jLabel21.setText("Producer WSDL URL");

		jLabel22.setText("PDS URL");

		jLabel23.setText("FTP Server");
		jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

		jLabel11.setText("FTP User");

		jLabel12.setText("FTP Password");

		jLabel15.setText("Job Queue Refresh Interval (secs)");

		jLabel17.setText("METS Writer");

		jLabel16.setText("Max Jobs Running");

		jLabel19.setText("Deposit Set ID");

		jLabel1.setText("Use Z39 Search");

		jLabel2.setText("User Institution");

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

		lblFileName.setText(" ");

		jLabel3.setText("Login Class");

		jLabel4.setText("Deposit Class");

		jLabel24.setText("SIP Status Refresh Interval (mins)");

		jLabel25.setText("Repository WSDL URL");

		jLabel26.setText("Max Structure Length");

		jLabel27.setText("Max Provenance Event Length");

		jLabel28.setText("CMS 1 Search URL");

		jLabel29.setText("CMS 2 Search URL");

		jLabel30.setText("SIP Status Definition File");

		jLabel5.setText("Re-enter password");

		jLabel6.setText("SRU Search Schema");

		jLabel31.setText("DPS Search URL");

		jLabel32.setText("Content Aggregator URL");

		jLabel33.setText("Bulk Upload Queue Path");

		jLabel34.setText("CMS 1 System Text");

		txtCMS1SystemText
				.setToolTipText("How often the system queries the DPS for the status of deposits");

		jLabel35.setText("CMS 2 System Text");

		jLabel7.setText("Include file dates in deposit");

		jLabel36.setText("Max SRU Queries");
		
		jLabel38 = new JLabel("CMS 2 Search Label");

		txtMaxSRUQueries
				.setToolTipText("How many SRU queries should process at a time for bulk load.");
		
		jLabel39 = new JLabel("CMS 1 Search Label");
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addGap(10)
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(jLabel33)
								.addComponent(jLabel21)
								.addComponent(jLabel20)
								.addComponent(jLabel18)
								.addComponent(jLabel9)
								.addComponent(jLabel25)
								.addComponent(jLabel22)
								.addComponent(jLabel13)
								.addComponent(jLabel14)
								.addComponent(jLabel28)
								.addComponent(jLabel29)
								.addComponent(jLabel32)
								.addComponent(jLabel31)
								.addComponent(jLabel15)
								.addComponent(jLabel24)
								.addComponent(jLabel17)
								.addComponent(jLabel11)
								.addComponent(jLabel23)
								.addComponent(jLabel30)
								.addComponent(jLabel34)
								.addComponent(jLabel27)))
						.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(jLabel39)))
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addGap(4)
							.addGroup(layout.createParallelGroup(Alignment.TRAILING)
								.addComponent(txtCMS2SearchURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtCMS1SearchURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtRepositoryWSDLURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtJobQueuePath, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtSettingsPath, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtMETSSavePath, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtSourceDirOnServer, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtProducerWSDLURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtPDSURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtDepositWSDLURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addComponent(txtDPSSearchURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
									.addComponent(txtBulkQueuePath, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGap(0))
						.addGroup(layout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtCMS1Label)
								.addComponent(txtMaxProvenanceEventLength)
								.addComponent(txtCMS1SystemText)
								.addComponent(cmbMETSWriter, 0, 121, Short.MAX_VALUE))
							.addGap(147)
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(jLabel16)
								.addComponent(jLabel12)
								.addComponent(jLabel4)
								.addComponent(jLabel19)
								.addComponent(jLabel35)
								.addComponent(jLabel26)
								.addComponent(jLabel38))
							.addGap(138)
							.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtCMS2Label)
								.addComponent(txtMaxStructureLength, Alignment.TRAILING)
								.addComponent(txtCMS2Text)
								.addComponent(cmbDepositClass, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(txtDepositSetID)
								.addComponent(txtMaxJobsRunning)
								.addComponent(txtFTPPassword, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
								.addComponent(btnSave, Alignment.TRAILING))
							.addGap(18)
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(jLabel6)
								.addGroup(layout.createSequentialGroup()
									.addComponent(jLabel2)
									.addContainerGap())
								.addGroup(layout.createParallelGroup(Alignment.LEADING)
									.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(Alignment.LEADING)
											.addComponent(jLabel5)
											.addComponent(jLabel36))
										.addGap(8)
										.addGroup(layout.createParallelGroup(Alignment.TRAILING)
											.addComponent(txtFTPPasswordConfirm, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
											.addComponent(txtMaxSRUQueries, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
											.addComponent(txtUserInstitution, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
											.addGroup(Alignment.LEADING, layout.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(txtSRUSearchSchema, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)))
										.addGap(40))
									.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
											.addGroup(layout.createSequentialGroup()
												.addComponent(jLabel7)
												.addGap(29)
												.addComponent(chkIncludeFileDates))
											.addGroup(layout.createSequentialGroup()
												.addComponent(jLabel1)
												.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(chkUseZ39Search))
											.addComponent(jLabel3))
										.addGap(130)
										.addComponent(cmbLoginClass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addContainerGap()))))))
				.addGroup(layout.createSequentialGroup()
					.addGap(180)
					.addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(txtSIPStatusRefreshInterval, Alignment.LEADING)
						.addComponent(txtJobQueueRefreshInterval, Alignment.LEADING)
						.addComponent(txtFTPUser, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
					.addContainerGap(915, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup()
					.addGap(10)
					.addComponent(btnSelectApplicationFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblFileName, GroupLayout.DEFAULT_SIZE, 1068, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup()
					.addGap(180)
					.addComponent(txtContentAggregatorURL, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup()
					.addGap(180)
					.addComponent(txtSipStatusDefinitionFile, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup()
					.addGap(180)
					.addComponent(txtFTPServer, GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup()
					.addContainerGap(1142, Short.MAX_VALUE)
					.addComponent(btnCancel)
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSelectApplicationFile)
						.addComponent(lblFileName))
					.addGap(13)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtJobQueuePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel13))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(jLabel33)
						.addComponent(txtBulkQueuePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtSettingsPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel14))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtMETSSavePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel9))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtSourceDirOnServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel18))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtDepositWSDLURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel20))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtProducerWSDLURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel21))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(jLabel25)
						.addComponent(txtRepositoryWSDLURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtPDSURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel22))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(jLabel31)
						.addComponent(txtDPSSearchURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(jLabel32)
						.addComponent(txtContentAggregatorURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(jLabel28)
						.addComponent(txtCMS1SearchURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(jLabel29)
						.addComponent(txtCMS2SearchURL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(jLabel30)
						.addComponent(txtSipStatusDefinitionFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtFTPServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel23))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.CENTER)
								.addComponent(jLabel11)
								.addComponent(jLabel12)
								.addComponent(txtFTPUser, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(jLabel15)
								.addComponent(txtJobQueueRefreshInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(jLabel24)
								.addComponent(jLabel19)
								.addComponent(txtSIPStatusRefreshInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(jLabel17)
								.addComponent(jLabel4)
								.addComponent(cmbMETSWriter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(layout.createSequentialGroup()
							.addGap(32)
							.addComponent(jLabel16))
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtFTPPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtFTPPasswordConfirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel5))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtMaxJobsRunning, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtMaxSRUQueries, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel36))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtDepositSetID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel6)
								.addComponent(txtSRUSearchSchema, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(cmbDepositClass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel2)
								.addComponent(txtUserInstitution, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addGroup(layout.createParallelGroup(Alignment.BASELINE)
									.addComponent(jLabel34)
									.addComponent(jLabel35)
									.addComponent(txtCMS1SystemText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createParallelGroup(Alignment.BASELINE)
									.addComponent(txtCMS2Text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(jLabel3)
									.addComponent(cmbLoginClass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addGap(6)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtMaxStructureLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel26)
								.addComponent(jLabel27)
								.addComponent(txtMaxProvenanceEventLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(6))
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(chkIncludeFileDates)
								.addComponent(jLabel7))
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(txtCMS1Label, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(105))
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
							.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(Alignment.TRAILING)
									.addComponent(chkUseZ39Search)
									.addGroup(layout.createParallelGroup(Alignment.BASELINE)
										.addComponent(txtCMS2Label, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel38)
										.addComponent(jLabel1)))
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnCancel)
									.addComponent(btnSave)))
							.addGroup(layout.createSequentialGroup()
								.addComponent(jLabel39)
								.addContainerGap()))))
		);
		this.setLayout(layout);
	}// </editor-fold>
	//GEN-END:initComponents

	private void txtFTPPasswordConfirmFocusLost(java.awt.event.FocusEvent evt) {
		checkButtons();
	}

	private void txtFTPPasswordFocusLost(java.awt.event.FocusEvent evt) {
		checkButtons();
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		loadApplicationData(true);
	}

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
		saveApplicationData();
	}

	private void btnSelectApplicationFileActionPerformed(
			java.awt.event.ActionEvent evt) {
		loadApplicationData(false);
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnSelectApplicationFile;
	private javax.swing.JCheckBox chkIncludeFileDates;
	private javax.swing.JCheckBox chkUseZ39Search;
	private javax.swing.JComboBox cmbDepositClass;
	private javax.swing.JComboBox cmbLoginClass;
	private javax.swing.JComboBox cmbMETSWriter;
	private javax.swing.JLabel jLabel1;
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
	private javax.swing.JLabel jLabel24;
	private javax.swing.JLabel jLabel25;
	private javax.swing.JLabel jLabel26;
	private javax.swing.JLabel jLabel27;
	private javax.swing.JLabel jLabel28;
	private javax.swing.JLabel jLabel29;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel30;
	private javax.swing.JLabel jLabel31;
	private javax.swing.JLabel jLabel32;
	private javax.swing.JLabel jLabel33;
	private javax.swing.JLabel jLabel34;
	private javax.swing.JLabel jLabel35;
	private javax.swing.JLabel jLabel36;
	private javax.swing.JLabel jLabel38;
	private javax.swing.JLabel jLabel39;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JLabel lblFileName;
	private javax.swing.JTextField txtBulkQueuePath;
	private javax.swing.JTextField txtContentAggregatorURL;
	private javax.swing.JTextField txtDPSSearchURL;
	private javax.swing.JTextField txtDepositSetID;
	private javax.swing.JTextField txtDepositWSDLURL;
	private javax.swing.JPasswordField txtFTPPassword;
	private javax.swing.JPasswordField txtFTPPasswordConfirm;
	private javax.swing.JTextField txtFTPServer;
	private javax.swing.JTextField txtFTPUser;
	private javax.swing.JTextField txtJobQueuePath;
	private javax.swing.JTextField txtJobQueueRefreshInterval;
	private javax.swing.JTextField txtMETSSavePath;
	private javax.swing.JTextField txtMaxJobsRunning;
	private javax.swing.JTextField txtMaxProvenanceEventLength;
	private javax.swing.JTextField txtMaxSRUQueries;
	private javax.swing.JTextField txtMaxStructureLength;
	private javax.swing.JTextField txtPDSURL;
	private javax.swing.JTextField txtProducerWSDLURL;
	private javax.swing.JTextField txtRepositoryWSDLURL;
	private javax.swing.JTextField txtSIPStatusRefreshInterval;
	private javax.swing.JTextField txtSRUSearchSchema;
	private javax.swing.JTextField txtSettingsPath;
	private javax.swing.JTextField txtSipStatusDefinitionFile;
	private javax.swing.JTextField txtSourceDirOnServer;
	private javax.swing.JTextField txtCMS2SearchURL;
	private javax.swing.JTextField txtCMS2Text;
	private javax.swing.JTextField txtCMS2Label;
	private javax.swing.JTextField txtCMS1Label;
	private javax.swing.JTextField txtUserInstitution;
	private javax.swing.JTextField txtCMS1SearchURL;
	private javax.swing.JTextField txtCMS1SystemText;
}