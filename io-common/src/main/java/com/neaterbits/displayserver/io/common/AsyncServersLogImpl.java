package com.neaterbits.displayserver.io.common;

import java.nio.channels.SelectionKey;

import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public final class AsyncServersLogImpl extends BaseLogImpl implements AsyncServersLog {

    public AsyncServersLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public void onSelectUpdated(int numChannels) {
        trace("selectUpdated", "numChannels", numChannels);
    }

    @Override
    public void onAccept(SelectionKey key) {
        debug("accept", "key", key);
    }
}
