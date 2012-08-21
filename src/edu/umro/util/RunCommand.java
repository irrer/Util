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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Static class to run a command from the system.
 * 
 * @author Dale White  dawh@umich.edu
 */
public class RunCommand 
{
	/**
	 * Run the input command using the Runtime process and return the program's exit value.
	 * 
	 * @param command - The system command to be run by the process
	 * @return Exit value of the program executed
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int run(String command) throws IOException, InterruptedException {
		Runtime r = Runtime.getRuntime();
				
		Process p = r.exec(command);
		//Ensure that the process finishes by getting output streams
		StreamGobbler errGobb = new StreamGobbler(p.getErrorStream(), "ERROR");
		StreamGobbler outGobb = new StreamGobbler(p.getInputStream(), "OUTPUT");
		errGobb.start();
		outGobb.start();
		int exitVal = p.waitFor();
		return(exitVal);
	}
	
    public static void main( String[] args )
    {
        
    }
}

/**
 * Helper class to get the output stream from a subprocess.
 * 
 * @author dawh
 *
 */
class StreamGobbler extends Thread
{
	/** Data stream to be read by the Gobbler. */
    InputStream is;
    /** Identifier for the type of the data stream. */
    String type;
    
    /**
     * Set the internal variables for the gobbler.
     * 
     * @param is - input stream to be read
     * @param type - denotes the type of stream (err/out)
     */
    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    /**
     * Reads the StreamGobbler's input stream and prints its contents to System.out.
     */
    @Override
    public void run()
    {
    	//Try to read the stream and print each line to the System.out
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                System.out.println(type + ">" + line);    
            } catch (IOException ioe)
              {
                ioe.printStackTrace();  
              }
    }
}