package in.org.klp.ilpkonnect;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yahoo.squidb.data.SquidCursor;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.constants.ApplicationConstants;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 12/29/2017.
 */

public class UpdateProfileActivity extends BaseActivity {

    //ImageView calBtn;
    ProgressDialog progressDialog;
    EditText edtFirstName, edtLastName, edtEmail;
    //EditText edtDob;
    int cyear, cmonth, cdate;
    SquidCursor<Respondent> respondentCursor = null;
    private LinkedHashMap<String, String> userType;
    private KontactDatabase db;
    SessionManager sessionManager;
    String emailValue;
    Button btnUpdate;
    private Spinner spnRespondantType;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        this.setTitle(getResources().getString(R.string.updateProfile));

        // Code commented for CR remove_login
        //calBtn = findViewById(R.id.calBtn);
        //edtDob = findViewById(R.id.edtDob);
        edtFirstName = findViewById(R.id.edtFirstName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        spnRespondantType = findViewById(R.id.spnRespondantType);
        sessionManager = new SessionManager(getApplicationContext());
        String stateKey = sessionManager.getStateSelection();
        btnUpdate = findViewById(R.id.btnUpdate);
        edtFirstName.setText(sessionManager.getFirstName());
        edtLastName.setText(sessionManager.getLastName());
        edtEmail.setText(sessionManager.getEmail() != null && !sessionManager.getEmail().equalsIgnoreCase("null") ? sessionManager.getEmail() : "");
        //edtDob.setText(getDDMMYYdate(sessionManager.getDOB()));
        cyear = 1988;
        cdate = 1;
        cmonth = 0;


        spnRespondantType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(UpdateProfileActivity.this);
                return false;
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validation();
            }
        });

        // Code commented for CR remove_login
      /*  calBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setdate(null, cyear, cmonth, cdate);
            }
        });*/
        db = ((KLPApplication) getApplicationContext()).getDb();
      /*  Query listQGQquery = Query.select().from(Respondent.TABLE)
                .orderBy(Respondent.NAME.asc());
        respondentCursor = db.query(Respondent.class, listQGQquery);
        if (respondentCursor != null) {
            userType = new LinkedHashMap<String, String>();
           *//* userType.put(getResources().getString(R.string.pleaseSelectrespondanttype), "No");*//*
            while (respondentCursor.moveToNext()) {
                userType.put(respondentCursor.get(Respondent.NAME).toUpperCase(), respondentCursor.get(Respondent.KEY).toUpperCase());

            }

        }*/
        userType = new LinkedHashMap<String, String>();
        userType.putAll(RolesUtils.getUserRoles(getApplicationContext(), db, stateKey));

        if (userType != null && userType.size() > 0) {
            List<String> userTypeNames = new ArrayList<>();
            userTypeNames.addAll(userType.keySet());
            ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(UpdateProfileActivity.this, R.layout.regspinner, userTypeNames);
            spnRespondantType.setAdapter(userTypeAdapter);

            for (Map.Entry<String, String> mapData : userType.entrySet()) {
                if (mapData.getValue().toUpperCase().equalsIgnoreCase(sessionManager.getUserType().toUpperCase())) {
                    String key = mapData.getKey();
                    int position = userTypeNames.indexOf(key);
                    spnRespondantType.setSelection(position);
                }

            }

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
                edtEmail.setError(getResources().getString(R.string.enter_valid_email));
                return false;
                //Please provide valid Email or phone number

            }
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void setdate(DatePicker view, int y, int m, int d) {
        DatePickerDialog dpd;

        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //edtDob.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
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


    public void validation() {
        View focusView = null;
        boolean cancel = false;
        //String dateofBirth = edtDob.getText().toString().trim();

        if (TextUtils.isEmpty(edtFirstName.getText().toString().trim())) {
            edtFirstName.setError(getResources().getString(R.string.error_field_required));
            focusView = edtFirstName;
            cancel = true;
        } else if (TextUtils.isEmpty(edtLastName.getText().toString().trim())) {
            edtLastName.setError(getResources().getString(R.string.error_field_required));
            focusView = edtLastName;
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
        } */
        else if (!isEmailValid(edtEmail.getText().toString().trim())) {
            //emailWidget.setError("This email address is invalid");
            focusView = edtEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {

            if (AppStatus.isConnected(getApplicationContext())) {
                if (!ApplicationConstants.isSyncing) {
                    showProgress(true);

                    // Code written for CR remove_login to call Token Auth API for background verification
                    // check whether token is valid or expired
                    if (sessionManager.getEXPIRY_DATETIME() != "" && sessionManager.getEXPIRY_DATETIME() != null && !sessionManager.getEXPIRY_DATETIME().isEmpty()) {

                        Date currentDateTime = new Date();
                        String tokenExpiryDate = sessionManager.getEXPIRY_DATETIME();

                        Date expiryDateFormat = null;
                        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                        try {
                            expiryDateFormat = sdf.parse(tokenExpiryDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (currentDateTime.before(expiryDateFormat)) {
                            saveUpdateProfile();
                        } else {
                            getNewToken();
                        }

                    } else {
                        getNewToken();

                    }
                } else {
                    DailogUtill.showDialog(getString(R.string.surveySyncInProgress), getSupportFragmentManager(), getApplicationContext());

                }
            } else {
                DailogUtill.showDialog(getString(R.string.netWorkError), getSupportFragmentManager(), getApplicationContext());
            }


        }
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

    public String getRevDate(String strDate) {
        String newstring = "1980-01-01";
        try {
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);


            newstring = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        } catch (Exception e) {

        }

        return newstring;
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

    public String getDDMMYYdate(String date) {

        String newstring = "01-01-1998";
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);


            newstring = new SimpleDateFormat("dd-MM-yyyy").format(date1);
        } catch (Exception e) {

        }

        return newstring;
    }


    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.updating_profile));
            progressDialog.show();
            progressDialog.setCancelable(false);
        } else {
            if (progressDialog != null) progressDialog.cancel();
        }
    }


    private void saveUpdateProfile() {
        new ProNetworkSettup(getApplicationContext()).setProfileUpdateAction(edtFirstName.getText().toString().trim(),
                edtLastName.getText().toString().trim(), edtEmail.getText().toString().trim(), "",
                userType.get(spnRespondantType.getSelectedItem().toString()), sessionManager.getToken(), sessionManager.getStateSelection(), new StateInterface() {
                    @Override
                    public void success(String message) {
                        showProgress(false);
                        //showSignupResultDialog(getString(R.string.app_name),,getResources().getString(R.string.Ok));

                        DailogUtill.showDialog(getResources().getString(R.string.profileUpdatedSuccessfully), getSupportFragmentManager(), getApplicationContext());

                        try {
                            subscribetoTopicsForNotification(sessionManager.getState(), sessionManager.getUserType());
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void failed(String message) {
                        showProgress(false);
                        //showSignupResultDialog(getString(R.string.app_name),message,getResources().getString(R.string.Ok));
                        DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                    }
                });
    }

    private void getNewToken() {
        try {
            new ProNetworkSettup(UpdateProfileActivity.this).tokenAuth(sessionManager.getMobile(), new StateInterface() {
                @Override
                public void success(String message) {

                    try {
                        JSONObject userLoginInfo = new JSONObject(message);
                        if (userLoginInfo.has("secure_login_token") && userLoginInfo.getString("secure_login_token") != null && !userLoginInfo.getString("secure_login_token").trim().equalsIgnoreCase("")) {
                            sessionManager.setKEY_TOKEN(userLoginInfo.getString("secure_login_token"));

                            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            Date currentDate = new Date();
                            String currentDateTime = formatter.format(currentDate);

                            sessionManager.setCURRENT_DATETIME(currentDateTime);
                            sessionManager.setEXPIRY_DATETIME(ApplicationConstants.getExpiryDateAndTime(currentDate));
                            saveUpdateProfile();

                        } else
                            showProgress(false);

                    } catch (Exception e) {
                        showProgress(false);
                    }

                }

                @Override
                public void failed(String message) {
                    showProgress(false);
                    DailogUtill.showDialog(getString(R.string.authfailed), getSupportFragmentManager(), getApplicationContext());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
