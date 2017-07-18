package ie.cex.connectivity;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ie.cex.R;

public class WatchlistGrabber extends AsyncTask<Void,Void,Void>
{
    private View mView;
    private String tmpurl;
    private Boolean inStock = true;
    private String price = "Null";
    private String cash = "Null";
    private String credit = "Null";

    public WatchlistGrabber(String tmpurl, View mView)
    {
        this.mView = mView;
        this.tmpurl = tmpurl;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try {
            Document doc = Jsoup.connect(tmpurl).get();
            Element sellprice = doc.getElementById("Asellprice");
            price = sellprice.text();
            Element cashprice = doc.getElementById("Acashprice");
            cash = cashprice.text();
            Element creditprice = doc.getElementById("Aexchprice");
            credit = creditprice.text();
            Elements stock = doc.getElementsByClass("buyNowButton");
            String stockText = stock.get(0).text();
            if (stockText.contains("Out Of Stock"))
                inStock = false;
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


        TextView buyText = (TextView) mView.findViewById(R.id.buy);
        TextView cashText = (TextView) mView.findViewById(R.id.cash);
        TextView creditText = (TextView) mView.findViewById(R.id.credit);
        TextView stockText = (TextView) mView.findViewById(R.id.stock);

        if (!inStock) {
            stockText.setText(R.string.not_in_stock);
        }

        price = price.substring(price.indexOf(';') + 1, price.indexOf('.') + 3);
        buyText.setText(price);
        cashText.setText(cash.substring(cash.indexOf(';') + 1, cash.indexOf('.') + 3));
        creditText.setText(credit.substring(credit.indexOf(';') + 1, credit.indexOf('.') + 3));

        mView.findViewById(R.id.buy).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.cash).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.credit).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.buyHeader).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.cashHead).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.creditHead).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.itemName).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.stockImage).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.stock).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.progressBar2).setVisibility(View.GONE);

    }
}
