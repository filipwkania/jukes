package com.melloware.jukes.file.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Utility to blend to images together.  It overlays one image over another.
 * <p>
 * Copyright (c) 2006
 * Melloware, Inc. <http://www.melloware.com>
 * @author Emil A. Lefkof III <info@melloware.com>
 * @version 4.0
 */
public final class ImageBlender {

    // Graphic Display constants
    public static final float BLEND_OPAQUE = 1.0f;
    public static final float BLEND_TRANSPARENT = 0.5f;

    /**
     * Default constructor. Private so no instantiation.
     */
    private ImageBlender() {
        super();
    }

    public static Icon blendIcons(final Icon source,
                                  final Icon destination,
                                  final float transparency,
                                  final ImageObserver imageObserver) {
        final ImageIcon sourceIcon = (ImageIcon)source;
        final ImageIcon destinationIcon = (ImageIcon)destination;
        final Image sourceImage = sourceIcon.getImage();
        final Image destinationImage = destinationIcon.getImage();

        final BufferedImage dest = new BufferedImage(destinationIcon.getIconWidth(), destinationIcon.getIconHeight(),
                                                     BufferedImage.TYPE_INT_ARGB);

        final Graphics2D destG = dest.createGraphics();
        destG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
        destG.drawImage(destinationImage, 0, 0, imageObserver);
        destG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        destG.drawImage(sourceImage, 0, 0, imageObserver);
        return new ImageIcon(dest);
    }

}