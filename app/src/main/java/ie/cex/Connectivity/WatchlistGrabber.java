package ie.cex.Connectivity;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ie.cex.DatabaseHandler.DatabaseHandler;
import ie.cex.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class WatchlistGrabber extends AsyncTask<Void,Void,Void>
{
    private View mView;
    String tmpurl;
    public String name;
    Boolean wantPrice;
    public Boolean exists = true;
    public Boolean inStock = false;
    public String price = "Null";
    public String cash = "Null";
    public String credit = "Null";
    public String pictureURL;

    public WatchlistGrabber(DatabaseHandler handler, String tmpurl,boolean wantPrice,View mView)
    {
        this.mView = mView;
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
            checkifInStock();
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

        Button button = (Button) mView.findViewById(R.id.investigate);
        button.setClickable(false);


        TextView buy_text = (TextView) mView.findViewById(R.id.buy);
        TextView cash_text = (TextView) mView.findViewById(R.id.cash);
        TextView credit_text = (TextView) mView.findViewById(R.id.credit);

        price = price.substring(price.indexOf(";") + 1, price.indexOf(".") + 3);
        buy_text.setText(price);
        cash_text.setText(cash.substring(cash.indexOf(";") + 1, cash.indexOf(".") + 3));
        credit_text.setText(credit.substring(credit.indexOf(";") + 1, credit.indexOf(".") + 3));

        if (inStock) {
            button.setText("In Stock!");
        }

        button.setClickable(true);

        mView.findViewById(R.id.buy).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.cash).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.credit).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.buyHeader).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.cashHead).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.creditHead).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.itemName).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.investigate).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.stockImage).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.progressBar2).setVisibility(View.GONE);

    }

    public void checkifInStock()
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



}
