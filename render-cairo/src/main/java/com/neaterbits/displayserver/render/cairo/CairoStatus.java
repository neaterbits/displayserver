package com.neaterbits.displayserver.render.cairo;

public enum CairoStatus {

    SUCCESS,

    NO_MEMORY,
    INVALID_RESTORE,
    INVALID_POP_GROUP,
    NO_CURRENT_POINT,
    INVALID_MATRIX,
    INVALID_STATUS,
    NULL_POINTER,
    INVALID_STRING,
    INVALID_PATH_DATA,
    READ_ERROR,
    WRITE_ERROR,
    SURFACE_FINISHED,
    SURFACE_TYPE_MISMATCH,
    PATTERN_TYPE_MISMATCH,
    INVALID_CONTENT,
    INVALID_FORMAT,
    INVALID_VISUAL,
    FILE_NOT_FOUND,
    INVALID_DASH,
    INVALID_DSC_COMMENT,
    INVALID_INDEX,
    CLIP_NOT_REPRESENTABLE,
    TEMP_FILE_ERROR,
    INVALID_STRIDE,
    FONT_TYPE_MISMATCH,
    USER_FONT_IMMUTABLE,
    USER_FONT_ERROR,
    NEGATIVE_COUNT,
    INVALID_CLUSTERS,
    INVALID_SLANT,
    INVALID_WEIGHT,
    INVALID_SIZE,
    USER_FONT_NOT_IMPLEMENTED,
    DEVICE_TYPE_MISMATCH,
    DEVICE_ERROR,
    INVALID_MESH_CONSTRUCTION,
    DEVICE_FINISHED,
    JBIG2_GLOBAL_MISSING,
    PNG_ERROR,
    FREETYPE_ERROR,
    WIN32_GDI_ERROR,
    TAG_ERROR;
    
    private final int cairoValue;
    
    private CairoStatus() {
        this.cairoValue = CairoNative.get_cairo_status_enum_value("CAIRO_STATUS_" + name());
        
        if (cairoValue < -1) {
            throw new IllegalStateException();
        }
    }

    int getCairoValue() {
        return cairoValue;
    }
    
    static CairoStatus fromCairoValue(int cairoValue) {
        
        for (CairoStatus status : values()) {
            if (status.getCairoValue() == cairoValue) {
                return status;
            }
        }
        
        throw new IllegalStateException();
    }
}
