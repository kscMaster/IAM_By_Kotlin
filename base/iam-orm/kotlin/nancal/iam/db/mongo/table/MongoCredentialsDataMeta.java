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

public class MongoCredentialsDataMeta extends MongoColumnName{
    private String parentPropertyName;
    public MongoCredentialsDataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.sha1 = new MongoCredentialItemDataMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "sha1"));

    this.sha256 = new MongoCredentialItemDataMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "sha256"));
    }
    
    public MongoCredentialsDataMeta(MongoColumnName value) {
        this(value.toString());
    }

    @nbcp.db.DbName(value = "SCRAM-SHA-1")
    private MongoCredentialItemDataMeta sha1 = null;
    public MongoCredentialItemDataMeta getSha1(){
        return sha1;
    }

    @nbcp.db.DbName(value = "SCRAM-SHA-256")
    private MongoCredentialItemDataMeta sha256 = null;
    public MongoCredentialItemDataMeta getSha256(){
        return sha256;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

