package cillian.cexstockwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddItemPopup extends Activity {

    String name;
    String url;
    EditText newSubject;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_item_popup);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .6), (int) (height * .4));

        newSubject = (EditText)findViewById(R.id.potentialItem);

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
    }

    public void addSubject(View v)
    {
        name = newSubject.getText().toString();
        if(name.length() < 2)
        {
            Toast.makeText(getBaseContext(), "Enter a name with more than 2 letters!", Toast.LENGTH_LONG).show();
        }

        else
        {
        DatabaseHandler handler = new DatabaseHandler(getBaseContext());
        handler.open();
        handler.insertData(name, url);
        int tmp = handler.returnAmount();
        handler.close();
        Toast.makeText(getBaseContext(), "Item Added To Watchlist!", Toast.LENGTH_LONG).show();
        finish();
        }
    }

    public void done(View v)
    {
        finish();
    }
}

