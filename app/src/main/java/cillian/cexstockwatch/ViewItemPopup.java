package cillian.cexstockwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

public class ViewItemPopup extends Activity {

    String name;
    String url;
    EditText currentName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_item_popup);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .6), (int) (height * .4));

        currentName = (EditText)findViewById(R.id.potentialItem);

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        name = intent.getStringExtra("NAME");
    }

    public void done(View v)
    {
        finish();
    }

    public void updateName(View v)
    {
        String newName = currentName.getText().toString();
        DatabaseHandler handler = new DatabaseHandler(getBaseContext());
        handler.open();
        handler.updateName(name, newName);
        handler.close();
        finish();
    }

    public void delete(View v)
    {
        DatabaseHandler handler = new DatabaseHandler(getBaseContext());
        handler.open();
        handler.removeName(name);
        handler.close();
        finish();
    }
}

