package com.melloware.jukes.tray;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.builder.PopupMenuBuilder;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.view.MainMenuBuilder;

/**
 * Tray icon using JDK 6 SystemTray class.  
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class JukesTrayIcon
    implements ITrayIcon {

    private static final Log LOG = LogFactory.getLog(JukesTrayIcon.class);
    private final JFrame parentWindow;
    private final SystemTray tray;
    private TrayIcon trayIcon;

    /**
    * Default Constructor.
    * <p>
    * @param aParentWindow the JFrame to control this tray icon
    * @throws AWTException if any error occurs initializing
    */
    public JukesTrayIcon(JFrame aParentWindow) throws AWTException {
        super();
        this.parentWindow = aParentWindow;
        tray = SystemTray.getSystemTray();
        initialize();
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.tray.ITrayIcon#isAvailable()
     */
    public boolean isAvailable() {
        return true;
    }

    /**
     * Builds and returns the Tray Icon Menu.
     */
    private JPopupMenu buildTrayMenu() {
        PopupMenuBuilder builder = new PopupMenuBuilder(MainMenuBuilder.CATALOG);
        builder.add(ActionManager.get(Actions.HELP_ABOUT_DIALOG_ID));
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.PREFERENCES_ID));
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.PLAYER_PLAY_ID));
        builder.add(ActionManager.get(Actions.PLAYER_PAUSE_ID));
        builder.add(ActionManager.get(Actions.PLAYER_STOP_ID));
        builder.add(ActionManager.get(Actions.PLAYER_PREVIOUS_ID));
        builder.add(ActionManager.get(Actions.PLAYER_NEXT_ID));
        builder.addSeparator();
        builder.add(ActionManager.get(Actions.APP_HIDE_ID));
        builder.add(ActionManager.get(Actions.APP_SHOW_ID));
        builder.add(ActionManager.get(Actions.EXIT_ID));
        return builder.getPopupMenu();
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.tray.ITrayIcon#cleanUp()
     */
    public void cleanUp() {
        LOG.debug("Cleaning up Unix Tray Icon");

    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.tray.ITrayIcon#hideWindow()
     */
    public void hideWindow() {
        if (parentWindow.isVisible()) {
            parentWindow.setVisible(false);
            ActionManager.get(Actions.APP_HIDE_ID).setEnabled(false);
            ActionManager.get(Actions.APP_SHOW_ID).setEnabled(true);
        }
    }

    /**
    * When the icon is single clicked in the tray. For now it will call pause
    * and play of the player.
    */
    public void leftClicked() {
       ActionManager.get(Actions.PLAYER_PAUSE_ID).actionPerformed(null);
    }

    /**
     * When the icon is middle button clicked.  For now advance to next song
     * when this button is pressed.
     */
    public void middleClicked() {
        ActionManager.get(Actions.PLAYER_NEXT_ID).actionPerformed(null);
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.tray.ITrayIcon#showWindow()
     */
    public void showWindow() {
        if (!parentWindow.isVisible()) {
            parentWindow.setVisible(true);
            ActionManager.get(Actions.APP_HIDE_ID).setEnabled(true);
            ActionManager.get(Actions.APP_SHOW_ID).setEnabled(false);
        }

        // restore the window if it was minimized
        parentWindow.setState(Frame.NORMAL);
    }

    /**
     * Initialize the Tray Icon and resources.
     * @throws AWTException if any tray icon exception occurs
     */
    private void initialize() throws AWTException {
        LOG.info("Initialize Unix Tray Icon.");

        // Init the Tray Icon library given the name for the hidden window
        trayIcon = new java.awt.TrayIcon(ImageFactory.ICO_TRAYICON.getImage(), parentWindow.getTitle(), null);
        trayIcon.setImageAutoSize(true);

        // add all listeners
        trayIcon.addMouseListener(new JukesTrayMouseAdapter(this, buildTrayMenu()));
        
        tray.add(trayIcon);
    }

	/* (non-Javadoc)
	 * @see com.melloware.jukes.tray.ITrayIcon#changeImage(java.awt.Image)
	 */
	public void changeImage(Image aImage) {
		trayIcon.setImage(aImage);
	}

	/* (non-Javadoc)
	 * @see com.melloware.jukes.tray.ITrayIcon#setToolTip(java.lang.String)
	 */
	public void setToolTip(String aTip) {
		trayIcon.setToolTip(aTip);
	}

}