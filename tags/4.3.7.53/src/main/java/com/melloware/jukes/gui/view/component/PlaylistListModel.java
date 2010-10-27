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
import com.melloware.jukes.file.Playlist;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.PlaylistPanel;

/**
 * Mode used for displaying playlists  in a JList.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class PlaylistListModel
    extends AbstractListModel
    implements PropertyChangeListener {

    private static final Log LOG = LogFactory.getLog(PlaylistListModel.class);
    private final Playlist playlist;
    private final PlaylistListModel model;
    private final PlaylistPanel panel;
    
    /**
     * KeyAdapter to deselect all on CTrl+D and delete items with DEL key.
     */
    private transient final KeyListener keyTypedListener = new KeyAdapter() {
        public void keyPressed(final KeyEvent event) {
            if ((event.getKeyCode() == KeyEvent.VK_DELETE) && (model.getSize() > 0)) {
                LOG.debug("Delete pressed");
                panel.putClientProperty(Resources.EDITOR_COMPONENT, panel);
                final ActionEvent action = new ActionEvent(panel, 1, "");
                ActionManager.get(Actions.PLAYLIST_REMOVE_TRACK_ID).actionPerformed(action);
            } else if ((event.isControlDown()) && (event.getKeyCode() == KeyEvent.VK_D)) {
                LOG.debug("Deselect All");
                panel.clearSelection();
            } else {
                super.keyPressed(event);
            }
        }
    };

    /**
    * Constructs a <code>PlaylistPanel</code> for the given module.
    *
    * @param aPlaylist  provides the playlist class needed for display
    */
    public PlaylistListModel(Playlist aPlaylist, PlaylistPanel aPanel) {
        super();
        LOG.debug("PlaylistListModel created.");
        this.playlist = aPlaylist;
        this.playlist.addPropertyChangeListener(this);
        this.panel = aPanel;
        this.model = this;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int aIndex) {
        Object returnValue;

        if (aIndex < this.playlist.getList().size()) {
            returnValue = this.playlist.getList().get(aIndex);
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

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return this.playlist.size();
    }

    /**
     * Whenever the master playlist changes update this view.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        LOG.debug("Playlist changed");
        fireContentsChanged(this, 0, getSize());
    }

}