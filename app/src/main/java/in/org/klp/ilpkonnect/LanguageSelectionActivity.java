package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.InterfacesPack.UserRolesInterface;
import in.org.klp.ilpkonnect.Pojo.LanguagePojo;
import in.org.klp.ilpkonnect.Pojo.StatePojo;
import in.org.klp.ilpkonnect.db.KontactDatabase;

import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.db.State;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.ILPService;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.ProgressUtil;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 2/2/2018.
 */

public class LanguageSelectionActivity extends BaseActivity {


    Button btnOK, btnNext;
    Spinner spnSelectStae, spnSelectLanguage;
    SessionManager sessionManager;

    private KontactDatabase db;
    private ArrayList<StatePojo> stateList;
    private ArrayList<LanguagePojo> languageList;
    ArrayAdapter statelistAdp;
    boolean flag = false;
    TextView tv1, tv2;
    boolean qFlag = false;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_setting_layout);
        btnNext = findViewById(R.id.btnNext);
        sessionManager = new SessionManager(getApplicationContext());
        spnSelectLanguage = findViewById(R.id.spnSelectLanguage);
        spnSelectStae = findViewById(R.id.spnSelectStae);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv1.setText(getResources().getString(R.string.selectStateForLanguage));
        tv2.setText(getResources().getString(R.string.selectLanguageForLanguage));
        progressDialog = ProgressUtil.showProgress(LanguageSelectionActivity.this, getResources().getString(R.string.authenticating));
        db = ((KLPApplication) getApplicationContext().getApplicationContext()).getDb();
        stateList = new ArrayList<>();
        languageList = new ArrayList<>();


        if (sessionManager.getState().equalsIgnoreCase("no") || sessionManager.getLanguage().equalsIgnoreCase("no")
                || sessionManager.getLanguageKey().equalsIgnoreCase("no") || sessionManager.getStateKey().equalsIgnoreCase("")) {
            getState(false);
        } else {
            getState(true);


        }


        spnSelectStae.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {

                if (position != 0) {

                    if (flag == true) {
                        flag = false;
                    } else {
                        // Toast.makeText(getApplicationContext(),stateList.size()+"",Toast.LENGTH_SHORT).show();
                        getLanguage(stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey(),
                                stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),

                                stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),
                                stateList.get(spnSelectStae.getSelectedItemPosition()).getLangKey(),
                                false);
                    }

                } else {
                    statelistAdp = new ArrayAdapter(LanguageSelectionActivity.this, R.layout.spinnertextview, new ArrayList());
                    spnSelectLanguage.setAdapter(statelistAdp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        btnOK = findViewById(R.id.btnOK);


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (spnSelectLanguage.getSelectedItemPosition() > 0)

                {
                    final String language = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getLanguageEng();
                    final String languagekey = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getKey();
                    final String state = stateList.get(spnSelectStae.getSelectedItemPosition()).getState();
                    final String stateKeyString = stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey();
                    /*if (!state.equalsIgnoreCase("odisha")) {*/

                      /*  progressDialog.show();
                        new ProNetworkSettup(LanguageSelectionActivity.this).getRespondentList(ILPService.RESPONDENTLIST, new UserRolesInterface() {
                            @Override
                            public void success(String message) {*/

                    String url = BuildConfig.HOST + "/api/v1/boundary/admin1s/?per_page=0&state=" + stateKeyString;
                    progressDialog.show();

                    new ProNetworkSettup(LanguageSelectionActivity.this).downloadStateData(url, stateKeyString, new StateInterface() {
                        @Override
                        public void success(String message) {


                            progressDialog.dismiss();
                            sessionManager.setLanguage(state, language, languagekey, stateKeyString);
                            sessionManager.setStateSelection(stateKeyString);
                            sessionManager.setStatePosition(spnSelectStae.getSelectedItemPosition());

                            // Toast.makeText(getApplicationContext(),stateKey+":"+languagekey,Toast.LENGTH_SHORT).show();
                            KLPApplication.setLanguage(getApplicationContext(), languagekey);
                            sessionManager.setLanguagePosition(spnSelectLanguage.getSelectedItemPosition());
                            Intent intent = new Intent(new Intent(getApplicationContext(), LoginActivity.class));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                System.exit(0);
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


                            }

                            else{
                                finish();
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }

                        }

                        @Override
                        public void failed(String message) {
                            progressDialog.dismiss();
                            showSignupResultDialog(getResources().getString(R.string.app_name), message, getResources().getString(R.string.Ok));
                        }
                    });





/*
                            }

                            @Override
                            public void failed(String message) {
                                progressDialog.dismiss();
                                showSignupResultDialog(getResources().getString(R.string.app_name), message, "OK");
                            }

                        });*/


                /*    } else {
                        showSignupResultDialog(
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.functioalityprogree),
                                getResources().getString(R.string.Ok_));
                    }*/
                } else {


                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.pleaseSelectsurveyLanguage),
                            getResources().getString(R.string.Ok_));

                }


            }
        });

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

       /* this.finish();
        System.exit(0);*/
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            try {


                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                super.onBackPressed();
                startActivity(a);
                return;
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    public void getState(boolean b) {
        Query listStateQuery = Query.select().from(State.TABLE).orderBy(State.STATE.asc());
        SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);

        if (stateCursor.getCount() > 0) {
            // we have surveys in DB, get them
            try {
                while (stateCursor.moveToNext()) {
                    State state = new State(stateCursor);
                    StatePojo pojo = new StatePojo(state.getState(), state.getStateLocText(), state.getStatekey(), state.getLangKey(), state.getLangName());
                    stateList.add(pojo);
                }
                // Toast.makeText(getApplicationContext(),stateList.size()+":"+b,Toast.LENGTH_SHORT).show();
            } finally {
                if (stateCursor != null) {
                    stateCursor.close();
                }
            }
            StatePojo pojo = new StatePojo(getString(R.string.selectYourState), getString(R.string.selectYourState), "0", "", "");
            stateList.add(0, pojo);

            if (stateList != null && stateList.size() > 1) {
                statelistAdp = new ArrayAdapter(this, R.layout.spinnertextview, stateList);
                spnSelectStae.setAdapter(statelistAdp);


                if (b) {
                    for (int i = 0; i < stateList.size(); i++) {
                        if (sessionManager.getStateKey().equalsIgnoreCase(stateList.get(i).getStateKey())) {
                            spnSelectStae.setSelection(i);
                            getLanguage(stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey(),
                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),

                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),
                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLangKey(),
                                    true);
                            flag = true;

                            //   Toast.makeText(getApplicationContext(),stateList.get(i).getState(),Toast.LENGTH_SHORT).show();
                            break;
                        }


                    }


                }
            }

        }

    }

    public void getLanguage(String stateKey, String langName, String langLocName, String langKey, boolean b) {

        languageList.clear();

        LanguagePojo pojo = new LanguagePojo(stateKey, langName, langLocName, langKey);
        languageList.add(pojo);


        pojo = new LanguagePojo("", getResources().getString(R.string.selectYourLanguage), getResources().getString(R.string.selectYourLanguage), "en");
        languageList.add(0, pojo);
        pojo = new LanguagePojo("", getResources().getString(R.string.english), getResources().getString(R.string.english), "en");
        languageList.add(1, pojo);
        spnSelectLanguage.setAdapter(new ArrayAdapter(this, R.layout.spinnertextview, languageList));

        if (b)

        {


            for (int i = 1; i < languageList.size(); i++) {

                if (sessionManager.getLanguageKey().equalsIgnoreCase(languageList.get(i).getKey())) {

                    // Toast.makeText(getApplicationContext(), i + "mm", Toast.LENGTH_SHORT).show();
                    spnSelectLanguage.setSelection(i);
                    KLPApplication.setLanguage(getApplicationContext(), sessionManager.getLanguageKey());
                    btnNext.setEnabled(true);
                    break;
                }
            }
        }


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


}