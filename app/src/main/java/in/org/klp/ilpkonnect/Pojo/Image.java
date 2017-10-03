package in.org.klp.ilpkonnect.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

@SerializedName("url")
@Expose
private String url;
@SerializedName("date")
@Expose
private String date;

public String getUrl() {
return url;
}

public void setUrl(String url) {
this.url = url;
}

public String getDate() {
return date;
}

public void setDate(String date) {
this.date = date;
}

}