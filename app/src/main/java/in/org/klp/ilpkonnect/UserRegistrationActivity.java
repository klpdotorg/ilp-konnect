package in.org.klp.ilpkonnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.Errorpack.Register400Error;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.Pojo.RegstrationResponsePojo;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.constants.ApplicationConstants;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;

public class UserRegistrationActivity extends BaseActivity {

    public String LOG_TAG = UserRegistrationActivity.class.getSimpleName();

    //UI references

    private EditText emailWidget;
    // private EditText edtDob;
    //private TextView passwordWidget;
    //  private TextView verifyPasswordWidget;
    private TextView lastNameWidget, firstNameWidget, phoneNoWidget, verifyUserPhone;
    private ProgressDialog progressDialog = null;
    private Spinner spnRespondantType;
    private LinkedHashMap<String, String> userType;
    private String mSelectedUserType;
    String emailValue, mblNo;
    private KontactDatabase db;
    //ImageView calBtn;
    int cyear, cmonth, cdate;
    SessionManager sessionManager;
    // String ReqDate;
    boolean isForRegister = true;
    SquidCursor<Respondent> respondentCursor = null;
    int flag = 0;


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
        if (sessionManager.getPASSWORD().isEmpty()) {
            try {
                sessionManager.setPASSWORD(ApplicationConstants.encrypt());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        cyear = 1988;
        cdate = 1;
        cmonth = 0;

        Button mEmailSignUpButton = findViewById(R.id.register_button);
        emailWidget = findViewById(R.id.user_email);
        //passwordWidget = findViewById(R.id.password);
        //verifyPasswordWidget = findViewById(R.id.verify_password);
        firstNameWidget = findViewById(R.id.user_first_name);
        lastNameWidget = findViewById(R.id.user_last_name);
        phoneNoWidget = findViewById(R.id.user_phone);
        spnRespondantType = findViewById(R.id.spnRespondantType);
        verifyUserPhone = findViewById(R.id.verify_user_phone);

        mblNo = getIntent().getStringExtra("mobileNumber");
        if( mblNo != null && !mblNo.isEmpty()){
            phoneNoWidget.setText(mblNo);
        }

        // Code commented for CR remove_login
        //edtDob = findViewById(R.id.edtDob);
        //calBtn = findViewById(R.id.calBtn);

       /* calBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                setdate(null, cyear, cmonth, cdate);
            }
        });*/


        userType = new LinkedHashMap<String, String>();
        userType.put(getResources().getString(R.string.pleaseSelectrespondanttype), "No");
        userType.putAll(RolesUtils.getUserRoles(getApplicationContext(), db, stateKey));
        List<String> userTypeNames = new ArrayList<>();
        userTypeNames.addAll(userType.keySet());
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(UserRegistrationActivity.this, R.layout.regspinner, userTypeNames);
        spnRespondantType.setAdapter(userTypeAdapter);
        mSelectedUserType = "PR";

        spnRespondantType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(UserRegistrationActivity.this);
                return false;
            }
        });
        if (mEmailSignUpButton != null) {
            mEmailSignUpButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Add error checking here
                    emailValue = emailWidget.getText().toString().trim();
                    /*String passwordValue = passwordWidget.getText().toString().trim();
                    String verifyPasswordValue = verifyPasswordWidget.getText().toString().trim();*/
                    String firstNameValue = firstNameWidget.getText().toString().trim();
                    String lastNameValue = lastNameWidget.getText().toString().trim();
                    final String phoneNoValue = phoneNoWidget.getText().toString().trim();
                    String verifyPhoneNo = verifyUserPhone.getText().toString().trim();
                    //String dateofBirth = edtDob.getText().toString().trim();
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
                    } else if (TextUtils.isEmpty(verifyPhoneNo) || !phoneNoValue.equals(verifyPhoneNo)) {
                        verifyUserPhone.setError(getResources().getString(R.string.doesnotmatcherwithmobileno));
                        focusView = verifyUserPhone;
                        cancel = true;
                    }

                    // Code commented for CR remove_login
                    /*else if (TextUtils.isEmpty(passwordValue)) {
                        passwordWidget.setError(getResources().getString(R.string.error_field_required));
                        focusView = passwordWidget;
                        cancel = true;
                    } else if (TextUtils.isEmpty(verifyPasswordValue) || !passwordValue.equals(verifyPasswordValue)) {
                        verifyPasswordWidget.setError(getResources().getString(R.string.doesnotmatcherwithpass));
                        focusView = verifyPasswordWidget;
                        cancel = true;
                    }*/
                    else if (TextUtils.isEmpty(firstNameValue)) {
                        firstNameWidget.setError(getResources().getString(R.string.error_field_required));
                        focusView = firstNameWidget;
                        cancel = true;
                    } else if (TextUtils.isEmpty(lastNameValue)) {
                        lastNameWidget.setError(getResources().getString(R.string.error_field_required));
                        focusView = lastNameWidget;
                        cancel = true;
                    }
                    // Code commented for CR remove_login
                    /*else if (TextUtils.isEmpty(dateofBirth)) {
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
                    }*/
                    else if (!isEmailValid(emailValue)) {
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
                        // ReqDate = getRevDate(dateofBirth);
                        showProgress(true);
                        if (emailValue.trim().isEmpty()) {
                            emailValue = "";
                            try {
                                response = ApiClient.getClient().create(ApiInterface.class).registrationServiceWithoutEmail(
                                        phoneNoValue.trim(), firstNameValue.trim(), lastNameValue.trim(), ApplicationConstants.decrypt(sessionManager.getPASSWORD()),
                                        "konnect", mSelectedUserType, "", sessionManager.getStateSelection());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {


                            try {
                                response = ApiClient.getClient().create(ApiInterface.class).registrationService(emailValue.trim(),
                                        phoneNoValue.trim(), firstNameValue.trim(), lastNameValue.trim(), ApplicationConstants.decrypt(sessionManager.getPASSWORD()),
                                        "konnect", mSelectedUserType, "", sessionManager.getStateSelection());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }


                        response.enqueue(new Callback<RegstrationResponsePojo>() {
                            @Override
                            public void onResponse(Call<RegstrationResponsePojo> call, retrofit2.Response<RegstrationResponsePojo> response) {

                                showProgress(false);
                                // Toast.makeText(getApplicationContext(), response.code()+"", Toast.LENGTH_SHORT).show();
                                if (response.isSuccessful()) {

                                    //  Toast.makeText(getApplicationContext(),response.body().getSmsVerificationPin()+"",Toast.LENGTH_LONG).show();
                                    // Code commented for CR remove_login
                                    /*Intent otpIntent = new Intent(getApplicationContext(), OTP_VarifyActivity.class);
                                    otpIntent.putExtra("mobile", phoneNoWidget.getText().toString().trim());
                                    startActivity(otpIntent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);*/
                                    //    clearAllFields();
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(UserRegistrationActivity.this);
                                    dialog.setCancelable(false);
                                    dialog.setMessage(getResources().getString(R.string.registeredsuccessful));
                                    dialog.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            showProgress(true);
                                            // Code written for CR remove_login to get Token after registration successful.
                                            try {
                                                new ProNetworkSettup(UserRegistrationActivity.this).tokenAuth(phoneNoValue, new StateInterface() {
                                                    @Override
                                                    public void success(String message) {

                                                        try {
                                                            JSONObject userLoginInfo = new JSONObject(message);
                                                            if (userLoginInfo.has("secure_login_token") && userLoginInfo.getString("secure_login_token") != null && !userLoginInfo.getString("secure_login_token").trim().equalsIgnoreCase("")) {
                                                                // create session

                                                                if (userLoginInfo.getString("user_type") != null &&
                                                                        !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("null")
                                                                        && !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("")) {

                                                                   // finishLogin(message, sessionManager.getStateSelection(), "Token " + userLoginInfo.getString("secure_login_token"));
                                                                    finishLogin(message, sessionManager.getStateSelection(), "" + userLoginInfo.getString("secure_login_token"));

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

                                                        // finishLogin(message, mSession.getStateSelection());

                                                    }

                                                    @Override
                                                    public void failed(String message) {
                                                        showProgress(false);

                                                        DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    dialog.show();

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
                // edtDob.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                cyear = year;
                cdate = dayOfMonth;
                cmonth = monthOfYear;
                //   ReqDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                // edtDob.setError(null);
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


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        /*passwordWidget.setText("");
        verifyPasswordWidget.setText("");*/
        firstNameWidget.setText("");
        lastNameWidget.setText("");
        phoneNoWidget.setText("");
        emailWidget.setText("");
        // edtDob.setText("");
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

    protected void finishLogin(final String userInfo, String stateKey, final String token) {
        //parse the userInfo String


        String URL = BuildConfig.HOST + "/api/v1/surveys/?survey_tag=konnect&state=" + stateKey + "&status=AC&per_page=0";

        new ProNetworkSettup(UserRegistrationActivity.this).getSurveyandQuestionGroup(URL, stateKey, token, new StateInterface() {
            @Override
            public void success(String message) {


                flag = 0;
                final ArrayList<Survey> pojoList = getS_Qids();
                if (pojoList != null && pojoList.size() > 0) {

                    for (int i = 0; i < pojoList.size(); i++) {
                        flag = i;

                        String url = BuildConfig.HOST + "/api/v1/surveys/" + pojoList.get(i).getId() + "/questiongroup/" + pojoList.get(i).getQuestionGroupId() + "/questions/?state=" + sessionManager.getStateSelection() + "&per_page=0";
                        new ProNetworkSettup(UserRegistrationActivity.this).getCommunitySurveyQuestions(url, pojoList.get(i).getQuestionGroupId(), flag, pojoList.size(), token, new StateInterface() {
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

                                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                        Date currentDate = new Date();
                                        String currentTime = formatter.format(currentDate);

                                        sessionManager.createLoginSession(
                                                userLoginInfo.getString("first_name"),
                                                userLoginInfo.getString("id"),
                                                userLoginInfo.getString("secure_login_token"),
                                                userLoginInfo.getString("last_name"),
                                                userLoginInfo.getString("email"),
                                                userLoginInfo.getString("mobile_no"),
                                                userLoginInfo.getString("dob"),
                                                users,currentTime,ApplicationConstants.getExpiryDateAndTime(currentDate));

                                        showProgress(false);

                                        try {
                                            subscribetoTopicsForNotification(sessionManager.getState(), sessionManager.getUserType());
                                        } catch (Exception e) {

                                        }
                                        Intent intent = new Intent(UserRegistrationActivity.this, TempLoading.class);
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
            //   Toast.makeText(getApplicationContext(),state+"-"+state + ":" + RolesUtils.getUserRoleValueForFcmGroup(getApplicationContext(), db, stateUserType),Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //may be topic contains some special symbols
        }
    }
}
