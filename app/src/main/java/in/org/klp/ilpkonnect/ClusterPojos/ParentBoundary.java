package in.org.klp.ilpkonnect.ClusterPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParentBoundary {

@SerializedName("id")
@Expose
private Long id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("parent")
@Expose
private Integer parent;
@SerializedName("dise_slug")
@Expose
private String diseSlug;
@SerializedName("boundary_type")
@Expose
private String boundaryType;
@SerializedName("type")
@Expose
private String type;
@SerializedName("status")
@Expose
private String status;

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

public Integer getParent() {
return parent;
}

public void setParent(Integer parent) {
this.parent = parent;
}

public String getDiseSlug() {
return diseSlug;
}

public void setDiseSlug(String diseSlug) {
this.diseSlug = diseSlug;
}

public String getBoundaryType() {
return boundaryType;
}

public void setBoundaryType(String boundaryType) {
this.boundaryType = boundaryType;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

}