package com.melloware.jukes.gui.view.editor;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.GradientBackgroundPanel;

/**
 * An empty panel to display when no node is selected.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see MainModule
 * @see Editor
 * @see javax.swing.SwingUtilities#updateComponentTreeUI(java.awt.Component)
 */
public final class EmptyPanel
    extends GradientBackgroundPanel
    implements Editor {

    /**
     * Constructs a <code>EmptyPanel</code>.
     */
    public EmptyPanel() {
        super(false);
        add(buildForeground());
    }

    public Class getDomainClass() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.melloware.jukes.gui.view.editor.Editor#getHeaderToolBar()
     */
    public JToolBar getHeaderToolBar() {
        return null;
    }

    // Implementing the Editor Interface ************************************

    public Icon getIcon() {
        return null;
    }

    public Object getModel() {
        return null;
    }

    public String getTitle() {
        return "";
    }

    public JToolBar getToolBar() {
        return null;
    }

    public void setModel(Object m) {
        // Nothing to do in this welcome panel.
    }

    // Building *************************************************************

    public void activate() {
        // Nothing to do in this welcome panel.
    }

    public void deactivate() {
        // Nothing to do in this welcome panel.
    }

    /**
     * Builds and answers the foreground.
     */
    private JComponent buildForeground() {
        FormLayout layout = new FormLayout("9dlu, left:pref:grow", "b:pref, c:pref, t:pref, 9dlu, pref, 6dlu, pref");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.getPanel().setOpaque(false);
        builder.setBorder(Borders.DLU14_BORDER);

        return builder.getPanel();
    }

}