package cillian.cexstockwatch;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    WebView mWebView;
    String pass;
    String email;
    boolean loginComplete;
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


        hide_watch.setAnimationListener(new Animation.AnimationListener(){
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

        show_watch.setAnimationListener(new Animation.AnimationListener(){
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


                if(!expanded) {
                    ViewCompat.animate(expand).rotation(135f).withLayer().setDuration(800).setInterpolator(interpolator).start();
                    expanded = true;
                    showFAB();
                }

                else
                {
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
        if(handler.returnAmount() > 0) {
            Cursor c1 = handler.returnData();
            if (c1.moveToFirst()) {
                do {
                    url = c1.getString(0);
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

        if(!email.equals("Skipped"))
            url = url + "/member/login";

        loginComplete = false;
        boolean linkFromProfile = false;
        Intent intent = getIntent();
        String potentialUrl = intent.getStringExtra("URL");
        if (potentialUrl != null)
        {
            linkFromProfile = true;
            findViewById(R.id.splash).setVisibility(View.GONE);
            url = potentialUrl;
            email = "Skipped";
        }

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        if(!linkFromProfile)
            mWebView.loadUrl("https://" + url);
        else
            mWebView.loadUrl(url);
        mWebView.setWebViewClient(new cillian.cexstockwatch.WebviewExt() {

            public void onPageFinished(WebView view, String url) {

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
                if(currentURL.contains("login"))
                {

                    //TODO Need to check if login has succeeded or not
                    TextView loadingText = (TextView)findViewById(R.id.loading_text);
                    loadingText.setText("Logging In...");

                    //hide loading image
                    findViewById(R.id.progressBar1).setVisibility(View.GONE);
                    findViewById(R.id.loading_text).setVisibility(View.GONE);
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

        if(!url.contains("productDetail"))
            Toast.makeText(getBaseContext(), "Can only add item pages to watchlist!", Toast.LENGTH_LONG).show();

        else
        {
            ItemAdder item = new ItemAdder(url);
            item.execute();
        }
    }

    public void scanBarcode()
    {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null && resultCode==RESULT_OK) {
            String contents = intent.getStringExtra("SCAN_RESULT");
            String url = "https://ie.m.webuy.com/site/productDetail?productId=" + contents;
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("URL", url);
            startActivity(intent);
        }

        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class ItemAdder extends AsyncTask<Void,Void,Void>
    {
        private String url;
        private String str;
        ItemAdder(String url)
        {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                URL inputURL = new URL(url);

                BufferedReader br = new BufferedReader(new InputStreamReader(inputURL.openStream()));
                String strTemp;
                while(null != (strTemp = br.readLine()))
                {
                    if(strTemp.contains("productTitle"))
                    {
                        int start = strTemp.indexOf(">");
                        int end = strTemp.indexOf("<",start);
                        str = strTemp.substring(start + 1,end);
                        break;
                    }
                }
                br.close();
            }

            catch (Exception ex)
            {
                Toast.makeText(getBaseContext(), "Unable to add item please try again later", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DatabaseHandler handler = new DatabaseHandler(getBaseContext());
            handler.open();
            handler.insertData(str, url);
            handler.close();
            Toast.makeText(getBaseContext(), "Item added to watchlist!", Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);
        }
    }
}



