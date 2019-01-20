package com.neaterbits.displayserver.xwindows.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.CURSOR;

public class XCursors implements XCursorsConstAccess {

    private final Map<CURSOR, XCursor> cursors;

    public XCursors() {
        this.cursors = new HashMap<>();
    }
    
    public void add(CURSOR resource, XCursor cursor) {

        Objects.requireNonNull(resource);
        Objects.requireNonNull(cursor);
    
        cursors.put(resource, cursor);
    }
    
    @Override
    public boolean hasCursor(CURSOR resource) {
        
        Objects.requireNonNull(resource);

        return cursors.containsKey(resource);
    }

    @Override
    public XCursor getCursor(CURSOR resource) {

        Objects.requireNonNull(resource);

        return cursors.get(resource);
    }
    
    public boolean remove(CURSOR resource) {
        
        Objects.requireNonNull(resource);
        
        return cursors.remove(resource) != null;
    }
}
