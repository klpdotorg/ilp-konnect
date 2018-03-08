package in.org.klp.ilpkonnect.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shridhars on 1/31/2018.
 */

class MyErrorMessage {

    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("non_field_errors")
    @Expose
    private List<String> nonFieldErrors = null;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getNonFieldErrors() {
        return nonFieldErrors;
    }

    public void setNonFieldErrors(List<String> nonFieldErrors) {
        this.nonFieldErrors = nonFieldErrors;
    }


}
