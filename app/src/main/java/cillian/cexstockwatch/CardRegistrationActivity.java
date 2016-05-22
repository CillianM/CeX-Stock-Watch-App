package cillian.cexstockwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

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
        try
        {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }

    }

    //alert dialog for downloadDialog
    public AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {

        AlertDialog.Builder downloadDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            downloadDialog = new AlertDialog.Builder(act, android.R.style.Theme_Material_Light_Dialog_Alert);
        }

        else
        {
            downloadDialog = new AlertDialog.Builder(act);
        }

        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try
                {
                    act.startActivity(intent);
                }

                catch (ActivityNotFoundException anfe)
                {
                    System.out.println(anfe);
                }
            }
        });

        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }
        });
        return downloadDialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {

                barcode = intent.getStringExtra("SCAN_RESULT");
                submit();
            }
        }

    }

}
