package org.example.project;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.util.stream.IntStream;

public class Stage2 {

    public static void main(String[] args) {
        final String imagePath = "src/main/resources/CSHandwriting/H090/OOP.MT2.240315.H090_p2.jpg";

        final ImagePlus croppedImage = IJ.openImage(imagePath.replace(".jpg", "-bin.png"));
        final ImageStatistics stats = croppedImage.getStatistics();
        final double pageWidth = stats.roiWidth;
        final double pageHeight = stats.roiHeight;
        final double pageOrientation = stats.angle;

        System.out.println("Page Width: " + pageWidth);
        System.out.println("Page Height: " + pageHeight);
        System.out.println("Page Orientation: " + pageOrientation);

        final ImageProcessor processor = croppedImage.getProcessor();
        processor.smooth();
        processor.sharpen();

        IJ.run(croppedImage, "Find Edges", "");

        System.out.println("Average Font Size: " + evaluateFontSize(croppedImage));
    }

    private static double evaluateFontSize(ImagePlus image) {
        IJ.run(image, "Make Binary", "");

        final ResultsTable resultsTable = new ResultsTable();
        final ParticleAnalyzer analyzer = new ParticleAnalyzer(
                ParticleAnalyzer.SHOW_OUTLINES | ParticleAnalyzer.ADD_TO_MANAGER,
                Measurements.AREA | Measurements.RECT,
                resultsTable,
                0,
                Double.POSITIVE_INFINITY
        );
        analyzer.analyze(image);

        final float[] column = resultsTable.getColumn(ResultsTable.AREA);
        final double totalArea = IntStream.range(0, column.length).mapToObj(i -> column[i]).reduce(0f, Float::sum);
        final double averageArea = totalArea / resultsTable.getCounter();

        return Math.sqrt(averageArea);
    }
}
