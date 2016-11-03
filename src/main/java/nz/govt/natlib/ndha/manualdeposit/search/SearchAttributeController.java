package nz.govt.natlib.ndha.manualdeposit.search;

import java.util.ArrayList;
import java.util.List;
import nz.govt.natlib.ndha.manualdeposit.exceptions.*;
import nz.govt.natlib.ndha.common.ilsquery.ILSQueryType;
import nz.govt.natlib.ndha.common.ilsquery.SruIndex;
import nz.govt.natlib.ndha.manualdeposit.AppProperties;
import nz.govt.natlib.ndha.manualdeposit.customui.LabelTextPair;
import nz.govt.natlib.ndha.manualdeposit.customui.SearchAttributeDetail;

public class SearchAttributeController {

    private final List<SearchAttributeCollection> theSearchAttributes = new ArrayList<SearchAttributeCollection>();

    public static SearchAttributeController create(AppProperties applicationProperties) throws SearchException {
        return new SearchAttributeController(applicationProperties);
    }
    
	public SearchAttributeController(AppProperties applicationProperties) throws SearchException {
		loadCollections(applicationProperties);
	}
	
    private void loadCollections(AppProperties applicationProperties) throws SearchException {
    	for (ILSQueryType.eServerType searchType : ILSQueryType.eServerType.values()) {
    		SearchAttributeCollection attribute = SearchAttributeCollection.create(applicationProperties, searchType);
    		theSearchAttributes.add(attribute);
    	}
    }
    
    public SearchAttributeCollection getSearchAttributes(ILSQueryType.eServerType searchType) {
    	SearchAttributeCollection retVal = null;
    	for (SearchAttributeCollection collection : theSearchAttributes) {
    		if (collection.getSearchType() == searchType) {
    			retVal = collection;
    			break;
    		}
    	}
    	return retVal;
    }
    
	public SearchAttributeDetail getIDAttribute(String idWanted, ILSQueryType.eServerType searchType) {
		SearchAttributeDetail attribute = null;
		SearchAttributeCollection attributes = getSearchAttributes(searchType);
		if (attributes != null) {
			List<SruIndex> indices = attributes.getSearchIndices();
			for (int i = 0; i < indices.size(); i++) {
				SruIndex index = indices.get(i);
	        	if ((index.getTitle().equalsIgnoreCase("Reference Number"))
	        			//|| (index.getTitle().equalsIgnoreCase("CMS ID"))
	        			|| (index.getTitle().equalsIgnoreCase("MMS ID"))
	        			|| (index.getTitle().equalsIgnoreCase("Record Number"))) {
					String name = index.getNames().get(0);
					attribute = new SearchAttributeDetail(name, idWanted);
					break;
	        	}
			}
		}
		return attribute;
	}
	
	private boolean criteriaSupplied(LabelTextPair pair) {
		if (pair.getIndex() != null
				&& pair.getField().getText() != null 
				&& !pair.getField().getText().equals("")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public List<SearchAttributeDetail> getSearchAttributesSupplied(ILSQueryType.eServerType searchType, List<LabelTextPair> searchAttributeList) {
		SearchAttributeCollection searchAttributes = getSearchAttributes(searchType);
		for (LabelTextPair pairTarget : searchAttributeList) {
			for (LabelTextPair pairSource : searchAttributes) {
				if (pairSource.getLabel().getText().equals(pairTarget.getLabel().getText())) {
					pairTarget.setIndex(pairSource.getIndex());
				}
			}
		}
		List<SearchAttributeDetail> attributes = new ArrayList<SearchAttributeDetail>();
		for (LabelTextPair pair : searchAttributeList) {
			if (criteriaSupplied(pair)) {
				String name = pair.getIndex().getNames().get(0);
				SearchAttributeDetail attribute = new SearchAttributeDetail(name, pair.getField().getText());
				attributes.add(attribute);
			}
		}
		return attributes;
	}
}
