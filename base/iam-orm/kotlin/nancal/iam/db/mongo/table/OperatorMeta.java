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

public class OperatorMeta extends MongoColumnName{
    private String parentPropertyName;
    public OperatorMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.name = MoerUtil.mongoColumnJoin(this.parentPropertyName, "name");

    this.key = MoerUtil.mongoColumnJoin(this.parentPropertyName, "key");

    this.value = MoerUtil.mongoColumnJoin(this.parentPropertyName, "value");
    }
    
    public OperatorMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName name = null;
    public MongoColumnName getName(){
        return name;
    }

    private MongoColumnName key = null;
    public MongoColumnName getKey(){
        return key;
    }

    private MongoColumnName value = null;
    public MongoColumnName getValue(){
        return value;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

