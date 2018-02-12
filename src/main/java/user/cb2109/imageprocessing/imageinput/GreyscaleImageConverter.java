package user.cb2109.imageprocessing.imageinput;

import java.awt.image.BufferedImage;

/**
 * Author: Christopher Bates
 * Date: 10/02/2018
 */
public class GreyscaleImageConverter {

    public double[][] convertToMap(BufferedImage image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double[][] output = new double[imageWidth][imageHeight];
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                output[i][j] = getIntensity(image.getRGB(i, j));
            }
        }
        return output;
    }
    private double getIntensity(int rgb) {
        int bitmask = 0xff;

        // red value is in the 16-31 bit range
        int r = (rgb >> 16) & bitmask;
        // green value is in the 8-15 bit range
        int g = (rgb >> 8) & bitmask;
        // blue is in the 0-7 bit range
        int b = (rgb & bitmask);

        return (r + g + b) / 3d;
    }
}
