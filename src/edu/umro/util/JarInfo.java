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

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;


/**
 * Provide general and standard support for declaring,
 * discovering, and showing application versions and
 * other associated information.
 *
 * @author Jim Irrer  irrer@umich.edu
 */
public class JarInfo {

    /**
     * Manifest of the jar that was used to load this class.
     */
    private Manifest manifest = null;

    /**
     * Name of manifest file.
     */
    private static final String MANIFEST_FILE_NAME = "MANIFEST.MF";

    /**
     * The full path name of the jar file from which this class was loaded.
     */
    private String fullJarFilePath = null;

    /**
     * The class for which version information is being provided.
     */
    private Class<?> clss = null;

    public JarInfo(Class<?> clss) {
        this.clss = clss;
    }

    /**
     * Get a value from the manifest associated with this Java package.
     *
     * @param key Name of value.
     * @return Value from manifest.
     * @throws UMROException
     */
    private String getProjectManifestValue(String key) throws UMROException {
        String packageName = getPackageNameOfClass(clss);
        Attributes attributes = getManifest().getAttributes(packageName);
        if (attributes == null) {
            attributes = getManifest().getAttributes(packageName.replace('/', '.'));
        }
        if (attributes == null) {
            attributes = getManifest().getAttributes(packageName.replace('.', '/'));
        }
        if (attributes == null) {
            return null;
        }
        String value = (String) attributes.get(new Attributes.Name(key));
        return value;
    }


    /**
     * Get a value from the main part of the manifest associated with this Java package.
     *
     * @param key Name of value.
     * @return Value from manifest.
     * @throws UMROException
     */
    public String getMainManifestValue(String key) throws UMROException {
        java.util.jar.Attributes attributes = getManifest().getMainAttributes();
        if (attributes == null) {
            return null;
        }
        String value = (String) attributes.get(new Attributes.Name(key));
        return value;
    }


    /**
     * Get list of attributes.
     *
     * @return List of attributes.
     * @throws UMROException
     */
    public Attributes getMainAttributes() throws UMROException {
        return getManifest().getMainAttributes();
    }


    /**
     * Get a value from the main part of the manifest associated with this Java package.
     *
     * @param key  Name of value.
     * @param dflt Default value if there is a problem.
     * @return Value from manifest.
     */
    public String getMainManifestValue(String key, String dflt) {
        try {
            Attributes attributes = getManifest().getMainAttributes();
            if (attributes == null) {
                return dflt;
            }
            String value = (String) attributes.get(new Attributes.Name(key));
            return (value == null) ? dflt : value;
        } catch (UMROException e) {
            return dflt;
        }
    }


    /**
     * Get the name of the application with no version information.
     *
     * @return
     * @throws UMROException
     */
    public String getApplicationName() throws UMROException {
        return getProjectManifestValue("ApplicationName");
    }


    /**
     * Get a description of the application.
     *
     * @return
     */
    public String getApplicationDescription() throws UMROException {
        return getProjectManifestValue("ApplicationDescription");
    }


    /**
     * Get the version of the application with no name.
     *
     * @return
     */
    public String getApplicationVersion() throws UMROException {
        return getProjectManifestValue("ApplicationVersion");
    }

    /**
     * Get the build date of this version of the application.
     *
     * @return The build date.
     */
    public String getBuildDate() throws UMROException {
        return getMainManifestValue("BuildDate");
    }

    /**
     * Get the main executable class for this jar.
     *
     * @return The main class.
     */
    public String getMainClass() throws UMROException {
        return getMainManifestValue("Main-Class");
    }


    /**
     * Get the build time in as a <code>Date</code> of this version of the application.
     *
     * @return The build date.
     */
    public Date getBuildTime() throws UMROException {
        String buildDate = getMainManifestValue("BuildDate");
        if (buildDate == null) {
            throw new UMROException("Could not find BuildDate in jar manifest.");
        }
        String format = getMainManifestValue("timeStampFormat");
        if (format == null) {
            format = "yyyy/MM/dd hh:mm:ss.SSS";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(buildDate);
            return date;
        } catch (ParseException e) {
            String msg =
                    "Could not find parse build date '" + buildDate + "' to a date using format '" + format + "'.";
            throw new UMROException(msg);
        }
    }


    /**
     * Get the full path name of the jar file that was used to
     * load this class;
     *
     * @return The full path of the jar file.
     * @throws UMROException
     */
    public String getFullJarFilePath() throws UMROException {
        getManifest();
        return fullJarFilePath;
    }


    /**
     * Get the user id of the builder of the application.
     *
     * @return The developers user id.
     */
    public String getBuiltBy() throws UMROException {
        return getMainManifestValue("Built-By");
    }


    /**
     * Get the base name of the jar.
     *
     * @return The jar base name.
     */
    public String getJarName() throws UMROException {
        return getMainManifestValue("JarName");
    }


    /**
     * Get the implementation version of the jar.
     *
     * @return The implementation version of the jar.
     */
    public String getImplementationVersion() throws UMROException {
        return getMainManifestValue("Implementation-Version");
    }

    /**
     * Get the implementation vendor of the jar.
     *
     * @return The implementation version of the jar.
     */
    public String getImplementationVendor() throws UMROException {
        return getMainManifestValue("Implementation-Vendor");
    }


    /**
     * Get the jar description.
     *
     * @return The jar description.
     */
    public String getJarDescription() throws UMROException {
        return getMainManifestValue("JarDescription");
    }


    /**
     * Get the manifest of the jar file that was used to load this class.
     *
     * @return Manifest from jar from which this class was loaded.
     * @throws UMROException
     */
    private Manifest getManifest() throws UMROException {
        if (manifest == null) {
            try {
                ClassLoader classLoader = clss.getClassLoader();
                String packageName = getPackageNameOfClass(clss);
                URL url = classLoader.getResource(packageName);
                fullJarFilePath = url.getFile();
                fullJarFilePath = fullJarFilePath.replaceAll("%20", " ");
                fullJarFilePath = fullJarFilePath.replaceAll("!.*", "");
                String prefix = "file:/";

                // If this is true, then we have a real jar file.
                if (fullJarFilePath.startsWith(prefix)) {
                    fullJarFilePath = fullJarFilePath.substring(0 + prefix.length());
                    JarFile jarFile = null;
                    try {
                        jarFile = new JarFile(fullJarFilePath);
                        manifest = jarFile.getManifest();
                        return manifest;
                    } finally {
                        if (jarFile != null) jarFile.close();
                    }
                }
                // Otherwise, not a real jar file.  Use the manifest file directly.  This
                // should only happen in development mode.
                else {
                    if (fullJarFilePath.startsWith("/")) {
                        while (fullJarFilePath.charAt(0) == '/') {
                            fullJarFilePath = fullJarFilePath.substring(1);
                        }
                        // Get the name of the directory containing the manifest file.
                        File directory = new File(fullJarFilePath);
                        File file = new File(directory, MANIFEST_FILE_NAME);
                        while ((!file.canRead()) && (directory.getParent() != null)) {
                            directory = (directory.getParentFile() == null) ? new File(".") : directory.getParentFile();
                            file = new File(directory, MANIFEST_FILE_NAME);
                        }
                        if (file.canRead()) {
                            FileInputStream fis = new FileInputStream(file);
                            manifest = new Manifest(fis);
                        } else {
                            String msg =
                                    "Could not get development manifest file for class '" +
                                            clss + "'.  Try building the project with ant to create a local copy of" +
                                            "the manifest file.";
                            throw new UMROException(msg);
                        }
                    }
                }
            } catch (IOException ex) {
                throw new UMROException("Could not get manifest for class '" + clss.getName() + "' : " + ex);
            }
            // System.out.println(toString);  for debug only
        }
        return manifest;
    }


    /**
     * Show all of the manifest information.
     *
     * @throws UMROException
     */
    public String toString() {
        try {
            getManifest();
        } catch (UMROException ex) {
            return ex.toString();
        }
        StringBuffer text = new StringBuffer();
        Map<String, Attributes> entries = manifest.getEntries();
        Set<String> set = entries.keySet();
        Iterator<String> iter = set.iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            Attributes attributes = (Attributes) entries.get(name);
            text.append("Name: " + name);
            Set<Object> keyList = attributes.keySet();
            for (Object key : keyList) {
                Object value = attributes.get(key);
                text.append("\n    key: " + key + "    value: " + value);
            }
        }
        return text.toString();
    }


    /**
     * Get the name of the package associated with a class in the
     * slash separated form (as opposed to the dot separated form).
     *
     * @param clss The class whose package we are interested in.
     * @return Package name of class (slash separated, not . separated).
     */
    private static String getPackageNameOfClass(Class<?> clss) {
        Package pkg = clss.getPackage();
        String packageName = pkg.getName().replace('.', '/');
        return packageName;
    }


    /**
     * Show version related information.
     *
     * @param version Object pointing to version data.
     */
    protected static void show(JarInfo version) {
        try {

            System.out.println("Jar file: " + version.getFullJarFilePath());
            Map<String, Attributes> entries = version.getManifest().getEntries();
            Set<String> set = entries.keySet();
            Iterator<String> iter = set.iterator();
            while (iter.hasNext()) {
                String pkey = (String) iter.next();
                Attributes attributes = (Attributes) entries.get(pkey);
                {
                    System.out.println("\nPackage: " + pkey);
                    Set<Object> keyList = attributes.keySet();
                    int max = 0;
                    for (Object key : keyList) {
                        int len = key.toString().length();
                        max = (len > max) ? len : max;
                    }
                    for (Object key : keyList) {
                        String value = attributes.get(key).toString();
                        String sKey = key.toString();
                        while (sKey.length() < max) {
                            sKey += " ";
                        }
                        System.out.println("    " + sKey + " : " + value);
                    }
                }
            }

            System.out.println("\nJar Properties");
            System.out.println("    Main-Class                 : " + version.getMainClass());
            System.out.println("    Build Date                 : " + version.getBuildDate());
            System.out.println("    Built By                   : " + version.getBuiltBy());
            System.out.println("    Jar Name                   : " + version.getJarName());
            System.out.println("    Implementation-UtilVersion : " + version.getImplementationVersion());
            System.out.println("    Jar Desc                   : " + version.getJarDescription());
            System.out.println("    App Name                   : " + version.getApplicationName());
            System.out.println("    App UtilVersion            : " + version.getApplicationVersion());
            System.out.println("    App Desc                   : " + version.getApplicationDescription());
            System.out.println("    Full Jar File Path         : " + version.getFullJarFilePath());
        } catch (Exception ex) {
            System.err.println("Unexpected exception from Version: " + ex);
            ex.printStackTrace();
        }
    }


    /**
     * Provide a command line interface for getting application
     * information.
     * <p>
     * Use the '-help' option for help.
     *
     * @param args
     */
    public static void main(String[] args) {
        JarInfo xversion = new JarInfo(JarInfo.class);
        show(xversion);
    }

}
