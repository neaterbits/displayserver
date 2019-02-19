package com.neaterbits.displayserver.layers;

enum Pos {

    BEFORE,
    AT_START,
    WITHIN,
    AT_END,
    AFTER;

    static Pos getHPos(int left, int width, int x) {
        return getPos(left, width, x);
    }

    static Pos getVPos(int top, int height, int y) {
        return getPos(top, height, y);
    }

    private static Pos getPos(int start, int pixels, int pt) {
        
        final Pos pos;
        
        if (pt < start) {
            pos = Pos.BEFORE;
        }
        else if (pt >= start + pixels) {
            pos = Pos.AFTER;
        }
        else if (pt > start && pt < start + pixels - 1) {
            pos = Pos.WITHIN;
        }
        else if (pt == start) {
            pos = Pos.AT_START;
        }
        else if (pt == start + pixels - 1) {
            pos = Pos.AT_END;
        }
        else {
            throw new IllegalStateException();
        }
        
        return pos;
    }
}
