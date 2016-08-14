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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    ImageView barcodeImage;
    TextView nameText;
    String username;
    String email;
    String barcode;
    String[] listOfItems;
    String[] listOfURLs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        barcodeImage = (ImageView) (findViewById(R.id.barCode));

        createBarcode();
        nameText = (TextView) (findViewById(R.id.userData));


        if (!barcode.equals("Skipped")) {
            BarcodeCreator bc = new BarcodeCreator(barcode, barcodeImage);
            bc.createBarcode();
            nameText.setText(barcode + System.getProperty("line.separator") + username + System.getProperty("line.separator"));
        } else {
            barcodeImage.setImageResource(R.drawable.card);
            nameText.setText(username + System.getProperty("line.separator"));
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
                    Intent intent = new Intent(ProfileActivity.this, StockCheck.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("NAME", name);
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
        listOfURLs = new String[length];
        int index = 0;
        if (c1.moveToFirst()) {
            do {
                listOfItems[index] = c1.getString(0);
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
                username = c1.getString(0);
                email = c1.getString(1);
                barcode = c1.getString(3);
            }
            while (c1.moveToNext());
        }

        handler.close();

    }

}
