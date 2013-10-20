/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * @author ueryu
 */
public final class DefaultPreferences {

    private static final String KEY_DEFAULT_ADDRESS_LINE = "default_addressLine";
    private static final String KEY_DEFAULT_REPOSITORY = "default_repository";

    /**
     * @param context コンテキスト
     * @return
     */
    public static String getDefaultAddressLine(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_DEFAULT_ADDRESS_LINE, null);
    }

    /**
     * @param context コンテキスト
     * @return
     */
    public static String getDefaultRepository(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_DEFAULT_REPOSITORY, null);
    }

    /**
     * @param context コンテキスト
     * @param address
     */
    public static void setDefaultAddressLine(final Context context, final String address) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_DEFAULT_ADDRESS_LINE, address)
                .commit();
    }

    /**
     * @param context コンテキスト
     * @param address
     */
    public static void setDefaultRepository(final Context context, final String address) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_DEFAULT_REPOSITORY, address)
                .commit();
    }

    /** 隠匿デフォルトコンストラクタ. */
    private DefaultPreferences() {
    }
}
