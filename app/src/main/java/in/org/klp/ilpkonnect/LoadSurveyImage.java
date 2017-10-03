package in.org.klp.ilpkonnect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.Pojo.SlideshowDialogFragment;
import in.org.klp.ilpkonnect.adapters.ImageLoadAdapter;

/**
 * Created by shridhars on 8/29/2017.
 */

public class LoadSurveyImage extends AppCompatActivity {



    RecyclerView recyclerImage;
    ArrayList<String> imageurl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadsurveyimages);
        recyclerImage=(RecyclerView)findViewById(R.id.recyclerImage);

        imageurl = (ArrayList<String>) getIntent().getSerializableExtra("image");



        recyclerImage.setLayoutManager(new LinearLayoutManager(this));
        ImageLoadAdapter imageLoadAdapter= new ImageLoadAdapter(this,imageurl);
        recyclerImage.setAdapter(imageLoadAdapter);
        imageLoadAdapter.notifyDataSetChanged();



        recyclerImage.addOnItemTouchListener(new ImageLoadAdapter.RecyclerTouchListener(getApplicationContext(), recyclerImage, new ImageLoadAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("images", imageurl);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = new SlideshowDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}

