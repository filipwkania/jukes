package com.melloware.jukes.file.tag;

import com.melloware.jukes.AbstractTestCase;
import com.melloware.jukes.exception.MusicTagException;

/**
 * Test case to excercise the MP3 ID3 library jaudiotagger.
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class MusicTagTest
    extends AbstractTestCase { 
    
    private MusicTag tag = null;

    /**
     * Constructor for MusicTagTest.
     * @param arg0
     */
    public MusicTagTest(String arg0) {
        super(arg0);

    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MusicTagTest.class);
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.extractTitleFromFilename()'
     */
    public void testExtractTitleFromFilename() {
        assertNotNull(tag.extractTitleFromFilename());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getDisc()'
     */
    public void testGetDisc() {
        assertEquals("Disc", tag.getDisc());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getArtist()'
     */
    public void testGetArtist() {
        assertEquals("Artist", tag.getArtist());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getComment()'
     */
    public void testGetComment() {
        assertEquals("Comment", tag.getComment());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getEncodedBy()'
     */
    public void testGetEncodedBy() {
        assertEquals("Junit", tag.getEncodedBy());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getGenre()'
     */
    public void testGetGenre() {
        assertEquals("Rock", tag.getGenre());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getHeaderInfo()'
     */
    public void testGetHeaderInfo() {
        assertNotNull(tag.getHeaderInfo());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getTitle()'
     */
    public void testGetTitle() {
        assertEquals("Title", tag.getTitle());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getTrack()'
     */
    public void testGetTrack() {
        assertEquals("01", tag.getTrack());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.getYear()'
     */
    public void testGetYear() {
        assertEquals("2006", tag.getYear());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.hashCode()'
     */
    public void testHashCode() {
        assertTrue(tag.hashCode() != 0);
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.removeTags()'
     */
    public void testRemoveTags() {
        try {
            tag.removeTags();
        } catch (MusicTagException ex) {
            fail("MusicTagException: Could not remove tags.");
        }
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.save()'
     */
    public void testSave() {
        try {
            tag.save();
        } catch (MusicTagException ex) {
            fail("MusicTagException: Could not save tags.");
        }
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setDisc(String)'
     */
    public void testSetDisc() {
        tag.setDisc("test");
        assertEquals("test", tag.getDisc());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setArtist(String)'
     */
    public void testSetArtist() {
        tag.setArtist("test");
        assertEquals("test", tag.getArtist());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setComment(String)'
     */
    public void testSetComment() {
        tag.setComment("test");
        assertEquals("test", tag.getComment());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setEncodedBy(String)'
     */
    public void testSetEncodedBy() {
        tag.setEncodedBy("test");
        assertEquals("test", tag.getEncodedBy());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setGenre(String)'
     */
    public void testSetGenre() {
        tag.setGenre("test");
        assertEquals("test", tag.getGenre());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setTitle(String)'
     */
    public void testSetTitle() {
        tag.setTitle("test");
        assertEquals("test", tag.getTitle());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setTrack(String)'
     */
    public void testSetTrack() {
        tag.setTrack("2");
        assertEquals("02", tag.getTrack());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.setYear(String)'
     */
    public void testSetYear() {
        tag.setYear("1870");
        assertEquals("1870", tag.getYear());
    }

    /*
     * Test method for 'com.melloware.jukes.file.MusicTag.toString()'
     */
    public void testToString() {
        assertNotNull(tag.toString());
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
                  throws Exception {
        super.setUp();
        
        try {
            tag = TagFactory.getTag("C:/dev/melloware/jukes/trunk/src/test/java/com/melloware/jukes/file/01 - Test.mp3");
            tag.setDisc("Disc");
            tag.setArtist("Artist");
            tag.setComment("Comment");
            tag.setEncodedBy("Junit");
            tag.setGenre("Rock");
            tag.setTitle("Title");
            tag.setTrack("1");
            tag.setYear("2006");
        } catch (MusicTagException ex) {
            fail("Tag could not be created");
        }
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown()
                     throws Exception {
        super.tearDown();
        if (tag != null) {
            tag = null;
        }
    }

}