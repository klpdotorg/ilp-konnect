package in.org.klp.ilpkonnect.BlocksPojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetBlockPojo {

@SerializedName("count")
@Expose
private Long count;
@SerializedName("next")
@Expose
private String next;
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

public String getNext() {
return next;
}

public void setNext(String next) {
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