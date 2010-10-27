package com.melloware.jukes.gui.tool.help;

import java.net.URL;

import javax.swing.Icon;

import com.jgoodies.uif.util.ResourceUtils;



/**
 * Instances of this class define help nodes in the dynamic help.
 * A help node is either a chapter or topic, where only topics have
 * an attached URL.
 * <p>
 * Copyright (c) 1999-2007 Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class HelpNode {

    public static final int CHAPTER_ITEM = 0;
    public static final int TOPIC_ITEM = 1;

    private static final String BOOK_ICON_ID = "com.jgoodies.help.openBook.icon";

    private static final String TOPIC_ICON_ID = "com.jgoodies.help.topic.icon";

    private static final Icon BOOK_ICON = ResourceUtils.getIcon(BOOK_ICON_ID);

    private static final Icon TOPIC_ICON = ResourceUtils.getIcon(TOPIC_ICON_ID);
    private final int type;

    private final String name;
    private final URL url;

    /**
     * Constructs a <code>HelpNode</code> with the given name, type, and URL.
     */
    private HelpNode(String name, int type, URL url) {
        this.name = name;
        this.type = type;
        this.url = url;
    }

    public Icon getIcon(boolean sel) {
        return isChapter() ? BOOK_ICON : TOPIC_ICON;
    }

    public URL getURL() {
        return url;
    }

    public boolean isChapter() {
        return type == CHAPTER_ITEM;
    }

    public String toString() {
        return name;
    }

    /**
     * Creates and returns a chapter help node with the given name.
     */
    static HelpNode createChapter(String name) {
        return new HelpNode(name, CHAPTER_ITEM, null);
    }

    /**
     * Creates and returns the help root node.
     */
    static HelpNode createRoot() {
        return createChapter("root");
    }

    /**
     * Creates and returns a topic help node with the given name and path.
     */
    static HelpNode createTopic(String name, String path) {
        URL url = ResourceUtils.getURL(path);
        return new HelpNode(name, TOPIC_ITEM, url);
    }

    boolean matches(URL aUrl) {
        return aUrl.equals(getURL());
    }

}