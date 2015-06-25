package com.zitego.util;

import java.util.*;
import javax.servlet.http.*;

/**
 * This class is used to manage values stored in the session. It retrieves
 * Objects given the name and optionally cleans all other attributes out
 * minus the ones specifeed in the ignore list. The ignore list is case
 * sensitive.
 *
 * @author John Glorioso
 * @version $Id: SessionManager.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class SessionManager
{
    /** The list of attribute names to ignore when cleaning. */
    private Hashtable _ignores;

    /**
     * Creates a SessionManager.
     */
    public SessionManager()
    {
        this(new String[0]);
    }

    /**
     * Creates a new SessionManager with the given attribute to ignore.
     *
     * @param String The attribute to ignore.
     */
    public SessionManager(String word)
    {
        this(new String[] { word });
    }

    /**
     * Creates a new SessionManager with the given list of attributes to ignore.
     *
     * @param String[] Ignore list.
     */
    public SessionManager(String[] list)
    {
        _ignores = new Hashtable();
        addToIgnoreList(list);
    }

    /**
     * Adds the given attribute to the ignore list.
     *
     * @param String The attribute to ignore.
     */
    public void addToIgnoreList(String attr)
    {
        addToIgnoreList(new String[] { attr });
    }

    /**
     * Adds the given list of attributes to the ignore list.
     *
     * @param String[] The ignore list.
     */
    public void addToIgnoreList(String[] list)
    {
        if (list != null)
        {
            for (int i=0; i<list.length; i++)
            {
                _ignores.put(list[i], "1");
            }
        }
    }

    /**
     * Returns the given value in the session (in the request) and does not clean out all others that
     * are not in the ignore list.
     *
     * @param HttpServletRequest The request object.
     * @param String The attribute to retrieve.
     * @return Object
     */
    public Object getAttribute(HttpServletRequest request, String attr)
    {
        return getAttribute(request.getSession(), attr, false);
    }

    /**
     * Returns the given value in the session and does not clean out all others that
     * are not in the ignore list.
     *
     * @param HttpSession The session object.
     * @param String The attribute to retrieve.
     * @return Object
     */
    public Object getAttribute(HttpSession session, String attr)
    {
        return getAttribute(session, attr, false);
    }

    /**
     * Returns the given value in the session (in the request) and cleans out all others that
     * are not in the ignore list and cleans the session depending on the clean flag.
     *
     * @param HttpServletRequest The request object.
     * @param String The attribute to retrieve.
     * @param boolean Whether or not to clean.
     * @return Object
     */
    public Object getAttribute(HttpServletRequest request, String attr, boolean clean)
    {
        return getAttribute(request.getSession(), attr, clean);
    }

    /**
     * Returns the given value in the session and cleans out all others that
     * are not in the ignore list and cleans the session depending on the clean flag.
     *
     * @param HttpSession The session object.
     * @param String The attribute to retrieve.
     * @param boolean Whether or not to clean.
     * @return Object
     */
    public Object getAttribute(HttpSession session, String attr, boolean clean)
    {
        Object ret = session.getAttribute(attr);
        if (clean) clean(session);
        if (ret != null) session.setAttribute(attr, ret);
        return ret;
    }

    /**
     * Cleans all values out of the session except for the ones in the ignore list.
     *
     * @param HttpSession The session to clean.
     */
    public void clean(HttpSession session)
    {
        Vector toRemove = new Vector();
        for (Enumeration e=session.getAttributeNames(); e.hasMoreElements();)
        {
            String attr = (String)e.nextElement();
            if (_ignores.get(attr) == null) toRemove.add(attr);
        }
        int size = toRemove.size();
        for (int i=0; i<size; i++)
        {
            session.removeAttribute( (String)toRemove.get(i) );
        }
    }
}