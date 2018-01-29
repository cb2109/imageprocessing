package user.cb2109.imageprocessing;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
public class RunnerTest {

    private final String testImagePath = "src/test/resources/billGates1.jpg";
    private final String outputPath = "src/test/resources/output/";
    private BufferedImage img;
    private Map<String, BufferedImage> outputs;

    @BeforeClass
    public void loadsImage() throws IOException {
        outputs = new HashMap<>();
        this.img = ImageIO.read(new File(testImagePath));

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

        assertEquals(img.getHeight(null), 416);
        assertEquals(img.getWidth(null), 416);
    }

    @Test
    public void blackAndWhiteImage() {
        Runner r = new Runner(this.img);
        BufferedImage output = r.processImage();
        outputs.put("blackAndWhite1.jpg", output);
    }

    @AfterClass
    public void outputImages() throws IOException {
        for (String outputName : outputs.keySet()) {
            BufferedImage img = outputs.get(outputName);
            ImageIO.write(img, "jpg", new File(outputPath + outputName));
        }
    }


}
