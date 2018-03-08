package in.org.klp.ilpkonnect.Pojo;

/**
 * Created by shridhars on 8/2/2017.
 */

public class StatePojo {



    public String getStateKey() {
        return stateKey;
    }

    String stateKey;
    public String langKey;

    public String getLangKey() {
        return langKey;
    }

    public String getLanguage() {
        return language;
    }

    public String language;


    public StatePojo(String state, String stateLocalText,String stateKey,String langKey,String language) {
         this.state = state;
        this.stateLocalText = stateLocalText;
        this.stateKey = stateKey;
        this.langKey=langKey;
        this.language=language;
    }

    public String state;
    public String stateLocalText;



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
