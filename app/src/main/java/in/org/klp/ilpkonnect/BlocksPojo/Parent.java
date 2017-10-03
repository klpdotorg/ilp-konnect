package in.org.klp.ilpkonnect.BlocksPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parent {

@SerializedName("id")
@Expose
private Long id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("dise_slug")
@Expose
private String diseSlug;
@SerializedName("type")
@Expose
private String type;
@SerializedName("school_type")
@Expose
private String schoolType;
@SerializedName("status")
@Expose
private Long status;

public Long getId() {
return id;
}

public void setId(Long id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getDiseSlug() {
return diseSlug;
}

public void setDiseSlug(String diseSlug) {
this.diseSlug = diseSlug;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public String getSchoolType() {
return schoolType;
}

public void setSchoolType(String schoolType) {
this.schoolType = schoolType;
}

public Long getStatus() {
return status;
}

public void setStatus(Long status) {
this.status = status;
}

}