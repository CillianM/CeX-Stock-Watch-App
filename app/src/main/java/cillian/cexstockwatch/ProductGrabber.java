package cillian.cexstockwatch;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ProductGrabber extends AsyncTask<Void,Void,Void>
{
    private DatabaseHandler handler;
    String tmpurl;
    String name;
    Boolean wantPrice;
    Boolean inStock = false;
    String price = "Null";
    String cash = "Null";
    String credit = "Null";
    String pictureURL;

    public ProductGrabber(DatabaseHandler handler, String tmpurl,boolean wantPrice)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.handler = handler;
        this.tmpurl = tmpurl;
        this.wantPrice = wantPrice;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try {
            URL url = new URL(tmpurl);
            String str;

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp;
            while(null != (strTemp = br.readLine()))
            {
                if(!wantPrice) {
                    if (strTemp.contains("productTitle")) {
                        int start = strTemp.indexOf(">");
                        int end = strTemp.indexOf("<", start);
                        str = strTemp.substring(start + 1, end);
                        name = str;
                    }

                    if (strTemp.contains("m-card-light")) {
                        int start = strTemp.indexOf("=") + 2;
                        str = strTemp.substring(start, strTemp.length());
                        start = str.indexOf("=") + 2;
                        str = str.substring(start, str.length());
                        int end = str.indexOf("\"");
                        pictureURL = str.substring(0, end);
                        if (!wantPrice)
                            break;
                    }
                }
                else
                {
                    if (strTemp.contains("id=\"wesellFor")) {
                        strTemp = br.readLine();
                        int end = strTemp.indexOf("<");
                        price = strTemp.substring(0,end);
                    }

                    if (strTemp.contains("id=\"webuyFor")) {
                        strTemp = br.readLine();
                        int end = strTemp.indexOf("<");
                        cash = strTemp.substring(0,end);
                    }

                    if (strTemp.contains("id=\"webuyVoucher")) {
                        strTemp = br.readLine();
                        strTemp = br.readLine();
                        int end = strTemp.indexOf("<");
                        credit = strTemp.substring(0,end);
                    }
                }
            }
            br.close();


        }

        catch (Exception e)
        {
            System.out.println(e);
        }

        if(!wantPrice)
        {
            handler.open();
            handler.insertData(name, tmpurl, pictureURL);
            handler.close();
        }
        else
        {
            try
            {
                String productCode = tmpurl.substring((tmpurl.indexOf('=')+1));
                String urlToCheck = "https://ie.webuy.com/product.php?sku=" + productCode;
                URL url = new URL(urlToCheck);

                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String strTemp;
                while(null != (strTemp = br.readLine()))
                {
                    if(strTemp.contains("Out Of Stock"))
                    {
                        inStock = false;
                        break;
                    }

                    if(strTemp.contains("I want to buy this item"))
                    {
                        inStock = true;
                        break;
                    }
                }
                br.close();
            }

            catch (Exception e)
            {
                System.out.println(e);
            }
        }

       return null;
    }

}
