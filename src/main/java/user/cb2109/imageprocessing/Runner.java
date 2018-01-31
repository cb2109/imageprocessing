package user.cb2109.imageprocessing;

import user.cb2109.imageprocessing.imageprocessing.TransformationSet;
import user.cb2109.imageprocessing.imageprocessing.transformations.GreyscaleTransformation;

import java.awt.image.BufferedImage;

/**
 * Author: Christopher Bates
 * Date: 28/01/2018
 */
public class Runner {

    private final BufferedImage img;

    Runner(BufferedImage img) {
        this.img = img;
    }

    public BufferedImage processImage() {

        TransformationSet set = new TransformationSet();
        set.add(new GreyscaleTransformation());

        set.runChain(this.img);

        return this.img;

    }


}
