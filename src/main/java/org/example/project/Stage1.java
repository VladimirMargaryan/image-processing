package org.example.project;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Binary;
import ij.process.ColorProcessor;

import java.awt.*;

public class Stage1 {
    private static final int TOLERANCE = 40;

    public static void main(String[] args) {
        final String imagePath = "src/main/resources/CSHandwriting/H090/OOP.MT2.240315.H090_p2.jpg";

        final ImagePlus image = IJ.openImage(imagePath);
        final ColorProcessor colorProcessor = (ColorProcessor) image.getProcessor();
        final Binary binary = new Binary();
        binary.setup("", image);
        binary.run(colorProcessor);

        colorProcessor.invert();
        colorProcessor.erode();

        for (int x = 0; x < colorProcessor.getWidth(); x++) {
            for (int y = 0; y < colorProcessor.getHeight(); y++) {
                final Color pixelColor = colorProcessor.getColor(x, y);

                final int red = pixelColor.getRed();
                final int green = pixelColor.getGreen();
                final int blue = pixelColor.getBlue();

                // Removing printed text and instructor mark
                if ((Math.abs(red - green) <= TOLERANCE
                        && Math.abs(red - blue) <= TOLERANCE
                        && Math.abs(green - blue) <= TOLERANCE)
                        || (green > TOLERANCE && blue > TOLERANCE && red < 3 * TOLERANCE)
                        || (y < 5)) {

                    colorProcessor.putPixel(x, y, 0);
                }
            }
        }

        colorProcessor.invert();
        colorProcessor.erode();

        // Detecting region with text and cropping
        IJ.run(image, "8-bit", "");
        IJ.run(image, "Convert to Mask", "");
        IJ.run(image, "Despeckle", "");
        IJ.run(image, "Create Selection", "");
        IJ.run(image, "Fit Rectangle", "");
        IJ.run(image, "Crop", "");

        IJ.saveAs(
                image,
                "PNG",
                imagePath.split(image.getTitle())[0] + image.getTitle().split(".jpg")[0] + "-bin.png"
        );
    }
}
