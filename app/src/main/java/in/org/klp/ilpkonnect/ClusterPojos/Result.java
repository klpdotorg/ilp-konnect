package in.org.klp.ilpkonnect.ClusterPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("dise_slug")
@Expose
private String diseSlug;
@SerializedName("type")
@Expose
private String type;
@SerializedName("boundary_type")
@Expose
private String boundaryType;
@SerializedName("parent_boundary")
@Expose
private ParentBoundary parentBoundary;

    @SerializedName("lang_name")
    @Expose
    private String langName;


    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }











    public Integer getId() {
return id;
}

public void setId(Integer id) {
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

public String getBoundaryType() {
return boundaryType;
}

public void setBoundaryType(String boundaryType) {
this.boundaryType = boundaryType;
}

public ParentBoundary getParentBoundary() {
return parentBoundary;
}

public void setParentBoundary(ParentBoundary parentBoundary) {
this.parentBoundary = parentBoundary;
}

}