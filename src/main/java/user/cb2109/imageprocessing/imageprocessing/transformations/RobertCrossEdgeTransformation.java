package user.cb2109.imageprocessing.imageprocessing.transformations;

import user.cb2109.imageprocessing.imageprocessing.ImageTransformation;

import java.awt.image.BufferedImage;

/**
 * Author: Christopher Bates
 * Date: 06/02/2018
 */
public class RobertCrossEdgeTransformation implements ImageTransformation {

    private final double[][] kernelX = new double[][]{
            {1, 0},
            {0, -1}
    };
    private final double[][] kernelY = new double[][]{
            {0, 1},
            {-1, 0}
    };


    @Override
    public BufferedImage transform(BufferedImage image) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        BufferedImage output = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        BufferedImage transformation1 = (new ConvolutionTransformation() {
            @Override
            protected double[][] getKernel() {
                return kernelX;
            }
        }).transform(image);

        BufferedImage transformation2 = (new ConvolutionTransformation() {
            @Override
            protected double[][] getKernel() {
                return kernelY;
            }
        }).transform(image);

        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                int intensity1 = transformation1.getRGB(x, y) & 255;
                int intensity2 = transformation2.getRGB(x, y) & 255;
                int outputIntensity = intensity1 + intensity2;
                int outputRgb = 255 << 24 | outputIntensity << 16 | outputIntensity << 8 | outputIntensity;
                output.setRGB(x, y, outputRgb);
            }
        }

        return transformation1;
    }
}
