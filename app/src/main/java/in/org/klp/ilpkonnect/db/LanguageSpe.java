package in.org.klp.ilpkonnect.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/2/2017.
 */



    @TableModelSpec(className = "Language", tableName = "language")
    public class LanguageSpe {
        @PrimaryKey
        @ColumnSpec(name="_id")
        long Id;

        public String languageENG;
        public String languageLoc;
        public long stateId;
        public String langKey;

    }

