package com.zitego.logging;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.ConsoleHandler;

/**
 * This class is a simplified wrapper around the java.util.logging.Logger class.
 * It is a singleton and can only be used as such. It holds one internal logger
 * object that has no parents. A console handler is registered by default and
 * logs messages on an INFO basis. You may register an email handler or a file
 * handler. All handlers may be removed as well. Calls can be made to log(<msg>)
 * and logSevere(<msg>) to log the messages. They will be processed by the handlers
 * as the log severity level specifies.
 *
 * The getInstance method uses the logging package NameSpace class to insure a
 * unique logger for the namespace of this class loader.
 *
 * @author John Glorioso
 * @version $Id: Logger.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 * @see com.zitego.logging.NameSpace
 */
public class Logger
{
    /** To keep track of all loggers. */
    private static Hashtable _allLoggers;
    /** A private copy of the java.util.logging.Logger. */
    private java.util.logging.Logger _logger;
    /** The logging handlers. */
    protected Hashtable _handlers = new Hashtable();

    public static void main(String[] args) throws Exception
    {
        Logger l = Logger.getInstance("tst");
        Logger l2 = Logger.getInstance("tst");
        System.out.println("l="+l);
        System.out.println("l2="+l2);
    }

    /**
     * Creates a new logger with a given internal logger.
     *
     * @param java.util.Logger The logger.
     */
    private Logger(java.util.logging.Logger l)
    {
        _logger = l;
    }

    /**
     * Returns a logger for the given instance. This goes against the class loader that the
     * class was loaded in and not the system class loader which the Logger uses by default.
     * The reason is that if
     *
     * param String The name of the logger to get or create.
     */
    public static Logger getInstance(String name)
    {
        if (_allLoggers == null) _allLoggers = new Hashtable();
        java.util.logging.Logger l = java.util.logging.Logger.getLogger( NameSpace.getName(name) );
        Logger l2 = (Logger)_allLoggers.get(l);
        if (l2 == null)
        {
            l2 = new Logger(l);
            l.setUseParentHandlers(false);
            _allLoggers.put(l, l2);
        }
        return l2;
    }

    /**
     * Registers a console handler with a handler name. If a handler already exists
     * with this name, then it is replaced with the new one. This uses a default level
     * of INFO.
     *
     * @param String The handler name.
     * @return ConsoleHandler
     */
    public ConsoleHandler registerConsoleHandler(String name)
    {
        return registerConsoleHandler(name, Level.INFO);
    }

    /**
     * Registers a console handler with a handler name and a log level. If
     * a handler already exists with this name, then it is replaced with the new one.
     *
     * @param String The handler name.
     * @param Level The log level.
     * @return ConsoleHandler
     */
    public ConsoleHandler registerConsoleHandler(String name, Level level)
    {
        return registerConsoleHandler(name, null, level);
    }

    /**
     * Registers a console handler with a handler name, a log level, and a format. If
     * a handler already exists with this name, then it is replaced with the new one.
     *
     * @param String The handler name.
     * @param Level The log level.
     * @return ConsoleHandler
     */
    public ConsoleHandler registerConsoleHandler(String name, String format, Level level)
    {
        //See if it is already there
        if (_handlers.get(name) != null) return (ConsoleHandler)_handlers.get(name);

        ConsoleHandler h = new ConsoleHandler();
        if (format != null) h.setFormatter( new LogFormatter(format) );
        else h.setFormatter( new LogFormatter("HH:mm:ss") );
        h.setLevel(level);
        if (_handlers.get(name) == null) _logger.addHandler(h);
        _handlers.put(name, h);
        return h;
    }

    /**
     * Registers an email handler with a handler name, an address recipient string,
     * a from address, and a mail server. The log level is made to be severe by default.
     * The recipient address string can be delimited by semicolon, space, or comma. If
     * a handler already exists with this name, then it is replaced with the new one.
     *
     * @param String The handler name.
     * @param String The recipients.
     * @param String The from address.
     * @param String The subject.
     * @param String The mail server.
     * @return EmailHandler
     */
    public EmailHandler registerEmailHandler(String name, String recips, String from, String subject, String server)
    {
        return registerEmailHandler(name, recips, from, subject, server, null, null);
    }

    /**
     * Registers an email handler with a handler name, an address recipient string,
     * a from address, a mail server, and smtp auth username and password. The log
     * level is made to be severe by default. The recipient address string can be
     * delimited by semicolon, space, or comma. If a handler already exists with
     * this name, then it is replaced with the new one.
     *
     * @param String The handler name.
     * @param String The recipients.
     * @param String The from address.
     * @param String The subject.
     * @param String The mail server.
     * @param String The username.
     * @param String The password.
     * @return EmailHandler
     */
    public EmailHandler registerEmailHandler(String name, String recips, String from, String subject, String server,
                                             String username, String password)
    {
        return registerEmailHandler(name, recips, from, subject, server, username, password, Level.SEVERE);
    }

    /**
     * Registers an email handler with a handler name, an address recipient string,
     * a from address, a mail server, and a log level. The recipient address string
     * can be delimited by semicolon, space, or comma. If a handler already exists
     * with this name, then it is replaced with the new one.
     *
     * @param String The handler name.
     * @param String The recipients.
     * @param String The from address.
     * @param String The subject.
     * @param String The mail server.
     * @param String The username.
     * @param String The password.
     * @param Level The log level.
     * @return EmailHandler
     */
    public EmailHandler registerEmailHandler(String name, String recips, String from, String subject, String server,
                                             String username, String password, Level level)
    {
        //See if it is already there
        if (_handlers.get(name) != null) return (EmailHandler)_handlers.get(name);

        EmailHandler h = new EmailHandler(recips, server, from, subject, level);
        if (username != null && password != null)
        {
            h.setUsername(username);
            h.setPassword(password);
        }
        if (_handlers.get(name) == null) _logger.addHandler(h);
        _handlers.put(name, h);
        return h;
    }

    /**
     * Registers the file handler with a handler name, a filename, and no rotation. This will use
     * the default log formatter time stamp format of "MM/dd/yyyy:HH:mm:ss Z". This will use a
     * default level of INFO for logging.
     *
     * @param String The name of the handler.
     * @param String The file name.
     * @return FileHandler
     * @throws IOException if an error occurs.
     */
    public FileHandler registerFileHandler(String name, String fileName) throws IOException
    {
        return registerFileHandler(name, fileName, null);
    }

    /**
     * Registers the file handler with a handler name, filename, and the start time to rotate the file. By default
     * the log file will be rotated every 24 hours. This will also use the default log formatter time
     * stamp format of "MM/dd/yyyy:HH:mm:ss Z". This will use a default level of INFO for logging.
     *
     * @param String The name of the handler.
     * @param String The file name.
     * @param Date The time to start the rotation. (null means no rotation)
     * @return FileHandler
     * @throws IOException if an error occurs.
     */
    public FileHandler registerFileHandler(String name, String fileName, Date start) throws IOException
    {
        return registerFileHandler(name, fileName, start, 60*24, Level.INFO);
    }

    /**
     * Registers the file handler with a handler name, filename, the start time to rotate the file, the
     * number of minutes to wait before rotating the file at that fixed interval, and the logging level
     * to use. This will use the default log formatter time stamp format of "MM/dd/yyyy:HH:mm:ss Z".
     *
     * @param String The name of the handler.
     * @param String The file name.
     * @param Date The time to start the rotation. (null means no rotation)
     * @param int The number of minutes between each rotation. (ignored if start date is null)
     * @param Level The logging level to use.
     * @return FileHandler
     * @throws IOException if an error occurs.
     */
    public FileHandler registerFileHandler(String name, String fileName, Date start, int interval, Level level)
    throws IOException
    {
        return registerFileHandler(name, fileName, start, interval, level, LogFormatter.DEFAULT_DATE_FORMAT);
    }

    /**
     * Registers the file handler with a handler name, filename, the start time to rotate the file, the
     * number of minutes to wait before rotating the file at that fixed interval, the logging level
     * to use, and the time stamp format to prepend to the log entries. Use null for none.
     *
     * @param String The name of the handler.
     * @param String The file name.
     * @param Date The time to start the rotation. (null means no rotation)
     * @param int The number of minutes between each rotation. (ignored if start date is null)
     * @param Level The logging level to use.
     * @param String The log formatter date format.
     * @return FileHandler
     * @throws IOException if an error occurs.
     */
    public FileHandler registerFileHandler(String name, String fileName, Date start, int interval,
                                           Level level, String format) throws IOException
    {
        //See if it is already there
        if (_handlers.get(name) != null) return (FileHandler)_handlers.get(name);

        FileHandler f = null;
        if (format == null)
        {
            f = new FileHandler(fileName, start, interval);
        }
        else
        {
            f = new FileHandler( fileName, start, interval, new LogFormatter(format) );
            ( (LogFormatter)f.getFormatter() ).setDelimiter(" - ");
        }
        f.setLevel(level);
        if (_handlers.get(name) == null) _logger.addHandler(f);
        _handlers.put(name, f);
        return f;
    }

    /**
     * Logs a message to the specified handler at the level of INFO.
     *
     * @param String The log name.
     * @param String The message.
     * @throws NoSuchHandlerException if the handler does not exist.
     */
    public void logTo(String name, String message) throws NoSuchHandlerException
    {
        logTo(name, message, Level.INFO);
    }

    /**
     * Logs a message to the specified handler at the level of SEVERE.
     *
     * @param String The log name.
     * @param String The message.
     * @throws NoSuchHandlerException if the handler does not exist.
     */
    public void logSevereTo(String name, String message) throws NoSuchHandlerException
    {
        logTo(name, message, Level.SEVERE);
    }

    /**
     * Logs a message to the specified handler at the level specified.
     *
     * @param String The log name.
     * @param String The message.
     * @param Level The log level.
     * @throws NoSuchHandlerException if the handler does not exist.
     */
    public void logTo(String name, String message, Level level) throws NoSuchHandlerException
    {
        Handler h = (Handler)_handlers.get(name);
        if (h == null) throw new NoSuchHandlerException( name + " does not exist in " + _logger.getName() );
        h.publish( new LogRecord(level, message) );
    }

    /**
     * Logs a message to the default logger and lets the handlers process it at
     * the level of INFO.
     *
     * @param String The message.
     */
    public void log(String message)
    {
        log(message, Level.INFO);
    }

    /**
     * Logs a message to the default logger and lets the handlers process it at
     * the level of SEVERE.
     *
     * @param String The message.
     */
    public void logSevere(String message)
    {
        log(message, Level.SEVERE);
    }

    /**
     * Logs a message to the default logger and lets the handlers process it at
     * the level specified.
     *
     * @param String The message.
     * @param Level The log level.
     */
    public void log(String message, Level level)
    {
        _logger.log( new LogRecord(level, message) );
    }
}