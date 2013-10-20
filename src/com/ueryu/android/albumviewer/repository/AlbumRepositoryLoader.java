/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.repository;

import android.content.Context;

import com.ueryu.android.albumviewer.downloader.DownloaderBase;
import com.ueryu.android.blownlibrary.net.HttpClientLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author ueryu
 */
public class AlbumRepositoryLoader extends HttpClientLoader<DownloaderBase[]> {

    /**
     * コンストラクタ.
     * 
     * @param context コンテキスト
     * @param address アドレス
     */
    public AlbumRepositoryLoader(final Context context, final String address) {
        super(context, address);
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.utils.HttpClientLoader#onResponsed(java .lang.String)
     */
    @Override
    protected DownloaderBase[] onHttpResponseInBackground(final HttpEntity entity) {

        if (getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            if (entity == null) {
                return null;
            }

            final AlbumRepositoryParser parser = new AlbumRepositoryParser();
            try {
                final String charset = "utf-8";
                final String data = EntityUtils.toString(entity, charset);
                return parser.parse(data);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

}
