package in.org.klp.ilpkonnect;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Shridhar S.Metraskar on 12/29/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
        //Displaying token on logcat 
       Log.d(TAG, "Refreshed token: " + refreshedToken);


       // System.out.println(refreshedToken);


    }

    private void sendRegistrationToServer(String token) {


    }


}
