
DEBUG_DIR := ./Debug

LIBXCB_SO := $(DEBUG_DIR)/libxcbjni.so
XCBTEST	  := $(DEBUG_DIR)/xcbtest

libxcb_bindings.c : com_neaterbits_displayserver_render_cairo_xcb_XCBNative.h
INCLUDES := \
		-I/usr/lib/jvm/java-8-openjdk-amd64/include/ \
		-I/usr/lib/jvm/java-8-openjdk-amd64/include/linux/

LIBS :=	-lxcb -lcairo

$(LIBXCB_SO) : libxcb_bindings.c
	gcc -Wall $(INCLUDES) -fPIC -shared -o $@ $^ $(LIBS)


$(XCBTEST)	: xcbtest.c
	gcc -Wall $(INCLUDES) -o $@ $^ $(LIBS)
	

$(DEBUG_DIR) :
	mkdir $(DEBUG_DIR)

compile : $(DEBUG_DIR) $(LIBXCB_SO) $(XCBTEST)

clean :
	rm -r $(DEBUG_DIR)

install : compile


all : install


