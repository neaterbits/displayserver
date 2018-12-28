
#include <stdio.h>
#include <string.h>

#include <jni.h>
#include <jni_md.h>

#include <xcb/xcb.h>
#include <cairo/cairo-xcb.h>

#include "com_neaterbits_displayserver_render_cairo_xcb_XCBNative.h"

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1connect
  (JNIEnv *env, jclass cl, jstring display) {

	const char *native_display = (*env)->GetStringUTFChars(env, display, NULL);

	int screen = 0;

	xcb_connection_t *connection = xcb_connect(native_display, &screen);

	printf("connection %p\n", connection);

	jlong connection_reference = (jlong)connection;

	printf("## screen no: %d\n", screen);

	(*env)->ReleaseStringUTFChars(env, display, native_display);

	return connection_reference;

}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1connect_1display
  (JNIEnv *env, jclass cl, jstring display, jstring protocol, jbyteArray data) {

	const char *native_display = (*env)->GetStringUTFChars(env, display, NULL);
	const char *native_protocol = (*env)->GetStringUTFChars(env, protocol, NULL);

	printf("display: %s, protocol: %s\n", native_display, native_protocol);

	int screen = 0;

	jbyte *native_data = (*env)->GetByteArrayElements(env, data, NULL);

	jsize datalen = (*env)->GetArrayLength(env, data);
	// xcb_connection_t *connection = xcb_connect((const char *)native_display, &screen);

	xcb_auth_info_t auth = {
			strlen(native_protocol),
			(char *)native_protocol,

			datalen,
			(char *)native_data
	};

	xcb_connection_t *connection = xcb_connect_to_display_with_auth_info(native_display, &auth, &screen);

	printf("connection %p\n", connection);

	jlong connection_reference = (jlong)connection;

	printf("## screen no: %d\n", screen);

	(*env)->ReleaseByteArrayElements(env, data, native_data, 0);
	(*env)->ReleaseStringUTFChars(env, protocol, native_protocol);
	(*env)->ReleaseStringUTFChars(env, display, native_display);

	return connection_reference;
}


JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1disconnect
  (JNIEnv *env, jclass cl, jlong connection) {

	xcb_disconnect((xcb_connection_t *)connection);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1flush
  (JNIEnv *env, jclass cl, jlong connection_reference) {

	printf("Flushing connection\n");

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	xcb_flush(connection);

	printf("Connection flushed\n");
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1get_1setup
  (JNIEnv *env, jclass cl, jlong connection_reference) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	printf("connection %p\n", connection);

	const xcb_setup_t *setup = xcb_get_setup(connection);

	printf("## setup %d/%d\n", setup->min_keycode, setup->max_keycode);

	return (jlong)setup;
}


JNIEXPORT jlongArray JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1get_1screens
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	xcb_screen_iterator_t iterator = xcb_setup_roots_iterator(setup);

	int num_screens = xcb_setup_roots_length(setup);

	jlongArray result = (*env)->NewLongArray(env, num_screens);

	for (int dst_idx = 0; dst_idx < num_screens; ++ dst_idx) {
		jlong value = (jlong)iterator.data;

		(*env)->SetLongArrayRegion(env, result, dst_idx, 1, &value);

		if (iterator.rem > 0) {
			xcb_screen_next(&iterator);
		}
	}

	return result;
}

JNIEXPORT jlongArray JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1get_1depths
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	int num_depths = xcb_screen_allowed_depths_length(screen);

	xcb_depth_iterator_t iterator = xcb_screen_allowed_depths_iterator(screen);

	jlongArray result = (*env)->NewLongArray(env, num_depths);

	for (int dst_idx = 0; dst_idx < num_depths; ++ dst_idx) {

		jlong value = (jlong)iterator.data;

		printf("## returning depth %d, remaining: %d\n", iterator.data->depth, iterator.rem);

		(*env)->SetLongArrayRegion(env, result, dst_idx, 1, &value);

		if (iterator.rem != 0) {
			xcb_depth_next(&iterator);
		}
	}

	return result;
}

JNIEXPORT jbyte JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_depth_1get_1depth
  (JNIEnv *env, jclass cl, jlong depth_reference) {

	xcb_depth_t *depth = (xcb_depth_t *)depth_reference;

	return depth->depth;
}


JNIEXPORT jlongArray JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_depth_1get_1visuals
  (JNIEnv *env, jclass cl, jlong depth_reference) {

	xcb_depth_t *depth = (xcb_depth_t *)depth_reference;

	int num_visuals = xcb_depth_visuals_length(depth);

	xcb_visualtype_iterator_t iterator = xcb_depth_visuals_iterator(depth);

	jlongArray result = (*env)->NewLongArray(env, num_visuals);

	for (int dst_idx = 0; dst_idx < num_visuals; ++ dst_idx) {

		jlong value = (jlong)iterator.data;

		(*env)->SetLongArrayRegion(env, result, dst_idx, 1, &value);

		if (iterator.rem != 0) {
			xcb_visualtype_next(&iterator);
		}
	}

	return result;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_visual_1get_1visual_1id
  (JNIEnv *env, jclass cl, jlong visual_reference) {

	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return visual->visual_id;
}

JNIEXPORT jbyte JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_visual_1get_1class
(JNIEnv *env, jclass cl, jlong visual_reference) {

	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return visual->_class;
}

JNIEXPORT jbyte JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_visual_1get_1bits_1per_1rgb_1value
(JNIEnv *env, jclass cl, jlong visual_reference) {

	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return visual->bits_per_rgb_value;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_visual_1get_1colormap_1entries
(JNIEnv *env, jclass cl, jlong visual_reference) {

	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return visual->colormap_entries;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_visual_1get_1red_1mask
(JNIEnv *env, jclass cl, jlong visual_reference) {

	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return visual->red_mask;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_visual_1get_1green_1mask
(JNIEnv *env, jclass cl, jlong visual_reference) {

	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return visual->green_mask;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_visual_1get_1blue_1mask
(JNIEnv *env, jclass cl, jlong visual_reference) {

	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return visual->blue_mask;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_cairo_1create_1xcb_1surface
  (JNIEnv *env, jclass cl, jlong connection_reference, jint drawable, jlong visual_reference, jint width, jint height) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;
	xcb_drawable_t xcb_drawable = drawable;
	xcb_visualtype_t *visual = (xcb_visualtype_t *)visual_reference;

	return (jlong)cairo_xcb_surface_create(
			connection,
			xcb_drawable,
			visual,
			width,
			height);
}
