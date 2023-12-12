package org.example.project;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Binary;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.Optional;
import java.util.stream.IntStream;

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

        for (int x = 0; x < colorProcessor.getWidth(); x++) {
            for (int y = 0; y < colorProcessor.getHeight(); y++) {
                final Color pixelColor = colorProcessor.getColor(x, y);

                if (pixelColor.getRGB() > new Color(240, 240, 240).getRGB()
                        || pixelColor.getRGB() < new Color(40, 40, 40).getRGB()) {
                    colorProcessor.putPixel(x, y, new int[]{255, 255, 255});
                }
            }
        }

        final ImagePlus tempImage = image.duplicate();
        IJ.run(tempImage, "8-bit", "");
        IJ.run(tempImage, "Convert to Mask", "");
        IntStream.range(0, 5).forEach(i -> IJ.run(tempImage, "Despeckle", ""));
        IJ.run(tempImage, "Create Selection", "");
        IJ.run(tempImage, "Fit Rectangle", "");

        final ImagePlus croppedImage = Optional.ofNullable(tempImage.getRoi())
                .map(roi -> {
                    image.setRoi(roi);
                    ImageProcessor croppedProcessor = image.getProcessor().crop();
                    return new ImagePlus("Cropped Image", croppedProcessor);
                }).orElse(image);

        IJ.saveAs(
                croppedImage,
                "PNG",
                imagePath.split(image.getTitle())[0] + image.getTitle().split(".jpg")[0] + "-bin.png"
        );
    }
}
