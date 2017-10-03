package in.org.klp.ilpkonnect;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import in.org.klp.ilpkonnect.adapters.MainDashListAdapter;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroup;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DialogConstants;
import in.org.klp.ilpkonnect.utils.SessionManager;
import needle.Needle;
import needle.UiRelatedProgressTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by shridhars on 8/1/2017.
 */

public class MainDashList extends AppCompatActivity {

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
    Long surveyId;
    String surveyName, partner;


    //for location
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private SettingsClient mSettingsClient;

    private LocationRequest mLocationRequest;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    boolean flaggps = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dash_list);
        tvPartner = (TextView) findViewById(R.id.tvPartner);
        listDashboard = (RecyclerView) findViewById(R.id.listDashboard);
//

        Constants.mainDashList = MainDashList.this;
        if (Constants.surveyType == 2) {
         /*   createLocationCallback();
            createLocationRequest();
            buildLocationSettingsRequest();*/


            mSettingsClient = LocationServices.getSettingsClient(this);
        }

        db = ((KLPApplication) getApplicationContext()).getDb();


        surveyId = getIntent().getLongExtra("surveyId", 0);
        surveyName = getIntent().getStringExtra("surveyName");
        partner = getIntent().getStringExtra("partener1");

        tvPartner.setText(partner);
        //    Toast.makeText(getApplicationContext(),surveyId+":"+surveyName,Toast.LENGTH_SHORT).show();
        if (surveyId == 0 || surveyName.isEmpty()) {
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
        logUserToCrashlytics();

       /* menues = new String[]{getResources().getString(R.string.new_response), getResources().getString(R.string.download_survey), getResources().getString(R.string.generate_report), getResources().getString(R.string.syncSurvey), getResources().getString(R.string.summary)};
        icons = new int[]{R.mipmap.ic_button_new_response, R.mipmap.downloadicon, R.mipmap.ic_button_report, R.mipmap.ic_button_sync, R.mipmap.ic_button_stories};
        ids = new int[]{1, 2, 3, 4, 5};*/


        menues = new String[]{getResources().getString(R.string.new_response), getResources().getString(R.string.download_survey), getResources().getString(R.string.generate_report), getResources().getString(R.string.syncSurvey)};
        icons = new int[]{R.drawable.ic_button_new_response, R.drawable.downloadicon, R.drawable.ic_button_report, R.drawable.ic_button_sync};
        ids = new int[]{1, 2, 3, 4};
        listDashboard.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MainDashListAdapter(menues, icons, ids, MainDashList.this, surveyId, surveyName);
        listDashboard.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (isSyncNeeded()) {

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


    private void logUserToCrashlytics() {


        HashMap<String, String> user = mSession.getUserDetails();

        Crashlytics.setUserIdentifier(user.get(SessionManager.KEY_ID));
        Crashlytics.setUserName(user.get(SessionManager.KEY_NAME));


    }

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
                /*JSONObject uploadJson = doUpload();
                ut.processUploadResponse(uploadJson);*/

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

    public void sync1(final boolean var) {

        dt = new DownloadTasks();
        ut = new UploadTask();

        API_URLS.put("survey", "/api/v1/surveys/?source=mobile");
        API_URLS.put("questiongroup", "/api/v1/questiongroups/?source=mobile");
        API_URLS.put("question", "/api/v1/questions/");

        final String[] thingsToDo = {"survey", "question", "questiongroup"};
        String data = doUploadForSyncSurvey();
        //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
        if (data.trim().equalsIgnoreCase(res) && var) {
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
            }else {
                preSync(getResources().getString(R.string.downloading), getResources().getString(R.string.downloading));

            }
            Needle.onBackgroundThread().execute(new UiRelatedProgressTask<String, String>() {
                @Override
                protected String doWork() {

                    publishProgress(getResources().getString(R.string.upload));
                    if (var) {
                        publishProgress(" " + getResources().getString(R.string.datasync));
                        String data = doUploadForSyncSurvey();
                        if (data.trim().equalsIgnoreCase(res)) {

                            DialogConstants d = new DialogConstants(MainDashList.this, getResources().getString(R.string.dataAlreadynSync));
                            d.show();
                            return null;
                        } else {

                            Log.d("shri", "Req:" + data);
                            JSONObject jsonData = SyncDataCall(data);
                            Log.d("shri", "Res:" + jsonData.toString());
                            ArrayList<Integer> countData = ut.processUploadResponse(jsonData);
                            String msg = getResources().getString(R.string.surveysyncsucc) + countData.get(0) + "\n" + getResources().getString(R.string.surveysyncfail) + countData.get(1);
                            return msg;
                        }


                    } else {
                        for (String thing : thingsToDo) {
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
                        }
                    }
                    return null;
                }

                @Override
                protected void thenDoUiRelatedWork(String s) {
                    endSync();
                    if (s != null && !s.equalsIgnoreCase("")) {
                        // Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                        DialogConstants d = new DialogConstants(MainDashList.this, s);
                        d.show();
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

    public JSONObject doDownload(String thing) {
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
    }

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
        final String SYNC_URL = BuildConfig.HOST + "/api/v1/sync";
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
                log("Upload Error", "There is something wrong with the Internet connection.");
                return new JSONObject();
            }

            if (okresponse.code() == 401) {
                log("Authentication Error", "Something went wrong. Please login again.");
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
            while (storiesCursor!=null&&storiesCursor.moveToNext()) {
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


    public class DownloadTasks {
        private final String LOG_TAG = "DownloadTask";

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


        private String saveSchoolDataFromJson(JSONObject schoolJson)
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
        }

        private void saveStoryDataFromJson(JSONObject storyJson)
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
        }


        private void saveQuestionDataFromJson(JSONObject questionJson)
                throws JSONException {
//            Log.d(LOG_TAG, "Saving Question Data: " + questionJson.toString());
            final String FEATURES = "features";
            JSONArray questionArray = questionJson.getJSONArray(FEATURES);

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

                Question question = new Question()
                        .setId(questionId)
                        .setText(text)
                        .setTextKn(text_kn)
                        .setDisplayText(display_text)
                        .setKey(key)
                        .setOptions(options)
                        .setType(type)
                        .setSchoolType(school_type);

                db.insertWithId(question);

                for (int j = 0; j < questiongroupSetArray.length(); j++) {
                    JSONObject questiongroupObject = questiongroupSetArray.getJSONObject(j);

                    Integer throughId = questiongroupObject.getInt("through_id");
                    long questiongroupId = questiongroupObject.getInt("questiongroup");
                    Integer sequence = questiongroupObject.getInt("sequence");
                    Integer status = questiongroupObject.getInt("status");
                    String source = questiongroupObject.getString("source");

                    if (source.equals("mobile")) {
                        if (status.equals(1)) {
                            QuestionGroupQuestion questionGroupQuestion = new QuestionGroupQuestion()
                                    .setId(throughId)
                                    .setQuestionId(questionId)
                                    .setQuestiongroupId(questiongroupId)
                                    .setSequence(sequence);
                            db.insertWithId(questionGroupQuestion);
                        }
                    }

                }
            }
        }


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
        }

        private void saveSurveyDataFromJson(JSONObject surveyJson)
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

        }

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
                                .where(Story.ID.eq(Long.valueOf(key)));
                        db.update(storyUpdate);
                        successCount++;

                    }

                    JSONArray failed = response.optJSONArray("failed");
                    if (failed != null && failed.length() > 0) {
                        //log("Upload onNext", "Upload failed for Story ids: " + failed.toString());
                        //   Toast.makeText(getApplicationContext(),failed.toString(),Toast.LENGTH_SHORT).show();
                        failedCount = failed.length();
                    }

                    String command = response.optString("command", "");
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




  /* if (!checkPermissions()) {
        requestPermissions();
    }else {
        createLocationRequest();
        buildLocationSettingsRequest();
        checkGPS();
    }  */


    public boolean callForGPS() {
        if (mSettingsClient == null) {
            mSettingsClient = LocationServices.getSettingsClient(this);
        }
        if (!checkPermissions()) {
            //device location
            requestPermissions();

            return false;

        } else {
            if (testGPS() == true) {
                return true;
            } else {
                createLocationRequest();
                buildLocationSettingsRequest();
                checkGPS();
                return false;
            }
        }
    }

    public boolean testGPS() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    //check permission granted or not
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            //show dialog for location
            ActivityCompat.requestPermissions(MainDashList.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    public boolean checkGPS() {

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //all permissions ok
                        flaggps = true;
                        // Toast.makeText(getApplicationContext(), "GPS on", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flaggps = false;
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                          /*  Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");*/
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainDashList.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                               /* Log.i(TAG, "PendingIntent unable to execute request.");*/
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                           /* Log.e(TAG, errorMessage);*/


                        }


                    }
                });
        return flaggps;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        //  Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        //  Log.i(TAG, "User chose not to make required location settings changes.");
                        //  mRequestingLocationUpdates = false;
                        //  Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_SHORT).show();
                        //updateUI();
                        break;
                }
                break;
        }

    }


    private void showSignupResultDialog(String title, String message, String buttonText) {
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        resultDialog.show(getSupportFragmentManager(), "Registration result");
    }



}

