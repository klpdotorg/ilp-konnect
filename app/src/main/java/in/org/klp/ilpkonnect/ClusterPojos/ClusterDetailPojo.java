package in.org.klp.ilpkonnect.ClusterPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClusterDetailPojo {

@SerializedName("next")
@Expose
private String next;
@SerializedName("results")
@Expose
private List<Result> results = null;
@SerializedName("count")
@Expose
private Integer count;
@SerializedName("previous")
@Expose
private Object previous;

public String getNext() {
return next;
}

public void setNext(String next) {
this.next = next;
}

public List<Result> getResults() {
return results;
}

public void setResults(List<Result> results) {
this.results = results;
}

public Integer getCount() {
return count;
}

public void setCount(Integer count) {
this.count = count;
}

public Object getPrevious() {
return previous;
}

public void setPrevious(Object previous) {
this.previous = previous;
}

}