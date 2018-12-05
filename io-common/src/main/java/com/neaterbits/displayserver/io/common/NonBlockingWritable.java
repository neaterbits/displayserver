package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface NonBlockingWritable {

    void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException;

}
