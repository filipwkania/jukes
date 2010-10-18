package com.melloware.jukes.gui.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.application.Application;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.util.MessageUtil;

import javax.sound.sampled.SourceDataLine;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 * Override BasicPlayer to get to some protected methods.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */
public final class JukesPlayer extends BasicPlayer {
   /**
    * Logger for this class
    */
   private static final Log LOG = LogFactory.getLog(JukesPlayer.class);

	/* (non-Javadoc)
    * @see javazoom.jlgui.basicplayer.BasicPlayer#run()
    */
   @Override
   public void run() {
      try {
    	  // AZ: Set Initial Volume
    	  if (super.hasGainControl()) {
    		  super.setGain(MainModule.SETTINGS.getPlayerVolume());
    	  } 
         super.run();
      } catch (RuntimeException ex) {
    	  final String errorMessage = Resources.getString("messages.ErrorPlayFile");
          LOG.error(errorMessage, ex);
          final MainFrame mainFrame = (MainFrame) Application.getDefaultParentFrame();
          MessageUtil.showError(mainFrame,errorMessage); 
          notifyEvent(BasicPlayerEvent.STOPPED, getEncodedStreamPosition(), -1, null);
      } catch (BasicPlayerException ex) {
    	  final String errorMessage = Resources.getString("messages.ErrorPlayFile");
          LOG.error(errorMessage, ex);
          MessageUtil.showError(null,errorMessage+": "+ ex.getMessage()); 
          notifyEvent(BasicPlayerEvent.STOPPED, getEncodedStreamPosition(), -1, null);
	}
   }

   public SourceDataLine getSourceDataLine() {
		return this.m_line;
	}
}
