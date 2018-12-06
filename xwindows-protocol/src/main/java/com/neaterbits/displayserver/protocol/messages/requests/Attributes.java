package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.FieldReader;
import com.neaterbits.displayserver.protocol.FieldWriter;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.BITMASK;

public abstract class Attributes extends Encodeable {

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

    protected final <T> void addIfSet(List<Object> params, String name, Object value, int flag) {
        
        if (isSet(flag)) {
            params.add(name);
            params.add(value);
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

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBITMASK(valueMask);
    }
}
