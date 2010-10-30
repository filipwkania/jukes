package com.melloware.jukes.gui.view.preferences;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.laf.ExtUIManager;
import com.jgoodies.uif.laf.LookConfiguration;
import com.jgoodies.uif.laf.LookConfigurations;
import com.jgoodies.uif.lazy.Preparable;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.util.MessageUtil;


/**
 * A panel for choosing a <code>LookAndFeel</code> and other
 * look configuration settings, e.g. a color theme.<p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * 
 * @see ListModel
 * @see ArrayListModel
 */

public final class LookAndFeelPanel extends JPanel {
   /**
    * Logger for this class
    */
   private static final Log LOG = LogFactory.getLog(LookAndFeelPanel.class);

    private static final ListModel PLASTIC_THEMES_MODEL =
        new ArrayListModel(PlasticLookAndFeel.getInstalledThemes());

    private static final ListModel NO_THEMES_MODEL =
        new ArrayListModel();


    private static ListModel supportedLookAndFeelInstances;

    private final LookConfigurations configurations;

    private JList lafList;
    private JList themesList;
    private transient ListSelectionListener themeListener;
    private LookAndFeelPreviewPanel previewPanel;
    private boolean isLookChanged = false;

    
    // Instance Creation ******************************************************
    
    /**
     * Constructs a <code>LookAndFeelPanel</code> using the given
     * apply trigger, which triggers the UI update.
     * 
     * @param triggerChannel    changes to <code>Boolean.TRUE</code> or 
     *      <code>Boolean.FALSE</code> to indicate a commit or flush event
     */
    public LookAndFeelPanel(ValueModel triggerChannel) {
        this.configurations = getClonedLookConfigurations();
        initComponents();
        build();
        registerListeners();
        triggerChannel.addValueChangeListener(new CommitHandler());
    }

    private static LookConfigurations getClonedLookConfigurations() {
        Object storedValue =
            ExtUIManager.getLookConfigurations();
        LookConfigurations storedConfigs = (LookConfigurations) storedValue;
        return (LookConfigurations) storedConfigs.clone();
    }

    // Public API ***********************************************************

    /**
     * Ensures that the preselections in the look-and-feel list
     * and in the themes list are visible.
     */
    public void ensureSelectionsAreVisible() {
        lafList.ensureIndexIsVisible(lafList.getSelectedIndex());
        themesList.ensureIndexIsVisible(themesList.getSelectedIndex());
    }

    // Building *************************************************************

    private void initComponents() {
        lafList = new JList();
        lafList.setModel(getSupportedLookAndFeelInstances());
        lafList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lafList.setCellRenderer(new LooksListCellRenderer());
        selectLook(configurations.getDefaultConfiguration().getLookAndFeel());

        themesList = new JList();
        updateThemesModel();
        selectTheme(getSelectedConfiguration().getTheme());
        themesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        themesList.setCellRenderer(new ThemesListCellRenderer());

        previewPanel = new LookAndFeelPreviewPanel();
    }
    
    /**
     * Builds the panel: look and feel panel on top, and the preview at the bottom.
     */
    private void build() {
        FormLayout fl = new FormLayout(
                "fill:default:grow, 4dlu, fill:default:grow", 
                "pref, 1dlu, fill:100dlu, 9dlu, pref, 1dlu, pref");
        fl.setColumnGroups(new int[][]{{1, 3}});
        fl.setRowGroups(new int[][]{{3, 7}});
        
        PanelBuilder builder = new PanelBuilder(fl, this);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addTitle(Resources.getString("label.LookFeel"),         cc.xy (1, 1));
        builder.addTitle(Resources.getString("label.Theme"),                cc.xy (3, 1));
        builder.add(new JScrollPane(lafList),    cc.xy (1, 3));
        builder.add(new JScrollPane(themesList), cc.xy (3, 3));
        builder.addTitle(Resources.getString("label.Preview"),              cc.xyw(1, 5, 3));
        builder.add(previewPanel,                cc.xyw(1, 7, 3));
    }

    /**
     * Registers listeners to respond to selections in the
     * look-and-feel and themes lists.
     */
    private void registerListeners() {
        // Listen to selection events.
        lafList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting())
                    changedLookAndFeel();
            }
        });
        themeListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting())
                    changedTheme();
            }
        };
        // Listen to selection changes
        themesList.addListSelectionListener(themeListener);
    }

    // Updating *************************************************************

    /**
     * The look and feel selection changed; performs the appropriate update actions.
     */
    private void changedLookAndFeel() {
        isLookChanged = true; 
        // Get the (new) selected look and feel.
        //LookAndFeel laf = getSelectedLookAndFeel();
        // Make it the selected configuration.
        //configurations.getDefaultConfiguration().
        	MessageUtil.showInformation(this, Resources.getString("messages.lookfeel")); //AZ

        // Update the themes list without firing the change listener.
        themesList.removeListSelectionListener(themeListener);
        updateThemesModel();
        selectTheme(getSelectedConfiguration().getTheme());
        themesList.addListSelectionListener(themeListener);

        updatePreviewPanel();
    }

    /**
     * The theme selection changed; performs the appropriate update actions.
     */
    private void changedTheme() {
        LookAndFeel laf = getSelectedLookAndFeel();
        if (!(laf instanceof PlasticLookAndFeel))
            return;
        putConfiguration(createLookConfiguration());
        updatePreviewPanel();
    }

    /**
     * Selects the specified <code>LookAndFeel</code>.
     */
    private void selectLook(LookAndFeel selectedLook) {
        String lafClassName = selectedLook.getClass().getName();
        ListModel model = lafList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            LookAndFeel laf = (LookAndFeel) model.getElementAt(i);
            if (lafClassName.equals(laf.getClass().getName())) {
                lafList.setSelectedIndex(i);
                lafList.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    /**
     * Selects the specified theme.
     */
    private void selectTheme(Object selectedTheme) {
        if (selectedTheme == null)
            return;
        String themeClassName = selectedTheme.getClass().getName();
        ListModel model = themesList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            PlasticTheme theme = (PlasticTheme) model.getElementAt(i);
            if (themeClassName.equals(theme.getClass().getName())) {
                themesList.setSelectedIndex(i);
                themesList.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    /**
     * Updates the preview panel.
     */
    private void updatePreviewPanel() {
        try {
            // Save the old L&F.
            LookAndFeel oldLaf = UIManager.getLookAndFeel();
            LookAndFeel selectedLaF = getSelectedLookAndFeel();
            PlasticTheme oldTheme = null;

            if (selectedLaF instanceof PlasticLookAndFeel) {
                oldTheme = (PlasticTheme) PlasticLookAndFeel.getPlasticTheme(); 
                PlasticTheme theme = getSelectedTheme();
                if (theme != null)
                    PlasticLookAndFeel.setPlasticTheme(theme);
            }

            UIManager.setLookAndFeel(selectedLaF);
            previewPanel.updateAndValidate();

            if (selectedLaF instanceof PlasticLookAndFeel)
                PlasticLookAndFeel.setPlasticTheme(oldTheme);

            // Restore the old L&F.
            UIManager.setLookAndFeel(oldLaf);
            previewPanel.updateComponentTree();

        } catch (UnsupportedLookAndFeelException e) {
            // Ignore unsupported looks
        }
    }

    /**
     * Updates the themes model.
     */
    private void updateThemesModel() {
        boolean supportsThemes =
            getSelectedLookAndFeel() instanceof PlasticLookAndFeel;

        ListModel newModel = supportsThemes 
                                ? PLASTIC_THEMES_MODEL 
                                : NO_THEMES_MODEL;
        if (themesList.getModel() != newModel)
            themesList.setModel(newModel);

        themesList.setEnabled(supportsThemes);
    }

    // Accessing the look and feel configurations ***************************

    private LookConfiguration getSelectedConfiguration() {
        return configurations.getDefaultConfiguration();
    }

    @SuppressWarnings("unchecked")
    private void putConfiguration(LookConfiguration config) {
        configurations.getConfigurations().add(config);
        configurations.setDefaultConfiguration(config);
    }

    private LookConfiguration createLookConfiguration() {
        LookAndFeel laf = getSelectedLookAndFeel();
        Object theme =
            laf instanceof PlasticLookAndFeel ? getSelectedTheme() : null;
        return new LookConfiguration(laf, theme);
    }

    private LookAndFeel getSelectedLookAndFeel() {
        return (LookAndFeel) lafList.getSelectedValue();
    }

    private PlasticTheme getSelectedTheme() {
        return (PlasticTheme) themesList.getSelectedValue();
    }

    // Computing the Supported Looks ****************************************

    /**
     * Answers a cached and lazily instantiated list of supported look and feels.
     */
    private synchronized static ListModel getSupportedLookAndFeelInstances() {
        if (supportedLookAndFeelInstances == null) {
            supportedLookAndFeelInstances =
                new ArrayListModel(computeSupportedLookAndFeelInstances());
        }
        return supportedLookAndFeelInstances;
    }

    /**
     * Computes the list of supported look and feels.
     */
    @SuppressWarnings("unchecked")
    private static java.util.List computeSupportedLookAndFeelInstances() {
        UIManager.LookAndFeelInfo[] lafInfos =
            UIManager.getInstalledLookAndFeels();
        java.util.List result = new ArrayList(lafInfos.length);
        for (int i = 0; i < lafInfos.length; i++) {
            String className = lafInfos[i].getClassName();
            LookAndFeel laf =
                ExtUIManager.createLookAndFeelInstance(className);
            if ((laf != null) && (laf.isSupportedLookAndFeel()))
                result.add(laf);
        }
        Collections.sort(result, new Comparator() {
            public int compare(Object o1, Object o2) {
                LookAndFeel laf1 = (LookAndFeel) o1;
                LookAndFeel laf2 = (LookAndFeel) o2;
                return laf1.getName().toUpperCase().compareTo(
                    laf2.getName().toUpperCase());
            }
        });
        return result;
    }

    // Inner Helper Classes ***************************************************
    
    private class CommitHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (Boolean.TRUE.equals(evt.getNewValue())) {
                if (isLookChanged) {
                    LOG.warn("You may have to restart Jukes to see the Look and Feel changes");
                }
                ExtUIManager.setLookConfigurations(configurations);
                final LookConfiguration config = createLookConfiguration();
                ExtUIManager.setDefaultLookConfiguration(config);
                final Preferences prefs = Application.getUserPreferences();
                if (config.getTheme() != null) {
                   prefs.put("laf.themeName."+ config.getLookAndFeel().getName(), config.getTheme().getClass().getName());
                }
                
            }
        }
    }

    // Renders instances of LookAndFeel displaying their name
    private static class LooksListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected,
                boolean cellHasFocus) {
            return super.getListCellRendererComponent(
                list,
                ((LookAndFeel) value).getName(),
                index, isSelected, cellHasFocus);
        }
    }
    
    // Renders instances of MetalTheme
    private static class ThemesListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected,
                boolean cellHasFocus) {
            return super.getListCellRendererComponent(
                list,
                (value instanceof PlasticTheme
                    ? ((PlasticTheme) value).getName()
                    : value),
                index, isSelected, cellHasFocus);
        }
    }
    
    /**
    * An implementation of the <code>Preparable</code> interface 
    * that computes a list of supported <code>LookAndFeel</code> instances.
    */
    public static class EagerInitializer implements Preparable {

        /**
         * Ensures that the supported look and feels are computed and that 
         * the look and feel preview panel has been loaded once.
         */
        public void prepare() {
            Logger.getLogger("LookAndFeelPanel").info(
                "Computing supported look and feels...");
            getSupportedLookAndFeelInstances();
            Logger.getLogger("LookAndFeelPanel").info(
                "Preloading preview panel classes...");
            new LookAndFeelPreviewPanel();
        }
    }

}