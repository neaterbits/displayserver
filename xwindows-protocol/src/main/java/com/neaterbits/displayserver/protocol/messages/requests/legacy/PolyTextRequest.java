package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.TEXTITEM;

public abstract class PolyTextRequest<STRING, ITEM extends TEXTITEM<STRING, ITEM>> extends DrawRequest {

    private final INT16 x;
    private final INT16 y;
    
    private final ITEM [] items;


    @FunctionalInterface
    interface DecodeItem<ITEM> {
        ITEM decode(XWindowsProtocolInputStream stream) throws IOException;
    }
    
    @FunctionalInterface
    interface CreateRequest<ITEM, REQUEST> {
        REQUEST create(DRAWABLE drawable, GCONTEXT gc, INT16 x, INT16 y, Collection<ITEM> items);
    }
    
    static <
            STRING,
            ITEM extends TEXTITEM<STRING, ITEM>,
            REQUEST extends PolyTextRequest<STRING, ITEM>>
    
    REQUEST decode(
            XWindowsProtocolInputStream stream,
            DecodeItem<ITEM> decodeItem,
            CreateRequest<ITEM, REQUEST> createRequest) throws IOException {
    
        readUnusedByte(stream);
        
        final CARD16 requestLength = stream.readCARD16();
        
        final DRAWABLE drawable = stream.readDRAWABLE();
        final GCONTEXT gc = stream.readGCONTEXT();
        
        final INT16 x = stream.readINT16();
        final INT16 y = stream.readINT16();
        
        int length = (requestLength.getValue() - 4) * 4;
        
        final List<ITEM> items = new ArrayList<>();
        
        while (length > 3) {
            final ITEM item = decodeItem.decode(stream);
    
            System.out.println("## decoded string of length " + item.getNumberOfEncodedBytes() + " from length " + length + " " + item.toDebugString());
            
            length -= item.getNumberOfEncodedBytes();
            
            items.add(item);
        }
        
        stream.readPad(length);
        
        return createRequest.create(drawable, gc, x, y, items);
    }
    
    PolyTextRequest(DRAWABLE drawable, GCONTEXT gc, INT16 x, INT16 y, ITEM[] items) {
        super(drawable, gc);

        this.x = x;
        this.y = y;
        this.items = items;
    }
    
    public final INT16 getX() {
        return x;
    }

    public final INT16 getY() {
        return y;
    }

    public final ITEM[] getItems() {
        return items;
    }

    @Override
    public final Object[] getDebugParams() {
        return wrap(
                "drawable", getDrawable(),
                "gc", getGC(),
                
                "x", x,
                "y", y,
                "items", outputArrayInBrackets(items)
        );
    }

    @Override
    public final void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        final int length = getLength(items);
        
        final int pad = XWindowsProtocolUtil.getPadding(length);
        
        writeRequestLength(stream, length);

        encodeArray(items, stream);
        
        stream.pad(pad);
    }

    @Override
    public final Class<? extends XReply> getReplyClass() {
        return null;
    }

    private int getLength(ITEM [] items) {
        
        int length = 0;
        
        for (ITEM item : items) {
            length += item.getNumberOfEncodedBytes();
        }

        return length;
    }
}
