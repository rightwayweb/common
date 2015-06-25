package com.zitego.util;

/**
 * An exception to be used when something is not found.
 *
 * @author John Glorioso
 * @version $Id: NotFoundException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class NotFoundException extends Exception
{
    /**
     * Constructs an empty exception.
     */
    public NotFoundException()
    {
        super();
    }

    /**
     * Constructs a new exception with the specified error message.
     *
     * @param String The error message.
     */
    public NotFoundException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new not found exception with the specified error message and a cause.
     *
     * @param String The error message.
     * @param Throwable The cause.
     */
    public NotFoundException(String err, Throwable cause)
    {
        super(err, cause);
    }
}
