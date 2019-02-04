package in.org.klp.ilpkonnect.adapters;

import in.org.klp.ilpkonnect.db.Question;

/**
 * Created by shridhars on 3/15/2018.
 */

class Questiontemp {

    String viewtype;
    Question question;

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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    boolean flag;
    String value;
    int id;


}
