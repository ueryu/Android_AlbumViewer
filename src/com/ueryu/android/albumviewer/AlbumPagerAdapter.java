/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ueryu.android.albumviewer.downloader.DownloaderBase;

/**
 * アルバムViewPagerのAdapter.
 * 
 * @author ueryu
 */
public final class AlbumPagerAdapter extends FragmentStatePagerAdapter {

    /** コンテキスト. */
    private final FragmentActivity mContext;

    /** ダウンローダ. */
    private final DownloaderBase mDownloader;

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     * @param downloader 対象となるダウンローダ
     */
    public AlbumPagerAdapter(final FragmentActivity context,
            final DownloaderBase downloader) {
        super(context.getSupportFragmentManager());
        this.mContext = context;
        this.mDownloader = downloader;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return mDownloader.getCount();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(final int position) {
        return ImagePageFragment.newInstance(mContext, mDownloader.getAddress(position));
    }

}
