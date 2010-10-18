package com.melloware.jukes.gui.tool;

import java.io.File;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.action.ActionManager;


/**
 * This class implements a simple player based on BasicPlayer.
 * BasicPlayer is a threaded class providing most features  of a music player.
 * BasicPlayer works with underlying JavaSound  SPIs to support multiple audio
 * formats. Basically JavaSound supports  WAV, AU, AIFF audio formats.
 * Add MP3 SPI and Vorbis  SPI in your CLASSPATH to play MP3 and
 * Ogg Vorbis file.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */
public final class Player
    implements BasicPlayerListener {

    private static final Log LOG = LogFactory.getLog(Player.class);
    private static final String ERROR_PAUSE_PLAY = Resources.getString("messages.ErrorPausingPlayingFile");
    private float volume = MainModule.SETTINGS.getPlayerVolume();
    private JukesPlayer player;
    private long elapsedTime = 0;

    /**
     * Default constructor.
     *
     */
    public Player() {
    	super();
    }

    /**
     * Gets the player.
     * <p>
     * @return Returns the player.
     */
    public JukesPlayer getPlayer() {
        // instantiate one if we need to
        if (this.player == null) {
            this.player = new JukesPlayer();

            // set buffer size to control skips
            BasicPlayer.EXTERNAL_BUFFER_SIZE = MainModule.SETTINGS.getPlayerBufferSize();

            // Register BasicPlayerTest to BasicPlayerListener events.
            // It means that this object will be notified on BasicPlayer
            // events such as : opened(...), progress(...), stateUpdated(...)
            this.player.addBasicPlayerListener(this);
        }
        return this.player;
    }

    /**
     * @return the player status
     */
    public int getStatus() {
        if (player == null) {
        	return -1;
        } else {
        	return getPlayer().getStatus();  
        }
    }

    /**
     * Gets the volume.
     * <p>
     * @return Returns the volume.
     */
    public float getVolume() {
        return this.volume;
    }

    /**
     * A handle to the BasicPlayer, plugins may control the player through
     * the controller (play, stop, ...)
     * <p>
     * @param controller : a handle to the player
     */
    public void setController(BasicController controller) {
        LOG.debug("setController : " + controller);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jajuk.base.IPlayerImpl#setVolume(float)
     */
    public void setVolume(float aVolume)
                   throws Exception {
        this.volume = aVolume;
        MainModule.SETTINGS.setPlayerVolume(aVolume);
        if (getPlayer().hasGainControl()) {
        getPlayer().setGain(aVolume);
        } 
    }

    /**
     * Fades the volume in.
     * <p>
     * @throws BasicPlayerException if any player error occurs
     * @throws InterruptedException if any thread error occurs
     */
    public void fadeIn()
                throws BasicPlayerException, InterruptedException {
        if (MainModule.SETTINGS.isFadeInOnPlay()) {
            for (float i = 0.0f; i < this.volume; i = i + 0.01f) {
                getPlayer().setGain(i);
                Thread.sleep(25);
            }
        }
    }

    /**
     * Fades the volume out.
     * <p>
     * @throws BasicPlayerException if any player error occurs
     * @throws InterruptedException if any thread error occurs
     */
    public void fadeOut()
                 throws BasicPlayerException, InterruptedException {

        for (double i = this.volume; i > 0.0f; i = i - 0.01f) {
            getPlayer().setGain(i);
            Thread.sleep(25);
        }
    }

    /**
     * Open callback, stream is ready to play.
     *
     * properties map includes audio format dependant features such as
     * bitrate, duration, frequency, channels, number of frames, vbr flag, ...
     * <p>
     * @param stream could be File, URL or InputStream
     * @param properties audio stream properties.
     */
    public void opened(Object stream, Map properties) {
        // Pay attention to properties. It's useful to get duration,
        // bitrate, channels, even tag such as ID3v2.
        if (LOG.isDebugEnabled()) {
            LOG.debug("opened : " + properties.toString());
        }
    }

    /**
     * Pause if playing, play if paused.
     */
    public void pause() {
        try {
            if (getPlayer().getStatus() == BasicPlayer.PLAYING) {
                if (MainModule.SETTINGS.isFadeOutOnPause()) {
                    fadeOut();
                }
                getPlayer().pause();
            } else if (getPlayer().getStatus() == BasicPlayer.PAUSED) {
                getPlayer().resume();
                fadeIn();
            }
        } catch (BasicPlayerException ex) {
            LOG.error(ERROR_PAUSE_PLAY, ex);
        } catch (InterruptedException ex) {
            LOG.error(ERROR_PAUSE_PLAY, ex);
        } catch (Exception ex) {
            LOG.error(ERROR_PAUSE_PLAY, ex);
        }
    }

    /**
     * Plays the file located at filename.
     */
    public void play() {
        try {
            if (getPlayer().getStatus() == BasicPlayer.PAUSED) {
                getPlayer().resume();
                fadeIn();
            } else if (getPlayer().getStatus() != BasicPlayer.STOPPED) { //NOPMD
                // make sure to stop any current player
                if (MainModule.SETTINGS.isFadeOutOnStop()) {
                    fadeOut();
                }
                getPlayer().stop();
                getPlayer().play();
            } else {
                getPlayer().play();
            }
        } catch (BasicPlayerException ex) {
            LOG.error(ERROR_PAUSE_PLAY, ex);
        } catch (InterruptedException ex) {
            LOG.error(ERROR_PAUSE_PLAY, ex);
        } catch (Exception ex) {
            LOG.error(ERROR_PAUSE_PLAY, ex);
        }
    }

    /**
     * Plays the file located at filename.
     * <p>
     * @param filename the filename to play
     */
    public void play(String filename) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Play " + filename);
        }
        try {
            // instantiate one if we need to
            if (player == null) {
                player = new JukesPlayer();

                // Register BasicPlayerTest to BasicPlayerListener events.
                // It means that this object will be notified on BasicPlayer
                // events such as : opened(...), progress(...), stateUpdated(...)
                player.addBasicPlayerListener(this);
            }

            // make sure to stop any current player
            if (player.getStatus() != BasicPlayer.STOPPED) {
                if ((player.getStatus() == BasicPlayer.PLAYING) && (MainModule.SETTINGS.isFadeOutOnChange())) {
                   fadeOut();
                }
                player.stop();
            }
            // Open file, or URL or Stream (shoutcast, icecast) to play.
            player.open(new File(filename));
            

            // control.open(new URL("http://yourshoutcastserver.com:8000"));
            // Start playback in a thread. control.play();
            // If you want to pause/resume/pause the played file then
            // write a Swing player and just call control.pause(),
            // control.resume() or control.stop().
            // Use control.seek(bytesToSkip) to seek file
            // (i.e. fast forward and rewind). seek feature will
            // work only if underlying JavaSound SPI implements
            // skip(...). True for MP3SPI and SUN SPI's
            // (WAVE, AU, AIFF). // Set Volume (0 to 1.0). control.setGain(0.85);
            // Set Pan (-1.0 to 1.0).
            if (player.hasPanControl()) {
            	player.setPan(0.0);
			}
            if (player.hasGainControl()) {
            	setVolume(volume);
            }
            player.play();
        } catch (BasicPlayerException ex) {
            LOG.error(Resources.getString("messages.ErrorPlayingFile"), ex);
        } catch (InterruptedException ex) {
            LOG.error(Resources.getString("messages.ErrorPlayingFile"), ex);
        } catch (Exception ex) {
            LOG.error(Resources.getString("messages.ErrorPlayingFile"), ex);
        }
    }

    /**
     * Progress callback while playing.
     *
     * This method is called severals time per seconds while playing.
     * properties map includes audio format features such as
     * instant bitrate, microseconds position, current frame number, ...
     * <p>
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        // Pay attention to properties. It depends on underlying JavaSound SPI MP3SPI provides mp3.equalizer.
        // LOG.debug("progress : " + properties.toString());
    	
    	//set the elapsed time property
    	this.elapsedTime = microseconds;
    }

    /**
     * Stop the currently playing song.
     */
    public void resume() {
        try {
            LOG.debug("Resume");
            getPlayer().resume();
        } catch (BasicPlayerException ex) {
            LOG.error(Resources.getString("messages.ErrorResumingFile"), ex);
        } catch (Exception ex) {
            LOG.error(Resources.getString("messages.ErrorResumingFile"), ex);
        }
    }

    /**
     * Seeks to a position in the song.
     * <p>
     * @param aPosition the position to seek to
     */
    public void seek(float aPosition) {
        LOG.debug("Seeking");
        try {
            getPlayer().seek((long)aPosition);
        } catch (BasicPlayerException ex) {
            LOG.error(Resources.getString("messages.ErrorSeekingFile"), ex);
        } catch (Exception ex) {
            LOG.error(Resources.getString("messages.ErrorSeekingFile"), ex);
        }
    }

    /**
     * Notification callback for basicplayer events such as opened, eom ...
     * <p>
     * @param event the BasicPlayerEvent fired.
     */
    public void stateUpdated(BasicPlayerEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("stateUpdated : " + event.toString());
        }
        try {
            switch (event.getCode()) {
                case BasicPlayerEvent.OPENED:
                case BasicPlayerEvent.PLAYING: {
                    ActionManager.get(Actions.PLAYER_PLAY_ID).setEnabled(false);
                    ActionManager.get(Actions.PLAYER_PAUSE_ID).setEnabled(true);
                    ActionManager.get(Actions.PLAYER_STOP_ID).setEnabled(true);
                    break;
                }
                case BasicPlayerEvent.RESUMED: {
                    ActionManager.get(Actions.PLAYER_PLAY_ID).setEnabled(false);
                    ActionManager.get(Actions.PLAYER_PAUSE_ID).setEnabled(true);
                    ActionManager.get(Actions.PLAYER_STOP_ID).setEnabled(true);
                    break;
                }
                case BasicPlayerEvent.PAUSED: {
                    ActionManager.get(Actions.PLAYER_PLAY_ID).setEnabled(true);
                    ActionManager.get(Actions.PLAYER_PAUSE_ID).setEnabled(false);
                    ActionManager.get(Actions.PLAYER_STOP_ID).setEnabled(true);
                    break;
                }
                case BasicPlayerEvent.STOPPED: {
                    ActionManager.get(Actions.PLAYER_PLAY_ID).setEnabled(true);
                    ActionManager.get(Actions.PLAYER_PAUSE_ID).setEnabled(false);
                    ActionManager.get(Actions.PLAYER_STOP_ID).setEnabled(false);
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception", ex);
        }
    }

    /**
     * Stop the currently playing song.
     */
    public void stop() {
        try {
            LOG.debug("Stop");
            switch (getPlayer().getStatus()) {
                case BasicPlayer.PLAYING:
                case BasicPlayer.PAUSED: {
                    if (MainModule.SETTINGS.isFadeOutOnStop()) {
                        fadeOut();
                    }
                    getPlayer().stop();
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (BasicPlayerException ex) {
            LOG.error(Resources.getString("messages.ErrorStoppingFile"), ex);
        } catch (InterruptedException ex) {
            LOG.error(Resources.getString("messages.ErrorStoppingFile"), ex);
        } catch (Exception ex) {
            LOG.error(Resources.getString("messages.ErrorStoppingFile"), ex);
        }
    }

	/**
	 * Gets the elapsedTime.
	 * <p>
	 * @return Returns the elapsedTime.
	 */
	public long getElapsedTime() {
		return (this.elapsedTime/1000);
	}
 
}