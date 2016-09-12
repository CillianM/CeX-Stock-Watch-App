package ie.cex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ie.cex.Connectivity.DetectConnection;
import ie.cex.Connectivity.DownloadImage;
import ie.cex.Connectivity.ProductGrabber;
import ie.cex.Connectivity.WatchlistGrabber;
import ie.cex.DatabaseHandler.DatabaseHandler;

import ie.cex.R;


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



       ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
        findViewById(R.id.connectionMessage).setVisibility(View.GONE);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .7), (int) (height * .7));

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
        if(DetectConnection.checkInternetConnection(StockCheck.this)) {
            findViewById(R.id.connectionMessage).setVisibility(View.GONE);
            ImageView stockImage = (ImageView) findViewById(R.id.stockImage);
            DownloadImage downloadImage = new DownloadImage(stockImage);
            downloadImage.execute(picUrl);
            WatchlistGrabber grabber = new WatchlistGrabber(new DatabaseHandler(getBaseContext()), url, true,findViewById(android.R.id.content));
            grabber.execute();
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





