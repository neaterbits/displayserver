package com.neaterbits.displayserver.server;

import java.util.BitSet;
import java.util.Objects;

final class ServerResourceIdAllocator {

    private static final int SERVER_RESERVED = 1; // for root windows
    private static final int CONNECTION_BITS = 10;
    private static final int RESOURCE_BITS = 18;
    private static final int ZEROED_BITS = 3;
    
    static {
        if (!Objects.equals(SERVER_RESERVED + CONNECTION_BITS + RESOURCE_BITS + ZEROED_BITS, 32)) {
            throw new IllegalStateException();
        }
    }
    
    private int numRootWindows;
    
    private int numVisuals;
    
    private final BitSet connectionBitSet;

    ServerResourceIdAllocator() {
        this.numRootWindows = 0;
        
        this.numVisuals = 0;
        
        this.connectionBitSet = new BitSet(1 << CONNECTION_BITS);
    }
    
    int allocateConnection() {

        final int connectionNo = connectionBitSet.nextClearBit(0);
        
        if (connectionNo < 0) {
            throw new IllegalStateException();
        }
        
        connectionBitSet.set(connectionNo);
        
        return connectionNo;
    }

    void freeConnection(int connectionNo) {
        if (!connectionBitSet.get(connectionNo)) {
            throw new IllegalStateException();
        }
        
        connectionBitSet.set(connectionNo);
    }
    
    private static int getServerReservedFlag() {
        return (1 << SERVER_RESERVED);
    }

    int allocateRootWindowId() {
        return (numRootWindows ++) << SERVER_RESERVED | getServerReservedFlag();
    }
    
    int allocateVisualId() {
        return numVisuals ++;
    }
    
    boolean isRootWindow(int windowId) {
        return (windowId & getServerReservedFlag()) != 0;
    }
    
    long getResourceMask(int connectionNo) {
        
        final int baseBits = CONNECTION_BITS + SERVER_RESERVED;
        
        final long baseMask = (1 << baseBits) - 1;
        
        return ((1 << (RESOURCE_BITS + baseBits)) - 1) & ~baseMask;
    }
    
    long getResourceBase(int connectionNo) {
        return connectionNo << SERVER_RESERVED;
    }
}
