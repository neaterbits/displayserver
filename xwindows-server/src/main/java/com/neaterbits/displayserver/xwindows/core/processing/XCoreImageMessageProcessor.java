package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.GetImageListener;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.ImageFormat;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetImage;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.xwindows.model.XDrawable;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreImageMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XPixmapsConstAccess xPixmaps;
    
    
    public XCoreImageMessageProcessor(
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
                OpCodes.PUT_IMAGE,
                OpCodes.GET_IMAGE
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
        case OpCodes.PUT_IMAGE: {
            final PutImage putImage = log(messageLength, opcode, sequenceNumber, PutImage.decode(stream));

            try {
                final XDrawable xDrawable = findDrawable(xWindows, xPixmaps, putImage.getDrawable());
                final XGC gc = client.getGC(putImage.getGC());
                
                if (putImage.getDataOffset() != 0) {
                    throw new IllegalArgumentException();
                }
                
                if (putImage.getDataLength() != putImage.getData().length) {
                    throw new IllegalArgumentException();
                }
                
                final XLibRenderer renderer = xDrawable.getRenderer();
                
                renderer.putImage(
                        gc,
                        putImage.getFormat().getValue(),
                        putImage.getWidth().getValue(),
                        putImage.getHeight().getValue(),
                        putImage.getDstX().getValue(),
                        putImage.getDstY().getValue(),
                        putImage.getLeftPad().getValue(),
                        putImage.getDepth().getValue(),
                        putImage.getData()
                );
                
                renderer.flush();
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.GET_IMAGE: {
            final GetImage getImage = log(messageLength, opcode, sequenceNumber, GetImage.decode(stream));

            try {
                final XDrawable xDrawable = findDrawable(xWindows, xPixmaps, getImage.getDrawable());
                
                getImage(
                        getImage,
                        sequenceNumber,
                        xDrawable.getSurface(),
                        xDrawable.getVisual(),
                        client);
            } catch (MatchException ex) {
                sendError(client, Errors.Match, sequenceNumber, 0L, opcode);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
            break;
        }
        }
    }

    private void getImage(
            GetImage getImage,
            CARD16 sequenceNumber,
            BufferOperations bufferOperations,
            VISUALID visual,
            XClientOps client)
    
        throws MatchException{
        
        if (getImage.getX().getValue() + getImage.getWidth().getValue() > bufferOperations.getWidth()) {
            throw new MatchException("Width outside of bounds");
        }
        
        if (getImage.getY().getValue() + getImage.getHeight().getValue() > bufferOperations.getHeight()) {
            throw new MatchException("Height outside of bounds");
        }

        switch (getImage.getFormat().getValue()) {

        case ImageFormat.ZPIXMAP:
            bufferOperations.getImage(
                    getImage.getX().getValue(), getImage.getY().getValue(),
                    getImage.getWidth().getValue(), getImage.getHeight().getValue(),
                    PixelFormat.RGB24,
                    
                    new GetImageListener() {
                        @Override
                        public void onResult(byte[] data) {
                            sendGetImageReply(sequenceNumber, bufferOperations, VISUALID.None, data, client);
                        }
                        
                        @Override
                        public void onError() {
                            sendError(
                                    client,
                                    Errors.Implementation,
                                    sequenceNumber,
                                    0L,
                                    OpCodes.GET_IMAGE);
                        }
                    }); 
            break;
            
        case ImageFormat.BITMAP:
        case ImageFormat.XYPIXMAP:
            throw new UnsupportedOperationException("TODO");

        default:
            throw new UnsupportedOperationException();
        }

    }
    
    private void sendGetImageReply(
            CARD16 sequenceNumber,
            BufferOperations bufferOperations,
            VISUALID visual,
            byte [] data,
            XClientOps client) {
        
        final GetImageReply getImageReply = new GetImageReply(
                sequenceNumber,
                new CARD8((byte)bufferOperations.getDepth()),
                visual,
                data);
        
        sendReply(client, getImageReply);
    }

}
