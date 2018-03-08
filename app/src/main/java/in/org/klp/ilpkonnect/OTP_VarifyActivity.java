package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        edtOTPNumber = findViewById(R.id.edtOTPNumber);
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);
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


    }


    private void showSignupResultDialog(String title, String message, String buttonText) {


        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        try {
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
