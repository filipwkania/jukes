package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.exception.InfrastructureException;
import com.melloware.jukes.file.filter.FilterFactory;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.component.ComponentFactory;
import com.melloware.jukes.gui.view.component.MessageCellRenderer;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.JukesValidationMessage;
import com.melloware.jukes.util.MessageUtil;

/**
 * Compares a text file list of a catalog against the current catalog loaded
 * in the database.  Useful for comparing what one catalog has that the other
 * one doesn't or what they have in common.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */
@SuppressWarnings("unchecked")
public final class DifferenceToolDialog
    extends AbstractDialog {

    private static final Log LOG = LogFactory.getLog(DifferenceToolDialog.class);
    private static final String BREAK = " - ";
    private final Map mapDiscs = new HashMap();
    private DefaultListModel listModel;
    private JButton buttonCancel;
    private JButton buttonClose;
    private JButton buttonDiff;
    private JButton buttonIntersection;
    private JButton buttonSave;
    private JButton buttonUnion;
    private JComponent buttonBar;
    private JList list;
    private JPanel panel;
    private JProgressBar progressBar;
    private JTextComponent file;
    private JCheckBox caseSensitive;
    private final Settings settings;
    private Worker worker;

    /**
     * Constructs a default about dialog using the given owner.
     *
     * @param owner   the dialog's owner
     */
    public DifferenceToolDialog(Frame owner, Settings settings) {
        super(owner);
        LOG.debug("Difference Tool created.");
        this.settings = settings;
        this.settings.getStartInDirectory();
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doCancel()
     */
    public void doCancel() {
        LOG.debug("Cancel Pressed.");
        if (worker != null) {
            worker.interrupt();
        }
    }

    /**
     * Builds and answers the dialog's content.
     *
     * @return the dialog's content with tabbed pane and button bar
     */
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout());
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
        return new HeaderPanel(Resources.getString("label.DifferenceAnalyzerTool"),
        		Resources.getString("label.DifferenceAnalyzerToolMessage"),
                               Resources.DIFFERENCE_TOOL_ICON);
    }

    /**
     * Builds and returns the dialog's pane.
     *
     * @return the dialog's  pane component
     */
    protected JComponent buildMainPanel() {
        // Create an action with an icon
        Action diff = new AbstractAction(Resources.getString("label.Differences"), Resources.DIFFERENCE_TOOL_DIFF_ICON) {
            // This method is called when the button is pressed
            public void actionPerformed(ActionEvent evt) {
                doCompare(evt);
            }
        };
        Action intersection = new AbstractAction(Resources.getString("label.Intersection"), Resources.DIFFERENCE_TOOL_INTERSECTION_ICON) {
            // This method is called when the button is pressed
            public void actionPerformed(ActionEvent evt) {
                doCompare(evt);
            }
        };
        Action union = new AbstractAction(Resources.getString("label.Union"), Resources.DIFFERENCE_TOOL_UNION_ICON) {
            // This method is called when the button is pressed
            public void actionPerformed(ActionEvent evt) {
                doCompare(evt);
            }
        };
        Action export = new AbstractAction(Resources.getString("label.Save"), Resources.FILE_TEXT_ICON) {
            // This method is called when the button is pressed
            public void actionPerformed(ActionEvent evt) {
                save(evt);
            }
        };
        buttonSave = new JButton(export);
        buttonDiff = new JButton(diff);
        buttonIntersection = new JButton(intersection);
        buttonUnion = new JButton(union);
        buttonCancel = createCancelButton();
        buttonClose = createCloseButton(true);
        buttonClose.setText(Resources.getString("label.Close"));
        buttonCancel.setEnabled(false);
        buttonCancel.setText(Resources.getString("label.Cancel"));
        JButton[] buttons = new JButton[6];
        buttons[0] = buttonDiff;
        buttons[1] = buttonIntersection;
        buttons[2] = buttonUnion;
        buttons[3] = buttonSave;
        buttons[4] = buttonCancel;
        buttons[5] = buttonClose;
        buttonBar = ButtonBarFactory.buildCenteredBar(buttons);
        FormLayout layout = new FormLayout("fill:pref:grow", "p, p, p, p, p, p, p, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        int row = 1;
        builder.addSeparator(Resources.getString("label.Find"), cc.xy(1, row++));
        builder.add(buildFilePanel(), cc.xy(1, row++));
        builder.add(buildProgressPanel(), cc.xy(1, row++));
        builder.add(buildOptionsPanel(), cc.xy(1, row++));
        builder.add(buildListPanel(), cc.xy(1, 8));
        panel = builder.getPanel();
        panel.setBorder(Borders.DIALOG_BORDER);
        return panel;
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doCloseWindow()
     */
    protected void doCloseWindow() {
        super.doClose();
    }

    /**
    * Performs the comparison in a thread.
    *
    * @param aEvt the ActionEvent fired
    */
    protected void doCompare(ActionEvent aEvt) {
        LOG.debug(aEvt.getActionCommand());

        final String command = aEvt.getActionCommand();
        buttonCancel.setEnabled(true);
        buttonDiff.setEnabled(false);
        buttonUnion.setEnabled(false);
        buttonIntersection.setEnabled(false);
        buttonClose.setEnabled(false);
        buttonSave.setEnabled(false);
        GuiUtil.setBusyCursor(this, true);

        /* Invoking start() on the SwingWorker causes a new Thread
         * to be created that will call construct(), and then finished().  Note that finished() is called even if the
         * worker is interrupted because we catch the InterruptedException in doWork().
         */
        worker = new Worker() {
                public Object construct() {
                    return doWork(command);
                }

                public void finished() {
                    threadFinished(get());
                }
            };
        worker.start();

    }

    /**
    * Resizes the given component to give it a quadratic aspect ratio.
    *
    * @param component   the component to be resized
    */
    protected void resizeHook(JComponent component) {
        //Resizer.ONE2ONE.resizeDialogContent(component);
    }

    /**
     * Saves the list to a text file.
     * <p>
     * @param aEvt the ActionEvent fired.
     */
    protected void save(ActionEvent aEvt) {
        LOG.debug("Save pressed.");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(Resources.getString("label.SaveReport"));

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
            // now query the catalog and save the results to the file
            ArrayList results = new ArrayList();
            Enumeration enumeration = listModel.elements();
            while (enumeration.hasMoreElements()) {
                ValidationMessage message = (ValidationMessage)enumeration.nextElement();
                results.add(message.formattedText());
            }

            FileUtils.writeLines(file, "UTF-8", results); //AZ: implementation of UTF-8

            MessageUtil.showInformation(this, Resources.getString("label.reportsaved"));
        } catch (IOException ex) {
            LOG.error("Error writing file: \n\n" + ex.getMessage(), ex);
        } catch (InfrastructureException ex) {
            LOG.error("Error writing file: \n\n" + ex.getMessage(), ex);
        } catch (Exception ex) {
            LOG.error("Unexpected error writing file.", ex);
        }
    }

    /**
     * Finds if we have this disc in the database by artist and disc name.
     * <p>
     * @param aArtistName the artist name to find
     * @param aDiscName the disc name to find
     * @return true if we already have this, false if not
     */
    private boolean isHashMatch(String aArtistName, String aDiscName) {
        final String key = aArtistName + BREAK + aDiscName;
        return mapDiscs.containsKey(key.toUpperCase(Locale.US));
    }

    /**
     * Builds the file selection panel.
     * <p>
     * @return the panel used to select the file.
     */
    private JComponent buildFilePanel() {
        file = new JTextField();
        ((JTextField)file).setColumns(50);
        file.setText("");
        final FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, left:min(60dlu;pref):grow, pref, 40dlu, ,pref, pref",
                                           "p");
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();

        builder.addLabel(Resources.getString("label.SelectFile"), cc.xy(1, 1));
        builder.add(file, cc.xyw(3, 1, 3));
        builder.add(ComponentFactory.createFileChooserButton(file, Resources.getString("label.SelectFile"), FilterFactory.textFileFilter()),
                    cc.xy(6, 1));

        return builder.getPanel();
    }

    /**
     * Builds the message list panel.
     * <p>
     * @return the panel used to display messages
     */
    private JComponent buildListPanel() {
        listModel = new DefaultListModel();
        // Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setFocusable(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setCellRenderer(new MessageCellRenderer());
        list.setVisibleRowCount(18);
        return new JScrollPane(list);
    }

    /**
     * Builds the progressbar panel.
     * <p>
     * @return the panel used to select the directory.
     */
    private JComponent buildProgressPanel() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        final FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, fill:pref:grow", "p"); 
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();

        builder.addLabel(Resources.getString("label.Progress"), cc.xy(1, 1));
        builder.add(progressBar, cc.xy(3, 1));

        return builder.getPanel();
    }

    /**
     * Builds the options panel.
     * <p>
     * @return the panel containing any options
     */
    private JComponent buildOptionsPanel() {
        caseSensitive = new JCheckBox();
        caseSensitive.setSelected(true);
        final FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, fill:pref:grow", "p, 4px, p");    // extra bottom space for icons

        layout.setRowGroups(new int[][] {
                                { 1, 3 }
                            });
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();

        builder.addLabel(Resources.getString("label.CaseSensitive"), cc.xy(1, 1));
        builder.add(caseSensitive, cc.xy(3, 1));

        return builder.getPanel();
    }
    
    /**
     * Compares the text catalog to the database for differences
     * <p>
     * @param aDirectory the file to recurse
     * @throws InterruptedException if the thread is Interrupted stop processing
     */
    private void comparison(File aFile, String aCommand)
                     throws InterruptedException {

        // build a hasmap of discs for fast searching
        final Collection allDiscs = loadAllDiscs();
        for (Iterator iter = allDiscs.iterator(); iter.hasNext();) {
            final String key = (String)iter.next();
            mapDiscs.put(key.toUpperCase(Locale.US), key);
        }

        // load the file as Strings
        Collection lines;
        List unionList = new ArrayList();
        try {
            lines = FileUtils.readLines(aFile, "UTF-8");//AZ: Use UTF-8 
            
            //AZ: convert lines to "artist TAB disc" format
            List ucList = new ArrayList();
            final Iterator itLines = lines.iterator();
            while (itLines.hasNext()) {
               String element = (String) itLines.next();
               final String[] value = StringUtils.split(element, Resources.TAB);
               final String artist = value[0];
               final String disc = value[2];
               element = artist + Resources.TAB + disc;
               if (!caseSensitive.isSelected()) {
            	   element = element.toUpperCase();   
               }
               ucList.add(element);
            }  
            lines = ucList;
            
            int counter = 0;
            resetProgressBar(lines.size());
            for (Iterator iter = lines.iterator(); iter.hasNext();) {
                counter++;
                String line = (String)iter.next();
                if (Thread.interrupted()) {
                    LOG.debug("Thread interrupted.");
                    throw new InterruptedException();
                }
                final String[] value = StringUtils.split(line, Resources.TAB);
                final String artist = value[0];
                final String disc = value[1];
                final String discString = artist + BREAK + disc;
                if (Resources.getString("label.Differences").equals(aCommand)) {
                    if (!isHashMatch(artist, disc)) {
                        final String output = "To Import: " + discString;
                        final String message = output;
                        final Severity severity = Severity.WARNING;
                        updateList(message, severity, counter);
                    }
                } else if (Resources.getString("label.Intersection").equals(aCommand)) {
                    if (isHashMatch(artist, disc)) {
                        final String message = discString;
                        final Severity severity = Severity.OK;
                        updateList(message, severity, counter);
                    }
                } else if (Resources.getString("label.Union").equals(aCommand)) {
                    unionList.add(discString);
                } else {
                    throw new InterruptedException("Not a valid command button.");
                }
            }
            progressBar.setValue(counter);

            // now get the differences the other way
            if (Resources.getString("label.Differences").equals(aCommand)) {
                // loop through all discs in the catalog and look in file
                final Collection discs = loadAllDiscs();
                counter = 0;
                resetProgressBar(discs.size());
                for (Iterator iter = discs.iterator(); iter.hasNext();) {
                    counter++;
                    final String disc = (String)iter.next();
                    String line = StringUtils.replace(disc, BREAK, Resources.TAB, 1);
                    if (!lines.contains(line)) {
                        final String message = "To Export: " + disc;
                        final Severity severity = Severity.OK;
                        updateList(message, severity, counter);
                    }
                }
                progressBar.setValue(counter);
            }

            // now union the current catalog into it
            if (Resources.getString("label.Union").equals(aCommand)) {
                final Collection discs = loadAllDiscs();

                for (Iterator iter = discs.iterator(); iter.hasNext();) {
                    String disc = (String)iter.next();
                    unionList.add(disc);
                }
                Collections.sort(unionList);
                final SetUniqueList uniqueList = SetUniqueList.decorate(unionList);
                counter = 0;
                resetProgressBar(uniqueList.size());
                for (Iterator iter = uniqueList.iterator(); iter.hasNext();) {
                    counter++;
                    String element = (String)iter.next();
                    final Severity severity = Severity.OK;
                    updateList(element, severity, counter);
                }
                progressBar.setValue(counter);
            }
            
        } catch (IOException ex) {
            final String error = "Error reading file: " + ex.getMessage();
            LOG.error(error);
            throw new InterruptedException(error);
        } catch (InfrastructureException ex) {
            final String error = "Error querying database: " + ex.getMessage();
            LOG.error(error);
            throw new InterruptedException(error);
        } catch (Exception ex) {
            final String error = "Error reading file: " + ex.getMessage();
            LOG.error(error);
            throw new InterruptedException(error);
        }
    }

    /**
     * This method represents the application code that we'd like to
     * run on a separate thread.
     */
    private Object doWork(String aCommand) {
        Object result = null;

        try {

            // clear all old elements out
            listModel.removeAllElements();

            // get the file from the text box
            String filename = this.file.getText();
            File file = new File(filename);
            // make sure it is a file
            if (!file.exists()) {
                LOG.error("Please select a valid file");
                throw new InterruptedException();
            }

            // now do differences
            comparison(file, aCommand);

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
     * Speadily loads all discs sorted by artist and disc.  Puts them into a
     * collection as "Artist - Disc".
     * <p>
     * @return the collection of discs strings
     */
    private Collection loadAllDiscs() {
        ArrayList results = new ArrayList();
        Collection discs = HibernateDao.findByQuery(Resources.getString("hql.export.catalog"));

        for (Iterator iter = discs.iterator(); iter.hasNext();) {
            Object[] queryResult = (Object[])iter.next();

            // Artist - Disc
            String disc = queryResult[0] + BREAK + queryResult[2];
            if (!caseSensitive.isSelected()) {
               disc = disc.toUpperCase();
            } 
            results.add(disc);
        }
        return results;
    }

    /**
     * Resets the progress bar and gives it a max progress.
     * <p>
     * @param aMaxProgress the maximum value of the progress bar.
     */
    private void resetProgressBar(int aMaxProgress) {
        progressBar.setMaximum(aMaxProgress);
        progressBar.setValue(0);
        progressBar.setIndeterminate(false);
    }

    /**
     * When the thread is finished this method is called.
     * <p>
     * @param result the Object return from the doWork thread.
     */
    private void threadFinished(Object result) {
    	if (LOG.isDebugEnabled()) {
        	LOG.debug("Thread Finished");
			LOG.debug(result);
		}
        GuiUtil.setBusyCursor(this, false);
        buttonCancel.setEnabled(false);
        buttonDiff.setEnabled(true);
        buttonUnion.setEnabled(true);
        buttonIntersection.setEnabled(true);
        buttonClose.setEnabled(true);
        buttonSave.setEnabled(true);
    }

    /**
     * When the worker needs to update the GUI we do so by queuing
     * a Runnable for the event dispatching thread with
     * SwingUtilities.invokeLater().  In this case we're just
     * changing the progress bars value.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    private void updateList(final String aMessage, final Severity aSeverity, final int aProgress)
                     throws InterruptedException, InvocationTargetException {
        Runnable updateList = new Runnable() {
            public void run() {
                progressBar.setValue(aProgress);
                ValidationMessage message = new JukesValidationMessage(aMessage, aSeverity);
                listModel.addElement(message);
                int index = listModel.indexOf(message);
                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        };
        EventQueue.invokeAndWait(updateList);
    }

}