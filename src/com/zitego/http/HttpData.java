package com.zitego.http;

import java.util.Vector;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * This is a generic class for request data to be used in the URLContentReader class.
 *
 * @author John Glorioso
 * @version $Id: HttpData.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public abstract class HttpData
{
    /** The request fields. */
    protected Vector _fields;
    /** Whether or not we are debugging. */
    private boolean _debug = false;

    /**
     * Creates a new HttpData object.
     */
    public HttpData()
    {
        _fields = new Vector();
    }

    /**
     * Sets whether we are debugging or not.
     *
     * @param boolean The flag.
     */
    public void setDebug(boolean flag)
    {
        _debug = flag;
    }

    /**
     * Returns if we are in debug mode or not.
     *
     * @return boolean
     */
    public boolean debugging()
    {
        return _debug;
    }

    /**
     * Returns the fields.
     *
     * @return Vector
     */
    public Vector getRequestFields()
    {
        return _fields;
    }

    /**
     * Adds a new request field.
     *
     * @param String The field name.
     * @param String The field value.
     * @throws IllegalArgumentException if the field name is null.
     */
    public void addField(String name, String value) throws IllegalArgumentException
    {
        if ( name == null || "".equals(name) ) throw new IllegalArgumentException("Request field name cannot be empty");
        _fields.add( new RequestField(name, value) );
    }

    /**
     * Returns the given field from the post data.
     *
     * @param String The field.
     * @return String
     */
    public String getField(String name)
    {
        int size = _fields.size();
        for (int i=0; i<size; i++)
        {
            RequestField f = (RequestField)_fields.get(i);
            if ( f.getName().equalsIgnoreCase(name) ) return f.getValue();
        }
        return null;
    }

    /**
     * Url encodes the given value in UTF-8 format. If the given value
     * is null, then null is returned.
     *
     * @return String
     */
    protected String encode(String val)
    {
        String ret = null;
        if (val != null)
        {
            try
            {
                ret = URLEncoder.encode(val, "UTF-8");
            }
            //Ignore this because UTF-8 is accepted
            catch (UnsupportedEncodingException uee) { }
        }
        return ret;
    }
}