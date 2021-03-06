package in.org.klp.ilpkonnect.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.widget.Toast;

import com.yahoo.squidb.android.AndroidOpenHelper;
import com.yahoo.squidb.data.ISQLiteDatabase;
import com.yahoo.squidb.data.ISQLiteOpenHelper;
import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.data.TableModel;
import com.yahoo.squidb.sql.Table;
import com.yahoo.squidb.sql.TableStatement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by bibhas on 6/17/16.
 */
// This is how you'd set up a database instance
public class KontactDatabase extends SquidDatabase {

    private static String DB_NAME = "konnect.db";
    private static final int VERSION = 5;

    private static String DB_PATH = "";
    private static Context mContext;

    public KontactDatabase(Context context) {
        mContext = context;

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            //DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
            DB_PATH = cw.getFilesDir().getAbsolutePath() + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
    }

    @Override
    protected ISQLiteOpenHelper createOpenHelper(String databaseName, OpenHelperDelegate delegate, int version) {
        return new AndroidOpenHelper(mContext, databaseName, delegate, version);
    }

    @Override
    public String getName() {
        return DB_NAME;
    }

    public String getDbPath() {
        return DB_PATH;
    }

    @Override
    protected Table[] getTables() {
        return new Table[]{
                // List all tables here
                School.TABLE,
                Boundary.TABLE,
                Survey.TABLE,
                Story.TABLE,
                Question.TABLE,
                QuestionGroup.TABLE,
                QuestionGroupQuestion.TABLE,
                Answer.TABLE,
                State.TABLE,

                Summmary.TABLE,
                Respondent.TABLE,
                SummaryInfo.TABLE,
                Surveyuser.TABLE,
                MySummary.TABLE


        };
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected boolean onUpgrade(ISQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing happens
        // to create tables, try like this -> tryCreateTable(School.TABLE)
        // https://github.com/yahoo/squidb/wiki/Implementing-database-upgrades
        switch (oldVersion) {
            case 1:
                dbRecreate();
                break;
            case 2:
                dbRecreate();
                break;
            case 3:
                dbRecreate();
                break;
            case 4:
                dbRecreate();
                break;

        }
//            case 1:
//                // These tables were added in v2
//                tryCreateTable(Story.TABLE);
//                tryCreateTable(Answer.TABLE);
//        }



        /*
         * Commented everything because we're letting SQLite Asset Helper handle the migrations.
         * because that library handles copying over the database.
         * If we had a normal application that didn't come with prepopulated database,
         * we could have used this method to manage migrations.
         */

        // https://github.com/yahoo/squidb/wiki/Implementing-database-upgrades#some-people-just-want-to-watch-the-world-burn
        return true;
    }

    // by default squidb wont let you override the id field
    // this is the way to do it
    // https://github.com/yahoo/squidb/issues/186
    public boolean insertWithId(TableModel item) {
        return persistWithOnConflict(item, TableStatement.ConflictAlgorithm.REPLACE);
    }

    public boolean insertforQuestionGroup(TableModel item) {
        boolean flag = false;
        try {
            flag = insertRow(item);
            // Log.d("test","Inserted");

        } catch (Exception e) {
            flag = persistWithOnConflict(item, TableStatement.ConflictAlgorithm.REPLACE);


        }
        return flag;
    }


    //for only schools,distrct,block,cluster
    public boolean insertNew(TableModel item) {
        boolean flag = false;
        try {
            flag = insertRow(item);

        } catch (Exception e) {
            flag = persist(item);


        }
        return flag;
    }


    public void dbRecreate()

    {
        try {
            recreate();
        } catch (Exception e) {
            //
        }
    }

    public JSONObject modelObjectToJson(TableModel obj) {
        JSONObject jobj = new JSONObject();
        try {
            for (Map.Entry<String, Object> s : obj.getMergedValues().valueSet()) {
                jobj.put(s.getKey(), s.getValue() == null ? "" : s.getValue());
            }
        } catch (JSONException e) {
            Log.d(this.toString(), e.getMessage());
            e.printStackTrace();
        }
        return jobj;
    }

    @Override
    protected void onMigrationFailed(MigrationFailedException failure) {
        super.onMigrationFailed(failure);
        try {
            recreate();
        } catch (Exception e) {

        }
    }

}