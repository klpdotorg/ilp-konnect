package in.org.klp.ilpkonnect.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import in.org.klp.ilpkonnect.R;

/**
 * Created by shridhars on 7/13/2017.
 */

public class DialogConstants extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes;
    TextView txt_dia;
    String msg;

    public DialogConstants(Activity a, String msg) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.msg=msg;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.custom_dialog);
        txt_dia=(TextView)findViewById(R.id.txt_dia);
        txt_dia.setText(msg);
        yes = (Button) findViewById(R.id.btn_yes);
        yes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
               dismiss();
                break;


            default:
                break;
        }
        dismiss();
    }
}