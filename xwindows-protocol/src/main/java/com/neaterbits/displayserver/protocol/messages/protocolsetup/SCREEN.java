package com.neaterbits.displayserver.protocol.messages.protocolsetup;

import java.io.IOException;
import java.util.Arrays;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.SET32;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class SCREEN extends Encodeable {

    private final WINDOW root;
    private final COLORMAP defaultColorMap;
    private final CARD32 whitePixel;
    private final CARD32 blackPixel;
    
    private final SET32 currentInputMasks;
    
    private final CARD16 widthInPixels;
    private final CARD16 heightInPixels;
    
    private final CARD16 widthInMillimiters;
    private final CARD16 heightInMillimiters;
    
    private final CARD16 minInstalledMaps;
    private final CARD16 maxInstalledMaps;
    
    private final VISUALID rootVisual;
    
    private final BYTE backingStores;
    private final BOOL saveUnders;
    private final CARD8 rootDepth;
    private final CARD8 numberOfDepths;
    private final DEPTH [] allowedDepths;

    public static SCREEN decode(XWindowsProtocolInputStream stream) throws IOException {

        final WINDOW root = stream.readWINDOW();
        final COLORMAP defaultColorMap = stream.readCOLORMAP();
        final CARD32 whitePixel = stream.readCARD32();
        final CARD32 blackPixel = stream.readCARD32();
        
        final SET32 currentInputMasks = stream.readSET32();
        
        final CARD16 widthInPixels = stream.readCARD16();
        final CARD16 heightInPixels = stream.readCARD16();
        
        final CARD16 widthInMillimiters = stream.readCARD16();
        final CARD16 heightInMillimiters = stream.readCARD16();
        
        final CARD16 minInstalledMaps = stream.readCARD16();
        final CARD16 maxInstalledMaps = stream.readCARD16();
        
        final VISUALID rootVisual = stream.readVISUALID();
        
        final BYTE backingStores = stream.readBYTE();
        final BOOL saveUnders = stream.readBOOL();
        final CARD8 rootDepth = stream.readCARD8();
        
        final CARD8 numberOfDepths = stream.readCARD8();
        
        final DEPTH [] allowedDepths = new DEPTH[numberOfDepths.getValue()];

        for (int i = 0; i < allowedDepths.length; ++ i) {
            allowedDepths[i] = DEPTH.decode(stream);
        }
        
        return new SCREEN(
                root,
                defaultColorMap,
                whitePixel, blackPixel,
                currentInputMasks,
                widthInPixels, heightInPixels,
                widthInMillimiters, heightInMillimiters,
                minInstalledMaps, maxInstalledMaps,
                rootVisual,
                backingStores, saveUnders,
                rootDepth,
                numberOfDepths, allowedDepths);
    }
    
    public SCREEN(
            WINDOW root,
            COLORMAP defaultColorMap,
            
            CARD32 whitePixel,
            CARD32 blackPixel,
            
            SET32 currentInputMasks,
            
            CARD16 widthInPixels,
            CARD16 heightInPixels,
            
            CARD16 widthInMillimiters,
            CARD16 heightInMillimiters,
            
            CARD16 minInstalledMaps,
            CARD16 maxInstalledMaps,
            VISUALID rootVisual,
            
            BYTE backingStores,
            BOOL saveUnders,
            
            CARD8 rootDepth,
            CARD8 numberOfDepths,
            DEPTH[] allowedDepths) {

        this.root = root;
        this.defaultColorMap = defaultColorMap;
        this.whitePixel = whitePixel;
        this.blackPixel = blackPixel;
        this.currentInputMasks = currentInputMasks;
        this.widthInPixels = widthInPixels;
        this.heightInPixels = heightInPixels;
        this.widthInMillimiters = widthInMillimiters;
        this.heightInMillimiters = heightInMillimiters;
        this.minInstalledMaps = minInstalledMaps;
        this.maxInstalledMaps = maxInstalledMaps;
        this.rootVisual = rootVisual;
        this.backingStores = backingStores;
        this.saveUnders = saveUnders;
        this.rootDepth = rootDepth;
        this.numberOfDepths = numberOfDepths;
        this.allowedDepths = allowedDepths;
    }

    public WINDOW getRoot() {
        return root;
    }
    
    public COLORMAP getDefaultColorMap() {
        return defaultColorMap;
    }
    
    public CARD32 getWhitePixel() {
        return whitePixel;
    }
    
    public CARD32 getBlackPixel() {
        return blackPixel;
    }
    
    public SET32 getCurrentInputMasks() {
        return currentInputMasks;
    }
    
    public CARD16 getWidthInPixels() {
        return widthInPixels;
    }
    
    public CARD16 getHeightInPixels() {
        return heightInPixels;
    }
    
    public CARD16 getWidthInMillimiters() {
        return widthInMillimiters;
    }
    
    public CARD16 getHeightInMillimiters() {
        return heightInMillimiters;
    }
    
    public CARD16 getMinInstalledMaps() {
        return minInstalledMaps;
    }
    
    public CARD16 getMaxInstalledMaps() {
        return maxInstalledMaps;
    }
    
    public VISUALID getRootVisual() {
        return rootVisual;
    }
    
    public BYTE getBackingStores() {
        return backingStores;
    }
    
    public BOOL getSaveUnders() {
        return saveUnders;
    }
    
    public CARD8 getRootDepth() {
        return rootDepth;
    }
    
    public CARD8 getNumberOfDepths() {
        return numberOfDepths;
    }
    
    public DEPTH[] getAllowedDepths() {
        return allowedDepths;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        stream.writeWINDOW(root);
        stream.writeCOLORMAP(defaultColorMap);
        stream.writeCARD32(whitePixel);
        stream.writeCARD32(blackPixel);
        stream.writeSET32(currentInputMasks);
        stream.writeCARD16(widthInPixels);
        stream.writeCARD16(heightInPixels);
        stream.writeCARD16(widthInMillimiters);
        stream.writeCARD16(heightInMillimiters);
        stream.writeCARD16(minInstalledMaps);
        stream.writeCARD16(maxInstalledMaps);
        stream.writeVISUALID(rootVisual);
        stream.writeBYTE(backingStores);
        stream.writeBOOL(saveUnders);
        stream.writeCARD8(rootDepth);
        stream.writeCARD8(numberOfDepths);
        encodeArray(allowedDepths, stream);
    }

    @Override
    public String toString() {
        return "SCREEN [root=" + root + ", defaultColorMap=" + defaultColorMap + ", whitePixel=" + whitePixel
                + ", blackPixel=" + blackPixel + ", currentInputMasks=" + currentInputMasks + ", widthInPixels="
                + widthInPixels + ", heightInPixels=" + heightInPixels + ", widthInMillimiters=" + widthInMillimiters
                + ", heightInMillimiters=" + heightInMillimiters + ", minInstalledMaps=" + minInstalledMaps
                + ", maxInstalledMaps=" + maxInstalledMaps + ", rootVisual=" + rootVisual + ", backingStores="
                + backingStores + ", saveUnders=" + saveUnders + ", rootDepth=" + rootDepth + ", numberOfDepths="
                + numberOfDepths + ", allowedDepths=" + Arrays.toString(allowedDepths) + "]";
    }
}
