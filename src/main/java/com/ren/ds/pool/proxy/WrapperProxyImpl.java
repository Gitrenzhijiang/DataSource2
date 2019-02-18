package com.ren.ds.pool.proxy;

import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.HashMap;
import java.util.Map;

import com.ren.ds.filter.FilterChain;


public abstract class WrapperProxyImpl implements WrapperProxy {

    private final Wrapper       raw;

    private final long          id;

    private Map<String, Object> attributes; // 不需要线程安全

    public WrapperProxyImpl(Wrapper wrapper, long id){
        this.raw = wrapper;
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public Object getRawObject() {
        return raw;
    }

    public abstract FilterChain createChain();

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }

        if (iface == this.getClass()) {
            return true;
        }

        return createChain().isWrapperFor(raw, iface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == null) {
            return null;
        }

        if (iface == this.getClass()) {
            return (T) this;
        }

        return createChain().unwrap(raw, iface);
    }
    
    public int getAttributesSize() {
        if (attributes == null) {
            return 0;
        }
        
        return attributes.size();
    }
    
    public void clearAttributes() {
        if (this.attributes == null) {
            return;
        }
        
        this.attributes.clear();
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(4);
        }
        return this.attributes;
    }
    
    public void putAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(4);
        }
        this.attributes.put(key, value);
    }

    public Object getAttribute(String key){
        if (attributes == null) {
            return null;
        }
        
        return this.attributes.get(key);
    }
    
}
