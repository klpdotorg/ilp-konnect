package in.org.klp.ilpkonnect.adapters;

import java.util.HashMap;

import in.org.klp.ilpkonnect.db.Question;

/**
 * Created by shridhars on 3/15/2018.
 */

class QuestionTempForCheck {

    String viewtype;
    Question question;

    public HashMap<Integer, Boolean> getHaspmaplist() {
        return haspmaplist;
    }

    public void setHaspmaplist(HashMap<Integer, Boolean> haspmaplist) {
        this.haspmaplist = haspmaplist;
    }

    HashMap<Integer, Boolean> haspmaplist;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getViewtype() {
        return viewtype;
    }

    public void setViewtype(String viewtype) {
        this.viewtype = viewtype;
    }



    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    String value;
    long id;


}
