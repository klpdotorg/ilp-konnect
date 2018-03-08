package in.org.klp.ilpkonnect;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.data.StringWithTags;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;

import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.DialogConstants;
import in.org.klp.ilpkonnect.utils.SessionManager;

public class BoundarySelectionActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    Long bid, sdate = null, edate = null;
    long surveyId;
    String surveyName, type = "", district = "", block = "", cluster = "";
    SquidCursor<Boundary> boundary_cursor = null;
    SquidCursor<School> school_cursor = null;
    int cyear, cmonth, cdate, chour, cminute;
    EditText editText = null;
    SimpleDateFormat dateFormat;
    Context context = this;
    long llHeight;
    LinearLayout reportType, linSpinSchool, linBackSchool;
    KontactDatabase db;
    DialogConstants dialogConstants;
    String blockid, distrciId;
    Spinner spnReport;
    TextView txReport;
    Spinner sp_district, sp_block, sp_cluster, select_school;
    List<StringWithTags> schoolList;
    ProgressDialog progressDialog;
    TextView txBlock, txCluster, txt_select_school;
    boolean b = false;
    boolean isImageRequired;
    private Long questionGroupId, surveyTypeId;
    SharedPreferences sharedPreferences;
    SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundary_selection);
        db = ((KLPApplication) getApplicationContext()).getDb();
        sessionManager = new SessionManager(getApplicationContext());
        sharedPreferences = getSharedPreferences("boundary", MODE_PRIVATE);
        progressDialog = new ProgressDialog(BoundarySelectionActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        this.setTitle(getResources().getString(R.string.app_name));
        surveyId = getIntent().getLongExtra("surveyId", 0);
        questionGroupId = getIntent().getLongExtra("ILPQuestionGroupId", 0);
        surveyName = getIntent().getStringExtra("surveyName");
        isImageRequired = getIntent().getBooleanExtra("imageRequired", false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getStringExtra("type");
        Button bt_report = findViewById(R.id.report_button);
        final EditText start_date = findViewById(R.id.start_date);
        EditText end_date = findViewById(R.id.end_date);
        reportType = findViewById(R.id.reportType);
        final LinearLayout llBoundarySelect = findViewById(R.id.ll_select_boundary);
        final ListView listView = findViewById(R.id.school_list);
        spnReport = findViewById(R.id.spnReport);
        sp_district = findViewById(R.id.select_district);
        sp_block = findViewById(R.id.select_block);
        sp_cluster = findViewById(R.id.select_cluster);
        select_school = findViewById(R.id.select_school);
        linSpinSchool = findViewById(R.id.linSpinSchool);
        txBlock = findViewById(R.id.txBlock);
        txCluster = findViewById(R.id.txCluster);
        txt_select_school = findViewById(R.id.txt_select_school);
        linBackSchool = findViewById(R.id.linBackSchool);
        txReport = findViewById(R.id.txReport);
        // listView.setNestedScrollingEnabled(true);
        spnReport.setAdapter(new ArrayAdapter<String>(this, R.layout.spinnertextview, getResources().getStringArray(R.array.reportType)));
    /*     SquidCursor<QuestionGroup> QuestionGroupCursor = null;
       Query listQGquery;
        if (Constants.surveyType == 1) {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.ID.eq(surveyId)).limit(1);
        } else {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.ID.eq(surveyId)).limit(1);
        }
        QuestionGroupCursor = db.query(QuestionGroup.class, listQGquery);

        try {
            if (QuestionGroupCursor.moveToNext()) {
                questionGroupId = QuestionGroupCursor.get(QuestionGroup.ID);
                surveyTypeId = QuestionGroupCursor.get(QuestionGroup.ID);

                Log.d("shri", "SURVEY ID:" + surveyId);
                QuestionGroupCursor = db.query(QuestionGroup.class, listQGquery);
            }
        } finally {
            QuestionGroupCursor.close();
        }*/


        if (type.equals("report")) {
            bt_report.setVisibility(View.VISIBLE);
            start_date.setVisibility(View.VISIBLE);
            end_date.setVisibility(View.VISIBLE);
            reportType.setVisibility(View.VISIBLE);
            txReport.setText(getResources().getString(R.string.selectReportLevel));
            listView.setVisibility(View.GONE);

            linBackSchool.setVisibility(View.GONE);
            linSpinSchool.setVisibility(View.VISIBLE);

        } else if (type.equals("response")) {
            bt_report.setVisibility(View.GONE);
            start_date.setVisibility(View.GONE);
            end_date.setVisibility(View.GONE);
            reportType.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            linBackSchool.setVisibility(View.VISIBLE);
            linSpinSchool.setVisibility(View.GONE);

        } else if (type.equals("liststories")) {
            bt_report.setText(getResources().getString(R.string.getAllSurveystories));
            bt_report.setVisibility(View.VISIBLE);
            start_date.setVisibility(View.VISIBLE);
            end_date.setVisibility(View.VISIBLE);
            reportType.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            linBackSchool.setVisibility(View.GONE);
            /*sp_district.setVisibility(View.GONE);
            sp_block.setVisibility(View.GONE);
            sp_cluster.setVisibility(View.GONE);
            txBlock.setVisibility(View.GONE);
            txCluster.setVisibility(View.GONE);*/

            linSpinSchool.setVisibility(View.VISIBLE);
        }

        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        cdate = c.get(Calendar.DAY_OF_MONTH);
        cmonth = c.get(Calendar.MONTH);
        chour = c.get(Calendar.HOUR_OF_DAY);
        cminute = c.get(Calendar.MINUTE);

        edate = System.currentTimeMillis();
        // Toast.makeText(getApplicationContext(),edate+")))",Toast.LENGTH_SHORT).show();

        ((EditText) findViewById(R.id.end_date)).setText(String.format("%02d-%02d-%04d", cdate, cmonth + 1, cyear));
      /*  if (cmonth < 5) {
            sdate = milliseconds("01-05-" + String.valueOf(cyear - 1));
            ((EditText) findViewById(R.id.start_date)).setText("01-05-" + String.valueOf(cyear - 1));
        } else {
            sdate = milliseconds("01-05-" + String.valueOf(cyear));
            ((EditText) findViewById(R.id.start_date)).setText("01-05-" + String.valueOf(cyear));
        }*/
        EditText editText = findViewById(R.id.start_date);


        ((EditText) findViewById(R.id.start_date)).setText(String.format("%02d-%02d-%04d", getTempCalendar(c).get(Calendar.DAY_OF_MONTH), (getTempCalendar(c).get(Calendar.MONTH) + 1), getTempCalendar(c).get(Calendar.YEAR)));
        sdate = milliseconds(editText.getText().toString().trim());


        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int year = cyear;
                int month = cmonth;
                int date = cdate;
                try {
                    EditText editText = findViewById(R.id.start_date);
                    Date datetemp = new SimpleDateFormat("dd-MM-yyyy").parse(editText.getText().toString().trim());
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTime(datetemp);
                    year = tempCal.get(Calendar.YEAR);
                    date = tempCal.get(Calendar.DAY_OF_MONTH);
                    month = tempCal.get(Calendar.MONTH);
                } catch (ParseException e) {
                    e.printStackTrace();

                }


                setdate(null, year, month, date, R.id.start_date);
            }
        });
        editText = findViewById(R.id.end_date);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int year = cyear;
                int month = cmonth;
                int date = cdate;
                try {
                    EditText editText = findViewById(R.id.end_date);
                    Date datetemp = new SimpleDateFormat("dd-MM-yyyy").parse(editText.getText().toString().trim());
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTime(datetemp);
                    year = tempCal.get(Calendar.YEAR);
                    date = tempCal.get(Calendar.DAY_OF_MONTH);
                    month = tempCal.get(Calendar.MONTH);
                } catch (ParseException e) {
                    e.printStackTrace();

                }


                setdate(null, year, month, date, R.id.end_date);
            }
        });

        if (surveyId == 0 || questionGroupId == 0 || TextUtils.isEmpty(surveyName)) {
            Intent intent = new Intent(BoundarySelectionActivity.this, SurveyTypeActivity.class);
            startActivity(intent);
        }

        //    Toast.makeText(getApplicationContext(),sp_district.getId()+"::"+sharedPreferences.getInt("district",0),Toast.LENGTH_SHORT).show();

        fill_dropdown(1, sp_district.getId(), 1);
       /* sp_district.setSelection(sharedPreferences.getInt("district", 0));
        sp_block.setSelection(sharedPreferences.getInt("block", 0));
        sp_cluster.setSelection(sharedPreferences.getInt("cluster", 0));*/


        TextView textViewName = findViewById(R.id.textViewSurveyName);
        //textViewName.setText(survey.getName());

        textViewName.setText(surveyName);


      /*  TextView textViewPartner = (TextView) findViewById(R.id.textViewSurveyPartner);
        textViewPartner.setText(survey.getPartner());*/


        spnReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switchReportType(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (type.equals("liststories")) {

            switchReportType(spnReport.getSelectedItemPosition());
            bt_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    b = false;
                    changedate();
                    Intent intent = new Intent(BoundarySelectionActivity.this, StoriesActivity.class);


                    if (spnReport.getSelectedItemPosition() == 0) {
                        intent.putExtra("boundary", district.toUpperCase());
                        //     Toast.makeText(getApplicationContext(), "District Level Not Yet Implemented", Toast.LENGTH_SHORT).show();
                        b = isDistrictLevelDataFound();
                        dismissPRogressDialog();
                    } else if (spnReport.getSelectedItemPosition() == 1) {
                        intent.putExtra("boundary", district.toUpperCase() + ", " + block.toUpperCase());
                        b = BlockLevelDataFound(blockid);
                        dismissPRogressDialog();
                    } else if (spnReport.getSelectedItemPosition() == 2) {
                        intent.putExtra("boundary", district.toUpperCase() + ", " + block.toUpperCase() + ", " + cluster.toUpperCase());
                        b = getIsSummaryDataFound(surveyId, surveyName, bid, sdate, edate);
                        dismissPRogressDialog();
                    } else if (spnReport.getSelectedItemPosition() == 3) {
                        if (schoolList.size() > 0) {
                            b = getSchoolLevelDataFound(schoolList.get(select_school.getSelectedItemPosition()).id.toString());
                            dismissPRogressDialog();
                            if (b) {
                                intent.putExtra("boundary", district.toUpperCase() + ", " + block.toUpperCase() + ", " + cluster.toUpperCase() + "\n" + select_school.getSelectedItem().toString());
                                intent.putExtra("schoolID", schoolList.get(select_school.getSelectedItemPosition()).id.toString());
                            }
                        } else {
                            dismissPRogressDialog();
                            showDialogConstant(getResources().getString(R.string.noschoolfound));
                        }

                    } else {
                        dismissPRogressDialog();
                    }

                    if (b) {
                        //Its for get Stories
                        intent.putExtra("surveyId", surveyId);
                        intent.putExtra("surveyName", surveyName);
                        intent.putExtra("bid", bid);
                        intent.putExtra("sdate", sdate);
                        intent.putExtra("edate", edate);
                        intent.putExtra("blockid", blockid);
                        intent.putExtra("ditrcitId", distrciId);
                        intent.putExtra("surveyType", surveyTypeId);
                        intent.putExtra("questionGroupId", questionGroupId);

                        intent.putExtra("reporttype", spnReport.getSelectedItemPosition());
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }
            });
        } else {
            bt_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reportLevelBoundary(0);
                    if (spnReport.getSelectedItemPosition() == 3) {
                        if (schoolList != null && schoolList.size() > 0) {

                            if (reportLevelBoundary(spnReport.getSelectedItemPosition()) == -1) {
                                DailogUtill.showDialog("Please load all district level data and create report", getSupportFragmentManager(), getApplicationContext());

                            } else {
                                String schoolId = schoolList.get(select_school.getSelectedItemPosition()).id.toString();
                                startActivityScreen(schoolId);
                            }

                        } else {
                            //  showDialogConstant(getResources().getString(R.string.noschoolfound));
                            DailogUtill.showDialog("Please load all district level data and create report", getSupportFragmentManager(), getApplicationContext());

                        }
                    } else {
                        if (reportLevelBoundary(spnReport.getSelectedItemPosition()) == -1) {
                            DailogUtill.showDialog("Please load all district level data and create report", getSupportFragmentManager(), getApplicationContext());

                        } else {
                            startActivityScreen("no");
                            overridePendingTransition(0, 0);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            fill_dropdown(1, sp_district.getId(), 1);
            //   sp_district.setSelection(sharedPreferences.getInt("district", 0));
        } catch (Exception e) {
            //loading to fail
        }


    }

    public int reportLevelBoundary(int position) {
        int flag = -1;
        switch (position) {

            case 0:
                flag = sp_district.getSelectedItemPosition();
                break;

            case 1:

                flag = sp_block.getSelectedItemPosition();

                break;

            case 2:
                flag = sp_cluster.getSelectedItemPosition();

                break;

            case 3:
                flag = select_school.getSelectedItemPosition();
                break;
        }


        return flag;
    }


    public void dismissPRogressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    public void startActivityScreen(String schoolId) {
        Intent intent = new Intent(BoundarySelectionActivity.this, ReportsActivity.class);
        intent.putExtra("surveyId", surveyId);
        intent.putExtra("surveyName", surveyName);
        intent.putExtra("bid", bid);
        intent.putExtra("boundary", district.toUpperCase() + ", " + block.toUpperCase() + ", " + cluster.toUpperCase());
        changedate();
        intent.putExtra("imageRequired", isImageRequired);
        intent.putExtra("sdate", sdate);
        intent.putExtra("edate", edate);
        intent.putExtra("blockid", blockid);
        intent.putExtra("ditrcitId", distrciId);
        intent.putExtra("clusterId", bid);
        intent.putExtra("schoolId", schoolId);

        intent.putExtra("surveyType", surveyTypeId);
        intent.putExtra("questionGroupId", questionGroupId);


        intent.putExtra("createReportLevel", spnReport.getSelectedItemPosition());

        //  Toast.makeText(getApplicationContext(),bid+"",Toast.LENGTH_SHORT).show();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void switchReportType(int selectedItemPosition) {

        switch (selectedItemPosition) {
            case 0:
                sp_district.setVisibility(View.VISIBLE);
                sp_block.setVisibility(View.GONE);
                txBlock.setVisibility(View.GONE);
                sp_cluster.setVisibility(View.GONE);
                txCluster.setVisibility(View.GONE);
                select_school.setVisibility(View.GONE);
                txt_select_school.setVisibility(View.GONE);

                break;
            case 1:
                sp_district.setVisibility(View.VISIBLE);
                sp_block.setVisibility(View.VISIBLE);
                txBlock.setVisibility(View.VISIBLE);
                sp_cluster.setVisibility(View.GONE);
                txCluster.setVisibility(View.GONE);
                select_school.setVisibility(View.GONE);
                txt_select_school.setVisibility(View.GONE);

                break;
            case 2:
                sp_district.setVisibility(View.VISIBLE);
                sp_block.setVisibility(View.VISIBLE);
                txBlock.setVisibility(View.VISIBLE);
                sp_cluster.setVisibility(View.VISIBLE);
                txCluster.setVisibility(View.VISIBLE);
                select_school.setVisibility(View.GONE);
                txt_select_school.setVisibility(View.GONE);

                break;

            case 3:
                sp_district.setVisibility(View.VISIBLE);
                sp_block.setVisibility(View.VISIBLE);
                txBlock.setVisibility(View.VISIBLE);

                sp_cluster.setVisibility(View.VISIBLE);
                txCluster.setVisibility(View.VISIBLE);
                select_school.setVisibility(View.VISIBLE);
                txt_select_school.setVisibility(View.VISIBLE);

                break;
        }


    }

    private boolean getIsSummaryDataFound(long surveyId, String surveyName, Long bid, Long sdate, Long edate) {


        SquidCursor<School> schoolsInBdryCursor = db.query(School.class, Query.select().from(School.TABLE).where(School.BOUNDARY_ID.eq(bid)));
        List<Long> schids = new ArrayList<Long>();

        try {
            while (schoolsInBdryCursor.moveToNext()) {
                School sch = new School(schoolsInBdryCursor);
                schids.add(sch.getId());
            }
        } finally {
            schoolsInBdryCursor.close();
        }
        if (schids != null && schids.size() > 0) {
            Query listStoryQuery = Query.select()
                    .from(Story.TABLE)
                    .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate)
                            .and(Story.SCHOOL_ID.in(schids))
                            .and(Story.GROUP_ID.eq(questionGroupId))
                            .and(Story.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection()))))
                    .orderBy(Story.CREATED_AT.desc());
            SquidCursor<Story> storyCursor = db.query(Story.class, listStoryQuery);

            if (storyCursor.getCount() > 0) {
                Constants.listStoryQuery = listStoryQuery;

                return true;
            } else {

                showDialogConstant(getResources().getString(R.string.noSummarydetail));

                return false;
            }

        } else

            showDialogConstant(getResources().getString(R.string.noschoolfound));


        return false;


    }


    public boolean getSchoolLevelDataFound(String schoolId) {
        Query listStoryQuery = Query.select()
                .from(Story.TABLE)
                .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate)
                        .and(Story.SCHOOL_ID.eq(schoolId))
                        .and(Story.GROUP_ID.eq(questionGroupId))
                        .and(Story.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection()))))
                .orderBy(Story.CREATED_AT.desc());
        SquidCursor<Story> storyCursor = db.query(Story.class, listStoryQuery);

        if (storyCursor.getCount() > 0) {
            //story found at school level
            Constants.listStoryQuery = listStoryQuery;
            return true;
        } else {
            //no stories at school level
            Constants.listStoryQuery = listStoryQuery;
            //  showDialogConstant("No summary detail found");
            return true;
        }

    }

    public boolean BlockLevelDataFound(String id) {


        SquidCursor<Boundary> boundaryCursor = db.query(Boundary.class, Query.select().from(Boundary.TABLE).where(Boundary.PARENT_ID.eq(id).and(Boundary.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection()))));
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
                    List<Long> school_ids = new ArrayList<Long>();
                    //clusters available for block _id
                    try {
                        while (schoolcursor.moveToNext()) {
                            school_ids.add(new School(schoolcursor).getId());

                        }
                    } finally {
                        schoolcursor.close();
                    }

                    if (school_ids.size() > 0) {

                        Query listStoryQuery1 = Query.select()
                                .from(Story.TABLE)
                                .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate).and(Story.SCHOOL_ID.in(school_ids))
                                        .and(Story.GROUP_ID.eq(questionGroupId))).and(Story.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection())))
                                .orderBy(Story.CREATED_AT.desc());
                        SquidCursor<Story> storyCursor1 = db.query(Story.class, listStoryQuery1);

                        if (storyCursor1.getCount() > 0) {
                            //stories found for bloack level
                            Constants.listStoryQuery = listStoryQuery1;
                            return true;
                        } else {

                            showDialogConstant(getResources().getString(R.string.noSummarydetail));

                            //no stories found
                            return false;
                        }
                    } else {

                        showDialogConstant(getResources().getString(R.string.noschoolfound));

                        return false;
                        //no schools found
                    }
                } else {

                    showDialogConstant(getResources().getString(R.string.noschoolfound));
                    return false;
                    //no schools found
                }
            } else {

                showDialogConstant(getResources().getString(R.string.noClusterfound));

                return false;
                //no clusters for block

            }
        } else {

            showDialogConstant(getResources().getString(R.string.noblock));
            return false;
            //no boundary for block
        }

    }


    public boolean isDistrictLevelDataFound() {

        SquidCursor<Boundary> BlocktBoundaryCursor = db.query(Boundary.class, Query.select().from(Boundary.TABLE).where(Boundary.PARENT_ID.eq(distrciId).and(Boundary.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection()))));
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
                        .where(Boundary.PARENT_ID.in(block_ids).and(Boundary.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection())));
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
                        // Toast.makeText(getApplicationContext(),cluster_ids.size()+"",Toast.LENGTH_SHORT).show();
                        Query BlocklistStoryQuery = Query.select()
                                .from(School.TABLE)
                                .where(School.BOUNDARY_ID.in(cluster_ids));
                        SquidCursor<School> schoolcursor = db.query(School.class, BlocklistStoryQuery);

                        //  Toast.makeText(getApplicationContext(),schoolcursor.getCount()+"",Toast.LENGTH_SHORT).show();
                        if (schoolcursor.getCount() > 0) {
                            List<Long> school_ids = new ArrayList<Long>();
                            //clusters available for block _id
                            try {
                                while (schoolcursor.moveToNext()) {
                                    school_ids.add(new School(schoolcursor).getId());

                                }
                            } finally {
                                schoolcursor.close();
                            }

                            if (school_ids.size() > 0) {
                                //Toast.makeText(getApplicationContext(),school_ids.size()+"-school",Toast.LENGTH_SHORT).show();

                                Query listStoryQuery1 = Query.select()
                                        .from(Story.TABLE)
                                        .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate).and(Story.SCHOOL_ID.in(school_ids)).and(Story.GROUP_ID.eq(questionGroupId))).and(Story.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection())))
                                        .orderBy(Story.CREATED_AT.desc());
                                SquidCursor<Story> storyCursor1 = db.query(Story.class, listStoryQuery1);


                                if (storyCursor1.getCount() > 0) {
                                    //stories found
                                    Constants.listStoryQuery = listStoryQuery1;


                                    return true;
                                } else {

                                    showDialogConstant(getResources().getString(R.string.noSummarydetail));

                                    return false;
                                    //stories not found
                                }

                            } else {
                                //  //no schools found for district
                                showDialogConstant(getResources().getString(R.string.noschoolfound));

                                return false;
                            }

                        } else {
                            //no schools found for district
                            showDialogConstant(getResources().getString(R.string.noschoolfound));
                            return false;

                        }


                    } else {
                        //no clusters for block
                        showDialogConstant(getResources().getString(R.string.noClusterfound));

                        return false;
                    }


                } else {
                    //no clusters found for selected district
                    showDialogConstant(getResources().getString(R.string.noClusterfound));

                    return false;
                }


            } else {
                showDialogConstant(getResources().getString(R.string.noblock));

                return false;
                //no blocks for distrct
            }


        } else {
            showDialogConstant(getResources().getString(R.string.noblock));

            //no blocks founds for distrcit
            return false;
        }


    }


    private void changedate() {
        EditText editText = findViewById(R.id.start_date);
        if (!editText.getText().toString().equals(""))
            sdate = milliseconds(editText.getText().toString());
        editText = findViewById(R.id.end_date);

        if (!editText.getText().toString().equals("")) {
            String[] alter_date = editText.getText().toString().split("\\-");
            String addstr = String.valueOf(Integer.parseInt(alter_date[0]) + 1) + "-" + alter_date[1] + "-" + alter_date[2];
            //found that date getting increment 1 in previous code so removed +1
            //String addstr = String.valueOf(Integer.parseInt(alter_date[0]) ) + "-" + alter_date[1] + "-" + alter_date[2];
            edate = milliseconds(addstr);
            //Toast.makeText(getApplicationContext(),editText.getText().toString()+"mmm",Toast.LENGTH_SHORT).show();


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setup_menu, menu);

        return true;
    }


    public void setup(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), TempLoading.class));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public long milliseconds(String date) {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date mDate = sdf.parse(date.trim());
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    private void setdate(DatePicker view, int y, int m, int d, int id) {
        DatePickerDialog dpd;
        editText = findViewById(id);
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
            }
        }, y, m, d);
        try {
            //dpd.getDatePicker().setMaxDate(new Date().getTime());
            Calendar maxCal = Calendar.getInstance();

            maxCal.set(Calendar.YEAR, maxCal.get(Calendar.YEAR));
            dpd.getDatePicker().setMaxDate(maxCal.getTimeInMillis() + 1 * 1000);
            //   dpd.getDatePicker().setMaxDate(maxCal.getTimeInMillis() - 1000);
            // dpd.getDatePicker().setMinDate();
        } catch (Exception e) {
        }
        dpd.show();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        SharedPreferences sharedPreferences = getSharedPreferences("boundary", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringWithTags boundaryForSelector = (StringWithTags) parent.getItemAtPosition(pos);
        int viewid = parent.getId();
        switch (viewid) {
            case R.id.select_district:
                fill_dropdown(1, R.id.select_block, Integer.parseInt(boundaryForSelector.id.toString()));
                fill_dropdown(1, R.id.select_cluster, Integer.parseInt(boundaryForSelector.id.toString()));
                fill_schools(R.id.school_list, Integer.parseInt(boundaryForSelector.id.toString()));
                fill_SchoolsForSpinner(Integer.parseInt(boundaryForSelector.id.toString()));
                editor.putInt("district", pos);
                //  Toast.makeText(getApplicationContext(),"district",Toast.LENGTH_SHORT).show();
                district = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                distrciId = ((StringWithTags) parent.getItemAtPosition(pos)).id.toString();
                // Toast.makeText(getApplicationContext(), distrciId + ":district", Toast.LENGTH_SHORT).show();

                break;
            case R.id.select_block:
                fill_dropdown(1, R.id.select_cluster, Integer.parseInt(boundaryForSelector.id.toString()));
                editor.putInt("block", pos);
                //     Toast.makeText(getApplicationContext(),"block",Toast.LENGTH_SHORT).show();
                block = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                blockid = (((StringWithTags) parent.getItemAtPosition(pos)).id.toString());
                //   Toast.makeText(getApplicationContext(), blockid + ":block", Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_cluster:
                fill_schools(R.id.school_list, Integer.parseInt(boundaryForSelector.id.toString()));
                //   Toast.makeText(getApplicationContext(),"school",Toast.LENGTH_SHORT).show();
                fill_SchoolsForSpinner(Integer.parseInt(boundaryForSelector.id.toString()));
                cluster = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                editor.putInt("cluster", pos);
                bid = new Long(((StringWithTags) parent.getItemAtPosition(pos)).id.toString());
                //  Toast.makeText(getApplicationContext(), bid + ":cluster", Toast.LENGTH_SHORT).show();
                break;
        }
        editor.commit();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void fill_dropdown(int type, int id, int parent) {
        List<StringWithTags> stringWithTags = get_boundary_data(parent);
          Spinner spinner = findViewById(id);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<StringWithTags> boundaryArrayAdapter = new ArrayAdapter<StringWithTags>(this, R.layout.spinnertextview, stringWithTags);
        spinner.setAdapter(boundaryArrayAdapter);
        boundaryArrayAdapter.setDropDownViewResource(R.layout.spinnertextview);
    }

    private void fill_SchoolsForSpinner(int parent) {
        schoolList = get_school_data(parent);
        select_school.setAdapter(new ArrayAdapter<StringWithTags>(this, R.layout.spinnertextview, schoolList));

       /* if(schoolList.size()<=0)
        {
            showDialogConstant("No Schools found");
        }*/


    }


    private void fill_schools(int id, int parent) {
        ListView listView = findViewById(id); //nothing
        List<StringWithTags> schoolList = get_school_data(parent);
        final ArrayAdapter<StringWithTags> schoolArrayAdapter = new ArrayAdapter<StringWithTags>(this, R.layout.schoollisttextview, schoolList);
        listView.setAdapter(schoolArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(BoundarySelectionActivity.this, QuestionActivity.class);
                intent.putExtra("surveyId", Long.parseLong(surveyId + ""));
                intent.putExtra("ILPQuestionGroupId", Long.parseLong(questionGroupId + ""));
                intent.putExtra("surveyName", surveyName);
                intent.putExtra("imageRequired", isImageRequired);

                intent.putExtra("schoolId", new Long(schoolArrayAdapter.getItem(i).id.toString()));
              /*  intent.putExtra("lat", schoolArrayAdapter.getItem(i).lat);
                intent.putExtra("lng", schoolArrayAdapter.getItem(i).lng);
*/
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                //finish();

            }
        });

    }

    private List<StringWithTags> get_boundary_data(int parent) {
        Query listboundary = Query.select().from(Boundary.TABLE)
                .where(Boundary.PARENT_ID.eq(parent).and(Boundary.TYPE.eq("primaryschool").and(Boundary.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection()))))
                .orderBy(Boundary.NAME.asc());

        List<StringWithTags> boundaryList = new ArrayList<StringWithTags>();
        boundary_cursor = db.query(Boundary.class, listboundary);
        if (boundary_cursor.moveToFirst()) {
            do {
                Boundary b = new Boundary(boundary_cursor);
                if ((b.getHierarchy().equalsIgnoreCase("district") || b.getHierarchy().equalsIgnoreCase("block"))&&b.isFlag()==true) {
                    StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), sessionManager,b.isFlag(),b.isFlagCB());
                    boundaryList.add(boundary);

                } else {
                    if (!b.getHierarchy().equalsIgnoreCase("district") && !b.getHierarchy().equalsIgnoreCase("block")) {
                        StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), sessionManager,b.isFlag(),b.isFlagCB());
                        boundaryList.add(boundary);
                    }
                }
            } while (boundary_cursor.moveToNext());
        }
        if (boundary_cursor != null)
            boundary_cursor.close();
        return boundaryList;
    }


    public String getLocTextBoundary(Boundary b) {
        if (sessionManager.getLanguagePosition() <= 1) {
            //english
            return b.getName() != null ? b.getName() : b.getLocName();

        } else {
            //native
            return b.getLocName() != null ? b.getLocName() : b.getName();
        }
    }

    private List<StringWithTags> get_school_data(int parent) {
        Query listschool = Query.select().from(School.TABLE)
                .where(School.BOUNDARY_ID.eq(parent));
        List<StringWithTags> schoolList = new ArrayList<StringWithTags>();
        school_cursor = db.query(School.class, listschool);
        if (school_cursor.moveToFirst()) {
            do {
                School sch = new School(school_cursor);
                StringWithTags school = new StringWithTags(sch.getName(), sch.getId(), 1, true, sessionManager);
                schoolList.add(school);
            } while (school_cursor.moveToNext());
        }
        if (school_cursor != null)
            school_cursor.close();
        return schoolList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
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


    public void showDialogConstant(String msg) {
        dialogConstants = new DialogConstants(BoundarySelectionActivity.this, msg);
        dialogConstants.show();

    }

    public Calendar getTempCalendar(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        return c;
    }

}
