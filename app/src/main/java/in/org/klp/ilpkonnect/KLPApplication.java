package in.org.klp.ilpkonnect;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;

import java.util.Locale;

import in.org.klp.ilpkonnect.db.KontactDatabase;
import io.fabric.sdk.android.Fabric;

/**
 * Created by bibhas on 7/5/16.
 */
public class KLPApplication extends Application {
    KontactDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        initSingletons();
        updateLanguage(this);
        Fabric.with(this, new Crashlytics());
       ;
    }

    private void initSingletons() {
        db = new KontactDatabase(this);
    }

    public KontactDatabase getDb() {
        return db;
    }


    public static void setLanguage(Context ctx,String language)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("user_locale", language);
        editor.commit();
        updateLanguage(ctx, language);
    }



    public static void updateLanguage(Context ctx)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString("user_locale", "En");
        updateLanguage(ctx, lang);
    }

    public static void updateLanguage(Context ctx, String lang)
    {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
    }
}
