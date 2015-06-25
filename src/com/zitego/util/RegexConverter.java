package com.zitego.util;

import com.zitego.util.getopts.*;
import java.util.regex.*;

/**
 * This class will take a perl style regular expression and convert it to the escaped java
 * String version. The best way to use this class is to feed it a regex from the command line.
 *
 * @author John Glorioso
 * @version $Id: RegexConverter.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class RegexConverter
{
    public static void main(String[] args) throws Exception
    {
        GetOpts opts = new GetOpts(new String[] { "pattern:" }, args, GetOpts.OPTION_CASE_INSENSITIVE);
        String pattern = null;
        try
        {
            int index = -1;
            while ( (index=opts.getOptions()) != -1 )
            {
                String arg = opts.getOptionString(index);
                String value = opts.getOptarg();
                if ( "pattern".equals(arg) )
                {
                    pattern = value;
                }
                else
                {
                    System.out.println("*** WARNING *** Ignoring invalid argument: " + arg);
                }
            }
            if (pattern == null) throw new MissingParameterException("Missing required parameter \"-pattern\".");
        }
        catch (Exception e)
        {
            System.err.println( e.getMessage() );
            System.err.println("Usage: java com.zitego.util.RegexConverter -pattern <regex>");
            System.exit(-1);
        }
        System.out.println( "Pattern: "+pattern );
        System.out.println( "Escaped: "+convert(pattern) );
    }

    /**
     * Converts the given regular expression into the Java String equivalent.
     *
     * @param String The pattern to convert.
     * @return String
     */
    public static String convert(String in)
    {
        Pattern p = Pattern.compile(in);
        char[] chars = p.pattern().toCharArray();
        StringBuffer ret = new StringBuffer();
        for (int i=0; i<chars.length; i++)
        {
            if (chars[i] == '\\') ret.append("\\");
            ret.append(chars[i]);
        }
        return ret.toString();
    }
}