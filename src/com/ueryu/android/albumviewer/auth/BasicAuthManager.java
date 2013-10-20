/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.auth;

import com.ueryu.android.blownlibrary.net.HttpClientLoaderCallbacks;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ueryu
 */
public final class BasicAuthManager {

    private static final BasicAuthManager INSTANCE = new BasicAuthManager();

    public static synchronized BasicAuthManager getInstance() {
        return INSTANCE;
    }

    private final Map<String, HttpClientLoaderCallbacks.AuthData> mCache = new HashMap<String, HttpClientLoaderCallbacks.AuthData>();

    public HttpClientLoaderCallbacks.AuthData getAuthData(final String address) {
        return mCache.get(address);
    }

    public void putAuthData(final String address, final HttpClientLoaderCallbacks.AuthData data) {
        mCache.put(address, data);
    }
}
