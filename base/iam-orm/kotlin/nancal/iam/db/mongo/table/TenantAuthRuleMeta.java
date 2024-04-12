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

public class TenantAuthRuleMeta extends MongoColumnName{
    private String parentPropertyName;
    public TenantAuthRuleMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.parentId = MoerUtil.mongoColumnJoin(this.parentPropertyName, "parentId");

    this.parentCodeName = MoerUtil.mongoColumnJoin(this.parentPropertyName, "parentCodeName");

    this.rulesCodeVoSon = new RulesCodeVoSonMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "rulesCodeVoSon"));

    this.conditionValue = MoerUtil.mongoColumnJoin(this.parentPropertyName, "conditionValue");

    this.sort = MoerUtil.mongoColumnJoin(this.parentPropertyName, "sort");

    this.conditionType = MoerUtil.mongoColumnJoin(this.parentPropertyName, "conditionType");
    }
    
    public TenantAuthRuleMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName parentId = null;
    public MongoColumnName getParentId(){
        return parentId;
    }

    private MongoColumnName parentCodeName = null;
    public MongoColumnName getParentCodeName(){
        return parentCodeName;
    }

    private RulesCodeVoSonMeta rulesCodeVoSon = null;
    public RulesCodeVoSonMeta getRulesCodeVoSon(){
        return rulesCodeVoSon;
    }

    private MongoColumnName conditionValue = null;
    public MongoColumnName getConditionValue(){
        return conditionValue;
    }

    private MongoColumnName sort = null;
    public MongoColumnName getSort(){
        return sort;
    }

    private MongoColumnName conditionType = null;
    public MongoColumnName getConditionType(){
        return conditionType;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

