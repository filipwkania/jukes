package com.melloware.jukes.gui.view.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uif.component.ToolBarButton;
import com.jgoodies.uifextras.panel.GradientBackgroundPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.view.component.ComponentFactory;

/**
 * The custom header for the Disc add dialog with toolbar.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * 
 * AZ Development 2010
 */
public final class DiscAddHeaderPanel
    extends GradientBackgroundPanel {

    public static final int DEFAULT_HEIGHT = 70;
    private DiscAddDialog owner;
    private final int height;
    private JLabel iconLabel;
    private JLabel titleLabel;
    private JTextArea descriptionArea;

    /**
     * Constructs a <code>HeaderPanel</code> for the given title,
     * description, and icon.
     */
    public DiscAddHeaderPanel(DiscAddDialog owner, String title, String description, Icon icon) {
        this(owner, title, description, icon, DEFAULT_HEIGHT);
    }

    /**
     * Constructs a <code>HeaderPanel</code> for the given title,
     * description, icon, and panel height.
     */
    public DiscAddHeaderPanel(DiscAddDialog owner, String title, String description, Icon icon, int height) {
        super(true);
        this.owner = owner;
        this.height = height;
        initComponents();
        build();
        setTitle(title);
        setDescription(description);
        setIcon(icon);
    }


    /**
     * Returns the description text.
     */
    public String getDescription() {
        return descriptionArea.getText();
    }

    /**
     * Returns the icon.
     */
    public Icon getIcon() {
        return iconLabel.getIcon();
    }

    /**
     * Returns the title text.
     */
    public String getTitle() {
        return titleLabel.getText();
    }

    /**
     * Sets the description text.
     */
    public void setDescription(String description) {
        descriptionArea.setText(description);
    }

    /**
     * Sets the icon.
     */
    public void setIcon(Icon icon) {
        if (null == icon) {
            iconLabel.setIcon(null);
            return;
        }
        if ((icon.getIconWidth() > 20) || !(icon instanceof ImageIcon)) {
            iconLabel.setIcon(icon);
            return;
        }
        Image image = ((ImageIcon)icon).getImage();
        int newWidth = 2 * icon.getIconWidth();
        int newHeight = 2 * icon.getIconHeight();
        image = image.getScaledInstance(newWidth, newHeight, 0);
        iconLabel.setIcon(new ImageIcon(image));
    }


    /**
     * Sets the title text.
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Builds and answers the panel's bottom component, a separator by default.
     */
    protected JComponent buildBottomComponent() {
        final ToolBarBuilder toolBar = new ToolBarBuilder("DiscToolBar");
        toolBar.addGap(2);
        ToolBarButton button = null;
        ActionManager.get(Actions.FREE_DB_ID).setEnabled(true);
        ActionManager.get(Actions.DISC_COVER_ID).setEnabled(true);
        ActionManager.get(Actions.FILE_RENAME_ID).setEnabled(true);
        //AZ: Add FreeDB Search
        button = (ToolBarButton)ComponentFactory.createToolBarButton(Actions.FREE_DB_ID);
        button.putClientProperty(Resources.EDITOR_COMPONENT, this.owner);
        toolBar.add(button);
        button = (ToolBarButton)ComponentFactory.createToolBarButton(Actions.DISC_COVER_ID);
        button.putClientProperty(Resources.EDITOR_COMPONENT, this.owner);
        toolBar.add(button);
        button = (ToolBarButton)ComponentFactory.createToolBarButton(Actions.FILE_RENAME_ID);
        button.putClientProperty(Resources.EDITOR_COMPONENT, this.owner);
        toolBar.add(button);
        button = (ToolBarButton)ComponentFactory.createToolBarButton(Actions.DISC_ADD_TITLECASE_ID);
        button.putClientProperty(Resources.EDITOR_COMPONENT, this.owner);
        toolBar.add(button);
        button = (ToolBarButton)ComponentFactory.createToolBarButton(Actions.DISC_ADD_RESET_FROM_FILENAME_ID);
        button.putClientProperty(Resources.EDITOR_COMPONENT, this.owner);
        toolBar.add(button);
        button = (ToolBarButton)ComponentFactory.createToolBarButton(Actions.DISC_ADD_RESET_NUMBERS_ID);
        button.putClientProperty(Resources.EDITOR_COMPONENT, this.owner);
        toolBar.add(button);
        button = (ToolBarButton)ComponentFactory.createToolBarButton(Actions.DISC_ADD_COMMENTS_ID);
        button.putClientProperty(Resources.EDITOR_COMPONENT, this.owner);
        toolBar.add(button);
        
        final JToolBar bar = toolBar.getToolBar();
        bar.setOpaque(false);
        return bar;
    }

    /**
     * Builds and answers the panel's center component.
     */
    protected JComponent buildCenterComponent() {
        FormLayout fl = new FormLayout("7dlu, 9dlu, left:pref, 14dlu:grow, pref, 4dlu",
                                       "7dlu, pref, 2dlu, pref, 0:grow");
        JPanel panel = new JPanel(fl);
        Dimension size = new Dimension(300, height);
        panel.setMinimumSize(size);
        panel.setPreferredSize(size);
        panel.setOpaque(false);

        CellConstraints cc = new CellConstraints();
        panel.add(titleLabel, cc.xywh(2, 2, 2, 1));
        panel.add(descriptionArea, cc.xy(3, 4));
        panel.add(iconLabel, cc.xywh(5, 1, 1, 5));

        return panel;
    }

    /**
     * Builds the panel.
     */
    private void build() {
        FormLayout fl = new FormLayout("pref:grow", "pref, pref");
        setLayout(fl);
        CellConstraints cc = new CellConstraints();
        add(buildCenterComponent(), cc.xy(1, 1));
        add(buildBottomComponent(), cc.xy(1, 2));
    }

    /**
     * Creates and configures the UI components.
     */
    private void initComponents() {
        titleLabel = UIFactory.createBoldLabel("", 0, Color.black);
        descriptionArea = UIFactory.createMultilineLabel("");
        descriptionArea.setForeground(Color.black);
        iconLabel = new JLabel();
    }

}