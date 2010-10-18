package com.melloware.jukes.gui.tool;

import javax.swing.Icon;

import com.jgoodies.uif.util.ResourceUtils;


/**
 * This class consists of string ids used for the resource bundle lookup,
 * and some preloaded resources.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 * AZ Development 2010
 */
public final class Resources {

    // from Resources.properties
    public static final String APPLICATION_NAME = getString("application.name");
    public static final String APPLICATION_LOCATION = getString("application.location");
    public static final String APPLICATION_EMAIL = getString("application.mailto.feeback");
    public static final String APPLICATION_URL = getString("application.vendor.url");
    public static final String APPLICATION_DESCRIPTION = getString("application.description");
    public static final String APPLICATION_COPYRIGHT = getString("application.vendor.copyright");
    public static final String APPLICATION_VENDOR = getString("application.vendor");
    public static final String APPLICATION_DONATE_URL = getString("application.donate.url");
    public static final String APPLICATION_FORUMS_URL = getString("application.forums.url");
    public static final String MESSAGE_EDITOR_ERRORS = getString("messages.editorerrors");
    public static final String HELP_CONTENTS_ICON_ID = "helpContents.icon";
    public static final String HELP_URL_PREFIX = "help.url.prefix";//AZ
    public static final String HELP_GLOBAL_HELPSET= "help.global.helpset.url";//AZ
    public static final String HELP_TIPS_INDEX= "help.tips.index.path";//AZ
    

    // static icons
    public static final Icon LOGO_ICON = getIcon("logo.icon");
    public static final Icon DISC_ADD_ICON = getIcon("disc.add.icon");
    public static final Icon DISC_TREE_ICON = getIcon("disc.treenode.icon");
    public static final Icon ARTIST_TREE_ICON = getIcon("artist.treenode.icon");
    public static final Icon TRACK_TREE_ICON = getIcon("track.treenode.icon");
    public static final Icon STATS_SMALL_ICON = getIcon("stats.small.icon");
    public static final Icon STATS_LARGE_ICON = getIcon("stats.large.icon");
    public static final Icon MEMORY_LARGE_ICON = getIcon("memory.large.icon");
    public static final Icon DISC_FINDER_ICON = getIcon("disc.finder.icon");
    public static final Icon DISC_REMOVER_ICON = getIcon("disc.remover.icon");
    public static final Icon NAVIGATOR_ICON = getIcon("navigator.icon");
    public static final Icon NODE_INVALID_OVERLAY_ICON = getIcon("node.invalid.icon");
    public static final Icon NODE_NEW_OVERLAY_ICON = getIcon("node.new.icon");
    public static final Icon FILE_JPG_ICON = getIcon("file.jpg.icon");
    public static final Icon FILE_PNG_ICON = getIcon("file.png.icon");
    public static final Icon FILE_GIF_ICON = getIcon("file.gif.icon");
    public static final Icon FILE_TIF_ICON = getIcon("file.tiff.icon");
    public static final Icon FILE_TEXT_ICON = getIcon("file.text.icon");
    public static final Icon WEB_SEARCH_ICON = getIcon("web.search.icon");
    public static final Icon DIFFERENCE_TOOL_ICON = getIcon("difference.tool.icon");
    public static final Icon GENRES_TOOL_ICON = getIcon("genres.tool.icon"); //AZ
    public static final Icon LOCATION_TOOL_ICON = getIcon("location.tool.icon");
    public static final Icon THREAD_START_ICON = getIcon("thread.start.icon");
    public static final Icon THREAD_STOP_ICON = getIcon("thread.stop.icon");
    public static final Icon DIFFERENCE_TOOL_DIFF_ICON = getIcon("difference.tool.diff.icon");
    public static final Icon DIFFERENCE_TOOL_INTERSECTION_ICON = getIcon("difference.tool.intersection.icon");
    public static final Icon DIFFERENCE_TOOL_UNION_ICON = getIcon("difference.tool.union.icon");
    public static final Icon DISC_GD_ICON = getIcon("disc.gd.icon");
    public static final Icon DISC_PF_ICON = getIcon("disc.pf.icon");
    public static final Icon DISC_WHO_ICON = getIcon("disc.who.icon");
    public static final Icon DISC_BEATLES_ICON = getIcon("disc.beatles.icon");
    public static final Icon DISC_STONES_ICON = getIcon("disc.stones.icon");
    public static final Icon SEARCH_ICON = getIcon("search.icon");
    public static final Icon CSV_ICON = getIcon("csv.icon");
    public static final Icon PDF_ICON = getIcon("pdf.icon");
    public static final Icon FILTER_ICON = getIcon("filter.icon");
    public static final Icon PLAYLIST_ICON = getIcon("playlist.icon");
    public static final Icon HISTORY_ICON = getIcon("history.icon");
    public static final Icon OGG_VORBIS_ICON = getIcon("oggvorbis.icon");
    public static final Icon FLAC_ICON = getIcon("flac.icon");
    public static final Icon DISCLIST_ICON = getIcon("disclist.icon");  //AZ
    public static final Icon TRACK_ADD_ICON = getIcon("track.add.icon");//AZ
    public static final Icon FREE_DB_ICON = getIcon("free.db.icon");//AZ
    public static final Icon XML_EXPORT_ICON = getIcon("xml.export.icon");//AZ

    // constants
    public static final String TAB = "\t";
    public static final String TEXT_COMPONENT = "textComponent";
    public static final String FILE_CHOOSER_FILTER = "fileChooserFilter";
    public static final String FILE_CHOOSER_TITLE = "fileChooserTitle";
    public static final String EDITOR_COMPONENT = "editorComponent";
    public static final String NODE_INSERTED = "nodeInserted";
    public static final String NODE_DELETED = "nodeDeleted";
    public static final String NODE_CHANGED = "nodeChanged";
    public static final String TRACK_COMPONENT = "trackComponent";

    public static final String[] BITRATES = {
        "32", "64", "96", "128", "160", "192", "224", "256", "288", "320", "352", "384", "416", "448", "999"
    };
    public static final String[] OPERATOR = { "=", ">", ">=", "<", "<=" };

    /**
     * Default constructor. Private so no instantiation.
     */
    private Resources() {
        super();
    }

    public static Icon getIcon(String id) {
        return ResourceUtils.getIcon(id);
    }

    public static String getString(String id) {
        return ResourceUtils.getString(id);
    }

}