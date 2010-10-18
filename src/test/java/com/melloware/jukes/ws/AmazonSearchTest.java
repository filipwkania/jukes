package com.melloware.jukes.ws;

import java.util.Collection;

import com.melloware.jukes.AbstractTestCase;
import com.melloware.jukes.exception.WebServiceException;

/**
 * Test the Amazon Web Service calls using Apache Axis.
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class AmazonSearchTest extends AbstractTestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AmazonSearchTest.class);
    }

    /**
     * Constructor for AmazonSearchTest.
     * @param arg0
     */
    public AmazonSearchTest(String arg0) {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /*
     * Test method for 'com.melloware.jukes.ws.AmazonSearch.findItemsByDisc(String)'
     */
    public void testFindItemsByDisc() {
        try {
            Collection collection = AmazonSearch.findItemsByDisc("Out Of State Plates", "ecs.amazonaws.com");
            assertNotNull(collection);
            assertTrue(collection.size() > 0);
        } catch (WebServiceException ex) {
            fail("WebServiceException testFindItemsByDisc" + ex.getMessage());
        }
    }

    /*
     * Test method for 'com.melloware.jukes.ws.AmazonSearch.findItemsByArtist(String)'
     */
    public void testFindItemsByArtist() {
        try {
            Collection collection = AmazonSearch.findItemsByArtist("Fountains Of Wayne", "ecs.amazonaws.com");
            assertNotNull(collection);
            assertTrue(collection.size() > 0);
        } catch (WebServiceException ex) {
            fail("WebServiceException findItemsByArtist" + ex.getMessage());
        }
    }

    /*
     * Test method for 'com.melloware.jukes.ws.AmazonSearch.findItemsByArtistDisc(String, String)'
     */
    public void testFindItemsByArtistDisc() {
        try {
            Collection collection = AmazonSearch.findItemsByArtistDisc("Fountains","Out Of State Plates", "ecs.amazonaws.com");
            assertNotNull(collection);
            assertTrue(collection.size() > 0);
        } catch (WebServiceException ex) {
            fail("WebServiceException findItemsByArtist" + ex.getMessage());
        }
    }

    /*
     * Test method for 'com.melloware.jukes.ws.AmazonSearch.findItemsByArtistDiscSort(String, String, String)'
     */
    public void testFindItemsByArtistDiscSort() {
        try {
            Collection collection = AmazonSearch.findItemsByArtistDiscSort("Fountains","Out Of State Plates","ecs.amazonaws.com", AmazonSearch.SORT_DATE);
            assertNotNull(collection);
            assertTrue(collection.size() > 0);
        } catch (WebServiceException ex) {
            fail("WebServiceException findItemsByArtist" + ex.getMessage());
        }
    }

}
