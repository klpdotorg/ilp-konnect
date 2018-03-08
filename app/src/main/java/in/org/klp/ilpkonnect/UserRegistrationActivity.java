package in.org.klp.ilpkonnect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import in.org.klp.ilpkonnect.Errorpack.Register400Error;
import in.org.klp.ilpkonnect.Pojo.RegstrationResponsePojo;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.db.KontactDatabase;

import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;

import in.org.klp.ilpkonnect.utils.DailogUtill;

import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;

public class UserRegistrationActivity extends BaseActivity {

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
    private KontactDatabase db;
    ImageView calBtn;
    int cyear, cmonth, cdate;
    SessionManager sessionManager;
    String ReqDate;
    boolean isForRegister = true;
    SquidCursor<Respondent> respondentCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        final TextView loginLink = findViewById(R.id.backtologin);
        sessionManager = new SessionManager(getApplicationContext());

        String stateKey = sessionManager.getStateSelection();
        Linkify.addLinks(loginLink, Linkify.ALL);
        db = ((KLPApplication) getApplicationContext()).getDb();
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


        cyear = 1988;
        cdate = 1;
        cmonth = 0;

        Button mEmailSignUpButton = findViewById(R.id.register_button);
        emailWidget = findViewById(R.id.user_email);
        passwordWidget = findViewById(R.id.password);
        verifyPasswordWidget = findViewById(R.id.verify_password);
        firstNameWidget = findViewById(R.id.user_first_name);
        lastNameWidget = findViewById(R.id.user_last_name);
        phoneNoWidget = findViewById(R.id.user_phone);
        spnRespondantType = findViewById(R.id.spnRespondantType);
        edtDob = findViewById(R.id.edtDob);
        calBtn = findViewById(R.id.calBtn);

        calBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                setdate(null, cyear, cmonth, cdate);
            }
        });


        userType = new LinkedHashMap<String, String>();
        userType.put(getResources().getString(R.string.pleaseSelectrespondanttype), "No");
        userType.putAll(RolesUtils.getUserRoles(getApplicationContext(), db, stateKey));
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
                    String passwordValue = passwordWidget.getText().toString().trim();
                    String verifyPasswordValue = verifyPasswordWidget.getText().toString().trim();
                    String firstNameValue = firstNameWidget.getText().toString().trim();
                    String lastNameValue = lastNameWidget.getText().toString().trim();
                    String phoneNoValue = phoneNoWidget.getText().toString().trim();
                    String dateofBirth = edtDob.getText().toString().trim();
                    //  mSelectedUserType = userType.get(spnRespondantType.getSelectedItem().toString());
                    mSelectedUserType = userType.get(spnRespondantType.getSelectedItem().toString());
                    //   Toast.makeText(getApplicationContext(), mSelectedUserType, Toast.LENGTH_SHORT).show();
                    View focusView = null;
                    boolean cancel = false;

                    if (TextUtils.isEmpty(phoneNoValue) || phoneNoValue.length() != 10 || !TextUtils.isDigitsOnly(phoneNoValue)) {
                        phoneNoWidget.setError(getResources().getString(R.string.enter_ten_digit_number));
                        focusView = phoneNoWidget;
                        cancel = true;
                        //make true
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
                    } else if (!isValidFormat(dateofBirth)) {
                        edtDob.setError(getResources().getString(R.string.enterValidDate));
                        focusView = edtDob;
                        cancel = true;
                    } else if (checkCalendarDate(dateofBirth)) {
                        edtDob.setError("You cannot enter a date in the future");
                        focusView = edtDob;
                        cancel = true;
                    } else if (!isEmailValid(emailValue)) {
                        //emailWidget.setError("This email address is invalid");
                        focusView = emailWidget;
                        cancel = true;
                    } else if (spnRespondantType.getSelectedItemPosition() == 0) {
                        focusView = spnRespondantType;
                        // ReqDate=getRevDate(dateofBirth);
                        //    Toast.makeText(getApplicationContext(),ReqDate,Toast.LENGTH_SHORT).show();

                        showSignupResultDialog(getResources().getString(R.string.app_name), getResources().getString(R.string.pleaseSelectrespondanttype), getResources().getString(R.string.Ok));
                        // Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseSelectrespondanttype), Toast.LENGTH_SHORT).show();
                        cancel = true;

                    }

                    //If no errors, proceed with post to server.


                    if (!cancel) {

                        Call<RegstrationResponsePojo> response = null;
                        ReqDate = getRevDate(dateofBirth);
                        showProgress(true);
                        if (emailValue.trim().isEmpty()) {
                            emailValue = "";
                            response = ApiClient.getClient().create(ApiInterface.class).registrationServiceWithoutEmail(
                                    phoneNoValue.trim(), firstNameValue.trim(), lastNameValue.trim(), passwordValue.trim(),
                                    "konnect", mSelectedUserType, ReqDate,sessionManager.getStateSelection());
                        } else {


                            response = ApiClient.getClient().create(ApiInterface.class).registrationService(emailValue.trim(),
                                    phoneNoValue.trim(), firstNameValue.trim(), lastNameValue.trim(), passwordValue.trim(),
                                    "konnect", mSelectedUserType, ReqDate,sessionManager.getStateSelection());

                        }


                        response.enqueue(new Callback<RegstrationResponsePojo>() {
                            @Override
                            public void onResponse(Call<RegstrationResponsePojo> call, retrofit2.Response<RegstrationResponsePojo> response) {

                                showProgress(false);
                                // Toast.makeText(getApplicationContext(), response.code()+"", Toast.LENGTH_SHORT).show();
                                if (response.isSuccessful()) {


                                    //  Toast.makeText(getApplicationContext(),response.body().getSmsVerificationPin()+"",Toast.LENGTH_LONG).show();
                                    Intent otpIntent = new Intent(getApplicationContext(), OTP_VarifyActivity.class);
                                    otpIntent.putExtra("mobile", phoneNoWidget.getText().toString().trim());
                                    startActivity(otpIntent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    //    clearAllFields();
                                } else if (response.code() == 400) {

                                    Gson gson = new Gson();
                                    Register400Error messageObject = gson.fromJson(response.errorBody().charStream(), Register400Error.class);
                                    String messsage = getResources().getString(R.string.oops);
                                    if (messageObject != null && messageObject.getEmail() != null && messageObject.getEmail().size() != 0 && messageObject.getMobileNo() != null && messageObject.getMobileNo().size() != 0) {
                                        DailogUtill.showDialog(messageObject.getMobileNo().get(0) + "\n" + messageObject.getEmail().get(0), getSupportFragmentManager(), getApplicationContext());
                                    } else if (messageObject != null && messageObject.getMobileNo() != null && messageObject.getMobileNo().size() != 0) {
                                        DailogUtill.showDialog(messageObject.getMobileNo().get(0), getSupportFragmentManager(), getApplicationContext());

                                    } else if (messageObject != null && messageObject.getEmail() != null && messageObject.getEmail().size() != 0) {
                                        DailogUtill.showDialog(messageObject.getEmail().get(0), getSupportFragmentManager(), getApplicationContext());

                                    } else {
                                        DailogUtill.showDialog(messsage, getSupportFragmentManager(), getApplicationContext());
                                    }


                                } else {
                                    DailogUtill.showDialog(getResources().getString(R.string.oops), getSupportFragmentManager(), getApplicationContext());

                                }

                            }

                            @Override
                            public void onFailure(Call<RegstrationResponsePojo> call, Throwable t) {
                                showProgress(false);
                                DailogUtill.showDialog(getFailureMessage(t), getSupportFragmentManager(), getApplicationContext());

                            }
                        });


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
                cyear = year;
                cdate = dayOfMonth;
                cmonth = monthOfYear;
                //   ReqDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                edtDob.setError(null);
            }
        }, y, m, d);
        try {
            // dpd.getDatePicker().setMaxDate(new Date().getTime());
            Calendar maxCal = Calendar.getInstance();
            maxCal.set(Calendar.YEAR, maxCal.get(Calendar.YEAR));
            dpd.getDatePicker().setMaxDate(maxCal.getTimeInMillis() - 1000);
            // dpd.getDatePicker().setMinDate();
        } catch (Exception e) {
        }
        dpd.show();
    }

    public boolean checkCalendarDate(String strDate) {
        boolean flag = false;
        try {
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date1);
            flag = !cal1.after(cal2);

        } catch (Exception e) {
//Toast.makeText(getApplicationContext(),"Exception",Toast.LENGTH_SHORT).show();
        }
        return flag;

    }


    public static boolean isValidFormat(String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
                //  Log.d("Sriii","if");
                sdf = new SimpleDateFormat("d-M-yyyy");
                date = sdf.parse(value);
                if (!value.equals(sdf.format(date))) {
                    date = null;
                }
            }

        } catch (Exception ex) {
            //  Log.d("Sriii","exccee");
            ex.printStackTrace();
        }
        return date != null;
    }

    public String getRevDate(String strDate) {
        String newstring = "1980-01-01";
        try {
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);


            newstring = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        } catch (Exception e) {

        }

        return newstring;
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

            progressDialog.cancel();
        }
    }

    private void clearAllFields() {
        emailWidget.setText("");
        passwordWidget.setText("");
        verifyPasswordWidget.setText("");
        firstNameWidget.setText("");
        lastNameWidget.setText("");
        phoneNoWidget.setText("");
        emailWidget.setText("");
        edtDob.setText("");
    }


    public int getUserCount() {
        Query listStoryQuery = Query.select().from(Respondent.TABLE);
        SquidCursor<Respondent> storiesCursor = db.query(Respondent.class, listStoryQuery);
        return storiesCursor.getCount();
    }


    public String getFailureMessage(Throwable t) {
        if (t instanceof IOException) {
            return getResources().getString(R.string.netWorkError);
            // logging probably not necessary
        } else {
            return getResources().getString(R.string.oops);
        }


    }
}
