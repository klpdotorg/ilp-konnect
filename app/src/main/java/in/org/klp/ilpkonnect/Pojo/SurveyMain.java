package in.org.klp.ilpkonnect.Pojo;

/**
 * Created by shridhars on 8/1/2017.
 */

public class SurveyMain {

    public long id;
    public String partener;
    public String community;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPartener() {
        return partener;
    }

    public void setPartener(String partener) {
        this.partener = partener;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    @Override
    public String toString() {
        return partener;
    }
}
