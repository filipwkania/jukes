package com.melloware.jukes.gui.view.preferences;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.Sizes;
import com.jgoodies.uif.AbstractDialog;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.application.ResourceIDs;
import com.jgoodies.uif.util.Resizer;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.util.GuiUtil;
import com.melloware.jukes.util.MessageUtil;

/**
 * Builds the preferences dialog.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class PreferencesDialog
    extends AbstractDialog {
    private static final Log LOG = LogFactory.getLog(PreferencesDialog.class);

    /**
     * Holds the panel for the look&feel choice and preview.
     */
    private LookAndFeelPanel lafPanel;

    /**
     * Holds the panel for the settings editor
     */
    private PropertySheetSettings settingsSheet;

    /**
     * Refers to the settings to be edited.
     */
    private final Settings settings;
    
    private JComponent mainPanel;
    private JTabbedPane tabbedPane;

    /**
     * Constructs a <code>PreferencesDialog</code>.
     *
     * @param owner      this dialog's parent frame
     * @param settings   the settings to edit
     */
    public PreferencesDialog(Frame owner, Settings settings) {
        super(owner);
        this.settings = settings;
    }

    /* (non-Javadoc)
     * @see com.jgoodies.swing.AbstractBoundDialog#doApply()
     */
    public void doApply() {
    	LOG.debug("Apply pressed");
        GuiUtil.setBusyCursor(this, true);

        // close all editors editing
        GuiUtil.stopTableEditing(settingsSheet.getSheet().getTable());

        // loop through the properties and set the Settings
        final PropertySheetPanel sheet = settingsSheet.getSheet();
        for (int i = 0; i < sheet.getPropertyCount(); i++) {
            final Property property = sheet.getProperties()[i];
            property.writeToObject(settings);
        }

        GuiUtil.setBusyCursor(this, false);
        super.doApply();
        //AZ refresh main tree
        ActionManager.get(Actions.REFRESH_ID).actionPerformed(null);
    }


    /**
     * Builds and returns the preference's content pane.
     */
    protected JComponent buildContent() {
    	mainPanel = new JPanel(new BorderLayout());
        JButton[] buttons = new JButton[3];
        Action restore = new AbstractAction(Resources.getString("label.RestoreDefaults")) {
            public void actionPerformed(ActionEvent evt) {
                restoreDefaults(evt);
            }
        };
        JButton buttonRestore = new JButton(restore);
        JButton buttonApply = createAcceptButton(Resources.getString("label.Apply"), true);
        buttons[0] = buttonRestore;
        buttons[1] = buttonApply;
        buttons[2] = createCloseButton(true);
        buttons[2].setText(Resources.getString("label.Close"));
        JPanel buttonBar = ButtonBarFactory.buildRightAlignedBar(buttons);
        buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
        tabbedPane = buildTabbedPane();
        mainPanel.add(tabbedPane , BorderLayout.CENTER);
        mainPanel.add(buttonBar, BorderLayout.SOUTH);
        return mainPanel;
    }

    /**
    * Builds and returns the dialog's system tab.
    *
    * @return the system tab component
    */
    protected JComponent buildGeneralTab() {
        settingsSheet = new PropertySheetSettings(settings);
        int width = Sizes.dialogUnitXAsPixel(300, settingsSheet);
        settingsSheet.setPreferredSize(Resizer.DEFAULT.fromWidth(width));
        settingsSheet.setBorder(Borders.DIALOG_BORDER);
        return settingsSheet;
    }

    /**
     * Builds and returns the preference's header.
     */
    protected JComponent buildHeader() {
        return new HeaderPanel(Resources.getString("label.Preferences"),
        		               Resources.getString("label.PreferencesMessage"),
                               ResourceUtils.getIcon(ResourceIDs.PREFERENCES_ICON));
    }

    /**
     * Builds and returns the tabbed pane.
     */
    protected JTabbedPane buildTabbedPane() {
    	//final LafChoiceModel model = new LafChoiceModel(getTriggerChannel());
        lafPanel = new LookAndFeelPanel(getTriggerChannel());

        JTabbedPane pane = new JTabbedPane();
        pane.addTab(Resources.getString("label.General"), buildGeneralTab());
        pane.addTab(Resources.getString("label.LookFeel"), lafPanel);
        return pane;
    }

    /**
     * Closes the window.
     */
    protected void doCloseWindow() {
        doCancel();
    }

    /**
     * Unlike the default try to get an aspect ratio of 1:1.
     */
    protected void resizeHook(JComponent component) {
        Resizer.ONE2ONE.resizeDialogContent(component);
    }

    /**
     * Restore defaults to the Settings.
     * <p>
     * @param aEvt the ActionEvent fired
     */
    protected void restoreDefaults(ActionEvent aEvt) {
    	LOG.debug("Restore Defaults");
    	
    	if (!MessageUtil.confirmUpdate(this)) {
    		return;
    	}
    	
        Preferences prefs = Application.getUserPreferences();
        try {
            prefs.clear();
            settings.restoreFrom(prefs);
            settingsSheet = new PropertySheetSettings(settings);
            tabbedPane.remove(0);
            tabbedPane.insertTab(Resources.getString("label.General"), null, settingsSheet, Resources.getString("label.GeneralTip"), 0);
            tabbedPane.setSelectedIndex(0);
            mainPanel.updateUI();
        } catch (BackingStoreException ex) {
            LOG.error("BackingStoreException", ex);
            MessageUtil.showError(this, "BackingStoreException"); //AZ 
        }

    }

}