package ie.cex.Connectivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ie.cex.DatabaseHandler.ScannerHandler;
import ie.cex.R;
import ie.cex.ScanningActivity;
import ie.cex.linkArrayAdaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ScannerGrabber extends AsyncTask<Void,Void,Void>
{
    private ScannerHandler handler;
    private Context context;
    private View mView;
    String tmpurl;
    public String name;
    Boolean wantPrice;
    private String code;
    public Boolean exists = true;
    public String price = "Null";
    public String cash = "Null";
    public String credit = "Null";
    public String pictureURL;

    public ScannerGrabber(ScannerHandler handler, String tmpurl,String code,boolean wantPrice,Context context,View mView)
    {
        this.code = code;
        this.mView = mView;
        this.context = context;
        this.handler = handler;
        this.tmpurl = tmpurl;
        this.wantPrice = wantPrice;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try {
            if(!tmpurl.contains(code))
                throw new noProductException();
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

                if (strTemp.contains("pr-detail-img")) {
                    strTemp = br.readLine();
                    name = getName(strTemp);
                    pictureURL = getPictureURL(strTemp);
                    if (!wantPrice)
                        break;
                }

                if (strTemp.contains("id=\"wesellFor")) {
                    int start = strTemp.indexOf(">") + 1;
                    int end = strTemp.indexOf("<",start);
                    price = strTemp.substring(start,end);
                }

                if (strTemp.contains("id=\"webuyFor")) {
                    strTemp = br.readLine();
                    int end = strTemp.indexOf("<");
                    cash = strTemp.substring(0,end);
                }

                if (strTemp.contains("id=\"webuyVoucher")) {
                    strTemp = br.readLine();
                    int end = strTemp.indexOf("<");
                    credit = strTemp.substring(0,end);
                }
            }
            br.close();
        }
        catch (noProductException e)
        {
            Toast.makeText(context, "This barcode does not exist, Try searching manually", Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... params) {

    }

    @Override
    protected void onPostExecute(Void result) {
        if (exists) {

            price = price.substring(price.indexOf(";") + 1, price.indexOf(".") + 3);
            cash = cash.substring(cash.indexOf(";") + 1, cash.indexOf(".") + 3);
            credit = credit.substring(credit.indexOf(";") + 1, credit.indexOf(".") + 3);
            try {
                handler.open();
                handler.insertData(tmpurl, name, pictureURL, price, cash, credit);
                handler.close();
            } catch (Exception e) {

            }

        }

        else {
            Toast.makeText(context, "This barcode does not exist, Try searching manually", Toast.LENGTH_LONG).show();
        }

    }

    private String getName(String input)
    {
        String output = null;
        int start = input.indexOf("=") + 2;
        output = input.substring(start, input.length());
        start = output.indexOf("=") + 2;
        output = output.substring(start, output.length());
        int end = output.indexOf("\"");
        output = output.substring(0, end);
        return output;
    }

    private String getPictureURL(String input)
    {
        String output = null;
        int start = input.indexOf("=") + 2;
        int end = input.indexOf("\"",start);
        output = input.substring(start,end);
        return output;
    }

    private class noProductException extends Exception
    {
        public noProductException(){
            //null
        }
    }



}
