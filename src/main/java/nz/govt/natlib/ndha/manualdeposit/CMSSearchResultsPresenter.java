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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nz.govt.natlib.ndha.common.exceptions.IlsException;
import nz.govt.natlib.ndha.common.ilsquery.CmsRecord;
import nz.govt.natlib.ndha.common.ilsquery.CmsResults;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType;
import nz.govt.natlib.ndha.common.ilsquery.ILSSearchFacade;
import nz.govt.natlib.ndha.common.ilsquery.IlsSearchFacadeImpl;
import nz.govt.natlib.ndha.common.ilsquery.criteria.CompositeCriteria;
import nz.govt.natlib.ndha.common.ilsquery.criteria.Operator;
import nz.govt.natlib.ndha.common.ilsquery.criteria.SingleCriteria;
import nz.govt.natlib.ndha.manualdeposit.customui.SearchAttributeDetail;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CMSSearchResultsPresenter {

	private final ICMSSearchResults searchFrame;
	private final ManualDepositPresenter manualDepositParent;
	private JTable theResultsTable;
	private ResultsTableModel theResultsTableModel;
	private static final String[] CMS_2_RESULTS_COLUMN_NAMES = { "Ref No",
			"Title", "Deposit Type", "Description" };
	private static final String[] CMS_1_RESULTS_COLUMN_NAMES = { "Title",
			"Publisher", "Author" };
	private final List<SearchAttributeDetail> searchAttributes;
	private final AppProperties applicationProperties;
	private CmsResults cmsResults;
	private final ILSQueryType.eServerType theSearchType;
	private int theStartRecord = 1;
	private static final int RECORDS_PER_PAGE = 10;
	private static final int SEARCH_TIMEOUT = 5000;
	private final static Log LOG = LogFactory.getLog(CMSSearchResultsPresenter.class);
	private String searchSchema = "dps";

	public CMSSearchResultsPresenter(final ICMSSearchResults frame,
			final List<SearchAttributeDetail> attributes,
			final ManualDepositPresenter parent,
			final AppProperties appProperties,
			final ILSQueryType.eServerType searchType) {
		searchFrame = frame;
		manualDepositParent = parent;
		searchAttributes = attributes;
		applicationProperties = appProperties;
		theSearchType = searchType;
	}

	public void setupPresenter() {
		searchFrame.showView();
	}

	public void addResultsTableModelAndHandlers(final JTable theTable) {
		theResultsTable = theTable;
		if (theSearchType.equals(ILSQueryType.eServerType.CMS2)) {
			theResultsTableModel = new ResultsTableModel(
					CMS_2_RESULTS_COLUMN_NAMES, theSearchType);
		} else {
			theResultsTableModel = new ResultsTableModel(
					CMS_1_RESULTS_COLUMN_NAMES, theSearchType);
		}
		theResultsTable.setModel(theResultsTableModel);
		theResultsTable.setSurrendersFocusOnKeystroke(true);
		theResultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		theResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		theResultsTable.setColumnSelectionAllowed(false);
		theResultsTable.setRowSelectionAllowed(true);
		final ListSelectionModel theListSelectionModel = theResultsTable
				.getSelectionModel();
		theListSelectionModel
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(final ListSelectionEvent e) {
						tableRowSelected(e);
					}
				});
		theResultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(final java.awt.event.MouseEvent evt) {
				tableMouseClicked(evt);
			}
		});
	}

	private boolean loadSearchResults() {
		searchFrame.setWaitCursor(true);
		if (searchAttributes == null || theResultsTableModel == null) {
			return false;
		}
		final ILSSearchFacade search;
		try {
			search = new IlsSearchFacadeImpl(applicationProperties
					.getApplicationData().getSearchStrategyClass());
		} catch (IlsException ex) {
			LOG.error("ILS Exception occurred", ex);
			return false;
		}
		search.setCMS2ServerUrl(applicationProperties.getApplicationData()
				.getCMS2SearchUrl());
		search.setCMS1ServerUrl(applicationProperties.getApplicationData()
				.getCMS1SearchUrl());
		final SingleCriteria criteria = new SingleCriteria(searchAttributes
				.get(0).getAttribute(), searchAttributes.get(0)
				.getAttributeName(), charEncode(searchAttributes.get(0)
				.getValue()));
		
		if (theSearchType.equals(ILSQueryType.eServerType.CMS1)) {
			searchSchema = "dc";
		}else{
			searchSchema = "dps";
		}
		
		try {
			if (searchAttributes.size() == 1) {
				cmsResults = search.runQuery(criteria, theSearchType,
						theStartRecord, RECORDS_PER_PAGE, SEARCH_TIMEOUT, searchSchema, applicationProperties.getSysProxyUser(), applicationProperties.getSysProxyPassword());
			} else {
				SingleCriteria criteria2 = new SingleCriteria(searchAttributes
						.get(1).getAttribute(), searchAttributes.get(1)
						.getAttributeName(), searchAttributes.get(1).getValue());
				CompositeCriteria compositeCriteria = new CompositeCriteria(
						Operator.and, criteria, criteria2);
				for (int i = 2; i < searchAttributes.size(); i++) {
					criteria2 = new SingleCriteria(searchAttributes.get(i)
							.getAttribute(), searchAttributes.get(i)
							.getAttributeName(), searchAttributes.get(i)
							.getValue());
					compositeCriteria = new CompositeCriteria(Operator.and,
							compositeCriteria, criteria2);
				}
				cmsResults = search.runQuery(compositeCriteria, theSearchType,
						theStartRecord, RECORDS_PER_PAGE, SEARCH_TIMEOUT, searchSchema, applicationProperties.getSysProxyUser(), applicationProperties.getSysProxyPassword());
			}
		} catch (Exception ex) {
			if (ex.getCause() instanceof java.net.SocketTimeoutException) {
				final StringBuilder message = new StringBuilder();
				message.append("Timeout occurred when running the search");
				message.append("\nProbably due to an overly broad search");
				message
						.append("\nPlease try refining your criteria and running the search again");
				searchFrame.showMessage("Timed out when running the search",
						message.toString());
			} else {
				searchFrame.showMessage("An error occurred", ex.getMessage());
			}
			searchFrame.closeForm();
			return false;
		}
		if (cmsResults == null || cmsResults.getRecordCount() == 0) {
			final String message = "No records were found that match your criteria\nPlease change your criteria and try again";
			searchFrame.showMessage("No Results Found", message);
			searchFrame.closeForm();
		} else {
			theResultsTableModel.clearTable();
			for (int i = 0; i < cmsResults.getRecordCount(); i++) {
				theResultsTableModel.addRow(cmsResults.getResults().get(i));
			}
			checkPaging();
			searchFrame.setWaitCursor(false);
		}
		return true;
	}

	/**
	 * This method encodes certain query values so that SRU accepts them as
	 * valid. These are the encodings covered so far:
	 * 
	 * "/" => "\/" (Example: Search by creator "55/3 Polish Workers" needs to be
	 * modified to "55\/3 Polish Workers")
	 * 
	 * @param queryString
	 * @return
	 */
	private String charEncode(final String queryString) {
		if (queryString == null)
			return null;
		if (queryString.contains("/"))
			return queryString.replace("/", "\\/");
		return queryString;
	}

	private void checkPaging() {
		boolean canPageForward = true;
		boolean canPageBackwards = true;
		if (theStartRecord < 1) {
			theStartRecord = 1;
		}
		if (theStartRecord + cmsResults.getRecordsPerPage() > cmsResults
				.getTotalNumberOfRecords()) {
			canPageForward = false;
		}
		if (theStartRecord - cmsResults.getRecordsPerPage() < 1) {
			canPageBackwards = false;
		}
		final int lastRecord = cmsResults.getStartRecord()
				+ cmsResults.getRecordCount() - 1;
		final StringBuilder pagingMessage = new StringBuilder();
		pagingMessage.append("Records ");
		pagingMessage.append(cmsResults.getStartRecord());
		pagingMessage.append(" to ");
		pagingMessage.append(lastRecord);
		pagingMessage.append(" of ");
		pagingMessage.append(cmsResults.getTotalNumberOfRecords());
		searchFrame.setStatus(theResultsTable.getSelectedRow() >= 0,
				canPageForward, canPageBackwards, pagingMessage.toString());
	}

	public void showResults() {
		if (loadSearchResults()) {
			checkPaging();
		}
	}

	public void pageForward() {
		theStartRecord = cmsResults.getStartRecord()
				+ cmsResults.getRecordsPerPage();
		showResults();
	}

	public void pageBackwards() {
		theStartRecord = cmsResults.getStartRecord()
				- cmsResults.getRecordsPerPage();
		showResults();
	}

	public void selectData() {
		final CmsRecord rec = theResultsTableModel.getRow(theResultsTable
				.getSelectedRow());
		if (rec != null) {
			searchFrame.showDetail(rec.toString());
		}
		checkPaging();
	}

	private void tableRowSelected(final ListSelectionEvent e) {
		selectData();
	}

	private void tableMouseClicked(final java.awt.event.MouseEvent evt) {
		selectData();
		if (evt.getClickCount() > 1) {
			selectRecord();
		}
	}

	public void selectRecord() {
		if (theResultsTable.getSelectedRow() >= 0) {
			final CmsRecord rec = theResultsTableModel.getRow(theResultsTable.getSelectedRow());
			MetaDataFields.ECMSSystem system;
			if (theSearchType.equals(ILSQueryType.eServerType.CMS2)) {
				system = MetaDataFields.ECMSSystem.CMS2;
			} else {
				if (manualDepositParent.isStaffMediated()){
					system = MetaDataFields.ECMSSystem.StaffMediated;
				} else if (manualDepositParent.isNoCMS()) {
					system = MetaDataFields.ECMSSystem.NoSystem;
				} else if (manualDepositParent.isCMS2()){
					system = MetaDataFields.ECMSSystem.CMS2;
				}else {
					system = MetaDataFields.ECMSSystem.CMS1;
				}
			}
			manualDepositParent.setCMSResults(rec, system);
			searchFrame.closeForm();
		}
	}

	public static class ResultsTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7047112194954893160L;
		public static final int COL_1 = 0;
		public static final int COL_2 = 1;
		public static final int COL_3 = 2;
		public static final int COL_4 = 3;
		public static final int COL_5 = 4;
		public static final int COL_6 = 5;

		protected String[] theColumnNames;
		protected List<CmsRecord> dataList;
		private final ILSQueryType.eServerType resultsSearchType;

		public ResultsTableModel(final String[] columnNames,
				final ILSQueryType.eServerType searchType) {
			super();
			resultsSearchType = searchType;
			theColumnNames = new String[columnNames.length];
			System.arraycopy(columnNames, 0, theColumnNames, 0,
					columnNames.length);
			dataList = new ArrayList<CmsRecord>();
		}

		public String getColumnName(final int column) {
			return theColumnNames[column];
		}

		public boolean isCellEditable(final int row, final int column) {
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(final int column) {
			return String.class;
		}

		public Object getValueAt(final int row, final int column) {
			final CmsRecord record = dataList.get(row);
			Object result;
			switch (column) {
			case COL_1:
				if (resultsSearchType.equals(ILSQueryType.eServerType.CMS2)) {
					result = record.getReference();
				} else {
					result = record.getTitleStatement();
				}
				break;
			case COL_2:
				if (resultsSearchType.equals(ILSQueryType.eServerType.CMS2)) {
					result = record.getTitleStatement();
				} else {
					result = record.getPublisher();
				}
				break;
			case COL_3:
				if (resultsSearchType.equals(ILSQueryType.eServerType.CMS2)) {
					result = record.getDepositType();
				} else {
					if ((record.getAuthorPersonal() == null)
							|| (record.getAuthorPersonal().equals(""))) {
						result = record.getAuthorCorporate();
					} else {
						result = record.getAuthorPersonal();
					}
				}
				break;
			case COL_4:
				result = record.getDescription();
				break;
			default:
				result = new Object();
			}
			return result;
		}

		public void setValueAt(final Object value, final int row,
				final int column) {
			// Non-editable - do nothing
		}

		public int getRowCount() {
			return dataList.size();
		}

		public int getColumnCount() {
			return theColumnNames.length;
		}

		public void addRow(final CmsRecord job) {
			dataList.add(job);
			fireTableRowsInserted(dataList.size() - 1, dataList.size() - 1);
		}

		public CmsRecord getRow(final int rowNum) {
			if (rowNum < dataList.size()) {
				return dataList.get(rowNum);
			} else {
				return null;
			}
		}

		public void clearTable() {
			dataList.clear();
		}
	}

}
