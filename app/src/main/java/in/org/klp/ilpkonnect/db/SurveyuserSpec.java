package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on 6/17/16.
 */
@TableModelSpec(className = "Surveyuser", tableName = "surveyUser")
public class SurveyuserSpec {
    @PrimaryKey
    @ColumnSpec(name = "_id")
    long Id;



    @ColumnSpec(name = "surveyid")
    public long surveyid;

    @ColumnSpec(name = "name")
    public String name;



}