package com.colaenjoyer.imagefilters.filters;

import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;

import com.colaenjoyer.imagefilters.utils.ImageUtils;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@NoArgsConstructor
@Log
public class Asciifilter implements ImageFilter {
    private static final int FONT_SIZE = 8;

    public BufferedImage execute(String pathname, String mask) {
        BufferedImage inputImage = ImageUtils.getInputImage(pathname);

        int bufferedImageWidth = inputImage.getWidth();
        int bufferedImageHeight = inputImage.getHeight();

        BufferedImage imageMask;
        boolean[][] maskArray = null;

        if(mask != null) {
            imageMask = ImageUtils.scaleImage(ImageUtils.getInputImage(mask), bufferedImageWidth * FONT_SIZE,
                    bufferedImageHeight * FONT_SIZE);
            maskArray = ImageUtils.extractMask(imageMask);
        }

        BufferedImage scaledInputImage = ImageUtils.scaleImage(inputImage, bufferedImageWidth * FONT_SIZE,
                bufferedImageHeight * FONT_SIZE);
        return ImageUtils.applyMask(scaledInputImage, textAsImage(toAscii(inputImage)), maskArray);
    }

    private BufferedImage textAsImage(String textString) {
        String[] textStringAsArray = textString.split("\n");

        BufferedImage resultImage = new BufferedImage(textStringAsArray[0].length() * FONT_SIZE,
                textStringAsArray.length * FONT_SIZE, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D resultImageGraphics = resultImage.createGraphics();

        resultImageGraphics.setPaint(Color.BLACK);
        resultImageGraphics.fillRect(0, 0, resultImage.getWidth(), resultImage.getHeight());

        resultImageGraphics.setColor(Color.WHITE);

        Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        fontAttributes.put(TextAttribute.TRACKING, 1);

        resultImageGraphics.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE).deriveFont(fontAttributes));

        for(int y = 0; y < resultImage.getHeight()/ FONT_SIZE; y++) {
            for (int x = 0; x < resultImage.getWidth()/ FONT_SIZE; x++) {
                resultImageGraphics.drawString("" + textStringAsArray[y].charAt(x), x * FONT_SIZE, y * FONT_SIZE);
            }
        }

        resultImageGraphics.dispose();
        return resultImage;
    }

    private int getPixelBrightness(BufferedImage img, int x, int y) {
        int pixel = img.getRGB(x, y);
        int imageRed = (pixel >> 16) & 0xff;
        int imageGreen = (pixel >> 8) & 0xff;
        int imageBlue = pixel & 0xff;
        return (imageBlue + imageGreen + imageRed) / 3;
    }

    private String toAscii(BufferedImage img) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                int imageBrightness = getPixelBrightness(img, j, i);

                if (imageBrightness <= 64) {
                    s.append(".");
                } else if (64 < imageBrightness && imageBrightness < 129) {
                    s.append("-");
                } else if (129 < imageBrightness && imageBrightness < 193) {
                    s.append("+");
                } else {
                    s.append("*");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }
}

// 78 79 32 77 65 73 68 69 78 83 63