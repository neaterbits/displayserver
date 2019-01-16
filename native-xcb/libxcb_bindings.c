
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <jni.h>
#include <jni_md.h>

#include <xcb/xcb.h>
#include <xcb/xcbext.h>
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

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1generate_1id
  (JNIEnv *env, jclass cl, jlong connection_reference) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	return xcb_generate_id(connection);
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

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1status
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->status;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1protocol_1major_1version
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->protocol_major_version;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1protocol_1minor_1version
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->protocol_minor_version;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1release_1number
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->release_number;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1resource_1id_1base
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->resource_id_base;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1resource_1id_1mask
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->resource_id_mask;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1motion_1buffer_1size
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->motion_buffer_size;
}

JNIEXPORT jstring JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1vendor
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	char vendor[setup->vendor_len + 1];

	strncpy(vendor, xcb_setup_vendor(setup), sizeof vendor);

	vendor[sizeof vendor - 1] = 0;

	return (*env)->NewStringUTF(env, vendor);
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1maximum_1request_1length
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->maximum_request_length;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1image_1byte_1order
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->image_byte_order;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1bitmap_1format_1bit_1order
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->bitmap_format_bit_order;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1bitmap_1format_1scanline_1unit
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->bitmap_format_scanline_unit;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1bitmap_1format_1scanline_1pad
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->bitmap_format_scanline_pad;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1min_1keycode
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->min_keycode;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1max_1keycode
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	return setup->max_keycode;
}

JNIEXPORT jlongArray JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_setup_1get_1formats
  (JNIEnv *env, jclass cl, jlong setup_reference) {

	xcb_setup_t *setup = (xcb_setup_t *)setup_reference;

	xcb_format_iterator_t iterator = xcb_setup_pixmap_formats_iterator(setup);

	int num_formats = xcb_setup_pixmap_formats_length(setup);

	jlongArray result = (*env)->NewLongArray(env, num_formats);

	for (int dst_idx = 0; dst_idx < num_formats; ++ dst_idx) {
		jlong value = (jlong)iterator.data;

		(*env)->SetLongArrayRegion(env, result, dst_idx, 1, &value);

		if (iterator.rem > 0) {
			xcb_format_next(&iterator);
		}
	}

	return result;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_format_1depth
  (JNIEnv *env, jclass cl, jlong format_reference) {

	xcb_format_t *format = (xcb_format_t *)format_reference;

	return format->depth;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_format_1bits_1per_1pixel
  (JNIEnv *env, jclass cl, jlong format_reference) {

	xcb_format_t *format = (xcb_format_t *)format_reference;

	return format->bits_per_pixel;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_format_1scanline_1pad
  (JNIEnv *env, jclass cl, jlong format_reference) {

	xcb_format_t *format = (xcb_format_t *)format_reference;

	return format->scanline_pad;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1root
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->root;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1default_1colormap
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->default_colormap;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1white_1pixel
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->white_pixel;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1black_1pixel
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->black_pixel;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1current_1input_1masks
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->current_input_masks;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1width_1in_1pixels
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->width_in_pixels;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1height_1in_1pixels
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->height_in_pixels;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1width_1in_1millimeters
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->width_in_millimeters;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1height_1in_1millimeters
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->height_in_millimeters;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1min_1installed_1maps
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->min_installed_maps;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1max_1installed_1maps
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->max_installed_maps;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1root_1visual
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->root_visual;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1backing_1stores
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->backing_stores;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1save_1unders
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->save_unders;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_screen_1root_1depth
  (JNIEnv *env, jclass cl, jlong screen_reference) {

	xcb_screen_t *screen = (xcb_screen_t *)screen_reference;

	return screen->root_depth;
}

JNIEXPORT jbyte JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_depth_1get_1depth
  (JNIEnv *env, jclass cl, jlong depth_reference) {

	xcb_depth_t *depth = (xcb_depth_t *)depth_reference;

	return depth->depth;
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

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1send_1request
  (JNIEnv *env, jclass cl, jlong connection_reference, jbyteArray vector, jint opcode, jboolean isvoid) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	jbyte *native_vector = (*env)->GetByteArrayElements(env, vector, NULL);

	int length = (*env)->GetArrayLength(env, vector);

	xcb_protocol_request_t request = {
		1,
		NULL,
		opcode,
		isvoid
	};

	struct iovec io [] = {
		{ NULL, 0 },
		{ NULL, 0 },
		{
			native_vector,
			length
		}
	};

	jint sequence_number = xcb_send_request(
			connection,
			XCB_REQUEST_CHECKED,
			&io[2],
			&request);

	xcb_flush(connection);

	(*env)->ReleaseByteArrayElements(env, vector, native_vector, 0);

	return sequence_number;
}


JNIEXPORT jbyteArray JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1wait_1reply
  (JNIEnv *env, jclass cl, jlong connection_reference, jint sequence_number) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	xcb_generic_error_t *error;

	void *reply = xcb_wait_for_reply(connection, sequence_number, &error);

	jbyteArray result;

	if (reply == NULL) {
		printf("## got reply error %d %08x\n", error->error_code, error->resource_id);

		free(error);

		result = NULL;
	}
	else {

		int size = 32 + ((uint32_t *)reply)[1] * 4;

		result = (*env)->NewByteArray(env, size);

		(*env)->SetByteArrayRegion(env, result, 0, size, reply);

		free(reply);
	}

	return result;
}

JNIEXPORT jint JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1wait_1for_1event
  (JNIEnv *env, jclass cl, jlong connection_reference) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	xcb_generic_event_t *event = xcb_wait_for_event(connection);

	int response_type = event->response_type;

	free(event);

	return response_type;
}


JNIEXPORT jbyteArray JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_xcb_1poll_1for_1event
  (JNIEnv *env, jclass cl, jlong connection_reference) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	xcb_generic_event_t *event = xcb_poll_for_event(connection);

	jbyteArray result = NULL;

	// printf("## poll for event %p\n", event);

	if (event == NULL) {
		result = NULL;
	}
	else {

		printf("## got event %d\n", event->response_type);

		if (event->response_type == 0) {
			xcb_generic_error_t *error = (xcb_generic_error_t *)event;

			printf("## error %d %d %d %d %08x\n",
					error->error_code,
					error->sequence,
					error->major_code,
					error->minor_code,
					error->resource_id);
		}

		int response_type = event->response_type;
		int size;

		switch (response_type) {
		case XCB_EXPOSE:
			size = sizeof(xcb_expose_event_t);
			break;

		case XCB_BUTTON_PRESS:
			size = sizeof(xcb_button_press_event_t);
			break;

		case XCB_BUTTON_RELEASE:
			size = sizeof(xcb_button_release_event_t);
			break;

		case XCB_MOTION_NOTIFY:
			size = sizeof(xcb_motion_notify_event_t);
			break;

		default:
			fprintf(stderr, "Unknown event type %d\n", response_type);
			size = 0;
			break;
		}

		if (size == 0) {
			result = NULL;
		}
		else {
			result = (*env)->NewByteArray(env, size);

			(*env)->SetByteArrayRegion(env, result, 0, size, (const jbyte *)event);
		}

		free(event);
	}

	return result;
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_xcb_XCBNative_test
  (JNIEnv *env, jclass cl, jlong connection_reference) {

	xcb_connection_t *connection = (xcb_connection_t *)connection_reference;

	int wid = xcb_generate_id(connection);

	const xcb_setup_t *setup = xcb_get_setup(connection);

	int window_depth = 24;

	xcb_screen_iterator_t iterator = xcb_setup_roots_iterator(setup);

	int num_screens = xcb_setup_roots_length(setup);

	const xcb_screen_t *screen = NULL;

	for (int dst_idx = 0; dst_idx < num_screens; ++ dst_idx) {

		if (iterator.data->root_depth == window_depth) {
			screen = iterator.data;
			break;
		}

		if (iterator.rem > 0) {
			xcb_screen_next(&iterator);
		}
	}

	xcb_create_window_value_list_t window_attributes;

	uint32_t window_mask = XCB_CW_BACKING_STORE | XCB_CW_EVENT_MASK;

	// window_attributes.background_pixel = 0xFFFFFF;
	window_attributes.backing_store = XCB_BACKING_STORE_ALWAYS;
	window_attributes.event_mask = XCB_EVENT_MASK_EXPOSURE|XCB_EVENT_MASK_STRUCTURE_NOTIFY;

	// void *window_attrs;

	// xcb_create_window_value_list_serialize(&window_attrs, window_mask, &window_attributes);

	printf("## create window\n");

	xcb_create_window_aux(
			connection,
			screen->root_depth,
			wid,
			screen->root,
			150, 150,
			1024, 768,
			0,
			XCB_WINDOW_CLASS_INPUT_OUTPUT,
			screen->root_visual,
			window_mask,
			&window_attributes);


	xcb_map_window(connection, wid);

	int gc = xcb_generate_id(connection);

	xcb_create_gc_value_list_t value_list;

	value_list.function = XCB_GX_COPY;
	value_list.plane_mask = 0xFFFFFFFF;
	value_list.foreground = 0xAAAAAA;
	value_list.fill_style = XCB_FILL_STYLE_SOLID;
	value_list.graphics_exposures = 0;


	int value_mask = XCB_GC_FUNCTION|XCB_GC_PLANE_MASK|XCB_GC_FOREGROUND
			| XCB_GC_FILL_STYLE
			// |XCB_GC_GRAPHICS_EXPOSURES
			;

	printf("## create gc\n");
	xcb_create_gc_aux(connection, gc, wid, value_mask, &value_list);

	xcb_rectangle_t rectangle = {
			250, 250,
			300, 300
	};

	xcb_flush(connection);

	xcb_generic_event_t *event;

	printf("## events\n");

	while (NULL != (event = xcb_wait_for_event(connection))) {
		printf("## response code %d\n", event->response_type);

		switch (event->response_type & ~0x80) {
		case XCB_EXPOSE:

			printf("## render rectangle\n");
			fflush(stdout);
			xcb_poly_fill_rectangle(connection, wid, gc, 1, &rectangle);

			xcb_flush(connection);
			break;

		case XCB_DESTROY_NOTIFY:
			free(event);
			return;
		}

		free(event);
	}

}

