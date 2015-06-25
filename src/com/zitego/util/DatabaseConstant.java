package com.zitego.util;

import java.util.Vector;

/**
 * A class to be extended that represents a database constant. That is it creates all
 * constants within this class from the database.
 *
 * @author John Glorioso
 * @version $Id: DatabaseConstant.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public abstract class DatabaseConstant extends Constant
{
    /**
     * Creates a database constant with a description and value.
     *
     * @param int The value.
     * @param String The description.
     */
    public DatabaseConstant(int value, String label)
    {
        super(value, label);
    }

    /**
     * This methods loads the constant data out of the database. SQLExceptions need to be caught
     * and handled here. If this method is not extended, an IllegalStateException will be thrown.
     */
    protected static void loadData()
    {
        //Extend this
        throw new IllegalStateException("loadData must be implemented.");
    }

    /**
     * Returns an instance of this database constant class.
     *
     * @return DatabaseConstant
     */
    public static DatabaseConstant getInstance()
    {
        //Extend this
        throw new IllegalStateException("getInstance must be implemented.");
    }
}