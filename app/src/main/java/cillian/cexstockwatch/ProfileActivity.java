package cillian.cexstockwatch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ImageView barcodeImage;
    TextView barcodeText;
    Spinner location;
    String locationURL;
    String email;
    String barcode;
    String[] listOfItems;
    String[] listOfURLs;
    String[] listOfPics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        barcodeImage = (ImageView) (findViewById(R.id.barCode));
        createBarcode();
        location = (Spinner)(findViewById(R.id.locationSpinner));
        createSpinner();
        barcodeText = (TextView) (findViewById(R.id.barcodeData));


        if (!barcode.equals("Skipped")) {
            BarcodeCreator bc = new BarcodeCreator(barcode, barcodeImage);
            bc.createBarcode();
            barcodeText.setText(barcode + System.getProperty("line.separator"));
        } else {
            barcodeImage.setImageResource(R.drawable.card);
            barcodeText.setText("No Barcode Data" + System.getProperty("line.separator"));
        }


        ListView mainListView = (ListView) findViewById(R.id.itemList);

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
                    Intent intent = new Intent(ProfileActivity.this, StockCheck.class);
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
                    Intent intent = new Intent(ProfileActivity.this, ViewItemPopup.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("NAME", name);
                    startActivity(intent);
                    return true;
                }
            });

            linkArrayAdaptor listAdapter = new linkArrayAdaptor(this, listOfItems);
            mainListView.setAdapter(listAdapter);
        }
        else
        {
            TextView noItems = (TextView) findViewById(R.id.noItems);
            mainListView.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
        }

        barcodeImage.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {

                Intent intent = new Intent(ProfileActivity.this, ViewBarcodePopup.class);
                intent.putExtra("BAR", barcode);
                startActivity(intent);
                return true;
            }
        });

    }

    public boolean itemsSaved()
    {
        DatabaseHandler handler = new DatabaseHandler(getBaseContext());
        handler.open();
        int amount = handler.returnAmount();
        handler.close();
        return(amount > 0);
    }
    void createList()
    {
        DatabaseHandler handler = new DatabaseHandler(getBaseContext());
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
        UserHandler handler = new UserHandler(getBaseContext());
        handler.open();
        Cursor c1 = handler.returnData();
        if (c1.moveToFirst()) {
            do {
                locationURL = c1.getString(0);
                email = c1.getString(1);
                barcode = c1.getString(3);
            }
            while (c1.moveToNext());
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

        for(int i = 0; i < locations.length; i++)
        {
            if(!locations[i].equals(locationURL))
                locationlist.add(locations[i]);
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, locationlist);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        location.setAdapter(dataAdapter);

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = location.getSelectedItem().toString();

                if(!selected.equals(locationURL))
                {
                    UserHandler handler = new UserHandler(getBaseContext());
                    handler.open();
                    handler.updateURL(locationURL, selected);
                    handler.close();
                    Toast.makeText(getBaseContext(), "You may need a new account for this region", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                    intent.putExtra("LOC","Yup");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Nothing necessary here
            }

        });

    }

}
