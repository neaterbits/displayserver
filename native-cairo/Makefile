
DEBUG_DIR := ./Debug

LIBCAIRO_SO := $(DEBUG_DIR)/libcairojni.so

libcairo_bindings.c : com_neaterbits_displayserver_render_cairo_CairoNative.h
INCLUDES := \
		-I/usr/lib/jvm/java-8-openjdk-amd64/include/ \
		-I/usr/lib/jvm/java-8-openjdk-amd64/include/linux/

LIBS :=	-lcairo

$(LIBCAIRO_SO) : libcairo_bindings.c
	gcc -Wall $(INCLUDES) -fPIC -shared -o $@ $^ $(LIBS)

$(DEBUG_DIR) :
	mkdir $(DEBUG_DIR)

compile : $(DEBUG_DIR) $(LIBCAIRO_SO)

clean :
	rm -r $(DEBUG_DIR)

install : compile


all : install


