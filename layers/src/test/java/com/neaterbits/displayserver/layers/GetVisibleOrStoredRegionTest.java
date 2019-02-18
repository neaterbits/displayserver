package com.neaterbits.displayserver.layers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public class GetVisibleOrStoredRegionTest extends BaseLayersTest {

    @Test
    public void testOneLayer() {

        final Size size = new Size(450, 350);
        
        final Layer layer = layers.createAndAddToRootLayer(new Position(250, 150), size);

        assertThat(layer).isNotNull();
        
        final LayerRegion region = layers.getVisibleOrStoredRegion(layer);
        
        assertThat(region).isNotNull();
        
        assertThat(region.getRectangles().size()).isEqualTo(1);
        assertThat(region.getRectangles().get(0).left).isEqualTo(0);
        assertThat(region.getRectangles().get(0).top).isEqualTo(0);
        assertThat(region.getRectangles().get(0).width).isEqualTo(size.getWidth());
        assertThat(region.getRectangles().get(0).height).isEqualTo(size.getHeight());
    }
}
