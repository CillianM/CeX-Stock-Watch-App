package ie.cex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ie.cex.handlers.DatabaseHandler;

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
        getWindow().setLayout((int) (width * .6), (int) (height * .3));

        currentName = (EditText)findViewById(R.id.potentialItem);

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        name = intent.getStringExtra("NAME");
    }


    public void updateName(View v)
    {
        String newName = currentName.getText().toString();
        if(newName.length() < 2)
        {
            Toast.makeText(getBaseContext(), "Enter a name with more than 2 letters!", Toast.LENGTH_LONG).show();
        }

        else
        {
            DatabaseHandler handler = new DatabaseHandler(getBaseContext());
            handler.open();
            handler.updateName(name, newName);
            handler.close();
            Intent intent = new Intent(this, ContainerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void delete(View v)
    {
        DatabaseHandler handler = new DatabaseHandler(getBaseContext());
        handler.open();
        handler.removeName(name);
        handler.close();
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

