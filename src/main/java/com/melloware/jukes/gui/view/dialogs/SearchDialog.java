package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.gui.view.MainMenuBuilder;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.component.EnhancedTableHeader;
import com.melloware.jukes.gui.view.component.ExportableTable;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Searches the database by criteria and returns results in Track format.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class SearchDialog
    extends AbstractDialog {

    private static final Log LOG = LogFactory.getLog(SearchDialog.class);
    private final AlbumImage webImagePreview;
    private final EnhancedTableHeader header;
    private final ExportableTable resultsTable;
    private JButton buttonCancel;
    private JButton buttonSearch;
    private JButton buttonSelect;
    private JButton buttonStop;
    private final JComboBox bitrateField;
    private final JComboBox genreField;
    private final JComboBox operatorBitrate;
    private final JComboBox operatorYear;
    private JComponent buttonBar;
    private JComponent splitPane;
    private final JTextField searchField;
    private final JTextField yearField;
    private Object[] results;
    private SearchTableModel tableModel;
    private final Settings settings;
    private RowSorter<TableModel> sorter;
    private Track selection;
    private Worker worker;

    /**
     * Constructs a default about dialog using the given owner.
     *
     * @param owner   the dialog's owner
     */
    public SearchDialog(Frame owner, Settings settings) {
        super(owner);
        LOG.debug("Search Dialog created.");
        this.settings = settings;
        this.settings.getDatabaseLocation();

        webImagePreview = new AlbumImage();
        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        doSearch();
                    } else {
                        super.keyPressed(e);
                    }

                }
            });
        genreField = new JComboBox(MusicTag.getGenreTypes().toArray());
        genreField.setSelectedItem(null);
        bitrateField = new JComboBox(Resources.BITRATES);
        bitrateField.setSelectedItem(null);
        operatorBitrate = new JComboBox(Resources.OPERATOR);
        operatorBitrate.setSelectedItem(">=");
        operatorYear = new JComboBox(Resources.OPERATOR);
        operatorYear.setSelectedItem(">=");
        yearField = new JTextField();
        ((JTextField)yearField).setColumns(5);
        resultsTable = new ExportableTable();
        resultsTable.setEvenColor(this.settings.getRowColorEven());
        resultsTable.setOddColor(this.settings.getRowColorOdd());
        final JPopupMenu popup = MainMenuBuilder.buildPlayerPopupMenu(resultsTable);
        resultsTable.addPopupMenu(popup);
        ActionManager.get(Actions.TRACK_PLAY_IMMEDIATE_ID).setEnabled(true);
        header = new EnhancedTableHeader(resultsTable.getColumnModel(), resultsTable);
        resultsTable.setTableHeader(header);

        this.setPreferredSize(new Dimension(700, 576));

    }

    /**
     * Gets the selection.
     * <p>
     * @return Returns the selection.
     */
    public Track getSelection() {
        return this.selection;
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doApply()
     */
    public void doAccept() {
        LOG.debug("Select pressed.");
        super.doAccept();
        //AZ GoTo selected item in main tree
        final MainFrame mainFrame = (MainFrame)Application.getDefaultParentFrame();
        String query = " and upper(disc.name) = '" + selection.getDisc().getName().toUpperCase() + "'";
        String filter = MainModule.SETTINGS.getFilter();
        //AZ define filter to allow selected node in the tree
        if (!(this.settings.isShowDefaultTree() && !StringUtils.isNotBlank(filter))) {
        	filter  = query;
            MainModule.SETTINGS.setFilter(filter);
            mainFrame.getMainModule().refreshTree();
        }
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doCancel()
     */
    public void doCancel() {
        LOG.debug("Cancel Pressed.");
        super.doCancel();
    }

    /**
     * Builds and answers the dialog's content.
     *
     * @return the dialog's content with tabbed pane and button bar
     */
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        JButton[] buttons = new JButton[2];
        JButton button = createAcceptButton(Resources.getString("label.Select"), false);
        button.setEnabled(false);
        buttonSelect = button;
        buttonCancel = createCancelButton();
        buttonCancel.setText(Resources.getString("label.Close"));
        buttons[0] = buttonSelect;
        buttons[1] = buttonCancel;
        buttonBar = ButtonBarFactory.buildRightAlignedBar(buttons);
        splitPane = buildSplitPane();
        content.add(splitPane, BorderLayout.CENTER);
        content.add(buttonBar, BorderLayout.SOUTH);
        return content;
    }

    /**
     * Builds and returns the dialog's header.
     *
     * @return the dialog's header component
     */
    protected JComponent buildHeader() {
        final HeaderPanel header = new HeaderPanel(Resources.getString("label.Search"),
        								     Resources.getString("label.searchcatalog"),
                                             Resources.SEARCH_ICON);

        return header;
    }

    /**
     * Builds and returns the dialog's pane.
     *
     * @return the dialog's  pane component
     */
    protected JComponent buildMainPanel() {
        FormLayout layout = new FormLayout("fill:pref:grow", "p, p, p, p, p, p, p, p, p, p, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.add(buildCriteria(), cc.xy(1, 1));
        builder.add(buildResultsTablePanel(), cc.xy(1, 7));
        return builder.getPanel();
    }

    /**
    * Executes the search.
    */
    protected void doSearch() {
        LOG.debug("Searching...");
        if ((StringUtils.isBlank(searchField.getText()))) {
            LOG.warn("Please enter search criteria.");
            MessageUtil.showError(this, Resources.getString("messages.entersearchcriteria"));
            return;
        }
        GuiUtil.setBusyCursor(this, true);
        buttonSearch.setEnabled(false);
        buttonCancel.setEnabled(false);
        buttonSelect.setEnabled(false);
        buttonStop.setEnabled(true);

        /* Invoking start() on the SwingWorker causes a new Thread
         * to be created that will call construct(), and then finished().  Note that finished() is called even if the
         * worker is interrupted because we catch the InterruptedException in doWork().
         */
        worker = new Worker() {
                public Object construct() {
                    return doWork();
                }

                public void finished() {
                    threadFinished(get());
                }
            };
        worker.start();
    }

    /**
     * Stops the search.
     */
    protected void doStop() {
        LOG.debug("Stopping...");
        GuiUtil.setBusyCursor(this, false);
        worker.interrupt();
        buttonSearch.setEnabled(true);
        buttonCancel.setEnabled(true);
        buttonStop.setEnabled(false);
    }

    /**
     * Resizes the given component to give it a quadratic aspect ratio.
     *
     * @param component   the component to be resized
     */
    protected void resizeHook(JComponent component) {
        Resizer.ONE2ONE.resizeDialogContent(component);
    }

    /**
    * When the thread is finished this method is called.
    * <p>
    * @param result the Object return from the doWork thread.
    */
    protected void threadFinished(Object result) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Thread Finished");
        }
        buttonSearch.setEnabled(true);
        buttonCancel.setEnabled(true);
        buttonStop.setEnabled(false);
        GuiUtil.setBusyCursor(this, false);

        if (result == null) {
            results = null;
            tableModel = new SearchTableModel(results);
            tableModel.setData(null);
            tableModel.fireTableDataChanged();
        } else {
            final Collection items = (Collection)result;
            results = items.toArray();
            tableModel = new SearchTableModel(results);
            sorter = new TableRowSorter<TableModel>(tableModel);
            resultsTable.setModel(tableModel);
            resultsTable.setRowSorter(sorter);
            header.autoSizeColumns();
        }

        resultsTable.updateUI();
        splitPane.updateUI();

    }

    /**
     * Builds the search criteria panel.
     * <p>
     * @return the panel used to specify criteria
     */
    private JComponent buildCriteria() {
        FormLayout layout = new FormLayout("right:max(14dlu;pref), pref, pref, 260px, pref, pref ,pref, fill:pref:grow",
                                           "p, 4px, p, 4px, p, 4px, p, 4px, p, 4px");

        layout.setRowGroups(new int[][] {
                                { 2, 4 }
                            });
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        JButton[] buttons = new JButton[2];

        // Create an action with an icon
        Action search = new AbstractAction(Resources.getString("label.Search"), Resources.THREAD_START_ICON) {
            public void actionPerformed(ActionEvent evt) {
                doSearch();
            }
        };
        buttonSearch = new JButton(search);
        // Create an action with an icon
        Action stop = new AbstractAction(Resources.getString("label.Stop"), Resources.THREAD_STOP_ICON) {
            public void actionPerformed(ActionEvent evt) {
                doStop();
            }
        };
        buttonStop = new JButton(stop);
        buttonStop.setEnabled(false);

        buttons[0] = buttonSearch;
        buttons[1] = buttonStop;
        JPanel searchButtonBar = ButtonBarFactory.buildCenteredBar(buttons);

        builder.addLabel(Resources.getString("label.findcriterium"), cc.xy(1, 1));
        builder.add(searchField, cc.xyw(2, 1, 4));
        builder.add(webImagePreview, cc.xywh(6, 1, 1, 7));
        builder.addLabel(Resources.getString("label.genre") + ": ", cc.xy(1, 3));
        builder.add(genreField, cc.xyw(2, 3, 4));
        builder.addLabel(Resources.getString("label.year") + ": ", cc.xy(1, 5));
        builder.add(operatorYear, cc.xy(2, 5));
        builder.add(yearField, cc.xy(3, 5));
        builder.addLabel(Resources.getString("label.bitrate") + ": ", cc.xy(1, 7));
        builder.add(operatorBitrate, cc.xy(2, 7));
        builder.add(bitrateField, cc.xy(3, 7));
        builder.add(searchButtonBar, cc.xyw(1, 9, 8));
        return builder.getPanel();
    }

    /**
     * Builds the panel with the JTable tags in it.
     * <p>
     * @return the panel used to display messages
     */
    private JComponent buildResultsTablePanel() {

        // build the table and model
        resultsTable.setShowGrid(false);
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ask to be notified of selection changes.
        final ListSelectionModel rowSM = resultsTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    // Ignore extra messages.
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    final ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (lsm.isSelectionEmpty()) {
                        selection = null;
                        buttonSelect.setEnabled(false);
                        webImagePreview.setImage(null);
                    } else {
                        int selectedRow = resultsTable.getSelectedRow();
                        selectedRow = sorter.convertRowIndexToModel(selectedRow);
                        selection = (Track)results[selectedRow];
                        buttonSelect.setEnabled(true);
                        if (StringUtils.isNotBlank(selection.getDisc().getCoverUrl())) {
                            webImagePreview.setImage(ImageFactory.getScaledImage(selection.getDisc().getCoverUrl(), 90,
                                                                                 90).getImage());
                        } else {
                            webImagePreview.setImage(null);
                        }
                    }
                }
            });

        final JComponent resultsPane = UIFactory.createTablePanel(resultsTable);
        resultsPane.setPreferredSize(new Dimension(300, 250));

        // build the form
        final FormLayout layout = new FormLayout("fill:pref:grow", "p");
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();
        builder.add(resultsPane, cc.xy(1, 1));
        return builder.getPanel();
    }

    /**
    * Builds the <code>Search Criteria</code>, the <code>Results</code>
    * and answers them wrapped by a stripped <code>JSplitPane</code>.
    */
    private JComponent buildSplitPane() {
        splitPane = UIFactory.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, buildCriteria(),
                                                      buildResultsTablePanel(), 0.25);
        splitPane.setPreferredSize(new Dimension(605, 470));
        splitPane.setBorder(Borders.DIALOG_BORDER);
        return splitPane;
    }

    /**
    * This method represents the application code that we'd like to
    * run on a separate thread.
    */
    private Object doWork() {
        Object result = null;
        try {
            // do the search
            try {
                String query = "";
                if (genreField.getSelectedItem() != null) {
                    query = query + " and disc.genre = '" + (String)genreField.getSelectedItem() + "'";
                }
                if (StringUtils.isNotBlank(yearField.getText())) {
                    query = query + " and disc.year " + operatorYear.getSelectedItem().toString()
                            + StringEscapeUtils.escapeSql(yearField.getText());
                }
                if (bitrateField.getSelectedItem() != null) {
                    query = query + " and track.bitrate  " + operatorBitrate.getSelectedItem().toString()
                            + StringEscapeUtils.escapeSql((String)bitrateField.getSelectedItem());
                }
                final String resource = ResourceUtils.getString("hql.search.criteria");
                final String hql = MessageFormat.format(resource,
                                                        new Object[] {
                                                            StringEscapeUtils.escapeSql(searchField.getText()), query
                                                        });
                result = HibernateDao.findByQuery(hql, 500);
            } catch (RuntimeException ex) {
                final String message = "Unexpected error occured performing search.";
                LOG.debug(message, ex);
                throw new InterruptedException(message);
            }

            if (Thread.interrupted()) {
                LOG.debug("Thread interrupted.");
                throw new InterruptedException();
            }

        } catch (InterruptedException e) {

            return result;    // SwingWorker.get() returns this
        }
        return result;    // or this
    }

}