package com.vms_backend.vms_backend.service;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;


@Service
public class EncryptionService {
	
	private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;

    public EncryptionService() {
        String key = "12345678901234567890123456789012";
        this.secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                "AES"
        );
    }

    public String encrypt(String plainText) throws Exception {

        byte[] iv = new byte[IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        GCMParameterSpec parameterSpec =
                new GCMParameterSpec(TAG_LENGTH, iv);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                parameterSpec
        );

        byte[] encrypted =
                cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(
                encrypted,
                0,
                combined,
                iv.length,
                encrypted.length
        );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(combined);
    }
    

    // =========================
    // DECRYPT
    // =========================
    public String decrypt(String encryptedText) throws Exception {

        byte[] combined =
                Base64.getUrlDecoder().decode(encryptedText);

        byte[] iv = new byte[IV_LENGTH];

        byte[] encrypted =
                new byte[combined.length - IV_LENGTH];

        System.arraycopy(
                combined,
                0,
                iv,
                0,
                IV_LENGTH
        );

        System.arraycopy(
                combined,
                IV_LENGTH,
                encrypted,
                0,
                encrypted.length
        );

        Cipher cipher =
                Cipher.getInstance(ALGORITHM);

        GCMParameterSpec parameterSpec =
                new GCMParameterSpec(TAG_LENGTH, iv);

        cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                parameterSpec
        );

        byte[] decrypted =
                cipher.doFinal(encrypted);

        return new String(
                decrypted,
                StandardCharsets.UTF_8
        );
    }

}
