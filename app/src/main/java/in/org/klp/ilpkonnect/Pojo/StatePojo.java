package in.org.klp.ilpkonnect.Pojo;

/**
 * Created by shridhars on 8/2/2017.
 */

public class StatePojo {

    public long _id;

    public StatePojo(long _id, String state, String stateLocalText) {
        this._id = _id;
        this.state = state;
        this.stateLocalText = stateLocalText;
    }

    public String state;
    public String stateLocalText;

    public long get_id() {
        return _id;
    }

    public String getState() {
        return state;
    }

    public String getStateLocalText() {
        return stateLocalText;
    }

    @Override
    public String toString() {
        return state ;
    }
}
