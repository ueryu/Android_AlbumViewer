/**
 * Copyright (c) 2013 ueryu All Rights Reserved.
 */

package com.ueryu.android.albumviewer.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ueryu.android.albumviewer.R;
import com.ueryu.android.blownlibrary.net.HttpClientLoaderCallbacks;

/**
 * @author ueryu
 */
public final class BasicAuthorizeDialogFragment extends DialogFragment {

    public static interface OnResultFragmentCallback {
        public static final int RESULT_OK = R.id.result_ok;
        public static final int RESULT_CANCEL = R.id.result_cancel;

        public void onResult(final String tag, final int result,
                final HttpClientLoaderCallbacks.AuthData data);
    }

    private static final String KEY_MESSAGE = "message";

    /**
     * インスタンス生成.
     * 
     * @param message 表示するメッセージ
     * @return インスタンス
     */
    public static DialogFragment newInstance(final String message) {
        final DialogFragment dialogFragment = new BasicAuthorizeDialogFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    private EditText mIdEditText;

    private EditText mPasswordEditText;

    /** OKボタン押下時処理. */
    private final DialogInterface.OnClickListener mOkListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            final HttpClientLoaderCallbacks.AuthData data = new HttpClientLoaderCallbacks.AuthData();
            data.mUsername = mIdEditText.getText().toString();
            data.mPassword = mPasswordEditText.getText().toString();

            final Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof OnResultFragmentCallback) {
                final OnResultFragmentCallback callback = (OnResultFragmentCallback) parentFragment;
                callback.onResult(getTag(), OnResultFragmentCallback.RESULT_OK, data);
            }
        }
    };

    /** Cancelボタン押下時処理. */
    private final DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            onCancel(dialog);
        }
    };

    @Override
    public void onCancel(DialogInterface dialog) {
        final Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnResultFragmentCallback) {
            final OnResultFragmentCallback callback = (OnResultFragmentCallback) parentFragment;
            callback.onResult(getTag(), OnResultFragmentCallback.RESULT_CANCEL, null);
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final String message = getArguments().getString(KEY_MESSAGE);

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.authorize_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.authorizeDialogTitle);
        builder.setPositiveButton(android.R.string.ok, mOkListener);
        builder.setNegativeButton(android.R.string.cancel, mCancelListener);
        builder.setView(view);

        final TextView messageTextView = (TextView) view.findViewById(R.id.messageTextBox);
        messageTextView.setText(message);

        mIdEditText = (EditText) view.findViewById(R.id.idEditText);
        mPasswordEditText = (EditText) view.findViewById(R.id.passwordEditText);

        return builder.create();
    }
}
