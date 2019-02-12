package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.FillPoly;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyFillRectangle;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyLine;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyPoint;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyRectangle;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolySegment;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.model.XDrawable;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreDrawMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XPixmapsConstAccess xPixmaps;
    
    public XCoreDrawMessageProcessor(
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
                OpCodes.POLY_POINT,
                OpCodes.POLY_LINE,
                OpCodes.POLY_SEGMENT,
                OpCodes.POLY_FILL_RECTANGLE,
                OpCodes.POLY_RECTANGLE,
                OpCodes.FILL_POLY
        };
    }

    @Override
    protected void onMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException {

        try {
            
            final XDrawable xDrawable;
            final XGC xgc;
            
            switch (opcode) {
            
            case OpCodes.POLY_POINT: {

                final PolyPoint polyPoint = log(messageLength, opcode, sequenceNumber, PolyPoint.decode(stream));
                
                xDrawable = findDrawable(xWindows, xPixmaps, polyPoint.getDrawable());
                xgc = client.getGC(polyPoint.getGC());
                
                xDrawable.getRenderer().polyPoint(xgc, polyPoint.getCoordinateMode(), polyPoint.getPoints());
                break;
            }
            
            case OpCodes.POLY_LINE: {
                
                final PolyLine polyLine = log(messageLength, opcode, sequenceNumber, PolyLine.decode(stream));
                
                xDrawable = findDrawable(xWindows, xPixmaps, polyLine.getDrawable());
                xgc = client.getGC(polyLine.getGC());
                
                xDrawable.getRenderer().polyLine(xgc, polyLine.getCoordinateMode(), polyLine.getPoints());
                break;
            }
            
            case OpCodes.POLY_SEGMENT: {
                
                final PolySegment polySegment = log(messageLength, opcode, sequenceNumber, PolySegment.decode(stream));
                
                xDrawable = findDrawable(xWindows, xPixmaps, polySegment.getDrawable());
                xgc = client.getGC(polySegment.getGC());
                
                xDrawable.getRenderer().polySegment(xgc, polySegment.getSegments());
                break;
            }
            
            case OpCodes.POLY_FILL_RECTANGLE: {
            
                final PolyFillRectangle polyFillRectangle = log(messageLength, opcode, sequenceNumber, PolyFillRectangle.decode(stream));
                
                xDrawable = findDrawable(xWindows, xPixmaps, polyFillRectangle.getDrawable());
                xgc = client.getGC(polyFillRectangle.getGC());
                
                xDrawable.getRenderer().polyFillRectangle(xgc, polyFillRectangle.getRectangles());
                break;
            }
            
            case OpCodes.POLY_RECTANGLE: {
                
                final PolyRectangle polyRectangle = log(messageLength, opcode, sequenceNumber, PolyRectangle.decode(stream));
                
                xDrawable = findDrawable(xWindows, xPixmaps, polyRectangle.getDrawable());
                xgc = client.getGC(polyRectangle.getGC());
                
                xDrawable.getRenderer().polyRectangle(xgc, polyRectangle.getRectangles());
                break;
            }
            
            case OpCodes.FILL_POLY: {
                
                final FillPoly fillPoly = log(messageLength, opcode, sequenceNumber, FillPoly.decode(stream));
                
                xDrawable = findDrawable(xWindows, xPixmaps, fillPoly.getDrawable());
                xgc = client.getGC(fillPoly.getGC());
                
                xDrawable.getRenderer().fillPoly(xgc, fillPoly.getPoints());
                break;
            }

            default:
                throw new UnsupportedOperationException();
            }

            xDrawable.getRenderer().flush();

        } catch (DrawableException ex) {
            sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
        } catch (GContextException ex) {
            sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
        }
    }
}
