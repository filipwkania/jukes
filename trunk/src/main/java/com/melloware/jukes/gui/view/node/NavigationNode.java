package com.melloware.jukes.gui.view.node;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

import com.melloware.jukes.db.orm.AbstractJukesObject;

/**
 * This interface describes tree nodes in the Jukes navigation tree.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * 
 * @see javax.swing.tree.TreeNode
 */
public interface NavigationNode
    extends TreeNode {

    /**
     * Returns this node's icon with any overlays added.
     *
     * @return this node's icon with any overlays added.
     */
    Icon getNodeIcon(boolean selected);
    
    /**
     * Returns this node's icon for the given selection state.
     *
     * @return this node's icon for the given selection state
     */
    Icon getIcon(boolean selected);
    
    /**
     * Returns this node's Font for the given selection state.
     *
     * @return this node's Font for the given selection state
     */
    Font getFont();
    
    /**
     * Returns this node's Font color for the given selection state.
     *
     * @return this node's Font color for the given selection state
     */
    Color getFontColor();

    /**
     * Returns this node's model.
     *
     * @return this node's model
     */
    AbstractJukesObject getModel();

    /**
     * Returns this node's name.
     *
     * @return this node's name
     */
    String getName();

}