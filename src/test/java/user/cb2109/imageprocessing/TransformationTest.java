package user.cb2109.imageprocessing;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import user.cb2109.imageprocessing.imageprocessing.TransformationSet;
import user.cb2109.imageprocessing.imageprocessing.transformations.GaussianSmoothingTransformation;
import user.cb2109.imageprocessing.imageprocessing.transformations.GreyscaleTransformation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Author: Christopher Bates
 * Date: 28/01/2018
 */
public class TransformationTest {

    private final String testImagePath = "src/test/resources/";
    private final String outputPath = "src/test/resources/output/";
    private BufferedImage imgBill;
    private BufferedImage imgCamera;
    private Map<String, BufferedImage> outputs;

    @BeforeClass
    public void loadsImage() throws IOException {
        outputs = new HashMap<>();
        this.imgBill = ImageIO.read(new File(testImagePath + "billGates1.jpg"));
        this.imgCamera = ImageIO.read(new File(testImagePath + "camera1.gif"));


        File dir = new File(outputPath);

        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("Failed to create directory");
            }
        } else if (!dir.isDirectory()) {
            throw new IOException(outputPath + " is not a directory");
        }
        File[] fileList = dir.listFiles();
        if (fileList == null) {
            throw new IOException("Invalid file created");
        }
        for(File file: fileList) {
            if (!file.isDirectory()) {
                if (!file.delete()) {
                    throw new IOException("Could not delete file " + file.getAbsolutePath());
                }
            }
        }

        assertEquals(imgBill.getHeight(null), 416);
        assertEquals(imgBill.getWidth(null), 416);

        outputs.put("originalBill.jpg", imgBill);
        outputs.put("originalCamera.jpg", imgCamera);
    }

    @Test
    public void blackAndWhiteBill() {
        TransformationSet set = new TransformationSet();
        set.add(new GreyscaleTransformation());
        BufferedImage output = set.runChain(this.imgBill);
        outputs.put("blackAndWhiteBill.jpg", output);
    }

    @Test
    public void blackAndWhiteCamera() {
        TransformationSet set = new TransformationSet();
        set.add(new GreyscaleTransformation());
        BufferedImage output = set.runChain(this.imgCamera);
        outputs.put("blackAndWhiteCamera.jpg", output);
    }

    @Test
    public void gaussianSmoothedBill() {
        TransformationSet set = new TransformationSet();
        set.add(new GreyscaleTransformation());
        set.add(new GaussianSmoothingTransformation());
        BufferedImage output = set.runChain(this.imgBill);
        outputs.put("gaussianSmoothingBill.jpg", output);
    }

    @Test
    public void gaussianSmoothedCamera() {
        TransformationSet set = new TransformationSet();
        set.add(new GreyscaleTransformation());
        set.add(new GaussianSmoothingTransformation());
        BufferedImage output = set.runChain(this.imgCamera);
        outputs.put("gaussianSmoothingCamera.jpg", output);
    }

    @AfterClass
    public void outputImages() throws IOException {
        for (String outputName : outputs.keySet()) {
            BufferedImage img = outputs.get(outputName);
            ImageIO.write(img, "jpg", new File(outputPath + outputName));
        }
    }


}
