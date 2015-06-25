package com.zitego.pool;

/**
 * An exception for when the pool is in use and it is trying to be illegally accessed.
 *
 * @author John Glorioso
 * @version $Id: PoolInUseException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class PoolInUseException extends RuntimeException
{
	/**
	 * Creates a new <code>PoolInUseException</code>.
	 */
	public PoolInUseException()
	{
		super();
	}

	/**
	 * Creates a new <code>PoolInUseException</Code> with the specified error message.
	 *
	 * @param String The error message.
	 */
	public PoolInUseException(String err)
	{
		super(err);
	}
}