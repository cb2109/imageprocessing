package user.cb2109.imageprocessing.imageprocessing.transformations;

import java.awt.image.BufferedImage;

/**
 * Author: Christopher Bates
 * Date: 06/02/2018
 */
public class RobertCrossEdgeTransformation extends ConvolutionTransformation {

    private final int[][] kernelX = new int[][]{
            {1, 0},
            {0, -1}
    };
    private final int[][] kernelY = new int[][]{
            {0, 1},
            {-1, 0}
    };

    @Override
    protected int getKernelHeight() {
        return kernelX[0].length;
    }

    @Override
    protected int getKernelWidth() {
        return kernelX.length;
    }

    @Override
    protected int calculateConvolutionIntensity(BufferedImage input, int x, int y) {
        // alpha value is in the 16-31 bit range
        int initialAlpha = (input.getRGB(x, y) >> 24) & 0xff;

        int kernelWidth = getKernelWidth();
        int kernelHeight = getKernelHeight();
        int gradientX = 0;
        int gradientY = 0;

        // sum across each element in the kernel
        for (int i = 0; i < kernelWidth; i++) {
            for (int j = 0; j < kernelHeight; j++) {
                // the greyscale intensity of the pixel in question (each 8 bits is the same 255 colors)
                int pixelIntensity = input.getRGB(x + i, y + j) & 255;
                gradientX += pixelIntensity * kernelX[i][j];
                gradientY += pixelIntensity * kernelY[i][j];
            }
        }
        return Math.min(Math.abs(gradientX) + Math.abs(gradientY), 255);
    }
}
