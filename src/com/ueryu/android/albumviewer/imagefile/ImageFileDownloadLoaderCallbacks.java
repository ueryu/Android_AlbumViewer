/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.imagefile;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ueryu.android.albumviewer.utils.ExLog;
import com.ueryu.android.blownlibrary.net.HttpClientLoader;
import com.ueryu.android.blownlibrary.net.HttpClientLoaderCallbacks;

/**
 * ImageFileダウンロード用LoaderCallbacks.
 * 
 * @author ueryu
 */
public abstract class ImageFileDownloadLoaderCallbacks extends HttpClientLoaderCallbacks<ImageFileBase> {

    public static final String KEY_ADDRESS = "address";

    private final Fragment mFragment;
    private int mId;

    private String mAddress = null;

    public ImageFileDownloadLoaderCallbacks(final Fragment fragment) {
        this.mFragment = fragment;
    }

    protected abstract void onAuthorizationRequired(final String message);

    @Override
    protected HttpClientLoader<ImageFileBase> onCreateHttpClientLoader(
            final int id,
            final Bundle bundle) {
        mId = id;
        mAddress = bundle.getString(KEY_ADDRESS);

        ExLog.base.d("download imageFile: " + mAddress);
        return new ImageFileDownloadLoader(mFragment.getActivity(), mAddress);
    }

    @Override
    protected void onHttpClientLoaderReset(final HttpClientLoader<ImageFileBase> loader) {
        ExLog.base.d("loader reset");
    }

    @Override
    protected void onHttpClientLoadFinished(
            final HttpClientLoader<ImageFileBase> loader,
            final ImageFileBase imageFile) {
        if (imageFile != null) {
            ImageFileManager.getInstance(mFragment.getActivity()).putImageFile(imageFile);
        }
        mFragment.getLoaderManager().destroyLoader(mId);
    }

    @Override
    protected void onHttpClientAuthorizationRequired(
            final HttpClientLoader<ImageFileBase> loader,
            final String message) {
        onAuthorizationRequired(message);
        mFragment.getLoaderManager().destroyLoader(mId);
    }

    @Override
    protected void onHttpClientLoadError(HttpClientLoader<ImageFileBase> loader) {
        // エラー発生.
        mFragment.getLoaderManager().destroyLoader(mId);
    }
}
