package com.zitego.pool;

import java.util.StringTokenizer;

/**
 * This class is used to hold information about an object pool's configuration. The properties
 * that are required to be set are minObjects, maxObjectx, and increment. The rest are optional
 * or have a default value as described below.
 *
 * @author John Glorioso
 * @version $Id: ObjectPoolConfig.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class ObjectPoolConfig implements Cloneable
{
    /** The maximum number of obejcts that the pool will create. */
    public int maxObjects;
    /** The minimum number of objects that the pool will contain. This is different from the number of objects
      * that are available. Also the pool will create this number of objects as soon as it is created. */
    public int minObjects;
    /** The number of objects that are created each time a new object is needed and none are available. */
    public int increment;
    /** The number of times an object can be used from the pool before it is automatically expired, -1
      * indicates never expire based on usage. */
    public int usecount = -1;
    /** The amount of time an object can go unused before it is destroyed and re-created, a value of -1
      * indicates that objects never expire. */
    public long expirationTime = -1l;
    /** The amount of time an object can be in use before it is destroyed and re-created, a value of -1
      * indicates that objects never expire. */
    public long maxUseTime = -1l;
    /** The amount of time an object can go unused before it is destroyed in order to shrink the pool.
      * Objects destroyed to shrink the pool are not recreated automatically. */
    public long shrinkDelay = 0;
    /** Whether or not to display verbose debug information. */
    public boolean debug = false;
    /** Whether or not to allow the pool to shrink to it's minimum size (or the shrink floor size, whichever is larger). */
    public boolean allowShrinking = false;
    /** Limit the size the pool can shrink to (only honored if greater than minObjects). This exists primarily
      * to allow for a pool to be initially created empty, but not shrink back to 0. */
    public int shrinkFloor;
    /** Enables stack tracing for in use objects. */
    public boolean enableTracing = false;
    /** Alert threshold. When the pool has reached this percentage of it's max size, the admin specified by
      * <alert email> will be notified. */
    public double alertThreshold = 100;
    /** Alert interval. The interval (in seconds) in which alerts will be sent. Default is 15 minutes (900 seconds). */
    public long alertInterval = 900;
    /** Alert admin. The email address to send an alert to when the pool is in trouble. */
    public String alertAdmin = null;
    /** The SMTP mail server to use to send the alert email (either this or mailCommand must be specified for
      * alerts to be delivered). */
    public String mailServer = null;
    /** The command line to which to spool email (either this or mailServer must be specified for alerts to be delivered). */
    public String mailCommand = null;
    /** Whether or not to alert the admin when the pool is expanded. */
    public boolean alertOnExpand = false;
    /** The level of debug messages that are displayed. A value of 0 indicates normal, 1 is intense. */
    public int debugLevel = 0;
    /** Should calls to get an object from the pool block or fail when the pool is maxed out. */
    public boolean block = false;

    /**
     * Creates a config object by parsing the supplied string. The string should be a comma or space
     * separated set of name/value pairs. The names that are required are: max, min, and increment.
     * Optional values are: usecount, expirationTime (in ms), and maxUseTime (in ms).<br>
     * <br>
     * For example:<br>
     * <br>
     * max=10,min=1,increment=2
     *
     * @param String The config string.
     * @return ObjectPoolConfig
     */
    public static ObjectPoolConfig parse(String config)
    {
        return parse(null, config);
    }

    /**
     * Create a config object by parsing the supplied string and inheriting any
     * values not provided from the base config supplied. The string should be a comma or space
     * separated set of name/value pairs. The names that are required are: max, min, and increment.
     * Optional values are: usecount, expirationTime (in ms), and maxUseTime (in ms).<br>
     * <br>
     * For example:<br>
     * <br>
     * max=10,min=1,increment=2
     *
     * @param ObjectPoolConfig The base config from which to inherit any values not provided in
     *                         config. (It will NOT be altered).
     * @param String The comma delimited config string.
     * @return ObjectPoolConfig
     */
    public static ObjectPoolConfig parse(ObjectPoolConfig base, String config)
    {
        ObjectPoolConfig oconfig = base == null ? new ObjectPoolConfig() : base.getCopy();

        StringTokenizer spaceTokens = new StringTokenizer(config, " ");
        while (spaceTokens.hasMoreTokens())
        {
            StringTokenizer commaTokens = new StringTokenizer(spaceTokens.nextToken(), ",");
            while ( commaTokens.hasMoreTokens() )
            {
                String token = commaTokens.nextToken();
                StringTokenizer nv = new StringTokenizer(token, "=");
                String name = nv.nextToken();
                if ( "max".equals(name) ) oconfig.maxObjects = Integer.parseInt( nv.nextToken() );
                else if ( "min".equals(name) ) oconfig.minObjects = Integer.parseInt( nv.nextToken() );
                else if ( "increment".equals(name) ) oconfig.increment = Integer.parseInt( nv.nextToken() );
                else if ( "usecount".equals(name) ) oconfig.usecount = Integer.parseInt( nv.nextToken() );
                else if ( "expirationTime".equals(name) ) oconfig.expirationTime = Long.parseLong( nv.nextToken() );
                else if ( "maxUseTime".equals(name) ) oconfig.maxUseTime = Long.parseLong( nv.nextToken() );
                else if ( "debug".equals(name) )
                {
                    String d = nv.nextToken();
                    if ( "1".equals(d) || "true".equals(d) ) oconfig.debug = true;
                }
                else if ( "allowshrinking".equals(name) )
                {
                    String d = nv.nextToken();
                    if ( "1".equals(d) || "true".equals(d) ) oconfig.allowShrinking = true;
                }
                else if ( "shrinkdelay".equals(name) ) oconfig.shrinkDelay = Long.parseLong( nv.nextToken() );
                else if ( "shrinkfloor".equals(name) ) oconfig.shrinkFloor = Integer.parseInt( nv.nextToken() );
                else if ( "alertthreshold".equals(name) ) oconfig.alertThreshold = Double.parseDouble( nv.nextToken() );
                else if ( "alertinterval".equals(name) ) oconfig.alertInterval = Long.parseLong( nv.nextToken() );
                else if ( "alertadmin".equals(name) ) oconfig.alertAdmin = nv.nextToken();
                else if ( "mailserver".equals(name) ) oconfig.mailServer = nv.nextToken();
                else if ( "mailcommand".equals(name) ) oconfig.mailCommand = nv.nextToken();
                else if ( "enabletracing".equals(name) )
                {
                    String d = nv.nextToken();
                    if ( "1".equals(d) || "true".equals(d) ) oconfig.enableTracing = true;
                }
                else if ( "alertonexpand".equals(name) )
                {
                    String d = nv.nextToken();
                    if ( "1".equals(d) || "true".equals(d) ) oconfig.alertOnExpand = true;
                }
                else if ( "debuglevel".equals(name) ) oconfig.debugLevel = Integer.parseInt( nv.nextToken() );
                else if ( "block".equals(name) )
                {
                    String d = nv.nextToken();
                    if ( "1".equals(d) || "true".equals(d) ) oconfig.block = true;
                }
            }
        }
        return oconfig;
    }

    public String toString()
    {
        if (debug)
        {
            StringBuffer out = new StringBuffer()
            .append("ObjectPoolConfig->[alertAdmin=").append(alertAdmin)
            .append(";alertInterval=").append(alertInterval)
            .append(";alertOnExpand=").append(alertOnExpand)
            .append(";alertThreshold=").append(alertThreshold)
            .append(";allowShrinking=").append(allowShrinking)
            .append(";shrinkDelay=").append(shrinkDelay)
            .append(";shrinkFloor=").append(shrinkFloor)
            .append(";debug=").append(debug)
            .append(";debugLevel=").append(debugLevel)
            .append(";enableTracing=").append(enableTracing)
            .append(";expirationTime=").append(expirationTime)
            .append(";maxUseTime=").append(maxUseTime)
            .append(";increment=").append(increment)
            .append(";mailCommand=").append(mailCommand)
            .append(";mailServer=").append(mailServer)
            .append(";maxObjects=").append(maxObjects)
            .append(";minObjects=").append(minObjects)
            .append(";useCount=").append(usecount)
            .append(";block=").append(usecount);
            return out.toString();
        }
        return super.toString();
    }


    /**
     * Get a copy of this ObjectPoolConfig.
     *
     * @return ObjectPoolConfig
     */
    public ObjectPoolConfig getCopy()
    {
        try
        {
            return (ObjectPoolConfig)super.clone();
        }
        catch (CloneNotSupportedException cne)
        {
            return null;
        }
    }
}