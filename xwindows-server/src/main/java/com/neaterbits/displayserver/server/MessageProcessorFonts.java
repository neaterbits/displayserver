package com.neaterbits.displayserver.server;

import java.util.List;

import com.neaterbits.displayserver.protocol.messages.replies.legacy.CHARINFO;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.FONTPROP;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.QueryFontReply;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryFont;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.xwindows.fonts.XFont;
import com.neaterbits.displayserver.xwindows.fonts.XFontCharacter;

final class MessageProcessorFonts {

    static void queryFont(QueryFont queryFont, CARD16 sequenceNumber, XClient client, XFont font, ServerToClient serverToClient) {
        
        final boolean allCharsExist = font.getMetrics().stream()
                .allMatch(fontCharacter -> fontCharacter.isNonZero());
        
        final QueryFontReply reply = new QueryFontReply(
                sequenceNumber,
                
                charInfo(font.getAccelerators().getMinbounds()),
                charInfo(font.getAccelerators().getMaxbounds()),
                
                new CARD16(font.getEncodings().getMinCharOrByte2()),
                new CARD16(font.getEncodings().getMaxCharOrByte2()),
                
                new CARD16(font.getEncodings().getDefaultChar()),
                
                // new CARD16(font.getProperties().size()),
                new CARD16(0),
                
                new BYTE((byte)font.getAccelerators().getDrawDirection()),
                
                new CARD8(font.getEncodings().getMinByte1()),
                new CARD8(font.getEncodings().getMaxByte1()),
                
                new BOOL(allCharsExist),

                new INT16((short)font.getAccelerators().getFontAscent()),
                new INT16((short)font.getAccelerators().getFontDescent()),

                new CARD32(font.getMetrics().size()),
                new FONTPROP[0],
                getCharInfos(font.getMetrics()));

                

       serverToClient.sendReply(client, reply);
    }
    
    private static CHARINFO [] getCharInfos(List<XFontCharacter> metrics) {
        
        final CHARINFO [] dst = new CHARINFO[metrics.size()];
        
        for (int i = 0; i < metrics.size(); ++ i) {
            dst[i] = charInfo(metrics.get(i));
        }
    
        return dst;
    }
    
    private static CHARINFO charInfo(XFontCharacter fontCharacter) {
        return new CHARINFO(
                new INT16(fontCharacter.getLeftSideBearing()),
                new INT16(fontCharacter.getRigthSideBearing()),
                
                new INT16(fontCharacter.getCharacterWidth()),
                
                new INT16(fontCharacter.getAscent()), new INT16(fontCharacter.getDescent()),
                
                new CARD16(fontCharacter.getAttributes()));
    }
    
}
