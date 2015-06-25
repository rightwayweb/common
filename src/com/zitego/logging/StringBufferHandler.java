package com.zitego.logging;

import java.util.logging.*;

/**
 * A handler for use with java logging facility. Accumulates logged messages
 * in a string buffer, the contents of which can later be processed by the
 * calling app.
 *
 * @author John Glorioso
 * @version $Id: StringBufferHandler.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class StringBufferHandler extends Handler
{
    /** The internal buffer to log to. */
    protected StringBuffer _buffer = new StringBuffer();

    /**
     * Creates a new handler with the specified severity.
     *
     * @param Level The severity level.
     */
    public StringBufferHandler(Level severity)
    {
        setLevel(severity);
    }

    public void close() { }

    public void flush() { }

    /**
     * Adds a message to the string buffer, along with a carriage return.
     *
     * @param LogRecord The log record to publish.
     */
    public void publish(LogRecord record)
    {
        if ( isLoggable(record) ) _buffer.append( record.getMessage() ).append("\n");
    }

    /**
     * Returns the text that has been logged as a string.
     *
     * @return String
     */
    public String getBufferedText()
    {
        return _buffer.toString();
    }

    /**
     * Resets the text buffer.
     */
    public void resetBufferedText()
    {
        _buffer.setLength(0);
    }
}