package com.melloware.jukes.db;

import org.apache.commons.lang.SystemUtils;

import com.melloware.jukes.AbstractTestCase;

/**
 * Test case for Database class.
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class DatabaseTest
    extends AbstractTestCase {

    /**
     * Constructor for DatabaseTest.
     * @param arg0
     */
    public DatabaseTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DatabaseTest.class);
    }

    /*
     * Test method for 'com.melloware.jukes.db.Database.getJdbcURL()'
     */
    public final void testGetJdbcURL() {
        Database.startup(SystemUtils.JAVA_IO_TMPDIR + "db/db", "test");
        Database.shutdown();
        String url = Database.getJdbcURL();
        assertNotNull(url);
    }

    /*
     * Test method for 'com.melloware.jukes.db.Database.setWriteDelay(Connection, String)'
     */
    public final void testSetWriteDelay() {

        // do nothing

    }

    /*
     * Test method for 'com.melloware.jukes.db.Database.shutdown()'
     */
    public final void testShutdown() {
        Database.startup(SystemUtils.JAVA_IO_TMPDIR + "db/db", "test");
        Database.shutdown();

    }

    /*
     * Test method for 'com.melloware.jukes.db.Database.startup(String)'
     */
    public final void testStartup() {
        Database.startup(SystemUtils.JAVA_IO_TMPDIR + "db/db", "test");
        Database.shutdown();

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