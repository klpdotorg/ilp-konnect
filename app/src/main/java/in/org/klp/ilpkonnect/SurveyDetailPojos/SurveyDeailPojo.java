package in.org.klp.ilpkonnect.SurveyDetailPojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyDeailPojo {

@SerializedName("next")
@Expose
private Object next;
@SerializedName("count")
@Expose
private Integer count;
@SerializedName("results")
@Expose
private List<Result> results = null;
@SerializedName("previous")
@Expose
private Object previous;

public Object getNext() {
return next;
}

public void setNext(Object next) {
this.next = next;
}

public Integer getCount() {
return count;
}

public void setCount(Integer count) {
this.count = count;
}

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

}