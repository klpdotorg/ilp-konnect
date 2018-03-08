package in.org.klp.ilpkonnect.Errorpack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvalidOTp {

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