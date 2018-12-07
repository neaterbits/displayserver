package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class Errors {

    private static BYTE make(int error) {
        return new BYTE((byte)error);
    }

    public static final int VALUE = 2;
    public static final int WINDOW = 3;
    public static final int ATOM = 5;
    public static final int MATCH = 8;
    public static final int IDCHOICE = 14;

    public static final BYTE Value = make(VALUE);
    public static final BYTE Window = make(WINDOW);
    public static final BYTE Atom = make(ATOM);
    public static final BYTE Match = make(MATCH);
    public static final BYTE IDChoice = make(IDCHOICE);
}
