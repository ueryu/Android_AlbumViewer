/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.imagefile;

import android.content.Context;

import com.ueryu.android.albumviewer.R;
import com.ueryu.android.blownlibrary.cache.CacheManager;
import com.ueryu.android.blownlibrary.utils.CompleteCallback;

/**
 * ImageFile管理クラス.
 * 
 * @author ueryu
 */
public final class ImageFileManager extends CacheManager<String, ImageFileBase> {

    private static final int MB = 1024 * 1024;

    private static ImageFileManager INSTANCE = null;

    /**
     * Singletonインスタンス取得.
     * 
     * @param context コンテキスト
     * @return インスタンス取得
     */
    public static final synchronized ImageFileManager getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ImageFileManager(
                    context.getApplicationContext().getResources().getInteger(R.integer.max_image_cache_mb) * MB);
        }
        return INSTANCE;
    }

    /**
     * コンストラクタ.
     * 
     * @param res リソース
     */
    private ImageFileManager(final int size) {
        super(size);
    }

    /**
     * キャッシュ取得.
     * 
     * @param path パス
     * @return キャッシュデータ
     */
    public synchronized void getImageFile(final String path,
            final CompleteCallback<ImageFileBase> callback) {
        super.get(path, callback);
    }

    /**
     * キャッシュ.
     * 
     * @param data データ
     */
    public synchronized void putImageFile(final ImageFileBase data) {
        super.put(data.getPath(), data);
    }
}
