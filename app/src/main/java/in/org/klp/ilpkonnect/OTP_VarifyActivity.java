package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;

import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 2/1/2018.
 */
//https://github.com/stfalcon-studio/SmsVerifyCatcher/blob/master/sample/src/main/java/com/stfalcon/smsverifycatcher_sample/MainActivity.java
public class OTP_VarifyActivity extends BaseActivity {



    String mobile;
    TextView tvMobileNumber;
    EditText edtOTPNumber;
    private ProgressDialog progressDialog = null;
    SessionManager sessionManager;
    TextView tvResentOTP, tvResentOTPTimer, tvHint;
    CountDownTimer cTimer = null;
    int flag = 0;
    KontactDatabase db;
    Button btnOK;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        edtOTPNumber = findViewById(R.id.edtOTPNumber);
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);
        db = ((KLPApplication) getApplicationContext()).getDb();


        tvResentOTP = findViewById(R.id.tvResentOTP);
        tvResentOTPTimer = findViewById(R.id.tvResentOTPTimer);
        tvResentOTP.setText(Html.fromHtml(getString(R.string.ResendOTP)));
        startTimer();
        sessionManager = new SessionManager(getApplicationContext());
        btnOK = findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation(edtOTPNumber.getText().toString().trim())) {
                    showProgress(true);

                    new ProNetworkSettup(getApplicationContext()).varifyOTPAfterSignup(mobile, edtOTPNumber.getText().toString().trim(), sessionManager.getStateSelection(), new StateInterface() {
                        @Override
                        public void success(String message) {


                            JSONObject jsonObject =null;
                            try {
                                jsonObject = new JSONObject(message);
                                finishLogin(message,sessionManager.getStateSelection(),"Token " + jsonObject.getString("token"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                showProgress(false);
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),
                                        getResources().getString(R.string.signupsuccess),
                                        getResources().getString(R.string.login));
                            }


                        }

                        @Override
                        public void failed(String message) {
                            showProgress(false);
                            DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                        }
                    });

                }

            }
        });

        tvResentOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProgress(true);

                progressDialog.setMessage(getString(R.string.resendingOtp));
                new ProNetworkSettup(getApplicationContext()).forgotPasswordGenerateOtp(mobile, sessionManager.getStateSelection(), false, new StateInterface() {
                    @Override
                    public void success(String message) {
                        // closeProgress();
                        showProgress(false);
                        startTimer();

                        tvResentOTP.setTextColor(Color.BLACK);
                        Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void failed(String message) {
                        // closeProgress();
                        showProgress(false);
                        DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    }
                });
            }
        });
    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    void startTimer() {
        tvResentOTP.setEnabled(false);
        cTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {

                tvResentOTPTimer.setText(String.format(" %02d", (millisUntilFinished / 60000)) + ":" + String.format(" %02d", (millisUntilFinished % 60000 / 1000)));

            }

            public void onFinish() {
                cancelTimer();
                tvResentOTPTimer.setText("");
                tvResentOTP.setEnabled(true);
                tvResentOTP.setTextColor(Color.BLUE);

            }
        };
        cTimer.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private String parseCode(String message) {
        try {
            Pattern p = Pattern.compile("\\b\\d{5}\\b");
            Matcher m = p.matcher(message);
            String code = "";
            while (m.find()) {
                code = m.group(0);
            }
            return code;
        }catch (Exception e)
        {
            return "";
        }
    }


    /**
     * need for Android 6 real time permissions
     */


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
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

    }

    public boolean validation(String edtmobile) {
        if (TextUtils.isEmpty(edtmobile.trim())) {

            edtOTPNumber.setError(getResources().getString(R.string.pleaseEnterValidOTP));
            edtOTPNumber.requestFocus();
            return false;

        } else
            return true;


    }

    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(OTP_VarifyActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(getResources().getString(R.string.authenticating));
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                if (!OTP_VarifyActivity.this.isFinishing()) {
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }




    protected void finishLogin(final String userInfo, String stateKey, final String token) {
        //parse the userInfo String


        String URL = BuildConfig.HOST + "/api/v1/surveys/?survey_tag=konnect&state=" + stateKey + "&status=AC&per_page=0";

        new ProNetworkSettup(OTP_VarifyActivity.this).getSurveyandQuestionGroup(URL, stateKey, token, new StateInterface() {
            @Override
            public void success(String message) {


                flag = 0;
                final ArrayList<Survey> pojoList = getS_Qids();
                if (pojoList != null && pojoList.size() > 0) {

                    for (int i = 0; i < pojoList.size(); i++) {
                        flag = i;

                        String url = BuildConfig.HOST + "/api/v1/surveys/" + pojoList.get(i).getId() + "/questiongroup/" + pojoList.get(i).getQuestionGroupId() + "/questions/?state=" + sessionManager.getStateSelection() + "&per_page=0";
                        new ProNetworkSettup(OTP_VarifyActivity.this).getCommunitySurveyQuestions(url, pojoList.get(i).getQuestionGroupId(), flag, pojoList.size(), token, new StateInterface() {
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

                                        sessionManager.createLoginSession(
                                                userLoginInfo.getString("first_name"),
                                                userLoginInfo.getString("id"),
                                                userLoginInfo.getString("token"),
                                                userLoginInfo.getString("last_name"),
                                                userLoginInfo.getString("email"),
                                                userLoginInfo.getString("mobile_no"),
                                                userLoginInfo.getString("dob"),
                                                users);

                                        showProgress(false);

                                        try {
                                            subscribetoTopicsForNotification(sessionManager.getState(), sessionManager.getUserType());
                                        } catch (Exception e) {

                                        }
                                        Intent intent = new Intent(OTP_VarifyActivity.this, TempLoading.class);
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
