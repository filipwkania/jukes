package com.melloware.jukes.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.util.Worker;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.file.MusicDirectory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.component.MessageCellRenderer;
import com.melloware.jukes.util.JukesValidationMessage;

/**
 * Searches for all directories that no longer exist for albums in the
 * catalog and removes them. Checks for the disc at the location on the hard
 * drive.  If that location no longer exists on the hard drive then this disc is
 * removed from the database.  This is useful for when using the Jukes with
 * portable storage devices like Archos Jukebox or IPods.
 * <p>
 * Thanks to Bill Farkas for suggesting this feature.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class DiscRemoveDialog
    extends AbstractDialog {

    private static final Log LOG = LogFactory.getLog(DiscRemoveDialog.class);
    private DefaultListModel listModel;
    private JButton buttonApply;
    private JButton buttonCancel;
    private JButton buttonClose;
    private JComponent buttonBar;
    private JList list;
    private JPanel panel;
    private JProgressBar progressBar;
    private Worker worker;

    /**
     * Constructs a default about dialog using the given owner.
     *
     * @param owner   the dialog's owner
     */
    public DiscRemoveDialog(Frame owner) {
        super(owner);
        LOG.debug("Disc Cleaner created.");
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doApply()
     */
    public void doApply() {
        LOG.debug("Apply pressed.");
        buttonCancel.setEnabled(true);
        buttonApply.setEnabled(false);
        buttonClose.setEnabled(false);
        LOG.info("[START] Disc Remover");

        /* Invoking start() on the SwingWorker causes a new Thread
         * to be created that will call construct(), and then finished().  Note that finished() is called even if the
         * worker is interrupted because we catch the InterruptedException in doWork().
         */
        worker = new Worker() {
                public Object construct() {
                    return doWork();
                }

                public void finished() {
                    buttonCancel.setEnabled(false);
                    buttonApply.setEnabled(true);
                    buttonClose.setEnabled(true);
                    threadFinished(get());
                }
            };
        worker.start();

    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractDialog#doCancel()
     */
    public void doCancel() {
        LOG.debug("Cancel Pressed.");
        if (worker != null) {
            worker.interrupt();
        }
        buttonCancel.setEnabled(false);
        buttonApply.setEnabled(true);
        buttonClose.setEnabled(true);
    }

    /**
     * Builds and answers the dialog's content.
     *
     * @return the dialog's content with tabbed pane and button bar
     */
    protected JComponent buildContent() {
    	final JPanel content = new JPanel(new BorderLayout());
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
        return new HeaderPanel(Resources.getString("label.DiscCleaner"),Resources.getString("label.DiscCleanerMessage"),
                               Resources.DISC_REMOVER_ICON);
    }

    /**
     * Builds and returns the dialog's pane.
     *
     * @return the dialog's  pane component
     */
    protected JComponent buildMainPanel() {
    	final JButton[] buttons = new JButton[3];
        final JButton button = createApplyButton();
        button.setText(Resources.getString("label.Remove"));
        buttonCancel = createCancelButton();
        buttonApply = button;
        buttonClose = createCloseButton(true);
        buttonClose.setText(Resources.getString("label.Close"));
        buttonCancel.setEnabled(false);
        buttonCancel.setText(Resources.getString("label.Cancel"));
        buttons[0] = buttonApply;
        buttons[1] = buttonCancel;
        buttons[2] = buttonClose;
        buttonBar = ButtonBarFactory.buildRightAlignedBar(buttons);
        final FormLayout layout = new FormLayout("fill:pref:grow", "p, p, p, 4px");
        final PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        final CellConstraints cc = new CellConstraints();
        int row = 1;
        row++;
        builder.add(buildProgressPanel(), cc.xy(1, row++));
        builder.add(buildListPanel(), cc.xy(1, 3));
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
        list.setVisibleRowCount(19);
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
        final FormLayout layout = new FormLayout("right:max(14dlu;pref), 4dlu, 550px, 4px",
                                           "p, 4px, p");    // extra bottom space for icons
        final PanelBuilder builder = new PanelBuilder(layout);
        final CellConstraints cc = new CellConstraints();

        builder.addLabel(Resources.getString("label.Progress"), cc.xy(1, 1));
        builder.add(progressBar, cc.xyw(3, 1, 2));

        return builder.getPanel();
    }

    /**
     * This method represents the application code that we'd like to
     * run on a separate thread.
     */
    private Object doWork() {
        Object result = null;
        try {
            // clear all old elements out
            listModel.removeAllElements();

            // get a list of all discs in the system
            // now just get the cached artists
            final Collection discs = HibernateDao.findAll(Disc.class, Disc.PROPERTYNAME_NAME);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Disc Count = " + discs.size());
            }

            // set the progressbar bounds
            progressBar.setMaximum(discs.size());
            progressBar.setValue(0);
            progressBar.setIndeterminate(false);

            // check them one by one and if the directory is not there then
            // remove them from the database
            int count = 0;
            for (final Iterator iter = discs.iterator(); iter.hasNext();) {
                final Disc disc = (Disc)iter.next();
                count++;
                final boolean success = MusicDirectory.removeDiscIfNoLongerExists(disc);
                String message = "";
                Severity severity = null;
                if (success) {
                    message = disc.getLocation();
                    severity = Severity.OK;
                } else {
                    message = "REMOVED " + disc.getLocation();
                    severity = Severity.ERROR;
                }
                updateList(message, severity, count);
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
			LOG.debug(result);
		}

        // refresh the tree
        ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
        LOG.info("[STOP] Disc Remover");
    }

    /**
     * When the worker needs to update the GUI we do so by queuing
     * a Runnable for the event dispatching thread with
     * SwingUtilities.invokeLater().  In this case we're just
     * changing the progress bars value.
     */
    private void updateList(final String aMessage, final Severity aSeverity, final int aProgress) {
    	final Runnable updateList = new Runnable() {
            public void run() {
                progressBar.setValue(aProgress);
                if (aSeverity == Severity.OK) {
                    return;
                }
                final ValidationMessage message = new JukesValidationMessage(aMessage, aSeverity);
                listModel.addElement(message);
                final int index = listModel.indexOf(message);
                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        };
        EventQueue.invokeLater(updateList);
    }

}