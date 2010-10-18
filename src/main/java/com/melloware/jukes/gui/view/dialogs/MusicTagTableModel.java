package com.melloware.jukes.gui.view.dialogs;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.application.Application;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.util.MessageUtil;


/**
 * The table model for displaying music tags from a directory.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class MusicTagTableModel
    extends AbstractTableModel {

    private static final Log LOG = LogFactory.getLog(MusicTagTableModel.class);
    private Object[] data;
    private final String[] columnNames = { Resources.getString("label.track"),
    		                               Resources.getString("label.title"),
    		                               Resources.getString("label.comment"),
    		                               Resources.getString("label.bitrate"),
    		                               Resources.getString("label.file") };

    /**
     * Constructor that takes a collection.
     */
    public MusicTagTableModel() {
        super();
        LOG.debug("MusicTagTableModel created.");
        this.data = null;
    }

    /**
     * Constructor that takes a collection.
     */
    public MusicTagTableModel(Object[] aData) {
        super();
        LOG.debug("MusicTagTableModel created.");
        this.data = aData;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * Gets the data.
     * <p>
     * @return Returns the data.
     */
    public Object[] getData() {
        return this.data;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (data == null) {
            return 0;
        } else {
            return data.length;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        try {
            if (data == null) {
                return "";
            }
            if (row >= data.length) {
                return "";
            }
            final MusicTag item = (MusicTag)data[row];
            Object value = null;
            switch (col) {
                case 0: {
                    value = item.getTrack();
                    break;
                }
                case 1: {
                    value = item.getTitle();
                    break;
                }
                case 2: {
                    value = item.getComment();
                    break;
                }
                case 3: {
                    value = "   " + item.getBitRateAsString() + "   ";
                    break;
                }
                case 4: {
                    value = item.getAbsolutePath();
                    break;
                }
                default: {
                    break;
                }
            }
            return (value == null) ? "" : value;
        } catch (Exception ex) {
        	final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
       	    final String errorMessage = Resources.getString("messages.ErrorLoadingResults");
    	    MessageUtil.showError(mainFrame, errorMessage);
            LOG.error(errorMessage, ex);
            return "";
        }
    }

    /**
     * Sets the data.
     * <p>
     * @param aData The data to set.
     */
    public void setData(Object[] aData) {
        this.data = aData;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int aRowIndex, int aColumnIndex) {
        if (aRowIndex >= data.length) {
            return;
        }
        final MusicTag tag = (MusicTag)data[aRowIndex];
        String value = aValue.toString();
        try {
            switch (aColumnIndex) {
                case 0: {
                    tag.setTrack(value);
                    break;
                }
                case 1: {
                    tag.setTitle(value);
                    break;
                }
                case 2: {
                    tag.setComment(value);
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception ex) {
        	final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
        	final String errorMessage = Resources.getString("messages.ErrorEditingTable") + ": \n\n" + ex.getMessage();
        	MessageUtil.showError(mainFrame, errorMessage);
            LOG.error(errorMessage, ex);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int col) {
    	boolean result = false;
        // only track, title,and comment are editable
        switch (col) {
            case 0:
            case 1:
            case 2: {
            	result = true;
                break;
            }
            default: {
            	result = false;
            }
        }
        return result;
    }
}