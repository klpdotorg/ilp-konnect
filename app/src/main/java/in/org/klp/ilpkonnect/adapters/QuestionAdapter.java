package in.org.klp.ilpkonnect.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import in.org.klp.ilpkonnect.MultiSelectSpinner;
import in.org.klp.ilpkonnect.MultiSelectSpinnerForQuestion;
import in.org.klp.ilpkonnect.Quesionviewspojo;
import in.org.klp.ilpkonnect.R;
import in.org.klp.ilpkonnect.db.Question;
import in.org.klp.ilpkonnect.utils.Constants;
import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by bibhas on 6/18/16.
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
    private Context _context;
    private ArrayList<Question> questions;
    private HashMap<Question, String> answers;
    //  private HashMap<Integer, String> radioButtons;
    SessionManager sessionManager;
    HashMap<Question, Questiontemp> answertemp;

    ArrayList<QuestionTempForCheck> tempCheckboxList;


    HashMap<Long, List<Integer>> hashMapSelected;
    HashMap<Long, Integer> singleSelTempList;

    //  ArrayList<Integer> indexesSelected;

    // View lookup cache
    private static class QuestionHolder {
        TextView qText;
        //RadioGroup rgQuestion;
        // RadioGroup rgQuestionnew;
        EditText edittext;
        //     LinearLayout listcheckview;
        MultiSelectSpinnerForQuestion multiSpinner;
        Spinner spnSingleSelection;

    }

    public QuestionAdapter(ArrayList<Question> questions, Context context) {
        super(context, R.layout.list_item_question, questions);
        this._context = context;
        this.questions = questions;
        this.answers = new HashMap<Question, String>();
        sessionManager = new SessionManager(this._context);
        this.answertemp = new HashMap<Question, Questiontemp>();
        tempCheckboxList = new ArrayList<>();
        hashMapSelected = new HashMap<>();
        singleSelTempList = new HashMap<>();

        // can't rely on the radio button text as
        // we're using kannada strings, so using button text ID
        // which is same for both english and kannada strings
        // Here we're just mapping button id to Yes/No/Don't Know
        // because those are what we're saving in database
       /* this.radioButtons = new HashMap<Integer, String>();
        this.radioButtons.put(R.id.radioYes, "Yes");
        this.radioButtons.put(R.id.radioNo, "No");
        this.radioButtons.put(R.id.radioNoAnswer, "Don't Know");*/
    }

    @Override
    public int getCount() {
        return questions.size(); // size, lenght, count ...?
    }

    @Override
    public Question getItem(int position) {
        return questions.get(position);
    }

    public HashMap<Question, String> getAnswers() {

        answers.values().removeAll(Collections.singleton(""));
        //   Toast.makeText(_context, answers.size() + "", Toast.LENGTH_SHORT).show();

        return answers;
        // return answers;
    }

    public int getQuestionSize() {
        return questions.size();
    }

    public void addAnswer(Question question, String answer) {
        this.answers.put(question, answer);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Question question = getItem(position);

        final QuestionHolder questionHolder = new QuestionHolder();

        LayoutInflater inflater = LayoutInflater.from(_context);
        convertView = inflater.inflate(R.layout.list_item_question, parent, false);
        questionHolder.qText = convertView.findViewById(R.id.textViewQuestion);
        questionHolder.edittext = convertView.findViewById(R.id.edittext);
        //   questionHolder.rgQuestionnew = convertView.findViewById(R.id.rgQuestionnew);
        questionHolder.spnSingleSelection = convertView.findViewById(R.id.spnSingleSelection);
        //  questionHolder.listcheckview = convertView.findViewById(R.id.listcheckview);
        questionHolder.multiSpinner = convertView.findViewById(R.id.multiSpinner);
        questionHolder.edittext.setVisibility(View.GONE);
//        questionHolder.rgQuestionnew.setVisibility(View.GONE);
        questionHolder.multiSpinner.setVisibility(View.GONE);
        questionHolder.spnSingleSelection.setVisibility(View.GONE);
        final View result = convertView;


        String type = question.getType();
        //type="radio";
        if (type.equalsIgnoreCase("checkbox")) {
            questionHolder.multiSpinner.setVisibility(View.VISIBLE);
            String option = question.getOptions();
            String nativeoption = question.getLangOptions() != null ? question.getLangOptions() : question.getOptions();
            String[] options = option.split(",");
            String[] nativeoptions = nativeoption.split(",");
            if (options.length != nativeoptions.length) {
                nativeoptions = options;
            }

            if (sessionManager.getLanguagePosition() <= 1) {
                questionHolder.multiSpinner.setItems(options, options);
            } else {
                questionHolder.multiSpinner.setItems(nativeoptions, options);
            }

            if (hashMapSelected != null && hashMapSelected.size() > 0 && hashMapSelected.get(question.getId()) != null) {
                int[] list = new int[hashMapSelected.get(question.getId()).size()];
                for (int i = 0; i < hashMapSelected.get(question.getId()).size(); i++) {
                    list[i] = hashMapSelected.get(question.getId()).get(i);
                    //    Log.d("shri",list[i]+":"+i);

                }

                if (list.length > 0) {
                    questionHolder.multiSpinner.setSelection(list);
                }


            } else {
                questionHolder.multiSpinner.setSelection(new int[]{-1});
            }
            questionHolder.multiSpinner.setListener(new MultiSelectSpinnerForQuestion.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //hashMapSelected.clear();
                    hashMapSelected.put(question.getId(), indices);
                    // Toast.makeText(_context,indices+"",Toast.LENGTH_SHORT).show();
                    answers.put(question, questionHolder.multiSpinner.getSelectedItemsAsString());
                }

                @Override
                public void selectedStrings(List<String> strings) {

                }
            });


        } else if (type.equalsIgnoreCase("radio")) {

            questionHolder.spnSingleSelection.setVisibility(View.VISIBLE);
            String option = question.getOptions();
            String nativeoption = question.getLangOptions() != null ? question.getLangOptions() : question.getOptions();
            String[] options = option.split(",");
            String[] nativeoptions = nativeoption.split(",");
            List<Quesionviewspojo> myList = new ArrayList<>();
            if (options.length == nativeoptions.length) {
                for (int i = 0; i < options.length; i++) {
                    String name = options[i];
                    String locname = nativeoptions[i];
                    Quesionviewspojo pojo = new Quesionviewspojo(name, locname, sessionManager.getLanguagePosition());
                    myList.add(pojo);


                }
            } else {

                for (int i = 0; i < options.length; i++) {
                    String name = options[i];
                    String locname = options[i];
                    Quesionviewspojo pojo = new Quesionviewspojo(name, locname, sessionManager.getLanguagePosition());
                    myList.add(pojo);


                }

            }


            Quesionviewspojo pojo = new Quesionviewspojo(_context.getResources().getString(R.string.selectoption), _context.getResources().getString(R.string.selectoption), -50);
            myList.add(0, pojo);
            final ArrayAdapter<Quesionviewspojo> adapter = new ArrayAdapter<Quesionviewspojo>(
                    _context, R.layout.selectoptionspinner, myList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            adapter.setDropDownViewResource(R.layout.selectoption);
            questionHolder.spnSingleSelection.setAdapter(adapter);
            questionHolder.spnSingleSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    Quesionviewspojo quesionviewspojo = (Quesionviewspojo) questionHolder.spnSingleSelection.getSelectedItem();
                    if (quesionviewspojo.getPosition() != -50) {
                        singleSelTempList.put(question.getId(), i);
                        answers.put(question, quesionviewspojo.getOption());

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (singleSelTempList != null && singleSelTempList.size() > 0) {
                if (singleSelTempList.get(question.getId()) != null) {
                    questionHolder.spnSingleSelection.setSelection(singleSelTempList.get(question.getId()));
                }
            }


        } else if (type.equalsIgnoreCase("NumericBox")) {

            questionHolder.edittext.setVisibility(View.VISIBLE);
            questionHolder.edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
            questionHolder.edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

            for (int j = 0; j < answertemp.size(); j++) {
                if (answertemp.get(question) != null && answertemp.get(question).getViewtype().equalsIgnoreCase("numeric"))

                {
                    questionHolder.edittext.setText(answertemp.get(question).getValue());
                }

            }

            questionHolder.edittext.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    // Toast.makeText(_context,"before text chnaged",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    String data = s.toString().trim();
                    answers.put(question, data);

                    Questiontemp questiontemp = new Questiontemp();

                    questiontemp.setViewtype("numeric");
                    questiontemp.setValue(data);
                    questiontemp.setQuestion(question);
                    answertemp.put(question, questiontemp);
                    // answers.put(question, data);
                }
            });


        } else {
            //text

            //   questionHolder.edittext.setVisibility(View.VISIBLE);
            //   questionHolder.edittext.setInputType(InputType.TYPE_CLASS_TEXT);
            //  questionHolder.edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});


            questionHolder.edittext.setVisibility(View.VISIBLE);
            questionHolder.edittext.setInputType(InputType.TYPE_CLASS_TEXT);
            questionHolder.edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(400)});

            for (int j = 0; j < answertemp.size(); j++) {
                if (answertemp.get(question) != null && answertemp.get(question).getViewtype().equalsIgnoreCase("text"))

                {
                    questionHolder.edittext.setText(answertemp.get(question).getValue());
                }

            }

            questionHolder.edittext.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    // Toast.makeText(_context,"before text chnaged",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    String data = s.toString().trim();
                    answers.put(question, data);

                    Questiontemp questiontemp = new Questiontemp();

                    questiontemp.setViewtype("text");
                    questiontemp.setValue(data);
                    questiontemp.setQuestion(question);
                    answertemp.put(question, questiontemp);
                    //   answers.put(question, data);
                }
            });


        }


        if (sessionManager.getLanguagePosition() <= 1) {
            //english
            questionHolder.qText.setText(question.getText() != null ? question.getText() : question.getTextKn());

        } else {
            //native
            questionHolder.qText.setText(question.getTextKn() != null ? question.getTextKn() : question.getText());
        }
        questionHolder.qText.setTag(question.getId());
        questionHolder.qText.setTypeface(questionHolder.qText.getTypeface(), Typeface.BOLD);


        return convertView;
    }

}
