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

import java.net.*;

/**
 * Provide support for operating system specific requirements.
 *
 * @author Jim Irrer  irrer@umich.edu
 */
public class OpSys {


    /**
     * List of operating systems that we might run on.
     */
    public static enum OpSysId {
        OPENVMS,
        WINDOWS,
        LINUX
    }

    /**
     * Once established, save the operating system id here.
     */
    private static OpSysId opSysId = null;

    private static String hostName = null;

    private static String hostIPAddress = null;

    private static String user = null;


    /**
     * Get the name of the currently running host.
     *
     * @return
     */
    public static String getHostName() {

        try {
            if (hostName == null) {
                java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
                hostName = localMachine.getHostName();
            }
        } catch (java.net.UnknownHostException ex) {
            return null;
        }
        return hostName;
    }


    /**
     * Get the IP address of the currently running host.
     *
     * @return The IP address of the currently running host.
     */
    public static String getHostIPAddress() {

        try {
            if (hostIPAddress == null) {
                java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
                hostIPAddress = localMachine.getHostAddress();
            }
        } catch (java.net.UnknownHostException ex) {
            return null;
        }
        return hostIPAddress;
    }


    /**
     * Get the id of the user currently running this program.
     *
     * @return The user's id.
     */
    public static String getUser() {
        if (user == null) {
            user = System.getProperty("user.name");
        }
        return user;
    }


    public static void main(String[] args) {
        System.out.println("getHostName: " + getHostName());
        System.out.println("getHostIPAddress: " + getHostIPAddress());
        System.out.println("getUser: " + getUser());
    }


    public static OpSysId StringToOpSys(String name) {
        name = name.toUpperCase();
        for (OpSysId os : OpSysId.values()) {
            if (name.contains(os.toString().toUpperCase())) {
                return opSysId = os;
            }
        }
        return null;
    }

    /**
     * Determine which operating system we are running on.
     *
     * @return ID of operating system.
     */
    public static OpSysId getOpSysId() {
        if (opSysId == null) {
            opSysId = StringToOpSys(System.getProperty("os.name"));
        }
        return opSysId;
    }


    /**
     * Save MAC address here for efficiency.
     */
    private static long macAddress = 0;


    /**
     * Get the MAC address of this host.
     *
     * @return The MAC address.
     * @throws UnknownHostException
     * @throws SocketException
     */
    public static long getMACAddress() throws UnknownHostException, SocketException {
        if (macAddress == 0) {
            InetAddress localMachine = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localMachine);
            byte[] hwAddr = networkInterface.getHardwareAddress();
            if (hwAddr == null) throw new SocketException("Insufficient privilges to get MAC address");
            for (byte b : hwAddr) {
                macAddress = (macAddress << 8) + ((int) b);
            }
        }
        return macAddress;
    }

}