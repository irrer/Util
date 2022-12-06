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
 * Representation of a version.  Versions are viewed
 * as a series of positive integers separated by single
 * periods (.).  They may not contain any other characters.
 * <p>
 * The maximum number of integer fields is restricted because
 * if a very large number is encountered then the string was
 * probably used in error.
 * <p>
 * In particular, alpha characters are not allowed because
 * they can introduce ambiguity when comparing versions.
 * For example, if comparing versions 1.0.9 and 1.0.10a, the
 * '10a' would be considered an alpha field and compared to
 * '9', and would be considered less than '9', which is not
 * the expected order.  Other cases mixing several alpha
 * and numeric values become even more complicated.
 *
 * @author Jim Irrer  irrer@umich.edu
 */
public class Version implements Comparable<Version> {

    /**
     * List of levels for this version.  For example, a version
     * of 1.0.3 would have fieldList = { 1, 0, 3 };
     */
    private int[] fieldList = null;

    /**
     * Maximum number of fields in the version specification.  In
     * most cases the number of fields will be about 3.  If a very
     * large number is attained then it probably means that the
     * wrong string was used.
     */
    public static final int MAX_VERSION_FIELDS = 100;


    /**
     * Construct a version from text.
     *
     * @param name        Name of service
     * @param versionText In a dot separated form such as: 1.2.34
     * @param description Description of service.
     * @throws UMROException
     */
    public Version(String versionText) throws UMROException {
        String check = versionText.replaceAll("[0-9\\.]", "");
        if (check.length() > 0) {
            throw new UMROException("Version text should only contain dots (.) and integers, but contains invalid characters: " + check);
        }
        String[] textFieldList = versionText.split("[^0-9]", MAX_VERSION_FIELDS + 3);
        int len = textFieldList.length;
        if (len < 1) {
            throw new UMROException("Invalid version specification contains no integers: " + versionText);
        }
        if (len > MAX_VERSION_FIELDS) {
            throw new UMROException("Invalid version specification contains no integers: " + versionText);
        }

        fieldList = new int[len];

        for (int l = 0; l < len; l++) {
            fieldList[l] = Integer.parseInt(textFieldList[l]);
        }
    }


    /**
     * Get the version of this service.
     *
     * @return Version as text.
     */
    public String formatToText() {
        StringBuffer text = new StringBuffer("" + fieldList[0]);
        for (int l = 1; l < fieldList.length; l++) {
            text.append("." + fieldList[l]);
        }
        return text.toString();
    }


    /**
     * Convert this version to a string.
     */
    @Override
    public String toString() {
        return formatToText();
    }


    /**
     * Compare two versions to see which is larger.  Each field of
     * the version number is compared.  Example: 1.0.22 is greater than 1.0.20 .
     */
    public int compareTo(Version other) {

        // If they have a different number of field then iterate
        // through the smaller one.
        int len = fieldList.length;
        if (other.fieldList.length < len) {
            len = other.fieldList.length;
        }

        // Determine which has the larger higher order number
        for (int l = 0; l < len; l++) {
            if (fieldList[l] != other.fieldList[l]) {
                return (fieldList[l] > other.fieldList[l]) ? 1 : -1;
            }
        }

        // If the number of fields is different, then the one with more fields is greater.
        if (fieldList.length != other.fieldList.length) {
            return (fieldList.length > other.fieldList.length) ? 1 : -1;
        }

        // Everything is the same, so they are equal.
        return 0;
    }
}
