package in.org.klp.ilpkonnect.utils;

import android.content.Context;
import android.util.Log;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.LinkedHashMap;

import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Respondent;

/**
 * Created by shridhars on 1/26/2018.
 */

public class RolesUtils {




    public static LinkedHashMap<String, String> getUserRoles(Context context, KontactDatabase db,String key) {
        LinkedHashMap<String, String> userType = null;
        Query listQGQquery = Query.select().from(Respondent.TABLE)
                .orderBy(Respondent.NAME.asc()).where(Respondent.STATE_KEY.eqCaseInsensitive(key));
        SquidCursor<Respondent> respondentCursor = db.query(Respondent.class, listQGQquery);

        if (respondentCursor != null) {
            //  userType = new LinkedHashMap<String, String>();
            //  userType.put(context.getResources().getString(R.string.pleaseSelectrespondanttype), "No");
            userType=new LinkedHashMap<>();
            while (respondentCursor.moveToNext()) {
                userType.put(respondentCursor.get(Respondent.NAME).toUpperCase(), respondentCursor.get(Respondent.KEY).toUpperCase());
               // Log.d("userroles","key:"+respondentCursor.get(Respondent.NAME).toUpperCase()+":: Value:"+respondentCursor.get(Respondent.KEY).toUpperCase());
            }
        }

        return userType;
    }


    public static String getUserRoleValueForFcmGroup(Context context, KontactDatabase db,String key )
    {
        String ressponse="PARENTS";
        String userKey=key.trim().toUpperCase();

        Query listQGQquery = Query.select().from(Respondent.TABLE)
                .orderBy(Respondent.NAME.asc());
        SquidCursor<Respondent> respondentCursor = db.query(Respondent.class, listQGQquery);

        if (respondentCursor != null) {

            while (respondentCursor.moveToNext()) {
                if(respondentCursor.get(Respondent.KEY).toUpperCase().equalsIgnoreCase(userKey))
                {
                    ressponse=respondentCursor.get(Respondent.NAME).toUpperCase().replaceAll("\\s+","");
                    return ressponse;
                }

            }
        }
        return ressponse;
    }

}
