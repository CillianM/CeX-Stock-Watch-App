package cillian.cexstockwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

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
                findViewById(R.id.list).setVisibility(View.VISIBLE);
                findViewById(R.id.scan).setVisibility(View.VISIBLE);
            }
        });

        FloatingActionButton watch = (FloatingActionButton) findViewById(R.id.watch);
        FloatingActionButton list = (FloatingActionButton) findViewById(R.id.list);
        FloatingActionButton scan = (FloatingActionButton) findViewById(R.id.scan);
        watch.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                popup();
            }
        });

        list.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, Watchlist.class);
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
            Intent intent = new Intent(MainActivity.this, AddItemPopup.class);
            intent.putExtra("URL", url);
            startActivity(intent);
        }
    }

    public void scanBarcode()
    {
        try
        {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }

    }

    //alert dialog for downloadDialog
    public AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {

        AlertDialog.Builder downloadDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            downloadDialog = new AlertDialog.Builder(act, android.R.style.Theme_Material_Light_Dialog_Alert);
        }

        else
        {
            downloadDialog = new AlertDialog.Builder(act);
        }

        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try
                {
                    act.startActivity(intent);
                }

                catch (ActivityNotFoundException anfe)
                {
                    System.out.println(anfe);
                }
            }
        });

        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }
        });
        return downloadDialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {

                String contents = intent.getStringExtra("SCAN_RESULT");
                String url = "https://ie.m.webuy.com/site/productDetail?productId=" + contents;
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("URL", url);
                startActivity(intent);
            }
        }

    }
}



