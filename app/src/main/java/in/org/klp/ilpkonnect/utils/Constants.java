package in.org.klp.ilpkonnect.utils;

import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.List;

import in.org.klp.ilpkonnect.MainDashList;

/**
 * Created by shridhars on 8/10/2017.
 */

public class Constants {

    public static final int SYNC_MAX_COUNT_AT_SINGLE = 1;
    public static  boolean APP_UPDATE_FLAG =false ;
    public static Query listStoryQuery;
    public static List<Long> cluster_ids;

    public static List<Long> schoolIds=new ArrayList<>();

    //1 for community survey,2 for gk monitoring
    //public static int surveyType;
    public static String GKA_IMAGE_STORAGE_PATH="GKSurveyIMG";



   // public static String GKA="24";
 //   public static String COMMUNITY="18";

    public static MainDashList mainDashList;
    public static int scoolCount=0;
}
