package in.org.klp.ilpkonnect.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import in.org.klp.ilpkonnect.BoundarySelectionActivity;
import in.org.klp.ilpkonnect.MainDashList;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DialogConstants;

import static java.security.AccessController.getContext;

/**
 * Created by shridhars on 8/1/2017.
 */

public class MainDashListAdapter extends RecyclerView.Adapter<MainDashListAdapter.MainDashViewHolder> {
    String[] menues;
    int[] icons;
    int[] ids;
    MainDashList mainDashList;
    Long surveyId;
    String surveyName;

    public MainDashListAdapter(String[] menues, int[] icons, int[] ids, MainDashList mainDashList, Long surveyId, String surveyName) {
        this.menues = menues;
        this.icons = icons;
        this.ids = ids;
        this.mainDashList = mainDashList;
        this.surveyId = surveyId;
        this.surveyName = surveyName;
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


                if (Constants.surveyType == 2) {
                   // mainDashList.callSetLocation();
                   // if (mainDashList.isLocationEnabled(mainDashList)) {
                    if(mainDashList.callForGPS()) {
                        Intent intent = new Intent(mainDashList, BoundarySelectionActivity.class);
                        intent.putExtra("surveyId", surveyId);
                        intent.putExtra("surveyName", surveyName);
                        intent.putExtra("type", "response");
                        mainDashList.startActivity(intent);
                        mainDashList. overridePendingTransition(0, 0);
                    }
                    else {
                        Toast.makeText(mainDashList.getApplicationContext(),"Please Enable Location/GPS",Toast.LENGTH_SHORT).show();
                    }
                   /* } else {

                        //  mainDashList.callSetLocation();
                        mainDashList.startLocationUpdates();
                        Toast.makeText(mainDashList, "Please Enable Location to take survey", Toast.LENGTH_SHORT).show();
                    }*/

                } else {
                    Intent intent = new Intent(mainDashList, BoundarySelectionActivity.class);
                    intent.putExtra("surveyId", surveyId);
                    intent.putExtra("surveyName", surveyName);
                    intent.putExtra("type", "response");
                    mainDashList.startActivity(intent);
                    mainDashList. overridePendingTransition(0, 0);
                }


                //New Response
                break;
            case 2:

                if (AppStatus.isConnected(mainDashList)) {
                    mainDashList.sync1(false);
                } else {
                    DialogConstants dialogConstants = new DialogConstants(mainDashList, mainDashList.getResources().getString(R.string.noInternetCon));
                    dialogConstants.show();
                }


                //Downlaod Survey
                break;
            case 3:
                //create Report
                Intent intent2 = new Intent(mainDashList, BoundarySelectionActivity.class);
                intent2.putExtra("surveyId", surveyId);
                intent2.putExtra("surveyName", surveyName);
                intent2.putExtra("type", "report");
                mainDashList.startActivity(intent2);
                mainDashList. overridePendingTransition(0, 0);
                break;
            case 4:
                if (AppStatus.isConnected(mainDashList)) {
                    mainDashList.sync1(true);

                } else {
                    DialogConstants dialogConstants = new DialogConstants(mainDashList, mainDashList.getResources().getString(R.string.noInternetCon));
                    dialogConstants.show();
                }

                break;
            //Sync survey
            case 5:
                Intent intent1 = new Intent(mainDashList, BoundarySelectionActivity.class);
                intent1.putExtra("surveyId", surveyId);
                intent1.putExtra("surveyName", surveyName);
                intent1.putExtra("type", "liststories");
                mainDashList.startActivity(intent1);
                mainDashList. overridePendingTransition(0, 0);
                //Summary
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
            adpButton = (Button) itemView.findViewById(R.id.adpButton);

        }
    }


}
