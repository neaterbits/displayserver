package com.neaterbits.displayserver.protocol;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnsignedTest {

    @Test
    public void tesUnsignedByte() {

        for (int i = 0; i <= 255; ++ i) {
            
            final byte byteValue = (byte)i;
            
            assertThat(Unsigned.byteToUnsigned(byteValue)).isEqualTo(i);
        }
    }

    @Test
    public void tesUnsignedShort() {

        for (int i = 0; i <= 65535; ++ i) {
            
            final short shortValue = (short)i;
            
            assertThat(Unsigned.shortToUnsigned(shortValue)).isEqualTo(i);
        }
    }

    @Test
    public void tesUnsignedInt() {

        for (long i = 0; i < (1 << 32); ++ i) {
            
            final int intValue = (int)i;
            
            assertThat(Unsigned.intToUnsigned(intValue)).isEqualTo(i);
        }
    }
}
