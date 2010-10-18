package com.melloware.jukes.gui.tool.logging;

import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.jgoodies.uif.application.Application;
import com.jgoodies.uifextras.convenience.SendFeedbackDialog;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Jgoodies Log4J appender to catch errors and display the feeback dialog.
 * It catches errors and warns and displays them to the user using either
 * a feedback dialog or a JOptionPane.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class Log4jFeedbackAppender
    extends AppenderSkeleton {

    private static final Log LOG = LogFactory.getLog(Log4jFeedbackAppender.class);
    private static final String OK_LABEL = "OK";
    private static final String FEEDBACK_LABEL = "Send Feedback...";
    private static final Object[] OPTIONS = new Object[] { OK_LABEL, FEEDBACK_LABEL };

    /**
     * Default constructor
     */
    public Log4jFeedbackAppender() {
        super();
    }

    /* (non-Javadoc)
     * @see org.apache.LOG4j.AppenderSkeleton#close()
     */
    public void close() {
        // do nothing

    }

    /* (non-Javadoc)
     * @see org.apache.LOG4j.AppenderSkeleton#requiresLayout()
     */
    public boolean requiresLayout() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.LOG4j.AppenderSkeleton#append(org.apache.LOG4j.spi.LoggingEvent)
     */
    protected void append(LoggingEvent aLoggingEvent) {
        if (aLoggingEvent.getLevel() == Level.WARN) {
            LOG.debug("Feeback appender showing warn.");
            String msg = aLoggingEvent.getRenderedMessage();
            JOptionPane.showMessageDialog(this.owner(), msg, getTitle(aLoggingEvent.getLevel()),
                                          getMessageType(aLoggingEvent.getLevel()));

        }
        if (aLoggingEvent.getLevel() == Level.ERROR) {
            LOG.debug("Feeback appender showing error.");
            String msg = aLoggingEvent.getRenderedMessage();
            if (aLoggingEvent.getThrowableInformation() == null) {
                JOptionPane.showMessageDialog(this.owner(), msg, getTitle(aLoggingEvent.getLevel()),
                                              getMessageType(aLoggingEvent.getLevel()));
            } else {
                showOptionWithFeedbackDialog(aLoggingEvent.getLevel(), msg,
                                             aLoggingEvent.getThrowableInformation().getThrowable());
            }
        }
    }

    private int getMessageType(Level level) {
        if (Level.ERROR.equals(level)) {
            return JOptionPane.ERROR_MESSAGE;
        } else if (Level.WARN.equals(level)) {
            return JOptionPane.WARNING_MESSAGE;
        } else {
            return JOptionPane.INFORMATION_MESSAGE;
        }
    }

    private String getSubject(Level level) {
        return Resources.APPLICATION_NAME + " " + getTitle(level);
    }

    private String getTitle(Level level) {
        if (Level.ERROR.equals(level)) {
            return "Error";
        } else if (Level.WARN.equals(level)) {
            return "warn";
        } else {
            return "Message";
        }
    }

    private Frame owner() {
        Frame frame = Application.getDefaultParentFrame();
        return (frame == null) ? new Frame() : frame;
    }

    private void sendFeedback(Level level, String msg, Throwable thrown) {
        StringWriter out = new StringWriter();
        out.write(msg);

        out.write("\n");
        writeSystemProperties(out,
                              new String[] {
                                  "os.name", "os.version", "java.vm.vendor", "java.vm.version",
                                  "application.fullversion"
                              });

        if (thrown != null) {
            out.write("\n\n");
            thrown.printStackTrace(new PrintWriter(out));
        }
        new SendFeedbackDialog(owner(), "info@melloware.com", getSubject(level), out.toString()).open();
    }

    private void showOptionWithFeedbackDialog(Level level, String msg, Throwable thrown) {
        int messageType = getMessageType(level);
        String title = getTitle(level);
        String fullMessage = msg + "\n" + thrown.getLocalizedMessage();

        int choice = JOptionPane.showOptionDialog(owner(), fullMessage, title, -1, messageType, null, OPTIONS,
                                                  OK_LABEL);
        if (choice == 1) {
            sendFeedback(level, msg, thrown);
        }
    }

    private void writeSystemProperties(StringWriter out, String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = System.getProperty(key);
            if (value != null) {
                out.write("\n");
                out.write(key);
                out.write("=");
                out.write(value);
            }
        }
    }

}