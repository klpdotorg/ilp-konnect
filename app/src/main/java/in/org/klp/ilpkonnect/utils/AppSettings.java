package in.org.klp.ilpkonnect.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.BaseActivity;
import in.org.klp.ilpkonnect.KLPApplication;
import in.org.klp.ilpkonnect.Pojo.LanguagePojo;
import in.org.klp.ilpkonnect.Pojo.StatePojo;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.SurveyTypeActivity;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;

import in.org.klp.ilpkonnect.db.State;

import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;

/**
 * Created by shridhars on 7/24/2017.
 */

public class AppSettings extends BaseActivity {


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

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        btnNext = findViewById(R.id.btnNext);
        sessionManager = new SessionManager(getApplicationContext());
        spnSelectLanguage = findViewById(R.id.spnSelectLanguage);
        spnSelectStae = findViewById(R.id.spnSelectStae);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv1.setText(getResources().getString(R.string.selectState));
        tv2.setText(getResources().getString(R.string.selectLanguage));
        String stateKey = sessionManager.getStateSelection();
        db = ((KLPApplication) getApplicationContext().getApplicationContext()).getDb();
        stateList = new ArrayList<>();
        languageList = new ArrayList<>();

        spnSelectStae.setVisibility(View.INVISIBLE);
        RolesUtils.getUserRoles(getApplicationContext(), db, stateKey);

        if (sessionManager.getState().equalsIgnoreCase("no") || sessionManager.getLanguage().equalsIgnoreCase("no")
                || sessionManager.getLanguageKey().equalsIgnoreCase("no") || sessionManager.getStateKey().equalsIgnoreCase("0")) {
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
                                false);  }

                } else {
                    statelistAdp = new ArrayAdapter(AppSettings.this, R.layout.spinnertextview, new ArrayList());
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
                    if (checkDistrictCount(stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey()) > 0) {
                        String language = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getLanguageEng();
                        String languagekey = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getKey();
                        String state = stateList.get(spnSelectStae.getSelectedItemPosition()).getState();
                        final String stateKeyString = stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey();

                  /*  if (!state.equalsIgnoreCase("odisha")) {*/
                        //  subscribetoTopicsForNotification(state.toString().trim(), sessionManager.getUserType().trim().toUpperCase());
                        sessionManager.setLanguage(state, language, languagekey, stateKeyString);
                        sessionManager.setStateSelection(stateKeyString);

                        KLPApplication.setLanguage(getApplicationContext(), languagekey);
                        sessionManager.setLanguagePosition(spnSelectLanguage.getSelectedItemPosition());

                        Intent intent=new Intent(getApplicationContext(), SurveyTypeActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                            System.exit(0);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }
                        else {
                            finish();
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }
/*
                    } else {
                        showSignupResultDialog(
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.functioalityprogree),
                                getResources().getString(R.string.Ok_));
                    }*/
                    } else {

                        showSignupResultDialog(
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.pleaseLoadDataforSelectedState),
                                getResources().getString(R.string.Ok_));

                    }
                } else{


                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.pleaseSelectsurveyLanguage),
                            getResources().getString(R.string.Ok_));

                }


            }
        });

    }

    private void subscribetoTopicsForNotification(String state, String stateUserType) {

        FirebaseMessaging.getInstance().subscribeToTopic(state);
        FirebaseMessaging.getInstance().subscribeToTopic(state + "-" + RolesUtils.getUserRoleValueForFcmGroup(getApplicationContext(), db, stateUserType));
        // Toast.makeText(getApplicationContext(),state+"-"+,Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void getState(boolean b) {
        Query listStateQuery = Query.select().from(State.TABLE).orderBy(State.STATE.asc());
        SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);

        if (stateCursor.getCount() > 0) {
            // we have surveys in DB, get them
            try {
                while (stateCursor.moveToNext()) {
                    State survey = new State(stateCursor);
                    StatePojo pojo = new StatePojo(survey.getState(), survey.getState(), survey.getStatekey(), survey.getLangKey(),survey.getLangName());
                    stateList.add(pojo);
                }
                // Toast.makeText(getApplicationContext(),stateList.size()+":"+b,Toast.LENGTH_SHORT).show();
            } finally {
                if (stateCursor != null) {
                    stateCursor.close();
                }
            }
            StatePojo pojo = new StatePojo(getString(R.string.selectYourState), getString(R.string.selectYourState), "0","","");
            stateList.add(0, pojo);

            if (stateList != null && stateList.size() > 1) {
                statelistAdp = new ArrayAdapter(this, R.layout.spinnertextview, stateList);
                spnSelectStae.setAdapter(statelistAdp);


                if (b) {
                    for (int i = 0; i < stateList.size(); i++) {
                        if (sessionManager.getStateKey() .equalsIgnoreCase( stateList.get(i).getStateKey())) {
                            spnSelectStae.setSelection(i);
                            getLanguage(stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey(),
                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),

                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),
                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLangKey(),
                                    true);      flag = true;

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








/*

    private void parseBlockDataToDb(Response<GetBlockPojo> response) {
        Log.d("w", response.body().getFeatures().size() + "");
        for (int i = 0; i < response.body().getFeatures().size(); i++) {
            Boundary boundary = new Boundary();
            boundary.setId(response.body().getFeatures().get(i).getId());
            boundary.setParentId(response.body().getFeatures().get(i).getParent().getId());
            boundary.setName(response.body().getFeatures().get(i).getName());
            boundary.setHierarchy(response.body().getFeatures().get(i).getType().toLowerCase());
            boundary.setType(response.body().getFeatures().get(i).getSchoolType());
            try {
                db.insertNew(boundary);
            } catch (Exception e) {
                Log.d("w", i + "Exce");
            }

            Log.d("w", i + "");
        }
        if (response.body().getNext() != null) {
            DownloadBlocksData(response.body().getNext());
        }


    }
*/

   /* private void parseClusterDataToDb(Response<GetClusterPojo> response) {

        for (int i = 0; i < response.body().getFeatures().size(); i++) {
            Boundary boundary = new Boundary();
            boundary.setId(response.body().getFeatures().get(i).getId());
            boundary.setParentId(response.body().getFeatures().get(i).getParent().getId());
            boundary.setName(response.body().getFeatures().get(i).getName());
            boundary.setHierarchy(response.body().getFeatures().get(i).getType().toLowerCase());
            boundary.setType(response.body().getFeatures().get(i).getSchoolType());

            try {
               Boolean b= db.insertNew(boundary);
                Log.d("Sreee",b+":"+response.body().getFeatures().get(i).getId()+"-"+response.body().getFeatures().get(i).getName()) ;
            } catch (Exception e) {
                Log.d("Sreee", i + "Exception" + response.body().getFeatures().get(i).getId() + ":" + response.body().getFeatures().get(i).getType().toLowerCase());
            }
            // Log.d("w", i + "");
        }
        if (response.body().getNext() != null) {
            DownloadClusterData(response.body().getNext());
            //Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
           Log.d("Sreee","------------------------NEXT----");
        }else
        {
            Log.d("Sreee","------------------------FINISH----");
        }


    }
*/


    public int checkDistrictCount(String stateKey) {

        Query listStoryQuery = Query.select()
                .from(Boundary.TABLE)
                .where(Boundary.STATE_KEY.eqCaseInsensitive(stateKey)
                );

        SquidCursor<Boundary> storyByUserNSCursor = db.query(Boundary.class, listStoryQuery);
        if (storyByUserNSCursor != null)

            return storyByUserNSCursor.getCount();
        return 0;

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
