public class ShortKeyXOREncryption {

    // Wandelt normalen Text in einen binären String (ASCII -> 8-bit Binär)
    public static String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            // Jeder Buchstabe wird als 8-Bit Binärstring dargestellt
            String binChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binary.append(binChar);
        }
        return binary.toString();
    }

    // Wandelt einen binären String (ASCII 8-bit) wieder zurück in normalen Text
    public static String binaryToText(String binary) {
        StringBuilder text = new StringBuilder();
        // In 8-Bit Schritte durch den Binärstring iterieren
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
            // Bitweise XOR: '1' XOR '0' = '1', '1' XOR '1' = '0' usw.
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

            // Wenn für diesen Shift die meisten Übereinstimmungen gefunden wurden,
            // ist das wahrscheinlich die richtige Schlüssellänge (oder ein Vielfaches davon)
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

        // Kurzschlüssel (z. B. aus einem Passwort)
        String key = "key";

        // Konvertiere Text und Key zu Binärstring
        String binaryPlaintext = textToBinary(plaintext);
        String binaryKey = textToBinary(key);

        // Verschlüsselung: Text XOR Key
        String ciphertext = xorEncrypt(binaryPlaintext, binaryKey);
        System.out.println("Ciphertext (binary): " + ciphertext);

        // Entschlüsselung: Ciphertext erneut mit dem Key XOR-en
        String decryptedBinary = xorEncrypt(ciphertext, binaryKey);
        String decryptedText = binaryToText(decryptedBinary);
        System.out.println("Decrypted Text: " + decryptedText);

        // Schätz die Schlüssellänge durch Counting Coincidences
        int estimatedKeyLength = estimateKeyLength(ciphertext, 50);
        System.out.println("Estimated Key Length: " + estimatedKeyLength);
    }
}
