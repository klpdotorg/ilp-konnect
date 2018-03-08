package in.org.klp.ilpkonnect.SchoolsPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.org.klp.ilpkonnect.SchoolsPojos.Geometry;

public class Feature {

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private Properties properties;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}