package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.sql.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import in.org.klp.ilpkonnect.DataLoad.TempLoading;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterfaceSync;
import in.org.klp.ilpkonnect.Pojo.VersionControlPojo;
import in.org.klp.ilpkonnect.adapters.MainDashListAdapter;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;

import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ILPService;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.SessionManager;
import in.org.klp.ilpkonnect.utils.SyncManager;
import needle.Needle;
import needle.UiRelatedProgressTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by shridhars on 8/1/2017.
 */

public class MainDashList extends BaseActivity {

    RecyclerView listDashboard;
    String menues[];
    int icons[];
    int ids[];
    MainDashListAdapter adapter;
    private DownloadTasks dt;
    private UploadTask ut;
    private ProgressDialog progressDialog = null;
    private KontactDatabase db;
    private OkHttpClient okclient;
    private HashMap<String, String> API_URLS = new HashMap<String, String>();
    TextView tvSettings, tvPartner;
    String res = "{\"stories\":[]}";
    private SessionManager mSession;
    long surveyId, questionGroupId;
    String surveyName, partner;
    boolean isImageRequired = false;
    MainDashList mainDashList;

    int flag = 0;
    private ProgressDialog progress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dash_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvPartner = findViewById(R.id.tvPartner);
        listDashboard = findViewById(R.id.listDashboard);
        mainDashList = MainDashList.this;
//

        Constants.mainDashList = MainDashList.this;


        db = ((KLPApplication) getApplicationContext()).getDb();


        surveyId = getIntent().getLongExtra("ILPSurveyId", 0);
        questionGroupId = getIntent().getLongExtra("ILPQuestionGroupId", 0);
        surveyName = getIntent().getStringExtra("surveyName");
        isImageRequired = getIntent().getBooleanExtra("imageRequired", false);
        //  Toast.makeText(this, surveyId+":"+surveyName, Toast.LENGTH_SHORT).show();
        if (surveyName != null && !surveyName.equalsIgnoreCase("")) {
            this.setTitle(surveyName);
        }
//        int a=2/0;
        //    Toast.makeText(getApplicationContext(),surveyId+":"+surveyName,Toast.LENGTH_SHORT).show();
        if (surveyId == 0 || surveyName.isEmpty() || questionGroupId == 0) {
            Toast.makeText(this, "Invalid Survey ID/Name", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainDashList.this, SurveyTypeActivity.class);
            startActivity(intent);
        }

        okclient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        mSession = new SessionManager(getApplicationContext());
        mSession.checkLogin();
        // Log user details to be used for crashlytics


      /*  menues = new String[]{getResources().getString(R.string.new_response), getResources().getString(R.string.download_survey), getResources().getString(R.string.generate_report), getResources().getString(R.string.syncSurvey), getResources().getString(R.string.summary)};
        icons = new int[]{R.drawable.ic_button_new_response, R.drawable.downloadicon, R.drawable.ic_button_report, R.drawable.ic_button_sync, R.drawable.ic_button_report};
        ids = new int[]{1, 2, 3, 4, 5};*/
        menues = new String[]{getResources().getString(R.string.new_response), getResources().getString(R.string.download_survey), getResources().getString(R.string.syncSurvey), getResources().getString(R.string.summary)};
        icons = new int[]{R.drawable.ic_button_new_response, R.drawable.downloadicon, R.drawable.ic_button_sync, R.drawable.ic_button_report};
        ids = new int[]{1, 2, 4, 5};


    /*   menues = new String[]{getResources().getString(R.string.new_response), getResources().getString(R.string.download_survey), getResources().getString(R.string.generate_report), getResources().getString(R.string.syncSurvey), getResources().getString(R.string.summary),getResources().getString(R.string.mySummary)};
        icons = new int[]{R.drawable.ic_button_new_response, R.drawable.downloadicon, R.drawable.ic_button_report, R.drawable.ic_button_sync,R.drawable.ic_button_report,R.drawable.ic_button_report};
         ids = new int[]{1, 2, 3, 4, 5,6}*/
        listDashboard.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MainDashListAdapter(menues, icons, ids, MainDashList.this, surveyId, questionGroupId, surveyName, isImageRequired);
        listDashboard.setAdapter(adapter);
        adapter.notifyDataSetChanged();

 /*       if (isSyncNeeded()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.youshouldsync))
                    .setTitle(getResources().getString(R.string.syncneeded));
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.oksyncnow), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    sync();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), SurveyTypeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), SurveyTypeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }





    public void sync1(final boolean var) {

        dt = new DownloadTasks();
        ut = new UploadTask();

        preSync(getResources().getString(R.string.uploading), getResources().getString(R.string.uploadingstorw));

        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<String, String>() {
            @Override
            protected String doWork() {

                publishProgress(getResources().getString(R.string.upload));
                if (var) {
                    publishProgress(" " + getResources().getString(R.string.datasync));
                    String data = doUploadForSyncSurvey().toString();
                    if (data.equalsIgnoreCase(res)) {

                        showSignupResultDialog(
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.dataAlreadynSync),
                                getResources().getString(R.string.Ok));
                        return null;
                    } else {


                        JSONObject jsonData = SyncDataCall(data);

                    //    Log.d("shri", "Res:" + jsonData.toString());
                        ArrayList<Integer> countData = ut.processUploadResponse(jsonData);
                        String msg = getResources().getString(R.string.noInternetCon);
                        if (countData != null && countData.size() >= 2) {
                            if (countData.get(0) > 0 && countData.get(1) > 0) {
                                msg = getResources().getString(R.string.surveysyncsucc) + countData.get(0) + "\n" + getResources().getString(R.string.surveysyncfail) + countData.get(1);
                            } else if (countData.get(0) > 0) {
                                msg = getResources().getString(R.string.surveysyncsucc) + countData.get(0);
                            } else if (countData.get(1) > 0) {
                                msg = getResources().getString(R.string.surveysyncfail) + countData.get(1);
                            }
                        }
                        // String msg = getResources().getString(R.string.surveysyncsucc) + countData.get(0) + "\n" + getResources().getString(R.string.surveysyncfail) + countData.get(1);
                        return msg;
                    }


                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(String s) {
                endSync();
                if (s != null && !s.equalsIgnoreCase("")) {
                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            s,
                            getResources().getString(R.string.Ok));

                }
            }

            @Override
            protected void onProgressUpdate(String s) {
                if (!s.equals(getResources().getString(R.string.upload))) {
                    if (!var) {
                        updateProgressDialog(getResources().getString(R.string.downloading), getResources().getString(R.string.downloading) + s + "..");
                    } else {
                        updateProgressDialog(getResources().getString(R.string.uploadingstorw), s + "..");

                    }
                }
            }
        });


    }

    public void sync2(String data, final int size, final int count_loop, final int surveys, ArrayList<JSONObject> jsondata) {

        // final String jsondata = doUploadForSyncSurvey().toString().trim();


        new ProNetworkSettup(getApplicationContext()).SyncData(data, mSession.getToken(), count_loop, size, jsondata, new StateInterfaceSync() {
            @Override
            public void success(String message) {
                updateSyncProgress(surveys);

                finishSyncProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());


            }

            @Override
            public void failed(String message) {
                //  if(size==count_loop) {
                finishSyncProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                //}
            }

            @Override
            public void update(int count, String message) {
                updateSyncProgress(count);
            }
        });
    }


    public void logoutUser() {
        mSession.logoutUser();
        this.finish();
    }


    public void newSync() {
        int count = getStoryCount();
        if (count > 0) {

            ArrayList<JSONObject> jsondata = doUploadForSyncSurvey();
          //  Log.d("shri","))))))))))))))))"+jsondata.toString().getBytes().length);
            if (jsondata != null && jsondata.size() > 0) {
                progressOnlySync(count);

                try {
                 //   Log.d("shri",jsondata.get(0)+"");
                    sync2(jsondata.get(0).toString().trim(), jsondata.size(), 0, count, jsondata);

                } catch (Exception e) {
                    Crashlytics.log(e.getMessage());
                    Crashlytics.logException(e);
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
                // }
            } else {
                DailogUtill.showDialog(getString(R.string.dataAlreadynSyn), getSupportFragmentManager(), getApplicationContext());
            }

        } else {
            DailogUtill.showDialog(getString(R.string.dataAlreadynSyn), getSupportFragmentManager(), getApplicationContext());

        }


    }


    public JSONObject SyncDataCall(String data) {
        JSONObject respJson = new JSONObject();
        final String SYNC_URL = BuildConfig.HOST + ILPService.SYNC;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        HashMap<String, String> user = mSession.getUserDetails();
        RequestBody body = RequestBody.create(JSON, data);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(SYNC_URL)
                .post(body)
                .addHeader("Authorization", "Token " + user.get("token"))
                .build();


        try {
            okhttp3.Response okresponse = okclient.newCall(request).execute();

            if (!okresponse.isSuccessful()) {
                //     log("Upload Error", "There is something wrong with the Internet connection.");
             //   Log.d("shri", "--" + okresponse.message());
                return new JSONObject(okresponse.message());
            }

            if (okresponse.code() == 401) {
                // log("Authentication Error", "Something went wrong. Please login again.");
                logoutUser();
            }

            return respJson = new JSONObject(okresponse.message());
        } catch (final IOException e) {
            e.printStackTrace();
            mainDashList.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // if (e.getMessage() != null) Log.d(this.toString(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();

            if (e.getMessage() != null) Log.d(this.toString(), e.getMessage());
        }
        return respJson;
    }

    public void log(String tag, String msg) {
        Log.d(tag, msg);
    }

    public void preSync(String title, String message) {
        // disable all buttons
        // show progress dialog
        // dismiss progress dialog if it's already showing
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainDashList.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        updateProgressDialog(title, message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

       /* Button survey_button = (Button) findViewById(R.id.survey_button);
        survey_button.setEnabled(false);
        survey_button.setAlpha(.5f);*/
    }

    public void updateProgressDialog(String title, String message) {
        if (progressDialog != null) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
    }

    public void endSync() {
        // enable all buttons
        // dismiss sync progress dialog
        if (progressDialog != null) {
           /* Button survey_button = (Button) findViewById(R.id.survey_button);
            survey_button.setEnabled(true);
            survey_button.setAlpha(1f);*/

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private ArrayList<JSONObject> doUploadForSyncSurvey() {


        Query listStoryQuery = Query.select().from(Story.TABLE)
                .where(Story.SYNCED.eq(0));
        SquidCursor<Story> storiesCursor = db.query(Story.class, listStoryQuery);
        SquidCursor<Answer> answerCursor = null;
        ArrayList<JSONObject> jsonDataList = new ArrayList<>();

        //  JSONObject requestJson = new JSONObject();

        JSONArray storyArray = new JSONArray();

        try {
            int size = 0, i = 0;
            if (storiesCursor != null) {
                size = storiesCursor.getCount();
             //   Log.d("shri", size + "{-------------------]");
            }
            while (storiesCursor != null && storiesCursor.moveToNext()) {
                i++;
                Story story = new Story(storiesCursor);
                JSONObject storyJson = db.modelObjectToJson(story);

                answerCursor = db.query(Answer.class,
                        Query.select().from(Answer.TABLE)
                                .where(Answer.STORY_ID.eq(story.getId()))
                );

                JSONArray answerArray = new JSONArray();
                while (answerCursor.moveToNext()) {
                    Answer answer = new Answer(answerCursor);
                    JSONObject answerJson = db.modelObjectToJson(answer);
                    answerArray.put(answerJson);
                }
                storyJson.put("answers", answerArray);

              // for (int k = 0; k < 5; k++) {
                    storyArray.put(storyJson);
               // }

                if (storyArray.length() >= Constants.SYNC_MAX_COUNT_AT_SINGLE) {
                    jsonDataList.add(new JSONObject().put("stories", storyArray));
                    storyArray = null;
                    storyArray = new JSONArray();
                } else if (i == size) {
                    jsonDataList.add(new JSONObject().put("stories", storyArray));
                    storyArray = null;
                    storyArray = new JSONArray();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (storiesCursor != null) storiesCursor.close();

        }

      //  Log.d("shri", jsonDataList.size() + "---------");
//Log.d("shri",requestJson.toString().getBytes().length+"--------------------------");
        return jsonDataList;


    }


    public int getStoryCount() {
        Query listStoryQuery = Query.select().from(Story.TABLE)
                .where(Story.SYNCED.eq(0));
        SquidCursor<Story> storiesCursor = db.query(Story.class, listStoryQuery);

        if (storiesCursor != null) {
            return storiesCursor.getCount();
        }
        return 0;
    }


    public class DownloadTasks {
        private final String LOG_TAG = "DownloadTask";


    }

    public class UploadTask {
        private ArrayList<Integer> processUploadResponse(JSONObject response) {
            int failedCount = 0, successCount = 0;
            try {
                Log.d(this.toString(), response.toString());
                // TODO: show error
                String error = response.optString("error");

                if (error != null && !error.isEmpty() && error != "null") {
                    Toast.makeText(MainDashList.this, error, Toast.LENGTH_LONG).show();
                } else {
                    JSONObject success = response.getJSONObject("success");
                    Iterator<String> keys = success.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String sysid = success.getString(key);
                        Update storyUpdate = Update.table(Story.TABLE)
                                .set(Story.SYNCED, 1)
                                .set(Story.SYSID, sysid)
                                .set(Story.IMAGE, null)
                                .where(Story.ID.eq(Long.valueOf(key)));
                        db.update(storyUpdate);
                        db.deleteWhere(Story.class, Story.ID.eq(key));
                        db.deleteWhere(Answer.class, Answer.STORY_ID.eq(key));
                        successCount++;

                    }

                    JSONArray failed = response.optJSONArray("failed");
                    if (failed != null && failed.length() > 0) {
                        //log("Upload onNext", "Upload failed for Story ids: " + failed.toString());
                        //   Toast.makeText(getApplicationContext(),failed.toString(),Toast.LENGTH_SHORT).show();
                        failedCount = failed.length();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<Integer> data = new ArrayList<>();
            data.add(successCount);
            data.add(failedCount);
            return data;
        }
    }


    public void downloadAll() {


        initPorgresssDialog();
        progressDialog.setMessage(getResources().getString(R.string.survey) + " " + getResources().getString(R.string.loading));
        String stateKey = mSession.getStateSelection();
        String URL = BuildConfig.HOST + "/api/v1/surveys/?survey_tag=konnect&state=" + stateKey + "&status=AC&per_page=0 ";
        new ProNetworkSettup(MainDashList.this).getSurveyandQuestionGroup(URL, stateKey, mSession.getToken(), new StateInterface() {
            @Override
            public void success(String message) {

                progressDialog.setMessage(getResources().getString(R.string.title_activity_question) + " " + getResources().getString(R.string.loading));

                flag = 0;
                final ArrayList<Survey> pojoList = getS_Qids();
                if (pojoList != null && pojoList.size() > 0) {

                    for (int i = 0; i < pojoList.size(); i++) {
                        flag = i;
                        String url = BuildConfig.HOST + "/api/v1/surveys/" + pojoList.get(i).getId() + "/questiongroup/" + pojoList.get(i).getQuestionGroupId() + "/questions/?state=" + mSession.getStateSelection() + "&per_page=0";

                        new ProNetworkSettup(MainDashList.this).getCommunitySurveyQuestions(url, pojoList.get(i).getQuestionGroupId(), flag, pojoList.size(), mSession.getToken(), new StateInterface() {
                            @Override
                            public void success(final String message) {


                                finishProgress();
                                //   DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                                AlertDialog alertDialog = new AlertDialog.Builder(MainDashList.this).create();

                                alertDialog.setCancelable(false);
                                alertDialog.setMessage(message);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                                            }
                                        });
                                alertDialog.show();


                            }

                            @Override
                            public void failed(String message) {
                                finishProgress();
                                showSignupResultDialog(
                                        getResources().getString(R.string.app_name),
                                        message,
                                        getResources().getString(R.string.Ok));
                            }
                        });

                    }


                } else {
                    finishProgress();
                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.surveyLoadingfailed),
                            getResources().getString(R.string.Ok));
                }


            }

            @Override
            public void failed(String message) {
                finishProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
            }
        });

    }

    public ArrayList<Survey> getS_Qids() {

        Query listQGIdsQquery = Query.select().from(Survey.TABLE);
        ArrayList<Survey> pojoList = new ArrayList<>();
        SquidCursor<Survey> surveyCursor = null;
        surveyCursor = db.query(Survey.class, listQGIdsQquery);
        if (surveyCursor != null) {
            while (surveyCursor.moveToNext()) {
                Survey survey = new Survey(surveyCursor);
                pojoList.add(survey);

            }


        }
        return pojoList;
    }

    public void showSignupResultDialog(String title, String message, String buttonText) {
        try {
            Bundle signUpResult = new Bundle();
            signUpResult.putString("title", title);
            signUpResult.putString("result", message);
            signUpResult.putString("buttonText", buttonText);

            SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
            resultDialog.setArguments(signUpResult);
            resultDialog.setCancelable(false);
            resultDialog.show(getSupportFragmentManager(), "Registration result");
        } catch (Exception e) {

        }
    }


    private void progressOnlySync(int count) {
        progress = new ProgressDialog(MainDashList.this);
        progress.setMessage(getString(R.string.syncing));
        progress.setProgress(0);//initially progress is 0
        progress.setMax(count);//sets the maximum value 100
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progress.show();
        progress.setCancelable(false);
    }

    private void updateSyncProgress(int count) {
        progress.setMessage(getResources().getString(R.string.syncing));
        progress.setProgress(count);
        Log.d("shri", count + "------------------count");
    }

    private void finishSyncProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

    }


    private void initPorgresssDialog() {
        progressDialog = new ProgressDialog(MainDashList.this);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void updateProgressMessage(String message) {

        progressDialog.setMessage(message);

    }

    private void finishProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

}

