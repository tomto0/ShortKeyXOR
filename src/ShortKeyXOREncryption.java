public class ShortKeyXOREncryption {

    public static String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

    public static String binaryToText(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i <= binary.length() - 8; i += 8) {
            int charCode = Integer.parseInt(binary.substring(i, i + 8), 2);
            text.append((char) charCode);
        }
        return text.toString();
    }

    public static String xorEncrypt(String binaryText, String binaryKey) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i++) {
            char textBit = binaryText.charAt(i);
            char keyBit = binaryKey.charAt(i % binaryKey.length()); // zyklische Wiederholung des Keys
            result.append(textBit == keyBit ? '0' : '1'); // XOR-Logik
        }
        return result.toString();
    }

    public static int estimateKeyLength(String ciphertext, int maxGuess) {
        int bestKeyLength = 1;         // Beste Schätzung der Schlüssellänge
        int maxCoincidences = 0;       // Höchste Anzahl an Übereinstimmungen bei einem Shift

        // Versuche verschiedene Shifts (1 bis maxGuess)
        for (int shift = 1; shift <= maxGuess; shift++) {
            int coincidences = 0;
            for (int i = 0; i < ciphertext.length() - shift; i++) {
                if (ciphertext.charAt(i) == ciphertext.charAt(i + shift)) {
                    coincidences++; // Zähle Übereinstimmungen
                }
            }
            if (coincidences > maxCoincidences) {
                maxCoincidences = coincidences;
                bestKeyLength = shift;
            }
        }
        return bestKeyLength;
    }

    // Berechnet den durchschnittlichen Hamming-Abstand zwischen Blöcken einer bestimmten Länge im Ciphertext
    public static double averageHammingDistance(String ciphertext, int keyLength, int maxBlocks) {
        // Bestimme die tatsächliche Anzahl der Blöcke, begrenzt durch maxBlocks
        int blockCount = Math.min(maxBlocks, ciphertext.length() / keyLength);

        // Wenn zu wenige Blöcke vorhanden sind, Rückgabe eines großen Werts
        if (blockCount < 2) return Double.MAX_VALUE;

        // Zerlege den Ciphertext in gleich große Blöcke
        String[] blocks = new String[blockCount];
        for (int i = 0; i < blockCount; i++) {
            blocks[i] = ciphertext.substring(i * keyLength, (i + 1) * keyLength);
        }

        double totalDist = 0;
        int comparisons = 0;

        // Vergleiche jeden Block mit allen späteren Blöcken
        for (int i = 0; i < blockCount; i++) {
            for (int j = i + 1; j < blockCount; j++) {
                totalDist += hammingDistance(blocks[i], blocks[j]);
                comparisons++;
            }
        }

        // Durchschnittlichen Hamming-Abstand pro Zeichen berechnen
        return (comparisons > 0) ? totalDist / (comparisons * keyLength) : Double.MAX_VALUE;
    }

    // Schätzt die wahrscheinlichste Schlüssellänge anhand des minimalen durchschnittlichen Hamming-Abstands
    public static int estimateKeyLengthByHamming(String ciphertext, int maxGuess) {
        double minAvgDist = Double.MAX_VALUE;
        int bestKeyLength = 1;

        // Probiere mögliche Schlüsselgrößen aus (Vielfache von 8 bis maxGuess)
        for (int k = 8; k <= maxGuess; k += 8) {
            double avgDist = averageHammingDistance(ciphertext, k, 20);

            // Wähle die Schlüssellänge mit dem kleinsten durchschnittlichen Abstand
            if (avgDist < minAvgDist) {
                minAvgDist = avgDist;
                bestKeyLength = k;
            }
        }
        return bestKeyLength;
    }


    public static int hammingDistance(String s1, String s2) {
        int dist = 0;
        for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
            if (s1.charAt(i) != s2.charAt(i)) dist++;
        }
        return dist;
    }

    public static void printKeyInfo(String key) {
        System.out.print("Schlüssel (dezimal): ");
        for (char c : key.toCharArray()) {
            System.out.print((int) c + " ");
        }
        System.out.println();

        System.out.print("ASCII (druckbar): ");
        for (char c : key.toCharArray()) {
            System.out.print((c >= 32 && c < 127) ? c : '.');
        }
        System.out.println();

        System.out.print("Hexadezimal: ");
        for (char c : key.toCharArray()) {
            System.out.printf("%02X ", (int) c);
        }
        System.out.println();

        System.out.print("Binär: ");
        for (char c : key.toCharArray()) {
            System.out.print(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0') + " ");
        }
        System.out.println("\n");
    }

    public static void runTest(String plaintext, String key) {
        String binaryPlaintext = textToBinary(plaintext);
        String binaryKey = textToBinary(key);
        String binaryCiphertext = xorEncrypt(binaryPlaintext, binaryKey);
        String decryptedText = binaryToText(xorEncrypt(binaryCiphertext, binaryKey));

        int estimatedLengthCoincidences = estimateKeyLength(binaryCiphertext, 100);
        int estimatedLengthHamming = estimateKeyLengthByHamming(binaryCiphertext, 40);

        System.out.println("Klartext:         " + plaintext);
        System.out.println(" Schlüssel:        " + key + " (Länge: " + key.length() + " Zeichen, " + binaryKey.length() + " Bit)");
        System.out.println("Ciphertext (bin): " + binaryCiphertext);
        System.out.println("Entschlüsselt:    " + decryptedText);

        System.out.println("Schätzungen der Schlüssellänge:");
        System.out.println("  ➤ Coincidences-Methode: " + estimatedLengthCoincidences + " Bit");
        System.out.println("  ➤ Hamming-Distanz-Methode: " + estimatedLengthHamming + " Bit\n");

        printKeyInfo(key);
    }

    public static void main(String[] args) {
        runTest("Das ist ein einfacher Text.", "a");
        runTest("Ein weiterer Text mit etwas mehr Inhalt zur Analyse.", "abc");
        runTest("Dieser Text dient dazu, die Erkennung eines längeren XOR-Keys zu testen.", "vertraulich");
    }
}