package com.zitego.util.getopts;

/**
 * A base class for GetOpts exceptions.
 *
 * @version $Id: GetOptException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class GetOptException extends RuntimeException
{
	public GetOptException()
	{
		super();
	}

	/**
	 * Constructor with message argument.
	 * @param msg the message to display
	 */
	public GetOptException(String msg)
	{
		super(msg);
	}
}