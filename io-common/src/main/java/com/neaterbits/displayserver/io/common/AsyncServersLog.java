package com.neaterbits.displayserver.io.common;

import java.nio.channels.SelectionKey;

public interface AsyncServersLog {

    void onSelectUpdated(int numChannels);
    
    void onAccept(SelectionKey key);
    
}
