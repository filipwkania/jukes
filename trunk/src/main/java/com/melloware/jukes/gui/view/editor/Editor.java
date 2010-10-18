package com.melloware.jukes.gui.view.editor;

import javax.swing.Icon;
import javax.swing.JToolBar;


/**
 * This interface describes general editor that have an <code>Icon</code>,
 * title, <code>ToolBar</code>, and can set and return a model.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public interface Editor {

    /**
     * Answers the editor's icon.
     */
    Icon getIcon();

    /**
     * Answers the editor's title.
     */
    String getTitle();

    /**
     * Answers the editor's tool bar.
     */
    JToolBar getToolBar();
    
    /**
     * Answers the editor's header tool bar.
     */
    JToolBar getHeaderToolBar();

    /**
     * Activates the editor.
     */
    void activate();

    /**
     * Deactivates the editor.
     */
    void deactivate();

    /**
     * Returns the associated domain class used to register this editor
     * with the EditorPanel's registry.
     */
    Class getDomainClass();

    /**
     * Returns this editor's model.
     */
    Object getModel();

    /**
     * Sets this editor's model. Called when the edited instance changed.
     */
    void setModel(Object model);

}
