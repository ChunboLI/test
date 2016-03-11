package com.lightchat.ims.web;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
/**
 * 
 * @author chunbo
 *
 * @version $Id: EmbeddedJettyServer.java, v 0.1 2016年1月11日 下午6:03:47 chunbo Exp $
 */
public class EmbeddedJettyServer {

    public static final String JETTY_PORT          = "jetty.port";
    public static final String JETTY_WAR           = "jetty.war";
    public static final String JETTY_CONTEXT_PATH  = "jetty.context.path";
    public static final String JETTY_THREAD_COUNTS = "jetty.thread.counts";
    public static final String JETTY_ACCESS_LOG    = "jetty.access.log";

    protected int              port;

    protected String           warPath             = "";

    protected String           contextPath         = "";

    protected int              idleMilliseconds    = 30000;

    protected int              threadCounts        = 50;

    protected int              requestBufferSize   = 8 * 1024;

    protected int              headerBufferSize    = 16 * 1024;

    protected boolean          enableAccessLog     = false;

    private Server             server;

    public EmbeddedJettyServer(int port) {
        this.port = port;
    }

    public EmbeddedJettyServer(Properties properties) {
        String port = (String) properties.get(JETTY_PORT);
        if (StringUtils.isNotBlank(port)) {
            setPort(Integer.valueOf(port));
        }
        String warPath = (String) properties.get(JETTY_WAR);
        if (StringUtils.isNotBlank(warPath)) {
            setWarPath(warPath);
        }
        String contextPath = (String) properties.get(JETTY_CONTEXT_PATH);
        if (StringUtils.isNotBlank(contextPath)) {
            setContextPath(contextPath);
        }
        String threadCounts = (String) properties.get(JETTY_THREAD_COUNTS);
        if (StringUtils.isNotBlank(threadCounts)) {
            setThreadCounts(Integer.valueOf(threadCounts));
        }
        String needAccessLogStr = (String) properties.get(JETTY_ACCESS_LOG);
        if (needAccessLogStr != null) {
            try {
                Boolean needAccessLog = Boolean.valueOf(needAccessLogStr);
                setEnableAccessLog(needAccessLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    final public void start() {
        init();
        beforeServerStart();
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        afterServerStart();
    }

    protected void beforeServerStart() {
    }

    protected void afterServerStart() {
    }

    private void init() {
        String port = System.getProperty(JETTY_PORT);
        if (port != null) {
            setPort(Integer.valueOf(port));
        }
        String warPath = System.getProperty(JETTY_WAR);
        if (warPath != null) {
            setWarPath(warPath);
        }

        String contextPath = System.getProperty(JETTY_CONTEXT_PATH);
        if (contextPath != null) {
            setContextPath(contextPath);
        }

        String threadCounts = System.getProperty(JETTY_THREAD_COUNTS);
        if (threadCounts != null) {
            setThreadCounts(Integer.valueOf(threadCounts));
        }

        String needAccessLogStr = System.getProperty(JETTY_ACCESS_LOG);
        if (needAccessLogStr != null) {
            try {
                Boolean needAccessLog = Boolean.valueOf(needAccessLogStr);
                setEnableAccessLog(needAccessLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startServer() throws Exception {
        config();
        Runtime.getRuntime().addShutdownHook(new Thread("Embedded-Jetty-Shutdown-Hooker") {
            @Override
            public void run() {
                try {
                    server.stop();
                    server.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        server.start();
        server.join();
    }

    private void config() {

        QueuedThreadPool threadPool = new QueuedThreadPool(threadCounts);
        threadPool.setName("ggtz-Jetty-ThreadPool");
        server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setIdleTimeout(idleMilliseconds);

        server.setConnectors(new Connector[] { connector });
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(contextPath);
        webAppContext.setWar(warPath);
        webAppContext.setMaxFormContentSize(-1);
        //        EnumSet<DispatcherType> dtype = EnumSet.of(DispatcherType.REQUEST);
        //        LoginFilter loginFilter = new LoginFilter();
        //        List<String> blackList = new ArrayList<String>();
        //        blackList.add(RequestUrlConstant.PEIZI_ACCOUNT);
        //        loginFilter.setBlackList(blackList);
        //        webAppContext.addFilter(new FilterHolder(loginFilter), "/*", dtype);
        server.setHandler(webAppContext);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getIdleMilliseconds() {
        return idleMilliseconds;
    }

    public void setIdleMilliseconds(int idleMilliseconds) {
        this.idleMilliseconds = idleMilliseconds;
    }

    public int getThreadCounts() {
        return threadCounts;
    }

    public void setThreadCounts(int threadCounts) {
        this.threadCounts = threadCounts;
    }

    public Server getServer() {
        return server;
    }

    public String getWarPath() {
        return warPath;
    }

    public void setWarPath(String warPath) {
        this.warPath = warPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public boolean isEnableAccessLog() {
        return enableAccessLog;
    }

    public void setEnableAccessLog(boolean enableAccessLog) {
        this.enableAccessLog = enableAccessLog;
    }

}
