package ie.cex.connectivity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ie.cex.handlers.ScannerHandler;

public class ScannerGrabber extends AsyncTask<Void,Void,Void>
{
    private ScannerHandler handler;
    private Context context;
    private String tmpurl;
    private String name;
    private String code;
    private String price = "Null";
    private String cash = "Null";
    private String credit = "Null";
    private String pictureURL;

    public ScannerGrabber(ScannerHandler handler, String tmpurl, String code, Context context)
    {
        this.code = code;
        this.context = context;
        this.handler = handler;
        this.tmpurl = tmpurl;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try {
            if(!tmpurl.contains(code))
                throw new NoProductException();

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
            Element sellprice = doc.getElementById("Asellprice");
            price = sellprice.text();
            Element cashprice = doc.getElementById("Acashprice");
            cash = cashprice.text();
            Element creditprice = doc.getElementById("Aexchprice");
            credit = creditprice.text();
        } catch (NoProductException e)
        {
            Toast.makeText(context, "This barcode does not exist, Try searching manually", Toast.LENGTH_LONG).show();
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
        price = price.substring(price.indexOf(';') + 1, price.indexOf('.') + 3);
        cash = cash.substring(cash.indexOf(';') + 1, cash.indexOf('.') + 3);
        credit = credit.substring(credit.indexOf(';') + 1, credit.indexOf('.') + 3);
        try {
            handler.open();
            handler.insertData(tmpurl, name, pictureURL, price, cash, credit);
            handler.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

    }

    private class NoProductException extends Exception
    {
        NoProductException() {
            //null
        }
    }



}
