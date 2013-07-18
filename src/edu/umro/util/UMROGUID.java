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

import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.server.UID;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Generate DICOM compliant GUIDs with authorship by UMRO.
 * 
 * @author Jim Irrer  irrer@umich.edu 
 *
 */

public class UMROGUID {

    /** The root GUID for University of Michigan Radiation Oncology. */
    public static String UMRO_ROOT_GUID = "1.3.6.1.4.1.22361.";

    /** The MAC address of this machine.  This is used to make
     * the GUID unique across machines.
     */
    private static long macAddress = 0;

    /** Flag to determine whether MAC address has been initialized. */
    private static boolean initialized = false;


    /**
     * Generate a DICOM compliant GUID using the UMRO root.
     *
     * @return A DICOM compliant GUID using the UMRO root.
     * @throws SocketException 
     * @throws UnknownHostException 
     */
    public static synchronized String getUID() throws UnknownHostException {

        // Initialized MAC address if necessary.
        if (!initialized) {
            initialized = true;
            try {
                macAddress = OpSys.getMACAddress();
            }
            catch (SocketException e) {
                macAddress = Long.parseLong(OpSys.getHostIPAddress().replace('.', 'x').replaceAll("x", ""));
                // if localhost (127.0.0.1) is returned, then try something random instead.  The risk is
                // that the same 2^63 random number will be returned twice, but it is a low risk. 
                if (macAddress == 127001) {
                    macAddress = new Random().nextLong();
                }
            }
            macAddress = Math.abs(macAddress);
        }

        // Use standard class to get unique values.
        String guidText = new UID().toString();

        StringTokenizer st = new StringTokenizer(guidText, ":");

        int unique = Math.abs(Integer.valueOf(st.nextToken(), 16).intValue());
        long time = Math.abs(Long.valueOf(st.nextToken(), 16).longValue());
        // why add 0x8000 ? because usually starts at -8000, which wastes 4 digits
        int count = Math.abs(Short.valueOf(st.nextToken(), 16).shortValue() + 0x8000);

        // concatenate values to make it into a DICOM GUID.
        String guid = UMRO_ROOT_GUID + macAddress + "." + unique + "." + time
                + "." + count;

        return guid;
    }


    private static SimpleDateFormat dicomDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat dicomTimeFormat = new SimpleDateFormat("HHmmss");


    /**
     * Return the given date formatted formatted for DICOM consumption 
     * @param date
     * @return Date in DICOM format.
     */
    public static String dicomDate(Date date) {
        return dicomDateFormat.format(date);
    }


    /**
     * Return the given time formatted for DICOM consumption 
     * @param date
     * @return Time in DICOM format.
     */
    public static String dicomTime(Date date) {
        return dicomTimeFormat.format(date);
    }


    /**
     * Main for testing.  No parameters required.
     */
    public static void main(String args[]) {
        try {
            for (int i = 0; i < 20; i++) {
                System.out.println(" UMROGUID.getUID(): " + UMROGUID.getUID());
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }

}
