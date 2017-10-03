package in.org.klp.ilpkonnect.utils;

import com.yahoo.squidb.sql.Query;

import java.util.List;

import in.org.klp.ilpkonnect.MainDashList;
import in.org.klp.ilpkonnect.db.Story;

/**
 * Created by shridhars on 8/10/2017.
 */

public class Constants {

    public static Query listStoryQuery;
    public static List<Long> cluster_ids;

    //1 for community survey,2 for gk monitoring
    public static int surveyType;
    public static String GKA_IMAGE_STORAGE_PATH="GKSurveyIMG";

    //this one will help in selecting question from db
    //1 means english else native language
    public static int Lang;


    public static MainDashList mainDashList;
    public static int scoolCount=0;
}
