

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <jni.h>
#include <jni_md.h>

#include <cairo/cairo.h>

#include "com_neaterbits_displayserver_render_cairo_CairoNative.h"


JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1create
  (JNIEnv *env, jclass cl, jlong surface) {

	return (jlong)cairo_create((cairo_surface_t *)surface);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1destroy
  (JNIEnv *env, jclass cl, jlong cr) {

	cairo_destroy((cairo_t *)cr);
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1status
  (JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	return cairo_status(cr);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1source_1rgb
  (JNIEnv *env, jclass cl, jlong cairo_reference, jdouble red, jdouble green, jdouble blue) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_set_source_rgb(cr, red, green, blue);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1source_1surface
  (JNIEnv *env, jclass cl, jlong cairo_reference, jlong surface_reference, jdouble x, jdouble y) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_surface_t * surface = (cairo_surface_t *)surface_reference;

	cairo_set_source_surface(cr, surface, x, y);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1dash
  (JNIEnv *env, jclass cl, jlong cairo_reference, jdoubleArray dashes, jdouble offset) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	int num_dashes = (*env)->GetArrayLength(env, dashes);
	double native_dashes[num_dashes];

	(*env)->GetDoubleArrayRegion(env, dashes, 0, num_dashes, native_dashes);

	cairo_set_dash(cr, native_dashes, num_dashes, offset);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1clip
  (JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_clip(cr);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1reset_1clip
  (JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_reset_clip(cr);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1fill_1rule
  (JNIEnv *env, jclass cl, jlong cairo_reference, jint fill_rule) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_set_fill_rule(cr, fill_rule);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1line_1width
  (JNIEnv *env, jclass cl, jlong cairo_reference, jdouble width) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_set_line_width(cr, width);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1operator
  (JNIEnv *env, jclass cl, jlong cairo_reference, jint operator) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_set_operator(cr, operator);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1fill
  (JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_fill(cr);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1mask_1surface
  (JNIEnv *env, jclass cl, jlong cairo_reference, jlong surface_reference, jdouble surface_x, jdouble surface_y) {

	cairo_t *cr = (cairo_t *)cairo_reference;
	cairo_surface_t *surface = (cairo_surface_t *)surface_reference;

	cairo_mask_surface(cr, surface, surface_x, surface_y);
}


JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1paint
(JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_paint(cr);

	printf("cairo_paint() called\n");
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1stroke
(JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_stroke(cr);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1stroke_1preserve
(JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_stroke_preserve(cr);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1rectangle
  (JNIEnv *env, jclass cl, jlong cairo_reference, jdouble x, jdouble y, jdouble width, jdouble height) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_rectangle(cr, x, y, width, height);

	printf("cairo status: %s\n", cairo_status_to_string(cairo_status(cr)));
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1new_1path
(JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_new_path(cr);
}


JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1move_1to
  (JNIEnv *env, jclass cl, jlong cairo_reference, jdouble x, jdouble y) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_move_to(cr, x, y);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1rel_1move_1to
(JNIEnv *env, jclass cl, jlong cairo_reference, jdouble dx, jdouble dy) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_rel_move_to(cr, dx, dy);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1line_1to
(JNIEnv *env, jclass cl, jlong cairo_reference, jdouble x, jdouble y) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_line_to(cr, x, y);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1rel_1line_1to
(JNIEnv *env, jclass cl, jlong cairo_reference, jdouble dx, jdouble dy) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_rel_line_to(cr, dx, dy);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1destroy
(JNIEnv *env, jclass cl, jlong surface_reference) {

	cairo_surface_t *surface = (cairo_surface_t *)surface_reference;

	cairo_surface_destroy(surface);
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1get_1reference_1count
  (JNIEnv *env, jclass cl, jlong surface_reference) {

	cairo_surface_t *surface = (cairo_surface_t *)surface_reference;

	return cairo_surface_get_reference_count(surface);
}


JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1flush
  (JNIEnv *env, jclass cl, jlong surface_reference) {

	cairo_surface_t *surface = (cairo_surface_t *)surface_reference;

	cairo_surface_flush(surface);
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1image_1surface_1create
  (JNIEnv *env, jclass cl, jint format, jint width, jint height) {

	return (jlong)cairo_image_surface_create(format, width, height);
}

JNIEXPORT jlongArray JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1image_1surface_1create_1for_1data
  (JNIEnv *env, jclass cl, jbyteArray data, jint format, jint width, jint height, jint stride) {

	jbyte *native_data = (*env)->GetByteArrayElements(env, data, NULL);

	jlongArray result;

	jsize datalen = (*env)->GetArrayLength(env, data);

	unsigned char *copied_data = malloc(datalen);

	memcpy(copied_data, native_data, datalen);

	(*env)->ReleaseByteArrayElements(env, data, native_data, 0);

	result = (*env)->NewLongArray(env, 2);

	jlong surface_reference = (jlong)cairo_image_surface_create_for_data(copied_data, format, width, height, stride);

	(*env)->SetLongArrayRegion(env, result, 0, 1, &surface_reference);
	(*env)->SetLongArrayRegion(env, result, 1, 1, (jlong *)&copied_data);

	return result;
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_free_1image_1surface_1data
  (JNIEnv *env, jclass cl, jlong data_reference) {

	char *data = (char *)data_reference;

	free(data);
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1write_1to_1png
  (JNIEnv *env, jclass cl, jlong surface_reference, jstring filename) {

	const char *native_filename = (*env)->GetStringUTFChars(env, filename, NULL);

	cairo_surface_t *surface = (cairo_surface_t *)surface_reference;

	cairo_status_t status = cairo_surface_write_to_png(surface, native_filename);

	(*env)->ReleaseStringUTFChars(env, filename, native_filename);

	return status;
}


#define OP(name) \
	ENUM(CAIRO_OPERATOR_, name)

#define ENUM(prefix, name) \
	{ #prefix  #name , prefix ## name }

#define ENUM_MAPPING_TERMINATOR { NULL, 0 }

struct enum_mapping {

	const char *name;
	const int value;
};

static struct enum_mapping operator_mapping [] = {

		OP(CLEAR),
		OP(SOURCE),
	    OP(OVER),
		OP(IN),
		OP(OUT),
		OP(ATOP),
		OP(DEST),
		OP(DEST_OVER),
		OP(DEST_IN),
		OP(DEST_OUT),
		OP(DEST_ATOP),
		OP(XOR),
		OP(ADD),
		OP(SATURATE),
		ENUM_MAPPING_TERMINATOR
};

static int get_enum_value(struct enum_mapping *mappings, const char *name) {

	while (mappings->name != NULL) {

		if (strcmp(mappings->name, name) == 0) {

			return mappings->value;
		}

		++ mappings;
	}

	return -1;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_get_1cairo_1operator_1enum_1value
  (JNIEnv *env, jclass cl, jstring name) {

	const char *native_name = (*env)->GetStringUTFChars(env, name, NULL);

	cairo_operator_t operator = get_enum_value(operator_mapping, native_name);

	(*env)->ReleaseStringUTFChars(env, name, native_name);

	return operator;
}

#define FORMAT(name) \
		ENUM(CAIRO_FORMAT_, name)

static struct enum_mapping format_mapping [] = {
	    FORMAT(INVALID),
	    FORMAT(ARGB32),
	    FORMAT(RGB24),
	    FORMAT(A8),
	    FORMAT(A1),
	    FORMAT(RGB16_565),
	    FORMAT(RGB30),

		ENUM_MAPPING_TERMINATOR
};


JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_get_1cairo_1format_1enum_1value
  (JNIEnv *env, jclass cl, jstring name) {

	const char *native_name = (*env)->GetStringUTFChars(env, name, NULL);

	cairo_format_t format = get_enum_value(format_mapping, native_name);

	(*env)->ReleaseStringUTFChars(env, name, native_name);

	return format;
}

#define STATUS(name) \
		ENUM(CAIRO_STATUS_, name)

static struct enum_mapping status_mapping [] = {
	    STATUS(SUCCESS),

		STATUS(NO_MEMORY),
		STATUS(INVALID_RESTORE),
		STATUS(INVALID_POP_GROUP),
		STATUS(NO_CURRENT_POINT),
		STATUS(INVALID_MATRIX),
		STATUS(INVALID_STATUS),
		STATUS(NULL_POINTER),
		STATUS(INVALID_STRING),
		STATUS(INVALID_PATH_DATA),
		STATUS(READ_ERROR),
		STATUS(WRITE_ERROR),
		STATUS(SURFACE_FINISHED),
		STATUS(SURFACE_TYPE_MISMATCH),
		STATUS(PATTERN_TYPE_MISMATCH),
		STATUS(INVALID_CONTENT),
		STATUS(INVALID_FORMAT),
		STATUS(INVALID_VISUAL),
		STATUS(FILE_NOT_FOUND),
		STATUS(INVALID_DASH),
		STATUS(INVALID_DSC_COMMENT),
		STATUS(INVALID_INDEX),
		STATUS(CLIP_NOT_REPRESENTABLE),
		STATUS(TEMP_FILE_ERROR),
		STATUS(INVALID_STRIDE),
		STATUS(FONT_TYPE_MISMATCH),
		STATUS(USER_FONT_IMMUTABLE),
		STATUS(USER_FONT_ERROR),
		STATUS(NEGATIVE_COUNT),
		STATUS(INVALID_CLUSTERS),
		STATUS(INVALID_SLANT),
		STATUS(INVALID_WEIGHT),
		STATUS(INVALID_SIZE),
		STATUS(USER_FONT_NOT_IMPLEMENTED),
		STATUS(DEVICE_TYPE_MISMATCH),
		STATUS(DEVICE_ERROR),
		STATUS(INVALID_MESH_CONSTRUCTION),
		STATUS(DEVICE_FINISHED),
		STATUS(JBIG2_GLOBAL_MISSING),
		STATUS(PNG_ERROR),
		STATUS(FREETYPE_ERROR),
		STATUS(WIN32_GDI_ERROR),
		STATUS(TAG_ERROR),

		ENUM_MAPPING_TERMINATOR
};

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_get_1cairo_1status_1enum_1value
  (JNIEnv *env, jclass cl, jstring name) {

	const char *native_name = (*env)->GetStringUTFChars(env, name, NULL);

	cairo_status_t status = get_enum_value(status_mapping, native_name);

	(*env)->ReleaseStringUTFChars(env, name, native_name);

	return status;
}



JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1format_1stride_1for_1width
  (JNIEnv *env, jclass cl, jint format, jint width) {

	return cairo_format_stride_for_width(format, width);

}

