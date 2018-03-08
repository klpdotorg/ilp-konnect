package in.org.klp.ilpkonnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.BuildConfig;
import in.org.klp.ilpkonnect.LoadSurveyImage;
import in.org.klp.ilpkonnect.Pojo.ImagesPOJO;
import in.org.klp.ilpkonnect.R;

/**
 * Created by shridhars on 8/28/2017.
 */

public class ImageLoadAdapter extends RecyclerView.Adapter<ImageLoadAdapter.ImageViewHolder>{

    Context context;
    ImagesPOJO body ;
    ArrayList<String> imageurl;

    public ImageLoadAdapter(Context context, ImagesPOJO body) {
        this.context=context;
        this.body=body;
    }

    public ImageLoadAdapter() {

    }

    public ImageLoadAdapter(LoadSurveyImage context, ArrayList<String> imageurl) {
        this.imageurl=imageurl;
        this.context=context;

    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.image_load,parent,false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
if(imageurl!=null) {
    Glide.with(context)
            .load(BuildConfig.HOST + imageurl.get(position))
                .into(holder.imageView)
    .onLoadFailed(context.getResources().getDrawable(R.drawable.loadingmark) )
    ;

}else {
    holder.imageView.setImageDrawable(null);
}



    }

    @Override
    public int getItemCount() {
        if(imageurl!=null) {
            return imageurl.size();
        }
        return 0;
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ImageLoadAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ImageLoadAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
















    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.imageView);

        }
    }
}
