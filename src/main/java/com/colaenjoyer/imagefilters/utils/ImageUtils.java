package com.colaenjoyer.imagefilters.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.extern.java.Log;

@Log
public class ImageUtils {
    private ImageUtils() {}

    public static BufferedImage getInputImage(String pathname) {
        try {
            return ImageIO.read(new File(pathname));
        } catch (IOException e) {
            log.severe(e.getMessage());
        }

        return null;
    }

    public static boolean[][] extractMask(BufferedImage maskImg) {
        int imageWidth = maskImg.getWidth();
        int imageHeight = maskImg.getHeight();

        boolean[][] mask = new boolean[imageWidth][imageHeight];

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                if(maskImg.getRGB(x, y) == 0xFF000000) {
                    mask[x][y] =  false;
                }
                if(maskImg.getRGB(x, y) == 0xFFFFFFFF) {
                    mask[x][y] =  true;
                }
            }
        }

        return mask;
    }

    public static BufferedImage scaleImage(BufferedImage originalScaleImage, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = scaledImage.createGraphics();

        graphics2D.drawImage(originalScaleImage, 0, 0, newWidth, newHeight, null);

        return scaledImage;
    }


    public static BufferedImage applyMask(BufferedImage image, BufferedImage filteredImage, boolean[][] mask) {
        int imageWidth = filteredImage.getWidth();
        int imageHeight = filteredImage.getHeight();

        if(mask != null) {
            for (int x = 0; x < imageWidth; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    if(mask[x][y]) {
                        filteredImage.setRGB(x, y, image.getRGB(x, y));
                    }
                    if(!mask[x][y]) {
                        filteredImage.setRGB(x, y, filteredImage.getRGB(x, y));
                    }
                }
            }
        }
        return filteredImage;
    }

    //TODO: Reduce complexity
    public static BufferedImage applyMask(Color[][] imageArray, Color[][] sortedImageArray, boolean[][] mask) {
        int imageWidth = sortedImageArray.length;
        int imageHeight = sortedImageArray[0].length;

        BufferedImage sortedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        if(mask == null) {
            for (int x = 0; x < imageWidth; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    sortedImage.setRGB(x, y, sortedImageArray[x][y].getRGB());
                }
            }
            return sortedImage;
        } else {
            for (int x = 0; x < imageWidth; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    if(mask[x][y]) {
                        sortedImage.setRGB(x, y, imageArray[x][y].getRGB());
                    }
                    if(!mask[x][y]) {
                        sortedImage.setRGB(x, y, sortedImageArray[x][y].getRGB());
                    }
                }
            }
            return sortedImage;
        }
    }
}
