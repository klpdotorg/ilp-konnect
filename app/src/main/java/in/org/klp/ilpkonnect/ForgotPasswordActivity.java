package in.org.klp.ilpkonnect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import in.org.klp.ilpkonnect.Pojo.ForgotPasswordPojo;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Subha on 7/13/16.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    EditText user_phone, edtDob, new_password;

    String dob,newPassword,mobile;
    int cyear, cmonth, cdate;
    String ReqDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        initlization();
        this.setTitle(getResources().getString(R.string.title_activity_forgot_password));

        final Button resetButton = (Button) findViewById(R.id.reset_password_button);


        edtDob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                setdate(null, cyear, cmonth, cdate);
            }
        });


        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mobile=user_phone.getText().toString().trim();
                dob=edtDob.getText().toString().trim();
                newPassword= new_password.getText().toString().trim();


                if(validation(mobile,dob,newPassword))
                {
                    progressDialog.show();

                    ApiClient.getClient().create(ApiInterface.class)
                            .setForgotPassword(mobile,ReqDate,newPassword).enqueue(new Callback<ForgotPasswordPojo>() {
                        @Override
                        public void onResponse(Call<ForgotPasswordPojo> call, retrofit2.Response<ForgotPasswordPojo> response) {

                            finishReset();
                            if(response.isSuccessful())
                            {
                                clearData();
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),
                                        response.body().getSuccess(),
                                        getResources().getString(R.string.Ok));
                            }

                            else
                            {
                                String res="User not found";
                                try {
                                    res=response.errorBody().string();
                                    JSONObject js=new JSONObject(res);
                                    res=js.getString("error");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),res
                                        ,
                                        getResources().getString(R.string.Ok));
                            }


                        }

                        @Override
                        public void onFailure(Call<ForgotPasswordPojo> call, Throwable t) {
                            finishReset();
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.title_activity_forgot_password)+"-" + getResources().getString(R.string.noInternetCon),
                                    getResources().getString(R.string.Ok));
                        }
                    });

                }

            }
        });

        final Button backToLogin = (Button) findViewById(R.id.email_sign_in_button);
        backToLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);

                finish();
            }
        });
    }

    private void setdate(DatePicker view, int y, int m, int d) {
        DatePickerDialog dpd;

        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtDob.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                ReqDate=String.format("%04d-%02d-%02d", year,monthOfYear + 1,dayOfMonth);
            }
        }, y, m, d);
        try {
           // dpd.getDatePicker().setMaxDate(new Date().getTime());
            Calendar maxCal = Calendar.getInstance();
            maxCal.set(Calendar.YEAR, maxCal.get(Calendar.YEAR) );
            dpd.getDatePicker().setMaxDate(maxCal.getTimeInMillis()-1000);
            // dpd.getDatePicker().setMinDate();
        } catch (Exception e) {
        }
        dpd.show();
    }
    private void initlization() {

        user_phone = (EditText) findViewById(R.id.user_phone);
        edtDob = (EditText) findViewById(R.id.edtDob);
        new_password = (EditText) findViewById(R.id.new_password);
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.resettingpassword));


        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        cdate = c.get(Calendar.DAY_OF_MONTH);
        cmonth = c.get(Calendar.MONTH);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void finishReset() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


    }


    public boolean validation(String mob,String dob,String PAssword)
    {

        if(TextUtils.isEmpty(mob.trim()))
        {
            user_phone.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        if(mob.length()!=10)
        {
            user_phone.setError(getResources().getString(R.string.enter_ten_digit_number));
            return false;
        }
        if(TextUtils.isEmpty(dob.trim()))
        {
            edtDob.setError(getResources().getString(R.string.error_field_required));
            return false;
        }

        if(TextUtils.isEmpty(PAssword.trim()))
        {
            new_password.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        return true;

    }

    private void showSignupResultDialog(String title, String message, String buttonText) {
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.show(getSupportFragmentManager(), "Forgot password result");
    }

    public void clearData()
    {
        edtDob.setText("");
        new_password.setText("");
        user_phone.setText("");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}
