package com.zitego.sql;

import java.sql.SQLException;

/**
 * A simple interface for any class that stores data in the database.
 *
 * @author John Glorioso
 * @version $Id: DatabaseUpdater.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public interface DatabaseUpdater
{
    /**
	 * Initializes the subscriber's information from the database.
	 *
	 * @throws SQLException
	 * @throws NoDataException
	 */
	public void init() throws SQLException, NoDataException;

	/**
	 * Saves the Subscriber's information. If they have an id, it will update. If
	 * not then it will insert.
	 *
	 * @throws SQLException
	 */
	public void save() throws SQLException;
}