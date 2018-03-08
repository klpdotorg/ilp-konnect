package in.org.klp.ilpkonnect.SchoolDataPojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SchoolDataPojo {

@SerializedName("count")
@Expose
private Integer count;
@SerializedName("features")
@Expose
private List<Feature> features = null;
@SerializedName("previous")
@Expose
private String previous;
@SerializedName("next")
@Expose
private String next;
@SerializedName("type")
@Expose
private String type;

public Integer getCount() {
return count;
}

public void setCount(Integer count) {
this.count = count;
}

public List<Feature> getFeatures() {
return features;
}

public void setFeatures(List<Feature> features) {
this.features = features;
}

public String getPrevious() {
return previous;
}

public void setPrevious(String previous) {
this.previous = previous;
}

public String getNext() {
return next;
}

public void setNext(String next) {
this.next = next;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

}