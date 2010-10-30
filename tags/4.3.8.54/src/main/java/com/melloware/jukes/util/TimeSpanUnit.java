/*
 * TimeSpanUnit.java
 *
 * Created on March 26, 2003, 3:46 PM
 */
/* ====================================================================
 *
 * The JavaRanch Software License, Version 1.0
 *
 * Copyright (c) 2003 JavaRanch. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. The name JavaRanch must not be used to endorse or promote products derived from this software without prior written
 * permission.
 *
 * 4. Products derived from this software may not be called "JavaRanch" nor may "JavaRanch" appear in their names without
 * prior written permission of JavaRanch.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JAVARANCH OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. ====================================================================
 *
 */

package com.melloware.jukes.util;

/** The TimeSpanUnit defines a standard set of units to be used with the TimeSpan
* class. The methods of the TimeSpan class require the use of TimeSpanUnit
* objects for certain methods. The purpose of this class is to provide a
* typesafe enum. (see <a href="http://java.sun.com/j2se/1.4/docs/api/java/util/logging/Level.html">
* java.util.logging.Level</a> for an example from the Java API.)
* <p>
* The TimeSpanUnit objects are:
* <ul>
* <li>MILLISECONDS</li>
* <li>SECONDS</li>
* <li>MINUTES</li>
* <li>HOURS</li>
* <li>DAYS</li>
* </UL>
*
* @author Thomas Paul
*/
public final class TimeSpanUnit {

    /** MILLISECONDS indicates that milliseconds are being used */
    public static final TimeSpanUnit MILLISECONDS = new TimeSpanUnit("MILLISECONDS", TimeSpanConstants.MILLISECONDS);

    /** SECONDS indicates that seconds are being used */
    public static final TimeSpanUnit SECONDS = new TimeSpanUnit("SECONDS", TimeSpanConstants.SECONDS);

    /** MINUTES indicates that minutes are being used */
    public static final TimeSpanUnit MINUTES = new TimeSpanUnit("MINUTES", TimeSpanConstants.MINUTES);

    /** HOURS indicates that hours are being used */
    public static final TimeSpanUnit HOURS = new TimeSpanUnit("HOURS", TimeSpanConstants.HOURS);

    /** DAYS indicates that days are being used */
    public static final TimeSpanUnit DAYS = new TimeSpanUnit("DAYS", TimeSpanConstants.DAYS);
    private final int value;

    private final String name;

    /** No TimeSpanUnit objects should be created outside of this class */
    private TimeSpanUnit(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /** Gets the name of this TimeSpanUnit.
    *
    * @return the name of this TimeSpanUnit.
    */
    public int getName() {
        return value;
    }

    /** Gets the number of milliseconds this TimeSpanUnit represents.
    *
    * @return the number of milliseconds.
    */
    public int getValue() {
        return value;
    }

    /** Returns a hash code value for the object. This method is
    * supported for the benefit of hashtables such as those provided by
    * <code>java.util.Hashtable</code>. The method returns the value
    * of the TimeSpanUnit.
    *
    * @return a hash code value for this object.
    *
    * @see java.lang.Object#equals(java.lang.Object)
    * @see java.util.Hashtable
    *
    */
    public int hashCode() {
        return value;
    }

    /** Returns a string representation of the object in the format
    * "name:value"
    *
    * @return a string containing the name and value of this TimeSpanUnit object.
    *
    */
    public String toString() {
        return name + ":" + value;
    }

    interface TimeSpanConstants {

        /** Constant for milliseconds unit and conversion */
        int MILLISECONDS = 1;

        /** Constant for seconds unit and conversion */
        int SECONDS = MILLISECONDS * 1000;

        /** Constant for minutes unit and conversion */
        int MINUTES = SECONDS * 60;

        /** Constant for hours unit and conversion */
        int HOURS = MINUTES * 60;

        /** Constant for days unit and conversion */
        int DAYS = HOURS * 24;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object aObj) { //NOPMD
		return super.equals(aObj);
	}
}