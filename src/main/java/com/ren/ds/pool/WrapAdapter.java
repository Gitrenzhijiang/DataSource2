package com.ren.ds.pool;

import java.sql.SQLException;
import java.sql.Wrapper;

public class WrapAdapter implements Wrapper{

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface != null && iface.isInstance(this)) {
            return (T) this;
        }
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface != null && iface.isInstance(this)) {
            return true;
        }
        return false;
    }

}
