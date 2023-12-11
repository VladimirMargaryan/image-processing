package org.example.HW01;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.PlugIn;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Task1 implements PlugIn {

    private static final String CRS_FILE_PATH = Paths.get("").toAbsolutePath() + File.separator + "tre-s-92.crs";
    private static final String STU_FILE_PATH = Paths.get("").toAbsolutePath() + File.separator + "tre-s-92.stu";

    @Override
    public void run(String args) {
        final int N = readLines(CRS_FILE_PATH).size();

        final ImageProcessor imageProcessor = new BinaryProcessor(new ByteProcessor(N, N));

        imageProcessor.setColor(Color.WHITE);
        imageProcessor.fill();
        imageProcessor.setColor(Color.BLACK);

        readLines(STU_FILE_PATH)
                .stream()
                .map(line -> line.trim().split("\\W+"))
                .filter(tokens -> tokens.length > 1)
                .flatMap(this::createPoints)
                .forEach(pixel -> imageProcessor.drawPixel(pixel.x, pixel.y));

        final String filename = this.getClass().getSimpleName();
        final String filePath = String.join(
                File.separator,
                Paths.get("").toAbsolutePath().toString(),
                filename + ".png"
        );

        final ImagePlus image = new ImagePlus(filename, imageProcessor);
        new FileSaver(image).saveAsPng(filePath);
        IJ.log("Created file path: " + filePath);

        image.show();
    }

    private Stream<Pixel> createPoints(String[] tokens) {
        return Arrays.stream(tokens)
                .flatMap(token1 -> Arrays.stream(tokens)
                        .map(token2 -> new Pixel(Integer.parseInt(token1), Integer.parseInt(token2))))
                .filter(pixel -> pixel.x != pixel.y);
    }

    private List<String> readLines(String path) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            IJ.log("Can't read the file: " + path);
            System.exit(0);
        }

        return lines;
    }

    private static class Pixel {
        private final int x;
        private final int y;

        private Pixel(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}