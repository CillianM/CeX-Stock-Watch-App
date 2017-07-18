package ie.cex.connectivity;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ie.cex.handlers.DatabaseHandler;

public class ProductGrabber extends AsyncTask<Void,Void,Void>
{
    private DatabaseHandler handler;
    private String tmpurl;
    private String name;
    private String pictureURL;

    public ProductGrabber(DatabaseHandler handler, String tmpurl)
    {
        this.handler = handler;
        this.tmpurl = tmpurl;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try {
            Document doc = Jsoup.connect(tmpurl).get();
            Elements imageDiv = doc.getElementsByClass("productImg");
            for (Element element : imageDiv)
            {
                Elements img = element.getElementsByClass("center");
                pictureURL = img.attr("src");
            }
            Elements titleDiv = doc.getElementsByClass("productNamecustm");
            for (Element element : titleDiv) {
                name = element.text();
            }
        }

        catch (Exception e)
        {
            Log.e("Error", e.getMessage());
        }

       return null;
    }

    @Override
    protected void onProgressUpdate(Void... params) {
        //Not needed
    }

    @Override
    protected void onPostExecute(Void result) {

        handler.open();
        handler.insertData(name, tmpurl, pictureURL);
        handler.close();

    }
}
