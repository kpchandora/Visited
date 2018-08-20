package developer.code.kpchandora.locationassignment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import developer.code.kpchandora.locationassignment.service.LocationService;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {

            String action = intent.getStringExtra("action");

            if (action.equals(LocationService.STOP_ACTION_MESSAGE)) {
                context.stopService(new Intent(context, LocationService.class));
            }

        }

    }
}
