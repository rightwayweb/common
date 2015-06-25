package com.zitego.sql;

import com.zitego.util.StaticProperties;
import com.zitego.util.PropertyStore;
import com.zitego.pool.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.IOException;
import java.sql.*;

/**
 * A class for maintaing pools of JDBC connections.
 * <p>
 * Using connection pools is a simple 4 step process:
 * <ol>
 *  <li> Create the pool.
 *  <li> Obtain a connection out of the pool.
 *  <li> Use the connection.
 *  <li> Return the connection to the pool.
 * </ol>
 * <b>Step 1 - Creating a pool</b>
 * <p>
 * A connection pool can be created in 2 ways using either of the createPool methods. Most of
 * arguments to the create pool methods are self-exaplanatory. A short discussion of the not
 * so obvious follows:<br>
 * <ul>
 *  <li> testtable - testtable is an optional argument that specifies a table to use to test
 *                   connections from the pool before they are returned to the caller. The table
 *                   is tested by performing a <code>SELECT 1 FROM <i>testtable</i> WHERE 1=2</code>,
 *                   so choosing the smallest table as possible is best for performance.
 *  <li> testinterval - testinterval is an optional argument that specifies how often (in seconds)
 *                      to test the connections from the pool using <code>testtable</code>. The connection
 *                      will not be tested more often that the interval specified. It can, however, be
 *                      tested less often if a connection is not requested from the pool as often as the
 *                      interval. A <code>testinterval</code> of <code>0</code> indicates that the connections
 *                      should be tested everytime they are requested (the default behavior).
 *  <li> config - config is an object that contains the following information about the pool: max, min, and
 *                increment, usecount, expirationTime - These control the size of the connection pool.
 *                <i>min</i> defines the minimum number of connections thatwill exist in the pool. When the
 *                pool is initially created, this many connections will be opened. <i>max</i> defines the
 *                maximum number of connections that the pool can hold. Once this number of connections are in
 *                use, then an attempt to get another connection from the pool will result in an exception.
 *                <i>increment</i> defines how many connections are opened at a time. This comes into play
 *                when a request is made for a connection and there are no free connections available. In this
 *                case, <i>increment</i> number of connections are opened and added to the pool. <i>usecount</i>
 *                is an optional parameter for limiting the number of times a connection will be used before it
 *                is dropped. <i>expirationTime</i> is an optional parameter for performing automatical shrinking
 *                of the pool. If set to a value not equal to -1, then the pool will close all connections that
 *                have not been used in <i>expirationTime</i> milliseconds. The only exception to this is that
 *                the pool will always keep <i>min</i> connections open. Again, <i>usecount</i> and
 *                <expirationTime> are optional parameters.
 * </ul>
 * <p>Using all of these parameters a Pool can be created like this:</p>
 * <p>With a default config (max=10, min=1, increment=1)</p>
 * <p>
 *  <code>
 *  ConnectionFactory.getInstance().createPool("mypool", "com.mysql.jdbc.Driver", "jdbc:mysql@localhost:3306:zitego", "username", "password", "status", null);
 *  </code>
 * </p>
 * or with a custom config
 * <p>
 *  <code>
 *  ObjectPoolConfig config = new ObjectPoolConfig();<br>
 *  config.maxObjects=15;<br>
 *  config.minObjects=5;<br>
 *  config.increment=2;<br>
 *  ConnectionFactory.getInstance().createPool("mypool", "com.mysql.jdbc.Driver", "jdbc:mysql@localhost:3306:zitego", "username", "password", "status", config);
 *  </code>
 * </p>
 * <p>
 * Another way to create a pool is to provide a formatted string containing the required parameters. The string
 * is formatted as comma delimited name/value pairs, with each pair specifying parameter for the pool:
 * </p>
 * <p>
 * <code>
 * String parms = "driver=com.mysql.jdbc.Driver, url=jdbc:mysql@localhost:3306:zitego, username=myusername, password=mypassword, testtable=status, min=5, max=15, increment=2";
 * <br>
 * ConnectionFactory.getInstance().createPool("mypool",parms);
 * </code>
 * </p>
 * <p>
 * This method for creating a pool is useful for specifying pool parameters in a properties file and creating the pool in a single line of code
 * <p>
 * <b>(2) Obtain a connection out of the pool</b>
 * <p>Now that the pool has been created, we can obtain a connection from it. This too can be done in 2 ways:</p>
 * <p>
 *  (a) by directly accessing the pool
 *  <code>
 *  Connection conn = ConnectionFactory.getInstance().getConnection("mypool");
 *  </code>
 * </p>
 * <p>
 *  (b) by using the connection pool's JDBC driver. This method makes it easy to integrate connection pools with
 *      any custom database libraries
 *  <code>
 *  Connection conn = DriverManager.getConnection("jdbc:zitego:pool:mypool", "", "");
 *  </code>
 * <p>
 * <b>(3) Use the connection - use the connection as a normal JDBC connection
 * <p>(4) Return the connection to the pool - Return the connection to the pool by simply closing the connection as normal:</b>
 * <p>
 * <code>
 * conn.close();
 * </code>
 *
 * @author John Glorioso
 * @version $Id: ConnectionFactory.java,v 1.2 2009/02/16 19:04:55 jglorioso Exp $
 */
public class ConnectionFactory
{
    /** A private instance. */
    private static ConnectionFactory _instance;

    static
    {
        try
        {
            //make sure the connection pool driver gets registered
            Class.forName("com.zitego.sql.Driver");
        }
        catch(Exception e) { }
    }

    /** Pools hashed by pool name. */
    protected Hashtable _pools;
    /** A hash that maps a reserved connection to a pool. */
    protected Hashtable _reservedConnections;

    /**
     * Creates a new connection factory.
     */
    protected ConnectionFactory()
    {
        _pools = new Hashtable();
        _reservedConnections = new Hashtable();
    }

    /**
     * Returns an instance of ConnectionFactory.
     *
     * @return ConnectionFactory
     */
    public synchronized static ConnectionFactory getInstance()
    {
        if (_instance == null) _instance = new ConnectionFactory();
        return _instance;
    }

    /**
     * Returns a connection out the pool with the supplied name.
     *
     * @param poolName The pool name.
     * @return Connection
     */
    public java.sql.Connection getConnection(String poolName) throws Exception
    {
        if (poolName == null) return null;
        ObjectPool pool = (ObjectPool)_pools.get(poolName);
        java.sql.Connection connection = (java.sql.Connection)pool.getObject();
        _reservedConnections.put(connection, pool);
        return new Connection(connection, this);
    }

    /**
     * Returns the state of the specified connetion pool.
     *
     * @return ObjectPoolState
     */
    public ObjectPoolState getState(String poolName)
    {
        if (poolName == null) return null;
        ObjectPool pool = (ObjectPool)_pools.get(poolName);
        return pool.getState();
    }

    /**
     * Returns the supplied connection to the pool.
     *
     * @param connection The connection to return to the pool.
     */
    public void returnConnection(Connection connection)
    {
        if (connection != null && connection._conn != null)
        {
            ObjectPool pool = (ObjectPool)_reservedConnections.get(connection._conn);
            _reservedConnections.remove(connection._conn);
            if (pool != null) pool.returnObject(connection._conn);
        }
    }

    /**
     * Parses a comma delimited configuration string and creates a pool using the information contained within. The
     * configuration string must contain the following fields: driver, url, username, password. It can optionally
     * include: min, max, increment, testtable, expirationTime, debug, debuglevel, sql_log, and allow_shrinking.
     * This stores a DBConfig object in StaticProperties as DBHandleFactory.DEFAULT_DBCONFIG_KEY by default. Use the
     * createPool(String, String, String) method with null as the configPropertyName parameter to not store
     * the config.<br>
     * <br>
     * For example:<br>
     * <pre>
     * driver=com.mysql.jdbc.Driver,\
     *       url=jdbc:mysql@localhost:3306:zitego,\
     *       username=johnnyg,\
     *       password=coolio,\
     *       min=1,\
     *       max=10,\
     *       increment=2,\
     *       testtable=status
     *
     * @param name The name of the pool to create.
     * @param configuration The string described above.
     * @throws Exception if an error occurs.
     */
    public void createPool(String name, String configuration) throws Exception
    {
        createPool(name, configuration, DBHandleFactory.DEFAULT_DBCONFIG_KEY);
    }

    /**
     * Parses a comma delimited configuration string and creates a pool using the information contained within. The
     * configuration string must contain the following fields: driver, url, username, password. It can optionally
     * include: min, max, increment, testtable, expirationTime, debug, debuglevel, sql_log, and allow_shrinking.<br>
     * <br>
     * For example:<br>
     * <pre>
     * driver=com.mysql.jdbc.Driver,\
     *       url=jdbc:mysql@localhost:3306:zitego,\
     *       username=johnnyg,\
     *       password=coolio,\
     *       min=1,\
     *       max=10,\
     *       increment=2,\
     *       testtable=status
     *
     * @param name The name of the pool to create.
     * @param configuration The string described above.
     * @param configPropertyName The property name to store a DBConfig object in StaticProperties. null means none.
     * @throws Exception if an error occurs.
     */
    public void createPool(String name, String configuration, String configPropertyName) throws Exception
    {
        createPool(name, configuration, configPropertyName, null);
    }

    /**
     * Parses a comma delimited configuration string and creates a pool using the information contained within. The
     * configuration string must contain the following fields: driver, url, username, password. It can optionally
     * include: min, max, increment, testtable, expirationTime, debug, debuglevel, sql_log, and allow_shrinking.<br>
     * <br>
     * For example:<br>
     * <pre>
     * driver=com.mysql.jdbc.Driver,\
     *       url=jdbc:mysql@localhost:3306:zitego,\
     *       username=johnnyg,\
     *       password=coolio,\
     *       min=1,\
     *       max=10,\
     *       increment=2,\
     *       testtable=status
     *
     * @param name The name of the pool to create.
     * @param configuration The string described above.
     * @param configPropertyName The property name to store a DBConfig object in StaticProperties. null means none.
     * @param store The property store to put the db config in. If null, StaticProperties is used.
     * @throws Exception if an error occurs.
     */
    public void createPool(String name, String configuration, String configPropertyName, PropertyStore store) throws Exception
    {
        ObjectPoolConfig config = ObjectPoolConfig.parse(configuration);
        ConnectionPoolManager mgr = getConnectionPoolManager(configuration);
        createPool(name, mgr, config, configPropertyName, store);
        mgr.debug("Connection Pool " + name + " initialized", 1);
    }

    /**
     * Create a connection pool with the supplied name, config and manager. This also sets a property
     * in StaticProperties (or the given store) with the key specified. If the key is null, then one called
     * DBHandleFactory.DEFAULT_DBCONFIG_KEY is used. This is the default config used when
     * DBHandleFactory.getDBHandle() is called. If more then one pool is created in this context, only
     * the DBConfig stored as DBHandleFactory.DEFAULT_DBCONFIG_KEY will be accessible this way. For more
     * then one poole, specify a config property name and use DBHandleFactory.getDBHandle(String) with
     * the name of the key.
     *
     * @param name The name of the pool.
     * @param manager The ConnectionPoolManager.
     * @param config The pool configuration.
     * @param configPropertyName The property name to store a DBConfig object. null means none.
     * @throws Exception if an error occurs.
     */
    public void createPool(String name, ConnectionPoolManager manager, ObjectPoolConfig config, String configPropertyName)
    throws Exception
    {
        createPool(name, manager, config, configPropertyName, null);
    }

    /**
     * Create a connection pool with the supplied name, config and manager. This also sets a property
     * in StaticProperties (or the given store) with the key specified. If the key is null, then one called
     * DBHandleFactory.DEFAULT_DBCONFIG_KEY is used. This is the default config used when
     * DBHandleFactory.getDBHandle() is called. If more then one pool is created in this context, only
     * the DBConfig stored as DBHandleFactory.DEFAULT_DBCONFIG_KEY will be accessible this way. For more
     * then one pool, specify a config property name and use DBHandleFactory.getDBHandle(String) with
     * the name of the key.
     *
     * @param name The name of the pool.
     * @param manager The ConnectionPoolManager.
     * @param config The pool configuration.
     * @param configPropertyName The property name to store a DBConfig object. null means none.
     * @param store The property store to put the db config in. If null, StaticProperties is used.
     * @throws Exception if an error occurs.
     */
    public void createPool(String name, ConnectionPoolManager manager, ObjectPoolConfig config,
                           String configPropertyName, PropertyStore store) throws Exception
    {
        manager.debug("Creating connection pool: " + name, 1);
        ObjectPool pool = new ObjectPool(manager, config);

        // Clean any existing pool under this name.
        ObjectPool tmp = (ObjectPool)_pools.get(name);
        if (tmp != null) tmp.flush();
        _pools.put(name, pool);

        if (configPropertyName != null)
        {
            //Create the db config object if we are supposed to and it isn't already there
            boolean createConfig = false;
            if (store != null) createConfig = (store.getProperty(configPropertyName) == null);
            else createConfig = (StaticProperties.getProperty(configPropertyName) == null);
            if (createConfig)
            {
                DBConfig dbcfg = new DBConfig
                (
                    "jdbc:zitego:pool:"+name,
                    (java.sql.Driver)Class.forName("com.zitego.sql.Driver").newInstance(),
                    "", "", DBConfig.MYSQL
                );
                dbcfg.setLogSql( (manager.getLogger() != null) );
                dbcfg.setLogger( manager.getLogger() );
                if (store == null) StaticProperties.setProperty(configPropertyName, dbcfg);
                else store.setProperty(configPropertyName, dbcfg);
            }
        }
    }

    /**
     * Creates a new DBCP pool with a name, configuration, config property name to store the db
     * config uncer and a property store to store it in. This folllows the same logic as the
     * identical createPool signature, but with the tomcatcommons connection pool.
     *
     * @param name The name of the pool.
     * @param manager The ConnectionPoolManager.
     * @param config The pool configuration.
     * @param configPropertyName The property name to store a DBConfig object. null means none.
     * @param store The property store to put the db config in. If null, StaticProperties is used.
     * @throws Exception if an error occurs.
     */
    public void createDBCPPool(String name, String configuration, String configPropertyName, PropertyStore store) throws Exception
    {
        ObjectPoolConfig config = ObjectPoolConfig.parse(configuration);
        ConnectionPoolManager manager = getConnectionPoolManager(configuration);

        manager.debug("Creating connection pool: " + name, 1);
        ObjectPool pool = (ObjectPool)_pools.get(name);
        if ( pool == null || !(pool instanceof DBCPObjectPool) )
        {
            pool = new DBCPObjectPool(manager, config);
            _pools.put(name, pool);
        }
 
        if (configPropertyName != null)
        {
            //Create the db config object if we are supposed to and it isn't already there
            boolean createConfig = false;
            if (store != null) createConfig = (store.getProperty(configPropertyName) == null);
            else createConfig = (StaticProperties.getProperty(configPropertyName) == null);
            if (createConfig)
            {
                DBConfig dbcfg = new DBConfig
                (
                    "jdbc:zitego:pool:"+name,
                    (java.sql.Driver)Class.forName("com.zitego.sql.Driver").newInstance(),
                    "", "", DBConfig.MYSQL
                );
                dbcfg.setLogSql( (manager.getLogger() != null) );
                dbcfg.setLogger( manager.getLogger() );
                if (store == null) StaticProperties.setProperty(configPropertyName, dbcfg);
                else store.setProperty(configPropertyName, dbcfg);
            }
        }
        manager.debug("Connection Pool " + name + " initialized", 1);
    }

    /**
     * Parses a comma delimited configuration string and creates a connection pool manager
     * using the information contained within. The configuration string must contain
     * the following fields: driver, url, username, password. It can optionally include: testtable.<br>
     * <br>
     * For example:<br>
     * driver=com.mysql.jdbc.Driver, url=jdbc:mysql@localhost:3306:zitego,username=johnnyg,password=coolio,testtable=status
     *
     * @param configuration The string described above.
     * @throws Exception if an error occurs.
     */
    protected ConnectionPoolManager getConnectionPoolManager(String configuration) throws Exception
    {
        return getConnectionPoolManager(null, configuration);
    }

    /**
     * Parses a comma delimited configuration string and creates a connection pool manager
     * using the information contained within and inheriting any values not provided from the
     * base config supplied.
     *
     * @param base The base config from which to inherit any values not provided
     *             in config (It will NOT be altered).
     * @param configuration The string described above.
     * @throws Exception if an error occurs.
     */
    protected ConnectionPoolManager getConnectionPoolManager(ConnectionPoolManager base, String configuration)
    throws Exception
    {
        StringTokenizer tokens = new StringTokenizer(configuration, ",");
        String driver = null,
               url = null,
               username = null,
               password = null,
               testTable = null,
               testInterval = null,
               sqlLog = null;
        boolean debug = true;
        int debugLevel = -1;

        while ( tokens.hasMoreTokens() )
        {
            String token = tokens.nextToken().trim();
            StringTokenizer nv = new StringTokenizer(token, "=");

            while ( nv.hasMoreTokens() )
            {
                String paramname = nv.nextToken().trim();

                if ( "driver".equals(paramname) ) driver = nv.nextToken();
                else if ( "url".equals(paramname) )
                {
                    url = nv.nextToken();
                    while ( nv.hasMoreTokens() )
                    {
                        url += "=" + nv.nextToken();
                    }
                }
                else if ( "username".equals(paramname) ) username = nv.nextToken();
                else if ( "password".equals(paramname) ) password = nv.nextToken();
                else if ( "testtable".equals(paramname) ) testTable = nv.nextToken();
                else if ( "debug".equals(paramname) )
                {
                    String d = nv.nextToken();
                    if ( "1".equals(d) || "true".equalsIgnoreCase(d) ) debug = true;
                    else debug = false;
                }
                else if ( "debuglevel".equals(paramname) ) debugLevel = Integer.parseInt( nv.nextToken() );
                else if ( "testinterval".equals(paramname) ) testInterval = nv.nextToken();
                else if ( "sql_log".equals(paramname) ) sqlLog = nv.nextToken();
            }
        }

        if (base == null)
        {
            if (driver==null) throw new IllegalArgumentException("driver not provided in ConnectionFactory");
            if (url==null) throw new IllegalArgumentException("url not provided in ConnectionFactory");
            if (username==null) throw new IllegalArgumentException("username not provided in ConnectionFactory");
            if (password==null) throw new IllegalArgumentException("password not provided in ConnectionFactory");
            return new ConnectionPoolManager
            (
                driver, url, username, password, testTable, testInterval, debug, debugLevel, sqlLog
            );
        }
        else
        {
            return new ConnectionPoolManager
            (
                (driver == null ? base.getDriver() : driver),
                (url == null ? base.getURL() : url),
                (username == null ? base.getUsername() : username),
                (password == null ? base.getPassword() : password),
                (testTable == null ? base.getTestTable() : testTable),
                (testInterval == null ? String.valueOf( base.getTestInterval() ) : testInterval),
                base.isDebugOn(),
                (debugLevel == -1 ? base.getDebugLevel() : debugLevel),
                sqlLog
            );
        }
    }
}
