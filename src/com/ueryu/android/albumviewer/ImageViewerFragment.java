
package com.ueryu.android.albumviewer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ueryu.android.albumviewer.auth.BasicAuthManager;
import com.ueryu.android.albumviewer.auth.BasicAuthorizeDialogFragment;
import com.ueryu.android.albumviewer.downloader.DownloaderBase;
import com.ueryu.android.albumviewer.downloader.DownloaderFactory;
import com.ueryu.android.albumviewer.imagefile.ImageFileBase;
import com.ueryu.android.albumviewer.imagefile.ImageFileDownloadLoaderCallbacks;
import com.ueryu.android.albumviewer.repository.AlbumSelectorDialogFragment;
import com.ueryu.android.albumviewer.utils.DefaultPreferences;
import com.ueryu.android.blownlibrary.net.HttpClientLoader;
import com.ueryu.android.blownlibrary.net.HttpClientLoaderCallbacks;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that contain this fragment must implement the
 * {@link ImageViewerFragment.OnFragmentInteractionListener} interface to handle interaction events. Use the {@link ImageViewerFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class ImageViewerFragment extends Fragment implements
        BasicAuthorizeDialogFragment.OnResultFragmentCallback,
        AlbumSelectorDialogFragment.OnResultFragmentCallback {

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_ALBUMREPO = "albumrepo";

    private static final String TAG_AUTHORIZE = "authorize";
    private static final String TAG_REPOSITORY_SELECTOR = "repo_select";

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     * 
     * @param context コンテキスト
     * @return A new instance of fragment ImageViewerFragment.
     */
    public static Fragment newInstance(final Context context) {
        return Fragment.instantiate(context, ImageViewerFragment.class.getName());
    }

    /** ViewPager. */
    private ViewPager mViewPager;

    // Lifecycles.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_viewer, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(getResources().getInteger(R.integer.offscreen_page_limit));

        final Intent startIntent = getActivity().getIntent();
        final String addressLine = DefaultPreferences.getDefaultAddressLine(getActivity());
        if (savedInstanceState == null) {
            if (startIntent != null
                    && SCHEME_ALBUMREPO.equalsIgnoreCase(startIntent.getScheme())) {
                // schemeによる起動.
                final Uri uri = startIntent.getData();
                final String repoAddress = SCHEME_HTTP + ":" + uri.getSchemeSpecificPart();
                DefaultPreferences.setDefaultRepository(getActivity(), repoAddress);

                // リポジトリ設定画面を出す.
                AlbumSelectorDialogFragment.showDialog(getChildFragmentManager(), TAG_REPOSITORY_SELECTOR);

            } else {
                if (addressLine == null) {
                    // リポジトリ設定画面を出す.
                    AlbumSelectorDialogFragment.showDialog(getChildFragmentManager(), TAG_REPOSITORY_SELECTOR);
                }
            }
        } else {
            if (addressLine != null) {
                // 既にデフォルト設定がある.
                setAddressLine(addressLine);
            }
        }
    }

    // overrides.

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
                    startDownload(downloader);
                    break;
                case BasicAuthorizeDialogFragment.OnResultFragmentCallback.RESULT_CANCEL:
                    // リポジトリ設定画面を出す.
                    AlbumSelectorDialogFragment.showDialog(getChildFragmentManager(), TAG_REPOSITORY_SELECTOR);
                    break;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ueryu.android.albumviewer.repository.AlbumSelectorDialog.OnResultFragmentCallback#onResult(java.lang.String, int, java.lang.String)
     */
    @Override
    public void onResult(final String tag,
            final int result,
            final String addressLine) {

        if (TAG_REPOSITORY_SELECTOR.equals(tag)) {
            // リポジトリ選択ダイアログからの戻り.

            switch (result) {
                case AlbumSelectorDialogFragment.OnResultFragmentCallback.RESULT_OK:
                    setAddressLine(addressLine);
                    break;
                case AlbumSelectorDialogFragment.OnResultFragmentCallback.RESULT_CANCEL:
                    // nop.
                    break;
            }
        }
    }

    // others.

    private void setAddressLine(final String addressLine) {
        DefaultPreferences.setDefaultAddressLine(getActivity(), addressLine);
        final DownloaderBase downloader = DownloaderFactory.create(addressLine);

        if (downloader != null) {
            startDownload(downloader);
        }
    }

    /**
     * ダウンロードを開始する.
     * 
     * @param downloader ダウンローダ.
     */
    private void startDownload(final DownloaderBase downloader) {
        final String address = downloader.getAddress(0);
        final Bundle bundle = new Bundle();

        // 認証情報をキャッシュしていないか、確認する.
        final Uri uri = Uri.parse(address);
        final HttpClientLoaderCallbacks.AuthData authData = BasicAuthManager.getInstance().getAuthData(uri.getHost());
        HttpClientLoaderCallbacks.setAuthDataToBundle(bundle, authData);

        // アドレスを設定しておく.
        bundle.putString(ImageFileDownloadLoaderCallbacks.KEY_ADDRESS, address);

        // ファイルをダウンロードする.
        final LoaderCallbacks<ImageFileBase> loaderCallbacks = new ImageFileDownloadLoaderCallbacks(ImageViewerFragment.this) {
            @Override
            protected void onAuthorizationRequired(String message) {
                final DialogFragment dialog = BasicAuthorizeDialogFragment.newInstance(message);
                dialog.show(getChildFragmentManager(), TAG_AUTHORIZE);
            }

            @Override
            protected void onHttpClientLoadFinished(HttpClientLoader<ImageFileBase> loader, ImageFileBase imageFile) {
                super.onHttpClientLoadFinished(loader, imageFile);

                if (imageFile != null) {
                    // ViewPagerにページをセットする.
                    mViewPager.setAdapter(new AlbumPagerAdapter(getActivity(), downloader));
                }
            }

        };

        getLoaderManager().initLoader(R.id.cached_image_loader, bundle, loaderCallbacks);
    }
}
