package com.melloware.jukes.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.application.Application;

/**
 * Swing Utilities used throughout the application.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class GuiUtil {

    private static final Log LOG = LogFactory.getLog(GuiUtil.class);

    /**
     * Private constructor, no instatiation.
     */
    private GuiUtil() {
        super();
    }

    /**
     * Gets the preferred width of a JScrollpane.
     * <p>
     * @param aScrollPane the scrollpane to get the width for
     * @return the width of the scrollpaneas an int
     */
    public static final int getPreferredWidth(JScrollPane aScrollPane) {
        final JScrollBar scrollBar = aScrollPane.getVerticalScrollBar();
        int scrollWidth = scrollBar.getUI().getPreferredSize(scrollBar).width;

        final Insets insets = aScrollPane.getInsets();
        int insetWidth = insets.left + insets.right;

        final Component view = aScrollPane.getViewport().getView();
        int viewWidth = view.getPreferredSize().width;

        return viewWidth + insetWidth + scrollWidth;
    }

    /**
     * Sets the cursor to hourglass for true and default for false.  Used for
     * long operations such as saves.
     * <p>
     * @param aBusy true for busy cursor, false for default
     */
    public static void setBusyCursor(Component aComponent, boolean aBusy) {
        if (aBusy) {
            LOG.debug("CURSOR: Set to WAIT");
            aComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            LOG.debug("CURSOR: Set to DEFAULT");
            aComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Goes to the root of component hierarchy until finds Frame or null.
     *
     * @param component start of search.
     *
     * @return owner frame.
     */
    public static Frame findComponentOwnerFrame(Component component) {
        Frame owner;

        if (component == null) {
            owner = Application.getDefaultParentFrame();
        } else {
            if (component instanceof Frame) {
                owner = (Frame)component;
            } else {
                owner = findComponentOwnerFrame(component.getParent());
            }
        }

        return owner;
    }

    /**
     * Goes to the root of component hierarchy until finds Frame or null.
     *
     * @param obj object to start searching.
     *
     * @return owner frame.
     */
    public static Frame findOwnerFrame(Object obj) {
        return (obj instanceof Component) ? findComponentOwnerFrame((Component)obj) : findComponentOwnerFrame(null);
    }

    /**
     * Find and stop a table's editing.
     * <p>
     * @param aTable the JTable to stop editing
     */
    public static void stopTableEditing(JTable aTable) {

        TableCellEditor cellEditor = null;
        int col = aTable.getEditingColumn();
        int row = aTable.getEditingRow();

        cellEditor = aTable.getCellEditor();
        if (cellEditor == null) {    // NOPMD
            if ((col >= 0) && (row >= 0)) {    // NOPMD
                cellEditor = aTable.getColumnModel().getColumn(col).getCellEditor();
                if (cellEditor == null) {
                    cellEditor = aTable.getDefaultEditor(aTable.getColumnClass(col));
                }
            }
        }
        if (cellEditor != null) {
            try {
                cellEditor.stopCellEditing();
            } catch (Exception e) {
                LOG.warn("failed to stop cell editing " + e, e);
            }
        }
    }

}