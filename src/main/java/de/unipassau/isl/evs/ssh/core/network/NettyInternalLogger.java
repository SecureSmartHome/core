/*
 * MIT License
 *
 * Copyright (c) 2016.
 * Bucher Andreas, Fink Simon Dominik, Fraedrich Christoph, Popp Wolfgang,
 * Sell Leon, Werli Philemon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.unipassau.isl.evs.ssh.core.network;

import io.netty.util.internal.logging.AbstractInternalLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * Simple InternalNetty logger for the netty framework which will, unlike the default logger, also log VERBOSE and DEBUG entries.
 *
 * @author Niko Fink
 */
@SuppressWarnings("ALL")
public class NettyInternalLogger extends AbstractInternalLogger {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public NettyInternalLogger(String name) {
        super(name);
    }

    public boolean isTraceEnabled() {
        return true;
        //return Log.isLoggable(name, Log.VERBOSE);
    }

    public boolean isDebugEnabled() {
        return true;
        //return Log.isLoggable(name, Log.DEBUG);
    }

    public boolean isInfoEnabled() {
        return true;
        //return Log.isLoggable(name, Log.INFO);
    }

    public boolean isWarnEnabled() {
        return true;
        //return Log.isLoggable(name, Log.WARN);
    }

    public boolean isErrorEnabled() {
        return true;
        //return Log.isLoggable(name, Log.ERROR);
    }

    public void trace(final String msg) {
        logger.trace(name(), msg);
    }

    public void trace(final String format, final Object param1) {
        logger.trace(name(), format(format, param1, null));
    }

    public void trace(final String format, final Object param1, final Object param2) {
        logger.trace(name(), format(format, param1, param2));
    }

    public void trace(final String format, final Object... arguments) {
        logger.trace(name(), format(format, arguments));
    }

    public void trace(final String msg, final Throwable t) {
        logger.trace(name(), msg, t);
    }

    public void debug(final String msg) {
        logger.debug(name(), msg);
    }

    public void debug(final String format, final Object arg1) {
        logger.debug(name(), format(format, arg1, null));
    }

    public void debug(final String format, final Object param1, final Object param2) {
        logger.debug(name(), format(format, param1, param2));
    }

    public void debug(final String format, final Object... arguments) {
        logger.debug(name(), format(format, arguments));
    }

    public void debug(final String msg, final Throwable t) {
        logger.debug(name(), msg, t);
    }

    public void info(final String msg) {
        logger.info(name(), msg);
    }

    public void info(final String format, final Object arg) {
        logger.info(name(), format(format, arg, null));
    }

    public void info(final String format, final Object arg1, final Object arg2) {
        logger.info(name(), format(format, arg1, arg2));
    }

    public void info(final String format, final Object... arguments) {
        logger.info(name(), format(format, arguments));
    }

    public void info(final String msg, final Throwable t) {
        logger.info(name(), msg, t);
    }

    public void warn(final String msg) {
        logger.warn(name(), msg);
    }

    public void warn(final String format, final Object arg) {
        logger.warn(name(), format(format, arg, null));
    }

    public void warn(final String format, final Object arg1, final Object arg2) {
        logger.warn(name(), format(format, arg1, arg2));
    }

    public void warn(final String format, final Object... arguments) {
        logger.warn(name(), format(format, arguments));
    }

    public void warn(final String msg, final Throwable t) {
        logger.warn(name(), msg, t);
    }

    public void error(final String msg) {
        logger.error(name(), msg);
    }

    public void error(final String format, final Object arg) {
        logger.error(name(), format(format, arg, null));
    }

    public void error(final String format, final Object arg1, final Object arg2) {
        logger.error(name(), format(format, arg1, arg2));
    }

    public void error(final String format, final Object... arguments) {
        logger.error(name(), format(format, arguments));
    }

    public void error(final String msg, final Throwable t) {
        logger.error(name(), msg, t);
    }

    private String format(final String format, final Object arg1, final Object arg2) {
        return MessageFormatter.format(format, arg1, arg2).getMessage();
    }

    private String format(final String format, final Object[] args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }
}
