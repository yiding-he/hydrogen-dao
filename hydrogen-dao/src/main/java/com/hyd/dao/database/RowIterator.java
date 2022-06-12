package com.hyd.dao.database;

import com.hyd.dao.DAOException;
import com.hyd.dao.Row;
import com.hyd.dao.database.type.NameConverter;
import com.hyd.dao.database.type.TypeConverter;
import com.hyd.dao.mate.util.ResultSetUtil;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.hyd.dao.mate.util.Closer.closeResultSet;

/**
 * <p>查询结果迭代器。当查询返回大量结果，又没有足够的内存进行缓存时，可以使用 DAO.queryIterator
 * 方法。该方法返回一个迭代器，用来每次获取一行查询结果。当处理完毕后，请务必记得将其关闭。</p>
 */
public class RowIterator implements Closeable, Iterable<Row> {

    private final ResultSet rs;

    private NameConverter nameConverter;

    private Consumer<Row> rowPreProcessor;

    private boolean closed;

    public RowIterator(ResultSet rs) {
        this(rs, null);
    }

    public RowIterator(ResultSet rs, Consumer<Row> rowPreProcessor) {
        this.rs = rs;
        this.rowPreProcessor = rowPreProcessor;

        if (this.rs == null) {
            closed = true;
        }
    }

    public void setRowPreProcessor(Consumer<Row> rowPreProcessor) {
        this.rowPreProcessor = rowPreProcessor;
    }

    public void setNameConverter(NameConverter nameConverter) {
        this.nameConverter = nameConverter;
    }

    /**
     * 获得是否还有查询结果
     *
     * @return 如果还有查询结果则返回 true，并移至下一行
     */
    public boolean next() {

        if (closed) {
            return false;
        }

        try {
            if (rs.isClosed()) {
                return false;
            }

            boolean next = rs.next();
            if (!next) {
                close();
            }
            return next;
        } catch (SQLException e) {
            throw new DAOException("failed to read next record", e);
        }
    }

    /**
     * 获得查询结果中的当前行
     *
     * @return 当前行的内容
     */
    public Row getRow() {
        try {
            Row row = ResultSetUtil.readRow(rs);
            if (this.rowPreProcessor != null) {
                this.rowPreProcessor.accept(row);
            }
            return row;
        } catch (IOException | SQLException e) {
            throw new DAOException("failed to read record", e);
        }
    }

    @Override
    public void close() {

        if (closed) {
            return;
        }

        try {
            closeResultSet(rs);
        } finally {
            closed = true;
        }
    }

    @Override
    public Iterator<Row> iterator() {
        return iterator(Row.class);
    }

    public <T> void forEach(Class<T> type, Consumer<T> action) {
        iterator(type).forEachRemaining(action);
    }

    @SuppressWarnings("unchecked")
    private <T> Iterator<T> iterator(Class<T> type) {
        Function<Row, T> converter = row -> {
            if (type.isAssignableFrom(Row.class)) {
                return (T) row;
            } else {
                try {
                    return (T) TypeConverter.convertRow(type, row, nameConverter);
                } catch (Throwable e) {
                    throw DAOException.wrap(e);
                }
            }
        };

        return new Iterator<>() {
            private T next = null;

            private void fetchNext() {
                if (RowIterator.this.next()) {
                    this.next = converter.apply(RowIterator.this.getRow());
                } else {
                    this.next = null;
                }
            }

            {
                fetchNext();
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public T next() {
                T result = this.next;
                fetchNext();
                return result;
            }
        };
    }
}
