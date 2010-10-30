package com.melloware.jukes.gui.tool;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.melloware.jukes.gui.tool.help.HelpNode;
import com.melloware.jukes.gui.tool.help.HelpSets;

/**
 * Provides bound bean properties for the help tree model,
 * the help page and the help visibility.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class DynamicHelpModule
    extends com.jgoodies.binding.beans.Model {

    private static final Log LOG = LogFactory.getLog(DynamicHelpModule.class);

    public static final String PROPERTYNAME_HELP_TREE_MODEL = "helpTreeModel";
    public static final String PROPERTYNAME_HELP_PAGE = "helpPage";
    public static final String PROPERTYNAME_HELP_VISIBLE = "helpVisible";

    /**
     * Determines whether the dynamic help shall be displayed or hidden.
     *
     * @see #isHelpVisible()
     * @see #setHelpVisible(boolean)
     */
    private boolean helpVisible;

    /**
     * Maps domain classes to help roots.
     */
    private final Map helpRegistry;

    /**
     * Holds the model for the help tree.
     * It changes everytime the selection changes.
     *
     * @see #getHelpTreeModel()
     * @see #getHelpTreeSelectionModel()
     */
    private TreeModel helpTreeModel;

    /**
     * Refers to the single selection model for the help tree.
     * The selection is reset if the help tree model changes.
     *
     * @see #getHelpTreeSelectionModel()
     * @see #getHelpTreeModel()
     */
    private final TreeSelectionModel helpTreeSelectionModel;

    /**
     * Holds the URL for the currently selected help page.
     *
     * @see #getHelpPage()
     */
    private URL helpPage;

    /**
     * Constructs a <code>DynamicHelpModule</code>
     * with the welcome help set preselected.
     */
    DynamicHelpModule() {
        helpRegistry = new HashMap();

        setHelpSet(HelpSets.WELCOME_HELP_SET);
        helpTreeSelectionModel = new DefaultTreeSelectionModel();
        helpTreeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        helpTreeSelectionModel.addTreeSelectionListener(new HelpTreeSelectionChangeHandler());
    }

    /**
     * Returns the current help page, a URL. This page
     * will be updated if the selection in the help tree changes.
     *
     * @return the URL for the currently selected help page.
     */
    public URL getHelpPage() {
        return helpPage;
    }

    /**
     * Returns the tree model for the help tree.
     *
     * @return the tree model for the help tree.
     */
    public TreeModel getHelpTreeModel() {
        return helpTreeModel;
    }

    /**
     * Returns the fixed selection model for the navigation tree.
     *
     * @return the fixed selection model for the navigation tree.
     */
    public TreeSelectionModel getHelpTreeSelectionModel() {
        return helpTreeSelectionModel;
    }

    /**
     * Sets a new visibility for the help viewer.
     *
     * @param newVisibility   the help page to set
     *
     * @see #isHelpVisible()
     */
    public void setHelpVisible(boolean newVisibility) {
        boolean oldVisibility = isHelpVisible();
        helpVisible = newVisibility;
        firePropertyChange(PROPERTYNAME_HELP_VISIBLE, oldVisibility, newVisibility);
    }

    /**
     * Answers whether the dynamic help is visible or not.
     *
     * @return true if the help viewer is visible, false if hidden.
     */
    public boolean isHelpVisible() {
        return helpVisible;
    }

    /**
     * Registers a help tree for a domain class.
     */
    @SuppressWarnings("unchecked")
    public void registerHelp(Class domainClass, TreeNode node) {
        Object oldValue = helpRegistry.put(domainClass, node);
        if (oldValue != null) {
            LOG.warn("Duplicate help registered for class " + domainClass);
        }
    }

    void updateHelpSet(Object selection) {
        TreeNode helpSet = lookupHelpSet(selection.getClass());
        if (helpSet != null) {
            setHelpSet(helpSet);
        }
    }

    /**
     * Sets a URL as new help page. This method is invoked
     * by the HelpTreeSelectionChangeHandler that observes changes in
     * the selection of the help tree.<p>
     *
     * Sets the help to visible.
     *
     * @param newHelpPage   the help page to set
     *
     * @see #getHelpPage()
     */
    private void setHelpPage(URL newHelpPage) {
        URL oldHelpPage = getHelpPage();
        helpPage = newHelpPage;
        if (equals(oldHelpPage, newHelpPage)) {
            return;
        }

        firePropertyChange(PROPERTYNAME_HELP_PAGE, oldHelpPage, newHelpPage);
        setHelpVisible(true);
    }

    private void setHelpSet(TreeNode helpSet) {
        setHelpTreeModel(new DefaultTreeModel(helpSet));
    }

    /**
     * Sets a new tree model for the help tree.
     *
     * @param newModel  the new tree model to set
     */
    private void setHelpTreeModel(TreeModel newModel) {
        TreeModel oldModel = getHelpTreeModel();
        helpTreeModel = newModel;
        firePropertyChange(PROPERTYNAME_HELP_TREE_MODEL, oldModel, newModel);
    }

    /**
     * Returns the <code>HelpNode</code> that has been registered
     * for the given domain object class, <code>null</code> if none.
     *
     * @param domainClass   the class associated with the editor to lookup
     * @return the editor associated with the given domain class
     */
    private TreeNode lookupHelpSet(Class domainClass) {
        return (TreeNode)helpRegistry.get(domainClass);
    }

    // Listens to changes in the help tree node selection
    // and updates the selection.
    private class HelpTreeSelectionChangeHandler
        implements TreeSelectionListener {

        /**
         * The selected node in the help tree has changed.
         * Updates this module's help page.<p>
         *
         * Does nothing if the new tree selection is <code>null</code>.
         *
         * @param evt    the event that describes the change
         */
        public void valueChanged(TreeSelectionEvent evt) {
            TreePath path = evt.getPath();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
            HelpNode helpNode = (HelpNode)selectedNode.getUserObject();
            if ((null == helpNode) || helpNode.isChapter()) {
                return;
            }

            setHelpPage(helpNode.getURL());
        }

    }

}