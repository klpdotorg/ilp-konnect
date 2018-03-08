package in.org.klp.ilpkonnect.SchoolDataPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature {

@SerializedName("geometry")
@Expose
private Geometry geometry;
@SerializedName("properties")
@Expose
private Properties properties;
@SerializedName("type")
@Expose
private String type;

public Geometry getGeometry() {
return geometry;
}

public void setGeometry(Geometry geometry) {
this.geometry = geometry;
}

public Properties getProperties() {
return properties;
}

public void setProperties(Properties properties) {
this.properties = properties;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

}