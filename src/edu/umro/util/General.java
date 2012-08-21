package edu.umro.util;

/*
 * Copyright 2012 Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * General purpose methods.
 * 
 * @author Jim Irrer  irrer@umich.edu 
 *
 */
public class General {

    /**
     * Determine if two strings are equal, including the case where
     * both are null, in which case they are considered equal.
     * 
     * @param a First string, may be null.
     * 
     * @param b Second string, may be null.
     * 
     * @return True if equal, false if not.
     */
    public static boolean eqString(final String a, final String b) {
        boolean eq =
            ((a == null) && (b == null)) ||
            (((a != null) && (b != null)) && (a.equals(b)));
        return eq;
    }


    /**
     * Increment a string value to the next value.  Preserve
     * alpha and numeric characters, and preserve upper and lower case.
     * Punctuation is ignored.
     * 
     * If the value is not long enough and 'wraps around', then prefix
     * it with another digit to make it longer.
     * 
     * 
     * Examples of incrementing:
     * 
     * <li>aaa --> aab</li>
     * <li>0-5 --> 0-6</li>
     * <li>r9Z --> s0A</li>
     * <li>zZz --> 1aAa</li>
     * 
     */
    public static String increment(String value) {
        if (value == null) {
            value = "";
        }
        StringBuffer val = new StringBuffer(value);
        for (int i = val.length()-1; i >= 0; i--) {
            char c = val.charAt(i);
            switch (c) {
            case 'z':
                val.setCharAt(i, 'a');
                break;

            case 'Z':
                val.setCharAt(i, 'A');
                break;

            case '9':
                val.setCharAt(i, '0');
                break;
                
            default:
                if (((c >= 'a') && (c < 'z')) || ((c >= 'A') && (c < 'Z')) || ((c >= '0') && (c < '9'))) {
                    c++;
                    val.setCharAt(i, c);
                    return val.toString();
                }
            }
        }
        return "1" + val.toString();
    }
    
}
