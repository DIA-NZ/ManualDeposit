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
 * or the file "LICENSE.text" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.manualdeposit;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nz.govt.natlib.ndha.common.guiutilities.FormControl;

public class StructMapFileDescManagement extends javax.swing.JDialog implements
		IStructMapFileDescManagement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -42884991551516585L;
	private final static Log LOG = LogFactory
			.getLog(StructMapFileDescManagement.class);
	private final String theSettingsPath;
	@SuppressWarnings("unused")
	private FormControl formControl;

	/** Creates new form StructMapFileDescManagement */
	public StructMapFileDescManagement(java.awt.Frame parent, boolean modal,
			String settingsPath, String xmlFileName) {
		super(parent, modal);
		theSettingsPath = settingsPath;
		initComponents();
		initPresenter(xmlFileName);
	}

	private void initPresenter(String xmlFileName) {
		thePresenter = StructMapFileDescMgmtPresenter.create(lstDescription,
				textfldDescription, textfldFilePrefix, checkAllowMultiples,
				checkMandatory, cmbPosition, textfldDescriptionMain, checkExtraLayers, 
				textfldDescriptionL2, textfldFilePrefixL2, checkAllowMultiplesL2, textfldDescriptionL3, 
				textfldFilePrefixL3, checkAllowMultiplesL3, textfldDescriptionL4, textfldFilePrefixL4, 
				checkAllowMultiplesL4, btnAddNew, btnDelete, btnSave,
				btnCancel, btnClose, btnMoveUp, btnMoveDown, btnGenMainDesc_1, xmlFileName, this);
	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		scrlDescription = new javax.swing.JScrollPane();
		lstDescription = new javax.swing.JList();
		pnlDetails = new javax.swing.JPanel();
		btnMoveUp = new javax.swing.JButton();
		btnMoveDown = new javax.swing.JButton();
		btnAddNew = new javax.swing.JButton();
		btnDelete = new javax.swing.JButton();
		btnSave = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();
		btnClose = new javax.swing.JButton();
		btnGenMainDesc = new javax.swing.JButton();

		setTitle("Manage Structure Map File Descriptions");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				formWindowOpened(evt);
			}

			public void windowClosing(java.awt.event.WindowEvent evt) {
				dialogWindowClosing(evt);
			}
		});

		scrlDescription.setViewportView(lstDescription);
		
		JPanel panelLayer1 = new JPanel();
		panelLayer1.setAlignmentY(Component.TOP_ALIGNMENT);
		
		panel_ExtraLayers = new JPanel();
		panel_ExtraLayers.setAlignmentY(Component.TOP_ALIGNMENT);
		
		panel_ExtraLayers.setMinimumSize(new Dimension(300, 0));
		panel_ExtraLayers.setPreferredSize(new Dimension(300, 500));
		panel_ExtraLayers.setMaximumSize(new Dimension(300, 500));
		panel_ExtraLayers.setVisible(false);		
		
		JLabel lblDescriptionL2 = new JLabel("Layer 2");
		lblDescriptionL2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblDescription_1 = new JLabel("Description");
		
		textfldDescriptionL2 = new JTextField();
		textfldDescriptionL2.setName("DescriptionL2");
		textfldDescriptionL2.setColumns(10);
		
		JLabel lblFilePrefixL2 = new JLabel("File Prefix/Suffix");
		
		textfldFilePrefixL2 = new JTextField();
		textfldFilePrefixL2.setName("FilePrefixL2");
		textfldFilePrefixL2.setColumns(10);
		
		JLabel lblAllowMultiplesL2 = new JLabel("Allow Multiples?");
		
		checkAllowMultiplesL2 = new JCheckBox("");
		checkAllowMultiplesL2.setName("AllowMultiplesL2");
		
		JLabel lblDescriptionL3 = new JLabel("Layer 3");
		lblDescriptionL3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblDescription_2 = new JLabel("Description");
		
		textfldDescriptionL3 = new JTextField();
		textfldDescriptionL3.setName("DescriptionL3");
		textfldDescriptionL3.setColumns(10);
		
		JLabel lblFilePrefixL3 = new JLabel("File Prefix/Suffix");
		
		textfldFilePrefixL3 = new JTextField();
		textfldFilePrefixL3.setName("FilePrefixL3");
		textfldFilePrefixL3.setColumns(10);
		
		JLabel lblAllowMultiplesL3 = new JLabel("Allow Multiples?");
		
		checkAllowMultiplesL3 = new JCheckBox("");
		checkAllowMultiplesL3.setName("AllowMultiplesL3");
		
		JLabel lblDescriptionL4 = new JLabel("Layer 4");
		lblDescriptionL4.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblDescription_3 = new JLabel("Description");
		
		textfldDescriptionL4 = new JTextField();
		textfldDescriptionL4.setName("DescriptionL4");
		textfldDescriptionL4.setColumns(10);
		
		lblFilePrefixL4 = new JLabel("File Prefix/Suffix");
		
		textfldFilePrefixL4 = new JTextField();
		textfldFilePrefixL4.setName("FilePrefixL4");
		textfldFilePrefixL4.setColumns(10);
		
		lblAllowMultiplesL4 = new JLabel("Allow Multiples?");
		
		checkAllowMultiplesL4 = new JCheckBox("");
		checkAllowMultiplesL4.setName("AllowMultiplesL4");
		
		GroupLayout gl_panel_ExtraLayers = new GroupLayout(panel_ExtraLayers);
		gl_panel_ExtraLayers.setHorizontalGroup(
			gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDescriptionL2)
						.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
							.addComponent(lblDescription_1)
							.addGap(44)
							.addComponent(textfldDescriptionL2, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
						.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
							.addComponent(lblFilePrefixL2)
							.addGap(18)
							.addComponent(textfldFilePrefixL2, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
							.addComponent(lblAllowMultiplesL2)
							.addGap(18)
							.addComponent(checkAllowMultiplesL2))
						.addComponent(lblDescriptionL3)
						.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
							.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
								.addComponent(lblDescription_2)
								.addComponent(lblFilePrefixL3)
								.addComponent(lblAllowMultiplesL3))
							.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
									.addGap(21)
									.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
										.addComponent(textfldFilePrefixL3, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
										.addComponent(textfldDescriptionL3, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
								.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
									.addGap(18)
									.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
										.addComponent(textfldDescriptionL4, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
										.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
											.addComponent(checkAllowMultiplesL3)
											.addPreferredGap(ComponentPlacement.RELATED, 172, Short.MAX_VALUE))))))
						.addComponent(lblDescriptionL4)
						.addComponent(lblDescription_3)
						.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
							.addComponent(lblFilePrefixL4)
							.addGap(18)
							.addComponent(textfldFilePrefixL4, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
							.addComponent(lblAllowMultiplesL4)
							.addGap(18)
							.addComponent(checkAllowMultiplesL4)))
					.addContainerGap())
		);
		gl_panel_ExtraLayers.setVerticalGroup(
			gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_ExtraLayers.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblDescriptionL2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDescription_1)
						.addComponent(textfldDescriptionL2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFilePrefixL2)
						.addComponent(textfldFilePrefixL2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAllowMultiplesL2)
						.addComponent(checkAllowMultiplesL2))
					.addGap(18)
					.addComponent(lblDescriptionL3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDescription_2)
						.addComponent(textfldDescriptionL3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFilePrefixL3)
						.addComponent(textfldFilePrefixL3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAllowMultiplesL3)
						.addComponent(checkAllowMultiplesL3))
					.addGap(18)
					.addComponent(lblDescriptionL4)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDescription_3)
						.addComponent(textfldDescriptionL4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFilePrefixL4)
						.addComponent(textfldFilePrefixL4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_ExtraLayers.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAllowMultiplesL4)
						.addComponent(checkAllowMultiplesL4))
					.addContainerGap(39, Short.MAX_VALUE))
		);
		panel_ExtraLayers.setLayout(gl_panel_ExtraLayers);
		
		lblDescription = new JLabel();
		lblDescription.setText("Description");
		
		textfldDescription = new JTextField();
		textfldDescription.setName("Description");
		
		textfldFilePrefix = new JTextField();
		textfldFilePrefix.setName("FilePrefix");
		
		lblFilePrefix = new JLabel();
		lblFilePrefix.setText("File Prefix/Suffix");
		
		lblPosition = new JLabel();
		lblPosition.setText("Position");
		
		cmbPosition = new JComboBox();
		cmbPosition.setName("Position");
		
		lblMandatory = new JLabel();
		lblMandatory.setText("Mandatory?");
		
		checkMandatory = new JCheckBox();
		checkMandatory.setName("Mandatory");
		
		lblAllowMultiples = new JLabel();
		lblAllowMultiples.setText("Allow Multiples?");
		
		checkAllowMultiples = new JCheckBox();
		checkAllowMultiples.setName("AllowMultiples");
		
		lblExtraLayers = new JLabel("Extra Layers?");
		
		checkExtraLayers = new JCheckBox("");
		checkExtraLayers.setName("ExtraLayers");
		checkExtraLayers.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				JCheckBox button = (JCheckBox) e.getSource();
			    if (button.isSelected()) {
			    	panel_MainDesc.setVisible(true);
			    	panel_ExtraLayers.setVisible(true);
			    	pnlDetails.revalidate();
			    	pnlDetails.repaint();
			    } else {
			    	panel_MainDesc.setVisible(false);
			    	panel_ExtraLayers.setVisible(false);
			    	pnlDetails.revalidate();
			    	pnlDetails.repaint();
			    }
			}
		});
		
		GroupLayout gl_panelLayer1 = new GroupLayout(panelLayer1);
		gl_panelLayer1.setHorizontalGroup(
			gl_panelLayer1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelLayer1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelLayer1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelLayer1.createSequentialGroup()
							.addGroup(gl_panelLayer1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblFilePrefix)
								.addComponent(lblDescription)
								.addComponent(lblPosition))
							.addGap(18)
							.addGroup(gl_panelLayer1.createParallelGroup(Alignment.LEADING)
								.addComponent(textfldFilePrefix, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
								.addComponent(textfldDescription, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
								.addComponent(cmbPosition, 0, 169, Short.MAX_VALUE)))
						.addGroup(gl_panelLayer1.createSequentialGroup()
							.addGroup(gl_panelLayer1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblAllowMultiples)
								.addComponent(lblExtraLayers)
								.addComponent(lblMandatory))
							.addGap(18)
							.addGroup(gl_panelLayer1.createParallelGroup(Alignment.LEADING)
								.addComponent(checkExtraLayers)
								.addComponent(checkMandatory)
								.addComponent(checkAllowMultiples))))
					.addContainerGap())
		);
		gl_panelLayer1.setVerticalGroup(
			gl_panelLayer1.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panelLayer1.createSequentialGroup()
					.addGap(24)
					.addGroup(gl_panelLayer1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDescription)
						.addComponent(textfldDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelLayer1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFilePrefix)
						.addComponent(textfldFilePrefix, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelLayer1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPosition)
						.addComponent(cmbPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addGroup(gl_panelLayer1.createParallelGroup(Alignment.LEADING)
						.addComponent(checkMandatory)
						.addComponent(lblMandatory))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelLayer1.createParallelGroup(Alignment.LEADING)
						.addComponent(checkAllowMultiples)
						.addComponent(lblAllowMultiples))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelLayer1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblExtraLayers)
						.addComponent(checkExtraLayers))
					.addContainerGap(26, Short.MAX_VALUE))
		);
		panelLayer1.setLayout(gl_panelLayer1);

		btnMoveUp.setPreferredSize(new java.awt.Dimension(91, 23));
		btnMoveUp.setText("Move Up");

		btnMoveDown.setText("Move Down");

		btnAddNew.setText("Add New");

		btnDelete.setText("Delete");

		btnSave.setText("Save");

		btnCancel.setText("Cancel");

		btnClose.setText("Close");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(scrlDescription, GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
							.addGap(18)
							.addGroup(layout.createParallelGroup(Alignment.TRAILING)
								.addGroup(layout.createSequentialGroup()
									.addComponent(btnAddNew)
									.addGap(18)
									.addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
									.addGap(19))
								.addComponent(pnlDetails, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(layout.createSequentialGroup()
							.addComponent(btnMoveUp, GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnMoveDown, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
							.addGap(89)
							.addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnClose, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)))
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(pnlDetails, GroupLayout.PREFERRED_SIZE, 597, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnDelete)
								.addComponent(btnAddNew)))
						.addComponent(scrlDescription, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
					.addGap(18)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnMoveUp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnMoveDown))
						.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnClose)
							.addComponent(btnCancel)
							.addComponent(btnSave)))
					.addContainerGap())
		);
		pnlDetails.setLayout(new BoxLayout(pnlDetails, BoxLayout.Y_AXIS));
		
		panel_MainDesc = new JPanel();
		
		panel_MainDesc.setMinimumSize(new Dimension(300, 0));
		panel_MainDesc.setPreferredSize(new Dimension(300, 60));
		panel_MainDesc.setMaximumSize(new Dimension(300, 60));
		panel_MainDesc.setVisible(false);
		
		
		lblDescriptionMain = new JLabel("Main Description");
		
		textfldDescriptionMain = new JTextField();
		textfldDescriptionMain.setName("DescriptionMain");
		textfldDescriptionMain.setColumns(10);
		
		btnGenMainDesc_1 = new JButton("");
		btnGenMainDesc_1.setToolTipText("Generate a main description by concatenating layer descriptions.");
		btnGenMainDesc_1.setContentAreaFilled(false);
		btnGenMainDesc_1.setIcon(new ImageIcon(StructMapFileDescManagement.class.getResource("/org/jdesktop/swingx/plaf/basic/resources/month-down.png")));
		GroupLayout gl_panel_MainDesc = new GroupLayout(panel_MainDesc);
		gl_panel_MainDesc.setHorizontalGroup(
			gl_panel_MainDesc.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_MainDesc.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblDescriptionMain)
					.addGap(18)
					.addComponent(textfldDescriptionMain, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnGenMainDesc_1, GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_MainDesc.setVerticalGroup(
			gl_panel_MainDesc.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_MainDesc.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_MainDesc.createParallelGroup(Alignment.LEADING)
						.addComponent(btnGenMainDesc_1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_MainDesc.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblDescriptionMain)
							.addComponent(textfldDescriptionMain, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_MainDesc.setLayout(gl_panel_MainDesc);
		pnlDetails.add(panel_MainDesc);
		pnlDetails.add(panelLayer1);
		pnlDetails.add(panel_ExtraLayers);
		getContentPane().setLayout(layout);

		pack();
	}// </editor-fold>

	// GEN-END:initComponents

	private void formWindowOpened(java.awt.event.WindowEvent evt) {
		try {
			formControl = new FormControl(this, theSettingsPath);
		} catch (Exception ex) {
			LOG.error("Error loading form parameters", ex);
		}
	}

	private void dialogWindowClosing(java.awt.event.WindowEvent evt) {
		thePresenter.saveIfRequired();
	}

	public boolean confirm(String message) {
		if (JOptionPane.showConfirmDialog(this, message, "Please Confirm",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	public void showMessage(String header, String message) {
		JOptionPane.showMessageDialog(this, message, header,
				JOptionPane.INFORMATION_MESSAGE);
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton btnAddNew;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnClose;
	private javax.swing.JButton btnDelete;
	private javax.swing.JButton btnMoveDown;
	private javax.swing.JButton btnMoveUp;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnGenMainDesc;
	private JButton btnGenMainDesc_1;
	private javax.swing.JList lstDescription;
	private javax.swing.JPanel pnlDetails;
	private javax.swing.JScrollPane scrlDescription;
	// End of variables declaration//GEN-END:variables

	private StructMapFileDescMgmtPresenter thePresenter;
	private JLabel lblDescription;
	private JTextField textfldDescription;
	private JTextField textfldFilePrefix;
	private JLabel lblFilePrefix;
	private JLabel lblPosition;
	private JComboBox cmbPosition;
	private JLabel lblMandatory;
	private JCheckBox checkMandatory;
	private JLabel lblAllowMultiples;
	private JCheckBox checkAllowMultiples;
	private JPanel panel_MainDesc;
	private JPanel panel_ExtraLayers;
	private JLabel lblExtraLayers;
	private JCheckBox checkExtraLayers;
	private JLabel lblDescriptionMain;
	private JTextField textfldDescriptionMain;
	private JTextField textfldDescriptionL2;
	private JTextField textfldFilePrefixL2;
	private JTextField textfldDescriptionL3;
	private JTextField textfldFilePrefixL3;
	private JTextField textfldDescriptionL4;
	private JLabel lblFilePrefixL4;
	private JTextField textfldFilePrefixL4;
	private JLabel lblAllowMultiplesL4;
	private JCheckBox checkAllowMultiplesL4;
	private JCheckBox checkAllowMultiplesL3;
	private JCheckBox checkAllowMultiplesL2;

	@Override
	public void closeForm() {
		this.setVisible(false);
	}

	@Override
	public void setFormFont(Font theFont) {
		FormUtilities.setFormFont(this, theFont);
	}

	@Override
	public void showView() {
		setVisible(true);
	}
}