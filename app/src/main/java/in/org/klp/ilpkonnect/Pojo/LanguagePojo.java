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

    public String get_stateKey() {
        return _stateKey;
    }

    public void set_stateKey(String _stateKey) {
        this._stateKey = _stateKey;
    }

    String _stateKey;
    String languageEng;
    String languageLoc;
    String key;

    public LanguagePojo(String _stateKey, String languageEng, String languageLoc, String key) {
        this._stateKey = _stateKey;
        this.languageEng = languageEng;
        this.languageLoc = languageLoc;
        this.key = key;
    }

    @Override
    public String toString() {
        return languageEng;
    }
}
