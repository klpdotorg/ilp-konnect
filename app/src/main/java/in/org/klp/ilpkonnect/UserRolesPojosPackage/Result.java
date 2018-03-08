package in.org.klp.ilpkonnect.UserRolesPojosPackage;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("short_form")
    @Expose
    private String shortForm;
    @SerializedName("long_form")
    @Expose
    private String longForm;
    @SerializedName("site_url")
    @Expose
    private String siteUrl;
    @SerializedName("respondent_types")
    @Expose
    private List<RespondentType> respondentTypes = null;
    @SerializedName("home_logo")
    @Expose
    private String homeLogo;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("state_code")
    @Expose
    private String stateCode;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("header_logo")
    @Expose
    private String headerLogo;
    @SerializedName("footer_logo")
    @Expose
    private String footerLogo;


    @SerializedName("lang_name")
    @Expose
    private String langName;


    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    @SerializedName("lang_key")
    @Expose
    private String langKey;


    public String getShortForm() {
        return shortForm;
    }

    public void setShortForm(String shortForm) {
        this.shortForm = shortForm;
    }

    public String getLongForm() {
        return longForm;
    }

    public void setLongForm(String longForm) {
        this.longForm = longForm;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public List<RespondentType> getRespondentTypes() {
        return respondentTypes;
    }

    public void setRespondentTypes(List<RespondentType> respondentTypes) {
        this.respondentTypes = respondentTypes;
    }

    public String getHomeLogo() {
        return homeLogo;
    }

    public void setHomeLogo(String homeLogo) {
        this.homeLogo = homeLogo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeaderLogo() {
        return headerLogo;
    }

    public void setHeaderLogo(String headerLogo) {
        this.headerLogo = headerLogo;
    }

    public String getFooterLogo() {
        return footerLogo;
    }

    public void setFooterLogo(String footerLogo) {
        this.footerLogo = footerLogo;
    }

}