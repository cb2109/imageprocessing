package user.cb2109.imageprocessing;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import user.cb2109.imageprocessing.imageinput.GreyscaleImageConverter;
import user.cb2109.imageprocessing.imageoutput.GreyscaleImageRenderer;
import user.cb2109.imageprocessing.imageprocessing.intensitytransformations.CannyEdgeDetectorTransformation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Author: Christopher Bates
 * Date: 11/02/2018
 */
public class IntensityTransformationTest {

    private final String testImagePath = "src/test/resources/";
    private final String outputPath = "src/test/resources/outputIntensityMap/";
    private BufferedImage imgBill;
    private BufferedImage imgCamera;
    private BufferedImage imgLine;
    private BufferedImage imgBox;
    private BufferedImage imgClown;
    private BufferedImage imgNoisy;
    private BufferedImage imgLina;
    private Map<String, BufferedImage> outputs;

    @BeforeClass
    public void loadsImage() throws IOException {
        outputs = new HashMap<>();
        this.imgBill = ImageIO.read(new File(testImagePath + "billGates1.jpg"));
        this.imgCamera = ImageIO.read(new File(testImagePath + "camera1.gif"));
        this.imgLine = ImageIO.read(new File(testImagePath + "straightLine.jpg"));
        this.imgBox = ImageIO.read(new File(testImagePath + "box.jpg"));
        this.imgClown = ImageIO.read(new File(testImagePath + "clown.gif"));
        this.imgNoisy = ImageIO.read(new File(testImagePath + "noisyImage.gif"));
        this.imgLina = ImageIO.read(new File(testImagePath + "linaImage.png"));


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
    }

    @Test
    public void cannyEdgeDetectedLine() {
        outputs.put("edgeAdjustedLine", runCannyEdgeDetector(this.imgLine));
    }


    @Test
    public void cannyEdgeDetectedBill() {
        outputs.put("edgeAdjustedBill", runCannyEdgeDetector(this.imgBill));
    }

    @Test
    public void cannyEdgeDetectedCamera() {
        outputs.put("edgeAdjustedCamera", runCannyEdgeDetector(this.imgCamera));
    }

//    @Test
//    public void cannyEdgeDetectedBox() {
//        outputs.put("edgeAdjustedBox", runCannyEdgeDetector(this.imgBox));
//    }

    @Test
    public void cannyEdgeDetectedClown() {
        outputs.put("edgeAdjustedClown", runCannyEdgeDetector(this.imgClown));
    }

    @Test
    public void cannyEdgeDetectedNoisy() {
        outputs.put("edgeAdjustedNoisy", runCannyEdgeDetector(this.imgNoisy));
    }

    @Test
    public void cannyEdgeDetectedLina() {
        outputs.put("edgeAdjustedLina", runCannyEdgeDetector(this.imgLina));
    }


    private BufferedImage runCannyEdgeDetector(BufferedImage input) {
        double[][] intensityMap = (new GreyscaleImageConverter()).convertToMap(input);


        CannyEdgeDetectorTransformation cannyTransformation = new CannyEdgeDetectorTransformation();

        double[][] outputMap = cannyTransformation.transform(intensityMap);
        return (new GreyscaleImageRenderer()).convertToImage(outputMap);

    }

    @AfterClass
    public void outputImages() throws IOException {
        for (String outputName : outputs.keySet()) {
            BufferedImage img = outputs.get(outputName);
            ImageIO.write(img, "bmp", new File(outputPath + outputName + ".bmp"));
        }
    }
}
