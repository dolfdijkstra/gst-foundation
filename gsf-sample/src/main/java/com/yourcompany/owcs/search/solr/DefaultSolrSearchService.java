package com.yourcompany.owcs.search.solr;

import COM.FutureTense.Interfaces.ICS;

import com.yourcompany.owcs.search.SearchResults;
import com.yourcompany.owcs.search.SolrSearchService;

public class DefaultSolrSearchService implements SolrSearchService {

    public DefaultSolrSearchService(ICS ics) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public SearchResults search(String string) {
        // TODO Auto-generated method stub
        return new SearchResults();

    }

}
