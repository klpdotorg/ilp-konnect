package in.org.klp.ilpkonnect.SchoolsPojos;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("boundary")
    @Expose
    private Boundary boundary;
    @SerializedName("admin1")
    @Expose
    private String admin1;
    @SerializedName("admin2")
    @Expose
    private String admin2;
    @SerializedName("admin3")
    @Expose
    private String admin3;
    @SerializedName("address_full")
    @Expose
    private String addressFull;
    @SerializedName("dise_info")
    @Expose
    private String diseInfo;
    @SerializedName("type")
    @Expose
    private Type type;
    @SerializedName("meeting_reports")
    @Expose
    private List<Object> meetingReports = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }

    public String getAdmin1() {
        return admin1;
    }

    public void setAdmin1(String admin1) {
        this.admin1 = admin1;
    }

    public String getAdmin2() {
        return admin2;
    }

    public void setAdmin2(String admin2) {
        this.admin2 = admin2;
    }

    public String getAdmin3() {
        return admin3;
    }

    public void setAdmin3(String admin3) {
        this.admin3 = admin3;
    }

    public String getAddressFull() {
        return addressFull;
    }

    public void setAddressFull(String addressFull) {
        this.addressFull = addressFull;
    }

    public String getDiseInfo() {
        return diseInfo;
    }

    public void setDiseInfo(String diseInfo) {
        this.diseInfo = diseInfo;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Object> getMeetingReports() {
        return meetingReports;
    }

    public void setMeetingReports(List<Object> meetingReports) {
        this.meetingReports = meetingReports;
    }

}