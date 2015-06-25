package com.zitego.util.getopts;

/**
 * Exception thrown when an unknown option was encountered from the
 * command line.
 *
 * @version $Id: OptionNotFoundException.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class OptionNotFoundException extends GetOptException
{
    /**
     * Constructor with message argument.
     * @param msg the message to display
     */
    public OptionNotFoundException(String msg)
    {
        super(msg);
    }
}