package in.org.klp.ilpkonnect.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import in.org.klp.ilpkonnect.SplashScreen;
import in.org.klp.ilpkonnect.VerifyMobileNumber;

/**
 * Created by bibhas on 6/16/16.
 */
public class SessionManager {
    private static final String STATEPOSITION = "STATEPOSITION";
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
    //first Name
    public static final String KEY_NAME = "userName";

    // Email address (make variable public to access from outside)
    public static final String KEY_ID = "userId";
    // Token
    public static final String KEY_TOKEN = "token";
    public static final String USER_TYPE = "userType";
    public static final String STATE = "STATE";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String LANGUAGE_KEY = "LANGUAGE_KEY";
    public static final String STATE_KEY = "STATE_KEY";
    public static final String BOUNDARYPOSITION = "BOUNDARYPOSITION";

    public static final String DOB = "DOB";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String EMAIL = "EMAIL";
    public static final String MOBILE = "MOBILE";

    public static final String SETUP = "SETUP";
    public static final String STATESELECTION = "STATESELECTION";

    public static final String LANGUAGEPOSITION = "LANGUAGEPOSITION";

    // for password saving
    public static final String PASSWORD = "";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String id, String token, String lastName, String email, String mobile, String dob, String usertype) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGGED_IN, true);
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_ID, id);
        // Storing token in pref
        editor.putString(KEY_TOKEN, token);
        editor.putString(USER_TYPE, usertype);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);
        editor.putString(MOBILE, mobile);
        editor.putString(DOB, dob);
        editor.putBoolean(SETUP, false);

        // commit changes
        editor.commit();
    }


    public void updateSetup(boolean flag) {
        editor.putBoolean(SETUP, flag);

        // commit changes
        editor.commit();
    }


    public void setStateSelection(String stateKey) {
        editor.putString(STATESELECTION, stateKey);

        // commit changes
        editor.commit();
    }

    public String getStateSelection() {

        return pref.getString(STATESELECTION, "ka");
    }


    public boolean isSetupDone() {

        return pref.getBoolean(SETUP, false);
    }


    public void updateSession(String firstName, String lastName, String dob, String email, String usertype) {
        editor.putString(KEY_NAME, firstName);

        editor.putString(USER_TYPE, usertype);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);
        editor.putString(DOB, dob);
        // commit changes
        editor.commit();

    }

    public String getUserType() {
        return pref.getString(USER_TYPE, "PR");
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, VerifyMobileNumber.class);
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
     */
    public HashMap<String, String> getUserDetails() {
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
     */

    public void setLanguage(String state, String language, String langkey, String stateKey) {
        editor.putString(STATE, state);
        editor.putString(STATE_KEY, stateKey);
        editor.putString(LANGUAGE, language);
        editor.putString(LANGUAGE_KEY, langkey);
        editor.commit();
    }


    public String getFirstName() {
        return pref.getString(KEY_NAME, null);

    }

    public int getLanguagePosition() {
        return pref.getInt(LANGUAGEPOSITION, 0);

    }

    public String getLastName() {
        return pref.getString(LAST_NAME, null);
    }

    public String getDOB() {
        return pref.getString(DOB, null);
    }

    public String getEmail() {
        return pref.getString(EMAIL, "");
    }

    public String getMobile() {
        return pref.getString(MOBILE, null);
    }


    public String getStateKey() {
        return pref.getString(STATE_KEY, "");
    }

    public String getState() {
        return pref.getString(STATE, "no");
    }


    public String getLanguage() {
        return pref.getString(LANGUAGE, "no");
    }


    public String getLanguageKey() {
        return pref.getString(LANGUAGE_KEY, "no");
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, SplashScreen.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }


    public String getToken() {
        //return "Token " + pref.getString(KEY_TOKEN, "");
        return "" + pref.getString(KEY_TOKEN, "");

    }

    public void setStatePosition(int statePosition) {
        editor.putInt(STATEPOSITION, statePosition);
        editor.commit();
    }

    public void setLanguagePosition(int langpos) {
        editor.putInt(LANGUAGEPOSITION, langpos);
        editor.commit();
    }


    public int getStatePosition() {

        return pref.getInt(STATEPOSITION, 0);
    }

    public String getPASSWORD() {
        return pref.getString(PASSWORD, "");
    }

    public void setPASSWORD(String pass) {
        editor.putString(PASSWORD, pass);
        editor.commit();
    }

    public void setKEY_TOKEN(String key) {
        editor.putString(KEY_TOKEN, key);
        editor.commit();
    }

   /*    public void setBoundaryPosition(int boundaryPosition) {
        editor.putInt(BOUNDARYPOSITION, boundaryPosition);
        editor.commit();
    }

    public int getBounaryPosition() {

        return pref.getInt(BOUNDARYPOSITION,0);
    }*/
}

