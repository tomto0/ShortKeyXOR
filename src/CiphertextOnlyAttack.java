import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class CiphertextOnlyAttack {

    private static double rowDistance(BufferedImage img, int r1, int r2) {
        int width = img.getWidth();
        double dist = 0;
        for (int x = 0; x < width; x++) {
            int rgb1 = img.getRGB(x, r1);
            int rgb2 = img.getRGB(x, r2);

            int dr = ((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF);
            int dg = ((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF);
            int db = (rgb1 & 0xFF) - (rgb2 & 0xFF);

            dist += Math.sqrt(dr * dr + dg * dg + db * db);
        }
        return dist;
    }

    public static BufferedImage decrypt(BufferedImage encrypted, int blocks) {
        int width = encrypted.getWidth();
        int height = encrypted.getHeight();
        BufferedImage decrypted = new BufferedImage(width, height, encrypted.getType());

        int[] blockStarts = new int[blocks + 1];
        for (int i = 0; i <= blocks; i++) {
            blockStarts[i] = i * height / blocks;
        }

        for (int b = 0; b < blocks; b++) {
            int start = blockStarts[b];
            int end = blockStarts[b + 1];

            if (end <= start) continue; // ❗ Wichtiger Fix: leeren Block überspringen

            List<Integer> remaining = new ArrayList<>();
            for (int i = start; i < end; i++) remaining.add(i);

            List<Integer> ordered = new ArrayList<>();
            ordered.add(remaining.removeFirst()); // erste Zeile zufällig

            while (!remaining.isEmpty()) {
                int last = ordered.getLast();
                int best = -1;
                double bestDist = Double.MAX_VALUE;

                for (int r : remaining) {
                    double dist = rowDistance(encrypted, last, r);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = r;
                    }
                }

                ordered.add(best);
                remaining.remove(Integer.valueOf(best));
            }

            for (int i = 0; i < ordered.size(); i++) {
                for (int x = 0; x < width; x++) {
                    decrypted.setRGB(x, start + i, encrypted.getRGB(x, ordered.get(i)));
                }
            }
        }

        return decrypted;
    }


    public static void main(String[] args) throws Exception {
        File inputFolder = new File("encrypted_images/");
        File outputFolder = new File("decrypted_images/");
        outputFolder.mkdirs();

        File[] files = inputFolder.listFiles((dir, name) -> name.startsWith("encrypted_") && name.endsWith(".png"));

        if (files == null || files.length == 0) {
            System.out.println("Keine verschlüsselten Bilder gefunden.");
            return;
        }

        for (File file : files) {
            String filename = file.getName(); // z.B. encrypted_spongebob_blocks2.png
            int blockIndex = filename.indexOf("_blocks") + 7;
            int blockEnd = filename.lastIndexOf('.');
            int blocks = Integer.parseInt(filename.substring(blockIndex, blockEnd));
            String baseName = filename.substring("encrypted_".length(), blockIndex - 7);

            BufferedImage encrypted = ImageIO.read(file);
            BufferedImage decrypted = decrypt(encrypted, blocks);

            File outFile = new File(outputFolder, "decrypted_" + baseName + "_blocks" + blocks + ".png");
            ImageIO.write(decrypted, "png", outFile);
            System.out.println("Entschlüsselt: " + outFile.getName());
        }
    }
}
