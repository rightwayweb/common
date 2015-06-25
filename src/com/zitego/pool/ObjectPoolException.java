package com.zitego.pool;

/**
 * A generic class for object pool exceptions.
 *
 * @author John Glorioso
 * @version $Id: ObjectPoolException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class ObjectPoolException extends RuntimeException
{
	/**
	 * Constructs a new <code>ObjectPoolException</code>.
	 */
	public ObjectPoolException()
	{
		super();
	}

	/**
	 * Constructs a new <code>ObjectPoolException</code> with the specified error message.
	 *
	 * @param String The error message.
	 */
	public ObjectPoolException(String err)
	{
		super(err);
	}
}