package com.zitego.logging;

/**
 * A class for when trying to log to a non-existant handler.
 *
 * @author John Glorioso
 * @version $Id: NoSuchHandlerException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class NoSuchHandlerException extends RuntimeException
{
	/**
	 * Constructs a new <code>NoSuchHandlerException</code>.
	 */
	public NoSuchHandlerException()
	{
		super();
	}

	/**
	 * Constructs a new <code>NoSuchHandlerException</code> with the specified error message.
	 *
	 * @param String The error message.
	 */
	public NoSuchHandlerException(String err)
	{
		super(err);
	}
}