import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class KnownPlaintextAttack {

    private static double similarity(BufferedImage img1, BufferedImage img2) {
        // Breite und Hoehe der Bilder bestimmen
        int width = img1.getWidth();
        int height = img1.getHeight();
        int matching = 0; // Zaehler fuer uebereinstimmende Pixel

        // Alle Pixel zeilenweise durchlaufen
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Vergleiche die Farbe des Pixels an (x,y) in beiden Bildern
                if (img1.getRGB(x, y) == img2.getRGB(x, y)) {
                    matching++; // Wenn gleich, Zaehler erhoehen
                }
            }
        }

        // Aehnlichkeit berechnen: Anteil gleicher Pixel
        return (double) matching / (width * height);
    }

    public static void main(String[] args) throws Exception {
        File encryptedFolder = new File("encrypted_images/");
        File originalFolder = new File("images/");
        File decryptedFolder = new File("decrypted_images/");
        decryptedFolder.mkdirs();

        File[] encryptedFiles = encryptedFolder.listFiles((dir, name) -> name.startsWith("encrypted_") && name.endsWith(".png"));

        if (encryptedFiles == null || encryptedFiles.length == 0) {
            System.out.println("Keine verschlüsselten Bilder gefunden.");
            return;
        }

        for (File encryptedFile : encryptedFiles) {
            String filename = encryptedFile.getName(); // z.B. encrypted_spongebob_blocks2.png
            String baseName = filename.substring("encrypted_".length(), filename.indexOf("_blocks"));

            File originalFile = new File(originalFolder, baseName + ".png");
            if (!originalFile.exists()) {
                System.out.println("Originalbild nicht gefunden: " + originalFile.getPath());
                continue;
            }

            BufferedImage original = ImageIO.read(originalFile);
            BufferedImage encrypted = ImageIO.read(encryptedFile);

            int bestBlocks = -1;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (int blocks = 1; blocks <= 20; blocks++) {
                BufferedImage decrypted = CiphertextOnlyAttack.decrypt(encrypted, blocks);
                double score = similarity(decrypted, original);

                System.out.printf("Bild: %s, Blöcke: %d, Ähnlichkeit: %.4f%n", baseName, blocks, score);

                if (score > bestScore) {
                    bestScore = score;
                    bestBlocks = blocks;
                }
            }

            System.out.println("Ermittelter Blockwert z für " + baseName + ": " + bestBlocks);

            // Entschlüsseltes Bild speichern
            BufferedImage finalDecrypted = CiphertextOnlyAttack.decrypt(encrypted, bestBlocks);
            File output = new File(decryptedFolder, "decrypted_" + baseName + "_blocks" + bestBlocks + ".png");
            ImageIO.write(finalDecrypted, "png", output);
            System.out.println("Entschlüsseltes Bild gespeichert: " + output.getName());
        }
    }
}

