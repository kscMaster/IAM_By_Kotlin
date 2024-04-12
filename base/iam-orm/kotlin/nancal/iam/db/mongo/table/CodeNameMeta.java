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

public class CodeNameMeta extends MongoColumnName{
    private String parentPropertyName;
    public CodeNameMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.name = MoerUtil.mongoColumnJoin(this.parentPropertyName, "name");

    this.code = MoerUtil.mongoColumnJoin(this.parentPropertyName, "code");
    }
    
    public CodeNameMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 名称
     */
    @nbcp.db.Cn(value = "名称")
    private MongoColumnName name = null;
    public MongoColumnName getName(){
        return name;
    }

    /**
     * 编码
     */
    @nbcp.db.Cn(value = "编码")
    private MongoColumnName code = null;
    public MongoColumnName getCode(){
        return code;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

