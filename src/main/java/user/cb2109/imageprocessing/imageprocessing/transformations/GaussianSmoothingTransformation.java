package user.cb2109.imageprocessing.imageprocessing.transformations;

import user.cb2109.imageprocessing.imageprocessing.ImageTransformation;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Author: Christopher Bates
 * Date: 31/01/2018
 */
public class GaussianSmoothingTransformation implements ImageTransformation {

    // standard gaussian kernel for Standard deviation 1
    private final int[][] kernel = new int[][]{
            {1,  4,  7,  4, 1},
            {4, 16, 26, 16, 4},
            {7, 26, 41, 26, 7},
            {1,  4,  7,  4, 1},
            {4, 16, 26, 16, 4}
    };

    private float[][] normalizedKernel;

    /*
     * although we could precompute the normalized kernel, this gives us more
     * freedom to change the kernel easily and also allows me to understand the
     * process for building a kernel and how Gaussian transform works
     */
    public GaussianSmoothingTransformation() {
        // calculate the total value of all ints in the kernel
        int kernelSize = kernel.length;
        int kernelSum = 0;
        for (int[] row : kernel) {
            kernelSum += Arrays.stream(row).sum();
        }

        // create a normalized kernel so that the sum is 1
        normalizedKernel = new float[kernelSize][kernelSize];
        for (int x = 0; x < kernelSize; x++) {
            for (int y = 0; y < kernelSize; y++) {
                normalizedKernel[x][y] = ((float) kernel[x][y]) / kernelSum;
            }
        }
    }


    @Override
    public BufferedImage transform(BufferedImage image) {

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        int kernelWidth = normalizedKernel.length;
        if (kernelWidth == 0) {
            throw new ArrayIndexOutOfBoundsException("Zero sized kernel");
        }
        int kernelHeight = normalizedKernel[0].length;

        BufferedImage output = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        // we have to make sure the kernel fits in each time we use it
        // this does mean we lose some data on the left and bottom side of the image
        int widthBoundary = imgWidth - kernelWidth;
        int heightBoundary = imgHeight - kernelHeight;
        // go along each x y pixel of the image
        for (int x = 0; x < widthBoundary; x++) {
            for (int y = 0; y < heightBoundary; y++) {
                output.setRGB(x, y, this.calculateConvolution(image, x, y));
            }
        }

        return output;
    }

    private int calculateConvolution(BufferedImage input, int x, int y) {

        // alpha value is in the 16-31 bit range
        int initialAlpha = (input.getRGB(x, y) >> 24) & 0xff;

        int kernelWidth = normalizedKernel.length;
        if (kernelWidth == 0) {
            throw new ArrayIndexOutOfBoundsException("Zero sized kernel");
        }
        int kernelHeight = normalizedKernel[0].length;
        float convolutedPixelValue = 0;
        // sum across each element in the kernel
        for (int i = 0; i < kernelWidth; i++) {
            for (int j = 0; j < kernelHeight; j++) {
                // the greyscale intensity of the pixel in question (each 8 bits is the same 255 colors)
                int pixelIntensity = input.getRGB(x + i, y + j) & 255;
                float kernelFactor = normalizedKernel[i][j];
                convolutedPixelValue += pixelIntensity * kernelFactor;
            }
        }
        // the greyness of the output pixel
        int outputIntensity = Math.round(convolutedPixelValue) & 0xff;
        // make sure the pixel is correctly alpha + rgb (cannot overflow because each value is &255)
        //noinspection NumericOverflow
        return initialAlpha << 24 | outputIntensity << 16 | outputIntensity << 8 | outputIntensity;
    }
}
