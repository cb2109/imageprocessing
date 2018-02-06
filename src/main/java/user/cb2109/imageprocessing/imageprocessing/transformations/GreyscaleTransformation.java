package user.cb2109.imageprocessing.imageprocessing.transformations;

import user.cb2109.imageprocessing.imageprocessing.ImageTransformation;

import java.awt.image.BufferedImage;

/**
 * Author: Christopher Bates
 * Date: 31/01/2018
 */
public class GreyscaleTransformation implements ImageTransformation {
    @Override
    public BufferedImage transform(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = image.getRGB(x, y);
                int greyScaleColor = greyScalePixel(color);
                output.setRGB(x, y, greyScaleColor);
            }
        }
        return output;
    }

    /**
     *
     * @param rgb a red/green/blue color.
     *            The bottom 8 bits are blue, the next 8 bits are green and the next red.
     *            The top 8 bits are the alpha, these are maintained when gray scaling
     * @return the grey scale equivalent of the color
     */
    private int greyScalePixel(int rgb) {
        int bitmask = 0xff;

        // alpha value is in the 16-31 bit range
        int a = (rgb >> 24) & bitmask;
        // red value is in the 16-31 bit range
        int r = (rgb >> 16) & bitmask;
        // green value is in the 8-15 bit range
        int g = (rgb >> 8) & bitmask;
        // blue is in the 0-7 bit range
        int b = (rgb & bitmask);

        int avg = Math.floorDiv(r + g + b, 3);

        return (a << 24) | (avg << 16) | (avg << 8) | avg;
    }
}
