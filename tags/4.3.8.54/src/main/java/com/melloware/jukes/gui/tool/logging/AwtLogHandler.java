package com.melloware.jukes.gui.tool.logging;

import java.awt.Frame;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.application.Application;
import com.jgoodies.uifextras.convenience.SendFeedbackDialog;

/**
 * This class pops up message dialogs to LOG messages with
 * a level greater or equal to Level.warn. In addition,
 * a <code>ConsoleHandler</code> is used to write
 * all messages to the console, too.  If any AWT or Java errors are trapped too.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class AwtLogHandler
    extends Handler {

    private static final Log LOG = LogFactory.getLog(AwtLogHandler.class);
    private static final String OK_LABEL = "OK";
    private static final String FEEDBACK_LABEL = "Send Feedback...";
    private static final Object[] OPTIONS = new Object[] { OK_LABEL, FEEDBACK_LABEL };

    /**
     * Constructor.
     */
    public AwtLogHandler() {
        super();
    }

    public void close() {
        // Implements Handler; this implementation does nothing
    }

    public void flush() {
        // Implements Handler; this implementation does nothing
    }

    /**
     * Handles AWT errors or anything else that comes down the line.
     * @param throwable the Throwable instance to handle
     */
    public void handle(Throwable throwable) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Generic Exception Caught: " + throwable);
        }
        
        // check whether to dispose the error or show feedback dialog.
        if (shouldDisposeError(throwable) == false) {
           showOptionWithFeedbackDialog(Level.SEVERE, "Generic Application Error has occurred.", throwable); 
        } 
    }
    
    /**
     * A list of exceptions that should not display the feedback dialog.  This
     * is useful for known errors like the JDK6 TrayIcon ClassCastException.
     * <p>
     * @param throwable the Throwable instance to check for disposal
     */
    private boolean shouldDisposeError(Throwable throwable) {
       boolean dispose = false;
       if (throwable instanceof java.lang.ClassCastException) {
          if (throwable.getLocalizedMessage().equals("java.awt.TrayIcon cannot be cast to java.awt.Component")) {
             dispose = true;
          }
       }
       return dispose;
    }

    // JGoodies Handler ******************************************************

    public void log(Level level, String catalog, String msg, Throwable throwable) {
        LOG.info("LogManager Exception Caught");      
        
        if (level.intValue() >= Level.WARNING.intValue()) {
            JOptionPane.showMessageDialog(this.owner(), msg, getTitle(level), getMessageType(level));
        }
    
        if (level.intValue() >= Level.SEVERE.intValue()) {
            if (throwable == null) {
                JOptionPane.showMessageDialog(this.owner(), msg, getTitle(level), getMessageType(level));
            } else {
                showOptionWithFeedbackDialog(level, msg, throwable);
            }
        }
    }

    public void publish(LogRecord LOGRecord) {
        int level = LOGRecord.getLevel().intValue();

        LOG.info("LogManager Publish");
        // Don't pop up Windows for Level < warn.
        if (level < Level.WARNING.intValue()) {
            return;
        }
        
        String msg = LOGRecord.getMessage();
        if (LOGRecord.getThrown() == null) {
            JOptionPane.showMessageDialog(owner(), msg, getTitle(LOGRecord.getLevel()),
                                          getMessageType(LOGRecord.getLevel()));
        } else {
            showOptionWithFeedbackDialog(LOGRecord.getLevel(), msg, LOGRecord.getThrown());
        }
    }

    private int getMessageType(Level level) {
        if (Level.SEVERE.equals(level)) {
            return JOptionPane.ERROR_MESSAGE;
        } else if (Level.WARNING.equals(level)) {
            return JOptionPane.WARNING_MESSAGE;
        } else {
            return JOptionPane.INFORMATION_MESSAGE;
        }
    }

    private String getSubject(Level level) {
        return "Execution " + getTitle(level);
    }

    private String getTitle(Level level) {
        if (Level.SEVERE.equals(level)) {
            return "Error";
        } else if (Level.WARNING.equals(level)) {
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