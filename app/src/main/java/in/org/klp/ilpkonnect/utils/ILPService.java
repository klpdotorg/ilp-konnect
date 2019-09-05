package in.org.klp.ilpkonnect.utils;

/**
 * Created by shridhars on 8/16/2017.
 */

public class ILPService {

    public static final String REGISTRATION="/api/v1/users/register/";

    public static final String DISTRICT="/api/v1/boundary/admin1s";
    public static final String BLOCKS="/api/v1/boundary/admin2s";
    public static final String CLUSTER="/api/v1/boundary/admin3s";
  //  public static final String SCHOOLS="/api/v1/schools/list/?geometry=yes&school_type=primaryschools";

    public static final String SCHOOLS="/api/v1/institutions/?&s_type=primaryschools";
    public static final String FORGOTPASSWORD ="/api/v1/konnect-password-change/";
    public static final String SYNC ="/api/v1/surveys/assessments/sync/";
    public static final String USER_SUMMERY ="/api/v1/surveys/usersummary?";


    public static final String TOTALSUMMARYDISTRICT="/api/v1/stories/konnect_summary";
    public static final String LOGINMOBILE ="/api/v1/users/konnect-mobile-status/?" ;
    public static final String UPDATION ="/api/v1/konnect-user-update-with-mobile/" ;


   // public static final String RESPONDENTLIST ="/api/v1/surveys/assessments/respondent-types/" ;
    public static final String UPDATE_PROFILE ="/api/v1/users/profile/?" ;


    public static final String LOGIN_API="/api/v1/users/login/";
    public static final String OTP_SIGNUP ="/api/v1/users/otp-update/" ;
    public static final String FORGOTPASSWORD_GENERATE_OTP = "/api/v1/users/otp-generate/";
    public static final String FORGOTPASSWORD_RESETWITH_OTP ="/api/v1/users/otp-password-reset/" ;
    public static final String CHECK_MOBILE ="api/v1/users/checkregistered/?";
    public static final String TOKEN_AUTH = "api/v1/users/tokenauth/?";
    public static final String SYNC_SURVEY ="/api/v1/surveys/assessments/sync/?";

}
