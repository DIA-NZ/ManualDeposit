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

import java.awt.Component;
import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nz.govt.natlib.ndha.common.exlibris.SIPStatus;
import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.manualdeposit.FormUtilities;

public class MetaDataConfigurator extends javax.swing.JFrame implements
		IMetaDataConfigurator {
    
    	private static final long serialVersionUID = 6465556271392696158L;
	private final static Log LOG = LogFactory
			.getLog(MetaDataConfigurator.class);
	private MetaDataConfiguratorPresenter metaPresenter;
	private FormControl theFormControl;
	private MaintainMetaData maintainMetaData;
	private MaintainApplicationData maintainApp;
	private MaintainUsers maintainUsers;
	private MaintainUserGroupData maintainUserGroups;
	private MaintainSipStatusDefinitions maintainSipStatusDefinitions;

	/**
	 * 
	 */
	/** Creates new form MetaDataConfigurator */
	public MetaDataConfigurator() {
		initComponents(); // NOPMD
	}

	public void setPresenter(final MetaDataConfiguratorPresenter thePresenter) {
		metaPresenter = thePresenter;
	}

	public void setupScreen(final String settingsPath) {
		try {
			theFormControl = new FormControl(this, settingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
		maintainApp = new MaintainApplicationData(metaPresenter, theFormControl);
		final javax.swing.GroupLayout pnlAppLayout = new javax.swing.GroupLayout(
				pnlApplicationData);
		pnlApplicationData.setLayout(pnlAppLayout);
		pnlAppLayout.setHorizontalGroup(pnlAppLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainApp, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		pnlAppLayout.setVerticalGroup(pnlAppLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainApp, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		maintainUsers = new MaintainUsers(metaPresenter, theFormControl);
		final javax.swing.GroupLayout pnlUsersLayout = new javax.swing.GroupLayout(
				pnlUsers);
		pnlUsers.setLayout(pnlUsersLayout);
		pnlUsersLayout.setHorizontalGroup(pnlUsersLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainUsers, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		pnlUsersLayout.setVerticalGroup(pnlUsersLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainUsers, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		maintainUserGroups = new MaintainUserGroupData(metaPresenter,
				theFormControl);
		final javax.swing.GroupLayout pnlUserGroupsLayout = new javax.swing.GroupLayout(
				pnlUserGroupData);
		pnlUserGroupData.setLayout(pnlUserGroupsLayout);
		pnlUserGroupsLayout.setHorizontalGroup(pnlUserGroupsLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(maintainUserGroups,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		pnlUserGroupsLayout.setVerticalGroup(pnlUserGroupsLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(maintainUserGroups,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		maintainMetaData = new MaintainMetaData(metaPresenter, theFormControl);
		final javax.swing.GroupLayout pnlMetaLayout = new javax.swing.GroupLayout(
				pnlMetaData);
		pnlMetaData.setLayout(pnlMetaLayout);
		pnlMetaLayout.setHorizontalGroup(pnlMetaLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainMetaData, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		pnlMetaLayout.setVerticalGroup(pnlMetaLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainMetaData, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		maintainSipStatusDefinitions = new MaintainSipStatusDefinitions(
				metaPresenter, theFormControl);
		final javax.swing.GroupLayout pnlSipLayout = new javax.swing.GroupLayout(
				pnlSipStatusData);
		pnlSipStatusData.setLayout(pnlSipLayout);
		pnlSipLayout.setHorizontalGroup(pnlSipLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainSipStatusDefinitions,
				javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		pnlSipLayout.setVerticalGroup(pnlSipLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				maintainSipStatusDefinitions,
				javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		checkButtons();
	}

	public void showUser(final IndigoUser user) {
		maintainUsers.showUser(user);
	}

	public void loadUserGroupData(final UserGroupData theData) {
		maintainUserGroups.loadUserGroupData(theData);
	}

	public void showApplicationData(final ApplicationData theData) {
		maintainApp.showApplicationData(theData);
	}

	public void showSipStatus(final SIPStatus sipData) {
		maintainSipStatusDefinitions.showSipStatus(sipData);
	}

	public void loadData(final MetaDataTypeImpl theData, final String dcOther) {
		maintainMetaData.loadData(theData, dcOther);
	}

	public void showView() {
		setVisible(true);
	}

	public void setFormFont(final Font theFont) {
		UIManager.put("OptionPane.messageFont", theFont);
		UIManager.put("OptionPane.buttonFont", theFont);
		UIManager.put("TextField.font", theFont);
		FormUtilities.setFormFont(this, theFont);
	}

	public void showError(final String header, final String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.ERROR_MESSAGE);
	}

	public void showMessage(final String header, final String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean confirm(final String message) {
		return (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}

	public String getInput(final String header, final String message) {
		return JOptionPane.showInputDialog(this, message, header,
				JOptionPane.QUESTION_MESSAGE);
	}

	public void setConfigurationFileName(final String fileName) {
		if (theFormControl != null) {
			theFormControl.setExtra("ConfigurationDirectory", fileName);
		}
	}

	public Component getComponent() {
		return this;
	}

	public void editLookupValue(final MetaDataListValues value) {
		maintainMetaData.editLookupValue(value);
	}

	public void editCmsMappingValue(final String value) {
		maintainMetaData.editCmsMappingValue(value);
	}

	public void checkButtons() {
		maintainMetaData.checkButtons();
		maintainUserGroups.checkButtons();
	}
        
        
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		tabMain = new javax.swing.JTabbedPane();
		pnlApplicationData = new javax.swing.JPanel();
		pnlUsers = new javax.swing.JPanel();
		pnlUserGroupData = new javax.swing.JPanel();
		pnlMetaData = new javax.swing.JPanel();
		pnlSipStatusData = new javax.swing.JPanel();

		setTitle("Indigo MetaData Configurator");
		setDefaultCloseOperation(3);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}

			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		javax.swing.GroupLayout pnlApplicationDataLayout = new javax.swing.GroupLayout(
				pnlApplicationData);
		pnlApplicationData.setLayout(pnlApplicationDataLayout);
		pnlApplicationDataLayout.setHorizontalGroup(pnlApplicationDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 884, Short.MAX_VALUE));
		pnlApplicationDataLayout.setVerticalGroup(pnlApplicationDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 429, Short.MAX_VALUE));

		tabMain.addTab("Maintain Application Data", pnlApplicationData);

		javax.swing.GroupLayout pnlUsersLayout = new javax.swing.GroupLayout(
				pnlUsers);
		pnlUsers.setLayout(pnlUsersLayout);
		pnlUsersLayout.setHorizontalGroup(pnlUsersLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 884,
				Short.MAX_VALUE));
		pnlUsersLayout.setVerticalGroup(pnlUsersLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 429,
				Short.MAX_VALUE));

		tabMain.addTab("Maintain Users", pnlUsers);

		javax.swing.GroupLayout pnlUserGroupDataLayout = new javax.swing.GroupLayout(
				pnlUserGroupData);
		pnlUserGroupData.setLayout(pnlUserGroupDataLayout);
		pnlUserGroupDataLayout.setHorizontalGroup(pnlUserGroupDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 884, Short.MAX_VALUE));
		pnlUserGroupDataLayout.setVerticalGroup(pnlUserGroupDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 429, Short.MAX_VALUE));

		tabMain.addTab("User Group Data", pnlUserGroupData);

		javax.swing.GroupLayout pnlMetaDataLayout = new javax.swing.GroupLayout(
				pnlMetaData);
		pnlMetaData.setLayout(pnlMetaDataLayout);
		pnlMetaDataLayout.setHorizontalGroup(pnlMetaDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 884, Short.MAX_VALUE));
		pnlMetaDataLayout.setVerticalGroup(pnlMetaDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 429, Short.MAX_VALUE));

		tabMain.addTab("Maintain Meta Data", pnlMetaData);

		javax.swing.GroupLayout pnlSipStatusDataLayout = new javax.swing.GroupLayout(
				pnlSipStatusData);
		pnlSipStatusData.setLayout(pnlSipStatusDataLayout);
		pnlSipStatusDataLayout.setHorizontalGroup(pnlSipStatusDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 884, Short.MAX_VALUE));
		pnlSipStatusDataLayout.setVerticalGroup(pnlSipStatusDataLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 429, Short.MAX_VALUE));

		tabMain.addTab("Maintain SIP status definitions", pnlSipStatusData);
		
		
		// Added by Ben - 6.12.2013
		// Surround main content in a scroll pane - to handle display on smaller screen resolutions.
		tabMain.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		javax.swing.JScrollPane scrollFrame = new JScrollPane(tabMain);
		tabMain.setAutoscrolls(true);
		scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
						scrollFrame, javax.swing.GroupLayout.DEFAULT_SIZE, 889,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
						scrollFrame, javax.swing.GroupLayout.DEFAULT_SIZE, 454,
				Short.MAX_VALUE));

		pack();
    }// </editor-fold>//GEN-END:initComponents

	private void formWindowClosing(final java.awt.event.WindowEvent evt) {
		if (metaPresenter.getIsDirty()
				&& (confirm("Data has changed, do you want to save?"))) {
			metaPresenter.saveConfigurationFile();
		}
	}

	private void formWindowOpened(java.awt.event.WindowEvent evt) {
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlApplicationData;
    private javax.swing.JPanel pnlMetaData;
    private javax.swing.JPanel pnlSipStatusData;
    private javax.swing.JPanel pnlUserGroupData;
    private javax.swing.JPanel pnlUsers;
    private javax.swing.JTabbedPane tabMain;
}