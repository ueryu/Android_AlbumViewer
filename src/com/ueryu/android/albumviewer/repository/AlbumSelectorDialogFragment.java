
package com.ueryu.android.albumviewer.repository;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ueryu.android.albumviewer.R;
import com.ueryu.android.albumviewer.auth.BasicAuthManager;
import com.ueryu.android.albumviewer.auth.BasicAuthorizeDialogFragment;
import com.ueryu.android.albumviewer.downloader.DownloaderBase;
import com.ueryu.android.albumviewer.utils.DefaultPreferences;
import com.ueryu.android.blownlibrary.net.HttpClientLoader;
import com.ueryu.android.blownlibrary.net.HttpClientLoaderCallbacks;

public class AlbumSelectorDialogFragment extends DialogFragment implements
        BasicAuthorizeDialogFragment.OnResultFragmentCallback {

    private static final String TAG_AUTHORIZE = "authorize";

    public static interface OnResultFragmentCallback {
        public static final int RESULT_OK = R.id.result_ok;
        public static final int RESULT_CANCEL = R.id.result_cancel;

        public void onResult(final String tag,
                final int result,
                final String addressLine);
    }

    private class AlbumSelectorListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private final DownloaderBase[] mDownloaders;

        private AlbumSelectorListAdapter(final DownloaderBase[] downloaders) {
            this.mInflater = getActivity().getLayoutInflater();
            this.mDownloaders = downloaders;
        }

        /*
         * (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return mDownloaders.length;
        }

        /*
         * (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(final int position) {
            return mDownloaders[position];
        }

        /*
         * (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(final int position) {
            return position;
        }

        /*
         * (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final DownloaderBase downloader = mDownloaders[position];

            final View view = mInflater.inflate(android.R.layout.two_line_list_item, null);
            final TextView textViewTitle = (TextView) view.findViewById(android.R.id.text1);
            textViewTitle.setText(downloader.getTitle());
            final TextView textViewDesc = (TextView) view.findViewById(android.R.id.text2);
            textViewDesc.setText(downloader.getRawAddress());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final String addressLine = downloader.toString();
                    DefaultPreferences.setDefaultAddressLine(getActivity(), addressLine);

                    final Fragment parentFragment = getParentFragment();
                    if (parentFragment instanceof OnResultFragmentCallback) {
                        final OnResultFragmentCallback callback = (OnResultFragmentCallback) parentFragment;
                        callback.onResult(getTag(), OnResultFragmentCallback.RESULT_OK, addressLine);
                    }
                    dismiss();
                }
            });

            return view;
        }
    }

    /**
     * リポジトリ設定画面を出す.
     */
    public static final void showDialog(final FragmentManager fragmentManager, final String tag) {
        final DialogFragment dialog = AlbumSelectorDialogFragment.newInstance();
        dialog.show(fragmentManager, tag);
    }

    /**
     * インスタンス生成.
     * 
     * @return インスタンス
     */
    private static DialogFragment newInstance() {
        final DialogFragment dialogFragment = new AlbumSelectorDialogFragment();
        dialogFragment.setCancelable(false);
        return dialogFragment;
    }

    /** リポジトリアドレス入力欄. */
    private EditText mRepositoryEditText = null;
    /** リポジトリアドレス送信ボタン. */
    private Button mRepositorySubmitButton = null;

    /** リストビュー. */
    private ListView mListView = null;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_album_selector, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        mRepositoryEditText = (EditText) view.findViewById(R.id.repositoryEditText);
        mRepositorySubmitButton = (Button) view.findViewById(R.id.repositorySubmitButton);
        mListView = (ListView) view.findViewById(R.id.listView);

        mRepositorySubmitButton.setOnClickListener(mSubmitListener);

        final String repository = DefaultPreferences.getDefaultRepository(getActivity());
        if (repository != null) {
            // リポジトリ名をセットして、取得を行う.
            mRepositoryEditText.setText(repository);
            mRepositorySubmitButton.performClick();
        }

        return builder.create();
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.dialogs.AuthorizeDialogFragment. OnResultFragmentCallback#onResult(java.lang.String, int, com.ueryu.android
     * .albumviewer.dialogs.AuthorizeDialogFragment.OnResultFragmentCallback .ResultData)
     */
    @Override
    public void onResult(final String tag,
            final int result,
            final HttpClientLoaderCallbacks.AuthData authData) {

        if (TAG_AUTHORIZE.equals(tag)) {
            // 認証ダイアログからの戻り.

            switch (result) {
                case BasicAuthorizeDialogFragment.OnResultFragmentCallback.RESULT_OK:
                    final String address = DefaultPreferences.getDefaultRepository(getActivity());

                    // 認証情報をキャッシュする.
                    final Uri uri = Uri.parse(address);
                    BasicAuthManager.getInstance().putAuthData(uri.getHost(), authData);

                    // 再度ダウンロードを開始する.
                    startDownload(address);
                    break;
                case BasicAuthorizeDialogFragment.OnResultFragmentCallback.RESULT_CANCEL:
                    break;
            }
        }
    }

    private final View.OnClickListener mSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final String address = mRepositoryEditText.getText().toString();
            if (address != null && address.length() > 0) {
                DefaultPreferences.setDefaultRepository(getActivity(), address);

                startDownload(address);
            }
        }
    };

    private final void startDownload(final String address) {
        final Bundle bundle = new Bundle();

        // 認証情報をキャッシュしていないか、確認する.
        final Uri uri = Uri.parse(address);
        final HttpClientLoaderCallbacks.AuthData authData = BasicAuthManager.getInstance().getAuthData(uri.getHost());
        HttpClientLoaderCallbacks.setAuthDataToBundle(bundle, authData);

        LoaderCallbacks<DownloaderBase[]> loaderCallbacks = new HttpClientLoaderCallbacks<DownloaderBase[]>() {

            @Override
            protected HttpClientLoader<DownloaderBase[]> onCreateHttpClientLoader(int id, Bundle bundle) {
                return new AlbumRepositoryLoader(getActivity(), address);
            }

            @Override
            protected void onHttpClientLoaderReset(HttpClientLoader<DownloaderBase[]> loader) {
                // nop.
            }

            @Override
            protected void onHttpClientLoadFinished(HttpClientLoader<DownloaderBase[]> loader, DownloaderBase[] data) {
                if (data != null) {
                    mListView.setAdapter(new AlbumSelectorListAdapter(data));
                }
                getLoaderManager().destroyLoader(R.id.album_repository_loader);
            }

            @Override
            protected void onHttpClientAuthorizationRequired(
                    final HttpClientLoader<DownloaderBase[]> loader,
                    final String message) {
                // BASIC認証
                final DialogFragment dialog = BasicAuthorizeDialogFragment.newInstance(message);
                dialog.show(getChildFragmentManager(), TAG_AUTHORIZE);
                getLoaderManager().destroyLoader(R.id.album_repository_loader);
            }

            @Override
            protected void onHttpClientLoadError(HttpClientLoader<DownloaderBase[]> loader) {
                // エラー発生.
                getLoaderManager().destroyLoader(R.id.album_repository_loader);
            }
        };

        getLoaderManager().initLoader(R.id.album_repository_loader, bundle, loaderCallbacks);
    }
}
