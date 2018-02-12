package user.cb2109.imageprocessing.imageprocessing.imagetransformations;

import user.cb2109.imageprocessing.imageprocessing.GaussianKernelGenerator;

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
        this.normalizedKernel = GaussianKernelGenerator.generateGaussianKernel(kernelSize, standardDeviation);
    }

    @Override
    protected double[][] getKernel() {
        return this.normalizedKernel;
    }
}
