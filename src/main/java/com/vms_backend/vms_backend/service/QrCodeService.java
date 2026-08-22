package com.vms_backend.vms_backend.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@Service
public class QrCodeService {

    public String generateQrCode(String content) throws Exception {

        int width = 250;
        int height = 250;

        BitMatrix matrix =
                new MultiFormatWriter().encode(
                        content,
                        BarcodeFormat.QR_CODE,
                        width,
                        height
                );

        BufferedImage image =
                MatrixToImageWriter.toBufferedImage(matrix);

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ImageIO.write(
                image,
                "PNG",
                outputStream
        );

        String base64 =
                Base64.getEncoder()
                        .encodeToString(
                                outputStream.toByteArray()
                        );

        return "data:image/png;base64," + base64;
    }
}