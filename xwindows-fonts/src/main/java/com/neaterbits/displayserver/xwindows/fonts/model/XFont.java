package com.neaterbits.displayserver.xwindows.fonts.model;


import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.Buffer;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.types.CHAR2B;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;
import com.neaterbits.displayserver.xwindows.util.Bitmaps;
import com.neaterbits.displayserver.xwindows.util.Refcountable;

public final class XFont extends Refcountable {

    private final String name;
    private final XFontModel model;
    private final FontBuffer [] renderBitmaps;
    
    public XFont(String name, XFontModel model, FontBuffer [] renderBitmaps) {

        Objects.requireNonNull(name);
        Objects.requireNonNull(model);
        Objects.requireNonNull(renderBitmaps);
        
        this.name = name;
        this.model = model;
        this.renderBitmaps = renderBitmaps;
    }

    public String getName() {
        return name;
    }

    public XFontModel getModel() {
        return model;
    }
    
    public int getGlyphIndex(CHAR2B character) throws MatchException {
        
        Objects.requireNonNull(character);
        
        return model.getGlyphIndex(character);
    }

    public int getGlyphRenderWidth(int glyphIndex) {
        return model.getGlyphRenderWidth(glyphIndex);
    }
    
    public Buffer getRenderBitmap(int glyphIndex) {
        return renderBitmaps[glyphIndex];
    }

    public void print(PrintStream out) throws MatchException {

        
        for (char c = 'A'; c <= 'H'; ++ c) {
            final int glyphIndex = model.getGlyphIndex(c);
            
            System.out.println("glyph index: " + glyphIndex + "/" + c);
            
            print(out, glyphIndex);
        }
    }
    
    private void print(PrintStream out, int i) {
        final List<byte []> bitmaps = model.getBitmaps().getBitmaps();
        final List<XFontCharacter> metrics = model.getMetrics();

        if (bitmaps.size() != metrics.size()) {
            throw new IllegalStateException();
        }

        final byte [] bitmap = bitmaps.get(i);
        final XFontCharacter character = metrics.get(i);
        
        final int width = character.getCharacterWidth();
        final int height = character.getAscent() + character.getDescent();

        final FontBitmapFormat fromFormat = model.getBitmaps().getBitmapFormat();
        
        out.println("-----------------------------------------");
        out.println("Font " + width + "/" + height + ", format " + fromFormat + ", count=" + bitmaps.size());

        print(out, fromFormat, bitmap, width, height);
    }

    private static void printData(PrintStream out,
            FontBitmapFormat fromFormat, byte [] from,
            int width, int height) {
        
        Bitmaps.printBitmap(out, from, fromFormat.getStride(width));
        
    }

    private static void print(
            PrintStream out,
            FontBitmapFormat fromFormat, byte [] from,
            int width, int height) {
        
            
        for (int y = 0; y < height; ++ y) {

            for (int x = 0; x < width; ++ x) {
               
                if (fromFormat.isSet(from, x, y, width)) {
                    out.print("#");
                }
                else {
                    out.print(" ");
                }
            }
            
            out.println();
        }
    }

    
    @Override
    protected void onNoRefs() {
        
        for (FontBuffer renderBitmap : renderBitmaps) {
            renderBitmap.dispose();
        }
    }
}
