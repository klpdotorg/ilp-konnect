package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/28/2017.
 */
@TableModelSpec(className = "SummaryInfo", tableName = "summaryInfo")
public class SummaryInfoSpe {

    @PrimaryKey
    @ColumnSpec(name="_id")
    long Id;
    @ColumnSpec(name="yes")
    long yes;

    @ColumnSpec(name="NO")
    long no;

    @ColumnSpec(name="dontknow")
    long dontknow;

    @ColumnSpec(name="qid")
    public long qid;

    @ColumnSpec(name="groupid")
    public long groupid;

    @ColumnSpec(name="bid")
    public long bid;

    @ColumnSpec(name="hierarchy")
    public String hierarchy;


    @ColumnSpec(name="state_key")
    public String state_key;






}
