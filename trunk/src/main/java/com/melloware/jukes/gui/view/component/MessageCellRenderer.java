package com.melloware.jukes.gui.view.component;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.melloware.jukes.util.JukesValidationMessage;

/**
 * Custom ListCellRenderer used to display Icons in JList messages from a
 * JukesValidationMessage.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class MessageCellRenderer
    extends JLabel
    implements ListCellRenderer {

    /**
     * Default Constructor
     */
    public MessageCellRenderer() {
        super();
    }

    /**
     * Constructor that take the display string.
     * <p>
     * @param aText the String to display
     */
    public MessageCellRenderer(final String aText) {
        super(aText);
    }

    /**
     * Constructor that take the display string.
     * <p>
     * @param aText the String to display
     * @param aIcon the Icon to display with this label
     */
    public MessageCellRenderer(final String aText, final Icon aIcon) {
        super(aText);
        this.setIcon(aIcon);
    }

    /**
     * Returns the warn icon for warns, the error icon for errors
     * and <code>null</code> otherwise.
     *
     * @param severity   the severity used to lookup the icon
     * @return the warn icon for warns, error icon for errors,
     *     <code>null</code> otherwise
     */
    public static Icon getIcon(final Severity severity) {
        if (severity == Severity.ERROR) {
            return ValidationResultViewFactory.getErrorIcon();
        } else if (severity == Severity.WARNING) {
            return ValidationResultViewFactory.getWarningIcon();
        } else if (severity == Severity.OK) {
            return ValidationResultViewFactory.getCheckIcon();
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean,
     * boolean)
     */
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value,
                                                  final int index,
                                                  final boolean isSelected,
                                                  final boolean cellHasFocus) {

        final JukesValidationMessage message = (JukesValidationMessage)value;
        this.setIcon(MessageCellRenderer.getIcon(message.severity()));
        this.setText(message.formattedText());
        this.setToolTipText(message.getToolTip());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }

}