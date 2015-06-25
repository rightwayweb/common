package com.zitego.util;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

/**
 * A bean for keeping track of what nodes are expanded on a tree. Nodes are stored
 * by passing a value in the request under the name "expand". Nodes are removed by
 * passing a value in the request under the name "collapse". This can be used as a
 * bean as follows:<br>
 * <pre>
 * <code>
 *  &lt;jsp:useBean id="someName" scope="session" type="com.zitego.util.TreeRequestObject"&gt;
 *   &lt;setProperty name="someName" property="request" value="&lt;%= request %&gt;"&gt;
 *  &lt;jsp:useBean&gt;
 *
 * @author John Glorioso
 * @version $Id: TreeRequestObject.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class TreeRequestObject
{
    /** The hash to keep track of expanded nodes. */
    private Hashtable _expandedNodes = new Hashtable();

    /**
     * Creates a tree request object.
     */
    public TreeRequestObject() { }

    /**
     * Sets the request to check to see if we are expanding or collapsing nodes.
     *
     * @param HttpServletRequest The request object.
     */
    public void setRequest(HttpServletRequest request)
    {
        if (request != null)
        {
            String val = request.getParameter("expand");
            if (val != null) _expandedNodes.put(val, "1");
            val = request.getParameter("collapse");
            if (val != null) _expandedNodes.remove(val);
        }
    }

    /**
     * Returns if the given named node is expanded.
     *
     * @param String The name.
     * @return boolean
     */
    public boolean isExpanded(String name)
    {
        if (name == null) return false;
        else return (_expandedNodes.get(name) != null);
    }
}