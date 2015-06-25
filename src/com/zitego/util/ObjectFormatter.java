package com.zitego.util;

import java.lang.reflect.Method;
import java.text.Format;

/**
 * This is a static utility class that handles formatting an object to a
 * string value. This can be done with an optional Format class. A default
 * value can be specified. If one is not, an empty string is returned if
 * the object is null. Additionally, you can specify a string of methods
 * to call on the provided object to retrieve the actual object to format.
 * If any of the objects along the way returned are null, then the default
 * value is returned. Ex on a com.zitego.customer.Customer object:<br>
 * <code>"getContactInfo.getFirstName"</code> will check to see if the
 * provided customer is null, if not it will attempt to call "getContactInfo".
 * If the object returned from that is not null, then "getFirstName" will
 * be called. The above mentioned logic will be performed on the first name
 * returned.
 *
 * @author John Glorioso
 * @version $Id: ObjectFormatter.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class ObjectFormatter
{
    /**
     * Formats the given object with no default value.
     *
     * @param Object The object to be formatted.
     * @return String
     */
    public static String format(Object obj)
    {
        return format(obj, null);
    }

    /**
     * Formats the given object with a default value. If the default
     * value is null, then an empty string is returned.
     *
     * @param Object The object to be formatted.
     * @param String The default value.
     * @return String
     */
    public static String format(Object obj, String def)
    {
        return format(obj, def, null);
    }

    /**
     * Formats the given object with a default value and a Format
     * class. If the default value is null, then an empty string is
     * returned.
     *
     * @param Object The object to be formatted.
     * @param String The default value.
     * @param Format The format class.
     * @return String
     */
    public static String format(Object obj, String def, Format formatter)
    {
        return format(obj, def, formatter, null);
    }

    /**
     * Formats the given object with a default value, a Format class,
     * and a String describing the methods to call on the object. The
     * String must be in the following format: <method1>.<method2>...
     * The string is case sensitive. If the methods are invalid an
     * RuntimeException is thrown. If any object along the way
     * is null, then the default value is returned. If the default value
     * is null, then an empty string is returned.
     *
     * @param Object The object to be formatted.
     * @param String The default value.
     * @param Format The format class.
     * @param String The methods to call.
     * @return String
     * @throws RuntimeException if the argument string is null.
     */
    public static String format(Object obj, String def, Format formatter, String methods)
    throws RuntimeException
    {
        if (def == null) def = "";

        if (obj == null)
        {
            return def;
        }
        else
        {
            //Check to see if we have methods to call on the object
            if (methods != null && methods.length() > 0)
            {
                int index = methods.indexOf(".");
                String method = methods;
                if (index > -1)
                {
                    method = methods.substring(0, index);
                    methods = methods.substring(index+1).trim();
                }
                else
                {
                    methods = null;
                }
                Class objectClass = obj.getClass();
                try
                {
                    Method m = objectClass.getMethod(method, (Class[])null);
                    obj = m.invoke(obj, (Object[])null);
                    if (obj == null) return def;
                }
                catch (Exception e)
                {
                    throw new RuntimeException("An error occurred processing the method string: "+methods, e);
                }
                //See if there are more methods to call
                if (methods != null) return format(obj, def, formatter, methods);
            }
            //See if we are supposed to format
            if (formatter != null) return formatter.format(obj);
        }
        //If we got here then we just need to return the object as a string
        return obj.toString();
    }
}