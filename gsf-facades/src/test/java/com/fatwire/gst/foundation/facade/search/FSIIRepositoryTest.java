/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fatwire.gst.foundation.facade.search;

import com.fatwire.cs.core.search.data.ResultRow;
import com.fatwire.cs.core.search.query.Operation;
import com.fatwire.cs.core.search.query.QueryExpression;

import junit.framework.TestCase;

/**
 * Simple test utility for running a search against the standard FSII search respository.
 * Not yet set up to follow a proper test framework.  Must be called from within a JSP scriptlet for now.
 *
 * @author Tony Field
 * @since Feb 15, 2011
 */
public final class FSIIRepositoryTest extends TestCase {
    // x and y and z and (a or b)
    // x is between condition
    // the rest are equals conditions
    /*
FSIIAbstract
FSIIBody
FSIIByline
FSIICategoryName
FSIIDescriptionAttr
FSIIHeadline
FSIIImage
FSIIKeyword
FSIINameAttr
FSIIPostDate
FSIISubheadline
FSIITemplateAttr
FSIITitle


<%
SimpleSearchEngine lucene = SimpleSearchEngine.getInstance("lucene");
        QueryExpression qry = lucene.newQuery("FSIIBody", Operation.CONTAINS, "device");
        qry = qry.and("createdby", Operation.CONTAINS, "admin");
List<Date> dates = new ArrayList<Date>();
dates.add(new Date(0L));
dates.add(new Date(911120000019828L));
//dates.add(new Date(2L));
//qry = qry.and("createddate", Operation.RANGE, dates);
//qry = qry.and("FSIIByline", Operation.EQUALS, "Barton P. Fooman");

        StringBuilder sb = new StringBuilder("Search results:");
        sb.append("<table>");
        sb.append("<tr><th>id</th><th>name</th><th>relevance</th></tr>");
        for (ResultRow row : lucene.search(qry, "Content_C")) {
            sb.append("<tr>");
            sb.append("<td>").append(row.getIndexData("id").getData()).append("</td>");
            sb.append("<td>").append(row.getIndexData("name").getData()).append("</td>");
            sb.append("<td>").append(row.getRelevance()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
%><%=sb%>



     */

    public void testAndQuery() {
        // getTestOutput();
        // todo: low priority: make this into an actual test.
    }

    public String getTestOutput() {

        SimpleSearchEngine lucene = SimpleSearchEngine.getInstance("lucene");
        QueryExpression qry = lucene.newQuery("FSIIByline", Operation.CONTAINS, "Barton");
        qry.and("FSIITitle", Operation.CONTAINS, "About");

        StringBuilder sb = new StringBuilder("Search results:");
        sb.append("<table>");
        sb.append("<tr><th>id</th><th>name</th><th>relevance</th></tr>");
        for (ResultRow row : lucene.search(qry, "Content_C")) {
            sb.append("<tr>");
            sb.append("<td>").append(row.getIndexData("id").getData()).append("</td>");
            sb.append("<td>").append(row.getIndexData("name").getData()).append("</td>");
            sb.append("<td>").append(row.getRelevance()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

}
