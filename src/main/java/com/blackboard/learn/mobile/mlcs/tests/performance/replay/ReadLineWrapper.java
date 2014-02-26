package com.blackboard.learn.mobile.mlcs.tests.performance.replay;

import java.io.LineNumberReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * This class wraps readline files for use across multiple threads
 * 
 * It does this by mainating a list of open files keyed by file name. 
 * @author molague
 *
 */


public final class ReadLineWrapper implements Closeable{
	
	public static ReadLineWrapper INSTANCE = new ReadLineWrapper();
	
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	private static final Map<String, LineNumberReader> fileReaders = new HashMap<String, LineNumberReader>();
	
	public synchronized Map<String, LineNumberReader> getFileReaders(){
		return fileReaders;
	}
	
	public synchronized void put(String fileName){
		try{
			fileReaders.put(fileName, new LineNumberReader(new FileReader(fileName)));
		}
		catch(FileNotFoundException f){
			log.error("File "+ fileName + " could not be opened");
		}
	}
	
	public void close(String fileName) throws IOException
	{
		LineNumberReader ln;
        synchronized (fileReaders) {
            ln = fileReaders.remove(fileName);
        }
        if (ln != null) {
            synchronized (ln) {
                ln.close();
            }
        }
        }
	
	public void close()
	{
		if (!fileReaders.isEmpty())
		{
			synchronized(fileReaders)
			{
				for(LineNumberReader ln: fileReaders.values())
				{
					try{
						ln.close();
					}
					catch(IOException ioe)
					{
						log.warn("Cannot close file" + ln.toString());
					}
				}
			}
			fileReaders.clear();
		}
	}
}
