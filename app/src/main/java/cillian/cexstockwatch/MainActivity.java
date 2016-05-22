package cillian.cexstockwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentUrl = "https://ie.m.webuy.com/";

        Intent intent = getIntent();
        String potentialUrl = intent.getStringExtra("URL");
        if (potentialUrl != null)
        {
            findViewById(R.id.splash).setVisibility(View.GONE);
            currentUrl = potentialUrl;
        }


        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(currentUrl);
        mWebView.setWebViewClient(new cillian.cexstockwatch.WebviewExt() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //hide loading image
                findViewById(R.id.progressBar1).setVisibility(View.GONE);
                //show webview
                findViewById(R.id.activity_main_webview).setVisibility(View.VISIBLE);
                findViewById(R.id.watch).setVisibility(View.VISIBLE);
                findViewById(R.id.profile).setVisibility(View.VISIBLE);
                findViewById(R.id.scan).setVisibility(View.VISIBLE);
            }
        });

        FloatingActionButton watch = (FloatingActionButton) findViewById(R.id.watch);
        FloatingActionButton profile = (FloatingActionButton) findViewById(R.id.profile);
        FloatingActionButton scan = (FloatingActionButton) findViewById(R.id.scan);
        watch.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                popup();
            }
        });

        profile.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        scan.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                scanBarcode();
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



