package in.org.klp.ilpkonnect;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SummaryDateScreen extends BaseActivity {


    int cyear, cmonth, cdate, chour, cminute;
    EditText edtFromDate, edtEndDate;
    EditText editText = null;
    Calendar c;


    long surveyId, stateID, questiongroup;
    Button btnOK;
    String surveyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_date_layout);

        edtFromDate = findViewById(R.id.edtFromDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        cdate = c.get(Calendar.DAY_OF_MONTH);
        cmonth = c.get(Calendar.MONTH);
        chour = c.get(Calendar.HOUR_OF_DAY);
        cminute = c.get(Calendar.MINUTE);
        btnOK = findViewById(R.id.btnOK);


        surveyId = getIntent().getLongExtra("surveyId", 0);
        questiongroup = getIntent().getLongExtra("ILPQuestionGroupId", 0);
        surveyName = getIntent().getStringExtra("surveyName");
        stateID = getIntent().getLongExtra("stateID", 0);

        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int year = cyear;
                int month = cmonth;
                int date = cdate;
                try {
                    Date datetemp = new SimpleDateFormat("dd-MM-yyyy").parse(edtFromDate.getText().toString().trim());
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTime(datetemp);
                    year = tempCal.get(Calendar.YEAR);
                    date = tempCal.get(Calendar.DAY_OF_MONTH);
                    month = tempCal.get(Calendar.MONTH);
                } catch (ParseException e) {
                    e.printStackTrace();
                }



                setdate(null, year, month, date, R.id.edtFromDate);
            }
        });

        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int year = cyear;
                int month = cmonth;
                int date = cdate;
                try {
                    Date datetemp = new SimpleDateFormat("dd-MM-yyyy").parse(edtEndDate.getText().toString().trim());
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTime(datetemp);
                    year = tempCal.get(Calendar.YEAR);
                    date = tempCal.get(Calendar.DAY_OF_MONTH);
                    month = tempCal.get(Calendar.MONTH);
                } catch (ParseException e) {
                    e.printStackTrace();

                }


                setdate(null, year, month,date, R.id.edtEndDate);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mySummaryIntent = new Intent(SummaryDateScreen.this, SummaryActiivity.class);
                mySummaryIntent.putExtra("surveyId", surveyId);
                mySummaryIntent.putExtra("ILPQuestionGroupId", questiongroup);
                mySummaryIntent.putExtra("surveyName", surveyName);
                mySummaryIntent.putExtra("stateID", "1");
                mySummaryIntent.putExtra("from", edtFromDate.getText().toString().trim());
                mySummaryIntent.putExtra("end", edtEndDate.getText().toString().trim());
                startActivity(mySummaryIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                // finish();
            }
        });

        ((EditText) findViewById(R.id.edtEndDate)).setText(String.format("%02d-%02d-%04d", cdate, cmonth + 1, cyear));

        //sdate = milliseconds("01-05-" + String.valueOf(cyear - 1));

        //((EditText) findViewById(R.id.edtFromDate)).setText("01-05-" + String.valueOf(cyear - 1));
        ((EditText) findViewById(R.id.edtFromDate)).setText(String.format("%02d-%02d-%04d", getTempCalendar(c).get(Calendar.DAY_OF_MONTH), (getTempCalendar(c).get(Calendar.MONTH) + 1), getTempCalendar(c).get(Calendar.YEAR)));


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
        }
        return super.onOptionsItemSelected(item);
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

    public Calendar getTempCalendar(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        return c;
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

            // dpd.getDatePicker().setMinDate();
        } catch (Exception e) {
        }
        dpd.show();
    }
}
