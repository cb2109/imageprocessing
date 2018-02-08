package user.cb2109.imageprocessing.imageprocessing.transformations;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Author: Christopher Bates
 * Date: 31/01/2018
 */
public class GaussianSmoothingTransformation extends ConvolutionTransformation {

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
    protected int getKernelHeight() {
        return this.normalizedKernel[0].length;
    }

    @Override
    protected  int getKernelWidth() {
        return this.normalizedKernel.length;
    }

    @Override
    protected int calculateConvolutionIntensity(BufferedImage input, int x, int y) {

        int kernelWidth = normalizedKernel.length;
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
        return Math.round(convolutedPixelValue) & 0xff;
    }
}
