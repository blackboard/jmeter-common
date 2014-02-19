package com.blackboard.learn.mobile.mlcs.tests.performance.replay;

import java.util.HashMap;
import java.util.Map;

public class GlobalContext {
    
    public static final GlobalContext INSTANCE = new GlobalContext();
    
    private Map<String, Object> context = new HashMap<String, Object>();
    
    private GlobalContext() { }
        
    public synchronized void putObject(final String key, Object value) {
        context.put(key, value);
    }
    
    public synchronized Object getObject(final String key) {
        return context.get(key);
    }
    
    public synchronized void clear() {
        context.clear();
    }
}
