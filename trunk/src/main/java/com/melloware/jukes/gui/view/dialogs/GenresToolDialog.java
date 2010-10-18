package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.file.Disclist;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.gui.view.MainMenuBuilder;
import com.melloware.jukes.gui.view.component.EnhancedTableHeader;
import com.melloware.jukes.gui.view.component.ExportableTable;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Searches the database by criteria and returns results in Disc format.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ 2009, 2010
 */
public final class GenresToolDialog
    extends AbstractDialog {

    private static final Log LOG = LogFactory.getLog(SearchDialog.class);
    private final EnhancedTableHeader header;
    private final ExportableTable resultsTable;
    private JButton buttonCancel;
    private JButton buttonSearch;
    private JButton buttonSelect;
    private JButton buttonStop;
    private JButton buttonSave;
    private JButton buttonAddToDiscList;
    private JComponent buttonBar;
    private JProgressBar progressBar;
    private Object[] results;
    private DiscTableModel tableModel;
    private final Settings settings;
    private RowSorter<TableModel> sorter;
    private Disc selection;
    private Worker worker;
    private JPopupMenu addDiscMenu;

    /**
     * Constructs a default about dialog using the given owner.
     *
     * @param owner   the dialog's owner
     */
    public GenresToolDialog(Frame owner, Settings settings) {
        super(owner);
        LOG.debug("Genre Tool Dialog created.");
        this.settings = settings;
        this.settings.getDatabaseLocation();

        resultsTable = new ExportableTable();
        resultsTable.setEvenColor(this.settings.getRowColorEven());
        resultsTable.setOddColor(this.settings.getRowColorOdd());
        header = new EnhancedTableHeader(resultsTable.getColumnModel(), resultsTable);
        resultsTable.setTableHeader(header);
        addDiscMenu = MainMenuBuilder.buildDiscExportableTablePopupMenu(resultsTable);
        resultsTable.addPopupMenu(addDiscMenu);

        this.setPreferredSize(new Dimension(800, 576));

    }

    /**
     * Gets the selection.
     * <p>
     * @return Returns the selection.
     */
    public Disc getSelection() {
        return this.selection;
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doApply()
     */
    public void doAccept() {
        LOG.debug("Select pressed.");
        super.doAccept();
        final MainFrame mainFrame = (MainFrame)Application.getDefaultParentFrame();
        String query = " and upper(disc.name) = '" + selection.getName().toUpperCase() + "'";
        String filter = MainModule.SETTINGS.getFilter();
        
        if (!(this.settings.isShowDefaultTree() && !StringUtils.isNotBlank(filter))) {
        	filter  = query;
        }
        MainModule.SETTINGS.setFilter(filter);
        mainFrame.getMainModule().refreshTree();
        mainFrame.getMainModule().selectNodeInTree(selection);
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
     * @return the dialog's content with table pane and button bar
     */
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        JButton[] buttons = new JButton[6];
        JButton button = createAcceptButton(Resources.getString("label.Select"), false);
        button.setEnabled(false);
        buttonSelect = button;
        buttonCancel = createCancelButton();
        buttonCancel.setText(Resources.getString("label.Close")); //AZ
        
        // Create an action with an icon
        final Action export = new AbstractAction(Resources.getString("label.SaveReport"), Resources.FILE_TEXT_ICON) {
            // This method is called when the button is pressed
            public void actionPerformed(ActionEvent evt) {
               saveReport(evt);
            }
         };
         buttonSave = new JButton(export);
         
        // Create an action with an icon
        Action search = new AbstractAction(Resources.getString("label.StartCheck"), Resources.THREAD_START_ICON) {
            public void actionPerformed(ActionEvent evt) {
                doSearch();
            }
        };
        buttonSearch = new JButton(search);
        
        // Create an action with an icon
        Action stop = new AbstractAction(Resources.getString("label.Cancel"), Resources.THREAD_STOP_ICON) {
            public void actionPerformed(ActionEvent evt) {
                doStop();
            }
        };
        buttonStop = new JButton(stop);
        buttonStop.setEnabled(false);
        
        // Create an action with an icon
        Action adddisclist = new AbstractAction(Resources.getString("label.AddToDiscList"), ResourceUtils.getIcon("disc.queue.icon")) {
            public void actionPerformed(ActionEvent evt) {
                doAddToDiscList();
            }
        };
        buttonAddToDiscList = new JButton(adddisclist);


        buttons[0] = buttonSearch;
        buttons[1] = buttonStop;
        buttons[2] = buttonSelect;
        buttons[3] = buttonSave;     
        buttons[4] = buttonAddToDiscList;
        buttons[5] = buttonCancel;
        buttonBar = ButtonBarFactory.buildCenteredBar(buttons);

        content.add(buildMainPanel(), BorderLayout.CENTER);
        content.add(buttonBar, BorderLayout.SOUTH);
        return content;
    }

    /**
     * Builds and returns the dialog's header.
     *
     * @return the dialog's header component
     */
    protected JComponent buildHeader() {
        final HeaderPanel header = new HeaderPanel(Resources.getString("label.GenresCheck"),
        		                                   Resources.getString("label.GenresCheckText"),
        		                                   Resources.GENRES_TOOL_ICON);

        return header;
    }

    /**
     * Builds and returns the dialog's pane.
     *
     * @return the dialog's  pane component
     */
    protected JComponent buildMainPanel() {
        FormLayout layout = new FormLayout("fill:pref:grow", "p, 4px, p, 4px");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.add(buildProgressPanel(), cc.xy(1, 1));
        builder.add(buildResultsTablePanel(), cc.xy(1, 3));
        return builder.getPanel();
    }

    /**
    * Executes the search.
    */
    protected void doSearch() {
        LOG.debug("Searching...");
        
        GuiUtil.setBusyCursor(this, true);
        buttonSearch.setEnabled(false);
        buttonCancel.setEnabled(false);
        buttonSelect.setEnabled(false);
        buttonSave.setEnabled(false);
        buttonAddToDiscList.setEnabled(false);
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
        buttonSave.setEnabled(true);
        buttonAddToDiscList.setEnabled(true);
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
        buttonSave.setEnabled(true);
        buttonAddToDiscList.setEnabled(true);
        buttonStop.setEnabled(false);
        GuiUtil.setBusyCursor(this, false);

        if (result == null) {
            results = null;
            tableModel = new DiscTableModel(results);
            tableModel.setData(null);
            tableModel.fireTableDataChanged();
        } else {
            final Collection items = (Collection)result;
            results = items.toArray();
            tableModel = new DiscTableModel(results);
            sorter = new TableRowSorter<TableModel>(tableModel);
            resultsTable.setModel(tableModel);
            resultsTable.setRowSorter(sorter);
            header.autoSizeColumns();
        }

        resultsTable.updateUI();

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
                        
                    } else {
                        int selectedRow = resultsTable.getSelectedRow();
                        selectedRow = sorter.convertRowIndexToModel(selectedRow);
                        selection = (Disc)results[selectedRow];
                        buttonSelect.setEnabled(true);

                    }
                }
            });

        final JComponent resultsPane = UIFactory.createTablePanel(resultsTable);
        resultsPane.setPreferredSize(new Dimension(300, 350));

        // build the form
        final FormLayout layout = new FormLayout("fill:pref:grow", "p");
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();
        builder.add(resultsPane, cc.xy(1, 1));
        return builder.getPanel();
    }

    /**
    * This method represents the application code that we'd like to
    * run on a separate thread.
    */
    private Object doWork() {
        ArrayList<Disc> selectedDiscs = new ArrayList<Disc>();
        boolean Stop = false;
        try {
            // do the search
            try {              
                // get a list of all discs in the system
                //final Collection discs = HibernateDao.findAll(Disc.class, Disc.PROPERTYNAME_NAME);
            	//final String hql = "select distinct disc from Disc as disc inner join disc.artist as artist order by upper(artist.name)";
            	final String hql = "select disc from Disc as disc inner join disc.artist as artist order by upper(artist.name)";
            	final Collection discs = HibernateDao.findByQuery(hql);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Disc Count = " + discs.size());
                }
                int count = 0;
                // set the progress-bar bounds
                progressBar.setMaximum(discs.size());
                progressBar.setValue(0);
                progressBar.setIndeterminate(false);
                for (final Iterator iter = discs.iterator(); ((iter.hasNext())&&(!Stop));) {
                    final Disc disc = (Disc)iter.next();
                    count++;
                    final boolean success = checkGenre(disc.getGenre());
                    progressBar.setValue(count);
                    if (!success) {
                      //AZ: Process Notes
                      if (!(disc.getNotes()==null)) {
                    	//AZ: Replace CR and LF with SPACE
                    	//AZ: Trim notes
                    	int charLength = disc.getNotes().length();
                    	if (charLength > 30) {
                    		charLength=30;
                    	};
                    	
                    	if (charLength > 0) {
                    	String noteStr = disc.getNotes().substring(0,charLength-1);
                    	String noteStrNew = "";
                    	for ( int i=0; i<charLength-1; i++ ) {
                    		if (((int)noteStr.charAt(i)==10)) {
                    			noteStrNew= noteStrNew + " ";
                    		}
                    		else if (((int)noteStr.charAt(i)==13)) {
                    			noteStrNew= noteStrNew + " ";
                    		}
                    		else {
                    			noteStrNew= noteStrNew + noteStr.charAt(i);
                    		}
                    	}
                    	disc.setNotes(noteStrNew);
                    	}
                    	LOG.info("Erroneous genre found: " + disc.getArtist().getName() + " - " + disc.getName() + " : " + disc.getGenre());//AZ
                    	selectedDiscs.add(disc); 
                      }
                    }
                    if (Thread.interrupted()) {
                    	Stop = true;
                    }
                }
                
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
            return selectedDiscs;    // SwingWorker.get() returns this
        }
        return selectedDiscs;    // or this
    }
    
    /**
     * AZ 2009
     * Try to find specified genre in the standard table of genres  
     */     
    private boolean checkGenre(String genre){
    	boolean isGenre = false;
    	final List GENRE_LIST = MusicTag.getGenreTypes();
        String BasicGenre = genre;
        String[] separatorList = {":", ";", ".", "-", ",", "/"};
        int index = genre.length();
        if (genre.equalsIgnoreCase("<none>")) {
        	return isGenre;	
        }
        for(int i=0; i<separatorList.length; i++){
        	final int currentIndex = genre.indexOf(separatorList[i]);
        	if ((currentIndex > 0) & (currentIndex < index) ){
            	index = currentIndex;
            }
        }
    	BasicGenre = genre.substring(0, index);
        if (containsIgnoreCase(GENRE_LIST, BasicGenre)) {
        	isGenre = true;
        }
        return isGenre; 
    }   
    /**
     * Return True if String s is a member of List l
     */
    public boolean containsIgnoreCase(List <String> l, String s){	
   	 Iterator<String> it = l.iterator();
   	 while(it.hasNext()){ 		 
   	  if(it.next().compareToIgnoreCase(s)==0) {
   	  return true;
   	  }
   	 }
   	 return false;
   	}
    /**
     * Builds the progress-bar panel.
     * <p>
     * @return the panel
     */
    private JComponent buildProgressPanel() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        final FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, p, fill:pref:grow",
                                           "p, 4px, p");   
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();

        builder.addLabel(Resources.getString("label.Progress"), cc.xy(1, 1));
        builder.add(progressBar, cc.xyw(3, 1, 2));

        return builder.getPanel();
    }
    
    /**AZ
     * Saves the found genres errors report to a TXT file.
     * <p>
     * @param aEvt the event fired
     */
    protected void saveReport(final ActionEvent aEvt) {
       LOG.debug("Save Found Genres Errors Report pressed.");
       final JFileChooser chooser = new JFileChooser();
       chooser.setDialogTitle(Resources.getString("label.SaveReport"));
       String messageString;

       chooser.setFileFilter(FilterFactory.textFileFilter());
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
       file = FilterFactory.forceTextExtension(file);

       try {
          // now print errors and warns to the file
          final ArrayList results = new ArrayList();
          final int col = resultsTable.getColumnCount();
          for(int i=0; i<resultsTable.getRowCount(); i++){	
        	    messageString = i+Character.toString((char)9);
                for (int ii=0; ii<col; ii++) {
                	messageString = messageString + resultsTable.getValueAt(i, ii).toString() + Character.toString((char)9);
                }
                results.add(messageString);     
           }

          FileUtils.writeLines(file, null, results);

          MessageUtil.showInformation(this, Resources.getString("label.reportsaved"));
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

    /**
     * Add all found discs to discList.
     */
    protected void doAddToDiscList() {
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      final Disclist disclist = mainFrame.getDisclist();
      LOG.debug("Adding found discs to discList");
      
      GuiUtil.setBusyCursor(this, true);
      for (int i = 0; i < this.results.length; i++) {
           disclist.add(tableModel.getData()[i]);
      }  
      GuiUtil.setBusyCursor(this, false);
    }

}