/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */

/**
 * @author Neil Ellis
 */

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class ClientLog {
    private static boolean loginWindowActive;
    @Nonnull
    private static       StringBuffer logBuffer         = new StringBuffer();
    private static final int          MAX_BUFFER_LENGTH = 50000;
    @Nullable
    public static  Element logWidget;
    private static boolean debugMode;
    @Nonnull
    private static final VortexThreadSafeExecutor executor = new VortexThreadSafeExecutor();

    public static String getLog() {
        return logBuffer.toString();
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(final boolean debugMode) {
        ClientLog.debugMode = debugMode;
    }

    public boolean isLogging() {
        return !GWT.isScript() || logWidget != null;
    }

    public static void log(@Nullable final String message, @Nullable final Throwable exception) {
        if (exception instanceof StatusCodeException && ((StatusCodeException) exception).getStatusCode() == 0) {
            return;
        }
        logInternal(message, exception);
        if (!GWT.isScript()) {
            if (exception != null) {
                exception.printStackTrace(System.err);
                if (message != null) {
                    System.out.println("ClientLog: " + message);
                }
                GWT.log(message != null ? message : exception.getMessage(), exception);
            }
            else if (message != null) {
                GWT.log(message, null);
            }
        }
        if (exception != null) {
            String trace = "";
            Throwable e = exception;
            while (e != null) {
                trace = buildTrace(e, trace);
                e = e.getCause();
            }
            if (debugMode) {
                Window.alert(message + ':' + exception.getMessage() + '\n' + trace);
            }
        }
    }

    private static String buildTrace(@Nonnull final Throwable exception, String trace) {
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        for (final StackTraceElement stackTraceElement : stackTrace) {
            trace = trace
                    + stackTraceElement.getClassName()
                    + "."
                    + stackTraceElement.getMethodName()
                    + "("
                    + stackTraceElement.getFileName()
                    + ":"
                    + stackTraceElement.getLineNumber()
                    + ")\n";
        }
        return trace;
    }


    public static void log(final Throwable exception) {
        log(null, exception);
    }


    public static void log(final String message) {
        log(message, null);
    }


    private static void logInternal(@Nullable final String message, @Nullable final Throwable exception) {
        if (isDebugMode()) {
            final StringBuffer localBuffer = new StringBuffer();
            if (exception != null) {
                localBuffer.append("*****************************\n");
            }
            if (message != null) {
                localBuffer.append("").append(new Date().toString()).append(":").append(message).append("\n");
            }
            if (exception != null) {
                localBuffer.append("**** ")
                           .append(new Date().toString())
                           .append(":")
                           .append(exceptionToString(exception))
                           .append(" ****\n");
            }
            if (exception != null) {
                localBuffer.append("******************************\n");
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    localBuffer.append(logBuffer);
                    logBuffer = localBuffer;
                    if (logBuffer.length() > MAX_BUFFER_LENGTH) {
                        logBuffer.delete(MAX_BUFFER_LENGTH, logBuffer.length() - 1);

                    }
                    if (logWidget != null) {
                        logWidget.setInnerHTML("<pre>" + logBuffer + "</pre>");
                    }
                }
            });
        }
    }

    @Nonnull
    private static String exceptionToString(@Nonnull final Throwable throwable) {
        if (throwable instanceof StatusCodeException) {
            final StatusCodeException sce = (StatusCodeException) throwable;
            if (sce.getStatusCode() == 401 && !loginWindowActive) {
                //Unauthorized
                loginWindowActive = true;
                return "Logged out";
            }
        }
        final StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        String details = throwable.getClass().getName() + ":" + throwable.getMessage();

        for (final StackTraceElement stackTraceElement : stackTraceElements) {
            details += "<p><font color='red'>"
                       + stackTraceElement.getClassName()
                       + ".<b>"
                       + stackTraceElement.getMethodName()
                       + "("
                       + stackTraceElement.getLineNumber()
                       + ")</b></font><br/></p>";
        }
        return details;
    }

    public static void assertTrue(final boolean b, final String message) {
        if (!b && ClientApplicationConfiguration.isDebug()) {
            Window.alert("Assertion failed " + message);
        }
    }

    public static void warn(@Nonnull final Exception e) {
        warn(e.getMessage());
        e.printStackTrace(System.err);
    }

    public static void warn(@Nonnull final String message) {
        log("WARN: " + message);
    }
}
