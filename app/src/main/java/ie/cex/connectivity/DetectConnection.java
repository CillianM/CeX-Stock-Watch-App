package ie.cex.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;

public class DetectConnection {

    private DetectConnection() {
        super();
    }
    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager conManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return conManager.getActiveNetworkInfo() != null
                && conManager.getActiveNetworkInfo().isAvailable()
                && conManager.getActiveNetworkInfo().isConnected();
    }
}
