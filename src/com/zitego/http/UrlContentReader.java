package com.zitego.http;

import java.util.Vector;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.servlet.http.Cookie;

/**
 * This is a class used to read content from a url. Headers can be set using the
 * setHeader(String) method. In addition, you can set the refer, user-agent, and host
 * headers through the convenience method created for each. Cookies can also be set eaily
 * using the setCookie(String) method. Finally, basic user authentication can be done
 * through the setUsername(String) and setPassword(String) methods. Redirects can be
 * automatically followed through the setFollowRedirects(boolean) method.
 *
 * Example:<br>
 * <code>
 *  <pre>
 *   HttpRequestData data = new HttpRequestData("http://mail.zitego.com/login");
 *   PostData post = new PostData();
 *   post.setField("email", "johnnyg@hishome.com");
 *   post.setField("password", "temp");
 *   data.setPostData(post);
 *   UrlContentReader urlResponse = new UrlContentReader(data);
 *   System.out.println( urlResponse.getContent() );
 *  </pre>
 * </code>
 *
 * @see HttpRequestData
 * @author John Glorioso
 * @version $Id: UrlContentReader.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class UrlContentReader
{
    private HttpRequestData _request;
    private Vector _cookies = new Vector();

    public static void main(String[] args) throws Exception
    {
        HttpRequestData data = new HttpRequestData(args[0]);
        UrlContentReader reader = new UrlContentReader(data);
        byte[] bytes = reader.getContentAsByteArray();
        System.out.write(bytes, 0, bytes.length);
        System.out.println("");
    }

    /**
     * Creates a new content reader with http request data.
     *
     * @param request The data.
     * @throws IllegalArgumentException if the data is null.
     */
    public UrlContentReader(HttpRequestData request)
    {
        setRequestData(request);
    }

    public String getContent() throws MalformedURLException, IOException
    {
        return new String( getContentAsByteArray() );
    }

    public byte[] getContentAsByteArray() throws MalformedURLException, IOException
    {
        byte[] bytes = new byte[0];
        HttpURLConnection conn = null;
        BufferedInputStream in = null;
        try
        {
            // Open a connection to the URL and get the response back.
            conn = (HttpURLConnection)new URL( _request.getUrl() ).openConnection();

            //Set the headers and post data
            _request.setRequestData(conn);
            
            //Store any cookies set
            for (int i=0; ; i++)
            {
                String name = conn.getHeaderFieldKey(i);
                String val = conn.getHeaderField(i);

                if (name == null && val == null) break;
                
                if ( _request.debugging() ) System.out.println( (name != null ? name+": " : "") + val );
                
                if ( "Set-Cookie".equalsIgnoreCase(name) )
                {
                    // Parse cookie
                    String[] fields = val.split(";\\s*");
                    int index = fields[0].indexOf("=");
                    if (index == -1) index = fields[0].length();
                    Cookie c = new Cookie( fields[0].substring(0, index), fields[0].substring(index+1) );
                    
                    // Parse each field
                    for (int j=1; j<fields.length; j++)
                    {
                        if ( "secure".equalsIgnoreCase(fields[j]) )
                        {
                            c.setSecure(true);
                        }
                        else if (fields[j].indexOf('=') > 0)
                        {
                            String[] f = fields[j].split("=");
                            //if ( "expires".equalsIgnoreCase(f[0]) ) c.setMaxAge( Integer.parseInt(f[1]) );
                            //else
                            if ( "domain".equalsIgnoreCase(f[0]) ) c.setDomain(f[1]);
                            else if ( "path".equalsIgnoreCase(f[0]) ) c.setPath(f[1]);
                        }
                    }
                    _cookies.add(c);
                }
            }

            in = new BufferedInputStream( conn.getInputStream() );
            Vector tmp = new Vector();
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ( (bytesRead=in.read(buffer, 0, buffer.length)) != -1 )
            {
                for (int i=0; i<bytesRead; i++)
                {
                    tmp.add( new Byte(buffer[i]) );
                }
            }
            bytes = new byte[tmp.size()];
            for (int i=0; i<bytes.length; i++)
            {
                bytes[i] = ( (Byte)tmp.elementAt(i) ).byteValue();
            }
        }
        catch (SocketException se)
        {
            //Close the sockets and reset the input stream
            if (conn != null) conn.disconnect();
            if (in != null) in.close();
        }

        return bytes;
    }

    /**
     * Sets the request data.
     *
     * @param request The data.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setRequestData(HttpRequestData request) throws IllegalArgumentException
    {
        if (request == null) throw new IllegalArgumentException("request data cannot be null");
        _request = request;
    }
    
    /**
     * Returns the vector of cookies set from this content read.
     *
     * @return Vector
     */
    public Vector getCookies()
    {
        return _cookies;
    }
}