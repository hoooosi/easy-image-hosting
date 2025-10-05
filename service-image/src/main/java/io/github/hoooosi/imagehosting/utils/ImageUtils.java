package io.github.hoooosi.imagehosting.utils;

import io.github.hoooosi.imagehosting.exception.BusinessException;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class ImageUtils {

    public static String getFormatName(String contentType) {
        if (contentType == null) throw new BusinessException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
        return switch (contentType) {
            case "image/jpeg" -> "jpeg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
        };
    }

    public static byte[] convertToBytes(BufferedImage image, String contentType) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, getFormatName(contentType), baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("convert to " + contentType + " failed", e);
        }
    }

    public static BufferedImage generateThumbnail(BufferedImage image) {
        return Scalr.resize(
                image,
                Scalr.Method.QUALITY,
                Scalr.Mode.AUTOMATIC,
                768,
                768,
                Scalr.OP_ANTIALIAS
        );
    }

    public static String getAverageColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        long sumR = 0, sumG = 0, sumB = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                sumR += r;
                sumG += g;
                sumB += b;
            }
        }

        long numPixels = (long) width * height;
        int avgR = (int) (sumR / numPixels);
        int avgG = (int) (sumG / numPixels);
        int avgB = (int) (sumB / numPixels);

        return String.format("#%02x%02x%02x", avgR, avgG, avgB);
    }
}
