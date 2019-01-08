package com.neaterbits.displayserver.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.framebuffer.common.Encoder;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.framebuffer.common.OffscreenBufferProvider;
import com.neaterbits.displayserver.framebuffer.common.OutputConnector;
import com.neaterbits.displayserver.framebuffer.common.RenderingProvider;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;
import com.neaterbits.displayserver.windows.config.DisplayConfig;

public class DisplayAreaFinder {

    public static DisplayArea makeDisplayArea(
            DisplayAreaConfig config,
            GraphicsDriver graphicsDriver) {
        
        final List<Output> outputs = findMostViableOutputCombination(graphicsDriver);
        
        final DisplayArea displayArea;
        
        if (outputs == null) {
            displayArea = null;
        }
        else {
            displayArea = computeDisplayArea(config, outputs);
        }
        
        return displayArea;
    }

    private static DisplayArea computeDisplayArea(
            DisplayAreaConfig config,
            List<Output> outputs) {
        
        if (outputs.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        final List<DisplayConfig> displayConfigs = config.getDisplays();
        
        int rowWidth = 0;
        int rowWidthInMillimeters = 0;
        
        int width = 0;
        int widthInMillimeters = 0;
        
        int rowHeight = 0;
        int rowHeightInMillimeters = 0;
        
        int height = 0;
        int heightInMillimeters = 0;
        
        int depth = -1;
        PixelFormat pixelFormat = null;

        final List<ViewPort> viewPorts = new ArrayList<>(outputs.size());
        
        for (int i = 0; i < displayConfigs.size(); ++ i) {
            
            final DisplayConfig displayConfig = displayConfigs.get(i);
            
            final Output output = outputs.stream()
                    .filter(vp -> vp.getDisplayDevice().getDisplayDeviceId().equals(displayConfig.getDeviceId()))
                    .findFirst()
                    .orElse(null);
            
            if (output == null) {
                throw new IllegalStateException();
            }
            
            
            final Size frameBufferSize = output.getFrameBuffer().getFrameBufferSize();
            final int frameBufferDepth = output.getFrameBuffer().getDepth();
            final PixelFormat frameBufferPixelFormat = output.getFrameBuffer().getPixelFormat();
            
            if (depth == -1) {
                depth = frameBufferDepth;
            }
            else {
                if (depth != frameBufferDepth) {
                    throw new IllegalStateException();
                }
            }
            
            if (pixelFormat == null) {
                pixelFormat = frameBufferPixelFormat;
            }
            else {
                if (!pixelFormat.equals(frameBufferPixelFormat)) {
                    throw new IllegalStateException();
                }
            }
            
            final int viewPortX = rowWidth;
            final int viewPortY = height;
            
            rowWidth += frameBufferSize.getWidth();
            
            if (frameBufferSize.getHeight() > rowHeight) {
                rowHeight = frameBufferSize.getHeight();
            }
           
            final Size displaySizeInMillimeters = output.getDisplayDevice().getSizeInMillimeters();
            
            rowWidthInMillimeters += displaySizeInMillimeters.getWidth();
            
            if (displaySizeInMillimeters.getHeight() > rowHeightInMillimeters) {
                rowHeightInMillimeters = displaySizeInMillimeters.getHeight();
            }
            
            if (displayConfigs.size() == 1 || i % config.getColumnCount() == 0 && i > 0) {
                
                height += rowHeight;
                heightInMillimeters += rowHeightInMillimeters;
                
                rowHeight = 0;
                rowHeightInMillimeters = 0;
                
                if (rowWidth > width) {
                    width = rowWidth;
                }
                
                if (rowWidthInMillimeters > widthInMillimeters) {
                    widthInMillimeters = rowWidthInMillimeters;
                }
                
                rowWidth = 0;
                rowHeight = 0;

                final ViewPort viewPort = new ViewPort(
                        output,
                        new Position(viewPortX, viewPortY),
                        frameBufferSize,
                        displaySizeInMillimeters);

                viewPorts.add(viewPort);
            }
        }
        
        final List<RenderingProvider> renderingProviders = outputs.stream()
                .map(output -> output.getRenderer().getProvider())
                .distinct()
                .collect(Collectors.toList());
                
        
        if (renderingProviders.size() != 1) {
            throw new UnsupportedOperationException("TODO - support more than one rendering provider");
        }
        
        final OffscreenBufferProvider offscreenBufferProvider = renderingProviders.get(0);
        
        return new DisplayAreaImpl(
                config,
                new Size(width, height), new Size(widthInMillimeters, heightInMillimeters),
                
                depth, pixelFormat,
                viewPorts,
                offscreenBufferProvider);
    }
    
    
    private static List<Output> findMostViableOutputCombination(GraphicsDriver graphicsDriver) {
        
        final List<RenderingProvider> renderingProviders = graphicsDriver.getRenderingProviders();
        
        final int numProviders = renderingProviders.size();
        
        final List<Encoder> encoders = graphicsDriver.getEncoders();
        
        final int numEncoders = encoders.size();
        
        final int [][] renderingProviderPermutations = findOrderPermutations(numProviders);

        final int [][] encoderPermutations = findOrderPermutations(numEncoders);
        
        final List<OutputConnector> connectedOutputs = graphicsDriver.getOutputConnectors().stream()
                    .filter(outputConnector -> outputConnector.getConnectedDisplay() != null)
                    .collect(Collectors.toList());
        
        final int [][] outputPermutations = findOrderPermutations(connectedOutputs.size());

        final List<ProviderEncoderConnector> scratchArea = new ArrayList<>(); 

        List<ProviderEncoderConnector> mostCombinations = null;
        
        // Match encoder/display to rendering providers
        for (int [] renderingProviderPermutation : renderingProviderPermutations) {
            
            for (int [] encoderPermutation : encoderPermutations) {
                
                for (int [] outputPermutation : outputPermutations) {

                    scratchArea.clear();
                    
                    for (int i = 0; i < outputPermutation.length; ++ i) {
                        
                        if (i >= encoderPermutation.length) {
                            break;
                        }
                        
                        if (i >= renderingProviderPermutations.length) {
                            break;
                        }
                        
                        final int outputIdx = outputPermutation[i];

                        final OutputConnector outputConnector       = connectedOutputs.get(outputIdx);
                        final Encoder encoder                       = encoders.get(
                                encoderPermutation[outputIdx]);
                        
                        final RenderingProvider renderingProvider   = renderingProviders.get(
                                renderingProviderPermutation[outputIdx]);
                        
                        if (
                                renderingProvider.supports(encoder)
                             && encoder.supports(outputConnector)
                             && renderingProvider.supports(outputConnector.getConnectedDisplay())) {

                            
                            scratchArea.add(new ProviderEncoderConnector(
                                    renderingProvider,
                                    encoder,
                                    outputConnector));
                        }
                    }

                    if (   !scratchArea.isEmpty()
                        && (mostCombinations == null || scratchArea.size() > mostCombinations.size())) {
                        
                        mostCombinations = new ArrayList<>(scratchArea);
                        
                    }
                }
            }
        }
        
        return mostCombinations == null
                ? null
                : mostCombinations.stream()
                    .map(combination -> new Output(
                            combination,
                            combination.getConnector().getConnectedDisplay(),
                            combination.getProvider().getFrameBuffer()))
                    .collect(Collectors.toList());
    }
    
    
    private static int[][] findOrderPermutations(int numberOf) {
        
        final ArrayList<int[]> list = new ArrayList<>();


        final int [] scratchArea = new int[numberOf]; 


        permuteSlotValuesAt(0, numberOf, scratchArea, list);
    
        return list.toArray(new int[list.size()][]);
    }

    
    private static void permuteSlotValuesAt(int slotIdx, int numberOf, int [] scratchArea, List<int[]> permutations) {
        
        for (int slotValue = 0; slotValue < numberOf; ++ slotValue) {

            scratchArea[slotIdx] = slotValue;

            if (slotIdx == numberOf - 1) {
                permutations.add(Arrays.copyOf(scratchArea, scratchArea.length));
            }
            else {
                permuteSlotValuesAt(slotIdx + 1, numberOf, scratchArea, permutations);
            }
        }
    }
}
