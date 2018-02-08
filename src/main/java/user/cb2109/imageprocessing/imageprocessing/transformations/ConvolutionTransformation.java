package user.cb2109.imageprocessing.imageprocessing.transformations;

import user.cb2109.imageprocessing.imageprocessing.ImageTransformation;

import java.awt.image.BufferedImage;

/**
 * Author: Christopher Bates
 * Date: 06/02/2018
 */
public abstract class ConvolutionTransformation implements ImageTransformation {

    protected abstract double[][] getKernel();

    @Override
    public BufferedImage transform(BufferedImage image) {

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        double[][] kernel = getKernel();
        int kernelWidth = kernel.length;
        if (kernelWidth == 0) {
            throw new ArrayIndexOutOfBoundsException("Zero sized kernel");
        }
        int kernelHeight = kernel[0].length;

        BufferedImage output = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        // we have to make sure the kernel fits in each time we use it
        // this does mean we lose some data on the left and bottom side of the image
        int widthBoundary = imgWidth - kernelWidth;
        int heightBoundary = imgHeight - kernelHeight;
        // go along each x y pixel of the image
        for (int x = 0; x < widthBoundary; x++) {
            for (int y = 0; y < heightBoundary; y++) {
                // alpha value is in the 16-31 bit range
                int initialAlpha = (image.getRGB(x, y) >> 24) & 0xff;
                int outputIntensity = this.calculateConvolutionIntensity(image, x, y);
                // make sure the pixel is correctly alpha + rgb (cannot overflow because each value is &255)
                //noinspection NumericOverflow
                int outputRgb = initialAlpha << 24 | outputIntensity << 16 | outputIntensity << 8 | outputIntensity;

                output.setRGB(x, y, outputRgb);
            }
        }

        return output;
    }

    protected int calculateConvolutionIntensity(BufferedImage input, int x, int y) {
        double[][] kernel = getKernel();
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;
        double convolutedPixelValue = 0;
        // sum across each element in the kernel
        for (int i = 0; i < kernelWidth; i++) {
            for (int j = 0; j < kernelHeight; j++) {
                // the greyscale intensity of the pixel in question (each 8 bits is the same 255 colors)
                int pixelIntensity = input.getRGB(x + i, y + j) & 0xff;
                convolutedPixelValue += pixelIntensity * kernel[i][j];
            }
        }
        // the greyness of the output pixel (cast to int is safe because of bitwise and)
        return ((int) Math.round(Math.abs(convolutedPixelValue))) & 0xff;
    }
}
