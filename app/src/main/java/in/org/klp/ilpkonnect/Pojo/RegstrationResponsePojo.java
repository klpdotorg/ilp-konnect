package in.org.klp.ilpkonnect.Pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegstrationResponsePojo {



    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("opted_email")
    @Expose
    private Boolean optedEmail;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("volunteer_activities")
    @Expose
    private List<Object> volunteerActivities = null;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("organizations")
    @Expose
    private List<Object> organizations = null;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("twitter_handle")
    @Expose
    private String twitterHandle;
    @SerializedName("fb_url")
    @Expose
    private String fbUrl;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("photos_url")
    @Expose
    private String photosUrl;
    @SerializedName("youtube_url")
    @Expose
    private String youtubeUrl;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("source")
    @Expose
    private String source;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Boolean getOptedEmail() {
        return optedEmail;
    }

    public void setOptedEmail(Boolean optedEmail) {
        this.optedEmail = optedEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Object> getVolunteerActivities() {
        return volunteerActivities;
    }

    public void setVolunteerActivities(List<Object> volunteerActivities) {
        this.volunteerActivities = volunteerActivities;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Object> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Object> organizations) {
        this.organizations = organizations;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public String getFbUrl() {
        return fbUrl;
    }

    public void setFbUrl(String fbUrl) {
        this.fbUrl = fbUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhotosUrl() {
        return photosUrl;
    }

    public void setPhotosUrl(String photosUrl) {
        this.photosUrl = photosUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    }












