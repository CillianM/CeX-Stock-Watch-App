package cillian.cexstockwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;



public class StockCheck extends Activity {

    String name;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_check);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        TextView itemName = (TextView) findViewById(R.id.itemName);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .6), (int) (height * .4));

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        name = intent.getStringExtra("NAME");
        itemName.setText(name);

    }

    public void seeItem(View v)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("URL",url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void check(View v)
    {
        ImageButton stockResult = (ImageButton) findViewById(R.id.refresh);
        String productCode = url.substring((url.indexOf('=')+1));
        String urlToCheck = "https://ie.webuy.com/product.php?sku=" + productCode;
        CheckStock check = new CheckStock(urlToCheck,stockResult);
        check.execute();
    }

    private class CheckStock extends AsyncTask<Void,Void,Void>
    {
        private  ImageButton result;
        private String url;
        private String str;
        CheckStock(String url,ImageButton result)
        {
            this.result = result;
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
                    if(strTemp.contains("Out Of Stock"))
                    {
                        str = strTemp;
                        break;
                    }

                    if(strTemp.contains("I want to buy this item"))
                    {
                        str = strTemp;
                        break;
                    }
                }
                br.close();
            }

            catch (Exception ex)
            {
                System.out.println("Looks like there was a problem!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(str.contains("Out Of Stock"))
            {
                result.setImageResource(R.drawable.x_icon);
            }

            if(str.contains("I want to buy this item"))
            {
                result.setImageResource(R.drawable.tick_icon);
            }
            super.onPostExecute(aVoid);
        }
    }

}



