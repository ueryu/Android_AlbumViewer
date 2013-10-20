
package com.ueryu.android.albumviewer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ueryu.android.albumviewer.auth.BasicAuthManager;
import com.ueryu.android.albumviewer.auth.BasicAuthorizeDialogFragment;
import com.ueryu.android.albumviewer.downloader.DownloaderBase;
import com.ueryu.android.albumviewer.downloader.DownloaderFactory;
import com.ueryu.android.albumviewer.imagefile.ImageFileBase;
import com.ueryu.android.albumviewer.imagefile.ImageFileDownloadLoaderCallbacks;
import com.ueryu.android.albumviewer.imagefile.ImageFileManager;
import com.ueryu.android.albumviewer.repository.AlbumSelectorDialogFragment;
import com.ueryu.android.albumviewer.utils.DefaultPreferences;
import com.ueryu.android.blownlibrary.net.HttpClientLoaderCallbacks;
import com.ueryu.android.blownlibrary.utils.CompleteCallback;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that contain this fragment must implement the
 * {@link ImagePageFragment.OnFragmentInteractionListener} interface to handle interaction events. Use the {@link ImagePageFragment#newInstance} factory method
 * to create an instance of this fragment.
 */
public class ImagePageFragment extends Fragment implements
        BasicAuthorizeDialogFragment.OnResultFragmentCallback {

    private static final String TAG_AUTHORIZE = "authorize";
    private static final String TAG_REPOSITORY_SELECTOR = "repo_select";

    private static final String ARG_ADDRESS = "address";

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     * 
     * @param context コンテキスト
     * @param address アドレス
     * @return A new instance of fragment ImageFragment.
     */
    public static Fragment newInstance(final Context context, final String address) {
        Bundle args = new Bundle();
        args.putString(ARG_ADDRESS, address);
        return Fragment.instantiate(context, ImagePageFragment.class.getName(), args);
    }

    private String mAddress;

    private ImageView mImageView;

    // Lifecycles.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAddress = getArguments().getString(ARG_ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());

        return mImageView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // キャッシュから反映する.
        ImageFileManager.getInstance(getActivity()).getImageFile(mAddress, mImageFileListener);
    }

    // overrides.

    /**
     * 画像のダウンロードが完了したら、反映するリスナー.
     */
    private final CompleteCallback<ImageFileBase> mImageFileListener = new CompleteCallback<ImageFileBase>() {
        @Override
        public void notifyError(final int errorCode) {
            startDownload();
        }

        @Override
        public void onCompleted(final ImageFileBase result) {
            mImageView.setImageDrawable(result.getDrawable());
        }
    };

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
                    final String addressLine = DefaultPreferences.getDefaultAddressLine(getActivity());
                    final DownloaderBase downloader = DownloaderFactory.create(addressLine);
                    if (downloader == null) {
                        // リポジトリ設定画面を出す.
                        AlbumSelectorDialogFragment.showDialog(getChildFragmentManager(), TAG_REPOSITORY_SELECTOR);
                        return;
                    }
                    final String address = downloader.getAddress(0);

                    // 認証情報をキャッシュする.
                    final Uri uri = Uri.parse(address);
                    BasicAuthManager.getInstance().putAuthData(uri.getHost(), authData);

                    // 再度ダウンロードを開始する.
                    startDownload();
                    break;
                case BasicAuthorizeDialogFragment.OnResultFragmentCallback.RESULT_CANCEL:
                    // リポジトリ設定画面を出す.
                    AlbumSelectorDialogFragment.showDialog(getChildFragmentManager(), TAG_REPOSITORY_SELECTOR);
                    break;
            }
        }
    }

    // others.

    private final void startDownload() {
        final Bundle bundle = new Bundle();

        // 認証情報をキャッシュしていないか、確認する.
        final Uri uri = Uri.parse(mAddress);
        final HttpClientLoaderCallbacks.AuthData authData = BasicAuthManager.getInstance().getAuthData(uri.getHost());
        HttpClientLoaderCallbacks.setAuthDataToBundle(bundle, authData);

        // アドレスを設定しておく.
        bundle.putString(ImageFileDownloadLoaderCallbacks.KEY_ADDRESS, mAddress);

        // ファイルをダウンロードする.
        final LoaderCallbacks<ImageFileBase> loaderCallbacks = new ImageFileDownloadLoaderCallbacks(ImagePageFragment.this) {
            @Override
            protected void onAuthorizationRequired(String message) {
                final DialogFragment dialog = BasicAuthorizeDialogFragment.newInstance(message);
                dialog.show(getChildFragmentManager(), TAG_AUTHORIZE);
            }
        };
        getLoaderManager().initLoader(R.id.cached_image_loader, bundle, loaderCallbacks);
    }
}
