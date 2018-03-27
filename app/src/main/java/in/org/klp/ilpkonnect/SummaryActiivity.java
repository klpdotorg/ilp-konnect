package in.org.klp.ilpkonnect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.db.KontactDatabase;

import in.org.klp.ilpkonnect.db.MySummary;

import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.SessionManager;

public class SummaryActiivity extends BaseActivity {

    TextView tvNameSurvey, tvTotalSurveySynced, tvTotalSchoolSurveyd, tvTotalSurveyedPendingsync;
    KontactDatabase db;
    long surveyId, stateID,questiongroup;
    String surveyName;
    String survey;
    long oneDay = 86400000;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    int cyear, cmonth, cdate, chour, cminute;
    String from = "", end = "";
    TextView tvdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_actiivity);
        idInit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        //question group it is
        surveyId = getIntent().getLongExtra("surveyId", 0);
        questiongroup = getIntent().getLongExtra("ILPQuestionGroupId", 0);


       // Toast.makeText(getApplicationContext(),surveyId+":"+questiongroup,Toast.LENGTH_SHORT).show();
        surveyName = getIntent().getStringExtra("surveyName");
        stateID = getIntent().getLongExtra("stateID", 0);
        from = getIntent().getStringExtra("from");
        end = getIntent().getStringExtra("end");


        tvdate = findViewById(R.id.tvdate);
        fetchData(surveyId, sessionManager.getStateSelection(), from, end);
        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        cdate = c.get(Calendar.DAY_OF_MONTH);
        cmonth = c.get(Calendar.MONTH);
        chour = c.get(Calendar.HOUR_OF_DAY);
        cminute = c.get(Calendar.MINUTE);


        if (AppStatus.isConnected(SummaryActiivity.this)) {

            mySync();
        }


    }

    public String getOneDayExstra(String date) {


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(date));
        } catch (Exception e) {

        }
        cal.add(Calendar.DATE, 1);

        return dateFormat.format(cal.getTime());
    }

    private void idInit() {
        progressDialog = new ProgressDialog(SummaryActiivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        tvNameSurvey = findViewById(R.id.tvNameSurvey);
        tvTotalSurveySynced = findViewById(R.id.tvTotalSurveySynced);
        tvTotalSchoolSurveyd = findViewById(R.id.tvTotalSchoolSurveyd);
        tvTotalSurveyedPendingsync = findViewById(R.id.tvTotalSurveyedPendingsync);
        db = ((KLPApplication) getApplicationContext()).getDb();

    }



    private void fetchData(long id, String statekey, String fromData, String endDate) {
        Query mySummaryQuery = Query.select().from(MySummary.TABLE)
                .where(MySummary.SURVEYID.eq(id).and(MySummary.STATE_KEY.eqCaseInsensitive(statekey)));

        SquidCursor<MySummary> mySummaryCursor = db.query(MySummary.class, mySummaryQuery);


        tvTotalSurveyedPendingsync.setText(CheckPendingSync() + "");


        if (mySummaryCursor != null && mySummaryCursor.getCount() > 0) {

            if (mySummaryCursor.moveToNext()) {
                MySummary summary = new MySummary(mySummaryCursor);
                //summary.getSurveyname()
                tvNameSurvey.setText(surveyName);
                tvTotalSurveySynced.setText(summary.getSurveysynced() + "");
                tvTotalSchoolSurveyd.setText(summary.getSchoolsurveyed() + "");

                tvdate.setText(getDDMMYYdate(getDate(summary.getFromdate(), "yyyy-MM-dd")) +" "+ getResources().getString(R.string.to) + "-" + getDDMMYYdate(getDate(summary.getEnddate(), "yyyy-MM-dd")));


                //tvTotalSurveyedPendingsync.setText(storyByUserNSCursor.getCount() + "");


                return;
            }


        } else {

        }

    }

    public String CheckPendingSync() {
        Query listStoryQuery = Query.select()
                .from(Story.TABLE)
                .where(Story.SYNCED.eq(0).and(Story.GROUP_ID.eq(questiongroup).and(Story.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection())))
                );

        SquidCursor<Story> storyByUserNSCursor = db.query(Story.class, listStoryQuery);
        if (storyByUserNSCursor != null)

            return storyByUserNSCursor.getCount() + "";
        return "0";

    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_summary_sync, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity();

                return true;

                case R.id.action_sync_block:
                    mySync();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void mySync() {
        progressDialog.show();
        final String fromd = getRevDate(from);
        final String endd = getRevDate(getOneDayExstra(end));

        //  Toast.makeText(getApplicationContext(),fromd+":"+endd,Toast.LENGTH_SHORT).show();

        //  startActivity(new Intent(getApplicationContext(),TempLoading.class));
        new ProNetworkSettup(getApplicationContext()).getMySummary(questiongroup, sessionManager.getStateSelection(), fromd, endd, sessionManager.getToken(),surveyId, new StateInterface() {
            @Override
            public void success(String message) {
                progressDialog.setMessage(message);
                fetchData(surveyId, sessionManager.getStateSelection(), from, end);
                closeProgress();
            }

            @Override
            public void failed(String message) {
                closeProgress();
                  DailogUtill.showDialog(message,getSupportFragmentManager(),getApplicationContext());
            }
        });

    }

    public void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity();
    }


    public void startActivity() {
       /* Intent intent = new Intent(getApplicationContext(), SummaryDateScreen.class);
        intent.putExtra("surveyId", surveyId);
        intent.putExtra("surveyName", surveyName);
        intent.putExtra("stateID", stateID);
        startActivity(intent);*/

        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

  /*  private void setEndDate(DatePicker view, int y, int m, int d) {
        DatePickerDialog dpd;

        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // edtDob.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                cyear = year;
                cdate = dayOfMonth;
                cmonth = monthOfYear;
                //   ReqDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                //  edtDob.setError(null);
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
    }*/


    public String getRevDate(String strDate) {
        String newstring = "1980-01-01";
        try {
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);


            newstring = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        } catch (Exception e) {

        }

        return newstring;
    }

}
