package in.org.klp.ilpkonnect.Pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponsePojo {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("is_email_verified")
@Expose
private Boolean isEmailVerified;
@SerializedName("is_mobile_verified")
@Expose
private Boolean isMobileVerified;
@SerializedName("is_active")
@Expose
private Boolean isActive;
@SerializedName("last_login")
@Expose
private String lastLogin;
@SerializedName("is_superuser")
@Expose
private Boolean isSuperuser;
@SerializedName("email")
@Expose
private Object email;
@SerializedName("mobile_no")
@Expose
private String mobileNo;
@SerializedName("mobile_no1")
@Expose
private Object mobileNo1;
@SerializedName("first_name")
@Expose
private String firstName;
@SerializedName("last_name")
@Expose
private String lastName;
@SerializedName("dob")
@Expose
private String dob;
@SerializedName("source")
@Expose
private String source;
@SerializedName("changed")
@Expose
private String changed;
@SerializedName("created")
@Expose
private String created;
@SerializedName("opted_email")
@Expose
private Boolean optedEmail;
@SerializedName("image")
@Expose
private Object image;
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
@SerializedName("user_type")
@Expose
private String userType;
@SerializedName("groups")
@Expose
private List<Object> groups = null;
@SerializedName("user_permissions")
@Expose
private List<Object> userPermissions = null;
@SerializedName("token")
@Expose
private String token;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Boolean getIsEmailVerified() {
return isEmailVerified;
}

public void setIsEmailVerified(Boolean isEmailVerified) {
this.isEmailVerified = isEmailVerified;
}

public Boolean getIsMobileVerified() {
return isMobileVerified;
}

public void setIsMobileVerified(Boolean isMobileVerified) {
this.isMobileVerified = isMobileVerified;
}

public Boolean getIsActive() {
return isActive;
}

public void setIsActive(Boolean isActive) {
this.isActive = isActive;
}

public String getLastLogin() {
return lastLogin;
}

public void setLastLogin(String lastLogin) {
this.lastLogin = lastLogin;
}

public Boolean getIsSuperuser() {
return isSuperuser;
}

public void setIsSuperuser(Boolean isSuperuser) {
this.isSuperuser = isSuperuser;
}

public Object getEmail() {
return email;
}

public void setEmail(Object email) {
this.email = email;
}

public String getMobileNo() {
return mobileNo;
}

public void setMobileNo(String mobileNo) {
this.mobileNo = mobileNo;
}

public Object getMobileNo1() {
return mobileNo1;
}

public void setMobileNo1(Object mobileNo1) {
this.mobileNo1 = mobileNo1;
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

public String getChanged() {
return changed;
}

public void setChanged(String changed) {
this.changed = changed;
}

public String getCreated() {
return created;
}

public void setCreated(String created) {
this.created = created;
}

public Boolean getOptedEmail() {
return optedEmail;
}

public void setOptedEmail(Boolean optedEmail) {
this.optedEmail = optedEmail;
}

public Object getImage() {
return image;
}

public void setImage(Object image) {
this.image = image;
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

public String getUserType() {
return userType;
}

public void setUserType(String userType) {
this.userType = userType;
}

public List<Object> getGroups() {
return groups;
}

public void setGroups(List<Object> groups) {
this.groups = groups;
}

public List<Object> getUserPermissions() {
return userPermissions;
}

public void setUserPermissions(List<Object> userPermissions) {
this.userPermissions = userPermissions;
}

public String getToken() {
return token;
}

public void setToken(String token) {
this.token = token;
}

}