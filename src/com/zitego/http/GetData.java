package com.zitego.http;

import com.zitego.util.TextUtils;

/**
 * This class encapsulates get data for an http request. All one needs
 * do is add fields to the get data. You can add String (text) values.
 * Calling toString will encode and convert all fields to the
 * appropriate post string to send to a server.
 *
 * @see UrlContentReader
 * @author John Glorioso
 * @version $Id: GetData.java,v 1.2 2009/08/02 07:27:39 jglorioso Exp $
 */
public class GetData extends HttpData
{
    /** Whether or not we should encode the data. Default is true. */
    private boolean _encode = true;

    /**
     * Creates a new GetData object.
     */
    public GetData()
    {
        super();
    }

    /**
     * Sets whether to encode or not.
     *
     * @param encode The boolean flag.
     */
    public void setEncode(boolean encode)
    {
        _encode = encode;
    }

    /**
     * Adds the given query string as fields.
     *
     * @param String The query string.
     */
    public void setQueryString(String queryString)
    {
        if (queryString == null) return;
        if (queryString.length() > 1 && queryString.charAt(0) == '?') queryString = queryString.substring(1);

        //Split into name=value pairs
        String[] pairs = TextUtils.split(queryString, '&');
        for (int i=0; i<pairs.length; i++)
        {
            int index = pairs[i].indexOf("=");
            if (index > -1)
            {
                addField( pairs[i].substring(0, index), pairs[i].substring(index+1) );
            }
            else
            {
                addData( pairs[i] );
            }
        }
    }

    /**
     * Adds just string data to the request.
     *
     * @param String The data to add.
     * @throws IllegalArgumentException if the data is null.
     */
    public void addData(String data) throws IllegalArgumentException
    {
        addField(data, null);
    }

    public String toString()
    {
        StringBuffer ret = new StringBuffer();
        int size = _fields.size();
        if (size > 0) ret.append("?");
        for (int i=0; i<size; i++)
        {
            if (i > 0) ret.append("&");
            RequestField f = (RequestField)_fields.get(i);
            String val = (String)f.getValue();
            ret.append( f.getName() ).append("=");

            if (val != null) ret.append( (_encode ? encode(val) : val) );
        }
        //If there is only one field and there is no value, strip off the = and encode it
        if (size == 1 && ((RequestField)_fields.get(0)).getValue() == null)
        {
            ret.deleteCharAt(ret.length()-1);
            String val = ret.substring(1);
            ret.setLength(0);
            ret.append("?").append( (_encode ? encode(val) : val) );
        }
        return ret.toString();
    }
}