package com.melloware.jukes.gui.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uifextras.convenience.SetupManager;
import com.melloware.jukes.db.orm.AbstractJukesObject;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Catalog;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.gui.view.node.AbstractTreeNode;
import com.melloware.jukes.gui.view.node.ArtistNode;
import com.melloware.jukes.gui.view.node.DiscNode;
import com.melloware.jukes.gui.view.node.NavigationNode;
import com.melloware.jukes.gui.view.node.RootNode;
import com.melloware.jukes.gui.view.node.TrackNode;
import com.melloware.jukes.util.MessageUtil;

/**
 * Provides bound bean properties for the catalog, navigation tree, navigation
 * tree selection, selection type. Refers to the presentation settings and a
 * submodule for the dynamic help.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ - some modifications 2009
 * @see Settings
 * @see DynamicHelpModule
 */
public final class MainModule extends Model {

   private static final Log LOG = LogFactory.getLog(MainModule.class);

   public static final String PROPERTYNAME_CATALOG = "catalog";
   public static final String PROPERTYNAME_HIGHLIGHT = "highlight";
   public static final String PROPERTYNAME_SELECTION = "selection";
   public static final String PROPERTYNAME_SELECTION_TYPE = "selectionType";
   public static final String PROPERTYNAME_NAVIGATION_TREE_MODEL = "navigationTreeModel";

   /**
    * Refers to the submodule for the UI-related settings.
    * @see #getSettings()
    */
   public static final Settings SETTINGS = new Settings();

   /**
    * Holds the current catalog.
    * @see #getCatalog()
    * @see #setCatalog(Catalog)
    */
   private Catalog catalog;

   /**
    * Holds the class of the current selection.
    * @see #getSelection()
    * @see #getSelectionType()
    */
   private Class selectionType;

   /**
    * Holds the node that refers to the current selection. Used to report a
    * potential change to its hosting tree model when the selection changes.
    * This is because the node's label may have changed as part of the editor
    * change.
    * <p>
    * This is a poor workaround that is based on the weak assumumption that this
    * class's tree selection listener is the first to know about the node
    * change.
    * <p>
    */
   private NavigationNode selectedNode;

   /**
    * Holds the current selection, an instance of Model.
    * @see #getSelection()
    * @see #getSelectionType()
    */
   private Object selection;

   /**
    * Holds the model for the navigation tree. It changes every time this
    * module's catalog changes.
    * @see #getNavigationTreeModel()
    * @see #getNavigationTreeSelectionModel()
    */
   private TreeModel navigationTreeModel;

   /**
    * Refers to the single selection model for the navigation tree. The
    * selection is reset if the navigation tree model changes.
    * @see #getNavigationTreeSelectionModel()
    * @see #getNavigationTreeModel()
    */
   private final TreeSelectionModel navigationTreeSelectionModel = new DefaultTreeSelectionModel();

   /**
    * Constructs a <code>MainModule</code> that has no catalog set, no selection
    * and no tree model.
    */
   public MainModule() {
      LOG.debug("MainModule created.");
      restoreFromFile(); // AZ
      restoreState();
      MusicTag.setSettings(SETTINGS); // AZ
      ImageFactory.setSettings(SETTINGS);// AZ
      Catalog.setSettings(SETTINGS);// AZ
   }

   /**
    * Returns the current catalog.
    * @return the current catalog.
    */
   public Catalog getCatalog() {
      return catalog;
   }

   /**
    * Returns the tree model for the navigation tree.
    * @return the tree model for the navigation tree.
    */
   public TreeModel getNavigationTreeModel() {
      return navigationTreeModel;
   }

   /**
    * Returns the fixed selection model for the navigation tree.
    * @return the fixed selection model for the navigation tree.
    */
   public TreeSelectionModel getNavigationTreeSelectionModel() {
      return navigationTreeSelectionModel;
   }

   /**
    * Gets the selectedNode.
    * <p>
    * @return Returns the selectedNode.
    */
   public NavigationNode getSelectedNode() {
      return this.selectedNode;
   }

   /**
    * Returns the current selection, a domain object. This selection will be
    * updated if the selection in the navigation tree changes. During the
    * transition from one tree selection to another this value may hold the old
    * or new selection.
    * @return the domain object selected in the navigation tree.
    * @see #getSelectionType()
    */
   public Object getSelection() {
      return selection;
   }

   /**
    * Returns the class of the selected domain object.
    * @return the class of the selected domain object.
    * @see #getSelection()
    */
   public Class getSelectionType() {
      return selectionType;
   }

   /**
    * Sets a new catalog.
    * @param newCatalog the catalog to set
    * @throws NullPointerException if the new catalog is null
    */
   public void setCatalog(final Catalog newCatalog) {
      if (newCatalog == null) {
         throw new IllegalArgumentException("The catalog must not be null.");
      }

      final Catalog oldCatalog = getCatalog();
      catalog = newCatalog;
      firePropertyChange(PROPERTYNAME_CATALOG, oldCatalog, newCatalog);
      setNavigationTreeModel(createNavigationTreeModel(newCatalog));

   }

   /**
    * Checks and answers whether the catalog's file path is valid.
    * @return true if the catalog's file path is valid.
    */
   public boolean isCatalogFilePathValid() {
      return false;
   }

   /**
    * Checks and answers if a catalog is loaded.
    * @return true if a catalog is loaded.
    */
   public boolean hasCatalog() {
      return getCatalog() != null;
   }

   /**
    * Refreshes the currently selected tree node.
    * <p>
    * @param domainObject the domain object may be useful
    * @param operation NODE_INSERTED, NODE_DELETED, NODE_CHANGED
    */
   public void refreshSelection(final Object domainObject, final String operation) {
      if (selectedNode == null) {
         return;
      }
      final DefaultTreeModel treeModel = ((DefaultTreeModel) getNavigationTreeModel());
      if (Resources.NODE_CHANGED.equals(operation)) {
         treeModel.nodeChanged(selectedNode);
         LOG.debug("Tree Node changed.");
      } else if (Resources.NODE_DELETED.equals(operation)) {
         final TreeNode parent = selectedNode.getParent();
         if (parent != null) {
            LOG.debug("Tree Node Deleted");
            treeModel.removeNodeFromParent((DefaultMutableTreeNode) selectedNode);
            setSelectedNode((AbstractTreeNode) parent);
         }
      }
   }

   /**
    * Refreshes the tree node from the database.
    * <p>
    * @throws NullPointerException if the new catalog is null
    */
   public void refreshTree() {
      setCatalog(new Catalog(SETTINGS.getFilter()));
      firePropertyChange(PROPERTYNAME_SELECTION, selection, null);
   }

   /**
    * AZ Refreshes the tree node from the database and go to specified selection
    * <p>
    * @throws NullPointerException if the new catalog is null
    */
   public void refreshTree(Object selectedObject) {
      setCatalog(new Catalog(SETTINGS.getFilter()));
      firePropertyChange(PROPERTYNAME_SELECTION, selection, selectedObject);
      selectNodeInTree(selectedObject);
   }

   /**
    * Restores the application state from the user preferences.
    */
   public void restoreState() {
      SETTINGS.restoreFrom(Application.getUserPreferences());
   }

   /**
    * Finds the selection in the tree and expands the node and sets the editor.
    * @param newSelection the selection to set
    * @see #getSelection()
    */
   public void selectNodeInTree(final Object newSelection) {
      final AbstractJukesObject selection = (AbstractJukesObject) newSelection;
      AbstractTreeNode node = null;
      String artistName = null;
      String discName = null;
      String trackName = null;
      if (selection instanceof Artist) {
         artistName = selection.getName();
      } else if (selection instanceof Disc) {
         final Disc disc = (Disc) selection;
         artistName = disc.getArtist().getName();
         discName = selection.getName();
      } else if (selection instanceof Track) {
         final Track track = (Track) selection;
         artistName = track.getDisc().getArtist().getName();
         discName = track.getDisc().getName();
         trackName = selection.getName();
      }

      final DefaultTreeModel treeModel = ((DefaultTreeModel) navigationTreeModel);
      final AbstractTreeNode root = (AbstractTreeNode) treeModel.getRoot();

      // loop through the artists
      ArtistNode artistNode = null;
      if (StringUtils.isNotBlank(artistName)) {
         final Enumeration artistEnum = root.children();
         while (artistEnum.hasMoreElements()) {
            artistNode = (ArtistNode) artistEnum.nextElement();
            if (StringUtils.equalsIgnoreCase(artistNode.toString(), artistName)) {
               node = artistNode;
               break; // found it
            }
         }
      }

      // now get the disc
      DiscNode discNode = null;
      if ((StringUtils.isNotBlank(discName)) && (artistNode != null)) {
         final Enumeration discEnum = artistNode.children();
         while (discEnum.hasMoreElements()) {
            discNode = (DiscNode) discEnum.nextElement();
            if (StringUtils.equalsIgnoreCase(discNode.toString(), discName)) {
               node = discNode;
               break; // found it
            }
         }
      }

      // now get the track
      TrackNode trackNode = null;
      if ((StringUtils.isNotBlank(trackName)) && (discNode != null)) {
         final Enumeration trackEnum = discNode.children();
         while (trackEnum.hasMoreElements()) {
            trackNode = (TrackNode) trackEnum.nextElement();
            if (StringUtils.equalsIgnoreCase(trackNode.toString(), trackName)) {
               node = trackNode;
               break; // found it
            }
         }
      }

      if (node != null) {
         // tree node found
         setSelectedNode(node);

         // fire this property change to tell the tree to select the node
         firePropertyChange(PROPERTYNAME_HIGHLIGHT, null, "Highlight");
      }
   }

   /**
    * Stores the application state to the user preferences.
    */
   public void storeState() {
      final Preferences prefs = Application.getUserPreferences();
      SETTINGS.storeIn(prefs);
      SetupManager.incrementUsageCounter();
   }

   /**
    * Sets a new tree model for the navigation tree.
    * <p>
    * @param newModel the new tree model to set
    */
   private void setNavigationTreeModel(final TreeModel newModel) {
      final TreeModel oldModel = getNavigationTreeModel();
      navigationTreeModel = newModel;
      firePropertyChange(PROPERTYNAME_NAVIGATION_TREE_MODEL, oldModel, newModel);
   }

   /**
    * Sets the selection and reports the previously selected node as changed.
    * <p>
    * <strong>Note:</strong> This code requires a special order. It assumes that
    * domain changes happen when the selection changes.
    * <p>
    * @param newSelectedNode the new node selection
    */
   private void setSelectedNode(final NavigationNode newSelectedNode) {
      final NavigationNode oldSelectedNode = selectedNode;
      selectedNode = newSelectedNode;
      setSelection(newSelectedNode.getModel());
      ((DefaultTreeModel) getNavigationTreeModel()).nodeChanged(oldSelectedNode);
   }

   /**
    * Sets a new domain object as selection. This method is invoked by the
    * NavigationTreeSelectionChangeHandler that observes changes in the
    * selection of the navigation tree.
    * @param newSelection the selection to set
    * @see #getSelection()
    */
   private void setSelection(final Object newSelection) {
      final Object oldSelection = getSelection();
      selection = newSelection;
      setSelectionType(newSelection.getClass());
      firePropertyChange(PROPERTYNAME_SELECTION, oldSelection, newSelection);
   }

   /**
    * Sets a new selection type. This method is invoked in
    * <code>#setSelection</code> with the class of the new selection.
    * @param newSelectionType the selection type to set
    * @see #getSelectionType()
    */
   private void setSelectionType(final Class newSelectionType) {
      final Class oldSelectionType = getSelectionType();
      selectionType = newSelectionType;
      if (equals(oldSelectionType, newSelectionType)) {
         return;
      }

      firePropertyChange(PROPERTYNAME_SELECTION_TYPE, oldSelectionType, newSelectionType);
   }

   /**
    * Creates and returns a tree model for the given catalog. Constructs a
    * <code>RootNode</code> with the catalog and returns a
    * <code>DefaultTreeModel</code> with this root.
    * @return a TreeModel for the given catalog
    */
   private TreeModel createNavigationTreeModel(final Catalog catalog) {
      final RootNode rootNode = new RootNode(catalog);
      rootNode.setSettings(SETTINGS);
      final DefaultTreeModel model = new DefaultTreeModel(rootNode);
      navigationTreeSelectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      navigationTreeSelectionModel.addTreeSelectionListener(new NavigationTreeSelectionChangeHandler());
      return model;
   }

   // Listens to changes in the navigation tree node selection
   // and updates the selection.
   private class NavigationTreeSelectionChangeHandler implements TreeSelectionListener {

      /**
       * The selected node in the navigation tree has changed. Updates this
       * module's selected domain object and in turn the selection type.
       * <p>
       * Does nothing if the new tree selection is <code>null</code>.
       * @param evt the event that describes the change
       */
      public void valueChanged(final TreeSelectionEvent evt) {
         final TreePath path = evt.getPath();
         final NavigationNode aSelectedNode = (NavigationNode) path.getLastPathComponent();
         if (aSelectedNode != null) {
            setSelectedNode(aSelectedNode);
         }
      }

   }

   // AZ
   // read preferences from file jukes.xml
   private void restoreFromFile() {
      LOG.info("Read Preferences from jukes.xml");
      final String errorMessage = ResourceUtils.getString("messages.ErrorReadPrefsFile");
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      final File file = new File(FilenameUtils.normalizeNoEndSeparator(SystemUtils.USER_DIR
               + SystemUtils.FILE_SEPARATOR + "jukes.xml"));
      // now try and import the preferences from a file
      if (file.exists()) {
         try {
            final FileInputStream stream = new FileInputStream(file);
            Preferences.importPreferences(stream);
         } catch (IOException ex) {
            MessageUtil.showError(mainFrame, errorMessage); // AZ
            LOG.error(errorMessage + ex.getMessage(), ex);
         } catch (Exception ex) {
            MessageUtil.showError(mainFrame, errorMessage); // AZ
            LOG.error(errorMessage, ex);
         }
      }
   }
}