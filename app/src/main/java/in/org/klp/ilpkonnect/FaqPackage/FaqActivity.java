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
        faqList.add(new FaqPojo("Question -1 ", "Answer -1"));
        faqList.add(new FaqPojo("Question -2 ", "Answer -2"));
        faqList.add(new FaqPojo("Question -3 ", "Answer -3"));
        faqList.add(new FaqPojo("Question -4 ", "Answer -4"));
        faqList.add(new FaqPojo("Question -5 ", "Answer -5"));
        faqList.add(new FaqPojo("Question -6 ", "Answer -6"));
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
