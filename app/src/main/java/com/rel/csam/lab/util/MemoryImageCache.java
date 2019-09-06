package com.rel.csam.lab.util;

import android.graphics.Bitmap;
import androidx.collection.LruCache;

import java.io.File;

public class MemoryImageCache implements ImageCache {

    private LruCache<String, Bitmap> lruCache;
    public static final int MAX_BUFFER_SIZE = 1024;

    public MemoryImageCache() {
        getCache();
    }

    public LruCache<String, Bitmap> getCache() {

        if (lruCache == null) {
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / MAX_BUFFER_SIZE);
            int cacheSize = (maxMemory / 8);
            lruCache = new LruCache<String, Bitmap>(cacheSize) {

                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / MAX_BUFFER_SIZE;
                }
            };
        }

        return lruCache;
    }

    @Override
    public void addBitmap(String key, Bitmap bitmap) {
        if (bitmap == null)
            return;

        getCache().put(key, bitmap);
    }

    @Override
    public void addBitmap(String key, File bitmapFile) {
        if (bitmapFile == null)
            return;

        if (!bitmapFile.exists())
            return;

//        Bitmap bitmap = AlbumContentsManager.getInstance().safeDecodeBitmapFile(bitmapFile);

//        if (bitmap != null) {
//            getCache().put(key, bitmap);
//        }
    }

    @Override
    public Bitmap getBitmap(String key) {
        if (getCache() != null) {
            return getCache().get(key);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        getCache().evictAll();
    }
}