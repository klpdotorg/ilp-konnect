package in.org.klp.ilpkonnect.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgotPasswordPojo {

@SerializedName("error")
@Expose
private String error;
@SerializedName("success")
@Expose
private String success;

public String getError() {
return error;
}

public void setError(String error) {
this.error = error;
}

public String getSuccess() {
return success;
}

public void setSuccess(String success) {
this.success = success;
}

}