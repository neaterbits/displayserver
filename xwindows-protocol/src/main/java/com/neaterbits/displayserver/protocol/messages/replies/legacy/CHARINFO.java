package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;

public final class CHARINFO extends Encodeable {

    private final INT16 leftSideBearing;
    private final INT16 rigthSideBearing;
    private final INT16 characterWidth;
    private final INT16 ascent;
    private final INT16 descent;

    private final CARD16 attributes;

    public CHARINFO(INT16 leftSideBearing, INT16 rigthSideBearing, INT16 characterWidth, INT16 ascent, INT16 descent,
            CARD16 attributes) {

        Objects.requireNonNull(leftSideBearing);
        Objects.requireNonNull(rigthSideBearing);
        Objects.requireNonNull(characterWidth);
        Objects.requireNonNull(ascent);
        Objects.requireNonNull(descent);
        Objects.requireNonNull(attributes);

        this.leftSideBearing = leftSideBearing;
        this.rigthSideBearing = rigthSideBearing;
        this.characterWidth = characterWidth;
        this.ascent = ascent;
        this.descent = descent;
        this.attributes = attributes;
    }

    public INT16 getLeftSideBearing() {
        return leftSideBearing;
    }

    public INT16 getRigthSideBearing() {
        return rigthSideBearing;
    }

    public INT16 getCharacterWidth() {
        return characterWidth;
    }

    public INT16 getAscent() {
        return ascent;
    }

    public INT16 getDescent() {
        return descent;
    }

    public CARD16 getAttributes() {
        return attributes;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "leftSideBearing", leftSideBearing,
                "rigthSideBearing", rigthSideBearing,
                "characterWidth", characterWidth,
                "ascent", ascent,
                "descent", descent,
                "attributes", attributes
                
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        stream.writeINT16(leftSideBearing);
        stream.writeINT16(rigthSideBearing);
        stream.writeINT16(characterWidth);
        stream.writeINT16(ascent);
        stream.writeINT16(descent);
        
        stream.writeCARD16(attributes);
    }
}
