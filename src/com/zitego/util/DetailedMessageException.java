package com.zitego.util;

/**
 * This represents an exception that has detailed information that can be retrieved
 * via the getDetailedMessage() method or the toString method.
 *
 * @author John Glorioso
 * @version $Id: DetailedMessageException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DetailedMessageException extends Exception
{
    /** The detailed message. */
    private String _detailedMessage;

    /**
     * Creates a new exception with a message.
     *
     * @param String The message.
     */
    public DetailedMessageException(String msg)
    {
        this(msg, (String)null);
    }

    /**
     * Creates a new exception with a message.
     *
     * @param String The message.
     * @param String The detailed message.
     */
    public DetailedMessageException(String msg, String detailedMessage)
    {
        super(msg);
        _detailedMessage = detailedMessage;
    }

    /**
     * Creates a new exception with a message and root cause.
     *
     * @param String The message.
     * @param Throwable The root cause.
     */
    public DetailedMessageException(String msg, Throwable cause)
    {
        this(msg, null, cause);
    }

    /**
     * Creates a new exception with a message and root cause.
     *
     * @param String The message.
     * @param String The detailed message.
     * @param Throwable The root cause.
     */
    public DetailedMessageException(String msg, String detailedMessage, Throwable cause)
    {
        super(msg, cause);
        _detailedMessage = detailedMessage;
    }

    /**
     * Sets the detailed message.
     *
     * @param String The detailed message.
     */
    public void setDetailedMessage(String msg)
    {
        _detailedMessage = msg;
    }

    /**
     * Returns the detailed message.
     *
     * @return String
     */
    public String getDetailedMessage()
    {
        return _detailedMessage;
    }

    /**
     * Overrides toString in order to append the detailed message if any.
     *
     * @return String
     */
    public String toString()
    {
        if (_detailedMessage != null) return super.toString() + " [" + _detailedMessage + "]";
        else return super.toString();
    }
}