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
import java.util.ArrayList;

import javax.swing.JTable;

import nz.govt.natlib.ndha.common.ilsquery.CmsRecord;
import nz.govt.natlib.ndha.common.ilsquery.CmsResults;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType;
import nz.govt.natlib.ndha.common.ilsquery.ILSSearchFacade;
import nz.govt.natlib.ndha.common.ilsquery.IlsSearchFacadeImpl;
import nz.govt.natlib.ndha.common.ilsquery.criteria.SingleCriteria;
import nz.govt.natlib.ndha.common.mets.IMetaDataType;
import nz.govt.natlib.ndha.manualdeposit.CMSSearchResultsPresenter.ResultsTableModel;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositPresenterTest.ManualDepositFrameTest;
import nz.govt.natlib.ndha.manualdeposit.customui.SearchAttributeDetail;
import nz.govt.natlib.ndha.manualdeposit.metadata.ApplicationData;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTableModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for searching cms1<br>
 * Assumes there will be a match for author John Clarke<br>
 * At time of test writing there were 18 matches so this seems a pretty safe bet
 * 
 * @author PlayerM
 */
public class CMSSearchResultsTest {

	private CMSSearchResultsPresenter searchPresenter;
	private CMSSearchResultsFrameTest theFrame;
	private ManualDepositFrameTest parentFrame;
	private ManualDepositPresenter parentPresenter;
	private boolean isVisible = false;
	private JTable theTable = new JTable();
	private static CmsResults cmsResults;
	private String selectedRecString;
	private boolean formClosed;
	private LoginTest loginFrame;
	private LoginPresenterTest loginPresenter;
	private AppProperties appProperties;
	private boolean canSelectSearch = false;
	private boolean canPageForwardResults = false;
	private boolean canPageBackwardsResults = false;
	private String thePagingMessage = "";
	private static SearchAttributeDetail attribute = new SearchAttributeDetail(
			"TI", "Maori");

	public class CMSSearchResultsFrameTest implements ICMSSearchResults {
		public void showView() {
			isVisible = true;
		}

		public void setPresenter(CMSSearchResultsPresenter presenter) {

		}

		public void showDetail(String detail) {
			selectedRecString = detail;
		}

		public void closeForm() {
			formClosed = true;
		}

		public void setFormFont(Font theFont) {

		}

		public void showError(String header, String message) {

		}

		public void showMessage(String header, String message) {

		}

		public void setStatus(boolean canSelect, boolean canPageForward,
				boolean canPageBackwards, String pagingMessage) {
			canSelectSearch = canSelect;
			canPageForwardResults = canPageForward;
			canPageBackwardsResults = canPageBackwards;
			thePagingMessage = pagingMessage;
		}

		public void setWaitCursor(boolean isWaiting) {

		}
	}

	/**
	 * Once only setup - used for high overhead transactions<br>
	 * Runs the ILS search<br>
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// SearchAttributeDetail attribute = new SearchAttributeDetail("TI",
		// "Maori");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Runs before every test<br>
	 * Sets up the manual deposit presenter & frame<br>
	 * - uses the ManualDepositFrameTest version so we don't have a UI Sets up
	 * the cms 1 search presenter & frame<br>
	 */
	@Before
	public void setUp() throws Exception {
		try {
			appProperties = new AppProperties();
		} catch (Exception ex) {
			fail();
		}
		ApplicationData appData = appProperties.getApplicationData();
		/*
		 * System.out.println("***********************************");
		 * System.out.println("SRU Search Schema " +
		 * appData.getSruSearchSchema());
		 * System.out.println("Tapuhi Search URL " +
		 * appData.getCMS2SearchUrl());
		 * System.out.println("CMS 1 Search URL " +
		 * appData.getCMS1SearchUrl());
		 * System.out.println("***********************************");
		 */
		ManualDepositPresenterTest depositTest = new ManualDepositPresenterTest();
		parentFrame = depositTest.new ManualDepositFrameTest();
		loginFrame = new LoginTest();
		SingleCriteria criteria = new SingleCriteria(attribute.getAttribute(),
				attribute.getAttributeName(), attribute.getValue());
		System.out.println("Loading ILSSearchFacade");
		ILSSearchFacade search = new IlsSearchFacadeImpl();
		search.setCMS2ServerUrl(appData.getCMS2SearchUrl());
		search.setCMS1ServerUrl(appData.getCMS1SearchUrl());
		cmsResults = search.runQuery(criteria, ILSQueryType.eServerType.CMS2,
				1, 10, 0, "dps", "mngroot", "mngroot");
		loginPresenter = new LoginPresenterTest();
		parentPresenter = new ManualDepositPresenter(parentFrame, loginFrame,
				loginPresenter, appProperties);
		parentFrame.setPresenter(parentPresenter);
		parentPresenter.setupScreen();
		loginPresenter.login("mngroot", "mngroot");
		parentFrame.setPresenter(parentPresenter);
		theFrame = new CMSSearchResultsFrameTest();
		formClosed = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests setting up the presenter<br>
	 */
	@Test
	public final void testSetup() {
		ArrayList<SearchAttributeDetail> attributes = new ArrayList<SearchAttributeDetail>();
		SearchAttributeDetail attribute = new SearchAttributeDetail("Bert",
				"Sutcliffe");
		attributes.add(attribute);
		searchPresenter = new CMSSearchResultsPresenter(theFrame, attributes,
				parentPresenter, appProperties, ILSQueryType.eServerType.CMS2);
		searchPresenter.setupPresenter();
		assertTrue(isVisible);
	}

	/**
	 * Main test for the cms 1 search<br>
	 * Tests everything<br>
	 * - table model has the same number of rows as the results<br>
	 * - selecting rows in the table<br>
	 * - selecting an ILS record as the CMS record<br>
	 * - meta data is set correctly
	 */
	@Test
	@Ignore
	public final void testAddResultsTableModelAndHandlers() {
		// Includes test for selecting data & selecting a record
		ArrayList<SearchAttributeDetail> attributes = new ArrayList<SearchAttributeDetail>();
		attributes.add(attribute);
		searchPresenter = new CMSSearchResultsPresenter(theFrame, attributes,
				parentPresenter, appProperties, ILSQueryType.eServerType.CMS2);
		theFrame.setPresenter(searchPresenter);
		searchPresenter.addResultsTableModelAndHandlers(theTable);
		searchPresenter.showResults();
		assertFalse(canSelectSearch);
		assertTrue(canPageForwardResults);
		assertFalse(canPageBackwardsResults);
		assertTrue(thePagingMessage != null && !thePagingMessage.equals(""));
		searchPresenter.pageForward();
		assertTrue(canPageForwardResults);
		assertTrue(canPageBackwardsResults);
		ResultsTableModel model = (ResultsTableModel) theTable.getModel();
		assertTrue(model.getRowCount() == cmsResults.getRecordCount());
		if (model.getRowCount() > 0) {
			int rowSelected = model.getRowCount() - 1;
			CmsRecord rec = model.getRow(rowSelected);
			String cmsID = rec.getId();
			String systemName = "Tapuhi";
			selectedRecString = "";
			theTable.setRowSelectionInterval(rowSelected, rowSelected);
			assertTrue(rec.toString().equals(selectedRecString));
			selectedRecString = "";
			searchPresenter.selectData();
			assertTrue(rec.toString().equals(selectedRecString));
			searchPresenter.selectRecord();
			assertTrue(formClosed);
			MetaDataTableModel metaModel = parentFrame.getMetaDataTableModel();
			boolean idFound = false;
			boolean idMatches = false;
			boolean systemFound = false;
			boolean systemMatches = false;
			for (int i = 0; i < metaModel.getRowCount(); i++) {
				IMetaDataType meta = metaModel.getRow(i);
				if (meta.getDataFieldName().equalsIgnoreCase("CMSIdentifier")) {
					idFound = true;
					idMatches = (meta.getDataFieldValue()
							.equalsIgnoreCase(cmsID));
					if (systemFound) {
						break;
					}
				}
				if (meta.getDataFieldName().equalsIgnoreCase("CMSSystem")) {
					systemFound = true;
					systemMatches = (meta.getDataFieldValue()
							.equalsIgnoreCase(systemName));
					if (idFound) {
						break;
					}
				}
			}
			assertTrue(idFound);
			assertTrue(idMatches);
			assertTrue(systemFound);
			assertTrue(systemMatches);
		}
	}

}
