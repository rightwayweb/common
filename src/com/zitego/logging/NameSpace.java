package com.zitego.logging;

/**
 * This is a package level class used with the Logger to identify namespace by
 * classloader. By default, the java.util.logging package creates loggers by
 * namespace under the System class loader. This does not allow for different
 * loggers in the same namespace run under the same jvm under different class
 * loaders. For example, Apache's tomcat web application container allows one
 * to run multiple web applications in their own context under the same jvm.
 * The behavior of the java Logger class negates this. To remedy the situation,
 * this class is used to simply identify the class loader that loads the logger
 * and appends the hex hashcode of the class loader for this class to the name
 * after a dot, extending any namespace dot notation that was given.
 *
 * @author John Glorioso
 * @version $Id: NameSpace.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
class NameSpace
{
    private static String _id;

    /**
     * Given a logger namespace, this will append it's own hex hashcode name
     * to it. If the hex hashcode is already appended, it returns the unaltered
     * name. If the name is null, then null is returned.
     *
     * @param String The namespace.
     * @return String
     */
    static String getName(String in)
    {
        if (in == null) return null;
        if (_id == null)
        {
            _id = Integer.toHexString( NameSpace.class.getClassLoader().hashCode() );
        }
        if ( in.endsWith(_id) ) return in;
        else return in + "." + _id;
    }

    /**
     * Returns the original logger namespace of the given namespace. If the given namespace
     * has this NameSpace's id appended to the end of it, it will be stripped and returned.
     * If it does not, then the name will be returned as is. If the given name is null, then
     * null is returned/
     *
     * @param String The namespace.
     * @return String
     */
    static String getOriginalName(String in)
    {
        if (in == null) return null;
        if (_id == null)
        {
            _id = Integer.toHexString( NameSpace.class.getClassLoader().hashCode() );
        }
        if ( in.endsWith(_id) ) return in.substring( 0, in.indexOf(_id) );
        else return in;
    }
}