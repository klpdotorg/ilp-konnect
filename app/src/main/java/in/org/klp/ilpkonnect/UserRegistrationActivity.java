package in.org.klp.ilpkonnect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import in.org.klp.ilpkonnect.Pojo.RegstrationResponsePojo;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;

public class UserRegistrationActivity extends AppCompatActivity {

    public String LOG_TAG = UserRegistrationActivity.class.getSimpleName();

    //UI references

    private EditText emailWidget, edtDob;
    private TextView passwordWidget;
    private TextView verifyPasswordWidget;
    private TextView lastNameWidget, firstNameWidget, phoneNoWidget;
    private ProgressDialog progressDialog = null;
    private Spinner spnRespondantType;
    private LinkedHashMap<String, String> userType;
    private String mSelectedUserType;
    String emailValue;
    int cyear, cmonth, cdate, chour, cminute;
    String ReqDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        final TextView loginLink = (TextView) findViewById(R.id.backtologin);

        Linkify.addLinks(loginLink, Linkify.ALL);

        if (loginLink != null) {
            loginLink.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserRegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }


        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        cdate = c.get(Calendar.DAY_OF_MONTH);
        cmonth = c.get(Calendar.MONTH);
        chour = c.get(Calendar.HOUR_OF_DAY);
        cminute = c.get(Calendar.MINUTE);

        Button mEmailSignUpButton = (Button) findViewById(R.id.register_button);
        emailWidget = (EditText) findViewById(R.id.user_email);
        passwordWidget = (TextView) findViewById(R.id.password);
        verifyPasswordWidget = (TextView) findViewById(R.id.verify_password);
        firstNameWidget = (TextView) findViewById(R.id.user_first_name);
        lastNameWidget = (TextView) findViewById(R.id.user_last_name);
        phoneNoWidget = (TextView) findViewById(R.id.user_phone);
        spnRespondantType = (Spinner) findViewById(R.id.spnRespondantType);
        edtDob = (EditText) findViewById(R.id.edtDob);

        edtDob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                setdate(null, cyear, cmonth, cdate);
            }
        });

        userType = new LinkedHashMap<String, String>();
        userType.put(getResources().getString(R.string.pleaseSelectrespondanttype), "No");
        userType.put("Parents", "PR");
        userType.put("Teachers", "TR");
        userType.put("Education Volunteers", "VR");
        userType.put("CBO Member", "CM");
        userType.put("Headmaster", "HM");
        userType.put("SDMC Member", "SM");
        userType.put("Local Leaders", "LL");
        userType.put("Akshara Staff", "AS");
        userType.put("Educated Youth", "EY");
        userType.put("Govt Official", "GO");
        userType.put("Education Official", "EO");
        userType.put("Elected Representative", "ER");

        List<String> userTypeNames = new ArrayList<>();
        userTypeNames.addAll(userType.keySet());
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(UserRegistrationActivity.this, R.layout.regspinner, userTypeNames);
        spnRespondantType.setAdapter(userTypeAdapter);
        mSelectedUserType = "PR";


        if (mEmailSignUpButton != null) {
            mEmailSignUpButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Add error checking here
                    emailValue = emailWidget.getText().toString().trim();
                    final String passwordValue = passwordWidget.getText().toString().trim();
                    String verifyPasswordValue = verifyPasswordWidget.getText().toString().trim();
                    final String firstNameValue = firstNameWidget.getText().toString().trim();
                    final String lastNameValue = lastNameWidget.getText().toString().trim();
                    final String phoneNoValue = phoneNoWidget.getText().toString().trim();
                    final String dateofBirth = edtDob.getText().toString().trim();
                    //  mSelectedUserType = userType.get(spnRespondantType.getSelectedItem().toString());
                    mSelectedUserType = userType.get(spnRespondantType.getSelectedItem().toString());
               //   Toast.makeText(getApplicationContext(), mSelectedUserType, Toast.LENGTH_SHORT).show();
                    View focusView = null;
                    boolean cancel = false;

                    if (TextUtils.isEmpty(phoneNoValue) || phoneNoValue.length() != 10 || !TextUtils.isDigitsOnly(phoneNoValue)) {
                        phoneNoWidget.setError(getResources().getString(R.string.enter_ten_digit_number));
                        focusView = phoneNoWidget;
                        cancel = true;
                    } else if (TextUtils.isEmpty(passwordValue)) {
                        passwordWidget.setError(getResources().getString(R.string.error_field_required));
                        focusView = passwordWidget;
                        cancel = true;
                    } else if (TextUtils.isEmpty(verifyPasswordValue) || !passwordValue.equals(verifyPasswordValue)) {
                        verifyPasswordWidget.setError(getResources().getString(R.string.doesnotmatcherwithpass));
                        focusView = verifyPasswordWidget;
                        cancel = true;
                    } else if (TextUtils.isEmpty(firstNameValue)) {
                        firstNameWidget.setError(getResources().getString(R.string.error_field_required));
                        focusView = firstNameWidget;
                        cancel = true;
                    } else if (TextUtils.isEmpty(lastNameValue)) {
                        lastNameWidget.setError(getResources().getString(R.string.error_field_required));
                        focusView = lastNameWidget;
                        cancel = true;
                    } else if (TextUtils.isEmpty(dateofBirth)) {
                        edtDob.setError(getResources().getString(R.string.error_field_required));
                        focusView = edtDob;
                        cancel = true;
                    }

                    /*else if (!TextUtils.isEmpty(emailValue)) {
                        emailWidget.setError(getResources().getString(R.string.error_field_required));
                        focusView = emailWidget;
                        cancel = true;
                    }*/
                    else if (!isEmailValid(emailValue)) {
                        //emailWidget.setError("This email address is invalid");
                        focusView = emailWidget;
                        cancel = true;
                    } else if (spnRespondantType.getSelectedItemPosition() == 0) {
                        focusView = spnRespondantType;
                        showSignupResultDialog(getResources().getString(R.string.app_name),getResources().getString(R.string.pleaseSelectrespondanttype),getResources().getString(R.string.Ok));
                       // Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseSelectrespondanttype), Toast.LENGTH_SHORT).show();
                        cancel = true;

                    }
                    //If no errors, proceed with post to server.

                    /*ApiClient.getClient().create(ApiInterface.class).registrationService(emailValue.trim(),
                            phoneNoValue.trim(),firstNameValue.trim(),lastNameValue.trim(),passwordValue.trim(),
                            "konnect",mSelectedUserType).enqueue(new Callback<RegstrationResponsePojo>() {
                        @Override
                        public void onResponse(Call<RegstrationResponsePojo> call, retrofit2.Response<RegstrationResponsePojo> response) {
                            if(response.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),response.body().getFirstName(),Toast.LENGTH_SHORT).show();

                            }else {

                                Toast.makeText(getApplicationContext(),response.message()+"ss",Toast.LENGTH_SHORT).show();

                            }


                        }

                        @Override
                        public void onFailure(Call<RegstrationResponsePojo> call, Throwable t) {

                            Toast.makeText(getApplicationContext(),t.getMessage().toString()+"12",Toast.LENGTH_SHORT).show();

                        }
                    });*/


                    if (!cancel) {
                        if (emailValue.trim().isEmpty()) {
                            emailValue = "";
                        }
                        showProgress(true);
                        Log.d("Reg", mSelectedUserType);
                        ApiClient.getClient().create(ApiInterface.class).registrationService(emailValue.trim(),
                                phoneNoValue.trim(), firstNameValue.trim(), lastNameValue.trim(), passwordValue.trim(),
                                "konnect", mSelectedUserType, ReqDate)
                                .enqueue(new Callback<RegstrationResponsePojo>() {
                                    @Override
                                    public void onResponse(Call<RegstrationResponsePojo> call, retrofit2.Response<RegstrationResponsePojo> response) {

                                        showProgress(false);
                                        if (response.isSuccessful()) {
                                            showSignupResultDialog(
                                                    getResources().getString(R.string.app_name),
                                                    getResources().getString(R.string.signupsuccess),
                                                    getResources().getString(R.string.login));
                                            clearAllFields();
                                        } else {
                                            String res = "Network issue";
                                            try {
                                                res = response.errorBody().string();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                JSONObject jsonObject = new JSONObject(res);
                                                String responseDetail = jsonObject.get(jsonObject.keys().next()).toString();


                                                showSignupResultDialog(
                                                        getResources().getString(R.string.app_name),
                                                        getResources().getString(R.string.signupfail) + "-" + responseDetail,
                                                        getResources().getString(R.string.Ok));
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                showSignupResultDialog(
                                                        getResources().getString(R.string.app_name),
                                                        getResources().getString(R.string.signupfail) + "-" + res,
                                                        getResources().getString(R.string.Ok));

                                            }

                                            clearAllFields();


                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<RegstrationResponsePojo> call, Throwable t) {
                                        showProgress(false);
                                        if(t instanceof IOException)
                                        {
                                            showSignupResultDialog(
                                                    getResources().getString(R.string.app_name),
                                                    getResources().getString(R.string.failed)+"-" + getResources().getString(R.string.noInternetCon),
                                                    getResources().getString(R.string.Ok));
                                        }else
                                        {
                                            showSignupResultDialog(
                                                    getResources().getString(R.string.app_name),
                                                    getResources().getString(R.string.failed)+"-" + getResources().getString(R.string.netWorkError),
                                                    getResources().getString(R.string.Ok));
                                        }
                                    }
                                });



                       /* Toast.makeText(getApplicationContext(),"s",Toast.LENGTH_SHORT).show();
                        showProgress(true);
                        final String SIGNUP_URL = BuildConfig.HOST + ILPService.REGISTRATION;
                         StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                SIGNUP_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                showSignupResultDialog(
                                        "Success!",
                                        "You have been successfully signed up. Please Login.",
                                        "Login");
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.getMessage() == null) {
                                    //  Toast.makeText(UserRegistrationActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                } else {
                                    showSignupResultDialog(
                                            "Error",
                                            "Signup failed - " + error.getMessage(),
                                            "Try Again");

                                    clearAllFields();
                                }
                                showProgress(false);
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                // set the POST params

                                  Map<String, String> params = new HashMap<String, String>();
                                params.put("email", emailValue.trim());
                                params.put("mobile_no", phoneNoValue.trim());
                                params.put("first_name", firstNameValue.trim());
                                params.put("last_name", lastNameValue.trim());

                                params.put("password", passwordValue);

                                params.put("source","konnect");
                                params.put("user_type",mSelectedUserType);


                                System.out.print("-----------------------------");
                                System.out.println(params);
                                System.out.println(BuildConfig.HOST + ILPService.REGISTRATION);

                                System.out.print("-----------------------------");

                                return params;
                            }


                             *//*Becuase the server returns the error response as JSON,
                            need to parse it before showing
                             *//*
                            @Override
                            protected VolleyError parseNetworkError(VolleyError volleyError) {
                                Log.d("shri",volleyError+"");

                                NetworkResponse networkResponse = volleyError.networkResponse;
                                String responseDetail = "some thing went wrong..";
                                JSONObject jsonObject = null;
                                if (networkResponse != null && networkResponse.data != null) {
                                    try {
                                        jsonObject = new JSONObject(new String(networkResponse.data));
                                        String key = jsonObject.keys().next();
                                        responseDetail = jsonObject.get(key).toString();
                                    } catch (JSONException e) {

                                      //  Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_SHORT).show();
                                    }

                                    // Print Error!

                                }
                                return new VolleyError(responseDetail);
                            }


                        };
                        //error = new VolleyError(jsonError.getString("detail"));
                        // Add request to the RequestQueue maintained by the Singleton

                       // AppController.getInstance().addToRequestQueue(postRequest);
                        KLPVolleySingleton.getInstance(UserRegistrationActivity.this).addToRequestQueue(stringRequest);*/
                    } else {
                        //There was an error. Do not attempt sign up. Just show the form field with the error
                        focusView.requestFocus();
                    }
                }
            });
        }
    }

    private void setdate(DatePicker view, int y, int m, int d) {
        DatePickerDialog dpd;

        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtDob.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                ReqDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                edtDob.setError(null);
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


    private void showSignupResultDialog(String title, String message, String buttonText) {
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        resultDialog.show(getSupportFragmentManager(), "Registration result");
    }


    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            emailValue = "";
            return true;
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {   //its an email id
                return true;
            } else {

                // Generic Message
                emailWidget.setError(getResources().getString(R.string.enter_valid_email));
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
            progressDialog = new ProgressDialog(UserRegistrationActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.authenticating));
            progressDialog.show();
            progressDialog.setCancelable(false);
        } else {
            if (progressDialog != null) progressDialog.cancel();
        }
    }

    private void clearAllFields() {
        emailWidget.clearComposingText();
        passwordWidget.clearComposingText();
        verifyPasswordWidget.clearComposingText();
        firstNameWidget.clearComposingText();
        lastNameWidget.clearComposingText();
        phoneNoWidget.clearComposingText();
        emailWidget.requestFocus();
    }


}
