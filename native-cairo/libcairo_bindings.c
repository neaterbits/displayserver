
#include <stdio.h>
#include <string.h>

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

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1source_1rgb
  (JNIEnv *env, jclass cl, jlong cairo_reference, jdouble red, jdouble green, jdouble blue) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_set_source_rgb(cr, red, green, blue);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1set_1fill_1rule
  (JNIEnv *env, jclass cl, jlong cairo_reference, jint fill_rule) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_set_fill_rule(cr, fill_rule);
}

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1fill
  (JNIEnv *env, jclass cl, jlong cairo_reference) {

	cairo_t *cr = (cairo_t *)cairo_reference;

	cairo_fill(cr);
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

JNIEXPORT void JNICALL Java_com_neaterbits_displayserver_render_cairo_CairoNative_cairo_1surface_1flush
  (JNIEnv *env, jclass cl, jlong surface_reference) {

	cairo_surface_t *surface = (cairo_surface_t *)surface_reference;

	cairo_surface_flush(surface);

	printf("cairo status after flush: %s\n", cairo_status_to_string(cairo_surface_status(surface)));

	printf("cairo device status after flush: %s\n", cairo_status_to_string(cairo_device_status(cairo_surface_get_device(surface))));
}



/*
JNIEXPORT jint JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_getReferenceSizeInBytes
  (JNIEnv * env, jclass cl) {
	return 8;
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_alloc
  (JNIEnv *env, jclass cl, jint size) {

	return (long)malloc(size);
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_allocExecutablePages
  (JNIEnv *env, jclass cl, jint size) {

	const int pageSize = sysconf(_SC_PAGE_SIZE);

	if (pageSize == -1) {
		return -1;
	}

	void *mem;

	if (posix_memalign(&mem, pageSize, pageSize * size) < 0) {
		return -1;
	}

	if (mprotect(mem, pageSize * size, PROT_READ|PROT_WRITE|PROT_EXEC) < 0) {
		return -1;
	}


	return (long)mem;
}


JNIEXPORT jlong JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_realloc
  (JNIEnv *env, jclass cl, jlong address, jint size, jint newSize) {

	void *ptr = (void *)address;

	void *newPtr = realloc(ptr, newSize);

	return (long)newPtr;
}

JNIEXPORT void JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_free
  (JNIEnv *env, jclass cl, jlong address) {

	free((void *)address);

}

JNIEXPORT void JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_setReference
  (JNIEnv *env, jclass cl, jlong address, jint offset, jlong reference) {

	void **mem = (void **)address;

	mem[offset] = (void *)reference;

}

JNIEXPORT jlong JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_getReference
  (JNIEnv *env, jclass cl, jlong address, jint offset) {

	void **mem = (void **)address;

	return (long)mem[offset];
}

JNIEXPORT void JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_putString
  (JNIEnv *env, jclass cl, jlong address, jint offset, jstring string) {

	const char *nativeString = (*env)->GetStringUTFChars(env, string, 0);

	char *ptr = (char *)address;

	strcpy(ptr, nativeString);

	(*env)->ReleaseStringUTFChars(env, string, nativeString);

}

JNIEXPORT void JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_putBytes
  (JNIEnv *env, jclass cl, jlong dstAddress, jint dstOffset, jbyteArray srcArray, jint srcOffset, jint length) {

	jboolean isCopy;

	const jbyte *array = (*env)->GetByteArrayElements(env, srcArray, &isCopy);

	char *dst = (char *)dstAddress;

	memcpy(&dst[dstOffset], &array[srcOffset], length);
}


JNIEXPORT void JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_runCode
  (JNIEnv *env, jclass cl, jlong address) {

	void *ptr = (void *)address;

	asm("call *%0" : :"r"(ptr));
}

JNIEXPORT jlong JNICALL Java_com_neaterbits_runtime__1native_NativeMethods_getFunctionAddress
  (JNIEnv *env, jclass cl, jstring library, jstring function) {

	const char *nativeLibraryString = (*env)->GetStringUTFChars(env, library, 0);

	void *handle = dlopen(nativeLibraryString, RTLD_LAZY);

	(*env)->ReleaseStringUTFChars(env, library, nativeLibraryString);

	if (handle == NULL) {
		return -1;
	}

	const char *nativeFunctionString = (*env)->GetStringUTFChars(env, function, 0);

	void *address = dlsym(handle, nativeFunctionString);

	(*env)->ReleaseStringUTFChars(env, library, nativeFunctionString);

	if (address == NULL) {
		return -1;
	}

	dlclose(handle);

	return (long)address;
}
*/
