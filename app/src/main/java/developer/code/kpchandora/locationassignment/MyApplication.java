package developer.code.kpchandora.locationassignment;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {

    private static Context context;
    private static final String TAG = "MyApplication";

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate: ");
        super.onTerminate();
    }
}
