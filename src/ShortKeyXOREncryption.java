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
        int estimatedLength = estimateKeyLength(binaryCiphertext, 100);

        System.out.println("Klartext:         " + plaintext);
        System.out.println(" Schlüssel:        " + key + " (Länge: " + key.length() + " Zeichen, " + binaryKey.length() + " Bit)");
        System.out.println("Ciphertext (bin): " + binaryCiphertext);
        System.out.println("Entschlüsselt:    " + decryptedText);
        System.out.println("Geschätzte Schlüssellänge (Bit): " + estimatedLength);
        System.out.println("Entspricht ca. " + (estimatedLength / 8.0) + " Byte\n");

        // Gib Schlüsseldetails aus
        printKeyInfo(key);
    }

    public static void main(String[] args) {
        // 🔹 Testfall 1: kurzer Key
        String text1 = "Das ist ein einfacher Text.";
        String key1 = "a"; // 1 Zeichen

        // 🔹 Testfall 2: mittellanger Key
        String text2 = "Ein weiterer Text mit etwas mehr Inhalt zur Analyse.";
        String key2 = "abc"; // 3 Zeichen

        // 🔹 Testfall 3: längerer Key
        String text3 = "Dieser Text dient dazu, die Erkennung eines längeren XOR-Keys zu testen.";
        String key3 = "vertraulich"; // 11 Zeichen

        runTest(text1, key1);
        runTest(text2, key2);
        runTest(text3, key3);
    }
}
