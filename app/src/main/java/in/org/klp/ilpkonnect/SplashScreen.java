package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.db.DatabaseCopyHelper;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.State;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.ProgressUtil;
import in.org.klp.ilpkonnect.utils.SessionManager;


public class SplashScreen extends BaseActivity {

    TextView tv2;
    ImageView tv1;

    ProgressDialog progressDialog;
    KontactDatabase db;
    private SessionManager mSession;
    ProgressDialog dailog;
    Button btnInternt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);
        mSession = new SessionManager(getApplicationContext());
        DatabaseCopyHelper dbCopyHelper = new DatabaseCopyHelper(this);
        SQLiteDatabase dbCopy = dbCopyHelper.getReadableDatabase();
        db = ((KLPApplication) getApplicationContext()).getDb();
        btnInternt= findViewById(R.id.btnInternt);
        dailog = ProgressUtil.showProgress(SplashScreen.this, getResources().getString(R.string.authenticating));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        }


        // check state and language if user first time login

        if (mSession.isLoggedIn()) {
            //langauge screen
            if(getStateCount()==0) {
               loadStateDeatil();
            }else {
                if(mSession.isLoggedIn()&&  mSession.isSetupDone()) {
                    Intent intent = new Intent(getApplicationContext(), SurveyTypeActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
                else {
                    Intent intent = new Intent(SplashScreen.this, TempLoading.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }

            //direct it survey screen
        } else {
            loadStateDeatil();


        }


        btnInternt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadStateDeatil();
            }
        });


    }


    public void loadStateDeatil() {
        dailog.show();
        new ProNetworkSettup(SplashScreen.this).getStateAndUserDeail(new StateInterface() {
            @Override
            public void success(String message) {

                dailog.dismiss();
                Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }

            @Override
            public void failed(String message) {

                dailog.dismiss();
                if(AppStatus.isConnected(getApplicationContext())) {
                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    btnInternt.setVisibility(View.VISIBLE);
                }
                else
                {
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                btnInternt.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        tv1 = findViewById(R.id.tv1);
        tv1.clearAnimation();
        tv1.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.transalte);
        anim.reset();
        tv2 = findViewById(R.id.tv2);
        tv2.clearAnimation();
        tv2.startAnimation(anim);


    }


    public int getStateCount() {
        try {
            Query listStateQuery = Query.select().from(State.TABLE);
            SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);

            if (stateCursor != null && stateCursor.getCount() > 0)
                return stateCursor.getCount();
            else
                return 0;
        }catch (Exception e)
        {
            return 0;
        }

    }
}


