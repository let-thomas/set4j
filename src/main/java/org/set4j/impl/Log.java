package org.set4j.impl;

import java.util.Properties;

/**
 * Set4j logging class.
 * @author Tomas Mikenda
 */
public class Log
{
    public enum LogLevel { TRACE, DEBUG, INFO, ERROR };
    protected static LogLevel logLevel = LogLevel.INFO;

    static {
        setup(null);
    }

    public static void setup(Properties p)
    {
        String level = null;
        try {

            if (p != null) level = p.getProperty("set4j.loglevel");
            if (level == null) level = System.getProperty("set4j.loglevel", LogLevel.INFO.toString()).toUpperCase();
            else level = level.toUpperCase();
            logLevel = Enum.valueOf(LogLevel.class, level);
            Log.info("loglevel = " + logLevel);
        } catch (Exception e)
        {
            System.err.println("set4j: loglevel - unsupported value " + level + "; exc: "  + e);
        }
    }

    static void trace(String msg)
    {
        if (logLevel == LogLevel.TRACE)
        {
            printout(msg, null);
        }
    }

    static void trace(String msg, Throwable t)
    {
        if (logLevel == LogLevel.TRACE)
        {
            printout(msg, t);
        }
    }

    public static void debug(String msg)
    {
        if (logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG)
        {
            printout(msg, null);
        }
    }

    public static void debug(String msg, Throwable t)
    {
        if (logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG)
        {
            printout(msg, t);
        }
    }

    public static void info(String msg)
    {
        if (logLevel != LogLevel.ERROR)
        {
            printout(msg, null);
        }
    }

     static void info(String msg, Throwable t)
    {
        if (logLevel != LogLevel.ERROR)
        {
            printout(msg, t);
        }
    }

    static void error(String msg)
    {
        printout(msg, null);
    }

    static void error(String msg, Throwable t)
    {
        printout(msg, t);
    }

    public static boolean isDebug()
    {
        return (logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG);
    }

    private static void printout(String msg, Throwable t)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("set4j: ");
        buf.append(msg);
        System.out.println(buf.toString());
        //stack
        if (t != null) t.printStackTrace();
    }
}
