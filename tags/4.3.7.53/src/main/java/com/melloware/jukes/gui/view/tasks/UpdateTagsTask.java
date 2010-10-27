package com.melloware.jukes.gui.view.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import com.jgoodies.uif.util.ResourceUtils;
import com.jgoodies.uif.util.Worker;
import com.melloware.jukes.db.orm.AbstractJukesObject;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.exception.MusicTagException;
import com.melloware.jukes.file.tag.MusicTag;
import com.melloware.jukes.file.tag.TagFactory;
import com.melloware.jukes.util.MessageUtil;

/**
 * In a thread attempts to update all tags for all tracks for a domain object
 * (Artist, Disc, Track). It will loop through all Artist's Disc's Tracks. It
 * will loop through all Disc's Tracks. Or it will update a single Track.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
@SuppressWarnings("unchecked")
public final class UpdateTagsTask extends LongTask {
   private static final Log LOG = LogFactory.getLog(UpdateTagsTask.class);
   public final AbstractJukesObject domain;

   /**
    * Constructor that needs a config object and a filename to work on.
    * <p>
    * @param aJukesObject the domain object to work with
    */
   public UpdateTagsTask(AbstractJukesObject aJukesObject) {
      super();
      this.domain = aJukesObject;

      current = 0;
      // calulate the total length of this task
      if (domain instanceof Artist) {
         final Artist artist = (Artist) domain;
         final Object[] discArray = artist.getDiscs().toArray();

         // calculate the total length first by adding up all tracks
         for (int i = 0; i < discArray.length; i++) {
            final Disc disc = (Disc) discArray[i];
            lengthOfTask += disc.getTracks().size();
         }
      } else if (domain instanceof Disc) {
         final Disc disc = (Disc) domain;
         final Object[] array = disc.getTracks().toArray();
         lengthOfTask = array.length;
      } else if (domain instanceof Track) {
         lengthOfTask = 1;
      } else {
         throw new IllegalArgumentException("Unknown type.");
      }
      LOG.debug("Length of Task = " + lengthOfTask);
   }

   /**
    * Gets the domain.
    * <p>
    * @return Returns the domain.
    */
   public AbstractJukesObject getDomain() {
      return this.domain;
   }

   /**
    * Called to start the task.
    */
   public void go() {
      final Worker worker = new Worker() {
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
    * The actual long running task. This runs in a SwingWorker thread.
    */
   class ActualTask {
      @SuppressWarnings("unused")
      ActualTask() {
    	 warning = false;//AZ
    	 boolean setComment = true;//AZ
         while (!canceled && !done) {
            try {
               if (domain instanceof Artist) {
                  final Artist artist = (Artist) domain;
                  final Object[] discArray = artist.getDiscs().toArray();

                  // now update all tracks on all discs of artist
                  DISC_LOOP: for (int i = 0; i < discArray.length; i++) {
                     final Disc disc = (Disc) discArray[i];
                     final Object[] trackArray = disc.getTracks().toArray();
                     TRACK_LOOP: for (int j = 0; j < trackArray.length; j++) {
                        if ((canceled || done)) {
                           break DISC_LOOP;
                        }
                        current++;
                        final Track track = (Track) trackArray[j];
                        statMessage = ResourceUtils.getString("label.Updating") + track.getName();
                        updateTag(track);
                     }
                  }
               } else if (domain instanceof Disc) {
                  final Disc disc = (Disc) domain;
                  String discNotes = disc.getNotes();//AZ
                  if (discNotes != null) {
                  if (discNotes.length() > 255) {
                	  discNotes.substring(0, 254);
                  }
                  }
                  final Object[] array = disc.getTracks().toArray();
                  //Look for track comments
                  for (int i = 0; i < array.length; i++) {
                	  final Track track = (Track) array[i];
                	  if (StringUtils.isNotEmpty(track.getComment())) {
                		  setComment = false;
                	  }
                  }
                  
                  for (int i = 0; i < array.length; i++) {
                     if ((canceled || done)) {
                        break;
                     }
                     current = i;
                     final Track track = (Track) array[i];
                     //if Track Comments are empty then set them to be equal to Disc Notes
                     if ((setComment) && (StringUtils.isNotEmpty(discNotes))) {
                    	 track.setComment(discNotes);
                     }
                     statMessage = ResourceUtils.getString("label.Updating") + track.getName();
                     updateTag(track);
                     if (setComment) {
                    	 track.setComment(""); 
                     }
                  }
               } else if (domain instanceof Track) {
                  final Track track = (Track) domain;
                  current = 1;
                  statMessage = ResourceUtils.getString("label.Updating") + track.getName();
                  updateTag(track);

               } else {
                  throw new IllegalArgumentException("Unknown type.");
               }
               done = true;
            } catch (MusicTagException ex) {
               stop();
               LOG.warn("Exception caught updating tags: " + ex.getMessage());
               statMessage = "Exception caught updating tags: " + ex.getMessage();
               MessageUtil.showError(null,ResourceUtils.getString("label.Errorwritingfile"));//AZ
            } catch (RuntimeException ex) {
               stop();
               LOG.warn("Exception caught updating tags: "+ ex.getMessage());
               statMessage = "Exception caught updating tags: " + ex.getMessage();
               MessageUtil.showError(null, ResourceUtils.getString("label.Errorwritingfile"));//AZ
            }
         }
      }

      /**
       * Updates a music tag filling out all it's info from the track, disc, and
       * artist based on the track provided.
       * <p>
       * @param aTrack the track to update the tag for
       * @throws MusicTagException if any error occurs updating
       */
      private void updateTag(final Track aTrack) throws MusicTagException {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Update Tag: " + aTrack.getTrackUrl());
         }

         if (aTrack.isNotValid()) {
        	 warning = true;//AZ
        	 LOG.warn("Error Updating Tag: " + aTrack.getTrackUrl());
        	 return;
         }
         try {
         final MusicTag musicTag = TagFactory.getTag(aTrack.getTrackUrl());
         musicTag.setArtist(aTrack.getDisc().getArtist().getName());
         musicTag.setDisc(aTrack.getDisc().getName());
         musicTag.setGenre(aTrack.getDisc().getGenre());
         musicTag.setYear(aTrack.getDisc().getYear());
         musicTag.setTitle(aTrack.getName());
         musicTag.setComment(aTrack.getComment());
         musicTag.setTrack(aTrack.getTrackNumber());
         musicTag.save();
         }
         catch (Exception ex) {
        	 warning = true;//AZ
        	 LOG.warn("Exception caught updating tags: "+ ex.getMessage());
         }
      }
   }

}