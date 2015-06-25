package com.zitego.sql;

import java.util.Vector;
import java.util.Calendar;
import java.math.BigDecimal;
import java.net.URL;
import java.io.Reader;
import java.io.InputStream;
import java.sql.*;

/**
 * Wraps a prepared statement for tracking and closing of result sets at a system level.
 *
 * @author John Glorioso
 * @version $Id: PreparedStatement.java,v 1.3 2011/10/16 17:33:19 jglorioso Exp $
 */
public class PreparedStatement implements java.sql.PreparedStatement
{
    private Vector<ResultSet> _resultSets = new Vector<ResultSet>();
    private java.sql.PreparedStatement _pst;

    PreparedStatement(java.sql.PreparedStatement pst)
    {
        _pst = pst;
    }

    private ResultSet getResultSet(ResultSet rs)
    {
        if ( !_resultSets.contains(rs) ) _resultSets.add(rs);
        return rs;
    }

    public ResultSet executeQuery() throws SQLException
    {
        return getResultSet( _pst.executeQuery() );
    }

    public ResultSet getGeneratedKeys() throws SQLException
    {
        return getResultSet( _pst.getGeneratedKeys() );
    }

    public ResultSet getResultSet() throws SQLException
    {
        return getResultSet( _pst.getResultSet() );
    }

    public ResultSet executeQuery(String sql) throws SQLException
    {
        return getResultSet( executeQuery(sql) );
    }

    public void close() throws SQLException
    {
        while (_resultSets.size() > 0)
        {
            ResultSet rs = _resultSets.get(0);
            if (rs != null)
            {
                try { rs.close(); } catch (SQLException e) { }
                _resultSets.remove(0);
                rs = null;
            }
        }
        _pst.close();
    }

    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        return _pst.getParameterMetaData();
    }

    public void setURL(int idx, URL x) throws SQLException
    {
        _pst.setURL(idx, x);
    }

    public void setNull(int idx, int type, String name) throws SQLException
    {
        _pst.setNull(idx, type, name);
    }

    public void setTimestamp(int idx, Timestamp x, Calendar cal) throws SQLException
    {
        _pst.setTimestamp(idx, x, cal);
    }

    public void setTime(int idx, Time x, Calendar cal) throws SQLException
    {
        _pst.setTime(idx, x, cal);
    }

    public void setDate(int idx, Date x, Calendar cal) throws SQLException
    {
        _pst.setDate(idx, x, cal);
    }

    public ResultSetMetaData getMetaData() throws SQLException
    {
        return _pst.getMetaData();
    }

    public void setArray(int i, Array x) throws SQLException
    {
        _pst.setArray(i, x);
    }

    public void setClob(int i, Clob x) throws SQLException
    {
        _pst.setClob(i, x);
    }

    public void setBlob(int i, Blob x) throws SQLException
    {
        _pst.setBlob(i, x);
    }

    public void setRef(int i, Ref x) throws SQLException
    {
        _pst.setRef(i, x);
    }

    public void setCharacterStream(int idx, Reader reader, int len) throws SQLException
    {
        setCharacterStream(idx, reader, len);
    }

    public void addBatch() throws SQLException
    {
        _pst.addBatch();
    }

    public boolean execute() throws SQLException
    {
        return _pst.execute();
    }

    public void setObject(int idx, Object x) throws SQLException
    {
        _pst.setObject(idx, x);
    }

    public void setObject(int idx, Object x, int type) throws SQLException
    {
        _pst.setObject(idx, x, type);
    }

    public void setObject(int idx, Object x, int type, int scale) throws SQLException
    {
        _pst.setObject(idx, x, type, scale);
    }

    public void clearParameters() throws SQLException
    {
        _pst.clearParameters();
    }

    public void setBinaryStream(int idx, InputStream x, int len) throws SQLException
    {
        _pst.setBinaryStream(idx, x, len);
    }

    public void setUnicodeStream(int idx, InputStream x, int len) throws SQLException
    {
        _pst.setUnicodeStream(idx, x, len);
    }

    public void setAsciiStream(int idx, InputStream x, int len) throws SQLException
    {
        _pst.setAsciiStream(idx, x, len);
    }

    public void setTimestamp(int idx, Timestamp x) throws SQLException
    {
        _pst.setTimestamp(idx, x);
    }

    public void setTime(int idx, Time x) throws SQLException
    {
        _pst.setTime(idx, x);
    }

    public void setDate(int idx, Date x) throws SQLException
    {
        _pst.setDate(idx, x);
    }

    public void setBytes(int idx, byte[] x) throws SQLException
    {
        _pst.setBytes(idx, x);
    }

    public void setString(int idx, String x) throws SQLException
    {
        _pst.setString(idx, x);
    }

    public void setBigDecimal(int idx, BigDecimal x) throws SQLException
    {
        _pst.setBigDecimal(idx, x);
    }

    public void setDouble(int idx, double x) throws SQLException
    {
        _pst.setDouble(idx, x);
    }

    public void setFloat(int idx, float x) throws SQLException
    {
        _pst.setFloat(idx, x);
    }

    public void setLong(int idx, long x) throws SQLException
    {
        _pst.setLong(idx, x);
    }

    public void setInt(int idx, int x) throws SQLException
    {
        _pst.setInt(idx, x);
    }

    public void setShort(int idx, short x) throws SQLException
    {
        _pst.setShort(idx, x);
    }

    public void setByte(int idx, byte x) throws SQLException
    {
        _pst.setByte(idx, x);
    }

    public void setBoolean(int idx, boolean x) throws SQLException
    {
        _pst.setBoolean(idx, x);
    }

    public void setNull(int idx, int type) throws SQLException
    {
        _pst.setNull(idx, type);
    }

    public int executeUpdate() throws SQLException
    {
        return _pst.executeUpdate();
    }

    public int getResultSetHoldability() throws SQLException
    {
        return _pst.getResultSetHoldability();
    }

    public boolean execute(String sql, String[] cols) throws SQLException
    {
        return _pst.execute(sql, cols);
    }

    public boolean execute(String sql, int[] cols) throws SQLException
    {
        return _pst.execute(sql, cols);
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
    {
        return _pst.execute(sql, autoGeneratedKeys);
    }

    public int executeUpdate(String sql, String[] cols) throws SQLException
    {
        return _pst.executeUpdate(sql, cols);
    }

    public int executeUpdate(String sql, int[] cols) throws SQLException
    {
        return _pst.executeUpdate(sql, cols);
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
    {
        return _pst.executeUpdate(sql, autoGeneratedKeys);
    }

    public boolean getMoreResults(int current) throws SQLException
    {
        return _pst.getMoreResults(current);
    }

    public java.sql.Connection getConnection() throws SQLException
    {
        return _pst.getConnection();
    }

    public int[] executeBatch() throws SQLException
    {
        return _pst.executeBatch();
    }

    public void clearBatch() throws SQLException
    {
        _pst.clearBatch();
    }

    public void addBatch(String sql) throws SQLException
    {
        _pst.addBatch(sql);
    }

    public int getResultSetType() throws SQLException
    {
        return _pst.getResultSetType();
    }

    public int getResultSetConcurrency() throws SQLException
    {
        return _pst.getResultSetConcurrency();
    }

    public int getFetchSize() throws SQLException
    {
        return _pst.getFetchSize();
    }

    public void setFetchSize(int rows) throws SQLException
    {
        _pst.setFetchSize(rows);
    }

    public int getFetchDirection() throws SQLException
    {
        return _pst.getFetchDirection();
    }

    public void setFetchDirection(int dir) throws SQLException
    {
        _pst.setFetchDirection(dir);
    }

    public boolean getMoreResults() throws SQLException
    {
        return _pst.getMoreResults();
    }

    public int getUpdateCount() throws SQLException
    {
        return _pst.getUpdateCount();
    }

    public boolean execute(String sql) throws SQLException
    {
        return _pst.execute(sql);
    }

    public void setCursorName(String name) throws SQLException
    {
        _pst.setCursorName(name);
    }

    public void clearWarnings() throws SQLException
    {
        _pst.clearWarnings();
    }

    public SQLWarning getWarnings() throws SQLException
    {
        return _pst.getWarnings();
    }

    public void cancel() throws SQLException
    {
        _pst.cancel();
    }

    public void setQueryTimeout(int secs) throws SQLException
    {
        _pst.setQueryTimeout(secs);
    }

    public int getQueryTimeout() throws SQLException
    {
        return _pst.getQueryTimeout();
    }

    public void setEscapeProcessing(boolean enable) throws SQLException
    {
        _pst.setEscapeProcessing(enable);
    }

    public void setMaxRows(int max) throws SQLException
    {
        _pst.setMaxRows(max);
    }

    public int getMaxRows() throws SQLException
    {
        return _pst.getMaxRows();
    }

    public void setMaxFieldSize(int max) throws SQLException
    {
        _pst.setMaxFieldSize(max);
    }

    public int getMaxFieldSize() throws SQLException
    {
        return _pst.getMaxFieldSize();
    }

    public int executeUpdate(String sql) throws SQLException
    {
        return _pst.executeUpdate(sql);
    }

    public void setNClob(int idx, Reader reader) throws SQLException
    {
        _pst.setNClob(idx, reader);
    }

    public void setNClob(int idx, NClob clob) throws SQLException
    {
        _pst.setNClob(idx, clob);
    }

    public void setNClob(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setNClob(idx, reader, len);
    }

    public void setBlob(int idx, InputStream in) throws SQLException
    {
        _pst.setBlob(idx, in);
    }

    public void setBlob(int idx, InputStream in, long len) throws SQLException
    {
        _pst.setBlob(idx, in, len);
    }

    public void setClob(int idx, Reader reader) throws SQLException
    {
        _pst.setClob(idx, reader);
    }

    public void setClob(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setClob(idx, reader, len);
    }

    public void setNCharacterStream(int idx, Reader reader) throws SQLException
    {
        _pst.setNCharacterStream(idx, reader);
    }

    public void setNCharacterStream(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setNCharacterStream(idx, reader, len);
    }

    public void setCharacterStream(int idx, Reader reader) throws SQLException
    {
        _pst.setCharacterStream(idx, reader);
    }

    public void setCharacterStream(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setCharacterStream(idx, reader, len);
    }

    public void setBinaryStream(int idx, InputStream in) throws SQLException
    {
        _pst.setBinaryStream(idx, in);
    }

    public void setBinaryStream(int idx, InputStream in, long len) throws SQLException
    {
        _pst.setBinaryStream(idx, in, len);
    }

    public void setAsciiStream(int idx, InputStream in) throws SQLException
    {
        _pst.setAsciiStream(idx, in);
    }

    public void setAsciiStream(int idx, InputStream in, long len) throws SQLException
    {
        _pst.setAsciiStream(idx, in, len);
    }

    public void setSQLXML(int idx, SQLXML xml) throws SQLException
    {
        _pst.setSQLXML(idx, xml);
    }

    public void setNString(int idx, String str) throws SQLException
    {
        _pst.setNString(idx, str);
    }

    public void setRowId(int idx, RowId id) throws SQLException
    {
        _pst.setRowId(idx, id);
    }

    public boolean isPoolable() throws SQLException
    {
        return _pst.isPoolable();
    }

    public void setPoolable(boolean flag) throws SQLException
    {
        _pst.setPoolable(flag);
    }

    public boolean isClosed() throws SQLException
    {
        return _pst.isClosed();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return _pst.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return _pst.unwrap(iface);
    }
}
