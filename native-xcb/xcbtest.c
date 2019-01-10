
#include <stdio.h>
#include <stdlib.h>

#define _GNU_SOURCE
#include <dlfcn.h>
#include <xcb/xcb.h>
#include <cairo/cairo.h>
#include <cairo/cairo-xcb.h>
#include <unistd.h>

int main(int argc, char **argv) {

	int screen = 0;

	xcb_connection_t *connection = xcb_connect(NULL, &screen);

	if (connection != NULL) {

		int error = xcb_connection_has_error(connection);

		printf("Opened connection: %d\n", error);

		const xcb_setup_t *setup = xcb_get_setup(connection);

		// printf("setup: %d\n", setup->protocol_major_version);

		xcb_window_t window = xcb_generate_id(connection);

		xcb_screen_t *screen = xcb_setup_roots_iterator(setup).data;

		xcb_depth_t *depth = NULL;

		xcb_depth_iterator_t depth_iterator = xcb_screen_allowed_depths_iterator(screen);

		for (;;) {
			if (depth_iterator.data->depth == 24) {
				depth = depth_iterator.data;
				break;
			}

			if (depth_iterator.rem == 0) {
				break;
			}

			xcb_depth_next(&depth_iterator);
		}

		xcb_visualtype_t *visual = xcb_depth_visuals_iterator(depth).data;

		int width = 720;
		int height = 560;

		if (argc == 1) {
			xcb_create_window(
					connection,
					24,
					window,
					screen->root,
					150, 150,
					720, 560,
					0,
					XCB_WINDOW_CLASS_INPUT_OUTPUT,
					0,
					0,
					NULL);

			error = xcb_connection_has_error(connection);
			printf("Create window: %d\n", error);

			xcb_map_window(connection, window);

			error = xcb_connection_has_error(connection);
			printf("Map window: %d\n", error);

			xcb_flush(connection);

			printf("Window: %d\n", window);
		}
		else if (argc == 2) {
			cairo_surface_t *surface = cairo_xcb_surface_create(
					connection,
					atoi(argv[1]), // window,
					visual,
					width,
					height);

			cairo_t *cr = cairo_create(surface);

			cairo_set_source_rgb(cr, 1.0, 1.0, 1.0);

			cairo_paint(cr);

			cairo_surface_flush(surface);

			xcb_flush(connection);
		}

		sleep(300000);

		xcb_disconnect(connection);
	}

	return 0;
}



