package user.cb2109.imageprocessing.imageprocessing.transformations;

import java.awt.image.BufferedImage;

/**
 * Author: Christopher Bates
 * Date: 31/01/2018
 */
public class GaussianSmoothingTransformation extends ConvolutionTransformation {

    private double[][] normalizedKernel;

    public GaussianSmoothingTransformation() {
        this(5, 1f);
    }

    /*
     * Compute the normalized Gaussian convolution kernel from the size and standard deviation
     * The larger the kernel the more pixels will be taken into consideration when "blurring"
     * The larger the standard deviation, the more edge pixels will be taken into account when
     * creating the final image.
     */
    public GaussianSmoothingTransformation(int kernelSize, double standardDeviation) {
        if (kernelSize < 3 || kernelSize % 2 == 0) {
            throw new IllegalArgumentException("Gaussian kernel size must be an odd number greater 1");
        }
        if (standardDeviation < 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }

        int maxDistFromCenterOfKernel = ((kernelSize - 1) / 2) + 1;
        normalizedKernel = new double[kernelSize][kernelSize];
        double twoVariance = 2 * standardDeviation * standardDeviation;

        // constant that ensures that all points on the kernel add up to 1
        double normalizingConstant = Math.PI * twoVariance;
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                int iDist = ((i + 1) - maxDistFromCenterOfKernel);
                int jDist = ((j + 1) - maxDistFromCenterOfKernel);

                /*
                 * this is the distance between the point in the gaussian filter and center of the kernel
                 * adjusted for the given variance
                 */
                double exponent = -((iDist * iDist) + (jDist * jDist)) / twoVariance;
                normalizedKernel[i][j] = Math.exp(exponent) / normalizingConstant;
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
        double convolutedPixelValue = 0;
        // sum across each element in the kernel
        for (int i = 0; i < kernelWidth; i++) {
            for (int j = 0; j < kernelHeight; j++) {
                // the greyscale intensity of the pixel in question (each 8 bits is the same 255 colors)
                int pixelIntensity = input.getRGB(x + i, y + j) & 255;
                double kernelFactor = normalizedKernel[i][j];
                convolutedPixelValue += pixelIntensity * kernelFactor;
            }
        }
        // the greyness of the output pixel (cast to int is safe because of bitwise and)
        return (int) Math.round(convolutedPixelValue) & 0xff;
    }
}
