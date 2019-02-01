package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
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
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 2/1/2018.
 */

public class ForgotPasswordOTP extends BaseActivity {

EditText edtOTPNumber;
Button btnOK;
SessionManager sessionManager;
ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword_forotp);
        edtOTPNumber= findViewById(R.id.edtOTPNumber);
        btnOK= findViewById(R.id.btnOK);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intitProgreess();
        sessionManager=new SessionManager(getApplicationContext());

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(validationmob(edtOTPNumber.getText().toString().trim()))
                {
                progressDialog.show();
                    new ProNetworkSettup(getApplicationContext()).forgotPasswordGenerateOtp(edtOTPNumber.getText().toString().trim(),sessionManager.getStateSelection(), true,new StateInterface() {
                        @Override
                        public void success(String message) {
                            closeProgress();
                            Intent i=new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                            i.putExtra("mobile",edtOTPNumber.getText().toString().trim());
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            finish();



                        }

                        @Override
                        public void failed(String message) {
                            closeProgress();
                            DailogUtill.showDialog(message,getSupportFragmentManager(),getApplicationContext());
                        }
                    });

                }
            }
        });
    }

    private boolean validationmob(String mob) {

        if(TextUtils.isEmpty(mob))
        {
            edtOTPNumber.setError(getString(R.string.error_field_required));
            edtOTPNumber.requestFocus();
            return false;
        }
        else return isEmailOrPhoneValid(mob) != false;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:


                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean isEmailOrPhoneValid(String mobile) {

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(mobile).matches()) {   //its an email id
            //  Toast.makeText(getApplicationContext(),"Syntactically valid email",Toast.LENGTH_SHORT).show();
            return true;
        } else {
            if (TextUtils.isDigitsOnly(mobile)) {
                if (mobile.length() == 10) {
                    //Toast.makeText(getApplicationContext(), "its Digit", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    edtOTPNumber.setError(getResources().getString(R.string.enter_ten_digit_number));
                    return false;
                }
            } else {
                // Generic Message
                // Toast.makeText(getApplicationContext(),"Please provide valid Email or phone number",Toast.LENGTH_SHORT).show();
                edtOTPNumber.setError(getResources().getString(R.string.enter_valid_email_or_Phone));
                return false;
                //Please provide valid Email or phone number
            }
        }

    }


    public void intitProgreess()
    {
        progressDialog = new ProgressDialog(ForgotPasswordOTP.this);
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage(getResources().getString(R.string.authenticating));
        progressDialog.setCancelable(false);
    }


    public void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
