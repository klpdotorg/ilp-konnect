package in.org.klp.ilpkonnect.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import in.org.klp.ilpkonnect.BlocksPojo.GetBlockPojo;
import in.org.klp.ilpkonnect.ClusterPojos.GetClusterPojo;
import in.org.klp.ilpkonnect.DistrictPojos.GetDistrictPojo;
import in.org.klp.ilpkonnect.KLPApplication;
import in.org.klp.ilpkonnect.Pojo.LanguagePojo;
import in.org.klp.ilpkonnect.Pojo.StatePojo;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.SchoolsPojos.GetSchoolsPojo;
import in.org.klp.ilpkonnect.SurveyTypeActivity;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Language;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.State;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shridhars on 7/24/2017.
 */

public class AppSettings extends AppCompatActivity {


    Button btnOK, btnNext;
    Spinner spnSelectStae, spnSelectLanguage;
    SessionManager sessionManager;
    LinkedHashMap<String, String> userType;
    private KontactDatabase db;
    private ArrayList<StatePojo> stateList;
    private ArrayList<LanguagePojo> languageList;
    ArrayAdapter statelistAdp;
    boolean flag = false;
    TextView tv1, tv2;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        btnNext = (Button) findViewById(R.id.btnNext);
        sessionManager = new SessionManager(getApplicationContext());
        spnSelectLanguage = (Spinner) findViewById(R.id.spnSelectLanguage);
        spnSelectStae = (Spinner) findViewById(R.id.spnSelectStae);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv1.setText(getResources().getString(R.string.selectState));
        tv2.setText(getResources().getString(R.string.selectLanguage));
        setetUSer();
        db = ((KLPApplication) getApplicationContext().getApplicationContext()).getDb();
        stateList = new ArrayList<>();
        languageList = new ArrayList<>();


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getLanguageKey().equalsIgnoreCase("no")) {
                    DialogConstants dialogConstants = new DialogConstants(AppSettings.this, "Please select your STATE & LANGUAGE and press OK");
                    dialogConstants.show();
                } else {
                    startActivity(new Intent(getApplicationContext(), SurveyTypeActivity.class));
                    //finish();
                }
            }
        });
        if (sessionManager.getState().equalsIgnoreCase("no") || sessionManager.getLanguage().equalsIgnoreCase("no")
                || sessionManager.getLanguageKey().equalsIgnoreCase("no") || sessionManager.getStateKey() == 0) {
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
                        getLanguage(stateList.get(spnSelectStae.getSelectedItemPosition()).get_id(), false);
                    }

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


        btnOK = (Button) findViewById(R.id.btnOK);


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (spnSelectLanguage.getSelectedItemPosition() > 0)

                {
                    String language = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getLanguageEng();
                    String languagekey = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getKey();
                    String state = stateList.get(spnSelectStae.getSelectedItemPosition()).getState();
                    long stateKey = stateList.get(spnSelectStae.getSelectedItemPosition()).get_id();
                    if (!state.equalsIgnoreCase("odisha")) {
                        subscribetoTopicsForNotification(state.toString().trim(), sessionManager.getUserType().trim().toUpperCase());
                        sessionManager.setLanguage(state, language, languagekey, stateKey);


                        KLPApplication.setLanguage(getApplicationContext(), languagekey);
                        Constants.Lang = spnSelectLanguage.getSelectedItemPosition();
                        startActivity(new Intent(getApplicationContext(), SurveyTypeActivity.class));
                        overridePendingTransition(0, 0);

                    } else {
                        showSignupResultDialog(
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.functioalityprogree),
                                getResources().getString(R.string.Ok_));
                    }
                } else {


                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.pleaseSelectsurveyLanguage),
                            getResources().getString(R.string.Ok_));

                }

                //  DownloadStateData();
                // DownloadBlocksData(ILPService.BLOCKS);
                // DownloadClusterData(ILPService.CLUSTER);
                // DownloadSchoolData(ILPService.SCHOOLS);


            }
        });

    }

    private void subscribetoTopicsForNotification(String state, String stateUserType) {

        FirebaseMessaging.getInstance().subscribeToTopic(state);
        FirebaseMessaging.getInstance().subscribeToTopic(state + "-" + getUserType(stateUserType));
        // Toast.makeText(getApplicationContext(),state+"-"+getUserType(stateUserType),Toast.LENGTH_SHORT).show();
    }


    public void DownloadSchoolData(String url) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        apiInterface.getAllSchoolsData(url).enqueue(new Callback<GetSchoolsPojo>() {
            @Override
            public void onResponse(Call<GetSchoolsPojo> call, Response<GetSchoolsPojo> response) {

                if (response.isSuccessful()) {
                    parseSchoolData(response);
                } else {

                    Toast.makeText(getApplicationContext(), "Ex", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<GetSchoolsPojo> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "ExNet", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void parseSchoolData(Response<GetSchoolsPojo> response) {

        for (int i = 0; i < response.body().getFeatures().size(); i++) {

            Log.d("sw", i + "");
            School schol = new School();
            schol.setId(response.body().getFeatures().get(i).getProperties().getId());
            schol.setName(response.body().getFeatures().get(i).getProperties().getName());
            schol.setBoundaryId(response.body().getFeatures().get(i).getProperties().getBoundary().getId());

            try {
                schol.setLat(response.body().getFeatures().get(i).getGeometry().getCoordinates().get(0));
                schol.setLng(response.body().getFeatures().get(i).getGeometry().getCoordinates().get(1));
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(),response.body().getFeatures().get(i).getProperties().getId()+"",Toast.LENGTH_SHORT).show();
                schol.setLat(0d);
                schol.setLng(0d);
                Log.d("ids", response.body().getFeatures().get(i).getProperties().getId() + "");

            }

            try {
                db.insertNew(schol);
            } catch (Exception e) {
                Log.d("sw", "--Exception--" + e.getMessage());
            }
        }
        if (response.body().getNext() != null) {
            DownloadSchoolData(response.body().getNext());
            Log.d("sw", "--NEXT--");
        } else {
            Log.d("sw", "--FINISH--");
        }


    }


    private void DownloadClusterData(String cluster) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getAllClusterData(cluster).enqueue(new Callback<GetClusterPojo>() {
            @Override
            public void onResponse(Call<GetClusterPojo> call, Response<GetClusterPojo> response) {

                if (response.isSuccessful()) {
                    parseClusterDataToDb(response);
                } else {

                    Toast.makeText(getApplicationContext(), "Ex", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetClusterPojo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ex1", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void DownloadBlocksData(String url) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getAllBlocksData(url).enqueue(new Callback<GetBlockPojo>() {
            @Override
            public void onResponse(Call<GetBlockPojo> call, Response<GetBlockPojo> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_SHORT).show();
                    parseBlockDataToDb(response);
                } else {

                    Toast.makeText(getApplicationContext(), "Ex", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetBlockPojo> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ex1", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser() {
        KLPApplication.setLanguage(getApplicationContext(), "en");
        sessionManager.logoutUser();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
        System.exit(0);
    }

    public void getState(boolean b) {

        Query listStateQuery = Query.select().from(State.TABLE);
        SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);

        if (db.countAll(Survey.class) > 0) {
            // we have surveys in DB, get them
            try {
                while (stateCursor.moveToNext()) {
                    State survey = new State(stateCursor);
                    StatePojo pojo = new StatePojo(survey.getId(), survey.getState(), survey.getStateLocText());
                    stateList.add(pojo);
                }
            } finally {
                if (stateCursor != null) {
                    stateCursor.close();
                }
            }
            StatePojo pojo = new StatePojo(0, getString(R.string.selectYourState), getString(R.string.selectYourState));
            stateList.add(0, pojo);

            if (stateList != null && stateList.size() > 1) {
                statelistAdp = new ArrayAdapter(this, R.layout.spinnertextview, stateList);
                spnSelectStae.setAdapter(statelistAdp);


                if (b) {
                    for (int i = 0; i < stateList.size(); i++) {
                        if (sessionManager.getStateKey() == stateList.get(i).get_id()) {
                            spnSelectStae.setSelection(i);
                            getLanguage(sessionManager.getStateKey(), true);
                            flag = true;

                            //   Toast.makeText(getApplicationContext(),stateList.get(i).getState(),Toast.LENGTH_SHORT).show();
                            break;
                        }


                    }


                }
            }

        }

    }

    public void getLanguage(long id, boolean b) {

        languageList.clear();
        Query listLanguageQuery = Query.select().from(Language.TABLE)
                .where(Language.STATE_ID.eq(id));

        SquidCursor<Language> LanguageCursor = db.query(Language.class, listLanguageQuery);

        if (db.countAll(Language.class) > 0) {
            // we have surveys in DB, get them
            try {
                while (LanguageCursor.moveToNext()) {
                    Language language = new Language(LanguageCursor);
                    LanguagePojo pojo = new LanguagePojo(language.getStateId(), language.getLanguageENG(), language.getLanguageLoc(), language.getLangKey());
                    languageList.add(pojo);
                }
            } finally {
                if (LanguageCursor != null) {
                    LanguageCursor.close();
                }
            }
            LanguagePojo pojo;
            pojo = new LanguagePojo(0, getResources().getString(R.string.selectYourLanguage), getResources().getString(R.string.selectYourLanguage), "en");
            languageList.add(0, pojo);
            pojo = new LanguagePojo(1, getResources().getString(R.string.english), getResources().getString(R.string.english), "en");
            languageList.add(1, pojo);
            spnSelectLanguage.setAdapter(new ArrayAdapter(this, R.layout.spinnertextview, languageList));
        }
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


    public void DownloadStateData() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        apiInterface.getAllDistrictData().enqueue(new Callback<GetDistrictPojo>() {
            @Override
            public void onResponse(Call<GetDistrictPojo> call, Response<GetDistrictPojo> response) {

                if (response.isSuccessful()) {
                    AddDataToDistrcit(response);

                } else {

                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetDistrictPojo> call, Throwable t) {
                Log.d("data1", "S2");
                Toast.makeText(getApplicationContext(), "Error1", Toast.LENGTH_SHORT).show();
            }
        });

        /*apiInterface.getAllBlocksData().execute();
        Log.d("data1","S2");
        apiInterface.getAllClusterData().execute();
        Log.d("data1","S3");
        apiInterface.getAllSchoolsData().execute();
        Log.d("data1","S4");*/


    }


    public void AddDataToDistrcit(Response<GetDistrictPojo> response) {
        for (int i = 0; i < response.body().getFeatures().size(); i++) {
            Log.d("sw", i + "");
            Boundary boundary = new Boundary();
            boundary.setId(response.body().getFeatures().get(i).getId());
            boundary.setParentId(1l);
            boundary.setName(response.body().getFeatures().get(i).getName());
            boundary.setHierarchy(response.body().getFeatures().get(i).getType().toLowerCase());
            boundary.setType(response.body().getFeatures().get(i).getSchoolType());
            db.insertNew(boundary);
        }

    }


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

    private void parseClusterDataToDb(Response<GetClusterPojo> response) {
        Log.d("w", response.body().getFeatures().size() + "");
        for (int i = 0; i < response.body().getFeatures().size(); i++) {
            Boundary boundary = new Boundary();
            boundary.setId(response.body().getFeatures().get(i).getId());
            boundary.setParentId(response.body().getFeatures().get(i).getParent().getId());
            boundary.setName(response.body().getFeatures().get(i).getName());
            boundary.setHierarchy(response.body().getFeatures().get(i).getType().toLowerCase());
            boundary.setType(response.body().getFeatures().get(i).getSchoolType());
            Log.d("w", i + "Exception" + response.body().getFeatures().get(i).getId() + ":" + response.body().getFeatures().get(i).getType().toLowerCase());

            try {
                db.insertNew(boundary);
            } catch (Exception e) {
                Log.d("w", i + "Exception" + response.body().getFeatures().get(i).getId() + ":" + response.body().getFeatures().get(i).getType().toLowerCase());
            }
            // Log.d("w", i + "");
        }
        if (response.body().getNext() != null) {
            DownloadClusterData(response.body().getNext());
            Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
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


    public void setetUSer() {
        userType = new LinkedHashMap<String, String>();
        userType.put("PR", "Parents");
        userType.put("TR", "Teachers");
        userType.put("VR", "EducationVolunteers");
        userType.put("CM", "CBOMember");
        userType.put("HM", "Headmaster");
        userType.put("SM", "SDMCMember");
        userType.put("LL", "LocalLeaders");
        userType.put("AS", "AksharaStaff");
        userType.put("EY", "EducatedYouth");
        userType.put("GO", "GovtOfficial");
        userType.put("EO", "EducationOfficial");
        userType.put("ER", "ElectedRepresentative");
    }

    public String getUserType(String key) {

        return userType.get(key);
    }


}
