package in.org.klp.ilpkonnect;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.squidb.data.ICursor;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterfaceSync;
import in.org.klp.ilpkonnect.Pojo.ImagesPOJO;
import in.org.klp.ilpkonnect.ReportPojo.ReportIndiPojo;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.adapters.NewPagerAdapter;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;

import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.SummaryInfo;
import in.org.klp.ilpkonnect.db.Summmary;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.DialogConstants;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.SessionManager;
import in.org.klp.ilpkonnect.utils.SmartFragmentStatePagerAdapter;
import in.org.klp.ilpkonnect.utils.SyncManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsActivity extends BaseActivity {

    public Menu _menu = null;
    Context context = this;
    SessionManager sessionManager;
    int qcount = 0;
    DialogConstants dialogConstants;
    int createReportLevel;
    TextView txtBlock, txtCluster;
    TextView tvD, tvC, tvB;
    String distId, blockId, schoolId;
    Long clusterId;
    String[] boundry_text;
    TextView tvPYes, tvPNo, tvPDN;
    Long surveyType, qgId;
    LinearLayout lnlt;
    TextView tvYes, tvNo, tvDontKn, tvSchoolName;
    RecyclerView listView;
    ProgressDialog progressDialog;
    String globalid;

    ViewPager viewPager;
    TextView tvLoadImage;
    int totalYes = 0, totalNo = 0, totaldontknow = 0;
    ArrayList<String> imageurl;
    long oneDay = 86400000;
    String CalSdate, CalEndate;
    private Long surveyId, questionGroupId, bid, sdate, edate, surveyTypeId;
    private KontactDatabase db;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    boolean isImageRequired;

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public String getYear(String strdate) {
        try {


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(strdate);
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            return df.format(date);
        } catch (Exception e) {

        }
        return "2018";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        this.setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(getApplicationContext());
        db = ((KLPApplication) getApplicationContext()).getDb();
        txtBlock = findViewById(R.id.txtBlock);
        txtCluster = findViewById(R.id.txtCluster);
        tvD = findViewById(R.id.dist_name);
        tvB = findViewById(R.id.blck_name);
        tvC = findViewById(R.id.clst_name);
        tvPYes = findViewById(R.id.tvPYes);
        tvPNo = findViewById(R.id.tvPNo);
        tvPDN = findViewById(R.id.tvPDN);
        lnlt = findViewById(R.id.lnlt);
        viewPager = findViewById(R.id.viewPager);
        tvYes = findViewById(R.id.tvYes);
        tvNo = findViewById(R.id.tvNo);
        tvDontKn = findViewById(R.id.tvDontKn);
        tvLoadImage = findViewById(R.id.tvLoadImage);
        tvSchoolName = findViewById(R.id.tvSchoolName);

        listView = findViewById(R.id.listView);
        Intent intent = getIntent();
        boundry_text = intent.getStringExtra("boundary").split(",");
        //  Toast.makeText(getApplicationContext(),boundry_text.length+"",Toast.LENGTH_SHORT).show();
        surveyId = intent.getLongExtra("surveyId", 0);
        questionGroupId = intent.getLongExtra("questionGroupId", 0);
        isImageRequired = getIntent().getBooleanExtra("imageRequired", false);


        // Long l=db.fetch(QuestionGroup.class,surveyTypeId).getSurveyType();

        bid = intent.getLongExtra("bid", 0);
        //   Toast.makeText(getApplicationContext(),bid+"",Toast.LENGTH_SHORT).show();
        sdate = intent.getLongExtra("sdate", 0);
        edate = intent.getLongExtra("edate", 0);
        edate = edate - oneDay;

        CalSdate = getDate(sdate, "yyyy-MM-dd");
        CalEndate = getDate(edate + oneDay, "yyyy-MM-dd");


        //this variable will decides the level of report dist,block,cluser or school
        createReportLevel = intent.getIntExtra("createReportLevel", 0);
        distId = intent.getStringExtra("ditrcitId");
        blockId = intent.getStringExtra("blockid");
        clusterId = intent.getLongExtra("clusterId", 0);
        schoolId = intent.getStringExtra("schoolId");
//Toast.makeText(getApplicationContext(),schoolId+"",Toast.LENGTH_SHORT).show();
        if (isImageRequired == true) {
            tvLoadImage.setVisibility(View.VISIBLE);
        } else {
            tvLoadImage.setVisibility(View.GONE);
        }
        surveyType = intent.getLongExtra("surveyType", 0);
        qgId = intent.getLongExtra("questionGroupId", 0);
        // Toast.makeText(getApplicationContext(),qgId+":",Toast.LENGTH_SHORT).show();
        ArrayList<ReportIndiPojo> reportIndiPojos = new ArrayList<>();
        /*listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listView.setAdapter(new ReportAdapter(getApplicationContext(), reportIndiPojos));
*/
        //    Toast.makeText(getApplicationContext(), schoolId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "Dist:" + distId + ",Block:" + blockId + ",Cluster: " + clusterId, Toast.LENGTH_SHORT).show();


        getBids(createReportLevel);

     /*   NewPagerAdapter adapter1 = new NewPagerAdapter(getApplicationContext(), reportIndiPojos);
        viewPager.setAdapter(adapter1);*/


        Query listStoryQquery = Query.select().from(Story.TABLE)
                .where(Story.SYNCED.eq(0).and(Story.GROUP_ID.eq(qgId)));
        final SquidCursor<Story> storySquidCursor = db.query(Story.class, listStoryQquery);
        fetchQuestions();
        if (storySquidCursor.getCount() < 1) {

            if (AppStatus.isConnected(ReportsActivity.this)) {

                syncdataOnCreate();
        }

        } else {

            if(checkSchool()==true)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
                builder.setCancelable(false);
                builder.setMessage(getResources().getString(R.string.unsyncedSurveysFound));
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        offLineData(storySquidCursor);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                     //   fetchQuestions();
                    }
                });

                builder.show();





            }else
            {
                fetchQuestions();
            }





        }


        tvLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();


            }
        });



    }


    public void offLineData(SquidCursor<Story> storySquidCursor) {
        ArrayList<ReportIndiPojo> reportIndiPojos = new ArrayList<>();

        totalYes = 0;
        totalNo = 0;
        totaldontknow = 0;
        // SquidCursor<QuestionGroup> qgCursor = null;
        SquidCursor<QuestionGroupQuestion> qgqCursor = null;
        try {

            Query listQGQquery = Query.select().from(QuestionGroupQuestion.TABLE)
                    .where(QuestionGroupQuestion.QUESTIONGROUP_ID.eq(qgId))
                    .orderBy(QuestionGroupQuestion.SEQUENCE.asc());
            qgqCursor = db.query(QuestionGroupQuestion.class, listQGQquery);
            ArrayList<Question> resultQuestions = new ArrayList<Question>();

            int count = 0;
            while (qgqCursor.moveToNext()) {

                Long qID = qgqCursor.get(QuestionGroupQuestion.QUESTION_ID);
                Question question = db.fetch(Question.class, qID);
                resultQuestions.add(question);
                count++;
            }
            qcount = count;
            ArrayList<Long> listStory = new ArrayList<>();
            HashMap<Long, Long> hasmapschoolwithResponses = new HashMap<>();
            try {


                while (storySquidCursor.moveToNext()) {
                    listStory.add(new Story(storySquidCursor).getId());
                    hasmapschoolwithResponses.put(new Story(storySquidCursor).getSchoolId(), new Story(storySquidCursor).getSchoolId());

                }
            } catch (Exception e) {

            }

            for (Question question : resultQuestions) {
                if (question == null) {
                    continue;
                }


                Query QueryYes = Query.select().from(Answer.TABLE).where(Answer.TEXT.eqCaseInsensitive("Yes"))
                        .where(Answer.STORY_ID.in(listStory).and(Answer.QUESTION_ID.eq(question.getId())));
                SquidCursor<Answer> squidYes = db.query(Answer.class, QueryYes);

                Query NotYes = Query.select().from(Answer.TABLE).where(Answer.TEXT.eqCaseInsensitive("No"))
                        .where(Answer.STORY_ID.in(listStory).and(Answer.QUESTION_ID.eq(question.getId())));
                SquidCursor<Answer> squidNo = db.query(Answer.class, NotYes);

                Query dontknow = Query.select().from(Answer.TABLE).where(Answer.TEXT.neq("No").and(Answer.TEXT.neq("Yes")))
                        .where(Answer.STORY_ID.in(listStory).and(Answer.QUESTION_ID.eq(question.getId())));
                SquidCursor<Answer> squidother = db.query(Answer.class, dontknow);


                ReportIndiPojo reporIndi = new ReportIndiPojo();
                reporIndi.setTextKan(question.getTextKn() != null ? question.getTextKn() : question.getText());
                reporIndi.setTextEng(question.getText() != null ? question.getText() : question.getTextKn());

                totalYes = totalYes + squidYes.getCount();
                totalNo = totalNo + squidNo.getCount();
                if(squidother.getCount()>0) {
                    while (squidother.moveToNext()) {
                        Answer answer = new Answer(squidother);
                        StringTokenizer st = new StringTokenizer(answer.getText(), ",");
                        while (st.hasMoreTokens()) {
                            String value = st.nextToken().trim();
                            if (value.equalsIgnoreCase("Yes")) {
                                totalYes = totalYes + 1;
                                Log.d("valueg",totalYes+"YEs");
                            } else if (value.equalsIgnoreCase("No")) {
                                totalNo = totalNo + 1;
                                Log.d("valueg",totalNo+"No");
                            } else {
                                totaldontknow = totaldontknow + 1;
                                Log.d("valueg",totaldontknow+"DNo");
                            }

                        }
                    }
                }
                //totaldontknow = totaldontknow + squidother.getCount();

                reporIndi.setTotal_responses(listStory.size());
                reporIndi.setToal_schools_with_res(hasmapschoolwithResponses.size());
                reporIndi.setTotal_school(Constants.scoolCount);


                reporIndi.setYes(totalYes + "");
                reporIndi.setNo(totalNo + "");
                reporIndi.setDont(totaldontknow + "");
                reportIndiPojos.add(reporIndi);

                tvYes.setText(totalYes + "");
                tvNo.setText(totalNo + "");
                tvDontKn.setText(totaldontknow + "");

                tvPYes.setText(getScorePercent(totalYes, (totalYes + totalNo + totaldontknow)) + "%");
                tvPNo.setText(getScorePercent(totalNo, (totalYes + totalNo + totaldontknow)) + "%");
                tvPDN.setText(getScorePercent(totaldontknow, (totalYes + totalNo + totaldontknow)) + "%");


                NewPagerAdapter adapter1 = new NewPagerAdapter(getApplicationContext(), reportIndiPojos, ReportsActivity.this);
                viewPager.setAdapter(adapter1);


            }
        } catch (Exception e) {

        }


    }

    private void showSignupResultDialog(String title, String message, String buttonText) {
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.show(getSupportFragmentManager(), "Registration result");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:


                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
            case R.id.action_sync_block:
               // Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
                syncBlock();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchQuestions() {

        //Toast.makeText(getApplicationContext(),"Fetch Question",Toast.LENGTH_SHORT).show();
        totalYes = 0;
        totalNo = 0;
        totaldontknow = 0;
        // SquidCursor<QuestionGroup> qgCursor = null;
        SquidCursor<QuestionGroupQuestion> qgqCursor = null;
        //  Log.d("test", 1 + "");
       /* Query listQGquery = Query.select().from(QuestionGroup.TABLE)
                .where(QuestionGroup.ID.eq(surveyId)).limit(1);
        qgCursor = db.query(QuestionGroup.class, listQGquery);
*/
        try {
            //  if (qgCursor.moveToFirst()) {

            //   questionGroupId = qgCursor.get(QuestionGroup.ID);
            Query listQGQquery = Query.select().from(QuestionGroupQuestion.TABLE)
                    .where(QuestionGroupQuestion.QUESTIONGROUP_ID.eq(questionGroupId))
                    .orderBy(QuestionGroupQuestion.SEQUENCE.asc());
            qgqCursor = db.query(QuestionGroupQuestion.class, listQGQquery);
            ArrayList<Question> resultQuestions = new ArrayList<Question>();

            int count = 0;
            while (qgqCursor.moveToNext()) {

                Long qID = qgqCursor.get(QuestionGroupQuestion.QUESTION_ID);
                Question question = db.fetch(Question.class, qID);
                resultQuestions.add(question);
                count++;
            }
            qcount = count;
            ArrayList<ReportIndiPojo> reportIndiPojos = new ArrayList<>();
            for (Question question : resultQuestions) {
                if (question == null) {
                    continue;
                }

                // Log.d("test", 4 + ":" + qgId + ":" + question.getId());
                Query summryQuery = Query.select().from(SummaryInfo.TABLE)
                        .where(SummaryInfo.BID.eq(globalid).and(SummaryInfo.GROUPID.eqCaseInsensitive(qgId + "")
                                .and(SummaryInfo.QID.eq(new Long(question.getId())).and(SummaryInfo.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection())))
                        ));
                SquidCursor<SummaryInfo> summaryCursor = db.query(SummaryInfo.class, summryQuery);


                ReportIndiPojo reporIndi = new ReportIndiPojo();
                reporIndi.setTextKan(question.getTextKn() != null ? question.getTextKn() : question.getText());
                reporIndi.setTextEng(question.getText() != null ? question.getText() : question.getTextKn());

                Query ReportSummary = Query.select().from(Summmary.TABLE)
                        .where(Summmary.BID.eq(globalid).and(Summmary.GROUPID.eqCaseInsensitive(qgId + "")
                                .and(Summmary.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection()))));
                SquidCursor<Summmary> reportSummaryCursor = db.query(Summmary.class, ReportSummary);
                if (reportSummaryCursor != null && reportSummaryCursor.getCount() > 0) {

                    while (reportSummaryCursor.moveToNext()) {
                        Summmary summaryInfo = new Summmary(reportSummaryCursor);
                        reporIndi.setTotal_school(summaryInfo.getTotalSchool());
                        reporIndi.setTotal_responses(summaryInfo.getTotalResponse());
                        reporIndi.setToal_schools_with_res(summaryInfo.getTotalSchoolWithResponse());
                    }
                }


                if (summaryCursor != null && summaryCursor.getCount() > 0) {

                    while (summaryCursor.moveToNext()) {
                        SummaryInfo summaryInfo = new SummaryInfo(summaryCursor);
                        int yes = Integer.parseInt(summaryInfo.getYes() + "");
                        int no = Integer.parseInt(summaryInfo.getNo() + "");
                        int dn = Integer.parseInt(summaryInfo.getDontknow() + "");
                        totalYes = totalYes + yes;
                        totalNo = totalNo + no;
                        totaldontknow = totaldontknow + dn;

                        reporIndi.setYes(yes + "");
                        reporIndi.setNo(no + "");
                        reporIndi.setDont(dn + "");
                        reportIndiPojos.add(reporIndi);
                               /* reporIndi.setTotal_school(reportSummaryCursor.getTotalSchool());
                            reporIndi.setTotal_responses(summaryInfo.getTotalResponse());
                            reporIndi.setToal_schools_with_res(summaryInfo.getTotalSchoolWithResponse());
*/

                    }
                } else {
                    reporIndi.setYes(0 + "");
                    reporIndi.setNo(0 + "");
                    reporIndi.setDont(0 + "");
                    reportIndiPojos.add(reporIndi);


                }
                tvYes.setText(totalYes + "");
                tvNo.setText(totalNo + "");
                tvDontKn.setText(totaldontknow + "");

                tvPYes.setText(getScorePercent(totalYes, (totalYes + totalNo + totaldontknow)) + "%");
                tvPNo.setText(getScorePercent(totalNo, (totalYes + totalNo + totaldontknow)) + "%");
                tvPDN.setText(getScorePercent(totaldontknow, (totalYes + totalNo + totaldontknow)) + "%");


                NewPagerAdapter adapter1 = new NewPagerAdapter(getApplicationContext(), reportIndiPojos, ReportsActivity.this);
                viewPager.setAdapter(adapter1);


            }


            //}
        } catch (Exception e) {
            //   Log.d("test", "------------------------Exception:" + e.getMessage());
        } finally {
            //  Log.d("test", 5 + "");
           /* if (qgCursor != null) {
                qgCursor.close();
            }*/
            if (qgqCursor != null) {
                qgqCursor.close();
            }

            try {

            } catch (Exception e) {
                db.close();
            }
        }


    }

    public void change_frag(View view) {
        int position = viewPager.getCurrentItem();

        if (view.getId() == R.id.left_arrow) {
            if (position - 1 >= 0)
                viewPager.setCurrentItem(position - 1);
        } else if (view.getId() == R.id.right_arrow) {
            if (position + 1 < qcount)
                viewPager.setCurrentItem(position + 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_at_resource, menu);
        _menu = menu;
        return true;
    }



    public void syncdataOnCreate()
    {
        if (AppStatus.isConnected(ReportsActivity.this)) {
            final SyncManager sync = new SyncManager(ReportsActivity.this, db, false, false, true);


            if (sync.isSyncDataFound()>0) {

//                sync2(jsondata.get(0).toString().trim(), jsondata.size(), 0, count, jsondata);



                showProgress(true);
                ArrayList<JSONObject> jsondata = sync.doUpload();
                Log.d("shri",jsondata.toString());
                new ProNetworkSettup(getApplicationContext()).SyncData(jsondata.get(0).toString(), sessionManager.getToken(), 0, jsondata.size(), jsondata, new StateInterfaceSync() {
                    @Override
                    public void success(String message) {

                        loadReportData(sync,true);
                    }

                    @Override
                    public void failed(String message) {
                        showProgress(false);
                        DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    }

                    @Override
                    public void update(int count, String message) {
                        Log.d("shri","report 1"+count);
                    }
                });







            } else {
                alertDialog(sync);
            }


        }
    }













    public void syncBlock() {


        final SyncManager sync = new SyncManager(ReportsActivity.this, db, false, false, true);

        if (AppStatus.isConnected(ReportsActivity.this)) {
       //     Toast.makeText(getApplicationContext(),"true"+sync.isSyncDataFound(),Toast.LENGTH_SHORT).show();

            if (sync.isSyncDataFound()>0) {

                        showProgress(true);
                    ArrayList<JSONObject> jsondata = sync.doUpload();
                    new ProNetworkSettup(getApplicationContext()).SyncData(jsondata.get(0).toString(), sessionManager.getToken(), 0, jsondata.size(), jsondata, new StateInterfaceSync() {
                        @Override
                            public void success(String message) {

                                loadReportData(sync,true);
                            }

                            @Override
                            public void failed(String message) {
                                showProgress(false);
                                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                            }

                        @Override
                        public void update(int count, String message) {
                      //  Log.d("shri","report "+count);
                        }
                    });







            } else {
                alertDialog(sync);
            }


        } else {

            if (sync.getStoriesCount() > 0) {

                showSignupResultDialog(
                        getResources().getString(R.string.app_name),
                        getResources().getString(R.string.noInternetCon) + ",\n"  +  String.format(
                                getString(R.string.survey_taken),
                                sync.getStoriesCount()
                        ),
                        getResources().getString(R.string.Ok));

            } else {

                showSignupResultDialog(
                        getResources().getString(R.string.app_name),
                        getResources().getString(R.string.noInternetCon),
                        getResources().getString(R.string.Ok));
            }

        }


    }


    public void loadReportData(SyncManager sync,final boolean flag) {

        String data = getIdof(createReportLevel);
        long oneday = 86400000L;
        String dataFound[] = data.split("\\|");
        final long id = Long.parseLong(dataFound[0]);
        final String level = dataFound[1];
        final String sdate = getDate(Long.parseLong(dataFound[2]), "yyyy-MM-dd");
        final String endate = getDate((Long.parseLong(dataFound[3])) + oneday, "yyyy-MM-dd");

        // Log.d("date",sdate+":"+endate);
        if (!level.equalsIgnoreCase("school")) {

            //  new ProNetworkSettup(getApplicationContext()).getReoportData(id, qgId, sessionManager.getStateSelection(), level,sdate,endate,sessionManager.getToken(), new StateInterface() {

            // getYear(CalSdate)
            new ProNetworkSettup(getApplicationContext()).getReoportData(id, qgId, surveyId, sessionManager.getStateSelection(), level, sdate, endate, sessionManager.getToken(), "gka", new StateInterface() {
                @Override
                public void success(String message) {
                    showProgress(false);
                    if (message.equalsIgnoreCase("success")) {
                        fetchQuestions();
                    } else {
                        fetchQuestions();
                        String msg=message;
                     /*   if(flag==true)
                        {
                            msg= message+"\n"+getResources().getString(R.string.allSurvey);
                        }*/
                        DailogUtill.showDialog(msg, getSupportFragmentManager(), ReportsActivity.this);

                    }

                }

                @Override
                public void failed(String message) {
                    showProgress(false);
                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                }
            });
        } else {
            //showProgress(true);
            new ProNetworkSettup(getApplicationContext()).getReoportDataSchool(id, qgId, surveyId, sessionManager.getStateSelection(), level, sdate, endate, sessionManager.getToken(), "gka", new StateInterface() {
                @Override
                public void success(String message) {
                    showProgress(false);
                    if (message.equalsIgnoreCase("success")) {
                        fetchQuestions();
                    } else {
                        fetchQuestions();
                        DailogUtill.showDialog(message, getSupportFragmentManager(), ReportsActivity.this);

                    }

                }

                @Override
                public void failed(String message) {
                    showProgress(false);
                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                }
            });

        }


    }


    public String getIdof(int createReportLevel) {
        if (createReportLevel == 0) {
            return distId + "|" + "admin1" + "|" + sdate + "|" + edate;
        } else if (createReportLevel == 1) {
            return blockId + "|" + "admin2" + "|" + sdate + "|" + edate;
        } else if (createReportLevel == 2) {
            return clusterId + "|" + "admin3" + "|" + sdate + "|" + edate;
        } else if (createReportLevel == 3) {
            return schoolId + "|" + "school" + "|" + sdate + "|" + edate;
        }
        return "";

    }

    public void alertDialog(final SyncManager syncManager) {


                showProgress(true);
                loadReportData(syncManager,false);




    }

    public void getBids(int createReportLevel) {

        switch (createReportLevel) {
            case 0:
                //district Level
                isDistrictLevelDataFound(distId);
                getSchoolCount();
                globalid = distId;
                tvD.setText(boundry_text[0]);
                txtBlock.setVisibility(View.GONE);
                txtCluster.setVisibility(View.GONE);
                tvC.setVisibility(View.GONE);
                tvB.setVisibility(View.GONE);
                lnlt.setVisibility(View.GONE);
                tvLoadImage.setVisibility(View.GONE);

                Log.d("Reports", globalid + ":Dist");
//Toast.makeText(getApplicationContext(),distId+":Dist",Toast.LENGTH_SHORT).show();
                break;

            case 1:
                //block Level
                isBlockLevelDataFound(blockId);
                getSchoolCount();
                globalid = blockId;
                // Toast.makeText(getApplicationContext(),globalid+"block",Toast.LENGTH_SHORT).show();
                tvD.setText(boundry_text[0]);
                tvB.setText(boundry_text[1]);
                tvC.setVisibility(View.GONE);
                tvB.setVisibility(View.VISIBLE);
                lnlt.setVisibility(View.GONE);
                tvLoadImage.setVisibility(View.GONE);
                Log.d("Reports", globalid + ":Block");

                txtBlock.setVisibility(View.VISIBLE);
                txtCluster.setVisibility(View.GONE);

                break;

            case 2:
                getClusterDataFound(clusterId);
                getSchoolCount();
                globalid = clusterId + "";
                tvD.setText(boundry_text[0]);
                tvB.setText(boundry_text[1]);
                tvC.setText(boundry_text[2]);
                tvC.setVisibility(View.VISIBLE);
                tvB.setVisibility(View.VISIBLE);
                lnlt.setVisibility(View.VISIBLE);
                Log.d("Reports", globalid + ":CLuster");
                //   Toast.makeText(getApplicationContext(),globalid+":Cluster",Toast.LENGTH_SHORT).show();
                tvLoadImage.setVisibility(View.GONE);

                txtBlock.setVisibility(View.VISIBLE);
                txtCluster.setVisibility(View.VISIBLE);

                //cluster level
                break;
            case 3:
                //school level
                try {
                    School school = db.fetch(School.class, Long.parseLong(schoolId));
                    tvSchoolName.setVisibility(View.VISIBLE);
                    tvSchoolName.setText(school.getName());
                } catch (Exception e) {
                    tvSchoolName.setVisibility(View.GONE);
                }
                globalid = schoolId;
                Constants.scoolCount = 1;
                txtBlock.setVisibility(View.VISIBLE);
                txtCluster.setVisibility(View.VISIBLE);
                tvD.setText(boundry_text[0]);
                tvB.setText(boundry_text[1]);
                tvC.setText(boundry_text[2]);
                tvC.setVisibility(View.VISIBLE);
                tvB.setVisibility(View.VISIBLE);
                lnlt.setVisibility(View.VISIBLE);
                Log.d("Reports", globalid + ":School");
                //Toast.makeText(getApplicationContext(),globalid+":school",Toast.LENGTH_SHORT).show();

                if (isImageRequired == true) {
                    tvLoadImage.setVisibility(View.VISIBLE);

                }
                //  Toast.makeText(getApplicationContext(),""+bid,Toast.LENGTH_SHORT).show();


                break;
        }


    }

    public void getSchoolCount() {
        Constants.schoolIds=new ArrayList<>();
        if (Constants.cluster_ids != null && Constants.cluster_ids.size() > 0) {
            //  for (Long id : Constants.cluster_ids) {



            Query schoolsQuery = Query.select().from(School.TABLE)
                    .where(School.BOUNDARY_ID.in(Constants.cluster_ids));
            SquidCursor<School> scholcursor = db.query(School.class, schoolsQuery);
            Constants.scoolCount = scholcursor.getCount();

            while (scholcursor.moveToNext()) {
                Constants.schoolIds.add(new School(scholcursor).getId());
            }



        }
    }

    public double getScorePercent(int num, int total) {
        if (num != 0 && total != 0) {
            float res = 100f * num / total;
            try {
                return Double.parseDouble(new DecimalFormat("##.##").format(res));
            } catch (Exception e) {
                return 0d;
            }
        } else return 0d;
    }

    public void showDialog(String msg) {
        DialogConstants d = new DialogConstants(ReportsActivity.this, msg);
        if (!d.isShowing()) {
            d.show();
        }
    }

    @Override
    protected void onDestroy() {
        try {

        } catch (Exception e) {
            db.close();
        }
        super.onDestroy();
    }

    public boolean isDistrictLevelDataFound(String distrciId) {

        SquidCursor<Boundary> BlocktBoundaryCursor = db.query(Boundary.class, Query.select().from(Boundary.TABLE).where(Boundary.PARENT_ID.eq(distrciId)));
        if (BlocktBoundaryCursor.getCount() > 0) {
            List<Long> block_ids = new ArrayList<Long>();
            //clusters available for block _id
            try {
                while (BlocktBoundaryCursor.moveToNext()) {
                    Boundary boundary = new Boundary(BlocktBoundaryCursor);
                    block_ids.add(boundary.getId());

                }
            } finally {
                BlocktBoundaryCursor.close();
            }
            if (block_ids.size() > 0) {
                Query BlocklistIdsQuery = Query.select()
                        .from(Boundary.TABLE)
                        .where(Boundary.PARENT_ID.in(block_ids));
                SquidCursor<Boundary> clustercursor = db.query(Boundary.class, BlocklistIdsQuery);
                if (clustercursor.getCount() > 0) {

                    List<Long> cluster_ids = new ArrayList<Long>();
                    //clusters available for block _id
                    try {
                        while (clustercursor.moveToNext()) {
                            Boundary boundary = new Boundary(clustercursor);
                            cluster_ids.add(boundary.getId());
                        }
                    } finally {
                        clustercursor.close();
                    }
                    if (cluster_ids.size() > 0) {
                        Query BlocklistStoryQuery = Query.select()
                                .from(School.TABLE)
                                .where(School.BOUNDARY_ID.in(cluster_ids));
                        SquidCursor<School> schoolcursor = db.query(School.class, BlocklistStoryQuery);

                        //  Toast.makeText(getApplicationContext(),schoolcursor.getCount()+"",Toast.LENGTH_SHORT).show();
                        if (schoolcursor.getCount() > 0) {

                            Constants.cluster_ids = cluster_ids;
                            Constants.scoolCount = schoolcursor.getCount();
                            //    Toast.makeText(getApplicationContext(),Constants.cluster_ids.size()+"",Toast.LENGTH_SHORT).show();
                            return true;
                        } else {
                            //no schools found for district
                            //            showDialogConstant("no schools found for district");
                            Constants.cluster_ids = new ArrayList<>();
                            return false;

                        }


                    } else {
                        //no clusters for block
                        //          showDialogConstant("no clusters for block");
                        Constants.cluster_ids = new ArrayList<>();
                        return false;
                    }


                } else {
                    //no clusters found for selected district
                    //   showDialogConstant("no clusters found for selected district");
                    Constants.cluster_ids = new ArrayList<>();
                    return false;
                }


            } else {
                //showDialogConstant("No blocks for distrct");
                Constants.cluster_ids = new ArrayList<>();
                return false;
                //no blocks for distrct
            }


        } else {
            //showDialogConstant("No blocks founds for distrcit");
            Constants.cluster_ids = new ArrayList<>();
            //no blocks founds for distrcit
            return false;
        }


    }

    public boolean isBlockLevelDataFound(String blockid) {


        SquidCursor<Boundary> boundaryCursor = db.query(Boundary.class, Query.select().from(Boundary.TABLE).where(Boundary.PARENT_ID.eq(blockid)));
        if (boundaryCursor.getCount() > 0) {
            List<Long> cluster_ids = new ArrayList<Long>();
            //clusters available for block _id
            try {
                while (boundaryCursor.moveToNext()) {
                    Boundary boundary = new Boundary(boundaryCursor);
                    cluster_ids.add(boundary.getId());
                }
            } finally {
                boundaryCursor.close();
            }

            if (cluster_ids.size() > 0) {


                Query BlocklistStoryQuery = Query.select()
                        .from(School.TABLE)
                        .where(School.BOUNDARY_ID.in(cluster_ids));
                SquidCursor<School> schoolcursor = db.query(School.class, BlocklistStoryQuery);

                //  Toast.makeText(getApplicationContext(),schoolcursor.getCount()+"",Toast.LENGTH_SHORT).show();
                if (schoolcursor.getCount() > 0) {
                    Constants.cluster_ids = cluster_ids;
                    Constants.scoolCount = schoolcursor.getCount();
                    return true;
                } else {
                    Constants.cluster_ids = new ArrayList<>();
                    // showDialogConstant("No schools found");
                    return false;
                    //no schools found
                }
            } else {
                Constants.cluster_ids = new ArrayList<>();
                //showDialogConstant("No Cluster for Block");

                return false;
                //no clusters for block

            }
        } else {
            Constants.cluster_ids = new ArrayList<>();
            //showDialogConstant("No Boundaries for Block");
            return false;
            //no boundary for block
        }


    }

    private boolean getClusterDataFound(Long bid) {
        Constants.cluster_ids = new ArrayList<>();
        Constants.cluster_ids.add(bid);
        return true;


    }


    public boolean checkSchool()
    {

        if(createReportLevel==3)
        {
            Query schoolIds = Query.select().from(Story.TABLE)
                    .where(Story.SCHOOL_ID.eq(globalid));
            SquidCursor<Story> SchoolsCursor = db.query(Story.class, schoolIds);
            if(SchoolsCursor.getCount()>0)
            {
                return true;
            }else {
                return false;
            }

        }else {
            Query schoolIds = Query.select().from(Story.TABLE)
                    .where(Story.SCHOOL_ID.in( Constants.schoolIds));
            SquidCursor<Story> SchoolsCursor = db.query(Story.class, schoolIds);
            if (SchoolsCursor.getCount() > 0) {
                return true;
            }
            return false;
        }

    }

    public void showAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportsActivity.this);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.searchSurveyimage))
                .setTitle(getResources().getString(R.string.app_name))
                .setPositiveButton(getResources().getString(R.string.bydate), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getAllData(true);
                            }
                        }

                ).setNegativeButton(getResources().getString(R.string.all), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getAllData(false);
                    }
                }

        ).setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }

        );


        // Create the AlertDialog object and return it
        builder.create().show();
    }

    public void getAllData(boolean byDate) {
        final ProgressDialog progressDialog = new ProgressDialog(ReportsActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        progressDialog.setCancelable(false);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ImagesPOJO> imagesPOJOCallback = null;
        if (byDate == false) {
            //all images
            //Toast.makeText(getApplicationContext(),"all",Toast.LENGTH_SHORT).show();

            imagesPOJOCallback = apiInterface.getImages(schoolId, sessionManager.getStateSelection(), sessionManager.getToken());
        } else {
            // Toast.makeText(getApplicationContext(),CalSdate+":"+CalEndate,Toast.LENGTH_SHORT).show();

            imagesPOJOCallback = apiInterface.getImagesbyDate(schoolId, CalSdate, CalEndate, sessionManager.getStateSelection(), sessionManager.getToken());
        }
        imagesPOJOCallback.enqueue(new Callback<ImagesPOJO>() {
            @Override
            public void onResponse(Call<ImagesPOJO> call, Response<ImagesPOJO> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful()) {
                    if (response.body().getImages().size() != 0) {
                        ArrayList<String> data = new ArrayList<String>();
                        for (int i = 0; i < response.body().getImages().size(); i++) {
                            data.add(response.body().getImages().get(i).getUrl());
                        }


                        imageurl = data;
                        Intent i = new Intent(getApplicationContext(), LoadSurveyImage.class);
                        i.putExtra("image", imageurl);
                        startActivity(i);
                    } else {
                        showSignupResultDialog(
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.failed) + "-" + getResources().getString(R.string.nooserveyimage),
                                getResources().getString(R.string.Ok));
                    }


                } else {

                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.failed) + "-" + getResources().getString(R.string.nooserveyimage),
                            getResources().getString(R.string.Ok));
                }

            }

            @Override
            public void onFailure(Call<ImagesPOJO> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
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

                // Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(ReportsActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.syncing));
            progressDialog.show();
            progressDialog.setCancelable(false);
        } else {
            if (progressDialog != null) progressDialog.cancel();
        }
    }
}



