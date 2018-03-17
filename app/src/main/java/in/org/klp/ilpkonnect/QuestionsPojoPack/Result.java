package in.org.klp.ilpkonnect.QuestionsPojoPack;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("question_text")
    @Expose
    private String questionText;
    @SerializedName("display_text")
    @Expose
    private String displayText;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("question_type")
    @Expose
    private String questionType;

    @SerializedName("options")
    @Expose
    private List<String> options = null;

    public List<String> getLangOptions() {
        return langOptions;
    }

    public void setLangOptions(List<String> langOptions) {
        this.langOptions = langOptions;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }

    @SerializedName("lang_options")
    @Expose
    private List<String> langOptions = null;

    @SerializedName("is_featured")
    @Expose
    private Boolean isFeatured;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lang_name")
    @Expose
    private String langName;
    @SerializedName("sequence")
    @Expose
    private Integer sequence;

    public String getQuestionText() {
        return questionText;
    }



    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

}