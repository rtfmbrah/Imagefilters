package com.colaenjoyer.imagefilters.filters;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.colaenjoyer.imagefilters.utils.ImageUtils;

public class Lowpass implements ImageFilter {

    @Override
    public BufferedImage execute(String pathname, String maskPath) {
        BufferedImage inputImage = ImageUtils.getInputImage(pathname);

        if (inputImage == null) {
            return null;
        }

        BufferedImage filterMask;
        boolean[][] maskArray = null;

        if(maskPath != null && !maskPath.isEmpty()) {
            filterMask = ImageUtils.getInputImage(maskPath);

            if (filterMask != null) {
                maskArray = ImageUtils.extractMask(filterMask);
            } else {
                return null;
            }
        }

        return ImageUtils.applyMask(inputImage, lowpassFilter(inputImage, 3), maskArray);
    }

    private BufferedImage lowpassFilter(BufferedImage inputImage, int blurAmount) {
        int imageWidth = inputImage.getWidth();
        int imageHeight = inputImage.getHeight();
        
        int padding = blurAmount;
        int kernelSize = 2 * padding + 1;
        int areaSize = kernelSize * kernelSize;

        int paddedWidth = imageWidth + 2 * padding;
        int paddedHeight = imageHeight + 2 * padding;

        BufferedImage paddedImage = new BufferedImage(paddedWidth, paddedHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                paddedImage.setRGB(
                    x + padding, 
                    y + padding, 
                    inputImage.getRGB(x, y)
                );
            }
        }

        BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = padding; y < paddedHeight - padding; y++) {
            for (int x = padding; x < paddedWidth - padding; x++) {
                long redChannel = 0;
                long greenChannel = 0;
                long blueChannel = 0;

                for (int matrixY = y - padding; matrixY <= y + padding; matrixY++) {
                    for (int matrixX = x - padding; matrixX <= x + padding; matrixX++) {
                        int rgb = paddedImage.getRGB(matrixX, matrixY);
                        Color color = new Color(rgb);

                        redChannel   += color.getRed();
                        greenChannel += color.getGreen();
                        blueChannel  += color.getBlue();
                    }
                }

                int averageRed   = (int) (redChannel   / areaSize);
                int averageGreen = (int) (greenChannel / areaSize);
                int averageBlue  = (int) (blueChannel  / areaSize);

                int averageRgb = new Color(averageRed, averageGreen, averageBlue).getRGB();

                resultImage.setRGB(x - padding, y - padding, averageRgb);
            }
        }

        return resultImage;
    }
}
