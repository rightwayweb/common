package com.zitego.sql;

import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * Aids in building a prepared statement where you do not know specifically how the sql
 * will be built or in what order the parameters should be bound. If a statement has a
 * dynamically built search clause, specific values may or may not be part of it. For
 * example, I may be searching for something by date, id, or name. With this class
 * you simply add the columns to it, then pass in a PreparedStatement to bind the values
 * to it.
 *
 * @author John Glorioso
 * @version $Id: PreparedStatementSupport.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class PreparedStatementSupport
{
	/** To keep track of the values passed in. */
	protected ArrayList _values;
	/** The sql. */
	protected String _sql;

	/**
	 * Creates a new PreparedStatementSupport.
	 */
	public PreparedStatementSupport()
	{
		_values = new ArrayList();
	}

	/**
	 * Adds an int to the params.
	 *
	 * @param int
	 */
	public void add(int val)
	{
	    _values.add( new Integer(val) );
	}

	/**
	 * Adds a double to the params.
	 *
	 * @param double
	 */
	public void add(double val)
	{
	    _values.add( new Double(val) );
	}

	/**
	 * Adds a float to the params.
	 *
	 * @param float
	 */
	public void add(float val)
	{
	    _values.add( new Float(val) );
	}

	/**
	 * Adds a short to the params.
	 *
	 * @param short
	 */
	public void add(short val)
	{
	    _values.add( new Short(val) );
	}

	/**
	 * Adds a long to the params.
	 *
	 * @param long
	 */
	public void add(long val)
	{
	    _values.add( new Long(val) );
	}

	/**
	 * Adds the given object to the prepared statement by evaluating its type.
	 *
	 * @param Object The object to add.
	 * @throws IllegalArgumentException if the type is invalid.
	 */
	public void add(Object obj)
	{
	    if (obj instanceof java.util.Date || obj instanceof java.sql.Date || obj instanceof Double ||
	        obj instanceof Float || obj instanceof Integer || obj instanceof Long ||
	        obj instanceof String)
	    {
	        _values.add(obj);
	    }
	    else
	    {
	        throw new IllegalArgumentException(obj+" is not a valid type of Object to add");
	    }
	}

	/**
	 * Adds a null of the specified type to the params.
	 *
	 * @param int The sql type from java.sql.Types.
	 */
	public void addNull(int type)
	{
	    _values.add( new NullValue(type) );
	}

	/**
	 * Sets the sql.
	 *
	 * @param String
	 */
	public void setSql(String sql)
	{
	    _sql = sql;
	}

	/**
	 * Sets the sql.
	 *
	 * @param StringBuffer
	 */
	public void setSql(StringBuffer sql)
	{
	    _sql = sql.toString();
	}

	/**
	 * Returns the sql.
	 *
	 * @return String
	 */
	public String getSql()
	{
	    return _sql;
	}

	/**
	 * Binds the parameters and returns a PreparedStatement.
	 *
	 * @param DBHandle The database handle to use to create the statement.
	 * @return PreparedStatement
	 * @throws SQLException if an error occurs.
	 */
	public PreparedStatement bindValues(DBHandle db) throws SQLException
	{
		if (_sql == null) throw new SQLException("SQL statement not set.");

		PreparedStatement pst = db.prepareStatement(_sql);
		int count = _values.size();
		for (int i=0; i<count; i++)
		{
			Object obj = _values.get(i);
			if (obj instanceof Integer) pst.setInt( i+1, ((Integer)obj).intValue() );
			else if (obj instanceof Double) pst.setDouble( i+1, ((Double)obj).doubleValue() );
			else if (obj instanceof Float) pst.setFloat( i+1, ((Float)obj).floatValue() );
			else if (obj instanceof Short) pst.setShort( i+1, ((Short)obj).shortValue() );
			else if (obj instanceof Long) pst.setLong( i+1, ((Long)obj).longValue() );
			else if (obj instanceof String) pst.setString( i+1, (String)obj );
			else if (obj instanceof java.sql.Date) pst.setDate( i+1, (java.sql.Date)obj );
			else if (obj instanceof java.util.Date) pst.setTimestamp( i+1, new Timestamp(((java.util.Date)obj).getTime()) );
			else if (obj instanceof java.sql.Timestamp) pst.setTimestamp( i+1, (java.sql.Timestamp)obj );
			else if (obj instanceof NullValue) pst.setNull( i+1, ((NullValue)obj).type );
			else throw new SQLException("Unsupported bind value: " + obj);
		}

		return pst;
	}

	private class NullValue
	{
		/** The sql type. */
		public int type = -1;

		/**
		 * Creates a new null value of the specified type.
		 *
		 * @param int The sql type from java.sql.Types.
		 */
		public NullValue(int type)
		{
			this.type = type;
		}
	}
}