package com.zitego.sql;

import com.zitego.pool.ObjectPool;
import com.zitego.pool.ObjectPoolConfig;
import com.zitego.pool.ObjectPoolManager;
import com.zitego.pool.PoolInUseException;
import com.zitego.pool.ObjectPoolState;
import java.io.PrintWriter;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.dbcp.datasources.SharedPoolDataSource;
import org.apache.tomcat.dbcp.dbcp.cpdsadapter.DriverAdapterCPDS;

/**
 * This is a  mock pool class to hold a DBCP connection which is simply
 * a wrapper of an apache commons SharedPoolDataSource backed by a
 * DriverAdapterCPDS.
 *
 * @author John Glorioso
 * @version $Id: DBCPObjectPool.java,v 1.3 2009/02/19 06:26:09 jglorioso Exp $
 */
public class DBCPObjectPool extends ObjectPool
{
    private SharedPoolDataSource _dataSource;

    /**
     * Creates a new DBCPObjectPool with a connection pool manager and an
     * object pool config.
     *
     * @param mgr The connection pool manager.
     * @param config The object pool config.
     * @throws Exception if an error occurs initializing the pool.
     */
    public DBCPObjectPool(ObjectPoolManager mgr, ObjectPoolConfig config) throws Exception
    {
        _manager = mgr;
        _config = config;
        initialize();
    }

    public void finalize() throws Throwable
    {
        super.finalize();
        destroy();
    }

    public void destroy()
    {
        _manager.debug("Destroy object pool", 1);
        close();
    }

    /**
     * Initialize the pool
     *
     * @throws Exception if an error occurs populating the pool.
     */
    protected void initialize() throws Exception
    {
        DriverAdapterCPDS cpds = new DriverAdapterCPDS();
        ConnectionPoolManager mgr = (ConnectionPoolManager)_manager;
        cpds.setDriver( mgr.getDriver() );
        cpds.setUrl( mgr.getURL() );
        cpds.setUser( mgr.getUsername() );
        cpds.setPassword( mgr.getPassword() );
        _dataSource = new SharedPoolDataSource();
        _dataSource.setConnectionPoolDataSource(cpds);
        if (mgr.getTestTable() != null)
        {
            _dataSource.setValidationQuery("SELECT 1 FROM " + mgr.getTestTable() + " LIMIT 1");
            _dataSource.setTestOnBorrow(true);
        }
        _dataSource.setMaxActive(_config.maxObjects);
        _dataSource.setMaxWait(50);
    }

    protected ObjectPoolItem createObject() throws Exception
    {
        //SharedDataSource handles this
        return null;
    }

    protected boolean validateObject(ObjectPoolItem item)
    {
        //SharedDataSource handles this
        return true;
    }

    protected void expireObject(ObjectPoolItem item)
    {
        //SharedDataSource handles this
    }

    synchronized public Object getObject() throws Exception
    {
        if (_config.debug && _config.debugLevel > 0)
        {
            _manager.debug("GetObject, InUse: " + _dataSource.getNumActive() + ", free = " + _dataSource.getNumIdle(), 1);
        }
        return _dataSource.getConnection();
    }

    protected synchronized ObjectPoolItem replace(ObjectPoolItem object) throws Exception
    {
        //SharedDataSource handles this
        return object;
    }

    protected synchronized boolean expand() throws Exception
    {
        //SharedDataSource handles this
        return true;
    }

    synchronized public void returnObject(Object obj)
    {
        //SharedDataSource handles this
        java.sql.Connection conn = (java.sql.Connection)obj;
        if (conn != null)
        {
            try { conn.close(); } catch (SQLException sqle) {}
            conn = null;
        }
    }

    protected synchronized void shrink()
    {
        //SharedDataSource handles this
    }

    synchronized public void flush()
    {
        //SharedDataSource handles this
    }

    synchronized public void close() throws PoolInUseException
    {
        if (_dataSource != null)
        {
            try
            {
                _dataSource.close();
            }
            catch (Exception e)
            {
                throw new PoolInUseException( e.toString() );
            }
        }
    }

    synchronized protected void inUseClean() throws Exception
    {
        //SharedDataSource handles this
    }

    synchronized protected void clean() throws Exception
    {
        //SharedDataSource handles this
    }

    protected void checkAlert()
    {
        //SharedDataSource handles this
    }

    public void printInUse(PrintWriter out)
    {
        out.println("******************" + new java.util.Date() + "**********************");
        out.println( _dataSource.getNumActive() + " connections in use");
        out.println("*******************************************************");
        out.flush();
    }

    public ObjectPoolState getState()
    {
        ObjectPoolState state = new ObjectPoolState();
        state.poolConfig = _config;
        state.objectsAvailable = _dataSource.getNumIdle();
        state.objectsInUse = _dataSource.getNumActive();
        state.objectsExpired = 0;
        state.inUseHash = new java.util.Hashtable();
        return state;
    }
}
