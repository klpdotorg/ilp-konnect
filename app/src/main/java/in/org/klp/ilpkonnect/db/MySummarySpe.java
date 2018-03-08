package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 1/30/2018.
 */




    @TableModelSpec(className = "MySummary", tableName = "mysummary")
    public class MySummarySpe {
    @PrimaryKey
    @ColumnSpec(name="_id")
    long Id;

    @ColumnSpec(name="surveyid")
    long surveyid;



        @ColumnSpec(name="surveyname")
        public String surveyname ;


        @ColumnSpec(name="surveysynced")
        public long surveysynced ;

        @ColumnSpec(name="schoolsurveyed")
        public long schoolsurveyed;

        @ColumnSpec(name="pendingsync")
        public long pendingsync ;

        @ColumnSpec(name="fromdate")
        public long fromdate ;

        @ColumnSpec(name="enddate")
        public long enddate ;


    @ColumnSpec(name="state_key")
    public String state_key;

}





