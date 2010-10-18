package com.melloware.jukes.gui.view.node;

import java.util.Iterator;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Catalog;

/**
 * Describes the root node in the Jukes navigation tree.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class RootNode
    extends AbstractTreeNode {

    private static final Log LOG = LogFactory.getLog(RootNode.class);

    private final Catalog catalog;

    /**
     * Creates a root node for the specified catalog.
     *
     * @param aCatalog    the associated catalog
     */
    public RootNode(Catalog aCatalog) {
        super(null, aCatalog);
        LOG.debug("Created RootNode");
        this.catalog = aCatalog;
    }

    /**
     * Returns this node's associated Catalog instance.
     *
     * @return this node's associated Description instance.
     * @see NavigationNode#getModel()
     */
    public Catalog getCatalog() {
        return (Catalog)getModel();
    }

    /**
     * Returns this node's icon. Since the root node will be hidden
     * by the tree, we can return <code>null</code>.
     *
     * @return null
     */
    public Icon getIcon(boolean sel) {
        return null;
    }

    /**
     * Returns this node's name. Since the root node will be hidden
     * by the tree, we can return <code>null</code>.
     *
     * @return null
     */
    public String getName() {
        return null;
    }


    /**
     * Loads all the artists in the collection sorted by name ascending.
     */
    public void loadChildren() {
    	if (!childrenLoaded) {
            loadingChildren = true;
            LOG.debug("Loading artists");
            Iterator iter = catalog.getArtists().iterator();
            while (iter.hasNext()) {
                Artist artist = (Artist)iter.next();
                if (LOG.isDebugEnabled()) {
                	LOG.debug("Loading artist: "+artist.getName());
				}
                ArtistNode node = new ArtistNode(this, artist);
                this.add(node);
            }
            loadingChildren = false;
            childrenLoaded = true;
        }
    }

}