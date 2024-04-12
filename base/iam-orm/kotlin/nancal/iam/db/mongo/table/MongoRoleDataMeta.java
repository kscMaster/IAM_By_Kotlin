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

public class MongoRoleDataMeta extends MongoColumnName{
    private String parentPropertyName;
    public MongoRoleDataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.db = MoerUtil.mongoColumnJoin(this.parentPropertyName, "db");

    this.role = MoerUtil.mongoColumnJoin(this.parentPropertyName, "role");
    }
    
    public MongoRoleDataMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName db = null;
    public MongoColumnName getDb(){
        return db;
    }

    private MongoColumnName role = null;
    public MongoColumnName getRole(){
        return role;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

