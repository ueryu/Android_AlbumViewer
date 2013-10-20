/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.downloader;

/**
 * @author ueryu
 */
public abstract class DownloaderBase {

    public abstract String getAddress(final int id);

    public abstract int getCount();

    public abstract String getRawAddress();

    public abstract String getTitle();

    @Override
    public String toString() {
        return getTitle() + "," + getRawAddress();
    }

}
