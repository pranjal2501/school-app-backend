package com.example.school_management.grievance.feeReciept.service;

import com.example.school_management.grievance.template.dto.PlaceholderContext;
import com.example.school_management.grievance.template.entity.LetterheadTemplate;
import com.example.school_management.grievance.template.util.PlaceholderResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeReceiptImageService {

    private final PlaceholderResolver placeholderResolver;

    public byte[] generate(LetterheadTemplate template, PlaceholderContext context) {
        String resolvedText = placeholderResolver.resolve(template.getContent(), context);
        BufferedImage letterhead = loadImage(template.getLetterheadUrl());
        BufferedImage finalImage = overlayText(letterhead, resolvedText, template);
        return toBytes(finalImage);
    }

    private BufferedImage loadImage(String imageUrl) {
        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load letterhead from URL: " + imageUrl, e);
        }
    }

    private BufferedImage overlayText(BufferedImage base, String text, LetterheadTemplate template) {
        BufferedImage output = new BufferedImage(
                base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = output.createGraphics();
        try {
            g.drawImage(base, 0, 0, null);

            int fontSize = template.getFontSize() != null ? template.getFontSize() : 28;
            g.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            g.setColor(parseColor(template.getTextColor()));

            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            int x = template.getTextXPosition() != null ? template.getTextXPosition() : 80;
            int y = template.getTextYPosition() != null ? template.getTextYPosition() : 280;
            int maxWidth = base.getWidth() - (x * 2);
            int lineHeight = g.getFontMetrics().getHeight() + 8;

            for (String paragraph : text.split("\n")) {
                List<String> wrappedLines = wrapText(paragraph, g.getFontMetrics(), maxWidth);
                for (String line : wrappedLines) {
                    g.drawString(line, x, y);
                    y += lineHeight;
                }
                y += lineHeight / 2;
            }

        } finally {
            g.dispose();
        }
        return output;
    }

    private Color parseColor(String hex) {
        if (hex == null || hex.isBlank()) return Color.BLACK;
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            log.warn("Invalid text color '{}', falling back to black", hex);
            return Color.BLACK;
        }
    }

    private byte[] toBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert receipt image to bytes", e);
        }
    }

    private List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String test = current.isEmpty() ? word : current + " " + word;
            if (fm.stringWidth(test) <= maxWidth) {
                current = new StringBuilder(test);
            } else {
                if (!current.isEmpty()) lines.add(current.toString());
                current = new StringBuilder(word);
            }
        }
        if (!current.isEmpty()) lines.add(current.toString());
        return lines;
    }
}