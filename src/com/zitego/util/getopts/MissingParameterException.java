package com.zitego.util.getopts;

/**
 * Exception thrown when an option that has required parameters, but none
 * were encountered on the command line.
 *
 * @version $Id: MissingParameterException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class MissingParameterException extends GetOptException
{
    /**
     * Constructor with message argument.
     * @param msg the message to display
     */
    public MissingParameterException(String msg)
    {
        super(msg);
    }
}