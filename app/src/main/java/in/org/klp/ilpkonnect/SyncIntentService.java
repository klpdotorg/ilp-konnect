package in.org.klp.ilpkonnect;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.sql.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroup;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.SessionManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by shridhars on 1/3/2018.
 */

public class SyncIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private KontactDatabase db;
    private OkHttpClient okclient;

    public SyncIntentService(String name) {
        super(name);
    }

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        db = ((KLPApplication) getApplicationContext()).getDb();
        if (AppStatus.isConnected(getApplicationContext())) {
            if (getStoryCount() > 0) {
                JSONObject object = doUploadForSyncSurvey();

                JSONObject resp = SyncDataCall(object.toString());
                processUploadResponse(resp);
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //    Log.d("service", "service destroyed");
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


    private JSONObject doUploadForSyncSurvey() {


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


        return requestJson;


    }

    private ArrayList<Integer> processUploadResponse(JSONObject response) {
        int failedCount = 0, successCount = 0;
        try {
            Log.d(this.toString(), response.toString());
            // TODO: show error
            String error = response.optString("error");

            if (error != null && !error.isEmpty() && error != "null") {
                //    Toast.makeText(MainDashList.this, error, Toast.LENGTH_LONG).show();
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

            /*    String command = response.optString("command", "");
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

    public JSONObject SyncDataCall(String data) {
        okclient = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        SessionManager mSession = new SessionManager(getApplicationContext());
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
                //log("Upload Error", "There is something wrong with the Internet connection.");
                return new JSONObject();
            }

            if (okresponse.code() == 401) {
                //log("Authentication Error", "Something went wrong. Please login again.");
                //logoutUser();
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

}
