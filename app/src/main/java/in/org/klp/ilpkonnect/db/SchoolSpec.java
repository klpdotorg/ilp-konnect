package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by bibhas on 6/17/16.
 */
@TableModelSpec(className = "School", tableName = "school")
public class SchoolSpec {
    @PrimaryKey
    @ColumnSpec(name="_id")
    long Id;

    @ColumnSpec(name="boundary_id")
    public long boundary_id;

    public String name;

    @ColumnSpec(name="loc_name")
    public String loc_name;

    @ColumnSpec(name="dise")
    public String dise;
}