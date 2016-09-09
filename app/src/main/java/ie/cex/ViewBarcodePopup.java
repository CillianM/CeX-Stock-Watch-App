package ie.cex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ie.cex.DatabaseHandler.UserHandler;

import ie.cex.R;

public class ViewBarcodePopup extends Activity {

    String barcode;
    EditText currentName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_barcode_popup);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .6), (int) (height * .25));

        currentName = (EditText)findViewById(R.id.potentialItem);

        Intent intent = getIntent();
        barcode = intent.getStringExtra("BAR");
    }

    public void done(View v)
    {
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void updateName(View v)
    {
        String newBarcode = currentName.getText().toString();
        if(newBarcode.length() < 2)
        {
            Toast.makeText(getBaseContext(), "Enter a code with more than 2 letters!", Toast.LENGTH_LONG).show();
        }

        else
        {
            UserHandler handler = new UserHandler(getBaseContext());
            handler.open();
            handler.updateBarcode(barcode, newBarcode);
            handler.close();
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void scan(View v)
    {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                currentName.setText(contents);

            } else if (resultCode == RESULT_CANCELED) { // Handle cancel
                Log.i("xZing", "Cancelled");
            }
        }
    }

}

