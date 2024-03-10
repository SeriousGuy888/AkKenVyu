package io.github.seriousguy888.akkenvyu.utils;

// Taken from https://www.baeldung.com/java-byte-arrays-hex-strings
public class HexStringToByteArray {

    public static byte[] convert(String hexString) throws IllegalArgumentException {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException("Invalid hexadecimal string supplied -- odd number of nybbles.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("Invalid hexadecimal character: " + hexChar);
        }
        return digit;
    }
}
