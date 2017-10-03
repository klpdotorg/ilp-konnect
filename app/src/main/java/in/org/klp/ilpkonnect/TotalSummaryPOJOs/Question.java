package in.org.klp.ilpkonnect.TotalSummaryPOJOs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Question {

@SerializedName("text")
@Expose
private String text;
@SerializedName("Yes")
@Expose
private Long yes;
@SerializedName("Don't Know")
@Expose
private Long donTKnow;
@SerializedName("id")
@Expose
private Long id;
@SerializedName("No")
@Expose
private Long no;

public String getText() {
return text;
}

public void setText(String text) {
this.text = text;
}

public Long getYes() {
return yes;
}

public void setYes(Long yes) {
this.yes = yes;
}

public Long getDonTKnow() {
return donTKnow;
}

public void setDonTKnow(Long donTKnow) {
this.donTKnow = donTKnow;
}

public Long getId() {
return id;
}

public void setId(Long id) {
this.id = id;
}

public Long getNo() {
return no;
}

public void setNo(Long no) {
this.no = no;
}

}