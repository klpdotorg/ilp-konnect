package in.org.klp.ilpkonnect.Pojo;

/**
 * Created by shridhars on 8/1/2017.
 */

public class SurveyMain {

    public long id;
    public String partener;
    public String community;
    public String communityLocal;

    public String getCommunityLocal() {
        return communityLocal;
    }

    public void setCommunityLocal(String communityLocal) {
        this.communityLocal = communityLocal;
    }

    public boolean isImageRequired() {
        return imageRequired;
    }

    public void setImageRequired(boolean imageRequired) {
        this.imageRequired = imageRequired;
    }

    public long getQuestiongroupId() {
        return questiongroupId;
    }

    public void setQuestiongroupId(long questiongroupId) {
        this.questiongroupId = questiongroupId;
    }

    public String getStateKey() {
        return stateKey;
    }

    public void setStateKey(String stateKey) {
        this.stateKey = stateKey;
    }

    public boolean imageRequired;
    public long questiongroupId;
    public String stateKey;

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
