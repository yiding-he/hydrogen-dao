package com.hyd.dao.database.dialects.impl;

import com.hyd.dao.DAOException;
import com.hyd.dao.database.FQN;
import com.hyd.dao.database.dialects.Dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class H2Dialect implements Dialect {

    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile(".*(HSQL|H2).*");

    @Override
    public Predicate<Connection> getMatcher() {
        return c -> {
            try {
                return PRODUCT_NAME_PATTERN.matcher(c.getMetaData().getDatabaseProductName()).matches();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        };
    }

    @Override
    public String wrapRangeQuery(String sql, int startPos, int endPos) {
        return null;
    }

    @Override
    public MetaNameConvention getMetaNameConvention() {
        return MetaNameConvention.Uppercase;
    }

    @Override
    public String fixCatalog(String connectionCatalog, FQN fqn) {
        return null;  // 一律返回 null
    }

    @Override
    public String identityQuoter() {
        return "";   // 不作引用
    }
}
