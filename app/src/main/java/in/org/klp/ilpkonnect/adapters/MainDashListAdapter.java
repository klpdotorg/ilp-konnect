package in.org.klp.ilpkonnect.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.org.klp.ilpkonnect.BoundarySelectionActivity;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.MainDashList;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.SummaryDateScreen;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;

/**
 * Created by shridhars on 8/1/2017.
 */

public class MainDashListAdapter extends RecyclerView.Adapter<MainDashListAdapter.MainDashViewHolder> {
    String[] menues;
    int[] icons;
    int[] ids;
    MainDashList mainDashList;
    long surveyId,questionGroupId;
    String surveyName;
    boolean isImageRequired;
    public MainDashListAdapter(String[] menues, int[] icons, int[] ids, MainDashList mainDashList, long surveyId,long questionGroupId, String surveyName,boolean isImageRequired) {
        this.menues = menues;
        this.icons = icons;
        this.ids = ids;
        this.mainDashList = mainDashList;
        this.surveyId = surveyId;
        this.surveyName = surveyName;
        this.questionGroupId=questionGroupId;
        this.isImageRequired=isImageRequired;
        notifyDataSetChanged();
    }

    @Override
    public MainDashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainDashList).inflate(R.layout.main_dash_adapter, parent, false);
        MainDashViewHolder mainDashViewHolder = new MainDashViewHolder(view);
        return mainDashViewHolder;

    }

    @Override
    public void onBindViewHolder(MainDashViewHolder holder, final int position) {

        holder.adpButton.setText(menues[position]);
      Drawable top;
        // //= ContextCompat.getDrawable(mainDashList, icons[position]);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            top = VectorDrawableCompat.create(mainDashList.getResources(), icons[position], mainDashList.getTheme());
        } else {
            top = mainDashList.getResources().getDrawable(icons[position], mainDashList.getTheme());
        }


        holder.adpButton.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
        // holder.adpButton.setBackgroundResource(icons[position]);


        holder.adpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeScreen(ids[position]);
                // Toast.makeText(mainDashList,ids[position]+"",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void changeScreen(int id) {

        switch (id) {
            case 1:



                    Intent intent = new Intent(mainDashList, BoundarySelectionActivity.class);
                    intent.putExtra("surveyId", surveyId);
                    intent.putExtra("ILPQuestionGroupId", questionGroupId);
                    intent.putExtra("surveyName", surveyName);
                    intent.putExtra("type", "response");
                    intent.putExtra("imageRequired",isImageRequired);
                    mainDashList.startActivity(intent);
                    mainDashList. overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);



                //New Response
                break;
            case 2:

                if (AppStatus.isConnected(mainDashList)) {
                    //mainDashList.sync1(false);
                    mainDashList. downloadAll();





                } else {
                /*    DialogConstants dialogConstants = new DialogConstants(mainDashList, mainDashList.getResources().getString(R.string.noInternetCon));
                    dialogConstants.show();*/
                    mainDashList.showSignupResultDialog(mainDashList.getResources().getString(R.string.app_name),mainDashList.getResources().getString(R.string.noInternetCon),mainDashList.getResources().getString(R.string.Ok));
                }


                //Downlaod Survey
                break;
            case 3:
                //create Report
                if(surveyId==13||surveyId==12){

                    DailogUtill.showDialog(mainDashList.getResources().getString(R.string.reportsNotgenerated),mainDashList.getSupportFragmentManager(),mainDashList);

                }else {
                    Intent intent2 = new Intent(mainDashList, BoundarySelectionActivity.class);
                    intent2.putExtra("surveyId", surveyId);
                    intent2.putExtra("ILPQuestionGroupId", questionGroupId);
                    intent2.putExtra("surveyName", surveyName);
                    intent2.putExtra("type", "report");
                    intent2.putExtra("imageRequired", isImageRequired);

                    mainDashList.startActivity(intent2);
                    mainDashList.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
                break;
            case 4:
                if (AppStatus.isConnected(mainDashList)) {
                //    mainDashList.sync1(true);
                   mainDashList.newSync();











                } else {
                   int storyCount= mainDashList.getStoryCount();
                    if(storyCount>0)
                    {
                       // DialogConstants dialogConstants = new DialogConstants(mainDashList, mainDashList.getResources().getString(R.string.noInternetCon)+"\n"+"You have taken "+storyCount+" surveys,Please sync when your in internet zone");
                       // dialogConstants.show();
                        //mainDashList.showSignupResultDialog(mainDashList.getString(R.string.app_name), mainDashList.getResources().getString(R.string.noInternetCon)+",\n"+storyCount+" "+mainDashList.getResources().getString(R.string.survey_taken),mainDashList.getResources().getString(R.string.Ok));

                        mainDashList.showSignupResultDialog(mainDashList.getString(R.string.app_name), mainDashList.getResources().getString(R.string.noInternetCon)+",\n"+String.format(mainDashList.getResources().getString(R.string.survey_taken), storyCount),mainDashList.getResources().getString(R.string.Ok));


                    }else {
                        //DialogConstants dialogConstants = new DialogConstants(mainDashList, mainDashList.getResources().getString(R.string.dataAlreadynSyn));
                       // dialogConstants.show();
                        mainDashList.showSignupResultDialog(mainDashList.getString(R.string.app_name),mainDashList.getResources().getString(R.string.dataAlreadynSyn),mainDashList.getResources().getString(R.string.Ok));

                    }
                }

                break;
            //Sync survey
            case 5:
            /*    Intent intent1 = new Intent(mainDashList, BoundarySelectionActivity.class);
                intent1.putExtra("surveyId", surveyId);
                intent1.putExtra("surveyName", surveyName);
                intent1.putExtra("type", "liststories");
                mainDashList.startActivity(intent1);
                mainDashList. overridePendingTransition(0, 0);*/





                Intent mySummaryIntent = new Intent(mainDashList, SummaryDateScreen.class);
                mySummaryIntent.putExtra("surveyId", surveyId);
                mySummaryIntent.putExtra("ILPQuestionGroupId", questionGroupId);
                mySummaryIntent.putExtra("surveyName", surveyName);
                mySummaryIntent.putExtra("stateID", 1l);
                mainDashList.startActivity(mySummaryIntent);
                mainDashList. overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


                //Summary
                break;

            case 6:
                // my Summary
                /*Intent mySummaryIntent = new Intent(mainDashList, SummaryActiivity.class);
                mySummaryIntent.putExtra("surveyId", surveyId);
                mySummaryIntent.putExtra("surveyName", surveyName);
                mySummaryIntent.putExtra("stateID", "1");
                mainDashList.startActivity(mySummaryIntent);
                mainDashList. overridePendingTransition(0, 0);
*/
                break;

            default:
                break;


        }

    }

    @Override
    public int getItemCount() {
        return menues.length;
    }

    public class MainDashViewHolder extends RecyclerView.ViewHolder

    {
        Button adpButton;

        public MainDashViewHolder(View itemView) {
            super(itemView);
            adpButton = itemView.findViewById(R.id.adpButton);

        }
    }


}
