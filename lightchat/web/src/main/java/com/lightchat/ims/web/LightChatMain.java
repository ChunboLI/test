package com.lightchat.ims.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

import com.lightchat.ims.common.ApplicationConfiguration;
import com.lightchat.ims.common.LoggingHelper;

/**
 * 
 * @author chunbo
 *
 */
public class LightChatMain {
    private static final String XINGEIMS_CONFIG      = "lightchat.config";
    private static final String XINGEIMS_LOG4J       = "lightchat.log4j";
    private static final String XINGEIMS_CONFIG_FILE = "lightchat-config.properties";

    public static void main(String[] args) {
        Properties p = configInit();
        logInit(p);
        jettyStart(p);
    }

    private static void jettyStart(Properties properties) {
        EmbeddedJettyServer jettyServer = new EmbeddedJettyServer(properties);
        jettyServer.start();
    }

    private static void logInit(Properties p) {
        String ggtz_log4j = System.getProperty(XINGEIMS_LOG4J);
        if (StringUtils.isNotBlank(ggtz_log4j)) {
            File f = new File(ggtz_log4j);
            InputStream in = null;
            try {
                in = new FileInputStream(f);
                DOMConfigurator domConf = new DOMConfigurator();
                InputStream inputStream = LoggingHelper.getInstance().evaluate(p, in);
                domConf.doConfigure(inputStream, LogManager.getLoggerRepository());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static Properties configInit() {
        if (StringUtils.isBlank(System.getProperty(EmbeddedJettyServer.JETTY_PORT))) {
            System.setProperty(EmbeddedJettyServer.JETTY_PORT, "8010");
        }
        if (StringUtils.isBlank(System.getProperty(EmbeddedJettyServer.JETTY_CONTEXT_PATH))) {
            System.setProperty(EmbeddedJettyServer.JETTY_CONTEXT_PATH, "/");
        }
        if (StringUtils.isBlank(System.getProperty(EmbeddedJettyServer.JETTY_THREAD_COUNTS))) {
            System.setProperty(EmbeddedJettyServer.JETTY_THREAD_COUNTS, "200");
        }
        if (StringUtils.isBlank(System.getProperty(EmbeddedJettyServer.JETTY_ACCESS_LOG))) {
            System.setProperty(EmbeddedJettyServer.JETTY_ACCESS_LOG, "false");
        }
        String zdebugger_config = System.getProperty(XINGEIMS_CONFIG);
        if (StringUtils.isBlank(zdebugger_config)) {
            zdebugger_config = LightChatMain.class.getClassLoader().getResource(XINGEIMS_CONFIG_FILE).getFile();
        }
        System.setProperty(XINGEIMS_CONFIG, zdebugger_config);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(zdebugger_config));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Properties sps = System.getProperties();
        for (Object key : sps.keySet()) {
            String name = key.toString().trim();
            if (StringUtils.isBlank(name))
                continue;
            String value = sps.get(key).toString().trim();
            properties.setProperty(name, value);
        }

        ApplicationConfiguration.setProperties(properties);
        return properties;
    }
}
