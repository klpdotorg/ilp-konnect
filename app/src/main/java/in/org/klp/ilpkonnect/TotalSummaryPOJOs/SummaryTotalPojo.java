package in.org.klp.ilpkonnect.TotalSummaryPOJOs;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SummaryTotalPojo {


    @SerializedName("no_of_schools")
    @Expose
    private Long noOfSchools;
    @SerializedName("no_of_schools_with_responses")
    @Expose
    private Long noOfSchoolsWithResponses;

    @SerializedName("no_of_responses")
    @Expose
    private Long noOfResponses;


    @SerializedName("questions")
    @Expose
    private List<Question> questions = null;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setNoOfSchools(Long noOfSchools) {
        this.noOfSchools = noOfSchools;
    }

    public Long getNoOfSchoolsWithResponses() {
        return noOfSchoolsWithResponses;
    }

    public void setNoOfSchoolsWithResponses(Long noOfSchoolsWithResponses) {
        this.noOfSchoolsWithResponses = noOfSchoolsWithResponses;
    }

    public Long getNoOfResponses() {
        return noOfResponses;
    }

    public void setNoOfResponses(Long noOfResponses) {
        this.noOfResponses = noOfResponses;
    }


    public Long getNoOfSchools() {
        return noOfSchools;
    }
}