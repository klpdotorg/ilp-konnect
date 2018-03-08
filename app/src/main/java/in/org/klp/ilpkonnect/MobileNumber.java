package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONObject;

import java.io.IOException;

import in.org.klp.ilpkonnect.InterfacesPack.UserRolesInterface;
import in.org.klp.ilpkonnect.Pojo.LoginMobilePojo;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.db.DatabaseCopyHelper;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.AppSettings;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.ILPService;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shridhars on 11/7/2017.
 */

public class MobileNumber extends AppCompatActivity {


    EditText mobilenumber;
    Button buttonNext;
    ProgressDialog progressDialog;
    private SessionManager mSession;
    KontactDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_number);
        mobilenumber = findViewById(R.id.mobilenumber);
        buttonNext = findViewById(R.id.buttonNext);
        progressDialog = new ProgressDialog(MobileNumber.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        mSession = new SessionManager(getApplicationContext());
        DatabaseCopyHelper dbCopyHelper = new DatabaseCopyHelper(this);
        SQLiteDatabase dbCopy = dbCopyHelper.getReadableDatabase();
        db = ((KLPApplication) getApplicationContext()).getDb();
        if (mSession.isLoggedIn()) {
            //  Toast.makeText(getApplicationContext(),"isLogin",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MobileNumber.this, AppSettings.class);
            startActivity(intent);
            finish();
        }


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

     /*           if (checkMobile(mobilenumber.getText().toString().trim())) {
                    progressDialog.show();
                    ApiClient.getClient().create(ApiInterface.class).loginMobile(mobilenumber.getText().toString().trim()).enqueue(new Callback<LoginMobilePojo>() {
                        @Override
                        public void onResponse(Call<LoginMobilePojo> call, Response<LoginMobilePojo> response) {


                            if (response.isSuccessful()) {
*//*
                                if (response.code() == 200 && response.body().getAction().equalsIgnoreCase("login")) {
                                    //login screen
                                    new ProNetworkSettup(MobileNumber.this).getRespondentList(ILPService.RESPONDENTLIST, new UserRolesInterface() {
                                                @Override
                                                public void success(String message) {
                                                    finishProgress();
                                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                                    i.putExtra("mobile", mobilenumber.getText().toString().trim());
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(i);
                                                    mobilenumber.setText("");
                                                    finish();
                                                }

                                                @Override
                                                public void failed(String message) {
                                                    finishProgress();
                                                    showSignupResultDialog(getResources().getString(R.string.app_name), "Some thing went wrong,Please try again", "OK");
                                                }

                                            }
                                    );


                                }
                                if (response.code() == 206 && response.body().getAction().equalsIgnoreCase("update")) {
                                    //update profile

                                    new ProNetworkSettup(MobileNumber.this).getRespondentList(ILPService.RESPONDENTLIST, new UserRolesInterface() {
                                                @Override
                                                public void success(String message) {
                                                    finishProgress();
                                                    Intent i = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                                                    i.putExtra("mobile", mobilenumber.getText().toString().trim());
                                                    i.putExtra("update", true);
                                                    startActivity(i);
                                                    mobilenumber.setText("");
                                                    // finish();
                                                }

                                                @Override
                                                public void failed(String message) {
                                                    finishProgress();
                                                    showSignupResultDialog(getResources().getString(R.string.app_name), "Some thing went wrong,Please try again", "OK");
                                                }

                                            }
                                    );


                                }
*//*

                            } else {

                                String data = null;
                                try {
                                    data = response.errorBody().string();
                                    data = new JSONObject(data).get("action").toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 404 && data != null && data.equalsIgnoreCase("signup")) {
                                    //registration screem


                         *//*           new ProNetworkSettup(MobileNumber.this).getRespondentList(ILPService.RESPONDENTLIST, new UserRolesInterface() {
                                                @Override
                                                public void success(String message) {
                                                    finishProgress();
                                                    Intent i = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                                                    i.putExtra("mobile", mobilenumber.getText().toString().trim());
                                                    i.putExtra("update", false);
                                                    startActivity(i);
                                                    mobilenumber.setText("");
                                                    // finish();
                                                }

                                                @Override
                                                public void failed(String message) {
                                                    finishProgress();
                                                    showSignupResultDialog(getResources().getString(R.string.app_name), "Some thing went wrong,Please try again", "OK");
                                                }

                                            }
                                    );
*//*

                                    //finish();
                                } else {
                                    showSignupResultDialog(getResources().getString(R.string.app_name), "Some thing went wrong,Please try again", "OK");
                                }


                            }
                        }

                        @Override
                        public void onFailure(Call<LoginMobilePojo> call, Throwable t) {
                            //     Toast.makeText(getApplicationContext(),t.getMessage()+"000",Toast.LENGTH_SHORT).show();
                            finishProgress();
                            if (t instanceof IOException) {
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),
                                        getResources().getString(R.string.failed) + "-" + getResources().getString(R.string.noInternetCon),
                                        getResources().getString(R.string.Ok));
                            } else {
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),
                                        getResources().getString(R.string.failed) + "-" + getResources().getString(R.string.netWorkError),
                                        getResources().getString(R.string.Ok));
                            }
                        }
                    });

                }*/

            }
        });
    }






    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public boolean checkMobile(String number) {
        if (TextUtils.isDigitsOnly(number)) {
            if (number.length() == 10) {
                //Toast.makeText(getApplicationContext(), "its Digit", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                mobilenumber.setError(getResources().getString(R.string.enter_ten_digit_number));
                return false;
            }
        }
        mobilenumber.setError(getResources().getString(R.string.enter_ten_digit_number));
        return false;
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

    public void getUserCount() {
        Query listStoryQuery = Query.select().from(Respondent.TABLE);
        SquidCursor<Respondent> storiesCursor = db.query(Respondent.class, listStoryQuery);
        if (storiesCursor.getCount() <= 0) {
            if (AppStatus.isConnected(getApplicationContext())) {
           //     new ProNetworkSettup(MobileNumber.this).getRespondentList(ILPService.RESPONDENTLIST);
            }
        }
    }

    private void finishProgress()
    {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
