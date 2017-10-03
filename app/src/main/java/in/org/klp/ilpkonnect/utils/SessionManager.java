package in.org.klp.ilpkonnect.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import in.org.klp.ilpkonnect.LoginActivity;

/**
 * Created by bibhas on 6/16/16.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "in.org.klp.mobile.KLPPrefs";

    // All Shared Preferences Keys
    private static final String IS_LOGGED_IN = "isLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "userName";

    // Email address (make variable public to access from outside)
    public static final String KEY_ID = "userId";

    // Token
    public static final String KEY_TOKEN = "token";

    public static final String USER_TYPE = "userType";

    public static final String STATE = "STATE";
    public static final String LANGUAGE="LANGUAGE";
    public static final String LANGUAGE_KEY="LANGUAGE_KEY";
    public static final String STATE_KEY="STATE_KEY";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String id, String token,String usertype){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGGED_IN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_ID, id);

        // Storing token in pref
        editor.putString(KEY_TOKEN, token);

        editor.putString(USER_TYPE,usertype);

        // commit changes
        editor.commit();
    }




    public String getUserType()
    {
        return pref.getString(USER_TYPE,"PR");
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */

    public void setLanguage(String state, String language, String langkey, long stateKey)
    {
        editor.putString(STATE,state);
        editor.putLong(STATE_KEY,stateKey);
        editor.putString(LANGUAGE,language);
        editor.putString(LANGUAGE_KEY,langkey);
        editor.commit();
    }

    public long getStateKey()
    {
        return pref.getLong(STATE_KEY,0);
    }
    public String getState() {
        return pref.getString(STATE,"no");
    }


    public String getLanguage()
    {
        return pref.getString(LANGUAGE,"no");
    }


    public String getLanguageKey()
    {
        return pref.getString(LANGUAGE_KEY,"no");
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }


}
