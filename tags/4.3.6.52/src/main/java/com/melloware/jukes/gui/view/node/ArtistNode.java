package com.melloware.jukes.gui.view.node;

import java.text.MessageFormat;
import java.util.Iterator;

import javax.swing.Icon;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.uif.util.ResourceUtils;
import com.melloware.jukes.db.HibernateDao;
import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.gui.tool.Resources;

/**
 * This class represents ARTISTS in the navigation tree.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see com.melloware.jukes.db.orm.Artist
 */
public final class ArtistNode
    extends AbstractTreeNode {

    private static final Log LOG = LogFactory.getLog(ArtistNode.class);

    /**
     * Constructs a <code>ArtistNode</code> for the given parent and artist.
     * <p>
     * @param aParent this node's parent
     * @param aModel the associated model, an instance of Artist
     */
    public ArtistNode(NavigationNode aParent, Artist aModel) {
        super(aParent, aModel);
        this.settings = ((AbstractTreeNode)aParent).settings;
    }

    /**
     * Returns this node's associated Artist instance.
     * <p>
     * @return this node's associated Artist instance.
     * @see NavigationNode#getModel()
     */
    public Artist getArtist() {
        return (Artist)getModel();
    }

    /**
     * Returns this node's icon, ignores the selection.
     * The icons is requested from a global resource repository.
     * <p>
     * @return this node's icon.
     */
    public Icon getIcon(final boolean sel) {
        Icon icon = null;
        if (StringUtils.equalsIgnoreCase(getArtist().getName(), "grateful dead")) {
            icon = Resources.DISC_GD_ICON;
        } else if (StringUtils.equalsIgnoreCase(getArtist().getName(), "pink floyd")) {
            icon = Resources.DISC_PF_ICON;
        } else if (StringUtils.equalsIgnoreCase(getArtist().getName(), "who")) {
            icon = Resources.DISC_WHO_ICON;
        } else if (StringUtils.equalsIgnoreCase(getArtist().getName(), "beatles")) {
            icon = Resources.DISC_BEATLES_ICON;
        } else if (StringUtils.equalsIgnoreCase(getArtist().getName(), "rolling stones")) {
            icon = Resources.DISC_STONES_ICON;
        } else {
            icon = Resources.ARTIST_TREE_ICON;
        }
        return icon;
    }

    /**
     * Returns this node's name, the identifier of the associated artist.
     * <p>
     * @return this node's name
     */
    public String getName() {
        return getArtist().getName();
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.gui.tool.node.AbstractTreeNode#loadChildren()
     */
    public void loadChildren() {
        if (!childrenLoaded) {
            loadingChildren = true;
            LOG.debug("Loading children");

            Iterator iterator = null;
            final String filter = this.settings.getFilter();

            // if a filter was applied the use the filter HQL
            if (StringUtils.isNotBlank(filter)) {
                final String resource = ResourceUtils.getString("hql.filter.disc");
                final String hql = MessageFormat.format(resource, new Object[] { getArtist().getId(), filter });
                LOG.debug(hql);
                iterator = HibernateDao.findByQuery(hql).iterator();
            } else {
                // else just get all discs for this artist
                iterator = getArtist().getDiscs().iterator();
            }

            // now loop through and add the children
            while (iterator.hasNext()) {
                final Disc disc = (Disc)iterator.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Loading disc " + disc.getName());
                }
                this.add(new DiscNode(this, disc));
            }
            loadingChildren = false;
            childrenLoaded = true;
        }

    }

}