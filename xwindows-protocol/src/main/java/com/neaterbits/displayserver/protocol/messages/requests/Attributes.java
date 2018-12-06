package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.FieldReader;
import com.neaterbits.displayserver.protocol.FieldWriter;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BITMASK;

public abstract class Attributes {

    private final BITMASK valueMask;

    public Attributes(BITMASK valueMask) {
        
        Objects.requireNonNull(valueMask);
        
        this.valueMask = valueMask;
    }
    
    public final int getCount() {
        
        int numBits = 0;
        
        for (int i = 0; i < 32; ++ i) {
            if (isSet(1 << i)) {
                ++ numBits;
            }
        }
        
        return numBits;
    }
    
    public final BITMASK getValueMask() {
        return valueMask;
    }
    
    public final boolean isSet(int flag) {
        return isSet(valueMask, flag);
    }
    
    private static boolean isSet(BITMASK valueMask, int flag) {
        return (valueMask.getValue() & flag) != 0;
    }

    protected final <T> void writeIfSet(T value, int flag, FieldWriter<T> writer) throws IOException {
        if (isSet(flag)) {
            
            System.out.println("## writeIfSet");
            
            writer.write(value);
        }
    }

    protected static <T> T readIfSet(BITMASK bitmask, int flag, FieldReader<T> reader) throws IOException {
        
        final T value;
        
        if (isSet(bitmask, flag)) {
            value = reader.read();
        }
        else {
            value = null;
        }
        
        return value;
    }

    void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBITMASK(valueMask);
    }
}
