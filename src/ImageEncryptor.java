import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class ImageEncryptor {

    public static BufferedImage encrypt(BufferedImage image, int blocks, long seed) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage encrypted = new BufferedImage(width, height, image.getType());

        int[] blockStarts = new int[blocks + 1];
        for (int i = 0; i <= blocks; i++) {
            blockStarts[i] = i * height / blocks;
        }

        Random random = new Random(seed);

        for (int b = 0; b < blocks; b++) {
            int start = blockStarts[b];
            int end = blockStarts[b + 1];

            List<Integer> rows = new ArrayList<>();
            for (int i = start; i < end; i++) rows.add(i);
            Collections.shuffle(rows, random);

            for (int i = 0; i < rows.size(); i++) {
                for (int x = 0; x < width; x++) {
                    encrypted.setRGB(x, start + i, image.getRGB(x, rows.get(i)));
                }
            }
        }

        return encrypted;
    }

    public static void main(String[] args) throws Exception {
        File inputFolder = new File("images/");
        File outputFolder = new File("encrypted_images/");
        outputFolder.mkdirs();

        File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
        int[] blockParams = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512};
        long seed = 42;

        if (files == null || files.length == 0) {
            System.out.println("Keine Bilder gefunden.");
            return;
        }

        for (File file : files) {
            BufferedImage image = ImageIO.read(file);
            String baseName = file.getName().substring(0, file.getName().lastIndexOf('.'));

            for (int blocks : blockParams) {
                BufferedImage encrypted = encrypt(image, blocks, seed);
                File outFile = new File(outputFolder, "encrypted_" + baseName + "_blocks" + blocks + ".png");
                ImageIO.write(encrypted, "png", outFile);
                System.out.println("Verschlüsselt mit: " + blocks + "Blöcken: " + outFile.getName());
            }
        }
    }
}
