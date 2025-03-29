public class ShortKeyXOREncryption {

    // Wandelt normalen Text in einen binären String (ASCII -> 8-bit Binär)
    public static String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            String binChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binary.append(binChar);
        }
        return binary.toString();
    }

    // Wandelt einen binären String (ASCII 8-bit) wieder zurück in normalen Text
    public static String binaryToText(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            int charCode = Integer.parseInt(byteStr, 2); // Binär -> Integer
            text.append((char) charCode); // Integer -> ASCII-Zeichen
        }
        return text.toString();
    }

    // Führt die XOR-Verschlüsselung durch: Text XOR Key (beide als Binärstring)
    public static String xorEncrypt(String binaryText, String binaryKey) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i++) {
            char textBit = binaryText.charAt(i);
            char keyBit = binaryKey.charAt(i % binaryKey.length()); // zyklische Wiederholung des Keys
            result.append(textBit == keyBit ? '0' : '1'); // XOR-Logik
        }
        return result.toString();
    }

    // Counting Coincidences Methode zur Schätzung der Schlüssellänge
    public static int estimateKeyLength(String ciphertext, int maxGuess) {
        int bestKeyLength = 1;         // Beste Schätzung der Schlüssellänge
        int maxCoincidences = 0;       // Höchste Anzahl an Übereinstimmungen bei einem Shift

        // Versuche verschiedene Shifts (1 bis maxGuess)
        for (int shift = 1; shift <= maxGuess; shift++) {
            int coincidences = 0;

            // Vergleiche jedes Bit mit dem um "shift" versetzten Bit
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

    public static void main(String[] args) {
        // Beispiel-Text zum Verschlüsseln
        String plaintext = "Dies ist ein Testtext für XOR Verschlüsselung!";
        String plaintext2 = "Dies ist ein Testtext für die Aufgabe 16, wo wir mit XOR Verschlüsselung arbeiten und die Schlüssellänge schätzen.";
        String plaintext3 = "Test";

        // Kurzschlüssel (z. B. aus einem Passwort)
        String key = "key";

        // Konvertiere Text und Key zu Binärstring
        String binaryPlaintext = textToBinary(plaintext);
        String binaryPlaintext2 = textToBinary(plaintext2);
        String binaryPlaintext3 = textToBinary(plaintext3);
        String binaryKey = textToBinary(key);

        // Verschlüsseln
        String binaryCiphertext = xorEncrypt(binaryPlaintext, binaryKey);
        String binaryCiphertext2 = xorEncrypt(binaryPlaintext2, binaryKey);
        String binaryCiphertext3 = xorEncrypt(binaryPlaintext3, binaryKey);
        System.out.println("Ciphertext (binär): " + binaryCiphertext);
        System.out.println("Ciphertext2 (binär): " + binaryCiphertext2);
        System.out.println("Ciphertext3 (binär): " + binaryCiphertext3);

        // Entschlüsseln (nochmal XOR mit Key)
        String decryptedBinary = xorEncrypt(binaryCiphertext, binaryKey);
        String decryptedBinary2 = xorEncrypt(binaryCiphertext2, binaryKey);
        String decryptedBinary3 = xorEncrypt(binaryCiphertext3, binaryKey);
        String decryptedText = binaryToText(decryptedBinary);
        String decryptedText2 = binaryToText(decryptedBinary2);
        String decryptedText3 = binaryToText(decryptedBinary3);
        System.out.println("Entschlüsselter Text: " + decryptedText);
        System.out.println("Entschlüsselter Text2: " + decryptedText2);
        System.out.println("Entschlüsselter Text3: " + decryptedText3);

        // Schätzung der Schlüssellänge
        int estimatedKeyLength = estimateKeyLength(binaryCiphertext, 100);
        int estimatedKeyLength2 = estimateKeyLength(binaryCiphertext2, 100);
        int estimatedKeyLength3 = estimateKeyLength(binaryCiphertext3, 100);
        System.out.println("Erwartete Schlüssellänge: " + estimatedKeyLength);
        System.out.println("Erwartete Schlüssellänge2: " + estimatedKeyLength2);
        System.out.println("Erwartete Schlüssellänge3: " + estimatedKeyLength3);
    }
}
