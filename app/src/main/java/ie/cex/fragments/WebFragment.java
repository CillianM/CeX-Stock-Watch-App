package ie.cex.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ie.cex.R;
import ie.cex.connectivity.DetectConnection;

/**
 * Created by Cillian on 17/07/2017.
 */

public class WebFragment extends Fragment {

    private static final String TAG = "LOG";
    WebView mWebView;
    private SwipeRefreshLayout swipeContainer;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLL_UP = 1;
    private static final int TOUCH_STATE_SCROLL_DOWN = 2;
    private int mTouchState = TOUCH_STATE_REST;

    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String url = "https://ie.m.webuy.com";
        mWebView = (WebView) view.findViewById(R.id.activity_main_webview);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        setScrollChanger();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);
            }
        });
        mWebView.loadUrl(url);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.webviewLayout);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!DetectConnection.checkInternetConnection(getActivity())) {
                    Toast.makeText(getActivity().getApplicationContext(), "No Internet! Tap to Refresh", Toast.LENGTH_SHORT).show();
                } else {
                    mWebView.loadUrl(mWebView.getUrl());
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        swipeContainer.setColorSchemeResources(R.color.red);


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setScrollChanger() {
        mWebView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (i >= 0 && i1 >= 0) {
                    if (i1 > i && mTouchState != TOUCH_STATE_SCROLL_UP) {
                        mTouchState = TOUCH_STATE_SCROLL_UP;
                    } else if (i1 < i && mTouchState != TOUCH_STATE_SCROLL_DOWN) {
                        mTouchState = TOUCH_STATE_SCROLL_DOWN;
                    }
                }
            }
        });
    }

    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    public String getUrl() {
        return mWebView.getUrl();
    }
}
