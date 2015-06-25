package com.zitego.pool;

/**
 * An Object pool manager interface.
 *
 * This interface is used by ObjectPools to act as a factory and validation of
 * objects contained in the ObjectPool.
 *
 * @author John Glorioso
 * @version $Id: ObjectPoolManager.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public interface ObjectPoolManager
{
	/**
	 * Create an instance of the type of object that is managed by this Pool Mananger.
     *
     * @return Object
	 */
	Object createPoolObject() throws Exception;

	/**
     * Validates that the supplied object is a valid instance of the type of object that this
     * pool mananges. "Valid" is defined by the implementing class.
     *
     * @return boolean
	 */
	boolean validate(Object o);

	/**
	 * Cleans up the supplied object. This is usually done when the object is going
     * to be removed from the pool for some reason. It returns the Object after expiring.
     *
     * @param Object
	 */
	void expire(Object o);

	/**
	 * Handles logging debug messages.
	 *
	 * @param String The message.
	 */
	void debug(String msg);

	/**
	 * Handles logging debug messages.
	 *
	 * @param String The message.
	 * @param int The debug level.
	 */
	void debug(String msg, int level);
}