package in.org.klp.ilpkonnect;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shridhars on 2/12/2018.
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {
//temp

        super.attachBaseContext(KLPApplication.updateLanguage(newBase));

    }
}
