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
 *       Copyright 2013-2014 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;

/**
 * This class is utility to load image from URL or even base encoded JSON string.
 */
public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    private final Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    /**
     * Constructor.
     *
     * @param context
     *            The context of the image loader.
     */
    public ImageLoader(final Context context) {
        executorService = Executors.newFixedThreadPool(5);
    }

    /**
     * Displays the image in the selected image view.
     *
     * @param url
     *            The image's URL.
     * @param imageView
     *            The image view in which the image should appear.
     */
    public void displayIn(final String url, final ImageView imageView) {
        imageViews.put(imageView, url);
        final Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView);
            imageView.setImageResource(Constants.DEFAULT_ID_PICTURE);
        }
    }

    private void queuePhoto(final String url, final ImageView imageView) {
        final PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    /**
     * Gets the bitmap from the specified URL.
     *
     * @param url
     *            The image's URL.
     * @return A bitmap if the file can be retrieve or {@code null} if cannot.
     */
    public Bitmap getBitmap(final String url) {
        final File f = AppContext.get(url);

        // If cache contains the file.
        Bitmap bitmap;
        try {
            bitmap = decodeFile(f);
            if (bitmap != null) {
                return bitmap;
            }
        } catch (final FileNotFoundException ex) {
            Log.w("File not found", ex.getMessage());
        }

        try {
            InputStream is = null;
            if (!URLUtil.isHttpUrl(url)) {
                byte[] imageAsBytes = Base64.decode(url, Base64.DEFAULT);
                is = new ByteArrayInputStream(imageAsBytes);
                imageAsBytes = null;
            } else {
                is = new BufferedInputStream(new URL(url).openStream(), 8 * 1024);
            }
            Utils.copyStream(is, new FileOutputStream(f));
            return decodeFile(f);
        } catch (final IllegalArgumentException ex) {
            Log.w("Illegal argument", ex.getMessage());
        } catch (final IOException ex) {
            Log.w("OpenStream failed", ex.getMessage());
        } catch (final Throwable ex) {
            Log.w("Unable to retrieve image", ex.getCause());
            if (ex instanceof OutOfMemoryError) {
                memoryCache.clear();
            }
        }
        return null;
    }

    /**
     * Decodes image and scale it to reduce memory consumption.
     *
     * @param f
     *            The bitmap to decode.
     * @return A bitmap file scaled.
     * @throws FileNotFoundException
     *             If the file cannot be found.
     */
    private Bitmap decodeFile(final File f) throws FileNotFoundException {
        try {
            final BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int fitSize = 120;
            int tmpWidth = o.outWidth, tmpHeight = o.outHeight;
            int scale = 1;
            while (true) {
                if (tmpWidth / 2 < fitSize || tmpHeight / 2 < fitSize) {
                    break;
                }
                tmpWidth /= 2;
                tmpHeight /= 2;
                scale *= 2;
            }

            final BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (final FileNotFoundException e) {
            throw e;
        }
    }

    private class PhotoToLoad {
        private final String url;
        private final ImageView imageView;

        /**
         * Constructor.
         *
         * @param photoUrl
         *            The photo's URL.
         * @param iv
         *            The image view.
         */
        public PhotoToLoad(final String photoUrl, final ImageView iv) {
            url = photoUrl;
            imageView = iv;
        }
    }

    class PhotosLoader implements Runnable {
        private final PhotoToLoad photo;

        /**
         * Constructor.
         *
         * @param photoToLoad
         *            The photo to load.
         */
        PhotosLoader(final PhotoToLoad photoToLoad) {
            this.photo = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photo)) {
                return;
            }
            final Bitmap bmp = getBitmap(photo.url);
            memoryCache.put(photo.url, bmp);
            if (imageViewReused(photo)) {
                return;
            }

            final Activity a = (Activity) photo.imageView.getContext();
            a.runOnUiThread(new BitmapDisplayer(bmp, photo));
        }
    }

    boolean imageViewReused(final PhotoToLoad photoToLoad) {
        final String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url)) {
            return true;
        }
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(final Bitmap b, final PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageResource(Constants.DEFAULT_ID_PICTURE);
            }
        }
    }
}
