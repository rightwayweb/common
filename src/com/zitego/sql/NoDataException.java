package com.zitego.sql;

/**
 * An exception that can be thrown when data is expected, but none is available.
 *
 * @author John Glorioso
 * @version $Id: NoDataException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class NoDataException extends Exception
{
	public NoDataException(String msg)
	{
		super(msg);
	}
}