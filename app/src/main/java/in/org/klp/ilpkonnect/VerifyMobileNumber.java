package in.org.klp.ilpkonnect;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.InterfacesPack.LoginInterface;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.db.DatabaseCopyHelper;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;

/*
 Created by Srilatha on 06/08/2019
 */
public class VerifyMobileNumber extends BaseActivity {

    EditText txtMobileNumber;
    Button btnSubmit, btnregister;
    private SessionManager mSession;
    private ProgressDialog progressDialog = null;
    KontactDatabase db;
    int flag = 0;
    final static int PERMISSION_REQUEST_CODE = 12;
    Handler handler = new Handler();
    Runnable r;
    int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_mobile_number);

        txtMobileNumber = findViewById(R.id.txtMobileNumber);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnregister = findViewById(R.id.register_button);

        this.setTitle("Welcome to ILP Konnect!");

        mSession = new SessionManager(getApplicationContext());
        DatabaseCopyHelper dbCopyHelper = new DatabaseCopyHelper(this);
        SQLiteDatabase dbCopy = dbCopyHelper.getReadableDatabase();
        db = ((KLPApplication) getApplicationContext()).getDb();


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });


        r =  new Runnable() {
            @Override
            public void run() {

            }
        };
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validateMobileNumber(txtMobileNumber.getText().toString().trim())) {

                    String mobilenumber = txtMobileNumber.getText().toString().trim();

                    new ProNetworkSettup(VerifyMobileNumber.this).checkMobileNumberRegistered(mobilenumber, new LoginInterface() {
                        @Override
                        public void success(String message) {

                            try {
                                JSONObject checkMobileInfo = new JSONObject(message);

                                if (checkMobileInfo.has("isRegistered")) {
                                    if (checkMobileInfo.getString("isRegistered") != null &&
                                            !checkMobileInfo.getString("isRegistered").trim().equalsIgnoreCase("null")
                                            && checkMobileInfo.getString("isRegistered").trim().equalsIgnoreCase("true")) {

                                        showProgress(true);
                                        new ProNetworkSettup(VerifyMobileNumber.this).tokenAuth(txtMobileNumber.getText().toString().trim(), new StateInterface() {
                                            @Override
                                            public void success(String message) {

                                                try {
                                                    JSONObject userLoginInfo = new JSONObject(message);
                                                    if (userLoginInfo.has("secure_login_token") && userLoginInfo.getString("secure_login_token") != null && !userLoginInfo.getString("secure_login_token").trim().equalsIgnoreCase("")) {
                                                        // create session

                                                        if (userLoginInfo.getString("user_type") != null &&
                                                                !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("null")
                                                                && !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("")) {

                                                            //finishLogin(message, mSession.getStateSelection(), "Token " + userLoginInfo.getString("secure_login_token"));
                                                            finishLogin(message, mSession.getStateSelection(), "" + userLoginInfo.getString("secure_login_token"));


                                                        } else {
                                                            //update profile
                                                            showProgress(false);
                                                            Intent intent = new Intent(getApplicationContext(), UpdateProfileBeforeLoginActivity.class);
                                                            intent.putExtra("firstName", userLoginInfo.getString("first_name"));
                                                            intent.putExtra("lastName", userLoginInfo.getString("last_name"));
                                                            intent.putExtra("mobile", userLoginInfo.getString("mobile_no"));
                                                            intent.putExtra("email", userLoginInfo.getString("email"));
                                                            intent.putExtra("token", userLoginInfo.getString("secure_login_token"));
                                                            startActivity(intent);


                                                        }
                                                    } else
                                                        showProgress(false);

                                                } catch (Exception e) {
                                                    showProgress(false);
                                                }

                                            }

                                            @Override
                                            public void failed(String message) {
                                                showProgress(false);

                                                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                                            }
                                        });

                                    } else {
                                        //user not registered navigate to registration screen

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(VerifyMobileNumber.this);
                                        dialog.setCancelable(false);
                                        dialog.setMessage(getResources().getString(R.string.mobilenumbernotfound));
                                        dialog.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                showProgress(false);
                                                Intent i = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                                                if (!txtMobileNumber.getText().toString().trim().equalsIgnoreCase("")) {
                                                    i.putExtra("mobileNumber", txtMobileNumber.getText().toString().trim());
                                                }
                                                startActivity(i);
                                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                            }
                                        });
                                        dialog.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog.show();

                                    }
                                } else showProgress(false);


                            } catch (Exception e) {
                                showProgress(false);
                            }


                        }

                        @Override
                        public void failed(String message) {
                            showProgress(false);

                            DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                        }
                    });

                } else {

                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private boolean validateMobileNumber(String mobileNumber) {

        if (TextUtils.isDigitsOnly(mobileNumber)) {
            if (mobileNumber.length() == 10) {
                return true;
            } else {
                txtMobileNumber.setError(getResources().getString(R.string.enter_ten_digit_number));
                return false;
            }
        } else {
            txtMobileNumber.setError(getResources().getString(R.string.enter_valid_email_or_Phone));
            return false;
        }


    }

    /**
     * Shows the progress UI.
     */
    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(VerifyMobileNumber.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(getResources().getString(R.string.authenticating));
            progressDialog.show();
        } else {
            try {


                if (progressDialog != null) {
                    if (!VerifyMobileNumber.this.isFinishing()) {
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


    protected void finishLogin(final String userInfo, String stateKey, final String token) {
        //parse the userInfo String

        String URL = BuildConfig.HOST + "/api/v1/surveys/?survey_tag=konnect&state=" + stateKey + "&status=AC&per_page=0";

        new ProNetworkSettup(VerifyMobileNumber.this).getSurveyandQuestionGroup(URL, stateKey, token, new StateInterface() {
            @Override
            public void success(String message) {


                flag = 0;
                final ArrayList<Survey> pojoList = getS_Qids();
                if (pojoList != null && pojoList.size() > 0) {

                    for (int i = 0; i < pojoList.size(); i++) {
                        flag = i;

                        String url = BuildConfig.HOST + "/api/v1/surveys/" + pojoList.get(i).getId() + "/questiongroup/" + pojoList.get(i).getQuestionGroupId() + "/questions/?state=" + mSession.getStateSelection() + "&per_page=0";
                        new ProNetworkSettup(VerifyMobileNumber.this).getCommunitySurveyQuestions(url, pojoList.get(i).getQuestionGroupId(), flag, pojoList.size(), token, new StateInterface() {
                            @Override
                            public void success(String message) {


                                try {
                                    JSONObject userLoginInfo = new JSONObject(userInfo);
                                    if (userLoginInfo.has("secure_login_token")) {
                                        // create session
                                        String users = "PR";
                                        if (userLoginInfo.getString("user_type") != null && !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("null")) {
                                            users = userLoginInfo.getString("user_type").toUpperCase();
                                        }

                                        mSession.createLoginSession(
                                                userLoginInfo.getString("first_name"),
                                                userLoginInfo.getString("id"),
                                                userLoginInfo.getString("secure_login_token"),
                                                userLoginInfo.getString("last_name"),
                                                userLoginInfo.getString("email"),
                                                userLoginInfo.getString("mobile_no"),
                                                userLoginInfo.getString("dob"),
                                                users);

                                        showProgress(false);

                                        try {
                                            subscribetoTopicsForNotification(mSession.getState(), mSession.getUserType());
                                        } catch (Exception e) {

                                        }
                                        Intent intent = new Intent(VerifyMobileNumber.this, TempLoading.class);
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

    private void subscribetoTopicsForNotification(String state, String stateUserType) {

        try {
            FirebaseMessaging.getInstance().subscribeToTopic(state);
            FirebaseMessaging.getInstance().subscribeToTopic(state + "-" + RolesUtils.getUserRoleValueForFcmGroup(getApplicationContext(), db, stateUserType));
        } catch (Exception e) {
            //may be topic contains some special symbols
        }
    }

    public void incoming_Timer() {
        handler.postDelayed(r, 1000);
    }
}
