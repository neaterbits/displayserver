package com.neaterbits.displayserver.events.common;

import java.util.Objects;

public final class KeyboardMapping {

    private final int minScancode;
    private final int maxScancode;
    
    private int [][] scancodeToKeysym;

    public KeyboardMapping(int minScancode, int maxScancode, int[][] scancodeToKeysym) {
        
        Objects.requireNonNull(scancodeToKeysym);
        
        this.minScancode = minScancode;
        this.maxScancode = maxScancode;
        
        if (maxScancode - minScancode + 1 != scancodeToKeysym.length) {
            throw new IllegalArgumentException();
        }
        
        this.scancodeToKeysym = scancodeToKeysym;
    }

    
    public int getMinScancode() {
        return minScancode;
    }

    public int getMaxScancode() {
        return maxScancode;
    }
    
    public int[][] getScancodeToKeysym() {
        return scancodeToKeysym;
    }

    public void setScancodeToKeysym(int[][] scancodeToKeysym) {
        this.scancodeToKeysym = scancodeToKeysym;
    }

    public int getNumScancodes() {
        return scancodeToKeysym.length;
    }
    
    public int [] getKeysyms(int scancode) {
        return scancodeToKeysym[scancode];
    }
}
