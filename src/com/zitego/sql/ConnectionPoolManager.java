package com.zitego.sql;

import com.zitego.logging.Logger;
import com.zitego.pool.ObjectPoolManager;
import java.io.IOException;
import java.util.*;

/**
 * A class for maintaing a pool of database connections.
 *
 * @author John Glorioso
 * @version $Id: ConnectionPoolManager.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 * @see ConnectionFactory
 */
public class ConnectionPoolManager implements ObjectPoolManager, Cloneable
{
    /** The driver class name. */
    private String _driver;
    /** A database url of the form jdbc:subprotocol:subname. */
    private String _url;
    /** The username used to connect to the database as. */
    private String _username;
    /** The password used to connect to the database with. */
    private String _password;
    /** The table to use to test dbhandle's before returning them. */
    private String _testTable;
    /** The maximum interval (in milliseconds) to test connections. */
    private long _testInterval;
    /** Whether to print debug messages. */
    private boolean _debug = false;
    /** The level of debugging we want. If debug is on and the level is > 0, then we debug all pool
        changes, otherwise just sql. */
    private int _debugLevel = -1;
    /** The sql log to use. */
    private Logger _logger;
    /** Hashtable of last test times of connections. */
    private Hashtable _lastTest;

    /**
     * Creates a new jdbc connection pool.
     *
     * @param String The name of the JDBC driver. Used to load the class.
     * @param String A jdbc database url to the database.
     * @param String The username used to connect to the database.
     * @param String The password used to connect to the database.
     * @param String The table to use to test the connections before returning them from the pool.
     *               This table should be as small as possible. If this argument is null, then the
     *               connections are not tested.
     * @param String The test time interval.
     * @param boolean Whether we are debugging or not.
     * @param int If debug is on and the level is > 0, then we debug all pool changes, otherwise just sql.
     * @param String The sql log.
     * @throws Exception if an error occurs.
     */
    public ConnectionPoolManager(String driver, String url, String username, String password, String testtable,
                                 String testInterval, boolean debug, int debugLevel, String sqlLog)
    throws Exception
    {
        Class.forName(driver).newInstance();
        _driver = driver;
        _url = url;
        _username = username;
        _password = password;
        _testTable = testtable;
        _debug = debug;
        _debugLevel = debugLevel;
        if (_debug || sqlLog != null)
        {
            _logger = Logger.getInstance("com.zitego.sql");
            if (_debug) _logger.registerConsoleHandler("sql_console_log");
            if (sqlLog != null)
            {
                GregorianCalendar midnight = new GregorianCalendar();
                midnight.add(Calendar.DATE, 1);
                midnight.set(Calendar.HOUR_OF_DAY, 0);
                midnight.set(Calendar.MINUTE, 0);
                midnight.set(Calendar.SECOND, 0);
                _logger.registerFileHandler( "sql_log", sqlLog, midnight.getTime() );
            }
        }

        try
        {
            _testInterval = Long.parseLong(testInterval)*1000l;
        }
        catch (NumberFormatException nfe)
        {
            _testInterval = 0;
        }
        _lastTest = new Hashtable();
    }

    public Object createPoolObject() throws Exception
    {
        debug("Creating Connection to: " + _url + " with user " + _username, 1);
        return java.sql.DriverManager.getConnection(_url, _username, _password);
    }

    public boolean validate(Object o)
    {
        if (o instanceof java.sql.Connection)
        {
            if (_testTable == null)
            {
                return true;
            }
            else
            {
                // Check the test interval.
                long currentTime = System.currentTimeMillis();
                long lastTest = -1;
                Object timeStamp = _lastTest.get(o);
                if (timeStamp != null) lastTest = ( (Long)timeStamp ).longValue();

                if ( lastTest == -1 || (currentTime - lastTest > _testInterval) )
                {
                    java.sql.Connection conn = (java.sql.Connection)o;
                    lastTest = currentTime;
                    _lastTest.put( o, new Long(lastTest) );
                    try
                    {
                        if ( conn == null || conn.isClosed() ) return false;
                        java.sql.Statement stmt = conn.createStatement();
                        stmt.executeQuery("SELECT 1 FROM " + _testTable + " WHERE 1=2");
                        stmt.close();
                        return true;
                    }
                    catch(java.sql.SQLException e)
                    {
                        e.printStackTrace();
                        return false;
                    }
                    catch(Throwable t)
                    {
                        t.printStackTrace();
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void expire(Object o)
    {
        try
        {
            debug("Expire Connection to " + _url + " for user " + _username, 1);
            if (o instanceof java.sql.Connection) ( (java.sql.Connection)o ).close();
            _lastTest.remove(o);
        }
        catch (java.sql.SQLException ignore) { }
    }

    public void debug(String msg)
    {
        debug(msg, 0);
    }

    public void debug(String msg, int level)
    {
        if (_debug && _debugLevel >= level) _logger.log(msg);
    }

    /**
     * Change the username used by the manager to connect to the database.
     *
     * @param String The username.
     */
    public void setUsername(String username)
    {
        _username = username;
    }

    /**
     * Returns the username used by the manager to connect to the database.
     *
     * @return String
     */
    public String getUsername()
    {
        return _username;
    }

    /**
     * Returns the password used by the manager to connect to the database.
     *
     * @return String
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Returns the URL used by the manager to connect to the database.
     *
     * @return String
     */
    public String getURL()
    {
        return _url;
    }

    /**
     * Returns the username used by the manager to connect to the database.
     *
     * @return String
     */
    public String getTestTable()
    {
        return _testTable;
    }

    /**
     * Returns the test interval (in seconds) used by the manager to connect to the database.
     *
     * @return String
     */
    public long getTestInterval()
    {
        return _testInterval / 1000l;
    }

    /**
     * Returns whether or not debug is on. If this is true, then getLogger is guaranteed
     * to be not null and will at least log to the console. If the sql_log property was
     * set, then it will log to the specified file as well.
     *
     * @return boolean
     */
    public boolean isDebugOn()
    {
        return _debug;
    }

    /**
     * Returns the debug level.
     *
     * @return int
     */
    public int getDebugLevel()
    {
        return _debugLevel;
    }

    /**
     * Returns the logger. This is guaranteed to be not null if isDebugOn() returns true.
     *
     * @return Logger
     */
    public Logger getLogger()
    {
        return _logger;
    }

    /**
     * Returns the driver used by the manager to connect to the database.
     *
     * @return String
     */
    public String getDriver()
    {
        return _driver;
    }

    /**
     * Returns a copy of this ConnectionPoolManager.
     *
     * @return ConnectionPoolManager
     */
    public ConnectionPoolManager getCopy()
    {
        try
        {
            return (ConnectionPoolManager)super.clone();
        }
        catch(CloneNotSupportedException cne)
        {
            return null;
        }
    }

    public String toString()
    {
        StringBuffer out = new StringBuffer()
            .append("ConnectionPoolManager->[driver=").append(_driver)
            .append(";url=").append(_url)
            .append(";username=").append(_username)
            .append(";password=").append("******")
            .append(";testTable=").append(_testTable)
            .append(";testInterval=").append(_testInterval)
            .append(";debug=").append(_debug)
            .append(";debugLevel=").append(_debugLevel)
            .append(";logger=").append(_logger);
        return out.toString();
    }
}