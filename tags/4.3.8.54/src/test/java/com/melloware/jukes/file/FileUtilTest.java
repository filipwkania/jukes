package com.melloware.jukes.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.melloware.jukes.AbstractTestCase;

/**
 * Test case to test out the FileUtil class.
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class FileUtilTest
    extends AbstractTestCase {

    /**
     * Constructor for FileUtilTest.
     * @param arg0
     */
    public FileUtilTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FileUtilTest.class);
    }

    /*
     * Test method for 'com.melloware.jukes.file.FileUtil.setReadOnly(File, boolean)'
     */
    public void testSetReadOnly() {
        File file = null;
        try {
            file = File.createTempFile("test", ".tmp");

            // try setting readonly
            FileUtil.setReadOnly(file, true);
            FileUtil.setReadOnly(file, false);

            FileUtils.forceDelete(file);

        } catch (IOException ex) {
            fail("IOException testing readonly" + ex.getMessage());
        }

    }
    
    /*
     * Test method for 'com.melloware.jukes.file.FileUtil.capitalize(string)'
     */
    public void testCapitalize() {
       String test1 = "tESt 1";
       String test2 = "Test two";
       String test3 = "Test 3" ;
       
       assertEquals(FileUtil.capitalize(test1), "Test 1");
       assertEquals(FileUtil.capitalize(test2), "Test Two");
       assertEquals(FileUtil.capitalize(test3), "Test 3");
    }
    
    /*
     * Test method for 'com.melloware.jukes.file.FileUtil.corectFilename(string)'
     */
    public void testCorrectFilename() {
       String test1 = "Test*1.mp3";
       String test2 = "Te?st2.mp3";
       String test3 = "<Test> 3:.mp3" ;
       
       assertEquals(FileUtil.correctFileName(test1), "Test_1.mp3");
       assertEquals(FileUtil.correctFileName(test2), "Te_st2.mp3");
       assertEquals(FileUtil.correctFileName(test3), "_Test_ 3_.mp3");
    }


    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
                  throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown()
                     throws Exception {
        super.tearDown();
    }

}