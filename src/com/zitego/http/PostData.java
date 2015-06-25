package com.zitego.http;

import java.net.*;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * This class encapsulates post data for an http request. All one needs
 * do is add fields to the post data. You can add both String (text)
 * values and Files. If you add a file, the encoding type (content type)
 * will be multipart. You can get the content type by calling the
 * getContentType() method. It will be "application/x-www-form-urlencoded"
 * if the content is not multi-part. Calling toString will encode and convert
 * all fields to the appropriate post string to send to a server. You
 * should call the prepConnection method to set all appropriate headers
 * rather then doing it manually.
 *
 * Example:<br>
 * <code>
 *  <pre>
 *   HttpRequestData request = new HttpRequestData("http://someurl.com/submitfile.cgi?user=john");
 *   PostData data = new PostData();
 *   data.addFileField("my_pic", "me.gif", new File("/home/jglorioso/me.gif"), "image/gif");
 *   data.addField("password", "temp");
 *   request.setPostData(data);
 *   System.out.println( new UrlContentReader(request).getContent() );
 *  </pre>
 * </code>
 *
 * @see HttpRequestData
 * @see UrlContentReader
 * @author John Glorioso
 * @version $Id: PostData.java,v 1.2 2009/08/02 07:27:50 jglorioso Exp $
 */
public class PostData extends GetData
{
    private static final String NEWLINE = "\r\n";
    private static final String BOUNDARY_PREFIX = "--";
    /** Whether this is multipart or not. */
    private boolean _multipart = false;
    /** The boundary of this post data. */
    private String _boundary;
    /** The content type. */
    private String _contentType;

    /**
     * Creates a new PostData object.
     */
    public PostData()
    {
        super();
    }

    /**
     * Returns whether or not this is multipart.
     *
     * @return boolean
     */
    public boolean isMultipart()
    {
        return _multipart;
    }

    /**
     * Sets the appropriate headers and values for this post data object.
     *
     * @param HttpURLConnection The connection.
     * @throws IllegalArgumentException if the connection is null.
     */
    public void prepConnection(HttpURLConnection conn) throws IllegalArgumentException
    {
        if (conn == null) throw new IllegalArgumentException("Connection cannot be null");
        try
        {
            conn.setRequestMethod( HttpMethodType.POST.getDescription() );
        }
        //Won't happen. POST is legal
        catch (ProtocolException pe) { }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty( "Content-Type", getContentType() );
        /*if ( debugging() )
        {
            System.out.println("PostData Headers:");
            Map headers = conn.getHeaderFields();
            for (Iterator keys=headers.keySet().iterator(); keys.hasNext();)
            {
                String header = (String)keys.next();
                System.out.println( header + ": " + headers.get(header) );
            }
        }*/
    }

    /**
     * Adds a file field.
     *
     * @param String The field name.
     * @param String The file name.
     * @param File The file to add.
     * @param String The mimetype of the file.
     * @throws IllegalArgumentException if the name, file, or mimetype is null.
     */
    public void addFileField(String name, String filename, File file, String mimeType) throws IllegalArgumentException
    {
        if ( name == null || "".equals(name) ) throw new IllegalArgumentException("Request field name cannot be empty");
        if (filename == null) throw new IllegalArgumentException("Request field file name cannot be null");
        if (file == null) throw new IllegalArgumentException("Request field file cannot be null");
        _multipart = true;
        _fields.add( new RequestField(name, filename, file, mimeType) );
    }

    /**
     * Writes the request data from this object to the given output stream.
     *
     * @param OutputStream The stream.
     * @throws IllegalArgumentException if the stream is null.
     * @throws IOException if an error occurs writing the data out.
     * @throws FileNotFoundException if an error occurs sending file data.
     */
    public void writeData(OutputStream out) throws IllegalArgumentException, IOException
    {
        if (out == null) throw new IllegalArgumentException("Output stream cannot be null");
        DataOutputStream output = new DataOutputStream(out);

        if (!_multipart)
        {
            //Just strip off the ?
            String data = super.toString();
            if (data.length() > 0) data = data.substring(1);
            if ( debugging() ) System.out.println("PostData: "+data);
            output.writeBytes(data);
        }
        else
        {
            int size = _fields.size();
            String line = null;
            for (int i=0; i<size; i++)
            {
                RequestField f = (RequestField)_fields.get(i);
                if (f.getValue() != null)
                {
                    // --boundary\r\n
                    // Content-Disposition: form-data; name="<fieldName>"\r\n
                    // \r\n
                    // <value>\r\n
                    line = BOUNDARY_PREFIX+getBoundary()+NEWLINE;
                    output.writeBytes(line);
                    if ( debugging() ) System.out.print(line);
                    line = "Content-Disposition: form-data; name=\""+f.getName()+"\""+NEWLINE;
                    output.writeBytes(line);
                    if ( debugging() ) System.out.print(line);
                    output.writeBytes(NEWLINE);
                    if ( debugging() ) System.out.println("");
                    line = encode( f.getValue() )+NEWLINE;
                    output.writeBytes(line);
                    if ( debugging() ) System.out.print(line);
                }
                else
                {
                    // --boundary\r\n
                    // Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
                    // Content-Type: <mime-type>\r\n
                    // \r\n
                    // <file-data>\r\n
                    line = BOUNDARY_PREFIX+getBoundary()+NEWLINE;
                    output.writeBytes(line);
                    if ( debugging() ) System.out.print(line);
                    line = "Content-Disposition: form-data; name=\""+f.getName()+"\" "+
                           "filename=\""+f.getFilename()+"\""+NEWLINE;
                    output.writeBytes(line);
                    if ( debugging() ) System.out.print(line);
                    if (f.getMimeType() != null)
                    {
                        line = "Content-Type: "+f.getMimeType()+NEWLINE;
                        output.writeBytes(line);
                        if ( debugging() ) System.out.print(line);
                    }
                    output.writeBytes(NEWLINE);
                    if ( debugging() ) System.out.println("");
                    FileInputStream in = new FileInputStream( f.getFile() );
                    byte[] data = new byte[1024];
                    int bytesRead = 0;
                    while ( (bytesRead=in.read(data, 0, data.length)) != -1 )
                    {
                        output.write(data, 0, bytesRead);
                        if ( debugging() ) System.out.print( new String(data) );
                    }
                    try
                    {
                        in.close();
                    }
                    catch(Exception e) {}
                    output.writeBytes(NEWLINE);
                    if ( debugging() ) System.out.println("");
                }
            }
            line = BOUNDARY_PREFIX+getBoundary()+BOUNDARY_PREFIX+NEWLINE;
            output.writeBytes(line);
            if ( debugging() ) System.out.print(line);
            output.flush();
            out.close();
        }
    }

    /**
     * Sets the content type of this post. If the content type is not set, then it
     * will automatically be returned as multipart or form encoded when getContentType
     * is called.
     *
     * @param String The content type.
     * @throws IllegalArgumentException if the type is null.
     */
    public void setContentType(String type) throws IllegalArgumentException
    {
        if (type == null) throw new IllegalArgumentException("Content type cannot be null");
        _contentType = type;
    }

    /**
     * Returns the content type.
     *
     * @return String
     */
    public String getContentType()
    {
        if (_contentType != null) return _contentType;
        else if (_multipart) return "multipart/form-data; boundary=" + getBoundary();
        else return "application/x-www-form-urlencoded";
    }

    /**
     * Returns the multipart boundary.
     *
     * @return String
     */
    private String getBoundary()
    {
        if (_boundary == null) _boundary = "--------------------" + Long.toString(System.currentTimeMillis(), 16);
        return _boundary;
    }

    public String toString()
    {
        ByteArrayOutputStream ret = new ByteArrayOutputStream();
        try
        {
            writeData(ret);
        }
        catch (Exception e) { }
        return ret.toString();
    }
}