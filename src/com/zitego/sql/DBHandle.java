package com.zitego.sql;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.util.Vector;

/**
 * This is an abstract class that defines how a database handle should operate. All specific database
 * implementations should extends this class.
 *
 * @author John Glorioso
 * @version $Id: DBHandle.java,v 1.6 2009/05/05 01:38:25 jglorioso Exp $
 */
public abstract class DBHandle implements java.io.Serializable
{
    /** The Connection to the database or connection pool. */
    protected transient java.sql.Connection _conn;
    /** The database config object. */
    protected DBConfig _config;
    private int _numConnects = 0;
    private String _handleId;
    private Vector<Statement> _statements;
    private boolean _transaction = false;
    private long _lastConnectionTime = 0;
    private long _lastExecuteTime = 0;
    private long _lastRetrieveTime = 0;

    /**
     * Constructs a new DBHandle with a configuration.
     *
     * @param DBConfig The configuration to use to connect to the database.
     * @throws IllegalArgumentException if config is null.
     */
    public DBHandle(DBConfig config) throws IllegalArgumentException
    {
        if (config == null) throw new IllegalArgumentException("config cannot be null in DBHandle");
        _config = config;
        _statements = new Vector<Statement>();
    }

    /**
     * Constructs a new DBHandle with a config and an id.
     *
     * @param DBConfig The configuration to use to connect to the database.
     * @param String The id for this handle.
     * @throws IllegalArgumentException if config is null.
     */
    public DBHandle(DBConfig config, String id) throws IllegalArgumentException
    {
        this(config);
        _handleId = id;
    }

    /**
     * Returns a raw Connection object. This allows the caller to use JDBC functionality
     * Not encapsulated by the DBHandle class.  An exception will occur if this method is called
     * after a call to connect() [or connect(String, String)], but before a call to disconnect. This
     * is to ensure the integrity of the connection and it's associated resources.
     *
     * @return java.sql.Connection
     * @throws IllegalStateException if getConnection was called between calls to connect and disconnect.
     */
    /*public java.sql.Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection( _config.getUrl(), _config.getUsername(), _config.getPassword() );
    }*/

    /**
     * Returns a DatabaseMetaData object.
     *
     * @return DatabaseMetaData
     * @throws SQLException if an error occurs.
     */
    public DatabaseMetaData getMetaData() throws SQLException
    {
        return _conn.getMetaData();
    }

    /**
     * Connects to the database.
     *
     * @throws SQLException if a DB error occurs.
     */
    public void connect() throws SQLException
    {
        try
        {
            //Only connect if we are not connected already
            if ( _conn == null || _conn.isClosed() )
            {
                _conn = DriverManager.getConnection( _config.getUrl(), _config.getUsername(), _config.getPassword() );
                if (_transaction) _conn.setAutoCommit(false);
            }
            _numConnects++;
            if (_numConnects > 1)
            {
                try { throw new Exception("NUMCONNECTS > 1: "+_numConnects); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new SQLException( t.toString() + " in connect()" );
        }
    }

    /**
     * Connects to the database using a specific username/password.
     *
     * @param String The username.
     * @param String The password.
     * @throws SQLException when a DB error occurs.
     */
    public void connect(String username, String password) throws SQLException
    {
        _config.setUsername(username);
        _config.setPassword(password);
        connect();
    }

    /**
     * Checks to see whether the db handle is currently connected to the database or not.
     *
     * @return boolean
     * @throws SQLException if an error occurs checking the connection.
     */
    public boolean isConnected() throws SQLException
    {
        return ( _conn != null && !_conn.isClosed() );
    }

    /**
     * Disconnects from the DB. If the connection is connected in the middle of a transaction, the
     * transaction is rolled back.
     * <p>
     * For Example:
     * <p>
     * DBHandle db = new DBHandle(config);
     * db.connect();
     * db.beginTransaction();
     * db.update(...);
     * db.delete(...);
     * db.disconnect();
     * <p>
     * This would result in the update and delete being rolled back.
     *
     * @throws SQLException when a DB error occurs.
     */
    public void disconnect() throws SQLException
    {
        if (_conn != null)
        {
            synchronized(_conn)
            {
                _numConnects--;
                //Only close if we are open
                if (_conn != null && !_conn.isClosed() && _numConnects <= 0)
                {
                    if (_transaction) endTransaction(false);
                    clean();
                    try { _conn.close(); } catch (SQLException sqle) { }
                    _conn = null;
                    _numConnects = 0;
                }
            }
        }
        else
        {
            _numConnects = 0;
        }
    }

    /**
     * Clean the resources used by this handle. This will close all statements
     * and result sets that have been opened by this handle since the last clean() or disconnect().
     * In order for resources to be eligible for "cleansing", they must be created by this class. In other
     * words, resources created in extensions to this class, unless created using methods in this class, will
     * not be cleansed.
     *
     * @throws SQLException if a database error occurs.
     */
    public void clean() throws SQLException
    {
        if ( _conn != null && !_conn.isClosed() )
        {
            while (_statements.size() > 0)
            {
                Statement stmt = _statements.get(0);
                if (stmt != null)
                {
                    stmt.close();
                    _statements.remove(stmt);
                    stmt = null;
                }
            }
        }
    }

    /**
     * Returns the next id available for a specific table, sequence, property, etc.
     *
     * @param String Then name of the next id to get.
     * @throws SQLException when a DB error occurs.
     */
    public abstract long getNextId(String name) throws SQLException;

    /**
     * Returns the last id inserted into a table.
     *
     * @param String The name of the last id to get.
     * @throws SQLException when a DB error occurs.
     */
    public abstract long getLastId(String name) throws SQLException;

    /**
     * Prepares a SQL statement to be executed.
     *
     * @param String The SQL to prepare.
     * @return java.sql.PreparedStatement
     * @throws SQLException when a DB error occurs.
     */
    public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException
    {
        if (_conn == null) throw new SQLException("Connection has not yet been obtained.");

        if ( _config.logSql() ) logSql(sql);
        java.sql.PreparedStatement rtn = new PreparedStatement( _conn.prepareStatement(sql) );
        if (_config.getCallTimeLimit() > 0) rtn.setQueryTimeout( _config.getCallTimeLimit() );
        _statements.add(rtn);
        return rtn;
    }

    /**
     * Creates a PreparedStatement object that will generate ResultSet objects
     * with the given type and concurrency. This method is the same as the
     * prepareCall method above, but it allows the default result set type
     * and concurrency to be overridden.
     *
     * @param String the sql.
     * @param int A result set type; one of ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE,
     *            or ResultSet.TYPE_SCROLL_SENSITIVE.
     * @param int A concurrency type; one of ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE.
     * @throws SQLException if a database error occurs.
     */
    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        if (_conn == null) throw new SQLException("Connection has not yet been obtained.");

        if ( _config.logSql() ) logSql(sql);
        java.sql.PreparedStatement rtn = new PreparedStatement( _conn.prepareStatement(sql, resultSetType, resultSetConcurrency) );
        _statements.add(rtn);
        return rtn;
    }

    /**
     * Prepares a SQL statement to be executed.
     *
     * @param StringBuffer The SQL to prepare.
     * @return java.sql.PreparedStatement
     * @throws SQLException when a DB error occurs.
     */
    public java.sql.PreparedStatement prepareStatement(StringBuffer sql) throws SQLException
    {
        return prepareStatement( sql.toString() );
    }

    /**
     * Logs a sql statement.
     *
     * @param String The sql.
     */
    public void logSql(String sql)
    {
        String entry = (getId() == null ? "" : getId() + ": ") + sql;

        //See if they have specified a file handler or not
        if (_config.getLogger() == null) System.out.println(entry);
        else _config.getLogger().log(entry);
    }

    /**
     * Returns the DBHandle's config object.
     *
     * @return DBConfig
     */
    public DBConfig getConfig()
    {
        return _config;
    }

    /**
     * Returns a String representation of the DB type.
     *
     * @return String
     */
    public abstract String getDBType();

    /**
     * Returns an object representation of the DB cursor type.
     *
     * @return Object
     */
    public abstract int getDBCursorType();

    /**
     * Returns a String representation of this database handle.
     *
     * @return String
     */
    public String toString()
    {
        return getDBType() + ": " + _config.toString();
    }

    /**
     * Sets the id of the DBHandle.
     *
     * @param String The id.
     */
    public void setId(String id)
    {
        _handleId = id;
    }

    /**
     * Returns the id of the DBHandle.
     *
     * @return String
     */
    public String getId()
    {
        return _handleId;
    }

    /**
     * Mark the beginning of a transaction. This turns automatic commit off in the JDBC connection.
     *
     * @throws SQLException if a db error occurs turning off autocommit.
     */
    public void beginTransaction() throws SQLException
    {
        if ( _conn != null && !_conn.isClosed() ) _conn.setAutoCommit(false);
        _transaction = true;
    }

    /**
     * Mark the end of a transaction. A transaction can be ended in 2 ways, by commiting the changes to the
     * database or by canceling the changed (rolling back). The commit parameter control which way the caller
     * would like.
     *
     * @param boolean If true, changes are commited to the database, otherwise changes are rolled back.
     * @throws SQLException if a db error occurs.
     */
    public void endTransaction(boolean commit) throws SQLException
    {
        if (!_transaction) return;

        if (_numConnects > 1) return;

        if (_conn != null)
        {
            if ( !_conn.isClosed() )
            {
                if (commit) _conn.commit();
                else _conn.rollback();
                _conn.setAutoCommit(true);
            }
        }
        _transaction = false;
    }

    /**
     * Returns the most recent connection time.
     *
     * @return long
     */
    public long getLastConnectTime()
    {
        return _lastConnectionTime;
    }

    /**
     * Returns the most recent execution time.
     *
     * @return long
     */
    public long getLastExecuteTime()
    {
        return _lastExecuteTime;
    }

    /**
      * Returns the most recent retrieval time.
      *
      * @return long
      */
    public long getLastRetrieveTime()
    {
        return _lastRetrieveTime;
    }

    /**
     * Returns the most recent timing info as a formatted string.
     *
     * @return String
     */
    public String getTimingInfo()
    {
         StringBuffer sb = new StringBuffer()
            .append("DB connect= ").append( getLastConnectTime() ).append(" ms. ")
            .append("Execute= ").append( getLastExecuteTime() ).append(" ms. ")
            .append("Retrieve= ").append( getLastRetrieveTime() ).append(" ms.");
         return sb.toString();
    }
}
