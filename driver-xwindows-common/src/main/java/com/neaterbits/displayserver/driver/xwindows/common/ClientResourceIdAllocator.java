package com.neaterbits.displayserver.driver.xwindows.common;

import java.util.BitSet;

public final class ClientResourceIdAllocator {

    private final long resourceBase;
    private final long resourceMask;
    private final int resourceShift;
    
    private final BitSet allocatedResources;
    
    public ClientResourceIdAllocator(long resourceBase, long resourceMask) {
    
        this.resourceBase = resourceBase;
        this.resourceMask = resourceMask;
        
        int resourceShift = -1;
        
        for (int i = 0; i < 32; ++ i) {
            if ((resourceMask & (1 << i)) != 0) {
                resourceShift = i;
                break;
            }
        }
        
        if (resourceShift == -1) {
            throw new IllegalStateException();
        }
        
        this.resourceShift = resourceShift;
        
        int numResourceBits = 1;
        
        for (int i = resourceShift + 1; i < 32; ++ i) {
            if ((resourceMask & (1 << i)) == 0) {
                break;
            }
            
            ++ numResourceBits;
        }
        
        if (numResourceBits < 18) {
            throw new IllegalArgumentException("");
        }
        
        this.allocatedResources = new BitSet(4);
    }
    
    public int allocateResourceId() {
        final int bit = allocatedResources.nextClearBit(0);
        
        allocatedResources.set(bit);
        
        return (int)(bit << resourceShift | resourceBase);
        
    }
    
    public void freeResourceId(long resourceId) {
        final int bit = (int)((resourceId & ~resourceMask) >> resourceShift);
        
        if (!allocatedResources.get(bit)) {
            throw new IllegalStateException();
        }
        
        allocatedResources.clear(bit);
    }
}
