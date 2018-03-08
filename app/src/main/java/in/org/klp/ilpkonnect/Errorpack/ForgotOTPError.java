package in.org.klp.ilpkonnect.Errorpack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shridhars on 2/1/2018.
 */

public class ForgotOTPError {
    @SerializedName("detail")
    @Expose
    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
