package com.zitego.sql;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * An implementation of the JDBC driver interface that is used for connection pooling.
 * This driver automatically calls newInstance(), so calling it yourself is not necessary.
 *
 * @author John Glorioso
 * @version $Id: Driver.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class Driver implements java.sql.Driver, java.io.Serializable
{
    /** A static copy of itself. */
    private static Driver _default;

    /**
     * Register the driver with the JDBC Driver Manager.
     */
    static
    {
        try
        {
            _default = new Driver();
            DriverManager.registerDriver(_default);
        }
        catch (SQLException e)
        {
            DriverManager.println( e.getClass().getName() + " - " + e.getMessage() );
        }
    }

    /**
     * Creates a new pool driver.
     */
    public Driver() { }

    /**
     * Determines whether this driver can accept create connections for the supplied url.
     * Returns true if it can accept connections, false otherwise.
     *
     * @param url
     * @return boolean
     */
    @Override
    public boolean acceptsURL(String url)
    {
        if (url == null) return false;
        else return url.startsWith("jdbc:zitego:pool");
    }

    /**
     * Returns a connection for the supplied url using information in the supplied properties.
     *
     * @param properties
     */
    @Override
    public java.sql.Connection connect(String url, Properties properties) throws SQLException
    {
        //See if we can connect to this url
        if ( !acceptsURL(url) ) return null;

        // Get the name of the pool to connect to from the url.
        String poolName = url.substring(url.lastIndexOf(":") + 1);
        try
        {
            return ConnectionFactory.getInstance().getConnection(poolName);
        }
        catch (Exception e)
        {
            throw new SQLException("Driver.connect()..." + e.toString() + " -> " + url);
        }
    }

    @Override
    public int getMajorVersion()
    {
        return 1;
    }

    @Override
    public int getMinorVersion()
    {
        return 0;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String string, Properties properties) throws SQLException
    {
        DriverPropertyInfo info[] = new DriverPropertyInfo[0];
        return info;
    }

    @Override
    public boolean jdbcCompliant()
    {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}