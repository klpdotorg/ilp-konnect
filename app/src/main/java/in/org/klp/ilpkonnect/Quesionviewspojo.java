package in.org.klp.ilpkonnect;

/**
 * Created by shridhars on 3/17/2018.
 */

public class Quesionviewspojo {

    String option;
    int position;

    public Quesionviewspojo(String option, String nativeOption,int position) {
        this.option = option;
        this.nativeOption = nativeOption;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    String nativeOption;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getNativeOption() {
        return nativeOption;
    }

    public void setNativeOption(String nativeOption) {
        this.nativeOption = nativeOption;
    }

    @Override
    public String toString() {

        if(position<=1) {
            return option;
        }else {
            return nativeOption;
        }
    }
}
