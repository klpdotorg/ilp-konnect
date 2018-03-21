package in.org.klp.ilpkonnect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import in.org.klp.ilpkonnect.adapters.QuestionAdapter;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;

import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.AppStatus;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.RolesUtils;
import in.org.klp.ilpkonnect.utils.SessionManager;

public class QuestionFragment extends Fragment implements MultiSelectSpinner.OnMultipleItemsSelectedListener {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

   /* https://android--code.blogspot.in/2015/08/android-spinner-hint.html*/
    private QuestionAdapter mQuestionsAdapter;
    private Long surveyId;
    private String surveyName;
    private Long schoolId;
    private Long questionGroupId;
    private String mSelectedUserType;
    private LinearLayout linLayout, respLin, lincomment;
    private KontactDatabase db;
    SessionManager session;
    ImageView imgBtnImage;
    private LinkedHashMap<String, String> userType;
    //  private int GKA_FLAG = 2;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;
    TextView tvImageName;
    EditText edtComment;
    ImageView imgPreview;
    File GlobalImagePath = null;
    ByteArrayOutputStream GLOBALbytes = null;
    QuestionActivity questionActivity;
    boolean isImageRequired, isCommentRequired, isRespondentlistRequired;
    String gradeType;
    LinearLayout linlayGradeSelection;
    MultiSelectSpinner spnGrade;
    Spinner spnGradesingle;
    boolean isgradeRequired;
    TextView tvlableGrade;
    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = ((KLPApplication) getActivity().getApplicationContext()).getDb();

        // check if user is logged in
        session = new SessionManager(getActivity());
        session.checkLogin();
        String stateKey = session.getStateSelection();
        Intent intent = getActivity().getIntent();
        surveyId = intent.getLongExtra("surveyId", 0);
        questionGroupId = intent.getLongExtra("ILPQuestionGroupId", 0);
        surveyName = intent.getStringExtra("surveyName");
        schoolId = intent.getLongExtra("schoolId", 0);
        isImageRequired = intent.getBooleanExtra("imageRequired", false);


        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
//Toast.makeText(getActivity(),lat+":"+lng,Toast.LENGTH_SHORT).show();
        School school = db.fetch(School.class, schoolId);
        Survey survey = db.fetch(Survey.class, surveyId);
        isRespondentlistRequired = survey.isRespondentRequired();
//Toast.makeText(getActivity(),isRespondentlistRequired+"",Toast.LENGTH_SHORT).show();
        isCommentRequired = survey.isCommentRequired();
        gradeType = survey.getGradeRequired();
        if(gradeType==null)
        {
            isgradeRequired=false;
        }else {
            isgradeRequired=true;
        }

        TextView textViewSchool = rootView.findViewById(R.id.textViewSchool);
        TextView textViewSchoolId = rootView.findViewById(R.id.textViewSchoolId);
        tvlableGrade=rootView.findViewById(R.id.tvlableGrade);
        tvlableGrade.setVisibility(View.GONE);
        textViewSchool.setText(school.getName());
        if (school.getDise() != null && !school.getDise().trim().equalsIgnoreCase("") && !school.getDise().trim().equalsIgnoreCase("null")) {
            textViewSchoolId.setText("DISE Code: " + String.valueOf(school.getDise()));
        } else {
            textViewSchoolId.setText("DISE Code: NA");
        }
        linLayout = rootView.findViewById(R.id.linLayout);
        spnGrade = rootView.findViewById(R.id.spnGrade);
        spnGradesingle = rootView.findViewById(R.id.spnGradesingle);

        requiredMultilevelgrade(gradeType);





        spnGrade.setListener(this);
        lincomment = rootView.findViewById(R.id.lincomment);
         linlayGradeSelection = rootView.findViewById(R.id.linlayGradeSelection);
        edtComment = rootView.findViewById(R.id.edtComment);
        edtComment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtComment.setLines(1);
        respLin = rootView.findViewById(R.id.respLin);
        tvImageName = rootView.findViewById(R.id.tvImageName);
        questionActivity = (QuestionActivity) getActivity();
        imgPreview = rootView.findViewById(R.id.imgPreview);
        userType = new LinkedHashMap<String, String>();

        respLin.setVisibility(View.GONE);
        checkVisiblity(stateKey);


        if (surveyId == 0) {
            Intent intentMain = new Intent(getActivity(), SurveyTypeActivity.class);
            startActivity(intentMain);
        }

        imgBtnImage = rootView.findViewById(R.id.imgBtnImage);
        imgBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();

            }
        });


        final Spinner spinnerUserType = rootView.findViewById(R.id.spinnerUserType);
        List<String> userTypeNames = new ArrayList<>();
        userTypeNames.addAll(userType.keySet());




        final ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(
                getActivity(),R.layout.question_spinner,userTypeNames){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };







        //    ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.question_spinner, userTypeNames);
        spinnerUserType.setAdapter(userTypeAdapter);
        // spinnerUserType.setSelection(8);//PR
        mSelectedUserType = session.getUserType();

        // this to remove all the invalid answers created by a bug in last release
       // db.deleteWhere(Answer.class, Answer.TEXT.notIn("Yes", "No", "Don't Know"));


        SquidCursor<QuestionGroupQuestion> qgqCursor = null;
        mQuestionsAdapter = new QuestionAdapter(new ArrayList<Question>(), getActivity());

  /*      Query listQGquery;
        // select * from questiongroup where survey_id=surveyId limit 1
        if (Constants.surveyType == 1) {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.ID.eq(surveyId)).limit(1);
        } else {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.ID.eq(surveyId)).limit(1);
        }
        Log.d("shri", "SURVEY ID:" + surveyId);
        qgCursor = db.query(QuestionGroup.class, listQGquery);
*/
        try {
            //   while (qgCursor.moveToNext()) {
            //   questionGroupId = qgCursor.get(QuestionGroup.ID);
            //  Log.d("shri", "QG ID: " + questionGroupId.toString());
            // select * from questiongroupquestion
            // where questiongroup_id=questionGroupId
            // order by sequence
            Query listQGQquery = Query.select().from(QuestionGroupQuestion.TABLE)
                    .where(QuestionGroupQuestion.QUESTIONGROUP_ID.eq(questionGroupId))
                    .orderBy(QuestionGroupQuestion.SEQUENCE.asc());
            qgqCursor = db.query(QuestionGroupQuestion.class, listQGQquery);

            while (qgqCursor.moveToNext()) {
                Long qID = qgqCursor.get(QuestionGroupQuestion.QUESTION_ID);
                // select * from question where id=qID
                Question question = db.fetch(Question.class, qID);
                mQuestionsAdapter.add(question);
                //}
            }
        } finally {
            /*if (qgCursor != null) {
                qgCursor.close();
            }*/
            if (qgqCursor != null) {
                qgqCursor.close();
            }
        }
        ListView listViewQuestions = rootView.findViewById(R.id.listViewQuestions);
        listViewQuestions.setItemsCanFocus(true);
        listViewQuestions.setAdapter(mQuestionsAdapter);


        Button btnSubmit = rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> user = session.getUserDetails();
                final Long currentTS = System.currentTimeMillis();
                //  Toast.makeText(getActivity(),currentTS+"(((((",Toast.LENGTH_SHORT).show();

                if (isRespondentlistRequired) {
                    mSelectedUserType = userType.get(spinnerUserType.getSelectedItem().toString());

                } else {
                    mSelectedUserType = session.getUserType().toUpperCase();

                }


                // Toast.makeText(getActivity(),mSelectedUserType+"",Toast.LENGTH_SHORT).show();
                final HashMap<Question, String> answers = mQuestionsAdapter.getAnswers();
                AlertDialog noAnswerDialog = new AlertDialog.Builder(getContext()).create();

                noAnswerDialog.setCancelable(false);
                boolean flag = true;
                //------------------------------------------


              //  Log.d("tag",isImageRequired+":"+isgradeRequired+":"+isCommentRequired+":"+isRespondentlistRequired);
                String message="";
                if (isImageRequired || isgradeRequired || isCommentRequired||isRespondentlistRequired||mQuestionsAdapter.getQuestionSize() == 0||answers.size() != mQuestionsAdapter.getQuestionSize()) {

                    if (mQuestionsAdapter.getQuestionSize() == 0||answers.size() != mQuestionsAdapter.getQuestionSize())
                    {
                        if(mQuestionsAdapter.getQuestionSize() == 0)
                        {
                            message = getString(R.string.surveyQuestionNotFound);
                            flag=false;

                        }else {
                            message = getString(R.string.survey_empty_response_body);
                            flag=false;
                        }

                     }
                     if(isRespondentlistRequired)
                     {
                         if (spinnerUserType.getSelectedItemPosition() == 0) {
                             if (!message.trim().equalsIgnoreCase("")) {
                                 message = message + "\n";
                             }
                             message = message + "* " + getResources().getString(R.string.pleaseSelectrespondanttypequestion);
                             flag=false;
                         }
                     }else {
                      //  Toast.makeText(getActivity(),isRespondentlistRequired+"",Toast.LENGTH_SHORT).show();
                     }
                     if(isImageRequired)
                     {
                         if(getFilePath() == null || getBitMapFile() == null)
                         {
                             if (!message.trim().equalsIgnoreCase("")) {
                                 message = message + "\n";
                             }
                             message = message + getResources().getString(R.string.pleaseuploadimage);
                             flag=false;
                         }
                     }

                     if(isCommentRequired)
                    {
                        if(TextUtils.isEmpty(edtComment.getText().toString().trim()))
                        {
                            if (!message.trim().equalsIgnoreCase("")) {
                                message = message + "\n";
                            }
                            message = message +"* "+ getResources().getString(R.string.pleaseENterComment);
                            flag=false;
                        }
                    }
                    if(isgradeRequired)
                    {
                        if(gradeType.equalsIgnoreCase("grade")) {
                            if (spnGradesingle.getSelectedItemPosition() == 0) {
                                if (!message.trim().equalsIgnoreCase("")) {
                                    message = message + "\n";
                                }
                                message = message + "* "+getResources().getString(R.string.PleaseSelectGrade);
                                flag = false;
                            }
                        }if(gradeType.equalsIgnoreCase("multigrade")) {

                                if (spnGrade.getSelectedItem().toString().equalsIgnoreCase("") ){
                                    if (!message.trim().equalsIgnoreCase("")) {
                                        message = message + "\n";
                                    }
                                    message = message + "* "+getResources().getString(R.string.PleaseSelectGrade);
                                    flag = false;
                                }

                        }
                    }




                }


                if(flag){

                    final Story story = new Story();
                    story.setSchoolId(schoolId);
                    story.setUserId(Long.parseLong(user.get(SessionManager.KEY_ID)));
                    story.setGroupId(questionGroupId);
                    story.setStateKey(session.getStateSelection());
                    story.setRespondentType(mSelectedUserType);
                    if(isCommentRequired)
                    {
                        story.setComments(edtComment.getText().toString().trim());
                    }
                    if(isgradeRequired)
                    {
                        if(gradeType.equalsIgnoreCase("grade"))
                        {
                        story.setGroupValue(    spnGradesingle.getSelectedItem().toString().trim());
                        }
                        if(gradeType.equalsIgnoreCase("multigrade"))
                        {
                            story.setGroupValue(     spnGrade.getSelectedItem().toString().trim());
                        }
                    }

                    //    Toast.makeText(getActivity(),currentTS+"",Toast.LENGTH_LONG).show();
                    if (isImageRequired == true) {
                        story.setImage(StoreImageToFile());

                    }
                    story.setCreatedAt(currentTS);
                    storetoDB(story, answers, currentTS);




                }else {

                    noAnswerDialog.setMessage(message);
                    noAnswerDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    noAnswerDialog.show();
                }


                //----------------------------------------

/*                if (isImageRequired == false) {
                    if (answers.size() != mQuestionsAdapter.getQuestionSize() || spinnerUserType.getSelectedItemPosition() == 0) {
                        flag = false;
                        String msg = "";
                        if (answers.size() != mQuestionsAdapter.getQuestionSize()) {
                            msg = getString(R.string.survey_empty_response_body);
                        }
                        if (spinnerUserType.getSelectedItemPosition() == 0) {
                            if (!msg.trim().equalsIgnoreCase("")) {
                                msg = msg + "\n";
                            }
                            msg = msg + "* " + getResources().getString(R.string.pleaseSelectrespondanttypequestion);
                        }
                        noAnswerDialog.setMessage(msg);
                    } else {
                        flag = true;
                    }

                    //community SurveyType
                } else {
                    //GK Monitoring
                    if (answers.size() != mQuestionsAdapter.getQuestionSize() || getFilePath() == null || getBitMapFile() == null) {
                        flag = false;
                        String msg = "";
                        if (answers.size() != mQuestionsAdapter.getQuestionSize()) {
                            msg = getString(R.string.survey_empty_response_body);
                        }
                        if (getFilePath() == null || getBitMapFile() == null) {
                            if (!msg.trim().equalsIgnoreCase("")) {
                                msg = msg + "\n";
                            }
                            msg = msg + getResources().getString(R.string.pleaseuploadimage);
                        }
                        noAnswerDialog.setMessage(msg);

                    } else {

                        flag = true;

                    }

                }


                if (flag == false) {

                    noAnswerDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    noAnswerDialog.show();
                } else {

                    if (mQuestionsAdapter.getQuestionSize() == 0) {

                        DailogUtill.showDialog(getResources().getString(R.string.surveyQuestionNotFound), getFragmentManager(), getActivity());

                    } else {
                        final Story story = new Story();
                        story.setSchoolId(schoolId);
                        story.setUserId(Long.parseLong(user.get(SessionManager.KEY_ID)));
                        story.setGroupId(questionGroupId);
                        story.setStateKey(session.getStateSelection());
                        story.setRespondentType(mSelectedUserType);
                        story.setCreatedAt(currentTS);
                        //    Toast.makeText(getActivity(),currentTS+"",Toast.LENGTH_LONG).show();
                        if (isImageRequired == true) {
                            story.setImage(StoreImageToFile());


                            storetoDB(story, answers, currentTS);
                        } else {

                            storetoDB(story, answers, currentTS);
                        }

                    }
                }*/
            }
        });


        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(getActivity(),getFilePath().getAbsolutePath(),Toast.LENGTH_SHORT).show();
                showImage(BitmapFactory.decodeByteArray(getBitMapFile().toByteArray(), 0, getBitMapFile().toByteArray().length));


            }
        });


        return rootView;
    }

    private void requiredMultilevelgrade(String level) {

        List<String> gradeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.grade_array)));
       gradeList.add(0,getResources().getString(R.string.grade));
        //    ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.textviewmultispinner, getResources().getStringArray(R.array.grade_array));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(),R.layout.textviewmultispinner,gradeList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spnGradesingle.setAdapter(spinnerArrayAdapter);
        spnGrade.setItems(getResources().getStringArray(R.array.grade_array));
        spnGrade.setSelection(new int[]{-1});

      /*  if(level==null)
        {
            spnGrade.setVisibility(View.GONE);
            spnGradesingle.setVisibility(View.GONE);
        }else {
            if (level.equalsIgnoreCase("grade")) {
                //
                spnGrade.setVisibility(View.GONE);
            } else {
                spnGradesingle.setVisibility(View.GONE);
            }
        }*/







    }

    private void checkVisiblity(String stateKey) {


        if (isImageRequired == false) {
            linLayout.setVisibility(View.GONE);

        } else {
            linLayout.setVisibility(View.VISIBLE);

        }
        if (isCommentRequired) {
            lincomment.setVisibility(View.VISIBLE);
        } else {
            lincomment.setVisibility(View.GONE);
        }

       if (isgradeRequired) {
           spnGradesingle.setVisibility(View.GONE);
           spnGrade.setVisibility(View.GONE);
            if(gradeType.equalsIgnoreCase("grade")) {
                spnGradesingle.setVisibility(View.VISIBLE);
                tvlableGrade.setVisibility(View.VISIBLE);
            }
            if(gradeType.equalsIgnoreCase("multigrade"))
            {
                spnGrade.setVisibility(View.VISIBLE);
            }
        } else {
            linlayGradeSelection.setVisibility(View.GONE);
        }

        if(isRespondentlistRequired)
        {
            respLin.setVisibility(View.VISIBLE);

            userType = new LinkedHashMap<String, String>();
            userType.put(getResources().getString(R.string.pleaseSelectrespondanttype), "No");
            userType.putAll(RolesUtils.getUserRoles(getActivity(), db, stateKey));
        }else {
            respLin.setVisibility(View.GONE);

            userType = new LinkedHashMap<String, String>();
            userType.put(getResources().getString(R.string.pleaseSelectrespondanttype), "No");
            userType.putAll(RolesUtils.getUserRoles(getActivity(), db, stateKey));
        }


    }

    private void storetoDB(Story story, HashMap<Question, String> answers, Long currentTS) {
        db.persist(story);
        //    Log.d(this.toString(), "Created story: " + String.valueOf(story.getId()));
        //  Log.d(this.toString(), answers.entrySet().toString());

        for (Map.Entry<Question, String> answer : answers.entrySet()) {
            Question q = answer.getKey();
            String a = answer.getValue();

            Answer new_answer = new Answer();
            new_answer.setStoryId(story.getId());
            new_answer.setQuestionId(q.getId());
            new_answer.setText(a);

            new_answer.setCreatedAt(currentTS);
            db.persist(new_answer);
            //  StoreImageToFile();
            //  Log.d(this.toString(), "Created answer: " + String.valueOf(new_answer.getId()) + " : " + new_answer.getText());
        }
        if (AppStatus.isConnected(getActivity())) {
            Intent intent1 = new Intent(getActivity(), SyncIntentService.class);
            getActivity().startService(intent1);
        }
        // Ask if the user wants to record more responses
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        String buttonName = getResources().getString(R.string.response_positive);
        if (isRespondentlistRequired == true) {
            builder.setMessage(getString(R.string.allSurvey)).setTitle(getResources().getString(R.string.responseSaved));
            buttonName = getResources().getString(R.string.Ok);
        } else {
            buttonName = getResources().getString(R.string.response_positive);
            builder.setMessage(getString(R.string.prompt_new_rsponse)).setTitle(getResources().getString(R.string.responseSaved));
        }
        builder.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (isRespondentlistRequired == true) {
                    getActivity().finish();
                } else {
                    Intent intent = new Intent(getActivity(), QuestionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("surveyId", surveyId);
                    intent.putExtra("ILPQuestionGroupId", questionGroupId);
                    intent.putExtra("surveyName", surveyName);
                    intent.putExtra("schoolId", schoolId);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }
        });

        if (isRespondentlistRequired == false) {


            builder.setNegativeButton(getResources().getString(R.string.response_negative), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    AlertDialog.Builder builderMes = new AlertDialog.Builder(getActivity());
                    builderMes.setCancelable(false);
                    builderMes.setMessage(getString(R.string.allSurvey));
                    builderMes.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    });
                    builderMes.show();


                }
            });
        }
        // Create the AlertDialog object and return it
        builder.show();
    }


    private void selectImage() {
        final CharSequence[] items = {getResources().getString(R.string.takephoto), getResources().getString(R.string.choosefromlib),
                getResources().getString(R.string.cancel)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals(getResources().getString(R.string.takephoto))) {
                    boolean result = checkPermission(getActivity());
                    userChoosenTask = getResources().getString(R.string.takephoto);
                    if (result)
                        cameraIntent();

                } else if (items[item].equals(getResources().getString(R.string.choosefromlib))) {
                    boolean result = checkPermission(getActivity());
                    userChoosenTask = getResources().getString(R.string.choosefromlib);
                    if (result)
                        galleryIntent();

                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Toast.makeText(getActivity(),"true",Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case UtilityCamImage.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(getResources().getString(R.string.takephoto)))
                        cameraIntent();
                    else if (userChoosenTask.equals(getResources().getString(R.string.choosefromlib)))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        // intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);

            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data, null);

        }

    }


    Bitmap ShrinkBitmap(Bitmap file) {


        int nh = (int) (file.getHeight() * (512.0 / file.getWidth()));
        Bitmap resized = Bitmap.createScaledBitmap(file, 512, nh, true);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);
        byte[] array = blob.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        return BitmapFactory.decodeStream(bis);

    }


    private void onCaptureImageResult(Intent data, Bitmap bitmap) {
        Bitmap thumbnail = null;
        if (data == null) {
            //from gellry
            thumbnail = bitmap;
        } else {
            //from camera

            thumbnail = (Bitmap) data.getExtras().get("data");

        }

        thumbnail = ShrinkBitmap(thumbnail);
        Bitmap dest = Bitmap.createBitmap(thumbnail.getWidth(), thumbnail.getHeight(), Bitmap.Config.ARGB_8888);


        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(20);
        tPaint.setColor(Color.BLUE);
        tPaint.setStyle(Paint.Style.FILL);
        float height = tPaint.measureText("yY");
        cs.drawBitmap(thumbnail, 0, 0, tPaint);
        cs.drawText(getDate(System.currentTimeMillis()), 20f, height + 15f, tPaint);


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //   thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            // Toast.makeText(getActivity(),bytes.size()+"",Toast.LENGTH_SHORT).show();
            ContextWrapper cw = new ContextWrapper(getActivity());
            File directory = cw.getDir(Constants.GKA_IMAGE_STORAGE_PATH, Context.MODE_PRIVATE);
            // Create imageDir
            File mypath = new File(directory, System.currentTimeMillis() + ".jpg");
            // Log.d("ss",mypath.toString());
            // File mypath1 = new File(compressImage(mypath.getAbsolutePath()));

            setImagePath(mypath, bytes);
            //getBase64(bytes);

            //  Log.d("mm",getPath(data.getData()));
     /*  FileOutputStream fo;
        try {
            mypath.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

            tvImageName.setText(getFilePath().getName().toString());

            imgPreview.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            // Toast.makeText(getActivity(),"Please select appropriate image",Toast.LENGTH_SHORT).show();
            showSignupResultDialog(
                    getResources().getString(R.string.app_name),
                    getResources().getString(R.string.pleaseSelectAppropriateImage),
                    getResources().getString(R.string.Ok));


        /*File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
*/

        }
        // ivImage.setImageBitmap(thumbnail);
    }


    public void showImage(Bitmap uri) {
        Dialog builder = new Dialog(getActivity(), android.R.style.Theme_Light);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.LTGRAY));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageBitmap(uri);
        builder.addContentView(imageView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    public String getBase64(byte[] bytes) {

        //  Log.d("base64",bytes.toString()+"");
        String encodedImage = "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);


        // Log.d("base64", encodedImage);
        return encodedImage;
    }

    public void setImagePath(File path, ByteArrayOutputStream bytes) {
        GlobalImagePath = path;
        GLOBALbytes = bytes;
    }

    public File getFilePath() {
        return GlobalImagePath;
    }


    public ByteArrayOutputStream getBitMapFile() {
        return GLOBALbytes;
    }

    public String StoreImageToFile() {
        File path = getFilePath();
        ByteArrayOutputStream bytes = getBitMapFile();
        String Base64Data = "no";
        if (path == null || bytes == null) {
            Toast.makeText(getActivity(), "Please upload survey image", Toast.LENGTH_SHORT).show();
            return "";
        } else {
            //  Toast.makeText(getActivity(), path.getAbsolutePath()+"", Toast.LENGTH_SHORT).show();

            // FileOutputStream fo;

            try {
                //   path.createNewFile();
                // fo = new FileOutputStream(path);
                //fo.write(bytes.toByteArray());
                Base64Data = getBase64(bytes.toByteArray());
                //fo.close();
                //} catch (FileNotFoundException e) {
                //  e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //return path.getName().toString();
        return Base64Data;
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                onCaptureImageResult(null, bm);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Toast.makeText(getActivity(),data.getData().getPath().toString(),Toast.LENGTH_SHORT).show();
        // tvImageName.setText(data.getData().getPath().toString());

        //ivImage.setImageBitmap(bm);
    }

    private void showSignupResultDialog(String title, String message, String buttonText) {
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        resultDialog.show(getFragmentManager(), "Registration result");
    }


    public double getLocationDistance(double schoolLat, double schoolLon, double currentLoc, double currentLon) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(schoolLon);
        startPoint.setLongitude(schoolLat);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(currentLoc);
        endPoint.setLongitude(currentLon);

        double distance = startPoint.distanceTo(endPoint);

        //distance=distance(schoolLon,schoolLat,currentLoc,currentLon);
        return distance;
    }


    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }


    public double getRoundUp(double val) {

        return Double.parseDouble(new DecimalFormat("##.##").format(val));
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(context.getResources().getString(R.string.permission));
                    alertBuilder.setMessage(context.getString(R.string.externalstorage));
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            // ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void selectedIndices(List<Integer> indices) {

    }

    @Override
    public void selectedStrings(List<String> strings) {

    }
}
