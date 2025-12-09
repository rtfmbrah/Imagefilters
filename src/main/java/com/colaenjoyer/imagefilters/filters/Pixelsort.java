package com.colaenjoyer.imagefilters.filters;

import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.image.BufferedImage;

import com.colaenjoyer.imagefilters.utils.ImageUtils;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@NoArgsConstructor
public class Pixelsort implements ImageFilter {
    private int bufferedImageWidth;
    private int bufferedImageHeight;

    private Random random = new Random();

    public BufferedImage execute(String pathname, String maskPath) {
        BufferedImage imageToSort = ImageUtils.getInputImage(pathname);

        if(imageToSort != null) {
            bufferedImageWidth = imageToSort.getWidth();
            bufferedImageHeight = imageToSort.getHeight();
        } else {
            return null;
        }

        Color[][] imgArray = extractImg(imageToSort);

        BufferedImage imageMask;
        boolean[][] mask = null;

        if(maskPath != null) {
            imageMask = ImageUtils.getInputImage(maskPath);
            if(imageMask != null) {
                mask = ImageUtils.extractMask(imageMask);
            } else {
                return null;
            }
        }

        Color[][] sortedPixels = sort(imageToSort);

        addRandomColumnShifts(sortedPixels, 50);

        return ImageUtils.applyMask(imgArray, sortedPixels, mask);
    }

    private int rgbAsSingleValue(int rgbValue) {
        int r = (rgbValue >> 16) & 0xff;
        int g = (rgbValue >> 8) & 0xff;
        int b = rgbValue & 0xff;

        return ((r * 299 + g * 587 + b * 114) / 1000);
    }

    private Color[][] extractImg(BufferedImage bufferedImage) {
        Color[][] img = new Color[bufferedImageWidth][bufferedImageHeight];

        for (int x = 0; x < bufferedImageWidth; x++) {
            for (int y = 0; y < bufferedImageHeight; y++) {
                img[x][y] = new Color(bufferedImage.getRGB(x, y));
            }
        }
        return img;
    }

    private Color[][] sort(BufferedImage bufferedImage) {
        Color[][] sortedImgArray = new Color[bufferedImageWidth][bufferedImageHeight];

        for (int x = 0; x < bufferedImageWidth; x++) {
            Color[] tempArray = new Color[bufferedImageHeight];

            for (int y = 0; y < bufferedImageHeight; y++) {
                tempArray[y] = new Color(bufferedImage.getRGB(x, y));
            }

            ArrayList<Color> tempArrayAsList = new ArrayList<>(Arrays.asList(tempArray));

            Collections.sort(tempArrayAsList, (o1, o2) -> rgbAsSingleValue(o1.getRGB()) - rgbAsSingleValue(o2.getRGB()));

            for (int y = 0; y < bufferedImageHeight; y++) {
                tempArray[y] = tempArrayAsList.get(y);
            }
            
            sortedImgArray[x] = tempArray;
        }
        return sortedImgArray;
    }

    private void addRandomColumnShifts(Color[][] colorArray, int maxShift) {
        Color previous;
        Color temp;

        for(int x = 0; x < bufferedImageWidth; x++) {
            int n = random.nextInt(maxShift);

            for(int i = 0; i < n; i++) {
                previous = colorArray[x][bufferedImageHeight-1];

                for(int y = 0; y < bufferedImageHeight; y++) {
                    temp = colorArray[x][y];
                    colorArray[x][y] = previous;
                    previous = temp;
                }
            }
        }
    }

}

// 78 79 32 77 65 73 68 69 78 83 63