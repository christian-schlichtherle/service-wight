/*
 * Copyright (C) 2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.logging;

import java.text.MessageFormat;
import java.util.Formatter;
import java.util.ResourceBundle;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * A localized logger.
 * This logger looks up the resource bundle for the class provided to the
 * constructor and uses the logging messages as keys to this resource bundle.
 * Any arguments provided to the logging messages are applied to the looked up
 * resource bundle value by using a {@link Formatter} - not a
 * {@link MessageFormat}!
 * 
 * @author Christian Schlichtherle
 */
@ThreadSafe
public final class LocalizedLogger implements Logger {

    private final Logger logger;
    private final ResourceBundle bundle;

    public LocalizedLogger(final Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
        bundle = ResourceBundle.getBundle(clazz.getName());
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if (logger.isTraceEnabled())
            logger.trace(new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void trace(String format, Object arg) {
        if (logger.isTraceEnabled())
            logger.trace(new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (logger.isTraceEnabled())
            logger.trace(new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void trace(String format, Object[] argArray) {
        if (logger.isTraceEnabled())
            logger.trace(new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (logger.isTraceEnabled())
            logger.trace(new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (logger.isTraceEnabled(marker))
            logger.trace(marker, new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (logger.isTraceEnabled(marker))
            logger.trace(marker, new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isTraceEnabled(marker))
            logger.trace(marker, new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void trace(Marker marker, String format, Object[] argArray) {
        if (logger.isTraceEnabled(marker))
            logger.trace(marker, new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (logger.isTraceEnabled(marker))
            logger.trace(marker, new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if (logger.isDebugEnabled())
            logger.debug(new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void debug(String format, Object arg) {
        if (logger.isDebugEnabled())
            logger.debug(new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (logger.isDebugEnabled())
            logger.debug(new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void debug(String format, Object[] argArray) {
        if (logger.isDebugEnabled())
            logger.debug(new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (logger.isDebugEnabled())
            logger.debug(new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (logger.isDebugEnabled(marker))
            logger.debug(marker, new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (logger.isDebugEnabled(marker))
            logger.debug(marker, new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isDebugEnabled(marker))
            logger.debug(marker, new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void debug(Marker marker, String format, Object[] argArray) {
        if (logger.isDebugEnabled(marker))
            logger.debug(marker, new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (logger.isDebugEnabled(marker))
            logger.debug(marker, new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if (logger.isInfoEnabled())
            logger.info(new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void info(String format, Object arg) {
        if (logger.isInfoEnabled())
            logger.info(new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (logger.isInfoEnabled())
            logger.info(new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void info(String format, Object[] argArray) {
        if (logger.isInfoEnabled())
            logger.info(new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void info(String msg, Throwable t) {
        if (logger.isInfoEnabled())
            logger.info(new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (logger.isInfoEnabled(marker))
            logger.info(marker, new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (logger.isInfoEnabled(marker))
            logger.info(marker, new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isInfoEnabled(marker))
            logger.info(marker, new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void info(Marker marker, String format, Object[] argArray) {
        if (logger.isInfoEnabled(marker))
            logger.info(marker, new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (logger.isInfoEnabled(marker))
            logger.info(marker, new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if (logger.isWarnEnabled())
            logger.warn(new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void warn(String format, Object arg) {
        if (logger.isWarnEnabled())
            logger.warn(new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void warn(String format, Object[] argArray) {
        if (logger.isWarnEnabled())
            logger.warn(new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (logger.isWarnEnabled())
            logger.warn(new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (logger.isWarnEnabled())
            logger.warn(new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (logger.isWarnEnabled(marker))
            logger.warn(marker, new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (logger.isWarnEnabled(marker))
            logger.warn(marker, new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isWarnEnabled(marker))
            logger.warn(marker, new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void warn(Marker marker, String format, Object[] argArray) {
        if (logger.isWarnEnabled(marker))
            logger.warn(marker, new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (logger.isWarnEnabled(marker))
            logger.warn(marker, new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        if (logger.isErrorEnabled())
            logger.error(new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void error(String format, Object arg) {
        if (logger.isErrorEnabled())
            logger.error(new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (logger.isErrorEnabled())
            logger.error(new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void error(String format, Object[] argArray) {
        if (logger.isErrorEnabled())
            logger.error(new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void error(String msg, Throwable t) {
        if (logger.isErrorEnabled())
            logger.error(new BundledMessage(bundle, msg).toString(), t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (logger.isErrorEnabled(marker))
            logger.error(marker, new BundledMessage(bundle, msg).toString());
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (logger.isErrorEnabled(marker))
            logger.error(marker, new BundledMessage(bundle, format, arg).toString());
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isErrorEnabled(marker))
            logger.error(marker, new BundledMessage(bundle, format, arg1, arg2).toString());
    }

    @Override
    public void error(Marker marker, String format, Object[] argArray) {
        if (logger.isErrorEnabled(marker))
            logger.error(marker, new BundledMessage(bundle, format, argArray).toString());
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (logger.isErrorEnabled(marker))
            logger.error(marker, new BundledMessage(bundle, msg).toString(), t);
    }
}
