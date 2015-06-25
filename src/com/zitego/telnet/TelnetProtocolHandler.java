package com.zitego.telnet;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import java.awt.Dimension;
import java.net.Socket;
import java.util.Vector;
import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import javax.security.auth.login.LoginException;

/**
 * This is a wrapper class for anything that uses the telnet protocol to connect
 * to a remote host/port. It is abstract and needs to be implemented for several
 * methods to handle the telnet options and to be able to read and write to the
 * buffer.
 *
 * @author John Glorioso
 * @version $Id: TelnetProtocolHandler.java,v 1.2 2011/10/16 17:33:23 jglorioso Exp $
 */
public abstract class TelnetProtocolHandler implements Runnable, TelnetNotificationHandler
{
    private TelnetClient _client;
    private StringBuffer _responseBuffer = new StringBuffer();
    private String _user;
    private String _pass;
    private String _host;
    private int _port = -1;
    private int _timeout;
    private Socket _socket;
    private OutputStream _commandStream;

    /** The default timeout is 30 seconds. */
    public static final int DEFAULT_TIMEOUT = 30*1000;

    /**
     * Creates a new telnet protocol handler with the DEFAULT_TIMEOUT
     * connect and command sends.
     */
    public TelnetProtocolHandler()
    {
        this(DEFAULT_TIMEOUT);
    }

    /**
     * Creates a new telnet protocol handler with a timeout in milliseconds.
     * A timeout of 0 means never timeout.
     *
     * @param timeout The timeout to use for inactivity.
     */
    public TelnetProtocolHandler(int timeout)
    {
        _timeout = timeout;
        _client = new TelnetClient();
        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);

        try
        {
            _client.addOptionHandler(ttopt);
            _client.addOptionHandler(echoopt);
            _client.addOptionHandler(gaopt);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error registering option handlers: " + e.getMessage());
        }
    }

    /**
     * Connects to the remote socket.
     *
     * @throws IOException if the connect fails.
     */
    public void connect() throws IOException
    {
        try
        {
            _client.connect(_host, _port);
            if (_timeout > 0) _client.setSoTimeout(_timeout);

            Thread reader = new Thread(this);
            _client.registerNotifHandler(this);

            reader.start();

            _commandStream = _client.getOutputStream();
        }
        catch (IOException ioe)
        {
            disconnect();
            throw ioe;
        }
    }

    /**
     * Disconnect the socket and closes the connection.
     *
     * @throws IOException if an error occurs.
     */
    public void disconnect() throws IOException
    {
        if ( _client.isConnected() ) _client.disconnect();
    }

    /**
     * Returns whether this handler is connected to the host.
     *
     * @return boolean
     */
    public boolean isConnected()
    {
        return _client.isConnected();
    }

    /**
     * Reads data from the socket and returns the amount of data read.
     *
     * @param b The input buffer to read in.
     * @return int
     * @throws IOException if an error occurs.
     */
    public int read(byte[] b) throws IOException
    {
        int size = b.length;
        int len = _responseBuffer.length();
        if (len == 0) size = -1;
        else if (len < size) size = len;
        if (size > 0)
        {
            String chunk = _responseBuffer.substring(0, size);
            byte[] b2 = chunk.getBytes();
            System.arraycopy(b2, 0, b, 0, b2.length);
        }
        return size;
    }

    /**
     * Sends the given command and appends a _lineFeedChar \n to the end.
     *
     * @param cmd The command to send.
     * @throws IOException if an error occurs.
     */
    public void sendCommand(String cmd) throws IOException
    {
        write( new String(cmd+"\n").getBytes() );
    }

    /**
     * Sends one byte to the remote host.
     *
     * @param b The byte to be sent.
     * @throws IOException if an error occurs.
     */
    private void write(byte b) throws IOException
    {
        write( new byte[] { b } );
    }

    /**
     * Writes data to the socket.
     *
     * @param b The buffer to be written.
     * @throws IOException if an error occurs.
     */
    public void write(byte[] b) throws IOException
    {
        _commandStream.write(b, 0, b.length);
        _commandStream.flush();
    }

    /**
     * Waits for a string to come from the remote host and returns all
     * the characters that are received until that happens (including
     * the string being waited for). If the string is never returned,
     * and there is nothing more available to read from the socket, then
     * the all input that was received is returned.
     *
     * @param searchElements The strings to look for.
     * @return String
     * @throws IOException if an error occurs.
     */
    public String waitfor(String[] searchElements) throws IOException
    {
        waitForReader();

        //If the _responseBuffer size has not changed in timeout time, then exit
        int lastLen = 0;
        int sameCount = 0;
        int sleepFor = 5;
        while ( sameCount < (_timeout/sleepFor) )
        {
            for (int i=0; i<searchElements.length; i++)
            {
                if (_responseBuffer.indexOf(searchElements[i]) > -1)
                {
                    sameCount = _timeout;
                    break;
                }
                else
                {
                    if ( lastLen == _responseBuffer.length() ) sameCount++;
                    else sameCount = 0;
                    lastLen = _responseBuffer.length();
                    try { Thread.sleep(sleepFor); } catch (InterruptedException ie) { }
                }
            }
        }
        String ret = _responseBuffer.toString();
        _responseBuffer.setLength(0);
        return ret;
    }

    public String waitfor(String match) throws IOException
    {
        return waitfor( new String[] { match } );
    }

    /**
     * Needs to be defined in case the login prompt differs on some servers.
     * Example implementation:<br>
     * <pre>
     * waitfor("login:");
     * write( _user.getBytes() );
     * waitfor("Password:");
     * write( _pass.getBytes() );
     * </pre>
     *
     * @throws IOException if an error occurs.
     * @throws LoginException if the login fails.
     */
    public abstract void login() throws IOException, LoginException;

    /**
     * Returns the terminal type for TTYPE telnet option.
     *
     * @return String
     */
    public abstract String getTerminalType();

    /**
     * Returns the current window size of the terminal for the NAWS telnet option.
     *
     * @return Dimension
     */
    public abstract Dimension getWindowSize();

    /**
     * Sets the local echo option of telnet.
     *
     * @param echo Whether or not to echo input.
     */
    public abstract void setLocalEcho(boolean echo);

    /**
     * Generate an EOR (end of record) request. For use by prompt displaying.
     */
    public abstract void notifyEndOfRecord();

    /**
     * Returns the last response. This will clear the response buffer of all text.
     *
     * @return String
     */
    public String getLastResponse()
    {
        waitForReader();
        String ret = _responseBuffer.toString();
        _responseBuffer.setLength(0);
        return ret;
    }

    /**
     * Returns an array of strings from the text returned from a waitfor call.
     *
     * @param waitfor The text to waitfor.
     * @return String[]
     */
    public String[] getMultiLineResponse(String waitfor)
    {
        return getMultiLineResponse( new String[] { waitfor } );
    }

    /**
     * Returns an array of strings from the text returned from a waitfor call.
     *
     * @param waitfor The array of text to waitfor.
     * @return String[]
     */
    public String[] getMultiLineResponse(String[] waitfor)
    {
        Vector tmp = new Vector();
        try
        {
            BufferedReader in = new BufferedReader( new StringReader(waitfor(waitfor)) );
            String line = null;
            while ( (line=in.readLine()) != null )
            {
                tmp.add(line);
            }
        }
        catch (IOException ioe) {}

        String[] ret = new String[tmp.size()];
        tmp.copyInto(ret);
        return ret;
    }

    /**
     * Sets the username.
     *
     * @param username The username.
     */
    protected void setUsername(String username)
    {
        _user = username;
    }

    /**
     * Returns the username.
     *
     * @return String
     */
    public String getUsername()
    {
        return _user;
    }

    /**
     * Sets the password.
     *
     * @param pass The password.
     */
    protected void setPassword(String pass)
    {
        _pass = pass;
    }

    /**
     * Returns the password.
     *
     * @return String
     */
    public String getPassword()
    {
        return _pass;
    }

    /**
     * Sets the host.
     *
     * @param host The host.
     */
    protected void setHost(String host)
    {
        _host = host;
    }

    /**
     * Returns the host.
     *
     * @return String
     */
    public String getHost()
    {
        return _host;
    }

    /**
     * Sets the port.
     *
     * @param port The port.
     */
    protected void setPort(int port)
    {
        _port = port;
    }

    /**
     * Returns the port.
     *
     * @return int
     */
    public int getPort()
    {
        return _port;
    }

    public void receivedNegotiation(int negotiation_code, int option_code) { }

    /**
     * Reader thread.
     * Reads lines from the TelnetClient and stores them in the response buffer.
     **/
    public void run()
    {
        InputStream instr = _client.getInputStream();
        try
        {
            byte[] buff = new byte[1024];
            int ret_read = 0;
            do
            {
                ret_read = instr.read(buff);
                if (ret_read > 0) _responseBuffer.append( new String(buff, 0, ret_read) );
            }
            while (ret_read >= 0);
        }
        catch (Exception e)
        {
            //Ignore if this was hit because the socket was closed
            String msg = e.toString();
            if (msg.indexOf("Socket closed") == -1) throw new RuntimeException( "Exception while reading socket:" + e.getMessage() );
        }

        try
        {
            if ( _client.isConnected() ) _client.disconnect();
        }
        catch (Exception e)
        {
            throw new RuntimeException( "Exception while closing telnet:" + e.getMessage() );
        }
    }

    private void waitForReader()
    {
        //Wait till there is something in there. Wait no more then _timeout
        int timer = 0;
        int delay = 100;
        while (_responseBuffer.length() == 0 && timer < _timeout)
        {
            try { Thread.sleep(delay); } catch (InterruptedException ie) { }
            timer += delay;
        }
    }
}
