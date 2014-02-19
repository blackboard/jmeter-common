package com.blackboard.learn.mobile.mlcs.tests.performance.replay;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PersistentFileReader implements Closeable {
    
    public static final PersistentFileReader INSTANCE = new PersistentFileReader();
    
    private Map<String, BufferedReader> fileReaders = new HashMap<String, BufferedReader>();
    
    private PersistentFileReader() { }
    
    public void open(final String filename) throws IOException {

        synchronized (fileReaders) {
            if (fileReaders.containsKey(filename)) {
                throw new IOException("File " + filename + " is already open");
            }

            File f = new File(filename);
            if (!f.exists()) {
                throw new FileNotFoundException(f.getCanonicalPath());
            }
            if (!f.canRead()) {
                throw new IOException("Cannot read from " + f.getCanonicalPath());
            }
            BufferedReader br = new BufferedReader(new FileReader(f));
            fileReaders.put(filename, br);
        }
    }
    
    public String readLine(final String filename) throws IOException {

        BufferedReader br;
        synchronized (fileReaders) {
            br = fileReaders.get(filename);
        }
        if (br == null) {
            throw new FileNotFoundException(filename);
        }
        synchronized (br) {
            try {
                return br.readLine();
            } catch (Throwable t) {
                return null;
            }
        }
    }
    
    public Map<String, BufferedReader> getFileReaders()
    {
    	return fileReaders;
    }
    
    public void close(final String filename) throws IOException {
        
        BufferedReader br;
        synchronized (fileReaders) {
            br = fileReaders.remove(filename);
        }
        if (br != null) {
            synchronized (br) {
                br.close();
            }
        }
    }

    @Override
    public void close() throws IOException {
        
        synchronized (fileReaders) {
            for (BufferedReader br: fileReaders.values()) {
                if (br != null) {
                    synchronized (br) {
                        br.close();
                    }
                }
            }
            fileReaders.clear();
        }
    }
}
