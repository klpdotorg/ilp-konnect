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
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.SessionManager;
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

    int flag = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dash_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvPartner = findViewById(R.id.tvPartner);
        listDashboard = findViewById(R.id.listDashboard);
//

        Constants.mainDashList = MainDashList.this;


        db = ((KLPApplication) getApplicationContext()).getDb();


        surveyId = getIntent().getLongExtra("ILPSurveyId", 0);
        questionGroupId = getIntent().getLongExtra("ILPQuestionGroupId", 0);
        surveyName = getIntent().getStringExtra("surveyName");
        isImageRequired = getIntent().getBooleanExtra("imageRequired", false);
        //  Toast.makeText(this, surveyId+":"+surveyName, Toast.LENGTH_SHORT).show();
        if(surveyName!=null&&!surveyName.equalsIgnoreCase("")) {
            this.setTitle(surveyName);
        }

        //    Toast.makeText(getApplicationContext(),surveyId+":"+surveyName,Toast.LENGTH_SHORT).show();
        if (surveyId == 0 || surveyName.isEmpty() || questionGroupId == 0) {
            Toast.makeText(this, "Invalid Survey ID/Name", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainDashList.this, SurveyTypeActivity.class);
            startActivity(intent);
        }

        okclient = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        mSession = new SessionManager(getApplicationContext());
        mSession.checkLogin();
        // Log user details to be used for crashlytics


        menues = new String[]{getResources().getString(R.string.new_response), getResources().getString(R.string.download_survey), getResources().getString(R.string.generate_report), getResources().getString(R.string.syncSurvey), getResources().getString(R.string.summary)};
        icons = new int[]{R.drawable.ic_button_new_response, R.drawable.downloadicon, R.drawable.ic_button_report, R.drawable.ic_button_sync, R.drawable.ic_button_report};
        ids = new int[]{1, 2, 3, 4, 5};


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
                Intent intent=new Intent(getApplicationContext(), SurveyTypeActivity.class);
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
        Intent intent=new Intent(getApplicationContext(), SurveyTypeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    public boolean isSyncNeeded() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int currentVersion = 0;
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
//            Log.d(this.toString(), "if you're here, you're in trouble");
            return true;
        }

        final int lastVersion = prefs.getInt("lastVersion", -1);
        if (currentVersion > lastVersion) {
            // first time running the app or app just updated
            prefs.edit().putInt("lastVersion", currentVersion).commit();
            return true;
        } else {
            return false;
        }
    }




/*
    public void sync() {
        dt = new DownloadTasks();
        ut = new UploadTask();

        API_URLS.put("survey", "/api/v1/surveys/?source=mobile");
        API_URLS.put("questiongroup", "/api/v1/questiongroups/?source=mobile");
        API_URLS.put("question", "/api/v1/questions/");

        // remove story sync from here
        // add on the report generation page
//        String story_url = "/api/v1/stories/?source=csv&source=mobile&answers=yes&admin2=detect&per_page=0&is_sync=yes";
//        Story last_story = db.fetchByQuery(Story.class,
//                Query.select().where(Story.SYSID.neq(null)).orderBy(Story.SYSID.desc()).limit(1));
//        if (last_story != null) {
//            story_url += "&since_id=" + last_story.getSysid();
//        }
//
//        API_URLS.put("story", story_url);

        final String[] thingsToDo = {"survey", "question", "questiongroup"};

        preSync(getResources().getString(R.string.downloading), getResources().getString(R.string.downloading));

        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<String, String>() {
            @Override
            protected String doWork() {
                //  publishProgress(getResources().getString(R.string.downloading));
                */
/*JSONObject uploadJson = doUpload();
                ut.processUploadResponse(uploadJson);*//*


                String data = doUploadForSyncSurvey();
                if (!data.trim().equalsIgnoreCase(res)) {
                    Log.d("shri", "DATA FOUNd FOR Sync");
                    JSONObject jsonData = SyncDataCall(data);
                    ut.processUploadResponse(jsonData);

                    Log.d("shri", "---" + data);


                    try {
                        synchronized (this) {
                            wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (String thing : thingsToDo) {
                    // publishProgress(thing);
                    JSONObject downloadJson = doDownload(thing);

                    try {
                        if (thing == "survey") {
                            publishProgress(" " + getResources().getString(R.string.question));

                            dt.saveSurveyDataFromJson(downloadJson);
                        } else if (thing == "questiongroup") {
                            publishProgress(" " + getResources().getString(R.string.questiongroup));
                            dt.saveQuestiongroupDataFromJson(downloadJson);
                        } else if (thing == "question") {
                            publishProgress(" " + getResources().getString(R.string.question));

                            dt.saveQuestionDataFromJson(downloadJson);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(String s) {
                endSync();
            }

            @Override
            protected void onProgressUpdate(String s) {
                if (!s.equals(getResources().getString(R.string.upload))) {
                    updateProgressDialog(getResources().getString(R.string.downloading), getResources().getString(R.string.downloading) + s + "..");
                }
            }
        });

    }
*/

    public void sync1(final boolean var) {

        dt = new DownloadTasks();
        ut = new UploadTask();

     /*   API_URLS.put("survey", "/api/v1/surveys/?source=mobile");
        API_URLS.put("questiongroup", "/api/v1/questiongroups/?source=mobile");
        API_URLS.put("question", "/api/v1/questions/");

        final String[] thingsToDo = {"survey", "question", "questiongroup"};
      */
        final String data = doUploadForSyncSurvey().trim();
        //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
        if (data.equalsIgnoreCase(res) && var) {
            //Toast.makeText(getApplicationContext(),"Data Already in Sync",Toast.LENGTH_SHORT).show();
          /*  DialogConstants d = new DialogConstants(MainDashList.this, getResources().getString(R.string.dataAlreadynSync));
            d.show();*/
            showSignupResultDialog(
                    getResources().getString(R.string.app_name),
                    getResources().getString(R.string.dataAlreadynSync),
                    getResources().getString(R.string.Ok));
        } else {
            if (var) {
                preSync(getResources().getString(R.string.uploading), getResources().getString(R.string.uploadingstorw));
            } else {
                preSync(getResources().getString(R.string.downloading), getResources().getString(R.string.downloading));

            }
            Needle.onBackgroundThread().execute(new UiRelatedProgressTask<String, String>() {
                @Override
                protected String doWork() {

                    publishProgress(getResources().getString(R.string.upload));
                    if (var) {
                        publishProgress(" " + getResources().getString(R.string.datasync));
                        //  String data = doUploadForSyncSurvey();
                        if (data.equalsIgnoreCase(res)) {

                          /*  DialogConstants d = new DialogConstants(MainDashList.this, getResources().getString(R.string.dataAlreadynSync));
                            d.show();*/
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.dataAlreadynSync),
                                    getResources().getString(R.string.Ok));
                            return null;
                        } else {

                         // Log.d("shri", "Req:" + data);
                            // System.out.println(data);
                            JSONObject jsonData = SyncDataCall(data);

                             //Log.d("shri", "Res:" + jsonData.toString());
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


                    } else {
                    /*    for (String thing : thingsToDo) {
                            //   publishProgress(thing);
                            JSONObject downloadJson = doDownload(thing);

                            try {
                                if (thing == "survey") {
                                    publishProgress(" " + getResources().getString(R.string.question));
                                    dt.saveSurveyDataFromJson(downloadJson);
                                } else if (thing == "questiongroup") {
                                    dt.saveQuestiongroupDataFromJson(downloadJson);
                                    publishProgress(" " + getResources().getString(R.string.questiongroup));

                                } else if (thing == "question") {
                                    publishProgress(" " + getResources().getString(R.string.question));

                                    dt.saveQuestionDataFromJson(downloadJson);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    return null;
                }

                @Override
                protected void thenDoUiRelatedWork(String s) {
                    endSync();
                    if (s != null && !s.equalsIgnoreCase("")) {
                        // Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                       /* DialogConstants d = new DialogConstants(MainDashList.this, s);
                        d.show();*/
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
    }

/*    public JSONObject doDownload(String thing) {
        JSONObject okresponse_json = new JSONObject();
        String url = BuildConfig.HOST + API_URLS.get(thing);

        if (!url.isEmpty()) {
            HashMap<String, String> user = mSession.getUserDetails();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Token " + user.get("token"))
                    .build();
            try {
                okhttp3.Response okresponse = okclient.newCall(request).execute();

                if (!okresponse.isSuccessful()) {
                    Log.d("Download Error", "There is something wrong with the Internet connection.");
                    return new JSONObject();
                }

                if (okresponse.code() == 401) {
                    Log.d("Authentication Error", "Something went wrong. Please login again.");
                    logoutUser();
                }

                String okresponse_body = okresponse.body().string();
                okresponse_json = new JSONObject(okresponse_body);
            } catch (IOException e) {
                logError("DlObErr IO", e);
            } catch (JSONException e) {
                logError("DlObErr JSON", e);
            }
        }
        Log.d("shri", "=====Res===" + okresponse_json.toString());
        return okresponse_json;
    }*/

    public void logoutUser() {
        mSession.logoutUser();
        this.finish();
    }

    public void logError(String tag, Throwable e) {
        if (e.getMessage() != null) {
            Log.e(tag, e.getMessage());
        }
        e.printStackTrace();
    }

    public JSONObject SyncDataCall(String data) {
        JSONObject respJson = new JSONObject();
        final String SYNC_URL = BuildConfig.HOST + "/api/v1/surveys/assessments/sync/";
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
                return new JSONObject();
            }

            if (okresponse.code() == 401) {
                // log("Authentication Error", "Something went wrong. Please login again.");
                logoutUser();
            }

            respJson = new JSONObject(okresponse.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage() != null) Log.d(this.toString(), e.getMessage());
        } catch (JSONException e) {
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

    private String doUploadForSyncSurvey() {


        Query listStoryQuery = Query.select().from(Story.TABLE)
                .where(Story.SYNCED.eq(0));
        SquidCursor<Story> storiesCursor = db.query(Story.class, listStoryQuery);
        SquidCursor<Answer> answerCursor = null;

        JSONObject requestJson = new JSONObject();

        JSONArray storyArray = new JSONArray();

        try {
            while (storiesCursor != null && storiesCursor.moveToNext()) {
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
                storyArray.put(storyJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (storiesCursor != null) storiesCursor.close();
            if (answerCursor != null) answerCursor.close();
        }
        try {
            requestJson.put("stories", storyArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return requestJson.toString();


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

/*
        private String saveBoundaryDataFromJson(JSONObject boundaryJson)
                throws JSONException {
            final String FEATURES = "features";
            String next_url = boundaryJson.getString("next");
            JSONArray boundaryArray = boundaryJson.getJSONArray(FEATURES);

            for (int i = 0; i < boundaryArray.length(); i++) {

                Integer boundaryId;
                long parentId;
                String name;
                String hierarchy;
                String school_type;

                JSONObject boundaryObject = boundaryArray.getJSONObject(i);
                if (boundaryObject.has("parent")) {
                    JSONObject parentObject = boundaryObject.getJSONObject("parent");
                    parentId = parentObject.getInt("id");
                } else {
                    parentId = 1;
                }

                boundaryId = boundaryObject.getInt("id");
                name = boundaryObject.getString("name");
                hierarchy = boundaryObject.getString("type");
                school_type = boundaryObject.getString("school_type");

                Boundary boundary = new Boundary()
                        .setId(boundaryId)
                        .setParentId(parentId)
                        .setName(name)
                        .setHierarchy(hierarchy)
                        .setType(school_type);
                db.insertWithId(boundary);
            }
            return next_url;
        }
*/


  /*      private String saveSchoolDataFromJson(JSONObject schoolJson)
                throws JSONException {

            final String FEATURES = "features";
            String next_url = schoolJson.getString("next");
            JSONArray schoolArray = schoolJson.getJSONArray(FEATURES);

            for (int i = 0; i < schoolArray.length(); i++) {

                Integer schoolId;
                long boundaryId;
                String name;

                JSONObject schoolObject = schoolArray.getJSONObject(i);
                JSONObject boundaryObject = schoolObject.getJSONObject("boundary");

                schoolId = schoolObject.getInt("id");
                boundaryId = boundaryObject.getInt("id");
                name = schoolObject.getString("name");

                School school = new School()
                        .setId(schoolId)
                        .setBoundaryId(boundaryId)
                        .setName(name);
                db.insertWithId(school);
            }
            return next_url;
        }*/

 /*       private void saveStoryDataFromJson(JSONObject storyJson)
                throws JSONException {
//            Log.d(LOG_TAG, "Saving Story Data: " + storyJson.toString());
            final String FEATURES = "features";

            JSONArray storyArray = storyJson.getJSONArray(FEATURES);
            Log.d(LOG_TAG, "Total stories received: " + String.valueOf(storyArray.length()));

            db.beginTransaction();
            try {
                for (int i = 0; i < storyArray.length(); i++) {
                    JSONObject storyObject = storyArray.getJSONObject(i);

                    Long schoolId = storyObject.getLong("school");
                    Long userId = storyObject.getLong("user");
                    Long groupId = storyObject.getLong("group");
                    String dateOfVisit = storyObject.getString("date_of_visit");
                    String userType = storyObject.getString("user_type");
                    // Storing the story ID from server as SYSID on the device
                    // This helps in keeping the stories unique on the device
                    String sysId = storyObject.getString("id");
                    Timestamp dateOfVisitTS;

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                        Date parsedDate = dateFormat.parse(dateOfVisit);
                        dateOfVisitTS = new Timestamp(parsedDate.getTime());
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.toString());
                        continue;
                    }

                    SquidCursor<Story> storyCursor = db.query(
                            Story.class,
                            Query.select().where(
                                    Story.SCHOOL_ID.eq(schoolId).and(
                                            Story.USER_ID.eq(userId).and(
                                                    Story.SYSID.eq(sysId)
                                            )
                                    )
                            )
                    );

                    try {
                        if (storyCursor.getCount() == 0) {
                            Story story = new Story()
                                    .setUserId(userId)
                                    .setSchoolId(schoolId)
                                    .setGroupId(groupId)
                                    .setRespondentType(userType)
                                    .setSynced(1)
                                    .setSysid(sysId);

                            if (dateOfVisitTS != null) {
                                story.setCreatedAt(dateOfVisitTS.getTime());
                            }
                            db.persist(story);
//                            Log.d("DL", "Story created: " + story.getId());

                            JSONObject storyAnswers = storyObject.getJSONObject("answers");
                            Iterator<String> answerKeys = storyAnswers.keys();

                            while (answerKeys.hasNext()) {
                                String key = answerKeys.next();
                                Long questionId = Long.valueOf(key);
                                String answerText = storyAnswers.getString(key);

                                Answer answer = new Answer()
                                        .setStoryId(story.getId())
                                        .setQuestionId(questionId)
                                        .setText(answerText)
                                        .setCreatedAt(dateOfVisitTS.getTime());
                                db.persist(answer);
//                                Log.d("DL", "Answer Created: " + answer.getId());
                            }
                        } else if (storyCursor.getCount() > 1) {
                            // there are multiple old stories with same SYSID
                            // this should not happen
                        } else {
                            // ignore existing story with same SYSID
                        }
                    } finally {
                        storyCursor.close();
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }*/


    /*    private void saveQuestionDataFromJson(JSONObject questionJson)
                throws JSONException {
//            Log.d(LOG_TAG, "Saving Question Data: " + questionJson.toString());
            final String FEATURES = "features";
            JSONArray questionArray = questionJson.getJSONArray(FEATURES);
            boolean flagi = false;
            boolean qFlag = false;
            for (int i = 0; i < questionArray.length(); i++) {

                long questionId;
                String text;
                String text_kn;
                String display_text;
                String key;
                String options;
                String type;
                String school_type;

                JSONObject questionObject = questionArray.getJSONObject(i);
                JSONObject schoolObject = questionObject.getJSONObject("school_type");
                JSONArray questiongroupSetArray = questionObject.getJSONArray("questiongroup_set");

                questionId = questionObject.getInt("id");
                text = questionObject.getString("text");
                text_kn = questionObject.getString("text_kn");
                display_text = questionObject.getString("display_text");
                key = questionObject.getString("key");
                options = questionObject.getString("options");
                type = questionObject.getString("question_type");
                school_type = schoolObject.getString("name");

                if (qFlag == false) {
                    qFlag = true;
                    db.deleteAll(Question.class);
                }

                Question question = new Question()
                        .setId(questionId)
                        .setText(text)
                        .setTextKn(text_kn)
                        .setDisplayText(display_text)
                        .setKey(key)
                        .setOptions(options)
                        .setType(type)
                        .setSchoolType(school_type);

                db.insertNew(question);

                if (flagi == false) {
                    if (questiongroupSetArray != null && questiongroupSetArray.length() > 0) {
                        flagi = true;
                        db.deleteAll(QuestionGroupQuestion.class);
                    }
                }
                for (int j = 0; j < questiongroupSetArray.length(); j++) {
                    JSONObject questiongroupObject = questiongroupSetArray.getJSONObject(j);

                    Integer throughId = questiongroupObject.getInt("through_id");
                    long questiongroupId = questiongroupObject.getInt("questiongroup");
                    Integer sequence = questiongroupObject.getInt("sequence");
                    Integer status = questiongroupObject.getInt("status");
                    String source = questiongroupObject.getString("source");

                    if (source.equals("mobile") || source.equals("konnectsms")) {
                        if (status.equals(1)) {
                            QuestionGroupQuestion questionGroupQuestion = new QuestionGroupQuestion()
                                    .setId(throughId)
                                    .setQuestionId(questionId)
                                    .setQuestiongroupId(questiongroupId)
                                    .setSequence(sequence);

                            db.insertNew(questionGroupQuestion);
                            // Log.d("test",b+"");
                        }
                    }

                }
            }
        }*/

/*
        private void saveQuestiongroupDataFromJson(JSONObject questiongroupJson)
                throws JSONException {
//            Log.d(LOG_TAG, "Saving QG Data: " + questiongroupJson.toString());
            final String FEATURES = "features";
            JSONArray questiongroupArray = questiongroupJson.getJSONArray(FEATURES);

            for (int i = 0; i < questiongroupArray.length(); i++) {

                Integer groupId;
                Integer status;
                long start_date;
                long end_date;
                Integer version;
                long surveyId;
                String source;

                // Get the JSON object representing the survey
                JSONObject questiongroupObject = questiongroupArray.getJSONObject(i);

                Integer qgStatus = questiongroupObject.getInt("status");
                if (!qgStatus.equals(1)) continue;

                // Get the JSON object representing the partner
                JSONObject surveyObject = questiongroupObject.getJSONObject("survey");

                groupId = questiongroupObject.getInt("id");
                status = questiongroupObject.getInt("status");
                start_date = questiongroupObject.optInt("start_date");
                end_date = questiongroupObject.optInt("end_date");
                version = questiongroupObject.getInt("version");
                source = questiongroupObject.getString("source");
                surveyId = surveyObject.getInt("id");

                QuestionGroup questionGroup = new QuestionGroup()
                        .setId(groupId)
                        .setStatus(status)
                        .setStartDate(start_date)
                        .setEndDate(end_date)
                        .setVersion(version)
                        .setSource(source)
                        .setSurveyId(surveyId);
                db.insertWithId(questionGroup);
                // db.insertforQuestionGroup(questionGroup);
            }
        }*/

  /*      private void saveSurveyDataFromJson(JSONObject surveyJson)
                throws JSONException {
//            Log.d(LOG_TAG, "Saving Survey Data: " + surveyJson.toString());
            final String FEATURES = "features";
            JSONArray surveyArray = surveyJson.getJSONArray(FEATURES);

            for (int i = 0; i < surveyArray.length(); i++) {

                Integer surveyId;
                String surveyName;
                String surveyPartner;

                // Get the JSON object representing the survey
                JSONObject surveyObject = surveyArray.getJSONObject(i);

                // Get the JSON object representing the partner
                JSONObject partnerObject = surveyObject.getJSONObject("partner");

                surveyId = surveyObject.getInt("id");
                surveyName = surveyObject.getString("name");
                surveyPartner = partnerObject.getString("name");

                Survey survey = new Survey()
                        .setId(surveyId)
                        .setName(surveyName)
                        .setPartner(surveyPartner);
                db.insertWithId(survey);
            }

        }*/

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

                    /*String command = response.optString("command", "");
                    Log.d("Command Log", command);
                    switch (command) {
                        case "wipe-stories":
                            db.deleteAll(Answer.class);
                            db.deleteAll(Story.class);
                            break;
                        case "wipe-all":
                            db.deleteAll(Answer.class);
                            db.deleteAll(Story.class);
                            db.deleteAll(QuestionGroupQuestion.class);
                            db.deleteAll(QuestionGroup.class);
                            db.deleteAll(Question.class);
                            db.deleteAll(Survey.class);
                            db.deleteAll(School.class);
                            db.deleteAll(Boundary.class);
                            break;
                        default:
                            Log.d("Command Log", "Nothing to do.");
                    }*/
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
        String URL = BuildConfig.HOST + "/api/v1/surveys/?survey_tag=konnect&state=" + stateKey+"&status=AC&per_page=0 ";
        new ProNetworkSettup(MainDashList.this).getSurveyandQuestionGroup(URL, stateKey,mSession.getToken(), new StateInterface() {
            @Override
            public void success(String message) {

                progressDialog.setMessage(getResources().getString(R.string.title_activity_question) + " " + getResources().getString(R.string.loading));

                flag = 0;
                final ArrayList<Survey> pojoList = getS_Qids();
                if (pojoList != null && pojoList.size() > 0) {

                    for (int i = 0; i < pojoList.size(); i++) {
                        flag = i;
                        String url = "/api/v1/surveys/" + pojoList.get(i).getId() + "/questiongroup/" + pojoList.get(i).getQuestionGroupId() + "/questions/?state=" + mSession.getStateSelection()+"&per_page=0";

                        new ProNetworkSettup(MainDashList.this).getCommunitySurveyQuestions(url, pojoList.get(i).getQuestionGroupId(), flag, pojoList.size(),mSession.getToken(), new StateInterface() {
                            @Override
                            public void success(final String message) {


                                finishProgress();
                             //   DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(MainDashList.this).create();

                                alertDialog.setCancelable(false);
                                alertDialog.setMessage(message);
                                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
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
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        resultDialog.show(getSupportFragmentManager(), "Registration result");
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

