package com.vms_backend.vms_backend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Packs hostId+mobileNo into an opaque AES-encrypted token for approval links,
 * and unpacks it back. Wire-compatible with CryptoJS.AES.encrypt(text, PASSPHRASE)
 * / CryptoJS.AES.decrypt(...) on the frontend: same "Salted__" + salt + ciphertext
 * layout, same OpenSSL EVP_BytesToKey (MD5) key/IV derivation, AES-256-CBC.
 *
 * IMPORTANT: PASSPHRASE here must be the exact same string passed to
 * CryptoJS.AES.encrypt/decrypt on the frontend, or tokens won't cross-decode.
 */
public class TokenUtil {

    private static final String DELIMITER = "|";

    // TODO: move to application.properties (e.g. app.token-secret) and inject via
    // @Value, then pass into a non-static encode/decode. Must match the frontend's
    // crypto-js passphrase exactly.
    private static final String PASSPHRASE = "vms-secret-key";

    private static final String SALTED_PREFIX = "Salted__";
    private static final int SALT_LEN = 8;
    private static final int KEY_LEN = 32; // AES-256
    private static final int IV_LEN = 16;

    public static String encode(String hostId, String mobileNo) {
        String raw = hostId + DELIMITER + mobileNo;
        try {
            byte[] salt = new byte[SALT_LEN];
            new SecureRandom().nextBytes(salt);

            byte[] keyAndIv = evpBytesToKey(PASSPHRASE.getBytes(StandardCharsets.UTF_8), salt, KEY_LEN + IV_LEN);
            byte[] key = Arrays.copyOfRange(keyAndIv, 0, KEY_LEN);
            byte[] iv = Arrays.copyOfRange(keyAndIv, KEY_LEN, KEY_LEN + IV_LEN);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));

            byte[] output = new byte[SALTED_PREFIX.length() + SALT_LEN + cipherText.length];
            System.arraycopy(SALTED_PREFIX.getBytes(StandardCharsets.US_ASCII), 0, output, 0, SALTED_PREFIX.length());
            System.arraycopy(salt, 0, output, SALTED_PREFIX.length(), SALT_LEN);
            System.arraycopy(cipherText, 0, output, SALTED_PREFIX.length() + SALT_LEN, cipherText.length);

            // URL-safe, unpadded — matches the frontend swapping +/ for -_ and stripping '='
            return Base64.getUrlEncoder().withoutPadding().encodeToString(output);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode approval token", e);
        }
    }

    /** Returns [hostId, mobileNo]. Throws IllegalArgumentException if the token is malformed. */
    public static String[] decode(String token) {
        try {
            byte[] data = Base64.getUrlDecoder().decode(token);

            String prefix = new String(data, 0, SALTED_PREFIX.length(), StandardCharsets.US_ASCII);
            if (!SALTED_PREFIX.equals(prefix)) {
                throw new IllegalArgumentException("Malformed approval token");
            }

            byte[] salt = Arrays.copyOfRange(data, SALTED_PREFIX.length(), SALTED_PREFIX.length() + SALT_LEN);
            byte[] cipherText = Arrays.copyOfRange(data, SALTED_PREFIX.length() + SALT_LEN, data.length);

            byte[] keyAndIv = evpBytesToKey(PASSPHRASE.getBytes(StandardCharsets.UTF_8), salt, KEY_LEN + IV_LEN);
            byte[] key = Arrays.copyOfRange(keyAndIv, 0, KEY_LEN);
            byte[] iv = Arrays.copyOfRange(keyAndIv, KEY_LEN, KEY_LEN + IV_LEN);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] plain = cipher.doFinal(cipherText);

            String raw = new String(plain, StandardCharsets.UTF_8);
            String[] parts = raw.split("\\|", 2);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalArgumentException("Malformed approval token");
            }
            return parts;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or corrupted approval token", e);
        }
    }

    /**
     * OpenSSL's EVP_BytesToKey with MD5, one iteration — the same derivation
     * CryptoJS.AES.encrypt/decrypt use internally when given a plain passphrase.
     */
    private static byte[] evpBytesToKey(byte[] password, byte[] salt, int outputLen) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] result = new byte[outputLen];
        byte[] prev = new byte[0];
        int generated = 0;

        while (generated < outputLen) {
            md5.reset();
            md5.update(prev);
            md5.update(password);
            md5.update(salt);
            prev = md5.digest();

            int toCopy = Math.min(prev.length, outputLen - generated);
            System.arraycopy(prev, 0, result, generated, toCopy);
            generated += toCopy;
        }
        return result;
    }
}