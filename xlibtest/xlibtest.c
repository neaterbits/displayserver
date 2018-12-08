

#include <stdio.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>


int main(int argc, char **argv) {

	Display *display = XOpenDisplay(NULL);
	Window window;
	Window rootWindow;

	if (display == NULL) {
		fprintf(stderr, "Failed to open display\n");
	}

	const int screen = DefaultScreen(display);

	rootWindow = RootWindow(display, screen);

	printf("Rootwindow: %08lx\n", rootWindow);

	window = XCreateSimpleWindow(
			display,
			rootWindow,
			0, 0,
			1024, 768,
			0,
			BlackPixel(display, screen),
			WhitePixel(display, screen));


	XSelectInput(display, window, ExposureMask | KeyPressMask | ButtonPressMask | StructureNotifyMask);

	XMapWindow(display, window);

	for (;;) {
		XEvent event;

		XNextEvent(display, &event);

		printf("Received event %d\n", event.type);

		if (event.type == KeyPress) {
			break;
		}
	}

	XDestroyWindow(display, window);

	XCloseDisplay(display);

	return 0;
}

