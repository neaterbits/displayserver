package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class Function extends ByteEnum {

    public static final int CLEAR       = 0;
    public static final int AND         = 1;
    public static final int AND_REVERSE = 2;
    public static final int COPY        = 3;
    public static final int AND_INVERTED = 4;
    public static final int NO_OP       = 5;
    public static final int XOR         = 6;
    public static final int OR          = 7;
    public static final int NOR         = 8;
    public static final int EQUIV       = 9;
    public static final int INVERT      = 10;
    public static final int OR_REVERSE  = 11;
    public static final int COPY_INVERTED = 12;
    public static final int OR_INVERTED = 13;
    public static final int NAND        = 14;
    public static final int SET         = 15;
    
    public static final BYTE Clear      = make(CLEAR);
    public static final BYTE And        = make(AND);
    public static final BYTE AndReverse = make(AND_REVERSE);
    public static final BYTE Copy       = make(COPY);
    public static final BYTE AndInverted = make(AND_INVERTED);
    public static final BYTE NoOp       = make(NO_OP);
    public static final BYTE Xor        = make(XOR);
    public static final BYTE Or         = make(OR);
    public static final BYTE Nor        = make(NOR);
    public static final BYTE Equiv      = make(EQUIV);
    public static final BYTE Invert     = make(INVERT);
    public static final BYTE OrReverse  = make(OR_REVERSE);
    public static final BYTE CopyInverted = make(COPY_INVERTED);
    public static final BYTE OrInverted = make(OR_INVERTED);
    public static final BYTE Nand       = make(NAND);
    public static final BYTE Set        = make(SET);
}
