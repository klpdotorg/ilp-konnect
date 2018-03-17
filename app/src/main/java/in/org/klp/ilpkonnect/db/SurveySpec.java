package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/2/2017.
 */
@TableModelSpec(className = "Survey", tableName = "survey")
public class SurveySpec {
    @PrimaryKey
    @ColumnSpec(name = "_id")
    long Id;

    public String partner;

    @ColumnSpec(name = "name")
    public String name;

    @ColumnSpec(name = "name_loc")
    public String name_loc;

    @ColumnSpec(name = "question_group_id")
    public long question_group_id;


    @ColumnSpec(name = "image_required")
    public boolean imageRequired;


    @ColumnSpec(name = "comment_required")
    public boolean commentRequired;



    @ColumnSpec(name = "respondent_required")
    public boolean respondentRequired;


    @ColumnSpec(name = "grade_required")
    public String gradeRequired;

    @ColumnSpec(name = "state_key")
    public String state_key;

}