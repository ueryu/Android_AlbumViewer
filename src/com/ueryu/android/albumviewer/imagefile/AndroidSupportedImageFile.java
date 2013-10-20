/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.imagefile;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * @author ueryu
 */
public class AndroidSupportedImageFile extends ImageFileBase {

    /** リソース. */
    private final Resources mRes;
    /** パス. */
    private final String mPath;
    /** データ. */
    private final byte[] mData;

    /**
     * コンストラクタ.
     * 
     * @param data JPEGデータ.
     */
    public AndroidSupportedImageFile(final Resources res, final String path, final byte[] data) {
        this.mRes = res;
        this.mPath = path;
        this.mData = data;
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.imagefile.ImageFileBase#getDrawable()
     */
    @Override
    public Drawable getDrawable() {
        final Bitmap bitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length);
        return new BitmapDrawable(mRes, bitmap);
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.imagefile.ImageFileBase#getPath()
     */
    @Override
    public String getPath() {
        return mPath;
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.imagefile.ImageFileBase#getSize()
     */
    @Override
    public int getSize() {
        return mData.length;
    }

}
