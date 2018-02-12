package user.cb2109.imageprocessing.imageprocessing.intensitytransformations;

import user.cb2109.imageprocessing.imageprocessing.IntensityTransformation;

/**
 * Author: Christopher Bates
 * Date: 08/02/2018
 */
public class CannyEdgeDetectorTransformation implements IntensityTransformation {

    private final GaussianSmoothingTransformation gaussianFilter;
    private final double highFilterThreshold;
    private final double lowFilterThreshold;

    public CannyEdgeDetectorTransformation() {
        this(3, 1d, 255, 100);
    }

    public CannyEdgeDetectorTransformation(int gaussianKernelSize, double gaussianStandardDeviation, double highFilterThreshold, double lowFilterThreshold) {

        this.gaussianFilter = new GaussianSmoothingTransformation(gaussianKernelSize, gaussianStandardDeviation);
        this.highFilterThreshold = highFilterThreshold;
        this.lowFilterThreshold = lowFilterThreshold;
    }

    /*
     * Sobel edge detector kernels
     */
    private final double[][] kernelX = new double[][]{
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };
    private final double[][] kernelY = new double[][]{
            { 1,  2,  1},
            { 0,  0,  0},
            {-1, -2, -1}
    };

    /*
     * Not all kernels have the same idea of North for the angles
     */
    private final int kernelAngleAdjustment = 90;

    @Override
    public double[][] transform(double[][] image) {
        int imgWidth = image.length;
        int imgHeight = image[0].length;

        double[][] blurredImage = gaussianFilter.transform(image);

        /*
         * Use a vertical and horizontal based kernel to determine
         * how pixel intensity changes between the current pixel
         * and the neighbouring ones
         */
        double[][] convolutionX = (new ConvolutionTransformation() {
            @Override
            protected double[][] getKernel() {
                return kernelX;
            }
        }).transform(blurredImage);

        double[][] convolutionY = (new ConvolutionTransformation() {
            @Override
            protected double[][] getKernel() {
                return kernelY;
            }
        }).transform(blurredImage);

        // calculate the intensity of the edge in the given pixel
        double[][] intensityMap = new double[imgWidth][imgHeight];
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                double intensity1 = convolutionX[x][y];
                double intensity2 = convolutionY[x][y];
                // how strong the edge is in the image
                intensityMap[x][y] = Math.sqrt(intensity1 * intensity1 + intensity2 * intensity2);
            }
        }

        double[][] filteredIntensityMap = computeIntensityDirectionality(convolutionX, convolutionY, intensityMap);

        return markStrongWeakPixels(filteredIntensityMap);

    }

    private double[][] computeIntensityDirectionality(double[][] convolutionX, double[][] convolutionY, double[][] intensityMap) {
        int imgWidth = intensityMap.length;
        int imgHeight = intensityMap[0].length;
        double[][] output = new double[imgWidth][imgHeight];
        /*
         * Iterate through the two kernel results as applied above
         * Calculate the "intensity" of the current edge
         * Check the intensities of the pixels on the side the edge is pointing to see
         * if the edge continues.
         *
         * we miss the outer most ring of pixel so we don't have to do boundary checking on the array
         * as we iterate through it
         */
        for (int x = 1; x < imgWidth - 1; x++) {
            for (int y = 1; y < imgHeight - 1; y++) {
                double intensityX = convolutionX[x][y];
                double intensityY = convolutionY[x][y];
                // how strong the edge is in the image
                double currentEdgeIntensity = intensityMap[x][y];
                if (currentEdgeIntensity == 0) {
                    // the edge is a 0 edge so it must have 0 intensity
                    continue;
                }
                // the "angle" of the edge in the image (edges that are 180deg are the same as edges 0deg)
                double edgeAngleRads = Math.atan2(intensityY, intensityX);

                /*
                 * The below comment is in degrees and not rads to make it easier
                 * The angle is between -180 and +180. We want to round it to 0, 45, 90 or 135
                 * so we can tell the "direction" of the edge.
                 * To do this we reduce each 45degree set to a single digit on the number line e.g.
                 * -180 = -4
                 * -135 = -3
                 * -90 = -2
                 * -45 = -1
                 * 0 = 0
                 * 45 = 1
                 * ...
                 */
                // Calculate where the the angle is on the -4 to 4 number line (instead of -pi to +pi)
                double ratioAngle = (edgeAngleRads * 4) / Math.PI;
                // rounded to an int so we can talk about our "canonical" directions of -180 -135 ... 135 180
                long roundedAngle = Math.round(ratioAngle) * 180 / 4;
                // not all kernels compute angle in the same direction
//                roundedAngle += this.kernelAngleAdjustment;
                // ensure all lines only have 1 valid direction (e.g. -135 = 45 for a line)
                long normalizedAngle = (roundedAngle + 180) % 180;

                /*
                 * If we have an edge intensity in a certain direction for this pixel, check that
                 * there is not a more prevalent edge on either side of this pixel in the wrong direction.
                 * e.g. if the edge is indicated as north-south then check that the edges that
                 * are east and west are not more powerful
                 *
                 * NOTE: It appears that 0 degrees is East, 45 is NorthEast and so on
                 */
                double outputIntensity = 0;
                if (normalizedAngle == 0) {
                    // east-west so check if the edges north and south are more intense
                    if (intensityMap[x][y + 1] <= currentEdgeIntensity || intensityMap[x][y - 1] <= currentEdgeIntensity) {
                        outputIntensity = currentEdgeIntensity;
                    }
                } else if (normalizedAngle == 45) {
                    // northeast-southwest so check if the edges northwest and southeast are more intense
                    if (intensityMap[x + 1][y - 1] <= currentEdgeIntensity || intensityMap[x - 1][y + 1] <= currentEdgeIntensity) {
                        outputIntensity = currentEdgeIntensity;
                    }
                } else if (normalizedAngle == 90) {
                    // north-south so check if the edges east and west are more intense
                    if (intensityMap[x + 1][y] <= currentEdgeIntensity || intensityMap[x - 1][y] <= currentEdgeIntensity) {
                        outputIntensity = currentEdgeIntensity;
                    }
                } else if (normalizedAngle == 135) {
                    // southeast-northwest so check if edges southwest or northeast are more intense
                    if (intensityMap[x - 1][y - 1] <= currentEdgeIntensity || intensityMap[x + 1][y + 1] <= currentEdgeIntensity) {
                        outputIntensity = currentEdgeIntensity;
                    }
                } else {
                    throw new Error("Wrong angle: " + roundedAngle);
                }
                output[x][y] = outputIntensity;
            }
        }
        return output;
    }

    private double[][] markStrongWeakPixels(double[][] filteredIntensityMap) {
        int imgWidth = filteredIntensityMap.length;
        int imgHeight = filteredIntensityMap[0].length;
        double[][] output = new double[imgWidth][imgHeight];
        /*
         * Filter edges based on a low-high threshold:
         * 1) if above high threshold then strong edge so mark as 2
         * 2) If between low-high threshold then weak edge so mark as 1
         * 3) If below low threshold then no edge so mark as 0
         */
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                double currentIntensity = filteredIntensityMap[x][y];
                if (currentIntensity >= highFilterThreshold) {
                    output[x][y] = 2;
                } else if (currentIntensity >= lowFilterThreshold) {
                    output[x][y] = 1;
                } else {
                    output[x][y] = 0;
                }
            }
        }
        return output;
    }
}
