package in.org.klp.ilpkonnect.InterfacesPack;

/**
 * Created by shridhars on 1/23/2018.
 */

public interface StateInterfaceSync {

    void success(String message);
    void failed(String message);
    void update (int count,String message);

}
