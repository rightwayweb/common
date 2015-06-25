package com.zitego.logging;

import java.util.logging.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A formatter for use with java logging facility. Prepends formatted date
 * and severity level (each of which can be disabled) to each message.
 *
 * @author John Glorioso
 * @version $Id: LogFormatter.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class LogFormatter extends Formatter
{
    /** The default date format to use for all message timestamps. */
    public final static String DEFAULT_DATE_FORMAT = "MM/dd/yyyy:HH:mm:ss Z";
    /** The default delimiter between the date and the logged message. */
    public final static String DEFAULT_DELIMITER = " ";
    /** A String used to format timestamp in log messages. */
    protected SimpleDateFormat _dateFormat;
    /** Whether or not to report severity level in log message. Default is false. */
    protected boolean _showSeverity;
    /** Whether or not to report timestamp in log message. Default is true. */
    protected boolean _showTimestamp;
    /** Whether or not to format the timestamp in log message (according to the dateFormat). Default is true. */
    protected boolean _formatTimestamp;
    /** The delimiter to use when formatting log messages. Default is space. */
    protected String _delimiter;
    /** The string buffer for formatting log messages. */
    protected StringBuffer _buffer;

    /**
     * Creates a new formatter which uses the default date format.
     */
    public LogFormatter()
    {
        this(DEFAULT_DATE_FORMAT);
    }

    /**
     * Creates a new formatter which uses the specified date format.
     *
     * @param String The format string for dates in messages.
     */
    public LogFormatter(String dateFormat)
    {
        setDateFormat(dateFormat);
        _showTimestamp = true;
        _formatTimestamp = true;
        _showSeverity = false;
        _buffer = new StringBuffer();
        setDelimiter(DEFAULT_DELIMITER);
    }

    /**
     * Returns the formatted record as a string.
     *
     * @param String The LogRecord to be formatted.
     * @return String
     */
    public String format(LogRecord record)
    {
        _buffer.setLength(0);
        if (_showTimestamp)
        {
            long millis = record.getMillis();
            if (_formatTimestamp && _dateFormat != null) _buffer.append( _dateFormat.format(new Date(millis)) );
            else _buffer.append(millis);
            _buffer.append(_delimiter);
        }

        if (_showSeverity)
        {
            _buffer.append( record.getLevel().toString() );
            _buffer.append(_delimiter);
        }
        _buffer.append( record.getMessage() );
        _buffer.append("\n");
        return _buffer.toString();
     }

     /**
      * Sets the date format.
      *
      * @param String The format.
      */
     public void setDateFormat(String dateFormat)
     {
        _dateFormat = new SimpleDateFormat(dateFormat);
     }

     /**
      * Returns the date format pattern.
      *
      * @return String
      */
     public String getDateFormat()
     {
        return _dateFormat.toPattern();
     }

     /**
      * Sets whether we should show the severity level in the log.
      *
      * @param boolean The severity flag.
      */
     public void setShowSeverity(boolean showSeverity)
     {
        _showSeverity = showSeverity;
     }

     /**
      * Returns whether we should show the severity level in the logs.
      *
      * @return boolean
      */
     public boolean showSeverity()
     {
        return _showSeverity;
     }

     /**
      * Sets whether we should prepend a time stamp to the log.
      *
      * @param boolean The prepend flag.
      */
     public void setShowTimestamp(boolean showTimestamp)
     {
        _showTimestamp = showTimestamp;
     }

     /**
      * Returns whether we should show a prepend time stamp in the log.
      *
      * @return boolean
      */
     public boolean showTimestamp()
     {
        return _showTimestamp;
     }

     /**
      * Sets whether we should format the timestamp or not.
      *
      * @param boolean The format flag.
      */
     public void setFormatTimestamp(boolean formatTimestamp)
     {
        _formatTimestamp = formatTimestamp;
     }

     /**
      * Returns whether we are formatting the timestamp or not.
      *
      * @return boolean
      */
     public boolean formatTimestamp()
     {
        return _formatTimestamp;
     }

     /**
      * Sets the delimiter.
      *
      * @param String The delimiter.
      */
     public void setDelimiter(String delimiter)
     {
        _delimiter = delimiter;
     }

     /**
      * Returns the delimiter.
      *
      * @return String
      */
     public String getDelimiter()
     {
        return _delimiter;
     }
}