package com.zitego.sql;

/**
 * Represents an exception to be thrown when a duplicate constraint is violated
 * in the database. For example, unique usernames and such.
 *
 * @author John Glorioso
 * @version $Id: DuplicateException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DuplicateException extends Exception
{
	/**
	 * Contructs a new exception with the default message.
	 */
	public DuplicateException()
	{
		super();
	}

	/**
	 * Contructs a new exception with the specified error message.
	 *
 	 * @param String The error message.
 	 */
	public DuplicateException(String err)
	{
		super(err);
	}
}
