package in.org.klp.ilpkonnect.adapters;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


    // View lookup cache
    private static class QuestionHolder {
        TextView qText;
        //RadioGroup rgQuestion;
        RadioGroup rgQuestionnew;
        EditText edittext;
        LinearLayout listcheckview;
        //  MultiSelectSpinnerForQuestion checkboxtemp;

    }

    public QuestionAdapter(ArrayList<Question> questions, Context context) {
        super(context, R.layout.list_item_question, questions);
        this._context = context;
        this.questions = questions;
        this.answers = new HashMap<Question, String>();
        sessionManager = new SessionManager(this._context);
        this.answertemp = new HashMap<Question, Questiontemp>();
        tempCheckboxList = new ArrayList<>();
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        final Question question = getItem(position);

        final QuestionHolder questionHolder = new QuestionHolder();

        LayoutInflater inflater = LayoutInflater.from(_context);
        convertView = inflater.inflate(R.layout.list_item_question, parent, false);
        questionHolder.qText = convertView.findViewById(R.id.textViewQuestion);
        questionHolder.edittext = convertView.findViewById(R.id.edittext);
        questionHolder.rgQuestionnew = convertView.findViewById(R.id.rgQuestionnew);
        questionHolder.listcheckview = convertView.findViewById(R.id.listcheckview);

        questionHolder.edittext.setVisibility(View.GONE);
        questionHolder.rgQuestionnew.setVisibility(View.GONE);
        final View result = convertView;


        String type = question.getType();
        //type="radio";
        if (type.equalsIgnoreCase("checkbox")) {
            String option = question.getOptions();
            String nativeoption = question.getLangOptions() != null ? question.getLangOptions() : question.getOptions();
            String[] options = option.split(",");
            String[] nativeoptions = nativeoption.split(",");
            List<Quesionviewspojo> myList = new ArrayList<>();
            if (options.length == nativeoptions.length) {
                for (int i = 0; i < options.length; i++) {
                    String name = options[i];
                    String locname = nativeoptions[i];
                    Quesionviewspojo pojo = new Quesionviewspojo(name, locname);
                    myList.add(pojo);


                }

            } else {
                for (int i = 0; i < options.length; i++) {
                    String name = options[i];
                    String locname = options[i];
                    Quesionviewspojo pojo = new Quesionviewspojo(name, locname);
                    myList.add(pojo);


                }
            }


            //   List<Quesionviewspojo> myList = new ArrayList<Quesionviewspojo>(Arrays.asList(options,nativeoptions));


            for (int i = 0; i < myList.size(); i++) {

                CheckBox checkBox = new CheckBox(_context);
                checkBox.setPadding(0, 0, 40, 0);
                if (sessionManager.getLanguagePosition() <= 1) {
                    //english

                    checkBox.setText(myList.get(i).getOption());
                } else {
                    //local
                    checkBox.setText(myList.get(i).getNativeOption());
                }

                checkBox.setTag(myList.get(i).getOption());

                checkBox.setId(i);
                String message = "";

                for (int j = 0; j < tempCheckboxList.size(); j++) {
                    /*if (checkBox.getId() == tempCheckboxList.get(j).getId() &&
                            tempCheckboxList.get(j).getQuestion().getId() == question.getId() &&
                            tempCheckboxList.get(j).getViewtype().equalsIgnoreCase("checkbox")) {
                        checkBox.setChecked(tempCheckboxList.get(j).isFlag());



                    }*/


                    //question id check
                    if (question.getId() == tempCheckboxList.get(j).getId() &&
                            tempCheckboxList.get(j).getViewtype().equalsIgnoreCase("checkbox")) {

                        if (tempCheckboxList.get(j).getHaspmaplist() != null && tempCheckboxList.get(j).getHaspmaplist().size() > 0)
                            for (int k = 0; k < tempCheckboxList.get(j).getHaspmaplist().size(); k++) {
                                if (tempCheckboxList.get(j).getHaspmaplist().get(k) != null
                                        && tempCheckboxList.get(j).getHaspmaplist().containsKey(k) && k == checkBox.getId())

                                {

                                    checkBox.setChecked(tempCheckboxList.get(j).getHaspmaplist().get(k));
                                }

                            }
//                           questionHolder.checkboxtemp.setSelection( select.toArray(new String[select.size()]));


                    }

                }

//

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = "";
                        QuestionTempForCheck temp = new QuestionTempForCheck();
                        HashMap<Integer, Boolean> hashMap = new HashMap<>();
                        for (int i = 0; i < questionHolder.listcheckview.getChildCount(); i++) {
                            View nextChild = questionHolder.listcheckview.getChildAt(i);

                            if (nextChild instanceof CheckBox) {
                                CheckBox check = (CheckBox) nextChild;
                                if (check.isChecked()) {
                                    if (message.equalsIgnoreCase("")) {
                                        message = message + check.getTag().toString();
                                    } else {
                                        message = message + "," + check.getTag().toString();
                                    }


                                }

                                temp.setId(question.getId());
                                temp.setViewtype("checkbox");
                                temp.setQuestion(question);
                                hashMap.put(check.getId(), check.isChecked());
                                temp.setHaspmaplist(hashMap);
                                tempCheckboxList.add(temp);

                            }

                        }

                        // Toast.makeText(_context,message,Toast.LENGTH_SHORT).show();

                        answers.put(question, message);


                        //    Log.d("sss",question.getId()+":");

                    }
                });

                //   TableRow tbrow = new TableRow(_context);
               /* if(tbrow.getChildCount()==2)
                {
                    tbrow = new TableRow(_context);
                }*/
                //  tbrow.addView(checkBox);
                questionHolder.listcheckview.addView(checkBox);
            }


        } else if (type.equalsIgnoreCase("radio")) {


            String option = question.getOptions();
            String nativeoption = question.getLangOptions() != null ? question.getLangOptions() : question.getOptions();
            String[] options = option.split(",");
            String[] nativeoptions = nativeoption.split(",");
            List<Quesionviewspojo> myList = new ArrayList<>();
            if (options.length == nativeoptions.length) {
                for (int i = 0; i < options.length; i++) {
                    String name = options[i];
                    String locname = nativeoptions[i];
                    Quesionviewspojo pojo = new Quesionviewspojo(name, locname);
                    myList.add(pojo);


                }
            } else {

                for (int i = 0; i < options.length; i++) {
                    String name = options[i];
                    String locname = options[i];
                    Quesionviewspojo pojo = new Quesionviewspojo(name, locname);
                    myList.add(pojo);


                }

            }


            for (int i = 0; i < myList.size(); i++) {

                RadioButton radioButton = new RadioButton(_context);
                //  radioButton.setText(myList.get(i));
                if (sessionManager.getLanguagePosition() <= 1) {
                    //english

                    radioButton.setText(myList.get(i).getOption());
                } else {
                    //local
                    radioButton.setText(myList.get(i).getNativeOption());
                }

                radioButton.setTag(myList.get(i).getOption());
                radioButton.setPadding(0, 0, 40, 0);
                radioButton.setId(i);
                questionHolder.rgQuestionnew.addView(radioButton);


                for (int j = 0; j < answertemp.size(); j++) {
                    if (answertemp.get(question) != null && answertemp.get(question).getViewtype().equalsIgnoreCase("radio")) {
                        if (question.getId() == answertemp.get(question).getQuestion().getId() && radioButton.getId() == answertemp.get(question).getId() && answertemp.get(question).getViewtype().equalsIgnoreCase("radio")) {
                            radioButton.setChecked(true);
                        }
                    }
                }

            }
            questionHolder.rgQuestionnew.setVisibility(View.VISIBLE);


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
                    answers.put(question, data);
                }
            });


        } else {
            //text

            //   questionHolder.edittext.setVisibility(View.VISIBLE);
            //   questionHolder.edittext.setInputType(InputType.TYPE_CLASS_TEXT);
            //  questionHolder.edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});


            questionHolder.edittext.setVisibility(View.VISIBLE);
            questionHolder.edittext.setInputType(InputType.TYPE_CLASS_TEXT);
            questionHolder.edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});

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
                    answers.put(question, data);
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

        // questionHolder.rgQuestion = convertView.findViewById(R.id.rgQuestion);
        // set question id a key
        // questionHolder.rgQuestion.setTag(question.getId());
        questionHolder.rgQuestionnew.setTag(question.getId());
   /*     for (int i = 0; i < questionHolder.rgQuestion.getChildCount(); i++) {
            RadioButton rb = (RadioButton) questionHolder.rgQuestion.getChildAt(i);
            if (answers.get(question) == null) {
                if (rb.isChecked()) {
                    answers.put(question, radioButtons.get(rb.getId()));
                    Log.d("answers",answers.toString()+"-------ddddddd");
                }
            } else if (radioButtons.get(rb.getId()) == answers.get(question)) {
                rb.setChecked(true);
                Log.d("answers",answers.toString()+"-------");
            }
        }*/


        questionHolder.rgQuestionnew.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) result.findViewById(radioGroup.getCheckedRadioButtonId());
                Questiontemp questiontemp = new Questiontemp();
                questiontemp.setFlag(true);
                questiontemp.setId(rb.getId());
                questiontemp.setViewtype("radio");
                questiontemp.setQuestion(question);
                answertemp.put(question, questiontemp);
                answers.put(question, rb.getTag().toString());

                //  Toast.makeText(_context,  radioGroup.getCheckedRadioButtonId()+"",Toast.LENGTH_SHORT).show();

            }
        });


      /*  questionHolder.rgQuestion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = parent.findViewById(radioGroup.getCheckedRadioButtonId());
                answers.put(question, radioButtons.get(rb.getId()));
                //  Log.d("answers", answers + "-onchange");
            }
        });*/

        return convertView;
    }

}
