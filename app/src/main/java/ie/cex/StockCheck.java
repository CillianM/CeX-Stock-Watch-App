package ie.cex;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ie.cex.connectivity.DetectConnection;
import ie.cex.connectivity.DownloadImage;
import ie.cex.connectivity.WatchlistGrabber;
import ie.cex.handlers.UserHandler;


public class StockCheck extends Activity {

    private String url;
    private String picUrl;
    private String locationUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_check);
            UserHandler handler = new UserHandler(getBaseContext());
            handler.open();
            if (handler.returnAmount() > 0) {
                Cursor c1 = handler.returnData();
                if (c1.moveToFirst()) {
                    do {
                        locationUrl = c1.getString(0);
                    }
                    while (c1.moveToNext());
                }
            }

        TextView itemName = (TextView) findViewById(R.id.itemName);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);



       ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar2);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
        findViewById(R.id.connectionMessage).setVisibility(View.GONE);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .7), (int) (height * .7));

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
            String name = intent.getStringExtra("NAME");
        picUrl = intent.getStringExtra("PIC");
        itemName.setText(name);
        check();
    }

    public void check()
    {
        if(DetectConnection.checkInternetConnection(StockCheck.this)) {
            findViewById(R.id.connectionMessage).setVisibility(View.GONE);
            ImageView stockImage = (ImageView) findViewById(R.id.stockImage);
            DownloadImage downloadImage = new DownloadImage(stockImage, locationUrl);
            downloadImage.execute(picUrl);
            WatchlistGrabber grabber = new WatchlistGrabber(url, findViewById(android.R.id.content));
            grabber.execute();
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





