package com.melloware.jukes.file;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.util.ResourceUtils;
import com.melloware.jspiff.jaxp.XspfPlaylist;
import com.melloware.jspiff.jaxp.XspfPlaylistTrackList;
import com.melloware.jspiff.jaxp.XspfTrack;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Catalog;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.file.filter.M3uFilter;
import com.melloware.jukes.file.filter.XspfFilter;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.util.TimeSpan;

/**
 * The playlist of the application. Stores history so you can go back and forth
 * between previous and next tracks. The history and current list can also be
 * saved as M3U playlists.
 * <p>
 * Copyright (c) 2006 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ 2009
 */
@SuppressWarnings("unchecked")
public final class Playlist extends Model implements PropertyChangeListener {

   private static final Log LOG = LogFactory.getLog(Playlist.class);
   public static final String PROPERTYNAME_CURRENT_LIST = "currentList";
   public static final String PROPERTYNAME_HISTORY_LIST = "historyList";
   private boolean current = true;
   private boolean shuffleCatalog = false;
   private boolean shufflePlaylist = false;
   private Catalog catalog = null;
   private final List currentList;
   private final List historyList;
   private Track currentTrack;

   /**
    * Default constructor constructs a list of 100 items.
    */
   public Playlist() {
      super();
      LOG.debug("Playlist created.");
      this.historyList = new ArrayList();
      this.currentList = new ArrayList();
   }

   /**
    * Returns an <code>Iterator</code> for the available backward elements.
    * @return an iterator that iterates over the available backward elements
    */
   public Iterator getBackIterator() {
      return historyList.iterator();
   }

   /**
    * Gets the current running time.
    * <p>
    * @return Returns the current running time.
    */
   public String getCurrentDuration() {
      long duration = 0;
      final Iterator iter = getNextIterator();
      while (iter.hasNext()) {
         final Track track = (Track) iter.next();
         duration += track.getDuration() * 1000;
      }
      return new TimeSpan(duration).getMusicDuration();
   }

   /**
    * Gets the currentList.
    * <p>
    * @return Returns the currentList.
    */
   public List getCurrentList() {
      return this.currentList;
   }

   /**
    * Gets the currentTrack.
    * <p>
    * @return Returns the currentTrack.
    */
   public Track getCurrentTrack() {
      synchronized (this) {
         return this.currentTrack;
      }
   }
   /**AZ
    * Delete the currentTrack.
    * <p>
    * @return Returns null for the currentTrack.
    */
   public Track removeCurrentTrack() {
      synchronized (this) {
    	 this.currentTrack = null;
         return this.currentTrack;
      }
   }
   /**
    * Gets the history running time.
    * <p>
    * @return Returns the history running time.
    */
   public String getHistoryDuration() {
      long duration = 0;
      final Iterator iter = getBackIterator();
      while (iter.hasNext()) {
         final Track track = (Track) iter.next();
         duration += track.getDuration() * 1000;
      }
      return new TimeSpan(duration).getMusicDuration();
   }

   /**
    * Gets the historyList.
    * <p>
    * @return Returns the historyList.
    */
   public List getHistoryList() {
      return this.historyList;
   }

   /**
    * Gets the correct list.
    * <p>
    * @return Returns the correct list
    */
   public List getList() {
      if (isCurrent()) {
         return this.currentList;
      } else {
         return this.historyList;
      }
   }

   /**
    * Returns the next element no matter if shuffling.
    * @return the next element
    */
   public Object getNextImmediate() {
      Object next = null;
      next = ((hasNext()) ? currentList.get(0) : null);
      if (next != null) {
         synchronized (this) {
            currentList.remove(next);
            historyList.add(next);
            currentTrack = (Track) next;
         }

      }
      updateState();
      return next;

   }

   /**
    * Returns the next element.
    * @return the next element
    */
   public Object getNext() {
      Object next = null;
      if (isShufflePlaylist()) {
         final Random random = new Random();
         next = ((hasNext()) ? currentList.get(random.nextInt(currentList.size())) : null);
      } else if (isShuffleCatalog()) {
         final Random random = new Random();

         // grab a random artist
         final List artists = catalog.getArtists();
         if (artists == null) {
            return null;
         }
         final Artist artist = (Artist) artists.get(random.nextInt(artists.size()));

         // grab a random disc from that artist
         Object[] discs = null;
         final String filter = MainModule.SETTINGS.getFilter();

         // if a filter was applied the use the filter HQL
         if (StringUtils.isNotBlank(filter)) {
            final String resource = ResourceUtils.getString("hql.filter.disc");
            final String hql = MessageFormat.format(resource, new Object[] { artist.getId(), filter });
            discs = HibernateDao.findByQuery(hql).toArray();
         } else {
            // else just get all discs for this artist
            discs = artist.getDiscs().toArray();
         }

         final Disc disc = (Disc) discs[(random.nextInt(discs.length))];

         // grab a random track from that disc
         final Object[] tracks = disc.getTracks().toArray();
         next = (Track) tracks[(random.nextInt(tracks.length))];
      } else {
         next = ((hasNext()) ? currentList.get(0) : null);
      }

      if (next != null) {
         synchronized (this) {
            currentList.remove(next);
            historyList.add(next);
            currentTrack = (Track) next;
         }

      }
      updateState();
      return next;
   }

   /**
    * Returns an <code>Iterator</code> for the available next elements.
    * @return an iterator that iterates over the available next elements
    */
   public Iterator getNextIterator() {
      return currentList.iterator();
   }

   /**
    * Returns the previous element.
    * @return the previous element
    */
   public Object getPrevious() {
      final Object prev = ((hasPrevious()) ? historyList.get(historyList.size() - 1) : null);
      if (prev != null) {
         this.addNext(prev);
         synchronized (this) {
            historyList.remove(prev);
            currentTrack = (Track) prev;
         }
      }
      updateState();
      return prev;
   }

   /**
    * Sets the current.
    * <p>
    * @param aCurrent The current to set.
    */
   public void setCurrent(final boolean aCurrent) {
      this.current = aCurrent;
      updateState();
   }

   /**
    * Sets the shuffleCatalog.
    * <p>
    * @param aShuffleCatalog The shuffleCatalog to set.
    */
   public void setShuffleCatalog(final boolean aShuffleCatalog) {
      this.shuffleCatalog = aShuffleCatalog;
   }

   /**
    * Sets the shufflePlaylist.
    * <p>
    * @param aShufflePlaylist The shufflePlaylist to set.
    */
   public void setShufflePlaylist(final boolean aShufflePlaylist) {
      this.shufflePlaylist = aShufflePlaylist;
   }

   /**
    * Gets the current.
    * <p>
    * @return Returns the current.
    */
   public boolean isCurrent() {
      return this.current;
   }

   /**
    * Gets the shuffleCatalog.
    * <p>
    * @return Returns the shuffleCatalog.
    */
   public boolean isShuffleCatalog() {
      return this.shuffleCatalog;
   }

   /**
    * Gets the shufflePlaylist.
    * <p>
    * @return Returns the shufflePlaylist.
    */
   public boolean isShufflePlaylist() {
      return this.shufflePlaylist;
   }

   /**
    * Adds an element to the history if it is not the previous element. Returns
    * whether the history changed.
    * @param o the object to add
    */
   public void add(final Object o) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Adding to playlist bottom: " + o);
      }

      synchronized (this) {
         final ArrayList tracks = new ArrayList();
         if (o instanceof Artist) {
            final Artist artist = (Artist) o;
            final ArrayList discs = new ArrayList();
            discs.addAll(artist.getDiscs());
            Collections.sort(discs);
            for (final Iterator iter = discs.iterator(); iter.hasNext();) {
               final Disc disc = (Disc) iter.next();
               tracks.clear();
               tracks.addAll(disc.getTracks());
               Collections.sort(tracks);
               for (final Iterator iterator = tracks.iterator(); iterator.hasNext();) {
                  final Track track = (Track) iterator.next();
                  currentList.add(track);
               }
            }
         } else if (o instanceof Disc) {
            final Disc disc = (Disc) o;
            tracks.clear();
            tracks.addAll(disc.getTracks());
            Collections.sort(tracks);
            for (final Iterator iterator = tracks.iterator(); iterator.hasNext();) {
               final Track track = (Track) iterator.next();
               currentList.add(track);
            }

         } else if (o instanceof Track) {
            currentList.add(o);
         }
         if (currentTrack == null) {
            currentTrack = (Track) currentList.get(0);
            ActionManager.get(Actions.PLAYER_PLAY_ID).setEnabled(true);
         }
         updateState();
      }
   }

   /**
    * Adds an element to the history if it is not the previous element. Returns
    * whether the history changed.
    * @param o the object to add
    */
   public void addNext(final Object o) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Adding to playlist top: " + o);
      }
      synchronized (this) {
         final ArrayList tracks = new ArrayList();
         if (o instanceof Artist) {
            final Artist artist = (Artist) o;
            final ArrayList discs = new ArrayList();
            discs.addAll(artist.getDiscs());
            Collections.reverse(discs);
            for (final Iterator iter = discs.iterator(); iter.hasNext();) {
               final Disc disc = (Disc) iter.next();
               tracks.clear();
               tracks.addAll(disc.getTracks());
               Collections.reverse(tracks);
               for (final Iterator iterator = tracks.iterator(); iterator.hasNext();) {
                  final Track track = (Track) iterator.next();
                  currentList.add(0, track);
               }
            }
         } else if (o instanceof Disc) {
            final Disc disc = (Disc) o;
            tracks.clear();
            tracks.addAll(disc.getTracks());
            Collections.reverse(tracks);
            for (final Iterator iterator = tracks.iterator(); iterator.hasNext();) {
               final Track track = (Track) iterator.next();
               currentList.add(0, track);
            }

         } else if (o instanceof Track) {
            currentList.add(0, o);
         }

         if (currentTrack == null) {
            currentTrack = (Track) currentList.get(0);
         }
         updateState();
      }
   }

   /**
    * Checks and answer if the object o is contained in the 'previous' list.
    * @return true if this item is in the previous list
    */
   public boolean contains(final Object o) {
      return (containsNext(o) || containsPrevious(o));
   }

   /**
    * Checks and answer if the object o is contained in the 'next' list.
    * @return true if this item is in the next list
    */
   public boolean containsNext(final Object o) {
      return (hasNext()) ? currentList.contains(o) : false;
   }

   /**
    * Checks and answer if the object o is contained in the 'previous' list.
    * @return true if this item is in the previous list
    */
   public boolean containsPrevious(final Object o) {
      return (hasPrevious()) ? historyList.contains(o) : false;
   }

   /**
    * Checks and answer if there's a next element.
    * @return true if there's a next element
    */
   public boolean hasNext() {
      synchronized (this) {
         return ((!currentList.isEmpty()) || this.shuffleCatalog);
      }
   }

   /**
    * Checks and answers if there's a previous element.
    * @return true if there's a previous element
    */
   public boolean hasPrevious() {
      synchronized (this) {
         return (!historyList.isEmpty());
      }
   }

   /**
    * Move an item down on the list.
    * <p>
    * @param index the index to move
    */
   public void moveDown(final int index) {
      synchronized (this) {
         final Object temp = getList().remove(index);
         getList().add(index + 1, temp);
      }
   }

   /**
    * Move an item to the other list.
    * <p>
    * @param index the index to move
    */
   public void moveOver(final int index) {
      synchronized (this) {
         final Object temp = getList().get(index);
         if (isCurrent()) {
            historyList.add(temp);
         } else {
            currentList.add(temp);
         }
      }
   }

   /**
    * Move an item up on the list.
    * <p>
    * @param index the index to move
    */
   public void moveUp(final int index) {
      synchronized (this) {
         final Object temp = getList().remove(index);
         getList().add(index - 1, temp);
      }
   }

   /**
    * Plays this track immediately.
    * @param track the track to play immediately
    */
   public void playImmediate(final Track track) {
      synchronized (this) {
         getList().remove(track);
         historyList.add(track);
      }
      updateState();
   }

   /**
    * The Catalog has changed.
    * @param evt describes the property change
    */
   public void propertyChange(final PropertyChangeEvent evt) {
      final String propertyName = evt.getPropertyName();
      if (MainModule.PROPERTYNAME_CATALOG.equals(propertyName)) {
         catalog = ((Catalog) evt.getNewValue());
      }
   }

   /**
    * Removes an item from the list.
    * <p>
    * @param index the index to remove from the list
    */
   public void remove(final int index) {
      synchronized (this) {
         if (index < getList().size()) {
            getList().remove(index);
         }
      }
   }

   /**
    * Saves a playlist to a file.
    * <p>
    * @param aFile the file to save.
    * @throws Exception if any error occurs
    */
   public void save(final File aFile) throws Exception {

      if (FilenameUtils.isExtension(aFile.getName(), M3uFilter.M3U)) {
         saveM3U(aFile);
      } else if (FilenameUtils.isExtension(aFile.getName(), XspfFilter.XSPF)) {
         saveXSPF(aFile);
      }

   }

   /**
    * Returns the size of the correct list.
    * @return the size of the correct list
    */
   public int size() {
      if (isCurrent()) {
         return sizeNext();
      } else {
         return sizePrevious();
      }
   }

   /**
    * Returns the size of the next list.
    * @return the size of the next list
    */
   public int sizeNext() {
      return currentList.size();
   }

   /**
    * Returns the size of the previous list.
    * @return the size of the previous list
    */
   public int sizePrevious() {
      return historyList.size();
   }

   public String toString() {
      return "(Current: " + currentList.size() + "; History: " + historyList.size() + ")";
   }

   /**
    * Updates the state of the previous next buttons.
    */
   public void updateState() {
      firePropertyChange(PROPERTYNAME_CURRENT_LIST, null, currentList);
      firePropertyChange(PROPERTYNAME_HISTORY_LIST, null, historyList);
      ActionManager.get(Actions.PLAYER_NEXT_ID).setEnabled(hasNext());
      ActionManager.get(Actions.PLAYER_PREVIOUS_ID).setEnabled(hasPrevious());
   }

   /**
    * Saves an M3U type winamp playlist.
    * <p>
    * @param aFile the file to save
    * @throws IOException if any error occrus writing file
    */
   private void saveM3U(final File aFile) throws IOException {
      final ArrayList results = new ArrayList();

      results.add("#EXTM3U");

      final StringBuffer sb = new StringBuffer();

      // add the currently playing track to the playlist
      if (this.getCurrentTrack() != null) {
         final Track track = this.getCurrentTrack();
         sb.delete(0, sb.length());
         sb.append("#EXTINF:");
         sb.append(track.getDuration());
         sb.append(',');
         sb.append(track.getDisc().getArtist().getName());
         sb.append(" - ");
         sb.append(track.getDisc().getName());
         sb.append(" - ");
         sb.append(track.getName());
         results.add(sb.toString());
         results.add(track.getTrackUrl());
      }

      // now loop through the tracks adding them
      for (final Iterator iter = getList().iterator(); iter.hasNext();) {
         final Track track = (Track) iter.next();
         sb.delete(0, sb.length());
         sb.append("#EXTINF:");
         sb.append(track.getDuration());
         sb.append(',');
         sb.append(track.getDisc().getArtist().getName());
         sb.append(" - ");
         sb.append(track.getDisc().getName());
         sb.append(" - ");
         sb.append(track.getName());
         results.add(sb.toString());
         results.add(track.getTrackUrl());
      }

      FileUtils.writeLines(aFile, null, results);
   }

   /**
    * Saves an XSPF type 'Spiff' playlist.
    * <p>
    * @param aFile the file to save
    * @throws Exception if any error occrus writing file
    */
   private void saveXSPF(final File aFile) throws Exception {

      final XspfPlaylist playlist = new XspfPlaylist();
      playlist.setTitle(System.getProperty("application.name") + " Playlist");
      playlist.setCreator(System.getProperty("user.name"));
      playlist.setDate(new Timestamp(System.currentTimeMillis()));
      playlist.setInfo("http://melloware.com/");
      playlist.setVersion("1");
      final XspfPlaylistTrackList tracks = new XspfPlaylistTrackList();
      XspfTrack out = null;

      // add the currently playing track to the playlist
      if (this.getCurrentTrack() != null) {
         final Track track = this.getCurrentTrack();
         out = new XspfTrack();
         out.setLocation(new File(track.getTrackUrl()).toURI().toASCIIString());
         out.setCreator(track.getDisc().getArtist().getName());
         out.setAlbum(track.getDisc().getName());
         out.setTitle(track.getName());
         out.setDurationByString(String.valueOf(track.getDuration() * 1000)); // milliseconds
         out.setTrackNumByString(track.getTrackNumber());
         tracks.addTrack(out);
      }

      // now loop through the queue and add tracks
      for (final Iterator iter = getList().iterator(); iter.hasNext();) {
         final Track track = (Track) iter.next();
         
         out = new XspfTrack();
         out.setLocation(new File(track.getTrackUrl()).toURI().toASCIIString());
         out.setCreator(track.getDisc().getArtist().getName());
         out.setAlbum(track.getDisc().getName());
         out.setTitle(track.getName());
         out.setTrackNumByString(track.getTrackNumber());
         tracks.addTrack(out);
      }
      playlist.setPlaylistTrackList(tracks);
      final OutputFormat format = OutputFormat.createPrettyPrint();
      format.setEncoding("UTF-8");
      final XMLWriter writer = new XMLWriter(new FileOutputStream(aFile), format);
      final String xml = new String(playlist.makeTextDocument().getBytes("UTF-8"), "UTF-8");
      final Document doc = DocumentHelper.parseText(xml);
      writer.write(doc);
      writer.close();
   }

}