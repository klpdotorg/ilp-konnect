package in.org.klp.ilpkonnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import in.org.klp.ilpkonnect.utils.AppStatus;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context,  Intent intent) {

        if (AppStatus.isConnected(context)) {
              //  Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();

            Intent intent1 = new Intent(context, SyncIntentService.class);
            context.startService(intent1);
        } else {
            //  Toast.makeText(context, "disconnected", Toast.LENGTH_LONG).show();
        }


    }
}