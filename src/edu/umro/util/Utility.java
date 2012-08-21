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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * General purpose methods.
 * 
 * @author Jim Irrer  irrer@umich.edu 
 *
 */

public class Utility {

    /** Number of bytes in a single buffer for reading. */
    private final static int BUFFER_SIZE = 1024 * 1024;

    /**
     * This datetime format complies with oracle db datetime format.
     * convert a Date to Java date string in the following format
     * 
     * @param date
     * 
     * @return
     * 
     * @throws UMROException
     */
    public static String getDateTimeString(Date date) throws UMROException { 

        String sDateFormat = "yyyy/MM/dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(sDateFormat);
        String sStartDateTime = dateFormat.format(date);

        //create a oracle database date time string for insert
        String sOracleDateTimeFormat = "yyyy/mm/dd hh24:mi:ss";
        sStartDateTime = "to_date('" + sStartDateTime+ "','" + sOracleDateTimeFormat + "')";

        return sStartDateTime;
    }



    public static boolean isWordPresentInString(final String sSource,
            final String sSearch) throws UMROException {
        boolean bPresent = false;
        if (sSource == null || sSource.length() == 0) {
            throw new UMROException ("The parameter, 'Source' is not set.");
        }

        int nPos = sSource.toUpperCase().indexOf(sSearch);
        if (nPos != -1)
            bPresent = true;

        return bPresent;
    }

    public static String printClassMethodMessage(final String sClassName,
            final String sMethodName) throws UMROException {
        String sMessage = "";
        sMessage += "class: '" + sClassName + "': method: '" + sMethodName + "' called.";

        return sMessage;
    }


    /**
     * Write the given text to a file.
     *
     * @param file Write to this file.
     *
     * @param text Text to write.
     */
    public static void writeFile(File file, byte[] text) throws UMROException {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(text);
            out.flush();
            out.close();
            out = null;
        }
        catch (FileNotFoundException ex) {
            throw new UMROException("Could not find file " + file);
        }
        catch (IOException ex) {
            throw new UMROException("Unable to write to file " + file);
        }
        catch (SecurityException ex) {
            throw new UMROException("Not permitted to create file " + file);
        }
    }


    public static void writeFile(final String sFilePath, final String content) throws UMROException {
        writeFile(new File(sFilePath), content.getBytes());
    }


    public static boolean isFileExist(final String sFilePath) throws UMROException {

        boolean bExist = false;
        File f = new File(sFilePath);
        try {
            // force test for file existing.
            new FileInputStream(f);
            if (f.exists()) {
                bExist = true;
            }

        }
        catch (FileNotFoundException ex) {
            throw new UMROException(ex.getMessage());
        }
        return bExist;
    }


    /**
     * Read a file into a String.  If there is any problem, throw an exception.
     *
     * @param file The file to read.
     *
     * @return Contents of file, or null if not found, null file
     * name, can not read, etc.
     */
    public static String readFile(File file) throws UMROException {
        try {
            return readInputStream(new FileInputStream(file));
        }
        catch (FileNotFoundException ex) {
            throw new UMROException("Error, file '" + file.getAbsolutePath() + "' not found. Exception: " + ex);
        }
        catch (IOException ex) {
            throw new UMROException("Error while reading file '" + file.getAbsolutePath() + "'. Exception: " + ex);
        }
    }

    
    /**
     * Read a file verbatim and return it as an array.
     * 
     * @param file File to read.
     * 
     * @return Contents of file.
     * 
     * @throws RemoteException If file does not exist, has no read permission, etc..
     */
    public static byte[] readBinFile(File file) {
        try {
            byte[] buffer = new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            long actual = fis.read(buffer);
            if (actual != buffer.length) {
                throw new RuntimeException("Error reading file " + file.getAbsolutePath() + " .  Expected " + buffer.length + " bytes but got " + actual);
            }
            return buffer;
        }
        catch (Exception e) {
            throw new RuntimeException("Error reading file " + file.getAbsolutePath() + " : " + e);
        }
    }


    /**
     * Read the entire given input stream into a string.
     * 
     * @param inputStream Read from here.
     * 
     * @return String representation of input.
     * 
     * @throws IOException
     */
    public static String readInputStream(InputStream inputStream) throws IOException  {
        byte [] data = new byte[BUFFER_SIZE];
        StringBuffer text = new StringBuffer("");
        int size = data.length;
        while (size == data.length) {
            size = inputStream.read(data);
            text.append(new String(data, 0, size));
        }

        return text.toString();
    }

}
