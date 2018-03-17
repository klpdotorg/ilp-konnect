package in.org.klp.ilpkonnect.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.sql.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.org.klp.ilpkonnect.BlocksPojo.BlockDetailPojo;
import in.org.klp.ilpkonnect.ClusterPojos.ClusterDetailPojo;
import in.org.klp.ilpkonnect.DistrictPojos.DistrictPojos;
import in.org.klp.ilpkonnect.Errorpack.ForgotOTPError;
import in.org.klp.ilpkonnect.Errorpack.InvalidOTp;
import in.org.klp.ilpkonnect.InterfacesPack.SchoolStateInterface;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.InterfacesPack.UserRolesInterface;
import in.org.klp.ilpkonnect.KLPApplication;
import in.org.klp.ilpkonnect.Pojo.ForgotPassswordOtpPojo;
import in.org.klp.ilpkonnect.Pojo.ResetPasswordPojo;
import in.org.klp.ilpkonnect.Pojo.UpdateProfilePojo;
import in.org.klp.ilpkonnect.QuestionsPojoPack.QuestionsPojos;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.RepondentPack.RespondentListPojo;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.SchoolDataPojo.SchoolDataPojo;
import in.org.klp.ilpkonnect.SurveyAndQuestionGPojoPsck.Questiongroup;
import in.org.klp.ilpkonnect.SurveyAndQuestionGPojoPsck.SurveyAndQuestionGrPojo;
import in.org.klp.ilpkonnect.SurveyDetailPojos.Result;
import in.org.klp.ilpkonnect.SurveyDetailPojos.SurveyDeailPojo;
import in.org.klp.ilpkonnect.UserRolesPojosPackage.UserRolesPojos;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;

import in.org.klp.ilpkonnect.db.MySummary;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.State;
import in.org.klp.ilpkonnect.db.SummaryInfo;
import in.org.klp.ilpkonnect.db.Summmary;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.db.Surveyuser;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shridhars on 1/19/2018.
 */

public class ProNetworkSettup {

    Context context;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    private KontactDatabase db;
    boolean foruserlist;
    long oneDay = 86400000;
    double schoolCountp = 0;

    public ProNetworkSettup(Context context) {
        this.context = context;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        db = ((KLPApplication) context.getApplicationContext()).getDb();
        foruserlist = false;
    }


    public void getSurveyandQuestionGroup(String url, final String stateKey, final String token, final StateInterface stateInterface) {
        apiInterface.getSurveyAndQuestionGFromNetworn(url, token).enqueue(new Callback<SurveyAndQuestionGrPojo>() {
            @Override
            public void onResponse(Call<SurveyAndQuestionGrPojo> call, Response<SurveyAndQuestionGrPojo> response) {

                if (response.body() != null && response.code() == 200) {

                    parseSurveyandQuestionGroup(response.body(), stateInterface, stateKey, token);


                } else {
                    stateInterface.failed(context.getResources().getString(R.string.surveyLoadingfailed));

                }


            }

            @Override
            public void onFailure(Call<SurveyAndQuestionGrPojo> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t));

            }
        });

    }

    private void parseSurveyandQuestionGroup(SurveyAndQuestionGrPojo body, StateInterface stateInterface, String stateKey, String token) {


        if (body.getResults() != null && body.getResults().size() > 0) {

            db.deleteAll(Surveyuser.class);
            db.deleteAll(Survey.class);
            for (int i = 0; i < body.getResults().size(); i++) {
                long surveyId = body.getResults().get(i).getId();
                String surveyNameEng = body.getResults().get(i).getName();
                String surveyNameLoc;
                if (body.getResults().get(i).getLangName() != null) {
                    surveyNameLoc = body.getResults().get(i).getLangName();
                } else {
                    surveyNameLoc = body.getResults().get(i).getName();
                }


                if (body.getResults().get(i).getQuestiongroups() != null && body.getResults().get(i).getQuestiongroups().size() > 0) {
                    for (int j = 0; j < body.getResults().get(i).getQuestiongroups().size(); j++) {
                        List<Questiongroup> questionGroupList = body.getResults().get(i).getQuestiongroups();
                        String source = questionGroupList.get(j).getSourceName().trim();
                        if (source.equalsIgnoreCase("mobile") || source.equalsIgnoreCase("konnectsms")) {
                            // then store survey and question group
                            long surveyGroupId = questionGroupList.get(j).getId();
                            String partnerName = body.getResults().get(i).getPartner();
                            boolean imageRequired;
                            try {
                                imageRequired = questionGroupList.get(j).getImageRequired();
                            } catch (Exception e) {
                                imageRequired = false;
                            }

                            boolean commentRequired;
                            try {
                                commentRequired = questionGroupList.get(j).getCommentsRequired();
                            } catch (Exception e) {
                                commentRequired = false;
                            }
                            Survey survey = new Survey();
                            survey.setId(surveyId);
                            survey.setName(surveyNameEng);
                            survey.setNameLoc(surveyNameLoc != null && !surveyNameLoc.equalsIgnoreCase("") && !surveyNameLoc.equalsIgnoreCase("null") ? surveyNameLoc : surveyNameEng);
                            survey.setPartner(partnerName);
                            survey.setGradeRequired(questionGroupList.get(j).getGroupText());

                            survey.setQuestionGroupId(surveyGroupId);
                            survey.setStateKey(stateKey);
                            survey.setIsImageRequired(imageRequired);
                            survey.setIsCommentRequired(commentRequired);
                            boolean respondentSelection=false;
                            if(body.getResults().get(i).getUserTypes()!=null&&body.getResults().get(i).getUserTypes().size()>0)
                                respondentSelection=false;
                            else
                                respondentSelection=true;
                            survey.setIsRespondentRequired(respondentSelection);
                            boolean b = db.insertforQuestionGroup(survey);


                            if (body.getResults().get(i).getUserTypes() != null && body.getResults().get(i).getUserTypes().size() > 0) {

                                for (int k = 0; k < body.getResults().get(i).getUserTypes().size(); k++) {
                                    Surveyuser surveyuser = new Surveyuser();
                                    surveyuser.setSurveyid(surveyId);
                                    surveyuser.setName(body.getResults().get(i).getUserTypes().get(k).getUsertype());
                                    db.insertforQuestionGroup(surveyuser);
                                }

                            } else {
                                //it is public survey
                                Surveyuser surveyuser = new Surveyuser();
                                surveyuser.setSurveyid(surveyId);
                                surveyuser.setName("XYZ");
                                db.insertforQuestionGroup(surveyuser);
                            }


                        }


                    }


                }

            }
            if (body.getNext() != null) {
                getSurveyandQuestionGroup(body.getNext().toString(), stateKey, token, stateInterface);
                //Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();

            } else {

                stateInterface.success("Success");
                //  stateInterface.success(getDistrictCount(stateKey) + ":" + context.getResources().getString(R.string.districtDownloaded));

            }


        } else {
            //  Toast.makeText(context, "No Surveys", Toast.LENGTH_SHORT).show();
            stateInterface.success("No surveys found");
        }


    }


   /* public void getSurveyDetail(final StateInterface stateInterface) {

        apiInterface.getSurveyDetailsFromNetwork().enqueue(new Callback<SurveyDeailPojo>() {
            @Override
            public void onResponse(Call<SurveyDeailPojo> call, Response<SurveyDeailPojo> response) {
                progressDialog.dismiss();
                if (response.code() == 200 && response.isSuccessful()) {
                    // showToast("Survey details success" + response.body().getResults().size());
                    stateInterface.success("Success");
                    parseSurveyListData(response);


                } else {
                    stateInterface.failed(context.getResources().getString(R.string.surveyLoadingfailed));
                }


            }

            @Override
            public void onFailure(Call<SurveyDeailPojo> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));

            }
        });


    }*/
/*

    private void parseSurveyListData(Response<SurveyDeailPojo> response) {

        if (response.body() != null && response.body().getResults().size() > 0) {

            for (Result result : response.body().getResults()) {

                if (result.getId() == 11 || result.getId() == 7) {

                    Survey survey = new Survey();
                    survey.setId(result.getId());
                    survey.setPartner(result.getPartner());
                    survey.setName(result.getName());
                    survey.setNameLoc(result.getName());
                    if (result.getId() == 11) {//gka
                        survey.setQuestionGroupId(24l);
                    } else {
                        //comunity
                        survey.setQuestionGroupId(18l);
                    }
                    boolean b = db.insertNew(survey);
                    //   Log.d("test", b + "surveys:" + result.getName());

                }


            }

        }

        //db.insertWithId(state);
    }
*/


    public void getStateAndUserDeail(final StateInterface stateInterface) {

        apiInterface.getStateDeailFromNetwork().enqueue(new Callback<UserRolesPojos>() {
            @Override
            public void onResponse(Call<UserRolesPojos> call, Response<UserRolesPojos> response) {

                JSONObject jsonObject;
                String stringJsonData;
                if (response.code() == 200 && response.isSuccessful()) {


                    parseStateDeailIntoDb(response);
                    stateInterface.success("success");


                } else {

                    stateInterface.failed(context.getResources().getString(R.string.stateLoadingFailed));
                }


            }

            @Override
            public void onFailure(Call<UserRolesPojos> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }

    public void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void updateProgressDialog(String message) {
        progressDialog.setMessage(message);
    }


    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public void showLog(String message) {
        Log.d("test", message);
    }

    public void parseStateDeailIntoDb(Response<UserRolesPojos> jsonObject) {

        if (jsonObject != null & jsonObject.body() != null && jsonObject.body().getResults() != null) {

            db.deleteAll(State.class);
            db.deleteAll(Respondent.class);
        }

        for (int i = 0; i < jsonObject.body().getResults().size(); i++) {
            //then it will be state detail


            String stateCode = jsonObject.body().getResults().get(i).getStateCode();
            if (!stateCode.equalsIgnoreCase("ilp")) {
                String stateLongForm = jsonObject.body().getResults().get(i).getLongForm();
                State state = new State();
                state.setState(jsonObject.body().getResults().get(i).getName());
                state.setStateLocText(jsonObject.body().getResults().get(i).getName());
                state.setStatekey(stateCode);
                state.setLangName(jsonObject.body().getResults().get(i).getLangName());
                state.setLangKey(jsonObject.body().getResults().get(i).getLangKey());
                db.insertNew(state);

                //loading user roles
                Respondent respondent = new Respondent();

                for (int j = 0; j < jsonObject.body().getResults().get(i).getRespondentTypes().size(); j++) {
                    respondent = new Respondent();
                    String key = jsonObject.body().getResults().get(i).getRespondentTypes().get(j).getCharId();
                    if (!key.equalsIgnoreCase("UK")) {
                        respondent.setKey(key);
                        respondent.setName(jsonObject.body().getResults().get(i).getRespondentTypes().get(j).getName());
                        respondent.setNamLoc(jsonObject.body().getResults().get(i).getRespondentTypes().get(j).getName());
                        respondent.setStateKey(stateCode);
                        boolean b = db.insertWithId(respondent);
                    }
                }


            }

        }


    }


   /* public void getRespondentList(String url, final UserRolesInterface userRolesInterface) {

        apiInterface.getRespondentListFromNetwork(url).enqueue(new Callback<UserRolesPojos>() {

            @Override
            public void onResponse(Call<UserRolesPojos> call, Response<UserRolesPojos> response) {

                if (response.isSuccessful() && response.code() == 200) {

                    if (response.body().getResults() != null && response.body().getResults().size() > 0) {

                        if (foruserlist == false) {
                            foruserlist = true;
                            db.deleteAll(Respondent.class);
                        }
                        Toast.makeText(context,response.body().getResults().size()+"",Toast.LENGTH_SHORT).show();


                        for (int i = 0; i < response.body().getResults().size(); i++) {


                       *//*     Respondent respondent = new Respondent();
                            String stateKey=response.body().getResults().get(i).getStateCode();
                            for(int j=0;j<response.body().getResults().get(0).getRespondentTypes().size();j++) {
                                respondent.setKey(response.body().getResults().get(0).getRespondentTypes().get(j).getCharId());
                                respondent.setName(response.body().getResults().get(0).getRespondentTypes().get(j).getName());
                                respondent.setNamLoc(response.body().getResults().get(0).getRespondentTypes().get(j).getName());
                                respondent.setStateKey(stateKey);
                            }
                            boolean b = db.insertWithId(respondent);*//*


                        }
                        userRolesInterface.success("Success");
                      *//*  if (response.body().getNext() != null && !response.body().getNext().equalsIgnoreCase("null")) {
                            getRespondentList(response.body().getNext(), userRolesInterface);
                        } else {

                            userRolesInterface.success("Success");
                        }*//*

                    } else {

                        userRolesInterface.failed("User roles loading failed");
                    }


                } else {

                    userRolesInterface.failed("User roles loading failed");

                }


            }

            @Override
            public void onFailure(Call<UserRolesPojos> call, Throwable t) {

                userRolesInterface.failed(getFailureMessage(t));
            }
        });


    }*/

    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void DownloadBlocksData(String url, final String statekey, final boolean isDataAlreadyDownloaded, final String token, final SchoolStateInterface stateInterface) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getAllBlocksData(url, token).enqueue(new Callback<BlockDetailPojo>() {
            @Override
            public void onResponse(Call<BlockDetailPojo> call, Response<BlockDetailPojo> response) {

                if (response.isSuccessful()) {
                    try {
                        double totalRecordsCount = response.body().getCount();
                        double resposeSingle = response.body().getResults().size();
                        double temp = Double.parseDouble((resposeSingle / totalRecordsCount) + "");
                        double onepercent = Double.parseDouble(((100 * temp)) + "");
                        schoolCountp = schoolCountp + onepercent;
                        Log.d("sssss", schoolCountp + "");
                        if (schoolCountp > 0 && schoolCountp <= 100) {
                            stateInterface.update((int) schoolCountp);

                        }
                    } catch (Exception e) {
                        //execption
                    }
                    parseBlockDataToDb(response, stateInterface, statekey, isDataAlreadyDownloaded, token);
                } else {

                    //Exception
                    stateInterface.failed(context.getResources().getString(R.string.blocksDataLoadingFailed));
                }

            }

            @Override
            public void onFailure(Call<BlockDetailPojo> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }

    private void parseBlockDataToDb(Response<BlockDetailPojo> response, SchoolStateInterface stateInterface, String stateKey, boolean isDataAlreadyDownloaded, String token) {

        for (int i = 0; i < response.body().getResults().size(); i++) {


            Boundary boundary = new Boundary();
            if (response.body().getResults().get(i).getType().equalsIgnoreCase("primary")) {
                boundary.setId(response.body().getResults().get(i).getId());
                boundary.setParentId(response.body().getResults().get(i).getParentBoundary().getId());
                boundary.setName(response.body().getResults().get(i).getName());
                boundary.setHierarchy("block");
                boundary.setType("primaryschool");
                if (isDataAlreadyDownloaded == false) {
                    boundary.setIsFlag(false);
                    boundary.setIsFlagCB(false);
                }
                boundary.setStateKey(stateKey);
                String locName = response.body().getResults().get(i).getLangName();
                if (locName == null)
                    boundary.setLocName(response.body().getResults().get(i).getName());
                else
                    boundary.setLocName(locName);
                try {
                    db.insertNew(boundary);
                } catch (Exception e) {

                    db.persist(boundary);
                }
            }


        }
        if (response.body().getNext() != null) {
            DownloadBlocksData(response.body().getNext().toString(), stateKey, isDataAlreadyDownloaded, token, stateInterface);
        } else {
            stateInterface.success("success");
        }


    }

    public void userLogin(String mobile, String password, String stateKey, final StateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.userLogin(mobile, password, stateKey).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        stateInterface.success(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        //  stateInterface.failed(context.getResources().getString(R.string.oops));
                    }

                } else if (response.code() == 401) {

                    stateInterface.failed(errorHandling(response.errorBody()).getDetail());
                } else if (response.code() == 400) {
                    //email or password invalid
                    stateInterface.failed(errorHandling(response.errorBody()).getNonFieldErrors().get(0));
                } else {
                    //email or password invalid
                    stateInterface.failed(context.getResources().getString(R.string.oops));
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                stateInterface.failed(getFailureMessage(t));


            }
        });


    }


    public String getFailureMessage(Throwable t) {
        if (t instanceof IOException) {
            return context.getResources().getString(R.string.netWorkError);
            // logging probably not necessary
        } else {
            return context.getResources().getString(R.string.oops);
        }


    }


    public void downloadStateData(String url, final String stateKey, final StateInterface stateInterface) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        apiInterface.getAllDistrictData(url).enqueue(new Callback<DistrictPojos>() {
            @Override
            public void onResponse(Call<DistrictPojos> call, Response<DistrictPojos> response) {

                if (response.isSuccessful()) {
                    AddDataToDistrcit(response, stateKey, stateInterface);

                } else {
                    stateInterface.failed(context.getResources().getString(R.string.districtDataLoadingFailed));

                }

            }

            @Override
            public void onFailure(Call<DistrictPojos> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }


    public void AddDataToDistrcit(Response<DistrictPojos> response, String stateKey, StateInterface stateInterface) {

        for (int i = 0; i < response.body().getResults().size(); i++) {

            Boundary boundary = new Boundary();
            if (response.body().getResults().get(i).getType().equalsIgnoreCase("primary")) {
                boundary.setId(response.body().getResults().get(i).getId());
                boundary.setParentId(1L);
                boundary.setName(response.body().getResults().get(i).getName());
                boundary.setHierarchy("district");
                boundary.setType("primaryschool");
                boundary.setStateKey(stateKey);
                boundary.setIsFlag(false);
                boundary.setIsFlagCB(false);
                String locName = response.body().getResults().get(i).getLangName();
                if (locName == null)
                    boundary.setLocName(response.body().getResults().get(i).getName());
                else
                    boundary.setLocName(locName);

                try {
                    db.insertNew(boundary);
                } catch (Exception e) {
                    db.persist(boundary);
                }
            }
            // Log.d("w", i + "");
        }
        if (response.body().getNext() != null) {
            downloadStateData(response.body().getNext().toString(), stateKey, stateInterface);
            //Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();

        } else {

            stateInterface.success(getDistrictCount(stateKey) + ":" + context.getResources().getString(R.string.districtDownloaded));

        }

    }


    public MyErrorMessage errorHandling(ResponseBody response) {
        Gson gson = new Gson();
        MyErrorMessage messageObject = gson.fromJson(response.charStream(), MyErrorMessage.class);

        return messageObject;
    }


    public void DownloadClusterData(String url, final long distId, final String stateKey, final boolean isDataAlreadyDownloaded, final String token, final SchoolStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getAllClusterData(url, token).enqueue(new Callback<ClusterDetailPojo>() {
            @Override
            public void onResponse(Call<ClusterDetailPojo> call, Response<ClusterDetailPojo> response) {

                if (response.isSuccessful()) {
                    try {
                        double totalRecordsCount = response.body().getCount();
                        double resposeSingle = response.body().getResults().size();
                        double temp = Double.parseDouble((resposeSingle / totalRecordsCount) + "");
                        double onepercent = Double.parseDouble(((100 * temp)) + "");
                        schoolCountp = schoolCountp + onepercent;
                        Log.d("sssss", schoolCountp + "-");
                        if (schoolCountp > 0 && schoolCountp <= 100) {
                            stateInterface.update((int) schoolCountp);
                        }
                    } catch (Exception e) {
                        //execption
                    }


                    parseClusterDataToDb(response, distId, stateInterface, stateKey, isDataAlreadyDownloaded, token);
                } else {

                    stateInterface.failed(context.getResources().getString(R.string.clusterDataLoadingFailed));
                    //   Toast.makeText(getApplicationContext(), "Ex", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ClusterDetailPojo> call, Throwable t) {
                //  Toast.makeText(getApplicationContext(), "Ex1", Toast.LENGTH_SHORT).show();
                // );
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }

    private void parseClusterDataToDb(Response<ClusterDetailPojo> response, long distId, SchoolStateInterface stateInterface, String stateKey, boolean isDataAlreadyDownloaded, String token) {

        for (int i = 0; i < response.body().getResults().size(); i++) {
            Boundary boundary = new Boundary();
            if (response.body().getResults().get(i).getType().equalsIgnoreCase("primary")) {
                boundary.setId(response.body().getResults().get(i).getId());
                boundary.setParentId(response.body().getResults().get(i).getParentBoundary().getId());
                boundary.setName(response.body().getResults().get(i).getName());
                boundary.setHierarchy("cluster");
                if (isDataAlreadyDownloaded == false) {
                    boundary.setIsFlag(false);
                    boundary.setIsFlagCB(false);
                }
                boundary.setType("primaryschool");
                boundary.setStateKey(stateKey);
                String locName = response.body().getResults().get(i).getLangName();
                if (locName == null)
                    boundary.setLocName(response.body().getResults().get(i).getName());
                else
                    boundary.setLocName(locName);

                try {
                    db.insertNew(boundary);
                } catch (Exception e) {
                    db.persist(boundary);
                }
            }
            // Log.d("w", i + "");
        }
        if (response.body().getNext() != null) {
            DownloadClusterData(response.body().getNext(), distId, stateKey, isDataAlreadyDownloaded, token, stateInterface);
            //Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
            //  Log.d("Sreee", "------------------------NEXT----");
        } else {
            Boundary boundary = new Boundary();
            boundary.setIsFlagCB(true);
            Update update = Update.table(Boundary.TABLE).where(Boundary.ID.eq(distId));
            Update boundaryupdate = update.fromTemplate(boundary);
            db.update(boundaryupdate);
            stateInterface.success("success");
            //  Log.d("Sreee", "------------------------FINISH----");
        }


    }

    public void setProfileUpdateAction(String firstName, String lastName, final String email, String dob, String usertype, String headertoken, String stateKey, final StateInterface stateInterface) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> api = null;

        api = apiInterface.setUpdateProfile(firstName, lastName, usertype, dob, email, headertoken, stateKey);
        /*if (TextUtils.isEmpty(email)) {
            api = apiInterface.setUpdateProfileWithoutEmail(firstName, lastName, usertype, dob, headertoken, stateKey);

        } else {
            api = apiInterface.setUpdateProfile(firstName, lastName, usertype, dob, email, headertoken, stateKey);
        }*/

        api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response != null && response.code() == 200 && response.isSuccessful()) {
                    try {

                        String data = response.body().string();
                        // Log.d("test", data);
                        stateInterface.success(data);
                        updateSessionData(data);

                    } catch (IOException e) {
                        e.printStackTrace();
                        stateInterface.failed(context.getResources().getString(R.string.profileUpdationFailed));
                    }

                } else {

                    stateInterface.failed(context.getResources().getString(R.string.profileUpdationFailed));

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t));

            }
        });

    }


    public void DownloadSchoolData(String url, final long blockid, final long distId, final String token, final SchoolStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        apiInterface.getAllSchoolsData(url, token).enqueue(new Callback<SchoolDataPojo>() {
            @Override
            public void onResponse(Call<SchoolDataPojo> call, Response<SchoolDataPojo> response) {

                if (response.isSuccessful()) {

                    try {
                        double totalRecordsCount = response.body().getCount();
                        double resposeSingle = response.body().getFeatures().size();
                        double temp = Double.parseDouble((resposeSingle / totalRecordsCount) + "");
                        double onepercent = Double.parseDouble(((100 * temp)) + "");
                        schoolCountp = schoolCountp + onepercent;
                        if (schoolCountp > 0 && schoolCountp <= 100) {
                            stateInterface.update((int) schoolCountp);
                        }
                    } catch (Exception e) {
                        //execption
                    }


                    parseSchoolData(response, blockid, distId, stateInterface, token);
                } else {

                    stateInterface.failed(context.getResources().getString(R.string.schoolloadingfailed));

                }


            }

            @Override
            public void onFailure(Call<SchoolDataPojo> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t));

            }
        });

    }


    private void parseSchoolData(Response<SchoolDataPojo> response, long blockid, long distId, SchoolStateInterface stateInterface, String token) {

        for (int i = 0; i < response.body().getFeatures().size(); i++) {


            School schol = new School();
            schol.setId(response.body().getFeatures().get(i).getProperties().getId());
            schol.setName(response.body().getFeatures().get(i).getProperties().getName());
            schol.setBoundaryId(response.body().getFeatures().get(i).getProperties().getBoundary().getId());
            schol.setDise(response.body().getFeatures().get(i).getProperties().getDiseCode() + "");

           /* String locName= response.body().getResults().get(i).getLangName();
            if(locName==null)
                schol.setLocName(response.body().getResults().get(i).getName());
            else
                schol.setLocName(locName);

*/



         /*   try {
                schol.setLat(response.body().getFeatures().get(i).getGeometry().getCoordinates().get(0));
                schol.setLng(response.body().getFeatures().get(i).getGeometry().getCoordinates().get(1));
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(),response.body().getFeatures().get(i).getProperties().getId()+"",Toast.LENGTH_SHORT).show();
                schol.setLat(0d);
                schol.setLng(0d);


            }*/


            try {
                Boolean b = db.insertNew(schol);
            } catch (Exception e) {
                Boolean b = db.persist(schol);
            }
        }
        if (response.body().getNext() != null) {
            DownloadSchoolData(response.body().getNext(), blockid, distId, token, stateInterface);
            // stateInterface.success("success");//remove this

        } else {
            Boundary boundary = new Boundary();
            boundary.setIsFlag(true);
            Update update = Update.table(Boundary.TABLE).where(Boundary.ID.eq(distId).or(Boundary.ID.eq(blockid)));
            Update boundaryupdate = update.fromTemplate(boundary);
            db.update(boundaryupdate);
            // stateInterface.success("success");
            stateInterface.success("success");
        }


    }


    private void updateSessionData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String firstName = jsonObject.getString("first_name");
            String lastName = jsonObject.getString("last_name");
            String dob = jsonObject.getString("dob");
            String email = jsonObject.getString("email");
            String usertype = jsonObject.getString("user_type");

            SessionManager sessionManager = new SessionManager(context);
            sessionManager.updateSession(firstName, lastName, dob, email, usertype);
        } catch (Exception e) {

        }
    }


    public int getDistrictCount(String stateKey) {
        Query listDistQuery = Query.select().from(Boundary.TABLE).where(Boundary.STATE_KEY.eqCaseInsensitive(stateKey).and(Boundary.PARENT_ID.eq(1)));
        SquidCursor<Boundary> distCursor = db.query(Boundary.class, listDistQuery);

        if (distCursor != null) {
            return distCursor.getCount();
        }
        return 0;
    }


    public void getMySummary(final long id, final String statekey, final String fromD, final String endD, String token, final StateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //  Log.d("test",id+":"+fromD+":"+endD+":"+token);
        apiInterface.getMySummary(id, fromD, endD, token, statekey).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {

                    try {
                        parseMySummaryData(response.body().string(), id, statekey, stateInterface, fromD, endD);
                    } catch (Exception e) {
                        e.printStackTrace();
                        stateInterface.failed(e.getMessage());
                    }
                } else {

                    stateInterface.failed(context.getResources().getString(R.string.summaryLoadingFailed));

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });
    }

    private void parseMySummaryData(String s, long id, String stateKey, StateInterface stateInterface, String fromD, String endD) {
        String surveyName = "";
        Query mySurveyQuery = Query.select().from(Survey.TABLE)
                .where(Survey.QUESTION_GROUP_ID.eq(id));

        SquidCursor<Survey> mySurveyCursor = db.query(Survey.class, mySurveyQuery);
        if (mySurveyCursor.moveToNext())
            surveyName = new Survey(mySurveyCursor).getName();

        try {
            JSONObject jsonObject = new JSONObject(s);

            // String childrenImpacted = !jsonSummary.getString("children_impacted").equalsIgnoreCase("null") ? jsonSummary.getString("children_impacted") : "0";
            //  String total_schools = !jsonSummary.getString("total_schools").equalsIgnoreCase("null") ? jsonSummary.getString("total_schools") : "0";
            // String total_schools =(jsonSummary.getString("total_schools")!=null)?jsonSummary.getString("total_schools"):0;
            String num_assessments = !jsonObject.getString("assessments").equalsIgnoreCase("null") ? jsonObject.getString("assessments") : "0";
            String schools_impacted = !jsonObject.getString("schools_covered").equalsIgnoreCase("null") ? jsonObject.getString("schools_covered") : "0";

            Query mySummaryQuery = Query.select().from(MySummary.TABLE)
                    .where(MySummary.SURVEYID.eq(id).and(MySummary.STATE_KEY.eq(stateKey)));

            //  Toast.makeText(context,jsonObject.getString("assessments"),Toast.LENGTH_SHORT).show();
            SquidCursor<MySummary> summaryCursor = db.query(MySummary.class, mySummaryQuery);
            //  Toast.makeText((ReportsActivity) context, summaryCursor.getCount() + "count", Toast.LENGTH_SHORT).show();
            MySummary mySummary = new MySummary();
            mySummary.setSurveyid(id);
            mySummary.setSurveyname(surveyName);
            mySummary.setStateKey(stateKey);
            mySummary.setFromdate(milliseconds(fromD));
            mySummary.setEnddate((milliseconds(endD) - oneDay));
            mySummary.setSurveysynced(Long.parseLong(num_assessments));
            mySummary.setSchoolsurveyed(Long.parseLong(schools_impacted));
            mySummary.setPendingsync(0l);

            if (summaryCursor != null && summaryCursor.getCount() > 0) {
                Update update = Update.table(MySummary.TABLE).where(MySummary.STATE_KEY.eq(stateKey).and(MySummary.SURVEYID.eq(id)));
                Update summryUpdate = update.fromTemplate(mySummary);
                db.update(summryUpdate);
                stateInterface.success("success");


//update
            } else {
                db.insertNew(mySummary);
                stateInterface.success("success");
                //insert

            }


        } catch (JSONException e) {
            e.printStackTrace();
            stateInterface.success(context.getResources().getString(R.string.summaryLoadingFailed));
        }

    }


    public void varifyOTPAfterSignup(String mobile, String otp, String stateKey, final StateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.otpSignUp(mobile, otp, stateKey).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String message = context.getResources().getString(R.string.invalidOTP);
                if (response.isSuccessful()) {
                    stateInterface.success("success");
                } else if (response.code() == 404) {
                    //invalid otp
                    Gson gson = new Gson();
                    InvalidOTp invalidOTp = gson.fromJson(response.errorBody().charStream(), InvalidOTp.class);
                    if (invalidOTp != null) {
                        message = invalidOTp.getDetail();
                    }
                    stateInterface.failed(message);


                } else {
                    stateInterface.failed(context.getResources().getString(R.string.oops) + "");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t));
            }
        });


    }


    public void forgotPasswordGenerateOtp(String mobilenumber, String stateKey, final StateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.generateOtpForForgotPassword(mobilenumber, stateKey).enqueue(new Callback<ForgotPassswordOtpPojo>() {
            @Override
            public void onResponse(Call<ForgotPassswordOtpPojo> call, Response<ForgotPassswordOtpPojo> response) {
                if (response.isSuccessful()) {
                    stateInterface.success(response.body().getSuccess());
                } else {
                    if (response.code() == 200) {
                        stateInterface.success("Sent OTP");
                    } else if (response.code() == 404) {
                        //user not registered in ilp
                        Gson gson = new Gson();
                        ForgotOTPError forgotpassword = gson.fromJson(response.errorBody().charStream(), ForgotOTPError.class);
                        if (forgotpassword != null && forgotpassword.getDetail() != null) {
                            stateInterface.failed(forgotpassword.getDetail());
                        } else {
                            stateInterface.failed(context.getResources().getString(R.string.oops));
                        }

                    } else {
                        stateInterface.failed(context.getResources().getString(R.string.oops));
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgotPassswordOtpPojo> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t));

            }
        });

    }


    public void forgotPasswordResetWithOTP(String mobile, String otp, String newPassword, String stateKey, final StateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.forgotPasswordResetWithOTP(mobile, otp, newPassword, stateKey).enqueue(new Callback<ResetPasswordPojo>() {
            @Override
            public void onResponse(Call<ResetPasswordPojo> call, Response<ResetPasswordPojo> response) {

                if (response.isSuccessful() && response.code() == 200) {
                    stateInterface.success(response.body().getSuccess());

                } else if (response.code() == 404) {
                    Gson gson = new Gson();
                    ForgotOTPError forgotpassword = gson.fromJson(response.errorBody().charStream(), ForgotOTPError.class);
                    if (forgotpassword != null && forgotpassword.getDetail() != null) {
                        stateInterface.failed(forgotpassword.getDetail());
                    } else {
                        stateInterface.failed(context.getResources().getString(R.string.oops));
                    }

                } else {
                    stateInterface.failed(context.getResources().getString(R.string.oops));
                }


            }

            @Override
            public void onFailure(Call<ResetPasswordPojo> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t));

            }
        });

    }


    public long milliseconds(String date) {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = sdf.parse(date.trim());
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public void getReoportData(final long boundaryId, final long questionGroup, final String stateKey, final String level, final String sdate, String eDate, String token, final StateInterface stateInterface) {


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.fetchReportData(questionGroup, boundaryId, sdate, eDate, stateKey, token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.code() == 200) {

                    parseReportData(response.body(), questionGroup, level, boundaryId, stateKey, stateInterface);
                }
                else if( response.code() == 400)
                {
                    stateInterface.failed(context.getResources().getString(R.string.reportsnotavailableforthissurveys));
                }
                else {
                    stateInterface.failed(context.getResources().getString(R.string.reportsLoadingFailed));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }


    public void getReoportDataSchool(final long school, final long questionGroup, final String stateKey, final String level, final String sdate, String endDate, String token, final StateInterface stateInterface) {


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.fetchReportDataSchool(questionGroup, school, sdate, endDate, stateKey, token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.code() == 200) {

                    parseReportData(response.body(), questionGroup, level, school, stateKey, stateInterface);
                } else {
                    stateInterface.failed(context.getResources().getString(R.string.reportsLoadingFailed));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }

    private void parseReportData(ResponseBody body, long group, String level, long boundaryId, String stateKey, StateInterface stateInterface) {

        try {
            String data = body.string();
            JSONObject jsonObject = new JSONObject(data);
            JSONObject jsonSurvey = jsonObject.getJSONObject("surveys");


            JSONObject jsonSummary = jsonObject.getJSONObject("summary");

            long totalschools = jsonSummary.optLong("total_schools");
            long schoolImapcted = jsonSummary.optLong("schools_impacted");
            long num_assessments = jsonSummary.optInt("num_assessments");


            Summmary summary = new Summmary();
            summary.setBid(boundaryId);
            summary.setHierarchy(level);
            summary.setGroupid(group);
            summary.setStateKey(stateKey);
            summary.setTotalResponse(num_assessments);
            summary.setTotalSchool(totalschools);
            summary.setTotalSchoolWithResponse(schoolImapcted);


            if (jsonSummary.length() != 0) {
                Query reportSummary = Query.select().from(Summmary.TABLE)
                        .where(Summmary.BID.eqCaseInsensitive(boundaryId + "").
                                and(Summmary.HIERARCHY.eqCaseInsensitive(level)
                                        .and(Summmary.GROUPID.eqCaseInsensitive(group + ""))
                                        .and(Summmary.STATE_KEY.eqCaseInsensitive(stateKey))
                                ));
                SquidCursor<Summmary> reportSummaryCursor = db.query(Summmary.class, reportSummary);


                if (reportSummaryCursor != null && reportSummaryCursor.getCount() > 0) {
                    //update
                    Update update = Update.table(Summmary.TABLE).where(Summmary.BID.eq(boundaryId + "")
                            .and(Summmary.HIERARCHY.eqCaseInsensitive(level))
                            .and(Summmary.GROUPID.eqCaseInsensitive(group + ""))
                            .and(Summmary.STATE_KEY.eqCaseInsensitive(stateKey)));
                    Update summryUpdate = update.fromTemplate(summary);
                    db.update(summryUpdate);

                } else {
                    //insert
                    db.insertNew(summary);

                }
            }


            if (jsonSurvey.length() != 0) {
                JSONArray surveyIdArray = jsonSurvey.names();


                for (int i = 0; i < surveyIdArray.length(); i++) {


                    String Surveyname = surveyIdArray.getString(i);
                    JSONObject jsonObjectForQue = jsonSurvey.getJSONObject(Surveyname);
                    JSONObject jsonQuestionGroup = jsonObjectForQue.getJSONObject("questiongroups");
                    JSONObject jsonQuestion = jsonQuestionGroup.getJSONObject(jsonQuestionGroup.names().getString(0));
                    JSONObject jsongetQuestion = jsonQuestion.getJSONObject("questions");
                    JSONArray jsonQuestionName = jsongetQuestion.names();


                    for (int k = 0; k < jsonQuestionName.length(); k++) {
                        String question = jsonQuestionName.getString(k);

                        JSONObject actualData = jsongetQuestion.getJSONObject(question);

                        long yes = actualData.optLong("Yes");
                        long no = actualData.optLong("No");
                        int questionId = actualData.optInt("id");
                        long dnt = actualData.optLong("Don't Know");
                        //         Toast.makeText(context, question, Toast.LENGTH_SHORT).show();


                        Query summryQuery = Query.select().from(SummaryInfo.TABLE)
                                .where(SummaryInfo.BID.eq(boundaryId + "").and(SummaryInfo.GROUPID.eqCaseInsensitive(group + "")
                                        .and(SummaryInfo.QID.eqCaseInsensitive(questionId + "")).and(SummaryInfo.STATE_KEY.eqCaseInsensitive(stateKey))));
                        SquidCursor<SummaryInfo> summaryCursor = db.query(SummaryInfo.class, summryQuery);
                        //  Toast.makeText((ReportsActivity) context, summaryCursor.getCount() + "count", Toast.LENGTH_SHORT).show();
                        if (summaryCursor != null && summaryCursor.getCount() > 0) {

                            Update summryUpdate = Update.table(SummaryInfo.TABLE)
                                    .set(SummaryInfo.YES, actualData.optLong("Yes"))
                                    .set(SummaryInfo.NO, actualData.optLong("No"))
                                    .set(SummaryInfo.DONTKNOW, actualData.optLong("Don't Know"))
                                    .set(SummaryInfo.STATE_KEY, stateKey)
                                /*    .set(SummaryInfo.TOTAL_SCHOOL, totalschools)
                                    .set(SummaryInfo.TOTAL_RESPONSE, num_assessments)
                                    .set(SummaryInfo.TOTAL_SCHOOL_WITH_RESPONSE, schoolImapcted)*/

                                    .where(SummaryInfo.QID.eq(questionId).
                                            and(SummaryInfo.BID.eqCaseInsensitive(boundaryId + ""))
                                            .and(SummaryInfo.HIERARCHY.eqCaseInsensitive(level))
                                            .and(SummaryInfo.GROUPID.eqCaseInsensitive(group + ""))
                                            .and(SummaryInfo.STATE_KEY.eqCaseInsensitive(stateKey)));

                            int updated = db.update(summryUpdate);
                            //already data exist update
                        } else {
                            //new record
                            SummaryInfo summaryInfo = new SummaryInfo();
                            summaryInfo.setYes(yes);
                            summaryInfo.setNo(no);
                            summaryInfo.setStateKey(stateKey);
                            summaryInfo.setQid(Long.parseLong(questionId + ""));
                            summaryInfo.setDontknow(dnt);
                            summaryInfo.setGroupid(Long.parseLong(group + ""));
                            summaryInfo.setHierarchy(level);
                            summaryInfo.setBid(boundaryId);
                        /*    summaryInfo.setTotalSchool(totalschools);
                            summaryInfo.setTotalResponse(num_assessments);
                            summaryInfo.setTotalSchoolWithResponse(schoolImapcted);*/

                            boolean b = db.insertNew(summaryInfo);

                        }


                    }


                }

                //    Toast.makeText(context,name,Toast.LENGTH_SHORT).show();

                stateInterface.success("success");
            } else {
                stateInterface.success("No detail found");
            }


        } catch (Exception e) {
            //  e.printStackTrace();
            stateInterface.failed(e.getMessage());
            //   Toast.makeText(context, "failr" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }


    public void getCommunitySurveyQuestions(String url, final long groupid, final int count, final int size, String token, final StateInterface stateInterface) {

 /*       try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Response<QuestionsPojos> response = apiInterface.fetchCummunitySurveyQuestions(url).execute();
            if (response.isSuccessful() && response.code() == 200) {

                parseStoringQuestion(response.body(), groupid);

            } else {
                stateInterface.failed(context.getResources().getString(R.string.questionLoadingFailed));
            }

        } catch (Exception e) {
            stateInterface.failed(getFailureMessage(e));
        }*/


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.fetchCummunitySurveyQuestions(url, token).enqueue(new Callback<QuestionsPojos>() {
            @Override
            public void onResponse(Call<QuestionsPojos> call, Response<QuestionsPojos> response) {

                if (response.isSuccessful() && response.code() == 200) {
                    if (count == (size - 1)) {
                        stateInterface.success(context.getResources().getString(R.string.downloadedSurveysandQuestions));
                    }
                    //stateInterface.success(context.getResources().getString(R.string.downloadedSurveysandQuestions));
                    parseStoringQuestion(response.body(), groupid);

                } else {
                    stateInterface.failed(context.getResources().getString(R.string.questionLoadingFailed));
                }

            }

            @Override
            public void onFailure(Call<QuestionsPojos> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }

    private void parseStoringQuestion(QuestionsPojos body, long groupid) {

        if (body.getResults() != null && body.getResults().size() > 0) {
            //db.deleteAll(QuestionGroupQuestion.class).
            db.deleteWhere(QuestionGroupQuestion.class, QuestionGroupQuestion.QUESTIONGROUP_ID.eq(groupid));
            for (int i = 0; i < body.getResults().size(); i++) {


                String engQue = body.getResults().get(i).getQuestionText();
                //     String otherLang = body.getResults().get(i).getDisplayText();
                String key = body.getResults().get(i).getKey();
               // Log.d("mmm",key+":"+groupid);
                String questiontype = body.getResults().get(i).getQuestionType();

                int sequence = body.getResults().get(i).getSequence()!=null?body.getResults().get(i).getSequence():0;
                long questionId = body.getResults().get(i).getId();
                String otherLang = null;
                if (body.getResults().get(i).getLangName() != null) {
                    otherLang = body.getResults().get(i).getLangName();

                } else {
                    otherLang = body.getResults().get(i).getQuestionText();

                }
                String options=null;
                if(body.getResults().get(i).getOptions()!=null&&body.getResults().get(i).getOptions().size()>0) {
                     //options = body.getResults().get(i).getOptions().toString();
                     options="";
                     for(int m=0;m<body.getResults().get(i).getOptions().size();m++)
                     {
                         if(!options.equalsIgnoreCase("")){
                             options=options+","+body.getResults().get(i).getOptions().get(m);
                         }else {
                             options=options+body.getResults().get(i).getOptions().get(m);
                         }


                     }
                }


               String lang_options=null;
                if(body.getResults().get(i).getLangOptions()!=null&&body.getResults().get(i).getLangOptions().size()>0) {
                   // lang_options = body.getResults().get(i).getLangOptions().toString();
                    lang_options="";
                    for(int m=0;m<body.getResults().get(i).getLangOptions().size();m++)
                    {
                        if(!lang_options.equalsIgnoreCase("")){
                            lang_options=lang_options+","+body.getResults().get(i).getLangOptions().get(m);
                        }else {
                            lang_options=lang_options+body.getResults().get(i).getLangOptions().get(m);
                        }


                    }
                }



                Question question = new Question()
                        .setId(questionId)
                        .setText(engQue)
                        .setTextKn(otherLang)
                        .setDisplayText(engQue)
                        .setKey(key)
                        .setOptions(options)
                        .setLangOptions(lang_options)
                        .setType(questiontype)
                        .setSchoolType("primaryschool");
                //  Log.d("test","qeustion");
                db.insertforQuestionGroup(question);

                QuestionGroupQuestion questionGroupQuestion = new QuestionGroupQuestion()
                        .setId(questionId)
                        .setQuestionId(questionId)
                        .setQuestiongroupId(groupid)
                        .setSequence(sequence);
                //Log.d("test","qeustiongroup");
                db.insertforQuestionGroup(questionGroupQuestion);


            }

        }
    }


    public void SyncData(String data, String header, final StateInterface stateInterface) {


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.syncDataforServerWithRetro(requestBody, header).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    JSONObject respJson = new JSONObject();
                    String data;
                    try {

                        respJson = new JSONObject(response.body().string());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    parseSyncData(respJson);
                    stateInterface.success("success");

//Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();
                } else {

                    stateInterface.failed(context.getResources().getString(R.string.syncFailed));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t));

            }
        });

    }


    public void parseSyncData(JSONObject jsonObject) {
        UploadTask uploadTask = new UploadTask();
        uploadTask.processUploadResponse(jsonObject, db);


    }


}