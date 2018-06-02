package group9.tcss450.uw.edu.chatappgroup9;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * @author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class WeatherService extends IntentService {
    public static final String TAG = "WeatherService";
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "group9.tcss450.uw.edu.chatappgroup9.action.FOO";
    public static final String ACTION_BAZ = "group9.tcss450.uw.edu.chatappgroup9.action.BAZ";

    public static final String EXTRA_PARAM1 = "group9.tcss450.uw.edu.chatappgroup9.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "group9.tcss450.uw.edu.chatappgroup9.extra.PARAM2";

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Performing the /qservice/q");
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
