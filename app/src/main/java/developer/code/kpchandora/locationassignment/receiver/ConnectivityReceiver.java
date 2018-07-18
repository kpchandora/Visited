package developer.code.kpchandora.locationassignment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import developer.code.kpchandora.locationassignment.MainActivity;
import developer.code.kpchandora.locationassignment.service.LocationService;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectivityReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive:ConnectivityReceiver " + isConnected(context));
        context.sendBroadcast(new Intent(LocationService.CONNECTION_BROADCAST)
                .putExtra("isOnline", isConnected(context)));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
