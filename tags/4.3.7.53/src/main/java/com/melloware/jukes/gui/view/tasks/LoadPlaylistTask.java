package com.melloware.jukes.gui.view.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicPlayer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.application.Application;
import com.jgoodies.uif.util.Worker;
import com.melloware.jspiff.jaxp.XspfPlaylist;
import com.melloware.jspiff.jaxp.XspfTrack;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Catalog;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.file.Playlist;
import com.melloware.jukes.file.filter.M3uFilter;
import com.melloware.jukes.file.filter.XspfFilter;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Player;
import com.melloware.jukes.gui.view.MainFrame;

/**
 * In a thread attempts to load the playlist one song at a time and notify
 * user of the progress.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
@SuppressWarnings("unchecked")
public final class LoadPlaylistTask
    extends LongTask {

    private static final Log LOG = LogFactory.getLog(LoadPlaylistTask.class);
    private static final String BREAK = " - ";
    private final File file;
    private final List m3uLines;
    private final Playlist playlist;
    private final XspfPlaylist xspfPlaylist;
    private final Map artists;

    /**
     * Constructor that needs a file to load.
     * <p>
     * @param aFile the file to load
     * @throws Exception if any error occurs
     */
    public LoadPlaylistTask(File aFile, Playlist aPlaylist)
                     throws Exception {
        super();
        LOG.debug("Loading playlist");
        this.file = aFile;
        this.playlist = aPlaylist;
        
        //put all the artists in a map for faster access
        List artists = Catalog.findAllArtists();
        HashMap map = new HashMap(artists.size());
        Iterator iter = artists.iterator();
        while (iter.hasNext()) {
			Artist element = (Artist) iter.next();
			map.put(element.getName(), element);
		}
        this.artists = map;

        
        // determine the playlist type and load appropriately
        if (FilenameUtils.isExtension(file.getName(), M3uFilter.M3U)) {
            m3uLines = FileUtils.readLines(file, null);
            lengthOfTask = m3uLines.size();
            xspfPlaylist = null;
        } else if (FilenameUtils.isExtension(file.getName(), XspfFilter.XSPF)) {
            xspfPlaylist = new XspfPlaylist(file);
            lengthOfTask = xspfPlaylist.getPlaylistTrackList().sizeTrack();
            m3uLines = null;
        } else {
            throw new IllegalArgumentException("Playlist is not a valid type");
        }
        LOG.debug("Length of Task = " + lengthOfTask);
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
     * Try to locate this track in the database and load into playlist.
     * <p>
     * @param artistName the artist name
     * @param discName the disc name
     * @param trackName the track name
     * @return the Track if found else null
     */
    @SuppressWarnings("unused")
    private Track findTrack(String artistName, String discName, String trackName) {
        Track result = null;
        
        Artist artist = (Artist)this.artists.get(artistName);
        if (artist == null) {
			return result;
		}
        Iterator iter = artist.getDiscs().iterator();
        DISC_LOOP:
        while (iter.hasNext()) {
			Disc disc = (Disc) iter.next();
			if (StringUtils.equalsIgnoreCase(disc.getName(), discName)) {
				Iterator iterator = disc.getTracks().iterator();
				TRACK_LOOP:
				while (iterator.hasNext()) {
					Track track = (Track) iterator.next();
					if (StringUtils.equalsIgnoreCase(track.getName(), trackName)) {
						result = track;
						break DISC_LOOP;
					}
				}
			}
		}

        return result;
    }

    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    class ActualTask {
        ActualTask() {
            while (!canceled && !done) {
                try {
                    if (m3uLines != null) { //NOPMD
                        int count = 1;
                        final Iterator iter = m3uLines.iterator();
                        while ((iter.hasNext()) && (!canceled && !done)) {
                            current = count;
                            count++;
                            String element = (String)iter.next();

                            // skip any lines that don't start with #
                            if (!element.startsWith("#EXTINF")) {
                                continue;
                            }

                            // strip off the '#EXTINF:duration,'
                            element = StringUtils.substringAfter(element, ",");
                            String artist = StringUtils.substringBefore(element, BREAK).trim();
                            String title = StringUtils.substringAfterLast(element, BREAK).trim();
                            element = StringUtils.substringAfter(element, BREAK).trim();
                            element = StringUtils.substringBeforeLast(element, BREAK).trim();
                            String disc = element;

                            Track track = findTrack(artist, disc, title);
                            if (track != null) {
                                statMessage = "Loading: " + track.getName();
                                playlist.add(track);
                            }

                        }
                    } else if (xspfPlaylist != null) { //NOPMD
                        final XspfTrack[] array = xspfPlaylist.getPlaylistTrackList().getTrack();
                        for (int i = 0; i < array.length; i++) {
                            if ((canceled || done)) {
                                break;
                            }
                            current = i;
                            final XspfTrack element = array[i];
                            Track track = findTrack(element.getCreator(), element.getAlbum(), element.getTitle());
                            if (track != null) {
                                statMessage = "Loading: " + track.getName();
                                playlist.add(track);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Unknown type.");
                    }

                    // if the player was stopped then play the next song
                    final Player player = ((MainFrame)Application.getDefaultParentFrame()).getPlayer();
                    if ((player.getStatus() == BasicPlayer.STOPPED) || (player.getStatus() == BasicPlayer.UNKNOWN)) {
                        ActionManager.get(Actions.PLAYER_NEXT_ID).actionPerformed(null);
                    }

                    done = true;
                } catch (Exception ex) {
                    stop();
                    LOG.info("Exception caught loading tracks:", ex);
                    statMessage = "Exception caught loading tracks: " + ex.getMessage();
                }
            }
        }

    }

}