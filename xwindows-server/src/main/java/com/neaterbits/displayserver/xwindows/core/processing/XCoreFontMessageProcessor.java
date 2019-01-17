package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;
import java.util.List;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.FontException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.CHARINFO;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.FONTPROP;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.ListFontsReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.ListFontsWithInfoLastReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.ListFontsWithInfoReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.QueryFontReply;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.CloseFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.ListFonts;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.ListFontsWithInfo;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.OpenFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryFont;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.server.XFonts;
import com.neaterbits.displayserver.xwindows.fonts.FontDescriptor;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;
import com.neaterbits.displayserver.xwindows.fonts.NoSuchFontException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontModel;
import com.neaterbits.displayserver.xwindows.fonts.model.XNamedFontModel;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreFontMessageProcessor extends XOpCodeProcessor {

    private final XFonts xFonts;
    private final FontBufferFactory fontBufferFactory;
    
    public XCoreFontMessageProcessor(XWindowsServerProtocolLog protocolLog, FontLoaderConfig config,
            FontBufferFactory fontBufferFactory) {
        super(protocolLog);

        this.xFonts = new XFonts(config);
        this.fontBufferFactory = fontBufferFactory;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.OPEN_FONT,
                OpCodes.CLOSE_FONT,
                OpCodes.QUERY_FONT,
                OpCodes.LIST_FONTS,
                OpCodes.LIST_FONTS_WITH_INFO
        };
    }

    @Override
    protected void onMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException {

        switch (opcode) {

        case OpCodes.OPEN_FONT: {
            final OpenFont openFont = log(messageLength, opcode, sequenceNumber, OpenFont.decode(stream));
            
            try {
                
                final String fontName = openFont.getName().equals("fixed")
                        ? "7x13"
                        : openFont.getName();
                
                final XFont font = xFonts.openFont(fontName, fontBufferFactory);
                
                client.openFont(openFont, font);
                
            } catch (NoSuchFontException|ValueException ex) {
                sendError(client, Errors.Name, sequenceNumber, openFont.getFid().getValue(), opcode);
            } catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.CLOSE_FONT: {
            
            final CloseFont closeFont = log(messageLength, opcode, sequenceNumber, CloseFont.decode(stream));
            
            try {
                final XFont font = client.closeFont(closeFont);

                xFonts.closeFont(font);
            } catch (FontException ex) {
                sendError(client, Errors.Font, sequenceNumber, ex.getFont().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.QUERY_FONT: {
            final QueryFont queryFont = log(messageLength, opcode, sequenceNumber, QueryFont.decode(stream));
            
            try {
                final XFont font = client.queryFont(queryFont);
                
                final XFontModel fontModel = font.getModel();
                
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
                        
                        new BOOL(allCharsExist(fontModel)),

                        new INT16((short)fontModel.getAccelerators().getFontAscent()),
                        new INT16((short)fontModel.getAccelerators().getFontDescent()),

                        new CARD32(fontModel.getMetrics().size()),
                        new FONTPROP[0],
                        getCharInfos(fontModel.getMetrics()));

               sendReply(client, reply);
            } catch (FontException ex) {
                sendError(client, Errors.Font, sequenceNumber, ex.getFont().getValue(), opcode);
            }
            break;
        }

        case OpCodes.LIST_FONTS: {
            
            final ListFonts listFonts = log(messageLength, opcode, sequenceNumber, ListFonts.decode(stream));
            
            try {
                final FontDescriptor [] matches = xFonts.listFonts(listFonts.getPattern());
                
                final int numToSend = Math.min(matches.length, listFonts.getMaxNames().getValue());
                
                final String [] stringMatches = new String[numToSend];
                
                for (int i = 0; i < numToSend; ++ i) {
                    
                    final FontDescriptor fontDescriptor = matches[i];
                    
                    final String match;
                    
                    if (fontDescriptor.getXlfd() != null) {
                        match = fontDescriptor.getXlfd().asString();
                    }
                    else {
                        match = fontDescriptor.getFontName();
                    }
                    
                    stringMatches[i] = match;
                }
                
                sendReply(client, new ListFontsReply(sequenceNumber, stringMatches));
            } catch (ValueException ex) {
                sendError(client, Errors.Value, sequenceNumber, ex.getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.LIST_FONTS_WITH_INFO: {
            
            final ListFontsWithInfo listFontsWithInfo = log(messageLength, opcode, sequenceNumber, ListFontsWithInfo.decode(stream));
            
            try {
                final XNamedFontModel [] matches = xFonts.listFontsWithInfo(listFontsWithInfo.getPattern());

                final int numReplies = Math.min(matches.length, listFontsWithInfo.getMaxNames().getValue());
                
                for (int i = 0; i < numReplies; ++ i) {

                    final XNamedFontModel font = matches[i];
                    
                    final XFontModel fontModel = font.getModel();

                    final ListFontsWithInfoReply reply = new ListFontsWithInfoReply(
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
                            
                            new BOOL(allCharsExist(fontModel)),

                            new INT16((short)fontModel.getAccelerators().getFontAscent()),
                            new INT16((short)fontModel.getAccelerators().getFontDescent()),

                            new CARD32(numReplies - i - 1),
                            new FONTPROP[0],
                            font.getName()
                    );
                    
                    sendReply(client, reply);
                }
                
                sendReply(client, new ListFontsWithInfoLastReply(sequenceNumber));
                
            } catch (ValueException ex) {
                sendError(client, Errors.Value, sequenceNumber, ex.getValue(), opcode);
            }
            break;
        }

        }
    }


    private static boolean allCharsExist(XFontModel fontModel) {

        final boolean allCharsExist = fontModel.getMetrics().stream()
                .allMatch(fontCharacter -> fontCharacter.isNonZero());

        return allCharsExist;
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
