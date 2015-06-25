package com.zitego.mail;

import com.zitego.util.StaticProperties;

/**
 * This is a basic reference class to test email addresses for legitimacy. The static string
 * DEFAULT_REGEX is used when the getEmailRegexp method is called unless a property named
 * "email.regex" is stored in the StaticProperties class. This must be manually inserted at
 * some point.
 *
 * @author John Glorioso
 * @version $Id: EmailAddress.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class EmailAddress
{
    /** The default email regular expression. Currently, this is
        ^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z0-9\-]+\.)+([a-zA-Z]{2,4})))$ */
    public static final String DEFAULT_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z0-9\\-]+\\.)+([a-zA-Z]{2,4})))$";
    /** The email regular expression. The default is used if this is null. */
    private String _regex;

    /**
     * Returns the valid email regular expression. This will return the DEFAULT_REGEX unless
     * the static property "email.regex" is set in StaticProperties.
     *
     * @return String
     */
    public static String getEmailRegexp()
    {
        String regex = (String)StaticProperties.getProperty("email.regex");
        if (regex == null) regex = DEFAULT_REGEX;
        return regex;
    }

    /**
     * Returns whether or not the supplied email address is valid.
     *
     * @param String The email to check.
     * @return boolean
     */
    public static boolean isValidEmail(String email)
    {
        if (email == null) return false;
        else return email.matches( getEmailRegexp() );
    }
}