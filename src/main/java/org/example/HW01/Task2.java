package org.example.HW01;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class Task2 implements PlugInFilter {

    @Override
    public int setup(String args, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {

        final int width = imageProcessor.getWidth();
        final int height = imageProcessor.getHeight();

        final int leftWidth = width / 2;
        final int rightWidth = width - leftWidth;
        final int topHeight = height / 2;
        final int bottomHeight = height - topHeight;

        IntStream.range(0, height).parallel().forEach(y -> {
            IntStream.range(0, leftWidth).parallel().forEach(x -> {
                final int leftPixel = imageProcessor.getPixel(x, y);
                final int rightPixel = imageProcessor.getPixel(x + rightWidth, y);
                imageProcessor.putPixel(x, y, rightPixel);
                imageProcessor.putPixel(x + rightWidth, y, leftPixel);
            });
        });


        IntStream.range(0, width).parallel().forEach(x -> {
            IntStream.range(0, topHeight).parallel().forEach(y -> {
                final int topPixel = imageProcessor.getPixel(x, y);
                final int bottomPixel = imageProcessor.getPixel(x, y + bottomHeight);
                imageProcessor.putPixel(x, y, bottomPixel);
                imageProcessor.putPixel(x, y + bottomHeight, topPixel);
            });
        });


        final String filePath = String.join(
                File.separator,
                Paths.get("").toAbsolutePath().toString(),
                "copy.png"
        );

        final ImagePlus image = new ImagePlus(this.getClass().getSimpleName(), imageProcessor);
        new FileSaver(image).saveAsPng(filePath);
        IJ.log("Created file path: " + filePath);

        image.show();
    }
}