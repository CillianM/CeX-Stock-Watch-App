package cillian.cexstockwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ViewBarcodePopup extends Activity {

    String barcode;
    EditText currentName;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
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
        finish();
    }

    public void updateName(View v)
    {
        String newBarcode = currentName.getText().toString();
        if(newBarcode.length() < 2)
        {
            Toast.makeText(getBaseContext(), "Enter a name with more than 2 letters!", Toast.LENGTH_LONG).show();
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
                currentName.setText(contents);
            }
        }

    }
}

