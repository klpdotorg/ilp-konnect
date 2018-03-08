package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/28/2017.
 */
@TableModelSpec(className = "Summmary", tableName = "summary")

public class
SummmarySpec {



        @PrimaryKey
        @ColumnSpec(name="_id")
        long Id;


        @ColumnSpec(name="bid")
        long bid;


        @ColumnSpec(name="groupid")
        public long groupid;


        @ColumnSpec(name="hierarchy")
        public String hierarchy;


        @ColumnSpec(name="total_school")
        public long total_school;

        @ColumnSpec(name="tot_sc_res")
        public long totalSchoolWithResponse;

        @ColumnSpec(name="tot_res")
        public long total_Response;

        @ColumnSpec(name="state_key")
        public String state_key;




    }
