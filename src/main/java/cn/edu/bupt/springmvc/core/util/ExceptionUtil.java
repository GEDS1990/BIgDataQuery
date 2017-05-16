package cn.edu.bupt.springmvc.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by limingkun on 2017/5/7.
 */
public class ExceptionUtil {
    public static String printStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }
}
