package in.org.klp.ilpkonnect.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import in.org.klp.ilpkonnect.BuildConfig;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.ReportsActivity;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.TotalSummaryPOJOs.SummaryTotalPojo;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;

import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.SummaryInfo;
import in.org.klp.ilpkonnect.db.Summmary;
import in.org.klp.ilpkonnect.db.Survey;
import needle.Needle;
import needle.UiRelatedTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bibhas on 23/2/17.
 */

public class SyncManager {
    public boolean doUpload = false, doUpdateSurvey = false, doDownloadStories = false;
    private KontactDatabase db;
    private Context context;
    private SessionManager mSession;
    private OkHttpClient okclient;
    private MenuItem syncButton;
   String groupId = "";
    ProgressDialog dialog;

    public String story_url = "/api/v1/stories/?source=csv&source=mobile&answers=yes&per_page=0&is_sync=yes";
    public String host_url = BuildConfig.HOST;

    public SyncManager(Context activity, KontactDatabase db, Boolean doUpload, Boolean doUpdateSurvey, Boolean doDownloadStories) {
        this.context = activity;
        this.db = db;
        this.doUpload = doUpload;
        this.doUpdateSurvey = doUpdateSurvey;
        this.doDownloadStories = doDownloadStories;
        dialog = new ProgressDialog(activity);
        mSession = new SessionManager(context.getApplicationContext());

        okclient = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
    }

    public int isSyncDataFound() {

        try {
            Query listStoryQuery = Query.select().from(Story.TABLE)
                    .where(Story.SYNCED.eq(0));
            SquidCursor<Story> storiesCursor = db.query(Story.class, listStoryQuery);

            if (storiesCursor != null) {
                return storiesCursor.getCount();
            }
        } catch (Exception e) {

        }
        return 0;
    }

   /* public void uploadStories() {


        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage(context.getResources().getString(R.string.uploadingstroespleasewait));
        // Toast.makeText(this.context, "Uploading stories. Please wait.", Toast.LENGTH_SHORT).show();
        Needle.onBackgroundThread().execute(new UiRelatedTask<Integer>() {
            @Override
            protected Integer doWork() {
                UploadTask ut = new UploadTask();
                String JSONDATA = doUpload();
                Integer successCount = 0;

                // Log.d("shri", "-----UpLOAD:--" + JSONDATA);
                JSONObject uploadJson = uploadSyncData(JSONDATA);
                //Log.d("shri", "----RESPONSE----" + uploadJson.toString());
                successCount = ut.processUploadResponse(uploadJson);


                return successCount;

            }

            @Override
            protected void thenDoUiRelatedWork(Integer count) {
                if (count == 0)
                    dialog.setMessage(context.getResources().getString(R.string.dataAlreadynSync));
                else
                    dialog.setMessage(context.getResources().getString(R.string.uploaded) + count + context.getResources().getString(R.string.stories));


                // Toast.makeText(SyncManager.this.context, "Uploaded " + count + " stories..", Toast.LENGTH_SHORT).show();
            }
        });

    }*/

    /* public void downloadStories() {
        // downloadStories(null, false);
     }
 */
    public void downloadStories(String clusterid, boolean flag,String qG_Id) {
        if (flag) {
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

        }

        long oneday= 86400000L;
        String dataFound[] = clusterid.split("\\|");
        final String id = dataFound[0];
        final String level = dataFound[1];
        final String sdate=getDate(Long.parseLong(dataFound[2]),"yyyy-MM-dd");
        final String endate=getDate((Long.parseLong(dataFound[3]))+oneday,"yyyy-MM-dd");
            dialog.setMessage(context.getResources().getString(R.string.downloadingsurveypleasewait));

        // Boundary cluster = db.fetch(Boundary.class, clusterid);
        syncButton = ((ReportsActivity) context)._menu.findItem(R.id.action_sync_block);
        syncButton.setTitle(context.getResources().getString(R.string.syncing));

            groupId = qG_Id;


        if (!level.equalsIgnoreCase("school")) {
            //  Toast.makeText((ReportsActivity) context, "Dist", Toast.LENGTH_SHORT).show();
           // loadDataForDis(id, level,sdate,endate);
            loadNewReport(id,groupId,level,sdate,endate);

        }



    }

    private void loadNewReport(String id, String groupId, String level, String sdate, String endate) {

      //  new ProNetworkSettup(context).getReoportData(Long.parseLong(id),Long.parseLong(groupId),1,level);

    }

    private void loadDataForSchool(final String id, final String level,final String sdate,final String eDate) {

/*

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


        apiInterface.getAllSummarySchoolData(id, groupId,sdate,eDate).enqueue(new Callback<SummaryTotalPojo>() {
            @Override
            public void onResponse(Call<SummaryTotalPojo> call, Response<SummaryTotalPojo> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));

                if (response.isSuccessful()) {
                    //  Toast.makeText(((ReportsActivity) context), response.body().getQuestions().size() + "", Toast.LENGTH_SHORT).show();
                    if (level.equalsIgnoreCase("school_id")) {
                        savetoSummaryTable(Long.parseLong(id), groupId, "school", response);
                    } else {
                        savetoSummaryTable(Long.parseLong(id), groupId, level, response);
                    }
                } else {
                    //Toast.makeText(((ReportsActivity) context), "fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SummaryTotalPojo> call, Throwable t) {
                // Toast.makeText(((ReportsActivity) context), "intenet", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    try{
                   dialog.dismiss();}
                    catch (Exception e)
                    {
                        // Toast.makeText(((ReportsActivity) context), "intenet", Toast.LENGTH_SHORT).show();
                    }
                }
                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));
            }
        });
*/


    }

    private void loadDataForCluster(final String id, final String level,final String sdate,final String eDate) {

       /* ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


        apiInterface.getAllSummaryClusterData(id, groupId,sdate,eDate).enqueue(new Callback<SummaryTotalPojo>() {
            @Override
            public void onResponse(Call<SummaryTotalPojo> call, Response<SummaryTotalPojo> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));

                if (response.isSuccessful()) {
                    //  Toast.makeText(((ReportsActivity) context), response.body().getQuestions().size() + "", Toast.LENGTH_SHORT).show();
                    savetoSummaryTable(Long.parseLong(id), groupId, level, response);
                } else {
                    //Toast.makeText(((ReportsActivity) context), "fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SummaryTotalPojo> call, Throwable t) {

                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));
                //  Toast.makeText(((ReportsActivity) context), "intenet", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });*/


    }

    private void loadDataForBlock(final String id, final String level,final String sdate,final String eDate) {
/*
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


        apiInterface.getAllSummaryBlockData(id, groupId,sdate,eDate).enqueue(new Callback<SummaryTotalPojo>() {
            @Override
            public void onResponse(Call<SummaryTotalPojo> call, Response<SummaryTotalPojo> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));

                if (response.isSuccessful()) {
                    //  Toast.makeText(((ReportsActivity) context), response.body().getQuestions().size() + "", Toast.LENGTH_SHORT).show();
                    savetoSummaryTable(Long.parseLong(id), groupId, level, response);
                } else {
                    // Toast.makeText(((ReportsActivity) context), "fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SummaryTotalPojo> call, Throwable t) {
                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));
                // Toast.makeText(((ReportsActivity) context), "intenet", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });*/


    }


    public void loadDataForDis(final String id, final String level,final String sdate,final String eDate) {
      /*  ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

      //  Log.d("service",id+":"+groupId+":"+sdate+":"+eDate);
        apiInterface.getAllSummaryData(id, groupId,sdate,eDate).enqueue(new Callback<SummaryTotalPojo>() {
            @Override
            public void onResponse(Call<SummaryTotalPojo> call, Response<SummaryTotalPojo> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));

                if (response.isSuccessful()) {
                    //  Toast.makeText(((ReportsActivity) context), response.body().getQuestions().size() + "", Toast.LENGTH_SHORT).show();
                    savetoSummaryTable(Long.parseLong(id), groupId, level, response);
                } else {
                    // Toast.makeText(((ReportsActivity) context), "fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SummaryTotalPojo> call, Throwable t) {
                //  Toast.makeText(((ReportsActivity) context), "intenet", Toast.LENGTH_SHORT).show();'
                syncButton.setTitle(context.getResources().getString(R.string.syncTitle));
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });*/
    }
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void savetoSummaryTable(Long id, String groupId, String hierarchy, Response<SummaryTotalPojo> response) {

/*

        if (response.body().getQuestions() != null && response.body().getQuestions().size() > 0) {

            Summmary summary = new Summmary();
            summary.setId(id);

           summary.setHierarchy(hierarchy);
            try {
                db.insertNew(summary);
            } catch (Exception e) {

            }
            int size = response.body().getQuestions().size();
            for (int i = 0; i < size; i++) {
                Query summryQuery = Query.select().from(SummaryInfo.TABLE)
                        .where(SummaryInfo.BID.eq(id).and(SummaryInfo.GROUPID.eqCaseInsensitive(groupId)
                                .and(SummaryInfo.QID.eqCaseInsensitive(response.body().getQuestions().get(i).getId() + ""))));
                SquidCursor<SummaryInfo> summaryCursor = db.query(SummaryInfo.class, summryQuery);
                //  Toast.makeText((ReportsActivity) context, summaryCursor.getCount() + "count", Toast.LENGTH_SHORT).show();
                if (summaryCursor != null && summaryCursor.getCount() > 0) {

                    Update summryUpdate = Update.table(SummaryInfo.TABLE)
                            .set(SummaryInfo.YES, response.body().getQuestions().get(i).getYes())
                            .set(SummaryInfo.NO, response.body().getQuestions().get(i).getNo())
                            .set(SummaryInfo.DONTKNOW, response.body().getQuestions().get(i).getDonTKnow())
                            .set(SummaryInfo.TOTAL_SCHOOL, response.body().getNoOfSchools())
                            .set(SummaryInfo.TOTAL_RESPONSE, response.body().getNoOfResponses())
                            .set(SummaryInfo.TOTAL_SCHOOL_WITH_RESPONSE, response.body().getNoOfSchoolsWithResponses())

                            .where(SummaryInfo.QID.eq(response.body().getQuestions().get(i).getId()).and(SummaryInfo.BID.eqCaseInsensitive(id + "")).and(SummaryInfo.HIERARCHY.eqCaseInsensitive(hierarchy)));
                    int updated = db.update(summryUpdate);
                    // Toast.makeText((ReportsActivity) context, updated + "Update", Toast.LENGTH_SHORT).show();
                    //already data exist update
                } else {
                    //new record
                    SummaryInfo summaryInfo = new SummaryInfo();
                    summaryInfo.setYes(response.body().getQuestions().get(i).getYes());
                    summaryInfo.setNo(response.body().getQuestions().get(i).getNo());
                    summaryInfo.setQid(response.body().getQuestions().get(i).getId());
                    summaryInfo.setDontknow(response.body().getQuestions().get(i).getDonTKnow());
                    summaryInfo.setGroupid(Long.parseLong(groupId));
                    summaryInfo.setHierarchy(hierarchy);
                    summaryInfo.setBid(id);
                    summaryInfo.setTotalSchool(response.body().getNoOfSchools());
                    summaryInfo.setTotalResponse(response.body().getNoOfResponses());
                    summaryInfo.setTotalSchoolWithResponse(response.body().getNoOfSchoolsWithResponses());

                    boolean b = db.insertNew(summaryInfo);
                    //Toast.makeText((ReportsActivity) context, b + "Inserted", Toast.LENGTH_SHORT).show();

                }

            }
            ((ReportsActivity) context).fetchQuestions();

        }
*/

    }

    public JSONObject download(String url) {
        JSONObject resp = new JSONObject();

        url = host_url + url;
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
            }

            String okresponse_body = okresponse.body().string();
            resp = new JSONObject(okresponse_body);
        } catch (IOException e) {
            Log.e("DlObErr IO", e.getMessage());
        } catch (JSONException e) {
            Log.e("DlObErr JSON", e.getMessage());
        }
        Log.d("shri", "Download:" + resp + "");
        return resp;
    }

    public ArrayList<JSONObject> doUpload() {
        ArrayList<JSONObject> jsonDataList = new ArrayList<>();
        Query listStoryQuery = Query.select().from(Story.TABLE)
                .where(Story.SYNCED.eq(0));
        SquidCursor<Story> storiesCursor = db.query(Story.class, listStoryQuery);
        SquidCursor<Answer> answerCursor = null;

        JSONArray storyArray = new JSONArray();

        try {
            int size=0,i=0;
            if (storiesCursor != null) {
                size = storiesCursor.getCount();
                // Log.d("shri", size + "{-------------------]");
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
               //  for(int k=0;k<4;k++) {
                storyArray.put(storyJson);
              //   }
                if(storyArray.length()>= Constants.SYNC_MAX_COUNT_AT_SINGLE)
                {
                    jsonDataList.add( new JSONObject().put("stories", storyArray));
                    storyArray=null;
                    storyArray=new JSONArray();
                }else if(i==size) {
                    jsonDataList.add( new JSONObject().put("stories", storyArray));
                    storyArray=null;
                    storyArray=new JSONArray();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (storiesCursor != null) storiesCursor.close();
            if (answerCursor != null) answerCursor.close();
        }
        try {
            // requestJson.put("stories", storyArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
       /* Log.d("shri","----+++++"+jsonDataList.size());
        for(JSONObject object:jsonDataList)
        {
            Log.d("shri","----+++++"+object.toString());
        }*/
        return jsonDataList;

    }

    public int getStoriesCount()
    {
        Query listStoryQuery = Query.select().from(Story.TABLE)
                .where(Story.SYNCED.eq(0));
        SquidCursor<Story> storiesCursor = db.query(Story.class, listStoryQuery);

        return storiesCursor.getCount();
    }


    public JSONObject uploadSyncData(String dataJSON) {
        JSONObject respJson = new JSONObject();
        final String SYNC_URL = BuildConfig.HOST + "/api/v1/surveys/assessments/sync/";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        HashMap<String, String> user = mSession.getUserDetails();
        RequestBody body = RequestBody.create(JSON, dataJSON.toString());


        Log.d("shri", "Synch Data to sending:" + dataJSON.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(SYNC_URL)
                .post(body)
                .addHeader("Authorization", "Token " + user.get("token"))
                .build();
        try {
            okhttp3.Response okresponse = okclient.newCall(request).execute();

            if (!okresponse.isSuccessful()) {
                Log.e("Upload Error", "There is something wrong with the Internet connection.");
                return new JSONObject();
            }

            if (okresponse.code() == 401) {
                Log.e("Authentication Error", "Something went wrong. Please login again.");
            }

            respJson = new JSONObject(okresponse.body().string());
        } catch (Exception e) {
            e.printStackTrace();

        }

     //   Log.d("shri", "Synch Data to Received Res:" + respJson.toString());
        return respJson;

    }

    public class UploadTask {
        private Integer processUploadResponse(JSONObject response) {
            Integer successCount = 0;
            try {
                // TODO: show error
                String error = response.optString("error");

                if (error != null && !error.isEmpty() && error != "null") {
                    Toast.makeText(SyncManager.this.context, error, Toast.LENGTH_LONG).show();
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
                        Log.d("Upload onNext", "Upload failed for Story ids: " + failed.toString());
                    }

               /*     String command = response.optString("command", "");
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
            return successCount;
        }
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


        private Integer saveStoryDataFromJson(JSONObject storyJson)
                throws JSONException {
//            Log.d(LOG_TAG, "Saving Story Data: " + storyJson.toString());
            final String FEATURES = "features";

            JSONArray storyArray = storyJson.getJSONArray(FEATURES);
            Log.d(LOG_TAG, "Total stories received: " + String.valueOf(storyArray.length()));
            Log.d(LOG_TAG, "Total stories----: " + storyJson.toString());

            db.beginTransaction();
            Log.d(LOG_TAG, "DB begin Transaction");
            System.out.println(storyJson);

            try {
                for (int i = 0; i < storyArray.length(); i++) {


                    JSONObject storyObject = storyArray.getJSONObject(i);
                    if (storyObject.get("user") == null || storyObject.get("user").toString().equalsIgnoreCase("null")) {

                        //user id doesn't found
                    } else {
                        Long schoolId = storyObject.getLong("school");

                        Long userId = storyObject.getLong("user");

                        String location = storyObject.getString("location");


                        Long groupId = storyObject.getLong("group");

                        String dateOfVisit = storyObject.getString("date_of_visit");

                        String userType = storyObject.getString("user_type");

                        // Storing the story ID from server as SYSID on the device
                        // This helps in keeping the stories unique on the device
                        String sysId = storyObject.getString("id");

                        JSONArray imagesArray = storyObject.getJSONArray("images");
                        String imageName = null;
                        if (imagesArray != null && imagesArray.length() > 0) {
                            for (int k = 0; k < imagesArray.length(); k++) {
                                if (k != 0) {
                                    imageName = imageName + ",";
                                }
                                imageName = imageName + imagesArray.getJSONObject(k).getString("image_url");
                            }
                        }


                        Timestamp dateOfVisitTS;

                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                            Date parsedDate = dateFormat.parse(dateOfVisit);
                            dateOfVisitTS = new Timestamp(parsedDate.getTime());
                        } catch (Exception e) {

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

                                Double ltVal = 0d, lgVal = 0d;

                                if (location != null && !location.trim().equalsIgnoreCase(" ") && !location.trim().equalsIgnoreCase("null")) {
                                    int startbrace = location.trim().indexOf("(");

                                    int endbrace = location.trim().indexOf(")");
                                    String updatedLoc = location.substring(startbrace + 1, endbrace);
                                    try {


                                        String[] output = updatedLoc.trim().split("\\s+");
                                        ltVal = Double.parseDouble(output[0]);
                                        lgVal = Double.parseDouble(output[1]);

                                    } catch (Exception e) {
                                        Log.d("location", "Exception" + updatedLoc);


                                    }
                                    Log.d("location", ltVal + ":" + lgVal);

                                } else {
                                    Log.d("location", "-----------------------");
                                }

                                Story story = new Story()
                                        .setUserId(userId)
                                        .setSchoolId(schoolId)
                                        .setGroupId(groupId)
                                        .setRespondentType(userType)
                                        .setSynced(1)

                                        .setImage(imageName)
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
                                Log.e(LOG_TAG, "else1");
                            } else {
                                // ignore existing story with same SYSID
                                Log.e(LOG_TAG, "else");
                            }
                        } catch (Exception e) {

                            Log.e(LOG_TAG, e.toString());

                        } finally {
                            storyCursor.close();
                        }
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(LOG_TAG, "qqqqqq");
                Log.e(LOG_TAG, e.toString());

            } finally {
                db.endTransaction();
            }
            return storyArray.length();
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


    /*    private void saveQuestiongroupDataFromJson(JSONObject questiongroupJson)
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
            }
        }
*/
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
}
