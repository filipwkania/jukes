package com.melloware.jukes.file;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.uif.application.Application;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.util.MessageUtil;

/**
 * The disclist of the application. The list can also be saved as file.
 * <p>
 * Copyright (c) 2006 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ Development 2009
 */
@SuppressWarnings("unchecked")
public final class Disclist extends Model implements PropertyChangeListener {

   private static final Log LOG = LogFactory.getLog(Disclist.class);
   public static final String PROPERTYNAME_DISC_LIST = "discList";
   private final List discList;
   private Disc currentDisc;

   /**
    * Default constructor constructs a list of 100 items.
    */
   public Disclist() {
      super();
      LOG.debug("Disclist created.");
      this.discList = new ArrayList();
   }

   /**
    * Gets the discList.
    * <p>
    * @return Returns the discList.
    */
   public List getDiscList() {
      return this.discList;
   }

   /**
    * Gets the currentDisc.
    * <p>
    * @return Returns the currentDisc.
    */
   public Disc getCurrentDisc() {
      synchronized (this) {
         return this.currentDisc;
      }
   }

   /**
    * Sets the currentDisc.
    * <p>
    * @return Returns the currentDisc.
    */
   public Disc setCurrentDisc(Disc aDisc) {
      synchronized (this) {
         this.currentDisc = aDisc;
         return this.currentDisc;
      }
   }

   /**
    * Delete the currentDisc.
    * <p>
    * @return Returns null for the currentDisc.
    */
   public Disc removeCurrentDisc() {
      synchronized (this) {
         this.currentDisc = null;
         return this.currentDisc;
      }
   }

   /**
    * Checks and answer if there's a next element.
    * @return true if there's a next element
    */
   public boolean hasNext() {
      synchronized (this) {
         if (discList.size() != 0) {
            if (discList.indexOf(this.currentDisc) < discList.size() - 1) {
               return (true);
            } else {
               return (false);
            }
         } else
            return (false);
      }
   }

   /**
    * Checks and answer if there's a next element.
    * @return true if there's a next element
    */
   public boolean hasPrevious() {
      synchronized (this) {
         if (discList.size() != 0) {
            if (discList.indexOf(this.currentDisc) > 0) {
               return (true);
            } else {
               return (false);
            }
         } else
            return (false);
      }
   }

   /**
    * Returns the next element
    * @return the next element
    */
   public Object getNext() {
      Object next = null;
      next = ((hasNext()) ? discList.get(discList.indexOf(this.currentDisc) + 1) : null);
      if (next != null) {
         synchronized (this) {
            currentDisc = (Disc) next;
         }
         updateState();
      }
      return next;
   }

   /**
    * Returns the previous element
    * @return the previous element
    */
   public Object getPrevious() {
      Object prev = null;
      prev = ((hasPrevious()) ? discList.get(discList.indexOf(this.currentDisc) - 1) : null);
      if (prev != null) {
         synchronized (this) {
            currentDisc = (Disc) prev;
         }
         updateState();
      }
      return prev;
   }

   /**
    * Returns an <code>Iterator</code> for the available next elements.
    * @return an iterator that iterates over the available next elements
    */
   public Iterator getNextIterator() {
      return discList.iterator();
   }

   /**
    * Move an item down on the list.
    * <p>
    * @param index the index to move
    */
   public void moveDown(final int index) {
      synchronized (this) {
         final Object temp = getDiscList().remove(index);
         getDiscList().add(index + 1, temp);
      }
   }

   /**
    * Move an item up on the list.
    * <p>
    * @param index the index to move
    */
   public void moveUp(final int index) {
      synchronized (this) {
         final Object temp = getDiscList().remove(index);
         getDiscList().add(index - 1, temp);
      }
   }

   /**
    * The Catalog has changed.
    * @param evt describes the property change
    */
   public void propertyChange(final PropertyChangeEvent evt) {
      // final String propertyName = evt.getPropertyName();
   }

   /**
    * Removes an item from the list.
    * <p>
    * @param index the index to remove from the list
    */
   public void remove(final int index) {
      synchronized (this) {
         if (index < getDiscList().size()) {
            getDiscList().remove(index);
         }
      }
   }

   /**
    * Saves a disclist to a file.
    * <p>
    * @param aFile the file to save.
    * @throws Exception if any error occurs
    */
   public void save(final File aFile) throws Exception {

      saveDiscList(aFile);
   }

   /**
    * Returns the size of the correct list.
    * @return the size of the correct list
    */
   public int size() {
      return discList.size();
   }

   @Override
   public String toString() {
      return "(Current disclist: " + discList.size() + ")";
   }

   /**
    * Updates the state
    */
   public void updateState() {
      firePropertyChange(PROPERTYNAME_DISC_LIST, null, discList);
   }

   /**
    * Saves a disclist to file.
    * <p>
    * @param aFile the file to save
    * @throws IOException if any error occurs writing file
    */
   private void saveDiscList(final File aFile) throws IOException {
      final ArrayList results = new ArrayList();

      final StringBuffer sb = new StringBuffer();

      // loop through the discs adding them
      for (final Iterator iter = getDiscList().iterator(); iter.hasNext();) {
         final Disc disc = (Disc) iter.next();
         sb.delete(0, sb.length());
         sb.append(disc.getArtist().getName());
         sb.append(" - ");
         sb.append(disc.getName());
         sb.append(" - ");
         sb.append(disc.getYear());
         results.add(sb.toString());
         results.add(disc.getLocation());
      }

      FileUtils.writeLines(aFile, null, results);
   }

   /**
    * Adds an element to the disclist.
    * @param o the object to add
    */
   public void add(final Object o) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Adding to disclist bottom: " + o);
      }
      final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
      synchronized (this) {
         if (o instanceof Artist) {
            final Artist artist = (Artist) o;
            final ArrayList discs = new ArrayList();
            discs.addAll(artist.getDiscs());
            Collections.sort(discs);
            for (final Iterator iter = discs.iterator(); iter.hasNext();) {
               final Disc disc = (Disc) iter.next();
               discList.add(disc);
            }
         } else if (o instanceof Disc) {
            final Disc disc = (Disc) o;
            discList.add(disc);
         } else if (o instanceof Track) {
            MessageUtil.showError(mainFrame, Resources.getString("messages.SelectArtistOrDisc"));
            return;
         }
         if (currentDisc == null) {
            currentDisc = (Disc) discList.get(0);
         }
         updateState();
      }
   }

}