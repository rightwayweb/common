package com.zitego.util;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents static cached properties that are loaded from
 * an input stream. <b>WARNING</b>! Anything stored in this class is
 * accessible and changable for <u>any</u> class running under the
 * same virtual machine in the <u>same</u> classloader. In the servlet
 * world, if you have two web applications running under the same container,
 * and this class appears in the system class path, when then they both call
 * the removeAllProperties() method before loading their own, one will wipe
 * out the other's set property. In addition, if both applications set a
 * property (i.e. db.config, the second one to set will be the one that stays.
 * To alleviate this problem, you need to make sure that this class is in the
 * lib directory or local classpath to the web application.
 *
 * @author John Glorioso
 * @version $Id: StaticProperties.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class StaticProperties
{
    /** To hold the static properties keyed by a String name. */
    protected static HashMap _props = new HashMap();

    /**
     * Sets a property keyed by a string name.
     *
     * @param String The name of the property.
     * @param obj The object to store.
     * @throws IllegalArgumentException if the key is null.
     */
    public static void setProperty(String key, Object obj)
    {
        if (key == null) throw new IllegalArgumentException("Property key cannot be null");
        _props.put(key, obj);
    }

    /**
     * Returns the object specified by the given key.
     *
     * @param String The name of the property to return.
     * @return Object
     * @throws IllegalArgumentException if the key is null.
     */
    public static Object getProperty(String key)
    {
        if (key == null) throw new IllegalArgumentException("Property key cannot be null");
        return _props.get(key);
    }

    /**
     * Removes the object associated with the specified key.
     *
     * @param String The property to remove.
     * @throws IllegalArgumentException if the key is null.
     */
    public static void removeProperty(String key)
    {
        if (key == null) throw new IllegalArgumentException("Property key cannot be null");
        _props.remove(key);
    }

    /**
     * Removes all properties.
     */
    public static void removeAllProperties()
    {
        _props.clear();
    }

    /**
     * Returns whether or not a property exists with the given name.
     *
     * @param String The name.
     * @return boolean
     */
    public static boolean hasProperty(String name)
    {
        return _props.containsKey(name);
    }

    /**
     * Loads properties from an InputStream.
     *
     * @param InputStream The inputStream from which to load the properties.
     * @throws IOException when an error occurs processing the stream.
     */
    public static void loadProperties(InputStream in) throws IOException
    {
        Properties props = new Properties();
        props.load(in);

        for (Enumeration e=props.propertyNames(); e.hasMoreElements();)
        {
            String key = (String)e.nextElement();
            setProperty( key, props.getProperty(key) );
        }
    }

    /**
     * Prints out the properties of this object in key=object fashion with each
     * name value pair on its own line. The toString() method is called on each object to
     * print it out. This will print out the properties in no particular order.
     *
     * @return String
     */
    public static String printProperties()
    {
        StringBuffer ret = new StringBuffer();
        ret.append( "Internal hashcode: "+_props.hashCode() ).append("\r\n")
           .append( "Classloader: "+StaticProperties.class.getClassLoader().hashCode() ).append("\r\n");
        for (Iterator i=_props.keySet().iterator(); i.hasNext();)
        {
            String key = (String)i.next();
            ret.append(key).append("=").append( getProperty(key) )
               .append("\r\n");
        }
        return ret.toString();
    }

    /**
     * Returns the property names.
     *
     * @return Set
     */
    public static Set getPropertyNames()
    {
        return _props.keySet();
    }
}