package in.org.klp.ilpkonnect;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.utils.AppStatus;
import okhttp3.OkHttpClient;

/**
 * Created by shridhars on 1/3/2018.
 */

public class SchoolIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private KontactDatabase db;
    private OkHttpClient okclient;

    public SchoolIntentService(String name) {
        super(name);
    }

    public SchoolIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        db = ((KLPApplication) getApplicationContext()).getDb();
        if(AppStatus.isConnected(getApplicationContext())) {


               downloadSchoolData(1);


        }


    }

    private void downloadSchoolData(long id) {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service", "service destroyed");
    }






}
