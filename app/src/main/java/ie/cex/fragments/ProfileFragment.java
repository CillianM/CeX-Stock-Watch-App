package ie.cex.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ie.cex.R;
import ie.cex.StockCheck;
import ie.cex.ViewBarcodePopup;
import ie.cex.ViewItemPopup;
import ie.cex.handlers.DatabaseHandler;
import ie.cex.handlers.UserHandler;
import ie.cex.utils.BarcodeCreator;

/**
 * Created by Cillian on 17/07/2017.
 */

public class ProfileFragment extends Fragment {

    ImageView barcodeImage;
    TextView barcodeText;
    Spinner location;
    String locationURL;
    String email;
    String barcode;
    String[] listOfItems;
    String[] listOfURLs;
    String[] listOfPics;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        barcodeImage = (ImageView) (view.findViewById(R.id.barCode));
        createBarcode();
        location = (Spinner) (view.findViewById(R.id.locationSpinner));
        createSpinner();
        barcodeText = (TextView) (view.findViewById(R.id.barcodeData));


        if (!barcode.equals("Skipped")) {
            BarcodeCreator bc = new BarcodeCreator(barcode, barcodeImage);
            bc.createBarcode();
            barcodeText.setText(barcode + System.getProperty("line.separator"));
        } else {
            barcodeImage.setImageResource(R.drawable.card);
            barcodeText.setText("No Barcode Data" + System.getProperty("line.separator"));
        }


        ListView mainListView = (ListView) view.findViewById(R.id.itemList);

        if (itemsSaved()) {
            createList();

            //create listview

            //create listener for each link in list
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //on item click create a url and open it in the browser
                public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                    String url = listOfURLs[position];
                    String name = listOfItems[position];
                    String picURL = listOfPics[position];
                    Intent intent = new Intent(getActivity(), StockCheck.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("NAME", name);
                    intent.putExtra("PIC", picURL);
                    startActivity(intent);
                }
            });

            mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                //on item click create a url and open it in the browser
                public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                    String url = listOfURLs[position];
                    String name = listOfItems[position];
                    Intent intent = new Intent(getActivity(), ViewItemPopup.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("NAME", name);
                    startActivity(intent);
                    return true;
                }
            });

            ArrayAdapter<String> ad = new ArrayAdapter<>(getActivity(), R.layout.textview, listOfItems);
            mainListView.setAdapter(ad);
        }
        else
        {
            TextView noItems = (TextView) view.findViewById(R.id.noItems);
            mainListView.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
        }

        barcodeImage.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {

                Intent intent = new Intent(getActivity(), ViewBarcodePopup.class);
                intent.putExtra("BAR", barcode);
                startActivity(intent);
                return true;
            }
        });


    }
    public boolean itemsSaved()
    {
        DatabaseHandler handler = new DatabaseHandler(getActivity().getBaseContext());
        handler.open();
        int amount = handler.returnAmount();
        handler.close();
        return(amount > 0);
    }
    void createList()
    {
        DatabaseHandler handler = new DatabaseHandler(getActivity().getBaseContext());
        handler.open();
        Cursor c1 = handler.returnData();
        int length = handler.returnAmount();
        listOfItems = new String[length];
        listOfPics = new String[length];
        listOfURLs = new String[length];
        int index = 0;
        if (c1.moveToFirst()) {
            do {
                listOfItems[index] = c1.getString(0);
                listOfPics[index] = c1.getString(2);
                listOfURLs[index] = c1.getString(1);
                index++;
            }
            while (c1.moveToNext());
        }

        handler.close();
    }

    void createBarcode()
    {
        UserHandler handler = new UserHandler(getActivity().getBaseContext());
        handler.open();
        if (handler.returnAmount() > 0) {
            Cursor c1 = handler.returnData();
            if (c1.moveToFirst()) {
                do {
                    locationURL = c1.getString(0);
                    email = c1.getString(1);
                    barcode = c1.getString(3);
                }
                while (c1.moveToNext());
            }
        } else {
            locationURL = "";
            email = "";
            barcode = "Skipped";
        }

        handler.close();

    }

    void createSpinner()
    {
        ArrayList<String> locationlist = new ArrayList<>();
        String [] locations = {
                "ie.m.webuy.com",
                "uk.m.webuy.com",
                "us.m.webuy.com",
                "es.m.webuy.com",
                "in.m.webuy.com",
                "pt.m.webuy.com",
                "nl.m.webuy.com",
                "mx.m.webuy.com",
                "pl.m.webuy.com",
                "au.m.webuy.com",
                "it.m.webuy.com"
        };

        locationlist.add(locationURL);

        for (String location1 : locations) {
            if (!location1.equals(locationURL))
                locationlist.add(location1);
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, locationlist);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        location.setAdapter(dataAdapter);
    }
}
