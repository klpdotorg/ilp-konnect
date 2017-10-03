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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
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

import in.org.klp.ilpkonnect.data.StringWithTags;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.QuestionGroup;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DialogConstants;

public class BoundarySelectionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Long surveyId, bid, sdate = null, edate = null;
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
    private Long questionGroupId, surveyTypeId;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundary_selection);
        db = ((KLPApplication) getApplicationContext()).getDb();

        SharedPreferences sharedPreferences = getSharedPreferences("boundary", MODE_PRIVATE);
        progressDialog = new ProgressDialog(BoundarySelectionActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        this.setTitle(getResources().getString(R.string.app_name));
        surveyId = getIntent().getLongExtra("surveyId", 0);
        surveyName = getIntent().getStringExtra("surveyName");
        type = getIntent().getStringExtra("type");
        Button bt_report = (Button) findViewById(R.id.report_button);
        EditText start_date = (EditText) findViewById(R.id.start_date);
        EditText end_date = (EditText) findViewById(R.id.end_date);
        reportType = (LinearLayout) findViewById(R.id.reportType);
        final LinearLayout llBoundarySelect = (LinearLayout) findViewById(R.id.ll_select_boundary);
        final ListView listView = (ListView) findViewById(R.id.school_list);
        spnReport = (Spinner) findViewById(R.id.spnReport);
        sp_district = (Spinner) findViewById(R.id.select_district);
        sp_block = (Spinner) findViewById(R.id.select_block);
        sp_cluster = (Spinner) findViewById(R.id.select_cluster);
        select_school = (Spinner) findViewById(R.id.select_school);
        linSpinSchool = (LinearLayout) findViewById(R.id.linSpinSchool);
        txBlock = (TextView) findViewById(R.id.txBlock);
        txCluster = (TextView) findViewById(R.id.txCluster);
        txt_select_school = (TextView) findViewById(R.id.txt_select_school);
        linBackSchool = (LinearLayout) findViewById(R.id.linBackSchool);
        txReport = (TextView) findViewById(R.id.txReport);
        // listView.setNestedScrollingEnabled(true);
        spnReport.setAdapter(new ArrayAdapter<String>(this,R.layout.spinnertextview,getResources().getStringArray(R.array.reportType)));
        SquidCursor<QuestionGroup> QuestionGroupCursor = null;
        Query listQGquery;
        if (Constants.surveyType == 1) {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.SURVEY_ID.eq(surveyId).and(QuestionGroup.SURVEY_TYPE.eq(1))).limit(1);
        } else {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.SURVEY_ID.eq(surveyId).and(QuestionGroup.SURVEY_TYPE.eq(2))).limit(1);
        }
        QuestionGroupCursor = db.query(QuestionGroup.class, listQGquery);

        try {
            if (QuestionGroupCursor.moveToNext()) {
                questionGroupId = QuestionGroupCursor.get(QuestionGroup.ID);
                surveyTypeId = QuestionGroupCursor.get(QuestionGroup.SURVEY_TYPE);

                Log.d("shri", "SURVEY ID:" + surveyId);
                QuestionGroupCursor = db.query(QuestionGroup.class, listQGquery);
            }
        } finally {
            QuestionGroupCursor.close();
        }

//Toast.makeText(getApplicationContext(),questionGroupId+"",Toast.LENGTH_SHORT).show();


        listView.setScrollContainer(false);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            private boolean toolbarCollapsed = false;
            private String lastScrollDirection = "DOWN";

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                /* scrollStat can be -->
                int	SCROLL_STATE_FLING
                    The user had previously been scrolling using touch and had performed a fling.
                int	SCROLL_STATE_IDLE
                    The view is not scrolling.
                int	SCROLL_STATE_TOUCH_SCROLL
                    The user is scrolling using touch, and their finger is still on the screen
                 */
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                llHeight = listView.getHeight();
                if (mLastFirstVisibleItem < firstVisibleItem) {
                    Log.i("SCROLLING DOWN", "TRUE");
                    if (llBoundarySelect.getVisibility() == LinearLayout.VISIBLE && !toolbarCollapsed && lastScrollDirection == "DOWN") {
                        //collapse(llBoundarySelect);
                        toolbarCollapsed = true;
                    }
                    lastScrollDirection = "DOWN";

                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    Log.i("SCROLLING UP", "TRUE");
                    if (llBoundarySelect.getVisibility() == LinearLayout.GONE && toolbarCollapsed && lastScrollDirection == "UP") {
                        //expand(llBoundarySelect);
                        toolbarCollapsed = false;
                    }
                    lastScrollDirection = "UP";
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });

        if (type.equals("report")) {
            bt_report.setVisibility(View.VISIBLE);
            start_date.setVisibility(View.VISIBLE);
            end_date.setVisibility(View.VISIBLE);
            reportType.setVisibility(View.VISIBLE);
            txReport.setText(getResources().getString(R.string.selectReportLevel));
            listView.setVisibility(View.GONE);
        /*    sp_district.setVisibility(View.VISIBLE);
            sp_block.setVisibility(View.VISIBLE);
            sp_cluster.setVisibility(View.VISIBLE);
          */
            linBackSchool.setVisibility(View.GONE);
            linSpinSchool.setVisibility(View.VISIBLE);

        } else if (type.equals("response")) {
            bt_report.setVisibility(View.GONE);
            start_date.setVisibility(View.GONE);
            end_date.setVisibility(View.GONE);
            reportType.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            linBackSchool.setVisibility(View.VISIBLE);
           /* sp_district.setVisibility(View.VISIBLE);
            sp_block.setVisibility(View.VISIBLE);
            sp_cluster.setVisibility(View.VISIBLE);*/
            linSpinSchool.setVisibility(View.GONE);

        } else if (type.equals("liststories")) {
            bt_report.setText("Get All Stories");
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

        ((EditText) findViewById(R.id.end_date)).setText(String.format("%d-%d-%d", cdate, cmonth + 1, cyear));
        if (cmonth < 5) {
            sdate = milliseconds("01-05-" + String.valueOf(cyear - 1));
            ((EditText) findViewById(R.id.start_date)).setText("01-05-" + String.valueOf(cyear - 1));
        } else {
            sdate = milliseconds("01-05-" + String.valueOf(cyear));
            ((EditText) findViewById(R.id.start_date)).setText("01-05-" + String.valueOf(cyear));
        }
        EditText editText = (EditText) findViewById(R.id.start_date);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdate(null, cyear, cmonth, cdate, R.id.start_date);
            }
        });
        editText = (EditText) findViewById(R.id.end_date);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdate(null, cyear, cmonth, cdate, R.id.end_date);
            }
        });

        if (surveyId == 0 | TextUtils.isEmpty(surveyName)) {
            Intent intent = new Intent(BoundarySelectionActivity.this, SurveyTypeActivity.class);
            startActivity(intent);
        }


        fill_dropdown(1, sp_district.getId(), 1);
        sp_district.setSelection(sharedPreferences.getInt("district", 0));

        sp_block.setSelection(sharedPreferences.getInt("block", 0));
        sp_cluster.setSelection(sharedPreferences.getInt("cluster", 0));

        Survey survey = db.fetch(Survey.class, surveyId);

        TextView textViewName = (TextView) findViewById(R.id.textViewSurveyName);
        textViewName.setText(survey.getName());

        TextView textViewPartner = (TextView) findViewById(R.id.textViewSurveyPartner);
        textViewPartner.setText(survey.getPartner());


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
                            showDialogConstant("No schools found currently");
                        }

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

                    if (spnReport.getSelectedItemPosition() == 3) {
                        if (schoolList.size() > 0) {

                            String schoolId = schoolList.get(select_school.getSelectedItemPosition()).id.toString();
                            startActivityScreen(schoolId);


                        } else {
                            showDialogConstant("No schools found currently");
                        }
                    } else {
                        startActivityScreen("no");
                         overridePendingTransition(0, 0);
                    }
                }
            });
        }
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
        overridePendingTransition(0, 0);
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

    private boolean getIsSummaryDataFound(Long surveyId, String surveyName, Long bid, Long sdate, Long edate) {


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
                    .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate).and(Story.SCHOOL_ID.in(schids)).and(Story.GROUP_ID.eq(questionGroupId))))
                    .orderBy(Story.CREATED_AT.desc());
            SquidCursor<Story> storyCursor = db.query(Story.class, listStoryQuery);

            if (storyCursor.getCount() > 0) {
                Constants.listStoryQuery = listStoryQuery;

                return true;
            } else {

                showDialogConstant("No summary detail found");

                return false;
            }

        } else

            showDialogConstant("No schools found currently");


        return false;


    }


    public boolean getSchoolLevelDataFound(String schoolId) {
        Query listStoryQuery = Query.select()
                .from(Story.TABLE)
                .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate).and(Story.SCHOOL_ID.eq(schoolId)).and(Story.GROUP_ID.eq(questionGroupId))))
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


        SquidCursor<Boundary> boundaryCursor = db.query(Boundary.class, Query.select().from(Boundary.TABLE).where(Boundary.PARENT_ID.eq(id)));
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
                                .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate).and(Story.SCHOOL_ID.in(school_ids)).and(Story.GROUP_ID.eq(questionGroupId))))
                                .orderBy(Story.CREATED_AT.desc());
                        SquidCursor<Story> storyCursor1 = db.query(Story.class, listStoryQuery1);

                        if (storyCursor1.getCount() > 0) {
                            //stories found for bloack level
                            Constants.listStoryQuery = listStoryQuery1;
                            return true;
                        } else {

                            showDialogConstant("No summary detail found");

                            //no stories found
                            return false;
                        }
                    } else {

                        showDialogConstant("No schools found currently");

                        return false;
                        //no schools found
                    }
                } else {

                    showDialogConstant("No schools found currently");
                    return false;
                    //no schools found
                }
            } else {

                showDialogConstant("No clusters found for Block");

                return false;
                //no clusters for block

            }
        } else {

            showDialogConstant("No boundaries found for Blocks");
            return false;
            //no boundary for block
        }

    }


    public boolean isDistrictLevelDataFound() {

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
                                        .where(Story.CREATED_AT.gte(sdate).and(Story.CREATED_AT.lte(edate).and(Story.SCHOOL_ID.in(school_ids)).and(Story.GROUP_ID.eq(questionGroupId))))
                                        .orderBy(Story.CREATED_AT.desc());
                                SquidCursor<Story> storyCursor1 = db.query(Story.class, listStoryQuery1);


                                if (storyCursor1.getCount() > 0) {
                                    //stories found
                                    Constants.listStoryQuery = listStoryQuery1;


                                    return true;
                                } else {

                                    showDialogConstant("No summary detail found");

                                    return false;
                                    //stories not found
                                }

                            } else {
                                //  //no schools found for district
                                showDialogConstant("No schools found currently");

                                return false;
                            }

                        } else {
                            //no schools found for district
                            showDialogConstant("No schools found currently");
                            return false;

                        }


                    } else {
                        //no clusters for block
                        showDialogConstant("No clusters found for block");

                        return false;
                    }


                } else {
                    //no clusters found for selected district
                    showDialogConstant("No clusters found for selected district");

                    return false;
                }


            } else {
                showDialogConstant("No blocks found for district");

                return false;
                //no blocks for distrct
            }


        } else {
            showDialogConstant("No blocks founds for distrcit");

            //no blocks founds for distrcit
            return false;
        }


    }


    private void changedate() {
        EditText editText = (EditText) findViewById(R.id.start_date);
        if (!editText.getText().toString().equals(""))
            sdate = milliseconds(editText.getText().toString());
        editText = (EditText) findViewById(R.id.end_date);

        if (!editText.getText().toString().equals("")) {
            String[] alter_date = editText.getText().toString().split("\\-");
            String addstr = String.valueOf(Integer.parseInt(alter_date[0]) + 1) + "-" + alter_date[1] + "-" + alter_date[2];
            //found that date getting increment 1 in previous code so removed +1
            //String addstr = String.valueOf(Integer.parseInt(alter_date[0]) ) + "-" + alter_date[1] + "-" + alter_date[2];
            edate = milliseconds(addstr);
            //Toast.makeText(getApplicationContext(),editText.getText().toString()+"mmm",Toast.LENGTH_SHORT).show();


        }

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
        editText = (EditText) findViewById(id);
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
            }
        }, y, m, d);
        try {
            //dpd.getDatePicker().setMaxDate(new Date().getTime());
            Calendar maxCal = Calendar.getInstance();
            maxCal.set(Calendar.YEAR, maxCal.get(Calendar.YEAR) );
            dpd.getDatePicker().setMaxDate(maxCal.getTimeInMillis()-1000);
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
                editor.putInt("district", pos);
                district = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                distrciId = ((StringWithTags) parent.getItemAtPosition(pos)).id.toString();

                break;
            case R.id.select_block:
                fill_dropdown(1, R.id.select_cluster, Integer.parseInt(boundaryForSelector.id.toString()));
                editor.putInt("block", pos);
                block = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                blockid = (((StringWithTags) parent.getItemAtPosition(pos)).id.toString());
                break;
            case R.id.select_cluster:
                fill_schools(R.id.school_list, Integer.parseInt(boundaryForSelector.id.toString()));

                fill_SchoolsForSpinner(Integer.parseInt(boundaryForSelector.id.toString()));
                cluster = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                editor.putInt("cluster", pos);
                bid = new Long(((StringWithTags) parent.getItemAtPosition(pos)).id.toString());
                break;
        }
        editor.commit();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void fill_dropdown(int type, int id, int parent) {
        List<StringWithTags> stringWithTags = get_boundary_data(parent);
        Spinner spinner = (Spinner) findViewById(id);
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
        ListView listView = (ListView) findViewById(id); //nothing
        List<StringWithTags> schoolList = get_school_data(parent);
        final ArrayAdapter<StringWithTags> schoolArrayAdapter = new ArrayAdapter<StringWithTags>(this, android.R.layout.simple_list_item_1, schoolList);
        listView.setAdapter(schoolArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(BoundarySelectionActivity.this, QuestionActivity.class);
                intent.putExtra("surveyId", surveyId);
                intent.putExtra("surveyName", surveyName);
                intent.putExtra("schoolId", new Long(schoolArrayAdapter.getItem(i).id.toString()));
                intent.putExtra("lat",schoolArrayAdapter.getItem(i).lat);
                intent.putExtra("lng",schoolArrayAdapter.getItem(i).lng);

                startActivity(intent);
                 overridePendingTransition(0, 0);
                //finish();

            }
        });

    }

    private List<StringWithTags> get_boundary_data(int parent) {
        Query listboundary = Query.select().from(Boundary.TABLE)
                .where(Boundary.PARENT_ID.eq(parent).and(Boundary.TYPE.eq("primaryschool")))
                .orderBy(Boundary.NAME.asc());

        List<StringWithTags> boundaryList = new ArrayList<StringWithTags>();
        boundary_cursor = db.query(Boundary.class, listboundary);
        if (boundary_cursor.moveToFirst()) {
            do {
                Boundary b = new Boundary(boundary_cursor);
                StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId());
                boundaryList.add(boundary);
            } while (boundary_cursor.moveToNext());
        }
        if (boundary_cursor != null)
            boundary_cursor.close();
        return boundaryList;
    }

    private List<StringWithTags> get_school_data(int parent) {
        Query listschool = Query.select().from(School.TABLE)
                .where(School.BOUNDARY_ID.eq(parent));
        List<StringWithTags> schoolList = new ArrayList<StringWithTags>();
        school_cursor = db.query(School.class, listschool);
        if (school_cursor.moveToFirst()) {
            do {
                School sch = new School(school_cursor);
                StringWithTags school = new StringWithTags(sch.getName(), sch.getId(), 1, true,sch.getLat(),sch.getLng());
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

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void showDialogConstant(String msg) {
        dialogConstants = new DialogConstants(BoundarySelectionActivity.this, msg);
        dialogConstants.show();

    }
}
