package in.org.klp.ilpkonnect.Pojo;

/**
 * Created by shridhars on 8/2/2017.
 */

public class LanguagePojo {

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getLanguageEng() {
        return languageEng;
    }

    public void setLanguageEng(String languageEng) {
        this.languageEng = languageEng;
    }

    public String getLanguageLoc() {
        return languageLoc;
    }

    public void setLanguageLoc(String languageLoc) {
        this.languageLoc = languageLoc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    long _id;

    public long get_stateid() {
        return _stateid;
    }

    public void set_stateid(long _stateid) {
        this._stateid = _stateid;
    }

    long _stateid;
    String languageEng;
    String languageLoc;
    String key;

    public LanguagePojo(long _stateid, String languageEng, String languageLoc, String key) {
        this._stateid = _stateid;
        this.languageEng = languageEng;
        this.languageLoc = languageLoc;
        this.key = key;
    }

    @Override
    public String toString() {
        return languageEng;
    }
}
