package ie.cex.Connectivity;

import android.os.AsyncTask;
import android.os.StrictMode;

import ie.cex.DatabaseHandler.DatabaseHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ProductGrabber extends AsyncTask<Void,Void,Void>
{
    private DatabaseHandler handler;
    String tmpurl;
    public String name;
    Boolean wantPrice;
    public Boolean exists = true;
    public Boolean inStock = false;
    public String price = "Null";
    public String cash = "Null";
    public String credit = "Null";
    public String pictureURL;

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
                    if (strTemp.contains("productDetailscontetn")) {
                        strTemp = br.readLine();
                        int start = strTemp.indexOf(">");
                        if(strTemp.contains("Sorry! This product could not be found"))
                        {
                            exists = false;
                            break;
                        }
                    }

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
            br.close();


        }

        catch (Exception e)
        {
            System.out.println(e);
        }

        if(!wantPrice && exists)
        {
            handler.open();
            handler.insertData(name, tmpurl, pictureURL);
            handler.close();
        }
        else if(wantPrice && exists)
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
                    if(strTemp.contains("stockListTable"))
                    {
                        while(null != (strTemp = br.readLine()))
                        {
                            if(strTemp.contains("stockList"))
                            {
                                inStock = true;
                                break;
                            }

                            if(strTemp.contains("Sorry, this item is currently out of stock."))
                            {
                                inStock = false;
                                break;
                            }
                        }
                    }

                    if(inStock)
                        break;

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
