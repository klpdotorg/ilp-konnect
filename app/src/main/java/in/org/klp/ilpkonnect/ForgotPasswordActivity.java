package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.ProgressUtil;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        initlization();
        sessionManager = new SessionManager(getApplicationContext());
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);
        startTimer();
        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validation(new_password.getText().toString().trim(), edtOTPNumber.getText().toString().trim())) {
                    progressDialog.show();
                    new ProNetworkSettup(getApplicationContext()).forgotPasswordResetWithOTP(mobile.trim(), edtOTPNumber.getText().toString().trim(), new_password.getText().toString().trim(), sessionManager.getStateSelection(), new StateInterface() {
                        @Override
                        public void success(String message) {
                            progressDialog.dismiss();
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.passwordChanged),
                                    getResources().getString(R.string.login));


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


}
