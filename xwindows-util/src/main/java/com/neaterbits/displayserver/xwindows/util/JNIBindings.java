package com.neaterbits.displayserver.xwindows.util;

public class JNIBindings {

    public static void load() {
        final String rootDir = System.getenv("HOME") + "/projects/displayserver";
        
        System.load(rootDir + "/native-xcb/Debug/libxcbjni.so");
        System.load(rootDir + "/native-cairo/Debug/libcairojni.so");
        
    }
}
