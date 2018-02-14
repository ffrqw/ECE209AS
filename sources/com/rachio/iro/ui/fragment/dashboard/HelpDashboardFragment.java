package com.rachio.iro.ui.fragment.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.rachio.iro.R;
import com.rachio.iro.ui.fragment.BaseFragment;

public class HelpDashboardFragment extends BaseFragment {
    private static final String TAG = HelpDashboardFragment.class.getCanonicalName();

    public static HelpDashboardFragment newInstance(String article) {
        HelpDashboardFragment fragment = new HelpDashboardFragment();
        Bundle args = new Bundle();
        args.putString("article", article);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String url;
        final WebView webView = new WebView(getActivity());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(PluginState.ON);
        webView.setWebViewClient(new WebViewClient());
        String article = getArguments().getString("article");
        if (article != null) {
            url = "http://www.support.rach.io/article/" + article;
        } else {
            url = "http://www.support.rach.io";
        }
        Log.d(TAG, "opening " + url);
        webView.loadUrl(url);
        webView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 4 || !webView.canGoBack()) {
                    return false;
                }
                webView.goBack();
                return true;
            }
        });
        return webView;
    }

    public final String getSection() {
        return getString(R.string.navigation_section_help);
    }
}
