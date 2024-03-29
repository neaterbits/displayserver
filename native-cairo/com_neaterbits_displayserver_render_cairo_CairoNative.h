/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_neaterbits_displayserver_render_cairo_CairoNative */

#ifndef _Included_com_neaterbits_displayserver_render_cairo_CairoNative
#define _Included_com_neaterbits_displayserver_render_cairo_CairoNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_create
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1create
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1destroy
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_status
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1status
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_set_source_rgb
 * Signature: (JDDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1source_1rgb
  (JNIEnv *, jclass, jlong, jdouble, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_set_source_surface
 * Signature: (JJDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1source_1surface
  (JNIEnv *, jclass, jlong, jlong, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_set_dash
 * Signature: (J[DD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1dash
  (JNIEnv *, jclass, jlong, jdoubleArray, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_set_fill_rule
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1fill_1rule
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_set_line_width
 * Signature: (JD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1line_1width
  (JNIEnv *, jclass, jlong, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_set_operator
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1operator
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_clip
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1clip
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_reset_clip
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1reset_1clip
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_fill
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1fill
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_mask_surface
 * Signature: (JJDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1mask_1surface
  (JNIEnv *, jclass, jlong, jlong, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_paint
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1paint
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_stroke
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1stroke
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_stroke_preserve
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1stroke_1preserve
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    get_cairo_operator_enum_value
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_get_1cairo_1operator_1enum_1value
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_new_path
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1new_1path
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_move_to
 * Signature: (JDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1move_1to
  (JNIEnv *, jclass, jlong, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_rel_move_to
 * Signature: (JDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1rel_1move_1to
  (JNIEnv *, jclass, jlong, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_line_to
 * Signature: (JDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1line_1to
  (JNIEnv *, jclass, jlong, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_rel_line_to
 * Signature: (JDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1rel_1line_1to
  (JNIEnv *, jclass, jlong, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_rectangle
 * Signature: (JDDDD)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1rectangle
  (JNIEnv *, jclass, jlong, jdouble, jdouble, jdouble, jdouble);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_surface_destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1destroy
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_surface_flush
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1flush
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_surface_get_reference_count
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1get_1reference_1count
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_image_surface_create
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1image_1surface_1create
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_image_surface_create_for_data
 * Signature: ([BIIII)[J
 */
JNIEXPORT jlongArray JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1image_1surface_1create_1for_1data
  (JNIEnv *, jclass, jbyteArray, jint, jint, jint, jint);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    free_image_surface_data
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_free_1image_1surface_1data
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_surface_write_to_png
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1write_1to_1png
  (JNIEnv *, jclass, jlong, jstring);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    get_cairo_format_enum_value
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_get_1cairo_1format_1enum_1value
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    cairo_format_stride_for_width
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1format_1stride_1for_1width
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_neaterbits_displayserver_render_cairo_CairoNative
 * Method:    get_cairo_status_enum_value
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_get_1cairo_1status_1enum_1value
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
