package com.melloware.jukes.gui.tool.help;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.melloware.jukes.db.orm.Artist;
import com.melloware.jukes.db.orm.Disc;
import com.melloware.jukes.db.orm.Track;
import com.melloware.jukes.gui.tool.DynamicHelpModule;
import com.melloware.jukes.gui.tool.Resources;

/**
 * This class provides help sets (TreeNodes) for each domain class.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @see     com.melloware.jukes.gui.tool.DynamicHelpModule
 * AZ Development 2010
 */
public final class HelpSets {

    public static final TreeNode WELCOME_HELP_SET = createWelcomeHelpSet();
    public static final TreeNode ARTIST_HELP_SET = createArtistHelpSet();
    public static final TreeNode DISC_HELP_SET = createDiscHelpSet();
    public static final TreeNode TRACK_HELP_SET = createTrackHelpSet();

    private static final String URL_PREFIX = Resources.getString(Resources.HELP_URL_PREFIX);//AZ
    private static final String HELP_TEXT = "Help";

    private HelpSets() {
        // Suppresses default constructor, ensuring non-instantiability.
    }

    /**
     * Registers all help sets with the DynamicHelpModule.
    *
    * @param helpModule   holds a map from selection types to help sets
     */
    public static void registerHelpSets(DynamicHelpModule helpModule) {
        helpModule.registerHelp(Artist.class, ARTIST_HELP_SET);
        helpModule.registerHelp(Disc.class, DISC_HELP_SET);
        helpModule.registerHelp(Track.class, TRACK_HELP_SET);
    }

    /**
     * Creates and answers the artist help set.
     */
    private static TreeNode createArtistHelpSet() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode chapter;

        chapter = createChapter(HELP_TEXT);
        chapter.add(createTopic("Artist", "artist/general"));
        chapter.add(createTopic("Information", "artist/information"));
        root.add(chapter);

        chapter = createChapter("Samples");
        chapter.add(createTopic("Sample1", "artist/sample1"));
        chapter.add(createTopic("Sample2", "artist/sample2"));
        root.add(chapter);

        return root;
    }

    /**
     * Creates and returns a chapter node.
     */
    private static DefaultMutableTreeNode createChapter(String name) {
        return new DefaultMutableTreeNode(HelpNode.createChapter(name));
    }

    /**
     * Creates and answers the disc help set.
     */
    private static TreeNode createDiscHelpSet() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode chapter;

        chapter = createChapter(HELP_TEXT);
        chapter.add(createTopic("Disc", "disc/general"));
        chapter.add(createTopic("Information", "disc/information"));
        chapter.add(createTopic("Audit data", "disc/audit"));
        root.add(chapter);

        chapter = createChapter("Samples");
        chapter.add(createTopic("Sample1", "disc/sample1"));
        root.add(chapter);

        return root;
    }

    /**
     * Creates and returns a topic node.
     */
    private static DefaultMutableTreeNode createTopic(String name, String path) {
        String fullPath = URL_PREFIX + path + ".html";
        return new DefaultMutableTreeNode(HelpNode.createTopic(name, fullPath));
    }

    /**
     * Creates and answers the track help set.
     */
    private static TreeNode createTrackHelpSet() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode chapter;

        chapter = createChapter(HELP_TEXT);
        chapter.add(createTopic("Track", "track/general"));
        chapter.add(createTopic("Information", "track/information"));
        chapter.add(createTopic("Audit data", "track/audit"));
        chapter.add(createTopic("Editing", "track/editing"));
        root.add(chapter);

        chapter = createChapter("Samples");
        chapter.add(createTopic("Sample1", "track/sample1"));
        chapter.add(createTopic("Sample2", "track/sample2"));
        root.add(chapter);

        return root;
    }

    /**
     * Creates and answers the welcome help set.
     */
    private static TreeNode createWelcomeHelpSet() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode chapter;

        chapter = createChapter(HELP_TEXT);
        chapter.add(createTopic("Welcome", "welcome/welcome"));
        chapter.add(createTopic("How to use dynamic help", "welcome/help"));
        root.add(chapter);

        chapter = createChapter("Getting Started");
        chapter.add(createTopic("Add new album to catalog", "welcome/new"));
        chapter.add(createTopic("Open existing project", "welcome/open"));
        root.add(chapter);

        return root;
    }

}