package ie.cex;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import ie.cex.Connectivity.DetectConnection;
import ie.cex.Connectivity.ProductGrabber;
import ie.cex.DatabaseHandler.DatabaseHandler;
import ie.cex.DatabaseHandler.UserHandler;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    WebView mWebView;
    private SwipeRefreshLayout swipeContainer;

    String pass;
    String email;
    boolean loginComplete;
    boolean locationupdated;
    boolean expanded = false;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    Animation show_watch;
    Animation hide_watch;
    Animation show_profile;
    Animation hide_profile;
    Animation show_scan ;
    Animation hide_scan;
    FloatingActionButton watch;
    FloatingActionButton profile;
    FloatingActionButton scan;
    FloatingActionButton expand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentUrl= "https://ie.m.webuy.com";
        final UserHandler handler = new UserHandler(getBaseContext());
        handler.open();
        if(handler.returnAmount() > 0) {
            Cursor c1 = handler.returnData();
            if (c1.moveToFirst()) {
                do {
                    currentUrl = "https://" + c1.getString(0) + "/";
                    email = c1.getString(1);
                    try {
                        pass = c1.getString(2);
                        pass = Crypto.decrypt(email,pass);
                    } catch (Exception e) {

                    }
                }
                while (c1.moveToNext());
            }
            handler.close();
        }
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.webviewLayout);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!DetectConnection.checkInternetConnection(MainActivity.this))
                {
                    findViewById(R.id.expand).setVisibility(View.GONE);
                    findViewById(R.id.splash).setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "No Internet! Tap to Refresh", Toast.LENGTH_SHORT).show();
                }
                else {
                    findViewById(R.id.splash).setVisibility(View.GONE);
                    mWebView.loadUrl(mWebView.getUrl());
                }
            }
        });
        swipeContainer.setColorSchemeResources(R.color.red);


        login();


        show_watch = AnimationUtils.loadAnimation(getApplication(), R.anim.left_fab_show);
        hide_watch = AnimationUtils.loadAnimation(getApplication(), R.anim.left_fab_hide);
        show_profile = AnimationUtils.loadAnimation(getApplication(), R.anim.top_fab_show);
        hide_profile = AnimationUtils.loadAnimation(getApplication(), R.anim.top_fab_hide);
        show_scan = AnimationUtils.loadAnimation(getApplication(), R.anim.right_fab_show);
        hide_scan = AnimationUtils.loadAnimation(getApplication(), R.anim.right_fab_hide);
        watch = (FloatingActionButton) findViewById(R.id.watch);
        profile = (FloatingActionButton) findViewById(R.id.profile);
        scan = (FloatingActionButton) findViewById(R.id.scan);
        expand = (FloatingActionButton) findViewById(R.id.expand);


        hide_watch.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                watch.setClickable(false);
                profile.setClickable(false);
                scan.setClickable(false);
            }
        });

        show_watch.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                watch.setClickable(true);
                profile.setClickable(true);
                scan.setClickable(true);
            }
        });

        expand.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                //Move the other buttons and make them visible
                final OvershootInterpolator interpolator = new OvershootInterpolator();


                if (!expanded) {
                    ViewCompat.animate(expand).rotation(135f).withLayer().setDuration(800).setInterpolator(interpolator).start();
                    expanded = true;
                    showFAB();
                } else {
                    ViewCompat.animate(expand).rotation(-180f).withLayer().setDuration(800).setInterpolator(interpolator).start();
                    expanded = false;
                    hideFAB();
                }
            }
        });

        watch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                popup();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                scanBarcode();
            }
        });


    }

    public void login()
    {
            String url = "https://ie.m.webuy.com";
            UserHandler handler = new UserHandler(getBaseContext());
            handler.open();
            if (handler.returnAmount() > 0) {
                Cursor c1 = handler.returnData();
                if (c1.moveToFirst()) {
                    do {
                        url = c1.getString(0);
                        email = c1.getString(1);
                        try {
                            pass = c1.getString(2);
                            pass = Crypto.decrypt(email, pass);
                        } catch (Exception e) {

                        }
                    }
                    while (c1.moveToNext());
                }
                handler.close();
            }
            Intent intent = getIntent();
            String loc = intent.getStringExtra("LOC");
            if (loc != null) {
                locationupdated = true;
            }

            if (!email.equals("Skipped") && !locationupdated)
                url = url + "/member/login";

            loginComplete = false;
            locationupdated = false;
            boolean linkFromProfile = false;

            String potentialUrl = intent.getStringExtra("URL");
            if (potentialUrl != null) {
                linkFromProfile = true;
                findViewById(R.id.splash).setVisibility(View.GONE);
                url = potentialUrl;
                email = "Skipped";
            }

            mWebView = (WebView) findViewById(R.id.activity_main_webview);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            if (!linkFromProfile)
                mWebView.loadUrl("https://" + url);
            else
                mWebView.loadUrl(url);
            mWebView.setWebViewClient(new WebviewExt() {

                public void onPageFinished(WebView view, String url) {

                    swipeContainer.setRefreshing(false);
                    if (!email.equals("Skipped")) {
                        final String js = "javascript:" +
                                "document.getElementById('uname').value = '" + email + "';" +
                                "document.getElementById('pwd').value = '" + pass + "';" +
                                "document.getElementById('loginBtn').click()";

                        if (Build.VERSION.SDK_INT >= 19) {
                            view.evaluateJavascript(js, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {

                                }
                            });
                        } else {
                            view.loadUrl(js);
                        }
                    }
                    String currentURL = mWebView.getUrl();
                    if (currentURL.contains("login")) {

                        //TODO Need to check if login has succeeded or not
                        TextView loadingText = (TextView) findViewById(R.id.loading_text);
                        loadingText.setText("Logging In...");


                    }

                    if (!DetectConnection.checkInternetConnection(MainActivity.this)) {
                        findViewById(R.id.progressBar1).setVisibility(View.GONE);
                        findViewById(R.id.loading_text).setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "No Internet! Tap to Refresh", Toast.LENGTH_SHORT).show();
                    } else {

                        //hide loading image
                        findViewById(R.id.progressBar1).setVisibility(View.GONE);
                        findViewById(R.id.loading_text).setVisibility(View.GONE);
                        findViewById(R.id.splash).setVisibility(View.GONE);
                        //show webview
                        findViewById(R.id.activity_main_webview).setVisibility(View.VISIBLE);
                        findViewById(R.id.expand).setVisibility(View.VISIBLE);
                    }
                }
            });
    }

    @Override
    public void onBackPressed()
    {
        if(mWebView.canGoBack())
        {
            mWebView.goBack();
        }

        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mWebView.clearCache(true);
        getBaseContext().deleteDatabase("webview.db");
        getBaseContext().deleteDatabase("webviewCache.db");
    }

    public void refresh(View view)
    {
        if (!DetectConnection.checkInternetConnection(MainActivity.this))
        {
            Toast.makeText(getApplicationContext(), "No Internet! Tap to Refresh", Toast.LENGTH_SHORT).show();
        }
        else {

            mWebView.loadUrl(mWebView.getUrl());
            findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
        }
    }

    private void hideFAB() {

        watch.startAnimation(hide_watch);
        scan.startAnimation(hide_scan);
        profile.startAnimation(hide_profile);
    }

    private void showFAB() {
        watch.startAnimation(show_watch);
        scan.startAnimation(show_scan);
        profile.startAnimation(show_profile);
    }

    public void popup()
    {

        String url = mWebView.getUrl();

        if(!url.contains("product-detail"))
            Toast.makeText(getBaseContext(), "Can only add item pages to watchlist!", Toast.LENGTH_LONG).show();

        else
        {
            if(DetectConnection.checkInternetConnection(MainActivity.this)) {
                try {
                    ProductGrabber grabber = new ProductGrabber(new DatabaseHandler(getBaseContext()), url);
                    grabber.execute();
                    grabber.get(1000, TimeUnit.MILLISECONDS);
                    Toast.makeText(getBaseContext(), "Item added to watchlist!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "An error occured please try again later", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(), "No Internet! Try Again Later", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void scanBarcode()
    {
        Intent intent = new Intent(MainActivity.this, ScanningActivity.class);
        startActivity(intent);
    }
}



