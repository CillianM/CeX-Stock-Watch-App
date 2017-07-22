package ie.cex.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;

import ie.cex.R;
import ie.cex.connectivity.DetectConnection;
import ie.cex.connectivity.DownloadImage;
import ie.cex.connectivity.ScannerGrabber;
import ie.cex.handlers.ScannerHandler;
import ie.cex.handlers.UserHandler;

/**
 * Created by Cillian on 17/07/2017.
 */

public class ScanningFragment extends Fragment {

    int deleteCounter = 0;
    ScannerGrabber grabber = null;
    ArrayAdapter<String> ad;
    String[] listOfItems;
    String[] picURL;
    String[] listOfBuy;
    String[] listOfCash;
    String[] listOfCredit;
    View layoutView;
    TextureView textureView;
    private String locationUrl;


    public ScanningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_scanning, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        layoutView = view;
        textureView = (TextureView) layoutView.findViewById(R.id.texture_view);
        Button scan = (Button) layoutView.findViewById(R.id.ScanNew);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanNew();
            }
        });
        Button total = (Button) layoutView.findViewById(R.id.CalculateTotal);
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateTotals(layoutView);
            }
        });
        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) layoutView.findViewById(R.id.itemListRefresh);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (deleteCounter > 0) {
                    ScannerHandler handler = new ScannerHandler(getActivity());
                    handler.open();
                    handler.deleteList();
                    handler.createTable();
                    handler.close();
                    deleteCounter = 0;
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(getActivity(), "List Cleared", Toast.LENGTH_SHORT).show();
                    ad.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), "Swipe Again To Clear List", Toast.LENGTH_SHORT).show();
                    deleteCounter++;
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        UserHandler handler = new UserHandler(getActivity().getBaseContext());
        handler.open();
        if (handler.returnAmount() > 0) {
            Cursor c1 = handler.returnData();
            if (c1.moveToFirst()) {
                do {
                    locationUrl = c1.getString(0);
                }
                while (c1.moveToNext());
            }
        }
        handler.close();
        swipeContainer.setColorSchemeResources(R.color.red);
        refreshList();

    }

    public void refreshList() {
        ScannerHandler handler = new ScannerHandler(getActivity().getBaseContext());
        handler.open();
        try {
            handler.createTable();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        Cursor c1 = handler.returnData();
        int length = handler.returnAmount();
        listOfItems = new String[length];
        listOfBuy = new String[length];
        listOfCash = new String[length];
        listOfCredit = new String[length];
        picURL = new String[length];
        int index = 0;
        if (c1.moveToFirst()) {
            do {
                listOfItems[index] = c1.getString(1);
                picURL[index] = c1.getString(2);
                listOfBuy[index] = c1.getString(3);
                listOfCash[index] = c1.getString(4);
                listOfCredit[index] = c1.getString(5);
                index++;
            }
            while (c1.moveToNext());
        }

        handler.close();

        ListView mainListView = (ListView) layoutView.findViewById(R.id.scanList);

        //create listener for each link in list
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //on item click create a url and open it in the browser
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                String cash = listOfCash[position];
                String credit = listOfCredit[position];

                ImageView stockImage = (ImageView) layoutView.findViewById(R.id.itemImage);
                DownloadImage downloadImage = new DownloadImage(stockImage, locationUrl);
                downloadImage.execute(picURL[position]);

                TextView selectedCashValue = (TextView) layoutView.findViewById(R.id.SelectedCashValue);
                selectedCashValue.setText(cash);
                TextView selectedCreditValue = (TextView) layoutView.findViewById(R.id.SelectedCreditValue);
                selectedCreditValue.setText(credit);
                hideFinalPrices();
            }
        });

        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //on item click create a url and open it in the browser
            public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                return true;
            }
        });

        ad = new ArrayAdapter<>(getActivity(), R.layout.textview, listOfItems);
        mainListView.setAdapter(ad);
    }

    public void addItem(String url, String contents) {
        if (DetectConnection.checkInternetConnection(getActivity())) {
            grabber = new ScannerGrabber(new ScannerHandler(getActivity()), url, contents, getActivity());
            grabber.execute();
            try {
                grabber.get();
                getActivity().recreate();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void scanNew() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan(); // `this` is the current Fragment
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                addItem("https://" + locationUrl + "/product.php?sku=" + result.getContents(), result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void calculateTotals(View view) {
        double totalCash = 0;
        double totalCredit = 0;
        for (int i = 0; i < listOfItems.length; i++) {
            String tmp = listOfCash[i];
            tmp = tmp.substring(tmp.indexOf('€') + 1);
            double current = Double.parseDouble(tmp);
            totalCash = totalCash + current;

            tmp = listOfCredit[i];
            tmp = tmp.substring(tmp.indexOf('€') + 1);
            current = Double.parseDouble(tmp);
            totalCredit = totalCredit + current;
        }

        DecimalFormat df = new DecimalFormat("#.##");

        TextView cashValue = (TextView) view.findViewById(R.id.CashValue);
        cashValue.setText(df.format(totalCash));
        TextView creditValue = (TextView) layoutView.findViewById(R.id.CreditValue);
        creditValue.setText(df.format(totalCredit));
        showFinalPrices();


    }

    public void showFinalPrices() {
        layoutView.findViewById(R.id.ScanNew).setVisibility(View.GONE);
        layoutView.findViewById(R.id.CalculateTotal).setVisibility(View.GONE);
        layoutView.findViewById(R.id.CashValue).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.CreditValue).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.CashIcon).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.CreditIcon).setVisibility(View.VISIBLE);
    }

    public void hideFinalPrices() {
        layoutView.findViewById(R.id.ScanNew).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.CalculateTotal).setVisibility(View.VISIBLE);
        layoutView.findViewById(R.id.CashValue).setVisibility(View.GONE);
        layoutView.findViewById(R.id.CreditValue).setVisibility(View.GONE);
        layoutView.findViewById(R.id.CashIcon).setVisibility(View.GONE);
        layoutView.findViewById(R.id.CreditIcon).setVisibility(View.GONE);
    }
}
