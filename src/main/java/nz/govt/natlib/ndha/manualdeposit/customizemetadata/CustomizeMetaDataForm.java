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

package nz.govt.natlib.ndha.manualdeposit.customizemetadata;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;
import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.manualdeposit.FormUtilities;

public final class CustomizeMetaDataForm extends javax.swing.JDialog implements
	ICustomizeMetaDataEditorView {

	private static final long serialVersionUID = -6992920177120441672L;
	private final static Log LOG = LogFactory.getLog(CustomizeMetaDataForm.class);
	// Can't replace with a local variable as it wouldn't work then
	@SuppressWarnings("unused")
	private FormControl theFormControl;
	private final String theSettingsPath;
	private CustomizeMetaDataPresenter metaPresenter;
	final private JPanel glass;

	public static CustomizeMetaDataForm create(final java.awt.Frame parent,
			final boolean modal, final String settingsPath) {
		return new CustomizeMetaDataForm(parent, modal, settingsPath);
	}

	/** Creates new form CustomizeMetaData */
	public CustomizeMetaDataForm(final java.awt.Frame parent, final boolean modal,
			final String settingsPath) {
		super(parent, modal);
		initComponents();
		theSettingsPath = settingsPath;
		glass = (JPanel) this.getGlassPane();
		//setCanClose(true);
	}

	public void showView() {
		setVisible(true);
	}
	
	public void setFormFont(Font theFont) {
		FormUtilities.setFormFont(this, theFont);
	}

	public void setCanClose(final boolean canClose) {
		if (canClose) {
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
	}

	public void showGlassPane(final boolean show) {
		glass.setVisible(show);
	}

	public void setPresenter(final CustomizeMetaDataPresenter presenter) {
		metaPresenter = presenter;
		checkButtons();
	}

	public void checkButtons() {
		btnClose.setEnabled(true);
		btnSaveAndClose.setEnabled(true);
	}

	public void showError(final String header, final String message) {
		showError(header, message, null);
	}

	public void showError(final String header, final String message,
			final Exception ex) {
		final StringWriter prompt = new StringWriter();
		if (message != null) {
			prompt.append(message);
			prompt.append("\n");
		}
		if (ex != null) {
			LOG.error(message, ex);
			if (ex.getMessage() != null) {
				prompt.append(ex.getMessage());
				prompt.append("\n");
			}
			final StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			prompt.append(writer.toString());
		}
		JOptionPane.showMessageDialog(this, prompt.toString(), header,
				JOptionPane.ERROR_MESSAGE);
	}

	public void showMessage(final String header, final String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean confirm(final String message) {
		return confirm(message, true);
	}

	public boolean confirm(final String message, final boolean useYesNo) {
		int optionType;
		if (useYesNo) {
			optionType = JOptionPane.YES_NO_OPTION;
		} else {
			optionType = JOptionPane.OK_CANCEL_OPTION;
		}
		return (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				optionType) == JOptionPane.YES_OPTION);
	}

	public void setWaitCursor(final boolean isWaiting) {
		glass.setVisible(isWaiting);
		if (isWaiting) {
			final Cursor hourglass = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglass);
		} else {
			final Cursor normal = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normal);
		}
	}
	
	@Override
	public void setTableDataBlank(ArrayList<FileGroupCollection> entities, List<String> metaDataList) {
		if (!metaDataList.isEmpty()){
			Object[][] entityData = new Object[entities.size()][metaDataList.size()];
			String[] columnNames = new String[metaDataList.size()];
			metaDataList.toArray(columnNames);
			
			if (entities.size() > 0 && metaDataList.size() > 1) {
				for (int i = 0; i < entities.size(); i++) {
					FileGroupCollection entity = entities.get(i);
					entityData[i][0] = entity.getEntityName();
					for (int j = 1; j < metaDataList.size(); j++){
						entityData[i][j] = "";						
					}
				}
			}
			tblMetaDataList.setModel(new CustomizeMetaDataTableModel(entityData, columnNames));
		}
	}
	
	@Override
	public void setTableData(ArrayList<FileGroupCollection> entities, List<String> metaDataList, CustomizeMetaDataTableModel allMetaDataValues) {
		if (!metaDataList.isEmpty()){
			Object[][] entityData = new Object[entities.size()][metaDataList.size()];
			String[] columnNames = new String[metaDataList.size()];
			metaDataList.toArray(columnNames);
			Map<String, String> entityMetaDataValues;
			
			if (entities.size() > 0 && metaDataList.size() > 1) {
				for (int i = 0; i < entities.size(); i++) {
					FileGroupCollection entity = entities.get(i);
					entityData[i][0] = entity.getEntityName();
					entityMetaDataValues = allMetaDataValues.getCustomMetaDataForEntity(entity.getEntityName());
					for (int j = 1; j < metaDataList.size(); j++){
						if(entityMetaDataValues.get(metaDataList.get(j)) != null){
							entityData[i][j] = entityMetaDataValues.get(metaDataList.get(j));
						}
						else{
							entityData[i][j] = "";
						}
						
					}
				}
			}
			tblMetaDataList.setModel(new CustomizeMetaDataTableModel(entityData, columnNames));
		}
	}
	
	public Map<String, Object> getTableData() {
		
		CustomizeMetaDataTableModel tableModel = (CustomizeMetaDataTableModel) tblMetaDataList.getModel();
		final Map<String, Object> tableData = new HashMap<String, Object>();
		int colLength = tableModel.getColumnCount();
		int rowLength = tableModel.getRowCount();
				
		for (int i = 0; i < rowLength; i++){
			final Map<String, Object> colData = new HashMap<String, Object>();
			String entityName = tableModel.getValueAt(i, 0).toString();
			for (int j = 1; j < colLength; j++){
				String colName = tableModel.getColumnName(j);
				Object value = tableModel.getValueAt(i, j);
				colData.put(colName, value);
			}
			tableData.put(entityName, colData);
		}
		return tableData;
	}
	
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlButtons = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSaveAndClose = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        scrlMetaDataList = new javax.swing.JScrollPane();
        tblMetaDataList = new javax.swing.JTable();

        setTitle("MetaData Customization");
        setName("CustomizeMetaDataForm"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnSaveAndClose.setText("Save and Close");
        btnSaveAndClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAndCloseActionPerformed(evt);
            }
        });
        
        btnOpen.setText("Open");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        
        tblMetaDataList.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseReleased(MouseEvent e) {
        		if(tblMetaDataList.getSelectedColumnCount() == 0 || tblMetaDataList.getSelectedColumnCount() > 1){
        			btnOpen.setEnabled(false);
        		}
        		else if(tblMetaDataList.getSelectedRowCount() > 1){
        			btnOpen.setEnabled(false);
        		}
        		else if(tblMetaDataList.getSelectedColumn() == 0){
        			btnOpen.setEnabled(true);
        			if(e.getClickCount() == 2){
        				btnOpenActionPerformed(null);
        			}
        		}
        		else{
        			btnOpen.setEnabled(false);
        		}
        	}
        });

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtonsLayout.setHorizontalGroup(
        	pnlButtonsLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(pnlButtonsLayout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(btnClose)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(btnSaveAndClose)
        			.addGap(48)
        			.addComponent(btnOpen)
        			.addContainerGap(560, Short.MAX_VALUE))
        );
        pnlButtonsLayout.setVerticalGroup(
        	pnlButtonsLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(pnlButtonsLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(pnlButtonsLayout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnClose)
        				.addComponent(btnSaveAndClose)
        				.addComponent(btnOpen))
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlButtons.setLayout(pnlButtonsLayout);

        tblMetaDataList.setModel(new javax.swing.table.DefaultTableModel(
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
        scrlMetaDataList.setViewportView(tblMetaDataList);
        tblMetaDataList.setCellSelectionEnabled(true);
        // Add Copy/Paste/Delete functionality to the Customize Meta Data table.
        CustomizeExcelAdapter copyPastFunct = new CustomizeExcelAdapter(tblMetaDataList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrlMetaDataList, javax.swing.GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrlMetaDataList, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void btnCloseActionPerformed(final java.awt.event.ActionEvent evt) {
		try {
			if(tblMetaDataList.isEditing()){
				tblMetaDataList.getCellEditor().stopCellEditing();
			}
			metaPresenter.closeForm();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void formWindowClosing(final java.awt.event.WindowEvent evt) {
		try {
			if(tblMetaDataList.isEditing()){
				tblMetaDataList.getCellEditor().stopCellEditing();
			}
			metaPresenter.closeForm();
		} catch (Exception ex) {
		}
	}

	private void btnSaveAndCloseActionPerformed(final java.awt.event.ActionEvent evt) {
		if(tblMetaDataList.isEditing()){
			tblMetaDataList.getCellEditor().stopCellEditing();
		}		
		metaPresenter.saveMetaData();
		checkButtons();
	}
	
	
	private void btnOpenActionPerformed(final java.awt.event.ActionEvent evt) {
		System.out.println("cell selected is in column: " + tblMetaDataList.getSelectedColumn());
		if(tblMetaDataList.isEditing()){
			tblMetaDataList.getCellEditor().stopCellEditing();
		}		
		int selectedRow = tblMetaDataList.getSelectedRow();
		int selectedColumn = tblMetaDataList.getSelectedColumn();
		CustomizeMetaDataTableModel tableModel = (CustomizeMetaDataTableModel) tblMetaDataList.getModel();
		metaPresenter.openFile((String)tableModel.getValueAt(selectedRow, selectedColumn));
		checkButtons();
	}

	private void formWindowOpened(final java.awt.event.WindowEvent evt) {
		try {
			theFormControl = FormControl.create(this, theSettingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSaveAndClose;
    private javax.swing.JButton btnOpen;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JScrollPane scrlMetaDataList;
    private javax.swing.JTable tblMetaDataList;
}