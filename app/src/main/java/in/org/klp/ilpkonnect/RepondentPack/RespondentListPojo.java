package in.org.klp.ilpkonnect.RepondentPack;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespondentListPojo {

@SerializedName("results")
@Expose
private List<Result> results = null;
@SerializedName("previous")
@Expose
private Object previous;
@SerializedName("count")
@Expose
private Integer count;
@SerializedName("next")
@Expose
private String next;

public List<Result> getResults() {
return results;
}

public void setResults(List<Result> results) {
this.results = results;
}

public Object getPrevious() {
return previous;
}

public void setPrevious(Object previous) {
this.previous = previous;
}

public Integer getCount() {
return count;
}

public void setCount(Integer count) {
this.count = count;
}

public String getNext() {
return next;
}

public void setNext(String next) {
this.next = next;
}

}