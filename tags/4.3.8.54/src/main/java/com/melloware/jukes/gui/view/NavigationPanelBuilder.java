package com.melloware.jukes.gui.view;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import skt.swing.SwingUtil;
import skt.swing.search.IncrementalSearchKeyListener;
import skt.swing.search.TreeFindAction;

import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.component.UIFTree;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.node.AbstractTreeNode;
import com.melloware.jukes.gui.view.node.NavigationNode;
import com.melloware.jukes.gui.view.node.TrackNode;
import com.melloware.jukes.util.GuiUtil;

/**
 * Builds the navigation panel that is primarily intended to display a tree of
 * <code>NavigationNode</code> instances. Since Skeleton Pro has no tree data at
 * application startup time, there's an empty white panel that is displayed as
 * long as there's no data. The first time the tree gets a tree model, the empty
 * panel is replaced by the tree. The empty panel and the tree are switched in a
 * CardPanel.
 * <p>
 * This panel is embedded in a stripped scrollpane that in turn is contained in
 * a <code>SimpleInternalFrame</code> that in turn is contained in the main
 * page.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ 2009
 */
final class NavigationPanelBuilder {

   private static final Log LOG = LogFactory.getLog(NavigationPanelBuilder.class);

   /**
    * Refers to the tree that displays the navigation nodes.
    * @see #initComponents()
    */
   private UIFTree tree;

   /**
    * Refers to the module that holds the tree model and tree selection model.
    */
   private final MainModule module;

   /**
    * Constructs a <code>NavigationPanelBuilder</code> for the given module.
    * @param mainModule provides the tree model and tree selection model
    */
   NavigationPanelBuilder(MainModule mainModule) {
      this.module = mainModule;
      initComponents();
      mainModule.addPropertyChangeListener(MainModule.PROPERTYNAME_NAVIGATION_TREE_MODEL, new TreeModelChangeHandler());
      SelectionChangeHandler selectionHandler = new SelectionChangeHandler();
      mainModule.addPropertyChangeListener(MainModule.PROPERTYNAME_SELECTION, selectionHandler);
      mainModule.addPropertyChangeListener(MainModule.PROPERTYNAME_HIGHLIGHT, selectionHandler);
   }

   /**
    * Builds and returns a CardPanel with two cards: one for the navigation
    * tree, another for the empty card..
    */
   JComponent build() {
      return UIFactory.createStrippedScrollPane(tree);
   }

   /**
    * Creates, binds and configures the navigation tree used to display the
    * <code>NavigationNodes</code>. Requests the tree model and the tree
    * selection model from the MainModule.
    */
   private void initComponents() {
      tree = new RefreshedTree(module.getNavigationTreeModel());
      tree.setSelectionModel(module.getNavigationTreeSelectionModel());
      final NavigationTreeExpandListener expander = new NavigationTreeExpandListener();
      tree.addTreeWillExpandListener(expander);
      tree.addTreeExpansionListener(expander);
      tree.putClientProperty("JTree.lineStyle", "None");
      tree.setRootVisible(false);
      tree.setShowsRootHandles(true);
      tree.setCellRenderer(new TreeCellRenderer());

      // add the incremental searching to the tree
      final TreeFindAction findAction = new TreeFindAction(true);
      SwingUtil.installActions(tree, new Action[] { new TreeFindAction(true), findAction });
      tree.addKeyListener(new IncrementalSearchKeyListener(findAction));

      // Add listener to components that can bring up popup menus.
      final JPopupMenu popup = MainMenuBuilder.buildPlayerPopupMenu(tree);
      MouseListener popupListener = new TreePopupListener(popup);
      tree.addMouseListener(popupListener);

      tree.setEditable(false);
      tree.setScrollsOnExpand(true);

      // Enable tool tips.
      ToolTipManager.sharedInstance().registerComponent(tree);
   }

   /**
    * Updates the expansion state of the selected node; honors the auto expand
    * and collapse settings.
    */
   private void updateExpansionState() {
      MainFrame mf = (MainFrame) Application.getDefaultParentFrame();
      AbstractTreeNode node = (AbstractTreeNode) mf.getMainModule().getSelectedNode();

      if (node != null) {
         final DefaultTreeModel treeModel = ((DefaultTreeModel) tree.getModel());
         final TreeNode[] nodes = treeModel.getPathToRoot(node);
         final TreePath path = new TreePath(nodes);
         boolean expanded = tree.isExpanded(path);
         if (expanded) {
            tree.expandPath(path);
         }
      }
   }

   /**
    * Sets a new tree model.
    * @param newModel the new tree model
    */
   private void updateModel(TreeModel newModel) {
      tree.setModel(newModel);
      // tree.setSelectionRow(0);
   }

   /**
    * Updates the selection state of the node.
    */
   private void updateSelectedState() {
      MainFrame mf = (MainFrame) Application.getDefaultParentFrame();
      AbstractTreeNode node = (AbstractTreeNode) mf.getMainModule().getSelectedNode();
      if (node != null) {
         final DefaultTreeModel treeModel = ((DefaultTreeModel) tree.getModel());
         final TreeNode[] nodes = treeModel.getPathToRoot(node);
         final TreePath path = new TreePath(nodes);

         // tree.setSelectionPath(path);
         tree.scrollPathToVisible(path);
         tree.makeVisible(path);
         tree.setSelectionPath(path);
      }
   }

   // Renders icons and labels for the NavigationNodes.
   private static class TreeCellRenderer extends DefaultTreeCellRenderer {

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
               boolean leaf, int row, boolean focus) {
         super.getTreeCellRendererComponent(tree, value, selected, expanded, false, row, focus);
         NavigationNode node = (NavigationNode) value;
         if (node.getFontColor() == null) {
            this.setForeground(sel ? getTextSelectionColor() : getTextNonSelectionColor());
         } else {
            this.setForeground(sel ? getTextSelectionColor() : node.getFontColor());
         }

         selected = sel;
         UIFTree extTree = (UIFTree) tree;
         this.setFont((node.getFont() == null) ? extTree.getFont() : node.getFont());
         this.setIcon(node.getNodeIcon(sel));
         this.setText(node.getName());
         this.setToolTipText(node.getName());
         return this;
      }
   }

   // Listens to tree expand collapse events
   private static class NavigationTreeExpandListener implements TreeWillExpandListener, TreeExpansionListener {

      /**
       * The tree has just finished collapsing.
       */
      public void treeCollapsed(TreeExpansionEvent aEvent) {
         GuiUtil.setBusyCursor(Application.getDefaultParentFrame(), false);
      }

      /**
       * The tree has just finished expanding.
       */
      public void treeExpanded(TreeExpansionEvent aEvent) {
         GuiUtil.setBusyCursor(Application.getDefaultParentFrame(), false);
      }

      /**
       * The tree is about to be collapsed.
       */
      public void treeWillCollapse(TreeExpansionEvent aEvent) throws ExpandVetoException {
         GuiUtil.setBusyCursor(Application.getDefaultParentFrame(), true);
      }

      /**
       * The tree is about to be expanded.
       */
      public void treeWillExpand(TreeExpansionEvent aEvent) throws ExpandVetoException {
         AbstractTreeNode node = (AbstractTreeNode) aEvent.getPath().getLastPathComponent();
         GuiUtil.setBusyCursor(Application.getDefaultParentFrame(), true);
         node.loadChildren();
      }

   }

   // A tree that refreshes the cell renderer when the
   private static class RefreshedTree extends UIFTree {

      public RefreshedTree(TreeModel treeModel) {
         super(treeModel);
      }

      /*
       * (non-Javadoc)
       * @see javax.swing.JTree#getScrollableUnitIncrement(java.awt.Rectangle,
       * int, int)
       */
      @Override
      public int getScrollableUnitIncrement(Rectangle aVisibleRect, int aOrientation, int aDirection) {
         // control the speed effect of the scrolling
         return MainModule.SETTINGS.getCatalogScrollUnits();
      }

      /**
       * Updates the UI; updates the selection path and cell renderer.
       */
      @Override
      public void updateUI() {
         super.updateUI();
         setSelectionPath(getSelectionPath());
         setCellRenderer(new TreeCellRenderer());
      }

   }

   // Updates the expansion state if the tree selection has changed.
   private class SelectionChangeHandler implements PropertyChangeListener {

      /**
       * The module's selection has changed. Updates the expansion state.
       * @param evt describes the property change
       */
      public void propertyChange(PropertyChangeEvent evt) {

         if (evt.getPropertyName().equals(MainModule.PROPERTYNAME_SELECTION)) {
            LOG.debug("Selected");
            updateExpansionState();
         } else if (evt.getPropertyName().equals(MainModule.PROPERTYNAME_HIGHLIGHT)) {
            LOG.debug("Highlighted");
            updateSelectedState();
         }
      }
   }

   // Listens to changes in the module's navigation tree model and rebuilds the
   // tree.
   private class TreeModelChangeHandler implements PropertyChangeListener {

      /**
       * The module's navigation tree model has changed. Update this navigator's
       * tree model switch to the tree card and expand the top-level nodes.
       * @param evt describes the property change
       */
      public void propertyChange(PropertyChangeEvent evt) {
         TreeModel newModel = (TreeModel) evt.getNewValue();
         updateModel(newModel);
         // cardPanel.showCard(TREE_CARD);
         // tree.setSelectionRow(0);
      }
   }

   // shows popups on right click of tree nodes
   private static class TreePopupListener extends MouseAdapter {

      final JPopupMenu popup;

      public TreePopupListener(JPopupMenu popup) {
         this.popup = popup;
      }

      @Override
      public void mousePressed(MouseEvent evt) {
         maybeShowPopup(evt);
         boolean queue = false;

         JTree tree = (JTree) evt.getComponent();

         // get the node at location x, y
         TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());

         if (path == null) {
            return;
         }

         // left mouse button pressed twice, queue in playlist if track
         if (((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && (evt.getClickCount() == 2)) {
            LOG.debug("[Treenode left double clicked].");
            // if it's not a Track, don't do anything
            if (!(path.getLastPathComponent() instanceof TrackNode)) {
               return;
            }
            queue = true;
         }

         // middle mouse button pressed once queue in playlist
         if (((evt.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) && (evt.getClickCount() == 1)) {
            LOG.debug("[Treenode mouse wheel clicked].");
            queue = true;
         }

         if (queue) {
            JComponent source = (JComponent) evt.getSource();
            final ActionEvent event = new ActionEvent(source, 1, Actions.PLAYER_QUEUE_ID);
            source.putClientProperty(Resources.EDITOR_COMPONENT, source);
            ActionManager.get(Actions.PLAYER_QUEUE_ID).actionPerformed(event);
         }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
         maybeShowPopup(e);
      }

      private void maybeShowPopup(MouseEvent e) {
         if ((e.isPopupTrigger() || ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0))) {
            JTree tree = (JTree) e.getComponent();

            // get the node at location x, y
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());

            if (path == null) {
               return;
            }

            // if it's not a AbstractTreeNode, don't do anything
            if (!(path.getLastPathComponent() instanceof AbstractTreeNode)) {
               return;
            }
            tree.addSelectionPath(path); // AZ append node to selection

            // popup the menu
            if (e.isPopupTrigger()) {
               popup.show(e.getComponent(), e.getX(), e.getY());
            }
         }
      }
   }

}