package in.org.klp.ilpkonnect.SurveyDetailPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("created_at")
@Expose
private String createdAt;
@SerializedName("updated_at")
@Expose
private Object updatedAt;
@SerializedName("partner")
@Expose
private String partner;
@SerializedName("description")
@Expose
private Object description;
@SerializedName("status")
@Expose
private String status;

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

public String getCreatedAt() {
return createdAt;
}

public void setCreatedAt(String createdAt) {
this.createdAt = createdAt;
}

public Object getUpdatedAt() {
return updatedAt;
}

public void setUpdatedAt(Object updatedAt) {
this.updatedAt = updatedAt;
}

public String getPartner() {
return partner;
}

public void setPartner(String partner) {
this.partner = partner;
}

public Object getDescription() {
return description;
}

public void setDescription(Object description) {
this.description = description;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

}