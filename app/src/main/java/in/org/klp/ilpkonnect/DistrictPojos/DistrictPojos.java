package in.org.klp.ilpkonnect.DistrictPojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistrictPojos {

@SerializedName("next")
@Expose
private String next;
@SerializedName("previous")
@Expose
private Object previous;
@SerializedName("results")
@Expose
private List<Result> results = null;
@SerializedName("count")
@Expose
private Integer count;

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

}