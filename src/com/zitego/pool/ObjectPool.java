package com.zitego.pool;

import com.zitego.util.TextUtils;
import com.zitego.mail.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Date;
import java.io.*;

/**
 * A base object pool that contains zero or more objects in a state of free
 * or in use.
 *
 * @author John Glorioso
 * @version $Id: ObjectPool.java,v 1.2 2009/02/16 19:03:19 jglorioso Exp $
 */
public class ObjectPool
{
    /** The objects that are in use. */
    protected Hashtable _inUse;
    /** The objects that are available to be used. */
    protected Hashtable _free;
    /** The actual objects. */
    protected Hashtable _objects;
    /** The object manager for this pool. */
    protected ObjectPoolManager _manager;
    /** The configuration for this pool. */
    protected ObjectPoolConfig _config;
    /** A thread that will expire connections that have been idle for the specified time limit. */
    protected ExpireThread _expireThread;
    /** A thread that will expire connections that have been in use for the specified time limit. */
    protected MaxUseThread _maxUseThread;
    /** A thread that will shrink the pool to its configured limits. */
    protected ShrinkThread _shrinkThread;
    /** The number of objects that have been expired. */
    protected long _expiredObjects = 0;
    /** The last time an alert was sent. */
    protected long _lastAlert = -1;

    /**
     * Empty constructor for extending classes. Does nothing. Any initialization is expected to be
     * handled by extending classes.
     */
    protected ObjectPool() { }

    /**
     * Creates an object pool given a configuration.
     *
     * @param ObjectPoolManager The object pool manager.
     */
    public ObjectPool(ObjectPoolManager mgr) throws Exception
    {
        this( mgr, new ObjectPoolConfig() );
    }

    /**
     * Creates an object pool with the given configuration and manager.
     *
     * @param ObjectPoolManager The pool manager.
     * @param ObjectPoolConfig The configuration.
     * @throws Exception if an error occurs createing the pool.
     */
    public ObjectPool(ObjectPoolManager mgr, ObjectPoolConfig config) throws Exception
    {
        _manager = mgr;
        _config = (config == null ? new ObjectPoolConfig() : config);

        _inUse = new Hashtable();
        _free = new Hashtable();
        _objects = new Hashtable();

        initialize();

        // Start cleaner thread if we are supposed to.
        if (_config.expirationTime >= 0)
        {
            _manager.debug("Initialize expiration Thread", 1);
            //Time passed in is seconds, so check at half the interval
            _expireThread = new ExpireThread( this, (long)(((double)_config.expirationTime)*1000l/2d) );
        }

        // Start maxUseCleaner thread if we are supposed to.
        if (_config.maxUseTime >= 0)
        {
            _manager.debug("Initialize maxUse Thread", 1);
            //Time passed in is seconds, so check at half the interval
            _maxUseThread = new MaxUseThread( this, (long)(((double)_config.maxUseTime)*1000l/2d) );
        }

        // Start the shrink thread if we are supposed to.
        if (_config.allowShrinking && _config.shrinkDelay > 0)
        {
            _manager.debug("Initialize shrink delay Thread", 1);
            //Time passed in is seconds, so check at the interval
            _shrinkThread = new ShrinkThread( this, (long)(_config.shrinkDelay*1000l) );
        }
    }

    public void finalize() throws Throwable
    {
        super.finalize();
        destroy();
    }

    public void destroy()
    {
        _manager.debug("Destroy object pool", 1);

        _config.maxObjects = 0;
        _config.minObjects = 0;

        //Everyone out of the pool!
        Enumeration e = _free.keys();
        while ( e.hasMoreElements() )
        {
            ObjectPoolItem o = (ObjectPoolItem)e.nextElement();
            _free.remove(o);
            expireObject(o);
        }

        //You too, I don't care if you are in use!
        e = _inUse.keys();
        while ( e.hasMoreElements() )
        {
            ObjectPoolItem o = (ObjectPoolItem)e.nextElement();
            _inUse.remove(o);
            expireObject(o);
        }

        _expireThread.destroy();
    }

    /**
     * Initialize the pool
     *
     * @throws Exception if an error occurs populating the pool.
     */
    protected void initialize() throws Exception
    {
        // Populate the pool with an initial number of objects based on the configration.
        for (int i=0; i<_config.minObjects; i++)
        {
            _free.put( createObject(), new Long(System.currentTimeMillis()) );
        }
        _manager.debug("Pool contains " + _free.size() + " items", 1);
    }

    /**
     * Creates a new instance of the Object that this ObjectPool manages.
     *
     * @throws Exception if an error occurs creating the object.
     */
    protected ObjectPoolItem createObject() throws Exception
    {
        _manager.debug("Create pool object", 1);

        Object o = _manager.createPoolObject();
        ObjectPoolItem item = new ObjectPoolItem(o);
        _objects.put(o, item);

        return item;
    }

    /**
     * Verify that the passed ObjectPoolItem can still be used.
     *
     * @param PbjectPoolItem The item to validate.
     */
    protected boolean validateObject(ObjectPoolItem item)
    {
        if (item.usecount == _config.usecount)
        {
            _manager.debug("[" + item.item + "] item has reached usecount", 1);
            return false;
        }
        else
        {
            return _manager.validate(item.item);
        }
    }

    /**
     * Clean up the passed ObjectPoolItem as it is about to be released by the ObjectPool.
     *
     * @param ObjectPoolItem The item to expire.
     */
    protected void expireObject(ObjectPoolItem item)
    {
        if ( !_inUse.contains(item) )
        {
            _manager.expire(item.item);
            _objects.remove(item.item);
            _free.remove(item);
            _expiredObjects++;
        }
    }

    /**
     * Returns a validated instance of an Object that this ObjectPool manages.
     *
     * @throws Exception if a new object needs to be created and an error occurs creating it.
     */
    synchronized public Object getObject() throws Exception
    {
        if (_config.debug && _config.debugLevel > 0)
        {
            _manager.debug("[" + Thread.currentThread() + "] GetObject, InUse: " + _inUse.size() + ", free = " + _free.size(), 1);
        }

        long now = System.currentTimeMillis();
        ObjectPoolItem object = null;
        try
        {
            //If the pool has no available objects, then try to expand it.
            //Retrieve next available object from the pool.
            //Validate the object.
            //Remove the object from the free pool.
            //Put the object in the in use pool.
            boolean keepTrying = true;
            // This while loop allows us to expand the loop and try again if possible
            while (keepTrying)
            {
                if (_free.size() > 0)
                {
                    // Iterate over available pool items, returning the first valid item.
                    Enumeration objects = _free.keys();
                    while ( objects.hasMoreElements() )
                    {
                        object = (ObjectPoolItem)objects.nextElement();
                        if (object != null && object.item != null)
                        {
                            if ( validateObject(object) )
                            {
                                _free.remove(object);
                                _inUse.put( object, new Long(now) );
                                if (_config.enableTracing) object.trace();
                                return object.item;
                            }
                            else
                            {
                                _manager.debug("[" + object.item + "] object failed validation", 1);

                                // The object failed validation, replace it with a new one.
                                object = replace(object);
                                if ( object != null && validateObject(object) )
                                {
                                    _inUse.put( object, new Long(now) );
                                    if (_config.enableTracing) object.trace();
                                    return object.item;
                                }
                                else
                                {
                                    // If we fail validation on the new object, stop trying.
                                    object = null;
                                }
                            }
                        }
                    }
                }

                //If we are not expanding the pool, then quit
                if ( !expand() )
                {
                    if (_config.block) wait();
                    else keepTrying = false;
                }
            }

            // We've expanded the pool as far as it will go and there still are no available connections.
            throw new Exception("Maximum number of objects in pool has been reached, none available");
        }
        finally
        {
            if (object != null) object.usecount++;
            checkAlert();
        }
    }

    /**
     * Replace the specified pool object with a new pool object. The specified object
     * is removed from the free pool and a new object is created and added to it.
     *
     * @param OBjectPoolItem The object to replace.
     * @return ObjectPoolItem
     */
    protected synchronized ObjectPoolItem replace(ObjectPoolItem object) throws Exception
    {
        if (object != null) expireObject(object);

        object = null;
        if (_inUse.size() + _free.size() < _config.maxObjects) object = createObject();

        return object;
    }

    /**
     * Expands the pool by the increment specified in the config, or 1 if <= 0 was specified.
     * The pool will only be expanded up to the maximum number of objects. Returns true only
     * if the pool was successfully expanded.
     *
     * @return boolean
     */
    protected synchronized boolean expand() throws Exception
    {
        _manager.debug("Enter Expand, InUse: " + _inUse.size() + ", free = " + _free.size(), 1);

        long now = System.currentTimeMillis();

        if (_config.alertOnExpand )
        {
            if (_config.debug) printInUse( new PrintWriter(System.err) );

            StringWriter msgWriter = new StringWriter();
            printInUse( new PrintWriter(msgWriter) );
            StringBuffer msg = new StringBuffer("Object pool is being expanded!\n\n").append( msgWriter.toString() );
            alert( msg.toString() );
        }

        // Default increment to 1 if not set to a valid value.
        long increment = 1;
        if (_config.increment >= 1) increment = _config.increment;

        int i = 0;
        for (; i<increment && _free.size()+_inUse.size() < _config.maxObjects; i++)
        {
            _free.put( createObject(), new Long(now) );
        }

        _manager.debug("Exit Expand, InUse: " + _inUse.size() + ", free = " + _free.size(), 1);

        if (i > 0) return true;

        return false;
    }

    /**
     * Returns the given Object to the ObjectPool when done with it.
     *
     * @param Object The Object to return.
     */
    synchronized public void returnObject(Object obj)
    {
        if (_config.debug && _config.debugLevel > 0)
        {
            _manager.debug
            (
                "[" + Thread.currentThread() + "], Enter ReturnObject InUse: " + _inUse.size() +
                ", free = " + _free.size()
            );
        }
        if (obj != null)
        {
            ObjectPoolItem item = (ObjectPoolItem)_objects.get(obj);
            if (item != null)
            {
                // Only return the object to the free pool if it is successfully removed from the inuse pool
                if (_inUse.remove(item)!= null)
                {
                    int poolMin = _config.minObjects;
                    if (_config.shrinkFloor > poolMin) poolMin = _config.shrinkFloor;

                    // If shrinking is turned on and we are above the minimum then do not return the object,
                    // instead discard it.
                    if (_config.allowShrinking && _config.shrinkDelay == 0l && _free.size() >= poolMin)
                    {
                        _manager.debug("[" + Thread.currentThread() + "], not returning object " + obj + " to allow pool to shrink", 1);
                        expireObject(item);
                    }
                    else
                    {
                        // Shrinking is not turned on, or we are at or below the minimum, return the object.
                        _free.put( item, new Long(System.currentTimeMillis()) );
                    }
                }
                else
                {
                    _manager.debug("[" + Thread.currentThread() + "], ReturnObject object not found in inuse hash, obj = " + obj, 1);
                }
            }
        }
        if (_config.debug && _config.debugLevel > 0)
        {
            _manager.debug
            (
                "[" + Thread.currentThread() + "], Exit ReturnObject InUse: " + _inUse.size() +
                ", free = " + _free.size()
            );
        }

        if (_config.block) notifyAll();
    }

    /**
     * Shrinks the pool to it's configured limits.
     */
    protected synchronized void shrink()
    {
        if (!_config.allowShrinking) return;

        if (_config.debug && _config.debugLevel > 0)
        {
            _manager.debug
            (
                "[" + Thread.currentThread() + ", " + this + "] Shrink, InUse: " + _inUse.size() +
                ", free = " + _free.size()
            );
        }

        int poolMin = _config.minObjects;
        if (_config.shrinkFloor > poolMin) poolMin = _config.shrinkFloor;

        while (_free.size() > poolMin)
        {
            ObjectPoolItem item = null;
            Enumeration objects = _free.keys();
            if ( objects.hasMoreElements() )
            {
                item = (ObjectPoolItem)objects.nextElement();
                if (_free.remove(item) == null) item = null;
            }

            if (item != null)
            {
                _manager.debug("[" + Thread.currentThread() + "], removing object " + item.item + " to shrink pool", 1);
                expireObject(item);
            }
        }

        _manager.debug("AfterShrink, InUse: " + _inUse.size() + ", free = " + _free.size(), 1);
    }

    /**
     * Expire all Objects in the free list.
     */
    synchronized public void flush()
    {
        Enumeration e = _free.keys();
        while ( e.hasMoreElements() )
        {
            ObjectPoolItem o = (ObjectPoolItem)e.nextElement();
            _free.remove( o );
            expireObject( o );
        }
    }

    /**
     * Clean the ObjectPool.
     */
    synchronized public void close() throws PoolInUseException
    {
        flush();
        int size = _inUse.size();
        if (size > 0) throw new PoolInUseException("There are " + size + " Objects in use");
    }

    /**
     * Go through the list of in use Objects and expire() all that have
     * exceeded their expiration time, and replace them with new objects.
     */
    synchronized protected void inUseClean() throws Exception
    {
        if (_config.debug && _config.debugLevel > 0)
        {
            _manager.debug
            (
                "[" + Thread.currentThread() + ", " + this + "] inUseClean, InUse: " + _inUse.size() +
                ", free = " + _free.size()
            );
        }

        long now = System.currentTimeMillis();
        // Expire in use objects
        Enumeration e2 = _inUse.keys();
        while ( e2.hasMoreElements() )
        {
            ObjectPoolItem o = (ObjectPoolItem)e2.nextElement();
            if ( (now - ((Long)_inUse.get(o)).longValue()) / 1000l > _config.maxUseTime && _config.maxUseTime >= 0 )
            {
                _manager.debug("[" + o.item + "] object has expired", 1);
                // The connection has expired, re-create it
                _inUse.remove(o);
                expireObject(o);
                _free.put( createObject(), new Long(now) );
            }
        }

        _manager.debug("AfterinUseClean, InUse: " + _inUse.size() + ", free = " + _free.size(), 1);
    }

    /**
     * Goes through the list of free Objects and expires all that have
     * exceeded their expiration time, and replace them with new objects.
     */
    synchronized protected void clean() throws Exception
    {
        if (_config.debug && _config.debugLevel > 0)
        {
            _manager.debug
            (
                "[" + Thread.currentThread() + ", " + this + "] Clean, InUse: " + _inUse.size() +
                ", free = " + _free.size()
            );
        }

        long now = System.currentTimeMillis();
        // Expire free objects
        Enumeration e = _free.keys();
        while ( e.hasMoreElements() )
        {
            ObjectPoolItem o = (ObjectPoolItem)e.nextElement();
            if ( (now - ((Long)_free.get(o)).longValue()) / 1000l > _config.expirationTime && _config.expirationTime >= 0 )
            {
                _manager.debug("[" + o.item + "] object has expired", 1);
                // The connection has expired, re-create it.
                _free.remove(o);
                expireObject(o);
                _free.put( createObject(), new Long(now) );
            }
        }

        _manager.debug("AfterClean, InUse: " + _inUse.size() + ", free = " + _free.size(), 1);
    }

    protected void checkAlert()
    {
        double percentInUse = ( (double)_inUse.size() ) / ( (double)_config.maxObjects );
        if (percentInUse*100 >= _config.alertThreshold)
        {
            if (_config.debug) printInUse( new PrintWriter(System.err) );

            StringBuffer msg = new StringBuffer()
                .append("Object Pool has reached it's threshold: ").append( (percentInUse*100) )
                .append("% of max available objects are in use");

            StringWriter msgWriter = new StringWriter();
            printInUse( new PrintWriter(msgWriter) );
            msg.append( msgWriter.toString() );

            String alertError = alert( msg.toString() );

            if (alertError != null)
            {
                System.err.println("********** Alert cannot be sent (" + alertError + "): " + msg);
            }
        }
    }

    protected String alert(String msg)
    {
        long now = System.currentTimeMillis();
        if (_lastAlert > 0 && now - _lastAlert < _config.alertInterval*1000) return null;

        String alertError = null;
        if (_config.alertAdmin != null)
        {
            String recips[] = TextUtils.split(_config.alertAdmin, ';');
            try
            {
                if (recips != null)
                {
                    if (_config.mailServer != null)
                    {
                        for (int i=0; i<recips.length; i++)
                        {
                            _manager.debug("Sending alert to " + recips[i] + " via SMTP Server", 1);
                            SMTPMail mailer = new SMTPMail();
                            mailer.setToAddress(recips[i]);
                            mailer.setSubject("Subject: Object Pool Alert!");
                            mailer.setMailServer(_config.mailServer);
                            mailer.setBody(msg);
                        }
                    }
                    else if (_config.mailCommand != null)
                    {
                        for (int i=0; i<recips.length; i++)
                        {
                            _manager.debug("Sending alert to " + recips[i] + " via " + _config.mailCommand, 1);
                            SpoolMail mailer = new SpoolMail();
                            mailer.beginSpool(_config.mailCommand, null);
                            mailer.spool("To: " + recips[i]);
                            mailer.spool("Subject: Object Pool Alert!");
                            mailer.spool(msg);
                            mailer.endSpool();
                        }
                    }
                }
                else
                {
                    alertError = "No recipients - " + _config.alertAdmin;
                }
                _lastAlert = now;
            }
            catch(Throwable t)
            {
                alertError = t.toString();
            }
        }
        else
        {
            alertError = "No recipients (b) - " + _config.alertAdmin;
        }

        return alertError;
    }

    public void printInUse(PrintWriter out)
    {
        out.println("******************" + new java.util.Date() + "**********************");
        Enumeration inuse = _inUse.keys();
        long now = System.currentTimeMillis();
        Object obj = null;
        while ( inuse.hasMoreElements() )
        {
            obj = inuse.nextElement();
            long time = ( (Long)_inUse.get(obj) ).longValue();
            out.println( "------> (" + ( (now-time)/1000l ) + " s) " + obj.toString() );
        }
        out.println("*******************************************************");
        out.flush();
    }

    public ObjectPoolState getState()
    {
        ObjectPoolState state = new ObjectPoolState();
        state.poolConfig = _config;
        state.objectsAvailable = _free.size();
        state.objectsInUse = _inUse.size();
        state.objectsExpired = _expiredObjects;
        state.inUseHash = _inUse;
        return state;
    }

    protected class ObjectPoolItem
    {
        public Object item;
        public int usecount = 0;
        public Exception tracer = null;

        ObjectPoolItem(Object object)
        {
            item = object;
        }

        public void trace()
        {
            tracer = new Exception("[tracer]");
        }

        public boolean equals(Object obj)
        {
            if (obj instanceof ObjectPoolItem) return item.equals( ((ObjectPoolItem)obj).item );
            return false;
        }

        public String toString()
        {
            if (tracer != null)
            {
                StringWriter writer = new StringWriter();
                PrintWriter out = new PrintWriter(writer);
                if (tracer != null)
                {
                    tracer.printStackTrace(out);
                    return writer.toString();
                }
            }

            return super.toString();
        }
    }
}

class ExpireThread extends Thread
{
    protected ObjectPool _pool;
    protected long _sleepTime;
    protected boolean _stop = false;

    /**
     * Create an ExpireThread. This will automatically start the thread.
     *
     * @param ObjectPool The ObjectPool this thread will cleanUp().
     * @param long The max time in milliseconds that this thread will sleep in between runs.
     */
    ExpireThread(ObjectPool pool, long sleepTime)
    {
        _pool = pool;
        _sleepTime = sleepTime;
        setDaemon(true);
        start();
    }

    public void finalize() throws Throwable
    {
        super.finalize();
        destroy();
    }

    public void destroy()
    {
        _stop = true;
        super.destroy();
    }

    /**
     * Infinite loop that will expire objects in the pool
     */
    public void run()
    {
        while (!_stop)
        {
            try { sleep( _sleepTime ); } catch(InterruptedException e) { }
            try
            {
                _pool.clean();
            }
            catch(Exception e)
            {
                e.printStackTrace(System.err);
            }
        }
    }
}


class MaxUseThread extends Thread
{
    protected ObjectPool _pool;
    protected long _sleepTime;
    protected boolean _stop = false;

    /**
     * Creates a MaxUseThread. This will automatically start the thread.
     *
     * @param ObjectPool The ObjectPool this thread will cleanUp().
     * @param long The max time in milliseconds that this thread will sleep in between runs.
     */
    MaxUseThread(ObjectPool pool, long sleepTime)
    {
        _pool = pool;
        _sleepTime = sleepTime;
        setDaemon(true);
        start();
    }

    public void finalize() throws Throwable
    {
        super.finalize();
        destroy();
    }

    public void destroy()
    {
        _stop = true;
        super.destroy();
    }

    /**
     * Infinite loop that will expire objects in the pool
     */
    public void run()
    {
        while (!_stop)
        {
            try { sleep(_sleepTime); } catch (InterruptedException e) { }
            try
            {
                _pool.inUseClean();
            }
            catch(Exception e)
            {
                e.printStackTrace(System.err);
            }
        }
    }
}


class ShrinkThread extends Thread
{
    protected ObjectPool _pool;
    protected long _sleepTime;
    protected boolean _stop = false;

    /**
     * Creates a ShrinkThread. This will automatically start the thread.
     *
     * @param ObjectPool The ObjectPool this thread will shrink().
     * @param long The max time in milliseconds that this thread will sleep in between runs.
     */
    ShrinkThread(ObjectPool pool, long sleepTime)
    {
        _pool = pool;
        _sleepTime = sleepTime;
        setDaemon(true);
        start();
    }

    public void finalize() throws Throwable
    {
        super.finalize();
        destroy();
    }

    public void destroy()
    {
        _stop = true;
        super.destroy();
    }

    /**
     * Infinite loop that will expire objects in the pool
     */
    public void run()
    {
        while (!_stop)
        {
            try { sleep(_sleepTime); } catch (InterruptedException e) { }
            try
            {
                _pool.shrink();
            }
            catch(Exception e)
            {
                e.printStackTrace(System.err);
            }
        }
    }
}
