package user.cb2109.imageprocessing.imageprocessing;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

/**
 * Author: Christopher Bates
 * Date: 31/01/2018
 */
public class ImageTransformationSet extends ArrayDeque<ImageTransformation> {

    public BufferedImage runChain(BufferedImage image) {
        BufferedImage output = image;
        for (ImageTransformation transform : this) {
            output = transform.transform(image);
        }
        return output;
    }
}
