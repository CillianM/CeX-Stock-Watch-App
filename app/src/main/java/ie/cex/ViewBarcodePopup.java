package ie.cex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import ie.cex.handlers.UserHandler;

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
            finish();
        }
    }

    public void scan(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan(); // `this` is the current Fragment
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        currentName.setText(result.getContents());
    }

}

