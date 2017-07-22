package ie.cex.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ie.cex.R;
import ie.cex.StockCheck;
import ie.cex.handlers.DatabaseHandler;
import ie.cex.handlers.UserHandler;
import ie.cex.utils.BarcodeCreator;

/**
 * Created by Cillian on 17/07/2017.
 */

public class ProfileFragment extends Fragment {

    private String barcode;
    private String username;
    private String[] listOfItems;
    private String[] listOfURLs;
    private String[] listOfPics;
    private ImageView barcodeImage;
    private TextView barcodeText;
    private ArrayAdapter<String> ad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    public void setupBarcode(String barcodeString) {
        BarcodeCreator bc = new BarcodeCreator(barcodeString, barcodeImage);
        bc.createBarcode();
        barcodeText.setText(barcodeString + System.getProperty("line.separator"));
    }

    @Override
    public void onViewCreated(final View view, Bundle instanceState) {

        barcodeImage = (ImageView) (view.findViewById(R.id.barCode));
        createBarcode();
        barcodeText = (TextView) (view.findViewById(R.id.barcodeData));
        TextView nameText = (TextView) (view.findViewById(R.id.Title));
        nameText.setText(username);

        if (barcode.length() > 0) {
            setupBarcode(barcode);
        } else {
            barcodeImage.setImageResource(R.drawable.card);
            barcodeText.setText("No Barcode Data" + System.getProperty("line.separator"));
        }

        final ListView mainListView = (ListView) view.findViewById(R.id.itemList);

        if (itemsSaved()) {
            createList();
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //on item click create a url and open it in the browser
                public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                    String url = listOfURLs[position];
                    String name = listOfItems[position];
                    String picURL = listOfPics[position];
                    openStockCheck(url, name, picURL);
                }
            });

            mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                //on item click create a url and open it in the browser
                public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
                    final String name = listOfItems[position];
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                removeName(name);
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Are you sure you want to delete " + name + "?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return true;
                }
            });

            ad = new ArrayAdapter<>(getActivity(), R.layout.textview, listOfItems);
            mainListView.setAdapter(ad);
        }
        else
        {
            TextView noItems = (TextView) view.findViewById(R.id.noItems);
            mainListView.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
        }

        barcodeImage.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                showDialog();
                return true;
            }
        });
    }

    private void openStockCheck(String url, String name, String picURL) {
        Intent intent = new Intent(getActivity(), StockCheck.class);
        intent.putExtra("URL", url);
        intent.putExtra("NAME", name);
        intent.putExtra("PIC", picURL);
        startActivity(intent);
    }

    private void removeName(String username) {
        DatabaseHandler handler = new DatabaseHandler(getActivity().getBaseContext());
        handler.open();
        handler.removeName(username);
        handler.close();
        ad.notifyDataSetChanged();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.activity_view_barcode_popup);
        dialog.setTitle("Title...");

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        dialog.getWindow().setLayout((int) (width * .6), (int) (height * .4));

        final EditText newCode = (EditText) dialog.findViewById(R.id.potentialItem);
        Button scanButton = (Button) dialog.findViewById(R.id.scan);
        Button submitButton = (Button) dialog.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserHandler handler = new UserHandler(getActivity());
                handler.open();
                handler.updateBarcode(barcode, newCode.getText().toString());
                handler.close();
                dialog.dismiss();
                setupBarcode(barcode);
            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Not needed
            }
        });

        dialog.show();

    }
    public boolean itemsSaved()
    {
        DatabaseHandler handler = new DatabaseHandler(getActivity().getBaseContext());
        handler.open();
        int amount = handler.returnAmount();
        handler.close();
        return(amount > 0);
    }

    private void createList()
    {
        DatabaseHandler handler = new DatabaseHandler(getActivity().getBaseContext());
        handler.open();
        Cursor c1 = handler.returnData();
        int length = handler.returnAmount();
        listOfItems = new String[length];
        listOfPics = new String[length];
        listOfURLs = new String[length];
        int index = 0;
        if (c1.moveToFirst()) {
            do {
                listOfItems[index] = c1.getString(0);
                listOfPics[index] = c1.getString(2);
                listOfURLs[index] = c1.getString(1);
                index++;
            }
            while (c1.moveToNext());
        }

        handler.close();
    }

    private void createBarcode()
    {
        UserHandler handler = new UserHandler(getActivity().getBaseContext());
        handler.open();
        if (handler.returnAmount() > 0) {
            Cursor c1 = handler.returnData();
            if (c1.moveToFirst()) {
                do {
                    username = c1.getString(1);
                    barcode = c1.getString(2);
                }
                while (c1.moveToNext());
            }
        } else {
            barcode = "Skipped";
        }

        handler.close();

    }
}
