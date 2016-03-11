package com.lightchat.ims.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

/**
 * 
 * 
 * @author jiapeng.li
 * @version $Id: LoggingHelper.java, v 0.1 Jan 13, 2015 9:52:23 PM nijiaben Exp $
 */
public class LoggingHelper {
    private static final LoggingHelper instance = new LoggingHelper();

    private LoggingHelper() {
    }

    public static LoggingHelper getInstance() {
        return instance;
    }

    public InputStream evaluate(Properties p, InputStream inputStream) {
        return evaluate(p, new InputStreamReader(inputStream));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private InputStream evaluate(Map map, Reader reader) {
        try {
            VelocityContext context = convertVelocityContext(map);
            CharArrayWriter writer = new CharArrayWriter();
            evaluate(context, writer, reader);
            byte[] dataBytes = writer.toString().getBytes();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(dataBytes));
            return bis;
        } catch (Exception e) {
            throw new RuntimeException("velocity evaluate error! detial [" + e.getMessage() + "]");
        }
    }

    private VelocityContext convertVelocityContext(Map<String, Object> map) {
        VelocityContext context = new VelocityContext();
        if (map == null)
            return context;
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            context.put(entry.getKey(), entry.getValue());
        }
        return context;
    }

    private boolean evaluate(Context context, Writer writer, Reader reader) {
        try {
            return Velocity.evaluate(context, writer, "", reader);
        } catch (Exception e) {
            throw new RuntimeException("velocity evaluate error! detial [" + e.getMessage() + "]");
        }
    }
}
