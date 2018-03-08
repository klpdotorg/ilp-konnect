package in.org.klp.ilpkonnect.Pojo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
 
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.BuildConfig;
import in.org.klp.ilpkonnect.R;


 
 
public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<String> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
 
    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.imageslider, container, false);
        viewPager = v.findViewById(R.id.vpPager);
        /*lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);*/
 
        images = (ArrayList<String>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");
 
       /* Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());
 */
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
 
        setCurrentItem(selectedPosition);
 
        return v;
    }
 
    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }
 
    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
 
        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }
 
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
 
        }
 
        @Override
        public void onPageScrollStateChanged(int arg0) {
 
        }
    };
 
    private void displayMetaInfo(int position) {
//        lblCount.setText((position + 1) + " of " + images.size());
 
        String image = images.get(position);
      /*  lblTitle.setText(image.getName());
        lblDate.setText(image.getTimestamp());*/
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
 
    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {
 
        private LayoutInflater layoutInflater;
 
        public MyViewPagerAdapter() {
        }
 
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
 
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.fullscreenimage, container, false);
 
            ImageView imageViewPreview = view.findViewById(R.id.image_preview);
 
             String url = images.get(position);
 
            Glide.with(getActivity()).load(BuildConfig.HOST+url)

                    .into(imageViewPreview).onLoadFailed((getActivity().getResources().getDrawable(R.drawable.loadingmark) ));
 
            container.addView(view);
 
            return view;
        }
 
        @Override
        public int getCount() {
            return images.size();
        }
 
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
 
 
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}