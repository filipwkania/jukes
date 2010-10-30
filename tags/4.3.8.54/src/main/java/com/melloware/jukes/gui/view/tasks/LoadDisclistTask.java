package com.melloware.jukes.gui.view.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uif.util.Worker;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Catalog;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.file.Disclist;

/**
 * In a thread attempts to load the disclist one disc at a time and notify user
 * of the progress.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0 AZ Development 2009
 */
@SuppressWarnings("unchecked")
public final class LoadDisclistTask extends LongTask {

   private static final Log LOG = LogFactory.getLog(LoadDisclistTask.class);
   private static final String BREAK = " - ";
   private final File file;
   private final List discLines;
   private final Disclist disclist;
   private final Map artists;

   /**
    * Constructor that needs a file to load.
    * <p>
    * @param aFile the file to load
    * @throws Exception if any error occurs
    */
   public LoadDisclistTask(File aFile, Disclist aDisclist) throws Exception {
      super();
      LOG.debug("Loading disclist");
      this.file = aFile;
      this.disclist = aDisclist;

      // put all the artists in a map for faster access
      List artists = Catalog.findAllArtists();
      HashMap map = new HashMap(artists.size());
      Iterator iter = artists.iterator();
      while (iter.hasNext()) {
         Artist element = (Artist) iter.next();
         map.put(element.getName(), element);
      }
      this.artists = map;

      // load disclist
      discLines = FileUtils.readLines(file, null);
      lengthOfTask = discLines.size();
      LOG.debug("Length of Task = " + lengthOfTask);
   }

   /**
    * Called to start the task.
    */
   @Override
   public void go() {
      final Worker worker = new Worker() {
         @Override
         public Object construct() {
            current = 0;
            done = false;
            canceled = false;
            statMessage = null;
            return new ActualTask();
         }
      };
      worker.start();
   }

   /**
    * Try to locate this disc in the database and load into disclist.
    * <p>
    * @param artistName the artist name
    * @param discName the disc name
    * @return the Disc if found else null
    */
   private Disc findDisc(String artistName, String discName) {
      Disc result = null;

      Artist artist = (Artist) this.artists.get(artistName);
      if (artist == null) {
         return result;
      }
      Iterator iter = artist.getDiscs().iterator();
      DISC_LOOP: while (iter.hasNext()) {
         Disc disc = (Disc) iter.next();
         if (StringUtils.equalsIgnoreCase(disc.getName(), discName)) {
            result = disc;
            break DISC_LOOP;
         }
      }

      return result;
   }

   /**
    * The actual long running task. This runs in a SwingWorker thread.
    */
   class ActualTask {
      ActualTask() {
         while (!canceled && !done) {
            try {
               if (discLines != null) { // NOPMD
                  int count = 1;
                  final Iterator iter = discLines.iterator();
                  while ((iter.hasNext()) && (!canceled && !done)) {
                     current = count;
                     count++;
                     String element = (String) iter.next();

                     String artist = StringUtils.substringBefore(element, BREAK).trim();
                     element = StringUtils.substringAfter(element, BREAK).trim();
                     element = StringUtils.substringBeforeLast(element, BREAK).trim();
                     String discString = element;
                     Disc disc = findDisc(artist, discString);
                     if (disc != null) {
                        statMessage = ResourceUtils.getString("messages.Loading") + disc.getName();
                        disclist.add(disc);
                     }

                  }
               }

               done = true;
            } catch (Exception ex) {
               stop();
               LOG.info(ResourceUtils.getString("messages.ExceptionLoadingDiscs"), ex);
               statMessage = ResourceUtils.getString("messages.ExceptionLoadingDiscs") + ex.getMessage();
            }
         }
      }

   }

}