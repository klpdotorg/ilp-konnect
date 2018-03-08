package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/2/2017.
 */


@TableModelSpec(className = "State", tableName = "state")
public class StateSpec {
    @PrimaryKey
    @ColumnSpec(name = "_id")
    long Id;

    public String state;

    public String stateLocText;

    public String statekey;


    public String langName;

    public String langKey;

}

