
DEBUG_DIR := ./Debug

XLIBTEST := $(DEBUG_DIR)/xlibtest



liblinux_bindings.c : com_neaterbits_runtime__native_NativeMethods.h
INCLUDES := \

LIBS :=	-lX11

$(XLIBTEST) : xlibtest.c
	gcc -Wall $(INCLUDES) -o $@ $^ $(LIBS)  


$(DEBUG_DIR) :
	mkdir $(DEBUG_DIR)

compile : $(DEBUG_DIR) $(XLIBTEST)

clean :
	rm -r $(DEBUG_DIR)

install : compile

all : install


