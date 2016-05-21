package cillian.cexstockwatch;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class SetupActivity extends AppCompatActivity {

    UserHandler handler;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    EditText barcodeField;
    Button skip;
    Button add;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        barcodeField = (EditText)(findViewById(R.id.barcodeField));
        skip = (Button)(findViewById(R.id.skipBarcode));
        add = (Button)(findViewById(R.id.addBarcode));
        submit = (Button)(findViewById(R.id.submitButton));

        handler = new UserHandler(getBaseContext());
        handler.open();
        int tmp = handler.returnAmount();
        if (handler.returnAmount() > 0)
        {
            Intent intent = new Intent(SetupActivity.this,MainActivity.class);
            handler.close();
            startActivity(intent);
        }
    }

    public void skipBarcode(View v)
    {
        barcodeField.setText("Skipped");
        skip.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
    }

    public void submitInfo(View v)
    {
        EditText nameField = (EditText)(findViewById(R.id.nameField));
        EditText emailField = (EditText)(findViewById(R.id.emailField));

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String barcode = barcodeField.getText().toString();

        if(name.length() > 0)
        {
            if(email.length() > 4 && email.contains("@"))
            {
                handler.insertData(name,email,barcode);
                Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                handler.close();
                startActivity(intent);
                Toast.makeText(getBaseContext(), "Profile Created Succesfully!", Toast.LENGTH_LONG).show();
            }

            else
            {
                Toast.makeText(getBaseContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
            }

        }

        else
        {
            Toast.makeText(getBaseContext(), "Please enter a valid name", Toast.LENGTH_LONG).show();
        }
    }

    public void scanBarcode(View v)
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

                String contents = intent.getStringExtra("SCAN_RESULT");
                barcodeField.setVisibility(View.VISIBLE);
                barcodeField.setText(contents);
                skip.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
            }
        }

    }

    public void manualBarcode(View v)
    {
        Button manaul = (Button)(findViewById(R.id.manualBarcode));
        barcodeField.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        manaul.setVisibility(View.GONE);
    }

}
