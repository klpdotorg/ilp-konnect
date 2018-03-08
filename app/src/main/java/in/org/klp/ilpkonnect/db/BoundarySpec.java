package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by bibhas on 6/17/16.
 */
@TableModelSpec(className = "Boundary", tableName = "boundary")
public class BoundarySpec {
    @PrimaryKey
    @ColumnSpec(name="_id")
    long Id;

    @ColumnSpec(name="boundary_id")
    public long parent_id;

    @ColumnSpec(name="name")
    public String name;

    @ColumnSpec(name="loc_name")
    public String loc_name;
  @ColumnSpec(name="state_key")
    public String state_key;

    public String hierarchy;
    public String type;

    @ColumnSpec(name="flag")
    public boolean flag;

    @ColumnSpec(name="flagCB")
    public boolean flagCB;
}