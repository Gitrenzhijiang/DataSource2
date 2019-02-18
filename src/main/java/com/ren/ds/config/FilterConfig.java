package com.ren.ds.config;


import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ren.ds.filter.FilterManager;


public class FilterConfig  {
    private static final Logger logger = Logger.getLogger((FilterConfig.class));
    private static final String CONFIG_FILE = "db.properties";
    public static Properties initConfig() {
        Properties properties = new Properties();
        try {
            properties.load(FilterConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
            // 解析所有用户配置的filter 放入FilterManager
            String filters = properties.getProperty("filters");
            if (filters != null && !"".equals(filters)) {
                filters.replaceAll(" ", "");
                String[] fs = filters.split(",");
                for (String fitem:fs) {
                    String[] item = fitem.split(":");
                    if (item[0] != null && item[1] != null)
                        // 注册进入FilterManager
                        FilterManager.put(item[0], item[1]);
                }
            }
        } catch (IOException e) {
            logger.error(e + " on parse "+ CONFIG_FILE);
        }
        return properties;
    }
}
