package com.melloware.jukes.gui.view.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.util.ResourceUtils;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.util.MessageUtil;

/**
 * Abtsract base table that can export its results alternates row colors, and
 * allows customization of right click popup menu.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class ExportableTable
    extends JTable {

    private static final Log LOG = LogFactory.getLog(ExportableTable.class);
    private static Icon csvIcon = Resources.CSV_ICON;
    private static String csvText = "Export To CSV";
    private boolean popupEnabled = true;
    // private static Icon pdfIcon = Resources.PDF_ICON;
    // private static String pdfText = "Export To PDF";
    private Color evenColor = Color.WHITE;
    private Color oddColor = new Color(0xee, 0xee, 0xff);
    private JPopupMenu popupMenu = null;

    public ExportableTable() {
        super();
        constructor();
    }

    public ExportableTable(TableModel dm) {
        super(dm);
        constructor();
    }

    public ExportableTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        constructor();
    }

    public ExportableTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        constructor();
    }


    public ExportableTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        constructor();
    }

    public ExportableTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        constructor();
    }

    /**
     * Gets the evenColor.
     * <p>
     * @return Returns the evenColor.
     */
    public Color getEvenColor() {
        return this.evenColor;
    }

    /**
     * Gets the oddColor.
     * <p>
     * @return Returns the oddColor.
     */
    public Color getOddColor() {
        return this.oddColor;
    }

    /**
     * Sets the evenColor.
     * <p>
     * @param aEvenColor The evenColor to set.
     */
    public void setEvenColor(Color aEvenColor) {
        this.evenColor = aEvenColor;
    }

    /**
     * Sets the oddColor.
     * <p>
     * @param aOddColor The oddColor to set.
     */
    public void setOddColor(Color aOddColor) {
        this.oddColor = aOddColor;
    }

    /**
     * Allows the customization of the popup menu
     * @param popup - This is a prebuilt popup menu that can be modified
     */
    public void addPopupMenu(JPopupMenu popup) {
        this.popupMenu.addSeparator();
        Component[] items = popup.getComponents();
        for (int i = 0; i < items.length; i++) {
            this.popupMenu.add(items[i]);
        }
    }

    public void enablePopupMenu(boolean enabled) {
        popupEnabled = enabled;
    }

    /**
     * Change even and odd rows to white and light blue.
     */
    public Component prepareRenderer(TableCellRenderer aRenderer, int aRow, int aColumn) {
        final Component component = super.prepareRenderer(aRenderer, aRow, aColumn);
        if (!isRowSelected(aRow)) {
            component.setBackground(((aRow % 2) == 0) ? getEvenColor() : getOddColor());
        }
        return component;
    }

    public void stopEditing() {
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
    }

    /**
     * Stores the table as a comma seperated values file.
     * <p>
     * @param isSelected if the row is selected
     */
    /**
     * @param isSelected
     */
    protected void storeTableAsCSV(boolean isSelected) {
        if (isSelected && (this.getSelectedRowCount() == 0)) {
            return;
        }

        final StringBuffer sb = new StringBuffer("\"");
        final TableColumnModel cm = this.getColumnModel();
        final Enumeration enumeration = cm.getColumns();
        while (enumeration.hasMoreElements()) {
            final TableColumn tc = (TableColumn)enumeration.nextElement();
            sb.append(tc.getHeaderValue().toString());
            if (enumeration.hasMoreElements()) {
                sb.append("\",\"");
            } else {
                sb.append("\"\n");
            }
        }

        final int rowCount = this.getRowCount();
        final int colCount = this.getColumnCount();
        for (int i = 0; i < rowCount; i++) {
            sb.append('"');
            for (int j = 0; j < colCount; j++) {
                if (!isSelected || this.isCellSelected(i, j)) {
                    if (this.getValueAt(i, j) == null) {
                        sb.append("");
                    } else {
                        String value = this.getValueAt(i, j).toString().replace(',', ' ');
                        sb.append(value);
                    }
                    if (j == (colCount - 1)) {
                        sb.append("\"\n");
                    } else {
                        sb.append("\",\"");
                    }
                }
            }
        }

        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Results");
        chooser.setFileFilter(FilterFactory.csvFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileHidingEnabled(true);
        final int returnVal = chooser.showSaveDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Absolute: " + file.getAbsolutePath());
        }

        // add the extension if missing
        file = FilterFactory.forceCsvExtension(file);

        try {
            final FileWriter writer = new FileWriter(file);
            writer.write(sb.toString());
            writer.close();
            final String infoMessage = ResourceUtils.getString("messages.TableExportedSuccessfully"); 
            MessageUtil.showInformation(this, infoMessage);
        } catch (IOException ex) {
           final String errorMessage = ResourceUtils.getString("label.Errorwritingfile"); 
           MessageUtil.showError(this, errorMessage); //AZ 
           LOG.error(errorMessage + "\n\n" + ex.getMessage(), ex);

        } catch (InfrastructureException ex) {
       	   final String errorMessage = ResourceUtils.getString("label.Errorwritingfile"); 
           MessageUtil.showError(this, errorMessage); //AZ 
           LOG.error(errorMessage + "\n\n" + ex.getMessage(), ex);
        } catch (Exception ex) {
       	   final String errorMessage = ResourceUtils.getString("label.Errorwritingfile"); 
           MessageUtil.showError(this, errorMessage); //AZ 
           LOG.error(errorMessage, ex);
        }
    }

    private JPopupMenu buildPopupMenu() {
        final JPopupMenu pop = new JPopupMenu("Menu");
        final AbstractAction storeAsCSVAction = new AbstractAction(csvText, csvIcon) {
            public void actionPerformed(ActionEvent event) {
                storeTableAsCSV(false);
            }
        };
        final JMenuItem item = new JMenuItem(storeAsCSVAction);
        pop.add(item);
        pop.setLabel("");
        this.addMouseListener(new MousePopupListener());
        return pop;
    }

    private void constructor() {
        popupMenu = buildPopupMenu();
    }

    private class MousePopupListener
        extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            checkPopup(e);
        }

        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            checkPopup(e);
        }

        private void checkPopup(MouseEvent e) {
            if ((e.isPopupTrigger()) && (popupEnabled)) {
                popupMenu.show(ExportableTable.this, e.getX(), e.getY());
            }
        }
    }
}