package in.org.klp.ilpkonnect;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class QuestionActivity extends BaseActivity {







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
     //   this.setTitle(getResources().getString(R.string.title_activity_question));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity();

                return true;
        }
        return super.onOptionsItemSelected(item);
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


}