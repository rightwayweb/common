package com.zitego.util;

/**
 * A generic exception to be used to timeouts.
 *
 * @author John Glorioso
 * @version $Id: TimeoutException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class TimeoutException extends Exception
{
    /**
     * Creates a new timeout exception with a message.
     *
     * @param String The message.
     */
    public TimeoutException(String msg)
    {
        super(msg);
    }
}