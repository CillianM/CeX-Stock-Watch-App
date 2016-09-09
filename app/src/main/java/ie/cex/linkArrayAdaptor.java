package ie.cex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ie.cex.R;

public class linkArrayAdaptor extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public linkArrayAdaptor(Context context, String[] values) {
        super(context, R.layout.row_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.rowTextView);
        textView.setText(values[position]);

        // Change icon based on name
        String s = values[position];
        s = s.toLowerCase();


        return rowView;
    }
}
