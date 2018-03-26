package in.org.klp.ilpkonnect.Retro;



import in.org.klp.ilpkonnect.BlocksPojo.BlockDetailPojo;
import in.org.klp.ilpkonnect.ClusterPojos.ClusterDetailPojo;
import in.org.klp.ilpkonnect.DistrictPojos.DistrictPojos;
import in.org.klp.ilpkonnect.Pojo.ForgotPassswordOtpPojo;
import in.org.klp.ilpkonnect.Pojo.ForgotPasswordPojo;
import in.org.klp.ilpkonnect.Pojo.ImagesPOJO;
import in.org.klp.ilpkonnect.Pojo.LoginMobilePojo;
import in.org.klp.ilpkonnect.Pojo.RegstrationResponsePojo;
import in.org.klp.ilpkonnect.Pojo.ResetPasswordPojo;
import in.org.klp.ilpkonnect.Pojo.UpdateProfilePojo;
import in.org.klp.ilpkonnect.QuestionsPojoPack.QuestionsPojos;
import in.org.klp.ilpkonnect.RepondentPack.RespondentListPojo;
import in.org.klp.ilpkonnect.SchoolDataPojo.SchoolDataPojo;
import in.org.klp.ilpkonnect.SurveyAndQuestionGPojoPsck.SurveyAndQuestionGrPojo;
import in.org.klp.ilpkonnect.SurveyDetailPojos.SurveyDeailPojo;
import in.org.klp.ilpkonnect.TotalSummaryPOJOs.SummaryTotalPojo;
import in.org.klp.ilpkonnect.UserRolesPojosPackage.UserRolesPojos;
import in.org.klp.ilpkonnect.utils.ILPService;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiInterface {

    /*@Headers( "Content-Type: application/json" )
    @POST("/api/v1/sync")
    @FormUrlEncoded
    Call<ResponseBody> loginKLP(@Body RequestBody jsonObject,@Header("Authorization") String token);*/


/*

    @GET(ILPService.LOGINMOBILE)
    Call<LoginMobilePojo> loginMobile(@Query("mobile") String mobile);

*/



    @POST(ILPService.REGISTRATION)
    @FormUrlEncoded
    Call<RegstrationResponsePojo> registrationService(@Field("email") String email,@Field("mobile_no") String mobilenumber,
                                                      @Field("first_name") String firstName,@Field("last_name") String lastName,
                                                      @Field("password") String password,@Field("source") String source,
                                                      @Field("user_type") String usertype,
                                                      @Field("dob") String dob,@Field("state") String stateKey);



    @POST(ILPService.REGISTRATION)
    @FormUrlEncoded
    Call<RegstrationResponsePojo> registrationServiceWithoutEmail(@Field("mobile_no") String mobilenumber,
                                                                  @Field("first_name") String firstName,@Field("last_name") String lastName,
                                                                  @Field("password") String password,@Field("source") String source,
                                                                  @Field("user_type") String usertype,
                                                                  @Field("dob") String dob,@Field("state") String stateKey);
  /*  @POST(ILPService.UPDATION)
    @FormUrlEncoded
    Call<RegstrationResponsePojo> updateService(@Field("email") String email,@Field("mobile") String mobilenumber,
                                                      @Field("first_name") String firstName,@Field("last_name") String lastName,
                                                      @Field("password") String password,@Field("source") String source,
                                                      @Field("user_type") String usertype,
                                                      @Field("dob") String dob,@Field("state") String stateKey);
*/

    @GET
    Call<DistrictPojos> getAllDistrictData(@Url String url);

   // @GET(ILPService.BLOCKS)
  //  Call<GetBlockPojo> getAllBlocksData();
@GET
    Call<BlockDetailPojo> getAllBlocksData(@Url String url,@Header("Authorization") String authHeader);

    @GET
    Call<ClusterDetailPojo> getAllClusterData(@Url String url,@Header("Authorization") String authHeader);

    @GET
    Call<SchoolDataPojo> getAllSchoolsData(@Url String url,@Header("Authorization") String authHeader);


 /*   @POST(ILPService.FORGOTPASSWORD)
    @FormUrlEncoded
    Call<ForgotPasswordPojo> setForgotPassword(@Field("mobile") String mobile,@Field("dob") String dob,
                                               @Field("password") String password);*/


  /*//  @GET("/api/v1/stories/konnect_summary/")
    @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummaryData(@Query("admin1") String admin, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate,@Query("state") String stateKey);

   *//* @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummaryBlockData(@Query("admin2") String admin, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate,@Query("state") String stateKey);
*//*
    @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummaryClusterData(@Query("admin3") String admin, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate,@Query("state") String stateKey);

    @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummarySchoolData(@Query("school_id") String school_id, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate,@Query("state") String stateKey);
*/

    @GET("/api/v1/surveys/assessments/images?")
    Call<ImagesPOJO> getImages(@Query("school_id") String school_id,@Query("state") String stateKey,@Header("Authorization") String authHeader);

    @GET("/api/v1/surveys/assessments/images?")
    Call<ImagesPOJO> getImagesbyDate(@Query("school_id") String school_id,@Query("from") String fromdate,@Query("to") String todate,@Query("state") String stateKey,@Header("Authorization") String authHeader);


    //new

    @GET("/api/v1/boundary/states")
    Call<UserRolesPojos> getStateDeailFromNetwork();

    @GET("/api/v1/surveys/")
    Call<SurveyDeailPojo>getSurveyDetailsFromNetwork();





  @GET("/api/v1/surveys/questiongroupdetails/?survey_tag=gka")
    Call<ResponseBody>fetchReportData(@Query("questiongroup_id") long questiongroup_id,@Query("boundary_id") long boundary_id,@Query("from") String from,@Query("to") String to,@Query("state") String stateKey,@Header("Authorization") String authHeader,@Query("survey_id") long surveyid,@Query("survey_tag") String survey_tag);



    @GET("/api/v1/surveys/questiongroupdetails/?survey_tag=gka")
    Call<ResponseBody>fetchReportDataSchool(@Query("questiongroup_id") long questiongroup_id,@Query("institution_id") long institution_id,@Query("from") String from,@Query("to") String to,@Query("state") String stateKey,@Header("Authorization") String authHeader,@Query("survey_id") long surveyid,@Query("survey_tag") String survey_tag);







    @GET
    Call<UserRolesPojos>getRespondentListFromNetwork(@Url String url);




   /* @GET("/api/v1/surveys/questiongroupdetails/?")
    Call<ResponseBody> getMySummary(@Query("questiongroup") long questiongroup);

*/

    @GET("/api/v1/surveys/usersummary?")
    Call<ResponseBody> getMySummary(@Query("questiongroup_id") long questiongroup,@Query("from") String from,@Query("to") String to,@Header("Authorization") String authHeader,@Query("state") String statekey,@Query("survey_id") long surveyid);


    @FormUrlEncoded
    @PUT(ILPService.UPDATE_PROFILE)
        Call<ResponseBody> setUpdateProfile(@Field("first_name") String firstName, @Field("last_name") String lastName,
                                              @Field("user_type") String userType,@Field("dob") String dob,
                                                 @Field("email") String email,
                                                 @Header("Authorization") String authHeader,@Field("state") String stateKey);


    @FormUrlEncoded
    @PUT(ILPService.UPDATE_PROFILE)
    Call<ResponseBody> setUpdateProfileWithoutEmail(@Field("first_name") String firstName, @Field("last_name") String lastName,
                                        @Field("user_type") String userType,@Field("dob") String dob,
                                         @Header("Authorization") String authHeader,@Field("state") String stateKey);






    @POST(ILPService.LOGIN_API)
    @FormUrlEncoded
    Call<ResponseBody> userLogin(@Field("username") String mobile, @Field("password") String password,@Field("state") String statekey);


    @POST(ILPService.OTP_SIGNUP)
    @FormUrlEncoded
    Call<ResponseBody> otpSignUp(@Field("mobile_no") String mobile,@Field("otp") String otp,@Field("state") String statekey);



    @POST(ILPService.FORGOTPASSWORD_GENERATE_OTP)
    @FormUrlEncoded
    Call<ForgotPassswordOtpPojo>generateOtpForForgotPassword(@Field("mobile_no") String mobile_no,@Field("state") String statekey);



    @POST(ILPService.FORGOTPASSWORD_RESETWITH_OTP)
    @FormUrlEncoded
    Call<ResetPasswordPojo>forgotPasswordResetWithOTP(@Field("mobile_no") String mobile_no,@Field("otp") String otp,@Field("password") String password,@Field("state") String statekey);

    @GET
    Call<QuestionsPojos> fetchCummunitySurveyQuestions(@Url String url,@Header("Authorization") String authHeader);


    @POST("/api/v1/surveys/assessments/sync/")
    Call<ResponseBody> syncDataforServerWithRetro(@Body RequestBody requestBody,@Header("Authorization") String authHeader);


    @GET
    Call<SurveyAndQuestionGrPojo>getSurveyAndQuestionGFromNetworn(@Url String url,@Header("Authorization") String authHeader);
}