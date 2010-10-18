package com.melloware.jukes.gui.view.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.jgoodies.uif.application.Application;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.Settings;
import com.melloware.jukes.gui.view.MainFrame;
import com.melloware.jukes.util.JukesValidationMessage;

/**
 * Custom TrackListCellRenderer used to display Tracks in JList messages.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class TrackListCellRenderer
    extends JLabel
    implements ListCellRenderer {

    /**
     * Comparator used to sort the list items by name constantly.
     */
    public static final Comparator TRACK_COMPARATOR = new Comparator() {
        public int compare(Object aObject1, Object aObject2) {
            final JukesValidationMessage item1 = (JukesValidationMessage)aObject1;
            final JukesValidationMessage item2 = (JukesValidationMessage)aObject2;
            final CompareToBuilder builder = new CompareToBuilder();
            builder.append(item1.formattedText(), item2.formattedText());
            return builder.toComparison();
        }
    };
    public final Settings settings;

    /**
     * Default Constructor
     */
    public TrackListCellRenderer() {
        super();
        this.settings = null;
    }

    /**
     * Default Constructor
     */
    public TrackListCellRenderer(Settings aSettings) {
        super();
        this.settings = aSettings;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean,
     * boolean)
     */
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value,
                                                  final int index,
                                                  final boolean isSelected,
                                                  final boolean cellHasFocus) {

        final JukesValidationMessage message = (JukesValidationMessage)value;
        // this.setIcon(TrackListCellRenderer.getIcon(message.severity()));
        final Track track = (Track)message.getDomainObject();

        // pad the display with two spaces for readability
        this.setText(track.getDisplayText("  " + this.settings.getDisplayFormatTrack()) + "  ");
        final MainFrame mainFrame = (MainFrame)Application.getDefaultParentFrame();
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            if (track.isNotValid()) {
                setForeground(Color.RED);
            } else if (mainFrame.getPlaylist().containsNext(track)) {
                setForeground(Color.BLUE);
            } else {
                setForeground(list.getSelectionForeground());
            }
        } else {
            setBackground(list.getBackground());
            if (track.isNotValid()) {
                setForeground(Color.RED);
            } else if (mainFrame.getPlaylist().containsNext(track)) {
                setForeground(Color.BLUE);
            } else {
                setForeground(list.getForeground());
            }
        }
        setEnabled(list.isEnabled());
        Font newfont = list.getFont();
        if (track.isNotValid()) {
            newfont = newfont.deriveFont(newfont.getStyle() ^ Font.ITALIC);
        }
        setFont(newfont);
        setOpaque(true);
        return this;
    }

}