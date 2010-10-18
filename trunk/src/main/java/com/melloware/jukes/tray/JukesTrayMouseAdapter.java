package com.melloware.jukes.tray;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Listens for all mouse events on the Tray Icon.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class JukesTrayMouseAdapter extends MouseAdapter {
   private static final Log LOG = LogFactory.getLog(JukesTrayMouseAdapter.class);
   private final JPopupMenu jpopup;
   private final JukesTrayIcon trayIcon;

   /**
    * Default constructor.
    * <p>
    * @param aTrayIcon the parent of this adapter to allow
    */
   public JukesTrayMouseAdapter(JukesTrayIcon aTrayIcon, JPopupMenu aJpopupMenu) {
      super();
      this.trayIcon = aTrayIcon;
      this.jpopup = aJpopupMenu;
   }

   /*
    * (non-Javadoc)
    * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
    */
   public void mousePressed(MouseEvent evt) {

      if (((evt.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) && (evt.getClickCount() == 1)) {
         LOG.debug("Tray Swing popup menu displayed.");
         jpopup.setLocation(evt.getX(), evt.getY());
         jpopup.setInvoker(jpopup);
         jpopup.setVisible(true);
         return;
      }

      // left mouse button pressed once
      if (((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && (evt.getClickCount() == 1)) {
         LOG.debug("[Tray icon left single click].");
         trayIcon.leftClicked();
      }

      // left mouse button pressed twice
      if (((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && (evt.getClickCount() == 2)) {
         LOG.debug("[Tray icon left double clicked].");
         trayIcon.showWindow();
      }

      // middle mouse button pressed once
      if (((evt.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) && (evt.getClickCount() == 1)) {
         LOG.debug("[Tray icon mouse wheel clicked].");
         trayIcon.middleClicked();
      }
   }

}