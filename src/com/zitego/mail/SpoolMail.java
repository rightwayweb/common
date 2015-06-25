package com.zitego.mail;

import java.io.*;

/**
 * A class for spooling mail jobs to the local mail program.
 *
 * Begin a spool session to the supplied local mail program. A session consists of all the commands
 * and text that is sent to the local mail program.
 * <p>
 * An example of a complete session is :<br>
 * /usr/lib/sendmail -t<br>
 * To: bob@boo.com <br>
 * From: j@p.com <br>
 * Subject: Hello! <br>
 * Text of email <br>
 * <p>
 * To accomplish this using this class, the session is first started by calling beginSpool(String, String).
 * <br>
 * beginSpool("/usr/lib/sendmail -t", "-fbounce@inyouremail.com");
 * <p>
 * The text of the mail is then spooled to the session<br>
 * spool("To: bob@boo.com");<br>
 * spool("From: j@p.com");<br>
 * spool("Subject: Hello!");<br>
 * spool("Text of email");<br>
 * <p>
 * And finally, the session is ended and the mail is sent via endSpool()
 * <p>
 * If beginSpool is called in between another call to beginSpool and endSpool, the current session is cleared and the mail
 * is not sent. <br>
 * E.g.<br>
 * beginSpool("sendmail", null);<br>
 * spool("To: je");<br>
 * beginSpool("sendmail", null); //previous session is cleared and a new one is started<br>
 * <p>
 *
 * @author John Glorioso
 * @version $Id: SpoolMail.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class SpoolMail
{
    /** Whether the session is started. */
    private boolean _sessionStarted = false;
    /** The session string. */
    private StringWriter _session;
    /** The session writer. */
    private BufferedWriter _sessionWriter;
    /** The mail command. */
    private String _mailCommand;
    /** Mail command arguments. */
    private String _mailArgs;

    /**
     * Begin a spool session to the supplied local mail program.
     *
     * @param String The local mail program.
     *               Optional parameters to define the command line to use for the mail program.
     *               If this is null, /usr/bin/sendmail is used by default.
     * @param String Any command line parameters to be passed to the mail program.
     * @throws IOException if the mailProg cannot be piped to.
     */
    public void beginSpool(String mailProg, String args) throws IOException
    {
        if (mailProg == null) mailProg = "/usr/bin/sendmail";

        _mailCommand = mailProg;
        _mailArgs = args == null ? "" : args;
        _session = new StringWriter();
        _sessionWriter = new BufferedWriter(_session);
        _sessionStarted = true;

        if (_mailCommand.toLowerCase().indexOf("sendmail") >= 0 && _mailArgs.toLowerCase().indexOf("-t") < 0)
        {
            //Check for recipients
            _mailArgs = "-t " + _mailArgs;
        }
    }

    /**
     * End the current session and send the mail
     *
     * @throws IllegalStateException if a session has not been started.
     * @throws IOException if an error occurs interacting with the local mail program.
     */
    public void endSpool() throws IllegalStateException, IOException
    {
        if (!_sessionStarted) throw new IllegalStateException("endSession called before beginSession in SpoolMail.endSession");

        _sessionWriter.flush();
        _sessionWriter.close();

        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec(_mailCommand + " " + _mailArgs);
        PrintWriter out = new PrintWriter( p.getOutputStream() );
        BufferedReader reader = new BufferedReader
        (
            new InputStreamReader( new ByteArrayInputStream(_session.getBuffer().toString().getBytes()) )
        );
        while ( reader.ready() )
        {
            String line = reader.readLine();
            out.println(line);
        }
        out.flush();
        out.close();
        out = null;
        _sessionStarted = false;
    }

    /**
     * Spool more text out to the session started via beginSpool. A new line is automatically inserted after the text.
     *
     * @param String the text to spool.
     * @throws IllegalStateException if a session has not been started.
     * @throws IOException if an error occurs interacting with the local mail program.
     */
    public void spool(String text) throws IllegalStateException, IOException
    {
        if (!_sessionStarted) throw new IllegalStateException("endSession called before beginSession in SpoolMail.spool");
        _sessionWriter.write(text);
        _sessionWriter.newLine();
    }

    /**
     * Spool an empty line (with a new line) to the session. This can be used to insert
     * carriage returns.
     *
     * @throws IllegalStateException if a session has not been started.
     * @throws IOException if an erro occurs interacting with the local mail program.
     */
    public void spool() throws IllegalStateException, IOException
    {
        if (!_sessionStarted) throw new IllegalStateException("endSession called before beginSession in SpoolMail.spool");
        _sessionWriter.newLine();
    }
}