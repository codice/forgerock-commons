/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If applicable, add the following below this MPL 2.0 HEADER, replacing
 * the fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *     Portions Copyright [yyyy] [name of copyright owner]
 *
 *     Copyright 2014 ForgeRock AS
 *
 */

package org.forgerock.doc.maven.utils;

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

@SuppressWarnings("javadoc")
public class PngUtilsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException, URISyntaxException {
        File image = new File(getClass().getResource("/unit/utils/pu/OpenAM.png").toURI());
        FileUtils.copyFileToDirectory(image, folder.getRoot());
    }

    @Test
    public void shouldSetDpiToDefault() throws IOException {
        long defaultDpi = 160L;

        File image = new File(folder.getRoot(), "OpenAM.png");
        PngUtils.setDpi(image);

        IIOMetadataNode rootNode = getRootNode(image);

        long horizontalDpi = getDpi(rootNode, "HorizontalPixelSize");
        assertThat(horizontalDpi).isEqualTo(defaultDpi);

        long verticalDpi = getDpi(rootNode, "VerticalPixelSize");
        assertThat(verticalDpi).isEqualTo(defaultDpi);
    }

    @Test
    public void shouldSetDpiCorrectly() throws IOException {
        File image = new File(folder.getRoot(), "OpenAM.png");
        PngUtils.setDpi(image, 42);

        IIOMetadataNode rootNode = getRootNode(image);

        long horizontalDpi = getDpi(rootNode, "HorizontalPixelSize");
        assertThat(horizontalDpi).isEqualTo(42L);

        long verticalDpi = getDpi(rootNode, "VerticalPixelSize");
        assertThat(verticalDpi).isEqualTo(42L);
    }

    private IIOMetadataNode getRootNode(File image) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(image);
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
        ImageReader reader = iterator.next();
        reader.setInput(iis);

        // There is a standard definition for image metadata.
        // https://docs.oracle.com/javase/7/docs/api/javax/imageio/metadata/doc-files/standard_metadata.html
        IIOMetadata metadata = reader.getImageMetadata(0);
        return (IIOMetadataNode) metadata.getAsTree("javax_imageio_1.0");
    }

    private long getDpi(IIOMetadataNode rootNode, String elementName) {
        NodeList sizeElements = rootNode.getElementsByTagName(elementName);

        if (sizeElements.getLength() > 0) {
            IIOMetadataNode sizeNode = (IIOMetadataNode) sizeElements.item(0);
            NamedNodeMap attributes = sizeNode.getAttributes();
            Node pixelSize = attributes.item(0);
            return getDpi(Double.parseDouble(pixelSize.getNodeValue()));
        } else {
            return 0L;
        }
    }

    private long getDpi(double pixelSize) {
        final double millimetersPerInch = 25.4;
        final double dotsPerMillimeter =  millimetersPerInch / pixelSize;

        return Math.round(dotsPerMillimeter);
    }

    @After
    public void tearDown() {
        folder.delete();
    }
}
