import java.util.HashMap;
import java.util.Map;
 import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.cs.core.search.data.IndexData;
import com.fatwire.cs.core.search.data.ResultRow;
import com.fatwire.cs.core.search.query.Operation;
import com.fatwire.cs.core.search.query.QueryExpression;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.search.SimpleSearchEngine;

public class SearchAction implements Action {

    @InjectForRequest
    public SimpleSearchEngine lucene;

    @InjectForRequest
    public Model model;
    static Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.customactions");

    @Override
    public void handleRequest(ICS ics) {
       model.add("title","the search page"); 
         String query = ics.GetVar("q");
       if(query ==null) return;

        QueryExpression qry = lucene.newQuery("name", Operation.CONTAINS, query);
LOG.info("searching for "+query);

        for (ResultRow row : lucene.search(qry, "Page")) {
            LOG.info("found search result "+ row);
            Map<String, String> result = new HashMap<String, String>();
            for (String name : row.getFieldNames()) {
                IndexData data = row.getIndexData(name);
                if (data != null) {
                    result.put(name, data.getData());
                }
            }
            model.list("results", result);
        }

    }
}

