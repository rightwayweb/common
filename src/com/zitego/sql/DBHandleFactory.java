package com.zitego.sql;

import com.zitego.util.StaticProperties;
import java.sql.Driver;

/**
 * A factory for building database handles. There are three ways to get a DBHandle back.
 * One is to call getDBHandle() and get back a handle for whatever DBConfig is stored
 * in the StaticProperties object as DEFAULT_DBCONFIG_KEY (or null if none). The second
 * is to pass in a DBConfig object to get it. The third is to pass in the name of the
 * DBConfig key.
 *
 * @author John Glorioso
 * @version $Id: DBHandleFactory.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DBHandleFactory
{
    /** The default name of the DBConfig key in StaticProperties. */
    public static final String DEFAULT_DBCONFIG_KEY = "db.config";

    /**
     * Returns a db handle by looking for a DBConfig object in the StaticProperties called
     * DEFAULT_DBCONFIG_KEY.
     *
     * @return DBHandle
     */
    public static DBHandle getDBHandle()
    {
        return getDBHandle( (String)null );
    }

    /**
     * Returns a db handle by looking for a DBConfig object in the StaticProperties with
     * the given name.
     *
     * @param name The config name.
     * @return DBHandle
     */
    public static DBHandle getDBHandle(String name)
    {
        if (name == null) name = DEFAULT_DBCONFIG_KEY;
        return getDBHandle( (DBConfig)StaticProperties.getProperty(name) );
    }

    /**
     * Returns a handle given the supplied configuration.
     *
     * @param config The configuration.
     * @return DBHandle
     * @throws IllegalArgumentException if config is null or represents an unsupported database.
     */
    public static DBHandle getDBHandle(DBConfig config)
    {
        if (config == null) throw new IllegalArgumentException("config cannot be null in getDBHandle()");

        switch ( config.getType() )
        {
            case DBConfig.MYSQL:
                return new MysqlDBHandle(config);
            case DBConfig.SQL_SERVER:
                return new SQLServerDBHandle(config);
            default:
                throw new IllegalArgumentException("the requested database type is not support by DBHandleFactory");
        }
    }
}