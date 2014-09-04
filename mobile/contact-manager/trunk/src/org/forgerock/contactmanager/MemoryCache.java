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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Memory cache is utility class used by lazy loading.
 */
public class MemoryCache {

    private static final String TAG = "MemoryCache";
    private final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f,
            true));

    private long size = 0;
    private long limit = 1000000;

    /**
     * Constructor.
     */
    public MemoryCache() {
        // use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    /**
     * Sets the memory cache limit.
     *
     * @param limitValue
     *            The limit value to set.
     */
    public void setLimit(final long limitValue) {
        limit = limitValue;
        Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
    }

    /**
     * Returns the bitmap if presents in the cache of this application.
     *
     * @param id
     *            The bitmap's id to retrieve.
     * @return A bitmap if found or {@code null} if not.
     */
    public Bitmap get(final String id) {
        try {
            if (!cache.containsKey(id)) {
                return null;
            }
            return cache.get(id);
        } catch (final NullPointerException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return null;
    }

    /**
     * Puts a bitmap in memory cache.
     *
     * @param id
     *            The id of the bitmap.
     * @param bitmap
     *            The bitmap to insert.
     */
    public void put(final String id, final Bitmap bitmap) {
        try {
            if (cache.containsKey(id)) {
                size = getBmpSize(cache.get(id));
            }
            cache.put(id, bitmap);
            size = size + getBmpSize(bitmap);
            checkSize();
        } catch (final Throwable th) {
            Log.e(TAG, th.getMessage());
        }
    }

    /**
     * Checks the memory cache size.
     */
    private void checkSize() {
        Log.i(TAG, "The cache size is " + size + " length=" + cache.size());
        if (size > limit) {
            final Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();

            while (iter.hasNext()) {
                size = size - getBmpSize(iter.next().getValue());
                iter.remove();
                if (size <= limit) {
                    break;
                }
            }
            Log.i(TAG, "Cleaning cache. New size " + cache.size());
        }
    }

    /**
     * Clears the memory cache size.
     */
    void clear() {
        try {
            cache.clear();
            size = 0;
        } catch (final NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the bitmap size in bytes.
     *
     * @param bitmap
     *            The bitmap to analyze.
     * @return The bitmap's size in bytes.
     */
    long getBmpSize(final Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
