package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface NonBlockingWritable {

    boolean onChannelWriteable(SelectionKey selectionKey) throws IOException;

}
