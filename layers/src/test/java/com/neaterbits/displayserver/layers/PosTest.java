package com.neaterbits.displayserver.layers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PosTest {

    @Test
    public void testHPos() {
        
        assertThat(Pos.getHPos(0, 25, -1)).isEqualTo(Pos.BEFORE);
        assertThat(Pos.getHPos(0, 25,  0)).isEqualTo(Pos.AT_START);
        assertThat(Pos.getHPos(0, 25,  1)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getHPos(0, 25,  15)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getHPos(0, 25,  23)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getHPos(0, 25,  24)).isEqualTo(Pos.AT_END);
        assertThat(Pos.getHPos(0, 25, 25)).isEqualTo(Pos.AFTER);
        
        assertThat(Pos.getHPos(35, 25, 34)).isEqualTo(Pos.BEFORE);
        assertThat(Pos.getHPos(35, 25,  35)).isEqualTo(Pos.AT_START);
        assertThat(Pos.getHPos(35, 25,  36)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getHPos(35, 25,  50)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getHPos(35, 25,  58)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getHPos(35, 25,  59)).isEqualTo(Pos.AT_END);
        assertThat(Pos.getHPos(35, 25, 60)).isEqualTo(Pos.AFTER);
    }

    @Test
    public void testVPos() {
        
        assertThat(Pos.getVPos(0, 25, -1)).isEqualTo(Pos.BEFORE);
        assertThat(Pos.getVPos(0, 25,  0)).isEqualTo(Pos.AT_START);
        assertThat(Pos.getVPos(0, 25,  1)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getVPos(0, 25,  15)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getVPos(0, 25,  23)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getVPos(0, 25,  24)).isEqualTo(Pos.AT_END);
        assertThat(Pos.getVPos(0, 25, 25)).isEqualTo(Pos.AFTER);
        
        assertThat(Pos.getVPos(35, 25, 34)).isEqualTo(Pos.BEFORE);
        assertThat(Pos.getVPos(35, 25,  35)).isEqualTo(Pos.AT_START);
        assertThat(Pos.getVPos(35, 25,  36)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getVPos(35, 25,  50)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getVPos(35, 25,  58)).isEqualTo(Pos.WITHIN);
        assertThat(Pos.getVPos(35, 25,  59)).isEqualTo(Pos.AT_END);
        assertThat(Pos.getVPos(35, 25, 60)).isEqualTo(Pos.AFTER);
    }
}
