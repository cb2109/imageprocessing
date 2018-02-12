package user.cb2109.imageprocessing.imageoutput;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

/**
 * Author: Christopher Bates
 * Date: 10/02/2018
 */
public class GreyscaleImageRenderer {

    /*
     * The idea of this function is to take whatever set of values we have for the pixel and move them
     * into the spectrum between 0-255 so we can display them in a grayscale image.
     *
     * We can then output a buffered image that represents the map
     */
    public BufferedImage convertToImage(double[][] intensityMap) {
        int imageWidth = intensityMap.length;
        if (imageWidth < 1) {
            throw new IllegalArgumentException("Pixel map with 0 width");
        }
        int imageHeight = intensityMap[0].length;
        // create a way to iterate through the intensity map (all pixels)
        Supplier<DoubleStream> allPixelStreamSupplier = () -> Arrays.stream(intensityMap).flatMapToDouble(Arrays::stream);

        // go through each pixel to get the max and min value
        double min = allPixelStreamSupplier.get().min().orElse(0);
        double max = allPixelStreamSupplier.get().max().orElse(255);

        double range = max - min;

        double adjustment = -1 * min;
        double scalingFactor = 255 / range;

        return scaleIntoImage(intensityMap, scalingFactor, adjustment);
    }

    private BufferedImage scaleIntoImage(double[][] pixelMap, double scalingFactor, double adjustment) {
        int imageWidth = pixelMap.length;
        int imageHeight = pixelMap[0].length;
        BufferedImage output = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                double outputValue = pixelMap[i][j];
                long intensity = Math.round((outputValue + adjustment) * scalingFactor);
                int pixelIntensity = (int) (intensity & 255);
                // turn the output intensity into a grayscale pixel value
                //noinspection NumericOverflow
                int rgb = 255 << 24 | pixelIntensity << 16 | pixelIntensity << 8 | pixelIntensity;
                output.setRGB(i, j, rgb);
            }
        }
        return output;
    }


}
