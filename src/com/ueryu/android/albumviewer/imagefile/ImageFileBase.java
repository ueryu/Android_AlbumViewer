/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.imagefile;

import android.graphics.drawable.Drawable;

import com.ueryu.android.blownlibrary.cache.SizeMeasurable;

/**
 * @author ueryu
 */
public abstract class ImageFileBase implements SizeMeasurable {

    public abstract Drawable getDrawable();

    public abstract String getPath();

    @Override
    public abstract int getSize();
}
