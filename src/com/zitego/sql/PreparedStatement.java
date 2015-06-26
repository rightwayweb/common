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
    private Vector<ResultSet> _resultSets = new Vector<>();
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

    @Override
    public ResultSet executeQuery() throws SQLException
    {
        return getResultSet( _pst.executeQuery() );
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException
    {
        return getResultSet( _pst.getGeneratedKeys() );
    }

    @Override
    public ResultSet getResultSet() throws SQLException
    {
        return getResultSet( _pst.getResultSet() );
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException
    {
        return getResultSet( executeQuery(sql) );
    }

    @Override
    public void close() throws SQLException
    {
        while (_resultSets.size() > 0)
        {
            ResultSet rs = _resultSets.get(0);
            if (rs != null)
            {
                try { rs.close(); } catch (SQLException ex) { }
                _resultSets.remove(0);
            }
        }
        _pst.close();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        return _pst.getParameterMetaData();
    }

    @Override
    public void setURL(int idx, URL x) throws SQLException
    {
        _pst.setURL(idx, x);
    }

    @Override
    public void setNull(int idx, int type, String name) throws SQLException
    {
        _pst.setNull(idx, type, name);
    }

    @Override
    public void setTimestamp(int idx, Timestamp x, Calendar cal) throws SQLException
    {
        _pst.setTimestamp(idx, x, cal);
    }

    @Override
    public void setTime(int idx, Time x, Calendar cal) throws SQLException
    {
        _pst.setTime(idx, x, cal);
    }

    @Override
    public void setDate(int idx, Date x, Calendar cal) throws SQLException
    {
        _pst.setDate(idx, x, cal);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        return _pst.getMetaData();
    }

    @Override
    public void setArray(int i, Array x) throws SQLException
    {
        _pst.setArray(i, x);
    }

    @Override
    public void setClob(int i, Clob x) throws SQLException
    {
        _pst.setClob(i, x);
    }

    @Override

    public void setBlob(int i, Blob x) throws SQLException
    {
        _pst.setBlob(i, x);
    }

    @Override
    public void setRef(int i, Ref x) throws SQLException
    {
        _pst.setRef(i, x);
    }

    @Override
    public void setCharacterStream(int idx, Reader reader, int len) throws SQLException
    {
        _pst.setCharacterStream(idx, reader, len);
    }

    @Override
    public void addBatch() throws SQLException
    {
        _pst.addBatch();
    }

    @Override
    public boolean execute() throws SQLException
    {
        return _pst.execute();
    }

    @Override
    public void setObject(int idx, Object x) throws SQLException
    {
        _pst.setObject(idx, x);
    }

    @Override
    public void setObject(int idx, Object x, int type) throws SQLException
    {
        _pst.setObject(idx, x, type);
    }

    @Override
    public void setObject(int idx, Object x, int type, int scale) throws SQLException
    {
        _pst.setObject(idx, x, type, scale);
    }

    @Override
    public void clearParameters() throws SQLException
    {
        _pst.clearParameters();
    }

    @Override
    public void setBinaryStream(int idx, InputStream x, int len) throws SQLException
    {
        _pst.setBinaryStream(idx, x, len);
    }

    @Override
    /*
     * @deprecated
     */
    public void setUnicodeStream(int idx, InputStream x, int len) throws SQLException
    {
        _pst.setUnicodeStream(idx, x, len);
    }

    @Override
    public void setAsciiStream(int idx, InputStream x, int len) throws SQLException
    {
        _pst.setAsciiStream(idx, x, len);
    }

    @Override
    public void setTimestamp(int idx, Timestamp x) throws SQLException
    {
        _pst.setTimestamp(idx, x);
    }

    @Override
    public void setTime(int idx, Time x) throws SQLException
    {
        _pst.setTime(idx, x);
    }

    @Override

    public void setDate(int idx, Date x) throws SQLException
    {
        _pst.setDate(idx, x);
    }

    @Override
    public void setBytes(int idx, byte[] x) throws SQLException
    {
        _pst.setBytes(idx, x);
    }

    @Override
    public void setString(int idx, String x) throws SQLException
    {
        _pst.setString(idx, x);
    }

    @Override
    public void setBigDecimal(int idx, BigDecimal x) throws SQLException
    {
        _pst.setBigDecimal(idx, x);
    }

    @Override
    public void setDouble(int idx, double x) throws SQLException
    {
        _pst.setDouble(idx, x);
    }

    @Override
    public void setFloat(int idx, float x) throws SQLException
    {
        _pst.setFloat(idx, x);
    }

    @Override
    public void setLong(int idx, long x) throws SQLException
    {
        _pst.setLong(idx, x);
    }

    @Override
    public void setInt(int idx, int x) throws SQLException
    {
        _pst.setInt(idx, x);
    }

    @Override

    public void setShort(int idx, short x) throws SQLException
    {
        _pst.setShort(idx, x);
    }

    @Override
    public void setByte(int idx, byte x) throws SQLException
    {
        _pst.setByte(idx, x);
    }

    @Override
    public void setBoolean(int idx, boolean x) throws SQLException
    {
        _pst.setBoolean(idx, x);
    }

    @Override
    public void setNull(int idx, int type) throws SQLException
    {
        _pst.setNull(idx, type);
    }

    @Override
    public int executeUpdate() throws SQLException
    {
        return _pst.executeUpdate();
    }

    @Override
    public int getResultSetHoldability() throws SQLException
    {
        return _pst.getResultSetHoldability();
    }

    @Override
    public boolean execute(String sql, String[] cols) throws SQLException
    {
        return _pst.execute(sql, cols);
    }

    @Override
    public boolean execute(String sql, int[] cols) throws SQLException
    {
        return _pst.execute(sql, cols);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
    {
        return _pst.execute(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, String[] cols) throws SQLException
    {
        return _pst.executeUpdate(sql, cols);
    }

    @Override
    public int executeUpdate(String sql, int[] cols) throws SQLException
    {
        return _pst.executeUpdate(sql, cols);
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
    {
        return _pst.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException
    {
        return _pst.getMoreResults(current);
    }

    @Override
    public java.sql.Connection getConnection() throws SQLException
    {
        return _pst.getConnection();
    }

    @Override
    public int[] executeBatch() throws SQLException
    {
        return _pst.executeBatch();
    }

    @Override
    public void clearBatch() throws SQLException
    {
        _pst.clearBatch();
    }

    @Override
    public void addBatch(String sql) throws SQLException
    {
        _pst.addBatch(sql);
    }

    @Override
    public int getResultSetType() throws SQLException
    {
        return _pst.getResultSetType();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException
    {
        return _pst.getResultSetConcurrency();
    }

    @Override
    public int getFetchSize() throws SQLException
    {
        return _pst.getFetchSize();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException
    {
        _pst.setFetchSize(rows);
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return _pst.getFetchDirection();
    }

    @Override
    public void setFetchDirection(int dir) throws SQLException
    {
        _pst.setFetchDirection(dir);
    }

    @Override
    public boolean getMoreResults() throws SQLException
    {
        return _pst.getMoreResults();
    }

    @Override
    public int getUpdateCount() throws SQLException
    {
        return _pst.getUpdateCount();
    }

    @Override
    public boolean execute(String sql) throws SQLException
    {
        return _pst.execute(sql);
    }

    @Override
    public void setCursorName(String name) throws SQLException
    {
        _pst.setCursorName(name);
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        _pst.clearWarnings();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return _pst.getWarnings();
    }

    @Override

    public void cancel() throws SQLException
    {
        _pst.cancel();
    }

    @Override
    public void setQueryTimeout(int secs) throws SQLException
    {
        _pst.setQueryTimeout(secs);
    }

    @Override
    public int getQueryTimeout() throws SQLException
    {
        return _pst.getQueryTimeout();
    }

    @Override

    public void setEscapeProcessing(boolean enable) throws SQLException
    {
        _pst.setEscapeProcessing(enable);
    }

    @Override

    public void setMaxRows(int max) throws SQLException
    {
        _pst.setMaxRows(max);
    }

    @Override
    public int getMaxRows() throws SQLException
    {
        return _pst.getMaxRows();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException
    {
        _pst.setMaxFieldSize(max);
    }

    @Override
    public int getMaxFieldSize() throws SQLException
    {
        return _pst.getMaxFieldSize();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException
    {
        return _pst.executeUpdate(sql);
    }

    @Override
    public void setNClob(int idx, Reader reader) throws SQLException
    {
        _pst.setNClob(idx, reader);
    }

    @Override
    public void setNClob(int idx, NClob clob) throws SQLException
    {
        _pst.setNClob(idx, clob);
    }

    @Override
    public void setNClob(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setNClob(idx, reader, len);
    }

    @Override
    public void setBlob(int idx, InputStream in) throws SQLException
    {
        _pst.setBlob(idx, in);
    }

    @Override
    public void setBlob(int idx, InputStream in, long len) throws SQLException
    {
        _pst.setBlob(idx, in, len);
    }

    @Override
    public void setClob(int idx, Reader reader) throws SQLException
    {
        _pst.setClob(idx, reader);
    }

    @Override
    public void setClob(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setClob(idx, reader, len);
    }

    @Override
    public void setNCharacterStream(int idx, Reader reader) throws SQLException
    {
        _pst.setNCharacterStream(idx, reader);
    }

    @Override
    public void setNCharacterStream(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setNCharacterStream(idx, reader, len);
    }

    @Override
    public void setCharacterStream(int idx, Reader reader) throws SQLException
    {
        _pst.setCharacterStream(idx, reader);
    }

    @Override
    public void setCharacterStream(int idx, Reader reader, long len) throws SQLException
    {
        _pst.setCharacterStream(idx, reader, len);
    }

    @Override
    public void setBinaryStream(int idx, InputStream in) throws SQLException
    {
        _pst.setBinaryStream(idx, in);
    }

    @Override
    public void setBinaryStream(int idx, InputStream in, long len) throws SQLException
    {
        _pst.setBinaryStream(idx, in, len);
    }

    @Override
    public void setAsciiStream(int idx, InputStream in) throws SQLException
    {
        _pst.setAsciiStream(idx, in);
    }

    @Override
    public void setAsciiStream(int idx, InputStream in, long len) throws SQLException
    {
        _pst.setAsciiStream(idx, in, len);
    }

    @Override
    public void setSQLXML(int idx, SQLXML xml) throws SQLException
    {
        _pst.setSQLXML(idx, xml);
    }

    @Override
    public void setNString(int idx, String str) throws SQLException
    {
        _pst.setNString(idx, str);
    }

    @Override
    public void setRowId(int idx, RowId id) throws SQLException
    {
        _pst.setRowId(idx, id);
    }

    @Override
    public boolean isPoolable() throws SQLException
    {
        return _pst.isPoolable();
    }

    @Override
    public void setPoolable(boolean flag) throws SQLException
    {
        _pst.setPoolable(flag);
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return _pst.isClosed();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return _pst.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return _pst.unwrap(iface);
    }

    @Override
    public void closeOnCompletion() throws SQLException
    {
        _pst.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException
    {
        return _pst.isCloseOnCompletion();
    }
}
