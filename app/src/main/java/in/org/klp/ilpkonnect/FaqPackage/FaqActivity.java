package in.org.klp.ilpkonnect.FaqPackage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import in.org.klp.ilpkonnect.BaseActivity;
import in.org.klp.ilpkonnect.R;

/**
 * Created by shridhars on 3/6/2018.
 */

public class FaqActivity extends BaseActivity {

    FaqAdapter faqAdapter;
    RecyclerView faqlistviw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faqlistview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        faqlistviw = (RecyclerView) findViewById(R.id.faqlist);
        faqlistviw.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        ArrayList<FaqPojo> faqList = new ArrayList<>();
        faqList.add(new FaqPojo(getResources().getString(R.string.question1), getResources().getString(R.string.answer1)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question2), getResources().getString(R.string.answer2)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question3), getResources().getString(R.string.answer3)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question4), getResources().getString(R.string.answer4)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question5), getResources().getString(R.string.answer5)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question6), getResources().getString(R.string.answer6)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question7), getResources().getString(R.string.answer7)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question8), getResources().getString(R.string.answer8)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question9), getResources().getString(R.string.answer9)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question10), getResources().getString(R.string.answer10)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question11), getResources().getString(R.string.answer11)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question12), getResources().getString(R.string.answer12)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question13), getResources().getString(R.string.answer13)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question14), getResources().getString(R.string.answer14)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question15), getResources().getString(R.string.answer15)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question16), getResources().getString(R.string.answer16)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question17), getResources().getString(R.string.answer17)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question18), getResources().getString(R.string.answer18)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question19), getResources().getString(R.string.answer19)));
        faqList.add(new FaqPojo(getResources().getString(R.string.question20), getResources().getString(R.string.answer20)));

        FaqAdapter adapter = new FaqAdapter(FaqActivity.this, faqList);
        faqlistviw.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:


              this.  finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.  finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
