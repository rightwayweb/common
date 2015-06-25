package com.zitego.sql;

import com.zitego.util.StatusType;
import com.zitego.util.InformationEntity;
import java.sql.SQLException;
import java.util.Date;

/**
 * This class represents a basic entity whose data resides in the database. The
 * properties include an id, whether or not they are active, and a database handle
 * for querying.
 *
 * @author John Glorioso
 */
public abstract class DatabaseEntity extends InformationEntity implements DatabaseUpdater
{
	/** The id. Default is -1. */
	private long _id = -1;
	/** Whether the entity is active. Default is true. */
	private boolean _active = true;
	/** An optional numeric status. */
	private int _status = -1;
	/** The status object. This should be kept in sync with status if it is set. */
	protected StatusType _statusObject;
	/** The creation date. */
	private Date _creationDate;
	/** The last updated date. */
	private Date _lastUpdated;
	/** The id of an entity that last updated this one. Default is -1. */
	private long _lastUpdatedBy = -1;
	/** The database object to get data with. */
	private DBConfig _config;

	/**
	 * Constructs a new entity with no properties.
	 */
	public DatabaseEntity() { }

	/**
	 * Constructs a new entity with a database handle.
	 *
	 * @param db The database handle.
	 * @deprecated Use DatabaseEntity(DBConfig)
	 */
	public DatabaseEntity(DBHandle db)
	{
		setDBConfig(db);
	}

	/**
	 * Constructs a new entity with a database config.
	 *
	 * @param config The database config.
	 */
	public DatabaseEntity(DBConfig config)
	{
		setDBConfig(config);
	}

	/**
	 * Constructs a new entity with an id and a database handle.
	 *
	 * @param id The id.
	 * @param db The database handle.
	 */
	public DatabaseEntity(long id, DBHandle db)
	{
		this(db);
		setId(id);
	}

	/**
	 * Constructs a new entity with an id and a database config.
	 *
	 * @param id The id.
	 * @param config The database config.
	 */
	public DatabaseEntity(long id, DBConfig config)
	{
		this(config);
		setId(id);
	}

	/**
	 * Sets the database handle.
	 *
	 * @param db The database handle.
	 * @deprecated Use setDBConfig(DBConfig)
	 */
	public void setDBHandle(DBHandle db) { setDBConfig(db); }

	/**
	 * Sets the database config.
	 *
	 * @param config The database config.
	 */
	public void setDBConfig(DBConfig config)
	{
	    _config = config;
	}

	private void setDBConfig(DBHandle db)
	{
	    if (db != null) _config = db.getConfig();
    }

	/**
	 * Returns the database handle.
	 *
	 * @return DBHandle
	 */
	public DBHandle getDBHandle()
	{
	    if (_config != null) return DBHandleFactory.getDBHandle(_config);
	    else return null;
	}

	/**
	 * Returns the database config.
	 *
	 * @return DBConfig
	 */
	public DBConfig getDBConfig()
	{
	    return _config;
	}

	/**
	 * Sets the id.
	 *
	 * @param id The id.
	 */
	public void setId(long id) { _id = id; }

	/**
	 * Returns the id.
	 *
	 * @return long
 	 */
	public long getId() { return _id; }

	/**
	 * Sets whether this entity is active or not.
	 *
	 * @param active The active flag.
	 */
	public void setActive(boolean active) { _active = active; }

	/**
	 * Returns whether the entity is active or not.
	 *
	 * @return boolean
 	 */
	public boolean isActive() { return _active; }

	/**
	 * Sets the status.
	 *
	 * @param status The status.
	 */
	public void setStatus(int status)
	{
	    _status = status;
	    setStatusObject();
	}

	/**
	 * Sets the status given a StatusType. Null is treated as StatusType.INACTIVE.
	 *
	 * @param status The type.
	 */
	public void setStatus(StatusType status)
	{
	    if (status == null) status = StatusType.INACTIVE;
	    _statusObject = status;
	    _status = _statusObject.getValue();
	}

	/**
	 * Sets the status object based on the value of getStatus(). This should be overridden
	 * if the status does not represent StatusType.ACTIVE or StatusType.INACTIVE.
	 */
	protected void setStatusObject()
	{
	    _statusObject = StatusType.evaluate(_status);
	}

	/**
	 * Returns the status.
	 *
	 * @return int
	 */
	public int getStatus() { return _status; }

	/**
	 * Returns the status object.
	 *
	 * @return StatusType
	 */
	public StatusType getStatusObject() { return _statusObject; }

	/**
	 * Sets creation date.
	 *
	 * @param dt The date.
	 */
	public void setCreationDate(Date dt) { _creationDate = dt; }

	/**
	 * Returns the creation date.
	 *
	 * @return Date
 	 */
	public Date getCreationDate() { return _creationDate; }

	/**
	 * Sets the last updated date.
	 *
	 * @param dt The date.
	 */
	public void setLastUpdated(Date dt) { _lastUpdated = dt; }

	/**
	 * Returns the last updated date.
	 *
	 * @return Date
 	 */
	public Date getLastUpdated() { return _lastUpdated; }

	/**
	 * Sets the last updated by id.
	 *
	 * @param id The id.
	 */
	public void setLastUpdatedBy(long id) { _lastUpdatedBy = id; }

	/**
	 * Returns the last updated by id.
	 *
	 * @return long
 	 */
	public long getLastUpdatedBy() { return _lastUpdatedBy; }

	/**
	 * Saves the Subscriber's information. If they have an id, it will update. If
	 * not then it will insert.
	 *
	 * @throws SQLException
	 */
	public final void save() throws SQLException
	{
		if (getId() > 0)
		{
			update();
		}
		else
		{
			insert();
		}
	}

	/**
	 * Inserts a new Subscriber's information into the database.
	 *
	 * @throws SQLException
	 */
	protected abstract void insert() throws SQLException;

	/**
	 * Updates the Subscriber's information.
	 *
	 * @throws SQLException
	 */
	protected abstract void update() throws SQLException;
}