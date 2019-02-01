package in.org.klp.ilpkonnect.InterfacesPack;

import in.org.klp.ilpkonnect.Pojo.ResetPasswordPojo;

/**
 * Created by shridhars on 1/23/2018.
 */

public interface RestPasswordStateInterface {

    void success(ResetPasswordPojo message);
    void failed(String message);

}
