package com.zitego.util;

/**
 * A base exception to deliver non fatal error messages.
 *
 * @author John Glorioso
 * @version $Id: NonFatalException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class NonFatalException extends Exception
{
    /**
     * Constructs an empty exception.
     */
    public NonFatalException()
    {
        super();
    }

    /**
     * Constructs a new exception with the specified error message.
     *
     * @param String The error message.
     */
    public NonFatalException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new non fatal exception with the specified error message and a cause.
     *
     * @param String The error message.
     * @param Throwable The cause.
     */
    public NonFatalException(String err, Throwable cause)
    {
        super(err, cause);
    }
}
