/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.downloader;

import com.ueryu.android.albumviewer.utils.ExLog;
import com.ueryu.android.blownlibrary.BlownUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTPダウンロード.
 * 
 * @author ueryu
 */
public class NormalHttpDownloader extends DownloaderBase {

    /** IDセクションのパターン. */
    private static final String IDSECTION_PATTERN = "\\[([0-9]+)-([0-9]+)\\]";

    private static final int IDSECTION_STARTID = 1;
    private static final int IDSECTION_ENDID = 2;

    /** 位置によって置き換える文字. */
    private static final String REPLACEMENT_CHAR = "*";

    private final String mTitle;
    private final int mStartId;
    private final int mEndId;
    private final String mAddress;
    private final String mRawAddress;

    /**
     * コンストラクタ.
     * 
     * @param address アドレスパターン <br />
     *            例:http://www.example.com/[0-9].jpgで<br />
     *            http://www.example.com/0.jpg<br />
     *            http://www.example.com/1.jpg<br />
     *            http://www.example.com/2.jpg<br />
     *            http://www.example.com/3.jpg<br />
     *            http://www.example.com/4.jpg<br />
     *            http://www.example.com/5.jpg<br />
     *            http://www.example.com/6.jpg<br />
     *            http://www.example.com/7.jpg<br />
     *            http://www.example.com/8.jpg<br />
     *            http://www.example.com/9.jpg<br />
     *            にアクセスする
     */
    public NormalHttpDownloader(final String title, final String address) {
        this.mTitle = title;
        this.mRawAddress = address;

        Pattern pattern = Pattern.compile(IDSECTION_PATTERN);
        Matcher matcher = pattern.matcher(address);

        // アドレスパターンから可変部分を抽出.
        int startId = BlownUtils.ERROR_GENERAL;
        int endId = BlownUtils.ERROR_GENERAL;
        try {
            if (matcher.find()) {
                final String strStartId = matcher.group(IDSECTION_STARTID);
                final String strEndId = matcher.group(IDSECTION_ENDID);
                startId = Integer.parseInt(strStartId);
                endId = Integer.parseInt(strEndId);
            }
        } catch (final NumberFormatException e) {

        }

        // 可変部分の設定値を覚える.
        this.mStartId = startId;
        this.mEndId = endId;

        // 可変部分を簡単に置き換えられるようにしておく.
        this.mAddress = address.replaceAll(IDSECTION_PATTERN, REPLACEMENT_CHAR);

        ExLog.downloader.i("download address > " + mAddress);
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.downloader.DownloaderBase#getAddress(int)
     */
    @Override
    public String getAddress(int id) {
        if (0 <= id && id < getCount()) {
            return mAddress.replace(REPLACEMENT_CHAR, String.valueOf(id + mStartId));
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.downloader.DownloaderBase#getCount()
     */
    @Override
    public int getCount() {
        return mEndId - mStartId + 1;
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.downloader.DownloaderBase#getRawAddress()
     */
    @Override
    public String getRawAddress() {
        return mRawAddress;
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.downloader.DownloaderBase#getTitle()
     */
    @Override
    public String getTitle() {
        return mTitle;
    }

}
