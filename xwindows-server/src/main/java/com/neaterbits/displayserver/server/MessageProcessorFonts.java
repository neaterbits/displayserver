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
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontModel;

final class MessageProcessorFonts {

    static void queryFont(QueryFont queryFont, CARD16 sequenceNumber, XClient client, XFont font, ServerToClient serverToClient) {
        
        final XFontModel fontModel = font.getModel();
        
        final boolean allCharsExist = fontModel.getMetrics().stream()
                .allMatch(fontCharacter -> fontCharacter.isNonZero());
        
        final QueryFontReply reply = new QueryFontReply(
                sequenceNumber,
                
                charInfo(fontModel.getAccelerators().getMinbounds()),
                charInfo(fontModel.getAccelerators().getMaxbounds()),
                
                new CARD16(fontModel.getEncodings().getMinCharOrByte2()),
                new CARD16(fontModel.getEncodings().getMaxCharOrByte2()),
                
                new CARD16(fontModel.getEncodings().getDefaultChar()),
                
                // new CARD16(font.getProperties().size()),
                new CARD16(0),
                
                new BYTE((byte)fontModel.getAccelerators().getDrawDirection()),
                
                new CARD8(fontModel.getEncodings().getMinByte1()),
                new CARD8(fontModel.getEncodings().getMaxByte1()),
                
                new BOOL(allCharsExist),

                new INT16((short)fontModel.getAccelerators().getFontAscent()),
                new INT16((short)fontModel.getAccelerators().getFontDescent()),

                new CARD32(fontModel.getMetrics().size()),
                new FONTPROP[0],
                getCharInfos(fontModel.getMetrics()));

                

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
