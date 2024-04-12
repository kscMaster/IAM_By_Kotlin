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

public class IdNameMeta extends MongoColumnName{
    private String parentPropertyName;
    public IdNameMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.id = MoerUtil.mongoColumnJoin(this.parentPropertyName, "_id");

    this.name = MoerUtil.mongoColumnJoin(this.parentPropertyName, "name");
    }
    
    public IdNameMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName id = null;
    public MongoColumnName getId(){
        return id;
    }

    /**
     * 名称
     */
    @nbcp.db.Cn(value = "名称")
    private MongoColumnName name = null;
    public MongoColumnName getName(){
        return name;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

