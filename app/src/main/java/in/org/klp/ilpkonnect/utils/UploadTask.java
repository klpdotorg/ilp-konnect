package in.org.klp.ilpkonnect.utils;

import android.util.Log;
import android.widget.Toast;

import com.yahoo.squidb.sql.Update;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroup;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.Survey;

public class UploadTask {
        public  Integer processUploadResponse(JSONObject response,KontactDatabase db) {
            Integer successCount = 0;
            try {
                // TODO: show error
                String error = response.optString("error");

                if (error != null && !error.isEmpty() && error != "null") {
                //    Toast.makeText(SyncManager.this.context, error, Toast.LENGTH_LONG).show();
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
                        db.deleteWhere(Story.class, Story.ID.eq(key));
                        db.deleteWhere(Answer.class, Answer.STORY_ID.eq(key));
                        successCount++;
                    }

                    JSONArray failed = response.optJSONArray("failed");
                    if (failed != null && failed.length() > 0) {
                        Log.d("Upload onNext", "Upload failed for Story ids: " + failed.toString());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return successCount;
        }
    }
