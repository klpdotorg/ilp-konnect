package in.org.klp.ilpkonnect.Pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegstrationResponsePojo {




        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("last_login")
        @Expose
        private Object lastLogin;
        @SerializedName("is_superuser")
        @Expose
        private Boolean isSuperuser;
        @SerializedName("email")
        @Expose
        private String email;
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
        @SerializedName("is_active")
        @Expose
        private Boolean isActive;
        @SerializedName("email_verification_code")
        @Expose
        private String emailVerificationCode;
        @SerializedName("sms_verification_pin")
        @Expose
        private Integer smsVerificationPin;
        @SerializedName("is_email_verified")
        @Expose
        private Boolean isEmailVerified;
        @SerializedName("is_mobile_verified")
        @Expose
        private Boolean isMobileVerified;
        @SerializedName("dob")
        @Expose
        private Object dob;
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
        private Object about;
        @SerializedName("twitter_handle")
        @Expose
        private Object twitterHandle;
        @SerializedName("fb_url")
        @Expose
        private Object fbUrl;
        @SerializedName("website")
        @Expose
        private Object website;
        @SerializedName("photos_url")
        @Expose
        private Object photosUrl;
        @SerializedName("youtube_url")
        @Expose
        private Object youtubeUrl;
        @SerializedName("user_type")
        @Expose
        private String userType;
        @SerializedName("groups")
        @Expose
        private List<Object> groups = null;
        @SerializedName("user_permissions")
        @Expose
        private List<Object> userPermissions = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Object getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(Object lastLogin) {
            this.lastLogin = lastLogin;
        }

        public Boolean getIsSuperuser() {
            return isSuperuser;
        }

        public void setIsSuperuser(Boolean isSuperuser) {
            this.isSuperuser = isSuperuser;
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

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public String getEmailVerificationCode() {
            return emailVerificationCode;
        }

        public void setEmailVerificationCode(String emailVerificationCode) {
            this.emailVerificationCode = emailVerificationCode;
        }

        public Integer getSmsVerificationPin() {
            return smsVerificationPin;
        }

        public void setSmsVerificationPin(Integer smsVerificationPin) {
            this.smsVerificationPin = smsVerificationPin;
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

        public Object getDob() {
            return dob;
        }

        public void setDob(Object dob) {
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

        public Object getAbout() {
            return about;
        }

        public void setAbout(Object about) {
            this.about = about;
        }

        public Object getTwitterHandle() {
            return twitterHandle;
        }

        public void setTwitterHandle(Object twitterHandle) {
            this.twitterHandle = twitterHandle;
        }

        public Object getFbUrl() {
            return fbUrl;
        }

        public void setFbUrl(Object fbUrl) {
            this.fbUrl = fbUrl;
        }

        public Object getWebsite() {
            return website;
        }

        public void setWebsite(Object website) {
            this.website = website;
        }

        public Object getPhotosUrl() {
            return photosUrl;
        }

        public void setPhotosUrl(Object photosUrl) {
            this.photosUrl = photosUrl;
        }

        public Object getYoutubeUrl() {
            return youtubeUrl;
        }

        public void setYoutubeUrl(Object youtubeUrl) {
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

    }














