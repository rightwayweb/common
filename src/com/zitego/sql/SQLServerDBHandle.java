package com.zitego.sql;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 * A sql server database handle.
 *
 * @author John Glorioso
 * @version $Id: SQLServerDBHandle.java,v 1.2 2009/02/17 02:31:26 jglorioso Exp $
 */
public class SQLServerDBHandle extends DBHandle
{
    /**
     * Creates a new sql server db handle with a config object.
     *
     * @param config The db config.
     * @throws IllegalArgumentException if the config object is invalid.
     */
    public SQLServerDBHandle(DBConfig config) throws IllegalArgumentException
    {
        super(config);
    }

    /**
     * Returns the string SQLServer.
     *
     * @return String
     */
    public String getDBType()
    {
        return "SQLServer";
    }

    /**
     * Returns the last id inserted by the current connection.
     *
     * @param name This is not used.
     * @return long
     * @throws SQLException if a problem occurs.
     */
    public long getLastId(String name) throws SQLException
    {
        connect();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try
        {
            pst = prepareStatement("SELECT @@identity");
            rs = pst.executeQuery();
            if ( !rs.next() ) throw new SQLException("An error occurred retrieving the last id inserted.");
            return rs.getLong(1);
        }
        finally
        {
            disconnect();
        }
    }

    public int getDBCursorType()
    {
        return 0;
    }

    /**
     * This Method can not be utilized for MySQL it is only here as a requirement of implementation.
     */
    public long getNextId(String name) throws SQLException
    {
        throw new SQLException("getNextId() can not be called when for a MySQL database.");
    }
}