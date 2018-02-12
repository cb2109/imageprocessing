package user.cb2109.imageprocessing.imageprocessing.intensitytransformations;

import user.cb2109.imageprocessing.imageprocessing.IntensityTransformation;

/**
 * Author: Christopher Bates
 * Date: 06/02/2018
 */
public abstract class ConvolutionTransformation implements IntensityTransformation {

    protected abstract double[][] getKernel();

    @Override
    public double[][] transform(double[][] image) {

        int imgWidth = image.length;
        int imgHeight = image[0].length;

        double[][] kernel = getKernel();
        int kernelWidth = kernel.length;
        if (kernelWidth == 0) {
            throw new ArrayIndexOutOfBoundsException("Zero sized kernel");
        }
        int kernelHeight = kernel[0].length;

        double[][] output = new double[imgWidth][imgHeight];
        // we have to make sure the kernel fits in each time we use it
        // this does mean we lose some data on the left and bottom side of the image
        int widthBoundary = imgWidth - kernelWidth;
        int heightBoundary = imgHeight - kernelHeight;
        // go along each x y pixel of the image
        for (int x = 0; x < widthBoundary; x++) {
            for (int y = 0; y < heightBoundary; y++) {
                output[x][y] = this.calculateConvolutionIntensity(image, x, y);
            }
        }

        return output;
    }

    private double calculateConvolutionIntensity(double[][] input, int x, int y) {
        double[][] kernel = getKernel();
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;
        double convolutedPixelValue = 0;
        // sum across each element in the kernel
        for (int i = 0; i < kernelWidth; i++) {
            for (int j = 0; j < kernelHeight; j++) {
                // the greyscale intensity of the pixel in question (each 8 bits is the same 255 colors)
                double pixelIntensity = input[x + i][y + j];
                convolutedPixelValue += pixelIntensity * kernel[i][j];
            }
        }
        // the greyness of the output pixel (cast to int is safe because of bitwise and)
        return convolutedPixelValue;
    }
}
