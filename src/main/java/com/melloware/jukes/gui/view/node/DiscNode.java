package com.melloware.jukes.gui.view.node;

import java.util.Iterator;

import javax.swing.Icon;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.Resources;

/**
 * This class represents ALBUMS in the navigation tree.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see com.melloware.jukes.db.orm.Disc
 */
public final class DiscNode
    extends AbstractTreeNode {

    private static final Log LOG = LogFactory.getLog(DiscNode.class);

    /**
     * Constructs a <code>DiscNode</code> for the given parent and disc.
     * <p>
     * @param aParent this node's parent
     * @param aModel the associated model, an instance of Disc
     */
    public DiscNode(NavigationNode aParent, Disc aModel) {
        super(aParent, aModel);
        this.settings = ((AbstractTreeNode)aParent).settings;
    }

    /**
     * Returns this node's associated Disc instance.
     * <p>
     * @return this node's associated Disc instance.
     * @see NavigationNode#getModel()
     */
    public Disc getDisc() {
        return (Disc)getModel();
    }


    /**
     * Returns this node's icon, ignores the selection.
     * The icons is requested from a global resource repository.
     * <p>
     * @return this node's icon.
     */
    public Icon getIcon(final boolean sel) {
    	Icon icon = null;
    	if (StringUtils.contains(getDisc().getName().toLowerCase(), "from the vault")) {
    		icon = Resources.DISC_GD_ICON;
		} else if (StringUtils.contains(getDisc().getName().toLowerCase(),"dark side of the moon")) {
			icon = Resources.DISC_PF_ICON;
		} else {
			icon = Resources.DISC_TREE_ICON;
		}
        return icon;
    }

    /**
     * Returns this node's name, the identifier of the associated disc.
     * <p>
     * @return this node's name
     */
    public String getName() {
        return getDisc().getDisplayText(this.settings.getDisplayFormatDisc());
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.gui.tool.node.AbstractTreeNode#loadChildren()
     */
    public void loadChildren() {
        if (!childrenLoaded) {
            loadingChildren = true;
            LOG.debug("Loading children");
            final Iterator iter = getDisc().getTracks().iterator();
            while (iter.hasNext()) {
                final Track track = (Track)iter.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Loading track " + track.getName());
                }
                this.add(new TrackNode(this, track));

            }

            loadingChildren = false;
            childrenLoaded = true;
        }

    }

}