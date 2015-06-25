package com.zitego.sql;

import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.PreparedStatementSupport;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.NoDataException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 * A uniquely named database entity is a table that has a column with a unique
 * constraint on it such as name or reference_id. The column names can be overridden.
 *
 * @author John Glorioso
 * @version $Id: UniqueNameDatabaseEntity.java,v 1.2 2009/05/05 01:45:24 jglorioso Exp $
 */
public abstract class UniqueNameDatabaseEntity extends DatabaseEntity
{
    private String _name;
    private String _label;

    /**
     * Creates a new UniqueNameDatabaseEntity with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use UniqueNameDatabaseEntity(DBConfig) instead.
     */
    public UniqueNameDatabaseEntity(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new UniqueNameDatabaseEntity with a DBHandle.
     *
     * @param config The db config to use for querying.
     */
    public UniqueNameDatabaseEntity(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new UniqueNameDatabaseEntity with a numeric id and a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use UniqueNameDatabaseEntity(long, DBConfig) instead.
     */
    public UniqueNameDatabaseEntity(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new UniqueNameDatabaseEntity with a numeric id and a DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public UniqueNameDatabaseEntity(long id, DBConfig config)
    {
        super(id, config);
    }

    /**
     * Creates a new UniqueNameDatabaseEntity with the name to load with and a DBHandle.
     *
     * @param name The name.
     * @param db The db handle to use for querying.
     * @deprecated Use UniqueNameDatabaseEntity(long, DBConfig) instead.
     */
    public UniqueNameDatabaseEntity(String name, DBHandle db)
    {
        this( name, db.getConfig() );
    }

    /**
     * Creates a new UniqueNameDatabaseEntity with the name to load with and a DBConfig.
     *
     * @param name The name.
     * @param config The db config to use for querying.
     */
    public UniqueNameDatabaseEntity(String name, DBConfig config)
    {
        super(config);
        setName(name);
    }

    /**
     * Returns the sql constraint for the unique column.
     *
     * @param supp The PreparedSupportStatement object.
     * @return String
     * @throws NoDataException if name and id are both null.
     */
    public String getUniqueConstraint(PreparedStatementSupport supp) throws NoDataException
    {
        if (getId() > -1)
        {
            supp.add( getId() );
            return getIdColName()+" = ?";
        }
        else if (getName() != null)
        {
            supp.add( getName() );
            return getNameColName()+" = ?";
        }
        else
        {
            throw new NoDataException("Both id and name are null");
        }
    }

    /**
     * Sets the name.
     *
     * @param name The name.
     */
    public void setName(String name)
    {
        _name = name;
    }

    /**
     * Returns the name.
     *
     * @return String
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the label.
     *
     * @param label The label.
     */
    public void setLabel(String label)
    {
        _label = label;
    }

    /**
     * Returns the label.
     *
     * @return String
     */
    public String getLabel()
    {
        return _label;
    }

    /**
     * Returns "id" as the name of the id column.
     *
     * @return String
     */
    public String getIdColName()
    {
        return "id";
    }

    /**
     * Returns "name" as the name of the name column.
     *
     * @return String
     */
    public String getNameColName()
    {
        return "name";
    }
}