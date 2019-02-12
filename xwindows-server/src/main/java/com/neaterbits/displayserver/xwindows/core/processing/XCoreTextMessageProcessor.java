package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.FontException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.ImageText16;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyText8;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyTextRequest;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CHAR2B;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.STRING16;
import com.neaterbits.displayserver.protocol.types.TEXTITEM;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.model.XDrawable;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreTextMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XPixmapsConstAccess xPixmaps;
    
    public XCoreTextMessageProcessor(
            XWindowsServerProtocolLog protocolLog,
            XWindowsConstAccess<?> xWindows,
            XPixmapsConstAccess xPixmaps) {

        super(protocolLog);
        
        this.xWindows = xWindows;
        this.xPixmaps = xPixmaps;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.POLY_TEXT_8,
                OpCodes.IMAGE_TEXT_16
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
        
        case OpCodes.POLY_TEXT_8: {
            
            final PolyText8 polyText = log(messageLength, opcode, sequenceNumber, PolyText8.decode(stream));
            
            try {
                polyText(polyText, client, String::length, XCoreTextMessageProcessor::getGlyphIndex8);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            } catch (MatchException ex) {
                sendError(client, Errors.Match, sequenceNumber, 0L, opcode);
            } catch (FontException ex) {
                sendError(client, Errors.Font, sequenceNumber, ex.getFont().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.IMAGE_TEXT_16: {

            final ImageText16 imageText = log(messageLength, opcode, sequenceNumber, ImageText16.decode(stream));
            
            try {
                final XDrawable xDrawable = findDrawable(xWindows, xPixmaps, imageText.getDrawable());
                final XGC gc = client.getGC(imageText.getGC());

                final FONT fontResource = gc.getAttributes().getFont();
                
                final XFont font;
                try {
                    font = client.getFont(fontResource);
                } catch (FontException ex) {
                    throw new GContextException("No such font", imageText.getGC());
                }
                
                final STRING16 string = imageText.getString();
                
                int x = imageText.getX().getValue();
                final int y = imageText.getY().getValue();
                
                final XLibRenderer renderer = xDrawable.getRenderer();

                renderText(
                        gc,
                        string,
                        string.length(),
                        x, y,
                        font,
                        renderer,
                        XCoreTextMessageProcessor::getGlyphIndex16);
                
                renderer.flush();
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            } catch (MatchException ex) {
                sendError(client, Errors.Match, sequenceNumber, 0L, opcode);
            }
            break;
        }
        
        }
    }
    
    @FunctionalInterface
    interface GetGlyphIndex<STRING> {
        int getGlyphIndex(STRING string, int index, XFont font) throws MatchException;
    }
    
    @FunctionalInterface
    interface GetStringLength<STRING> {
        int getLength(STRING string);
    }

    private static int getGlyphIndex8(String string, int index, XFont font) throws MatchException {
        return font.getGlyphIndex(new CHAR2B((byte)0, (byte)string.charAt(index)));
    }

    private static int getGlyphIndex16(STRING16 string, int index, XFont font) throws MatchException {
        return font.getGlyphIndex(string.getCharacter(index));
    }
    
    private <STRING> void renderText(
            XGC gc,
            STRING string,
            int length,
            int x, int y,
            XFont font,
            XLibRenderer renderer,
            GetGlyphIndex<STRING> getGlyphIndex) throws MatchException {

        for (int i = 0; i < length; ++ i) {
            
            final int glyphIndex = getGlyphIndex.getGlyphIndex(string, i, font);
            
            renderer.renderBitmap(gc, font.getRenderBitmap(glyphIndex), x, y);
            // drawable.getRenderer().fillRectangle(x, y, 15, 15, 0, 0, 0);
            
            x += font.getGlyphRenderWidth(glyphIndex);
        }
    }
    
    private <STRING, ITEM extends TEXTITEM<STRING, ITEM>> void polyText(
            PolyTextRequest<STRING, ITEM> request,
            XClientOps client,
            GetStringLength<STRING> getStringLength,
            GetGlyphIndex<STRING> getGlyphIndex) throws DrawableException, GContextException, MatchException, FontException {
        
        final XDrawable xDrawable = findDrawable(xWindows, xPixmaps, request.getDrawable());
        final XGC gc = client.getGC(request.getGC());

        final XLibRenderer renderer = xDrawable.getRenderer();
        
        int x = request.getX().getValue();
        int y = request.getY().getValue();


        FONT fontResource = gc.getAttributes().getFont();
        
        XFont font = client.getFont(fontResource);
        
        for (ITEM item : request.getItems()) {

            if (item.isString()) {
                
                x += item.getDelta().getValue();
                
                renderText(
                        gc,
                        item.getString(),
                        getStringLength.getLength(item.getString()),
                        x, y,
                        font,
                        renderer,
                        getGlyphIndex);
            }
            else {

                final int f = 
                          item.getFontByte3().getValue() << 24
                        | item.getFontByte2().getValue() << 16
                        | item.getFontByte1().getValue() << 8
                        | item.getFontByte0().getValue();
                
                fontResource = new FONT(f);
                
                font = client.getFont(fontResource);
                
                gc.setFont(fontResource);
            }
        }
    }
}
