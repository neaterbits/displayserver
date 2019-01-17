package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.types.TIMESTAMP;

public interface XTimestampGenerator {

    TIMESTAMP getTimestamp();
    
}
