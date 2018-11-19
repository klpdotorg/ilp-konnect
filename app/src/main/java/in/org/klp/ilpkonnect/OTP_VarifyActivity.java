package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.pm.ComponentInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 2/1/2018.
 */

public class OTP_VarifyActivity extends BaseActivity {


    String mobile;
    TextView tvMobileNumber;
    EditText edtOTPNumber;
    private ProgressDialog progressDialog = null;
    SessionManager sessionManager;
    TextView tvResentOTP,tvResentOTPTimer,tvHint;
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        edtOTPNumber = findViewById(R.id.edtOTPNumber);
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);


        tvResentOTP= findViewById(R.id.tvResentOTP);
        tvResentOTPTimer= findViewById(R.id.tvResentOTPTimer);
        tvResentOTP .setText(Html.fromHtml(getString(R.string.ResendOTP)));
        startTimer();
        sessionManager=new SessionManager(getApplicationContext());
        Button btnOK = findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation(edtOTPNumber.getText().toString().trim())) {
                    showProgress(true);

                    new ProNetworkSettup(getApplicationContext()).varifyOTPAfterSignup(mobile, edtOTPNumber.getText().toString().trim(),sessionManager.getStateSelection(), new StateInterface() {
                        @Override
                        public void success(String message) {
                            showProgress(false);
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.signupsuccess),
                                    getResources().getString(R.string.login));
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
                 new ProNetworkSettup(getApplicationContext()).forgotPasswordGenerateOtp(mobile,sessionManager.getStateSelection(),false, new StateInterface() {
                    @Override
                    public void success(String message) {
                       // closeProgress();
                        showProgress(false);
                        startTimer();
                        tvResentOTP.setTextColor(Color.BLACK);
                        Toast.makeText(getApplicationContext(),"OTP Sent",Toast.LENGTH_SHORT).show();



                    }

                    @Override
                    public void failed(String message) {
                       // closeProgress();
                        showProgress(false);
                        DailogUtill.showDialog(message,getSupportFragmentManager(),getApplicationContext());
                    }
                });
            }
        });
    }

    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }
    void startTimer() {
        tvResentOTP.setEnabled(false);
        cTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {

                tvResentOTPTimer.setText(String.format(" %02d", (millisUntilFinished / 60000))+":"+String.format(" %02d", (millisUntilFinished % 60000 / 1000)));
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
}
