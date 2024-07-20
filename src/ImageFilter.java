import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageFilter {

    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        Image resultingImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    public static BufferedImage copyImage(BufferedImage original) {
        BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int pixel = original.getRGB(x, y);
                copy.setRGB(x, y, pixel);
            }
        }

        return copy;
    }

    public static BufferedImage negative(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));
                Color contrastColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                resultImage.setRGB(x, y, contrastColor.getRGB());
            }
        }
        return resultImage;
    }

    public static BufferedImage mirror(BufferedImage image, SquareFilter square, Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        for (int x = x1; x < x2 - ((x2 - x1) / 2); x++) {
            for (int y = y1; y < y2; y++) {
                int colorRight = image.getRGB(x, y);
                int colorLeft = image.getRGB(x2 - 1 - (x - x1), y);

                resultImage.setRGB(x, y, colorLeft);
                resultImage.setRGB(x2 - 1 - (x - x1), y, colorRight);
            }
        }

        if ((x2 - x1) % 2 != 0) {
            int middleX = x1 + (x2 - x1) / 2;
            for (int y = y1; y < y2; y++) {
                int middlePixel = image.getRGB(middleX, y);
                resultImage.setRGB(middleX, y, middlePixel);
            }
        }
        return resultImage;
    }

    public static BufferedImage pixelate(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        int pixelSize = 5;

        for (int x = x1; x < x2; x += pixelSize) {
            for (int y = y1; y < y2; y += pixelSize) {
                int pixelColor = image.getRGB(x, y);

                int endX = Math.min(x + pixelSize, image.getWidth());
                int endY = Math.min(y + pixelSize, image.getHeight());

                for (int dx = x; dx < endX; dx++) {
                    for (int dy = y; dy < endY; dy++) {
                        resultImage.setRGB(dx, dy, pixelColor);
                    }
                }
            }
        }
        return resultImage;
    }

    public static BufferedImage contrast(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        float contrastFactor = 2.2f;
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));
                int red = truncate((int) (contrastFactor * (color.getRed() - 128) + 128));
                int green = truncate((int) (contrastFactor * (color.getGreen() - 128) + 128));
                int blue = truncate((int) (contrastFactor * (color.getBlue() - 128) + 128));
                Color contrastColor = new Color(red, green, blue);
                resultImage.setRGB(x, y, contrastColor.getRGB());
            }
        }
        return resultImage;
    }

    private static int truncate(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static BufferedImage blackWhite(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        int threshold = 128;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));
                int grayValue = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                int bwValue = grayValue > threshold ? 255 : 0;
                Color bwColor = new Color(bwValue, bwValue, bwValue);
                resultImage.setRGB(x, y, bwColor.getRGB());
            }
        }
        return resultImage;
    }

    public static BufferedImage grayscale(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));
                int grayValue = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                Color grayColor = new Color(grayValue, grayValue, grayValue);
                resultImage.setRGB(x, y, grayColor.getRGB());
            }
        }
        return resultImage;
    }

    public static BufferedImage sepia(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        int sepiaDepth = 20;
        int sepiaIntensity = 50;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));

                int gray = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                int r = gray + sepiaDepth * 2;
                int g = gray + sepiaDepth;

                if (r > 255) r = 255;
                if (g > 255) g = 255;

                int b = gray - sepiaIntensity;
                if (b < 0) b = 0;

                Color newColor = new Color(r, g, b);
                resultImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return resultImage;
    }

    public static BufferedImage vignette(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        int centerX = image.getWidth() / 2;
        int centerY = image.getHeight() / 2;
        float radius = Math.min(centerX, centerY) * 2f;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                int dx = centerX - x;
                int dy = centerY - y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float vignetteAmount = 1.0f - (distance / radius);

                if (vignetteAmount < 0) {
                    vignetteAmount = 0;
                }

                Color color = new Color(image.getRGB(x, y));
                int r = (int) (color.getRed() * vignetteAmount);
                int g = (int) (color.getGreen() * vignetteAmount);
                int b = (int) (color.getBlue() * vignetteAmount);

                Color newColor = new Color(r, g, b);
                resultImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return resultImage;
    }

    public static BufferedImage solarize(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        int threshold = 128;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                if (red < threshold) {
                    red = 255 - red;
                }

                if (green < threshold) {
                    green = 255 - green;
                }

                if (blue < threshold) {
                    blue = 255 - blue;
                }

                red = clamp(red);
                green = clamp(green);
                blue = clamp(blue);

                Color newColor = new Color(red, green, blue);
                resultImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return resultImage;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(value, 255));
    }

    public static BufferedImage addNoise(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        Random random = new Random();
        int intensity = 50;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int noise = random.nextInt(intensity * 2) - intensity;
                red = clamp(red + noise);
                green = clamp(green + noise);
                blue = clamp(blue + noise);

                Color newColor = new Color(red, green, blue);
                resultImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return resultImage;
    }

    public static BufferedImage lighter(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        int factor = 50;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                red = clamp(red + factor);
                green = clamp(green + factor);
                blue = clamp(blue + factor);

                Color newColor = new Color(red, green, blue);
                resultImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return resultImage;
    }

    public static BufferedImage darker(BufferedImage image, SquareFilter square,Window window) {
        BufferedImage resultImage = copyImage(image);

        int x1 = square == null ?  0 : square.getPoints().get(0).getX();
        int y1 = square == null ? 0 : square.getPoints().get(0).getY()-80;
        int x2 = square == null ? image.getWidth() : square.getPoints().get(1).getX();
        int y2 = square == null ? image.getHeight() : square.getPoints().get(3).getY()-80;

        int factor = 50;

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                Color color = new Color(image.getRGB(x, y));

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                red = clamp(red - factor);
                green = clamp(green - factor);
                blue = clamp(blue - factor);

                Color newColor = new Color(red, green, blue);
                resultImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return resultImage;
    }
}