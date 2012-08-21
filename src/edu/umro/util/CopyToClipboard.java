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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;


/**
 * Copy text to the clipboard so that the
 * user may paste it into another application. 
 * 
 * @author Jim Irrer  irrer@umich.edu 
 *
 */

class CopyToClipboard {
    private static class CopyToClip implements ClipboardOwner {
        /**
         * Copy the text to the clipboard so that the
         * user may paste it into another application.
         * 
         * @param text Text to copy.
         */
        public CopyToClip(String text) {
            StringSelection stringSelection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }

        /**
         * Not used, only provided to adhere to the <code>ClipboardOwner</code> interface.
         */
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    }

    /**
     * Copy the text to the clipboard so that the
     * user may paste it into another application.
     * 
     * @param text Text to copy.
     */
    public static void copy(String text) {
        new CopyToClip(text);
    }
}