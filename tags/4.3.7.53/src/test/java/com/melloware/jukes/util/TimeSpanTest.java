package com.melloware.jukes.util;


import com.melloware.jukes.AbstractTestCase;

/**
 * Test case to test TimeSpan class.
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public class TimeSpanTest extends AbstractTestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TimeSpanTest.class);
	}

	/**
	 * Constructor for TimeSpanTest.
	 * @param arg0
	 */
	public TimeSpanTest(String arg0) {
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
	 * Test method for 'com.melloware.jukes.util.TimeSpan.getDays()'
	 */
	public void testGetDays() {
		TimeSpan timespan = new TimeSpan(10000000000l);
		assertTrue(timespan.getDays() > 0);

	}

	/*
	 * Test method for 'com.melloware.jukes.util.TimeSpan.getHours()'
	 */
	public void testGetHours() {
		TimeSpan timespan = new TimeSpan(10000000000l);
		assertTrue(timespan.getHours() > 0);
	}

	/*
	 * Test method for 'com.melloware.jukes.util.TimeSpan.getMilliseconds()'
	 */
	public void testGetMilliseconds() {
		TimeSpan timespan = new TimeSpan(10000000000l);
		assertTrue(timespan.getMilliseconds() > 0);
	}

	/*
	 * Test method for 'com.melloware.jukes.util.TimeSpan.getMinutes()'
	 */
	public void testGetMinutes() {
		TimeSpan timespan = new TimeSpan(10000000000l);
		assertTrue(timespan.getMinutes() > 0);
	}

	/*
	 * Test method for 'com.melloware.jukes.util.TimeSpan.getMusicDuration()'
	 */
	public void testGetMusicDuration() {
		TimeSpan timespan = new TimeSpan(10000000000l);
		assertTrue(timespan.getMusicDuration() != null);
	}

	/*
	 * Test method for 'com.melloware.jukes.util.TimeSpan.getSeconds()'
	 */
	public void testGetSeconds() {
		TimeSpan timespan = new TimeSpan(10000000000l);
		assertTrue(timespan.getSeconds() > 0);
	}

}
