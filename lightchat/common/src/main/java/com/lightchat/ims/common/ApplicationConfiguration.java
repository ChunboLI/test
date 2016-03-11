package com.lightchat.ims.common;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
/**
 * 
 * @author chunbo
 * @version $Id: ApplicationConfiguration.java, v 0.1 2016�?�?1�?下午5:53:12 chunbo Exp $
 */
public class ApplicationConfiguration {
    public static final Logger logger            = LoggerFactory.getLogger(ApplicationConfiguration.class);
    // 服务器主机地�?
    public static String        SERVER_HOST       = "server.host";
    public static String        SERVER_HOST_DEBUG = "server.host.debug";
    public static String        UTS_DEBUG         = "uts.debug";
    private static Properties   properties;

    public static void setProperties(Properties properties) {
        ApplicationConfiguration.properties = properties;
        if (System.getProperty(UTS_DEBUG) != null && System.getProperty(UTS_DEBUG).contains("true")) {
            logger.warn("当前配置文件为DEBUG模式");
            SERVER_HOST = properties.getProperty(SERVER_HOST_DEBUG);
        } else {
            SERVER_HOST = properties.getProperty(SERVER_HOST);
        }
        Assert.hasText(SERVER_HOST, "没有设置服务器主机地");
        logger.info("当前服务器主机地" + SERVER_HOST);
    }

    public static Properties getProperties() {
        return ApplicationConfiguration.properties;
    }

    public static String getPropertyValue(String key) {
        String result = null;
        if (null != properties) {
            result = properties.getProperty(key);
        }
        return result;
    }

    public static String getPropertyValue(String key, String defaultVal) {
        String val = getPropertyValue(key);
        if (val == null) {
            return defaultVal;
        }
        return val;
    }
}
