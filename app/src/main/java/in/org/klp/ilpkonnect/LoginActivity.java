package in.org.klp.ilpkonnect;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.db.DatabaseCopyHelper;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private SessionManager mSession;
    private ProgressDialog progressDialog = null;
    KontactDatabase db;
    int flag = 0;
    final static int PERMISSION_REQUEST_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Calling getReadableDatabase() uses SQLiteAssetHelper
        // to copy the prepopulated database to theusername_sign_up_button device.
        // Read comments at DatabaseCopyHelper class.
        mSession = new SessionManager(getApplicationContext());
        DatabaseCopyHelper dbCopyHelper = new DatabaseCopyHelper(this);
        SQLiteDatabase dbCopy = dbCopyHelper.getReadableDatabase();
        db = ((KLPApplication) getApplicationContext()).getDb();
      //  isStoragePermissionGranted(LoginActivity.this, null);
        this.setTitle(getResources().getString(R.string.app_name));
        Button username_sign_up_button = findViewById(R.id.username_sign_up_button);


        if (mSession.isLoggedIn() && mSession.isSetupDone()) {
            //  Toast.makeText(getApplicationContext(),"isLogin",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, SurveyTypeActivity.class);
            startActivity(intent);
            finish();
        } else if (mSession.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, TempLoading.class);
            startActivity(intent);
            finish();
        }


        // Set up the login form.
        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);


        username_sign_up_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


          /*  showProgress(true);
                new ProNetworkSettup(LoginActivity.this).getRespondentList(ILPService.RESPONDENTLIST, new UserRolesInterface() {
                    @Override
                    public void success(String message) {
                        showProgress(false);*/
                Intent i = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                  /*  }

                    @Override
                    public void failed(String message) {
                        showProgress(false);
                        showSignupResultDialog(getResources().getString(R.string.app_name), message, "OK");
                    }

                });
*/


            }
        });


        Button mEmailSignInButton = findViewById(R.id.username_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });


        TextView forgotPasswordLink = findViewById(R.id.forgotPassword);
        forgotPasswordLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Log.v(LoginActivity.class.getSimpleName(), "forgot password clicked");
                Intent intent = new Intent(v.getContext(), ForgotPasswordOTP.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                //finish();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    //For fragments
    public static boolean isStoragePermissionGranted(Activity activity, Fragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (fragment == null) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                } else {
                    fragment.requestPermissions(
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                }
                return false;
            }
        } else {
            return true;
        }
    }

    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mUsernameView.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address or phone number.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else {

            if (!isEmailOrPhoneValid(email)) {
                //mUsernameView.setError(getResources().getString(R.string.enter_valid_email_or_Phone));
                focusView = mUsernameView;
                cancel = true;
            }


        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            new ProNetworkSettup(LoginActivity.this).userLogin(email, password, mSession.getStateSelection(), new StateInterface() {
                @Override
                public void success(String message) {

                    try {
                        JSONObject userLoginInfo = new JSONObject(message);
                        if (userLoginInfo.has("token")) {
                            // create session

                            if (userLoginInfo.getString("user_type") != null &&
                                    !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("null")
                                    && !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("")) {

                                finishLogin(message, mSession.getStateSelection(), "Token " + userLoginInfo.getString("token"));


                            } else {
                                //update profile
                                showProgress(false);
                                Intent intent = new Intent(getApplicationContext(), UpdateProfileBeforeLoginActivity.class);
                                intent.putExtra("firstName", userLoginInfo.getString("first_name"));
                                intent.putExtra("lastName", userLoginInfo.getString("last_name"));
                                intent.putExtra("mobile", userLoginInfo.getString("mobile_no"));
                                intent.putExtra("email", userLoginInfo.getString("email"));
                                intent.putExtra("token", userLoginInfo.getString("token"));
                                startActivity(intent);


                            }
                        } else
                            showProgress(false);

                    } catch (Exception e) {
                        showProgress(false);
                    }

                    // finishLogin(message, mSession.getStateSelection());

                }

                @Override
                public void failed(String message) {
                    showProgress(false);

                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                }
            });


        }
    }

    private boolean isEmailOrPhoneValid(String email) {

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {   //its an email id
            //  Toast.makeText(getApplicationContext(),"Syntactically valid email",Toast.LENGTH_SHORT).show();
            return true;
        } else {
            if (TextUtils.isDigitsOnly(email)) {
                if (email.length() == 10) {
                    //Toast.makeText(getApplicationContext(), "its Digit", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    mUsernameView.setError(getResources().getString(R.string.enter_ten_digit_number));
                    return false;
                }
            } else {
                // Generic Message
                // Toast.makeText(getApplicationContext(),"Please provide valid Email or phone number",Toast.LENGTH_SHORT).show();
                mUsernameView.setError(getResources().getString(R.string.enter_valid_email_or_Phone));
                return false;
                //Please provide valid Email or phone number
            }
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(getResources().getString(R.string.authenticating));
            progressDialog.show();
        } else {
            try {


                if (progressDialog != null) {
                    if (!LoginActivity.this.isFinishing()) {
                        try {


                            progressDialog.cancel();
                        } catch (Exception e) {
                            progressDialog.dismiss();
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void finishLogin(final String userInfo, String stateKey, final String token) {
        //parse the userInfo String


        String URL = BuildConfig.HOST + "/api/v1/surveys/?survey_tag=konnect&state=" + stateKey + "&status=AC&per_page=0";

        new ProNetworkSettup(LoginActivity.this).getSurveyandQuestionGroup(URL, stateKey, token, new StateInterface() {
            @Override
            public void success(String message) {


                flag = 0;
                final ArrayList<Survey> pojoList = getS_Qids();
                if (pojoList != null && pojoList.size() > 0) {

                    for (int i = 0; i < pojoList.size(); i++) {
                        flag = i;

                        String url = BuildConfig.HOST + "/api/v1/surveys/" + pojoList.get(i).getId() + "/questiongroup/" + pojoList.get(i).getQuestionGroupId() + "/questions/?state=" + mSession.getStateSelection() + "&per_page=0";
                        new ProNetworkSettup(LoginActivity.this).getCommunitySurveyQuestions(url, pojoList.get(i).getQuestionGroupId(), flag, pojoList.size(), token, new StateInterface() {
                            @Override
                            public void success(String message) {


                                try {
                                    JSONObject userLoginInfo = new JSONObject(userInfo);
                                    if (userLoginInfo.has("token")) {
                                        // create session
                                        String users = "PR";
                                        if (userLoginInfo.getString("user_type") != null && !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("null")) {
                                            users = userLoginInfo.getString("user_type").toUpperCase();
                                        }

                                       /* mSession.createLoginSession(
                                                userLoginInfo.getString("first_name"),
                                                userLoginInfo.getString("id"),
                                                userLoginInfo.getString("token"),
                                                userLoginInfo.getString("last_name"),
                                                userLoginInfo.getString("email"),
                                                userLoginInfo.getString("mobile_no"),
                                                userLoginInfo.getString("dob"),
                                                users);*/

                                        showProgress(false);

                                        try {
                                            subscribetoTopicsForNotification(mSession.getState(), mSession.getUserType());
                                        } catch (Exception e) {

                                        }
                                        Intent intent = new Intent(LoginActivity.this, TempLoading.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                        finish();


                                    } else {
                                        showProgress(false);
                                        //Show login dialog again. Clear out fields. Show a message. Ask to login again
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showProgress(false);
                                }


                            }

                            @Override
                            public void failed(String message) {
                                showProgress(false);
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),
                                        message,
                                        getResources().getString(R.string.Ok));
                            }
                        });

                    }


                } else {
                    showProgress(false);
                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.surveyLoadingfailed),
                            getResources().getString(R.string.Ok));
                }


            }

            @Override
            public void failed(String message) {
                showProgress(false);
                showSignupResultDialog(
                        getResources().getString(R.string.app_name),
                        message,
                        getResources().getString(R.string.Ok));
            }
        });

    }

    private void subscribetoTopicsForNotification(String state, String stateUserType) {

        try {
            FirebaseMessaging.getInstance().subscribeToTopic(state);
            FirebaseMessaging.getInstance().subscribeToTopic(state + "-" + RolesUtils.getUserRoleValueForFcmGroup(getApplicationContext(), db, stateUserType));
            //   Toast.makeText(getApplicationContext(),state+"-"+state + ":" + RolesUtils.getUserRoleValueForFcmGroup(getApplicationContext(), db, stateUserType),Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //may be topic contains some special symbols
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void showSignupResultDialog(String title, String message, String buttonText) {
        try {
            Bundle signUpResult = new Bundle();
            signUpResult.putString("title", title);
            signUpResult.putString("result", message);
            signUpResult.putString("buttonText", buttonText);

            SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
            resultDialog.setArguments(signUpResult);
            resultDialog.setCancelable(false);
            resultDialog.show(getSupportFragmentManager(), "Registration result");
        } catch (Exception e) {

        }
    }


    public ArrayList<Survey> getS_Qids() {

        Query listQGIdsQquery = Query.select().from(Survey.TABLE);
        ArrayList<Survey> pojoList = new ArrayList<>();
        SquidCursor<Survey> surveyCursor = null;
        surveyCursor = db.query(Survey.class, listQGIdsQquery);
        if (surveyCursor != null) {
            while (surveyCursor.moveToNext()) {
                Survey survey = new Survey(surveyCursor);
                pojoList.add(survey);

            }


        }
        return pojoList;
    }
}

