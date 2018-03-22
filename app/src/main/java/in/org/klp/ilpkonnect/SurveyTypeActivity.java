package in.org.klp.ilpkonnect;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.FaqPackage.FaqActivity;
import in.org.klp.ilpkonnect.Pojo.StatePojo;
import in.org.klp.ilpkonnect.Pojo.SurveyMain;

import in.org.klp.ilpkonnect.data.StringWithTags;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.MySummary;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroup;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.State;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.SummaryInfo;
import in.org.klp.ilpkonnect.db.Summmary;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.db.Surveyuser;
import in.org.klp.ilpkonnect.utils.AppSettings;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 8/1/2017.
 */

public class SurveyTypeActivity extends BaseActivity {


    private KontactDatabase db;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    RecyclerView surveylistGrid;
    ArrayList<Survey> surveysList;
    SurveyTypeAdapter surveyTypeAdapter;
    SessionManager sessionManager;

    TextView tvSurveyNot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_type);
        this.setTitle(getResources().getString(R.string.surveyType));

        surveylistGrid = findViewById(R.id.surveylistGrid);
        surveylistGrid.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        db = ((KLPApplication) getApplicationContext().getApplicationContext()).getDb();
        sessionManager = new SessionManager(getApplicationContext());
        tvSurveyNot=(TextView)findViewById(R.id.tvSurveyNot);
//Toast.makeText(getApplicationContext(),sessionManager.getStateSelection(),Toast.LENGTH_SHORT).show();


/*        Query listSurveyQuery = Query.select().from(Survey.TABLE);
        SquidCursor<Survey> surveyCursor = db.query(Survey.class, listSurveyQuery);

        if (surveyCursor.getCount() > 0) {
            // we have surveys in DB, get them
            try {
                surveysList = new ArrayList<>();
                while (surveyCursor.moveToNext()) {
                    Survey survey = new Survey(surveyCursor);
                    SurveyMain pojo = new SurveyMain();
                    pojo.setCommunity(survey.getName());
                    pojo.setImageRequired(survey.isImageRequired());
                    pojo.setCommunityLocal(survey.getNameLoc());
                    pojo.setQuestiongroupId(survey.getQuestionGroupId());
                    pojo.setId(survey.getId());
                    pojo.setStateKey(survey.getStateKey());
                    pojo.setPartener(survey.getPartner());
                    surveysList.add(survey);

                }
                if (surveysList != null && surveysList.size() > 0) {
                    surveyTypeAdapter = new SurveyTypeAdapter(SurveyTypeActivity.this, surveysList,sessionManager);
                    surveylistGrid.setAdapter(surveyTypeAdapter);
                    surveyTypeAdapter.notifyDataSetChanged();

                }
            } finally {
                if (surveyCursor != null) {
                    surveyCursor.close();
                }
            }
        }*/


    }


    @Override
    protected void onResume() {
        super.onResume();
        surveylistGrid.setVisibility(View.VISIBLE);
        tvSurveyNot.setVisibility(View.GONE);
        surveyTypeAdapter = new SurveyTypeAdapter(SurveyTypeActivity.this, new ArrayList<Survey>(), sessionManager);
        surveylistGrid.setAdapter(surveyTypeAdapter);
        surveyTypeAdapter.notifyDataSetChanged();


        SquidCursor<Surveyuser> surveyUser = null;
        Query surveyuserquery = Query.select().from(Surveyuser.TABLE)
                .where(Surveyuser.NAME.eqCaseInsensitive(sessionManager.getUserType())
                        .or(Surveyuser.NAME.eqCaseInsensitive("XYZ")));

        surveyUser = db.query(Surveyuser.class, surveyuserquery);
        if(surveyUser!=null&&surveyUser.getCount()>0)
        { ArrayList<Long> surveyIds = new ArrayList<>();
           while (surveyUser.moveToNext()) {
               surveyIds.add(surveyUser.get(Surveyuser.SURVEYID));

           }

           if(surveyIds!=null&&surveyIds.size()>0) {
               Query listSurveyQuery = Query.select().from(Survey.TABLE).where(Survey.ID.in(surveyIds));
               SquidCursor<Survey> surveyCursor = db.query(Survey.class, listSurveyQuery);
               if (surveyCursor.getCount() > 0) {
                   // we have surveys in DB, get them
                   try {
                       surveysList = new ArrayList<>();
                       while (surveyCursor.moveToNext()) {
                           Survey survey = new Survey(surveyCursor);
                 /*   SurveyMain pojo = new SurveyMain();
                    pojo.setCommunity(survey.getName());
                    pojo.setImageRequired(survey.isImageRequired());
                    pojo.setCommunityLocal(survey.getNameLoc());
                    pojo.setQuestiongroupId(survey.getQuestionGroupId());
                    pojo.setId(survey.getId());
                    pojo.setStateKey(survey.getStateKey());
                    pojo.setPartener(survey.getPartner());*/
                           surveysList.add(survey);

                       }
                       if (surveysList != null && surveysList.size() > 0) {
                           surveyTypeAdapter = new SurveyTypeAdapter(SurveyTypeActivity.this, surveysList, sessionManager);
                           surveylistGrid.setAdapter(surveyTypeAdapter);
                           surveyTypeAdapter.notifyDataSetChanged();

                       }else
                       {
                           surveylistGrid.setVisibility(View.GONE);
                           tvSurveyNot.setVisibility(View.VISIBLE);
                       }
                   } finally {
                       if (surveyCursor != null) {
                           surveyCursor.close();
                       }
                   }
               }

           }else {
               surveylistGrid.setVisibility(View.GONE);
               tvSurveyNot.setVisibility(View.VISIBLE);
           }

        }else {
            surveylistGrid.setVisibility(View.GONE);
            tvSurveyNot.setVisibility(View.VISIBLE);
        }




    }
    public int getStoriesCount()
    {
        Query listStoryQuery = Query.select().from(Story.TABLE)
                .where(Story.SYNCED.eq(0));
        SquidCursor<Story> storiesCursor = db.query(Story.class, listStoryQuery);

        return storiesCursor.getCount();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {

            String message="";
            if(getStoriesCount()>0)
            {
                message=String.format(getResources().getString(R.string.performinglogout),getStoriesCount())+"\n"+getResources().getString(R.string.doyouwantToLogout);

            }else
            {
                message=getResources().getString(R.string.doyouwantToLogout);
            }
            android.support.v7.app.AlertDialog alertDailog = new android.support.v7.app.AlertDialog.Builder(SurveyTypeActivity.this).create();


            alertDailog.setCancelable(false);
            alertDailog.setMessage(message);
            alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_positive),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            logoutUser();

                        }
                    });
            alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDailog.show();


        } else if (id == R.id.action_updateProfile) {
            startActivity(new Intent(getApplicationContext(), UpdateProfileActivity.class));
        } else if (id == R.id.action_Setup) {
            startActivity(new Intent(getApplicationContext(), TempLoading.class));
        } else if (id == R.id.action_setting) {
            startActivity(new Intent(getApplicationContext(), AppSettings.class));
        }else if (id == R.id.actionFAQ) {
            startActivity(new Intent(getApplicationContext(), FaqActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser() {
        // KLPApplication.setLanguage(getApplicationContext(), "en");
        sessionManager.logoutUser();
        KLPApplication.setLanguage(getApplicationContext(), "en");
        db.deleteAll(Surveyuser.class);
        db.deleteAll(School.class);

        db.deleteAll(Respondent.class);
        db.deleteAll(State.class);
        db.deleteAll(Question.class);
        db.deleteAll(Summmary.class);
        db.deleteAll(SummaryInfo.class);
        db.deleteAll(MySummary.class);
        db.deleteAll(QuestionGroupQuestion.class);
        db.deleteAll(Boundary.class);
        db.deleteAll(Answer.class);
        db.deleteAll(Story.class);
        db.deleteAll(Survey.class);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            try {


                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                super.onBackPressed();
                startActivity(a);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();

    }


}
