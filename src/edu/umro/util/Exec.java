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

import java.util.*;

/**
 * Utilities having to do with program execution and
 * operating system functions.
 *
 * @author Jim Irrer  irrer@umich.edu
 */
public class Exec {

    /**
     * Sleep for the given amount of time.  If the sleep was
     * interrupted, then do another sleep for the remaining time.
     *
     * @param ms Time to sleep in milliseconds.
     */
    public static void sleep(long ms) {

        Date now = new Date();
        long finish = now.getTime() + ms;
        long remaining = finish - (now.getTime());

        while (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException ex) {
                // do nothing
            }
            now = new Date();
            remaining = finish - (now.getTime());
        }
    }


    /**
     * Sleep for the given amount of time.
     *
     * @param ms Time to sleep in milliseconds.
     */
    public static void sleep(int ms) {
        sleep((long) ms);
    }


    /**
     * Return the current time in milliseconds.
     *
     * @return The current time in milliseconds.
     */
    public static long now() {
        return new Date().getTime();
    }

}
