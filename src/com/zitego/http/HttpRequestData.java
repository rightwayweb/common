package com.zitego.http;

import com.zitego.util.StringValidation;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Map;
import java.util.List;
import java.net.HttpURLConnection;
import java.io.IOException;

/**
 * Wrappers an entire http request including different types of http data (GET/POST),
 * headers, username/password, and whether or not to follow redirects.
 *
 * Example request:<br>
 * <code>
 *  <pre>
 *   HttpRequestData request = new HttpRequestData("http://www.yahoo.com");
 *   request.setFollowRedirects(true);
 *   request.setReferer("http://google.com");
 *   request.setUserAgent("Java Client");
 *  </pre>
 * </code>
 *
 * @see UrlContentReader
 * @author John Glorioso
 * @version $Id: HttpRequestData.java,v 1.2 2012/12/30 07:00:08 jglorioso Exp $
 */
public class HttpRequestData
{
    /** The headers for this reader. */
    private Hashtable _headers = new Hashtable();
    /** The url. */
    private String _url;
    /** The username. */
    private String _username;
    /** The password. */
    private String _password;
    /** Whether or not to follow redirects. */
    private boolean _followRedirects = true;
    /** The post data. */
    protected PostData _postData = null;
    /** The post data. */
    protected GetData _getData;
    /** Whether or not we are debugging. */
    private boolean _debug = false;

    /**
     * Creates a new HttpRequestData object with a url. The url can be a standalone url
     * without a query string or it can have a query string. The query string will be treated
     * as a GET data.
     *
     * @param String The url
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public HttpRequestData(String url) throws IllegalArgumentException
    {
        _getData = new GetData();
        setUrl(url);
    }

    /**
     * Sets whether we are debugging or not.
     *
     * @param boolean The flag.
     */
    public void setDebug(boolean flag)
    {
        _debug = flag;
        _getData.setDebug(flag);
        if (_postData != null) _postData.setDebug(flag);
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
     * Sets the url of this reader. This will cause the headers hash to be reset.
     *
     * @param String The url.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public void setUrl(String url)
    {
        setUrl(url, true);
    }

    /**
     * Sets the url of this reader and clears out the headers if the clearHeaders
     * flag is true. If the url has a query string it will be escaped appropriately.
     *
     * @param String The url.
     * @param boolean Whether to clear the headers.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public void setUrl(String url, boolean clearHeaders)
    {
        if ( StringValidation.doesNotPartiallyMatch(url, "^https?://") ) throw new IllegalArgumentException("Invalid url: "+url);
        int index = url.indexOf("?");
        if (index > -1)
        {
            _url = url.substring(0, index);
            _getData.setQueryString( url.substring(index+1) );
        }
        else
        {
            _url = url;
        }
        if (clearHeaders) _headers.clear();
    }

    /**
     * Returns the url that this reader read (encoded)
     *
     * @return String
     */
    public String getUrl()
    {
        if (_debug) System.out.println("Url: "+_url);
        return _url+_getData.toString();
    }

    /**
     * Sets the get data.
     *
     * @param GetData The data.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setGetData(GetData data)
    {
        if (data == null) throw new IllegalArgumentException("GET data cannot be null");
        _getData = data;
    }

    /**
     * Returns the get data.
     *
     * @return GetData
     */
    public GetData getGetData()
    {
        return _getData;
    }

    /**
     * Sets the referer header.
     *
     * @param String The referer.
     */
    public void setReferer(String referer)
    {
        if (referer != null) _headers.put("Referer", referer);
        else _headers.remove("Referer");
    }

    /**
     * Returns the refer or null if it doesn't exist.
     *
     * @return String
     */
    public String getReferer()
    {
        return (String)_headers.get("Referer");
    }

    /**
     * Sets the user agent header.
     *
     * @param String The user agent.
     */
    public void setUserAgent(String userAgent)
    {
        if (userAgent != null) _headers.put("User-Agent", userAgent);
        else _headers.remove("User-Agent");
    }

    /**
     * Returns the user agent header.
     *
     * @return String
     */
    public String getUserAgent()
    {
        return (String)_headers.get("User-Agent");
    }

    /**
     * Sets the host header.
     *
     * @param String The host.
     */
    public void setHost(String host)
    {
        if (host != null) _headers.put("Host", host);
        else _headers.remove("Host");
    }

    /**
     * Returns the host header.
     *
     * @return String
     */
    public String getHost()
    {
        return (String)_headers.get("Host");
    }

    /**
     * Returns the cookie header.
     *
     * @param String the cookie.
     */
    public void setCookie(String cookie)
    {
        if (cookie != null) _headers.put("Cookie", cookie);
        else _headers.remove("Cookie");
    }

    /**
     * Returns the cookie header.
     *
     * @return String
     */
    public String getCookie()
    {
        return (String)_headers.get("Cookie");
    }

    /**
     * Sets the content type to send to the url.
     *
     * @param String The content type.
     */
    public void setContentType(String type)
    {
        if (type != null) _headers.put("Content-Type", type);
        else _headers.remove("Content-Type");
    }

    /**
     * Returns the content type.
     *
     * @return String
     */
    public String getContentType()
    {
        return (String)_headers.get("Content-Type");
    }

    /**
     * Sets the username.
     *
     * @param String username.
     */
    public void setUsername(String username)
    {
        _username = username;
    }

    /**
     * Returns the username.
     *
     * @return String
     */
    public String getUsername()
    {
        return _username;
    }

    /**
     * Sets the password.
     *
     * @param String The password.
     */
    public void setPassword(String password)
    {
        _password = password;
    }

    /**
     * Returns the password.
     *
     * @return String
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Sets the post data.
     *
     * @param PostData The post data.
     */
    public void setPostData(PostData data)
    {
        _postData = data;
        if (_postData != null) _postData.setDebug(_debug);
    }

    /**
     * Returns the post data.
     *
     * @return PostData
     */
    public PostData getPostData()
    {
        return _postData;
    }

    /**
     * Sets a header.
     *
     * @param String The name of the header to set.
     * @param String The value of the header to set.
     * @throws IllegalArgumentException if either the name or value is null.
     */
    public void setHeader(String name, String val) throws IllegalArgumentException
    {
        if (name == null) throw new IllegalArgumentException("Header name cannot be null.");
        if (val != null) _headers.put(name, val);
        else _headers.remove(name);
    }

    /**
     * Returns the value of the given header.
     *
     * @param String The header to return.
     * @return String
     */
    public String getHeader(String name)
    {
        if (name == null) return null;
        return (String)_headers.get(name);
    }

    /**
     * Clears the headers for this reader.
     */
    public void clearHeaders()
    {
        _headers.clear();
    }

    /**
     * Returns the headers.
     *
     * @return Hashtable
     */
    public Hashtable getHeaders()
    {
        return _headers;
    }

    /**
     * Sets whether or not we should follow redirects.
     *
     * @param boolean Whether to follow.
     */
    public void setFollowRedirects(boolean followRedirects)
    {
        _followRedirects = followRedirects;
    }

    /**
     * Returns whether we should follow redirects or not.
     *
     * @return boolean
     */
    public boolean getFollowRedirects()
    {
        return _followRedirects;
    }

    /**
     * Sets all of the necessary headers and any post data that there may be.
     *
     * @param HttpURLConnection The connection.
     * @throws IllegalArgumentException if the connection is null.
     * @throws IOException if an error occurs setting the data.
     */
    public void setRequestData(HttpURLConnection conn) throws IllegalArgumentException, IOException
    {
        if (conn == null) throw new IllegalArgumentException("HttpURLConnection cannot be null");

        if (_username != null && _password != null)
        {
            _headers.put( "Authorization", "Basic "+Base64.encode(_username+":"+_password) );
        }

        //Loop through the headers and set them all
        String header = null;
        if (_debug) System.out.println("Headers:");
        for (Enumeration e=_headers.keys(); e.hasMoreElements();)
        {
            header = (String)e.nextElement();
            conn.setRequestProperty( header, (String)_headers.get(header) );
        }

        if (_debug)
        {
            System.out.println("Headers:");
            Map<String,List<String>> props = conn.getRequestProperties();
            for (String key : props.keySet())
            {
                System.out.print(key + ": ");
                List<String> vals = props.get(key);
                int size = vals.size();
                for (int i=0; i<size; i++)
                {
                    System.out.print( (i > 0 ? ", " : "") + vals.get(i) );
                }
                System.out.println("");
            }
        }

        if (_debug) System.out.println("\nFollow Redirects="+_followRedirects);
        conn.setFollowRedirects(_followRedirects);

        if (_debug) System.out.println("GetData: "+_getData);

        //Set the request method if it is set.
        if (_postData != null)
        {
            //Set all appropriate values in the connection
            _postData.prepConnection(conn);

            //Add any post data (this handles a debug message too)
            _postData.writeData( conn.getOutputStream() );
        }
    }

    /**
     * Returns a string representation of this request data.
     *
     * @return String
     */
    public String toString()
    {
        StringBuffer ret = new StringBuffer("url - ").append( getUrl() );
        ret.append(", headers - ");
        int count = 0;
        for (Enumeration e=_headers.keys(); e.hasMoreElements();)
        {
            String header = (String)e.nextElement();
            ret.append( (count++ > 0 ? " - " : "") ).append(header).append(": ").append( _headers.get(header) );
        }
        ret.append(", followRedirects - ").append(_followRedirects);
        if (_postData != null)
        {
            ret.append(", postData - ").append( _postData.toString() );
        }
        return ret.toString();
    }
}
