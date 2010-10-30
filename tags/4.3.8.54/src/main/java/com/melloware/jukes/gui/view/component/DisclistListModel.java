package com.melloware.jukes.gui.view.component;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.action.ActionManager;
import com.melloware.jukes.file.Disclist;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.DisclistPanel;

/**
 * Mode used for displaying disclists in a JList.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ Development 2009
 */
public final class DisclistListModel extends AbstractListModel implements PropertyChangeListener {

   private static final Log LOG = LogFactory.getLog(DisclistListModel.class);
   private final Disclist disclist;
   private final DisclistListModel model;
   private final DisclistPanel panel;

   /**
    * KeyAdapter to deselect all on CTrl+D and delete items with DEL key.
    */
   private transient final KeyListener keyTypedListener = new KeyAdapter() {
      @Override
      public void keyPressed(final KeyEvent event) {
         if ((event.getKeyCode() == KeyEvent.VK_DELETE) && (model.getSize() > 0)) {
            LOG.debug("Delete pressed");
            panel.putClientProperty(Resources.EDITOR_COMPONENT, panel);
            final ActionEvent action = new ActionEvent(panel, 1, "");
            ActionManager.get(Actions.DISCLIST_REMOVE_DISC_ID).actionPerformed(action);
         } else if ((event.isControlDown()) && (event.getKeyCode() == KeyEvent.VK_D)) {
            LOG.debug("Deselect All");
            panel.clearSelection();
         } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            disclist.getNext();
         } else if (event.getKeyCode() == KeyEvent.VK_UP) {
            disclist.getPrevious();
         } else {
            super.keyPressed(event);
         }
      }
   };

   /**
    * Constructs a <code>DisclistPanel</code> for the given module.
    * @param aDisclist provides the disclist class needed for display
    */
   public DisclistListModel(Disclist aDisclist, DisclistPanel aPanel) {
      super();
      LOG.debug("DisclistListModel created.");
      this.disclist = aDisclist;
      this.disclist.addPropertyChangeListener(this);
      this.panel = aPanel;
      this.model = this;
   }

   /*
    * (non-Javadoc)
    * @see javax.swing.ListModel#getElementAt(int)
    */
   public Object getElementAt(int aIndex) {
      Object returnValue;

      if (aIndex < this.disclist.getDiscList().size()) {
         returnValue = this.disclist.getDiscList().get(aIndex);
      } else {
         returnValue = null;
      }
      return returnValue;
   }

   /**
    * Gets the keyTypedListener.
    * <p>
    * @return Returns the keyTypedListener.
    */
   public KeyListener getKeyTypedListener() {
      return this.keyTypedListener;
   }

   /*
    * (non-Javadoc)
    * @see javax.swing.ListModel#getSize()
    */
   public int getSize() {
      return this.disclist.size();
   }

   /**
    * Whenever the master disclist changes update this view.
    */
   public void propertyChange(PropertyChangeEvent evt) {
      LOG.debug("Disclist changed");
      fireContentsChanged(this, 0, getSize());
   }

}