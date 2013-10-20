/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.imagefile;

import android.content.Context;

import com.ueryu.android.blownlibrary.net.HttpClientLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author ueryu
 */
public class ImageFileDownloadLoader extends HttpClientLoader<ImageFileBase> {

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     * @param address アドレス
     */
    public ImageFileDownloadLoader(final Context context, final String address) {
        super(context, address);
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.utils.HttpClientLoader# onHttpResponseInBackground(java.lang.String)
     */
    @Override
    protected ImageFileBase onHttpResponseInBackground(final HttpEntity responsedData) {

        if (getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            try {
                if (responsedData == null) {
                    return null;
                }

                final byte[] data = EntityUtils.toByteArray(responsedData);
                final ImageFileBase imageFile = new AndroidSupportedImageFile(
                        getContext().getResources(),
                        getAddress(),
                        data);

                // キャッシュにいれておく.
                ImageFileManager.getInstance(getContext()).putImageFile(imageFile);

                return imageFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
