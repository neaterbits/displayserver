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
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CHAR2B;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.STRING16;
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

                for (int i = 0; i < string.length(); ++ i) {
                    
                    final CHAR2B character = string.getCharacter(i);
                    
                    final int glyphIndex = font.getGlyphIndex(character);
                    
                    renderer.renderBitmap(gc, font.getRenderBitmap(glyphIndex), x, y);
                    // drawable.getRenderer().fillRectangle(x, y, 15, 15, 0, 0, 0);
                    
                    x += font.getGlyphRenderWidth(glyphIndex);
                }
                
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
}
