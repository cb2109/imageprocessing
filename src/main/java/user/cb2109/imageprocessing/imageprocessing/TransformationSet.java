package user.cb2109.imageprocessing.imageprocessing;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

/**
 * Author: Christopher Bates
 * Date: 31/01/2018
 */
public class TransformationSet extends ArrayDeque<ImageTransformation> {

    public void runChain(BufferedImage image) {
        for (ImageTransformation transform : this) {
            transform.transform(image);
        }
    }
}
