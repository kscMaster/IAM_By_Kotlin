package nancal.iam.db.mongo.table;

import nbcp.db.*;
import nbcp.db.mongo.*;
import nbcp.utils.*;
import nbcp.comm.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import nancal.iam.db.mongo.*;

//generate auto @2022-08-11 11:01:01

public class RulesCodeVoSonMeta extends MongoColumnName{
    private String parentPropertyName;
    public RulesCodeVoSonMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.codeId = MoerUtil.mongoColumnJoin(this.parentPropertyName, "codeId");

    this.codeName = MoerUtil.mongoColumnJoin(this.parentPropertyName, "codeName");
    }
    
    public RulesCodeVoSonMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName codeId = null;
    public MongoColumnName getCodeId(){
        return codeId;
    }

    private MongoColumnName codeName = null;
    public MongoColumnName getCodeName(){
        return codeName;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

