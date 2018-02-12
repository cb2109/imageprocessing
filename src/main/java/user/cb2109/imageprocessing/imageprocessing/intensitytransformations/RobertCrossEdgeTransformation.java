package user.cb2109.imageprocessing.imageprocessing.intensitytransformations;

import user.cb2109.imageprocessing.imageprocessing.IntensityTransformation;

/**
 * Author: Christopher Bates
 * Date: 08/02/2018
 */
public class RobertCrossEdgeTransformation implements IntensityTransformation {

    private final double[][] kernelX = new double[][]{
            {1, 0},
            {0, -1}
    };
    private final double[][] kernelY = new double[][]{
            {0, 1},
            {-1, 0}
    };


    @Override
    public double[][] transform(double[][] image) {
        int imgWidth = image.length;
        int imgHeight = image[0].length;
        double[][] output = new double[imgWidth][imgHeight];
        double[][] transformation1 = (new ConvolutionTransformation() {
            @Override
            protected double[][] getKernel() {
                return kernelX;
            }
        }).transform(image);

        double[][] transformation2 = (new ConvolutionTransformation() {
            @Override
            protected double[][] getKernel() {
                return kernelY;
            }
        }).transform(image);

        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                double intensity1 = transformation1[x][y];
                double intensity2 = transformation2[x][y];
                double outputIntensity = Math.sqrt(intensity1 * intensity1 + intensity2 * intensity2);
                output[x][y] = outputIntensity;
            }
        }

        return output;
    }
}
