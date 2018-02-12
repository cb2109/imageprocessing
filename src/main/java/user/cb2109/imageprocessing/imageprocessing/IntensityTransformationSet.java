package user.cb2109.imageprocessing.imageprocessing;

import java.util.ArrayDeque;

/**
 * Author: Christopher Bates
 * Date: 08/02/2018
 */
public class IntensityTransformationSet extends ArrayDeque<IntensityTransformation> {

    public double[][] runChain(double[][] image) {
        double[][] output = image;
        for (IntensityTransformation transform : this) {
            output = transform.transform(output);
        }
        return output;
    }
}
