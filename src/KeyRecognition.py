def binstr_to_bytes(binstr):
    # Entferne Leerzeichen/Zeilenumbrüche und konvertiere zu Bytes
    binstr = ''.join(binstr.strip().split())
    return [int(binstr[i:i+8], 2) for i in range(0, len(binstr), 8)]

def get_key_from_plain_and_cipher(plaintext, cipher_bin, key_len):
    cipher_bytes = binstr_to_bytes(cipher_bin)
    plain_bytes = [ord(c) for c in plaintext]

    print(f"📏 Plaintext-Bytes: {len(plain_bytes)} | Cipher-Bytes: {len(cipher_bytes)}")

    min_len = min(len(cipher_bytes), len(plain_bytes))
    key = [cipher_bytes[i] ^ plain_bytes[i] for i in range(key_len)]

    for i in range(min_len):
        expected = plain_bytes[i] ^ key[i % key_len]
        if cipher_bytes[i] != expected:
            print(f"❌ Schlüssel stimmt an Position {i} nicht überein. Erwartet {expected}, aber ist {cipher_bytes[i]}")
            return key, False

    print("✅ Schlüssel wiederholt sich korrekt über den gesamten Text.")
    return key, True



ciphertext_bin = """001011110000110000011100000110000100010100010000000110000001000101011001000011100000110000010111010010110011000100011100000110000001000100001101000011100001110100001101010010110000001110000101000110010100010100011101000000100000000001011001001010100001000000011111000011000000010000011011000011100100010101001000010111010100100101011001000111000000101001011001000111000000110000001011010010110000100000010000000111110100010100100001001001000011011101011001001111010000000000001011000110000000011000010001000001111001100100001010000110000000000000010101000111100000101100011110010010110000010000001011000010010000000000010000000111110000000000010111010010110001000000010111000011110100010100011101000000100000000001011001001110000000011000010001000001111001100100001010000110000000000000010101000001111000000100010111000011000000000001011001000110000000011000010001100011110001000100000011000011100000101101010111"""

plaintext = "Dies ist ein Testtext für die Aufgabe 16, wo wir mit XOR Verschlüsselung arbeiten und die Schlüssellänge schätzen."

key_length = 24

key, valid = get_key_from_plain_and_cipher(plaintext, ciphertext_bin, key_length)

if valid:
    print(f"🎉 Erkannter Schlüssel (dezimal): {key}")
