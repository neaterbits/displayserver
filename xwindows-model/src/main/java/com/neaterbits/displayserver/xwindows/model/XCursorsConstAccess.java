package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.CURSOR;

public interface XCursorsConstAccess {

    boolean hasCursor(CURSOR resource);

    XCursor getCursor(CURSOR resource);
}
