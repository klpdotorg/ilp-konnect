package in.org.klp.ilpkonnect.RepondentPack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

@SerializedName("char_id")
@Expose
private String charId;
@SerializedName("name")
@Expose
private String name;

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

}