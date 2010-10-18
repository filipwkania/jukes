package com.melloware.jukes.gui.view;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;

import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.jgoodies.uif.action.ActionManager;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uif.component.ToolBarButton;
import com.melloware.jukes.gui.tool.Actions;

/**
 * Builds the tool bar of the Jukes application.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 *
 * @see MainFrame
 * @see Actions
 */
final class MainToolBarBuilder {

    /**
     * Creates, configures, composes and returns the tool bar.
     *
     * @return the application's toolbar.
     */
    JToolBar build() {
    	final ToolBarBuilder toolBar = new ToolBarBuilder("Main ToolBar");
        toolBar.addGap(2);
        toolBar.add(createToolBarButton(Actions.REFRESH_ID));
        toolBar.add(createToolBarButton(Actions.TRACK_ADD_ID)); //AZ
        toolBar.add(createToolBarButton(Actions.DISC_ADD_ID));
        toolBar.add(createToolBarButton(Actions.DISC_FINDER_ID));
        toolBar.add(createToolBarButton(Actions.DISC_REMOVER_ID));
        toolBar.addSeparator();
        toolBar.add(createToolBarButton(Actions.SEARCH_ID));
        toolBar.add(createToolBarButton(Actions.PLAYLIST_SHOW_ID));
        toolBar.add(createToolBarButton(Actions.DISCLIST_SHOW_ID)); //AZ
        toolBar.add(createToolBarButton(Actions.FILTER_SHOW_ID));
        toolBar.add(createToolBarButton(Actions.CATALOG_EXPORT_ID));
        toolBar.add(createToolBarButton(Actions.TOOL_DIFFERENCE_ID));
        toolBar.add(createToolBarButton(Actions.TOOL_CHECK_GENRES_ID));  //AZ 
        toolBar.add(createToolBarButton(Actions.TOOL_LOCATION_ID));
        toolBar.add(createToolBarButton(Actions.TOOL_BACKUP_ID));
        toolBar.add(createToolBarButton(Actions.TOOL_MEMORY_ID));
        toolBar.add(createToolBarButton(Actions.TOOL_STATISTICS_ID));
        toolBar.addSeparator();
        toolBar.addLargeGap();
        toolBar.addLargeGap();
        toolBar.addGlue();
        toolBar.add(ActionManager.get(Actions.PREFERENCES_ID));
        toolBar.add(ActionManager.get(Actions.HELP_CONTENTS_ID));
        toolBar.addGap(2);
        
        JToolBar bar = toolBar.getToolBar();
//      Set a hint so that JGoodies Looks will detect it as being in the header.
        bar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        // Unlike the default, use a separator border.
        bar.putClientProperty(WindowsLookAndFeel.BORDER_STYLE_KEY, BorderStyle.SEPARATOR);
        bar.putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY, BorderStyle.SEPARATOR);
        return bar;
    }
    
    /**
     * Builds and returns the Tray Icon Menu.
     */
    public static JToolBar buildPlayerToolBar() {
    	final ToolBarBuilder toolBar = new ToolBarBuilder("Player");

        toolBar.addGap(2);
        toolBar.add(createToolBarButton(Actions.PLAYER_PLAY_ID));
        toolBar.add(createToolBarButton(Actions.PLAYER_PAUSE_ID));
        toolBar.add(createToolBarButton(Actions.PLAYER_STOP_ID));
        toolBar.add(createToolBarButton(Actions.PLAYER_PREVIOUS_ID));
        toolBar.add(createToolBarButton(Actions.PLAYER_NEXT_ID));
        toolBar.addGap(2);
        return toolBar.getToolBar();
    }

    /**
     * Creates and returns a button which is suitable for use in a tool bar.
     */
    private static AbstractButton createToolBarButton(String actionID) {
        return new ToolBarButton(ActionManager.get(actionID));
    }

}