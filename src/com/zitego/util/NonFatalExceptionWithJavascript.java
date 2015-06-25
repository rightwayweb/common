package com.zitego.util;

/**
 * This extends NonFatalException to include some javascript function that should be
 * performed within the error page.
 *
 * @author John Glorioso
 * @version $Id: NonFatalExceptionWithJavascript.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class NonFatalExceptionWithJavascript extends com.zitego.util.NonFatalException
{
    /** To hold the javascript to execute. */
    protected String _javascript;

    public NonFatalExceptionWithJavascript(String msg, String js)
    {
        super(msg);
        _javascript = js;
    }

    public String getJavascript()
    {
        return _javascript;
    }
}