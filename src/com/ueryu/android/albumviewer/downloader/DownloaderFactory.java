/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.downloader;

/**
 * @author ueryu
 */
public final class DownloaderFactory {

    private static final String DELIMITOR = ",";
    private static final int TITLE = 0;
    private static final int ADDRESS = 1;

    /**
     * アドレスに合わせた DownloaderBase インスタンスを生成する.
     * 
     * @param line アドレスを含んだ文字列.
     * @return アドレスに合わせた DownloaderBase インスタンス
     */
    public static final DownloaderBase create(final String line) {
        final String[] parsed = line.split(DELIMITOR);
        if (parsed.length > ADDRESS) {
            final String title = parsed[TITLE];
            final String address = parsed[ADDRESS];
            return new NormalHttpDownloader(title, address);
        }
        return null;
    }

    /** 隠匿デフォルトコンストラクタ. */
    private DownloaderFactory() {
    }
}
