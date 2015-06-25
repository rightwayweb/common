package com.zitego.util;

/**
 * A class that holds login exception messages.
 *
 * @author John Glorioso
 * @version $Id: InvalidLoginException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class InvalidLoginException extends NonFatalException
{
    /**
     * Constructs an empty exception.
     */
    public InvalidLoginException()
    {
        super();
    }

    /**
     * Constructs a new exception with the specified error message.
     *
     * @param String The error message.
     */
    public InvalidLoginException(String err)
    {
        super(err);
    }

    /**
     * Constructs a new exception with the specified error message and a cause.
     *
     * @param String The error message.
     * @param Throwable The cause.
     */
    public InvalidLoginException(String err, Throwable cause)
    {
        super(err, cause);
    }
}