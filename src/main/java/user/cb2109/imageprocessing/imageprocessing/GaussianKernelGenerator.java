package user.cb2109.imageprocessing.imageprocessing;

/**
 * Author: Christopher Bates
 * Date: 11/02/2018
 */
public class GaussianKernelGenerator {

    public static double[][] generateGaussianKernel(int kernelSize, double standardDeviation) {
        if (kernelSize < 3 || kernelSize % 2 == 0) {
            throw new IllegalArgumentException("Gaussian kernel size must be an odd number greater 1");
        }
        if (standardDeviation < 0) {
            throw new IllegalArgumentException("Standard deviation must be positive");
        }

        int maxDistFromCenterOfKernel = ((kernelSize - 1) / 2) + 1;
        double[][] normalizedKernel = new double[kernelSize][kernelSize];
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
        return normalizedKernel;
    }
}
