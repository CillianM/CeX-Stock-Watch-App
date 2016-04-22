package cillian.cexstockwatch;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Watchlist extends AppCompatActivity {

    String[] listOfItems;
    String[] listOfURLs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        list();
        //create listview
        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        //create listener for each link in list
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //on item click create a url and open it in the browser
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                String url = listOfURLs[position];
                String name = listOfItems[position];
                Intent intent = new Intent(Watchlist.this, StockCheck.class);
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
                Intent intent = new Intent(Watchlist.this, ViewItemPopup.class);
                intent.putExtra("URL", url);
                intent.putExtra("NAME", name);
                startActivity(intent);
                return true;
            }
        });

        linkArrayAdaptor listAdapter = new linkArrayAdaptor(this, listOfItems);
        mainListView.setAdapter(listAdapter);

    }

    void list()
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
}


