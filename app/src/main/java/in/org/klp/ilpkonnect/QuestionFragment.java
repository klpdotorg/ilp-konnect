package in.org.klp.ilpkonnect;

import android.app.Activity;
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
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import in.org.klp.ilpkonnect.adapters.QuestionAdapter;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroup;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.dialogs.SignUpResultDialogFragment;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.SessionManager;

public class QuestionFragment extends Fragment {

    private QuestionAdapter mQuestionsAdapter;
    private Long surveyId;
    private String surveyName;
    private Long schoolId;
    private Long questionGroupId;
    private String mSelectedUserType;
    private LinearLayout linLayout;
    private KontactDatabase db;
    SessionManager session;
    ImageView imgBtnImage;
    private LinkedHashMap<String, String> userType;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;
    TextView tvImageName;
    double lat,lng;

    File GlobalImagePath = null;
    ByteArrayOutputStream GLOBALbytes = null;
    QuestionActivity questionActivity;

    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = ((KLPApplication) getActivity().getApplicationContext()).getDb();

        // check if user is logged in
        session = new SessionManager(getActivity());
        session.checkLogin();

        Intent intent = getActivity().getIntent();
        surveyId = intent.getLongExtra("surveyId", 0);
        surveyName = intent.getStringExtra("surveyName");
        schoolId = intent.getLongExtra("schoolId", 0);
        lat=intent.getDoubleExtra("lat",0d);
        lng=intent.getDoubleExtra("lng",0d);

        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
//Toast.makeText(getActivity(),lat+":"+lng,Toast.LENGTH_SHORT).show();
        School school = db.fetch(School.class, schoolId);
        TextView textViewSchool = (TextView) rootView.findViewById(R.id.textViewSchool);
        TextView textViewSchoolId = (TextView) rootView.findViewById(R.id.textViewSchoolId);
        textViewSchool.setText(school.getName());
        textViewSchoolId.setText("ILP ID: " + String.valueOf(school.getId()));
        linLayout = (LinearLayout) rootView.findViewById(R.id.linLayout);
        tvImageName = (TextView) rootView.findViewById(R.id.tvImageName);
        questionActivity = (QuestionActivity) getActivity();


        if (Constants.surveyType == 1) {
            linLayout.setVisibility(View.GONE);

        } else {
            linLayout.setVisibility(View.VISIBLE);
            //  questionActivity.();


        }

        if (surveyId == 0) {
            Intent intentMain = new Intent(getActivity(), SurveyTypeActivity.class);
            startActivity(intentMain);
        }

        imgBtnImage = (ImageView) rootView.findViewById(R.id.imgBtnImage);
        imgBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();

            }
        });


        // defining user types
        userType = new LinkedHashMap<String, String>();
        userType.put(getResources().getString(R.string.parents), "PR");
        userType.put(getResources().getString(R.string.teachers), "TR");
        userType.put(getResources().getString(R.string.educationVol), "VR");
        userType.put(getResources().getString(R.string.cbomember), "CM");
        userType.put(getResources().getString(R.string.headermaster), "HM");
        userType.put(getResources().getString(R.string.sdmcmember), "SM");
        userType.put(getResources().getString(R.string.localleaders), "LL");
        userType.put(getResources().getString(R.string.aksharastaff), "AS");
        userType.put(getResources().getString(R.string.educatedyouth), "EY");
        userType.put(getResources().getString(R.string.govtofficial), "GO");
        userType.put(getResources().getString(R.string.educationofficial), "EO");
        userType.put(getResources().getString(R.string.electedRepre), "ER");

        final Spinner spinnerUserType = (Spinner) rootView.findViewById(R.id.spinnerUserType);
        List<String> userTypeNames = new ArrayList<>();
        userTypeNames.addAll(userType.keySet());
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, userTypeNames);
        spinnerUserType.setAdapter(userTypeAdapter);
        mSelectedUserType = "PR";

        // this to remove all the invalid answers created by a bug in last release
        db.deleteWhere(Answer.class, Answer.TEXT.notIn("Yes", "No", "Don't Know"));

        SquidCursor<QuestionGroup> qgCursor = null;
        SquidCursor<QuestionGroupQuestion> qgqCursor = null;
        mQuestionsAdapter = new QuestionAdapter(new ArrayList<Question>(), getActivity());

        Query listQGquery;
        // select * from questiongroup where survey_id=surveyId limit 1
        if (Constants.surveyType == 1) {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.SURVEY_ID.eq(surveyId).and(QuestionGroup.SURVEY_TYPE.eq(1))).limit(1);
        } else {
            listQGquery = Query.select().from(QuestionGroup.TABLE)
                    .where(QuestionGroup.SURVEY_ID.eq(surveyId).and(QuestionGroup.SURVEY_TYPE.eq(2))).limit(1);
        }
        Log.d("shri", "SURVEY ID:" + surveyId);
        qgCursor = db.query(QuestionGroup.class, listQGquery);

        try {
            while (qgCursor.moveToNext()) {
                questionGroupId = qgCursor.get(QuestionGroup.ID);
                Log.d("shri", "QG ID: " + questionGroupId.toString());
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
                    Log.d("shri", "Question Id:" + qID);
                    Question question = db.fetch(Question.class, qID);
                    mQuestionsAdapter.add(question);
                }
            }
        } finally {
            if (qgCursor != null) {
                qgCursor.close();
            }
            if (qgqCursor != null) {
                qgqCursor.close();
            }
        }
        ListView listViewQuestions = (ListView) rootView.findViewById(R.id.listViewQuestions);
        listViewQuestions.setAdapter(mQuestionsAdapter);

        Button btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> user = session.getUserDetails();
                final Long currentTS = System.currentTimeMillis();
                //  Toast.makeText(getActivity(),currentTS+"(((((",Toast.LENGTH_SHORT).show();

                mSelectedUserType = userType.get(spinnerUserType.getSelectedItem().toString());
                // Toast.makeText(getActivity(),mSelectedUserType+"",Toast.LENGTH_SHORT).show();
                final HashMap<Question, String> answers = mQuestionsAdapter.getAnswers();
                AlertDialog noAnswerDialog = new AlertDialog.Builder(getContext()).create();
                noAnswerDialog.setTitle(R.string.app_name);
                noAnswerDialog.setCancelable(false);

                boolean flag = false;
                if (Constants.surveyType == 1) {
                    if (answers.size() != mQuestionsAdapter.getQuestionSize()) {
                        flag = false;
                        noAnswerDialog.setMessage(getString(R.string.survey_empty_response_body));

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
                    // noAnswerDialog = new AlertDialog.Builder(getContext()).create();
                    // noAnswerDialog.setTitle(R.string.survey_empty_response_title);
                    //     noAnswerDialog.setMessage(getString(R.string.survey_empty_response_body));
                    noAnswerDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    noAnswerDialog.show();
                } else {

                    if (Constants.surveyType == 2 && questionActivity.getLati() == 0d && questionActivity.getLong() == 0d) {
                        // Toast.makeText(getActivity(), "make sure GPS enabled & try to submit once ", Toast.LENGTH_SHORT).show();
                        showSignupResultDialog(getResources().getString(R.string.app_name), "make sure GPS enabled & try to submit once ", getResources().getString(R.string.Ok));
                        questionActivity.registerForLocationUpdates();
                        questionActivity.l1();
                        // Toast.makeText(getActivity(),questionActivity.getLati()+":"+questionActivity.getLong(),Toast.LENGTH_SHORT).show();


                    } else {
                        //Toast.makeText(getActivity(),user.get(session.KEY_ID)+"",Toast.LENGTH_SHORT).show();
                        final Story story = new Story();
                        story.setSchoolId(schoolId);
                        story.setUserId(Long.parseLong(user.get(SessionManager.KEY_ID)));
                        story.setGroupId(questionGroupId);
                        story.setRespondentType(mSelectedUserType);
                        story.setCreatedAt(currentTS);
                    //    Toast.makeText(getActivity(),currentTS+"",Toast.LENGTH_LONG).show();
                        if (Constants.surveyType == 2) {
                            story.setImage(StoreImageToFile());
                            story.setLat(questionActivity.getLati());
                            story.setLng(questionActivity.getLong());
                            double distance = getLocationDistance(lat, lng, questionActivity.getLati(), questionActivity.getLong());
                            double distancetemp=0l;
                            String distancecal;
                            distancetemp=distance;
                            if (distance >= 1000) {
                                // Toast.makeText(getActivity(),distance/1000+" Km",Toast.LENGTH_SHORT).show();
                                distance = (distance / 1000);

                                distancecal = getRoundUp(distance) + " "+getResources().getString(R.string.km);

                            } else {
                                //   Toast.makeText(getActivity(),distance+" Metres",Toast.LENGTH_SHORT).show();
                                distancecal = distance + " "+getResources().getString(R.string.meters);
                            }

if(distancetemp<=500l||lat==0l||lng==0l)
{
    storetoDB(story, answers, currentTS);
}else {

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setCancelable(false);


    builder.setMessage(getResources().getString(R.string.distancemsg)+" " + distancecal).setTitle(getResources().getString(R.string.distance))
            .setPositiveButton(getResources().getString(R.string.response_positive), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    storetoDB(story, answers, currentTS);


                }
            })
            .setNegativeButton(getResources().getString(R.string.response_negative), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    questionActivity.registerForLocationUpdates();
                    questionActivity.l1();

                }
            });

    builder.show();

}
                        } else {
                            story.setLat(0d);
                            story.setLng(0d);
                            storetoDB(story, answers, currentTS);
                        }


                    }
                }
            }
        });

        return rootView;
    }

    private void storetoDB(Story story, HashMap<Question, String> answers, Long currentTS) {
        db.persist(story);
        Log.d(this.toString(), "Created story: " + String.valueOf(story.getId()));
        Log.d(this.toString(), answers.entrySet().toString());

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
            Log.d(this.toString(), "Created answer: " + String.valueOf(new_answer.getId()) + " : " + new_answer.getText());
        }

        // Ask if the user wants to record more responses
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.prompt_new_rsponse)).setTitle(getResources().getString(R.string.responseSaved))
                .setPositiveButton(getResources().getString(R.string.response_positive), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(getActivity(), QuestionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("surveyId", surveyId);
                        intent.putExtra("surveyName", surveyName);
                        intent.putExtra("schoolId", schoolId);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.response_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                                    /*Intent intent = new Intent(getActivity(), MainDashList.class);
                                    intent.putExtra("surveyId", surveyId);
                                    intent.putExtra("surveyName", surveyName);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);*/
                        getActivity().finish();

                    }
                });
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
                boolean result = UtilityCamImage.checkPermission(getActivity());

                if (items[item].equals(getResources().getString(R.string.takephoto))) {
                    userChoosenTask = getResources().getString(R.string.takephoto);
                    if (result)
                        cameraIntent();

                } else if (items[item].equals(getResources().getString(R.string.choosefromlib))) {
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


    Bitmap ShrinkBitmap(Bitmap file, int width, int height) {

        Bitmap resized = Bitmap.createScaledBitmap(file, width, height, true);

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

        thumbnail = ShrinkBitmap(thumbnail, 800, 600);
        Bitmap dest = Bitmap.createBitmap(thumbnail.getWidth(), thumbnail.getHeight(), Bitmap.Config.ARGB_8888);







        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(35);
        tPaint.setColor(Color.BLUE);
        tPaint.setStyle(Paint.Style.FILL);
        float height = tPaint.measureText("yY");
        cs.drawBitmap(thumbnail,0 ,0,tPaint);
        cs.drawText(getDate(System.currentTimeMillis()), 20f, height+15f, tPaint);






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
       /* FileOutputStream fo;
        try {
            mypath.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
            tvImageName.setText(mypath.getName().toString());
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
        double dist = (double) (earthRadius * c);

        return dist;
    }







    public double getRoundUp(double val) {

        return Double.parseDouble(new DecimalFormat("##.##").format(val));
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:hh:ss a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
