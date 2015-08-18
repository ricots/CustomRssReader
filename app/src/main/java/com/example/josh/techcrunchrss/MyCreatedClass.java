package com.example.josh.techcrunchrss;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Created by Josh on 8/17/2015.
 */
public class MyCreatedClass extends Activity {

    private WebView mWebView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_webview);

        Bundle b = getIntent().getExtras();
        String KEY_LINK = b.getString("KEY_LINK");

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

        mWebView = (WebView)findViewById(R.id.tutView);

        //mWebView.setWebViewClient(new MyWebViewClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.loadUrl(KEY_LINK);

    }
/*
    public class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)&& mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    */
}

