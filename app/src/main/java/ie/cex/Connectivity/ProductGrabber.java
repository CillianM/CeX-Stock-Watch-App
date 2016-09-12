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
    public Boolean exists = true;
    public String pictureURL;

    public ProductGrabber(DatabaseHandler handler, String tmpurl)
    {
        this.handler = handler;
        this.tmpurl = tmpurl;
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
                        break;
                    }
            }
            br.close();


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

        handler.open();
        handler.insertData(name, tmpurl, pictureURL);
        handler.close();

    }



}
