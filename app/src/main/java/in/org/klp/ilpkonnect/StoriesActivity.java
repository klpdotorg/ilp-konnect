package in.org.klp.ilpkonnect;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import in.org.klp.ilpkonnect.Pojo.ImagesPOJO;
import in.org.klp.ilpkonnect.Pojo.SlideshowDialogFragment;
import in.org.klp.ilpkonnect.Retro.ApiClient;
import in.org.klp.ilpkonnect.Retro.ApiInterface;
import in.org.klp.ilpkonnect.adapters.ImageLoadAdapter;
import in.org.klp.ilpkonnect.adapters.StoryAdapter;
import in.org.klp.ilpkonnect.db.Answer;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroup;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.Story;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoriesActivity extends AppCompatActivity {
    private StoryAdapter mStoryAdapter;
    private KontactDatabase db;
    private Long surveyId;
    private String surveyName, blockid, distrcitId;
    private Long boundaryId, sdate, edate;
    private SessionManager mSession;
    Integer Yes = 0;
    Integer No = 0;
    Integer dontknow = 0;
    double yesP = 0;
    double noP = 0;
    double dontKnowP = 0;
    String loggedinUserId;
    int reporttype;
    Long surveyType;
    TextView tv_total_stories, tv_total_by_user, tv_total_by_user_not_synced, tvYes, tvNo, tvDontKnow, tvYesP, tvNoP, tvDontKnowp;
    RecyclerView recycler;
    TextView tvShow;
    ArrayList<String> imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportsample);

        mSession = new SessionManager(getApplicationContext());
        HashMap<String, String> user = mSession.getUserDetails();
        loggedinUserId = user.get(SessionManager.KEY_ID);

        db = ((KLPApplication) getApplicationContext()).getDb();
        surveyId = getIntent().getLongExtra("surveyId", 0);
        surveyName = getIntent().getStringExtra("surveyName");
        String bName = getIntent().getStringExtra("boundary");
        final String schoolID = getIntent().getStringExtra("schoolID");
        boundaryId = getIntent().getLongExtra("bid", 0);
        sdate = getIntent().getLongExtra("sdate", 0);
        surveyType = getIntent().getLongExtra("surveyType", 0);
        edate = getIntent().getLongExtra("edate", 0);
        blockid = getIntent().getStringExtra("blockid");
        distrcitId = getIntent().getStringExtra("ditrcitId");
        tvShow = (TextView) findViewById(R.id.tvShow);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        if (schoolID != null && !schoolID.equalsIgnoreCase("") && Constants.surveyType == 2) {
            tvShow.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.VISIBLE);
        } else {
            tvShow.setVisibility(View.INVISIBLE);
            recycler.setVisibility(View.INVISIBLE);
        }

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new ImageLoadAdapter(getApplicationContext(), new ImagesPOJO()));
        //  Toast.makeText(getApplicationContext(), distrcitId + "", Toast.LENGTH_SHORT).show();

        //   Toast.makeText(getApplicationContext(),surveyType+"",Toast.LENGTH_SHORT).show();
        reporttype = getIntent().getIntExtra("reporttype", 0);
        mStoryAdapter = new StoryAdapter(
                new ArrayList<Story>(),
                this, db, loggedinUserId
        );

        TextView tv_bdry = (TextView) findViewById(R.id.tv_bdry);
        TextView tv_daterange = (TextView) findViewById(R.id.tv_daterange);
        tv_total_stories = (TextView) findViewById(R.id.tv_total_stories);
        tv_total_by_user = (TextView) findViewById(R.id.tv_total_by_user);
        tv_total_by_user_not_synced = (TextView) findViewById(R.id.tv_total_by_user_not_synced);
        tvYes = (TextView) findViewById(R.id.tvYes);
        tvNo = (TextView) findViewById(R.id.tvNo);
        tvDontKnow = (TextView) findViewById(R.id.tvDontKnow);


        tvYesP = (TextView) findViewById(R.id.tvYesP);
        tvNoP = (TextView) findViewById(R.id.tvNoP);
        tvDontKnowp = (TextView) findViewById(R.id.tvDontKnowp);
// 1 day equal to 86400000 millisecond


        long oneDay = 86400000;
        tv_bdry.setText(bName);
        tv_daterange.setText(
                String.format(
                        getString(R.string.meta_daterange),
                        getDate(sdate, "dd-MM-yyyy"),
                        getDate(edate - oneDay, "dd-MM-yyyy")
                )
        );

        getAllLevelReport(loggedinUserId);


        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(StoriesActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.loading));
                progressDialog.show();
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                apiInterface.getImages(schoolID).enqueue(new Callback<ImagesPOJO>() {
                    @Override
                    public void onResponse(Call<ImagesPOJO> call, Response<ImagesPOJO> response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (response.isSuccessful()) {
                          //  imageurl = (ArrayList<String>) response.body().getImages();
                            ImageLoadAdapter adapter = new ImageLoadAdapter(getApplicationContext(), response.body());
                            recycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        } else {
                            Toast.makeText(getApplicationContext(), "No Image", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ImagesPOJO> call, Throwable t) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.noInternetCon), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


        recycler.addOnItemTouchListener(new ImageLoadAdapter.RecyclerTouchListener(getApplicationContext(), recycler, new ImageLoadAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("images", imageurl);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = new SlideshowDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }


    public ArrayList<Long> getAllQuestionIds() {

        SquidCursor<QuestionGroup> qgCursor = null;
        SquidCursor<QuestionGroupQuestion> qgqCursor = null;

        Query listQGquery = Query.select().from(QuestionGroup.TABLE)
                .where(QuestionGroup.SURVEY_ID.eq(surveyId).and(QuestionGroup.SURVEY_TYPE.eq(surveyType))).limit(1);
        qgCursor = db.query(QuestionGroup.class, listQGquery);

        try {
            while (qgCursor.moveToFirst()) {
                Long questionGroupId = qgCursor.get(QuestionGroup.ID);
                Query listQGQquery = Query.select().from(QuestionGroupQuestion.TABLE)
                        .where(QuestionGroupQuestion.QUESTIONGROUP_ID.eq(questionGroupId));
                qgqCursor = db.query(QuestionGroupQuestion.class, listQGQquery);
                ArrayList<Question> resultQuestions = new ArrayList<Question>();


                while (qgqCursor.moveToNext()) {
                    Long qID = qgqCursor.get(QuestionGroupQuestion.QUESTION_ID);
                    Question question = db.fetch(Question.class, qID);
                    resultQuestions.add(question);

                }

                ArrayList<Long> allqids = new ArrayList<>();

                for (Question question : resultQuestions) {
                    if (question == null) {
                        continue;
                    }
                    allqids.add(question.getId());


                }
                return allqids;

            }
        } finally {

            qgCursor.close();
            qgqCursor.close();

        }
        return null;
    }


    public void getAllLevelReport(String loggedinUserId1) {


        Query listStoryQuery = Constants.listStoryQuery;
        SquidCursor<Story> storyCursor = db.query(Story.class, listStoryQuery);
        Query listStoryByUserQuery = listStoryQuery.where(Story.USER_ID.eq(loggedinUserId1));
        SquidCursor<Story> storyByUserCursor = db.query(Story.class, listStoryByUserQuery);
        Query listStoryByUserNotSyncedQuery = listStoryQuery.where(Story.SYNCED.eq(0));
        SquidCursor<Story> storyByUserNSCursor = db.query(Story.class, listStoryByUserNotSyncedQuery);

        try {
            if (storyCursor.getCount() > 0) {
                // we have stories in DB, get them
                tv_total_stories.setText(
                        String.format(
                                getString(R.string.meta_total),
                                storyCursor.getCount()
                        )
                );
                tv_total_by_user.setText(
                        String.format(
                                getString(R.string.meta_total_by_user),
                                storyByUserCursor.getCount()
                        )
                );
                tv_total_by_user_not_synced.setText(
                        String.format(
                                getString(R.string.meta_total_by_user_nsy),
                                storyByUserNSCursor.getCount()
                        )
                );

                try {

                    ArrayList<Long> al = getAllQuestionIds();
                    while (storyCursor.moveToNext()) {
                        Story story = new Story(storyCursor);
                        mStoryAdapter.add(story);
                        Yes += db.count(Answer.class, Answer.STORY_ID.eq(story.getId()).and(Answer.TEXT.eq("Yes").and(Answer.QUESTION_ID.in(al))));
                        No += db.count(Answer.class, Answer.STORY_ID.eq(story.getId()).and(Answer.TEXT.eq("No").and(Answer.QUESTION_ID.in(al))));
                        dontknow += db.count(Answer.class, Answer.STORY_ID.eq(story.getId()).and(Answer.TEXT.eq("Don't Know").and(Answer.QUESTION_ID.in(al))));


                        //  Toast.makeText(getApplicationContext(),"YES:"+yescount+"NO:"+nocount+"Dont Know:"+dontknow,Toast.LENGTH_SHORT).show();


                    }
                } finally {

                    tvYes.setText(Yes + "");
                    tvNo.setText(No + "");
                    tvDontKnow.setText(dontknow + "");
                    yesP = getScorePercent(Yes, (Yes + No + dontknow));
                    noP = getScorePercent(No, (Yes + No + dontknow));
                    //   noP = getScorePercent(No, storyCursor.getCount() *(Yes+No+dontknow));
                    dontKnowP = getScorePercent(dontknow, (Yes + No + dontknow));
                    tvYesP.setText(yesP + "%");
                    tvNoP.setText(noP + "%");
                    tvDontKnowp.setText(dontKnowP + "%");


                    if (storyCursor != null) {
                        storyCursor.close();
                    }

                    //  Toast.makeText(getApplicationContext(),Yes+":"+No+":"+dontknow,Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(StoriesActivity.this, "No stories found!", Toast.LENGTH_LONG).show();
            }
        } finally {
            if (storyCursor != null) {
                storyCursor.close();
            }
            storyByUserCursor.close();
            storyByUserNSCursor.close();
        }

 /*       ListView listview = (ListView) findViewById(R.id.listview_story);
        listview.setAdapter(mStoryAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Long storyId = mStoryAdapter.getItem(i).getId();
                Log.d("Story", storyId.toString());
//                String storyName = mStoryAdapter.getItem(i).getName();
//                Intent intent = new Intent(getActivity(), StoryDetails.class);
//                intent.putExtra("storyId", storyId);
//                intent.putExtra("storyName", storyName);
//                startActivity(intent);
            }
        });*/
    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public double getScorePercent(int correct, int total) {
        float val = 100f * correct / total;
        return Double.parseDouble(new DecimalFormat("##.##").format(val));
    }
}
