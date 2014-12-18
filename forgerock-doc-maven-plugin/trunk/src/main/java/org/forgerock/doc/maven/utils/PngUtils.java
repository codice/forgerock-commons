/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013-2014 ForgeRock AS
 */

package org.forgerock.doc.maven.utils;

import org.apache.commons.io.FileUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Set dots per inch in the metadata of a Portable Network Graphics image.
 */
public final class PngUtils {

    /**
     * Return image height in pixels.
     *
     * @param image image file.
     * @throws IOException Failed to read the image.
     * @return Image height in pixels.
     */
    public static int getHeight(final File image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image);
        return bufferedImage.getHeight();
    }

    /**
     * Set the DPI on {@code image} so that it fits in {@code maxHeightInInches},
     * or to 160 if short enough.
     *
     * @param image PNG image file.
     * @param maxHeightInInches maximum available image height in inches.
     * @throws IOException Failed to save the image.
     */
    public static void setSafeDpi(final File image, final int maxHeightInInches)
            throws IOException {
        final int imageHeight = getHeight(image);
        final int defaultDpi = 160;
        final int defaultMaxHeight = maxHeightInInches * defaultDpi;

        // Images that do not fit by default must be
        if (imageHeight > defaultMaxHeight) {
            final double dpi = imageHeight * 1.0 / maxHeightInInches;
            setDpi(image, (int) Math.round(dpi));
        } else {
            setDpi(image);
        }
    }

    /**
     * Set the DPI on {@code image} to 160.
     *
     * @param image PNG image file.
     * @throws IOException Failed to save the image.
     */
    public static void setDpi(final File image) throws IOException {
        setDpi(image, 160);
    }

    /**
     * Set the DPI on {@code image} to {@code dotsPerInch}.
     *
     * @param image PNG image file.
     * @param dotsPerInch DPI to set in metadata.
     * @throws IOException Failed to save the image.
     */
    public static void setDpi(final File image, final int dotsPerInch) throws IOException {
        BufferedImage in = ImageIO.read(image);
        File updatedImage = File.createTempFile(image.getName(), ".tmp");
        saveBufferedImage(in, updatedImage, dotsPerInch);

        FileUtils.deleteQuietly(image);
        FileUtils.moveFile(updatedImage, image);
    }

    /*
     * Save an image, setting the DPI.
     *
     * @param bufferedImage The image to save.
     * @param outputFile The file to save the image to.
     * @param dotsPerInch The DPI setting to use.
     * @throws IOException Failed to write the image.
     */
    private static void saveBufferedImage(final BufferedImage bufferedImage,
                                          final File outputFile,
                                          final int dotsPerInch)
            throws IOException {
        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier =
                    ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDpi(metadata, dotsPerInch);

            final ImageOutputStream stream = ImageIO.createImageOutputStream(outputFile);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(bufferedImage, null, metadata), writeParam);
            } finally {
                stream.close();
            }
            break;
        }
    }

    /*
     * Set the DPI in image metadata.
     *
     * @param metadata Image metadata.
     * @param dotsPerInch DPI setting to set.
     * @throws IIOInvalidTreeException Failed to write metadata.
     */
    private static void setDpi(IIOMetadata metadata, final int dotsPerInch)
            throws IIOInvalidTreeException {

        final double inchesPerMillimeter = 1.0 / 25.4;
        final double dotsPerMillimeter = dotsPerInch * inchesPerMillimeter;

        IIOMetadataNode horizontalPixelSize = new IIOMetadataNode("HorizontalPixelSize");
        horizontalPixelSize.setAttribute("value", Double.toString(dotsPerMillimeter));

        IIOMetadataNode verticalPixelSize = new IIOMetadataNode("VerticalPixelSize");
        verticalPixelSize.setAttribute("value", Double.toString(dotsPerMillimeter));

        IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
        dimension.appendChild(horizontalPixelSize);
        dimension.appendChild(verticalPixelSize);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dimension);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

    private PngUtils() {
        // Not used.
    }
}
