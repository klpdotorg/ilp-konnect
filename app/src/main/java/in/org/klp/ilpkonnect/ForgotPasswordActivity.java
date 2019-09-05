package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import in.org.klp.ilpkonnect.InterfacesPack.RestPasswordStateInterface;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.Pojo.ResetPasswordPojo;

import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.ProgressUtil;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by Shridhar on 01/02/2018.
 */
public class ForgotPasswordActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    EditText new_password, edtOTPNumber;
    Button btnOK;
    TextView tvMobileNumber;
    SessionManager sessionManager;
    String mobile = "";
    TextView tvResentOTP, tvResentOTPTimer, tvHint;
    CountDownTimer cTimer = null;

    int flag = 0;
    KontactDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        initlization();
        sessionManager = new SessionManager(getApplicationContext());
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);
        startTimer();
        db = ((KLPApplication) getApplicationContext()).getDb();

/*
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                edtOTPNumber.setText(code);//set code in edit text
                new_password.requestFocus();
                //then you can send verification code to server
            }
        });
*/
        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validation(new_password.getText().toString().trim(), edtOTPNumber.getText().toString().trim())) {
                    progressDialog.show();
                    new ProNetworkSettup(getApplicationContext()).forgotPasswordResetWithOTP(mobile.trim(), edtOTPNumber.getText().toString().trim(),
                            new_password.getText().toString().trim(), sessionManager.getStateSelection(), new RestPasswordStateInterface() {
                                @Override
                                public void success(final ResetPasswordPojo message) {
                          /*  progressDialog.dismiss();
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.passwordChanged),
                                    getResources().getString(R.string.login));
*/
                          progressDialog.dismiss();
                                    finishLogin(message,sessionManager.getStateKey(),message.getToken());

                                 /*   android.support.v7.app.AlertDialog alertDailog = new android.support.v7.app.AlertDialog.Builder(ForgotPasswordActivity.this).create();


                                    alertDailog.setCancelable(false);
                                    alertDailog.setMessage(getResources().getString(R.string.passwordChanged));
                                    alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_positive),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();


                                                }
                                            });
                                    alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(i);
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });

                                    alertDailog.show();*/

                                }

                                @Override
                                public void failed(String message) {
                                    progressDialog.dismiss();
                                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                                }
                            });

                    //      Toast.makeText(getApplicationContext(),"Coming soon",Toast.LENGTH_SHORT).show();


                }


            }
        });


        tvResentOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setMessage(getString(R.string.resendingOtp));

                new ProNetworkSettup(getApplicationContext()).forgotPasswordGenerateOtp(mobile, sessionManager.getStateSelection(), false, new StateInterface() {
                    @Override
                    public void success(String message) {
                        closeProgress();
                        tvResentOTP.setTextColor(Color.BLACK);
                        startTimer();

                        Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void failed(String message) {
                        closeProgress();
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

    @Override
    protected void onStart() {
        super.onStart();
        //smsVerifyCatcher.onStart();
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
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */

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

    public void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void initlization() {

        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        new_password = findViewById(R.id.new_password);
        tvHint = findViewById(R.id.tvHint);
        edtOTPNumber = findViewById(R.id.edtOTPNumber);
        tvResentOTP = findViewById(R.id.tvResentOTP);
        tvResentOTPTimer = findViewById(R.id.tvResentOTPTimer);
        tvResentOTP.setText(Html.fromHtml(getString(R.string.ResendOTP)));
        btnOK = findViewById(R.id.btnOK);
        progressDialog = ProgressUtil.showProgress(ForgotPasswordActivity.this, getResources().getString(R.string.resettingpassword));

    }


    protected void finishReset() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


    }


    public boolean validation(String PAssword, String otp) {


        if (TextUtils.isEmpty(otp.trim())) {
            edtOTPNumber.setError(getResources().getString(R.string.error_field_required));
            return false;
        }

        if (TextUtils.isEmpty(PAssword.trim())) {
            new_password.setError(getResources().getString(R.string.error_field_required));
            return false;
        }


        return true;

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
            resultDialog.show(getSupportFragmentManager(), "Forgot password result");
        } catch (Exception e) {

        }
    }


    @Override
    public void onBackPressed() {

    }

    protected void finishLogin(final ResetPasswordPojo userInfo, String stateKey, final String token) {
        //parse the userInfo String
        progressDialog.setMessage(getResources().getString(R.string.downloadingSurvey));
        progressDialog.show();
      //  Log.d("shri",stateKey+":---"+token);
//        Log.d("shri",userInfo.toString());
        String URL = BuildConfig.HOST + "/api/v1/surveys/?survey_tag=konnect&state=" + stateKey + "&status=AC&per_page=0";

        new ProNetworkSettup(ForgotPasswordActivity.this).getSurveyandQuestionGroup(URL, stateKey, token, new StateInterface() {
            @Override
            public void success(String message) {


                flag = 0;
                final ArrayList<Survey> pojoList = getS_Qids();
  //              Log.d("shri",pojoList.size()+"---------------------");
                if (pojoList != null && pojoList.size() > 0) {

                    for (int i = 0; i < pojoList.size(); i++) {
                        flag = i;

                        String url = BuildConfig.HOST + "/api/v1/surveys/" + pojoList.get(i).getId() + "/questiongroup/" + pojoList.get(i).getQuestionGroupId() + "/questions/?state=" + sessionManager.getStateSelection() + "&per_page=0";
                        new ProNetworkSettup(ForgotPasswordActivity.this).getCommunitySurveyQuestions(url, pojoList.get(i).getQuestionGroupId(), flag, pojoList.size(), token, new StateInterface() {
                            @Override
                            public void success(String message) {
                          //      Log.d("shri",flag+"---------------======------");


                                try {
                                    // JSONObject userLoginInfo = new JSONObject(userInfo);
                                    if (userInfo.getToken() != null && !userInfo.getToken().equals("") && !userInfo.getToken().equals("null")) {
                                        // create session
                                        String users = "PR";
                                        if (userInfo.getUserType() != null && !userInfo.getUserType().trim().equalsIgnoreCase("null")) {
                                            users = userInfo.getUserType().toUpperCase();
                                        }

                                        String firstname = userInfo.getFirstName() != null ? userInfo.getFirstName() : "";
                                        int userid = userInfo.getId() != null ? userInfo.getId() : 0;
                                        String lastName = userInfo.getLastName() != null ? userInfo.getLastName() : "";
                                        String tokenID = userInfo.getToken() != null ? userInfo.getToken() : "";
                                        String email = userInfo.getEmail() != null ? userInfo.getEmail() : "";
                                        String mobile = userInfo.getMobileNo() != null ? userInfo.getMobileNo() : "";
                                        String dob = userInfo.getDob() != null ? userInfo.getDob() : "";
                                       /* sessionManager.createLoginSession(
                                                firstname,
                                                userid + "",
                                                tokenID,
                                                lastName,
                                                email,
                                                mobile,
                                                dob,
                                                users);*/

                                        closeProgress();

                                        try {
                                            subscribetoTopicsForNotification(sessionManager.getState(), sessionManager.getUserType());
                                        } catch (Exception e) {

                                        }
                                        Intent intent = new Intent(ForgotPasswordActivity.this, TempLoading.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                        finish();


                                    } else {
                                        closeProgress();
                                        //Show login dialog again. Clear out fields. Show a message. Ask to login again
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    closeProgress();
                                }


                            }

                            @Override
                            public void failed(String message) {
                                closeProgress();
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),
                                        message,
                                        getResources().getString(R.string.Ok));
                            }
                        });

                    }


                } else {
                    closeProgress();
                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.surveyLoadingfailed),
                            getResources().getString(R.string.Ok));
                }


            }

            @Override
            public void failed(String message) {
                closeProgress();
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
