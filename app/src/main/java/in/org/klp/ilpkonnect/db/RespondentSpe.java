package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/2/2017.
 */


@TableModelSpec(className = "Respondent", tableName = "RESPONDENT")
public class RespondentSpe {
    @PrimaryKey
    @ColumnSpec(name = "_id")
    long Id;

    @ColumnSpec(name = "name")
    public String name;

    @ColumnSpec(name = "nam_loc")
    public String nam_loc;

    @ColumnSpec(name = "key")
    public String key;

    @ColumnSpec(name="state_key")
    public String state_key;

}

