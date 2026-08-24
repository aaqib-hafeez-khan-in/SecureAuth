package com.example.authenticatorapp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class TotpGenerator {
    private TotpGenerator() {}

    public static String generate(String secret, long timestampMillis) throws Exception {
        byte[] key = decodeBase32(secret);
        long counter = timestampMillis / 1000L / 30L;
        byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key, "HmacSHA1"));
        byte[] hash = mac.doFinal(data);
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);
        return String.format(java.util.Locale.US, "%06d", binary % 1_000_000);
    }

    private static byte[] decodeBase32(String value) {
        String normalized = value.replaceAll("[\\s-]", "").replace("=", "").toUpperCase(java.util.Locale.US);
        if (normalized.isEmpty()) throw new IllegalArgumentException("Secret is required");
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        int buffer = 0, bitsLeft = 0;
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        for (char c : normalized.toCharArray()) {
            int index = alphabet.indexOf(c);
            if (index < 0) throw new IllegalArgumentException("Secret must be valid Base32");
            buffer = (buffer << 5) | index;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                output.write((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }
        return output.toByteArray();
    }
}
