package com.xtra.core;

public class Core {
    
    private static Object plugin;
    
    public static void initialize(Object plugin) {
        Core.plugin = plugin;
    }
    
    public static Object plugin() {
        return plugin;
    }
}
