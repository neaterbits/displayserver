package com.neaterbits.displayserver.windows.compositor;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.GetImageListener;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.render.cairo.CairoSurface;
import com.neaterbits.displayserver.types.Size;

public class SurfaceWrapper implements Surface {

    private BufferOperations bufferOperations;
    private CoordinateTranslator coordinateTranslator;
    private Size size;
    private int depth;
    
    public SurfaceWrapper(
            BufferOperations bufferOperations,
            CoordinateTranslator coordinateTranslator,
            Size size,
            int depth) {
        
        Objects.requireNonNull(bufferOperations);
        Objects.requireNonNull(coordinateTranslator);
        Objects.requireNonNull(size);
        
        if (bufferOperations instanceof SurfaceWrapper) {
            throw new IllegalArgumentException();
        }
        

        this.bufferOperations = bufferOperations;
        this.coordinateTranslator = coordinateTranslator;
        this.size = size;
        this.depth = depth;
    }

    @Override
    public final void putImage(int x, int y, int width, int height, PixelFormat format, byte[] data) {

        bufferOperations.putImage(
                coordinateTranslator.translateX(x),
                coordinateTranslator.translateY(y),
                width, height,
                format,
                data);
    }
    
    @Override
    public final void getImage(int x, int y, int width, int height, PixelFormat format, GetImageListener listener) {
    
        bufferOperations.getImage(
                coordinateTranslator.translateX(x),
                coordinateTranslator.translateY(y),
                width, height,
                format,
                listener);
        
    }
    
    @Override
    public final void copyArea(BufferOperations src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        
        if (!(src instanceof SurfaceWrapper)) {
            throw new IllegalStateException("Not a surface wrapper " + src.getClass());
        }

        src = ((SurfaceWrapper)src).bufferOperations;
        
        bufferOperations.copyArea(
                src,
                srcX, srcY,
                coordinateTranslator.translateX(dstX),
                coordinateTranslator.translateY(dstY),
                width, height);
    }
    
    @Override
    public final CairoSurface createCairoSurface() {
        final CairoSurface cairoSurface = bufferOperations.createCairoSurface();
        
        return new CairoSurfaceWrapper(cairoSurface, coordinateTranslator);
    }
    
    @Override
    public final int translateX(int x) {
        return coordinateTranslator.translateX(x);
    }
    
    @Override
    public final int translateY(int y) {
        return coordinateTranslator.translateY(y);
    }
    
    @Override
    public final double translateX(double x) {
        return coordinateTranslator.translateX(x);
    }

    @Override
    public final double translateY(double y) {
        return coordinateTranslator.translateY(y);
    }

    @Override
    public final Size getSize() {
        return size;
    }
    
    @Override
    public final int getDepth() {
        return depth;
    }
    
    protected final BufferOperations getBuffer() {
        return bufferOperations;
    }
}
