package in.org.klp.ilpkonnect.FaqPackage;

/**
 * Created by shridhars on 3/6/2018.
 */

public class FaqPojo {

    public String faqQuestion;

    public FaqPojo(String faqQuestion, String faqAnswer) {
        this.faqQuestion = faqQuestion;
        this.faqAnswer = faqAnswer;
    }

    public String getFaqQuestion() {
        return faqQuestion;
    }

    public String getFaqAnswer() {
        return faqAnswer;
    }

    public String faqAnswer;
}
