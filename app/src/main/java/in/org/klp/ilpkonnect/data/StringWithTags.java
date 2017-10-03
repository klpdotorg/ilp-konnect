package in.org.klp.ilpkonnect.data;

/**
 * Created by deviprasad on 20/6/16.
 */
public class StringWithTags {
    public String string;
    public Object id, parent;
    public boolean type;
    public double lat;
    public double lng;

    public StringWithTags(String stringPart, Object idpart, Object parentpart) {
        string = stringPart;
        id = idpart;
        parent = parentpart;
        type = false;
    }
    public StringWithTags(String stringPart, Object idpart, Object parentpart, boolean includeid,double lat,double lng) {
        string = stringPart;
        id = idpart;
        parent = parentpart;
        type = includeid;
        this.lat=lat;
        this.lng=lng;

    }

    @Override
    public String toString() {
        if (type)
            return String.valueOf(id) + " : " + string;
        else
            return string;
    }
}
