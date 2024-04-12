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

public class IdUrlMeta extends MongoColumnName{
    private String parentPropertyName;
    public IdUrlMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.id = MoerUtil.mongoColumnJoin(this.parentPropertyName, "_id");

    this.url = MoerUtil.mongoColumnJoin(this.parentPropertyName, "url");
    }
    
    public IdUrlMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName id = null;
    public MongoColumnName getId(){
        return id;
    }

    /**
     * 网络资源地址
     */
    @nbcp.db.Cn(value = "网络资源地址")
    private MongoColumnName url = null;
    public MongoColumnName getUrl(){
        return url;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

