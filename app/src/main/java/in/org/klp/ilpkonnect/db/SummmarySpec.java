package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/28/2017.
 */
@TableModelSpec(className = "Summmary", tableName = "summary")

public class SummmarySpec {



        @PrimaryKey
        @ColumnSpec(name="_id")
        long Id;


        @ColumnSpec(name="hierarchy")
        public String hierarchy;








    }
