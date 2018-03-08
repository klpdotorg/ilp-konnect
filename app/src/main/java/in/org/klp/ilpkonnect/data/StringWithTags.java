package in.org.klp.ilpkonnect.data;

import in.org.klp.ilpkonnect.utils.SessionManager;

/**
 * Created by deviprasad on 20/6/16.
 */
public class StringWithTags {
    public String string;
    public Object id, parent;
    public boolean type;
    public boolean flag;
    public boolean flagCb;
    String loc_name;
    SessionManager sessionManager;
   /* public double lat;
    public double lng;*/

    public StringWithTags(String stringPart, Object idpart, Object parentpart,String loc_name,SessionManager sessionManager,boolean flag, boolean flagCb) {
        string = stringPart;
        id = idpart;
        parent = parentpart;
        type = false;
        this.loc_name=loc_name;
        this.flag=flag;
        this.flagCb=flagCb;
        this.sessionManager=sessionManager;
    }




 public StringWithTags(String stringPart, Object idpart, Object parentpart, boolean includeid,SessionManager sessionManager) {
      //school
        string = stringPart;
        id = idpart;
        parent = parentpart;
        type = includeid;
        this.sessionManager=sessionManager;


    }

  /*  @Override
    public String toString() {
        if (type)
            return String.valueOf(id) + " : " + string;
        else
            return string;
    }*/

    @Override
    public String toString() {
        if (type) {
            //school
            return string;
        }
        else {
            //boundary
            if(sessionManager.getLanguagePosition()<=1)
            {
                return string;
                //english
            }else {
                if(loc_name==null)
                {
                    return string;
                }
               return loc_name;
            }
        }
    }
}
