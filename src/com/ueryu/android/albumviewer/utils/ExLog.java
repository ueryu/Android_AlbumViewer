/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.utils;

import com.ueryu.android.blownlibrary.log.BlownLogUtil;

/**
 * @author ueryu
 */
public final class ExLog extends BlownLogUtil {

    public static final BlownLog base = new ImplAlways("base");
    public static final BlownLog imagefile = new ImplAlways("imagefile");
    public static final BlownLog repository = new ImplAlways("repository");
    public static final BlownLog downloader = new ImplAlways("downloader");
}
