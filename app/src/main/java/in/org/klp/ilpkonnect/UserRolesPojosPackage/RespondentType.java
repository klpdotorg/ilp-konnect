package in.org.klp.ilpkonnect.UserRolesPojosPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespondentType {

@SerializedName("char_id")
@Expose
private String charId;
@SerializedName("name")
@Expose
private String name;
@SerializedName("state_code")
@Expose
private Object stateCode;

public String getCharId() {
return charId;
}

public void setCharId(String charId) {
this.charId = charId;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public Object getStateCode() {
return stateCode;
}

public void setStateCode(Object stateCode) {
this.stateCode = stateCode;
}

}