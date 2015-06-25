package com.zitego.util;

import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Utilities for working with images.
 *
 * @author John Glorioso
 * @version $Id: ImageUtils.java,v 1.6 2009/11/10 16:30:51 jglorioso Exp $
 */
public class ImageUtils
{
    public static void main(String[] args) throws Exception
    {
        if (args.length == 3)
        {
            if ( args[2].endsWith("%") )
            {
                String w = args[2].substring(0, args[2].length()-1);
                ImageUtils.scaleImage( new File(args[0]), new File(args[1]), Double.parseDouble(w) );
            }
            else
            {
                ImageUtils.scaleImage( new File(args[0]), new File(args[1]), Integer.parseInt(args[2]) );
            }
        }
        else if (args.length == 4)
        {
            if ( args[2].endsWith("%") )
            {
                String w = args[2].substring(0, args[2].length()-1);
                String h = args[2].substring(0, args[2].length()-1);
                ImageUtils.scaleImage( new File(args[0]), new File(args[1]), Double.parseDouble(w), Double.parseDouble(h) );
            }
            else
            {
                ImageUtils.scaleImage( new File(args[0]), new File(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]) );
            }
        }
        else
        {
            System.out.println("Usage: ImageUtils <infile> <outfile> <width px|%> <height? px|%>");
            System.exit(1);
        }
    }

    /**
     * Returns a scaled image given the image and width. The height is determined
     * as a proportionate value based on the new width. If the new width is less
     * than the current width, then nothing is changed.
     *
     * @param source The image source.
     * @param width The new width.
     * @return BufferedImage
     */
    public static BufferedImage getScaleImage(BufferedImage source, int width)
    {
        if (source.getWidth() <= width) return source;
        double p = (double)width / (double)source.getWidth();
        int height = (int)(source.getHeight() * p);
        if (height < 1) height = 1;
        return getScaleImage(source, width, height);
    }

    /**
     * Returns a scaled image given the image, width, and height.
     *
     * @param source The image source.
     * @param width The new width.
     * @param height The new height.
     * @return BufferedImage
     */
    public static BufferedImage getScaleImage(BufferedImage source, int width, int height)
    {
        /*BufferedImage image = new BufferedImage( width, height, source.getType() );
        image.createGraphics().drawImage(source, 0, 0, width, height, null);*/

        Object hint = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        int type = (source.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)source;
        int w = source.getWidth();
        int h = source.getHeight();

        do
        {
            if (w > width)
            {
                w /= 2;
                if (w < width) w = width;
            }

            if (h > height)
            {
                h /= 2;
                if (h < height) h = height;
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        }
        while (w != width || h != height);


        return ret;
    }

    /**
     * Returns a scaled image given a source image, the percentage of width and the percentage of height.
     *
     * @param source The image source.
     * @param width The percentage of the source image's width.
     * @param height The percentage of the source image's height.
     * @return BufferedImage
     */
    public static BufferedImage getScaleImage(BufferedImage source, double xscale, double yscale)
    {
        return getScaleImage( source, (int)(source.getWidth() * xscale), (int)(source.getHeight() * yscale) );
    }

    /**
     * Returns a scaled image given a source image and the percentage of width. The height is determined
     * as a proportionate value based on the new width.
     *
     * @param source The image source.
     * @param width The percentage of the source image's width.
     * @return BufferedImage
     */
    public static BufferedImage getScaleImage(BufferedImage source, double xscale)
    {
        return getScaleImage( source, (int)(source.getWidth() * xscale), (int)(source.getHeight() * xscale) );
    }

    /**
     * Reads in an input image file, scales it, then writes an output image file.
     *
     * @param input The input image file.
     * @param output The output image file.
     * @param width The new width.
     * @param height The new height.
     * @return boolean
     */
    public static boolean scaleImage(File input, File output, int width, int height) throws IOException
    {
        BufferedImage image = ImageIO.read(input);
        if (image == null) return false;
        image = getScaleImage(image, width, height);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.')+1).toLowerCase();
        return ImageIO.write(image, format, output);
    }

    /**
     * Reads in an input image file, scales it based on the width, then writes an output image file.
     *
     * @param input The input image file.
     * @param output The output image file.
     * @param width The new width.
     * @return boolean
     */
    public static boolean scaleImage(File input, File output, int width) throws IOException
    {
        BufferedImage image = ImageIO.read(input);
        if (image == null) return false;
        image = getScaleImage(image, width);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.')+1).toLowerCase();
        return ImageIO.write(image, format, output);
    }

    /**
     * Reads an input image file, scales it given percentages, then writes an output image file.
     *
     * @param input The input image file.
     * @param output The output image file.
     * @param width The percentage of the source image's width.
     * @param height The percentage of the source image's height.
     * @return boolean
     */
    public static boolean scaleImage(File input, File output, double xscale, double yscale) throws IOException
    {
        BufferedImage image = ImageIO.read(input);
        if (image == null) return false;
        image = getScaleImage(image, xscale, yscale);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.')+1).toLowerCase();
        return ImageIO.write(image, format, output);
    }

    /**
     * Reads an input image file, scales it given a width percentage, then writes an output image file.
     *
     * @param input The input image file.
     * @param output The output image file.
     * @param width The percentage of the source image's width.
     * @return boolean
     */
    public static boolean scaleImage(File input, File output, double xscale) throws IOException
    {
        BufferedImage image = ImageIO.read(input);
        if (image == null) return false;
        image = getScaleImage(image, xscale);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.')+1).toLowerCase();
        return ImageIO.write(image, format, output);
    }
}
