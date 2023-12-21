package org.example.project;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ByteProcessor;
import ij.process.FHT;
import ij.process.ImageProcessor;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class Stage3 {

    public static void main(String[] args) {
        final String imagePath = "src/main/resources/CSHandwriting/H090/OOP.MT2.240315.H090_p2.jpg";

        final ImagePlus binaryImage = IJ.openImage(imagePath.replace(".jpg", "-bin.png"));
        List<Line2D.Double> lines = detectLines(binaryImage);
        analyzeDetectedLines(lines);
    }

    private static List<Line2D.Double> detectLines(ImagePlus binaryImage) {
        ByteProcessor bp = binaryImage.getProcessor().convertToByteProcessor();
        ImageProcessor houghIp = new FHT(bp).getMask();

        return detectLinesWithAnalyzer(houghIp);
    }

    private static List<Line2D.Double> detectLinesWithAnalyzer(ImageProcessor houghIp) {
        final List<Line2D.Double> lines = new ArrayList<>();

        final ResultsTable rt = Analyzer.getResultsTable();
        final int rowCount = rt.getCounter();

        for (int i = 0; i < rowCount; i++) {
            final double theta = rt.getValue("Angle", i);
            final double rho = rt.getValue("Rho", i);

            final double x1 = rho * Math.cos(theta);
            final double y1 = rho * Math.sin(theta);
            final double x2 = x1 + 1000 * Math.sin(theta);
            final double y2 = y1 - 1000 * Math.cos(theta);

            lines.add(new Line2D.Double(x1, y1, x2, y2));
        }

        return lines;
    }

    private static void analyzeDetectedLines(List<Line2D.Double> lines) {
        if (lines.isEmpty()) {
            System.out.println("No lines detected.");
            return;
        }

        double totalBaseline = 0.0;
        double totalSlant = 0.0;
        double totalLineSpacing = 0.0;

        for (int i = 0; i < lines.size() - 1; i++) {
            final Line2D.Double line1 = lines.get(i);
            final Line2D.Double line2 = lines.get(i + 1);
            totalBaseline += Math.abs(line2.getY1() - line1.getY2());
            totalSlant += Math.atan2(line2.getY1() - line1.getY2(), line2.getX1() - line1.getX2());
            totalLineSpacing += line2.getY1() - line1.getY2();
        }

        double averageBaseline = totalBaseline / (lines.size() - 1);
        double averageSlant = totalSlant / (lines.size() - 1);
        double averageLineSpacing = totalLineSpacing / (lines.size() - 1);

        System.out.println("Average Baseline: " + averageBaseline);
        System.out.println("Average Slant: " + averageSlant);
        System.out.println("Average Line Spacing: " + averageLineSpacing);
    }
}
