package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public abstract class PolyRequest<T extends Encodeable> extends DrawRequest {

    private final T [] list;
    
    public interface CreateArray<T extends Encodeable> {
        T [] create(int length);
    }
    
    @FunctionalInterface
    public interface CreatePolyRequest<T extends Encodeable, REQUEST extends PolyRequest<T>> {

        REQUEST create(
                BYTE initialByte,
                DRAWABLE drawable,
                GCONTEXT gc,
                T [] list);

    }

    @FunctionalInterface
    public interface CreatePolyRequestNoInitialByte<T extends Encodeable, REQUEST extends PolyRequest<T>> {

        REQUEST create(
                DRAWABLE drawable,
                GCONTEXT gc,
                T [] list);

    }

    protected static <T extends Encodeable, REQUEST extends PolyRequest<T>> 
    REQUEST decodeNoInitialByte(
            XWindowsProtocolInputStream stream,
            int card32PerEntry,
            CreateArray<T> createArray,
            DecodeArrayElement<T> decodeArrayElement,
            CreatePolyRequestNoInitialByte<T, REQUEST> createPolyRequest) throws IOException {
        
        return decode(
                stream,
                card32PerEntry,
                createArray,
                decodeArrayElement,
                (BYTE initialByte, DRAWABLE drawable, GCONTEXT gc, T [] list) -> createPolyRequest.create(drawable, gc, list));
    }

    protected static <T extends Encodeable, REQUEST extends PolyRequest<T>> 
    REQUEST decode(
            XWindowsProtocolInputStream stream,
            int card32PerEntry,
            CreateArray<T> createArray,
            DecodeArrayElement<T> decodeArrayElement,
            CreatePolyRequest<T, REQUEST> createPolyRequest) throws IOException {
        
        final BYTE initialByte = stream.readBYTE();
        
        final CARD16 requestLength = stream.readCARD16();
        
        final int numPoints = (requestLength.getValue() - 3) / card32PerEntry;
        
        final DRAWABLE drawable = stream.readDRAWABLE();
        final GCONTEXT gc = stream.readGCONTEXT();
        
        final T [] list = createArray.create(numPoints);

        decodeArray(stream, list, decodeArrayElement);
        
        return createPolyRequest.create(initialByte, drawable, gc, list);
    }
    
    
    public PolyRequest(DRAWABLE drawable, GCONTEXT gc, T [] list) {
        super(drawable, gc);
    
        Objects.requireNonNull(list);
        
        this.list = list;
    }

    final T [] getList() {
        return list;
    }
    
    final String getListDebugParam() {
        return Arrays.toString(list);
    }

    final void encode(XWindowsProtocolOutputStream stream, int card32PerEntry) throws IOException {
        encode(stream, null, card32PerEntry);
    }
    
    final void encode(XWindowsProtocolOutputStream stream, BYTE initialByte, int card32PerEntry) throws IOException {

        writeOpCode(stream);

        if (initialByte == null) {
            writeUnusedByte(stream);
        }
        else {
            stream.writeBYTE(initialByte);
        }
        
        writeRequestLength(stream, 3 + (list.length * card32PerEntry));
        
        stream.writeDRAWABLE(getDrawable());
        stream.writeGCONTEXT(getGC());

        encodeArray(list, stream);
    }
}
