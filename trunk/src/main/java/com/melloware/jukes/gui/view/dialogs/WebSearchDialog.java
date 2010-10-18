package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.panel.SimpleInternalFrame;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.exception.WebServiceException;
import com.melloware.jukes.file.FileUtil;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.gui.view.component.AlbumImage;
import com.melloware.jukes.gui.view.component.EnhancedTableHeader;
import com.melloware.jukes.gui.view.component.ExportableTable;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;
import com.melloware.jukes.ws.AmazonItem;
import com.melloware.jukes.ws.AmazonSearch;



/**
 * Searches the web for an artist or album.  Uses Amazon.com web services to
 * query the web.  Allows user to select YEAR, ALBUM NAME, and
 * a cover image to update the disc being edited.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */
public final class WebSearchDialog
    extends AbstractDialog {
	
    private static final Log LOG = LogFactory.getLog(WebSearchDialog.class);
    private final AlbumImage webImagePreview;
    private AmazonItem selection;
    private Collection selectedTracks;
    private DefaultListModel listModel;
    private final EnhancedTableHeader header;
    private final ExportableTable resultsTable;
    private Image selectedImage;
    private JButton buttonApply;
    private JButton buttonCancel;
    private JButton buttonSearch;
    private JButton buttonStop;
    private JComponent buttonBar;
    private JComponent splitPane;
    private JList list;
    private final JTextField artistField;
    private final JTextField discField;
    private Object[] results;
    private String selectedArtist;
    private String selectedDisc;
    private String selectedYear;
    private RowSorter<TableModel> sorter;
    private WebSearchTableModel tableModel;
    private Worker worker;
    private JComboBox endpointsCombo; //AZ
    public static final String[] endPoints = { "ecs.amazonaws.com",  //AZ
    							"ecs.amazonaws.ca",
    							"ecs.amazonaws.co.uk",
    							"ecs.amazonaws.de",
    							"ecs.amazonaws.fr",
    							"ecs.amazonaws.jp"};

    /**
     * Constructs a default about dialog using the given owner.
     *
     * @param owner   the dialog's owner
     */
    public WebSearchDialog(Frame owner, Settings settings) {
        super(owner);
        LOG.debug("Web Search Dialog created.");
        artistField = new JTextField();
        discField = new JTextField();
        resultsTable = new ExportableTable();
        resultsTable.setEvenColor(settings.getRowColorEven());
        resultsTable.setOddColor(settings.getRowColorOdd());
        header = new EnhancedTableHeader(resultsTable.getColumnModel(), resultsTable);
        resultsTable.setTableHeader(header);
        webImagePreview = new AlbumImage();
        webImagePreview.setPreferredSize(new Dimension(100, 100));
        this.setPreferredSize(new Dimension(720, 616));
        endpointsCombo = new JComboBox(endPoints);
        endpointsCombo.setSelectedIndex(0);
    }

    /**
     * Gets the selectedArtist.
     * <p>
     * @return Returns the selectedArtist.
     */
    public String getSelectedArtist() {
        return FileUtil.capitalize(this.selectedArtist);
    }

    /**
     * Gets the selectedDisc.
     * <p>
     * @return Returns the selectedDisc.
     */
    public String getSelectedDisc() {
        return FileUtil.capitalize(this.selectedDisc);
    }

    /**
     * Gets the selectedImage.
     * <p>
     * @return Returns the selectedImage.
     */
    public Image getSelectedImage() {
        return this.selectedImage;
    }

    /**
     * Gets the selectedTracks.
     * <p>
     * @return Returns the selectedTracks.
     */
    public Collection getSelectedTracks() {
        return this.selectedTracks;
    }

    /**
     * Gets the selectedYear.
     * <p>
     * @return Returns the selectedYear.
     */
    public String getSelectedYear() {
        return this.selectedYear;
    }

    /**
     * Sets the selectedArtist.
     * <p>
     * @param aSelectedArtist The selectedArtist to set.
     */
    public void setSelectedArtist(String aSelectedArtist) {
        this.selectedArtist = aSelectedArtist;
        artistField.setText(this.selectedArtist);
    }

    /**
     * Sets the selectedDisc.
     * <p>
     * @param aSelectedDisc The selectedDisc to set.
     */
    public void setSelectedDisc(String aSelectedDisc) {
        this.selectedDisc = StringUtils.substringBeforeLast(aSelectedDisc, " - ");
        discField.setText(this.selectedDisc);
    }

    /**
     * Sets the selectedImage.
     * <p>
     * @param aSelectedImage The selectedImage to set.
     */
    public void setSelectedImage(Image aSelectedImage) {
        this.selectedImage = aSelectedImage;
    }

    /**
     * Sets the selectedTracks.
     * <p>
     * @param aSelectedTracks The selectedTracks to set.
     */
    public void setSelectedTracks(Collection aSelectedTracks) {
        this.selectedTracks = aSelectedTracks;
    }

    /**
     * Sets the selectedYear.
     * <p>
     * @param aSelectedYear The selectedYear to set.
     */
    public void setSelectedYear(String aSelectedYear) {
        this.selectedYear = aSelectedYear;
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doApply()
     */
    public void doApply() {
        LOG.debug("Select pressed.");
        AmazonItem item = selection;
        setSelectedArtist(item.getArtist());
        setSelectedDisc(item.getDisc());
        setSelectedYear(item.getReleaseYear());
        setSelectedImage(item.getBestImage());
        setSelectedTracks(item.getTracks());
        super.doClose();
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doCancel()
     */
    public void doCancel() {
        LOG.debug("Cancel Pressed.");
        super.doCancel();
    }

    /**
     * Runs the Amazon.com query in a thread.
     */
    public void doSearch() {
        LOG.debug("Searching...");

        if ((StringUtils.isBlank(artistField.getText())) && (StringUtils.isBlank(discField.getText()))) {
            LOG.warn("Please select either an artist or disc or both.");
            return;
        }
        GuiUtil.setBusyCursor(this, true);
        buttonSearch.setEnabled(false);
        buttonCancel.setEnabled(false);
        buttonApply.setEnabled(false);
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
     * Stops the current Amazon query.
     */
    public void doStop() {
        LOG.debug("Stopping...");
        GuiUtil.setBusyCursor(this, false);
        worker.interrupt();
        buttonSearch.setEnabled(true);
        buttonCancel.setEnabled(true);
        buttonStop.setEnabled(false);
    }

    /**
     * Builds and answers the dialog's content.
     *
     * @return the dialog's content with tabbed pane and button bar
     */
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        JButton[] buttons = new JButton[2];
        JButton button = createApplyButton();
        button.setText(Resources.getString("label.Select"));
        button.setEnabled(false);
        buttonApply = button;
        buttonCancel = createCancelButton();
        buttonCancel.setText(Resources.getString("label.Cancel"));
        buttons[0] = buttonApply;
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
        return new HeaderPanel(Resources.getString("label.WebSearch"),
        				       Resources.getString("label.WebSearchMessage"),
                               Resources.WEB_SEARCH_ICON);
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
        int row = 0;
        row++;  
        builder.add(buildCriteria(), cc.xy(1, 1));
        builder.add(buildResultsPanel(), cc.xy(1, 7));
        return builder.getPanel();
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
     * Builds the search criteria panel.
     * <p>
     * @return the panel used to specify criteria
     */
    private JComponent buildCriteria() {
        FormLayout layout = new FormLayout("right:max(14dlu;pref), fill:pref:grow", "4px, p, 4px, p, 4px, p, 4px, p, 4px");

        layout.setRowGroups(new int[][] {
                                { 2, 6 }
                            });
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        JButton[] buttons = new JButton[2];

        // Create an action with an icon
        Action search = new AbstractAction(Resources.getString("label.Search"), Resources.THREAD_START_ICON) {
            // This method is called when the button is pressed
            public void actionPerformed(ActionEvent evt) {
                doSearch();
            }
        };
        buttonSearch = new JButton(search);
        // Create an action with an icon
        Action stop = new AbstractAction(Resources.getString("label.Stop"), Resources.THREAD_STOP_ICON) {
            // This method is called when the button is pressed
            public void actionPerformed(ActionEvent evt) {
                doStop();
            }
        };
        buttonStop = new JButton(stop);
        buttonStop.setEnabled(false);

        buttons[0] = buttonSearch;
        buttons[1] = buttonStop;
        JPanel searchButtonBar = ButtonBarFactory.buildCenteredBar(buttons);

        builder.addLabel(Resources.getString("label.artist") +": ", cc.xy(1, 2));
        builder.add(artistField, cc.xy(2, 2));
        builder.addLabel(Resources.getString("label.disc") +": ", cc.xy(1, 4));
        builder.add(discField, cc.xy(2, 4));
        builder.addLabel(Resources.getString("label.endpoint") +": ", cc.xy(1, 6));
        builder.add(endpointsCombo, cc.xy(2, 6));
        builder.add(searchButtonBar, cc.xyw(1, 8, 2)); 
        return builder.getPanel();
    }

    /**
     * Builds the panel with the JTable results in it.
     * <p>
     * @return the panel used to display messages
     */
    private JComponent buildResultsPanel() {
        final Component dialog = this;
        // build the table and model
        resultsTable.setShowGrid(false);
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ask to be notified of selection changes.
        ListSelectionModel rowSM = resultsTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    // Ignore extra messages.
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (lsm.isSelectionEmpty()) {
                        selection = null;
                        buttonApply.setEnabled(false);
                        webImagePreview.setImage(null);
                        listModel.removeAllElements();
                    } else {
                        GuiUtil.setBusyCursor(dialog, true);
                        try {
                            int selectedRow = resultsTable.getSelectedRow();
                            selectedRow = sorter.convertRowIndexToModel(selectedRow);
                            selection = (AmazonItem)results[selectedRow];
                            buttonApply.setEnabled(true);
                            webImagePreview.setImage(selection.getSmallestImage());
                            listModel.removeAllElements();
                            int count = 0;
                            for (Iterator iter = selection.getTracks().iterator(); iter.hasNext();) {
                                String element = (String)iter.next();
                                count++;
                                listModel.addElement(count + ". " + element);
                            }
                        } catch (Exception ex) {
                        	final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
                            LOG.error("RuntimeException", ex);
                            MessageUtil.showError(mainFrame, "RuntimeException");
                        } finally {
                            GuiUtil.setBusyCursor(dialog, false);
                        }
                    }
                }
            });

        // build the tracks JList and model
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setFocusable(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(7);
        JScrollPane listScrollPane = UIFactory.createStrippedScrollPane(list);
        listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.add(webImagePreview, BorderLayout.NORTH);
        previewPanel.add(listScrollPane, BorderLayout.CENTER);
        final SimpleInternalFrame previewer = new SimpleInternalFrame(Resources.getString("label.Preview"));
        previewer.add(previewPanel);
        previewer.setPreferredSize(new Dimension(150, 304));

        JComponent resultsPane = UIFactory.createTablePanel(resultsTable);
        resultsPane.setPreferredSize(new Dimension(300, 300));

        // build the form
        FormLayout layout = new FormLayout("fill:pref:grow, right:max(14dlu;pref)", "p");
        layout.setRowGroups(new int[][] {
                                { 1 }
                            });
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.add(resultsPane, cc.xy(1, 1));
        builder.add(previewer, cc.xy(2, 1));
        builder.getPanel().setPreferredSize(new Dimension(450, 300));
        return builder.getPanel();
    }

    /**
     * Builds the <code>Search Criteria</code>, the <code>Results</code>
     * and answers them wrapped by a stripped <code>JSplitPane</code>.
     */
    private JComponent buildSplitPane() {
        splitPane = UIFactory.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, buildCriteria(), buildResultsPanel(),
                                                      0.25);
        splitPane.setPreferredSize(new Dimension(630, 470));
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
                result = AmazonSearch.findItemsByArtistDisc(artistField.getText(), discField.getText(), endpointsCombo.getSelectedItem().toString());
            } catch (WebServiceException ex) {
                String message = Resources.getString("messages.ErrorQueryingWeb") + "\n\n" + ex.getMessage();
                LOG.error(message);
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

    /**
     * When the thread is finished this method is called.
     * <p>
     * @param result the Object return from the doWork thread.
     */
    private void threadFinished(Object result) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Thread Finished");
        }
        buttonSearch.setEnabled(true);
        buttonCancel.setEnabled(true);
        buttonStop.setEnabled(false);
        GuiUtil.setBusyCursor(this, false);

        if (result == null) {
            results = null;
            tableModel = new WebSearchTableModel(results);
            tableModel.setData(null);
            tableModel.fireTableDataChanged();
        } else {
            final Collection items = (Collection)result;
            results = items.toArray();
            tableModel = new WebSearchTableModel(results);
            sorter = new TableRowSorter<TableModel>(tableModel);
            resultsTable.setModel(tableModel);
            resultsTable.setRowSorter(sorter);
            header.autoSizeColumns();
        }

        resultsTable.updateUI();
        splitPane.updateUI();
    }

}