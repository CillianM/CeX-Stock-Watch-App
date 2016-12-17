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

                    if (strTemp.contains("pr-detail-img")) {
                        strTemp = br.readLine();
                        name = getName(strTemp);
                        pictureURL = getPictureURL(strTemp);
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


}
