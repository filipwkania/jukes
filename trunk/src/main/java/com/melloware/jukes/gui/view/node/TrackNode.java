package com.melloware.jukes.gui.view.node;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.file.filter.MusicFilter;
import com.melloware.jukes.gui.tool.Resources;

/**
 * This class represents TRACKS in the navigation tree.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see com.melloware.jukes.db.orm.Track
 */
public final class TrackNode
    extends AbstractTreeNode {

    private static final Log LOG = LogFactory.getLog(TrackNode.class);

    /**
     * Constructs a <code>TrackNode</code> for the given parent and track.
     * <p>
     * @param aParent this node's parent
     * @param aModel the associated model, an instance of Track
     */
    public TrackNode(NavigationNode aParent, Track aModel) {
        super(aParent, aModel);
        this.settings = ((AbstractTreeNode)aParent).settings;
    }

    /**
     * Returns this node's icon, ignores the selection.
     * The icons is requested from a global resource repository.
     * <p>
     * @return this node's icon.
     */
    public Icon getIcon(final boolean sel) {
        final String extension = getTrack().getTrackUrl().toLowerCase();
        if ((extension.endsWith(MusicFilter.OGG)) || (extension.endsWith(MusicFilter.SPEEX))) {
            return Resources.OGG_VORBIS_ICON;
        } else if (extension.endsWith(MusicFilter.FLAC)) {
            return Resources.FLAC_ICON;
        } else {
            return Resources.TRACK_TREE_ICON;
        }
    }

    /**
     * Returns this node's name, the identifier of the associated track.
     * <p>
     * @return this node's name
     */
    public String getName() {
        return getTrack().getDisplayText(this.settings.getDisplayFormatTrack());
    }

    /**
     * Returns this node's associated Track instance.
     * <p>
     * @return this node's associated Track instance.
     * @see NavigationNode#getModel()
     */
    public Track getTrack() {
        return (Track)getModel();
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.gui.tool.node.AbstractTreeNode#loadChildren()
     */
    public void loadChildren() {
        if (!childrenLoaded) {
            loadingChildren = true;
            LOG.debug("Loading children");
            loadingChildren = false;
            childrenLoaded = true;
        }
    }

}