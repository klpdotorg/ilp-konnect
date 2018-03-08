package in.org.klp.ilpkonnect.DistrictPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetDistrictPojo {

@SerializedName("count")
@Expose
private Long count;
@SerializedName("next")
@Expose
private Object next;
@SerializedName("previous")
@Expose
private Object previous;
@SerializedName("features")
@Expose
private List<Feature> features = null;

public Long getCount() {
return count;
}

public void setCount(Long count) {
this.count = count;
}

public Object getNext() {
return next;
}

public void setNext(Object next) {
this.next = next;
}

public Object getPrevious() {
return previous;
}

public void setPrevious(Object previous) {
this.previous = previous;
}

public List<Feature> getFeatures() {
return features;
}

public void setFeatures(List<Feature> features) {
this.features = features;
}

}