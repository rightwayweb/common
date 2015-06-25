package com.zitego.sql;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 * This class handles DB connectivity to an MySQL Database.
 *
 * @author John Glorioso
 * @version $Id: MysqlDBHandle.java,v 1.3 2009/05/05 01:43:03 jglorioso Exp $
 */
public class MysqlDBHandle extends DBHandle
{
	/**
	 * Creates a new MysqlDBHandle with the supplied configuration.
	 *
	 * @param DBConfig The parameters to use to connect to the database.
	 * @throws IllegalArgumentException if config is null.
	 */
	public MysqlDBHandle(DBConfig config) throws IllegalArgumentException
	{
		super(config);
	}

	/**
	 * Returns the word "MySQL".
	 *
	 * @return String
	 */
	public String getDBType()
	{
		return "MySQL";
	}

	/**
	 * Retrieves the last id by obtaining the last id inserted by this connection.
	 * Must be called after connect is called.
	 *
	 * @param String Not used.
	 * @return long
	 * @throws SQLException when a DB error occurs.
	 */
	public long getLastId(String name) throws SQLException
	{
		PreparedStatement pst = prepareStatement("SELECT last_insert_id()");
		ResultSet rs = pst.executeQuery();
		if ( !rs.next() ) throw new SQLException("An error occurred retrieving the last id inserted.");
		return rs.getLong(1);
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