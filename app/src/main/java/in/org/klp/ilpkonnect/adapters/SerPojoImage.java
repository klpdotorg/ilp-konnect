package in.org.klp.ilpkonnect.adapters;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shridhars on 8/29/2017.
 */

public class SerPojoImage implements Serializable {

    private List<String> images = null;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }


}
