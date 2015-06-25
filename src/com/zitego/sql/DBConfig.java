package com.zitego.sql;

import com.zitego.logging.Logger;
import java.util.Properties;

/**
 * Configuration information for an object that implements DBHandle.
 *
 * @author John Glorioso
 * @version $Id: DBConfig.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DBConfig implements java.io.Serializable
{
    /** MYSQL handle. */
    public static final int MYSQL = 1;
    /** SQLServer handle. */
    public static final int SQL_SERVER = 2;
    /** The JDBC url. */
    protected String _url;
    /** The JDBC driver */
    protected java.sql.Driver _driver;
    /** The username to use to connect. */
    protected String _username;
    /** The password to use to connect. */
    protected String _password;
    /** The type of database to connect to. */
    protected int _type = MYSQL;
    /** An optional Logger for logging SQL. The logging flags are set in DBConfig. */
    private Logger _logger;
    /** A flag on whether or not to log sql statements. */
    private boolean _logSql = false;
    /** Seconds to allow a query to run. */
    private int _callTimeLimit = 0;

    /**
     * Creates a new DBConfig object with the supplied attributes.
     *
     * @param url The JDBC url.
     * @param driver The JDBC driver.
     * @param username The username to use to connect.
     * @param password The password to use to connect.
     * @param type The type of handle.
     * @throws IllegalArgumentException if any values are null.
     */
    public DBConfig(String url, java.sql.Driver driver, String username, String password, int type)
    throws IllegalArgumentException
    {
        super();
        setUrl(url);
        setDriver(driver);
        setUsername(username);
        setPassword(password);
        setType(type);
    }

    /**
     * Creates a new DBConfig object with the supplied attributes.
     *
     * @param url The JDBC url.
     * @param driver The JDBC driver.
     * @param username The username to use to connect.
     * @param password The password to use to connect.
     * @param type The type of handle.
     * @param writer Used to log sql queries. Can be null for no logging
     * @throws IllegalArgumentException - if any values are null
     */
    public DBConfig(String url, java.sql.Driver driver, String username, String password, int type, Logger writer)
    throws IllegalArgumentException
    {
        this(url, driver, username, password, type);
        setLogger(writer);
    }

    /**
     * Creates a new DBConfig object from the attributes supplied. Note that type (only) can be NULL,
     * in which case a connection of type MYSQL is established.
     *
     * @param url The JDBC url.
     * @param driverClassName The JDBC driver class name
     * @param username The username to use to connect.
     * @param password The password to use to connect.
     * @param type The type of handle.  Must be MYSQL. If null, MYSQL is assumed.
     * @param writer Used to log sql queries. Can be null to indicate no logging.
     * @throws Exception if there's a problem with one of the arguments (likely the driver class name).
     */
    public DBConfig(String url, String driverClassName, String username, String password, int type, Logger writer)
    throws Exception
    {
        this(url, (java.sql.Driver)Class.forName(driverClassName).newInstance(), username, password, type, writer);
    }

    /**
     * Creates a bew DBConfig object from the Properties object. Looks for the following properties in this object:<br>
     * jdbc.url<br>
     * jdbc.username<br>
     * jdbc.password<br>
     * jdbc.driver<br>
     * jdbc.type<br>
     * Note jdbc.type (only) can be NULL, in which case a connection of type MYSQL is established.
     *
     * @param properties The property object.
     * @param writer Used to log sql queries. Can be null to indicate no logging.
     * @throws IllegalArgumentException - if any values in Properties object are null or invalid.
     */
    public DBConfig(Properties properties, Logger writer) throws IllegalArgumentException
    {
        setUrl( properties.getProperty("jdbc.url") );
        setUsername( properties.getProperty("jdbc.username") );
        setPassword( properties.getProperty("jdbc.password") );
        setLogger(writer);
        String driverClassName = properties.getProperty("jdbc.driver");
        String type = properties.getProperty("jdbc.type");

        try
        {
            setDriver( (java.sql.Driver)Class.forName(driverClassName).newInstance() );
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid jdbc.driver " + driverClassName);
        }

        int itype = parseType(type);
        if (itype == MYSQL || itype == SQL_SERVER) setType(itype);
        else throw new IllegalArgumentException("type must be MYSQL or SQL_SERVER");
    }

    /**
     * Sets the JDBC url.
     *
     * @param url The new url.
     * @throws IllegalArgumentException if the url is null.
     */
    public void setUrl(String url) throws IllegalArgumentException
    {
        if (url == null) throw new IllegalArgumentException("url cannot be null in DBConfig()");
        _url = url;
    }

    /**
     * Returns the JDBC url.
     *
     * @return String
     */
    public String getUrl()
    {
        return _url;
    }

    /**
     * Sets the JDBC driver.
     *
     * @param driver The new driver.
     * @throws IllegalArgumentException if the driver is null.
     */
    public void setDriver(java.sql.Driver driver) throws IllegalArgumentException
    {
        if (driver == null) throw new IllegalArgumentException("driver cannot be null in DBConfig()");
        _driver= driver;
    }

    /**
     * Returns the JDBC driver.
     *
     * @return java.sql.Driver
     */
    public java.sql.Driver getDriver()
    {
        return _driver;
    }

    /**
     * Sets the username.
     *
     * @param username The new username.
     * @throws IllegalArgumentException if the username is null.
     */
    public void setUsername(String username) throws IllegalArgumentException
    {
        if (username == null) throw new IllegalArgumentException("username cannot be null in DBConfig()");
        _username = username;
    }

    /**
     * Returns the username.
     *
     * @return String
     */
    public String getUsername()
    {
        return _username;
    }

    /**
     * Sets the password.
     *
     * @param password The new password.
     * @throws IllegalArgumentException if the password is null.
     */
    public void setPassword(String password) throws IllegalArgumentException
    {
        if (password == null) throw new IllegalArgumentException("password cannot be null in DBConfig()");
        _password = password;
    }

    /**
     * Returns the password.
     *
     * @return String
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Sets the database type.
     *
     * @param type The new type.
     */
    public void setType(int type)
    {
        _type = type;
    }

    /**
     * Returns the database type.
     *
     * @return int
     */
    public int getType()
    {
        return _type;
    }

    /**
     * Sets the Logger.
     *
     * @param writer The logger.
     */
    public void setLogger(Logger writer)
    {
        _logger = writer;
    }

    /**
     * Returns the Logger.
     *
     * @return Logger
     */
    public Logger getLogger()
    {
        return _logger;
    }

    /**
     * Sets whether or not to log select queries.
     *
     * @param flag true or false.
     */
    public void setLogSql(boolean flag)
    {
        _logSql = flag;
    }

    /**
     * Returns whether we are logging sql or not.
     *
     * @return boolean
     */
    public boolean logSql()
    {
        return _logSql;
    }

    /**
     * Sets number of seconds to allow a query to go on.
     *
     * @param callTimeLimit The call time limit.
     */
    public void setCallTimeLimit(int callTimeLimit)
    {
        _callTimeLimit = callTimeLimit;
    }

    /**
     * Returns seconds a query can go on for.
     *
     * @return int
     */
    public int getCallTimeLimit()
    {
        return _callTimeLimit;
    }

    /**
     * Returns a String representation of this object.
     *
     */
    public String toString()
    {
        StringBuffer ret = new StringBuffer()
            .append("[url=").append( getUrl() ).append(", driver=").append( getDriver() )
            .append(", username=").append( getUsername() ).append(", password=*****]");
        return ret.toString();
    }

    /**
     * Returns the type based on the string passed in. If the type is invalid, -1 will be returned.
     *
     * @param type Presumably mysql or sql_server.
     * @return int
     */
    public static int parseType(String type)
    {
        if ( "mysql".equalsIgnoreCase(type) ) return 1;
        else if ( "sql_server".equalsIgnoreCase(type) ) return 2;
        else return -1;
    }
}