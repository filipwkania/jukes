package com.melloware.jukes.gui.view.component;

import java.awt.AWTEventMulticaster;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import com.jgoodies.uif.action.ActionManager;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.file.image.ImageFactory;
import com.melloware.jukes.gui.tool.Actions;
import com.melloware.jukes.gui.tool.Resources;
import com.melloware.jukes.gui.tool.MainModule;
import com.melloware.jukes.gui.view.MainMenuBuilder;

/**
 * Helper for image album cover images.  If this object contains a disc object
 * it is clickable to take you to that disc.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class AlbumImage
    extends JComponent {

    ActionListener actionListener = null;
    Disc disc = null;
    transient Image image = null;
    ImageIcon thumbnail = null;

    /**
     * Default constructor.
     */
    public AlbumImage() {
        setPreferredSize(new Dimension(91, 91));
        initComponents();
    }

    public AlbumImage(Dimension preferredSize) {
        setPreferredSize(preferredSize);
        initComponents();
    }

    /**
     * Gets the disc.
     * <p>
     * @return Returns the disc.
     */
    public Disc getDisc() {
        return this.disc;
    }

    /**
     * Gets the image.
     * <p>
     * @return Returns the image.
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Sets the disc.
     * <p>
     * @param aDisc The disc to set.
     */
    public void setDisc(final Disc aDisc) {
        this.disc = aDisc;
        if (disc == null) {
        	this.setToolTipText(null);
        	this.setImage(null);
        } else {
        	//Set current cover URL as ToolTipText
        	if (MainModule.SETTINGS.isCopyImagesToDirectory()) {
        		this.setToolTipText(ImageFactory.standardImageFileName(disc.getArtist().getName(), disc.getName(), disc.getYear()));
            }
            else {
            	this.setToolTipText((disc.getCoverUrl()));
            }
        }
    }

    /**
     * Sets the image.
     * <p>
     * @param aImage The image to set.
     */
    public void setImage(final Image aImage) {
        this.image = aImage;
        // Update the preview accordingly.
        thumbnail = null;
        if (isShowing()) {
            loadImage();
            repaint();
        }
    }

    public void addActionListener(final ActionListener l) {
        actionListener = AWTEventMulticaster.add(actionListener, l);
    }

    public void loadImage() {
        if (image == null) {
            thumbnail = null;
            return;
        }

        // Don't use createImageIcon (which is a wrapper for getResource)
        // because the image we're trying to load is probably not one
        // of this program's own resources.
        final ImageIcon tmpIcon = new ImageIcon(image);
        if (tmpIcon != null) {
            thumbnail = tmpIcon;
        }
    }

    public void removeActionListener(final ActionListener l) {
        actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    protected void paintComponent(final Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int x = (getWidth() / 2) - (thumbnail.getIconWidth() / 2);
            int y = (getHeight() / 2) - (thumbnail.getIconHeight() / 2);

            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }

    private void initComponents() {
        final JPopupMenu popup = MainMenuBuilder.buildPlayerPopupMenu(this);
        addMouseListener(new ClickAdapter(popup));
    }

    /**
     * Handles Actionlistener and popping up the popup menu.
     */
    private final class ClickAdapter
        extends MouseAdapter {

        final JPopupMenu popup;

        public ClickAdapter(JPopupMenu popup) {
            this.popup = popup;
        }

        public void mouseClicked(final MouseEvent evt) {
            maybeShowPopup(evt);

            // middle mouse button pressed once queue in playlist
            if (((evt.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) && (evt.getClickCount() == 1)) {
            	final JComponent source = (JComponent)evt.getSource();
                final ActionEvent event = new ActionEvent(source, 1, Actions.PLAYER_QUEUE_ID);
                source.putClientProperty(Resources.EDITOR_COMPONENT, source);
                ActionManager.get(Actions.PLAYER_QUEUE_ID).actionPerformed(event);
                return;
            }

            //any other button fire the action event
            if (actionListener != null) {
                actionListener.actionPerformed(new ActionEvent(AlbumImage.this, ActionEvent.ACTION_PERFORMED, ""));
            }
        }

        public void mousePressed(final MouseEvent evt) {
            maybeShowPopup(evt);
        }

        public void mouseReleased(final MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(final MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                popup.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }

    }
}