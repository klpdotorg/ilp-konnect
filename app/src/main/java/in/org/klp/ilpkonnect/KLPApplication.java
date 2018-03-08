package in.org.klp.ilpkonnect;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;



import java.util.Locale;

import in.org.klp.ilpkonnect.db.KontactDatabase;


/**
 * Created by bibhas on 7/5/16.
 */
public class KLPApplication extends Application {
    KontactDatabase db;
    @Override
    public void onCreate() {
        super.onCreate();


       this. registerReceiver(new NetworkChangeReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        initSingletons();
        updateLanguage(this);

    }

    private void initSingletons() {
        db = new KontactDatabase(this);
    }

    public KontactDatabase getDb() {
        return db;
    }




    public static Context setLanguage(Context ctx,String language)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("user_locale", language);
        editor.commit();
       return updateLanguage(ctx, language);
    }



    public static Context updateLanguage(Context ctx)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString("user_locale", "En");
       return updateLanguage(ctx, lang);
    }

    public static Context updateLanguage(Context ctx, String lang)
    {
     /*   Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);*/

        Context context=ctx;

        Resources rs = context.getResources();
        Configuration config = rs.getConfiguration();

    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }*/
        if (!TextUtils.isEmpty(lang)){
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context = context.createConfigurationContext(config);
            } else {
                  ctx.getResources().updateConfiguration(config,  rs.getDisplayMetrics());
            }
        }

        return  context;


    }





    @Override
    protected void attachBaseContext(Context newBase) {

        Context context = updateLanguage(newBase);
       super.attachBaseContext(context);

    }







}
