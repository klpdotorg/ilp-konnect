package in.org.klp.ilpkonnect;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.Pojo.SurveyMain;

import in.org.klp.ilpkonnect.adapters.SurveyAdapter;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.utils.AppSettings;
import in.org.klp.ilpkonnect.utils.Constants;

/**
 * Created by shridhars on 8/1/2017.
 */

public class SurveyTypeActivity extends AppCompatActivity {

    Button btnComminitySurvey, btnGkMonitoring;
    Spinner spnSurveyType;
    private SurveyAdapter mSurveyAdapter;
    private KontactDatabase db;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_type);
        this.setTitle(getResources().getString(R.string.surveyType));

        btnComminitySurvey = (Button) findViewById(R.id.btnComminitySurvey);
        spnSurveyType = (Spinner) findViewById(R.id.spnSurveyType);
        btnGkMonitoring = (Button) findViewById(R.id.btnGkMonitoring);
        db = ((KLPApplication) getApplicationContext().getApplicationContext()).getDb();
        mSurveyAdapter = new SurveyAdapter(
                new ArrayList<SurveyMain>(),
                getApplicationContext());


        Drawable community,gka;

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            community = VectorDrawableCompat.create(getResources(), R.drawable.communityicon, getApplicationContext().getTheme());
            gka = VectorDrawableCompat.create(getResources(), R.drawable.gkmonitoring, getApplicationContext().getTheme());
        } else {
            community = getApplicationContext().getResources().getDrawable(R.drawable.communityicon, getApplicationContext().getTheme());
            gka = getApplicationContext().getResources().getDrawable(R.drawable.gkmonitoring, getApplicationContext().getTheme());
        }


        btnGkMonitoring.setCompoundDrawablesWithIntrinsicBounds(null, gka, null, null);
        btnComminitySurvey.setCompoundDrawablesWithIntrinsicBounds(null, community, null, null);




        Query listSurveyQuery = Query.select().from(Survey.TABLE);
        SquidCursor<Survey> surveyCursor = db.query(Survey.class, listSurveyQuery);

        if (db.countAll(Survey.class) > 0) {
            // we have surveys in DB, get them
            try {
                while (surveyCursor.moveToNext()) {
                    Survey survey = new Survey(surveyCursor);
                    SurveyMain pojo = new SurveyMain();
                    pojo.setCommunity(survey.getName());
                    pojo.setId(survey.getId());
                    pojo.setPartener(survey.getPartner());
                    mSurveyAdapter.add(pojo);
                }
            } finally {
                if (surveyCursor != null) {
                    surveyCursor.close();
                }
            }
        }


        spnSurveyType.setAdapter(mSurveyAdapter);


        btnGkMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.surveyType = 2;


                if (!checkPermissions()) {
                    requestPermissions();
                }else {
                    startActivity();
                }

            }
        });





        btnComminitySurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.surveyType = 1;
               /* int posi=spnSurveyType.getSelectedItemPosition();
                Long surveyId =mSurveyAdapter.getItem(posi).getId();
                String surveyName = mSurveyAdapter.getItem(posi).getCommunity();
                String partner=mSurveyAdapter.getItem(posi).getPartener();

                Toast.makeText(getApplicationContext(),surveyId+":"+surveyName,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SurveyTypeActivity.this, MainDashList.class);
                intent.putExtra("surveyId", surveyId);
                intent.putExtra("surveyName", surveyName);
                intent.putExtra("partener1", partner);
                startActivity(intent);*/

                    startActivity();


            }
        });


    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (!shouldProvideRationale) {
            //show dialog for location
            ActivityCompat.requestPermissions(SurveyTypeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }else
        {
            ActivityCompat.requestPermissions(SurveyTypeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    //check permission granted or not
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    public void startActivity() {


        int posi = spnSurveyType.getSelectedItemPosition();
        Long surveyId = mSurveyAdapter.getItem(posi).getId();
        String surveyName = mSurveyAdapter.getItem(posi).getCommunity();
        String partner = mSurveyAdapter.getItem(posi).getPartener();

        //    Toast.makeText(getApplicationContext(), surveyId + ":" + surveyName, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SurveyTypeActivity.this, MainDashList.class);
        intent.putExtra("surveyId", surveyId);
        intent.putExtra("surveyName", surveyName);
        intent.putExtra("partener1", partner);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(), AppSettings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }


}
