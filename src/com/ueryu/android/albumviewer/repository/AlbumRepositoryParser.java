/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.repository;

import com.ueryu.android.albumviewer.downloader.DownloaderBase;
import com.ueryu.android.albumviewer.downloader.DownloaderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ueryu
 */
public class AlbumRepositoryParser {

    public DownloaderBase[] parse(final String data) {
        // parse.
        final String[] lines = data.split("\n");

        final List<DownloaderBase> downloaderList = new ArrayList<DownloaderBase>(lines.length);
        for (final String line : lines) {
            DownloaderBase downloader = DownloaderFactory.create(line);
            if (downloader != null) {
                downloaderList.add(downloader);
            }
        }
        return downloaderList.toArray(new DownloaderBase[downloaderList.size()]);
    }
}
