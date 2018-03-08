package in.org.klp.ilpkonnect.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import in.org.klp.ilpkonnect.R;

/**
 * Created by shridhars on 1/26/2018.
 */

public class DailogUtill extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle args = getArguments();
        builder.setMessage(args.getString("result"))
                 .setPositiveButton(args.getString("buttonText"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       /* if(args.getString("buttonText").equalsIgnoreCase(getResources().getString(R.string.login))) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().finish();
                            startActivity(intent);
                        }*/
                        dialog.dismiss();
                    }
                });

        //   .setTitle(args.getString("title", ""))


        // Create the AlertDialog object and return it
        return builder.create();
    }



    public static void showDialog(String message,  FragmentManager manager,Context context) {
        Bundle dialogBundle = new Bundle();
        dialogBundle.putString("title", context.getString(R.string.app_name));
        dialogBundle.putString("result", message);
        dialogBundle.putString("buttonText", context.getString(R.string.Ok));

        DailogUtill resultDialog = new DailogUtill();
        resultDialog.setArguments(dialogBundle);
        resultDialog.setCancelable(false);
        try {
            resultDialog.show(manager, " Result");
        }catch (Exception e)
        {
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }

    }



}