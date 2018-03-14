package in.org.klp.ilpkonnect.SurveyAndQuestionGPojoPsck;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Questiongroup {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("survey")
    @Expose
    private Integer survey;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("inst_type")
    @Expose
    private String instType;
    @SerializedName("group_text")
    @Expose
    private String groupText;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private Object endDate;
    @SerializedName("academic_year")
    @Expose
    private Object academicYear;
    @SerializedName("version")
    @Expose
    private Object version;
    @SerializedName("source")
    @Expose
    private Integer source;
    @SerializedName("source_name")
    @Expose
    private String sourceName;
    @SerializedName("double_entry")
    @Expose
    private Boolean doubleEntry;
    @SerializedName("created_by")
    @Expose
    private Object createdBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("image_required")
    @Expose
    private Boolean imageRequired;
    @SerializedName("comments_required")
    @Expose
    private Boolean commentsRequired;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSurvey() {
        return survey;
    }

    public void setSurvey(Integer survey) {
        this.survey = survey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInstType() {
        return instType;
    }

    public void setInstType(String instType) {
        this.instType = instType;
    }

    public String getGroupText() {
        return groupText;
    }

    public void setGroupText(String groupText) {
        this.groupText = groupText;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

    public Object getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Object academicYear) {
        this.academicYear = academicYear;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Boolean getDoubleEntry() {
        return doubleEntry;
    }

    public void setDoubleEntry(Boolean doubleEntry) {
        this.doubleEntry = doubleEntry;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getImageRequired() {
        return imageRequired;
    }

    public void setImageRequired(Boolean imageRequired) {
        this.imageRequired = imageRequired;
    }

    public Boolean getCommentsRequired() {
        return commentsRequired;
    }

    public void setCommentsRequired(Boolean commentsRequired) {
        this.commentsRequired = commentsRequired;
    }

}