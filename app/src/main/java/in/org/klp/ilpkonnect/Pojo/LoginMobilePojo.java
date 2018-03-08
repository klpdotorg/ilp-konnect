package in.org.klp.ilpkonnect.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginMobilePojo {

@SerializedName("action")
@Expose
private String action;

/**
* No args constructor for use in serialization
* 
*/
public LoginMobilePojo() {
}

/**
* 
* @param action
*/
public LoginMobilePojo(String action) {
super();
this.action = action;
}

public String getAction() {
return action;
}

public void setAction(String action) {
this.action = action;
}

}