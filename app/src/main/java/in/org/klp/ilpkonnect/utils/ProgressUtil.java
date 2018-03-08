package in.org.klp.ilpkonnect.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by shridhars on 2/2/2018.
 */

public class ProgressUtil {


    static  ProgressDialog dialog;

    public static ProgressDialog showProgress(Context context,String message) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false); // if you want people to be able to cancel the download
      /* dialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog)
            {

            }}); */
        return dialog;
    }

    public static void updateProgress(String message)
    {
        dialog.setMessage(message);
    }





}
