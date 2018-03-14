package in.org.klp.ilpkonnect.SurveyAndQuestionGPojoPsck;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserType {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("survey")
@Expose
private Integer survey;
@SerializedName("usertype")
@Expose
private String usertype;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Integer getSurvey() {
return survey;
}

public void setSurvey(Integer survey) {
this.survey = survey;
}

public String getUsertype() {
return usertype;
}

public void setUsertype(String usertype) {
this.usertype = usertype;
}

}