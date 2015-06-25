package com.zitego.util;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * A generic base for storing properties. This is an abstract class that must
 * be implemented.
 *
 * @author John Glorioso
 * @version $Id: PropertyStore.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public abstract class PropertyStore extends Hashtable
{
    /**
     * Creates a new set of webapp properties.
     */
    public PropertyStore()
    {
        super();
    }

    /**
     * Sets a property keyed by a string name.
     *
     * @param key The property key.
     * @param obj The object to store.
     * @throws IllegalArgumentException if the key is null.
     */
    public void setProperty(String key, Object obj)
    {
        if (key == null) throw new IllegalArgumentException("Property key cannot be null");
        super.put(key, obj);
    }

    /**
     * Returns the object specified by the given key.
     *
     * @param key The key of the property to return.
     * @return Object
     * @throws IllegalArgumentException if the key is null.
     */
    public Object getProperty(String key)
    {
        if (key == null) throw new IllegalArgumentException("Property key cannot be null");
        else return super.get(key);
    }

    /**
     * Removes the object associated with the specified key.
     *
     * @param key The key of the property to remove.
     * @throws IllegalArgumentException if the key is null.
     */
    public void removeProperty(String key)
    {
        if (key == null) throw new IllegalArgumentException("Property key cannot be null");
        super.remove(key);
    }

    /**
     * Removes all properties.
     */
    public void removeAllProperties()
    {
        super.clear();
    }

    /**
     * Returns whether or not a property exists with the given name.
     *
     * @param key The key of the property to check.
     * @return boolean
     */
    public boolean hasProperty(String key)
    {
        if (key == null) return false;
        else return super.containsKey(key);
    }

    /**
     * Loads properties from an InputStream.
     *
     * @param in The inputStream from which to load the properties.
     * @throws IOException when an error occurs processing the stream.
     */
    public void loadProperties(InputStream in) throws IOException
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
    public String printProperties()
    {
        StringBuffer ret = new StringBuffer();
        ret.append( "Internal hashcode: "+this.hashCode() ).append("\r\n")
           .append( "Classloader: "+getClass().getClassLoader().hashCode() ).append("\r\n");
        for (Iterator i=getPropertyNames().iterator(); i.hasNext();)
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
    public Set getPropertyNames()
    {
        return super.keySet();
    }
}