package com.zitego.util;

import java.util.Hashtable;

/**
 * Represents any type of content that can be cached. This class (and all
 * extending classes) must handle storing when the contents of the class
 * changes by using the <code>setChanged</code> method.
 *
 * @author John Glorioso
 * @version $Id: CachedContent.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class CachedContent
{
    /** Whether we are debugging or not. */
    protected static final boolean _debug = "1".equals( System.getProperty("debug") );
    /** Whether the element has changed or not. */
    private boolean _changed = true;
    /** The cached content, keyed by any object. */
    private Hashtable _cachedContent = new Hashtable();

    /**
     * Sets that this has changed.
     */
    protected void setChanged()
    {
        //if (_debug) System.out.println(getClass()+" changed");
        _changed = true;
    }

    /**
     * Returns whether this has changed or not.
     *
     * @return boolean
     */
    public boolean hasChanged()
    {
        return _changed;
    }

    /**
     * Caches the given content.
     *
     * @param Object What to key the content by.
     * @param Object The content to cache.
     */
    protected void cacheContent(Object key, Object content)
    {
        _cachedContent.put(key, content);
        _changed = false;
    }

    /**
     * Returns the cached content given the key.
     *
     * @param Object The key.
     * @return Object
     */
    public Object getCachedContent(Object key)
    {
        return _cachedContent.get(key);
    }

    /**
     * Clears the cached content.
     *
     * @param Object The key.
     */
    public void clearContent(Object key)
    {
        _cachedContent.remove(key);
    }
}