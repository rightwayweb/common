package com.zitego.logging;

import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A file log handler for use with java logging facility. Supports rotation of
 * files on a timed or sized basis.
 *
 * @author John Glorioso
 * @version $Id: FileHandler.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class FileHandler extends Handler
{
    /** The default format of date used in rotated filenames. */
    public final static String DEFAULT_ROTATE_DATE_FORMAT = "yyyyMMdd";
    /** The buffered writer associated with the log file. */
    private BufferedWriter _bufferedWriter;
    /** The name of the basic log file. */
    protected String _filename;
    /** The timer that controls timed log file rotation. */
    protected Timer _rotateTimer;
    /** The format of timestamp in rotated log file name. */
    protected SimpleDateFormat _rotateDateFormat;
    /** An optional extension to add after timestamp in rotated log file name. */
    protected String _rotateExtension;
    private boolean _autoFlush = false;

    /**
     * A constructor to log to a given filename, which is not rotated.
     *
     * @param filename The log file to be used.
     */
    protected FileHandler(String filename) throws IOException
    {
        this(filename, null, 0);
    }

    /**
     * A constructor to log to a given filename, which is rotated after the specified
     * number of minutes.
     *
     * @param filename The log file to be used.
     * @param start The startup date for rotating. (null means no rotation)
     * @param rotateMinutes Number of minutes before log file is rotated. (ignored if start date is null)
     */
    protected FileHandler(String filename, Date start, int rotateMinutes) throws IOException
    {
        this( filename, start, rotateMinutes, new LogFormatter() );
    }

    /**
     * A constructor to log to a given filename, which is not rotated and uses the given formatter.
     *
     * @param filename The log file to be used.
     * @param formatter The formatter for individual log records.
     */
    protected FileHandler(String filename, Formatter formatter) throws IOException
    {
        this(filename, null, 0, formatter);
    }

    /**
     * A constructor to log to a given filename, which is rotated after the specified
     * number of minutes using the given formatter.
     *
     * @param filename The log file to be used.
     * @param start The startup date for rotating. (null means no rotation)
     * @param rotateMinutes The number of minutes before log file is rotated. (ignored if start date is null)
     * @param formatter The formatter for individual log records.
     */
    protected FileHandler(String filename, Date start, int rotateMinutes, Formatter formatter) throws IOException
    {
        _filename = filename;
        setRotateDateFormat(DEFAULT_ROTATE_DATE_FORMAT);
        setFormatter(formatter);
        _bufferedWriter = new BufferedWriter( new FileWriter(filename, true) );
        if (start != null)
        {
            _rotateTimer = new Timer(true);
            _rotateTimer.scheduleAtFixedRate
            (
                new TimerTask()
                {
                    public void run()
                    {
                        rotate();
                    }
                }, start, rotateMinutes*60L*1000L
            );
        }
    }

    /**
     * Flushes any buffered messages.
     */
    public void flush()
    {
        if (_bufferedWriter != null)
        {
            try
            {
                _bufferedWriter.flush();
            }
            catch (Exception ex)
            {
                getErrorManager().error("Can't flush log", ex, ErrorManager.GENERIC_FAILURE);
            }
        }
    }

    /**
     * Flushes buffered messages and closes the log file stream.
     */
    public void close()
    {
        flush();
        if (_bufferedWriter != null)
        {
            try
            {
                _bufferedWriter.close();
            }
            catch (Exception ex)
            {
                getErrorManager().error("Can't close log", ex, ErrorManager.GENERIC_FAILURE);
            }
        }
    }

    /**
     * Writes loggable records to the log file.
     *
     * @param record The log record to publish.
     */
    public void publish(LogRecord record)
    {
        if ( isLoggable(record) )
        {
            String s = getFormatter().format(record);
            if (s != null)
            {
                try
                {
                    _bufferedWriter.write( s, 0, s.length() );
                    if (_autoFlush) _bufferedWriter.flush();
                }
                catch (Exception ex)
                {
                    getErrorManager().error("Can't write log record " + s, ex, ErrorManager.GENERIC_FAILURE);
                }
            }
        }
    }

    /**
     * Flushes and rotates the log file.
     */
    public synchronized void rotate()
    {
        try
        {
            close();
            String date = _rotateDateFormat.format( new Date() );
            File oldFile = new File(_filename);
            String newFilename = _filename + "." + date;
            if (_rotateExtension != null) newFilename += "." + _rotateExtension;
            File newFile = new File(newFilename);
            oldFile.renameTo(newFile);
            //Remove it if it is empty
            if (newFile.exists() && newFile.length() == 0)
            {
                newFile.delete();
            }
            _bufferedWriter = new BufferedWriter( new FileWriter(_filename, true) );
        }
        catch (Exception ex)
        {
            getErrorManager().error("Can't rotate log file " + _filename, ex, ErrorManager.GENERIC_FAILURE);
        }
    }

    /**
     * Sets the file name.
     *
     * @param filename The file name.
     */
    public void setFilename(String filename)
    {
        _filename = filename;
    }

    /**
     * Returns the file name.
     *
     * @return String
     */
    public String getFilename()
    {
        return _filename;
    }

    /**
     * Sets the filename date format.
     *
     * @param format The format.
     */
    public void setRotateDateFormat(String format)
    {
        _rotateDateFormat = new SimpleDateFormat(format);
    }

    /**
     * Returns the rotate date format pattern.
     *
     * @return String
     */
    public String getRotateDateFormat()
    {
        return _rotateDateFormat.toPattern();
    }

    /**
     * Sets the rotate file extension.
     *
     * @param ext The extension.
     */
    public void setRotateExtension(String ext)
    {
        _rotateExtension = ext;
    }

    /**
     * Returns the rotate file extension.
     *
     * @return String
     */
    public String getRotateExtension()
    {
        return _rotateExtension;
    }

    /**
     * Sets whether auto flush is on or off. Auto flush will flush
     * the buffer every time publish is called.
     *
     * @param autoflush The autoflush flag.
     */
    public void setAutoFlush(boolean autoFlush)
    {
        _autoFlush = autoFlush;
    }

    /**
     * Returns whether autoflush is on or not.
     *
     * @return boolean
     */
    public boolean getAutoflush()
    {
        return _autoFlush;
    }
}