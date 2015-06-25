package com.zitego.http;

import java.io.*;

/**
 * This uses the Base64 class to decode bytes from an input stream. The
 * decoding occurs as bytes are read out.
 *
 * @author John Glorioso
 * @version $Id: Base64DecoderStream.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class Base64DecoderStream extends FilterInputStream
{
    /** The decoded bytes. */
    private byte[] _buffer;
    /** The size of the byte buffer. */
    private int _bufferSize = 0;
    /** The index at which we are at in the buffer. */
    private int _index = 0;

    public static void main(String args[]) throws Exception
    {
        if (args.length != 1)
        {
            System.out.println("Usage: java Base64DecoderStream <base64 data file path>");
            System.exit(1);
        }
        FileInputStream infile = new FileInputStream(args[0]);
        Base64DecoderStream decoder = new Base64DecoderStream(infile);

        int c;
        while ( (c=decoder.read()) != -1 )
        {
            System.out.print( (char)c );
        }
        System.out.flush();
    }

    /** 
     * Creates a new decoder stream from the given input stream.
     *
     * @param in The input stream.
     */
    public Base64DecoderStream(InputStream in)
    {
        super(in);
        //Each decoded val
        _buffer = new byte[3];
    }

    /**
     * Reads the next decoded byte from this input stream. It is an int
     * from 0 to 255. If no byte is available because the end of 
     * the stream has been reached, the -1 is returned.
     *
     * @return int
     * @throws IOException if an error occurs reading the stream.
     */
    public int read() throws IOException
    {
        if (_index >= _bufferSize)
        {
            decode(); // Fills up buffer
            if (_bufferSize == 0) return -1;
            _index = 0;
        }
        return _buffer[_index++] & 0xff;
    }

    /**
     * Reads up to len decoded bytes of data from this input stream
     * into an array of bytes. This method blocks until some input is
     * available. This returns the total number of bytes read into the
     * buffer, or -1 if there is no more data because the end of
     * the stream has been reached.
     *
     * @param buf The buffer into which the data is read.
     * @param start The start offset of the data.
     * @param len The number of bytes to read.
     * @return int
     * @throws IOException if an I/O error occurs.
     */
    public int read(byte[] buf, int start, int len) throws IOException
    {
        int i, c;
        for (i=0; i<len; i++)
        {
            if ( (c=read()) == -1 )
            {
                if (i == 0) i = -1;
                break;
            }
            buf[start+i] = (byte)c;
        }
        return i;
    }

    public boolean markSupported()
    {
        return false;
    }

    /**
     * Returns the result of the .available() method on the input stream
     * multiplied by 3 and divided by 4 (standard base64) plus the buffer
     * offset. This is an estimation as the input stream may contain
     * CRLF's.
     *
     * @return int
     */ 
    public int available() throws IOException
    {
         return ( (in.available()*3)/4 + (_bufferSize-_index) );
    }

    private final static char[] validChars =
    {
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n',
        'o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3',
        '4','5','6','7','8','9','+','/'
    };
    private final static byte convertArray[] = new byte[256];

    static
    {
        for (int i=0; i<255; i++)
        {
            convertArray[i] = -1;
        }
        for(int i=0; i<validChars.length; i++)
        {
            convertArray[validChars[i]] = (byte)i;
        }
    }

    private void decode() throws IOException
    {
        byte[] buffer = new byte[4];
        _bufferSize = 0;
        int got = 0;
        while (got < 4)
        {
            int i = in.read();
            if (i == -1) return;
            
            if (i >= 0 && i < 256 && i == '=' || convertArray[i] != -1) buffer[got++] = (byte)i;
        }

        byte[] decoded = Base64.decodeToBytes(buffer);
        if (decoded == null) decoded = new byte[0];
        _bufferSize = decoded.length;
        System.arraycopy(decoded, 0, _buffer, 0, decoded.length);
    }
}