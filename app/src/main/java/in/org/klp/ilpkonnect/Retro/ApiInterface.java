package in.org.klp.ilpkonnect.Retro;



import in.org.klp.ilpkonnect.BlocksPojo.GetBlockPojo;
import in.org.klp.ilpkonnect.ClusterPojos.GetClusterPojo;
import in.org.klp.ilpkonnect.DistrictPojos.GetDistrictPojo;
import in.org.klp.ilpkonnect.Pojo.ForgotPasswordPojo;
import in.org.klp.ilpkonnect.Pojo.ImagesPOJO;
import in.org.klp.ilpkonnect.Pojo.RegstrationResponsePojo;
import in.org.klp.ilpkonnect.SchoolsPojos.GetSchoolsPojo;
import in.org.klp.ilpkonnect.TotalSummaryPOJOs.SummaryTotalPojo;
import in.org.klp.ilpkonnect.utils.ILPService;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiInterface {

    @Headers( "Content-Type: application/json" )
    @POST("/api/v1/sync")
    @FormUrlEncoded
    Call<ResponseBody> loginKLP(@Body RequestBody jsonObject,@Header("Authorization") String token);


    @POST(ILPService.REGISTRATION)
    @FormUrlEncoded
    Call<RegstrationResponsePojo> registrationService(@Field("email") String email,@Field("mobile_no") String mobilenumber,
                                                      @Field("first_name") String firstName,@Field("last_name") String lastName,
                                                      @Field("password") String password,@Field("source") String source,
                                                      @Field("user_type") String usertype,
                                                      @Field("dob") String dob);


    @GET(ILPService.DISTRICT)
    Call<GetDistrictPojo> getAllDistrictData();

   // @GET(ILPService.BLOCKS)
  //  Call<GetBlockPojo> getAllBlocksData();
@GET
    Call<GetBlockPojo> getAllBlocksData(@Url String url);

    @GET
    Call<GetClusterPojo> getAllClusterData(@Url String url);

    @GET
    Call<GetSchoolsPojo> getAllSchoolsData(@Url String url);


    @POST(ILPService.FORGOTPASSWORD)
    @FormUrlEncoded
    Call<ForgotPasswordPojo> setForgotPassword(@Field("mobile") String mobile,@Field("dob") String dob,
                                               @Field("password") String password);


  //  @GET("/api/v1/stories/konnect_summary/")
    @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummaryData(@Query("admin1") String admin, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate);

    @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummaryBlockData(@Query("admin2") String admin, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate);

    @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummaryClusterData(@Query("admin3") String admin, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate);

    @GET("/api/v1/stories/konnect_summary/")
    Call<SummaryTotalPojo> getAllSummarySchoolData(@Query("school_id") String school_id, @Query("group") String groupId,@Query("from") String fromdate,@Query("to") String toDate);


    @GET("/api/v1/stories/images/?")
   Call<ImagesPOJO> getImages(@Query("school_id") String school_id);

    @GET("/api/v1/stories/images/?")
    Call<ImagesPOJO> getImagesbyDate(@Query("school_id") String school_id,@Query("from") String fromdate,@Query("to") String todate);



}