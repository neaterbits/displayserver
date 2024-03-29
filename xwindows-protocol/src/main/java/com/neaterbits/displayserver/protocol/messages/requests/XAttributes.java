package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.FieldReader;
import com.neaterbits.displayserver.protocol.FieldWriter;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;
import com.neaterbits.displayserver.protocol.types.BITMASK;

public abstract class XAttributes extends XEncodeable {

    private final BITMASK valueMask;

    public XAttributes(BITMASK valueMask) {
        
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
        return valueMask.isSet(flag);
    }
    
    protected final <T> void writeIfSet(T value, int flag, FieldWriter<T> writer) throws IOException {
        
        if (isSet(flag)) {
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
        
        if (bitmask.isSet(flag)) {
            value = reader.read();
        }
        else {
            value = null;
        }
        
        return value;
    }

    protected static <T> T returnIfSet(BITMASK existingBitmask, BITMASK applyBitmask, int flag, T existing, T toApply) {
        
        final T result;

        if (applyBitmask.isSet(flag)) {
            result = toApply;
        }
        else if (existingBitmask.isSet(flag)) {
            result = existing;
        }
        else {
            result = null;
        }
        
        return result;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBITMASK(valueMask);
    }
    
    protected final void encodeBITMASK16(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBITMASK16(valueMask);
    }
}
