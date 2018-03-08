package in.org.klp.ilpkonnect;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 1/23/2018.
 */

public class SurveyTypeAdapter extends RecyclerView.Adapter<SurveyTypeAdapter.SurveyTypeHolder> {

    SurveyTypeActivity activity;
    ArrayList<Survey> surveysList;
    SessionManager sessionManager;

    public SurveyTypeAdapter(SurveyTypeActivity activity, ArrayList<Survey> surveysList,SessionManager sessionManager) {
        this.activity=activity;
        this.surveysList=surveysList;
        notifyDataSetChanged();
        this.sessionManager=sessionManager;
    }

    @Override
    public SurveyTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.surveylistadapter,parent,false);

        return new SurveyTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(SurveyTypeHolder holder, final int position) {

        if(sessionManager.getLanguagePosition()<=1)
        {
            holder.btnsurveylist.setText(surveysList.get(position).getName());
        }
        else
            holder.btnsurveylist.setText(surveysList.get(position).getNameLoc());



    holder.btnsurveylist.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(activity, MainDashList.class);
            intent.putExtra("ILPSurveyId", surveysList.get(position).getId());
            intent.putExtra("ILPQuestionGroupId", surveysList.get(position).getQuestionGroupId());
                       intent.putExtra("imageRequired",surveysList.get(position).isImageRequired());
            if(sessionManager.getLanguagePosition()<=1)
            {
                intent.putExtra("surveyName", surveysList.get(position).getName());
            }
            else
                intent.putExtra("surveyName", surveysList.get(position).getNameLoc());

             activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    });

    }

    @Override
    public int getItemCount() {
        return surveysList.size();
    }

    class SurveyTypeHolder extends RecyclerView.ViewHolder {

       Button  btnsurveylist;
        public SurveyTypeHolder(View itemView) {
            super(itemView);
            btnsurveylist= itemView.findViewById(R.id.btnsurveylist);
        }
    }
}
