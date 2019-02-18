package com.ren.ds.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class FilterManager {
    private static final Logger logger = Logger.getLogger(FilterManager.class);
    /**
     * filterName - filterClassName
     */
    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>(16, 0.75f, 1);
    static {
        try {
            Properties filterProperties = loadFilterConfig();
            for (Map.Entry<Object, Object> entry : filterProperties.entrySet()) {
                String key = (String) entry.getKey();
                map.put(key, (String) entry.getValue());
            }
        } catch (Exception e) {
            logger.error("load filters file error");
        }
    }
    public static void put(String asName, String className) {
        map.putIfAbsent(asName, className);
    }
    public static String getFilter(String key) {
        return map.get(key);
    }
    public static List<Filter> getFilters() {
        List<Filter> lists = new ArrayList<>();
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            try {
                String key = (String) iterator.next();
                Class<Filter> clazz;
                clazz = (Class<Filter>) Class.forName(map.get(key));
                lists.add(clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lists;
    }
    private static Properties loadFilterConfig() throws IOException {
        Properties properties = new Properties();
        InputStream is = FilterManager.class.getClassLoader()
                .getResourceAsStream("META-INF/filters.properties");
        properties.load(is);
        return properties;
    }
    
}
