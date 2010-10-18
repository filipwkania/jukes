package com.melloware.jukes.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.ResourceUtils;
import com.melloware.jukes.gui.tool.Resources;

/**
 * Static methods to easily display message dialogs in the application.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class MessageUtil {

    private static final String MESSAGE_TITLE = ResourceUtils.getString("messages.message.title");
    private static final String WARNING_TITLE = ResourceUtils.getString("messages.warning.title");
    private static final String ERROR_TITLE = ResourceUtils.getString("messages.error.title");
    private static final String CONFIRMATION_TITLE = ResourceUtils.getString("messages.confirmation.title");
    private static final String INPUT_TITLE = ResourceUtils.getString("messages.input.title");
    private static final String CONFIRM_DELETE_MSG = ResourceUtils.getString("messages.confirmDelete");
    private static final String CONFIRM_UPDATE_MSG = ResourceUtils.getString("messages.confirmUpdate");
    private static final String CONFIRM_BACKUP_MSG = ResourceUtils.getString("messages.confirmBackup");
    private static final String CANCEL_MSG = ResourceUtils.getString("messages.confirmCancelAndDiscardChanges");
    private static final String CLOSE_MSG = ResourceUtils.getString("messages.confirmClose");

    /**
     * Private cosntructor. No instantiation.
     */
    private MessageUtil() {
        super();
    }

    public static boolean confirmBackup(Component parent) {
        return promptYesNo(getParentWindow(parent), CONFIRM_BACKUP_MSG);
    }

    public static boolean confirmCancelAndDiscardChanges(Component parent) {
        return promptYesNo(getParentWindow(parent), CANCEL_MSG);
    }

    public static boolean confirmClose(Component parent) {
        return promptYesNo(getParentWindow(parent), CLOSE_MSG);
    }

    // some common, specific messages ****************************************

    public static boolean confirmDelete(Component parent) {
        return promptYesNo(getParentWindow(parent), CONFIRM_DELETE_MSG);
    }

    public static boolean confirmUpdate(Component parent) {
        return promptYesNo(getParentWindow(parent), CONFIRM_UPDATE_MSG);
    }

    // closing the window is the same as CANCEL
    public static boolean promptOkCancel(Component parent, String message) {
        return promptOkCancel(parent, message, JOptionPane.QUESTION_MESSAGE);
    }

    // closing the window is the same as CANCEL
    public static boolean promptOkCancel(Component parent, String message, int icon) {
        int answer = showConfirmDialog(getParentWindow(parent), message, WARNING_TITLE, JOptionPane.OK_CANCEL_OPTION,
                                       icon);
        return (answer == JOptionPane.OK_OPTION);
    }

    public static boolean promptwarn(Component parent, String message) {
        int answer = showConfirmDialog(getParentWindow(parent), message, WARNING_TITLE, JOptionPane.YES_NO_OPTION,
                                       JOptionPane.WARNING_MESSAGE);
        return (answer == JOptionPane.YES_OPTION);
    }

    // closing the window is the same as NO
    public static boolean promptYesNo(Component parent, String message) {
        int answer = showConfirmDialog(getParentWindow(parent), message, CONFIRMATION_TITLE, JOptionPane.YES_NO_OPTION,
                                       JOptionPane.QUESTION_MESSAGE);
        return (answer == JOptionPane.YES_OPTION);
    }

    // closing the window is the same as CANCEL
    public static int promptYesNoCancel(Component parent, String message) {
        int answer = showConfirmDialog(getParentWindow(parent), message, INPUT_TITLE, JOptionPane.YES_NO_CANCEL_OPTION,
                                       JOptionPane.QUESTION_MESSAGE);
        return ((answer == JOptionPane.CLOSED_OPTION) ? JOptionPane.CANCEL_OPTION : answer);
    }

    public static int showConfirmDialog(Component parentComponent,
                                        Object message,
                                        String title,
                                        int optionType,
                                        int messageType) {
        return showDialog(parentComponent, message, title, optionType, messageType, null, null, null);
    }

    public static void showError(JPanel panel) {
        showMessageDialog(null, panel, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        showMessageDialog(getParentWindow(parent), message, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Component parent, JPanel panel) {
        showMessageDialog(getParentWindow(parent), panel, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    public static void showInformation(Component parent, String message) {
        showMessageDialog(getParentWindow(parent), message, MESSAGE_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    // showing general messages **********************************************

    public static void showMessage(Component parent, String message) {
        showMessageDialog(getParentWindow(parent), message, MESSAGE_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType) {
        showDialog(parentComponent, message, title, JOptionPane.DEFAULT_OPTION, messageType, null, null, null);
    }

    public static void showSuccess(Component parent) {
        showMessageDialog(getParentWindow(parent), Resources.getString("messages.save.success"), MESSAGE_TITLE,
                          JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showTaskCompleted(Component parent) {
        showMessageDialog(getParentWindow(parent), Resources.getString("messages.taskCompleted"), MESSAGE_TITLE,
                          JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showwarn(Component parent, String message) {
        showMessageDialog(getParentWindow(parent), message, WARNING_TITLE, JOptionPane.WARNING_MESSAGE);
    }

    // private methods *******************************************************

    private static Window getParentWindow(Component c) {
        Window w = GuiUtil.findComponentOwnerFrame(c);
        return ((w == null) ? Application.getDefaultParentFrame() : w);
    }

    /**
     * Recursive method to verify the input when the target is focused.
     * <p>
     * @param c the component to verify
     * @param b true whether to focus or not.
     */
    private static void setVerifyInputWhenFocusTarget(Component c, boolean b) {
        if (c != null) {
            if (c instanceof JComponent) {
                ((JComponent)c).setVerifyInputWhenFocusTarget(b);
            }
            if (c instanceof Container) {
                Component[] components = ((Container)c).getComponents();
                for (int i = 0; i < components.length; i++) {
                    setVerifyInputWhenFocusTarget(components[i], b);
                }
            }
        }
    }

    private static int showDialog(Component parentComponent,
                                        Object message,
                                        String title,
                                        int optionType,
                                        int messageType,
                                        Icon icon,
                                        Object[] options,
                                        Object initialValue) {
        JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ? JOptionPane.getRootFrame() : parentComponent).getComponentOrientation());

        JDialog dialog = pane.createDialog(parentComponent, title);
        setVerifyInputWhenFocusTarget(dialog.getRootPane(), false);

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (options == null) {
            if (selectedValue instanceof Integer) {
                return ((Integer)selectedValue).intValue();
            }
            return JOptionPane.CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

}