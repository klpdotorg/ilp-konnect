package in.org.klp.ilpkonnect.DataLoad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.List;

import in.org.klp.ilpkonnect.BaseActivity;
import in.org.klp.ilpkonnect.BuildConfig;
import in.org.klp.ilpkonnect.InterfacesPack.SchoolStateInterface;
import in.org.klp.ilpkonnect.InterfacesPack.StateInterface;
import in.org.klp.ilpkonnect.KLPApplication;
import in.org.klp.ilpkonnect.Pojo.StatePojo;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.SurveyTypeActivity;
import in.org.klp.ilpkonnect.data.StringWithTags;
import in.org.klp.ilpkonnect.db.Boundary;
import in.org.klp.ilpkonnect.db.KontactDatabase;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.db.QuestionGroupQuestion;
import in.org.klp.ilpkonnect.db.Respondent;
import in.org.klp.ilpkonnect.db.School;
import in.org.klp.ilpkonnect.db.State;
import in.org.klp.ilpkonnect.db.SummaryInfo;
import in.org.klp.ilpkonnect.db.Summmary;
import in.org.klp.ilpkonnect.db.Survey;
import in.org.klp.ilpkonnect.utils.DailogUtill;
import in.org.klp.ilpkonnect.utils.ILPService;
import in.org.klp.ilpkonnect.utils.NoDefaultSpinner;
import in.org.klp.ilpkonnect.utils.ProNetworkSettup;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by shridhars on 1/24/2018.
 */

public class TempLoading extends BaseActivity implements OnItemSelectedListener {
    ArrayAdapter statelistAdp;
    private KontactDatabase db;
    private ArrayList<StatePojo> stateList;
    NoDefaultSpinner select_state, select_district, select_block;
    SquidCursor<Boundary> boundary_cursor = null;

    ProgressDialog progressDialog;
    SessionManager mSession;
    TextView tvNoteText;
    boolean flagForState, flagForDistrict, flagForblock;
    CardView linLayState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temploading);
        db = ((KLPApplication) getApplicationContext()).getDb();
        mSession = new SessionManager(getApplicationContext());
        tvNoteText = findViewById(R.id.tvNoteText);
        select_state = findViewById(R.id.select_state);
        select_block = findViewById(R.id.select_block);
        linLayState = findViewById(R.id.linLayState);


        select_district = findViewById(R.id.select_district);
        Query listStateQuery = Query.select().from(State.TABLE).orderBy(State.STATE.asc());
        select_state.setOnItemSelectedListener(this);
        select_block.setOnItemSelectedListener(this);
        select_district.setOnItemSelectedListener(this);
//        selectBlock.setOnItemSelectedListener(this);
        final String statePersonalKey = mSession.getStateSelection();
        SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);
        stateList = new ArrayList<>();

        if (stateCursor.getCount() > 0) {
            // we have surveys in DB, get them
            try {
                while (stateCursor.moveToNext()) {
                    State state = new State(stateCursor);
                    StatePojo pojo = new StatePojo(state.getState(), state.getStateLocText(), state.getStatekey(), state.getLangKey(), state.getLangName());
                    stateList.add(pojo);
                }

            } finally {
                if (stateCursor != null) {
                    stateCursor.close();
                }
            }
            /*StatePojo pojo = new StatePojo(0, getString(R.string.selectYourState), getString(R.string.selectYourState));
            stateList.add(0, pojo);*/

            if (stateList != null && stateList.size() > 1) {
                statelistAdp = new ArrayAdapter(this, R.layout.spinnertextview, stateList);
                select_state.setAdapter(statelistAdp);
                if (mSession.isSetupDone()) {
                    tvNoteText.setText(getResources().getString(R.string.downloadDistrcitData));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    select_state.setEnabled(false);
                    linLayState.setVisibility(View.GONE);

                } else {
                    tvNoteText.setText(getResources().getString(R.string.downloadDistrcitDataNext));
                    select_state.setSelection(mSession.getStatePosition() - 1);
                    select_state.setEnabled(false);
                    linLayState.setVisibility(View.VISIBLE);
                    linLayState.setVisibility(View.GONE);

                }


            }
            select_state.setSelection(mSession.getStatePosition() - 1);
        }


        fill_dropdown(1, select_district.getId(), 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());
        fill_dropdown(1, select_block.getId(), 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());

        //setPosition();
        select_state.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fill_dropdown(1, select_district.getId(), 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                if (flagForState)
                    loadDistrictData(((StatePojo) select_state.getSelectedItem()).getStateKey(), statePersonalKey);
                else
                    flagForState = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        select_district.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (flagForDistrict) {
                    //   boundaryForSelector.id.toString()
                    if (select_district.getSelectedItem() != null) {
                        if (select_district.getSelectedItemPosition() == 0) {
                            fill_dropdown(1, select_block.getId(), 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                        } else {
                            // mSession.setBoundaryPosition(select_district.getSelectedItemPosition());
                            fill_dropdown(1, R.id.select_block, Integer.parseInt(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

                            if (((StringWithTags) select_district.getSelectedItem()).flagCb == true) {
                                android.support.v7.app.AlertDialog noAnswerDialog = new android.support.v7.app.AlertDialog.Builder(TempLoading.this).create();

                                noAnswerDialog.setCancelable(false);
                                noAnswerDialog.setMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtDataAlreadyFound));
                                noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                loadData(Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), true,mSession.getToken());

                                            }
                                        });
                                noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                noAnswerDialog.show();


                            } else {
                                loadData(Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), false,mSession.getToken());
                            }
                        }
                    } else {
                        //Toast.makeText(getApplicationContext(), "load district", Toast.LENGTH_SHORT).show();
                        DailogUtill.showDialog(getResources().getString(R.string.pleaseLoadDataforSelectedState), getSupportFragmentManager(), getApplicationContext());
                    }
                } else {
                    flagForDistrict = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        select_block.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (flagForblock) {
                    //   boundaryForSelector.id.toString()
                    if (select_block.getSelectedItem() != null) {
                        if (select_block.getSelectedItemPosition() == 0) {

                        } else {
                            if (((StringWithTags) select_block.getSelectedItem()).flag == true) {
                                android.support.v7.app.AlertDialog noAnswerDialog = new android.support.v7.app.AlertDialog.Builder(TempLoading.this).create();

                                noAnswerDialog.setCancelable(false);
                                noAnswerDialog.setMessage(select_block.getSelectedItem().toString() + " " + getResources().getString(R.string.blockDataAlreadyFound));
                                noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                loadSchooldataForBlock(Long.parseLong(((StringWithTags) select_block.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()),mSession.getToken());
                                            }
                                        });
                                noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                noAnswerDialog.show();


                            } else {
                                loadSchooldataForBlock(Long.parseLong(((StringWithTags) select_block.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()),mSession.getToken());
                                //  loadData(Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());
                            }
                        }
                    } else {
                        //Toast.makeText(getApplicationContext(), "load district", Toast.LENGTH_SHORT).show();
                        DailogUtill.showDialog(getResources().getString(R.string.pleaseLoadDataforSelectedState), getSupportFragmentManager(), getApplicationContext());
                    }
                } else {
                    flagForblock = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }


    public void logout(MenuItem item) {
        if (item.getItemId() == R.id.action_logoutdataload) {
            android.support.v7.app.AlertDialog alertDailog = new android.support.v7.app.AlertDialog.Builder(TempLoading.this).create();

            alertDailog.setCancelable(false);
            alertDailog.setMessage(getResources().getString(R.string.doyouwantToLogout));
            alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_positive),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            logoutUser();

                        }
                    });
            alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDailog.show();
        }


    }

    public void logoutUser() {
        // KLPApplication.setLanguage(getApplicationContext(), "en");
        mSession.logoutUser();
        KLPApplication.setLanguage(getApplicationContext(), "en");
        db.deleteAll(Survey.class);
        db.deleteAll(School.class);
        db.deleteAll(Boundary.class);
        db.deleteAll(Respondent.class);
        db.deleteAll(State.class);
        db.deleteAll(Question.class);
        db.deleteAll(Summmary.class);
        db.deleteAll(SummaryInfo.class);
        db.deleteAll(QuestionGroupQuestion.class);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        this.finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDistrictData(final String stateKey, String personalStateKey) {

        initPorgresssDialogForSchool();
        updateProgressMessage(select_state.getSelectedItem().toString() + " " + getResources().getString(R.string.loadingStateDistrict), 0);
        String URL = BuildConfig.HOST + "/api/v1/boundary/admin1s/?state=" + stateKey;

        new ProNetworkSettup(TempLoading.this).downloadStateData(URL, stateKey, new StateInterface() {
            @Override
            public void success(String message) {
                finishProgress();
                flagForDistrict = false;
                fill_dropdown(1, select_district.getId(), 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

            }

            @Override
            public void failed(String message) {
                finishProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

            }
        });

    }


    public int getBlockID(Object parent) {
        StringWithTags boundaryForSelector = (StringWithTags) parent;
        if (boundaryForSelector != null) {
            return Integer.parseInt(boundaryForSelector.id.toString());
        }
        return 0;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();
    }

    private void loadSchooldataForBlock(final long id, String stateKey, long distId,String token) {
        initPorgresssDialogForSchool();

        updateProgressMessage(select_block.getSelectedItem().toString() + " "
                + getResources().getString(R.string.blockSchoolLoading), 0);
        //String URL=BuildConfig.HOST+ ILPService.SCHOOLS+"&admin2="+id;

        String URL = BuildConfig.HOST + ILPService.SCHOOLS + "&geometry=yes&admin2=" +
                id + "&state=" + stateKey.toLowerCase();

        new ProNetworkSettup(TempLoading.this).DownloadSchoolData(URL, id, distId,token, new SchoolStateInterface() {
            @Override
            public void success(String message) {
                finishProgress();

                if (mSession.isSetupDone() == false) {
                    //first time
                    // SharedPreferences sharedPreferences = getSharedPreferences("boundary", MODE_PRIVATE);
                    //   SharedPreferences.Editor editor = sharedPreferences.edit();
                    //  editor.putInt("district", select_district.getSelectedItemPosition()-1);
                    // editor.commit();
                    mSession.updateSetup(true);
                    Intent intent = new Intent(getApplicationContext(), SurveyTypeActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                } else {


                    int position = select_block.getSelectedItemPosition();
                    DailogUtill.showDialog(select_block.getSelectedItem().toString() + " " + getResources().getString(R.string.blockLeveldataLoadedSchussfully), getSupportFragmentManager(), getApplicationContext());
                    fill_dropdown(1, select_block.getId(), Integer.parseInt(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());
                    flagForblock = false;
                    select_block.setSelection(position);


                }


            }

            @Override
            public void failed(String message) {
                finishProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

            }


            @Override
            public void update(int message) {
                updateProgressMessage(select_block.getSelectedItem().toString() + " "
                        + getResources().getString(R.string.blockSchoolLoading), message);

            }
        });
    }


    private void fill_dropdown(int type, int id, int parent, String statekey) {
        List<StringWithTags> stringWithTags = get_boundary_data(parent, statekey, id);
        NoDefaultSpinner spinner = findViewById(id);
        //  spinner.setOnItemSelectedListener(this);
        ArrayAdapter<StringWithTags> boundaryArrayAdapter = new ArrayAdapter<StringWithTags>(this, R.layout.spinnertextview, stringWithTags);
        spinner.setAdapter(boundaryArrayAdapter);
        boundaryArrayAdapter.setDropDownViewResource(R.layout.spinnertextview);
    }


    private List<StringWithTags> get_boundary_data(int parent, String stateKey, int idview) {
        Query listboundary = Query.select().from(Boundary.TABLE)
                .where(Boundary.PARENT_ID.eq(parent).and(Boundary.TYPE.eq("primaryschool").and(Boundary.STATE_KEY.eq(stateKey))))
                .orderBy(Boundary.NAME.asc());

        List<StringWithTags> boundaryList = new ArrayList<StringWithTags>();
        boundary_cursor = db.query(Boundary.class, listboundary);
        if (boundary_cursor.moveToFirst()) {
            do {
                Boundary b = new Boundary(boundary_cursor);

                StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), mSession, b.isFlag(), b.isFlagCB());
                boundaryList.add(boundary);

            } while (boundary_cursor.moveToNext());
        }
        if (boundary_cursor != null)
            boundary_cursor.close();
        if (idview == R.id.select_district) {
            boundaryList.add(0, new StringWithTags(getResources().getString(R.string.selectDistrict), "0", "0", getResources().getString(R.string.selectDistrict), mSession, false, false));
        } else {
            boundaryList.add(0, new StringWithTags(getResources().getString(R.string.selectblock), "0", "0", getResources().getString(R.string.selectDistrict), mSession, false, false));

        }
        return boundaryList;
    }

    public String getLocTextBoundary(Boundary b) {
        if (mSession.getLanguagePosition() <= 1) {
            //english
            return b.getName() != null ? b.getName() : b.getLocName();

        } else {
            //native
            return b.getLocName() != null ? b.getLocName() : b.getName();
        }
    }


  /*  @Override
    protected void onResume() {
        super.onResume();
        setPosition();
        fill_dropdown(1, R.id.select_block, Integer.parseInt(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

    }*/

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)


        StringWithTags boundaryForSelector = null;
        try {
            boundaryForSelector = (StringWithTags) parent.getItemAtPosition(pos);
        } catch (Exception e) {

        }

        int viewid = 0;
        if (parent != null) {
            viewid = parent.getId();
        }


        switch (viewid) {


            case R.id.select_state:
                fill_dropdown(1, R.id.select_district, 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());


                if (boundaryForSelector != null) {
                    fill_dropdown(1, R.id.select_block, Integer.parseInt(boundaryForSelector.id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

                } else {

                    fill_dropdown(1, R.id.select_block, 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                }
                flagForDistrict = false;


                break;


            case R.id.select_district:
                if (boundaryForSelector != null) {
                    fill_dropdown(1, R.id.select_block, Integer.parseInt(boundaryForSelector.id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

                } else {
                    fill_dropdown(1, R.id.select_block, 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                }
                break;
            default:

                break;


        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void loadData(final long id, final String stateKey, final boolean isDataAlreadyDownloaded, final String token) {

        //loading block,cluster and school
        initPorgresssDialogForSchool();

        updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtblockloading), 0);

        String url = BuildConfig.HOST + "/api/v1/boundary/admin1/" + id + "/admin2/";

        new ProNetworkSettup((TempLoading.this)).DownloadBlocksData(url, stateKey, isDataAlreadyDownloaded, token,new SchoolStateInterface() {
            @Override
            public void success(String message) {

                updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtClusterLoading), 0);
                String url = BuildConfig.HOST + "/api/v1/boundary/admin1/" + id + "/admin3";
                new ProNetworkSettup(TempLoading.this).DownloadClusterData(url, id, stateKey, isDataAlreadyDownloaded,token, new SchoolStateInterface() {
                    @Override
                    public void success(String message) {
                        finishProgress();
                        DailogUtill.showDialog(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.blockandCLusterdownloaded), getSupportFragmentManager(), getApplicationContext());
                        int position = select_district.getSelectedItemPosition();
                        fill_dropdown(1, R.id.select_district, 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());
                        select_district.setSelection(position);
                        flagForDistrict = false;
                        flagForblock = false;
                        //setPosition();
                        fill_dropdown(1, R.id.select_block, Integer.parseInt(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

                    }

                    @Override
                    public void failed(String message) {
                        finishProgress();
                        DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    }

                    @Override
                    public void update(int message) {
                        updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtClusterLoading), message);
                    }


                });

            }

            @Override
            public void failed(String message) {
                finishProgress();

                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
            }

            @Override
            public void update(int message) {
                //show progress bar for block
                updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtblockloading), message);
            }
        });

    }


    /*public void setPosition() {
        select_district.setSelection(mSession.getBounaryPosition());
        flagForDistrict = false;
    }
*/
    private void initPorgresssDialogForSchool() {
        progressDialog = new ProgressDialog(TempLoading.this);
        progressDialog.setMessage("");
        progressDialog.setProgress(0);//initially progress is 0
        progressDialog.setMax(100);//sets the maximum value 100
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void updateProgressMessage(String message, int count) {

        progressDialog.setMessage(message);
        progressDialog.setProgress(count);

    }

    private void finishProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }


}
