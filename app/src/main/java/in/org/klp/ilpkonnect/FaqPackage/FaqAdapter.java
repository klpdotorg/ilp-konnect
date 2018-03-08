package in.org.klp.ilpkonnect.FaqPackage;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import in.org.klp.ilpkonnect.R;

/**
 * Created by shridhars on 3/6/2018.
 */

class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqAdapterHolder> {


    FaqActivity faqActivity;
    ArrayList<FaqPojo> faqList;
    private static int currentPosition = -1;

    public FaqAdapter(FaqActivity faqActivity, ArrayList<FaqPojo> faqList) {
        this.faqActivity = faqActivity;
        this.faqList = faqList;
        currentPosition = -1;
    }

    @Override
    public FaqAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(faqActivity).inflate(R.layout.faqadapter_layout, parent, false);
        return new FaqAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(final FaqAdapterHolder holder, final int position) {

        holder.txtQuestion.setText(faqList.get(position).getFaqQuestion());
        holder.txtAnswer.setText(faqList.get(position).getFaqAnswer());
        holder.txtAnswer.setVisibility(View.GONE);
        if (currentPosition == position) {

            holder.txtAnswer.setVisibility(View.VISIBLE);

        }
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = position;
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    class FaqAdapterHolder extends RecyclerView.ViewHolder {

        TextView txtQuestion, txtAnswer;
        CardView cardview;

        public FaqAdapterHolder(View itemView) {
            super(itemView);
            txtQuestion = (TextView) itemView.findViewById(R.id.txtQuestion);
            txtAnswer = (TextView) itemView.findViewById(R.id.txtAnswer);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
