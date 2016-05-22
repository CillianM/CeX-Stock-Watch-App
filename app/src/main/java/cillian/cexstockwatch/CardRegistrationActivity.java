package cillian.cexstockwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CardRegistrationActivity extends AppCompatActivity {

    String name;
    String email;
    String barcode;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_registration);
        Intent intent = getIntent();
        name = intent.getStringExtra("NAME");
        email = intent.getStringExtra("EMAIL");
        TextView brief = (TextView)(findViewById(R.id.Description));
        String description = getResources().getString(R.string.description);
        brief.setText(System.getProperty("line.separator") + "Hello " + name + "! "+ System.getProperty("line.separator") + description);
    }


    public void submit()
    {
        UserHandler handler = new UserHandler(getBaseContext());
        handler.open();
        handler.insertData(name, email, barcode);
        handler.close();
        Intent intent = new Intent(CardRegistrationActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(getBaseContext(), "Profile Created Succesfully!", Toast.LENGTH_LONG).show();
    }

    public void skip(View v)
    {
        barcode= "Skipped";
        submit();
    }



    public void addBarcode(View v)
    {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            barcode = scanningResult.getContents();
            submit();
        }

        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
