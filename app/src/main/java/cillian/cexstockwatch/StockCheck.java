package cillian.cexstockwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class StockCheck extends Activity {

    String name;
    String url;
    String picUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_check);

        TextView itemName = (TextView) findViewById(R.id.itemName);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        name = intent.getStringExtra("NAME");
        picUrl = intent.getStringExtra("PIC");
        itemName.setText(name);
        check();
    }

    public void seeItem(View v)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("URL",url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void check()
    {
        findViewById(R.id.progressBar2).setVisibility(View.GONE);
        if(DetectConnection.checkInternetConnection(StockCheck.this)) {
            findViewById(R.id.connectionMessage).setVisibility(View.GONE);
            ImageView stockImage = (ImageView) findViewById(R.id.stockImage);
            DownloadImage downloadImage = new DownloadImage(stockImage);
            downloadImage.execute(picUrl);
            ProductGrabber grabber = new ProductGrabber(new DatabaseHandler(getBaseContext()), url, true);
            grabber.execute();
            try {
                grabber.get();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "An error occured please try again later", Toast.LENGTH_LONG).show();
            }
            Button button = (Button) findViewById(R.id.investigate);
            button.setClickable(false);


            TextView buy = (TextView) findViewById(R.id.buy);
            TextView cash = (TextView) findViewById(R.id.cash);
            TextView credit = (TextView) findViewById(R.id.credit);

            if (grabber.inStock) {
                button.setText("In Stock!");
            }


            grabber.price = grabber.price.substring(grabber.price.indexOf(";") + 1, grabber.price.indexOf(".") + 3);
            buy.setText(grabber.price);
            cash.setText(grabber.cash.substring(grabber.cash.indexOf(";") + 1, grabber.cash.indexOf(".") + 3));
            credit.setText(grabber.credit.substring(grabber.credit.indexOf(";") + 1, grabber.credit.indexOf(".") + 3));

            button.setClickable(true);

            findViewById(R.id.buy).setVisibility(View.VISIBLE);
            findViewById(R.id.cash).setVisibility(View.VISIBLE);
            findViewById(R.id.credit).setVisibility(View.VISIBLE);
            findViewById(R.id.buyHeader).setVisibility(View.VISIBLE);
            findViewById(R.id.cashHead).setVisibility(View.VISIBLE);
            findViewById(R.id.creditHead).setVisibility(View.VISIBLE);
            findViewById(R.id.itemName).setVisibility(View.VISIBLE);
            findViewById(R.id.investigate).setVisibility(View.VISIBLE);
            findViewById(R.id.stockImage).setVisibility(View.VISIBLE);
        }
        else
        {

        }
    }

    public void refreshStock(View view)
    {
        if (DetectConnection.checkInternetConnection(StockCheck.this))
        {
            findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
            findViewById(R.id.connectionMessage).setVisibility(View.GONE);
            check();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet! Tap to Refresh", Toast.LENGTH_SHORT).show();
        }
    }

}





