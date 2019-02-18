package com.ren.ds.utils;

import java.util.Properties;

public class Util {
    
    public static Boolean getBoolean(Properties properties, String key) {
        String s = properties.getProperty(key);
        if (s == null) {
            return null;
        }
        return Boolean.valueOf(s);
    }
    
    public static Integer getInteger(Properties properties, String key) {
        String s = properties.getProperty(key);
        if (s == null) {
            return null;
        }
        return Integer.valueOf(s);
    }
    
    public static Long getLong(Properties properties, String key) {
        String s = properties.getProperty(key);
        if (s == null) {
            return null;
        }
        return Long.valueOf(s);
    }
}
