package ie.cex;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ie.cex.Connectivity.DetectConnection;
import ie.cex.Connectivity.DownloadImage;
import ie.cex.Connectivity.ProductGrabber;
import ie.cex.Connectivity.ScannerGrabber;
import ie.cex.DatabaseHandler.DatabaseHandler;
import ie.cex.DatabaseHandler.ScannerHandler;

import java.text.DecimalFormat;
import java.util.List;

import ie.cex.R;


public class ScanningActivity extends Activity {

    int deleteCounter = 0;
    linkArrayAdaptor listAdapter;
    ScannerGrabber grabber = null;
    String [] listOfItems;
    String [] picURL;
    String [] listOfBuy;
    String [] listOfCash;
    String [] listOfCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scanning);

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.itemListRefresh);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(deleteCounter > 0) {
                    ScannerHandler handler = new ScannerHandler(getBaseContext());
                    handler.open();
                    handler.deleteList();
                    handler.createTable();
                    handler.close();
                    deleteCounter = 0;
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "List Cleared", Toast.LENGTH_SHORT).show();
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Swipe Again To Clear List", Toast.LENGTH_SHORT).show();
                    deleteCounter++;
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        swipeContainer.setColorSchemeResources(R.color.red);
        refreshList();


    }



    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.i("xZing", "contents: " + contents + " format: " + format); // Handle successful scan
                addItem("https://ie.m.webuy.com/site/productDetail?productId=" + contents);
                Intent i = getIntent();
                finish();
                startActivity(i);

            } else if (resultCode == RESULT_CANCELED) { // Handle cancel
                Log.i("xZing", "Cancelled");
            }
        }
    }

    public void refreshList()
    {
        ScannerHandler handler = new ScannerHandler(getBaseContext());
        handler.open();
        Cursor c1 = handler.returnData();
        int length = handler.returnAmount();
        listOfItems = new String[length];
        listOfBuy = new String[length];
        listOfCash = new String[length];
        listOfCredit = new String[length];
        picURL = new String[length];
        int index = 0;
        if (c1.moveToFirst()) {
            do {
                listOfItems[index] = c1.getString(1);
                picURL[index] = c1.getString(2);
                listOfBuy[index] = c1.getString(3);
                listOfCash[index] = c1.getString(4);
                listOfCredit[index] = c1.getString(5);
                index++;
            }
            while (c1.moveToNext());
        }

        handler.close();

        ListView mainListView = (ListView) findViewById(R.id.scanList);

            //create listener for each link in list
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //on item click create a url and open it in the browser
                public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                    String url = listOfItems[position];
                    String buy = listOfBuy[position];
                    String cash = listOfCash[position];
                    String credit = listOfCredit[position];

                    ImageView stockImage = (ImageView) findViewById(R.id.itemImage);
                    DownloadImage downloadImage = new DownloadImage(stockImage);
                    downloadImage.execute(picURL[position]);

                    TextView selectedCashValue = (TextView) findViewById(R.id.SelectedCashValue);
                    selectedCashValue.setText(cash);
                    TextView selectedCreditValue = (TextView) findViewById(R.id.SelectedCreditValue);
                    selectedCreditValue.setText(credit);
                    hideFinalPrices();
                }
            });

            mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                //on item click create a url and open it in the browser
                public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                    return true;
                }
            });

        listAdapter = new linkArrayAdaptor(this, listOfItems);
            mainListView.setAdapter(listAdapter);
    }

    public void addItem(String url)
    {
        if (DetectConnection.checkInternetConnection(ScanningActivity.this))
        {
            View view = findViewById(android.R.id.content);
            grabber = new ScannerGrabber(new ScannerHandler(getBaseContext()), url, true,getBaseContext(),view);
            grabber.execute();
            try {
                grabber.get();
                recreate();
            }
            catch(Exception e)
            {}
        }
        else
        {
            Toast.makeText(getBaseContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void scanNew(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void calculateTotals(View view)
    {
        double totalCash = 0;
        double totalCredit = 0;
        for(int i = 0; i < listOfItems.length; i++)
        {
            String tmp = listOfCash[i];
            double current = Double.parseDouble(tmp);
            totalCash = totalCash + current;

            tmp = listOfCredit[i];
            current = Double.parseDouble(tmp);
            totalCredit = totalCredit + current;
        }

        DecimalFormat df = new DecimalFormat("#.##");

        TextView cashValue = (TextView) findViewById(R.id.CashValue);
        cashValue.setText(df.format(totalCash));
        TextView creditValue = (TextView) findViewById(R.id.CreditValue);
        creditValue.setText(df.format(totalCredit));
        showFinalPrices();


    }

    public void showFinalPrices()
    {
        findViewById(R.id.ScanNew).setVisibility(View.GONE);
        findViewById(R.id.CalculateTotal).setVisibility(View.GONE);
        findViewById(R.id.CashValue).setVisibility(View.VISIBLE);
        findViewById(R.id.CreditValue).setVisibility(View.VISIBLE);
        findViewById(R.id.CashIcon).setVisibility(View.VISIBLE);
        findViewById(R.id.CreditIcon).setVisibility(View.VISIBLE);
    }

    public void hideFinalPrices()
    {
        findViewById(R.id.ScanNew).setVisibility(View.VISIBLE);
        findViewById(R.id.CalculateTotal).setVisibility(View.VISIBLE);
        findViewById(R.id.CashValue).setVisibility(View.GONE);
        findViewById(R.id.CreditValue).setVisibility(View.GONE);
        findViewById(R.id.CashIcon).setVisibility(View.GONE);
        findViewById(R.id.CreditIcon).setVisibility(View.GONE);
    }

}
