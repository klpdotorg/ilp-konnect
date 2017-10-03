package in.org.klp.ilpkonnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.org.klp.ilpkonnect.Pojo.SurveyMain;
import in.org.klp.ilpkonnect.R;

/**
 * Created by bibhas on 6/18/16.
 */
public class SurveyAdapter extends ArrayAdapter<SurveyMain> {
    private Context _context;
    private ArrayList<SurveyMain> surveys;

    // View lookup cache
    private static class SurveyHolder {
        TextView name;
        TextView partner;
    }

    public SurveyAdapter(ArrayList<SurveyMain> surveys, Context context) {
        super(context, R.layout.list_item_survey, surveys);
        this._context = context;
        this.surveys = surveys;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SurveyMain survey = getItem(position);
        SurveyHolder surveyHolder = new SurveyHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_item_survey, parent, false);
        surveyHolder.partner = (TextView) convertView.findViewById(R.id.textViewPartner);
        surveyHolder.partner.setText(survey.getPartener());

        return convertView;
    }
}
