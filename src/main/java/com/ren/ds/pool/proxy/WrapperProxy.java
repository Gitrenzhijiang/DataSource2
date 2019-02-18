package com.ren.ds.pool.proxy;

import java.sql.Wrapper;
import java.util.Map;
/**
 * 所有的代理接口都必须继承这个父类
 * @author REN
 *
 */
public interface WrapperProxy extends Wrapper {

    long getId();

    Object getRawObject();

    int getAttributesSize();

    void clearAttributes();

    Map<String, Object> getAttributes();

    Object getAttribute(String key);

    void putAttribute(String key, Object value);
}