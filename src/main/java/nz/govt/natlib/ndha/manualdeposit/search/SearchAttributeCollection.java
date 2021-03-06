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


package nz.govt.natlib.ndha.manualdeposit.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType;
import nz.govt.natlib.ndha.common.ilsquery.ILSSearchFacade;
import nz.govt.natlib.ndha.common.ilsquery.IlsSearchFacadeImpl;
import nz.govt.natlib.ndha.common.ilsquery.SruIndex;
import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.exceptions.*;
import nz.govt.natlib.ndha.manualdeposit.customui.LabelTextPair;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SearchAttributeCollection implements Iterable<LabelTextPair> {

	private final static Log LOG = LogFactory.getLog(SearchAttributeCollection.class);
    private final List<SruIndex> indices = new ArrayList<SruIndex>();
    private final List<LabelTextPair> theSearchFields = new ArrayList<LabelTextPair>();
    private final ILSQueryType.eServerType searchType;
    private String searchLabel = "";

    public static SearchAttributeCollection create(AppProperties applicationProperties, ILSQueryType.eServerType searchType) throws SearchException {
        return new SearchAttributeCollection(applicationProperties, searchType);
    }
    
    public SearchAttributeCollection(AppProperties applicationProperties, ILSQueryType.eServerType searchType) throws SearchException {
		this.searchType = searchType;
		loadSearchFields(applicationProperties);
    }

    public Iterator<LabelTextPair> iterator() {
        return theSearchFields.iterator();
    }
    
    public ILSQueryType.eServerType getSearchType() {
    	return searchType;
    }

    public List<SruIndex> getSearchIndices() {
    	return indices;
    }

    public List<LabelTextPair> getTheSearchFields() {
    	return theSearchFields;
    }
    
    public int size() {
    	return theSearchFields.size();
    }
    
    public LabelTextPair get(int index) {
    	return theSearchFields.get(index);
    }
    
    public SruIndex getIndex(int index) {
    	return indices.get(index);
    }
    
    private void loadSearchFields(AppProperties applicationProperties) throws SearchException {
    	theSearchFields.clear();
    	loadSruIndices(applicationProperties);
		for (int i = 0; i < indices.size(); i++) {
			JLabel lblSearch = new JLabel();
			JTextField txtSearch = new JTextField();
			LabelTextPair pair = LabelTextPair.create(lblSearch, txtSearch);
			SruIndex index = indices.get(i);
			pair.setIndex(index);
			pair.getLabel().setText(index.getTitle());
			pair.getField().setText("");
			theSearchFields.add(pair);
		}
    }
    
	private void loadSruIndices(AppProperties applicationProperties) throws SearchException {
		indices.clear();
        try {
	        ILSSearchFacade search = new IlsSearchFacadeImpl(applicationProperties.getApplicationData().getSearchStrategyClass());
	        if (searchType.name() == "CMS2"){
	        	search.setCMS2ServerUrl(applicationProperties.getApplicationData().getCMS2SearchUrl());
	        	searchLabel = applicationProperties.getApplicationData().getCMS2Label();
	        }else if (searchType.name() == "CMS1"){
	        	search.setCMS1ServerUrl(applicationProperties.getApplicationData().getCMS1SearchUrl());
	        	searchLabel = applicationProperties.getApplicationData().getCMS1Label();
	        }
            List<SruIndex> searchIndices = search.getIndexList(searchType, applicationProperties.getApplicationData().getSruSearchSchema());
            for (SruIndex index : searchIndices) {
            	indices.add(index);
            }
        } catch (Exception ex) {
        	//throw new SearchException(ex);
        	LOG.error("Error loading search attributes. " + searchType.name() + " system is not working.", ex);

        	JLabel errorMessage = new JLabel();
			LabelTextPair pair = LabelTextPair.create(errorMessage, null);
			pair.getLabel().setText(searchLabel + " system is currently unavailable.");
			theSearchFields.add(pair);
        }
	}
}
