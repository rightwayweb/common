package com.zitego.util;

/**
 * A base exception to deliver non fatal error messages for a webapp.
 *
 * @author John Glorioso
 * @version $Id: NonFatalWebappException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class NonFatalWebappException extends NonFatalException
{
    /** The url to forward to. */
    private String _forwardUrl;

    /**
     * Constructs an empty exception.
     */
    public NonFatalWebappException()
    {
        super();
    }

    /**
     * Constructs a new exception with the specified error message.
     *
     * @param String The error message.
     */
    public NonFatalWebappException(String msg)
    {
        this(msg, (String)null);
    }

    /**
     * Constructs a new non fatal exception with the specified error message and a cause.
     *
     * @param String The error message.
     * @param Throwable The cause.
     */
    public NonFatalWebappException(String err, Throwable cause)
    {
        this(err, null, cause);
    }

    /**
     * Constructs a new exception with the specified error message and forward url.
     *
     * @param String The error message.
     * @param String The url to forward to.
     */
    public NonFatalWebappException(String msg, String forwardUrl)
    {
        super(msg);
        _forwardUrl = forwardUrl;
    }

    /**
     * Constructs a new non fatal exception with the specified error message and a cause.
     *
     * @param String The error message.
     * @param String The url to forward to.
     * @param Throwable The cause.
     */
    public NonFatalWebappException(String err, String forwardUrl, Throwable cause)
    {
        super(err, cause);
        _forwardUrl = forwardUrl;
    }

    /**
     * Sets the forward url.
     *
     * @param String The forward url.
     */
    public void setForwardUrl(String url)
    {
        _forwardUrl = url;
    }

    /**
     * Returns the forward url.
     *
     * @return String
     */
    public String getForwardUrl()
    {
        return _forwardUrl;
    }
}